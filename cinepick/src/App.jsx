import { useEffect, useState } from 'react';
import { getPopularMovies, getMoviesByGenre, searchMovies, filterMovies } from './services/api';
import { useMovieLists } from './hooks/useMovieLists';
import { useTheme } from './hooks/useTheme';
import { useAuth } from './hooks/useAuth';
import { useUserRatings } from './hooks/useUserRatings';
import { MOODS } from './data/moods';

import Header from './components/Header';
import MoodSelector from './components/MoodSelector';
import GenreSelector from './components/GenreSelector';
import MovieCard from './components/MovieCard';
import MovieModal from './components/MovieModal';
import EmptyWatchlist from './components/EmptyWatchlist';
import ProfileModal from './components/ProfileModal';
import AuthModal from './components/AuthModal';
import OnboardingModal from './components/OnboardingModal';
import RecommenderBlock from './components/RecommenderBlock';
import FilterPanel from './components/FilterPanel';
import MovieWizardModal from './components/MovieWizardModal';
import IntentDiscoveryModal from './components/IntentDiscoveryModal';
import MoodSelectorModal from './components/MoodSelectorModal';
import Footer from './components/Footer';

import { Bookmark, CheckCircle2, Loader2, Compass, ChevronLeft, ChevronRight, Wand2, Filter, Sparkles, Smile } from 'lucide-react';

