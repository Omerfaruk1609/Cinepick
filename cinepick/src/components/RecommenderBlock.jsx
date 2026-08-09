import React, { useEffect, useState } from 'react';
import { fetchAllPublicMovies } from '../services/api';
import MovieCard from './MovieCard';
import { Sparkles, RefreshCw } from 'lucide-react';

export default function RecommenderBlock({
  watchlist,
  watched,
  ratings = {},
  isInWatchlist,
  isWatched,
  onToggleWatchlist,
  onToggleWatched,
  onMovieClick,
}) {
  const [recommended, setRecommended] = useState([]);
  const [loading, setLoading] = useState(true);
  const [refreshSeed, setRefreshSeed] = useState(0);

  useEffect(() => {
    let isMounted = true;
    setLoading(true);

    const calculateRecommendations = async () => {
      const allMovies = await fetchAllPublicMovies();
      if (!allMovies || allMovies.length === 0) {
        if (isMounted) setLoading(false);
        return;
      }

      // Tür Ağırlık Haritası
      const genreWeights = {};

      // 1. İzleyeceklerim listesindeki filmler (Ağırlık: +2)
      watchlist.forEach((movie) => {
        const gIds = movie.genre_ids || (movie.genres ? movie.genres.map(g => typeof g === 'number' ? g : g.id) : []);
        gIds.forEach(id => {
          if (id) genreWeights[id] = (genreWeights[id] || 0) + 2;
        });
      });

      // 2. İzlediklerim listesindeki filmler (Ağırlık: +1.5)
      watched.forEach((movie) => {
        const gIds = movie.genre_ids || (movie.genres ? movie.genres.map(g => typeof g === 'number' ? g : g.id) : []);
        gIds.forEach(id => {
          if (id) genreWeights[id] = (genreWeights[id] || 0) + 1.5;
        });
      });

      // 3. Kullanıcının Yıldız Puanları (7+ puan: +3 Ağırlık, 5 altı: -2 Ceza)
      Object.entries(ratings).forEach(([movieId, score]) => {
        const movie = allMovies.find(m => String(m.id) === String(movieId));
        if (movie) {
          const gIds = movie.genre_ids || (movie.genres ? movie.genres.map(g => typeof g === 'number' ? g : g.id) : []);
          const weightChange = score >= 7 ? (score - 6) * 1.5 : (score < 5 ? -2 : 0);
          gIds.forEach(id => {
            if (id) genreWeights[id] = (genreWeights[id] || 0) + weightChange;
          });
        }
      });

      // En yüksek ağırlığa sahip Tür ID'leri
      const topGenreIds = Object.keys(genreWeights)
        .map(Number)
        .filter(id => genreWeights[id] > 0)
        .sort((a, b) => genreWeights[b] - genreWeights[a]);

      // Kullanıcının zaten izlediği ve watchlist'indeki film ID'leri
      const excludedIds = new Set([
        ...watchlist.map(m => String(m.id)),
        ...watched.map(m => String(m.id))
      ]);

      // Öneri Skorlama
      let candidateMovies = allMovies.filter(m => !excludedIds.has(String(m.id)));

      if (topGenreIds.length > 0) {
        // En sevilen türlere uygun filmleri öncelikle seç
        candidateMovies = candidateMovies.map(movie => {
          const gIds = movie.genre_ids || (movie.genres ? movie.genres.map(g => typeof g === 'number' ? g : g.id) : []);
          let score = 0;
          gIds.forEach(id => {
            if (genreWeights[id]) score += genreWeights[id];
          });
          // Küçük rastgelelik ekle (Shuffle / Taze Öneri)
          const randomFactor = Math.random() * 1.5;
          return { movie, score: score + randomFactor };
        }).sort((a, b) => b.score - a.score).map(item => item.movie);
      } else {
        // Kullanıcının henüz etkileşimi yoksa popüler ve yüksek puanlı filmlerden rastgele 5 tane seç
        candidateMovies = candidateMovies.sort(() => Math.random() - 0.5);
      }

      if (isMounted) {
        setRecommended(candidateMovies.slice(0, 5));
        setLoading(false);
      }
    };

    calculateRecommendations();

    return () => {
      isMounted = false;
    };
  }, [watchlist, watched, ratings, refreshSeed]);

  if (loading || recommended.length === 0) return null;

  return (
    <div className="mb-10 p-5 rounded-2xl bg-gradient-to-r from-rose-950/40 via-slate-900/60 to-slate-950 border border-rose-500/20 shadow-xl space-y-4">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          <Sparkles className="w-5 h-5 text-rose-500 animate-pulse" />
          <h2 className="text-lg font-bold text-slate-900 dark:text-slate-100 tracking-wide">
            Sana Özel Sinematik Öneriler
          </h2>
        </div>
        <div className="flex items-center gap-3">
          <button
            onClick={() => setRefreshSeed(prev => prev + 1)}
            className="flex items-center gap-1.5 px-3 py-1 rounded-xl bg-slate-800/80 hover:bg-rose-600 text-xs text-slate-300 hover:text-white border border-slate-700 transition-all cursor-pointer"
            title="Önerileri Yenile"
          >
            <RefreshCw className="w-3.5 h-3.5" />
            <span>Yenile</span>
          </button>
          <span className="hidden sm:inline text-xs text-rose-400 font-medium">
            Kişiselleştirilmiş Algoritma
          </span>
        </div>
      </div>

      <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-5 gap-4">
        {recommended.map((movie) => (
          <MovieCard
            key={movie.id}
            movie={movie}
            isInWatchlist={isInWatchlist(movie.id)}
            isWatched={isWatched(movie.id)}
            onToggleWatchlist={onToggleWatchlist}
            onToggleWatched={onToggleWatched}
            onClick={() => onMovieClick(movie)}
          />
        ))}
      </div>
    </div>
  );
}
