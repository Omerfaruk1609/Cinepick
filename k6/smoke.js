/**
 * CinePick Smoke Test — k6
 *
 * Purpose: Quick sanity check — "Is the app alive?"
 * Load:    1–5 virtual users, 30 seconds
 * Targets: HTTP error rate = 0%, all endpoints respond
 *
 * Run:
 *   k6 run k6/smoke.js
 *   k6 run k6/smoke.js --env BASE_URL=http://localhost:8080
 */

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

// ─── Configuration ────────────────────────────────────────────────────────────

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export const options = {
  vus: 3,
  duration: '30s',
  thresholds: {
    http_req_failed:   ['rate<0.01'],   // Error rate < 1%
    http_req_duration: ['p(95)<500'],   // p95 < 500ms
  },
};

// ─── Custom Metrics ────────────────────────────────────────────────────────────

const errorRate = new Rate('cinepick_errors');
const recLatency = new Trend('cinepick_rec_latency');
const searchLatency = new Trend('cinepick_search_latency');

// ─── Auth Helper ──────────────────────────────────────────────────────────────

function getAuthToken() {
  http.post(
    `${BASE_URL}/api/v1/auth/register`,
    JSON.stringify({ username: 'smoke_test_user', email: 'smoke@k6.test', password: 'smoke_password_123' }),
    { headers: { 'Content-Type': 'application/json' } }
  );
  const loginRes = http.post(
    `${BASE_URL}/api/v1/auth/login`,
    JSON.stringify({ username: 'smoke_test_user', password: 'smoke_password_123' }),
    { headers: { 'Content-Type': 'application/json' } }
  );
  if (loginRes.status === 200) {
    try {
      return JSON.parse(loginRes.body).token;
    } catch (e) {}
  }
  return null;
}


// ─── Main Scenario ────────────────────────────────────────────────────────────

export default function () {
  const token = getAuthToken();
  const headers = token
    ? { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' }
    : { 'Content-Type': 'application/json' };

  // 1. Health check
  const health = http.get(`${BASE_URL}/actuator/health`);
  check(health, { 'health is UP': (r) => r.status === 200 });
  errorRate.add(health.status !== 200);

  // 2. Onboarding pool (public)
  const pool = http.get(`${BASE_URL}/api/v1/movies/onboarding-pool`);
  check(pool, {
    'onboarding-pool status 200': (r) => r.status === 200,
    'onboarding-pool has movies':  (r) => r.status === 200 && r.body && r.body.length > 0,
  });


  // 3. Search (keyword mode)
  const t0 = Date.now();
  const search = http.get(`${BASE_URL}/api/v1/movies/search?q=inception&mode=keyword`);
  searchLatency.add(Date.now() - t0);
  check(search, { 'search returns 200': (r) => r.status === 200 });

  // 4. Personalized recommendations (auth required)
  if (token) {
    const t1 = Date.now();
    const recs = http.get(`${BASE_URL}/api/v1/recommendations/personalized?limit=10`, { headers });
    recLatency.add(Date.now() - t1);
    check(recs, { 'recommendations 200': (r) => r.status === 200 || r.status === 204 });
  }

  sleep(1);
}

// ─── Teardown Summary ─────────────────────────────────────────────────────────

export function handleSummary(data) {
  const summary = {
    'smoke_test': {
      'error_rate':       data.metrics.cinepick_errors ? data.metrics.cinepick_errors.values.rate : 0,
      'rec_p50_ms':       data.metrics.cinepick_rec_latency ? data.metrics.cinepick_rec_latency.values['p(50)'] : null,
      'search_p50_ms':    data.metrics.cinepick_search_latency ? data.metrics.cinepick_search_latency.values['p(50)'] : null,
      'http_p95_ms':      data.metrics.http_req_duration.values['p(95)'],
    }
  };
  console.log('Smoke Test Summary:', JSON.stringify(summary, null, 2));
  return { 'stdout': JSON.stringify(summary, null, 2) };
}
