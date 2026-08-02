import React from 'react';
import { GENRES } from '../data/genres';
import { Layers } from 'lucide-react';

export default function GenreSelector({ selectedGenreId, onSelectGenre }) {
  return (
    <div className="mb-8">
      <div className="flex items-center gap-2 mb-3">
        <Layers className="w-4 h-4 text-rose-500" />
        <h3 className="text-sm font-bold text-slate-300 dark:text-slate-300">
          Film Türüne Göre Filtrele
        </h3>
      </div>

      <div className="flex items-center gap-2 overflow-x-auto pb-2 custom-scrollbar no-scrollbar">
        {GENRES.map((genre) => {
          const isActive = genre.id === selectedGenreId;
          return (
            <button
              key={genre.id}
              onClick={() => onSelectGenre(genre.id)}
              className={`flex items-center gap-1.5 px-3.5 py-1.5 rounded-xl border text-xs font-semibold whitespace-nowrap transition-all duration-200 cursor-pointer ${
                isActive
                  ? 'bg-rose-600 text-white border-rose-500 shadow-md shadow-rose-600/30 scale-105'
                  : 'bg-slate-200 dark:bg-slate-900 border-slate-300 dark:border-slate-800 text-slate-700 dark:text-slate-300 hover:border-rose-500/40 hover:text-rose-500 dark:hover:text-white'
              }`}
            >
              <span>{genre.emoji}</span>
              <span>{genre.name}</span>
            </button>
          );
        })}
      </div>
    </div>
  );
}
