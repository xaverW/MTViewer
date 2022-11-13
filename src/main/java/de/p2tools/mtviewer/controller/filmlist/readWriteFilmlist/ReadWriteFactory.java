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


package de.p2tools.mtviewer.controller.filmlist.readWriteFilmlist;

//!! wird zum Laden/Schreiben der Filmliste gebraucht !!
// {"Filmliste":["13.11.2022, 10:18","13.11.2022, 09:18","3","MSearch [Vers.: 3.1.199]","99d19c1ceff0628c7c453a5bd544a38f"],

// "Filmliste":["Sender","Thema","Titel",
// "Datum","Zeit","Dauer","Größe [MB]",

// "Beschreibung","Url","Website","Url Untertitel",
// "Url RTMP","Url Klein","Url RTMP Klein","Url HD","Url RTMP HD",
// "DatumL","Url History","Geo","neu"]

public class ReadWriteFactory {
    public static final int JSON_NAMES_CHANNEL = 0;
    public static final int JSON_NAMES_THEME = 1;
    public static final int JSON_NAMES_TITLE = 2;
    public static final int JSON_NAMES_DATE = 3;
    public static final int JSON_NAMES_TIME = 4;
    public static final int JSON_NAMES_DURATION = 5;
    public static final int JSON_NAMES_SIZE = 6;
    public static final int JSON_NAMES_DESCRIPTION = 7;
    public static final int JSON_NAMES_URL = 8;
    public static final int JSON_NAMES_WEBSITE = 9;
    public static final int JSON_NAMES_URL_SUBTITLE = 10;
    public static final int JSON_NAMES_URL_RTMP = 11;
    public static final int JSON_NAMES_URL_SMALL = 12;
    public static final int JSON_NAMES_URL_RTMP_SMALL = 13;
    public static final int JSON_NAMES_URL_HD = 14;
    public static final int JSON_NAMES_URL_RTMP_HD = 15;
    public static final int JSON_NAMES_DATE_LONG = 16;
    public static final int JSON_NAMES_URL_HISTORY = 17;
    public static final int JSON_NAMES_GEO = 18;
    public static final int JSON_NAMES_NEW = 19;

    public static final int MAX_JSON_NAMES = 20;
    public static final String TAG_JSON_LIST = "X";
    public static final String[] JSON_NAMES = {
            "Sender",
            "Thema",
            "Titel",

            "Datum",
            "Zeit",
            "Dauer [min]",
            "Größe [MB]",

            "Beschreibung",
            "Url",
            "Website",
            "Url Untertitel",

            "Url RTMP",
            "Url Klein",
            "Url RTMP Klein",
            "Url HD",
            "Url RTMP HD",

            "DatumL",
            "Url History",
            "Geo",
            "neu",
    };

    private ReadWriteFactory() {
    }
}
