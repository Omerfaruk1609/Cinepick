import React from 'react';

export const MovieCardSkeleton = () => {
  return (
    <div className="animate-pulse bg-slate-800/80 rounded-xl overflow-hidden shadow-lg border border-slate-700/50 flex flex-col h-[420px] w-full">
      <div className="bg-slate-700/60 h-64 w-full" />
      <div className="p-4 flex flex-col flex-1 justify-between space-y-3">
        <div className="space-y-2">
          <div className="h-5 bg-slate-700/80 rounded-md w-3/4" />
          <div className="h-4 bg-slate-700/50 rounded-md w-1/2" />
        </div>
        <div className="space-y-2">
          <div className="h-3 bg-slate-700/40 rounded-md w-full" />
          <div className="h-3 bg-slate-700/40 rounded-md w-5/6" />
        </div>
        <div className="flex justify-between items-center pt-2 border-t border-slate-700/30">
          <div className="h-4 bg-slate-700/60 rounded-md w-1/4" />
          <div className="h-8 bg-purple-600/30 rounded-lg w-1/3" />
        </div>
      </div>
    </div>
  );
};

export const NarrativeAnalysisSkeleton = () => {
  return (
    <div className="animate-pulse bg-slate-900/90 border border-purple-500/20 rounded-2xl p-6 space-y-6 max-w-4xl mx-auto shadow-2xl backdrop-blur-md">
      <div className="flex items-center space-x-3">
        <div className="w-8 h-8 rounded-full bg-purple-500/30" />
        <div className="h-6 bg-slate-700/80 rounded-md w-1/3" />
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div className="bg-slate-800/50 p-4 rounded-xl space-y-2 border border-slate-700/40">
          <div className="h-4 bg-slate-700/70 rounded w-1/4" />
          <div className="h-4 bg-slate-700/40 rounded w-3/4" />
        </div>
        <div className="bg-slate-800/50 p-4 rounded-xl space-y-2 border border-slate-700/40">
          <div className="h-4 bg-slate-700/70 rounded w-1/4" />
          <div className="h-4 bg-slate-700/40 rounded w-3/4" />
        </div>
      </div>

      <div className="space-y-3">
        <div className="h-5 bg-slate-700/70 rounded w-1/5" />
        <div className="flex flex-wrap gap-2">
          <div className="h-7 w-20 bg-purple-600/20 rounded-full" />
          <div className="h-7 w-24 bg-purple-600/20 rounded-full" />
          <div className="h-7 w-16 bg-purple-600/20 rounded-full" />
        </div>
      </div>

      <div className="bg-purple-950/20 p-5 rounded-xl border border-purple-800/30 space-y-2">
        <div className="h-5 bg-purple-400/40 rounded w-1/4" />
        <div className="h-4 bg-slate-700/50 rounded w-full" />
        <div className="h-4 bg-slate-700/50 rounded w-5/6" />
      </div>
    </div>
  );
};
