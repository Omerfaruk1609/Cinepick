<div align="center">

# 🎬 CinePick

### AI-Powered Movie Recommendation & Discovery Platform

*Hybrid LLM + Vector Search | Spring Boot 3.2 + React 19 | Production-Grade Stack*

![Architecture](C:\Users\ÖMER FARUK\.gemini\antigravity\brain\b7f295a5-77d3-4c48-80bd-74b8bf229e01\cinepick_architecture_1785683476329.jpg)

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19-61DAFB?style=for-the-badge&logo=react&logoColor=black)](https://react.dev/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://docs.docker.com/compose/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-pgvector-336791?style=for-the-badge&logo=postgresql&logoColor=white)](https://github.com/pgvector/pgvector)
[![Redis](https://img.shields.io/badge/Redis-7.0-DC382D?style=for-the-badge&logo=redis&logoColor=white)](https://redis.io/)
[![License](https://img.shields.io/badge/License-MIT-purple?style=for-the-badge)](LICENSE)

</div>

---

## 📖 İçindekiler

- [Proje Hakkında](#-proje-hakkında)
- [Mimari](#-mimari)
- [Teknoloji Yığını](#-teknoloji-yığını)
- [Özellikler](#-özellikler)
- [Kurulum](#-kurulum)
- [Ortam Değişkenleri](#-ortam-değişkenleri)
- [API Referansı](#-api-referansı)
- [Test](#-test)
- [İzleme & Observability](#-izleme--observability)
- [CI/CD Pipeline](#-cicd-pipeline)
- [Proje Yapısı](#-proje-yapısı)
- [Git Geçmişi](#-git-geçmişi)

---

## 🎯 Proje Hakkında

**CinePick**, geleneksel kural tabanlı öneri sistemlerini tamamen geride bırakan, **Büyük Dil Modelleri (LLM)** ve **pgvector cosine similarity** aramasını birleştiren hibrit bir film öneri platformudur.

Kullanıcı, tercihlerini doğal dil ile ifade eder ("karanlık, atmosferik, 90'lar neo-noir" gibi). Backend bu metni vektöre dönüştürür ve gerçek zamanlı anlamsal eşleşme ile film önerileri üretir.

### Temel Yaklaşımlar

| Yaklaşım | Teknoloji | Açıklama |
|----------|-----------|----------|
| Anlamsal Arama | `pgvector` + Spring AI Embeddings | Film özetleri vektörize edilerek cosine similarity ile eşleşme |
| Narrative Engine | Ollama / Llama3 | LLM tabanlı film analizi ve açıklama üretimi |
| Güvenlik | JWT + Spring Security | Stateless kimlik doğrulama |
| Önbellekleme | Redis + `@Cacheable` | TMDB API ve LLM yanıtları cache'lenir |
| Hata Toleransı | Resilience4j Circuit Breaker | LLM zaman aşımında kural tabanlı fallback devreye girer |

---

## 🏛️ Mimari

![Tech Stack](C:\Users\ÖMER FARUK\.gemini\antigravity\brain\b7f295a5-77d3-4c48-80bd-74b8bf229e01\cinepick_tech_stack_1785683504365.jpg)

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENT LAYER                             │
│              React 19 + Vite + Axios JWT Interceptor            │
└─────────────────────────┬───────────────────────────────────────┘
                          │ HTTPS / REST
┌─────────────────────────▼───────────────────────────────────────┐
│                    NGINX REVERSE PROXY                          │
│         SSL Termination + CORS + Security Headers               │
└─────────────────────────┬───────────────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────────────┐
│               SPRING BOOT 3.2 BACKEND (Java 21)                 │
│                                                                 │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────┐  │
│  │ Auth Layer   │  │ Narrative     │  │ Movie Embedding      │  │
│  │ JWT + Spring │  │ Engine (AI)   │  │ Ingestion Job        │  │
│  │ Security     │  │ + Fallback    │  │ @Scheduled Batch     │  │
│  └──────────────┘  └──────────────┘  └──────────────────────┘  │
│                                                                 │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────┐  │
│  │ Redis Cache  │  │ Resilience4j │  │ Spring Actuator      │  │
│  │ @Cacheable   │  │ Circuit      │  │ + Prometheus Metrics │  │
│  │ Abstraction  │  │ Breaker      │  │                      │  │
│  └──────────────┘  └──────────────┘  └──────────────────────┘  │
└────┬─────────────────────┬────────────────────┬─────────────────┘
     │                     │                    │
┌────▼──────┐     ┌────────▼──────┐    ┌───────▼──────────┐
│PostgreSQL  │     │    Redis 7    │    │  Ollama (LLM)    │
│+ pgvector  │     │   (Cache)     │    │  Llama3/Mistral  │
│(Vectors)   │     │               │    │                  │
└────────────┘     └───────────────┘    └──────────────────┘
                                                 │
                                        ┌────────▼──────────┐
                                        │    TMDB API       │
                                        │  (Film Veritabanı)│
                                        └───────────────────┘
```

---

## 🛠️ Teknoloji Yığını

### Backend
| Teknoloji | Versiyon | Kullanım |
|-----------|----------|----------|
| Java | 21 (Virtual Threads) | Platform dili |
| Spring Boot | 3.2.4 | Ana framework |
| Spring AI | 1.0.0-M1 | LLM entegrasyonu + Embedding |
| Spring Security | 6.x | JWT tabanlı auth |
| Spring Data JPA | 3.2.x | ORM katmanı |
| Spring Data Redis | 3.2.x | Cache katmanı |
| Resilience4j | 2.2.0 | Circuit breaker + Rate limiter |
| Spring Actuator | 3.2.x | Health + Prometheus metrics |
| Micrometer Prometheus | 1.12.x | Metrik ihracatı |
| JJWT | 0.12.6 | JWT üretimi ve doğrulama |
| Lombok | latest | Boilerplate azaltma |

### Veritabanı & Altyapı
| Teknoloji | Versiyon | Kullanım |
|-----------|----------|----------|
| PostgreSQL | 15 | Ana veritabanı |
| pgvector | 0.5.1 | Vektör similarity araması |
| Redis | 7.0 Alpine | Cache + Session store |
| Ollama | latest | Local LLM sunucu |

### Frontend
| Teknoloji | Versiyon | Kullanım |
|-----------|----------|----------|
| React | 19 | UI framework |
| Vite | 8.2 | Build tool |
| Axios | 1.19 | HTTP client + JWT interceptor |
| Lucide React | 1.28 | İkon kütüphanesi |

### DevOps & Observability
| Teknoloji | Kullanım |
|-----------|----------|
| Docker + Docker Compose | Konteynerizasyon |
| Nginx | Reverse proxy + SSL |
| GitHub Actions | CI/CD pipeline |
| Prometheus | Metrik toplama |
| Grafana | Dashboard + Görselleştirme |
| Testcontainers | Integration test |

---

## ✨ Özellikler

### 🤖 AI-Powered Narrative Engine
- **Spring AI** ile Ollama (Llama3/Mistral) entegrasyonu
- **Structured Output Converter** ile LLM yanıtlarını `MovieAnalysisResponse` DTO'ya dönüştürme
- LLM servis zaman aşımında otomatik **kural tabanlı fallback**
- `@Scheduled` batch job ile film özeti → vektör embedding otomatik indeksleme

### 🔐 Production-Grade Security
- Stateless JWT kimlik doğrulama (Access Token)
- `ApplicationConfig` üzerinden döngüsel bağımlılık (circular dependency) çözümü
- CORS + Security headers (Nginx seviyesinde)

### ⚡ Performans & Hata Toleransı
- **Java 21 Virtual Threads** ile non-blocking I/O
- Redis `@Cacheable` ile TMDB API ve LLM yanıt önbellekleme
- Resilience4j **Circuit Breaker** — açık devrede fallback otomatik devreye girer
- Resilience4j **Rate Limiter** — TMDB API throttling koruması
- HikariCP connection pool (max 20 bağlantı)

### 📊 Observability
- Spring Boot Actuator: `/actuator/health`, `/actuator/metrics`, `/actuator/prometheus`
- Prometheus + **Grafana Dashboard** (4 panel: JVM Heap, HTTP Latency p95/p99, Redis Hit/Miss, HikariCP)

---

## 🚀 Kurulum

### Ön Gereksinimler

- Docker Desktop 24+
- Docker Compose v2+
- Java 21 (local geliştirme)
- Node.js 20+ (local geliştirme)
- Ollama (`ollama pull llama3`)

### 1. Repoyu Klonla

```bash
git clone https://github.com/<kullanici>/cinepick.git
cd cinepick
```

### 2. Ortam Değişkenlerini Ayarla

```bash
cp .env.example .env
# .env dosyasını düzenle (aşağıdaki tabloya bak)
```

### 3. Docker Compose ile Çalıştır

```bash
# Tüm stack'i ayağa kaldır
docker compose up -d

# Sadece altyapıyı başlat (local geliştirme için)
docker compose up -d postgres redis ollama
```

### 4. Monitoring Stack (Opsiyonel)

```bash
cd monitoring
docker compose -f docker-compose.monitoring.yml up -d
```

### 5. Servis URL'leri

| Servis | URL |
|--------|-----|
| Frontend | http://localhost:3000 |
| Backend API | http://localhost:8080/api/v1 |
| Actuator Health | http://localhost:8080/actuator/health |
| Prometheus | http://localhost:9090 |
| Grafana | http://localhost:3001 (admin/admin) |
| Ollama | http://localhost:11434 |

---

## 🔧 Ortam Değişkenleri

> **Kritik:** `JWT_SECRET` ve `TMDB_API_KEY` **kesinlikle** ortam değişkeni olarak verilmelidir. Kaynak kodunda bulunmaz.

| Değişken | Zorunlu | Varsayılan | Açıklama |
|----------|---------|------------|----------|
| `SPRING_DATASOURCE_URL` | ✅ | `jdbc:postgresql://localhost:5432/cinepick_db` | PostgreSQL JDBC URL |
| `SPRING_DATASOURCE_USERNAME` | ✅ | `postgres` | DB kullanıcı adı |
| `SPRING_DATASOURCE_PASSWORD` | ✅ | — | DB şifresi |
| `JWT_SECRET` | ✅ | — | 256-bit hex JWT imzalama anahtarı |
| `JWT_EXPIRATION_MS` | ❌ | `86400000` (24 saat) | Token geçerlilik süresi (ms) |
| `TMDB_API_KEY` | ✅ | — | TMDB API anahtarı |
| `TMDB_BASE_URL` | ❌ | `https://api.themoviedb.org/3` | TMDB API base URL |
| `REDIS_HOST` | ❌ | `localhost` | Redis sunucu adresi |
| `REDIS_PORT` | ❌ | `6379` | Redis portu |
| `REDIS_PASSWORD` | ❌ | — | Redis şifresi |

---

## 📡 API Referansı

### Kimlik Doğrulama

```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "securepassword"
}
```

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "johndoe",
  "password": "securepassword"
}

# Yanıt: { "token": "eyJhbGc..." }
```

### Narrative Engine (LLM)

```http
POST /api/v1/narrative/analyze
Authorization: Bearer <token>
Content-Type: application/json

{
  "query": "karanlık neo-noir, 90'lar, atmosferik suç filmi"
}
```

### Film Embedding (pgvector Araması)

```http
GET /api/v1/movies/similar?query=mind+bending+sci-fi+thriller&limit=10
Authorization: Bearer <token>
```

---

## 🧪 Test

### Unit Testler

```bash
mvn test
```

```
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

| Test Sınıfı | Kapsam |
|-------------|--------|
| `CinepickApplicationTests` | Spring context yükleme (`@SpringBootTest`) |
| `NarrativeServiceTest` | AI Narrative Engine — Mockito stubbing |

### Integration Testler (Testcontainers)

> ⚠️ Docker Desktop çalışıyor olmalıdır.

```bash
mvn verify
```

```
[INFO] Running proje.cinepick.integration.BaseIntegrationTest
  ✓ contextLoads_andDatabaseIsReachable
  ✓ pgvectorExtension_isInstalled
  ✓ redisContainer_isReachable
[INFO] BUILD SUCCESS
```

| Test | Konteyner | Doğrulama |
|------|-----------|-----------|
| `contextLoads_andDatabaseIsReachable` | `ankane/pgvector:v0.5.1` | `SELECT 1` başarılı |
| `pgvectorExtension_isInstalled` | PostgreSQL | `pg_extension`'da `vector` var |
| `redisContainer_isReachable` | `redis:7-alpine` | Port bind edilmiş ve aktif |

---

## 📊 İzleme & Observability

### Grafana Dashboard — "CinePick Platform Observability"

Grafana `http://localhost:3001` adresinde açıldığında **CinePick** klasörü altında otomatik yüklenen 4 panel bulunur:

| Panel | Metrik | Grafik |
|-------|--------|--------|
| 🧠 JVM Heap Memory | `jvm_memory_used_bytes` | Time series |
| ⚡ HTTP Latency p95/p99 | `http_server_requests_seconds_bucket` | Time series |
| 🔴 Redis Hit/Miss Ratio | `cache_gets_total{result="hit/miss"}` | Donut pie |
| 🗄️ HikariCP Connection Pool | `hikaricp_connections_active/idle/pending` | Time series |

### Actuator Endpoints

```http
GET /actuator/health      # Uygulama sağlık durumu
GET /actuator/metrics     # Tüm metrikler
GET /actuator/prometheus  # Prometheus scrape endpoint
GET /actuator/info        # Uygulama bilgisi
```

---

## 🔄 CI/CD Pipeline

`.github/workflows/ci-cd.yml` ile otomatik çalışan 3 aşamalı pipeline:

```
Push to main
     │
     ▼
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│  Test Stage  │───▶│  Build Stage │───▶│  Deploy Stage│
│              │    │              │    │              │
│ mvn test     │    │ Docker Build │    │ Push to GHCR │
│ (unit tests) │    │ Backend      │    │ docker compose│
│              │    │ Frontend     │    │ up -d        │
└──────────────┘    └──────────────┘    └──────────────┘
```

---

## 📂 Proje Yapısı

```
cinepick/
├── 📁 src/main/java/proje/cinepick/
│   ├── 📁 config/
│   │   ├── ApplicationConfig.java        # UserDetailsService, PasswordEncoder, AuthManager
│   │   ├── SecurityConfig.java           # SecurityFilterChain, CORS
│   │   └── CacheConfig.java              # Redis CacheManager
│   ├── 📁 controller/
│   │   ├── AuthController.java           # /auth/register, /auth/login
│   │   └── NarrativeController.java      # /narrative/analyze
│   ├── 📁 service/
│   │   ├── SpringAiNarrativeService.java # LLM-powered film analizi
│   │   ├── MovieEmbeddingService.java    # pgvector embedding kayıt/sorgu
│   │   ├── ResilientTmdbService.java     # Circuit breaker + cache TMDB
│   │   └── AuthService.java             # Kayıt/giriş işlemleri
│   ├── 📁 job/
│   │   └── MovieEmbeddingIngestionJob.java # @Scheduled batch embedding
│   ├── 📁 entity/
│   │   ├── User.java
│   │   ├── Movie.java
│   │   └── UserMovieInteraction.java
│   ├── 📁 security/
│   │   ├── JwtService.java
│   │   └── JwtAuthenticationFilter.java
│   └── 📁 repository/
│       ├── UserRepository.java
│       └── MovieRepository.java          # findUnindexedMovies() native query
│
├── 📁 src/test/java/proje/cinepick/
│   ├── CinepickApplicationTests.java     # Context load (@SpringBootTest)
│   ├── service/NarrativeServiceTest.java # Unit tests (Mockito)
│   └── integration/
│       └── BaseIntegrationTest.java      # Testcontainers IT tests
│
├── 📁 cinepick/                          # React Frontend (Vite)
│   ├── src/services/apiClient.js         # Axios + JWT interceptor
│   └── Dockerfile                        # Multi-stage Nginx build
│
├── 📁 monitoring/
│   ├── grafana/dashboards/
│   │   └── cinepick-overview.json        # Grafana dashboard
│   └── grafana/provisioning/             # Auto-provisioning configs
│
├── 📁 nginx/
│   └── prod.conf                         # Production reverse proxy
│
├── 📁 .github/workflows/
│   └── ci-cd.yml                         # GitHub Actions pipeline
│
├── docker-compose.yml                    # Full stack orchestration
├── Dockerfile                            # Backend multi-stage build
└── pom.xml                              # Maven + Testcontainers BOM
```

---

## 📜 Git Geçmişi

| Commit | Tip | Açıklama |
|--------|-----|----------|
| `76c4e77` | test | Testcontainers BaseIntegrationTest (pgvector + Redis) |
| `b2a4032` | feat | Grafana dashboard + auto-provisioning |
| `15104c1` | fix | Frontend Docker image build hatası düzeltme |
| `85a2393` | fix | Circular dependency + Maven unit test hataları |
| `f9b7e43` | fix | Docker build context yolları ve CI/CD düzeltme |
| `9f4b101` | feat | Observability: Actuator + Prometheus + Nginx |
| `96e5cc1` | ci | Multi-stage Dockerfiles + GitHub Actions |
| `bcf24fe` | feat | Redis caching + Resilience4j circuit breakers |
| `adcecaa` | feat | Spring AI Narrative Engine + pgvector search |
| `81cbe05` | feat | PostgreSQL JPA layer + JWT authentication |

---

## 📄 Lisans

Bu proje [MIT Lisansı](LICENSE) altında dağıtılmaktadır.

---

<div align="center">

**⭐ Beğendiyseniz yıldız atmayı unutmayın!**

*CinePick — Where AI Meets Cinema*

</div>
