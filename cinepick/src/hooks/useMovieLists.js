import { useState, useEffect } from 'react';

const WATCHLIST_KEY = 'cinepick_watchlist';
const WATCHED_KEY = 'cinepick_watched';

export function useMovieLists() {
  const [watchlist, setWatchlist] = useState(() => {
    try {
      const saved = localStorage.getItem(WATCHLIST_KEY);
      return saved ? JSON.parse(saved) : [];
    } catch (err) {
      console.error('Watchlist okuma hatası:', err);
      return [];
    }
  });

  const [watched, setWatched] = useState(() => {
    try {
      const saved = localStorage.getItem(WATCHED_KEY);
      return saved ? JSON.parse(saved) : [];
    } catch (err) {
      console.error('Watched list okuma hatası:', err);
      return [];
    }
  });

  useEffect(() => {
    try {
      localStorage.setItem(WATCHLIST_KEY, JSON.stringify(watchlist));
    } catch (err) {
      console.error('Watchlist kaydetme hatası:', err);
    }
  }, [watchlist]);

  useEffect(() => {
    try {
      localStorage.setItem(WATCHED_KEY, JSON.stringify(watched));
    } catch (err) {
      console.error('Watched list kaydetme hatası:', err);
    }
  }, [watched]);

  const isInWatchlist = (movieId) => watchlist.some((m) => m.id === movieId);
  const isWatched = (movieId) => watched.some((m) => m.id === movieId);

  const toggleWatchlist = (movie) => {
    if (isInWatchlist(movie.id)) {
      setWatchlist((prev) => prev.filter((m) => m.id !== movie.id));
    } else {
      setWatchlist((prev) => [...prev, movie]);
      // Eğer izlediklerim listesindeyse, izleyeceklerim'e eklenince izlediklerimden kaldırılabilir
      setWatched((prev) => prev.filter((m) => m.id !== movie.id));
    }
  };

  const toggleWatched = (movie) => {
    if (isWatched(movie.id)) {
      setWatched((prev) => prev.filter((m) => m.id !== movie.id));
    } else {
      setWatched((prev) => [...prev, movie]);
      // İzlediklerime eklenince izleyeceklerim listesinden otomatik çıkarılır
      setWatchlist((prev) => prev.filter((m) => m.id !== movie.id));
    }
  };

  return {
    watchlist,
    watched,
    isInWatchlist,
    isWatched,
    toggleWatchlist,
    toggleWatched,
  };
}
