import React, { useState } from 'react';
import { Filter, RotateCcw, Search, Calendar, Clock, Star, Globe, Tv } from 'lucide-react';

const AVAILABLE_GENRES = [
  'Aksiyon', 'Macera', 'Animasyon', 'Komedi', 'Suç', 'Belgesel',
  'Dram', 'Aile', 'Fantezi', 'Tarih', 'Korku', 'Müzik', 'Gizem',
  'Romantik', 'Bilim Kurgu', 'Gerilim', 'Savaş', 'Vahşi Batı'
];

const STREAMING_PLATFORMS = [
  { id: 'Netflix', label: 'Netflix' },
  { id: 'Amazon Prime Video', label: 'Prime Video' },
  { id: 'Disney Plus', label: 'Disney+' },
  { id: 'BluTV', label: 'BluTV' },
  { id: 'TOD', label: 'TOD' },
  { id: 'MUBI', label: 'MUBI' }
];

export default function FilterPanel({ onFilterSubmit, onReset }) {
  const [selectedGenres, setSelectedGenres] = useState([]);
  const [selectedPlatform, setSelectedPlatform] = useState('all');
  const [originalLanguage, setOriginalLanguage] = useState('all');
  const [minYear, setMinYear] = useState('');
  const [maxYear, setMaxYear] = useState('');
  const [minRating, setMinRating] = useState(0);
  const [maxRuntime, setMaxRuntime] = useState(240);
  const [limit, setLimit] = useState(50);

  const toggleGenre = (genre) => {
    setSelectedGenres((prev) =>
      prev.includes(genre) ? prev.filter((g) => g !== genre) : [...prev, genre]
    );
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const filterData = {
      genres: selectedGenres.length > 0 ? selectedGenres : null,
      platform: selectedPlatform !== 'all' ? selectedPlatform : null,
      originalLanguage: originalLanguage !== 'all' ? originalLanguage : null,
      minYear: minYear ? parseInt(minYear, 10) : null,
      maxYear: maxYear ? parseInt(maxYear, 10) : null,
      minRating: minRating > 0 ? parseFloat(minRating) : null,
      maxRuntime: maxRuntime < 240 ? parseInt(maxRuntime, 10) : null,
      limit: parseInt(limit, 10) || 50,
      page: 0
    };
    onFilterSubmit(filterData);
  };

  const handleReset = () => {
    setSelectedGenres([]);
    setSelectedPlatform('all');
    setOriginalLanguage('all');
    setMinYear('');
    setMaxYear('');
    setMinRating(0);
    setMaxRuntime(240);
    setLimit(50);
    if (onReset) onReset();
  };

  return (
    <form onSubmit={handleSubmit} className="bg-slate-900/90 backdrop-blur-md border border-slate-800 rounded-2xl p-6 shadow-2xl space-y-6 text-slate-200">
      <div className="flex items-center justify-between border-b border-slate-800 pb-4">
        <div className="flex items-center gap-2 text-rose-500 font-bold text-lg">
          <Filter className="w-5 h-5" />
          <h2>Gelişmiş Filtreleme</h2>
        </div>
        <button
          type="button"
          onClick={handleReset}
          className="flex items-center gap-1.5 text-xs text-slate-400 hover:text-rose-400 transition-colors cursor-pointer"
        >
          <RotateCcw className="w-3.5 h-3.5" />
          Filtreleri Sıfırla
        </button>
      </div>

      {/* Yayın Platformları (Streaming Platforms TR) */}
      <div className="space-y-2">
        <label className="text-xs font-semibold text-slate-400 uppercase tracking-wider flex items-center gap-1.5">
          <Tv className="w-3.5 h-3.5 text-rose-400" />
          Yayın Platformu (Türkiye)
        </label>
        <div className="flex flex-wrap gap-2">
          <button
            type="button"
            onClick={() => setSelectedPlatform('all')}
            className={`px-3 py-1.5 rounded-xl text-xs font-medium transition-all cursor-pointer border ${
              selectedPlatform === 'all'
                ? 'bg-rose-600 text-white border-rose-500 shadow-md shadow-rose-900/30'
                : 'bg-slate-800/60 text-slate-300 border-slate-700/60 hover:bg-slate-700/80 hover:text-white'
            }`}
          >
            Tüm Platformlar
          </button>
          {STREAMING_PLATFORMS.map((p) => {
            const isSelected = selectedPlatform === p.id;
            return (
              <button
                type="button"
                key={p.id}
                onClick={() => setSelectedPlatform(p.id)}
                className={`px-3 py-1.5 rounded-xl text-xs font-medium transition-all cursor-pointer border ${
                  isSelected
                    ? 'bg-rose-600 text-white border-rose-500 shadow-md shadow-rose-900/30'
                    : 'bg-slate-800/60 text-slate-300 border-slate-700/60 hover:bg-slate-700/80 hover:text-white'
                }`}
              >
                {p.label}
              </button>
            );
          })}
        </div>
      </div>

      {/* Tür Seçimi */}
      <div className="space-y-2">
        <label className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Türler (Çoklu Seçim)</label>
        <div className="flex flex-wrap gap-2">
          {AVAILABLE_GENRES.map((genre) => {
            const isSelected = selectedGenres.includes(genre);
            return (
              <button
                type="button"
                key={genre}
                onClick={() => toggleGenre(genre)}
                className={`px-3 py-1.5 rounded-xl text-xs font-medium transition-all cursor-pointer border ${
                  isSelected
                    ? 'bg-rose-600 text-white border-rose-500 shadow-md shadow-rose-900/30'
                    : 'bg-slate-800/60 text-slate-300 border-slate-700/60 hover:bg-slate-700/80 hover:text-white'
                }`}
              >
                {genre}
              </button>
            );
          })}
        </div>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {/* Dil Seçimi */}
        <div className="space-y-1.5">
          <label className="text-xs font-semibold text-slate-400 flex items-center gap-1">
            <Globe className="w-3.5 h-3.5 text-rose-400" />
            Dil Seçeneği
          </label>
          <select
            value={originalLanguage}
            onChange={(e) => setOriginalLanguage(e.target.value)}
            className="w-full bg-slate-800 border border-slate-700 rounded-xl px-3 py-2 text-sm text-slate-200 focus:outline-none focus:border-rose-500 transition-colors"
          >
            <option value="all">Tümü (Hepsi)</option>
            <option value="tr">Yerli Yapımlar (Türkçe)</option>
            <option value="en">Yabancı Yapımlar (İngilizce)</option>
          </select>
        </div>

        {/* Yapım Yılı Aralığı */}
        <div className="space-y-1.5">
          <label className="text-xs font-semibold text-slate-400 flex items-center gap-1">
            <Calendar className="w-3.5 h-3.5 text-rose-400" />
            Yapım Yılı Aralığı
          </label>
          <div className="flex items-center gap-2">
            <input
              type="number"
              placeholder="Min (1970)"
              value={minYear}
              onChange={(e) => setMinYear(e.target.value)}
              className="w-1/2 bg-slate-800 border border-slate-700 rounded-xl px-3 py-2 text-sm text-slate-200 focus:outline-none focus:border-rose-500 placeholder:text-slate-500"
            />
            <span className="text-slate-500">-</span>
            <input
              type="number"
              placeholder="Max (2026)"
              value={maxYear}
              onChange={(e) => setMaxYear(e.target.value)}
              className="w-1/2 bg-slate-800 border border-slate-700 rounded-xl px-3 py-2 text-sm text-slate-200 focus:outline-none focus:border-rose-500 placeholder:text-slate-500"
            />
          </div>
        </div>

        {/* Min IMDb Puanı */}
        <div className="space-y-1.5">
          <label className="text-xs font-semibold text-slate-400 flex items-center justify-between">
            <span className="flex items-center gap-1">
              <Star className="w-3.5 h-3.5 text-amber-400 fill-amber-400" />
              Min IMDb Puanı
            </span>
            <span className="text-amber-400 font-bold">{minRating} +</span>
          </label>
          <input
            type="range"
            min="0"
            max="9.5"
            step="0.5"
            value={minRating}
            onChange={(e) => setMinRating(e.target.value)}
            className="w-full accent-rose-500 cursor-pointer h-2 bg-slate-800 rounded-lg"
          />
        </div>

        {/* Maksimum Süre */}
        <div className="space-y-1.5">
          <label className="text-xs font-semibold text-slate-400 flex items-center justify-between">
            <span className="flex items-center gap-1">
              <Clock className="w-3.5 h-3.5 text-rose-400" />
              Maksimum Süre
            </span>
            <span className="text-slate-300 font-bold">{maxRuntime} dk</span>
          </label>
          <input
            type="range"
            min="60"
            max="240"
            step="10"
            value={maxRuntime}
            onChange={(e) => setMaxRuntime(e.target.value)}
            className="w-full accent-rose-500 cursor-pointer h-2 bg-slate-800 rounded-lg"
          />
        </div>
      </div>

      {/* Uygula Butonu & Sonuç Limiti */}
      <div className="flex items-center justify-between pt-2">
        <div className="flex items-center gap-2 text-xs text-slate-400">
          <span>Gösterilecek Film Limiti:</span>
          <select
            value={limit}
            onChange={(e) => setLimit(e.target.value)}
            className="bg-slate-800 border border-slate-700 rounded-lg px-2.5 py-1 text-slate-200 focus:outline-none focus:border-rose-500"
          >
            <option value="20">20 Film</option>
            <option value="50">50 Film</option>
            <option value="100">100 Film</option>
          </select>
        </div>

        <button
          type="submit"
          className="flex items-center gap-2 px-6 py-2.5 rounded-xl bg-gradient-to-r from-rose-600 to-rose-700 hover:from-rose-500 hover:to-rose-600 text-white font-semibold text-sm shadow-lg shadow-rose-950/50 transition-all cursor-pointer active:scale-95"
        >
          <Search className="w-4 h-4" />
          Filtreleri Uygula
        </button>
      </div>
    </form>
  );
}
