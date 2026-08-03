import React, { useState, useEffect } from 'react';
import apiClient from '../services/apiClient';
import { Ban, ShieldAlert } from 'lucide-react';

const ALL_GENRES = ["Korku", "Romantik", "Aksiyon", "Bilim-Kurgu", "Dram", "Komedi", "Belgesel", "Gerilim", "Animasyon", "Fantezi"];

const BlacklistSettings = () => {
  const [excludedGenres, setExcludedGenres] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    let isMounted = true;
    apiClient.get('/users/blacklist')
      .then(res => {
        if (isMounted && res.data && res.data.excludedGenres) {
          setExcludedGenres(res.data.excludedGenres);
        }
      })
      .catch(err => {
        console.warn("Kara liste yüklenemedi:", err);
      });
    return () => { isMounted = false; };
  }, []);

  const toggleExcludeGenre = async (genre) => {
    try {
      setLoading(true);
      let updated;
      if (excludedGenres.includes(genre)) {
        updated = excludedGenres.filter(g => g !== genre);
      } else {
        updated = [...excludedGenres, genre];
      }
      setExcludedGenres(updated);

      // Backend'e kara liste güncellemesi gönder
      await apiClient.post('/users/blacklist/genres', { excludedGenres: updated });
    } catch (err) {
      console.error("Kara liste güncellenemedi:", err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-6 bg-slate-900/90 border border-slate-800 rounded-2xl max-w-xl shadow-xl backdrop-blur-md">
      <h3 className="text-lg font-bold text-slate-100 mb-2 flex items-center gap-2">
        <Ban className="w-5 h-5 text-rose-500" />
        <span>Nefret Edilen Türler (Kara Liste)</span>
      </h3>
      <p className="text-xs text-slate-400 mb-4 leading-relaxed">
        Seçtiğin türler hiçbir öneri algoritmasında ve ana sayfa akışında karşına çıkarılmaz. Algoritma bu filmleri kesin olarak eler.
      </p>

      <div className="flex flex-wrap gap-2">
        {ALL_GENRES.map((genre) => {
          const isBlacklisted = excludedGenres.includes(genre);
          return (
            <button
              key={genre}
              disabled={loading}
              onClick={() => toggleExcludeGenre(genre)}
              className={`px-3.5 py-1.5 rounded-xl text-xs font-semibold transition-all cursor-pointer flex items-center gap-1.5 border ${
                isBlacklisted
                  ? 'bg-rose-500/20 text-rose-400 border-rose-500/40 shadow-lg shadow-rose-500/10'
                  : 'bg-slate-800/80 text-slate-400 border-slate-700/50 hover:bg-slate-800 hover:text-slate-200'
              }`}
            >
              {isBlacklisted ? (
                <>
                  <ShieldAlert className="w-3.5 h-3.5 text-rose-400" />
                  <span>✕ {genre} (Engellendi)</span>
                </>
              ) : (
                <span>+ {genre}</span>
              )}
            </button>
          );
        })}
      </div>
    </div>
  );
};

export default BlacklistSettings;
