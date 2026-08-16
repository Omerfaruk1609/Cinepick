import React, { useState } from 'react';
import MovieCard from './MovieCard';
import MatchBadge from './MatchBadge';
import { cleanTitle } from '../services/api';
import { Film, ChevronLeft, ChevronRight } from 'lucide-react';

export default function DiscoveryResults({
  movies = [],
  loading = false,
  isInWatchlist,
  isWatched,
  onToggleWatchlist,
  onToggleWatched,
  onMovieClick,
  itemsPerPage = 50
}) {
  const [currentPage, setCurrentPage] = useState(1);

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center py-16 space-y-4">
        <div className="w-12 h-12 border-4 border-rose-500/30 border-t-rose-500 rounded-full animate-spin"></div>
        <p className="text-slate-400 text-sm animate-pulse">Filmler filtreleniyor ve vektör uzayında sıralanıyor...</p>
      </div>
    );
  }

  if (!movies || movies.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center py-16 text-center space-y-3 bg-slate-900/40 rounded-2xl border border-slate-800">
        <Film className="w-12 h-12 text-slate-600" />
        <h3 className="text-base font-bold text-slate-300">Kriterlere Uygun Film Bulunamadı</h3>
        <p className="text-xs text-slate-500 max-w-sm">
          Lütfen filtre kriterlerinizi biraz daha genişleterek yeniden arama yapmayı deneyin.
        </p>
      </div>
    );
  }

  const totalPages = Math.ceil(movies.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const currentMovies = movies.slice(startIndex, startIndex + itemsPerPage);

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h3 className="text-sm font-semibold text-slate-400">
          Toplam <span className="text-rose-400 font-bold">{movies.length}</span> film bulundu
        </h3>
        {totalPages > 1 && (
          <div className="flex items-center gap-2 text-xs text-slate-400">
            <span>Sayfa {currentPage} / {totalPages}</span>
          </div>
        )}
      </div>

      {/* Film Izgarası */}
      <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-4">
        {currentMovies.map((movie) => {
          const displayMovie = {
            ...movie,
            title: cleanTitle(movie.title)
          };

          return (
            <div key={movie.id || movie.tmdbId} className="relative group">
              {movie.matchPercentage && (
                <div className="absolute top-2 left-2 z-10">
                  <MatchBadge matchPercentage={movie.matchPercentage} />
                </div>
              )}

              <MovieCard
                movie={displayMovie}
                isInWatchlist={isInWatchlist ? isInWatchlist(movie.id) : false}
                isWatched={isWatched ? isWatched(movie.id) : false}
                onToggleWatchlist={onToggleWatchlist}
                onToggleWatched={onToggleWatched}
                onClick={() => onMovieClick && onMovieClick(displayMovie)}
              />
            </div>
          );
        })}
      </div>

      {/* Sayfalama Kontrolleri */}
      {totalPages > 1 && (
        <div className="flex items-center justify-center gap-3 pt-6 border-t border-slate-800">
          <button
            onClick={() => setCurrentPage((prev) => Math.max(prev - 1, 1))}
            disabled={currentPage === 1}
            className="flex items-center gap-1 px-3 py-1.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs disabled:opacity-40 disabled:cursor-not-allowed cursor-pointer transition-colors"
          >
            <ChevronLeft className="w-4 h-4" />
            Önceki
          </button>
          
          <div className="flex items-center gap-1">
            {Array.from({ length: totalPages }, (_, i) => i + 1).map((pageNum) => (
              <button
                key={pageNum}
                onClick={() => setCurrentPage(pageNum)}
                className={`w-8 h-8 rounded-lg text-xs font-bold transition-colors cursor-pointer ${
                  currentPage === pageNum
                    ? 'bg-rose-600 text-white'
                    : 'bg-slate-800/80 text-slate-400 hover:bg-slate-700 hover:text-white'
                }`}
              >
                {pageNum}
              </button>
            ))}
          </div>

          <button
            onClick={() => setCurrentPage((prev) => Math.min(prev + 1, totalPages))}
            disabled={currentPage === totalPages}
            className="flex items-center gap-1 px-3 py-1.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs disabled:opacity-40 disabled:cursor-not-allowed cursor-pointer transition-colors"
          >
            Sonraki
            <ChevronRight className="w-4 h-4" />
          </button>
        </div>
      )}
    </div>
  );
}
