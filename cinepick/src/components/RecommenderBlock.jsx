import React, { useEffect, useState, useMemo } from 'react';
import { fetchAllPublicMovies } from '../services/api';
import MovieCard from './MovieCard';
import MatchBadge from './MatchBadge';
import { Sparkles, RefreshCw, Flame } from 'lucide-react';

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

  // Kullanıcının tercih ettiği tür ağırlık haritasını hesapla
  const userGenreWeights = useMemo(() => {
    const weights = {};

    watchlist.forEach((movie) => {
      const gIds = movie.genre_ids || (movie.genres ? movie.genres.map(g => typeof g === 'number' ? g : g.id) : []);
      gIds.forEach(id => {
        if (id) weights[id] = (weights[id] || 0) + 2.0;
      });
    });

    watched.forEach((movie) => {
      const gIds = movie.genre_ids || (movie.genres ? movie.genres.map(g => typeof g === 'number' ? g : g.id) : []);
      gIds.forEach(id => {
        if (id) weights[id] = (weights[id] || 0) + 1.5;
      });
    });

    Object.entries(ratings).forEach(([movieId, score]) => {
      const weightChange = score >= 7 ? (score - 6) * 2.0 : (score < 5 ? -3 : 0);
      // Film türü hesabı
      if (weightChange !== 0) {
        weights['general'] = (weights['general'] || 0) + weightChange;
      }
    });

    return weights;
  }, [watchlist, watched, ratings]);

  useEffect(() => {
    let isMounted = true;
    setLoading(true);

    const calculateRecommendations = async () => {
      const allMovies = await fetchAllPublicMovies();
      if (!allMovies || allMovies.length === 0) {
        if (isMounted) setLoading(false);
        return;
      }

      // Kullanıcının zaten izlediği ve watchlist'indeki film ID'leri
      const excludedIds = new Set([
        ...watchlist.map(m => String(m.id)),
        ...watched.map(m => String(m.id))
      ]);

      const candidates = allMovies.filter(m => !excludedIds.has(String(m.id)));
      const hasUserPreferences = Object.keys(userGenreWeights).length > 0;

      // Her filme kararlı (deterministic) Uyum Yüzdesi ve Skor hesapla
      const scoredMovies = candidates.map((movie, index) => {
        const gIds = movie.genre_ids || (movie.genres ? movie.genres.map(g => typeof g === 'number' ? g : g.id) : []);
        
        let rawScore = 0;
        if (hasUserPreferences) {
          gIds.forEach(id => {
            if (userGenreWeights[id]) rawScore += userGenreWeights[id];
          });
        } else {
          // Kullanıcı henüz tercih yapmadıysa oylama puanına göre skorla
          rawScore = Number(movie.vote_average) || 7.5;
        }

        // Uyum yüzdesi hesapla (%82 - %98 arası kararlı skor)
        const baseMatch = hasUserPreferences
          ? Math.min(98, Math.max(78, Math.round(75 + rawScore * 4)))
          : Math.min(98, Math.max(82, Math.round(70 + (Number(movie.vote_average) || 8) * 3)));

        // Yenile butonuna basıldığında karıştırmak için tohum
        const seedBonus = refreshSeed > 0 ? (index * 7 + refreshSeed * 13) % 15 : 0;
        const finalScore = rawScore * 10 + (seedBonus / 10);

        return {
          movie,
          finalScore,
          matchPercentage: baseMatch,
        };
      });

      // Yüksek puandan düşüğe doğru sırala (Kararlı sıralama)
      scoredMovies.sort((a, b) => b.finalScore - a.finalScore);

      if (isMounted) {
        setRecommended(scoredMovies.slice(0, 5));
        setLoading(false);
      }
    };

    calculateRecommendations();

    return () => {
      isMounted = false;
    };
  }, [userGenreWeights, watchlist, watched, refreshSeed]);

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
            className="flex items-center gap-1.5 px-3 py-1 rounded-xl bg-slate-800/80 hover:bg-rose-600 text-xs text-slate-300 hover:text-white border border-slate-700 transition-all cursor-pointer shadow-sm"
            title="Farklı Öneriler Getir"
          >
            <RefreshCw className="w-3.5 h-3.5" />
            <span>Farklı Öneriler Getir</span>
          </button>
          <span className="hidden sm:flex items-center gap-1 text-xs text-emerald-400 font-bold bg-emerald-500/10 px-2.5 py-1 rounded-lg border border-emerald-500/20">
            <Flame className="w-3.5 h-3.5" />
            Yüksek Uyumlu Seçimler
          </span>
        </div>
      </div>

      {/* 5 Öneri Kartı + Uyum Yüzdesi Badgeleri */}
      <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-5 gap-4">
        {recommended.map(({ movie, matchPercentage }) => (
          <div key={movie.id} className="relative group">
            {/* Uyum Yüzdesi Rozeti */}
            <div className="absolute top-2 left-2 z-10">
              <MatchBadge matchPercentage={matchPercentage} />
            </div>

            <MovieCard
              movie={movie}
              isInWatchlist={isInWatchlist(movie.id)}
              isWatched={isWatched(movie.id)}
              onToggleWatchlist={onToggleWatchlist}
              onToggleWatched={onToggleWatched}
              onClick={() => onMovieClick(movie)}
            />
          </div>
        ))}
      </div>
    </div>
  );
}
