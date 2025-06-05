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

package de.p2tools.mtviewer.controller.load.loadaudiolist;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.load.LoadAudioFactoryDto;
import de.p2tools.p2lib.atdata.P2AudioFactory;
import de.p2tools.p2lib.atdata.P2AudioListFactory;
import de.p2tools.p2lib.mtdownload.MLHttpClient;
import de.p2tools.p2lib.mtfilm.film.FilmData;
import de.p2tools.p2lib.mtfilm.film.Filmlist;
import de.p2tools.p2lib.mtfilm.film.FilmlistFactory;
import de.p2tools.p2lib.mtfilm.film.FilmlistXml;
import de.p2tools.p2lib.mtfilm.readwritefilmlist.P2ReadFilmlist;
import de.p2tools.p2lib.mtfilm.readwritefilmlist.P2WriteFilmlistJson;
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
import org.apache.commons.lang3.time.FastDateFormat;
import org.tukaani.xz.XZInputStream;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.zip.ZipInputStream;

public class ReadAudioList {

    private List<String> logList = new ArrayList<>();
    private static int countDouble = 0;

    public ReadAudioList() {
    }

    public boolean readLocalList(Path path) {
        // beim Programmstart wird die gespeicherte Liste geladen
        boolean ret;
        P2Duration.counterStart("readDb");

        try {
            if (!Files.exists(path) || path.toFile().length() == 0) {
                return false;
            }

            LoadAudioFactoryDto.audioListAkt.clear();
            LoadAudioFactoryDto.audioListNew.clear();
            logList.add("Audioliste lesen");
            logList.add("   --> Lesen von: " + path);
            new P2ReadFilmlist().readFilmlistWebOrLocal(logList, LoadAudioFactoryDto.audioListNew, path.toString());
            setDateFromLocal();

            logList.add("## Filme markieren");
            final int count = LoadAudioFactoryDto.audioListNew.markFilms();

            logList.add("## Anzahl doppelte Filme: " + count);
            LoadAudioFactoryDto.audioListNew.loadSender();

            logList.add("##   Audioliste gelesen, OK");
            logList.add("##   Anzahl gelesen: " + LoadAudioFactoryDto.audioListNew.size());
            ret = true;

        } catch (final Exception ex) {
            logList.add("##   Audioliste lesen hat nicht geklappt");
            P2Log.errorLog(645891204, ex);
            ret = false;
        }
        return ret;
    }

    public boolean readWebList(Path path) {
        boolean ret;
        P2Duration.counterStart("readWebList");

        try {
            // Hash füllen
            fillHash(logList, LoadAudioFactoryDto.audioListAkt);
            fillHash(logList, LoadAudioFactoryDto.audioListNew);
            LoadAudioFactoryDto.audioListAkt.clear();
            LoadAudioFactoryDto.audioListNew.clear();

            //dann aus dem Web mit der URL laden
            logList.add("## Audioliste aus URL laden: " + de.p2tools.p2lib.atdata.P2AudioFactory.AUDIOLIST_URL);
            processFromWeb(new URL(P2AudioFactory.AUDIOLIST_URL), LoadAudioFactoryDto.audioListNew);

            if (LoadAudioFactoryDto.audioListNew.isEmpty()) {
                // dann hats nicht geklappt
                ret = false;

            } else {
                setDateFromWeb();
                // unerwünschte löschen
                removeUnwanted(logList, LoadAudioFactoryDto.audioListNew);
                // neue Filme markieren
                markNewFilms(logList, LoadAudioFactoryDto.audioListNew);
                markDoubleAudios(logList, LoadAudioFactoryDto.audioListNew); // todo löschen??

                // und dann auch speichern
                logList.add("##");
                logList.add("## Audioliste schreiben (" + LoadAudioFactoryDto.audioListNew.size() + " Audios) :");
                logList.add("##    --> Start Schreiben nach: " + path);
                new P2WriteFilmlistJson().write(path.toString(), LoadAudioFactoryDto.audioListNew);
                logList.add("##    --> geschrieben!");
                logList.add("##");

                ret = true;
            }
        } catch (final Exception ex) {
            logList.add("##   Audioliste lesen hat nicht geklappt");
            P2Log.errorLog(645891204, ex);
            ret = false;
        }

        P2Log.sysLog(logList);
        P2Duration.counterStop("readWebList");
        return ret;
    }

    private void setDateFromLocal() {
        // Datum setzen, Format stimmt da ja schon!
        String date = FilmlistFactory.genDate(LoadAudioFactoryDto.audioListNew.metaData);
        LoadAudioFactoryDto.audioListDate.set(date);
    }

    private void setDateFromWeb() {
        // Datum setzen
        LocalDateTime date = P2AudioListFactory.getDate(LoadAudioFactoryDto.audioListNew.metaData);
        String dateStr = P2LDateTimeFactory.toString(date, P2DateConst.DT_FORMATTER__FILMLIST);
        LoadAudioFactoryDto.audioListDate.set(dateStr);
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

        // meta korrigieren
        changeMeta(audioList.metaData);
    }

    private static void changeMeta(String[] metaData) {
        // audiothek: "AudioList" : [ "22.05.2025 06:59:17", "22.05.2025 08:59:17" ]
        // audiolist: metaData = new String[]{"GMT", "LocalDate"}; // AudioDataXml.AUDIO_LIST_META_MAX_ELEM

        // mediathek: "Filmliste" : [ "22.05.2025, 10:32", "22.05.2025, 08:32", "3", "MSearch [Vers.: 3.1.255]", "a34202f7a2c9077f7b654e878c11a784" ],
        // FILMLIST_DATE_NR = 0;
        // FILMLIST_DATE_GMT_NR = 1;

        final String DATE_TIME_FORMAT_MEDIATHEK = "dd.MM.yyyy, HH:mm";
        final String DATE_TIME_FORMAT_AUDIOTHEK = "dd.MM.yyyy HH:mm:ss";
        try {
            final SimpleDateFormat sdf_audiothek = new SimpleDateFormat(DATE_TIME_FORMAT_AUDIOTHEK);

            String date = metaData[1];
            String dateGmt = metaData[0];
            Date filmDate = sdf_audiothek.parse(date);
            Date filmDateGmt = sdf_audiothek.parse(dateGmt);

            metaData[FilmlistXml.FILMLIST_DATE_GMT_NR] = FastDateFormat.getInstance(DATE_TIME_FORMAT_MEDIATHEK).format(filmDateGmt);
            metaData[FilmlistXml.FILMLIST_DATE_NR] = FastDateFormat.getInstance(DATE_TIME_FORMAT_MEDIATHEK).format(filmDate);
        } catch (Exception ex) {
            P2Log.errorLog(965874548, ex, "GenDateTime: Audiolist");

            SimpleDateFormat sdf_mediathek = new SimpleDateFormat(DATE_TIME_FORMAT_MEDIATHEK);
            SimpleDateFormat sdfUtf_mediathek = new SimpleDateFormat(DATE_TIME_FORMAT_MEDIATHEK);
            sdfUtf_mediathek.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
            String d = sdf_mediathek.format(new Date());
            String dUtf = sdfUtf_mediathek.format(new Date());
            metaData[FilmlistXml.FILMLIST_DATE_NR] = d;
            metaData[FilmlistXml.FILMLIST_DATE_GMT_NR] = dUtf;
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
