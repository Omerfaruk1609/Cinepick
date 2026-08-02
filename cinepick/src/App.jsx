import { useEffect, useState } from 'react';
import { getPopularMovies, getMoviesByGenre, searchMovies } from './services/api';
import { useMovieLists } from './hooks/useMovieLists';
import { MOODS } from './data/moods';
import Header from './components/Header';
import MoodSelector from './components/MoodSelector';
import MovieCard from './components/MovieCard';
import MovieModal from './components/MovieModal';
import EmptyWatchlist from './components/EmptyWatchlist';
import ProfileModal from './components/ProfileModal';
import { Bookmark, CheckCircle2, Loader2, Compass } from 'lucide-react';

function App() {
  const [movies, setMovies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedMoodId, setSelectedMoodId] = useState('all');
  const [selectedMovie, setSelectedMovie] = useState(null);
  const [activeTab, setActiveTab] = useState('explore'); // 'explore' | 'watchlist' | 'watched'
  const [searchQuery, setSearchQuery] = useState('');
  const [isProfileOpen, setIsProfileOpen] = useState(false);

  const {
    watchlist,
    watched,
    isInWatchlist,
    isWatched,
    toggleWatchlist,
    toggleWatched,
  } = useMovieLists();

  // Mood verisini çek
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

  // Arama metni değiştiğinde arama yap
  const handleSearchChange = (query) => {
    setSearchQuery(query);
    if (query.trim().length > 0) {
      setSelectedMoodId('all'); // Arama yapıldığında aktif modu sıfırla
      setLoading(true);
      searchMovies(query).then((results) => {
        setMovies(results || []);
        setLoading(false);
      });
    } else {
      fetchMoviesForMood({ id: selectedMoodId, genreIds: [] });
    }
  };

  useEffect(() => {
    fetchMoviesForMood({ id: 'all', genreIds: [] });
  }, []);

  // Mood seçildiğinde arama girdisini temizle ve moda göre film getir
  const handleMoodSelect = (mood) => {
    setSelectedMoodId(mood.id);
    setSearchQuery(''); // Mod seçildiğinde arama girdisini sıfırla
    fetchMoviesForMood(mood);
  };

  // Logo tıklandığında tüm filtreleri sıfırla
  const handleLogoClick = () => {
    setActiveTab('explore');
    setSelectedMoodId('all');
    setSearchQuery('');
    fetchMoviesForMood({ id: 'all', genreIds: [] });
  };

  // Aktif sekmeye göre gösterilecek liste
  const getDisplayedMovies = () => {
    if (activeTab === 'watchlist') return watchlist;
    if (activeTab === 'watched') return watched;
    return movies;
  };

  const displayedMovies = getDisplayedMovies();
  const currentMoodObj = MOODS.find((m) => m.id === selectedMoodId);

  return (
    <div className="min-h-screen bg-[#0b0f19] text-slate-100 p-4 sm:p-6 font-sans">
      {/* Üst Navigasyon & Header */}
      <Header
        activeTab={activeTab}
        onTabChange={setActiveTab}
        watchlistCount={watchlist.length}
        watchedCount={watched.length}
        selectedMoodLabel={currentMoodObj ? currentMoodObj.label : 'Tümü'}
        searchQuery={searchQuery}
        onSearchChange={handleSearchChange}
        onLogoClick={handleLogoClick}
        onOpenProfile={() => setIsProfileOpen(true)}
      />

      {/* Ana İçerik */}
      <main className="max-w-6xl mx-auto">
        {/* Ruh Hali Seçici (Sadece Keşfet Sekmesinde ve arama yapılmıyorsa gösterilir) */}
        {activeTab === 'explore' && !searchQuery && (
          <MoodSelector
            selectedMoodId={selectedMoodId}
            onSelectMood={handleMoodSelect}
          />
        )}

        {/* Bölüm Başlığı */}
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-xl font-bold text-slate-200 flex items-center gap-2">
            {activeTab === 'explore' ? (
              searchQuery ? (
                <span>"{searchQuery}" Arama Sonuçları</span>
              ) : selectedMoodId === 'all' ? (
                'Öne Çıkan Filmler'
              ) : (
                'Seçilen Moda Uygun Filmler'
              )
            ) : activeTab === 'watchlist' ? (
              <span className="flex items-center gap-2">
                <Bookmark className="w-5 h-5 text-rose-500" />
                İzleyeceklerim Listesi
              </span>
            ) : (
              <span className="flex items-center gap-2">
                <CheckCircle2 className="w-5 h-5 text-emerald-500" />
                İzlediklerim Listesi
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
          <EmptyWatchlist onGoToExplore={() => setActiveTab('explore')} />
        ) : activeTab === 'watched' && watched.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-20 px-4 text-center border border-dashed border-slate-800 rounded-2xl bg-slate-900/40">
            <div className="w-16 h-16 rounded-full bg-slate-800/80 flex items-center justify-center text-emerald-500 mb-4 border border-slate-700/50">
              <CheckCircle2 className="w-8 h-8" />
            </div>
            <h3 className="text-lg font-bold text-slate-200 mb-2">
              Henüz izlediğiniz bir film işaretlemediniz
            </h3>
            <p className="text-sm text-slate-400 max-w-md mb-6">
              İzlediğiniz filmlerin üzerindeki onay ikonuna tıklayarak izlediklerim listenize ekleyebilirsiniz.
            </p>
            <button
              onClick={() => setActiveTab('explore')}
              className="flex items-center gap-2 px-5 py-2.5 rounded-xl bg-rose-600 hover:bg-rose-500 text-white font-semibold text-sm transition-all shadow-lg shadow-rose-600/25 cursor-pointer"
            >
              <Compass className="w-4 h-4" />
              <span>Filmleri Keşfetmeye Başla</span>
            </button>
          </div>
        ) : displayedMovies.length === 0 ? (
          <div className="text-center py-20 text-slate-500 border border-dashed border-slate-800 rounded-xl">
            Gösterilecek film bulunamadı.
          </div>
        ) : (
          /* Film Grid Listesi */
          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-6">
            {displayedMovies.map((movie) => (
              <MovieCard
                key={movie.id}
                movie={movie}
                isInWatchlist={isInWatchlist(movie.id)}
                isWatched={isWatched(movie.id)}
                onToggleWatchlist={toggleWatchlist}
                onToggleWatched={toggleWatched}
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
          isInWatchlist={isInWatchlist}
          isWatched={isWatched}
          onToggleWatchlist={toggleWatchlist}
          onToggleWatched={toggleWatched}
        />
      )}

      {/* Kullanıcı Profil & Ayarlar Modalı */}
      <ProfileModal
        isOpen={isProfileOpen}
        onClose={() => setIsProfileOpen(false)}
        watchlistCount={watchlist.length}
        watchedCount={watched.length}
      />
    </div>
  );
}

export default App;
