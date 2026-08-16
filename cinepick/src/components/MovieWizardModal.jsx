import React, { useState } from 'react';
import { getWizardRecommendations, cleanTitle } from '../services/api';
import MovieCard from './MovieCard';
import MatchBadge from './MatchBadge';
import {
  Wand2, X, ChevronRight, ChevronLeft, Check, Sparkles,
  Tv, Smile, Compass, RefreshCw, Flame
} from 'lucide-react';

const STREAMING_PLATFORMS = [
  { id: 'Netflix', name: 'Netflix', color: 'border-red-600/50 bg-red-950/20 text-red-400' },
  { id: 'Amazon Prime Video', name: 'Prime Video', color: 'border-blue-500/50 bg-blue-950/20 text-blue-400' },
  { id: 'Disney Plus', name: 'Disney+', color: 'border-indigo-500/50 bg-indigo-950/20 text-indigo-400' },
  { id: 'BluTV', name: 'BluTV', color: 'border-cyan-500/50 bg-cyan-950/20 text-cyan-400' },
  { id: 'TOD', name: 'TOD / beIN', color: 'border-purple-500/50 bg-purple-950/20 text-purple-400' },
  { id: 'MUBI', name: 'MUBI', color: 'border-amber-500/50 bg-amber-950/20 text-amber-400' }
];

const MOOD_OPTIONS = [
  { id: 'energetic', emoji: '⚡', label: 'Enerjik & Adrenalin', desc: 'Heyecanlı, aksiyon ve macera dolu' },
  { id: 'melancholic', emoji: '🌧️', label: 'Melankolik & Dokunaklı', desc: 'Derin duygular, gözyaşı ve drama' },
  { id: 'tense', emoji: '🍿', label: 'Gerilimli & Gizemli', desc: 'Nefes kesen gizem ve psikolojik gerilim' },
  { id: 'romantic', emoji: '✨', label: 'Romantik & Sıcak', desc: 'İç ısıtan aşk ve samimi ilişkiler' },
  { id: 'thoughtful', emoji: '🧠', label: 'Zihin Büken', desc: 'Felsefi, düşündürücü ve ters köşe' },
  { id: 'cheerful', emoji: '🎉', label: 'Neşeli & Kahkaha', desc: 'Kafayı dağıtacak hafif komedi' }
];

