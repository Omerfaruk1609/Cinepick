/**
 * CinePick Load Test — k6
 *
 * Purpose: "Normal yük altında nasıl?" — sustained load test
 * Load:    Ramp up to 100 VUs, sustain 5 minutes, ramp down
 * Targets:
 *   - HTTP error rate     < 1%
 *   - p50 avg response    < 200ms
 *   - p95 response        < 500ms
 *   - p99 response        < 1000ms
 *   - Throughput          > 50 req/s
 *
 * Run:
 *   k6 run k6/load.js
 *   k6 run k6/load.js --env BASE_URL=http://localhost:8080 --out json=results/load-results.json
 */

import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Rate, Trend, Counter } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

// ─── Load Profile ────────────────────────────────────────────────────────────

export const options = {
  stages: [
    { duration: '1m',  target: 20  },  // Ramp up to 20 VUs
    { duration: '1m',  target: 50  },  // Continue ramp to 50 VUs
    { duration: '3m',  target: 100 },  // Sustain 100 VUs (main load)
    { duration: '1m',  target: 50  },  // Ramp down
    { duration: '30s', target: 0   },  // Cool down
  ],
  thresholds: {
    http_req_failed:              ['rate<0.01'],     // < 1% error rate
    http_req_duration:            ['p(50)<200', 'p(95)<500', 'p(99)<1000'],
    cinepick_rec_latency:         ['p(50)<200', 'p(95)<500'],
    cinepick_search_latency:      ['p(50)<100', 'p(95)<300'],
  },
};

// ─── Custom Metrics ────────────────────────────────────────────────────────────

const errorRate         = new Rate('cinepick_errors');
const recLatency        = new Trend('cinepick_rec_latency');
const searchLatency     = new Trend('cinepick_search_latency');
const cacheHitCounter   = new Counter('cinepick_cache_hint');   // Inferred via latency
const successCounter    = new Counter('cinepick_success');

// ─── Test Users Pool ──────────────────────────────────────────────────────────

const TEST_USERS = [
  { username: 'load_user_1', password: 'LoadPass123!' },
  { username: 'load_user_2', password: 'LoadPass123!' },
  { username: 'load_user_3', password: 'LoadPass123!' },
  { username: 'load_user_4', password: 'LoadPass123!' },
  { username: 'load_user_5', password: 'LoadPass123!' },
];

const SEARCH_QUERIES = [
  'inception', 'the dark knight', 'drama', 'sci-fi thriller',
  'interstellar', 'parasite', 'action', 'anime', 'crime'
];

// ─── Setup: Create test users ─────────────────────────────────────────────────

export function setup() {
  const tokens = {};
  for (const user of TEST_USERS) {
    // Register (may fail if already exists — that's OK)
    http.post(
      `${BASE_URL}/api/v1/auth/register`,
      JSON.stringify({ username: user.username, email: `${user.username}@k6.test`, password: user.password }),
      { headers: { 'Content-Type': 'application/json' } }
    );

    // Login
    const loginRes = http.post(
      `${BASE_URL}/api/v1/auth/login`,
      JSON.stringify({ username: user.username, password: user.password }),
      { headers: { 'Content-Type': 'application/json' } }
    );

    if (loginRes.status === 200) {
      tokens[user.username] = JSON.parse(loginRes.body).token;
    }
  }
  return { tokens };
}

// ─── Main Scenario ────────────────────────────────────────────────────────────

