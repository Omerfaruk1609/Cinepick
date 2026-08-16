# 🎬 CinePick — AI Destekli Yeni Nesil Film Öneri & Keşif Platformu (V2)

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen.svg" alt="Spring Boot 3.2" />
  <img src="https://img.shields.io/badge/Java-21-orange.svg" alt="Java 21" />
  <img src="https://img.shields.io/badge/PostgreSQL-16%20%2B%20pgvector-blue.svg" alt="PostgreSQL pgvector" />
  <img src="https://img.shields.io/badge/React-18%20%2B%20Vite-61dafb.svg" alt="React Vite" />
  <img src="https://img.shields.io/badge/Local%20AI-ONNX%20all--MiniLM--L6--v2-purple.svg" alt="ONNX Runtime" />
  <img src="https://img.shields.io/badge/License-MIT-yellow.svg" alt="MIT License" />
</p>

CinePick, sinemaseverler için **geleneksel kategori filtrelerinin ötesine geçen**, **384 boyutlu anlamsal vektör uzayı (pgvector)**, **sıfır harici API maliyetli yerel ONNX yapay zekâ modeli**, **Bayesian IMDb derecelendirme algoritması** ve **etkileşimli AI soru sihirbazı** ile donatılmış yeni nesil akıllı film öneri platformudur.

---

## 🌟 Öne Çıkan Özellikler (V2 Architecture)

### 1. 🧠 Sıfır Harici API Maliyetli Yerel AI (ONNX Runtime)
- Microsoft ONNX Runtime ve Hugging Face DJL Tokenizer entegrasyonu sayesinde harici LLM servislerine bağımlı olmadan (`all-MiniLM-L6-v2`) yerel olarak **384 boyutlu dense vector embedding** üretir.
- Mean pooling ve L2 normalizasyonu ile milisaniyeler içerisinde anlamsal (semantic) metin analizleri gerçekleştirir.

### 2. 🎯 Akıllı & Kişiselleştirilmiş Vektör Profil Motoru
- **Time-Decay Formülü**: Kullanıcının izleme ve puanlama geçmişi zamanla azalan ağırlıklandırma ($w_i = e^{-\lambda \cdot \Delta t}$) ile işlenerek dinamik tekil $V_{user}$ zevk profili vektörüne dönüştürülür.
- **Puanlama Etkisi**: 7–10 yıldız verilen filmler pozitif ilgi alanlarını genişletirken, <5 puan alan yapımlar elenir.
- **Cold-Start Koruması**: Yeni kullanıcılarda IMDb Top 250 Bayesian ağırlıklı algoritması devreye girer.

### 3. 🍿 5.000+ Gerçek TMDB Kataloğu & Türkiye Yayın Platformları
- 1970–2026 yılları arasındaki 5.000 seçkin yapım Türkçe sinopsis, afiş ve meta verileriyle içe aktarılmıştır.
- **Watch Providers Entegrasyonu**: Her film için Türkiye'deki yayın platformları (*Netflix, Amazon Prime Video, Disney+, BluTV, TOD, TV+*) taranıp indekslenmiştir.

### 4. 🪄 4 Farklı Keşif Modu
- 🪄 **AI Film Sihirbazı**: Yayın platformu, atmosfer ve dönem tercihlerini yapay zeka eşleşme yüzdesiyle (%98 Uyum) analiz eder.
- 🔍 **Gelişmiş Filtreleme**: Dil (Yerli/Yabancı), minimum puan, maksimum süre, yıl aralığı ve yayın platformu ile anında hibrit arama.
- ✨ **Niyetle Keşfet (Intent Discovery)**: *"Cuma akşamı kafa yormayan ama zekice yazılmış komedi"* gibi serbest doğal dil cümlelerini vektörel olarak çözer.
- 😊 **Ruh Hali Keşfi (Mood Selector)**: Melankolik, Enerjik, Gerilimli, Zihin Büken ve Neşeli hazır modlarla anında eşleşme.

### 5. 📑 Şeffaf & Tekrarsız Film Analiz Kartı
- **Film Özeti**: Resmi TMDB hikaye örgüsü.
- **30 Saniyelik Akıllı Özet**: Spoilersız tek cümlelik merak uyandırıcı kanca.
- **Neden İzlemelisin?**: Sinematografik ve tematik eleştirmen görüşü.
- **Kime Göre? / Kime Göre Değil?**: Net izleyici profili eşleştirmesi.

---

## 🏗️ Mimari & Teknoloji Yığını

| Katman | Teknoloji / Kütüphane |
|---|---|
| **Backend** | Spring Boot 3.2.4, Java 21, Spring Data JPA, Spring Security (JWT) |
| **Yapay Zekâ (AI)** | Microsoft ONNX Runtime 1.17.1, Hugging Face Tokenizers (`all-MiniLM-L6-v2`) |
| **Veritabanı** | PostgreSQL 16 + `pgvector` eklentisi (HNSW Cosine Vector Index) |
| **Önbellek (Cache)** | Redis 7 + Spring Cache |
| **Frontend** | React 18, Vite, Tailwind CSS, Lucide Icons, Axios |
| **DevOps & CI/CD** | Docker Multi-Stage, Docker Compose, GitHub Actions, Nginx (SPA + Reverse Proxy) |

---

## 🚀 Hızlı Başlangıç & Kurulum

### Gereksinimler
- Docker & Docker Desktop
- Java 21 & Maven 3.9+ (Geliştirme için)
- Node.js 20+ (Geliştirme için)

### 1. Depoyu Klonlayın
```bash
git clone https://github.com/Omerfaruk1609/Cinepick.git
cd Cinepick
```

### 2. Docker Compose ile Tüm Sistemi Başlatın
```bash
docker compose up -d
```
Tüm servisler otomatik olarak ayağa kalkacaktır:
- 🌐 **Frontend (Web Arayüzü)**: `http://localhost:5173` (veya Docker ile `http://localhost:80`)
- ⚙️ **Backend REST API**: `http://localhost:8080/api`
- 🗄️ **PostgreSQL (pgvector)**: `localhost:5433`
- ⚡ **Redis**: `localhost:6379`

---

## 📡 API Endpoint Özeti

| Metot | Endpoint | Açıklama |
|---|---|---|
| `GET` | `/api/v1/movies/popular?limit=50` | Bayesian ağırlıklı popüler filmler |
| `POST` | `/api/v1/movies/filter` | Platform, tür, yıl, dil ve süreye göre hibrit filtreleme |
| `POST` | `/api/v1/movies/mood-recommendation` | Ruh haline göre vektörel öneriler |
| `POST` | `/api/v1/movies/intent-discovery` | Doğal dil niyet bazlı anlamsal keşif |
| `POST` | `/api/v1/movies/wizard-discovery` | 3 adımlı sihirbaz anket sonuçları |
| `POST` | `/api/v1/user-interactions/rate` | Filme 1-10 puan verme ve profil vektörünü güncelleme |
| `POST` | `/api/admin/catalog/bulk-import-5k` | 5.000+ filmi TMDB'den toplu içe aktarma |

---

## 🧪 Test & Kalite Güvencesi

```bash
# Tüm Backend Birim ve Entegrasyon Testlerini Çalıştır
mvn test

# Frontend Derleme Testi
cd cinepick && npm run build
```

---

## 📄 Lisans
Bu proje **MIT** lisansı altında geliştirilmiştir.
Tüm hakları saklıdır © 2026 CinePick Ekibi.