export default function MovieWizardModal({
  isOpen,
  onClose,
  isInWatchlist,
  isWatched,
  onToggleWatchlist,
  onToggleWatched,
  onMovieClick
}) {
  const [step, setStep] = useState(1);
  const [platforms, setPlatforms] = useState([]);
  const [mood, setMood] = useState('energetic');
  const [customPrompt, setCustomPrompt] = useState('');
  const [origin, setOrigin] = useState('ALL'); // "TR", "FOREIGN", "ALL"
  const [era, setEra] = useState('ALL'); // "CLASSIC", "MODERN", "NEW", "ALL"
  const [pace, setPace] = useState('BALANCED'); // "FAST", "SLOW", "BALANCED"

  const [loading, setLoading] = useState(false);
  const [results, setResults] = useState([]);
  const [completed, setCompleted] = useState(false);

  if (!isOpen) return null;

  const togglePlatform = (pName) => {
    setPlatforms(prev =>
      prev.includes(pName) ? prev.filter(p => p !== pName) : [...prev, pName]
    );
  };

  const handleFinish = async () => {
    setLoading(true);
    setCompleted(true);
    try {
      const response = await getWizardRecommendations({
        mood,
        origin,
        era,
        pace,
        platforms: platforms.length > 0 ? platforms : null,
        customPrompt: customPrompt.trim() || undefined,
        limit: 10
      });
      setResults(response || []);
    } catch (err) {
      console.error('Wizard search error:', err);
      setResults([]);
    } finally {
      setLoading(false);
    }
  };

  const handleResetWizard = () => {
    setStep(1);
    setCompleted(false);
    setResults([]);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-md animate-fadeIn">
      <div className="relative w-full max-w-4xl max-h-[92vh] flex flex-col bg-slate-900 border border-rose-500/30 rounded-3xl shadow-2xl overflow-hidden">
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-800 bg-gradient-to-r from-rose-950/40 via-slate-900 to-slate-900">
          <div className="flex items-center gap-3">
            <div className="p-2 rounded-2xl bg-rose-500/10 border border-rose-500/30 text-rose-400">
              <Wand2 className="w-5 h-5 animate-pulse" />
            </div>
            <div>
              <h2 className="text-base sm:text-lg font-bold text-white flex items-center gap-2">
                İnteraktif AI Film Sihirbazı
              </h2>
              <p className="text-xs text-slate-400">
                {completed ? 'Sana özel kişiselleştirilmiş sihirbaz sonuçları' : `Adım ${step} / 3: Tercihlerini Belirle`}
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

        {/* Progress Bar */}
        {!completed && (
          <div className="w-full bg-slate-800 h-1.5">
            <div
              className="bg-gradient-to-r from-rose-600 to-rose-400 h-1.5 transition-all duration-300"
              style={{ width: `${(step / 3) * 100}%` }}
            />
          </div>
        )}

        {/* Body Content */}
        <div className="flex-1 overflow-y-auto p-6 space-y-6">
          {!completed ? (
            <>
              {/* ADIM 1: Platformlar */}
              {step === 1 && (
                <div className="space-y-4 animate-fadeIn">
                  <div className="flex items-center gap-2 text-rose-400 font-semibold text-sm">
                    <Tv className="w-4 h-4" />
                    <span>Hangi yayın platformlarına sahipsin?</span>
                  </div>
                  <p className="text-xs text-slate-400">
                    Birden fazla seçebilirsin. Hiçbirini seçmezsen tüm platformlardaki filmler aranır.
                  </p>

                  <div className="grid grid-cols-2 sm:grid-cols-3 gap-3 pt-2">
                    {STREAMING_PLATFORMS.map(p => {
                      const isSelected = platforms.includes(p.id);
                      return (
                        <button
                          key={p.id}
                          type="button"
                          onClick={() => togglePlatform(p.id)}
                          className={`flex items-center justify-between p-3.5 rounded-2xl border transition-all cursor-pointer ${
                            isSelected
                              ? `${p.color} border-opacity-100 shadow-md ring-2 ring-rose-500/40`
                              : 'bg-slate-800/60 border-slate-700/60 text-slate-300 hover:bg-slate-700/60'
                          }`}
                        >
                          <span className="text-xs font-semibold">{p.name}</span>
                          {isSelected && <Check className="w-4 h-4 text-rose-400" />}
                        </button>
                      );
                    })}
                  </div>
                </div>
              )}

              {/* ADIM 2: Ruh Hali */}
              {step === 2 && (
                <div className="space-y-4 animate-fadeIn">
                  <div className="flex items-center gap-2 text-rose-400 font-semibold text-sm">
                    <Smile className="w-4 h-4" />
                    <span>Şu anki ruh halin ve izlemek istediğin atmosfer nasıl?</span>
                  </div>

                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-2">
                    {MOOD_OPTIONS.map(m => {
                      const isSelected = mood === m.id;
                      return (
                        <button
                          key={m.id}
                          type="button"
                          onClick={() => setMood(m.id)}
                          className={`flex items-start gap-3 p-3.5 rounded-2xl border text-left transition-all cursor-pointer ${
                            isSelected
                              ? 'bg-rose-950/40 border-rose-500 text-white ring-2 ring-rose-500/40'
                              : 'bg-slate-800/60 border-slate-700/60 text-slate-300 hover:bg-slate-700/60'
                          }`}
                        >
                          <span className="text-2xl leading-none">{m.emoji}</span>
                          <div>
                            <p className="text-xs font-bold">{m.label}</p>
                            <p className="text-[11px] text-slate-400 mt-0.5">{m.desc}</p>
                          </div>
                        </button>
                      );
                    })}
                  </div>

                  {/* İsteğe Bağlı Ek Açıklama */}
                  <div className="space-y-1.5 pt-2">
                    <label className="text-xs font-semibold text-slate-400">
                      Özel bir detay eklemek ister misin? (İsteğe bağlı)
                    </label>
                    <input
                      type="text"
                      value={customPrompt}
                      onChange={e => setCustomPrompt(e.target.value)}
                      placeholder="Örn: 90'lar nostaljisi, yağmurlu New York atmosferi..."
                      className="w-full bg-slate-800 border border-slate-700 focus:border-rose-500 rounded-xl px-3.5 py-2.5 text-xs text-white placeholder:text-slate-500 focus:outline-none"
                    />
                  </div>
                </div>
              )}

              {/* ADIM 3: Sinematik Tat ve Kriterler */}
              {step === 3 && (
                <div className="space-y-5 animate-fadeIn">
                  <div className="flex items-center gap-2 text-rose-400 font-semibold text-sm">
                    <Compass className="w-4 h-4" />
                    <span>Nasıl bir sinematik tat arıyorsun?</span>
                  </div>

                  {/* Yapım Menşei */}
                  <div className="space-y-2">
                    <label className="text-xs font-semibold text-slate-400">Yapım Menşei</label>
                    <div className="grid grid-cols-3 gap-2">
                      {[
                        { id: 'ALL', label: '🌍 Tümü' },
                        { id: 'TR', label: '🇹🇷 Yerli (Türkçe)' },
                        { id: 'FOREIGN', label: '🌐 Yabancı' }
                      ].map(item => (
                        <button
                          key={item.id}
                          type="button"
                          onClick={() => setOrigin(item.id)}
                          className={`p-2.5 rounded-xl border text-xs font-medium transition-all cursor-pointer ${
                            origin === item.id
                              ? 'bg-rose-600 border-rose-500 text-white font-bold shadow-md'
                              : 'bg-slate-800/60 border-slate-700/60 text-slate-300 hover:bg-slate-700/60'
                          }`}
                        >
                          {item.label}
                        </button>
                      ))}
                    </div>
                  </div>

                  {/* Dönem */}
                  <div className="space-y-2">
                    <label className="text-xs font-semibold text-slate-400">Dönem / Yıl</label>
                    <div className="grid grid-cols-4 gap-2">
                      {[
                        { id: 'ALL', label: 'Tüm Yıllar' },
                        { id: 'CLASSIC', label: "90'lar & Öncesi" },
                        { id: 'MODERN', label: '2000 - 2019' },
                        { id: 'NEW', label: 'Yeni Vizyon (2020+)' }
                      ].map(item => (
                        <button
                          key={item.id}
                          type="button"
                          onClick={() => setEra(item.id)}
                          className={`p-2.5 rounded-xl border text-xs font-medium transition-all cursor-pointer ${
                            era === item.id
                              ? 'bg-rose-600 border-rose-500 text-white font-bold shadow-md'
                              : 'bg-slate-800/60 border-slate-700/60 text-slate-300 hover:bg-slate-700/60'
                          }`}
                        >
                          {item.label}
                        </button>
                      ))}
                    </div>
                  </div>

                  {/* Tempo */}
                  <div className="space-y-2">
                    <label className="text-xs font-semibold text-slate-400">Tempo & Süre</label>
                    <div className="grid grid-cols-3 gap-2">
                      {[
                        { id: 'BALANCED', label: '⚖️ Dengeli' },
                        { id: 'FAST', label: '⚡ Hızlı & Akıcı (<110 dk)' },
                        { id: 'SLOW', label: '☕ Sakin & Sanatsal' }
                      ].map(item => (
                        <button
                          key={item.id}
                          type="button"
                          onClick={() => setPace(item.id)}
                          className={`p-2.5 rounded-xl border text-xs font-medium transition-all cursor-pointer ${
                            pace === item.id
                              ? 'bg-rose-600 border-rose-500 text-white font-bold shadow-md'
                              : 'bg-slate-800/60 border-slate-700/60 text-slate-300 hover:bg-slate-700/60'
                          }`}
                        >
                          {item.label}
                        </button>
                      ))}
                    </div>
                  </div>
                </div>
              )}
            </>
          ) : (
            /* SONUÇLAR ALANI */
            <div className="space-y-5 animate-fadeIn">
              {loading ? (
                <div className="flex flex-col items-center justify-center py-16 space-y-4">
                  <div className="relative flex items-center justify-center">
                    <div className="w-14 h-14 border-4 border-rose-500/20 border-t-rose-500 rounded-full animate-spin"></div>
                    <Sparkles className="w-6 h-6 text-rose-400 absolute animate-pulse" />
                  </div>
                  <div className="text-center space-y-1">
                    <p className="text-sm font-semibold text-slate-200">Sihirbaz Kriterleri Vektörleştiriliyor...</p>
                    <p className="text-xs text-slate-500">5.000+ film arasından en uyumlu yapımlar taranıyor.</p>
                  </div>
                </div>
              ) : (
                <>
                  <div className="flex items-center justify-between">
                    <h3 className="text-sm font-bold text-slate-200 flex items-center gap-2">
                      <Sparkles className="w-4 h-4 text-rose-400" />
                      Sihirbazın Senin İçin Seçtiği Filmler ({results.length})
                    </h3>
                    <button
                      onClick={handleResetWizard}
                      className="flex items-center gap-1.5 text-xs text-rose-400 hover:text-rose-300 transition-colors cursor-pointer"
                    >
                      <RefreshCw className="w-3.5 h-3.5" />
                      Sihirbazı Yeniden Başlat
                    </button>
                  </div>

                  {results.length === 0 ? (
                    <div className="py-12 text-center text-slate-500 text-xs bg-slate-950/40 rounded-2xl border border-slate-800">
                      Bu kriter kombinasyonuna uygun film bulunamadı. Lütfen kriterlerinizi genişletmeyi deneyin.
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
                </>
              )}
            </div>
          )}
        </div>

        {/* Footer Navigation */}
        {!completed && (
          <div className="flex items-center justify-between px-6 py-4 border-t border-slate-800 bg-slate-950/50">
            <button
              type="button"
              onClick={() => setStep(prev => Math.max(1, prev - 1))}
              disabled={step === 1}
              className="flex items-center gap-1.5 px-4 py-2 rounded-xl text-xs font-semibold text-slate-400 hover:text-white disabled:opacity-30 disabled:cursor-not-allowed cursor-pointer transition-colors"
            >
              <ChevronLeft className="w-4 h-4" />
              Geri
            </button>

            {step < 3 ? (
              <button
                type="button"
                onClick={() => setStep(prev => Math.min(3, prev + 1))}
                className="flex items-center gap-1.5 px-5 py-2.5 rounded-xl bg-gradient-to-r from-rose-600 to-rose-700 hover:from-rose-500 hover:to-rose-600 text-white text-xs font-bold shadow-md cursor-pointer transition-all active:scale-95"
              >
                Sonraki Adım
                <ChevronRight className="w-4 h-4" />
              </button>
            ) : (
              <button
                type="button"
                onClick={handleFinish}
                className="flex items-center gap-2 px-6 py-2.5 rounded-xl bg-gradient-to-r from-rose-600 via-rose-500 to-amber-500 hover:opacity-90 text-white text-xs font-bold shadow-lg shadow-rose-950/50 cursor-pointer transition-all active:scale-95"
              >
                <Wand2 className="w-4 h-4" />
                Sihirli Önerileri Getir
              </button>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
