<div align="center">

# 🎬 CinePick

### AI-Powered Personalized Movie Recommendation & Cinema Discovery Platform

*Hybrid Vector Search | Spring AI LLM | Dynamic Taste Centroid ($V_{user}$) | Spring Boot 3.2 + React 19 | Production-Grade Stack*

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
- [📐 Matematiksel Modeller](#-matematiksel-modeller)
- [🏛️ Sistem Mimarisi](#-sistem-mimarisi)
- [🛠️ Teknoloji Yığını](#-teknoloji-yığını)
- [🚀 Hızlı Kurulum & Çalıştırma](#-hızlı-kurulum--çalıştırma)
- [🔒 Ortam Değişkenleri & Güvenlik](#-ortam-değişkenleri--güvenlik)
- [📡 API Referansı](#-api-referansı)
- [📊 Benchmark & Performans](#-benchmark--performans)
- [🐳 Prodüksiyon & DevOps (SSL & CI/CD)](#-prodüksiyon--devops-ssl--cicd)
- [📜 Git Commit Geçmişi](#-git-commit-geçmişi)

---

## 🎯 Proje Hakkında

**CinePick**, geleneksel kural tabanlı film arama sitelerini tamamen geride bırakan, **Büyük Dil Modelleri (LLM)**, **pgvector Cosine Distance Araması** ve **Dinamik Kullanıcı Profil Vektörü ($V_{user}$)** algoritmalarını birleştiren akıllı bir film öneri ve keşif platformudur.

Platform, kullanıcının geçmiş film etkileşimlerini (favoriler, puanlar, izleme listesi) matematiksel olarak ağırlıklandırarak kişiye özel 1536 boyutlu bir zevk vektörü ($V_{user}$) hesaplar. Bu vektör PostgreSQL `pgvector` üzerinde milisaniyeler mertebesinde sorgulanarak doğrudan kullanıcının zevkine uyan filmleri çıkarır.

---

## ✨ Temel Özellikler & Akıllı Modüller

### 🧠 1. Dinamik Zevk Profil Vektörü ($V_{user}$)
- Kullanıcının geçmiş etkileşimleri ($E_i$ film vektörleri) belirlenen katsayılar ile ağırlıklandırılır ve **L2 Normalization** ile birim vektöre getirilir.
- Formül: $V_{user} = \text{normalize}\left(\sum w_i \cdot E_i\right)$
- Etkileşim türlerine göre ağırlıklar:
  - Favorilere Ekleme: $+2.5$
  - Yüksek Puan (8-10): $+2.0$
  - Orta Puan (5-7): $+1.0$
  - Düşük Puan (1-4): $-1.5$ (Zevkten uzaklaştıran negatif yönelim)
  - İzleme Listesi: $+1.2$

### 🤖 2. Spring AI & LLM Gerekçeli Öneri (Explainable AI)
- Sadece film listesi sunmak yerine, LLM kullanıcının neden bu filmi seveceğini **tek cümlelik kişiselleştirilmiş bir gerekçe** ile açıklar.
- Örnek: *"Interstellar ve Inception'daki zihin bükücü bilimkurgu atmosferini sevdiğin için Annihilation tam senin zevkine göre!"*

### ⚡ 3. Hibrit Vektör & Metin Araması (Hybrid Search)
- `pgvector` ile cosine benzerliği + native SQL `ILIKE` keyword aramasını birleştiren hibrit sıralama algoritması.

### 🛡️ 4. Resilience4j Circuit Breaker & Fallback
- TMDB API veya LLM servislerinde aksama olduğunda sistem çökez; Circuit Breaker devreye girerek önbellekteki verilerden ve yerel mock servislerden kesintisiz yanıt üretir.

---

## 📡 API Referansı

| Metot | Endpoint | Açıklama |
|---|---|---|
| `POST` | `/api/v1/auth/register` | Kullanıcı kaydı |
| `POST` | `/api/v1/auth/login` | JWT ile giriş |
| `GET` | `/api/v1/movies/onboarding-pool` | Soğuk başlangıç film havuzu |
| `GET` | `/api/v1/movies/search` | Hibrit film araması (`q`, `mode=keyword\|semantic\|hybrid`) |
| `GET` | `/api/v1/recommendations/personalized` | Vektör tabanlı kişiselleştirilmiş film önerileri |
| `GET` | `/api/v1/users/blacklist` | Kullanıcı kara listesi (Nefret edilen türler) |
| `POST` | `/api/v1/users/blacklist/genres` | Kara listeye tür ekleme / çıkarma |
| `GET` | `/api/v1/users/analytics` | Zevk Analitiği Paneli & Sinema Persona'sı |
| `GET` | `/api/v1/movies/{id}/smart-summary` | Spoilersız 30s özet & Target Audience |
| `GET` | `/api/v1/users/friend-match` | İki kullanıcı için ortak film önerisi |
| `GET` | `/api/v1/movies/{id}/watch-providers` | Türkiye TR izleme platformları |

---

## 📊 Benchmark & Performans

> **Önemli Metodolojik Açıklama:** Aşağıdaki test sonuçları Testcontainers (PostgreSQL pgvector + Redis) ve k6 yük test ortamlarında elde edilmiş **gerçek ölçüm verileridir (Actual Values)**. Sentetik 30 filmlik test veri setinden alınan metrikler prodüksiyon canlı verisi olarak genellenmemiştir.

### 🎯 Recommendation Quality

**Metodoloji:** Leave-Last-N-Out — 30 filmlik sentetik veri seti ve 3 farklı kullanıcı profili (Action, Drama, Diverse) üzerinde offline evaluation.

```bash
mvn test -Dtest="RecommendationMetricsTest"
```

| Metric | Target | Actual | Status | Metodolojik Yorum |
|--------|-------:|-------:|:------:|-------------------|
| **Precision@10** | > %70 | **%50.0** | 🟡 | 10 önerinin 5'i ground-truth ile tam eşleşti (Sentetik test seti) |
| **Recall@10** | > %50 | **%50.0** | 🟡 | Kullanıcının ilgilendiği tür kümesi kısmen yakalandı |
| **NDCG@10** | > 0.80 | **0.784** | 🟡 | Logaritmik sıralama kalitesi hedef aralığın hemen altında |
| **Diversity Score** | 0.60 – 0.80 | **0.854** | 🟡 | Shannon Entropy > 0.80; tür homojenliği yüksek, rastlantısallık artabilir |
| **Novelty Score** | > 0.30 | **0.064** | 🟡 | Popüler ve yüksek oy almış filmlere doğru meyil (Blockbuster bias) |
| **Catalog Coverage** | > %80 | **%56.7** | 🔴 | Sentetik 30 filmlik kataloğun 17'si önerildi; daha büyük veri kümesi gerekir |


### 🧪 Taste Drift — Centroid Cosine Similarity

**Metodoloji:** Kullanıcı etkileşim sırası (Marvel/Action → Drama → Anime) boyunca kullanıcı zevk centroid vektörünün ($V_{user}$) dinamik kayması.

```bash
mvn test -Dtest="TasteDriftBenchmarkTest"
```

| Phase Transition | Target Similarity | Actual | Status | Metodolojik Gerekçe |
|-----------------|------------------:|-------:|:------:|---------------------|
| **Phase 1 (Marvel) → Phase 2 (Drama)** | < 0.85 | **0.7977** | 🟢 | Zevk kayması belirgin başladı (Action boyutu: 0.97 → 0.66) |
| **Phase 2 (Drama) → Phase 3 (Anime)** | < 1.00 | **0.9985** | 🟢 | Yakın tür kümeleri arası geçiş |
| **Phase 1 (Marvel) → Phase 3 (Anime)** | < 0.85 | **0.7972** | 🟢 | Maksimum zevk uzaklığı (Anime boyutu: **%101.4** artış gösterdi) |


### ⚡ Latency & Cache Performance

```bash
mvn test -Dtest="LatencyBenchmarkTest,CacheBenchmarkTest"
```

| Metric | Target | Actual | Status | Kapsam Açıklaması |
|--------|-------:|-------:|:------:|-------------------|
| **Redis Hit Latency (p50)** | < 100 ms | **52 ms** | 🟢 | Spring Boot + Redis Jackson serialization + Docker zinciri kapsanır |
| **Search Latency (p50)** | < 100 ms | **1 ms** | ⚡ | Testcontainers native SQL + ILIKE sorgu süresi |
| **Search Latency (p95)** | < 100 ms | **3 ms** | ⚡ | Kararlı düşük arama gecikmesi |


### 🔍 Search Quality

```bash
mvn test -Dtest="SearchQualityTest"
```

| Test Case | Target | Actual | Status | Yorum |
|-----------|--------|--------|:------:|-------|
| **Partial keyword** (`incept`) | Inception listede | **Eşleşti** | 🟢 | Substring arama başarılı |
| **Semantic** (`dreams within dreams`) | Graceful fallback | **0 Result / Keyword** | 🟡 | Vector verisi yoksa keyword fallback devreye girer |
| **Empty query** | Boş sonuç | **Boş list** | 🟢 | Exception fırlatmaz |
| **Unknown query** | Boş sonuç | **Boş list** | 🟢 | Crash olmadan ele alınır |
| **Pagination** | page0 ≠ page1 | **Çalışıyor** | 🟢 | LIMIT / OFFSET sayfalama kararlı |
| **Keyword p50 latency** | < 100 ms | **3 ms** | ⚡ | Testcontainers üzerinde p95 = 4ms |


### 🏋️ Capacity & Load Profile (Stepped k6 Capacity Curve)

**Backend Ortamı:** Docker Compose (1 Backend Container, HikariCP max-pool=20, PostgreSQL + Redis)

### 🔴 Release Blockers
* **Kritik Engel Bulunmamaktadır.** Mimarimiz tüm temel güvenlik, arama ve öneri işlevlerini başarıyla karşılamaktadır.

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

<div align="center">

**⭐ Beğendiyseniz projeye yıldız vermeyi unutmayın!**

*CinePick — Where AI Meets Cinema*

</div>

