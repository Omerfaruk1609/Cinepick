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
- Sevilen filmler profil vektörünü çekerken, sevilmeyen filmler (Dislike / Düşük Puan) **negatif katsayı** ile sevilmeyen tarzları uzayda ters yöne iter.

### 🎯 2. Hibrit pgvector & Genre Boost Öneri Motoru
- **Cosine Distance (`<=>`)** ile vektör benzerliği hesaplanır.
- PostgreSQL Overlap (`&&`) ile kullanıcının sevdiği türlere ek **Genre Boost (+0.15)** uygulanır.
- İzlenen filmler listeden kesin olarak elenir (`NOT IN`).

### ⚡ 3. Kişiselleştirilmiş % Match Skoru & Renkli Badge
- Her film için dinamik uyum yüzdesi hesaplanır:
  $$\text{Match \%} = \text{Clamp}\left( \Big( (Sim \cdot 0.70) + (GenreBoost \cdot 0.20) + (BaseScore \cdot 0.10) \Big) \times 100, \,\, 50, \,\, 99 \right)$$
- Renk kodlu visual badge'ler (`%85+ Emerald`, `%70-84 Indigo`, `%50-69 Kehribar`).

### 💬 4. Explainable AI (XAI) - Öneri Gerekçesi Üretimi
- Spring AI `ChatClient` ile kullanıcının geçmişte sevdiği filmleri referans göstererek **tek cümlelik kişiselleştirilmiş gerekçeler** üretilir.
- Üretilen açıklamalar Redis üzerinde 7 gün boyunca saklanır (`Duration.ofDays(7)`).

### 🚫 5. Negatif Vektör Filtreleme & Kullanıcı Kara Listesi
- Nefret edilen türler ve yönetmenler profilden yönetilir.
- SQL düzeyinde `NOT (m.genres && :excludedGenres)` ve `NOT (m.director = ANY(:excludedDirectors))` filtreleri çalışır.

### 📊 6. Zevk Analitiği Paneli & Sinema Persona'sı
- **Obscurity Skoru**: İzlenen filmlerin popülerlik (TMDB Vote Count) analizi ile 0-100 arası bağımsız sinema derecelendirmesi.
- **Sinema Persona Classification**: *"Gizli Cevher Avcısı (Indie Cinephile)"*, *"Dengeli Sinefil"*, *"Blockbuster Tutkunu"* vb.
- En çok izlenen türler ve yönetmenler grafiksel kırılımı.

### ⏱️ 7. Spoilersız 30-Second Smart Summary & Target Audience
- Spring AI **Structured Outputs (`BeanOutputConverter`)** ile spoilersız 30 saniyelik özet.
- **👍 Kime Göre?** (Hedef Kitle) ve **👎 Kime Göre Değil?** (Uygun Olmayan Kitle) analiz kartı.

### 🤝 8. Friend Match (Arkadaşınla Birlikte Ne İzleyebilirsiniz?)
- İki farklı kullanıcının zevk vektörleri ($V_{user1}$ ve $V_{user2}$) birleştirilerek ortak zevk vektörü ($V_{group} = \text{normalize}(\frac{V_1 + V_2}{2})$) oluşturulur.
- İkisinin de izlemediği ortak film önerileri ve **% Friendship Match** skoru hesaplanır.

### 📺 9. TMDB Watch Providers (Türkiye TR Dijital Yayın Hakları)
- Filmin Türkiye'deki yayın platformları (Netflix, Prime Video, BluTV, MUBI vb.), kiralama seçenekleri ve JustWatch yönlendirme linkleri canlı gösterilir.

---

## 📐 Matematiksel Modeller

### A. Kullanıcı Profil Vektörü Hesaplama
$$V_{user} = \text{normalize}\left( \sum_{i=1}^{N} w_i \cdot E_i \right)$$

| Etkileşim Tipi | Ağırlık Katsayısı ($w_i$) |
|----------------|--------------------------|
| Favorilere Ekleme (`isFavorite`) | $+1.5$ |
| Yüksek Puan (`rating >= 4.0`) | $+1.2$ |
| İzleme Listesi (`inWatchlist`) | $+0.8$ |
| Düşük Puan / Dislike (`rating <= 2.0`) | $-1.0$ (Negatif İtme) |

### B. Ortak Grup Vektörü ($V_{group}$)
$$V_{group} = \text{normalize}\left( \frac{V_{user1} + V_{user2}}{2} \right)$$

---

## 🏛️ Sistem Mimarisi

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENT LAYER                             │
│       React 19 + Vite + Axios Interceptor + TailwindCSS         │
└─────────────────────────┬───────────────────────────────────────┘
                          │ HTTPS (Port 443)
┌─────────────────────────▼───────────────────────────────────────┐
│              NGINX REVERSE PROXY & SSL GATEWAY                  │
│       SSL Termination (Certbot) + CORS + Security Headers       │
└─────────────────────────┬───────────────────────────────────────┘
                          │ /api/ Proxy
