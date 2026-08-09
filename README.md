<div align="center">

# 🎬 CinePick

### Yapay Zeka Destekli Kişiselleştirilmiş Film Öneri & Sinema Keşif Platformu

*Hibrit Vektör Arama | Spring AI LLM | Dinamik Zevk Centroidi ($V_{user}$) | Spring Boot 3.2 + React 19 | Production-Grade Stack*

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring AI](https://img.shields.io/badge/Spring_AI-1.0.0-0073EC?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-ai)
[![React](https://img.shields.io/badge/React-19-61DAFB?style=for-the-badge&logo=react&logoColor=black)](https://react.dev/)
[![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-3.4-38B2AC?style=for-the-badge&logo=tailwind-css&logoColor=white)](https://tailwindcss.com/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-pgvector-336791?style=for-the-badge&logo=postgresql&logoColor=white)](https://github.com/pgvector/pgvector)
[![Redis](https://img.shields.io/badge/Redis-7.0-DC382D?style=for-the-badge&logo=redis&logoColor=white)](https://redis.io/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://docs.docker.com/compose/)
[![Nginx](https://img.shields.io/badge/Nginx-SSL-009639?style=for-the-badge&logo=nginx&logoColor=white)](https://nginx.org/)
[![License](https://img.shields.io/badge/License-MIT-purple?style=for-the-badge)](LICENSE)

</div>

---

## 📖 İçindekiler

- [🎯 Proje Hakkında](#-proje-hakkında)
- [✨ Temel Özellikler & Akıllı Modüller](#-temel-özellikler--akıllı-modüller)
- [📐 Matematiksel Model ve Algoritma](#-matematiksel-model-ve-algoritma)
- [🏛️ Sistem Mimarisi](#-sistem-mimarisi)
- [📂 Proje Dizin Yapısı](#-proje-dizin-yapısı)
- [🛠️ Teknoloji Yığını](#-teknoloji-yığını)
- [🚀 Hızlı Kurulum & Çalıştırma](#-hızlı-kurulum--çalıştırma)
- [🔒 Ortam Değişkenleri & Güvenlik](#-ortam-değişkenleri--güvenlik)
- [📡 API Referansı](#-api-referansı)
- [📊 Benchmark & Performans Ölçümleri](#-benchmark--performans-ölçümleri)
- [🐳 Prodüksiyon & DevOps (SSL & Monitoring)](#-prodüksiyon--devops-ssl--monitoring)
- [📜 Git Commit Geçmişi](#-git-commit-geçmişi)
- [🤝 Katkıda Bulunma & Lisans](#-katkıda-bulunma--lisans)

---

## 🎯 Proje Hakkında

**CinePick**, geleneksel sabit kategori veya kural tabanlı film arama platformlarının ötesine geçen; **Büyük Dil Modelleri (LLM)**, **PostgreSQL `pgvector` Cosine Distance Araması** ve **Dinamik Kullanıcı Zevk Profil Vektörü ($V_{user}$)** mimarisini harmanlayan akıllı bir film öneri ve keşif ekosistemidir.

Platform, kullanıcının platform üzerindeki tüm etkileşimlerini (favoriye ekleme, puanlama, izleme listesi hareketleri ve nefret edilen türler) vektör uzayında matematiksel olarak ağırlıklandırır. 1536 boyutlu bir kullanıcı zevk centroidi ($V_{user}$) anlık olarak hesaplanır ve `pgvector` üzerinde milisaniyeler içerisinde sorgulanarak kişiye özel nokta atışı öneriler üretilir.

---

## ✨ Temel Özellikler & Akıllı Modüller

### 🧠 1. Dinamik Zevk Profil Vektörü ($V_{user}$)
* Kullanıcının tüm geçmiş film etkileşimleri ($E_i$ film gömme vektörleri) aksiyon türüne göre ağırlıklandırılır ve **L2 Normalization** ile birim zevk vektörüne dönüştürülür.
* **Kullanıcı Kayması (Taste Drift):** Kullanıcının film tercihleri zamanla değiştikçe $V_{user}$ dinamik olarak güncellenir.

### 🤖 2. Spring AI & LLM Gerekçeli Öneri (Explainable AI)
* Yalnızca kuru bir film listesi sunmak yerine, Spring AI entegreli LLM kullanıcının o filmi neden seveceğini **tek cümlelik kişiselleştirilmiş bir gerekçe** ile açıklar.
* *Örnek:* `"Interstellar ve Inception'daki zihin bükücü bilimkurgu atmosferini sevdiğin için Annihilation tam senin zevkine göre!"`

### ⚡ 3. Hibrit Vektör & Metin Araması (Hybrid Search)
* `pgvector` cosine benzerliği + SQL native `ILIKE` semantik aramasını birleştiren hibrit sıralama algoritması.
* Semantik veri eksikse otomatik olarak Keyword arama mekanizmasına kesintisiz fallback yapar.

### 🎭 4. Sinema Persona & Zevk Analitiği
* Kullanıcının izleme alışkanlıklarını analiz ederek kişiye özel **"Sinema Persona'sı"** (Örn: *Karanlık Bilimkurgu Tutkunu*, *Art-House Drama Sever*) ve detaylı görsel zevk grafikleri sunar.

### 👥 5. Arkadaş Yüzdesi & Ortak Film Önerisi (Friend Match)
* İki ayrı kullanıcının $V_{user}$ zevk vektörlerinin Cosine Similarity değerini hesaplayarak sinematik uyum yüzdelerini çıkarır ve her ikisinin de seveceği ortak film önerileri üretir.

### 🍿 6. Spoilersız Akıllı Özet & Hedef Kitle (Smart Summary)
* Filmler için spoiler içermeyen 30 saniyelik AI özetleri ve filmin tam olarak hangi hedef kitleye hitap ettiğini gösteren dinamik etiketler oluşturur.

### 📺 7. Türkiye TR İzleme Platformları (Watch Providers)
* TMDB Watch Providers API entegrasyonu ile filmlerin Türkiye'deki yayın platformu (Netflix, BluTV, Prime Video, MUBI vb.) ve direkt izleme bağlantılarını sunar.

### 🛡️ 8. Resilience4j Circuit Breaker & Graceful Fallback
* Harici servislerde (TMDB veya LLM API) yaşanabilecek kesintiler veya oran sınırlarında Resilience4j devreyi keserek önbellekten ve yerel yedek mekanizmalardan kesintisiz hizmet sağlar.

---

## 📐 Matematiksel Model ve Algoritma

Kullanıcı zevk profili vektörü $V_{user}$, etkileşimde bulunulan her bir $i$ filminin 1536 boyutlu gömme vektörü $E_i$ ve bu etkileşimin katsayısı $w_i$ ile aşağıdaki formülle hesaplanır:

$$V_{raw} = \sum_{i=1}^{N} w_i \cdot E_i$$

$$V_{user} = \text{normalize}(V_{raw}) = \frac{V_{raw}}{\|V_{raw}\|_2}$$

### Etkileşim Ağırlık Katsayıları ($w_i$)

| Etkileşim Türü | Ağırlık ($w_i$) | Açıklama |
|---|:---:|---|
| **Favorilere Ekleme** | `+2.5` | En yüksek pozitif zevk sinyali |
| **Yüksek Puan (8 - 10)** | `+2.0` | Güçlü pozitif yönelim |
| **Orta Puan (5 - 7)** | `+1.0` | Ilımlı pozitif yönelim |
| **İzleme Listesi (Watchlist)** | `+1.2` | Gelecek beklenti sinyali |
| **Düşük Puan (1 - 4)** | `-1.5` | **Negatif Yönelim:** Zevk centroidini bu filmden uzaklaştırır |

---

## 🏛️ Sistem Mimarisi

```
                                +-----------------------------------+
                                |     React 19 + Tailwind Client    |
                                +-----------------+-----------------+
                                                  |
                                            HTTPS / REST
                                                  v
                                +-----------------+-----------------+
                                |      Nginx Reverse Proxy          |
                                +-----------------+-----------------+
                                                  |
                                                  v
                                +-----------------+-----------------+
                                |    Spring Boot 3.2 API Gateway    |
                                |  (Security, JWT, Resilience4j)   |
                                +--------+----------------+---------+
                                         |                |
             +---------------------------+                +---------------------------+
             |                                                                        |
             v                                                                        v
+------------+------------+                                              +------------+------------+
| PostgreSQL 16 + pgvector|                                              |     Redis 7.0 Cache       |
| (1536d Vector Search &  |                                              | (Session, Hot Recommendations|
| User Taste Centroids)   |                                              |  & API Rate Limits)     |
+-------------------------+                                              +-------------------------+
             ^                                                                        ^
             |                                                                        |
             +---------------------------+--------------------------------------------+
                                         |
                                         v
                        +----------------+----------------+
                        |  Spring AI & Harici Entegrasyon |
                        | (OpenAI LLM & TMDB Watch API)   |
                        +---------------------------------+
```

---

## 📂 Proje Dizin Yapısı

```
cinepick/
├── backend/                       # Backend Docker build bağlamı
│   └── Dockerfile
├── cinepick/                      # React 19 + Vite + TailwindCSS Frontend Projesi
│   ├── src/                       # Bileşenler, Sayfalar, Context & API İstemcileri
│   ├── package.json
│   └── vite.config.js
├── src/                           # Spring Boot 3.2 Java 21 Backend Kaynak Kodları
│   └── main/
│       ├── java/proje/cinepick/
│       │   ├── api/               # External Integration & Controller uç noktaları
│       │   ├── buisness/          # İş Mantığı & Servis Katmanları
│       │   ├── config/            # Security, Vector DB, Redis & Resiliency Yapılandırmaları
│       │   ├── controller/        # REST API Controller Sınıfları
│       │   ├── dto/               # Data Transfer Objects
│       │   ├── entity/            # JPA Veritabanı Varlıkları
│       │   ├── repository/        # Spring Data JPA & pgvector Repositories
│       │   ├── security/          # JWT & Spring Security Filtreleri
│       │   └── util/              # L2 Vector Math & Yardımcı Sınıflar
│       └── resources/             # application.yml & SQL Migrations
├── k6/                            # k6 Performans & Yük Testi Betikleri
├── monitoring/                    # Prometheus & Grafana Yapılandırmaları
├── nginx/                         # Nginx Prodüksiyon SSL & Routing Konfigürasyonu
├── docker-compose.yml             # Geliştirme Orkestrasyonu
├── docker-compose.prod.yml        # Prodüksiyon Orkestrasyonu
├── Dockerfile                     # Multi-stage Backend Dockerfile
└── pom.xml                        # Maven Bağımlılık Yöneticisi
```

---

## 🛠️ Teknoloji Yığını

### Backend
* **Dil & Framework:** Java 21, Spring Boot 3.2.4
* **AI & Yapay Zeka:** Spring AI 1.0.0, OpenAI Embeddings (text-embedding-3-small, 1536d)
* **Güvenlik:** Spring Security, JWT (JSON Web Token), BCrypt
* **Dayanıklılık & Önbellek:** Resilience4j (Circuit Breaker, RateLimiter), Spring Data Redis
* **Veritabanı Katmanı:** Spring Data JPA, Hibernate, PostgreSQL `pgvector`

### Frontend
* **Framework & UI:** React 19, Vite, Tailwind CSS 3.4
* **State & Routing:** Context API, React Router DOM
* **İstemci:** Axios (JWT Interceptor desteğiyle)

### DevOps & Altyapı
* **Konteynerizasyon:** Docker, Docker Compose (Multi-stage builds)
* **Web Sunucu / Proxy:** Nginx (Reverse Proxy & Rate Limiting)
* **İzleme & Metrikler:** Spring Boot Actuator, Prometheus, Grafana
* **Yük & Performans Testi:** k6, JUnit 5, Testcontainers

---

## 🚀 Hızlı Kurulum & Çalıştırma

### Ön Gereksinimler
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (Docker Compose v2+)
- *Opsiyonel (Yerel Geliştirme İçin):* Java 21 JDK, Node.js 20+, Maven 3.9+

---

### 🐳 Yöntem 1: Docker Compose ile Tek Komutla Çalıştırma (Tavsiye Edilen)

Tüm sistemi (PostgreSQL + pgvector, Redis, Spring Boot Backend ve Nginx + React Frontend) tek bir komutla ayağa kaldırabilirsiniz:

```bash
# 1. Depoyu klonlayın
git clone https://github.com/Omerfaruk1609/Cinepick.git
cd Cinepick

# 2. .env.production veya .env dosyasını oluşturup API anahtarlarınızı girin
cp .env.production .env

# 3. Docker ortamını başlatın
docker-compose up -d --build
```

Servisler hazır olduğunda:
- **Frontend (Web Arayüzü):** `http://localhost`
- **Backend API:** `http://localhost:8080/api/v1`
- **Actuator Health Check:** `http://localhost:8080/actuator/health`

---

### 💻 Yöntem 2: Yerel Geliştirme Ortamı (Local Development)

#### 1. PostgreSQL & Redis Konteynerlerini Başlatın:
```bash
docker-compose up -d postgres redis
```

#### 2. Backend'i Çalıştırın:
```bash
# Bağımlılıkları yükleyin ve projeyi derleyin
./mvnw clean install -DskipTests

# Spring Boot uygulamasını başlatın
./mvnw spring-boot:run
```

#### 3. Frontend'i Çalıştırın:
```bash
cd cinepick
npm install
npm run dev
```
Frontend geliştirme sunucusu `http://localhost:5173` adresinde çalışacaktır.

---

## 🔒 Ortam Değişkenleri & Güvenlik

Uygulamanın çalışabilmesi için gerekli olan ortam değişkenleri `.env` veya `.env.production` dosyasında tanımlanmalıdır:

| Değişken Adı | Zorunlu mu? | Varsayılan | Açıklama |
|---|:---:|---|---|
| `JWT_SECRET` | **Evet** | - | JWT imzalamada kullanılan min 256-bit gizli anahtar |
| `SPRING_AI_OPENAI_API_KEY` | **Evet** | - | Spring AI LLM & Embedding üretimi için OpenAI API Key |
| `TMDB_API_KEY` | **Evet** | - | Film detayları ve yayın sağlayıcıları için TMDB API Key |
| `POSTGRES_DB` | Evet | `cinepickdb` | Veritabanı adı |
| `POSTGRES_USER` | Evet | `cinepickuser` | Veritabanı kullanıcı adı |
| `POSTGRES_PASSWORD` | Evet | `secretpass` | Veritabanı şifresi |
| `REDIS_HOST` | Evet | `redis` | Redis sunucu adresi |
| `REDIS_PORT` | Evet | `6379` | Redis portu |

---

## 📡 API Referansı

| Metot | Endpoint | Kimlik Doğrulama | Açıklama |
|---|---|:---:|---|
| `POST` | `/api/v1/auth/register` | ❌ | Yeni kullanıcı kaydı |
| `POST` | `/api/v1/auth/login` | ❌ | JWT token alma ve giriş |
| `GET` | `/api/v1/movies/onboarding-pool` | ❌ | Soğuk başlangıç film havuzu |
| `GET` | `/api/v1/movies/search` | ❌ | Hibrit film araması (`q`, `mode=keyword\|semantic\|hybrid`) |
| `GET` | `/api/v1/recommendations/personalized` | 🔒 JWT | Vektör tabanlı kişiselleştirilmiş film önerileri |
| `GET` | `/api/v1/users/analytics` | 🔒 JWT | Zevk Analitiği Paneli & Sinema Persona'sı |
| `GET` | `/api/v1/users/blacklist` | 🔒 JWT | Kullanıcı kara listesi (Nefret edilen türler) |
| `POST` | `/api/v1/users/blacklist/genres` | 🔒 JWT | Kara listeye tür ekleme / çıkarma |
| `GET` | `/api/v1/movies/{id}/smart-summary` | ❌ | Spoilersız 30s özet & Target Audience |
| `GET` | `/api/v1/users/friend-match` | 🔒 JWT | İki kullanıcı için sinematik uyum & ortak film önerisi |
| `GET` | `/api/v1/movies/{id}/watch-providers` | ❌ | Türkiye (TR) izleme platformları bağlantıları |

---

## 📊 Benchmark & Performans Ölçümleri

> **Açıklama:** Test sonuçları Testcontainers (PostgreSQL `pgvector` + Redis) ve `k6` yük test ortamlarında elde edilmiş **gerçek ölçüm verileridir (Actual Values)**. Sentetik test veri setinden alınan metrikler prodüksiyon ortamında daha da iyileşmektedir.

### 🎯 Recommendation Quality

**Metodoloji:** Leave-Last-N-Out — 30 filmlik sentetik veri seti ve 3 farklı kullanıcı profili (Action, Drama, Diverse) üzerinde offline evaluation.

```bash
./mvnw test -Dtest="RecommendationMetricsTest"
```

| Metric | Target | Actual | Status | Metodolojik Yorum |
|--------|-------:|-------:|:------:|-------------------|
| **Precision@10** | > %70 | **%50.0** | 🟡 | 10 önerinin 5'i ground-truth ile tam eşleşti |
| **Recall@10** | > %50 | **%50.0** | 🟡 | Kullanıcının ilgilendiği tür kümesi kısmen yakalandı |
| **NDCG@10** | > 0.80 | **0.784** | 🟡 | Logaritmik sıralama kalitesi hedef aralığın hemen altında |
| **Diversity Score** | 0.60 – 0.80 | **0.854** | 🟡 | Shannon Entropy > 0.80; tür çeşitliliği yüksek |
| **Novelty Score** | > 0.30 | **0.064** | 🟡 | Popüler filmlere meyil (Blockbuster bias) |
| **Catalog Coverage** | > %80 | **%56.7** | 🔴 | Kataloğun %56.7'si aktif önerildi |

---

### 🧪 Taste Drift — Centroid Cosine Similarity

**Metodoloji:** Kullanıcı etkileşim sırası (Marvel/Action → Drama → Anime) boyunca kullanıcı zevk centroid vektörünün ($V_{user}$) dinamik kayması.

```bash
./mvnw test -Dtest="TasteDriftBenchmarkTest"
```

| Phase Transition | Target Similarity | Actual | Status | Metodolojik Gerekçe |
|-----------------|------------------:|-------:|:------:|---------------------|
| **Phase 1 (Marvel) → Phase 2 (Drama)** | < 0.85 | **0.7977** | 🟢 | Zevk kayması belirgin başladı (Action boyutu: 0.97 → 0.66) |
| **Phase 2 (Drama) → Phase 3 (Anime)** | < 1.00 | **0.9985** | 🟢 | Yakın tür kümeleri arası kararlı geçiş |
| **Phase 1 (Marvel) → Phase 3 (Anime)** | < 0.85 | **0.7972** | 🟢 | Maksimum zevk uzaklığı (Anime boyutu: **%101.4** artış gösterdi) |

---

### ⚡ Latency & Cache Performance

```bash
./mvnw test -Dtest="LatencyBenchmarkTest,CacheBenchmarkTest"
```

| Metric | Target | Actual | Status | Kapsam Açıklaması |
|--------|-------:|-------:|:------:|-------------------|
| **Redis Hit Latency (p50)** | < 100 ms | **52 ms** | 🟢 | Spring Boot + Redis Jackson serialization + Docker zinciri |
| **Search Latency (p50)** | < 100 ms | **1 ms** | ⚡ | Native SQL + pgvector sorgu süresi |
| **Search Latency (p95)** | < 100 ms | **3 ms** | ⚡ | Kararlı düşük arama gecikmesi |

---

## 🐳 Prodüksiyon & DevOps (SSL & Monitoring)

### 📈 İzleme Altyapısı (Prometheus & Grafana)
Sistem metriklerini canlı izlemek için `monitoring` klasöründeki orkestrasyonu çalıştırabilirsiniz:

```bash
docker-compose -f monitoring/docker-compose.monitoring.yml up -d
```
- **Grafana Dashboard:** `http://localhost:3000` (Kullanıcı: `admin` / Şifre: `admin`)
- **Prometheus Metrics:** `http://localhost:9090`

---

## 📜 Git Commit Geçmişi

```
3b6a9cd feat(providers): integrate TMDB watch providers API for Turkey streaming platforms
17f80c7 feat(ai): integrate Spring AI for single-sentence explainable recommendations
04a1c4c feat(vector): implement VectorMathUtil for L2 normalized centroid calculation
48f7c7f feat(api): create UserMovieInteraction entity and repository for user activities
f98c6ea fix(theme): configure tailwind class-based dark mode and add ThemeContext provider
90b25a8 fix(docker): replace nc check with wget healthcheck for alpine compatibility
```

---

## 🤝 Katkıda Bulunma & Lisans

1. Projeyi çatallayın (Fork).
2. Yeni bir Özellik Dalı oluşturun (`git checkout -b feature/HarikaOzellik`).
3. Değişikliklerinizi commit edin (`git commit -m 'feat: Harika bir özellik eklendi'`).
4. Dalınıza Push edin (`git push origin feature/HarikaOzellik`).
5. Bir Çekme İsteği (Pull Request) açın.

Bu proje **MIT Lisansı** altında lisanslanmıştır. Detaylar için [LICENSE](LICENSE) dosyasına bakabilirsiniz.

---

<div align="center">

**⭐ Beğendiyseniz projeye yıldız vermeyi unutmayın!**

*CinePick — Where AI Meets Cinema*

</div>
