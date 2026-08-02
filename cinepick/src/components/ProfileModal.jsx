import React, { useState, useEffect } from 'react';
import { X, User, CheckCircle2, Bookmark, Film, Settings, ShieldCheck, Sparkles, Star } from 'lucide-react';

export default function ProfileModal({ isOpen, onClose, watchlistCount, watchedCount, user, onLogout, ratings }) {
  const [userName, setUserName] = useState(() => {
    return user?.username || localStorage.getItem('cinepick_username') || 'Ömer Faruk';
  });
  const [isEditing, setIsEditing] = useState(false);

  useEffect(() => {
    if (user?.username) setUserName(user.username);
  }, [user]);

  useEffect(() => {
    const handleKeyDown = (e) => {
      if (e.key === 'Escape') onClose();
    };
    if (isOpen) {
      window.addEventListener('keydown', handleKeyDown);
    }
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  const handleSaveName = () => {
    localStorage.setItem('cinepick_username', userName);
    setIsEditing(false);
  };

  const ratedCount = ratings ? Object.keys(ratings).length : 0;
  const avgRating = ratedCount > 0
    ? (Object.values(ratings).reduce((a, b) => a + b, 0) / ratedCount).toFixed(1)
    : 0;

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center p-4 sm:p-6 bg-slate-950/80 backdrop-blur-md transition-all duration-300 animate-in fade-in"
      onClick={onClose}
    >
      <div
        className="relative w-full max-w-md bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl overflow-hidden shadow-2xl p-6 text-slate-900 dark:text-slate-100 space-y-6 transition-colors"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Kapat Butonu */}
        <button
          onClick={onClose}
          className="absolute top-4 right-4 p-2 rounded-full bg-slate-100 dark:bg-slate-800 hover:bg-rose-600 text-slate-500 dark:text-slate-400 hover:text-white transition-all cursor-pointer"
        >
          <X className="w-5 h-5" />
        </button>

        {/* Profil Header */}
        <div className="flex items-center gap-4 border-b border-slate-200 dark:border-slate-800 pb-5">
          <div className="w-16 h-16 rounded-full bg-gradient-to-tr from-rose-600 to-amber-500 flex items-center justify-center text-white text-2xl font-bold shadow-lg shadow-rose-600/30">
            {userName.charAt(0).toUpperCase()}
          </div>
          <div>
            {isEditing ? (
              <div className="flex items-center gap-2">
                <input
                  type="text"
                  value={userName}
                  onChange={(e) => setUserName(e.target.value)}
                  className="bg-slate-100 dark:bg-slate-950 border border-rose-500 rounded-lg px-2.5 py-1 text-sm font-semibold text-slate-900 dark:text-white focus:outline-none"
                />
                <button
                  onClick={handleSaveName}
                  className="text-xs px-3 py-1 bg-rose-600 rounded-lg font-bold hover:bg-rose-500 text-white"
                >
                  Kaydet
                </button>
              </div>
            ) : (
              <div className="flex items-center gap-2">
                <h2 className="text-xl font-bold text-slate-900 dark:text-white">{userName}</h2>
                <button
                  onClick={() => setIsEditing(true)}
                  className="text-[11px] text-rose-500 font-medium hover:underline"
                >
                  Düzenle
                </button>
              </div>
            )}
            <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5 flex items-center gap-1">
              <ShieldCheck className="w-3.5 h-3.5 text-emerald-500" />
              {user ? user.email : 'CinePick Üyesi'}
            </p>
          </div>
        </div>

        {/* İstatistikler */}
        <div>
          <h3 className="text-xs font-bold uppercase tracking-wider text-slate-500 dark:text-slate-400 mb-3 flex items-center gap-1.5">
            <Sparkles className="w-4 h-4 text-rose-500" />
            Sinema İstatistiklerin
          </h3>
          <div className="grid grid-cols-3 gap-2.5">
            <div className="p-3 rounded-xl bg-slate-50 dark:bg-slate-950/70 border border-slate-200 dark:border-slate-800 text-center">
              <p className="text-[10px] text-slate-500 uppercase font-semibold">İzlenen</p>
              <p className="text-base font-extrabold text-emerald-600 dark:text-emerald-400 mt-0.5">{watchedCount}</p>
            </div>

            <div className="p-3 rounded-xl bg-slate-50 dark:bg-slate-950/70 border border-slate-200 dark:border-slate-800 text-center">
              <p className="text-[10px] text-slate-500 uppercase font-semibold">İzlenecek</p>
              <p className="text-base font-extrabold text-rose-600 dark:text-rose-400 mt-0.5">{watchlistCount}</p>
            </div>

            <div className="p-3 rounded-xl bg-slate-50 dark:bg-slate-950/70 border border-slate-200 dark:border-slate-800 text-center">
              <p className="text-[10px] text-slate-500 uppercase font-semibold">Ort. Puan</p>
              <p className="text-base font-extrabold text-amber-500 mt-0.5">{avgRating > 0 ? avgRating : '-'}</p>
            </div>
          </div>
        </div>

        {/* Tercihler */}
        <div className="space-y-2.5 pt-2 border-t border-slate-200 dark:border-slate-800">
          <h3 className="text-xs font-bold uppercase tracking-wider text-slate-500 dark:text-slate-400 flex items-center gap-1.5">
            <Settings className="w-4 h-4 text-slate-400" />
            Sistem Bilgisi
          </h3>
          <div className="space-y-2 text-xs">
            <div className="flex items-center justify-between p-2.5 rounded-lg bg-slate-50 dark:bg-slate-950/40 border border-slate-200 dark:border-slate-800">
              <span className="text-slate-600 dark:text-slate-300">API Katmanı</span>
              <span className="text-slate-500 font-mono">TMDB / Spring Boot API</span>
            </div>
          </div>
        </div>

        {user && onLogout && (
          <button
            onClick={() => {
              onLogout();
              onClose();
            }}
            className="w-full py-2 rounded-xl bg-rose-600/10 hover:bg-rose-600 border border-rose-500/30 text-rose-500 hover:text-white text-xs font-bold transition-all cursor-pointer"
          >
            Hesaptan Çıkış Yap
          </button>
        )}
      </div>
    </div>
  );
}
