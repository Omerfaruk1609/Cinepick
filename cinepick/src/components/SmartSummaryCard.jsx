import React from 'react';
import { Timer, Sparkles, ThumbsUp, ThumbsDown } from 'lucide-react';

const SmartSummaryCard = ({ summary }) => {
  if (!summary) return null;

  return (
    <div className="mt-6 bg-slate-900/90 border border-slate-800 rounded-2xl p-5 backdrop-blur-md shadow-2xl space-y-5">
      {/* 30 Saniyelik Özet */}
      <div>
        <div className="flex items-center gap-2 mb-2">
          <Timer className="w-5 h-5 text-rose-500" />
          <h3 className="text-xs font-bold text-rose-400 uppercase tracking-wider">
            30 Saniyelik Akıllı Özet (Spoilersız)
          </h3>
        </div>
        <p className="text-slate-300 text-sm leading-relaxed italic bg-slate-950/60 p-3.5 rounded-xl border border-slate-800/80">
          "{summary.thirtySecondOverview}"
        </p>
      </div>

      {/* Güçlü Yönler (Highlights) */}
      {summary.keyHighlights && summary.keyHighlights.length > 0 && (
        <div>
          <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider block mb-2 flex items-center gap-1.5">
            <Sparkles className="w-3.5 h-3.5 text-indigo-400" />
            Filmin Öne Çıkan Yönleri
          </span>
          <div className="flex flex-wrap gap-2">
            {summary.keyHighlights.map((highlight, index) => (
              <span key={index} className="px-3 py-1 bg-indigo-500/10 text-indigo-300 border border-indigo-500/30 rounded-lg text-xs font-medium">
                {highlight}
              </span>
            ))}
          </div>
        </div>
      )}

      {/* Kime Göre / Kime Göre Değil Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4 pt-4 border-t border-slate-800">
        {/* Kime Göre? */}
        <div className="bg-emerald-950/30 border border-emerald-500/25 p-3.5 rounded-xl space-y-2">
          <h4 className="text-xs font-bold text-emerald-400 uppercase tracking-wider flex items-center gap-1.5">
            <ThumbsUp className="w-3.5 h-3.5 text-emerald-400" />
            Kime Göre?
          </h4>
          <ul className="space-y-1.5">
            {summary.forWhom?.map((item, index) => (
              <li key={index} className="text-xs text-emerald-200/90 flex items-start gap-1.5">
                <span className="text-emerald-500 font-bold">•</span>
                <span>{item}</span>
              </li>
            ))}
          </ul>
        </div>

        {/* Kime Göre Değil? */}
        <div className="bg-rose-950/30 border border-rose-500/25 p-3.5 rounded-xl space-y-2">
          <h4 className="text-xs font-bold text-rose-400 uppercase tracking-wider flex items-center gap-1.5">
            <ThumbsDown className="w-3.5 h-3.5 text-rose-400" />
            Kime Göre Değil?
          </h4>
          <ul className="space-y-1.5">
            {summary.notForWhom?.map((item, index) => (
              <li key={index} className="text-xs text-rose-200/90 flex items-start gap-1.5">
                <span className="text-rose-500 font-bold">•</span>
                <span>{item}</span>
              </li>
            ))}
          </ul>
        </div>
      </div>
    </div>
  );
};

export default SmartSummaryCard;
