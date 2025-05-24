/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
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

package de.p2tools.mtviewer.controller.data.film;

import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.p2lib.mtfilm.film.FilmData;
import de.p2tools.p2lib.mtfilm.film.FilmDataProps;
import de.p2tools.p2lib.mtfilm.film.FilmDataXml;
import de.p2tools.p2lib.tools.duration.P2Duration;
import javafx.beans.property.ListProperty;

import java.util.Arrays;
import java.util.HashSet;

public class FilmToolsFactory {
    private static int countDouble = 0;

    private FilmToolsFactory() {
    }

    public static int markFilms(ListProperty<? extends FilmData> filmList) {
        // läuft direkt nach dem Laden der Filmliste!
        // doppelte Filme (URL), Geo, InFuture markieren
        // viele Filme sind bei mehreren Sendern vorhanden

        String[] senderArr = {"ARD,ZDF"};
        final HashSet<String> urlHashSet = new HashSet<>(filmList.size(), 0.75F);
        countDouble = 0;

        P2Duration.counterStart("markFilms");
        filmList.forEach((FilmData f) -> {
            mark(f);
        });
        for (String sender : senderArr) {
            addSender(filmList, urlHashSet, senderArr, sender);
        }
        // und dann noch für den Rest
        addSender(filmList, urlHashSet, senderArr, "");
        urlHashSet.clear();

        if (ProgConfig.SYSTEM_FILMLIST_REMOVE_DOUBLE.getValue()) {
            // dann auch gleich noch entfernen
            filmList.removeIf(FilmDataProps::isDoubleUrl);
        }
        ProgConfig.SYSTEM_FILMLIST_COUNT_DOUBLE.setValue(countDouble);

        P2Duration.counterStop("markFilms");
        return countDouble;
    }

    private static void mark(FilmData filmData) {
        filmData.setGeoBlocked();
        filmData.setInFuture();
    }

    private static String getHashFromFilm(FilmData filmData) {
        if (ProgConfig.SYSTEM_FILMLIST_DOUBLE_WITH_THEME_TITLE.get()) {
            return filmData.getTheme() + filmData.getTitle() + filmData.getUrl();
        } else {
            return filmData.getUrl();
        }
    }

    private static void addSender(ListProperty<? extends FilmData> filmList,
                                  HashSet<String> urlHashSet,
                                  String[] senderArr, String sender) {

        filmList.forEach((FilmData f) -> {
            if (sender.isEmpty()) {
                // dann nur noch die Sender die nicht im senderArr sind
                if (Arrays.stream(senderArr).noneMatch(s -> s.equals(f.arr[FilmDataXml.FILM_CHANNEL]))) {
                    if (!urlHashSet.add(getHashFromFilm(f))) {
                        ++countDouble;
                        f.setDoubleUrl(true);
                    }
                }

            } else if (f.arr[FilmDataXml.FILM_CHANNEL].equals(sender)) {
                // jetzt erst mal die Sender aus dem Array
                if (!urlHashSet.add(getHashFromFilm(f))) {
                    ++countDouble;
                    f.setDoubleUrl(true);
                }
            }
        });
    }
}
