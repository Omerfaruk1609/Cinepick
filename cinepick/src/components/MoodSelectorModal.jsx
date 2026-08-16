import React, { useState } from 'react';
import { getMoodRecommendations, cleanTitle } from '../services/api';
import MovieCard from './MovieCard';
import MatchBadge from './MatchBadge';
import { Sparkles, X, Send, Smile, RefreshCw } from 'lucide-react';

const PRESET_MOODS = [
  { tag: 'melancholic', label: 'Melankolik & Duygusal', emoji: '🌧️', query: 'melancholic emotional touching deeply moving drama' },
  { tag: 'energetic', label: 'Enerjik & Adrenalin', emoji: '⚡', query: 'energetic high adrenaline action adventure fast paced' },
  { tag: 'tense', label: 'Gerilimli & Gizemli', emoji: '🍿', query: 'tense suspenseful psychological thriller dark mystery' },
  { tag: 'romantic', label: 'Romantik & Sıcak', emoji: '✨', query: 'romantic heartwarming sweet love story feel good' },
  { tag: 'thoughtful', label: 'Zihin Büken & Düşündürücü', emoji: '🧠', query: 'thought-provoking philosophical mind bending complex sci-fi' },
  { tag: 'cheerful', label: 'Neşeli & Eğlenceli', emoji: '🎉', query: 'cheerful funny uplifting hilarious lighthearted comedy' }
];

export default function MoodSelectorModal({
  isOpen,
  onClose,
  isInWatchlist,
  isWatched,
  onToggleWatchlist,
  onToggleWatched,
  onMovieClick
}) {
  const [selectedTag, setSelectedTag] = useState(null);
  const [customMood, setCustomMood] = useState('');
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searched, setSearched] = useState(false);

  if (!isOpen) return null;

  const fetchRecommendations = async (tag, customText) => {
    setLoading(true);
    setSearched(true);
    try {
      const response = await getMoodRecommendations({
        moodTag: tag || undefined,
        moodText: customText || undefined,
        limit: 20
      });
      setResults(response || []);
    } catch (err) {
      console.error('Mood recommendation error:', err);
      setResults([]);
    } finally {
      setLoading(false);
    }
  };

  const handlePresetSelect = (mood) => {
    setSelectedTag(mood.tag);
    fetchRecommendations(mood.tag, customMood);
  };

  const handleCustomSubmit = (e) => {
    e.preventDefault();
    if (!customMood.trim() && !selectedTag) return;
    fetchRecommendations(selectedTag, customMood.trim());
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-md animate-fadeIn">
      <div className="relative w-full max-w-4xl max-h-[90vh] flex flex-col bg-slate-900 border border-rose-500/20 rounded-3xl shadow-2xl overflow-hidden">
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-5 border-b border-slate-800 bg-gradient-to-r from-rose-950/40 via-slate-900 to-slate-900">
          <div className="flex items-center gap-3">
            <div className="p-2.5 rounded-2xl bg-rose-500/10 border border-rose-500/30 text-rose-400">
              <Smile className="w-6 h-6 animate-bounce" />
            </div>
            <div>
              <h2 className="text-lg font-bold text-white flex items-center gap-2">
                Ruh Haline Göre AI Film Keşfi
              </h2>
              <p className="text-xs text-slate-400">
                Şu anki modunu seç veya hissettiğin atmosferi serbestçe yaz!
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
          {/* Preset Mood Buttons */}
          <div className="space-y-2">
            <label className="text-xs font-semibold text-slate-400 uppercase tracking-wider">
              Hazır Ruh Halleri
            </label>
            <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
              {PRESET_MOODS.map((mood) => {
                const isSelected = selectedTag === mood.tag;
                return (
                  <button
                    key={mood.tag}
                    onClick={() => handlePresetSelect(mood)}
                    className={`flex items-center gap-2.5 px-4 py-3 rounded-2xl border text-xs font-semibold transition-all cursor-pointer shadow-md ${
                      isSelected
                        ? 'bg-rose-600 border-rose-500 text-white shadow-rose-900/40 scale-[1.02]'
                        : 'bg-slate-800/80 border-slate-700/70 text-slate-300 hover:bg-slate-700/80 hover:text-white'
                    }`}
                  >
                    <span className="text-lg leading-none">{mood.emoji}</span>
                    <span>{mood.label}</span>
                  </button>
                );
              })}
            </div>
          </div>

          {/* Custom Mood Text Input */}
          <form onSubmit={handleCustomSubmit} className="space-y-3">
            <div className="relative flex items-center">
              <input
                type="text"
                value={customMood}
                onChange={(e) => setCustomMood(e.target.value)}
                placeholder="Veya kendi ruh halini tarif et: 'Hafif yorgun ama heyecan arayan bir cuma akşamı'..."
                className="w-full bg-slate-800/80 border border-slate-700 focus:border-rose-500 rounded-2xl py-3.5 pl-4 pr-28 text-xs sm:text-sm text-white placeholder:text-slate-500 focus:outline-none transition-all shadow-inner"
              />
              <button
                type="submit"
                disabled={loading || (!customMood.trim() && !selectedTag)}
                className="absolute right-2 flex items-center gap-1.5 px-4 py-2 rounded-xl bg-gradient-to-r from-rose-600 to-rose-700 hover:from-rose-500 hover:to-rose-600 text-white font-medium text-xs disabled:opacity-50 disabled:cursor-not-allowed cursor-pointer transition-all shadow-md"
              >
                {loading ? <RefreshCw className="w-3.5 h-3.5 animate-spin" /> : <Send className="w-3.5 h-3.5" />}
                Öner
              </button>
            </div>
          </form>

          {/* Loading Animation */}
          {loading && (
            <div className="flex flex-col items-center justify-center py-12 space-y-3">
              <div className="w-12 h-12 border-4 border-rose-500/20 border-t-rose-500 rounded-full animate-spin"></div>
              <p className="text-xs text-slate-400 animate-pulse">Ruh haline en uygun filmler eşleştiriliyor...</p>
            </div>
          )}

          {/* Results List */}
          {!loading && searched && (
            <div className="space-y-4">
              <h3 className="text-sm font-semibold text-slate-400">
                Ruh Haline En Uygun Filmler ({results.length})
              </h3>

              {results.length === 0 ? (
                <div className="py-10 text-center text-slate-500 text-xs bg-slate-950/40 rounded-2xl border border-slate-800">
                  Bu ruh haline uygun film bulunamadı. Lütfen başka bir ruh hali deneyin.
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
