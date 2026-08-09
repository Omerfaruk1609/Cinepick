import { useState, useEffect } from 'react';
import { updateInteraction, fetchWatchlist, fetchFavorites, syncLocalStorageToBackend } from '../services/interactionApi';

const getWatchlistKey = (userId) => userId ? `cinepick_${userId}_watchlist` : 'cinepick_guest_watchlist';
const getWatchedKey = (userId) => userId ? `cinepick_${userId}_watched` : 'cinepick_guest_watched';

export function useMovieLists(user = null) {
  const userId = user?.id || null;

  const [watchlist, setWatchlist] = useState(() => {
    try {
      const saved = localStorage.getItem(getWatchlistKey(userId));
      return saved ? JSON.parse(saved) : [];
    } catch (err) {
      console.error('Watchlist okuma hatası:', err);
      return [];
    }
  });

  const [watched, setWatched] = useState(() => {
    try {
      const saved = localStorage.getItem(getWatchedKey(userId));
      return saved ? JSON.parse(saved) : [];
    } catch (err) {
      console.error('Watched list okuma hatası:', err);
      return [];
    }
  });

  // Kullanıcı değiştiğinde o kullanıcının verilerini yükle
  useEffect(() => {
    try {
      const savedWatchlist = localStorage.getItem(getWatchlistKey(userId));
      setWatchlist(savedWatchlist ? JSON.parse(savedWatchlist) : []);

      const savedWatched = localStorage.getItem(getWatchedKey(userId));
      setWatched(savedWatched ? JSON.parse(savedWatched) : []);
    } catch (err) {
      console.error('Kullanıcı verisi yükleme hatası:', err);
    }

    const token = localStorage.getItem('token');
    if (token && userId) {
      syncLocalStorageToBackend().then(() => {
        fetchWatchlist().then(data => {
          if (Array.isArray(data)) setWatchlist(data);
        }).catch(() => {});
        fetchFavorites().then(data => {
          if (Array.isArray(data)) setWatched(data);
        }).catch(() => {});
      });
    }
  }, [userId]);

  // Watchlist güncellendiğinde ilgili kullanıcının key'ine kaydet
  useEffect(() => {
    try {
      localStorage.setItem(getWatchlistKey(userId), JSON.stringify(watchlist));
    } catch (err) {
      console.error('Watchlist kaydetme hatası:', err);
    }
  }, [watchlist, userId]);

  // Watched list güncellendiğinde ilgili kullanıcının key'ine kaydet
  useEffect(() => {
    try {
      localStorage.setItem(getWatchedKey(userId), JSON.stringify(watched));
    } catch (err) {
      console.error('Watched list kaydetme hatası:', err);
    }
  }, [watched, userId]);

  const isInWatchlist = (movieId) => watchlist.some((m) => m.id === movieId || m.tmdbId === movieId);
  const isWatched = (movieId) => watched.some((m) => m.id === movieId || m.tmdbId === movieId);

  const toggleWatchlist = async (movie) => {
    const isPresent = isInWatchlist(movie.id);
    const newStatus = !isPresent;

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
