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

package de.p2tools.mtviewer.tools.filmFilter;

import de.p2tools.mtviewer.controller.config.ProgConst;

public class FilmFilterToXml {

    public static final String SELECTED_FILTER_NAME = "Name";
    public static final String SELECTED_FILTER_CHANNEL = "Sender";
    //    public static final String SELECTED_FILTER_THEMA_EXACT = "Thema-exakt";
//    public static final String SELECTED_FILTER_THEMA = "Thema";
    public static final String SELECTED_FILTER_THEME = "Thema";
    public static final String SELECTED_FILTER_TITLE = "Titel";
    public static final String SELECTED_FILTER_SOMEWHERE = "Irgendwo";

    public static final String SELECTED_FILTER_TIME_RANGE = "Tage";
    public static final String SELECTED_FILTER_ONLY_NEW = "nur-neu";
    public static final String SELECTED_FILTER_ONLY_LIVE = "nur-live";

    public static final int FILTER_NAME = 0;
    public static final int FILTER_SENDER = 1;
    //    public static final int FILTER_THEME_EXACT = 2;
//    public static final int FILTER_THEME = 3;
    public static final int FILTER_THEME = 2;
    public static final int FILTER_TITLE = 3;
    public static final int FILTER_SOMEWHERE = 4;

    public static final int FILTER_TIME_RANGE = 5;
    public static final int FILTER_ONLY_NEW = 6;
    public static final int FILTER_ONLY_LIVE = 7;

    public static final String[] XML_NAMES = {"Name",
            "Sender",
//            "Thema-exakt",
//            "Thema",
            "Thema",
            "Titel",
            "Irgendwo",

            "Tage",
            "nur-neu",
            "nur-live"
    };

    public static final String TAG = "Filter-Film";

    public static String[] getEmptyArray() {
        final String[] array = new String[XML_NAMES.length];
        for (int i = 0; i < array.length; ++i) {
            array[i] = "";
        }
        return array;
    }

    public static void setValueArray(FilmFilter sf, String[] array) {
        // fürs Einselesen aus dem Configfile

        sf.setName(array[FILTER_NAME]);
        sf.setChannel(array[FILTER_SENDER]);
//        sf.setThemeExact(Boolean.parseBoolean(array[FILTER_THEME_EXACT]));
//        sf.setTheme(array[FILTER_THEME]);
        sf.setTheme(array[FILTER_THEME]);
        sf.setTitle(array[FILTER_TITLE]);
        sf.setSomewhere(array[FILTER_SOMEWHERE]);
        sf.setOnlyNew(Boolean.parseBoolean(array[FILTER_ONLY_NEW]));
        sf.setOnlyLive(Boolean.parseBoolean(array[FILTER_ONLY_LIVE]));

        parsInt(sf, array);
    }

    private static void parsInt(FilmFilter sf, String[] array) {
        // filter days
        if (array[FILTER_TIME_RANGE].equals(ProgConst.FILTER_ALL)) {
            sf.setTimeRange(FilmFilterCheck.FILTER_TIME_RANGE_ALL_VALUE);
        } else {
            try {
                sf.setTimeRange(Integer.parseInt(array[FILTER_TIME_RANGE]));
            } catch (Exception ex) {
                sf.setTimeRange(FilmFilterCheck.FILTER_TIME_RANGE_ALL_VALUE);
            }
        }
    }

    public static String[] getXmlArray() {
        // erstellt die XML-Namen fürs Lesen/Schreiben aus/ins Configfile
        final String[] array = getEmptyArray();
        for (int i = 0; i < XML_NAMES.length; ++i) {
            array[i] = XML_NAMES[i];
        }
        return array;
    }
}
