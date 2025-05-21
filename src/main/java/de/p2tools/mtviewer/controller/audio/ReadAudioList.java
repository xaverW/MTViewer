/*
 * P2Tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */

package de.p2tools.mtviewer.controller.audio;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.p2lib.atdata.AudioFactory;
import de.p2tools.p2lib.atdata.AudioListFactory;
import de.p2tools.p2lib.mtdownload.MLHttpClient;
import de.p2tools.p2lib.mtfilm.film.FilmData;
import de.p2tools.p2lib.mtfilm.film.Filmlist;
import de.p2tools.p2lib.mtfilm.readwritefilmlist.ReadFilmlist;
import de.p2tools.p2lib.mtfilm.readwritefilmlist.WriteFilmlistJson;
import de.p2tools.p2lib.mtfilm.tools.InputStreamProgressMonitor;
import de.p2tools.p2lib.mtfilm.tools.LoadFactoryConst;
import de.p2tools.p2lib.mtfilm.tools.ProgressMonitorInputStream;
import de.p2tools.p2lib.tools.date.P2DateConst;
import de.p2tools.p2lib.tools.date.P2LDateTimeFactory;
import de.p2tools.p2lib.tools.duration.P2Duration;
import de.p2tools.p2lib.tools.log.P2Log;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.tukaani.xz.XZInputStream;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipInputStream;

public class ReadAudioList {

    private List<String> logList = new ArrayList<>();
    private static int countDouble = 0;

    public ReadAudioList() {
    }

    public boolean readDb(boolean localList, Path path) {
        boolean ret;
        P2Duration.counterStart("readDb");

        try {
            if (localList) {
                if (!Files.exists(path) || path.toFile().length() == 0) {
                    return false;
                }

                logList.add("##");
                logList.add("## Jetzige Liste erstellt am: " + LoadAudioFactoryDto.audioListNew.genDate());
                logList.add("##   Anzahl Filme: " + LoadAudioFactoryDto.audioListNew.size());
                logList.add("##   Anzahl Neue:  " + LoadAudioFactoryDto.audioListNew.countNewFilms());
                logList.add("##");
                logList.add("## " + P2Log.LILNE2);
                logList.add("##");

                LoadAudioFactoryDto.audioListAkt.clear();
                LoadAudioFactoryDto.audioListNew.clear();
                logList.add("Audioliste lesen");
                logList.add("   --> Lesen von: " + path);
                new ReadFilmlist().readFilmlistWebOrLocal(logList, LoadAudioFactoryDto.audioListNew, path.toString());
                setDate();

                logList.add("## Filme markieren");
                final int count = LoadAudioFactoryDto.audioListNew.markFilms();

                logList.add("## Anzahl doppelte Filme: " + count);
                LoadAudioFactoryDto.audioListNew.loadSender();

                logList.add("##   Audioliste gelesen, OK");
                logList.add("##   Anzahl gelesen: " + LoadAudioFactoryDto.audioListNew.size());
                ret = true;

            } else {
                // Hash füllen
                fillHash(logList, LoadAudioFactoryDto.audioListAkt);
                fillHash(logList, LoadAudioFactoryDto.audioListNew);
                LoadAudioFactoryDto.audioListAkt.clear();
                LoadAudioFactoryDto.audioListNew.clear();

                //dann aus dem Web mit der URL laden
                logList.add("## Audioliste aus URL laden: " + de.p2tools.p2lib.atdata.AudioFactory.AUDIOLIST_URL);
                processFromWeb(new URL(AudioFactory.AUDIOLIST_URL), LoadAudioFactoryDto.audioListNew);

                if (LoadAudioFactoryDto.audioListNew.isEmpty()) {
                    // dann hats nicht geklappt
                    ret = false;

                } else {
                    setDate();
                    // unerwünschte löschen
                    removeUnwanted(logList, LoadAudioFactoryDto.audioListNew);
                    // neue Filme markieren
                    markNewFilms(logList, LoadAudioFactoryDto.audioListNew);
                    markDoubleAudios(logList, LoadAudioFactoryDto.audioListNew);

                    // und dann auch speichern
                    logList.add("##");
                    logList.add("## Audioliste schreiben (" + LoadAudioFactoryDto.audioListNew.size() + " Audios) :");
                    logList.add("##    --> Start Schreiben nach: " + path);
                    new WriteFilmlistJson().write(path.toString(), LoadAudioFactoryDto.audioListNew);
                    logList.add("##    --> geschrieben!");
                    logList.add("##");

                    ret = true;
                }
            }
        } catch (final Exception ex) {
            logList.add("##   Audioliste lesen hat nicht geklappt");
            P2Log.errorLog(645891204, ex);
            ret = false;
        }

        P2Log.sysLog(logList);
        P2Duration.counterStop("readDb");
        return ret;
    }

    private void setDate() {
        // Datum setzen
        LocalDateTime date = AudioListFactory.getDate(LoadAudioFactoryDto.audioListNew.metaData);
        String dateStr = P2LDateTimeFactory.toString(date, P2DateConst.DT_FORMATTER_dd_MM_yyyy___HH__mm);
        ProgConfig.SYSTEM_AUDIOLIST_DATE_TIME.setValue(dateStr);
    }

