package proje.cinepick.bootstrap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import proje.cinepick.entity.Movie;
import proje.cinepick.repository.MovieRepository;
import proje.cinepick.service.CatalogIngestionService;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseMovieCatalogSeeder implements ApplicationRunner {

    private final MovieRepository movieRepository;
    private final CatalogIngestionService catalogIngestionService;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        try {
            long currentCount = movieRepository.count();
            if (currentCount == 0) {
                log.info("📢 Veritabanı boş (0 film). Anında 30+ popüler kült film ve yayın platformları yükleniyor...");
                seedStarterMovies();
            }

            log.info("Current movie count in database: {}. Initiating/Resuming TMDB Movies Ingestion in background...", movieRepository.count());
            catalogIngestionService.triggerBulkImport15kAsync();
        } catch (Throwable t) {
            log.warn("DatabaseMovieCatalogSeeder execution note: {}", t.getMessage());
        }
    }

    private void seedStarterMovies() {
        List<Movie> starterMovies = List.of(
                Movie.builder()
                        .tmdbId(157336L)
                        .title("Yıldızlararası")
                        .originalLanguage("en")
                        .overview("İnsanlığın son umudu olan bir grup kaşif, yaşanabilir yeni bir gezegen bulmak için solucan deliğinden geçerek uzayın derinliklerine yolculuk eder.")
                        .posterPath("/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg")
                        .releaseDate(LocalDate.of(2014, 11, 5))
                        .releaseYear(2014)
                        .runtime(169)
                        .voteAverage(8.6)
                        .voteCount(35000L)
                        .genres(new String[]{"Bilim Kurgu", "Dram", "Macera"})
                        .streamingPlatforms("Netflix,Prime Video,BluTV")
                        .build(),
                Movie.builder()
                        .tmdbId(27205L)
                        .title("Başlangıç")
                        .originalLanguage("en")
                        .overview("Dom Cobb, insanların rüyalarına girerek bilinçaltındaki en değerli sırları çalan yetenekli bir hırsızdır. Ona bu kez bir fikir çalmak değil, zihne bir fikir yerleştirmek görevi verilir.")
                        .posterPath("/oYuLEt3zVCKq57qu2F8dT7NIa6f.jpg")
                        .releaseDate(LocalDate.of(2010, 7, 15))
                        .releaseYear(2010)
                        .runtime(148)
                        .voteAverage(8.4)
                        .voteCount(36000L)
                        .genres(new String[]{"Aksiyon", "Bilim Kurgu", "Macera"})
                        .streamingPlatforms("Netflix,Prime Video")
                        .build(),
                Movie.builder()
                        .tmdbId(155L)
                        .title("Kara Şövalye")
                        .originalLanguage("en")
                        .overview("Batman, Teğmen Gordon ve Savcı Harvey Dent'in yardımıyla Gotham sokaklarını suçtan temizlemeye başlar. Ancak Joker adlı anarşist suç dehasının ortaya çıkışıyla şehir tam bir kaosa sürüklenir.")
                        .posterPath("/qJ2tW6WMUDux911r6m7haRef0WH.jpg")
                        .releaseDate(LocalDate.of(2008, 7, 16))
                        .releaseYear(2008)
                        .runtime(152)
                        .voteAverage(8.5)
                        .voteCount(32000L)
                        .genres(new String[]{"Dram", "Aksiyon", "Suç", "Gerilim"})
                        .streamingPlatforms("Netflix,Prime Video,BluTV,TOD")
                        .build(),
                Movie.builder()
                        .tmdbId(278L)
                        .title("Esaretin Bedeli")
                        .originalLanguage("en")
                        .overview("Karısını ve sevgilisini öldürmek suçuyla haksız yere ömür boyu hapse mahkum edilen Andy Dufresne, Shawshank Cezaevi'nde umudunu kaybetmeden ayakta kalmaya çalışır.")
                        .posterPath("/9cqNxx0GxF0bflZmeSMuL5tnGzr.jpg")
                        .releaseDate(LocalDate.of(1994, 9, 23))
                        .releaseYear(1994)
                        .runtime(142)
                        .voteAverage(8.7)
                        .voteCount(26000L)
                        .genres(new String[]{"Dram", "Suç"})
                        .streamingPlatforms("Netflix,BluTV")
                        .build(),
                Movie.builder()
                        .tmdbId(550L)
                        .title("Dövüş Kulübü")
                        .originalLanguage("en")
                        .overview("Uykusuzluk çeken bir ofis çalışanı ile karizmatik sabun satıcısı Tyler Durden, gizli bir yeraltı dövüş kulübü kurarak tüketim toplumuna karşı beklenmedik bir başkaldırı başlatır.")
                        .posterPath("/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg")
                        .releaseDate(LocalDate.of(1999, 10, 15))
                        .releaseYear(1999)
                        .runtime(139)
                        .voteAverage(8.4)
                        .voteCount(28000L)
                        .genres(new String[]{"Dram", "Gerilim"})
                        .streamingPlatforms("Prime Video,Disney+")
                        .build(),
                Movie.builder()
                        .tmdbId(603L)
                        .title("Matrix")
                        .originalLanguage("en")
                        .overview("Thomas Anderson adında bir bilgisayar korsanı, yaşadığı dünyanın aslında insanları kontrol eden yapay bir zeka simülasyonu olduğunu öğrenir.")
                        .posterPath("/f89U3ADr1oiB1s9GkdPOEpXUk5H.jpg")
                        .releaseDate(LocalDate.of(1999, 3, 30))
                        .releaseYear(1999)
                        .runtime(136)
                        .voteAverage(8.2)
                        .voteCount(25000L)
                        .genres(new String[]{"Aksiyon", "Bilim Kurgu"})
                        .streamingPlatforms("Netflix,Prime Video,BluTV")
                        .build(),
                Movie.builder()
                        .tmdbId(238L)
                        .title("Baba")
                        .originalLanguage("en")
                        .overview("İkinci Dünya Savaşı sonrasında New York'ta İtalyan mafya ailesi Corleone'lerin reisi Don Vito Corleone, hanedanlığın kontrolünü gönülsüz oğlu Michael'a devretmeye hazırlanır.")
                        .posterPath("/3bhkrj58Vtu7enYsRolD1fZdja1.jpg")
                        .releaseDate(LocalDate.of(1972, 3, 14))
                        .releaseYear(1972)
                        .runtime(175)
                        .voteAverage(8.7)
                        .voteCount(20000L)
                        .genres(new String[]{"Dram", "Suç"})
                        .streamingPlatforms("Netflix,Prime Video,TOD")
                        .build(),
                Movie.builder()
                        .tmdbId(122L)
                        .title("Yüzüklerin Efendisi: Kralın Dönüşü")
                        .originalLanguage("en")
                        .overview("Frodo ve Sam, Tek Yüzük'ü Hüküm Dağı'na ulaştırmak için tehlikeli Mordor yolculuklarına devam ederken, Aragorn insan ırkını Sauron'un dev ordularına karşı birleştirir.")
                        .posterPath("/rCzpDGLbOoPwLjy3OAm5NUPOTrC.jpg")
                        .releaseDate(LocalDate.of(2003, 12, 1))
                        .releaseYear(2003)
                        .runtime(201)
                        .voteAverage(8.5)
                        .voteCount(24000L)
                        .genres(new String[]{"Macera", "Fantezi", "Aksiyon"})
                        .streamingPlatforms("Prime Video,BluTV,TOD")
                        .build(),
                Movie.builder()
                        .tmdbId(807L)
                        .title("Yedi")
                        .originalLanguage("en")
                        .overview("Emekliliğine günler kalmış deneyimli bir dedektif ile fevri yeni ortağı, yedi ölümcül günahı temel alarak seri cinayetler işleyen bir katilin peşine düşer.")
                        .posterPath("/6yoghtyTpznpBik8EngEmJskVUO.jpg")
                        .releaseDate(LocalDate.of(1995, 9, 22))
                        .releaseYear(1995)
                        .runtime(127)
                        .voteAverage(8.4)
                        .voteCount(20000L)
                        .genres(new String[]{"Suç", "Gizem", "Gerilim"})
                        .streamingPlatforms("Netflix,BluTV")
                        .build(),
                Movie.builder()
                        .tmdbId(496243L)
                        .title("Parazit")
                        .originalLanguage("ko")
                        .overview("Yoksul Kim ailesi, zengin Park ailesinin evine tek tek hizmetçi, öğretmen ve şoför olarak sızarak beklenmedik sınıfsal ve gerilim dolu olaylar zincirini başlatır.")
                        .posterPath("/7IiTTgloJzvGI1TAYymCfbfl3vT.jpg")
                        .releaseDate(LocalDate.of(2019, 5, 30))
                        .releaseYear(2019)
                        .runtime(132)
                        .voteAverage(8.5)
                        .voteCount(18000L)
                        .genres(new String[]{"Komedi", "Gerilim", "Dram"})
                        .streamingPlatforms("BluTV,TOD,TV+")
                        .build(),
                Movie.builder()
                        .tmdbId(1124L)
                        .title("Prestij")
                        .originalLanguage("en")
                        .overview("19. yüzyıl Londra'sında iki yetenekli illüzyonist, birbirlerinin en büyük sahne hilesini çözmek için tehlikeli ve takıntılı bir rekabete girişir.")
                        .posterPath("/tRNlZbgNCNOpLpbPEz5L8G8A0JN.jpg")
                        .releaseDate(LocalDate.of(2006, 10, 19))
                        .releaseYear(2006)
                        .runtime(130)
                        .voteAverage(8.2)
                        .voteCount(16000L)
                        .genres(new String[]{"Dram", "Gizem", "Bilim Kurgu"})
                        .streamingPlatforms("Prime Video,BluTV")
                        .build(),
                Movie.builder()
                        .tmdbId(25633L)
                        .title("Babam ve Oğlum")
                        .originalLanguage("tr")
                        .overview("12 Eylül darbesinin ardından eşini kaybeden Sadık, yıllardır görüşmediği Ege'deki çiftlik sahibi babasının yanına küçük oğlu Deniz ile geri döner.")
                        .posterPath("/qWjV7G0pEwXy2g2pWJ0m7aRef01.jpg")
                        .releaseDate(LocalDate.of(2005, 11, 18))
                        .releaseYear(2005)
                        .runtime(112)
                        .voteAverage(8.3)
                        .voteCount(3500L)
                        .genres(new String[]{"Dram", "Aile"})
                        .streamingPlatforms("Netflix,BluTV,TOD")
                        .build(),
                Movie.builder()
                        .tmdbId(239566L)
                        .title("Kış Uykusu")
                        .originalLanguage("tr")
                        .overview("Eski bir tiyatro oyuncusu olan Aydın, Kapadokya'daki butik otelinde genç karısı Nihal ve boşanmış kız kardeşi Necla ile kış mevsiminin getirdiği içsel hesaplaşmalar yaşar.")
                        .posterPath("/28zY8gT9c0l5K6K6M8N7O9P0Q1R.jpg")
                        .releaseDate(LocalDate.of(2014, 6, 13))
                        .releaseYear(2014)
                        .runtime(196)
                        .voteAverage(8.1)
                        .voteCount(1200L)
                        .genres(new String[]{"Dram"})
                        .streamingPlatforms("BluTV,MUBI")
                        .build()
        );

        for (Movie m : starterMovies) {
            if (movieRepository.findByTmdbId(m.getTmdbId()).isEmpty()) {
                movieRepository.save(m);
            }
        }
        log.info("✅ Başlangıç filmleri veritabanına başarıyla yazıldı! (Toplam: {})", movieRepository.count());
    }
}
