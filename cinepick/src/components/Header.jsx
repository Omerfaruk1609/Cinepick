import React from 'react';
import { Film, Compass, Bookmark, CheckCircle2, Search, User, Sparkles } from 'lucide-react';

export default function Header({
  activeTab,
  onTabChange,
  watchlistCount,
  watchedCount,
  selectedMoodLabel,
  searchQuery,
  onSearchChange,
  onLogoClick,
  onOpenProfile,
}) {
  return (
    <header className="max-w-6xl mx-auto flex flex-col md:flex-row justify-between items-center py-4 mb-8 border-b border-slate-800 gap-4">
      {/* Logo & Dinamik Alt Başlık */}
      <div
        onClick={onLogoClick}
        className="flex items-center gap-2.5 cursor-pointer group"
      >
        <Film className="w-8 h-8 text-rose-500 group-hover:scale-110 transition-transform duration-200" />
        <div>
          <h1 className="text-2xl font-extrabold tracking-wide text-white flex items-center gap-1 group-hover:text-rose-400 transition-colors">
            Cine<span className="text-rose-500">Pick</span>
          </h1>
          <p className="text-[11px] text-slate-400 flex items-center gap-1">
            <Sparkles className="w-3 h-3 text-rose-500" />
            {selectedMoodLabel && selectedMoodLabel !== 'Tümü'
              ? `Seçilen Mod: ${selectedMoodLabel}`
              : 'Sinema Keşif Platformu'}
          </p>
        </div>
      </div>

      {/* Orta Arama Çubuğu */}
      <div className="relative w-full md:w-64">
        <Search className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
        <input
          type="text"
          value={searchQuery}
          onChange={(e) => onSearchChange(e.target.value)}
          placeholder="Film veya konu ara..."
          className="w-full bg-slate-900 border border-slate-800 focus:border-rose-500/60 rounded-xl pl-9 pr-3 py-1.5 text-xs text-slate-100 placeholder-slate-500 focus:outline-none transition-all"
        />
        {searchQuery && (
          <button
            onClick={() => onSearchChange('')}
            className="absolute right-2.5 top-1/2 -translate-y-1/2 text-slate-500 hover:text-white text-xs font-bold"
          >
            ×
          </button>
        )}
      </div>

      {/* Navigasyon Sekmeleri ve Profil Butonu */}
      <div className="flex items-center gap-3 w-full md:w-auto justify-between md:justify-end">
        <nav className="flex items-center gap-1.5 bg-slate-900 p-1.5 rounded-xl border border-slate-800 shadow-inner">
          <button
            onClick={() => onTabChange('explore')}
            className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold transition-all cursor-pointer ${
              activeTab === 'explore'
                ? 'bg-rose-600 text-white shadow-md shadow-rose-600/20'
                : 'text-slate-400 hover:text-white hover:bg-slate-800/50'
            }`}
          >
            <Compass className="w-3.5 h-3.5" />
            <span>Keşfet</span>
          </button>

          <button
            onClick={() => onTabChange('watchlist')}
            className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold transition-all cursor-pointer relative ${
              activeTab === 'watchlist'
                ? 'bg-rose-600 text-white shadow-md shadow-rose-600/20'
                : 'text-slate-400 hover:text-white hover:bg-slate-800/50'
            }`}
          >
            <Bookmark className="w-3.5 h-3.5" />
            <span>İzleyeceklerim</span>
            {watchlistCount > 0 && (
              <span
                className={`px-1.5 py-0.2 text-[10px] font-extrabold rounded-full ${
                  activeTab === 'watchlist' ? 'bg-white text-rose-600' : 'bg-rose-600 text-white'
                }`}
              >
                {watchlistCount}
              </span>
            )}
          </button>

          <button
            onClick={() => onTabChange('watched')}
            className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold transition-all cursor-pointer relative ${
              activeTab === 'watched'
                ? 'bg-emerald-600 text-white shadow-md shadow-emerald-600/20'
                : 'text-slate-400 hover:text-white hover:bg-slate-800/50'
            }`}
          >
            <CheckCircle2 className="w-3.5 h-3.5" />
            <span>İzlediklerim</span>
            {watchedCount > 0 && (
              <span
                className={`px-1.5 py-0.2 text-[10px] font-extrabold rounded-full ${
                  activeTab === 'watched' ? 'bg-white text-emerald-600' : 'bg-emerald-600 text-white'
                }`}
              >
                {watchedCount}
              </span>
            )}
          </button>
        </nav>

        {/* Profil Icon Butonu */}
        <button
          onClick={onOpenProfile}
          className="p-2 rounded-xl bg-slate-900 border border-slate-800 text-slate-300 hover:text-white hover:border-rose-500/50 transition-all cursor-pointer shadow-md"
          title="Kullanıcı Profil & Ayarlar"
        >
          <User className="w-4 h-4 text-rose-400" />
        </button>
      </div>
    </header>
  );
}
