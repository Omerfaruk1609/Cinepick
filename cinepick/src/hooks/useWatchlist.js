import { useState, useEffect } from 'react';

const STORAGE_KEY = 'cinepick_watchlist';

export function useWatchlist() {
  const [watchlist, setWatchlist] = useState(() => {
    try {
      const saved = localStorage.getItem(STORAGE_KEY);
      return saved ? JSON.parse(saved) : [];
    } catch (err) {
      console.error('LocalStorage okuma hatası:', err);
      return [];
    }
  });

  useEffect(() => {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(watchlist));
    } catch (err) {
      console.error('LocalStorage kaydetme hatası:', err);
    }
  }, [watchlist]);

  const addToWatchlist = (movie) => {
    setWatchlist((prev) => {
      if (prev.some((m) => m.id === movie.id)) return prev;
      return [...prev, movie];
    });
  };

  const removeFromWatchlist = (movieId) => {
    setWatchlist((prev) => prev.filter((m) => m.id !== movieId));
  };

  const isBookmarked = (movieId) => {
    return watchlist.some((m) => m.id === movieId);
  };

  const toggleWatchlist = (movie) => {
    if (isBookmarked(movie.id)) {
      removeFromWatchlist(movie.id);
    } else {
      addToWatchlist(movie);
    }
  };

  return {
    watchlist,
    addToWatchlist,
    removeFromWatchlist,
    isBookmarked,
    toggleWatchlist,
  };
}
