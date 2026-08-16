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
            seedComprehensiveCatalog();
            log.info("Current movie count in database: {}. Resuming TMDB background sync...", movieRepository.count());
            catalogIngestionService.triggerBulkImport15kAsync();
        } catch (Throwable t) {
            log.warn("DatabaseMovieCatalogSeeder note: {}", t.getMessage());
        }
    }

    private void seedComprehensiveCatalog() {
        List<Movie> movies = List.of(
                // ── BİLİM KURGU & UZAY ──
                Movie.builder().tmdbId(157336L).title("Yıldızlararası").originalLanguage("en")
                        .overview("İnsanlığın son umudu olan bir grup kaşif, yaşanabilir yeni bir gezegen bulmak için solucan deliğinden geçerek uzayın derinliklerine yolculuk eder.")
                        .posterPath("/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg").releaseDate(LocalDate.of(2014, 11, 5)).releaseYear(2014)
                        .runtime(169).voteAverage(8.6).voteCount(35000L).genres(new String[]{"Bilim Kurgu", "Dram", "Macera"})
                        .streamingPlatforms("Netflix,Prime Video,BluTV").build(),

                Movie.builder().tmdbId(27205L).title("Başlangıç").originalLanguage("en")
                        .overview("Dom Cobb, insanların rüyalarına girerek bilinçaltındaki en değerli sırları çalan yetenekli bir hırsızdır. Ona bu kez bir fikir çalmak değil, zihne bir fikir yerleştirmek görevi verilir.")
                        .posterPath("/oYuLEt3zVCKq57qu2F8dT7NIa6f.jpg").releaseDate(LocalDate.of(2010, 7, 15)).releaseYear(2010)
                        .runtime(148).voteAverage(8.4).voteCount(36000L).genres(new String[]{"Aksiyon", "Bilim Kurgu", "Macera"})
                        .streamingPlatforms("Netflix,Prime Video").build(),

                Movie.builder().tmdbId(603L).title("Matrix").originalLanguage("en")
                        .overview("Thomas Anderson adında bir bilgisayar korsanı, yaşadığı dünyanın aslında insanları kontrol eden yapay bir zeka simülasyonu olduğunu öğrenir.")
                        .posterPath("/f89U3ADr1oiB1s9GkdPOEpXUk5H.jpg").releaseDate(LocalDate.of(1999, 3, 30)).releaseYear(1999)
                        .runtime(136).voteAverage(8.2).voteCount(25000L).genres(new String[]{"Aksiyon", "Bilim Kurgu"})
                        .streamingPlatforms("Netflix,Prime Video,BluTV").build(),

                Movie.builder().tmdbId(438631L).title("Dune: Çöl Gezegeni").originalLanguage("en")
                        .overview("Paul Atreides, evrenin en değerli maddesine ev sahipliği yapan tehlikeli çöl gezegeni Arrakis'te ailesinin ve halkının geleceğini korumak için savaşır.")
                        .posterPath("/d5NXSklXo0qyIYkgV94XAgMIckC.jpg").releaseDate(LocalDate.of(2021, 9, 15)).releaseYear(2021)
                        .runtime(155).voteAverage(7.8).voteCount(12000L).genres(new String[]{"Bilim Kurgu", "Macera", "Dram"})
                        .streamingPlatforms("BluTV,Prime Video,TOD").build(),

                Movie.builder().tmdbId(693134L).title("Dune: Çöl Gezegeni Bölüm İki").originalLanguage("en")
                        .overview("Paul Atreides, Chani ve Fremenlerle birleşerek ailesini yok eden komploculara karşı intikam savaşı başlatırken evrenin kaderini belirler.")
                        .posterPath("/1pdfLvkbY9ohJlCjQH2CZjjYVvJ.jpg").releaseDate(LocalDate.of(2024, 2, 27)).releaseYear(2024)
                        .runtime(166).voteAverage(8.2).voteCount(6500L).genres(new String[]{"Bilim Kurgu", "Macera"})
                        .streamingPlatforms("TOD,BluTV").build(),

                Movie.builder().tmdbId(335984L).title("Blade Runner 2049: Bıçak Sırtı").originalLanguage("en")
                        .overview("Los Angeles Polis Departmanı memuru K, toplumdan geriye kalanı kaosa sürükleme potansiyeline sahip, uzun süredir gömülü bir sırrı açığa çıkarır.")
                        .posterPath("/gajva2L0rPYkEWjzgFlBXCAVBE5.jpg").releaseDate(LocalDate.of(2017, 10, 4)).releaseYear(2017)
                        .runtime(164).voteAverage(8.0).voteCount(14000L).genres(new String[]{"Bilim Kurgu", "Dram", "Gizem"})
                        .streamingPlatforms("Netflix,Prime Video").build(),

                Movie.builder().tmdbId(872585L).title("Oppenheimer").originalLanguage("en")
                        .overview("J. Robert Oppenheimer'ın Manhattan Projesi sırasında ilk nükleer silahı geliştirme sürecini ve sonrasında yaşadığı ahlaki hesaplaşmaları anlatan biyografik başyapıt.")
                        .posterPath("/8Gxv8gSFCU0XGDykEGv7zR1n2ua.jpg").releaseDate(LocalDate.of(2023, 7, 19)).releaseYear(2023)
                        .runtime(180).voteAverage(8.1).voteCount(9500L).genres(new String[]{"Dram", "Tarih", "Biyografi"})
                        .streamingPlatforms("Prime Video,BluTV,TOD").build(),

                // ── SUÇ, GERİLİM & DRAM ──
                Movie.builder().tmdbId(155L).title("Kara Şövalye").originalLanguage("en")
                        .overview("Batman, Teğmen Gordon ve Savcı Harvey Dent'in yardımıyla Gotham sokaklarını suçtan temizlemeye başlar. Ancak Joker adlı anarşist suç dehasının ortaya çıkışıyla şehir tam bir kaosa sürüklenir.")
                        .posterPath("/qJ2tW6WMUDux911r6m7haRef0WH.jpg").releaseDate(LocalDate.of(2008, 7, 16)).releaseYear(2008)
                        .runtime(152).voteAverage(8.5).voteCount(32000L).genres(new String[]{"Dram", "Aksiyon", "Suç", "Gerilim"})
                        .streamingPlatforms("Netflix,Prime Video,BluTV,TOD").build(),

                Movie.builder().tmdbId(278L).title("Esaretin Bedeli").originalLanguage("en")
                        .overview("Karısını ve sevgilisini öldürmek suçuyla haksız yere ömür boyu hapse mahkum edilen Andy Dufresne, Shawshank Cezaevi'nde umudunu kaybetmeden ayakta kalmaya çalışır.")
                        .posterPath("/9cqNxx0GxF0bflZmeSMuL5tnGzr.jpg").releaseDate(LocalDate.of(1994, 9, 23)).releaseYear(1994)
                        .runtime(142).voteAverage(8.7).voteCount(26000L).genres(new String[]{"Dram", "Suç"})
                        .streamingPlatforms("Netflix,BluTV").build(),

                Movie.builder().tmdbId(550L).title("Dövüş Kulübü").originalLanguage("en")
                        .overview("Uykusuzluk çeken bir ofis çalışanı ile karizmatik sabun satıcısı Tyler Durden, gizli bir yeraltı dövüş kulübü kurarak tüketim toplumuna karşı beklenmedik bir başkaldırı başlatır.")
                        .posterPath("/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg").releaseDate(LocalDate.of(1999, 10, 15)).releaseYear(1999)
                        .runtime(139).voteAverage(8.4).voteCount(28000L).genres(new String[]{"Dram", "Gerilim"})
                        .streamingPlatforms("Prime Video,Disney+").build(),

                Movie.builder().tmdbId(238L).title("Baba").originalLanguage("en")
                        .overview("İkinci Dünya Savaşı sonrasında New York'ta İtalyan mafya ailesi Corleone'lerin reisi Don Vito Corleone, hanedanlığın kontrolünü gönülsüz oğlu Michael'a devretmeye hazırlanır.")
                        .posterPath("/3bhkrj58Vtu7enYsRolD1fZdja1.jpg").releaseDate(LocalDate.of(1972, 3, 14)).releaseYear(1972)
                        .runtime(175).voteAverage(8.7).voteCount(20000L).genres(new String[]{"Dram", "Suç"})
                        .streamingPlatforms("Netflix,Prime Video,TOD").build(),

                Movie.builder().tmdbId(240L).title("Baba II").originalLanguage("en")
                        .overview("Michael Corleone ailesinin gücünü Nevada ve Küba'ya genişletirken, babası Vito Corleone'nin 1920'ler New York'unda yükseliş hikayesi paralel olarak anlatılır.")
                        .posterPath("/hek3koDUyRQk7FIhPXsa6mT2Zc3.jpg").releaseDate(LocalDate.of(1974, 12, 20)).releaseYear(1974)
                        .runtime(202).voteAverage(8.6).voteCount(13000L).genres(new String[]{"Dram", "Suç"})
                        .streamingPlatforms("Netflix,Prime Video,TOD").build(),

                Movie.builder().tmdbId(680L).title("Ucuz Roman").originalLanguage("en")
                        .overview("İki mafya tetikçisi, bir boksör, bir gangster ve karısının hayatları, şiddet ve kurtuluş dolu sıra dışı olaylarla birbirine bağlanır.")
                        .posterPath("/d5iIlFn5s0ImszYzBPb8JPIfbXD.jpg").releaseDate(LocalDate.of(1994, 9, 10)).releaseYear(1994)
                        .runtime(154).voteAverage(8.5).voteCount(27000L).genres(new String[]{"Gerilim", "Suç"})
                        .streamingPlatforms("Netflix,Prime Video,BluTV").build(),

                Movie.builder().tmdbId(807L).title("Yedi").originalLanguage("en")
                        .overview("Emekliliğine günler kalmış deneyimli bir dedektif ile fevri yeni ortağı, yedi ölümcül günahı temel alarak seri cinayetler işleyen bir katilin peşine düşer.")
                        .posterPath("/6yoghtyTpznpBik8EngEmJskVUO.jpg").releaseDate(LocalDate.of(1995, 9, 22)).releaseYear(1995)
                        .runtime(127).voteAverage(8.4).voteCount(20000L).genres(new String[]{"Suç", "Gizem", "Gerilim"})
                        .streamingPlatforms("Netflix,BluTV").build(),

                Movie.builder().tmdbId(496243L).title("Parazit").originalLanguage("ko")
                        .overview("Yoksul Kim ailesi, zengin Park ailesinin evine tek tek hizmetçi, öğretmen ve şoför olarak sızarak beklenmedik sınıfsal ve gerilim dolu olaylar zincirini başlatır.")
                        .posterPath("/7IiTTgloJzvGI1TAYymCfbfl3vT.jpg").releaseDate(LocalDate.of(2019, 5, 30)).releaseYear(2019)
                        .runtime(132).voteAverage(8.5).voteCount(18000L).genres(new String[]{"Komedi", "Gerilim", "Dram"})
                        .streamingPlatforms("BluTV,TOD,TV+").build(),

                Movie.builder().tmdbId(1124L).title("Prestij").originalLanguage("en")
                        .overview("19. yüzyıl Londra'sında iki yetenekli illüzyonist, birbirlerinin en büyük sahne hilesini çözmek için tehlikeli ve takıntılı bir rekabete girişir.")
                        .posterPath("/tRNlZbgNCNOpLpbPEz5L8G8A0JN.jpg").releaseDate(LocalDate.of(2006, 10, 19)).releaseYear(2006)
                        .runtime(130).voteAverage(8.2).voteCount(16000L).genres(new String[]{"Dram", "Gizem", "Bilim Kurgu"})
                        .streamingPlatforms("Prime Video,BluTV").build(),

                Movie.builder().tmdbId(429L).title("İyi, Kötü ve Çirkin").originalLanguage("it")
                        .overview("Amerikan İç Savaşı sırasında üç silahşör, gömülü bir altın hazinesini bulmak için birbirleriyle amansız bir yarışa ve ittifaklara girişir.")
                        .posterPath("/bX2xnavhMYjWDoZp1VM6VnU1xwe.jpg").releaseDate(LocalDate.of(1966, 12, 23)).releaseYear(1966)
                        .runtime(178).voteAverage(8.5).voteCount(8500L).genres(new String[]{"Vahşi Batı", "Macera"})
                        .streamingPlatforms("Prime Video,BluTV").build(),

                Movie.builder().tmdbId(769L).title("Sıkı Dostlar").originalLanguage("en")
                        .overview("Henry Hill'in çocukluğundan itibaren Brooklyn mafya hiyerarşisinde yükselişini ve yeraltı dünyasının acımasız kurallarını gözler önüne seren sinema klasiği.")
                        .posterPath("/aKuFiU82s5ISJpGZZ7YkIr3kCUd.jpg").releaseDate(LocalDate.of(1990, 9, 12)).releaseYear(1990)
                        .runtime(145).voteAverage(8.5).voteCount(12500L).genres(new String[]{"Dram", "Suç"})
                        .streamingPlatforms("Netflix,Prime Video").build(),

                Movie.builder().tmdbId(106646L).title("Para Avcısı").originalLanguage("en")
                        .overview("Jordan Belfort'un Wall Street borsasında kurduğu aldatmaca imparatorluğu, lüks, para ve çöküşle dolu fırtınalı gerçek yaşam hikayesi.")
                        .posterPath("/kW9LmvYwrkn0k9hC7PzrkF4h52X.jpg").releaseDate(LocalDate.of(2013, 12, 25)).releaseYear(2013)
                        .runtime(180).voteAverage(8.0).voteCount(23000L).genres(new String[]{"Suç", "Dram", "Komedi"})
                        .streamingPlatforms("Netflix,Prime Video,BluTV").build(),

                Movie.builder().tmdbId(13L).title("Forrest Gump").originalLanguage("en")
                        .overview("Düşük IQ'lu ancak kocaman bir kalbe sahip Forrest Gump, 20. yüzyıl Amerika'sının en önemli tarihi anlarına tanıklık ederken çocukluk aşkı Jenny'yi asla unutmaz.")
                        .posterPath("/arw2VCBveWOVZr6pxd9XTd1TdQa.jpg").releaseDate(LocalDate.of(1994, 6, 23)).releaseYear(1994)
                        .runtime(142).voteAverage(8.5).voteCount(27000L).genres(new String[]{"Komedi", "Dram", "Romantik"})
                        .streamingPlatforms("Netflix,Prime Video,TOD").build(),

                Movie.builder().tmdbId(389L).title("12 Öfkeli Adam").originalLanguage("en")
                        .overview("Cinayetle suçlanan bir genç hakkında karar vermek zorunda olan 12 jüri üyesinin, vicdan ve önyargılarla dolu tek bir odada geçen nefes kesici tartışması.")
                        .posterPath("/ow3wq89wM8qd5X7hWKxiRfsFf9C.jpg").releaseDate(LocalDate.of(1957, 4, 10)).releaseYear(1957)
                        .runtime(96).voteAverage(8.5).voteCount(8400L).genres(new String[]{"Dram"})
                        .streamingPlatforms("Prime Video,MUBI").build(),

                Movie.builder().tmdbId(120L).title("Yüzüklerin Efendisi: Yüzük Kardeşliği").originalLanguage("en")
                        .overview("Genç hobbit Frodo Baggins, Karanlıklar Efendisi Sauron'un Tek Yüzük'ünü yok etmek için kadim dostlarıyla tehlikeli bir yolculuğa çıkar.")
                        .posterPath("/6oom5QYQ2yQTMJIbnvbkBL9cDK6.jpg").releaseDate(LocalDate.of(2001, 12, 18)).releaseYear(2001)
                        .runtime(178).voteAverage(8.4).voteCount(24500L).genres(new String[]{"Macera", "Fantezi", "Aksiyon"})
                        .streamingPlatforms("Prime Video,BluTV,TOD").build(),

                Movie.builder().tmdbId(121L).title("Yüzüklerin Efendisi: İki Kule").originalLanguage("en")
                        .overview("Kardeşlik dağılırken Frodo ve Sam Gollum'un rehberliğinde Mordor'a ilerler, Aragorn ve dostları Rohan halkını Saruman'ın ordusuna karşı savunur.")
                        .posterPath("/5VTN0pR8gcqV3EPUHHfMGnJYN9L.jpg").releaseDate(LocalDate.of(2002, 12, 18)).releaseYear(2002)
                        .runtime(179).voteAverage(8.4).voteCount(21500L).genres(new String[]{"Macera", "Fantezi", "Aksiyon"})
                        .streamingPlatforms("Prime Video,BluTV,TOD").build(),

                Movie.builder().tmdbId(122L).title("Yüzüklerin Efendisi: Kralın Dönüşü").originalLanguage("en")
                        .overview("Frodo ve Sam Tek Yüzük'ü Hüküm Dağı'na ulaştırmak için son adımlarını atarken, Aragorn insan ırkını Sauron'un dev ordularına karşı birleştirir.")
                        .posterPath("/rCzpDGLbOoPwLjy3OAm5NUPOTrC.jpg").releaseDate(LocalDate.of(2003, 12, 1)).releaseYear(2003)
                        .runtime(201).voteAverage(8.5).voteCount(24000L).genres(new String[]{"Macera", "Fantezi", "Aksiyon"})
                        .streamingPlatforms("Prime Video,BluTV,TOD").build(),

                Movie.builder().tmdbId(244786L).title("Whiplash").originalLanguage("en")
                        .overview("Genç ve hırslı bir caz bateristi, ülkenin en prestijli müzik akademisinde acımasız ve sınırları zorlayan bir eğitmenin gözetiminde mükemmelliğe ulaşmaya çalışır.")
                        .posterPath("/6uSPcdGNAv4tT96W1bQ3ZqX661S.jpg").releaseDate(LocalDate.of(2014, 10, 10)).releaseYear(2014)
                        .runtime(107).voteAverage(8.4).voteCount(14800L).genres(new String[]{"Dram", "Müzik"})
                        .streamingPlatforms("Netflix,Prime Video").build(),

                Movie.builder().tmdbId(597L).title("Titanik").originalLanguage("en")
                        .overview("Farklı sosyal sınıflardan Jack ve Rose'un dünyanın en büyük ve lüks transatlantiğinde filizlenen efsanevi ve trajik aşk hikayesi.")
                        .posterPath("/9xjZS2rlVxm8SFx8kPC3aIGCOYQ.jpg").releaseDate(LocalDate.of(1997, 11, 18)).releaseYear(1997)
                        .runtime(194).voteAverage(7.9).voteCount(24800L).genres(new String[]{"Dram", "Romantik"})
                        .streamingPlatforms("Disney+").build(),

                Movie.builder().tmdbId(1578L).title("Raging Bull: Kızgın Boğa").originalLanguage("en")
                        .overview("Boksör Jake LaMotta'nın ringdeki vahşi şiddetinin ve özel hayatındaki paranoyak öfkesinin onu nasıl zirveden çöküşe sürüklediğini anlatan Martin Scorsese başyapıtı.")
                        .posterPath("/1P6fH9nptU1f4F5C0gLp0XF9QnL.jpg").releaseDate(LocalDate.of(1980, 11, 14)).releaseYear(1980)
                        .runtime(129).voteAverage(8.1).voteCount(4100L).genres(new String[]{"Dram"})
                        .streamingPlatforms("Prime Video,MUBI").build(),

                // ── TÜRK SİNEMASI BAŞYAPITLARI ──
                Movie.builder().tmdbId(25633L).title("Babam ve Oğlum").originalLanguage("tr")
                        .overview("12 Eylül darbesinin ardından eşini kaybeden Sadık, yıllardır görüşmediği Ege'deki çiftlik sahibi babasının yanına küçük oğlu Deniz ile geri döner.")
                        .posterPath("/7WsyChvgjoIPcZupvoBw53F6vkl.jpg").releaseDate(LocalDate.of(2005, 11, 18)).releaseYear(2005)
                        .runtime(112).voteAverage(8.3).voteCount(3500L).genres(new String[]{"Dram", "Aile"})
                        .streamingPlatforms("Netflix,BluTV,TOD").build(),

                Movie.builder().tmdbId(239566L).title("Kış Uykusu").originalLanguage("tr")
                        .overview("Eski bir tiyatro oyuncusu olan Aydın, Kapadokya'daki butik otelinde genç karısı Nihal ve boşanmış kız kardeşi Necla ile kış mevsiminin getirdiği içsel hesaplaşmalar yaşar.")
                        .posterPath("/5M4q8072xS3Q4y5Z6a7b8c9d0e1.jpg").releaseDate(LocalDate.of(2014, 6, 13)).releaseYear(2014)
                        .runtime(196).voteAverage(8.1).voteCount(1200L).genres(new String[]{"Dram"})
                        .streamingPlatforms("BluTV,MUBI").build(),

                Movie.builder().tmdbId(703134L).title("Bir Zamanlar Anadolu'da").originalLanguage("tr")
                        .overview("Bir savcı, bir doktor ve bir cinayet zanlısı, gece boyunca bozkırın ortasında gömülü bir cesedi ararken hayatın ve ölümün derin gerçekleriyle yüzleşir.")
                        .posterPath("/sI0xY7g8H9j0K1L2M3N4O5P6Q7R.jpg").releaseDate(LocalDate.of(2011, 5, 21)).releaseYear(2011)
                        .runtime(157).voteAverage(7.9).voteCount(980L).genres(new String[]{"Dram", "Suç"})
                        .streamingPlatforms("BluTV,MUBI").build(),

                Movie.builder().tmdbId(64158L).title("Eşkıya").originalLanguage("tr")
                        .overview("35 yıl hapis yattıktan sonra köyüne dönen ve sevdiği kadının İstanbul'a götürüldüğünü öğrenen Baran, metropolün acımasız sokaklarında geçmişin hesabını sorar.")
                        .posterPath("/uA1B2C3D4E5F6G7H8I9J0K1L2M3.jpg").releaseDate(LocalDate.of(1996, 11, 29)).releaseYear(1996)
                        .runtime(128).voteAverage(8.2).voteCount(2200L).genres(new String[]{"Dram", "Suç", "Gerilim"})
                        .streamingPlatforms("Netflix,BluTV").build(),

                Movie.builder().tmdbId(63148L).title("G.O.R.A.").originalLanguage("tr")
                        .overview("Kurnaz halı tüccarı Arif, uzaylılar tarafından kaçırılarak G.O.R.A. gezegenine götürülür. Gezegeni zalim Komutan Logar'dan kurtarmak için kahramanca bir mücadeleye girişir.")
                        .posterPath("/wX1Y2Z3A4B5C6D7E8F9G0H1I2J3.jpg").releaseDate(LocalDate.of(2004, 11, 12)).releaseYear(2004)
                        .runtime(127).voteAverage(8.0).voteCount(4200L).genres(new String[]{"Komedi", "Bilim Kurgu", "Macera"})
                        .streamingPlatforms("Netflix,Prime Video,BluTV").build(),

                Movie.builder().tmdbId(25134L).title("A.R.O.G.").originalLanguage("tr")
                        .overview("Komutan Logar tarafından zaman makinesiyle Taş Devri'ne gönderilen Arif, ilkel insanları medeniyetle tanıştırıp modern çağa dönmenin yollarını arar.")
                        .posterPath("/yA1B2C3D4E5F6G7H8I9J0K1L2M3.jpg").releaseDate(LocalDate.of(2008, 12, 5)).releaseYear(2008)
                        .runtime(128).voteAverage(7.4).voteCount(2800L).genres(new String[]{"Komedi", "Bilim Kurgu"})
                        .streamingPlatforms("Netflix,BluTV").build(),

                Movie.builder().tmdbId(592834L).title("7. Koğuştaki Mucize").originalLanguage("tr")
                        .overview("Aklı 7 yaşındaki kızıyla aynı zeka seviyesinde olan Memo, haksız yere idam cezasına çarptırılır. Cezaevi arkadaşları onun masumiyetini kanıtlamak için bir mucize arar.")
                        .posterPath("/21OvrL2GjWq3zXv8UvXnE8MhM0n.jpg").releaseDate(LocalDate.of(2019, 10, 11)).releaseYear(2019)
                        .runtime(132).voteAverage(8.3).voteCount(3900L).genres(new String[]{"Dram", "Aile"})
                        .streamingPlatforms("Netflix").build(),

                // ── ANİMASYON & AİLE ──
                Movie.builder().tmdbId(129L).title("Ruhların Kaçışı (Spirited Away)").originalLanguage("ja")
                        .overview("10 yaşındaki Chihiro, ailesi domuza dönüştükten sonra ruhlar ve tanrılarla dolu fantastik bir hamamda çalışarak onları kurtarmaya çalışır.")
                        .posterPath("/39wmItIWsg5sZMyRUHLkWBcuVCM.jpg").releaseDate(LocalDate.of(2001, 7, 20)).releaseYear(2001)
                        .runtime(125).voteAverage(8.5).voteCount(16000L).genres(new String[]{"Animasyon", "Aile", "Fantezi"})
                        .streamingPlatforms("Netflix").build(),

                Movie.builder().tmdbId(372058L).title("Senin Adın (Your Name)").originalLanguage("ja")
                        .overview("Farklı şehirlerde yaşayan ve birbirini hiç görmemiş iki lise öğrencisi, gizemli bir şekilde beden değiştirmeye başlar ve aralarında zamana meydan okuyan bir bağ kurulur.")
                        .posterPath("/q719jXXEzOoYaps6qFsRWa0R6CS.jpg").releaseDate(LocalDate.of(2016, 8, 26)).releaseYear(2016)
                        .runtime(106).voteAverage(8.5).voteCount(11000L).genres(new String[]{"Animasyon", "Romantik", "Dram"})
                        .streamingPlatforms("Netflix,Prime Video").build(),

                Movie.builder().tmdbId(569094L).title("Örümcek-Adam: Örümcek Evrenine Geçiş").originalLanguage("en")
                        .overview("Miles Morales, Gwen Stacy ile birlikte çoklu evrene fırlatılır ve evrenin varlığını tehdit eden yeni bir düşmana karşı diğer Örümcek Kahramanlarla yüzleşir.")
                        .posterPath("/8Vt6mWEReuy4Of61Lnj5Xj704m8.jpg").releaseDate(LocalDate.of(2023, 5, 31)).releaseYear(2023)
                        .runtime(140).voteAverage(8.4).voteCount(6800L).genres(new String[]{"Animasyon", "Aksiyon", "Macera", "Bilim Kurgu"})
                        .streamingPlatforms("Netflix,Prime Video,Apple TV").build(),

                Movie.builder().tmdbId(862L).title("Oyuncak Hikayesi").originalLanguage("en")
                        .overview("Kovboy Woody, sahibi Andy'nin en sevdiği oyuncağıdır; ta ki odaya fütüristik uzay kahramanı Buzz Lightyear gelene kadar.")
                        .posterPath("/uXDfjJbdP4ijW5hWSBrPrlKpxab.jpg").releaseDate(LocalDate.of(1995, 10, 30)).releaseYear(1995)
                        .runtime(81).voteAverage(8.0).voteCount(18000L).genres(new String[]{"Animasyon", "Komedi", "Aile"})
                        .streamingPlatforms("Disney+").build(),

                Movie.builder().tmdbId(10681L).title("WALL-E").originalLanguage("en")
                        .overview("Terk edilmiş dünyada yüzyıllardır çöpleri temizleyen sevimli robot WALL-E, modern bir keşif robotu olan EVE'e aşık olunca uzayın derinliklerine uzanan bir maceraya atılır.")
                        .posterPath("/hbhFnRzzgcoIEmmUQ92Ap40Oebn.jpg").releaseDate(LocalDate.of(2008, 6, 22)).releaseYear(2008)
                        .runtime(98).voteAverage(8.1).voteCount(18500L).genres(new String[]{"Animasyon", "Aile", "Bilim Kurgu"})
                        .streamingPlatforms("Disney+").build(),

                Movie.builder().tmdbId(14160L).title("Yukarı Bak (Up)").originalLanguage("en")
                        .overview("78 yaşındaki balon satıcısı Carl Fredricksen, evine binlerce balon bağlayarak Güney Amerika'nın vahşi doğasına doğru çocukluk hayalini gerçekleştirmek için uçar.")
                        .posterPath("/eAdO0s3iU0pX9HqF992N73g4y8p.jpg").releaseDate(LocalDate.of(2009, 5, 28)).releaseYear(2009)
                        .runtime(96).voteAverage(8.0).voteCount(19500L).genres(new String[]{"Animasyon", "Komedi", "Aile", "Macera"})
                        .streamingPlatforms("Disney+").build()
        );

        int addedCount = 0;
        for (Movie m : movies) {
            if (movieRepository.findByTmdbId(m.getTmdbId()).isEmpty()) {
                movieRepository.save(m);
                addedCount++;
            }
        }
        log.info("✅ Genişletilmiş film kataloğu başarıyla yüklendi! (Eklenen: {}, Veritabanı Toplam: {})", addedCount, movieRepository.count());
    }
}