export default function (data) {
  const userIndex    = __VU % TEST_USERS.length;
  const user         = TEST_USERS[userIndex];
  const token        = data.tokens[user.username];
  const searchQuery  = SEARCH_QUERIES[Math.floor(Math.random() * SEARCH_QUERIES.length)];
  const searchMode   = ['keyword', 'semantic', 'hybrid'][Math.floor(Math.random() * 3)];

  const authHeaders = {
    Authorization:  `Bearer ${token}`,
    'Content-Type': 'application/json',
  };

  // ── Group 1: Public endpoints ──────────────────────────────────────────────
  group('public_endpoints', () => {
    const health = http.get(`${BASE_URL}/actuator/health`);
    check(health, { 'health up': (r) => r.status === 200 });
  });

  // ── Group 2: Search ────────────────────────────────────────────────────────
  group('search', () => {
    const t0 = Date.now();
    const res = http.get(
      `${BASE_URL}/api/v1/movies/search?q=${encodeURIComponent(searchQuery)}&mode=${searchMode}&size=20`
    );
    searchLatency.add(Date.now() - t0);

    const ok = check(res, {
      'search 200': (r) => r.status === 200,
      'search has results field': (r) => {
        try { return 'results' in JSON.parse(r.body); } catch { return false; }
      },
    });
    errorRate.add(!ok);
    if (ok) successCounter.add(1);
  });

  // ── Group 3: Recommendations (authenticated) ───────────────────────────────
  if (token) {
    group('recommendations', () => {
      const t1 = Date.now();
      const res = http.get(
        `${BASE_URL}/api/v1/recommendations/personalized?limit=10`,
        { headers: authHeaders }
      );
      recLatency.add(Date.now() - t1);

      const ok = check(res, {
        'recs 200 or 204': (r) => r.status === 200 || r.status === 204,
      });
      errorRate.add(!ok);
    });
  }

  // ── Group 4: Analytics ────────────────────────────────────────────────────
  if (token && Math.random() < 0.3) { // 30% of iterations
    group('analytics', () => {
      const res = http.get(
        `${BASE_URL}/api/v1/analytics/taste`,
        { headers: authHeaders }
      );
      check(res, { 'analytics 200': (r) => r.status === 200 || r.status === 204 });
    });
  }

  sleep(Math.random() * 1 + 0.5); // 0.5–1.5s between requests
}

// ─── Summary ──────────────────────────────────────────────────────────────────

export function handleSummary(data) {
  const m = data.metrics;
  const summary = {
    timestamp: new Date().toISOString(),
    load_test: {
      http_reqs:       m.http_reqs.values.count,
      http_req_failed: m.http_req_failed.values.rate,
      avg_ms:          m.http_req_duration.values.avg,
      p50_ms:          m.http_req_duration.values['p(50)'],
      p95_ms:          m.http_req_duration.values['p(95)'],
      p99_ms:          m.http_req_duration.values['p(99)'],
    },
    recommendation: {
      p50_ms: m.cinepick_rec_latency    ? m.cinepick_rec_latency.values['p(50)']    : null,
      p95_ms: m.cinepick_rec_latency    ? m.cinepick_rec_latency.values['p(95)']    : null,
    },
    search: {
      p50_ms: m.cinepick_search_latency ? m.cinepick_search_latency.values['p(50)'] : null,
      p95_ms: m.cinepick_search_latency ? m.cinepick_search_latency.values['p(95)'] : null,
    },
  };

  const markdown = `## Load Test Results — ${summary.timestamp}

| Metric | Value | Target |
|--------|-------|--------|
| HTTP Requests | ${summary.load_test.http_reqs} | — |
| Error Rate | ${(summary.load_test.http_req_failed * 100).toFixed(2)}% | < 1% |
| Avg Response | ${summary.load_test.avg_ms?.toFixed(0)} ms | — |
| p50 | ${summary.load_test.p50_ms?.toFixed(0)} ms | < 200ms |
| p95 | ${summary.load_test.p95_ms?.toFixed(0)} ms | < 500ms |
| p99 | ${summary.load_test.p99_ms?.toFixed(0)} ms | < 1000ms |
| Rec p50 | ${summary.recommendation.p50_ms?.toFixed(0)} ms | < 200ms |
| Search p50 | ${summary.search.p50_ms?.toFixed(0)} ms | < 100ms |
`;

  console.log('Load Test Summary:', JSON.stringify(summary, null, 2));

  return {
    'results/load-summary.json': JSON.stringify(summary, null, 2),
    'results/load-summary.md':   markdown,
    'stdout': markdown,
  };
}
