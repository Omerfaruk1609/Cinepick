import axios from 'axios';
import { analyzeNarrative as analyzeNarrativeJS } from '../utils/narrativeEngine';

const API_BASE_URL = 'http://localhost:8080/api/narrative/analyze';

/**
 * Java Spring Boot Backend'deki /api/narrative/analyze POST endpoint'ine istek atarak
 * filmin anlatı ve atmosfer analizini (atmosphere, narrativePace, keyThemes, whyToWatch) çeker.
 * 
 * Eğer Java backend sunucusu kapalıysa veya yanıt vermezse,
 * kesintisiz deneyim için istemci tarafındaki pure JavaScript narrativeEngine devreye girer.
 */
export const fetchMovieInsight = async (movieData) => {
  const payload = {
    title: movieData.title || '',
    overview: movieData.overview || '',
    genres: Array.isArray(movieData.genres)
      ? movieData.genres.map((g) => (typeof g === 'string' ? g : g.name || ''))
      : [],
    runtime: Number(movieData.runtime) || 120,
    voteAverage: Number(movieData.vote_average) || 8.0,
    releaseYear: movieData.release_date
      ? new Date(movieData.release_date).getFullYear()
      : 2020,
  };

  try {
    const response = await axios.post(API_BASE_URL, payload, { timeout: 2500 });
    if (response.data && response.data.atmosphere) {
      return {
        atmosphere: response.data.atmosphere,
        narrativePace: response.data.narrativePace,
        keyThemes: response.data.keyThemes || ['Varoluşçuluk', 'İnsan Doğası'],
        whyToWatch: response.data.whyToWatch,
        source: 'Spring Boot Java Backend',
      };
    }
  } catch (error) {
    console.warn('Spring Boot backend servisine ulaşılamadı, JS motoruna geçiliyor:', error.message);
  }

  // Fallback Pure JS Engine
  const fallback = analyzeNarrativeJS(movieData);
  return {
    atmosphere: fallback.vibe,
    narrativePace: fallback.pace,
    keyThemes: ['Varoluşçuluk', 'Zaman Algısı', 'İnsan Doğası'],
    whyToWatch: fallback.insight,
    source: 'Pure JavaScript Engine',
  };
};
