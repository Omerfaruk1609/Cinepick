package proje.cinepick.benchmark;

import proje.cinepick.entity.Movie;
import proje.cinepick.entity.Role;
import proje.cinepick.entity.User;
import proje.cinepick.entity.UserMovieInteraction;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates synthetic test data for benchmark tests.
 *
 * Data model:
 *  - 3 user profiles: MarvelUser, DramaUser, DiverseUser
 *  - 30 seed movies across 6 genres (action, drama, sci-fi, thriller, crime, anime)
 *  - Each user has 15 interactions; last 5 are the "held-out" ground truth
 */
public class BenchmarkDataFixtures {

    // ─── Genre Sets ───────────────────────────────────────────────────────────

    public static final String[] ACTION_GENRES  = {"Action", "Adventure"};
    public static final String[] DRAMA_GENRES   = {"Drama"};
    public static final String[] SCIFI_GENRES   = {"Science Fiction"};
    public static final String[] THRILLER_GENRES = {"Thriller", "Crime"};
    public static final String[] ANIME_GENRES   = {"Animation", "Fantasy"};

    // ─── Seed Movies ─────────────────────────────────────────────────────────

    /**
     * Builds 30 deterministic test movies with pre-set embeddings.
     * Embeddings are tiny 4-dim vectors for test speed — prod uses 1536-dim.
     */
    public static List<Movie> buildSeedMovies() {
        List<Movie> movies = new ArrayList<>();

        // Action / Marvel-like block  (tmdbId 1001–1010)
        movies.add(buildMovie(1001L, "Iron Man",          ACTION_GENRES,  8.0, 500_000L, new float[]{0.9f, 0.1f, 0.1f, 0.1f}));
        movies.add(buildMovie(1002L, "Thor",              ACTION_GENRES,  7.5, 400_000L, new float[]{0.9f, 0.2f, 0.1f, 0.0f}));
        movies.add(buildMovie(1003L, "Captain America",  ACTION_GENRES,  7.8, 450_000L, new float[]{0.8f, 0.2f, 0.1f, 0.1f}));
        movies.add(buildMovie(1004L, "Avengers",         ACTION_GENRES,  8.1, 700_000L, new float[]{0.9f, 0.1f, 0.0f, 0.1f}));
        movies.add(buildMovie(1005L, "Black Panther",    ACTION_GENRES,  7.9, 550_000L, new float[]{0.8f, 0.2f, 0.1f, 0.0f}));
        movies.add(buildMovie(1006L, "Guardians",        ACTION_GENRES,  8.0, 480_000L, new float[]{0.8f, 0.2f, 0.1f, 0.1f}));
        movies.add(buildMovie(1007L, "Doctor Strange",   ACTION_GENRES,  7.5, 390_000L, new float[]{0.8f, 0.1f, 0.2f, 0.1f}));
        movies.add(buildMovie(1008L, "Ant-Man",          ACTION_GENRES,  7.3, 330_000L, new float[]{0.7f, 0.2f, 0.1f, 0.1f}));
        movies.add(buildMovie(1009L, "Winter Soldier",   ACTION_GENRES,  7.9, 440_000L, new float[]{0.9f, 0.1f, 0.0f, 0.1f}));
        movies.add(buildMovie(1010L, "Spider-Man",       ACTION_GENRES,  8.2, 620_000L, new float[]{0.8f, 0.2f, 0.1f, 0.1f}));

        // Drama block (tmdbId 2001–2010)
        movies.add(buildMovie(2001L, "The Shawshank Redemption", DRAMA_GENRES, 9.3, 600_000L, new float[]{0.1f, 0.9f, 0.1f, 0.1f}));
        movies.add(buildMovie(2002L, "Schindler's List",          DRAMA_GENRES, 8.9, 400_000L, new float[]{0.1f, 0.9f, 0.0f, 0.1f}));
        movies.add(buildMovie(2003L, "The Green Mile",            DRAMA_GENRES, 8.6, 350_000L, new float[]{0.1f, 0.8f, 0.1f, 0.1f}));
        movies.add(buildMovie(2004L, "Forrest Gump",              DRAMA_GENRES, 8.8, 550_000L, new float[]{0.1f, 0.9f, 0.0f, 0.0f}));
        movies.add(buildMovie(2005L, "Good Will Hunting",         DRAMA_GENRES, 8.3, 280_000L, new float[]{0.0f, 0.9f, 0.1f, 0.1f}));
        movies.add(buildMovie(2006L, "Whiplash",                  DRAMA_GENRES, 8.5, 300_000L, new float[]{0.1f, 0.8f, 0.0f, 0.1f}));
        movies.add(buildMovie(2007L, "Parasite",                  DRAMA_GENRES, 8.5, 320_000L, new float[]{0.1f, 0.8f, 0.1f, 0.1f}));
        movies.add(buildMovie(2008L, "La La Land",                DRAMA_GENRES, 8.0, 290_000L, new float[]{0.0f, 0.8f, 0.0f, 0.1f}));
        movies.add(buildMovie(2009L, "Moonlight",                 DRAMA_GENRES, 7.4, 150_000L, new float[]{0.0f, 0.9f, 0.0f, 0.0f}));
        movies.add(buildMovie(2010L, "Marriage Story",            DRAMA_GENRES, 7.9, 170_000L, new float[]{0.0f, 0.8f, 0.1f, 0.0f}));

        // Sci-Fi / Thriller (tmdbId 3001–3005)
        movies.add(buildMovie(3001L, "Inception",          SCIFI_GENRES,    8.8, 700_000L, new float[]{0.2f, 0.2f, 0.9f, 0.1f}));
        movies.add(buildMovie(3002L, "Interstellar",       SCIFI_GENRES,    8.6, 650_000L, new float[]{0.2f, 0.1f, 0.9f, 0.0f}));
        movies.add(buildMovie(3003L, "The Matrix",         SCIFI_GENRES,    8.7, 620_000L, new float[]{0.2f, 0.1f, 0.9f, 0.1f}));
        movies.add(buildMovie(3004L, "Primer",             SCIFI_GENRES,    6.9,  20_000L, new float[]{0.1f, 0.2f, 0.8f, 0.1f}));
        movies.add(buildMovie(3005L, "Coherence",          SCIFI_GENRES,    7.2,  30_000L, new float[]{0.1f, 0.2f, 0.8f, 0.2f}));

        // Thriller / Crime (tmdbId 4001–4003)
        movies.add(buildMovie(4001L, "Se7en",              THRILLER_GENRES, 8.6, 480_000L, new float[]{0.1f, 0.1f, 0.2f, 0.9f}));
        movies.add(buildMovie(4002L, "Zodiac",             THRILLER_GENRES, 7.7, 200_000L, new float[]{0.1f, 0.2f, 0.2f, 0.8f}));
        movies.add(buildMovie(4003L, "Gone Girl",          THRILLER_GENRES, 8.1, 350_000L, new float[]{0.1f, 0.2f, 0.1f, 0.9f}));

        // Anime (tmdbId 5001–5002)
        movies.add(buildMovie(5001L, "Spirited Away",      ANIME_GENRES,    8.5, 280_000L, new float[]{0.0f, 0.1f, 0.1f, 0.2f}));
        movies.add(buildMovie(5002L, "Akira",              ANIME_GENRES,    8.0,  90_000L, new float[]{0.1f, 0.0f, 0.2f, 0.1f}));

        return movies;
    }

