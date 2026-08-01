import React from 'react';
import { Bookmark, Compass } from 'lucide-react';

export default function EmptyWatchlist({ onGoToExplore }) {
  return (
    <div className="flex flex-col items-center justify-center py-20 px-4 text-center border border-dashed border-slate-800 rounded-2xl bg-slate-900/40">
      <div className="w-16 h-16 rounded-full bg-slate-800/80 flex items-center justify-center text-slate-500 mb-4 border border-slate-700/50">
        <Bookmark className="w-8 h-8 text-rose-500/70" />
      </div>
      <h3 className="text-lg font-bold text-slate-200 mb-2">
        Henüz izleme listenize film eklemediniz
      </h3>
      <p className="text-sm text-slate-400 max-w-md mb-6">
        Keşfet sekmesinden beğendiğiniz filmlerin üzerindeki ayraç ikonuna tıklayarak listenize ekleyebilirsiniz.
      </p>
      <button
        onClick={onGoToExplore}
        className="flex items-center gap-2 px-5 py-2.5 rounded-xl bg-rose-600 hover:bg-rose-500 text-white font-semibold text-sm transition-all shadow-lg shadow-rose-600/25 cursor-pointer"
      >
        <Compass className="w-4 h-4" />
        <span>Filmleri Keşfetmeye Başla</span>
      </button>
    </div>
  );
}
