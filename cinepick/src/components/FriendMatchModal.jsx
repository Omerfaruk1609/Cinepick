import React, { useState } from 'react';
import apiClient from '../services/apiClient';
import { Users, Zap, Star, Loader2 } from 'lucide-react';
import { IMAGE_BASE_URL } from '../services/api';

const FriendMatchModal = () => {
  const [friendUsername, setFriendUsername] = useState('');
  const [matchResult, setMatchResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleMatch = async () => {
    if (!friendUsername.trim()) return;
    try {
      setLoading(true);
      setError(null);
      const response = await apiClient.get(`/v1/users/friend-match?friendUsername=${encodeURIComponent(friendUsername.trim())}`);
      setMatchResult(response.data);
    } catch (err) {
      console.error("Eşleşme başarısız:", err);
      setError(err.response?.data?.message || "Arkadaş bulunamadı veya henüz yeterli zevk verisi yok.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 max-w-2xl mx-auto shadow-2xl backdrop-blur-md space-y-6">
      {/* Başlık & Açıklama */}
      <div>
        <h3 className="text-xl font-black text-slate-100 mb-1 flex items-center gap-2">
          <Users className="w-5 h-5 text-indigo-400" />
          Arkadaşımla Eşleş (Ortak Öneri)
        </h3>
        <p className="text-xs text-slate-400 leading-relaxed">
          Arkadaşının kullanıcı adını gir, ortak sinema zevkinizi ölçelim ve ikinizin de izlemediği en uygun filmleri bulalım.
        </p>
      </div>

      {/* Arama Barı */}
      <div className="flex gap-2">
        <input
          type="text"
          value={friendUsername}
          onChange={(e) => setFriendUsername(e.target.value)}
          onKeyDown={(e) => e.key === 'Enter' && handleMatch()}
          placeholder="Arkadaşının Kullanıcı Adı veya E-postası"
          className="flex-1 bg-slate-950/80 border border-slate-800 rounded-xl px-4 py-2.5 text-sm text-slate-100 focus:outline-none focus:border-indigo-500 transition-all placeholder:text-slate-500"
        />
        <button
          onClick={handleMatch}
          disabled={loading}
          className="px-5 py-2.5 bg-indigo-600 hover:bg-indigo-500 disabled:opacity-50 text-white font-bold text-sm rounded-xl transition-all flex items-center gap-1.5 cursor-pointer shadow-lg shadow-indigo-600/30"
        >
          {loading ? (
            <>
              <Loader2 className="w-4 h-4 animate-spin" />
              <span>Hesaplanıyor...</span>
            </>
          ) : (
            <>
              <span>Eşleştir</span>
              <Zap className="w-4 h-4 fill-white" />
            </>
          )}
        </button>
      </div>

      {error && (
        <div className="p-3 rounded-xl bg-rose-500/10 border border-rose-500/30 text-rose-300 text-xs font-medium">
          {error}
        </div>
      )}

      {/* Sonuç Kartı */}
      {matchResult && (
        <div className="pt-6 border-t border-slate-800 space-y-6">
          {/* Skor Rozeti */}
          <div className="bg-gradient-to-r from-indigo-950/80 to-purple-950/80 border border-indigo-500/30 p-5 rounded-2xl text-center space-y-1 shadow-xl">
            <span className="text-xs font-semibold text-indigo-300 uppercase tracking-widest">
              Ortak Sinema Uyumunuz
            </span>
            <div className="text-4xl font-black text-slate-100">
              %{matchResult.friendshipMatchPercentage} Match
            </div>
            <p className="text-xs text-slate-300 font-medium">
              <span className="text-indigo-400 font-bold">{matchResult.user1Name}</span> & <span className="text-purple-400 font-bold">{matchResult.user2Name}</span> için harika bir sinema akşamı planı hazır!
            </p>
          </div>

          {/* Ortak Önerilen Filmler Grid */}
          <div className="space-y-3">
            <h4 className="text-xs font-bold text-slate-400 uppercase tracking-wider">
              🍿 İkinizin De İzlemediği Ortak Öneriler
            </h4>

            <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
              {matchResult.recommendedMovies?.map((movie) => {
                const posterUrl = movie.posterPath?.startsWith('http')
                  ? movie.posterPath
                  : movie.posterPath
                  ? `${IMAGE_BASE_URL}${movie.posterPath}`
                  : null;

                return (
                  <div key={movie.id} className="bg-slate-950/80 rounded-xl overflow-hidden border border-slate-800/80 hover:border-indigo-500/50 transition-all">
                    <div className="aspect-[2/3] w-full bg-slate-900 overflow-hidden">
                      {posterUrl ? (
                        <img src={posterUrl} alt={movie.title} className="w-full h-full object-cover" />
                      ) : (
                        <div className="w-full h-full flex items-center justify-center text-xs text-slate-600">Görsel Yok</div>
                      )}
                    </div>
                    <div className="p-2.5 space-y-1">
                      <h5 className="text-xs font-bold text-slate-100 truncate" title={movie.title}>{movie.title}</h5>
                      <div className="flex items-center gap-1 text-[10px] text-amber-400 font-medium">
                        <Star className="w-3 h-3 fill-amber-400" />
                        <span>{movie.voteAverage ? Number(movie.voteAverage).toFixed(1) : 'N/A'}</span>
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default FriendMatchModal;
