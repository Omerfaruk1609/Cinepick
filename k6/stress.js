/**
 * CinePick Stress Test — k6
 *
 * Purpose: "Nerede kırılmaya başlıyor?" — find the breaking point
 * Load:    100 → 250 → 500 → 750 VUs (ramp then hold)
 * Targets:
 *   - Error rate    < 5% at 500 VUs
 *   - p95           < 2000ms at max load
 *   - No cascading  failures (circuit breaker kicks in)
 *
 * Run:
 *   k6 run k6/stress.js
 *   k6 run k6/stress.js --env BASE_URL=http://localhost:8080 --out json=results/stress-results.json
 *
 * Note: This test is intentionally aggressive. The system is expected
 *       to degrade gracefully (circuit breaker, rate limiter) rather
 *       than crash. Monitor Redis, Postgres, and Ollama during this test.
 */

import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Rate, Trend } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

// ─── Stress Profile ───────────────────────────────────────────────────────────

export const options = {
  stages: [
    { duration: '2m',  target: 100 },  // Normal load baseline
    { duration: '2m',  target: 250 },  // Push higher
    { duration: '2m',  target: 500 },  // Stress zone
    { duration: '1m',  target: 750 },  // Near breaking point
    { duration: '2m',  target: 500 },  // Sustained high
    { duration: '1m',  target: 250 },  // Recovery check
    { duration: '30s', target: 0   },  // Cool down
  ],
  thresholds: {
    // More lenient than load test — we're probing limits
    http_req_failed:   ['rate<0.10'],       // < 10% errors acceptable at stress
    http_req_duration: ['p(95)<2000'],      // p95 < 2s at peak
    cinepick_errors:   ['rate<0.05'],       // Our custom error rate < 5%
  },
};

// ─── Metrics ─────────────────────────────────────────────────────────────────

const errorRate    = new Rate('cinepick_errors');
const recLatency   = new Trend('cinepick_rec_latency');
const searchLatency = new Trend('cinepick_search_latency');

// ─── Scenarios (mixed traffic) ────────────────────────────────────────────────

const SEARCH_QUERIES = [
  'action', 'drama', 'inception', 'thriller', 'anime',
  'dreams within dreams', 'space exploration', 'crime heist'
];

export default function () {
  const query = SEARCH_QUERIES[__ITER % SEARCH_QUERIES.length];

  // 60% search, 30% recommendations, 10% health
  const roll = Math.random();

  if (roll < 0.10) {
    // Health check
    const res = http.get(`${BASE_URL}/actuator/health`);
    const ok = check(res, { 'health 200': (r) => r.status === 200 });
    errorRate.add(!ok);

  } else if (roll < 0.70) {
    // Search (most common)
    const mode = ['keyword', 'hybrid'][Math.floor(Math.random() * 2)];
    const t0 = Date.now();
    const res = http.get(
      `${BASE_URL}/api/v1/movies/search?q=${encodeURIComponent(query)}&mode=${mode}`
    );
    searchLatency.add(Date.now() - t0);
    const ok = check(res, { 'search 200': (r) => r.status === 200 });
    errorRate.add(!ok);

  } else {
    // Onboarding pool (no auth needed — tests DB read under load)
    const t1 = Date.now();
    const res = http.get(`${BASE_URL}/api/v1/movies/onboarding-pool`);
    recLatency.add(Date.now() - t1);
    const ok = check(res, { 'pool 200': (r) => r.status === 200 });
    errorRate.add(!ok);
  }

  sleep(0.1); // Minimal sleep to maximize RPS
}

// ─── Summary ──────────────────────────────────────────────────────────────────

export function handleSummary(data) {
  const m = data.metrics;
  const summary = {
    timestamp: new Date().toISOString(),
    stress_test: {
      http_reqs:       m.http_reqs.values.count,
      rps:             m.http_reqs.values.rate,
      http_req_failed: m.http_req_failed.values.rate,
      avg_ms:          m.http_req_duration.values.avg,
      p50_ms:          m.http_req_duration.values['p(50)'],
      p95_ms:          m.http_req_duration.values['p(95)'],
      p99_ms:          m.http_req_duration.values['p(99)'],
      max_ms:          m.http_req_duration.values.max,
    },
  };

  const markdown = `## Stress Test Results — ${summary.timestamp}

> ⚠️ This is a stress test. Some errors at peak load are expected.

| Metric | Value | Notes |
|--------|-------|-------|
| Total Requests | ${summary.stress_test.http_reqs} | |
| RPS (avg) | ${summary.stress_test.rps?.toFixed(1)} req/s | Target: 100–500 |
| Error Rate | ${(summary.stress_test.http_req_failed * 100).toFixed(2)}% | Acceptable < 10% at stress |
| avg response | ${summary.stress_test.avg_ms?.toFixed(0)} ms | |
| p50 | ${summary.stress_test.p50_ms?.toFixed(0)} ms | |
| p95 | ${summary.stress_test.p95_ms?.toFixed(0)} ms | |
| p99 | ${summary.stress_test.p99_ms?.toFixed(0)} ms | |
| max | ${summary.stress_test.max_ms?.toFixed(0)} ms | |

### Interpretation
- Error rate < 5% at 500 VUs → System is handling stress gracefully ✅
- Error rate > 10% at 500 VUs → Bottleneck found, investigate DB/Redis/LLM ⚠️
- Circuit breaker activations visible in Spring Boot logs → Expected behavior ✅
`;

  console.log('Stress Test Summary:', JSON.stringify(summary, null, 2));

  return {
    'results/stress-summary.json': JSON.stringify(summary, null, 2),
    'results/stress-summary.md':   markdown,
    'stdout': markdown,
  };
}
