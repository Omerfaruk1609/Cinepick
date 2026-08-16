/**
 * CinePick Pure JavaScript Narrative Engine (İstemci Taraflı Anlatı Analizi Motoru)
 * 
 * Film verilerini (overview, genres, vote_average, runtime, release_date) analiz ederek
 * kural tabanlı algoritma ile atmosfer, anlatı temposu ve edebî inceleme metni üretir.
 */

export function analyzeNarrative(movie) {
  if (!movie) {
    return {
      vibe: 'Atmosferik & Gizemli',
      pace: 'Dengeli Anlatı Yapısı',
      insight: 'Bu eser derin teması ve görsel estetiğiyle izleyiciye benzersiz bir sinema tecrübesi sunar.',
      targetAudience: 'Sinematik derinlik ve güçlü atmosfer arayanlar',
      notForAudience: 'Hızlı tüketilen, yüzeysel yapımları tercih edenler',
      quickHook: 'Büyüleyici bir atmosfer ve karakter odaklı anlatım eşliğinde gelişen sinematik bir serüven.',
      depthScore: 8.0,
    };
  }

  const title = movie.title || '';
  const overview = movie.overview || '';
  const runtime = Number(movie.runtime) || 110;
  const releaseYear = movie.release_date ? new Date(movie.release_date).getFullYear() : (movie.releaseYear || 2020);
  const voteAverage = Number(movie.vote_average ?? movie.voteAverage) || 7.5;
  const genreNames = Array.isArray(movie.genres)
    ? movie.genres.map((g) => (typeof g === 'string' ? g : g.name || ''))
    : [];

  const genreText = genreNames.join(' ').toLowerCase();
  const lowerOverview = overview.toLowerCase();

  // 1. Atmosfer & Visual Vibe
  let vibe = 'Atmosferik & Etkileyici';
  let targetAudience = 'Kaliteli sinematografi ve dengeli tempo arayan sinemaseverler';
  let notForAudience = 'Aşırı gürültülü veya aceleci kurguları tercih edenler';
  let quickHook = overview.length > 130 ? overview.slice(0, 130) + '...' : overview;

  if (/katil|suç|cinayet|gizem|sır|zihin|akıl|karanlık|psikoloj/.test(lowerOverview) || /mystery|crime|thriller|gizem|gerilim|suç/.test(genreText)) {
    vibe = 'Kasvetli & Psikolojik Gerilim';
    targetAudience = 'Ters köşe sonları, zihin büken gizemleri ve karanlık polisiye atmosferleri sevenler';
    notForAudience = 'Hafif, tasasız ve neşeli komedi arayanlar';
    quickHook = `${title}, karmaşık sır perdeleri ve yüksek tempolu gerilimiyle izleyiciyi koltuğa çivileyen bir psikolojik labirent sunuyor.`;
  } else if (/uzay|gelecek|bilim|yapay|zaman|robot|rüy|sürreal/.test(lowerOverview) || /sci-fi|science fiction|bilim kurgu/.test(genreText)) {
    vibe = 'Sürreal & Zihin Büken';
    targetAudience = 'Evrenin sınırlarını, zaman kavramını ve felsefi bilimkurgu temalarını sorgulayanlar';
    notForAudience = 'Basit ve doğrusal günlük yaşam hikayeleri arayanlar';
    quickHook = `${title}, geleceğin ve bilinmeyenin kapılarını aralayarak zaman, algı ve insan varoluşunu radikal bir vizyonla inceliyor.`;
  } else if (/savaş|patlama|kaçış|intikam|silah|dövüş|aksiyon/.test(lowerOverview) || /action|aksiyon|adventure|macera|savaş/.test(genreText)) {
    vibe = 'Yüksek Adrenalin & Dinamik Kaos';
    targetAudience = 'Görsel efektler, soluksuz kovalamacalar ve güçlü aksiyon koreografilerinden keyif alanlar';
    notForAudience = 'Ağır tempolu, diyaloğa dayalı sanat filmi beklentisinde olanlar';
    quickHook = `${title}, dur durak bilmeyen enerjisi ve etkileyici aksiyon sahneleriyle saf bir sinematik heyecan yaşatıyor.`;
  } else if (/hayat|varoluş|ölüm|aşk|ilişki|dram|trajed|vicdan/.test(lowerOverview) || /drama|dram|romance|romantik/.test(genreText)) {
    vibe = 'Duygusal & Varoluşsal Derinlik';
    targetAudience = 'İnsan psikolojisi, gerçekçi karakter çatışmaları ve dokunaklı hayat öyküleri izlemek isteyenler';
    notForAudience = 'Kafa yormayan, çerezlik ve patlamalı eğlence arayanlar';
    quickHook = `${title}, insan kalbinin en hassas noktalarına dokunan samimi dili ve güçlü oyunculuklarıyla unutulmaz bir duygu seli yaratıyor.`;
  } else if (/komedi|eğlence|kahkaha|komik|animasyon|aile/.test(lowerOverview) || /comedy|komedi|animation|animasyon|family|aile/.test(genreText)) {
    vibe = 'Neşeli, Sıcak & Eğlenceli';
    targetAudience = 'Günün stresini atmak, kahkaha dolu ve keyifli bir vakit geçirmek isteyen herkes';
    notForAudience = 'Karanlık, kasvetli ve kanlı gerilim filmi arayanlar';
    quickHook = `${title}, zekice yazılmış mizahı, renkli karakterleri ve sıcacık dinamikleriyle yüzünüzde tebessüm bırakmayı garantiliyor.`;
  }

  // 2. Anlatı Temposu
  let pace = 'Dengeli ve Sürükleyici Anlatı';
  if (releaseYear < 1990) {
    pace = 'Yavaş Salınımlı Klasik Anlatı';
  } else if (runtime >= 135) {
    pace = 'Katmanlı ve Geniş Zamana Yayılan Epik Kurgu';
  } else if (runtime <= 100) {
    pace = 'Akıcı ve Hızlı Tempolu Kurgu';
  }

  // 3. Neden İzlemelisin?
  let insight = '';
  if (voteAverage >= 8.2) {
    insight = `Eleştirmenlerden tam not almış bu yapım, ${vibe.toLowerCase()} tonu ve ${pace.toLowerCase()} yapısıyla sinema tarihinin seçkin eserleri arasında yer alıyor. Hem görsel estetiği hem de derin alt metinleriyle birden fazla kez izlenmeyi hak ediyor.`;
  } else if (voteAverage >= 7.0) {
    insight = `${vibe} atmosferiyle kendi türünün en başarılı örneklerinden biri. ${pace} temposu sayesinde izleyiciyi hikayenin içine çekmeyi ve merak duygusunu son ana kadar diri tutmayı başarıyor.`;
  } else {
    insight = `Türün dinamiklerini sevenler için ${vibe.toLowerCase()} dokusuyla keyifli bir seyir vadeden dinamik bir yapım.`;
  }

  const depthScore = Math.min(9.9, Math.max(6.5, (voteAverage * 0.8) + (runtime > 120 ? 1.0 : 0.6))).toFixed(1);

  return {
    vibe,
    pace,
    insight,
    targetAudience,
    notForAudience,
    quickHook,
    depthScore: Number(depthScore),
  };
}
