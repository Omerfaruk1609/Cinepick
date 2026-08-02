import React, { useState, useEffect } from 'react';
import { X, User, CheckCircle2, Bookmark, Film, Settings, ShieldCheck, Sparkles } from 'lucide-react';

export default function ProfileModal({ isOpen, onClose, watchlistCount, watchedCount }) {
  const [userName, setUserName] = useState(() => {
    return localStorage.getItem('cinepick_username') || 'Ömer Faruk';
  });
  const [isEditing, setIsEditing] = useState(false);

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

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center p-4 sm:p-6 bg-slate-950/80 backdrop-blur-md transition-all duration-300 animate-in fade-in"
      onClick={onClose}
    >
      <div
        className="relative w-full max-w-md bg-slate-900 border border-slate-800 rounded-2xl overflow-hidden shadow-2xl p-6 text-slate-100 space-y-6"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Kapat Butonu */}
        <button
          onClick={onClose}
          className="absolute top-4 right-4 p-2 rounded-full bg-slate-800 hover:bg-rose-600 text-slate-300 hover:text-white transition-all cursor-pointer"
        >
          <X className="w-5 h-5" />
        </button>

        {/* Profil Header */}
        <div className="flex items-center gap-4 border-b border-slate-800 pb-5">
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
                  className="bg-slate-950 border border-rose-500 rounded-lg px-2.5 py-1 text-sm font-semibold text-white focus:outline-none"
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
                <h2 className="text-xl font-bold text-white">{userName}</h2>
                <button
                  onClick={() => setIsEditing(true)}
                  className="text-[11px] text-rose-400 hover:underline font-medium"
                >
                  Düzenle
                </button>
              </div>
            )}
            <p className="text-xs text-slate-400 mt-0.5 flex items-center gap-1">
              <ShieldCheck className="w-3.5 h-3.5 text-emerald-400" />
              CinePick Üyesi & Sinema Sever
            </p>
          </div>
        </div>

        {/* İstatistikler */}
        <div>
          <h3 className="text-xs font-bold uppercase tracking-wider text-slate-400 mb-3 flex items-center gap-1.5">
            <Sparkles className="w-4 h-4 text-rose-500" />
            Sinema İstatistiklerin
          </h3>
          <div className="grid grid-cols-2 gap-3">
            <div className="p-3.5 rounded-xl bg-slate-950/70 border border-slate-800 flex items-center gap-3">
              <div className="p-2.5 rounded-lg bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
                <CheckCircle2 className="w-5 h-5" />
              </div>
              <div>
                <p className="text-[10px] text-slate-400 uppercase font-semibold">İzlenen Filmler</p>
                <p className="text-lg font-extrabold text-white">{watchedCount}</p>
              </div>
            </div>

            <div className="p-3.5 rounded-xl bg-slate-950/70 border border-slate-800 flex items-center gap-3">
              <div className="p-2.5 rounded-lg bg-amber-500/10 text-amber-400 border border-amber-500/20">
                <Bookmark className="w-5 h-5" />
              </div>
              <div>
                <p className="text-[10px] text-slate-400 uppercase font-semibold">İzlenecekler</p>
                <p className="text-lg font-extrabold text-white">{watchlistCount}</p>
              </div>
            </div>
          </div>
        </div>

        {/* Tercihler & Ayarlar Mockup */}
        <div className="space-y-3 pt-2 border-t border-slate-800">
          <h3 className="text-xs font-bold uppercase tracking-wider text-slate-400 flex items-center gap-1.5">
            <Settings className="w-4 h-4 text-slate-400" />
            Uygulama Ayarları
          </h3>
          <div className="space-y-2 text-xs">
            <div className="flex items-center justify-between p-2.5 rounded-lg bg-slate-950/40 border border-slate-800">
              <span className="text-slate-300">Tema Modu</span>
              <span className="px-2 py-0.5 rounded bg-rose-500/20 text-rose-300 font-bold border border-rose-500/30">
                Koyu Sinematik
              </span>
            </div>
            <div className="flex items-center justify-between p-2.5 rounded-lg bg-slate-950/40 border border-slate-800">
              <span className="text-slate-300">API Veri Kaynağı</span>
              <span className="text-slate-400 font-mono">TMDB / Spring Boot API</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
