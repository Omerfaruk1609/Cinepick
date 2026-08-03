import { useState, useEffect } from 'react';
import { updateInteraction, fetchWatchlist, fetchFavorites, syncLocalStorageToBackend } from '../services/interactionApi';

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
    const token = localStorage.getItem('token');
    if (token) {
      syncLocalStorageToBackend().then(() => {
        fetchWatchlist().then(data => {
          if (Array.isArray(data) && data.length > 0) setWatchlist(data);
        }).catch(() => {});
        fetchFavorites().then(data => {
          if (Array.isArray(data) && data.length > 0) setWatched(data);
        }).catch(() => {});
      });
    }
  }, []);

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

  const isInWatchlist = (movieId) => watchlist.some((m) => m.id === movieId || m.tmdbId === movieId);
  const isWatched = (movieId) => watched.some((m) => m.id === movieId || m.tmdbId === movieId);

  const toggleWatchlist = async (movie) => {
    const isPresent = isInWatchlist(movie.id);
    const newStatus = !isPresent;

    // Optimistic UI update
    if (isPresent) {
      setWatchlist((prev) => prev.filter((m) => m.id !== movie.id && m.tmdbId !== movie.id));
    } else {
      setWatchlist((prev) => [...prev, movie]);
      setWatched((prev) => prev.filter((m) => m.id !== movie.id && m.tmdbId !== movie.id));
    }

    try {
      const token = localStorage.getItem('token');
      if (token) {
        await updateInteraction(movie.id, isWatched(movie.id), newStatus);
      }
    } catch (err) {
      console.error('Watchlist API güncelleme hatası:', err);
    }
  };

  const toggleWatched = async (movie) => {
    const isPresent = isWatched(movie.id);
    const newStatus = !isPresent;

    // Optimistic UI update
    if (isPresent) {
      setWatched((prev) => prev.filter((m) => m.id !== movie.id && m.tmdbId !== movie.id));
    } else {
      setWatched((prev) => [...prev, movie]);
      setWatchlist((prev) => prev.filter((m) => m.id !== movie.id && m.tmdbId !== movie.id));
    }

    try {
      const token = localStorage.getItem('token');
      if (token) {
        await updateInteraction(movie.id, newStatus, isInWatchlist(movie.id));
      }
    } catch (err) {
      console.error('Watched list API güncelleme hatası:', err);
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
