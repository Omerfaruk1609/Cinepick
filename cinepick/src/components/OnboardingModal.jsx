import React, { useState, useEffect } from 'react';
import { fetchOnboardingPool, processOnboarding } from '../services/interactionApi';
import { IMAGE_BASE_URL } from '../services/api';
import { Sparkles, Loader2, Heart, X, SkipForward } from 'lucide-react';

const OnboardingModal = ({ onComplete }) => {
  const [movies, setMovies] = useState([]);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [ratings, setRatings] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchOnboardingPool()
      .then((data) => {
        if (Array.isArray(data) && data.length > 0) {
          setMovies(data);
        }
        setLoading(false);
      })
      .catch((err) => {
        console.error("Onboarding havuzu yüklenemedi:", err);
        setLoading(false);
      });
  }, []);

  const handleRate = (preference) => {
    const currentMovie = movies[currentIndex];
    const updatedRatings = [
      ...ratings,
      { movieId: currentMovie.id || currentMovie.tmdbId, preference }
    ];
    setRatings(updatedRatings);

    if (currentIndex + 1 < movies.length) {
      setCurrentIndex(currentIndex + 1);
    } else {
      submitOnboarding(updatedRatings);
    }
  };

  const submitOnboarding = async (finalRatings) => {
    try {
      setLoading(true);
      await processOnboarding(finalRatings);
      if (onComplete) onComplete();
    } catch (error) {
      console.error("Onboarding tamamlanamadı:", error);
      if (onComplete) onComplete();
    } finally {
      setLoading(false);
    }
  };

  if (loading || movies.length === 0) {
    return (
      <div className="fixed inset-0 z-50 bg-slate-950/90 backdrop-blur-md flex flex-col items-center justify-center p-4">
        <Loader2 className="w-10 h-10 text-rose-500 animate-spin mb-3" />
        <div className="text-white text-center font-medium">Taste Profiliniz Hazırlanıyor...</div>
      </div>
    );
  }

  const currentMovie = movies[currentIndex];
  const progressPercent = ((currentIndex + 1) / movies.length) * 100;
  const posterUrl = currentMovie.poster_path?.startsWith('http')
    ? currentMovie.poster_path
    : `${IMAGE_BASE_URL}${currentMovie.poster_path}`;

  return (
    <div className="fixed inset-0 bg-slate-950/90 backdrop-blur-md flex flex-col items-center justify-center p-4 z-50 animate-in fade-in duration-300">
      {/* İlerleme Çubuğu */}
      <div className="w-full max-w-md bg-slate-800 h-2 rounded-full mb-6 overflow-hidden border border-slate-700/50">
        <div
          className="bg-gradient-to-r from-rose-500 to-indigo-500 h-full transition-all duration-300"
          style={{ width: `${progressPercent}%` }}
        ></div>
      </div>

      {/* Film Kartı */}
      <div className="w-full max-w-md bg-slate-900 border border-slate-800 rounded-2xl p-6 text-center shadow-2xl relative">
        <span className="flex items-center justify-center gap-1.5 text-xs font-bold text-rose-400 uppercase tracking-widest mb-1">
          <Sparkles className="w-4 h-4 text-rose-400" />
          Zevk Profilini Eğit ({currentIndex + 1} / {movies.length})
        </span>

        <img
          src={posterUrl}
          alt={currentMovie.title}
          className="w-48 h-72 object-cover rounded-xl mx-auto my-4 shadow-xl border border-slate-800"
        />

        <h2 className="text-xl font-bold text-white mb-1 truncate">{currentMovie.title}</h2>
        <p className="text-slate-400 text-xs mb-6 line-clamp-2 leading-relaxed">
          {currentMovie.overview || 'Bu eser sinematik zevk profilinizi eğitmek için sunulmuştur.'}
        </p>

        {/* Aksiyon Butonları */}
        <div className="flex justify-center items-center gap-3">
          <button
            onClick={() => handleRate('DISLIKE')}
            className="flex-1 py-3 bg-rose-500/10 hover:bg-rose-500/20 text-rose-400 font-semibold rounded-xl border border-rose-500/30 transition-all active:scale-95 flex items-center justify-center gap-1.5 cursor-pointer text-xs sm:text-sm"
          >
            <X className="w-4 h-4" />
            <span>Beğenmedim</span>
          </button>

          <button
            onClick={() => handleRate('SKIP')}
            className="px-4 py-3 bg-slate-800 hover:bg-slate-700 text-slate-400 font-semibold rounded-xl transition-all cursor-pointer flex items-center justify-center gap-1 text-xs sm:text-sm"
          >
            <SkipForward className="w-4 h-4" />
            <span>Pas</span>
          </button>

          <button
            onClick={() => handleRate('LIKE')}
            className="flex-1 py-3 bg-emerald-500/10 hover:bg-emerald-500/20 text-emerald-400 font-semibold rounded-xl border border-emerald-500/30 transition-all active:scale-95 flex items-center justify-center gap-1.5 cursor-pointer text-xs sm:text-sm"
          >
            <Heart className="w-4 h-4 fill-emerald-400" />
            <span>Beğendim</span>
          </button>
        </div>
      </div>
    </div>
  );
};

export default OnboardingModal;
