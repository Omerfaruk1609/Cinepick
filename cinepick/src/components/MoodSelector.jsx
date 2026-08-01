import React from 'react';
import { MOODS } from '../data/moods';
import { Sparkles } from 'lucide-react';

export default function MoodSelector({ selectedMoodId, onSelectMood }) {
  const activeMood = MOODS.find(m => m.id === selectedMoodId) || MOODS[0];

  return (
    <div className="mb-10">
      {/* Başlık alanı */}
      <div className="flex items-center gap-2 mb-4">
        <Sparkles className="w-5 h-5 text-rose-500 animate-pulse" />
        <h2 className="text-lg font-bold text-slate-200 tracking-wide">
          Şu anki Ruh Halin Nasıl?
        </h2>
      </div>

      {/* Ruh hali butonları */}
      <div className="flex flex-wrap gap-3">
        {MOODS.map((mood) => {
          const isActive = mood.id === selectedMoodId;
          return (
            <button
              key={mood.id}
              onClick={() => onSelectMood(mood)}
              className={`flex items-center gap-2.5 px-4 py-2.5 rounded-xl border text-sm font-medium transition-all duration-200 cursor-pointer shadow-md ${
                isActive
                  ? 'bg-rose-600/90 border-rose-500 text-white shadow-rose-600/30 ring-2 ring-rose-500/50 scale-[1.02]'
                  : 'bg-slate-900/80 border-slate-800 text-slate-300 hover:border-rose-500/40 hover:text-white hover:bg-slate-800/60'
              }`}
            >
              <span className="text-base leading-none">{mood.emoji}</span>
              <span>{mood.label}</span>
            </button>
          );
        })}
      </div>

      {/* Seçili mood açıklaması */}
      {activeMood && activeMood.description && (
        <p className="mt-3 text-xs text-slate-400 italic">
          💡 {activeMood.description}
        </p>
      )}
    </div>
  );
}
