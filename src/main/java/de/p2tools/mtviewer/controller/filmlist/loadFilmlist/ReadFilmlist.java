/*
 * MTViewer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
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

package de.p2tools.mtviewer.controller.filmlist.loadFilmlist;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgConst;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.config.ProgInfos;
import de.p2tools.mtviewer.controller.data.film.FilmData;
import de.p2tools.mtviewer.controller.data.film.FilmDataXml;
import de.p2tools.mtviewer.controller.data.film.Filmlist;
import de.p2tools.mtviewer.controller.data.film.FilmlistXml;
import de.p2tools.mtviewer.controller.filmlist.LoadFactory;
import de.p2tools.mtviewer.tools.InputStreamProgressMonitor;
import de.p2tools.mtviewer.tools.ProgressMonitorInputStream;
import de.p2tools.p2Lib.MTDownload.MLHttpClient;
import de.p2tools.p2Lib.tools.PStringUtils;
import de.p2tools.p2Lib.tools.duration.PDuration;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.application.Platform;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.tukaani.xz.XZInputStream;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipInputStream;

public class ReadFilmlist {

    private final int REDUCED_BANDWIDTH = 55;//ist ein Wert, der nicht eingestellt werden kann
    int sumFilms = 0;
    String channel = "", theme = "";
    private double progress = 0;
    private int countAll = 0;
    private Map<String, Integer> filmsPerChannelFoundCompleteList = new TreeMap<>();
    private Map<String, Integer> filmsPerChannelUsed = new TreeMap<>();
    private Map<String, Integer> filmsPerChannelBlocked = new TreeMap<>();
    private Map<String, Integer> filmsPerDateBlocked = new TreeMap<>();
    private Map<String, Integer> filmsPerDurationBlocked = new TreeMap<>();
    private int savedBandwidth = ProgConfig.DOWNLOAD_MAX_BANDWIDTH_KBYTE.getValue();

    /*
    Hier wird die Filmliste tatsächlich geladen (von Datei/URL)
     */
    public void readFilmlist(List<String> logList, String sourceFileUrl, final Filmlist filmlist) {
        countAll = 0;
        filmsPerChannelFoundCompleteList.clear();
        filmsPerChannelUsed.clear();
        filmsPerChannelBlocked.clear();
        filmsPerDateBlocked.clear();
        filmsPerDurationBlocked.clear();

        logList.add("");
        logList.add(PLog.LILNE2);

        PDuration.counterStart("ReadFilmlist.readFilmlist()");
        try {
            notifyStart(sourceFileUrl); // für die Progressanzeige

            filmlist.clear();
            if (sourceFileUrl.startsWith("http")) {
                // URL laden
                logList.add("Filmliste aus URL laden: " + sourceFileUrl);
                processFromWeb(new URL(sourceFileUrl), filmlist);

            } else {
                // lokale Datei laden
                logList.add("Filmliste aus Datei laden: " + sourceFileUrl);
                processFromFile(sourceFileUrl, filmlist);
            }

            if (ProgData.getInstance().loadFilmlist.isStop()) {
                logList.add(" -> Filmliste laden abgebrochen");
                filmlist.clear();

            } else {
                logList.add("   erstellt am:        " + filmlist.genDate());
                logList.add("   Anzahl Gesamtliste: " + countAll);
                logList.add("   Anzahl verwendet:   " + filmlist.size());
                countFoundChannel(logList, filmlist);
            }
        } catch (final MalformedURLException ex) {
            PLog.errorLog(945120201, ex);
        } catch (final Exception ex) {
            PLog.errorLog(965412378, ex);
        }

        PDuration.counterStop("ReadFilmlist.readFilmlist()");
        logList.add(PLog.LILNE2);
        logList.add("");
        notifyFinished(sourceFileUrl);
    }

    private void countFoundChannel(List<String> logList, Filmlist filmlist) {
        final int KEYSIZE = 12;

        PDuration.counterStart("ReadFilmlist.countFoundChannel()");
        if (!filmsPerChannelFoundCompleteList.isEmpty()) {
            logList.add(PLog.LILNE3);
            logList.add(" ");
            logList.add("== Filme pro Sender in der Gesamtliste ==");

            sumFilms = 0;
            filmsPerChannelFoundCompleteList.keySet().stream().forEach(key -> {
                int found = filmsPerChannelFoundCompleteList.get(key);
                sumFilms += found;
                logList.add(PStringUtils.increaseString(KEYSIZE, key) + ": " + found);
            });
            logList.add("--");
            logList.add(PStringUtils.increaseString(KEYSIZE, "=> Summe") + ": " + sumFilms);
            logList.add(" ");
        }

        if (sumFilms == filmlist.size()) {
            // dann werden alle gefunden Filme auch genommen
            // -> gibt keine "blocked"
            return;
        }

        if (!filmsPerChannelUsed.isEmpty()) {
            logList.add(PLog.LILNE3);
            logList.add(" ");
            logList.add("== Filme pro Sender verwendet ==");

            sumFilms = 0;
            filmsPerChannelUsed.keySet().stream().forEach(key -> {
                int found = filmsPerChannelUsed.get(key);
                sumFilms += found;
                logList.add(PStringUtils.increaseString(KEYSIZE, key) + ": " + found);
            });
            logList.add("--");
            logList.add(PStringUtils.increaseString(KEYSIZE, "=> Summe") + ": " + sumFilms);
            logList.add(" ");
        }

        if (!filmsPerChannelBlocked.isEmpty()) {
            logList.add(PLog.LILNE3);
            logList.add(" ");
            logList.add("== nach Sender geblockte Filme ==");

            sumFilms = 0;
            filmsPerChannelBlocked.keySet().stream().forEach(key -> {
                int found = filmsPerChannelBlocked.get(key);
                sumFilms += found;
                logList.add(PStringUtils.increaseString(KEYSIZE, key) + ": " + found);
            });
            logList.add("--");
            logList.add(PStringUtils.increaseString(KEYSIZE, "=> Summe") + ": " + sumFilms);
            logList.add(" ");
        }

        if (!filmsPerDateBlocked.isEmpty()) {
            logList.add(PLog.LILNE3);
            logList.add(" ");
            final int date = ProgConfig.SYSTEM_LOAD_FILMLIST_MAX_DAYS.getValue();
            logList.add("== nach Datum geblockte Filme (max. " + date + " Tage) ==");

            sumFilms = 0;
            filmsPerDateBlocked.keySet().stream().forEach(key -> {
                int found = filmsPerDateBlocked.get(key);
                sumFilms += found;
                logList.add(PStringUtils.increaseString(KEYSIZE, key) + ": " + found);
            });
            logList.add("--");
            logList.add(PStringUtils.increaseString(KEYSIZE, "=> Summe") + ": " + sumFilms);
            logList.add(" ");
        }

        if (!filmsPerDurationBlocked.isEmpty()) {
            logList.add(PLog.LILNE3);
            logList.add(" ");
            final int dur = ProgConfig.SYSTEM_LOAD_FILMLIST_MIN_DURATION.getValue();
            logList.add("== nach Filmlänge geblockte Filme (mind. " + dur + " min.) ==");

            sumFilms = 0;
            filmsPerDurationBlocked.keySet().stream().forEach(key -> {
                int found = filmsPerDurationBlocked.get(key);
                sumFilms += found;
                logList.add(PStringUtils.increaseString(KEYSIZE, key) + ": " + found);
            });
            logList.add("--");
            logList.add(PStringUtils.increaseString(KEYSIZE, "=> Summe") + ": " + sumFilms);
            logList.add(" ");
        }
        PDuration.counterStop("ReadFilmlist.countFoundChannel()");
    }

    private InputStream selectDecompressor(String source, InputStream in) throws Exception {
        if (source.endsWith(ProgConst.FORMAT_XZ)) {
            in = new XZInputStream(in);
        } else if (source.endsWith(ProgConst.FORMAT_ZIP)) {
            final ZipInputStream zipInputStream = new ZipInputStream(in);
            zipInputStream.getNextEntry();
            in = zipInputStream;
        }
        return in;
    }

    private void readData(JsonParser jp, Filmlist filmlist) throws IOException {
        JsonToken jsonToken;
        ArrayList listChannel = LoadFactory.getSenderListNotToLoad();
        final long loadFilmsMaxMilliSeconds = getDaysLoadingFilms();
        final int loadFilmsMinDuration = ProgConfig.SYSTEM_LOAD_FILMLIST_MIN_DURATION.getValue();


        if (jp.nextToken() != JsonToken.START_OBJECT) {
            throw new IllegalStateException("Expected data to start with an Object");
        }

        while ((jsonToken = jp.nextToken()) != null) {
            if (jsonToken == JsonToken.END_OBJECT) {
                break;
            }
            if (jp.isExpectedStartArrayToken()) {
                for (int k = 0; k < FilmlistXml.MAX_ELEM; ++k) {
                    filmlist.metaData[k] = jp.nextTextValue();
                }
                break;
            }
        }
        while ((jsonToken = jp.nextToken()) != null) {
            if (jsonToken == JsonToken.END_OBJECT) {
                break;
            }
            if (jp.isExpectedStartArrayToken()) {
                // sind nur die Feldbeschreibungen, brauch mer nicht
                jp.nextToken();
                break;
            }
        }

        final boolean listChannelIsEmpty = listChannel.isEmpty();
        while (!ProgData.getInstance().loadFilmlist.isStop() && (jsonToken = jp.nextToken()) != null) {
            if (jsonToken == JsonToken.END_OBJECT) {
                break;
            }

            if (jp.isExpectedStartArrayToken()) {
                final FilmData film = new FilmData();
                addValue(film, jp);

                ++countAll;
                countFilm(filmsPerChannelFoundCompleteList, film);

                if (!listChannelIsEmpty && listChannel.contains(film.arr[FilmDataXml.FILM_CHANNEL])) {
                    // diesen Sender nicht laden
                    countFilm(filmsPerChannelBlocked, film);
                    continue;
                }

                film.init(); // damit wird auch das Datum! gesetzt
                if (loadFilmsMaxMilliSeconds > 0 && !checkDate(film, loadFilmsMaxMilliSeconds)) {
                    // wenn das Datum nicht passt, nicht laden
                    countFilm(filmsPerDateBlocked, film);
                    continue;
                }
                if (loadFilmsMinDuration > 0 && !checkDuration(film, loadFilmsMinDuration)) {
                    // wenn das Datum nicht passt, nicht laden
                    countFilm(filmsPerDurationBlocked, film);
                    continue;
                }

                countFilm(filmsPerChannelUsed, film);
                filmlist.importFilmOnlyWithNr(film);
            }

        }

    }

    private void countFilm(Map<String, Integer> map, FilmData film) {
        if (map.containsKey(film.arr[FilmData.FILM_CHANNEL])) {
            map.put(film.arr[FilmData.FILM_CHANNEL], 1 + map.get(film.arr[FilmData.FILM_CHANNEL]));
        } else {
            map.put(film.arr[FilmData.FILM_CHANNEL], 1);
        }
    }

    private void addValue(FilmData film, JsonParser jp) throws IOException {
        for (int i = 0; i < FilmDataXml.JSON_NAMES.length; ++i) {
            String str = jp.nextTextValue();

            switch (FilmDataXml.JSON_NAMES[i]) {
                case FilmDataXml.FILM_NEW:
                    // This value is unused...
                    // datenFilm.arr[DatenFilm.FILM_NEU_NR] = value;
                    film.setNewFilm(Boolean.parseBoolean(str));
                    break;

                case FilmDataXml.FILM_CHANNEL:
                    if (!str.isEmpty()) {
                        channel = str.intern();
                    }
                    film.arr[FilmDataXml.FILM_CHANNEL] = channel;
                    break;

                case FilmDataXml.FILM_THEME:
                    if (!str.isEmpty()) {
                        theme = str.intern();
                    }
                    film.arr[FilmDataXml.FILM_THEME] = theme;
                    break;

                default:
                    film.arr[FilmDataXml.JSON_NAMES[i]] = str;
                    break;
            }

            /// für die Entwicklungszeit
            if (film.arr[FilmDataXml.JSON_NAMES[i]] == null) {
                film.arr[FilmDataXml.JSON_NAMES[i]] = "";
            }

        }
    }

    /**
     * Read a locally available filmlist.
     *
     * @param source   file path as string
     * @param filmlist the list to read to
     */
    private void processFromFile(String source, Filmlist filmlist) {
        notifyProgress(source, ListenerLoadFilmlist.PROGRESS_INDETERMINATE);
        try (InputStream in = selectDecompressor(source, new FileInputStream(source));
             JsonParser jp = new JsonFactory().createParser(in)) {
            readData(jp, filmlist);
        } catch (final FileNotFoundException ex) {
            PLog.errorLog(894512369, "FilmListe existiert nicht: " + source);
            filmlist.clear();
        } catch (final Exception ex) {
            PLog.errorLog(945123641, ex, "FilmListe: " + source);
            filmlist.clear();
        }
    }

    private long getDaysLoadingFilms() {
        final long days = ProgConfig.SYSTEM_LOAD_FILMLIST_MAX_DAYS.getValue();
        if (days > 0) {
            return System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(days, TimeUnit.DAYS);
        } else {
            return 0;
        }
    }

    /**
     * Download a process a filmliste from the web.
     *
     * @param source   source url as string
     * @param filmlist the list to read to
     */
    private void processFromWeb(URL source, Filmlist filmlist) {
        final Request.Builder builder = new Request.Builder().url(source);
        builder.addHeader("User-Agent", ProgInfos.getUserAgent());

        // our progress monitor callback
        final InputStreamProgressMonitor monitor = new InputStreamProgressMonitor() {
            private int oldProgress = 0;

            @Override
            public void progress(long bytesRead, long size) {
                final int iProgress = (int) (bytesRead * 100/* zum Runden */ / size);
                if (iProgress != oldProgress) {
                    oldProgress = iProgress;
                    notifyProgress(source.toString(), 1.0 * iProgress / 100);
                }
            }
        };

        try (Response response = MLHttpClient.getInstance().getHttpClient().newCall(builder.build()).execute();
             ResponseBody body = response.body()) {
            if (body != null && response.isSuccessful()) {

                try (InputStream input = new ProgressMonitorInputStream(body.byteStream(), body.contentLength(), monitor)) {
                    try (InputStream is = selectDecompressor(source.toString(), input);
                         JsonParser jp = new JsonFactory().createParser(is)) {
                        readData(jp, filmlist);
                    }
                }

            }
        } catch (final Exception ex) {
            PLog.errorLog(820147395, ex, "FilmListe: " + source);
            filmlist.clear();
        }
    }

    private boolean checkDate(FilmData film, long loadFilmsLastSeconds) {
        // true wenn der Film angezeigt werden kann!
        try {
            if (film.filmDate.getTime() != 0) {
                if (film.filmDate.getTime() < loadFilmsLastSeconds) {
                    return false;
                }
            }
        } catch (final Exception ex) {
            PLog.errorLog(495623014, ex);
        }
        return true;
    }

    private boolean checkDuration(FilmData film, int loadFilmsMinDuration) {
        // true wenn der Film angezeigt werden kann!
        try {
            if (film.getDurationMinute() != 0) {
                if (film.getDurationMinute() < loadFilmsMinDuration) {
                    return false;
                }
            }
        } catch (final Exception ex) {
            PLog.errorLog(495623014, ex);
        }
        return true;
    }

    private void notifyStart(String url) {
        progress = 0;
        // save download bandwidth
        if (ProgConfig.DOWNLOAD_MAX_BANDWIDTH_KBYTE.getValue() == REDUCED_BANDWIDTH) {
            PLog.sysLog("Bandbreite reduzieren: Ist schon reduziert!!!!");
        } else {
            savedBandwidth = ProgConfig.DOWNLOAD_MAX_BANDWIDTH_KBYTE.getValue();
            PLog.sysLog("Bandbreite zurücksetzen für das Laden der Filmliste von: " + savedBandwidth + " auf " + REDUCED_BANDWIDTH);
            Platform.runLater(() -> {
                ProgConfig.DOWNLOAD_MAX_BANDWIDTH_KBYTE.setValue(REDUCED_BANDWIDTH);
            });
        }


        ProgData.getInstance().loadFilmlist.setStart(
                new ListenerFilmlistLoadEvent(url, "Filmliste downloaden", 0, 0, false));
    }

    private void notifyProgress(String url, double iProgress) {
        progress = iProgress;
        if (progress > ListenerLoadFilmlist.PROGRESS_MAX) {
            progress = ListenerLoadFilmlist.PROGRESS_MAX;
        }
        ProgData.getInstance().loadFilmlist.setProgress(
                new ListenerFilmlistLoadEvent(url, "Filmliste downloaden", progress, 0, false));
    }

    private void notifyFinished(String url) {
        // reset download bandwidth
        PLog.sysLog("Bandbreite wieder herstellen: " + savedBandwidth);
        Platform.runLater(() -> {
            ProgConfig.DOWNLOAD_MAX_BANDWIDTH_KBYTE.setValue(savedBandwidth);
        });

        // Laden ist durch

        ProgData.getInstance().loadFilmlist.setLoaded(
                new ListenerFilmlistLoadEvent("", "Filme verarbeiten",
                        ListenerLoadFilmlist.PROGRESS_INDETERMINATE, 0, false/* Fehler */));
    }
}
