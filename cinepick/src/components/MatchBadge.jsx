import React from 'react';
import { Zap } from 'lucide-react';

const MatchBadge = ({ matchPercentage }) => {
  if (!matchPercentage) return null;

  // Skor seviyesine göre renk belirleme
  const getBadgeColor = (score) => {
    if (score >= 85) return 'bg-emerald-500/20 text-emerald-400 border-emerald-500/40 shadow-emerald-500/10';
    if (score >= 70) return 'bg-indigo-500/20 text-indigo-400 border-indigo-500/40 shadow-indigo-500/10';
    return 'bg-amber-500/20 text-amber-400 border-amber-500/40 shadow-amber-500/10';
  };

  return (
    <div className={`flex items-center gap-1 px-2.5 py-1 rounded-full border text-xs font-bold backdrop-blur-md shadow-md transition-all ${getBadgeColor(matchPercentage)}`}>
      <Zap className="w-3.5 h-3.5 fill-current" />
      <span>%{matchPercentage} Uyum</span>
    </div>
  );
};

export default MatchBadge;
