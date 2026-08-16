import apiClient from './apiClient';

export const updateInteraction = async (movieId, isFavorite, inWatchlist, rating = null) => {
  const response = await apiClient.post('/v1/users/interactions/toggle', {
    movieId,
    isFavorite,
    inWatchlist,
    rating
  });
  return response.data;
};

export const fetchFavorites = async () => {
  const token = localStorage.getItem('token');
  if (!token) return [];
  try {
    const response = await apiClient.get('/v1/users/interactions/favorites');
    return response.data || [];
  } catch (err) {
    return [];
  }
};

export const fetchWatchlist = async () => {
  const token = localStorage.getItem('token');
  if (!token) return [];
  try {
    const response = await apiClient.get('/v1/users/interactions/watchlist');
    return response.data || [];
  } catch (err) {
    return [];
  }
};

export const processOnboarding = async (ratings) => {
  const response = await apiClient.post('/v1/users/interactions/onboarding', ratings);
  return response.data;
};

export const fetchPersonalizedRecommendations = async (genres = [], limit = 10) => {
  try {
    const params = { limit };
    if (genres && genres.length > 0) {
      params.genres = genres.join(',');
    }
    const response = await apiClient.get('/v1/recommendations/personalized', { params });
    return response.data;
  } catch (err) {
    console.warn("Kişiselleştirilmiş öneri çekme hatası:", err);
    return [];
  }
};

export const fetchOnboardingPool = async () => {
  try {
    const response = await apiClient.get('/v1/movies/onboarding-pool');
    return response.data;
  } catch (err) {
    console.warn("Backend onboarding pool çağrısı başarısız, varsayılan havuza geçiliyor:", err);
    return [
      { id: 27205, title: 'Inception', overview: 'Zihin hırsızlığı üzerine sürükleyici bir bilim kurgu başyapıtı.', poster_path: '/oYuLE29W9Z12Y9z2vB2c277u2r.jpg' },
      { id: 157336, title: 'Interstellar', overview: 'İnsanlığın geleceği için solucan deliğinden uzay yolculuğu.', poster_path: '/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg' },
      { id: 155, title: 'The Dark Knight', overview: 'Batman ile Joker arasındaki kaotik felsefi mücadele.', poster_path: '/qJ2tW6WMUDux911r6m7haRef0WH.jpg' },
      { id: 680, title: 'Pulp Fiction', overview: 'Quentin Tarantino\'nun sinematik kült klasiği.', poster_path: '/d5iIlFn5s0ImszYzBPb8SuB1YxW.jpg' },
      { id: 603, title: 'The Matrix', overview: 'Gerçeklik ve simülasyon arasındaki siberpunk yolculuk.', poster_path: '/f89U3w9WFi2V2v2vB2c277u2r.jpg' },
      { id: 550, title: 'Fight Club', overview: 'Tüketim toplumuna ve kimlik krizine radikal bir başkaldırı.', poster_path: '/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg' },
      { id: 496243, title: 'Parasite', overview: 'Sınıf çatışmasını kara mizahla ele alan Oscar ödüllü eser.', poster_path: '/7IiTWWZ9gZ77NI6lCU6MxlNBvIx.jpg' },
      { id: 129, title: 'Spirited Away', overview: 'Hayao Miyazaki\'nin büyüleyici ruhlar alemi fantezisi.', poster_path: '/39xRfiMu2v2v2vB2c277u2r.jpg' },
      { id: 313369, title: 'La La Land', overview: 'Los Angeles\'ta tutku, caz ve aşkın sinematik dansı.', poster_path: '/uDO8zWDhfWwo1Ip4vB2c277u2r.jpg' },
      { id: 244786, title: 'Whiplash', overview: 'Müzik tutkusu ile mükemmeliyetçiliğin karanlık sınırı.', poster_path: '/777xRfiMu2v2v2vB2c277u2r.jpg' },
      { id: 872585, title: 'Oppenheimer', overview: 'Atom bombasının doğuşu ve ahlaki vicdan muhasebesi.', poster_path: '/8Gxv8g7V2v2v2vB2c277u2r.jpg' },
      { id: 438631, title: 'Dune', overview: 'Çöl gezegeninde kehanet, hanedanlık savaşı ve kader.', poster_path: '/d55xRfiMu2v2v2vB2c277u2r.jpg' }
    ];
  }
};

export const syncLocalStorageToBackend = async () => {
  const localFavorites = JSON.parse(localStorage.getItem('favorites') || '[]');
  const localWatchlist = JSON.parse(localStorage.getItem('watchlist') || '[]');

  if (localFavorites.length === 0 && localWatchlist.length === 0) return;

  try {
    // Tüm local öğeleri backend'e aktar
    for (const movie of localFavorites) {
      await updateInteraction(movie.id, true, false);
    }
    for (const movie of localWatchlist) {
      await updateInteraction(movie.id, false, true);
    }
    // Aktarım bittiğinde local veriyi temizle
    localStorage.removeItem('favorites');
    localStorage.removeItem('watchlist');
  } catch (error) {
    console.error("Local veri senkronizasyonu başarısız:", error);
  }
};
