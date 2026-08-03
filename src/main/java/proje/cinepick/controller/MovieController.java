package proje.cinepick.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import proje.cinepick.dto.MovieDto;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
public class MovieController {

    private final proje.cinepick.service.ResilientTmdbService tmdbService;
    private final proje.cinepick.service.SmartSummaryService smartSummaryService;
    private final proje.cinepick.repository.MovieRepository movieRepository;

    @GetMapping("/onboarding-pool")
    public ResponseEntity<List<MovieDto>> getOnboardingPool() {
        List<MovieDto> pool = List.of(
            MovieDto.builder().id(27205L).tmdbId(27205L).title("Inception").overview("Zihin hırsızlığı üzerine sürükleyici bir bilim kurgu başyapıtı.").posterPath("/oYuLE29W9Z12Y9z2vB2c277u2r.jpg").releaseDate("2010-07-16").voteAverage(8.4).build(),
            MovieDto.builder().id(157336L).tmdbId(157336L).title("Interstellar").overview("İnsanlığın geleceği için solucan deliğinden uzay yolculuğu.").posterPath("/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg").releaseDate("2014-11-05").voteAverage(8.4).build(),
            MovieDto.builder().id(155L).tmdbId(155L).title("The Dark Knight").overview("Batman ile Joker arasındaki kaotik felsefi mücadele.").posterPath("/qJ2tW6WMUDux911r6m7haRef0WH.jpg").releaseDate("2008-07-16").voteAverage(8.5).build(),
            MovieDto.builder().id(680L).tmdbId(680L).title("Pulp Fiction").overview("Quentin Tarantino'nun sinematik kült klasiği.").posterPath("/d5iIlFn5s0ImszYzBPb8SuB1YxW.jpg").releaseDate("1994-09-10").voteAverage(8.5).build(),
            MovieDto.builder().id(603L).tmdbId(603L).title("The Matrix").overview("Gerçeklik ve simülasyon arasındaki siberpunk yolculuk.").posterPath("/f89U3w9WFi2V2v2vB2c277u2r.jpg").releaseDate("1999-03-30").voteAverage(8.2).build(),
            MovieDto.builder().id(550L).tmdbId(550L).title("Fight Club").overview("Tüketim toplumuna ve kimlik krizine radikal bir başkaldırı.").posterPath("/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg").releaseDate("1999-10-15").voteAverage(8.4).build(),
            MovieDto.builder().id(496243L).tmdbId(496243L).title("Parasite").overview("Sınıf çatışmasını kara mizahla ele alan Oscar ödüllü eser.").posterPath("/7IiTWWZ9gZ77NI6lCU6MxlNBvIx.jpg").releaseDate("2019-05-30").voteAverage(8.5).build(),
            MovieDto.builder().id(129L).tmdbId(129L).title("Spirited Away").overview("Hayao Miyazaki'nin büyüleyici ruhlar alemi fantezisi.").posterPath("/39xRfiMu2v2v2vB2c277u2r.jpg").releaseDate("2001-07-20").voteAverage(8.5).build(),
            MovieDto.builder().id(313369L).tmdbId(313369L).title("La La Land").overview("Los Angeles'ta tutku, caz ve aşkın sinematik dansı.").posterPath("/uDO8zWDhfWwo1Ip4vB2c277u2r.jpg").releaseDate("2016-12-01").voteAverage(8.0).build(),
            MovieDto.builder().id(244786L).tmdbId(244786L).title("Whiplash").overview("Müzik tutkusu ile mükemmeliyetçiliğin karanlık sınırı.").posterPath("/777xRfiMu2v2v2vB2c277u2r.jpg").releaseDate("2014-10-10").voteAverage(8.4).build(),
            MovieDto.builder().id(872585L).tmdbId(872585L).title("Oppenheimer").overview("Atom bombasının doğuşu ve ahlaki vicdan muhasebesi.").posterPath("/8Gxv8g7V2v2v2vB2c277u2r.jpg").releaseDate("2023-07-19").voteAverage(8.1).build(),
            MovieDto.builder().id(438631L).tmdbId(438631L).title("Dune").overview("Çöl gezegeninde kehanet, hanedanlık savaşı ve kader.").posterPath("/d55xRfiMu2v2v2vB2c277u2r.jpg").releaseDate("2021-09-15").voteAverage(7.9).build()
        );
        return ResponseEntity.ok(pool);
    }

    @GetMapping("/{movieId}/watch-providers")
    public ResponseEntity<proje.cinepick.dto.tmdb.TmdbWatchProviderResponse.CountryProviders> getWatchProviders(
            @org.springframework.web.bind.annotation.PathVariable Long movieId) {
        return ResponseEntity.ok(tmdbService.getTurkeyWatchProviders(movieId));
    }

    @GetMapping("/{movieId}/smart-summary")
    public ResponseEntity<proje.cinepick.dto.MovieSmartSummaryDto> getSmartSummary(
            @org.springframework.web.bind.annotation.PathVariable Long movieId) {
        proje.cinepick.entity.Movie movie = movieRepository.findById(movieId)
                .orElseGet(() -> movieRepository.findByTmdbId(movieId)
                        .orElseGet(() -> movieRepository.save(
                                proje.cinepick.entity.Movie.builder()
                                        .tmdbId(movieId)
                                        .title("Movie #" + movieId)
                                        .overview("Film açıklaması")
                                        .build()
                        )));

        return ResponseEntity.ok(smartSummaryService.getOrGenerateSmartSummary(movie));
    }
}