    private void processFromFile(String source, Filmlist<FilmData> filmlist) {
        try (InputStream in = selectDecompressor(source, new FileInputStream(source));
             JsonParser jp = new JsonFactory().createParser(in)) {
            new de.p2tools.mtviewer.controller.audio.ReadAudioListJson().readFilmData(jp, filmlist);

        } catch (final FileNotFoundException ex) {
            logList.add("Audioliste existiert nicht: " + source + "\n" + ex.getLocalizedMessage());
            P2Log.errorLog(894512369, "Audioliste existiert nicht: " + source);
            filmlist.clear();

        } catch (final Exception ex) {
            logList.add("Audioliste: " + source + "\n" + ex.getLocalizedMessage());
            P2Log.errorLog(945123641, ex, "Audioliste: " + source);
            filmlist.clear();
        }
    }

    private static InputStream selectDecompressor(String source, InputStream in) throws Exception {
        if (source.endsWith(LoadFactoryConst.FORMAT_XZ)) {
            in = new XZInputStream(in);
        } else if (source.endsWith(LoadFactoryConst.FORMAT_ZIP)) {
            final ZipInputStream zipInputStream = new ZipInputStream(in);
            zipInputStream.getNextEntry();
            in = zipInputStream;
        }
        return in;
    }

    private void processFromWeb(URL source, Filmlist<FilmData> audioList) {
        final Request.Builder builder = new Request.Builder().url(source);
        builder.addHeader("User-Agent", LoadFactoryConst.userAgent);

        // our progress monitor callback
        final InputStreamProgressMonitor monitor = new InputStreamProgressMonitor() {
            private int oldProgress = 0;

            @Override
            public void progress(long bytesRead, long size) {
                final int iProgress = (int) (bytesRead * 100/* zum Runden */ / size);
                if (iProgress != oldProgress) {
                    oldProgress = iProgress;
//                    notifyProgress(1.0 * iProgress / 100);
                }
            }
        };

        try (Response response = MLHttpClient.getInstance().getHttpClient().newCall(builder.build()).execute();
             ResponseBody body = response.body()) {
            if (body != null && response.isSuccessful()) {

                try (InputStream input = new ProgressMonitorInputStream(body.byteStream(), body.contentLength(), monitor)) {
                    try (InputStream is = selectDecompressor(source.toString(), input);
                         JsonParser jp = new JsonFactory().createParser(is)) {
                        new ReadAudioListJson().readFilmData(jp, audioList);
                    }
                }
            }
        } catch (final Exception ex) {
            P2Log.errorLog(820147395, ex, "FilmListe: " + source);
            audioList.clear();
        }
    }

    private void fillHash(List<String> logList, Filmlist<FilmData> audioList) {
        //alle historyURLs in den hash schreiben
        logList.add("## " + P2Log.LILNE3);
        logList.add("## Hash füllen, Größe vorher: " + LoadAudioFactoryDto.hashSet.size());

        LoadAudioFactoryDto.hashSet.addAll(audioList.stream().map(FilmData::getUrlHistory).toList());
        logList.add("##                   nachher: " + LoadAudioFactoryDto.hashSet.size());
        logList.add("## " + P2Log.LILNE3);
    }

    private void removeUnwanted(List<String> logList, Filmlist<FilmData> audioList) {
        if (LoadAudioFactoryDto.SYSTEM_LOAD_FILMLIST_MAX_DAYS == 0 &&
                LoadAudioFactoryDto.SYSTEM_LOAD_FILMLIST_MIN_DURATION == 0) {
            // dann alles
            return;
        }

        try {
            logList.add("## unerwünschte löschen");
            Iterator<FilmData> it = audioList.iterator();
            LocalDate minDate = LocalDate.now().minusDays(LoadAudioFactoryDto.SYSTEM_LOAD_FILMLIST_MAX_DAYS);
            while (it.hasNext()) {
                FilmData audioData = it.next();
                if (LoadAudioFactoryDto.SYSTEM_LOAD_FILMLIST_MAX_DAYS > 0 &&
                        audioData.getDate().getLocalDate().isBefore(minDate)) {
                    it.remove();
                    continue;
                }
                if (LoadAudioFactoryDto.SYSTEM_LOAD_FILMLIST_MIN_DURATION > 0 &&
                        audioData.getDurationMinute() != 0 &&
                        audioData.getDurationMinute() < LoadAudioFactoryDto.SYSTEM_LOAD_FILMLIST_MIN_DURATION) {
                    it.remove();
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }

    private void markNewFilms(List<String> logList, Filmlist<FilmData> audioList) {
        logList.add("## neue Audios markieren");
        audioList.stream() //genauso schnell wie "parallel": ~90ms
                .peek(film -> film.setNewFilm(false))
                .filter(film -> !LoadAudioFactoryDto.hashSet.contains(film.getUrl()))
                .forEach(film -> {
                    film.setNewFilm(true);
                });

        LoadAudioFactoryDto.hashSet.clear();
    }

    public void markDoubleAudios(List<String> logList, Filmlist<FilmData> audioList) {
        // läuft direkt nach dem Laden der Filmliste!
        // doppelte Filme (URL)
        // viele Filme sind bei mehreren Sendern vorhanden

        logList.add("## neue Audios markieren");
        final HashSet<String> urlHashSet = new HashSet<>(audioList.size(), 0.75F);
        P2Duration.counterStart("markAudios");
        try {
            countDouble = 0;
            audioList.forEach((FilmData f) -> {
                if (!urlHashSet.add(f.getUrl())) {
                    ++countDouble;
                    f.setDoubleUrl(true);
                }
            });
        } catch (Exception ex) {
            P2Log.errorLog(951024789, ex);
        }

        urlHashSet.clear();
        P2Duration.counterStop("markAudios");

        ProgConfig.SYSTEM_AUDIOLIST_COUNT_DOUBLE.setValue(countDouble);
    }
}
