import React, { useEffect, useState } from 'react';
import { getMovieDetails, getMovieCredits, BACKDROP_IMAGE_BASE_URL, IMAGE_BASE_URL } from '../services/api';
import { X, Star, Clock, Calendar, Clapperboard, Users, Loader2, Bookmark, Check } from 'lucide-react';

export default function MovieModal({ movie, movieId, onClose, isBookmarked, onToggleWatchlist }) {
  const [details, setDetails] = useState(null);
  const [credits, setCredits] = useState(null);
  const [loading, setLoading] = useState(true);

  const activeId = movie?.id || movieId;

  useEffect(() => {
    if (!activeId) return;

    let isMounted = true;
    setLoading(true);

    Promise.all([getMovieDetails(activeId), getMovieCredits(activeId)])
      .then(([detailsData, creditsData]) => {
        if (!isMounted) return;
        setDetails(detailsData || movie);
        setCredits(creditsData);
        setLoading(false);
      })
      .catch((err) => {
        console.error('Modal detay hatası:', err);
        if (isMounted) {
          setDetails(movie);
          setLoading(false);
        }
      });

    return () => {
      isMounted = false;
    };
  }, [activeId, movie]);

  // ESC tuşu ile kapatma
  useEffect(() => {
    const handleKeyDown = (e) => {
      if (e.key === 'Escape') {
        onClose();
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [onClose]);

  if (!activeId) return null;

  const currentMovie = details || movie || {};
  const inWatchlist = isBookmarked ? isBookmarked(currentMovie.id) : false;

  // Arka plan resmi
  const backdropUrl = currentMovie.backdrop_path
    ? currentMovie.backdrop_path.startsWith('http')
      ? currentMovie.backdrop_path
      : `${BACKDROP_IMAGE_BASE_URL}${currentMovie.backdrop_path}`
    : currentMovie.poster_path
    ? currentMovie.poster_path.startsWith('http')
      ? currentMovie.poster_path
      : `${IMAGE_BASE_URL}${currentMovie.poster_path}`
    : null;

  // Afiş resmi
  const posterUrl = currentMovie.poster_path
    ? currentMovie.poster_path.startsWith('http')
      ? currentMovie.poster_path
      : `${IMAGE_BASE_URL}${currentMovie.poster_path}`
    : null;

  // Yönetmen bulma
  const director = credits?.crew?.find(c => c.job === 'Director')?.name || 'Bilinmiyor';

  // Oyuncular (İlk 5 kişi)
  const topCast = credits?.cast?.slice(0, 5) || [];

  // Çıkış yılı
  const releaseYear = currentMovie.release_date
    ? new Date(currentMovie.release_date).getFullYear()
    : 'N/A';

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center p-4 sm:p-6 md:p-10 bg-slate-950/80 backdrop-blur-md transition-all duration-300 animate-in fade-in"
      onClick={onClose}
    >
      <div
        className="relative w-full max-w-4xl max-h-[90vh] bg-slate-900 border border-slate-800 rounded-2xl overflow-hidden shadow-2xl overflow-y-auto custom-scrollbar text-slate-100"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Kapat Butonu */}
        <button
          onClick={onClose}
          className="absolute top-4 right-4 z-20 p-2.5 rounded-full bg-slate-950/70 hover:bg-rose-600 text-slate-300 hover:text-white border border-slate-700/50 hover:border-rose-500 transition-all duration-200 cursor-pointer shadow-lg group"
          title="Kapat (ESC)"
        >
          <X className="w-5 h-5 group-hover:rotate-90 transition-transform duration-200" />
        </button>

        {loading ? (
          <div className="flex flex-col items-center justify-center min-h-[400px] gap-3 text-slate-400">
            <Loader2 className="w-10 h-10 text-rose-500 animate-spin" />
            <p className="text-sm">Film detayları ve kadro yükleniyor...</p>
          </div>
        ) : (
          <div>
            {/* Hero & Backdrop Alanı */}
            <div className="relative h-64 sm:h-80 w-full overflow-hidden bg-slate-950">
              {backdropUrl ? (
                <img
                  src={backdropUrl}
                  alt={currentMovie.title}
                  className="w-full h-full object-cover object-center opacity-60 scale-105"
                />
              ) : (
                <div className="w-full h-full flex items-center justify-center text-slate-600 bg-slate-950">
                  <Clapperboard className="w-16 h-16 opacity-30" />
                </div>
              )}

              {/* Gradient Kaplama */}
              <div className="absolute inset-0 bg-gradient-to-t from-slate-900 via-slate-900/60 to-transparent" />
              <div className="absolute inset-0 bg-gradient-to-r from-slate-900/90 via-transparent to-transparent" />

              {/* Başlık, İzleme Listesi Butonu (Backdrop üzeri) */}
              <div className="absolute bottom-4 left-6 right-6 flex items-end justify-between gap-4">
                <div>
                  <h2 className="text-2xl sm:text-4xl font-extrabold text-white tracking-wide drop-shadow-md">
                    {currentMovie.title}
                  </h2>
                  {currentMovie.tagline && (
                    <p className="text-xs sm:text-sm text-rose-400 italic mt-1 font-medium">
                      "{currentMovie.tagline}"
                    </p>
                  )}
                </div>

                {/* İzleme Listesine Ekle / Çıkar Butonu */}
                {onToggleWatchlist && (
                  <button
                    onClick={() => onToggleWatchlist(currentMovie)}
                    className={`flex items-center gap-2 px-4 py-2.5 rounded-xl border text-xs sm:text-sm font-semibold transition-all duration-200 cursor-pointer shadow-xl ${
                      inWatchlist
                        ? 'bg-rose-600 border-rose-500 text-white shadow-rose-600/30 ring-2 ring-rose-500/50'
                        : 'bg-slate-900/90 border-slate-700 text-slate-200 hover:bg-rose-600 hover:border-rose-500 hover:text-white'
                    }`}
                  >
                    {inWatchlist ? (
                      <>
                        <Check className="w-4 h-4 text-white" />
                        <span>Listede Ekli</span>
                      </>
                    ) : (
                      <>
                        <Bookmark className="w-4 h-4 text-rose-400 group-hover:text-white" />
                        <span>İzleme Listeme Ekle</span>
                      </>
                    )}
                  </button>
                )}
              </div>
            </div>

            {/* Detay İçeriği */}
            <div className="p-6 sm:p-8 space-y-6">
              {/* Meta Bilgi Çubuğu */}
              <div className="flex flex-wrap items-center gap-4 text-xs sm:text-sm border-b border-slate-800 pb-4">
                {/* Puan */}
                <div className="flex items-center gap-1.5 px-3 py-1 rounded-lg bg-amber-500/10 border border-amber-500/30 text-amber-400 font-bold">
                  <Star className="w-4 h-4 fill-amber-400" />
                  <span>{currentMovie.vote_average ? Number(currentMovie.vote_average).toFixed(1) : 'N/A'}</span>
                </div>

                {/* Çıkış Yılı */}
                <div className="flex items-center gap-1.5 text-slate-300">
                  <Calendar className="w-4 h-4 text-rose-500" />
                  <span>{releaseYear}</span>
                </div>

                {/* Süre */}
                {currentMovie.runtime > 0 && (
                  <div className="flex items-center gap-1.5 text-slate-300">
                    <Clock className="w-4 h-4 text-rose-500" />
                    <span>{currentMovie.runtime} dk</span>
                  </div>
                )}

                {/* Yönetmen */}
                <div className="flex items-center gap-1.5 text-slate-300">
                  <Clapperboard className="w-4 h-4 text-rose-500" />
                  <span>Yönetmen: <strong className="text-white font-medium">{director}</strong></span>
                </div>
              </div>

              {/* Tür Etiketleri */}
              {currentMovie.genres && currentMovie.genres.length > 0 && (
                <div className="flex flex-wrap gap-2">
                  {currentMovie.genres.map((genre) => (
                    <span
                      key={genre.id || genre.name}
                      className="px-3 py-1 text-xs font-semibold rounded-full bg-slate-800 border border-slate-700 text-rose-300"
                    >
                      {genre.name}
                    </span>
                  ))}
                </div>
              )}

              {/* Afiş + Özet Bölümü */}
              <div className="grid grid-cols-1 md:grid-cols-4 gap-6 items-start">
                {/* Sol Afiş (Masaüstünde) */}
                {posterUrl && (
                  <div className="hidden md:block md:col-span-1 rounded-xl overflow-hidden border border-slate-800 shadow-xl aspect-[2/3]">
                    <img
                      src={posterUrl}
                      alt={currentMovie.title}
                      className="w-full h-full object-cover"
                    />
                  </div>
                )}

                {/* Sağ Özet & Detaylar */}
                <div className={`space-y-4 ${posterUrl ? 'md:col-span-3' : 'md:col-span-4'}`}>
                  <div>
                    <h3 className="text-sm font-bold uppercase tracking-wider text-rose-500 mb-2">
                      Film Özeti
                    </h3>
                    <p className="text-sm sm:text-base text-slate-300 leading-relaxed">
                      {currentMovie.overview || 'Bu film için henüz Türkçe özet bulunmamaktadır.'}
                    </p>
                  </div>

                  {/* Oyuncu Kadrosu */}
                  {topCast.length > 0 && (
                    <div className="pt-2">
                      <div className="flex items-center gap-2 mb-3">
                        <Users className="w-4 h-4 text-rose-500" />
                        <h3 className="text-sm font-bold uppercase tracking-wider text-slate-300">
                          Öne Çıkan Oyuncular
                        </h3>
                      </div>
                      <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
                        {topCast.map((actor) => (
                          <div
                            key={actor.id || actor.name}
                            className="flex items-center gap-2.5 p-2 rounded-lg bg-slate-950/60 border border-slate-800"
                          >
                            <div className="w-8 h-8 rounded-full bg-slate-800 overflow-hidden flex-shrink-0 border border-slate-700 flex items-center justify-center text-xs text-slate-400 font-bold">
                              {actor.profile_path ? (
                                <img
                                  src={`${IMAGE_BASE_URL}${actor.profile_path}`}
                                  alt={actor.name}
                                  className="w-full h-full object-cover"
                                />
                              ) : (
                                actor.name.charAt(0)
                              )}
                            </div>
                            <div className="min-w-0">
                              <p className="text-xs font-semibold text-slate-200 truncate">
                                {actor.name}
                              </p>
                              {actor.character && (
                                <p className="text-[10px] text-slate-400 truncate">
                                  {actor.character}
                                </p>
                              )}
                            </div>
                          </div>
                        ))}
                      </div>
                    </div>
                  )}
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
