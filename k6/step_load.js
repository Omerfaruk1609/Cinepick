import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://backend:8080';
const TARGET_VUS = parseInt(__ENV.TARGET_VUS || '10');
const DURATION = __ENV.DURATION || '30s';

export const options = {
  vus: TARGET_VUS,
  duration: DURATION,
};

const counter2xx   = new Counter('status_2xx');
const counter4xx   = new Counter('status_4xx');
const counter5xx   = new Counter('status_5xx');

export function setup() {
  const username = `bench_global_user_${Date.now()}`;
  const password = 'BenchPass123!';

  const reg = http.post(
    `${BASE_URL}/api/v1/auth/register`,
    JSON.stringify({ username: username, email: `${username}@k6.test`, password: password }),
    { headers: { 'Content-Type': 'application/json' } }
  );

  const loginRes = http.post(
    `${BASE_URL}/api/v1/auth/login`,
    JSON.stringify({ username: username, password: password }),
    { headers: { 'Content-Type': 'application/json' } }
  );

  let token = null;
  if (loginRes.status === 200 && loginRes.body) {
    try {
      token = JSON.parse(loginRes.body).token;
    } catch (e) {}
  }
  console.log('SETUP COMPLETE, TOKEN OBTAINED:', token ? 'YES' : 'NO');
  return { token: token };
}

function trackStatus(res) {
  if (res.status >= 200 && res.status < 300) {
    counter2xx.add(1);
  } else if (res.status >= 400 && res.status < 500) {
    counter4xx.add(1);
    console.log(`4XX DETECTED: [${res.status}] ${res.url} -> ${res.body}`);
  } else if (res.status >= 500 && res.status < 600) {
    counter5xx.add(1);
    console.log(`5XX DETECTED: [${res.status}] ${res.url} -> ${res.body}`);
  }
}

export default function (data) {
  // 1. Health check (Public)
  const health = http.get(`${BASE_URL}/actuator/health`);
  trackStatus(health);

  // 2. Search (Public)
  const search = http.get(`${BASE_URL}/api/v1/movies/search?q=inception&mode=keyword&size=10`);
  trackStatus(search);

  // 3. Recommendation (Authenticated)
  if (data && data.token) {
    const headers = { Authorization: `Bearer ${data.token}`, 'Content-Type': 'application/json' };
    const recs = http.get(`${BASE_URL}/api/v1/recommendations/personalized?limit=10`, { headers });
    trackStatus(recs);
  } else {
    // If no token, test another public endpoint like onboarding-pool
    const pool = http.get(`${BASE_URL}/api/v1/movies/onboarding-pool`);
    trackStatus(pool);
  }

  sleep(0.2);
}