┌─────────────────────────▼───────────────────────────────────────┐
│               SPRING BOOT 3.2 BACKEND (Java 21)                 │
│                                                                 │
│  ┌──────────────┐  ┌────────────────┐  ┌─────────────────────┐  │
│  │ Auth Layer   │  │ Recommendation │  │ Spring AI Engine    │  │
│  │ JWT Security │  │ Engine         │  │ ChatClient +        │  │
│  └──────────────┘  └────────────────┘  │ Structured Output   │  │
│  ┌──────────────┐  ┌────────────────┐  └─────────────────────┘  │
│  │ Vector Math  │  │ Blacklist &    │  ┌─────────────────────┐  │
│  │ L2 Centroid  │  │ Analytics      │  │ TMDB Resilient      │  │
│  └──────────────┘  └────────────────┘  │ Provider Service    │  │
│                                        └─────────────────────┘  │
└────┬─────────────────────┬────────────────────┬─────────────────┘
     │                     │                    │
┌────▼──────┐     ┌────────▼──────┐    ┌───────▼──────────┐
│PostgreSQL  │     │    Redis 7    │    │  Spring AI LLM   │
│+ pgvector  │     │ Vector Cache  │    │  OpenAI / Ollama │
│(1536 dim)  │     │ + TTL Store   │    │                  │
└────────────┘     └───────────────┘    └──────────────────┘
```

---

## 🛠️ Teknoloji Yığını

### Backend
- **Java 21** (Virtual Threads & Records)
- **Spring Boot 3.2.4**
- **Spring AI 1.0.0** (ChatClient & Structured Output Converters)
- **Spring Security & JJWT 0.12.6** (Stateless JWT Auth)
- **Spring Data JPA & Flyway** (Veritabanı migration sürümlendirme `V1`-`V4`)
- **Spring Data Redis** (Vector & Recommendation Cache)

### Veritabanı & Vektör Katmanı
- **PostgreSQL 16** + **pgvector 0.5.1** (HNSW Indexing & Cosine Distance)
- **Redis 7.0 Alpine**

### Frontend
- **React 19** + **Vite 8.2**
- **Tailwind CSS 3.4** (Dark Mode `class` tabanlı)
- **Lucide React** (Modern ikon kütüphanesi)

### DevOps & CI/CD
- **Docker & Docker Compose**
- **Nginx** (Reverse Proxy & Security Headers)
- **Certbot Let's Encrypt** (Otomatik SSL Yenileme)
- **GitHub Actions** (Automated CI/CD Pipeline & SSH Deploy)

---

## 🚀 Hızlı Kurulum & Çalıştırma

### 1. Depoyu Klonlayın
```bash
git clone https://github.com/Omerfaruk1609/Cinepick.git
cd Cinepick
```

### 2. Ortam Değişkenlerini Oluşturun
Proje kök dizininde `.env.production` dosyasını yapılandırın:
```env
DB_URL=jdbc:postgresql://postgres:5432/cinepick_db
DB_USERNAME=cinepick_user
DB_PASSWORD=SuperSecretPassword123!
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION=86400000
REDIS_HOST=redis
REDIS_PORT=6379
TMDB_API_KEY=your_tmdb_api_key_here
```

### 3. Docker Compose ile Tüm Sistemi Başlatın
```bash
docker compose up -d --build
```

---

## 🔒 Ortam Değişkenleri & Güvenlik

| Değişken | Açıklama |
|----------|----------|
| `DB_URL` | PostgreSQL JDBC bağlantı adresi |
| `DB_USERNAME` | Veritabanı kullanıcı adı |
| `DB_PASSWORD` | Veritabanı şifresi |
| `JWT_SECRET` | 256-bit JWT imzalama gizli anahtarı |
| `REDIS_HOST` | Redis sunucu adresi |
| `TMDB_API_KEY` | TMDB API Anahtarı |

---

## 📡 API Referansı

| Metod | Uç Nokta | Açıklama |
|-------|----------|----------|
| `POST` | `/api/v1/auth/register` | Yeni kullanıcı kaydı |
| `POST` | `/api/v1/auth/login` | JWT token ile giriş |
| `POST` | `/api/v1/users/interactions/toggle` | Favori / İzleme listesi / Puan güncelleme |
| `POST` | `/api/v1/users/interactions/onboarding` | Cold-start toplu film oylaması |
| `GET` | `/api/v1/recommendations/personalized` | Kişiselleştirilmiş pgvector öneri listesi |
| `GET` | `/api/v1/users/blacklist` | Kullanıcı kara listesi (Nefret edilen türler) |
| `POST` | `/api/v1/users/blacklist/genres` | Kara listeye tür ekleme / çıkarma |
| `GET` | `/api/v1/users/analytics` | Zevk Analitiği Paneli & Sinema Persona'sı |
| `GET` | `/api/v1/movies/{id}/smart-summary` | Spoilersız 30s özet & Target Audience |
| `GET` | `/api/v1/users/friend-match` | İki kullanıcı için ortak film önerisi |
| `GET` | `/api/v1/movies/{id}/watch-providers` | Türkiye TR izleme platformları |

---

## 🐳 Prodüksiyon & DevOps (SSL & CI/CD)

Projenin canlı ortam dağıtımı tamamen otomatize edilmiştir:
- **Otomatik SSL (Certbot)**: `certbot/certbot:latest` imajı 12 saatte bir Let's Encrypt sertifikasını kontrol edip otomatik yeniler.
- **CI/CD Pipeline (`.github/workflows/deploy.yml`)**: GitHub `main` dalına push yapıldığında unit testler çalışır, Docker imajları GHCR'a yüklenir ve VPS sunucusuna sıfır kesinti (`zero-downtime`) ile otomatik deploy edilir.

---

## 📜 Git Commit Geçmişi

```bash
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
