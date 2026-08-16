import axios from 'axios';
import apiClient from './apiClient';

export const IMAGE_BASE_URL = 'https://image.tmdb.org/t/p/w500';
export const BACKDROP_IMAGE_BASE_URL = 'https://image.tmdb.org/t/p/w1280';

// TMDB Genre Id to Genre Name Mapping
export const GENRE_MAP = {
  28: 'Aksiyon',
  12: 'Macera',
  16: 'Animasyon',
  35: 'Komedi',
  80: 'Suç',
  99: 'Belgesel',
  18: 'Dram',
  10751: 'Aile',
  14: 'Fantezi',
  36: 'Tarih',
  27: 'Korku',
  10402: 'Müzik',
  9648: 'Gizem',
  10749: 'Romantik',
  878: 'Bilim Kurgu',
  53: 'Gerilim',
  10752: 'Savaş',
  37: 'Vahşi Batı'
};

export const cleanTitle = (title) => {
  if (!title || typeof title !== 'string') return title || '';
  return title.replace(/\s+(Vol\.\s*\d+|\d+)$/i, '').trim();
};

export const formatRuntime = (minutes) => {
  if (!minutes || minutes <= 0) return 'Bilinmiyor';
  const hours = Math.floor(minutes / 60);
  const mins = minutes % 60;
  if (hours === 0) return `${mins}dk`;
  return mins > 0 ? `${hours}s ${mins}dk` : `${hours}s`;
};

// 1. Popüler & Öne Çıkan Filmler (5.000+ Gerçek Film Veritabanından)
export const getPopularMovies = async (limit = 5000, page = 0) => {
  try {
    const response = await apiClient.get(`/v1/movies/popular?limit=${limit}&page=${page}`);
    if (Array.isArray(response.data) && response.data.length > 0) {
      return response.data;
    }
  } catch (error) {
    console.error('getPopularMovies API error:', error);
  }

  // Backend fallback filter
  try {
    const fallbackRes = await apiClient.post('/v1/movies/filter', { limit, page });
    return fallbackRes.data || [];
  } catch (e) {
    return [];
  }
};

// 2. Tür Filtreleme (15.000+ Gerçek Film İçerisinden)
export const getMoviesByGenre = async (genreIds = [], limit = 15000, page = 0) => {
  if (!genreIds || genreIds.length === 0 || genreIds.includes(0)) {
    return getPopularMovies(limit, page);
  }

  const genreNames = Array.isArray(genreIds)
    ? genreIds.map(id => typeof id === 'number' ? GENRE_MAP[id] || String(id) : id).filter(Boolean)
    : [typeof genreIds === 'number' ? GENRE_MAP[genreIds] : genreIds];

  try {
    const response = await apiClient.post('/v1/movies/filter', {
      genres: genreNames,
      limit,
      page
    });
    return response.data || [];
  } catch (error) {
    console.error('getMoviesByGenre API error:', error);
    return [];
  }
};

// 3. Genel Arama (Keyword / Semantic / Hybrid)
export const searchMovies = async (query, limit = 50, page = 0) => {
  if (!query || !query.trim()) {
    return getPopularMovies(limit, page);
  }

  try {
    const response = await apiClient.get(`/v1/movies/search?q=${encodeURIComponent(query.trim())}&size=${limit}&page=${page}&mode=hybrid`);
    if (response.data && response.data.movies) {
      return response.data.movies;
    }
  } catch (error) {
    console.error('searchMovies API error, falling back to intent discovery:', error);
  }

  try {
    const intentRes = await apiClient.post('/v1/movies/intent-discovery', { query: query.trim(), limit });
    return intentRes.data || [];
  } catch (e) {
    return [];
  }
};

// 4. Hibrit Filtreleme (Platform, Tür, Dil, Yıl, Süre, Puan)
export const filterMovies = async (filterData, userId = null) => {
  try {
    const response = await apiClient.post(`/v1/movies/filter${userId ? `?userId=${userId}` : ''}`, filterData);
    return response.data || [];
  } catch (error) {
    console.error('Filter movies API error:', error);
    return [];
  }
};

// 5. İnteraktif AI Film Sihirbazı (Questionnaire Wizard)
export const getWizardRecommendations = async (wizardData) => {
  try {
    const response = await apiClient.post('/v1/movies/wizard-discovery', wizardData);
    return response.data || [];
  } catch (error) {
    console.error('Wizard recommendations API error:', error);
    return [];
  }
};

// 6. Ruh Hali Önerileri (Mood-Based Recommendations)
export const getMoodRecommendations = async (moodData) => {
  try {
    const response = await apiClient.post('/v1/movies/mood-recommendation', moodData);
    return response.data || [];
  } catch (error) {
    console.error('Mood recommendations API error:', error);
    return [];
  }
};

// 7. Niyet Bazlı Keşif (Intent-Based Discovery)
export const discoverByIntent = async (prompt, limit = 20) => {
  try {
    const payload = typeof prompt === 'string' ? { query: prompt, limit } : prompt;
    const response = await apiClient.post('/v1/movies/intent-discovery', payload);
    return response.data || [];
  } catch (error) {
    console.error('Intent discovery API error:', error);
    return [];
  }
};

// 8. Kişiselleştirilmiş Öneriler
export const getPersonalizedRecommendations = async (genres = null, limit = 10) => {
  try {
    const params = new URLSearchParams();
    if (genres && genres.length > 0) {
      params.append('genres', genres.join(','));
    }
    params.append('limit', limit);

    const response = await apiClient.get(`/v1/recommendations/personalized?${params.toString()}`);
    return response.data || [];
  } catch (error) {
    console.error('Personalized recommendations API error:', error);
    return [];
  }
};

// 9. Film Detayları
export const getMovieDetails = async (movieId, initialMovieData = null) => {
  if (initialMovieData && initialMovieData.title) {
    return initialMovieData;
  }
  return {
    id: movieId,
    title: 'Sinematik Film Eseri',
    overview: 'Bu film hakkında sinematik inceleme ve atmosfer bilgileri.',
    release_date: '2023-01-01',
    runtime: 120,
    vote_average: 8.0,
    original_language: 'tr',
    genres: ['Dram'],
    poster_path: null
  };
};

export const getMovieCredits = async (movieId) => {
  return { cast: [], crew: [] };
};

export const getMovieVideos = async (movieId) => {
  return null;
};

export const getMovieWatchProviders = async (movieId) => {
  try {
    const res = await apiClient.get(`/v1/movies/${movieId}/watch-providers`);
    return res.data;
  } catch (e) {
    return null;
  }
};

// 10. Admin: 5.000+ Film Toplu Çekim Tetikleme
export const bulkImport5kMovies = async () => {
  try {
    const response = await apiClient.post('/admin/catalog/bulk-import-5k');
    return response.data;
  } catch (error) {
    console.error('Bulk import API error:', error);
    return { status: 'error', message: error.message };
  }
};
