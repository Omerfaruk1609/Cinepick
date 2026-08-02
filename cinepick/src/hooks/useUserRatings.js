import { useState, useEffect } from 'react';

const RATINGS_KEY = 'cinepick_user_ratings';

export function useUserRatings() {
  const [ratings, setRatings] = useState(() => {
    try {
      const saved = localStorage.getItem(RATINGS_KEY);
      return saved ? JSON.parse(saved) : {};
    } catch {
      return {};
    }
  });

  useEffect(() => {
    try {
      localStorage.setItem(RATINGS_KEY, JSON.stringify(ratings));
    } catch (err) {
      console.error('Rating kaydetme hatası:', err);
    }
  }, [ratings]);

  const getRating = (movieId) => ratings[movieId] || 0;

  const setMovieRating = (movieId, score) => {
    setRatings((prev) => ({
      ...prev,
      [movieId]: score,
    }));
  };

  return {
    ratings,
    getRating,
    setMovieRating,
  };
}
