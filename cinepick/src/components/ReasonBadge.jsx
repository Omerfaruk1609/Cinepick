import React from 'react';
import { Sparkles } from 'lucide-react';

const ReasonBadge = ({ reason }) => {
  if (!reason) return null;

  return (
    <div className="mt-3 p-3 bg-gradient-to-r from-indigo-950/70 to-rose-950/70 border border-indigo-500/30 rounded-xl backdrop-blur-md flex items-start gap-2.5 shadow-md">
      <Sparkles className="w-4 h-4 text-rose-400 shrink-0 mt-0.5 animate-pulse" />
      <p className="text-xs text-indigo-200/90 font-medium leading-relaxed italic">
        "{reason}"
      </p>
    </div>
  );
};

export default ReasonBadge;
