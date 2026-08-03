import React from 'react';
import { Tv, ExternalLink } from 'lucide-react';

const WhereToWatch = ({ watchProviders }) => {
  // Türkiye (TR) verisi yoksa gösterim yapma
  if (!watchProviders || (!watchProviders.flatrate && !watchProviders.rent && !watchProviders.buy)) {
    return (
      <div className="mt-4 p-3 bg-slate-900/60 rounded-xl border border-slate-800 text-xs text-slate-400">
        Bu film için Türkiye'de aktif bir dijital yayın platformu bilgisi bulunamadı.
      </div>
    );
  }

  const { flatrate, rent, buy, link } = watchProviders;

  return (
    <div className="mt-6 p-4 bg-slate-950/80 border border-slate-800 rounded-2xl shadow-lg space-y-4">
      <div className="flex items-center justify-between">
        <h3 className="text-xs sm:text-sm font-bold text-slate-200 uppercase tracking-wider flex items-center gap-2">
          <Tv className="w-4 h-4 text-rose-500" />
          Nerede İzleyebilirim? (Türkiye)
        </h3>
        {link && (
          <a
            href={link}
            target="_blank"
            rel="noopener noreferrer"
            className="text-xs text-rose-400 hover:text-rose-300 hover:underline flex items-center gap-1 font-medium transition-colors"
          >
            <span>JustWatch'ta Aç</span>
            <ExternalLink className="w-3 h-3" />
          </a>
        )}
      </div>

      {/* Abonelikle İzle (Flatrate) */}
      {flatrate && flatrate.length > 0 && (
        <div>
          <span className="text-xs text-slate-400 block mb-2 font-semibold">Abonelikle İzlenebilenler:</span>
          <div className="flex flex-wrap gap-2">
            {flatrate.map((provider) => (
              <div
                key={provider.provider_id || provider.providerId || provider.provider_name}
                className="flex items-center gap-2 bg-slate-900 px-3 py-1.5 rounded-xl border border-slate-800 hover:border-slate-700 transition-colors"
                title={provider.provider_name || provider.providerName}
              >
                <img
                  src={`https://image.tmdb.org/t/p/original${provider.logo_path || provider.logoPath}`}
                  alt={provider.provider_name || provider.providerName}
                  className="w-5 h-5 rounded-md object-cover"
                />
                <span className="text-xs text-slate-200 font-medium">
                  {provider.provider_name || provider.providerName}
                </span>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Kirala / Satın Al */}
      {(rent || buy) && (
        <div>
          <span className="text-xs text-slate-400 block mb-2 font-semibold">Kirala / Satın Al:</span>
          <div className="flex flex-wrap gap-2">
            {[...(rent || []), ...(buy || [])]
              .filter((v, i, a) => a.findIndex(t => (t.provider_id || t.providerId) === (v.provider_id || v.providerId)) === i)
              .map((provider) => (
                <div
                  key={provider.provider_id || provider.providerId || provider.provider_name}
                  className="flex items-center gap-1.5 bg-slate-900/80 px-2.5 py-1 rounded-lg border border-slate-800"
                  title={provider.provider_name || provider.providerName}
                >
                  <img
                    src={`https://image.tmdb.org/t/p/original${provider.logo_path || provider.logoPath}`}
                    alt={provider.provider_name || provider.providerName}
                    className="w-4 h-4 rounded"
                  />
                  <span className="text-[11px] text-slate-300 font-medium">
                    {provider.provider_name || provider.providerName}
                  </span>
                </div>
              ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default WhereToWatch;
