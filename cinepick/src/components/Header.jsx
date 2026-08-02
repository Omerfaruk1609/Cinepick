import React from 'react';
import { Film, Compass, Bookmark, CheckCircle2, Search, User, Sun, Moon, Sparkles, LogIn, LogOut } from 'lucide-react';

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
  onOpenAuth,
  user,
  onLogout,
  theme,
  onToggleTheme,
}) {
  return (
    <header className="max-w-6xl mx-auto flex flex-col md:flex-row justify-between items-center py-4 mb-8 border-b border-slate-200 dark:border-slate-800 gap-4 transition-colors">
      {/* Logo & Dinamik Alt Başlık */}
      <div
        onClick={onLogoClick}
        className="flex items-center gap-2.5 cursor-pointer group"
      >
        <Film className="w-8 h-8 text-rose-500 group-hover:scale-110 transition-transform duration-200" />
        <div>
          <h1 className="text-2xl font-extrabold tracking-wide text-slate-900 dark:text-white flex items-center gap-1 group-hover:text-rose-500 transition-colors">
            Cine<span className="text-rose-500">Pick</span>
          </h1>
          <p className="text-[11px] text-slate-500 dark:text-slate-400 flex items-center gap-1">
            <Sparkles className="w-3 h-3 text-rose-500" />
            {selectedMoodLabel && selectedMoodLabel !== 'Tümü'
              ? `Seçilen Mod: ${selectedMoodLabel}`
              : 'Sinema Keşif Platformu'}
          </p>
        </div>
      </div>

      {/* Arama Çubuğu */}
      <div className="relative w-full md:w-64">
        <Search className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
        <input
          type="text"
          value={searchQuery}
          onChange={(e) => onSearchChange(e.target.value)}
          placeholder="Film veya konu ara..."
          className="w-full bg-slate-100 dark:bg-slate-900 border border-slate-300 dark:border-slate-800 focus:border-rose-500 rounded-xl pl-9 pr-8 py-1.5 text-xs text-slate-900 dark:text-slate-100 placeholder-slate-400 focus:outline-none transition-all"
        />
        {searchQuery && (
          <button
            onClick={() => onSearchChange('')}
            className="absolute right-2.5 top-1/2 -translate-y-1/2 text-slate-400 hover:text-rose-500 text-xs font-bold"
          >
            ×
          </button>
        )}
      </div>

      {/* Navigasyon Sekmeleri, Tema & Auth */}
      <div className="flex flex-wrap items-center gap-2.5 w-full md:w-auto justify-between md:justify-end">
        <nav className="flex items-center gap-1 bg-slate-100 dark:bg-slate-900 p-1 rounded-xl border border-slate-200 dark:border-slate-800 shadow-inner">
          <button
            onClick={() => onTabChange('explore')}
            className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold transition-all cursor-pointer ${
              activeTab === 'explore'
                ? 'bg-rose-600 text-white shadow-md shadow-rose-600/20'
                : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white'
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
                : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white'
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
                : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white'
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

        {/* Tema Anahtarı (Dark/Light Toggle) */}
        <button
          onClick={onToggleTheme}
          className="p-2 rounded-xl bg-slate-100 dark:bg-slate-900 border border-slate-300 dark:border-slate-800 text-slate-700 dark:text-slate-300 hover:text-rose-500 transition-all cursor-pointer shadow-sm"
          title={theme === 'dark' ? 'Aydınlık Temaya Geç' : 'Karanlık Temaya Geç'}
        >
          {theme === 'dark' ? (
            <Sun className="w-4 h-4 text-amber-400" />
          ) : (
            <Moon className="w-4 h-4 text-slate-700" />
          )}
        </button>

        {/* Kullanıcı Auth / Profil Butonu */}
        {user ? (
          <div className="flex items-center gap-2">
            <button
              onClick={onOpenProfile}
              className="flex items-center gap-2 px-3 py-1.5 rounded-xl bg-slate-100 dark:bg-slate-900 border border-slate-300 dark:border-slate-800 hover:border-rose-500 transition-all text-xs font-bold text-slate-800 dark:text-slate-100 cursor-pointer"
            >
              <div className="w-5 h-5 rounded-full bg-rose-600 text-white flex items-center justify-center text-[10px]">
                {user.username.charAt(0).toUpperCase()}
              </div>
              <span>{user.username}</span>
            </button>
            <button
              onClick={onLogout}
              className="p-2 rounded-xl bg-slate-100 dark:bg-slate-900 border border-slate-300 dark:border-slate-800 text-slate-500 hover:text-rose-500 transition-all cursor-pointer"
              title="Çıkış Yap"
            >
              <LogOut className="w-4 h-4" />
            </button>
          </div>
        ) : (
          <button
            onClick={onOpenAuth}
            className="flex items-center gap-1.5 px-3.5 py-1.5 rounded-xl bg-rose-600 hover:bg-rose-500 text-white font-bold text-xs shadow-md shadow-rose-600/20 transition-all cursor-pointer"
          >
            <LogIn className="w-3.5 h-3.5" />
            <span>Giriş Yap</span>
          </button>
        )}
      </div>
    </header>
  );
}
