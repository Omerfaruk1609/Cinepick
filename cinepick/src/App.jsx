import { useEffect, useState } from 'react';
import { getPopularMovies, getMoviesByGenre } from './services/api';
import { useWatchlist } from './hooks/useWatchlist';
import Header from './components/Header';
import MoodSelector from './components/MoodSelector';
import MovieCard from './components/MovieCard';
import MovieModal from './components/MovieModal';
import EmptyWatchlist from './components/EmptyWatchlist';
import { Bookmark, Loader2 } from 'lucide-react';

function App() {
  const [movies, setMovies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedMoodId, setSelectedMoodId] = useState('all');
  const [selectedMovie, setSelectedMovie] = useState(null);
  const [activeTab, setActiveTab] = useState('explore'); // 'explore' | 'watchlist'

  const { watchlist, isBookmarked, toggleWatchlist } = useWatchlist();

  const fetchMoviesForMood = async (mood) => {
    setLoading(true);
    try {
      let data;
      if (!mood || mood.id === 'all' || !mood.genreIds || mood.genreIds.length === 0) {
        data = await getPopularMovies();
      } else {
        data = await getMoviesByGenre(mood.genreIds);
      }
      setMovies(data || []);
    } catch (error) {
      console.error('Film yüklenirken hata:', error);
      setMovies([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMoviesForMood({ id: 'all', genreIds: [] });
  }, []);

  const handleMoodSelect = (mood) => {
    setSelectedMoodId(mood.id);
    fetchMoviesForMood(mood);
  };

  const displayedMovies = activeTab === 'explore' ? movies : watchlist;

  return (
    <div className="min-h-screen bg-[#0b0f19] text-slate-100 p-4 sm:p-6 font-sans">
      {/* Üst Navigasyon & Header */}
      <Header
        activeTab={activeTab}
        onTabChange={setActiveTab}
        watchlistCount={watchlist.length}
      />

      {/* Ana İçerik */}
      <main className="max-w-6xl mx-auto">
        {/* Ruh Hali Seçici (Sadece Keşfet Sekmesinde) */}
        {activeTab === 'explore' && (
          <MoodSelector
            selectedMoodId={selectedMoodId}
            onSelectMood={handleMoodSelect}
          />
        )}

        {/* Bölüm Başlığı */}
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-xl font-bold text-slate-200 flex items-center gap-2">
            {activeTab === 'explore' ? (
              selectedMoodId === 'all' ? 'Öne Çıkan Filmler' : 'Seçilen Moda Uygun Filmler'
            ) : (
              <span className="flex items-center gap-2">
                <Bookmark className="w-5 h-5 text-rose-500" />
                İzleme Listeniz
              </span>
            )}
          </h2>
          <span className="text-xs text-slate-400 font-mono">
            {displayedMovies.length} Film Bulundu
          </span>
        </div>

        {/* Yükleniyor Durumu */}
        {loading && activeTab === 'explore' ? (
          <div className="flex flex-col items-center justify-center py-24 gap-3 text-slate-400">
            <Loader2 className="w-8 h-8 text-rose-500 animate-spin" />
            <p className="text-sm">Filmler yükleniyor...</p>
          </div>
        ) : activeTab === 'watchlist' && watchlist.length === 0 ? (
          /* Boş Liste Durumu */
          <EmptyWatchlist onGoToExplore={() => setActiveTab('explore')} />
        ) : displayedMovies.length === 0 ? (
          <div className="text-center py-20 text-slate-500 border border-dashed border-slate-800 rounded-xl">
            Bu kategoride gösterilecek film bulunamadı.
          </div>
        ) : (
          /* Film Izgarası */
          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-6">
            {displayedMovies.map((movie) => (
              <MovieCard
                key={movie.id}
                movie={movie}
                isBookmarked={isBookmarked(movie.id)}
                onToggleWatchlist={toggleWatchlist}
                onClick={() => setSelectedMovie(movie)}
              />
            ))}
          </div>
        )}
      </main>

      {/* Film Detay Modalı */}
      {selectedMovie && (
        <MovieModal
          movie={selectedMovie}
          onClose={() => setSelectedMovie(null)}
          isBookmarked={isBookmarked}
          onToggleWatchlist={toggleWatchlist}
        />
      )}
    </div>
  );
}

export default App;
