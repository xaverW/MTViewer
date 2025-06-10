/*
 * P2tools Copyright (C) 2022 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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


package de.p2tools.mtviewer.controller.load;

import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.data.film.FilmListMtc;
import de.p2tools.p2lib.mtfilm.film.FilmData;
import de.p2tools.p2lib.mtfilm.film.Filmlist;
import de.p2tools.p2lib.mtfilm.loadfilmlist.P2LoadConst;
import de.p2tools.p2lib.mtfilm.loadfilmlist.P2LoadFilmlist;

public class LoadFilmFactory {
    private static P2LoadFilmlist p2LoadFilmlist = null;

    private LoadFilmFactory() {
    }

    public static void loadFilmListProgStart() {
        // Programmstart
        ProgData.FILMLIST_IS_DOWNLOADING.set(true);
        Filmlist<FilmData> filmlistNew = new FilmListMtc();
        Filmlist<FilmData> filmlistDiff = new FilmListMtc();
        p2LoadFilmlist = new P2LoadFilmlist(ProgData.getInstance().pEventHandler, filmlistNew, filmlistDiff);
        LoadFactory.initLoadFactoryConst();
        p2LoadFilmlist.loadFilmlistProgStart();
    }

    public static void loadFilmListButton(boolean alwaysLoadNew) {
        // Button
        ProgData.FILMLIST_IS_DOWNLOADING.set(true);
        Filmlist<FilmData> filmlistNew = new FilmListMtc();
        Filmlist<FilmData> filmlistDiff = new FilmListMtc();
        p2LoadFilmlist = new P2LoadFilmlist(ProgData.getInstance().pEventHandler, filmlistNew, filmlistDiff);
        LoadFactory.initLoadFactoryConst();
        p2LoadFilmlist.loadNewFilmlistFromWeb(alwaysLoadNew);
    }

    public static void setLoadStop() {
        if (p2LoadFilmlist != null) {
            P2LoadConst.stop.set(true);
        }
    }
}