function App() {
  const [movies, setMovies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedMoodId, setSelectedMoodId] = useState('all');
  const [selectedGenreId, setSelectedGenreId] = useState(0);
  const [selectedMovie, setSelectedMovie] = useState(null);
  const [activeTab, setActiveTab] = useState('explore'); // 'explore' | 'watchlist' | 'watched'
  const [searchQuery, setSearchQuery] = useState('');
  const [isProfileOpen, setIsProfileOpen] = useState(false);
  const [isAuthOpen, setIsAuthOpen] = useState(false);
  const [showOnboarding, setShowOnboarding] = useState(true);
  const [isWizardOpen, setIsWizardOpen] = useState(false);
  const [isFilterOpen, setIsFilterOpen] = useState(false);
  const [isIntentOpen, setIsIntentOpen] = useState(false);
  const [isMoodModalOpen, setIsMoodModalOpen] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 50;


  const { theme, toggleTheme } = useTheme();
  const { user, login, register, logout } = useAuth();
  const { ratings, getRating, setMovieRating } = useUserRatings();

  const {
    watchlist,
    watched,
    isInWatchlist,
    isWatched,
    toggleWatchlist,
    toggleWatched,
  } = useMovieLists(user);

  // Filme puan verildiğinde otomatik "İzlediklerim" kategorisine alma
  const handleRateMovie = (movieId, score) => {
    setMovieRating(movieId, score);
    const targetMovie = movies.find((m) => String(m.id) === String(movieId)) ||
                        watchlist.find((m) => String(m.id) === String(movieId)) ||
                        watched.find((m) => String(m.id) === String(movieId)) ||
                        selectedMovie;

    if (targetMovie && !isWatched(movieId)) {
      toggleWatched(targetMovie);
    }
  };

  // Film verilerini çekme
  const fetchMovies = async (moodId = selectedMoodId, genreId = selectedGenreId) => {
    setLoading(true);
    try {
      let data;
      if (genreId > 0) {
        data = await getMoviesByGenre([genreId]);
      } else {
        const mood = MOODS.find((m) => m.id === moodId);
        if (!mood || mood.id === 'all' || !mood.genreIds || mood.genreIds.length === 0) {
          data = await getPopularMovies();
        } else {
          data = await getMoviesByGenre(mood.genreIds);
        }
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
    fetchMovies('all', 0);
  }, []);

  // Arama metni değiştiğinde
  const handleSearchChange = (query) => {
    setSearchQuery(query);
    setCurrentPage(1);
    if (query.trim().length > 0) {
      setSelectedMoodId('all');
      setSelectedGenreId(0);
      setLoading(true);
      searchMovies(query).then((results) => {
        setMovies(results || []);
        setLoading(false);
      });
    } else {
      fetchMovies(selectedMoodId, selectedGenreId);
    }
  };

  // Mood seçildiğinde
  const handleMoodSelect = (mood) => {
    setSelectedMoodId(mood.id);
    setSelectedGenreId(0); // Mood seçilince Tür filtresini sıfırla
    setSearchQuery('');
    setCurrentPage(1);
    fetchMovies(mood.id, 0);
  };

  // Tür seçildiğinde
  const handleGenreSelect = (genreId) => {
    setSelectedGenreId(genreId);
    setSelectedMoodId('all'); // Tür seçilince Mood filtresini sıfırla
    setSearchQuery('');
    setCurrentPage(1);
    fetchMovies('all', genreId);
  };

  // Logo tıklandığında tüm filtreleri sıfırla
  const handleLogoClick = () => {
    setActiveTab('explore');
    setSelectedMoodId('all');
    setSelectedGenreId(0);
    setSearchQuery('');
    setCurrentPage(1);
    fetchMovies('all', 0);
  };

  const getDisplayedMovies = () => {
    if (activeTab === 'watchlist') return watchlist;
    if (activeTab === 'watched') return watched;
    return movies;
  };

  const displayedMovies = getDisplayedMovies();
  const totalMovies = displayedMovies.length;
  const totalPages = Math.ceil(totalMovies / itemsPerPage) || 1;
  const indexOfLastMovie = currentPage * itemsPerPage;
  const indexOfFirstMovie = indexOfLastMovie - itemsPerPage;
  const current50Movies = displayedMovies.slice(indexOfFirstMovie, indexOfLastMovie);

  const handlePageChange = (newPage) => {
    if (newPage >= 1 && newPage <= totalPages) {
      setCurrentPage(newPage);
      window.scrollTo({ top: 350, behavior: 'smooth' });
    }
  };

  const currentMoodObj = MOODS.find((m) => m.id === selectedMoodId);

  return (
    <div className="min-h-screen flex flex-col justify-between bg-slate-50 dark:bg-[#0b0f19] text-slate-900 dark:text-slate-100 font-sans transition-colors duration-200">
      <div className="p-4 sm:p-6">
        {/* Header */}
        <Header
          activeTab={activeTab}
          onTabChange={(tab) => { setActiveTab(tab); setCurrentPage(1); }}
          watchlistCount={watchlist.length}
          watchedCount={watched.length}
          selectedMoodLabel={currentMoodObj ? currentMoodObj.label : 'Tümü'}
          searchQuery={searchQuery}
          onSearchChange={handleSearchChange}
          onLogoClick={handleLogoClick}
          onOpenProfile={() => setIsProfileOpen(true)}
          onOpenAuth={() => setIsAuthOpen(true)}
          user={user}
          onLogout={logout}
          theme={theme}
          onToggleTheme={toggleTheme}
        />

        {/* Ana İçerik */}
        <main className="max-w-6xl mx-auto">
          {/* Keşfet Sekmesi: Öneri Bloğu, Mood Selector & Genre Slider */}
          {activeTab === 'explore' && !searchQuery && (
            <>
              {/* Discovery Actions Toolbar */}
              <div className="mb-6 flex flex-wrap items-center gap-3">
                <button
                  type="button"
                  onClick={() => setIsWizardOpen(true)}
                  className="flex items-center gap-2 px-4 py-2.5 rounded-2xl bg-gradient-to-r from-rose-600 to-rose-700 hover:from-rose-500 hover:to-rose-600 text-white text-xs font-bold shadow-lg shadow-rose-950/40 cursor-pointer transition-all active:scale-95"
                >
                  <Wand2 className="w-4 h-4 text-amber-300 animate-pulse" />
                  <span>AI Film Sihirbazı</span>
                </button>

                <button
                  type="button"
                  onClick={() => setIsFilterOpen(!isFilterOpen)}
                  className={`flex items-center gap-2 px-4 py-2.5 rounded-2xl border text-xs font-bold transition-all cursor-pointer ${
                    isFilterOpen
                      ? 'bg-rose-600 text-white border-rose-500 shadow-md'
                      : 'bg-slate-900/80 border-slate-800 text-slate-300 hover:bg-slate-800 hover:text-white'
                  }`}
                >
                  <Filter className="w-4 h-4 text-rose-400" />
                  <span>Gelişmiş Filtreleme</span>
                </button>

                <button
                  type="button"
                  onClick={() => setIsIntentOpen(true)}
                  className="flex items-center gap-2 px-4 py-2.5 rounded-2xl bg-slate-900/80 border border-slate-800 hover:border-rose-500/40 text-slate-300 hover:text-white text-xs font-bold transition-all cursor-pointer"
                >
                  <Sparkles className="w-4 h-4 text-rose-400" />
                  <span>Niyetle Keşfet</span>
                </button>

                <button
                  type="button"
                  onClick={() => setIsMoodModalOpen(true)}
                  className="flex items-center gap-2 px-4 py-2.5 rounded-2xl bg-slate-900/80 border border-slate-800 hover:border-rose-500/40 text-slate-300 hover:text-white text-xs font-bold transition-all cursor-pointer"
                >
                  <Smile className="w-4 h-4 text-rose-400" />
                  <span>Ruh Hali Keşfi</span>
                </button>
              </div>

              {/* Filtre Paneli Açık Olduğunda */}
              {isFilterOpen && (
                <div className="mb-6 animate-fadeIn">
                  <FilterPanel
                    onFilterSubmit={async (filterData) => {
                      setLoading(true);
                      try {
                        const results = await filterMovies(filterData, user?.id);
                        setMovies(results || []);
                        setCurrentPage(1);
                      } catch (e) {
                        console.error('Filter error:', e);
                      } finally {
                        setLoading(false);
                      }
                    }}
                    onReset={() => fetchMovies('all', 0)}
                  />
                </div>
              )}

              {/* Sana Özel Öneriler Bloğu */}
              <RecommenderBlock
                watchlist={watchlist}
                watched={watched}
                ratings={ratings}
                isInWatchlist={isInWatchlist}
                isWatched={isWatched}
                onToggleWatchlist={toggleWatchlist}
                onToggleWatched={toggleWatched}
                onMovieClick={setSelectedMovie}
              />

              {/* Mood Selector & Genre Selector */}
              <div className="my-6 space-y-6">
                <MoodSelector
                  selectedMoodId={selectedMoodId}
                  onSelectMood={handleMoodSelect}
                />

                <GenreSelector
                  selectedGenreId={selectedGenreId}
                  onSelectGenre={handleGenreSelect}
                />
              </div>
            </>
          )}

          {/* Film Listesi Başlığı */}
          <div className="flex items-center justify-between mb-6 pb-2 border-b border-slate-200 dark:border-slate-800">
            <h2 className="text-xl font-bold text-slate-900 dark:text-slate-200 flex items-center gap-2">
              {activeTab === 'explore' ? (
                searchQuery ? (
                  <span>"{searchQuery}" Arama Sonuçları</span>
                ) : selectedGenreId > 0 ? (
                  'Türe Göre Filmler'
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
            <span className="text-xs text-slate-500 dark:text-slate-400 font-mono">
              {totalMovies} Film Bulundu (Sayfa {currentPage}/{totalPages})
            </span>
          </div>

          {/* Yükleniyor Durumu */}
          {loading && activeTab === 'explore' ? (
            <div className="flex flex-col items-center justify-center py-24 gap-3 text-slate-400">
              <Loader2 className="w-8 h-8 text-rose-500 animate-spin" />
              <p className="text-sm">Filmler yükleniyor...</p>
            </div>
          ) : activeTab === 'watchlist' && watchlist.length === 0 ? (
            <EmptyWatchlist onGoToExplore={() => { setActiveTab('explore'); setCurrentPage(1); }} />
          ) : activeTab === 'watched' && watched.length === 0 ? (
            <div className="flex flex-col items-center justify-center py-20 px-4 text-center border border-dashed border-slate-300 dark:border-slate-800 rounded-2xl bg-slate-100 dark:bg-slate-900/40">
              <div className="w-16 h-16 rounded-full bg-slate-200 dark:bg-slate-800/80 flex items-center justify-center text-emerald-500 mb-4 border border-slate-300 dark:border-slate-700/50">
                <CheckCircle2 className="w-8 h-8" />
              </div>
              <h3 className="text-lg font-bold text-slate-900 dark:text-slate-200 mb-2">
                Henüz izlediğiniz bir film işaretlemediniz
              </h3>
              <p className="text-sm text-slate-500 dark:text-slate-400 max-w-md mb-6">
                İzlediğiniz filmlerin üzerindeki onay ikonuna tıklayarak izlediklerim listenize ekleyebilirsiniz.
              </p>
              <button
                onClick={() => { setActiveTab('explore'); setCurrentPage(1); }}
                className="flex items-center gap-2 px-5 py-2.5 rounded-xl bg-rose-600 hover:bg-rose-500 text-white font-semibold text-sm transition-all shadow-lg shadow-rose-600/25 cursor-pointer"
              >
                <Compass className="w-4 h-4" />
                <span>Filmleri Keşfetmeye Başla</span>
              </button>
            </div>
          ) : displayedMovies.length === 0 ? (
            <div className="text-center py-20 text-slate-500 border border-dashed border-slate-300 dark:border-slate-800 rounded-xl">
              Gösterilecek film bulunamadı.
            </div>
          ) : (
            <>
              {/* 50'şerli Film Grid Listesi */}
              <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-6">
                {current50Movies.map((movie) => (
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

              {/* 50'şerli İleri / Geri Sayfalama Butonları */}
              {totalPages > 1 && (
                <div className="flex flex-col sm:flex-row items-center justify-between gap-4 mt-10 pt-6 border-t border-slate-200 dark:border-slate-800">
                  <div className="text-xs text-slate-500 font-mono">
                    Gösterilen: {indexOfFirstMovie + 1} - {Math.min(indexOfLastMovie, totalMovies)} / Toplam {totalMovies} Film
                  </div>
                  <div className="flex items-center gap-2">
                    <button
                      onClick={() => handlePageChange(currentPage - 1)}
                      disabled={currentPage === 1}
                      className="flex items-center gap-1 px-4 py-2 rounded-xl bg-slate-200 dark:bg-slate-800 hover:bg-rose-600 hover:text-white text-xs font-semibold disabled:opacity-40 disabled:hover:bg-slate-200 disabled:hover:text-current transition-all cursor-pointer"
                    >
                      <ChevronLeft className="w-4 h-4" />
                      <span>Önceki Sayfa</span>
                    </button>

                    <span className="px-3.5 py-1.5 text-xs font-bold font-mono rounded-lg bg-rose-500/10 text-rose-500 border border-rose-500/20">
                      Sayfa {currentPage} / {totalPages}
                    </span>

                    <button
                      onClick={() => handlePageChange(currentPage + 1)}
                      disabled={currentPage === totalPages}
                      className="flex items-center gap-1 px-4 py-2 rounded-xl bg-slate-200 dark:bg-slate-800 hover:bg-rose-600 hover:text-white text-xs font-semibold disabled:opacity-40 disabled:hover:bg-slate-200 disabled:hover:text-current transition-all cursor-pointer"
                    >
                      <span>Sonraki Sayfa</span>
                      <ChevronRight className="w-4 h-4" />
                    </button>
                  </div>
                </div>
              )}
            </>
          )}
        </main>
      </div>

      {/* Footer */}
      <Footer onNavigate={(tab) => setActiveTab(tab)} />

      {/* Modallar */}
      {selectedMovie && (
        <MovieModal
          movie={selectedMovie}
          onClose={() => setSelectedMovie(null)}
          isInWatchlist={isInWatchlist}
          isWatched={isWatched}
          onToggleWatchlist={toggleWatchlist}
          onToggleWatched={toggleWatched}
          userRating={getRating(selectedMovie.id)}
          onRateMovie={handleRateMovie}
          isAuthenticated={!!user}
          onRequireAuth={() => setIsAuthOpen(true)}
        />
      )}

      <ProfileModal
        isOpen={isProfileOpen}
        onClose={() => setIsProfileOpen(false)}
        watchlistCount={watchlist.length}
        watchedCount={watched.length}
        user={user}
        onLogout={logout}
        ratings={ratings}
      />

      <AuthModal
        isOpen={isAuthOpen}
        onClose={() => setIsAuthOpen(false)}
        onLogin={login}
        onRegister={register}
      />

      {user && user.hasCompletedOnboarding === false && showOnboarding && (
        <OnboardingModal onComplete={() => setShowOnboarding(false)} />
      )}

      {/* İnteraktif AI Film Sihirbazı Modalı */}
      <MovieWizardModal
        isOpen={isWizardOpen}
        onClose={() => setIsWizardOpen(false)}
        isInWatchlist={isInWatchlist}
        isWatched={isWatched}
        onToggleWatchlist={toggleWatchlist}
        onToggleWatched={toggleWatched}
        onMovieClick={setSelectedMovie}
      />

      {/* Niyetle Keşfet Modalı */}
      <IntentDiscoveryModal
        isOpen={isIntentOpen}
        onClose={() => setIsIntentOpen(false)}
        isInWatchlist={isInWatchlist}
        isWatched={isWatched}
        onToggleWatchlist={toggleWatchlist}
        onToggleWatched={toggleWatched}
        onMovieClick={setSelectedMovie}
      />

      {/* Ruh Hali Keşfi Modalı */}
      <MoodSelectorModal
        isOpen={isMoodModalOpen}
        onClose={() => setIsMoodModalOpen(false)}
        isInWatchlist={isInWatchlist}
        isWatched={isWatched}
        onToggleWatchlist={toggleWatchlist}
        onToggleWatched={toggleWatched}
        onMovieClick={setSelectedMovie}
      />
    </div>
  );
}

export default App;