    // ─── User Profiles ────────────────────────────────────────────────────────

    /** Marvel-heavy user: 10 action movies interacted */
    public static User buildMarvelUser() {
        return User.builder()
                .email("marvel_user@cinepick.test")
                .username("marvel_user")
                .password("$2a$10$benchmark_hash")
                .role(Role.USER)
                .hasCompletedOnboarding(true)
                .build();
    }

    /** Drama-heavy user: 10 drama movies interacted */
    public static User buildDramaUser() {
        return User.builder()
                .email("drama_user@cinepick.test")
                .username("drama_user")
                .password("$2a$10$benchmark_hash")
                .role(Role.USER)
                .hasCompletedOnboarding(true)
                .build();
    }

    /** Diverse user: mixed genres */
    public static User buildDiverseUser() {
        return User.builder()
                .email("diverse_user@cinepick.test")
                .username("diverse_user")
                .password("$2a$10$benchmark_hash")
                .role(Role.USER)
                .hasCompletedOnboarding(true)
                .build();
    }

    // ─── Interaction Builder ──────────────────────────────────────────────────

    public static UserMovieInteraction buildInteraction(User user, Movie movie, boolean isFavorite, double rating) {
        UserMovieInteraction umi = new UserMovieInteraction();
        umi.setUser(user);
        umi.setMovie(movie);
        umi.setFavorite(isFavorite);
        umi.setRating(rating);
        umi.setInWatchlist(false);
        return umi;
    }

    // ─── Private Factory ─────────────────────────────────────────────────────

    private static Movie buildMovie(Long tmdbId, String title, String[] genres,
                                    double voteAvg, long voteCount, float[] embedding) {
        Movie m = new Movie();
        m.setTmdbId(tmdbId);
        m.setTitle(title);
        m.setOverview("Overview of " + title);
        m.setGenres(genres);
        m.setVoteAverage(voteAvg);
        m.setVoteCount(voteCount);
        
        float[] fullDimEmbedding = new float[1536];
        if (embedding != null) {
            System.arraycopy(embedding, 0, fullDimEmbedding, 0, Math.min(embedding.length, 1536));
        }
        m.setEmbedding(fullDimEmbedding);
        
        m.setReleaseDate(java.time.LocalDate.parse("2020-01-01"));
        m.setPosterPath("/placeholder.jpg");
        return m;
    }

}
