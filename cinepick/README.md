# 🎬 CinePick — Sinematik & Felsefi Film Keşif Platformu

<div align="center">

![React](https://img.shields.io/badge/React-19-61DAFB?style=for-the-badge&logo=react&logoColor=black)
![Vite](https://img.shields.io/badge/Vite-6.0-646CFF?style=for-the-badge&logo=vite&logoColor=white)
![TailwindCSS](https://img.shields.io/badge/Tailwind_CSS-3.4-38BDF8?style=for-the-badge&logo=tailwindcss&logoColor=white)
![TMDB API](https://img.shields.io/badge/TMDB_API-v3-01B4E4?style=for-the-badge&logo=themoviedatabase&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

<p align="center">
  <b>CinePick</b>, kullanıcıların anlık ruh hallerine (<i>Kasvetli, Zihin Bükücü, Adrenalin, Nostaljik...</i>) göre filmleri keşfetmesini sağlayan, karanlık ve sinematik temaya sahip modern bir web uygulamasıdır.
</p>

</div>

---

## 🌟 Öne Çıkan Özellikler

- 🎭 **Ruh Hali Tabanlı Filtreleme (MoodSelector)**:
  - *Kasvetli & Felsefi* (Derin dram ve gizem)
  - *Adrenalin & Kaos* (Tempolu aksiyon ve gerilim)
  - *Zihin Bükücü* (Algıları zorlayan bilim kurgu)
  - *Nostaljik & Tarihi* (Geçmiş dönem klasikleri)
  - *Sıcak & Eğlenceli* (Neşeli ve iç ısıtan hikayeler)
  - *Korku & Karanlık* (Ürpertici atmosferler)

- 🍿 **Sinematik Film Detay Modalı (MovieModal)**:
  - Yüksek çözünürlüklü kapak (`backdrop_path`) ve degrade kaplama.
  - Yönetmen, çıkış yılı, film süresi ve tür etiketleri.
  - Film özeti ve öne çıkan 5 oyuncu kadrosu.
  - ESC tuşu ve dış alana tıklama ile akıcı kapatma desteği.

- 🔖 **Kişisel İzleme Listesi (Watchlist)**:
  - `localStorage` entegrasyonu ile favori filmleri tarayıcıda kalıcı saklama.
  - Tek tıkla ekleme/çıkarma ve dinamik rozet sayaçları.
  - Boş liste durumunda yönlendirici özel arayüz.

- ⚡ **Ultra Hızlı Performans**:
  - Vite derleme altyapısı, Lucide ikoları ve Tailwind CSS ile optimize edilmiş modern UI.

---

## 🛠️ Teknoloji Yığını

| Katman | Teknoloji | Açıklama |
| :--- | :--- | :--- |
| **Frontend Framework** | React 19 | Bileşen tabanlı kullanıcı arayüzü |
| **Build Tool** | Vite 8 | Ultra hızlı modül paketleme & HMR |
| **Styling** | Tailwind CSS 3 | Karanlık sinematik tasarım ve cam efekti (`backdrop-blur`) |
| **HTTP Client** | Axios | REST API istekleri |
| **İkonlar** | Lucide React | Modern ve hafif SVG ikon seti |
| **API** | TMDB (The Movie Database) | Film ve oyuncu verileri |

---

## 📁 Proje Klasör Yapısı

```text
cinepick/
├── src/
│   ├── assets/             # Statik görseller ve grafikler
│   ├── components/         # Modüler UI bileşenleri
│   │   ├── Header.jsx          # Üst navigasyon ve sekme geçişi
│   │   ├── MovieCard.jsx       # Film kartı bileşeni
│   │   ├── MovieModal.jsx      # Detay modal penceresi
│   │   ├── MoodSelector.jsx    # Ruh hali filtreleme butonları
│   │   └── EmptyWatchlist.jsx  # Boş liste uyarı arayüzü
│   ├── data/               # Ruh hali veri haritası (moods.js)
│   ├── hooks/              # Özel React hook'ları (useWatchlist.js)
│   ├── services/           # TMDB API servis katmanı (api.js)
│   ├── App.jsx             # Ana uygulama orkestrasyonu
│   ├── index.css           # Global Tailwind direktifleri & scrollbar
│   └── main.jsx            # React giriş noktası
├── .env                    # Ortam değişkenleri (API Key)
├── tailwind.config.js      # Tailwind CSS yapılandırması
├── postcss.config.js       # PostCSS ayarları
└── package.json            # Proje bağımlılıkları
```

---

## 🚀 Kurulum ve Çalıştırma

Projeyi yerel ortamınızda çalıştırmak için aşağıdaki adımları takip edebilirsiniz:

### 1. Depoyu Klonlayın veya Klasöre Gidin

```bash
cd cinepick
```

### 2. Bağımlılıkları Yükleyin

```bash
npm install
```

### 3. Ortam Değişkenlerini Tanımlayın

Projenin kök dizininde bir `.env` dosyası oluşturun ve TMDB API anahtarınızı ekleyin:

```env
VITE_API_KEY=your_tmdb_api_key_here
```

### 4. Geliştirme Sunucusunu Başlatın

```bash
npm run dev
```

Uygulama varsayılan olarak `http://localhost:5173` adresinde açılacaktır.

---

## 📄 Lisans

Bu proje **MIT Lisansı** ile lisanslanmıştır. Dilediğiniz gibi kullanabilir ve geliştirebilirsiniz.
