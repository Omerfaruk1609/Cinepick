package proje.cinepick.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import proje.cinepick.entity.Movie;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;


import lombok.extern.slf4j.Slf4j;

/**
 * Native-SQL search queries ayrı bir repository'de tutulur —
 * JpaRepository'e yığmamak için.
 */
@Slf4j
@Repository
public class MovieSearchRepository {


    private final JdbcTemplate jdbcTemplate;

    public MovieSearchRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ─── Keyword Search (ILIKE title + overview) ─────────────────────────────

    public List<Movie> findByKeyword(String query, int limit, int offset) {
        String pattern = "%" + query.toLowerCase() + "%";
        String sql = """
                SELECT id, tmdb_id, title, overview, poster_path,
                       CAST(release_date AS TEXT) AS release_date,
                       vote_average, genres, director, vote_count
                FROM movies
                WHERE LOWER(title) LIKE ?
                   OR (overview IS NOT NULL AND LOWER(overview) LIKE ?)
                ORDER BY
                    CASE WHEN LOWER(title) = LOWER(?) THEN 0
                         WHEN LOWER(title) LIKE ? THEN 1
                         ELSE 2 END,
                    vote_average DESC NULLS LAST
                LIMIT ? OFFSET ?
                """;
        try {
            List<Movie> list = jdbcTemplate.query(sql,
                    ps -> {
                        ps.setString(1, pattern);
                        ps.setString(2, pattern);
                        ps.setString(3, query.toLowerCase());
                        ps.setString(4, query.toLowerCase() + "%");
                        ps.setInt(5, limit);
                        ps.setInt(6, offset);
                    },
                    this::mapRow);
            log.info("findByKeyword('{}') returned {} rows", query, list.size());
            return list;
        } catch (Exception e) {
            log.error("findByKeyword failed for query: {}", query, e);
            return List.of();
        }
    }


    // ─── Semantic / Vector Search (pgvector cosine) ───────────────────────────

    /**
     * Embedding string'i pgvector formatında alır: "[0.12, -0.34, ...]"
     * @return tmdbId listesi (sıralı: en yakın önce)
     */
    public List<Long> findTopKBySemanticSimilarity(String queryVector, int topK) {
        String sql = """
                SELECT m.tmdb_id
                FROM movies m
                WHERE m.embedding IS NOT NULL
                ORDER BY m.embedding <=> CAST(? AS vector)
                LIMIT ?
                """;
        return jdbcTemplate.queryForList(sql, Long.class, queryVector, topK);
    }

    // ─── Row Mapper ───────────────────────────────────────────────────────────

    private Movie mapRow(ResultSet rs, int rowNum) throws SQLException {
        Movie movie = new Movie();
        movie.setId(rs.getLong("id"));
        movie.setTmdbId(rs.getLong("tmdb_id"));
        movie.setTitle(rs.getString("title"));
        movie.setOverview(rs.getString("overview"));
        movie.setPosterPath(rs.getString("poster_path"));
        java.sql.Date sqlDate = rs.getDate("release_date");
        if (sqlDate != null) {
            movie.setReleaseDate(sqlDate.toLocalDate());
        }
        movie.setVoteAverage(rs.getDouble("vote_average"));
        movie.setDirector(rs.getString("director"));
        movie.setVoteCount(rs.getLong("vote_count"));

        java.sql.Array sqlGenres = rs.getArray("genres");
        String[] genres = null;
        if (sqlGenres != null) {
            Object obj = sqlGenres.getArray();
            if (obj instanceof String[] strArr) {
                genres = strArr;
            } else if (obj instanceof Object[] objArr) {
                genres = Arrays.stream(objArr).map(Object::toString).toArray(String[]::new);
            }
        }
        movie.setGenres(genres);



        return movie;
    }
}
