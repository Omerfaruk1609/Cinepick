import React, { useState } from 'react';
import { discoverByIntent, cleanTitle } from '../services/api';
import MovieCard from './MovieCard';
import MatchBadge from './MatchBadge';
import { Sparkles, X, Send, Bot, RefreshCw } from 'lucide-react';

export default function IntentDiscoveryModal({
  isOpen,
  onClose,
  isInWatchlist,
  isWatched,
  onToggleWatchlist,
  onToggleWatched,
  onMovieClick
}) {
  const [prompt, setPrompt] = useState('');
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searched, setSearched] = useState(false);

  if (!isOpen) return null;

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!prompt.trim()) return;

    setLoading(true);
    setSearched(true);
    try {
      const movies = await discoverByIntent(prompt.trim(), 10);
      setResults(movies || []);
    } catch (err) {
      console.error('Intent discovery search error:', err);
      setResults([]);
    } finally {
      setLoading(false);
    }
  };

  const handleQuickPrompt = (quickText) => {
    setPrompt(quickText);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-md animate-fadeIn">
      <div className="relative w-full max-w-4xl max-h-[90vh] flex flex-col bg-slate-900 border border-rose-500/20 rounded-3xl shadow-2xl overflow-hidden">
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-5 border-b border-slate-800 bg-gradient-to-r from-rose-950/40 via-slate-900 to-slate-900">
          <div className="flex items-center gap-3">
            <div className="p-2.5 rounded-2xl bg-rose-500/10 border border-rose-500/30 text-rose-400">
              <Sparkles className="w-6 h-6 animate-pulse" />
            </div>
            <div>
              <h2 className="text-lg font-bold text-white flex items-center gap-2">
                Niyet Bazlı AI Keşif Motoru
              </h2>
              <p className="text-xs text-slate-400">
                Aradığın hissi, atmosferi veya dönemi serbest metin olarak ifade et!
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-2 text-slate-400 hover:text-white hover:bg-slate-800 rounded-xl transition-colors cursor-pointer"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Content Area */}
        <div className="flex-1 overflow-y-auto p-6 space-y-6">
          {/* Prompt Input Form */}
          <form onSubmit={handleSearch} className="space-y-3">
            <div className="relative flex items-center">
              <input
                type="text"
                value={prompt}
                onChange={(e) => setPrompt(e.target.value)}
                placeholder="Örn: Beni 90'lara götürecek nostaljik ve duygusal bir film öner..."
                className="w-full bg-slate-800/80 border border-slate-700 focus:border-rose-500 rounded-2xl py-4 pl-4 pr-32 text-sm text-white placeholder:text-slate-500 focus:outline-none transition-all shadow-inner"
              />
              <button
                type="submit"
                disabled={loading || !prompt.trim()}
                className="absolute right-2 flex items-center gap-2 px-5 py-2.5 rounded-xl bg-gradient-to-r from-rose-600 to-rose-700 hover:from-rose-500 hover:to-rose-600 text-white font-medium text-xs disabled:opacity-50 disabled:cursor-not-allowed cursor-pointer transition-all shadow-md"
              >
                {loading ? (
                  <>
                    <RefreshCw className="w-3.5 h-3.5 animate-spin" />
                    Analiz Ediliyor...
                  </>
                ) : (
                  <>
                    <Send className="w-3.5 h-3.5" />
                    Keşfet
                  </>
                )}
              </button>
            </div>

            {/* Quick Prompts */}
            <div className="flex flex-wrap items-center gap-2 text-xs">
              <span className="text-slate-500 flex items-center gap-1 font-semibold">
                <Bot className="w-3.5 h-3.5 text-rose-400" /> Örnekler:
              </span>
              {[
                "Yağmurlu bir günde izlenecek melancholic dram",
                "Zihin büken şaşırtıcı sonlu siberpunk gerilim",
                "90'lar nostaljisi samimi arkadaşlık hikayesi",
                "Ege kasabasında geçen sıcak Ege komedisi"
              ].map((example, idx) => (
                <button
                  key={idx}
                  type="button"
                  onClick={() => handleQuickPrompt(example)}
                  className="px-3 py-1 rounded-xl bg-slate-800/60 hover:bg-slate-700/80 text-slate-300 hover:text-white border border-slate-700/50 transition-colors cursor-pointer"
                >
                  {example}
                </button>
              ))}
            </div>
          </form>

          {/* Loading Animation */}
          {loading && (
            <div className="flex flex-col items-center justify-center py-16 space-y-4">
              <div className="relative flex items-center justify-center">
                <div className="w-16 h-16 border-4 border-rose-500/20 border-t-rose-500 rounded-full animate-spin"></div>
                <Sparkles className="w-6 h-6 text-rose-400 absolute animate-pulse" />
              </div>
              <div className="text-center space-y-1">
                <p className="text-sm font-semibold text-slate-200">Yapay Zeka Semantik Analiz Yapıyor...</p>
                <p className="text-xs text-slate-500">Cümleniz ONNX 384d vektör alanında haritalanıyor.</p>
              </div>
            </div>
          )}

          {/* Results List */}
          {!loading && searched && (
            <div className="space-y-4">
              <h3 className="text-sm font-semibold text-slate-400">
                Niyetinize En Uygun Yapay Zeka Sonuçları ({results.length})
              </h3>

              {results.length === 0 ? (
                <div className="py-12 text-center text-slate-500 text-sm bg-slate-950/40 rounded-2xl border border-slate-800">
                  Bu aramaya uygun film bulunamadı. Lütfen cümlenizi farklı kelimelerle ifade etmeyi deneyin.
                </div>
              ) : (
                <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-5 gap-4">
                  {results.map((movie) => {
                    const displayMovie = {
                      ...movie,
                      title: cleanTitle(movie.title)
                    };

                    return (
                      <div key={movie.id || movie.tmdbId} className="relative group">
                        {movie.matchPercentage && (
                          <div className="absolute top-2 left-2 z-10">
                            <MatchBadge matchPercentage={movie.matchPercentage} />
                          </div>
                        )}

                        <MovieCard
                          movie={displayMovie}
                          isInWatchlist={isInWatchlist ? isInWatchlist(movie.id) : false}
                          isWatched={isWatched ? isWatched(movie.id) : false}
                          onToggleWatchlist={onToggleWatchlist}
                          onToggleWatched={onToggleWatched}
                          onClick={() => {
                            if (onMovieClick) onMovieClick(displayMovie);
                            onClose();
                          }}
                        />
                      </div>
                    );
                  })}
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
