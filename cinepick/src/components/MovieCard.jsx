import React from 'react';
import { Star, Bookmark, CheckCircle2 } from 'lucide-react';
import { IMAGE_BASE_URL } from '../services/api';
import MatchBadge from './MatchBadge';
import ReasonBadge from './ReasonBadge';

export default function MovieCard({
  movie,
  isInWatchlist,
  isWatched,
  onToggleWatchlist,
  onToggleWatched,
  onClick,
}) {
  const rawPoster = movie.poster_path || movie.posterPath;
  const posterUrl = rawPoster?.startsWith('http')
    ? rawPoster
    : rawPoster
    ? `${IMAGE_BASE_URL}${rawPoster}`
    : null;

  const matchPercentage = movie.matchPercentage || movie.match_percentage;
  const reason = movie.recommendationReason || movie.recommendation_reason;
  const rating = movie.vote_average ?? movie.voteAverage;
  const year = movie.releaseYear || movie.release_year || (movie.release_date ? movie.release_date.substring(0, 4) : null);
  const platforms = movie.streamingPlatforms || movie.streaming_platforms;

  return (
    <div
      onClick={onClick}
      className="bg-slate-900 border border-slate-800 rounded-xl overflow-hidden hover:border-rose-500/50 transition-all duration-300 hover:-translate-y-1.5 shadow-lg group cursor-pointer flex flex-col justify-between relative"
    >
      {/* Sol Üst Köşe: % Match Badge */}
      {matchPercentage && (
        <div className="absolute top-2.5 left-2.5 z-10">
          <MatchBadge matchPercentage={matchPercentage} />
        </div>
      )}
      {/* Aksiyon Butonları (İzleyeceklerim & İzlediklerim) */}
      <div className="absolute top-2.5 right-2.5 z-10 flex items-center gap-1.5">
        {/* İzleyeceklerime Ekle (Bookmark) */}
        <button
          onClick={(e) => {
            e.stopPropagation();
            onToggleWatchlist(movie);
          }}
          className={`p-1.5 rounded-full backdrop-blur-md transition-all duration-200 cursor-pointer border ${
            isInWatchlist
              ? 'bg-rose-600 border-rose-500 text-white shadow-md shadow-rose-600/40 scale-105'
              : 'bg-slate-950/70 border-slate-700/60 text-slate-300 hover:text-white hover:bg-rose-600 hover:border-rose-500'
          }`}
          title={isInWatchlist ? 'İzleyeceklerimden Çıkar' : 'İzleyeceklerime Ekle'}
        >
          <Bookmark
            className={`w-3.5 h-3.5 ${
              isInWatchlist ? 'fill-white text-white' : ''
            }`}
          />
        </button>

        {/* İzlediklerime Ekle (Checkmark) */}
        <button
          onClick={(e) => {
            e.stopPropagation();
            onToggleWatched(movie);
            if (!isWatched && onClick) {
              onClick(); // Puan vermesi için film detay modalını aç
            }
          }}
          className={`p-1.5 rounded-full backdrop-blur-md transition-all duration-200 cursor-pointer border ${
            isWatched
              ? 'bg-emerald-600 border-emerald-500 text-white shadow-md shadow-emerald-600/40 scale-105'
              : 'bg-slate-950/70 border-slate-700/60 text-slate-300 hover:text-white hover:bg-emerald-600 hover:border-emerald-500'
          }`}
          title={isWatched ? 'İzlediklerimden Çıkar' : 'İzledim Olarak İşaretle (Puan Ver)'}
        >
          <CheckCircle2
            className={`w-3.5 h-3.5 ${
              isWatched ? 'text-white fill-emerald-600' : ''
            }`}
          />
        </button>
      </div>

      {/* Afış Görseli */}
      <div className="aspect-[2/3] w-full overflow-hidden bg-slate-800 relative">
        {posterUrl ? (
          <img
            src={posterUrl}
            alt={movie.title}
            className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
            loading="lazy"
          />
        ) : (
          <div className="flex items-center justify-center h-full text-slate-500 text-xs">
            Görsel Yok
          </div>
        )}
      </div>

      {/* Kart Alt Bilgileri */}
      <div className="p-3">
        <h3 className="font-semibold text-sm text-slate-100 truncate" title={movie.title}>
          {movie.title}
        </h3>
        <div className="flex items-center justify-between mt-2">
          <span className="flex items-center gap-1 text-xs text-amber-400 font-medium">
            <Star className="w-3.5 h-3.5 fill-amber-400" />
            {rating ? Number(rating).toFixed(1) : 'N/A'}
          </span>
          {year && (
            <span className="text-xs text-slate-400 font-medium">
              {year}
            </span>
          )}
          {isWatched ? (
            <span className="text-[10px] px-1.5 py-0.5 rounded bg-emerald-500/20 text-emerald-300 font-semibold border border-emerald-500/30">
              İzlendi
            </span>
          ) : isInWatchlist ? (
            <span className="text-[10px] px-1.5 py-0.5 rounded bg-rose-500/20 text-rose-300 font-semibold border border-rose-500/30">
              İzlenecek
            </span>
          ) : null}
        </div>

        {/* Platform Rozeti */}
        {platforms && (
          <div className="mt-1.5 flex flex-wrap gap-1">
            {platforms.split(',').slice(0, 2).map((p, idx) => (
              <span key={idx} className="text-[9px] px-1.5 py-0.5 rounded bg-indigo-950/70 text-indigo-300 border border-indigo-800/40 truncate max-w-[120px]">
                {p.trim()}
              </span>
            ))}
          </div>
        )}

        {/* Explainable AI Gerekçe Kutusu */}
        {reason && <ReasonBadge reason={reason} />}
      </div>
    </div>
  );
}
