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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import de.p2tools.mtviewer.controller.load.LoadAudioFactoryDto;
import de.p2tools.p2lib.atdata.AudioDataXml;
import de.p2tools.p2lib.atdata.AudioList;
import de.p2tools.p2lib.mtfilm.film.FilmData;
import de.p2tools.p2lib.mtfilm.film.FilmDataXml;
import de.p2tools.p2lib.mtfilm.film.Filmlist;
import de.p2tools.p2lib.tools.log.P2Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ReadWebAudioToFilmListJson {

    private String channel = "", genre = "", theme = "";

    public ReadWebAudioToFilmListJson() {
    }

    public void readAudioData(JsonParser jp, Filmlist<FilmData> filmList) throws IOException {

        final long loadFilmsMaxMilliSeconds = getDaysLoadingFilms();
        final int loadFilmsMinDuration = LoadAudioFactoryDto.SYSTEM_LOAD_MIN_DURATION;

        JsonToken jsonToken;
        if (jp.nextToken() != JsonToken.START_OBJECT) {
            throw new IllegalStateException("Expected data to start with an Object");
        }

        while ((jsonToken = jp.nextToken()) != null) {
            if (jsonToken == JsonToken.END_OBJECT) {
                break;
            }
            if (jp.isExpectedStartArrayToken()) {
                for (int k = 0; k < AudioList.AUDIO_LIST_META_MAX_ELEM; ++k) {
                    filmList.metaData[k] = jp.nextTextValue();
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

        while ((jsonToken = jp.nextToken()) != null) {
            if (jsonToken == JsonToken.END_OBJECT) {
                break;
            }

            if (jp.isExpectedStartArrayToken()) {
                final FilmData audioData = filmList.getNewElement();
                try {
                    addValue(audioData, jp);

                    // aus dem Web muss das immer gemacht werden
                    audioData.init(); // damit wird auch das Datum! gesetzt

                    //und jetzt wird gefiltert, wenn aus dem Web
                    if (loadFilmsMaxMilliSeconds > 0 && !checkDays(audioData, loadFilmsMaxMilliSeconds)) {
                        //wenn er zu alt ist, nicht laden
                        continue;
                    }
                    if (loadFilmsMinDuration > 0 && !checkDuration(audioData, loadFilmsMinDuration)) {
                        //wenn er zu kurz ist, nicht laden
                        continue;
                    }

                    filmList.add(audioData);
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
        }
    }

    private void addValue(FilmData filmData, JsonParser jp) throws IOException {
        for (int i = 0; i < AudioDataXml.JSON_MAX_ELEM; ++i) {
            String str = jp.nextTextValue();

            switch (i) {
                case AudioDataXml.JSON_AUDIO_CHANNEL:
                    if (!str.isEmpty()) {
                        channel = str.intern();
                    }
                    filmData.arr[FilmDataXml.FILM_CHANNEL] = channel;
                    break;
//                case AudioDataXml.JSON_AUDIO_GENRE:
//                    if (!str.isEmpty()) {
//                        genre = str.intern();
//                    }
//                    filmData.arr[FilmDataXml.AUDIO_GENRE] = genre;
//                    break;
                case AudioDataXml.JSON_AUDIO_THEME:
                    if (!str.isEmpty()) {
                        theme = str.intern();
                    }
                    filmData.arr[FilmDataXml.FILM_THEME] = theme;
                    break;
                case AudioDataXml.JSON_AUDIO_TITLE:
                    filmData.arr[FilmDataXml.FILM_TITLE] = str;
                    break;

                case AudioDataXml.JSON_AUDIO_DATE:
                    filmData.arr[FilmDataXml.FILM_DATE] = str;
                    break;
                case AudioDataXml.JSON_AUDIO_TIME:
                    filmData.arr[FilmDataXml.FILM_TIME] = str + ":00";
                    break;
                case AudioDataXml.JSON_AUDIO_DURATION:
                    filmData.arr[FilmDataXml.FILM_DURATION] = str;
                    break;
                case AudioDataXml.JSON_AUDIO_SIZE_MB:
                    filmData.arr[FilmDataXml.FILM_SIZE] = str;
                    break;
                case AudioDataXml.JSON_AUDIO_DESCRIPTION:
                    filmData.arr[FilmDataXml.FILM_DESCRIPTION] = str;
                    break;

                case AudioDataXml.JSON_AUDIO_URL:
                    filmData.arr[FilmDataXml.FILM_URL] = str;
                    break;
                case AudioDataXml.JSON_AUDIO_WEBSITE:
                    filmData.arr[FilmDataXml.FILM_WEBSITE] = str;
                    break;
                case AudioDataXml.JSON_AUDIO_NEW:
                    filmData.arr[FilmDataXml.FILM_NEW] = str;
                    break;
//                case AudioDataXml.JSON_AUDIO_PODCAST:
//                    filmData.arr[FilmDataXml.AUDIO_PODCAST] = str;
//                    break;
//                case AudioDataXml.JSON_AUDIO_DOUBLE:
//                    filmData.arr[FilmDataXml.AUDIO_DOUBLE] = str;
//                    break;
            }
        }
    }

    private boolean checkDays(FilmData film, long loadFilmsLastMilliSeconds) {
        // true, wenn der Film angezeigt werden kann!
        try {
            if (film.filmDate.getTime() != 0) {
                if (film.filmDate.getTime() < loadFilmsLastMilliSeconds) {
                    //dann ist er zu alt
                    return false;
                }
            }
        } catch (final Exception ex) {
            P2Log.errorLog(495623014, ex);
        }
        return true;
    }

    private boolean checkDuration(FilmData film, int loadFilmsMinDuration) {
        //true, wenn der Film angezeigt werden kann!
        try {
            if (film.getDurationMinute() != 0) {
                if (film.getDurationMinute() < loadFilmsMinDuration) {
                    //dann ist er zu kurz
                    return false;
                }
            }
        } catch (final Exception ex) {
            P2Log.errorLog(495623014, ex);
        }
        return true;
    }

    private long getDaysLoadingFilms() {
        final long days = LoadAudioFactoryDto.SYSTEM_LOAD_MAX_DAYS;
        if (days > 0) {
            return System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(days, TimeUnit.DAYS);
        } else {
            return 0;
        }
    }
}