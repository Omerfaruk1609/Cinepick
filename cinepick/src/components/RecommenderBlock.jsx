import React, { useEffect, useState } from 'react';
import { getMoviesByGenre, getPopularMovies } from '../services/api';
import MovieCard from './MovieCard';
import { Sparkles, Compass } from 'lucide-react';

export default function RecommenderBlock({
  watchlist,
  watched,
  ratings,
  isInWatchlist,
  isWatched,
  onToggleWatchlist,
  onToggleWatched,
  onMovieClick,
}) {
  const [recommended, setRecommended] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let isMounted = true;
    setLoading(true);

    // En çok etkileşime girilen film türlerini çıkar
    const genreCounts = {};
    const allUserMovies = [...watchlist, ...watched];

    allUserMovies.forEach((movie) => {
      if (movie.genre_ids && Array.isArray(movie.genre_ids)) {
        movie.genre_ids.forEach((id) => {
          genreCounts[id] = (genreCounts[id] || 0) + 1;
        });
      } else if (movie.genres && Array.isArray(movie.genres)) {
        movie.genres.forEach((g) => {
          const id = typeof g === 'number' ? g : g.id;
          if (id) genreCounts[id] = (genreCounts[id] || 0) + 1;
        });
      }
    });

    // En popüler tür ID'sini bul
    const topGenreIds = Object.keys(genreCounts)
      .sort((a, b) => genreCounts[b] - genreCounts[a])
      .map(Number)
      .slice(0, 2);

    const fetchRecommendations = async () => {
      let data = [];
      if (topGenreIds.length > 0) {
        data = await getMoviesByGenre(topGenreIds);
      } else {
        data = await getPopularMovies();
      }
      if (isMounted) {
        // Zaten izlenenleri filtrelere dahil etmeyelim
        const filtered = data.filter((m) => !isWatched(m.id)).slice(0, 5);
        setRecommended(filtered.length > 0 ? filtered : data.slice(0, 5));
        setLoading(false);
      }
    };

    fetchRecommendations();

    return () => {
      isMounted = false;
    };
  }, [watchlist, watched, isWatched]);

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
        <span className="text-xs text-rose-400 font-medium">
          İzleme Alışkanlıklarına Göre
        </span>
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
