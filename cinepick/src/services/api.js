import axios from 'axios';
import { analyzeNarrative as analyzeNarrativeJS } from '../utils/narrativeEngine';

export const IMAGE_BASE_URL = 'https://image.tmdb.org/t/p/w500';
export const BACKDROP_IMAGE_BASE_URL = 'https://image.tmdb.org/t/p/w1280';

const API_KEY_ENV = import.meta.env.VITE_API_KEY || import.meta.env.VITE_TMDB_API_KEY || '';
const SPRING_BOOT_URL = 'http://localhost:8080/api/narrative/analyze';

const extractApiKey = (raw) => {
  if (!raw) return '';
  const match = raw.match(/apikey=([a-zA-Z0-9]+)/);
  if (match) return match[1];
  if (raw.startsWith('http')) return '';
  return raw;
};

const CLEAN_API_KEY = extractApiKey(API_KEY_ENV);
const IS_TMDB = CLEAN_API_KEY.length > 20 || API_KEY_ENV.includes('themoviedb.org');
const BASE_URL_TMDB = 'https://api.themoviedb.org/3';

const tmdbClient = axios.create({
  baseURL: BASE_URL_TMDB,
  params: {
    api_key: CLEAN_API_KEY,
    language: 'tr-TR',
  },
});

// Popüler Filmler
export const getPopularMovies = async () => {
  if (IS_TMDB && CLEAN_API_KEY) {
    try {
      const response = await tmdbClient.get('/movie/popular');
      return response.data.results;
    } catch (err) {
      console.warn('TMDB API çağrısı başarısız, yedek veri kaynağına geçiliyor:', err);
    }
  }

  try {
    const response = await axios.get('https://api.sampleapis.com/movies/classic');
    return response.data.map(movie => ({
      id: movie.id,
      title: movie.title,
      poster_path: movie.posterURL,
      backdrop_path: movie.posterURL,
      vote_average: 8.5,
      overview: 'Klasik sinematik eser.',
      release_date: '1995-01-01',
      runtime: 120
    }));
  } catch (err) {
    console.error('Film listesi alınamadı:', err);
    return [];
  }
};

// Tür / Ruh Haline Göre Film Getir
export const getMoviesByGenre = async (genreIds = []) => {
  if (!genreIds || genreIds.length === 0) {
    return getPopularMovies();
  }

  const genreString = Array.isArray(genreIds) ? genreIds.join(',') : genreIds;

  if (IS_TMDB && CLEAN_API_KEY) {
    try {
      const response = await tmdbClient.get('/discover/movie', {
        params: {
          with_genres: genreString,
          sort_by: 'popularity.desc',
        },
      });
      return response.data.results;
    } catch (err) {
      console.warn('TMDB discover çağrısı başarısız:', err);
    }
  }

  return getPopularMovies();
};

// Film Detayları (/movie/{movie_id})
export const getMovieDetails = async (movieId) => {
  if (IS_TMDB && CLEAN_API_KEY && typeof movieId !== 'string' && movieId > 100) {
    try {
      const response = await tmdbClient.get(`/movie/${movieId}`);
      return response.data;
    } catch (err) {
      console.warn(`TMDB film detayları alınamadı (ID: ${movieId}):`, err);
    }
  }

  return {
    id: movieId,
    title: 'Örnek Sinema Eseri',
    overview: 'Bu film hakkında kasvetli, felsefi ve derin sinematik incelemeler ve detaylar sunulmaktadır.',
    release_date: '2022-10-15',
    runtime: 134,
    vote_average: 8.4,
    genres: [{ id: 18, name: 'Drama' }, { id: 9648, name: 'Gizem' }],
    poster_path: null,
    backdrop_path: null
  };
};

// Oyuncu ve Ekip Kadrosu (/movie/{movie_id}/credits)
export const getMovieCredits = async (movieId) => {
  if (IS_TMDB && CLEAN_API_KEY && typeof movieId !== 'string' && movieId > 100) {
    try {
      const response = await tmdbClient.get(`/movie/${movieId}/credits`);
      return response.data;
    } catch (err) {
      console.warn(`TMDB film kadrosu alınamadı (ID: ${movieId}):`, err);
    }
  }

  return {
    cast: [
      { id: 1, name: 'Christian Bale', character: 'Ana Karakter', profile_path: null },
      { id: 2, name: 'Cillian Murphy', character: 'Yan Karakter', profile_path: null },
      { id: 3, name: 'Marion Cotillard', character: 'Gizemli Kadın', profile_path: null },
      { id: 4, name: 'Willem Dafoe', character: 'Felsefeci', profile_path: null },
    ],
    crew: [
      { id: 10, name: 'Christopher Nolan', job: 'Director' }
    ]
  };
};

// Narrative Engine (Spring Boot API + JS Fallback Entegrasyonu)
export const fetchNarrativeAnalysis = async (movieData) => {
  const payload = {
    title: movieData.title || '',
    overview: movieData.overview || '',
    genres: Array.isArray(movieData.genres) ? movieData.genres.map(g => typeof g === 'string' ? g : g.name) : [],
    runtime: movieData.runtime || 120,
    voteAverage: movieData.vote_average || 8.0,
    releaseYear: movieData.release_date ? new Date(movieData.release_date).getFullYear() : 2020,
  };

  try {
    const response = await axios.post(SPRING_BOOT_URL, payload, { timeout: 2000 });
    if (response.data && response.data.atmosphere) {
      return {
        vibe: response.data.atmosphere,
        pace: response.data.narrativePace,
        insight: response.data.whyToWatch,
        source: 'Spring Boot Java Backend',
      };
    }
  } catch (err) {
    // Spring Boot kapalı ise istemci tarafı Pure JS Motoru devreye girer
  }

  const jsResult = analyzeNarrativeJS(movieData);
  return { ...jsResult, source: 'Pure JavaScript Engine' };
};

// Arama Yap
export const searchMovies = async (query) => {
  if (!query) return getPopularMovies();

  if (IS_TMDB && CLEAN_API_KEY) {
    try {
      const response = await tmdbClient.get('/search/movie', {
        params: { query },
      });
      return response.data.results;
    } catch (err) {
      console.warn('TMDB arama başarısız:', err);
    }
  }

  const allMovies = await getPopularMovies();
  return allMovies.filter(m => m.title.toLowerCase().includes(query.toLowerCase()));
};
