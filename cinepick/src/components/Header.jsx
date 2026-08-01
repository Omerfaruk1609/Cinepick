import React from 'react';
import { Film, Compass, Bookmark } from 'lucide-react';

export default function Header({ activeTab, onTabChange, watchlistCount }) {
  return (
    <header className="max-w-6xl mx-auto flex flex-col sm:flex-row justify-between items-center py-4 mb-8 border-b border-slate-800 gap-4">
      {/* Logo & Tagline */}
      <div className="flex items-center gap-2.5">
        <Film className="w-8 h-8 text-rose-500" />
        <div>
          <h1 className="text-2xl font-extrabold tracking-wide text-white flex items-center gap-1">
            Cine<span className="text-rose-500">Pick</span>
          </h1>
          <p className="text-[11px] text-slate-400">Kasvetli & Felsefi Sinema Keşfi</p>
        </div>
      </div>

      {/* Navigasyon Sekmeleri */}
      <nav className="flex items-center gap-2 bg-slate-900 p-1.5 rounded-xl border border-slate-800 shadow-inner">
        <button
          onClick={() => onTabChange('explore')}
          className={`flex items-center gap-2 px-4 py-2 rounded-lg text-xs sm:text-sm font-semibold transition-all duration-200 cursor-pointer ${
            activeTab === 'explore'
              ? 'bg-rose-600 text-white shadow-md shadow-rose-600/20'
              : 'text-slate-400 hover:text-white hover:bg-slate-800/50'
          }`}
        >
          <Compass className="w-4 h-4" />
          <span>Keşfet</span>
        </button>

        <button
          onClick={() => onTabChange('watchlist')}
          className={`flex items-center gap-2 px-4 py-2 rounded-lg text-xs sm:text-sm font-semibold transition-all duration-200 cursor-pointer relative ${
            activeTab === 'watchlist'
              ? 'bg-rose-600 text-white shadow-md shadow-rose-600/20'
              : 'text-slate-400 hover:text-white hover:bg-slate-800/50'
          }`}
        >
          <Bookmark className="w-4 h-4" />
          <span>İzleme Listem</span>
          {watchlistCount > 0 && (
            <span
              className={`px-2 py-0.5 text-[10px] font-extrabold rounded-full ${
                activeTab === 'watchlist' ? 'bg-white text-rose-600' : 'bg-rose-600 text-white'
              }`}
            >
              {watchlistCount}
            </span>
          )}
        </button>
      </nav>
    </header>
  );
}
