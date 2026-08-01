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
      depthScore: 8.0,
    };
  }

  const overview = movie.overview || '';
  const runtime = Number(movie.runtime) || 110;
  const releaseYear = movie.release_date ? new Date(movie.release_date).getFullYear() : 2020;
  const voteAverage = Number(movie.vote_average) || 7.5;
  const genreNames = Array.isArray(movie.genres)
    ? movie.genres.map((g) => (typeof g === 'string' ? g : g.name || ''))
    : [];

  const genreText = genreNames.join(' ').toLowerCase();

  // 1. Atmosfer & Visual Vibe Matrisi
  let vibe = 'Atmosferik & Etkileyici';
  const lowerOverview = overview.toLowerCase();

  if (/katil|suç|cinayet|gizem|sır|zihin|akıl|karanlık|psikoloj/.test(lowerOverview) || /mystery|crime|thriller|gizem|gerilim/.test(genreText)) {
    vibe = 'Kasvetli & Felsefi';
  } else if (/uzay|gelecek|bilim|yapay|zaman|robot|rüy|sürreal/.test(lowerOverview) || /sci-fi|science fiction|bilim kurgu/.test(genreText)) {
    vibe = 'Sürreal & Düşündürücü';
  } else if (/savaş|patlama|kaçış|intikam|silah|dövüş|aksiyon/.test(lowerOverview) || /action|aksiyon|adventure|macera/.test(genreText)) {
    vibe = 'Sinematik & Tempolu Kaos';
  } else if (/hayat|varoluş|ölüm|aşk|ilişki|dram|trajed|vicdan/.test(lowerOverview) || /drama|dram|romance|romantik/.test(genreText)) {
    vibe = 'Duygusal & Varoluşsal Derinlik';
  }

  // 2. Anlatı Temposu (Narrative Pace)
  let pace = 'Dengeli ve Sürükleyici Anlatı';
  if (releaseYear < 1990) {
    pace = 'Yavaş Salınımlı Klasik Anlatı';
  } else if (runtime >= 135) {
    pace = 'Katmanlı ve Geniş Zamana Yayılan Kurgu';
  } else if (runtime <= 100) {
    pace = 'Hızlı ve Doğrusal Kurgu';
  }

  // 3. Edebî İnceleme Metni (Dynamic Insight Generator)
  let insight = '';
  if (voteAverage >= 8.2) {
    insight = `Zengin alt metinleri ve ${vibe.toLowerCase()} dokusuyla öne çıkan bu film, izleyicisine sadece bir hikaye sunmakla kalmıyor; insan ruhunun derinliklerine ayna tutuyor. ${pace} yapısı sayesinde her sahnesinde düşünsel bir keşif vaat ediyor.`;
  } else if (voteAverage >= 7.0) {
    insight = `${vibe} atmosferi ve ${pace.toLowerCase()} temposuyla dikkat çeken yapım, türün meraklıları için sinematik bir lezzet sunuyor. Özgün kurgusu ve kilit sahneleriyle akılda kalıcı bir iz bırakıyor.`;
  } else {
    insight = `Farklı ve sürprizlerle dolu bir sinematik deneyim arayanlar için ${vibe.toLowerCase()} tonuyla öne çıkan merak uyandırıcı bir eser.`;
  }

  // 4. Derinlik Puanı Hesaplama
  const depthScore = Math.min(9.9, Math.max(6.5, (voteAverage * 0.8) + (runtime > 120 ? 1.0 : 0.6))).toFixed(1);

  return {
    vibe,
    pace,
    insight,
    depthScore: Number(depthScore),
  };
}
