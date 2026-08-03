import React, { useEffect, useState } from 'react';
import apiClient from '../services/apiClient';
import { Compass, Film, Clapperboard, Loader2 } from 'lucide-react';

const TasteAnalyticsPanel = () => {
  const [analytics, setAnalytics] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let isMounted = true;
    apiClient.get('/users/analytics')
      .then((res) => {
        if (isMounted && res.data) setAnalytics(res.data);
      })
      .catch((err) => console.warn("Analizler alınamadı:", err))
      .finally(() => {
        if (isMounted) setLoading(false);
      });
    return () => { isMounted = false; };
  }, []);

  if (loading) {
    return (
      <div className="flex items-center justify-center gap-2 text-slate-400 py-12">
        <Loader2 className="w-5 h-5 animate-spin text-rose-500" />
        <span>Sinema Analizlerin Hazırlanıyor...</span>
      </div>
    );
  }

  if (!analytics) return null;

  const obscurity = analytics.obscurityScore || 50;
  const popularity = 100 - obscurity;

  return (
    <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 max-w-2xl mx-auto shadow-2xl backdrop-blur-md space-y-6">
      {/* Header & Persona Card */}
      <div className="flex items-center justify-between pb-6 border-b border-slate-800">
        <div>
          <span className="text-xs font-semibold text-rose-400 uppercase tracking-widest flex items-center gap-1.5 mb-1">
            <Compass className="w-4 h-4 text-rose-500" />
            Sinema Kimliğin
          </span>
          <h2 className="text-2xl font-black text-slate-100">{analytics.cinemaPersona}</h2>
        </div>
        <div className="text-right">
          <span className="text-3xl font-black text-rose-500">{analytics.totalMoviesWatched}</span>
          <span className="block text-xs text-slate-400 font-medium">İncelenen Film</span>
        </div>
      </div>

      {/* Obscurity Index Gauge (Popüler vs. Bağımsız) */}
      <div className="space-y-2">
        <div className="flex justify-between text-xs font-bold">
          <span className="text-blue-400">Popüler / Ana Akım (%{popularity.toFixed(0)})</span>
          <span className="text-purple-400">Gizli Cevher / Bağımsız (%{obscurity.toFixed(0)})</span>
        </div>

        {/* Progress Bar */}
        <div className="w-full h-3.5 bg-slate-950 rounded-full overflow-hidden flex border border-slate-800 shadow-inner">
          <div
            className="bg-gradient-to-r from-blue-500 to-indigo-500 h-full transition-all duration-700"
            style={{ width: `${popularity}%` }}
            title={`Popülerlik: %${popularity.toFixed(0)}`}
          />
          <div
            className="bg-gradient-to-r from-purple-500 to-rose-500 h-full transition-all duration-700"
            style={{ width: `${obscurity}%` }}
            title={`Bağımsız Sinema: %${obscurity.toFixed(0)}`}
          />
        </div>
      </div>

      {/* Grid: Top Genres & Top Directors */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {/* Favori Türler */}
        <div className="bg-slate-950/60 border border-slate-800/80 p-4 rounded-xl space-y-3">
          <h3 className="text-xs font-bold text-slate-400 uppercase tracking-wider flex items-center gap-1.5">
            <Film className="w-3.5 h-3.5 text-indigo-400" />
            En Çok İzlenen Türler
          </h3>
          <div className="space-y-2">
            {Object.keys(analytics.topGenres || {}).length > 0 ? (
              Object.entries(analytics.topGenres).map(([genre, count]) => (
                <div key={genre} className="flex justify-between items-center text-sm">
                  <span className="text-slate-200 font-medium">{genre}</span>
                  <span className="px-2 py-0.5 bg-indigo-500/10 border border-indigo-500/30 text-indigo-400 rounded-md text-xs font-bold">
                    {count} Film
                  </span>
                </div>
              ))
            ) : (
              <p className="text-xs text-slate-500 italic">Henüz yeterli etkileşim yok</p>
            )}
          </div>
        </div>

        {/* Favori Yönetmenler */}
        <div className="bg-slate-950/60 border border-slate-800/80 p-4 rounded-xl space-y-3">
          <h3 className="text-xs font-bold text-slate-400 uppercase tracking-wider flex items-center gap-1.5">
            <Clapperboard className="w-3.5 h-3.5 text-purple-400" />
            Favori Yönetmenler
          </h3>
          <div className="space-y-2">
            {Object.keys(analytics.topDirectors || {}).length > 0 ? (
              Object.entries(analytics.topDirectors).map(([director, count]) => (
                <div key={director} className="flex justify-between items-center text-sm">
                  <span className="text-slate-200 font-medium">{director}</span>
                  <span className="px-2 py-0.5 bg-purple-500/10 border border-purple-500/30 text-purple-400 rounded-md text-xs font-bold">
                    {count} Film
                  </span>
                </div>
              ))
            ) : (
              <p className="text-xs text-slate-500 italic">Henüz yeterli etkileşim yok</p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default TasteAnalyticsPanel;
