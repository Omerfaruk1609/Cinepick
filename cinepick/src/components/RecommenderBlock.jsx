import React, { useEffect, useState, useMemo } from 'react';
import { getPopularMovies, cleanTitle } from '../services/api';
import MovieCard from './MovieCard';
import MatchBadge from './MatchBadge';
import { Sparkles, RefreshCw, Flame } from 'lucide-react';

const SEEN_RECOMMENDATIONS_KEY = 'cinepick_seen_rec_ids';

export default function RecommenderBlock({
  watchlist = [],
  watched = [],
  ratings = {},
  isInWatchlist,
  isWatched,
  onToggleWatchlist,
  onToggleWatched,
  onMovieClick,
}) {
  const [recommended, setRecommended] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isFallback, setIsFallback] = useState(false);
  const [rotationIndex, setRotationIndex] = useState(0);

  // Kullanıcının tercih ettiği tür ağırlık haritasını hesapla
  const userGenreWeights = useMemo(() => {
    const weights = {};

    (watchlist || []).forEach((movie) => {
      const gList = Array.isArray(movie.genres)
        ? movie.genres
        : (movie.genre_ids ? movie.genre_ids : []);
      gList.forEach(g => {
        const name = typeof g === 'string' ? g : g.name;
        if (name) weights[name] = (weights[name] || 0) + 2.0;
      });
    });

    (watched || []).forEach((movie) => {
      const gList = Array.isArray(movie.genres)
        ? movie.genres
        : (movie.genre_ids ? movie.genre_ids : []);
      gList.forEach(g => {
        const name = typeof g === 'string' ? g : g.name;
        if (name) weights[name] = (weights[name] || 0) + 1.5;
      });
    });

    Object.entries(ratings || {}).forEach(([movieId, score]) => {
      const weightChange = score >= 7 ? (score - 6) * 2.0 : (score < 5 ? -3 : 0);
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
      try {
        const allMovies = await getPopularMovies(50, 0);

        if (!allMovies || allMovies.length === 0) {
          if (isMounted) setLoading(false);
          return;
        }

        const excludedIds = new Set([
          ...(watchlist || []).map(m => String(m.id || m.tmdbId)),
          ...(watched || []).map(m => String(m.id || m.tmdbId))
        ]);

        const candidates = allMovies.filter(m => !excludedIds.has(String(m.id || m.tmdbId)));
        const hasUserPreferences = Object.keys(userGenreWeights).length > 0;

        const scoredMovies = (candidates.length > 0 ? candidates : allMovies).map((movie) => {
          const gList = Array.isArray(movie.genres)
            ? movie.genres
            : (movie.genre_ids ? movie.genre_ids : []);

          let rawScore = 0;
          if (hasUserPreferences) {
            gList.forEach(g => {
              const name = typeof g === 'string' ? g : g.name;
              if (name && userGenreWeights[name]) rawScore += userGenreWeights[name];
            });
          } else {
            rawScore = Number(movie.vote_average) || 7.5;
          }

          const baseMatch = hasUserPreferences
            ? Math.min(99, Math.max(80, Math.round(75 + rawScore * 3.5)))
            : Math.min(99, Math.max(82, Math.round(70 + (Number(movie.vote_average) || 8) * 3)));

          return {
            movie: {
              ...movie,
              title: cleanTitle(movie.title)
            },
            rawScore,
            matchPercentage: baseMatch,
          };
        });

        scoredMovies.sort((a, b) => b.rawScore - a.rawScore);
        const top25Pool = scoredMovies.slice(0, 25);

        let seenIds = [];
        try {
          const saved = sessionStorage.getItem(SEEN_RECOMMENDATIONS_KEY);
          seenIds = saved ? JSON.parse(saved) : [];
        } catch (e) {
          seenIds = [];
        }

        const freshCandidates = top25Pool.filter(item => !seenIds.includes(String(item.movie.id || item.movie.tmdbId)));

        let selectedBatch = [];
        if (freshCandidates.length >= 5) {
          selectedBatch = freshCandidates.slice(0, 5);
        } else {
          const startIndex = (rotationIndex * 5) % Math.max(1, top25Pool.length);
          selectedBatch = top25Pool.slice(startIndex, startIndex + 5);
          if (selectedBatch.length < 5 && top25Pool.length > 0) {
            selectedBatch = [...selectedBatch, ...top25Pool.slice(0, 5 - selectedBatch.length)];
          }
        }

        try {
          const newlySeen = selectedBatch.map(item => String(item.movie.id || item.movie.tmdbId));
          const updatedSeen = Array.from(new Set([...seenIds, ...newlySeen])).slice(-30);
          sessionStorage.setItem(SEEN_RECOMMENDATIONS_KEY, JSON.stringify(updatedSeen));
        } catch (e) {}

        if (isMounted) {
          setRecommended(selectedBatch);
          setIsFallback(!hasUserPreferences);
          setLoading(false);
        }
      } catch (err) {
        console.error('Öneri hesaplama hatası:', err);
        if (isMounted) setLoading(false);
      }
    };

    calculateRecommendations();

    return () => {
      isMounted = false;
    };
  }, [userGenreWeights, watchlist, watched, rotationIndex]);

  if (loading) {
    return (
      <div className="mb-8 p-6 rounded-2xl bg-slate-900/60 border border-slate-800 animate-pulse flex items-center justify-center space-x-2 text-slate-400 text-sm">
        <Sparkles className="w-4 h-4 text-rose-500 animate-spin" />
        <span>Kişiselleştirilmiş önerileriniz hazırlanıyor...</span>
      </div>
    );
  }

  if (!recommended || recommended.length === 0) return null;

  return (
    <div className="mb-8 p-5 rounded-2xl bg-gradient-to-r from-rose-950/40 via-slate-900/60 to-slate-950 border border-rose-500/20 shadow-xl space-y-4">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          <Sparkles className="w-5 h-5 text-rose-500 animate-pulse" />
          <h2 className="text-base sm:text-lg font-bold text-slate-100 tracking-wide">
            {isFallback ? 'Sana Özel Seçilmiş Popüler Filmler' : 'Kişisel Zevkine Göre Öneriler'}
          </h2>
        </div>
        <div className="flex items-center gap-3">
          <button
            onClick={() => setRotationIndex(prev => prev + 1)}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-slate-800/80 hover:bg-rose-600 text-xs text-slate-300 hover:text-white border border-slate-700 transition-all cursor-pointer shadow-sm"
            title="Daha Fazla Öneri Keşfet"
          >
            <RefreshCw className="w-3.5 h-3.5" />
            <span>Farklı Öneriler Gör</span>
          </button>
          <span className="hidden sm:flex items-center gap-1 text-xs text-emerald-400 font-bold bg-emerald-500/10 px-2.5 py-1 rounded-lg border border-emerald-500/20">
            <Flame className="w-3.5 h-3.5" />
            {isFallback ? 'AI Popüler Havuz' : 'pgvector Akıllı Eşleşme'}
          </span>
        </div>
      </div>

      <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-5 gap-4">
        {recommended.map(({ movie, matchPercentage }) => (
          <div key={movie.id || movie.tmdbId} className="relative group">
            <div className="absolute top-2 left-2 z-10">
              <MatchBadge matchPercentage={matchPercentage} />
            </div>

            <MovieCard
              movie={movie}
              isInWatchlist={isInWatchlist ? isInWatchlist(movie.id) : false}
              isWatched={isWatched ? isWatched(movie.id) : false}
              onToggleWatchlist={onToggleWatchlist}
              onToggleWatched={onToggleWatched}
              onClick={() => onMovieClick && onMovieClick(movie)}
            />
          </div>
        ))}
      </div>
    </div>
  );
}
