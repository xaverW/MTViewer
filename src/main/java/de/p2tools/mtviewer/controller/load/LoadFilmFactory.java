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

import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.config.ProgInfos;
import de.p2tools.mtviewer.controller.data.film.FilmListMtc;
import de.p2tools.p2lib.mtfilm.loadfilmlist.P2LoadFilmlist;
import de.p2tools.p2lib.mtfilm.tools.LoadFactoryConst;

public class LoadFilmFactory {
    private static P2LoadFilmlist p2LoadFilmlist = null;

    private LoadFilmFactory() {
    }

    public static void loadFilmListProgStart() {
        // Programmstart
        ProgData.FILMLIST_IS_DOWNLOADING.set(true);
        p2LoadFilmlist = new P2LoadFilmlist(ProgData.getInstance().pEventHandler, new FilmListMtc(), new FilmListMtc());
        initLoadFactoryConst(p2LoadFilmlist);
        p2LoadFilmlist.loadFilmlistProgStart();
    }

    public static void loadFilmListButton(boolean alwaysLoadNew) {
        // Button
        ProgData.FILMLIST_IS_DOWNLOADING.set(true);
        p2LoadFilmlist = new P2LoadFilmlist(ProgData.getInstance().pEventHandler, new FilmListMtc(), new FilmListMtc());
        initLoadFactoryConst(p2LoadFilmlist);
        p2LoadFilmlist.loadNewFilmlistFromWeb(alwaysLoadNew);
    }

    public static void setLoadStop() {
        if (p2LoadFilmlist != null) {
            p2LoadFilmlist.setStop(true);
        }
    }

    private static void initLoadFactoryConst(P2LoadFilmlist p2LoadFilmlist) {
        LoadFactoryConst.debug = ProgData.debug;

        LoadFactoryConst.GEO_HOME_PLACE = ProgConfig.SYSTEM_GEO_HOME_PLACE.getValue();
        LoadFactoryConst.SYSTEM_LOAD_NOT_SENDER = ProgConfig.SYSTEM_LOAD_NOT_SENDER.getValue();

        LoadFactoryConst.dateStoredFilmlist = ProgConfig.SYSTEM_FILMLIST_DATE.getValue();
        LoadFactoryConst.firstProgramStart = ProgData.firstProgramStart;
        LoadFactoryConst.localFilmListFile = ProgInfos.getFilmListFile();
        LoadFactoryConst.loadNewFilmlistOnProgramStart = ProgConfig.SYSTEM_LOAD_FILMS_ON_START.getValue();
        LoadFactoryConst.SYSTEM_LOAD_FILMLIST_MAX_DAYS = ProgConfig.SYSTEM_LOAD_FILMLIST_MAX_DAYS.getValue();
        LoadFactoryConst.SYSTEM_LOAD_FILMLIST_MIN_DURATION = ProgConfig.SYSTEM_LOAD_FILMLIST_MIN_DURATION.getValue();
        LoadFactoryConst.removeDiacritic = ProgConfig.SYSTEM_REMOVE_DIACRITICS.getValue();
        LoadFactoryConst.userAgent = ProgConfig.SYSTEM_USERAGENT.getValue();
        LoadFactoryConst.filmlist = ProgData.getInstance().filmlist;

        LoadFactoryConst.p2LoadFilmlist = p2LoadFilmlist;
        LoadFactoryConst.primaryStage = ProgData.getInstance().primaryStage;
        LoadFactoryConst.filmListUrl = ProgData.filmListUrl;
    }
}
