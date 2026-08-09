import React from 'react';
import { Film, Heart, Github, Share2, MessageCircle, ShieldAlert } from 'lucide-react';

export default function Footer({ onNavigate }) {
  return (
    <footer className="mt-20 border-t border-slate-200 dark:border-slate-800/80 bg-slate-100 dark:bg-slate-950/80 text-slate-600 dark:text-slate-400 py-12 px-6 transition-colors">
      <div className="max-w-6xl mx-auto grid grid-cols-1 md:grid-cols-4 gap-8 mb-10">
        {/* Logo & Slogan */}
        <div className="space-y-3 md:col-span-1">
          <div className="flex items-center gap-2">
            <Film className="w-7 h-7 text-rose-500" />
            <span className="text-xl font-extrabold text-slate-900 dark:text-white tracking-wide">
              Cine<span className="text-rose-500">Pick</span>
            </span>
          </div>
          <p className="text-xs text-slate-500 dark:text-slate-400 leading-relaxed">
            Sinematik ve felsefi bakış açısıyla ruh halinize en uygun filmleri keşfedin ve kişisel kitaplığınızı oluşturun.
          </p>
        </div>

        {/* Hızlı Linkler */}
        <div className="space-y-3">
          <h4 className="text-xs font-bold uppercase tracking-wider text-slate-900 dark:text-slate-200">
            Hızlı Menü
          </h4>
          <ul className="space-y-2 text-xs">
            <li>
              <button onClick={() => onNavigate && onNavigate('explore')} className="hover:text-rose-500 transition-colors cursor-pointer">
                Keşfet & Popüler
              </button>
            </li>
            <li>
              <button onClick={() => onNavigate && onNavigate('watchlist')} className="hover:text-rose-500 transition-colors cursor-pointer">
                İzleyeceklerim Listesi
              </button>
            </li>
            <li>
              <button onClick={() => onNavigate && onNavigate('watched')} className="hover:text-rose-500 transition-colors cursor-pointer">
                İzlediklerim
              </button>
            </li>
          </ul>
        </div>

        {/* TMDB Yasal Attribution */}
        <div className="space-y-3 md:col-span-1">
          <h4 className="text-xs font-bold uppercase tracking-wider text-slate-900 dark:text-slate-200 flex items-center gap-1.5">
            <ShieldAlert className="w-3.5 h-3.5 text-rose-500" />
            Veri Sağlayıcı
          </h4>
          <p className="text-[11px] text-slate-500 leading-relaxed">
            Bu proje TMDB (The Movie Database) API kullanmaktadır ancak TMDB tarafından onaylanmamış veya sertifikalandırılmamıştır.
          </p>
        </div>

        {/* Sosyal Medya & GitHub Bağlantısı */}
        <div className="space-y-3">
          <h4 className="text-xs font-bold uppercase tracking-wider text-slate-900 dark:text-slate-200">
            Açık Kaynak & İletişim
          </h4>
          <div className="flex items-center gap-3">
            <a
              href="https://github.com/Omerfaruk1609/Cinepick"
              target="_blank"
              rel="noreferrer"
              className="flex items-center gap-2 px-3 py-2 rounded-xl bg-slate-900 dark:bg-slate-900 hover:bg-rose-600 text-white text-xs font-bold transition-all border border-slate-800 hover:border-rose-500 shadow-md cursor-pointer group"
              title="GitHub Reposunu İnceleyin"
            >
              <Github className="w-4 h-4 text-rose-500 group-hover:text-white transition-colors" />
              <span>GitHub'da İncele</span>
            </a>
            <a
              href="#"
              className="p-2 rounded-xl bg-slate-200 dark:bg-slate-900 hover:text-rose-500 transition-all border border-slate-300 dark:border-slate-800"
              title="Sosyal Medya"
            >
              <Share2 className="w-4 h-4" />
            </a>
            <a
              href="#"
              className="p-2 rounded-xl bg-slate-200 dark:bg-slate-900 hover:text-rose-500 transition-all border border-slate-300 dark:border-slate-800"
              title="İletişim"
            >
              <MessageCircle className="w-4 h-4" />
            </a>
          </div>
        </div>
      </div>

      <div className="max-w-6xl mx-auto pt-6 border-t border-slate-200 dark:border-slate-900 flex flex-col sm:flex-row items-center justify-between text-xs text-slate-500 gap-3">
        <p>© {new Date().getFullYear()} CinePick. Tüm hakları saklıdır.</p>
        <p className="flex items-center gap-1">
          <span>Sevgi ve sinema tutkusuyla tasarlandı</span>
          <Heart className="w-3.5 h-3.5 text-rose-500 fill-rose-500" />
        </p>
      </div>
    </footer>
  );
}
