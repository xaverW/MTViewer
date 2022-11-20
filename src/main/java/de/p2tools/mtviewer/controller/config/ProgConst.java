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

package de.p2tools.mtviewer.controller.config;

public class ProgConst {

    public static final String PROGRAM_NAME = "MTViewer";
    public static final String USER_AGENT_DEFAULT = "";
    public static final int MAX_USER_AGENT_SIZE = 100;

    // settings file
    public static final String CONFIG_FILE = "mtv.xml";
    public static final String CONFIG_FILE_OLD = "mtv.xml";
    public static final String STYLE_FILE = "style.css";
    public static final String CONFIG_FILE_COPY = "mtv.xml_copy_";
    public static final String CONFIG_DIRECTORY = "p2Mtviewer"; // im Homeverzeichnis
    public static final String XML_START = "Mediathek";

    public static final String LOG_DIR = "Log";
    public static final String CSS_FILE = "de/p2tools/mtviewer/mtfx.css";
    public static final String CSS_FILE_DARK_THEME = "de/p2tools/mtviewer/mtfx-dark.css";

//    public static final String FORMAT_ZIP = ".zip";
//    public static final String FORMAT_XZ = ".xz";

    public static final int SYSTEM_LOAD_FILMLIST_MAX_DAYS = 300; // Filter beim Programmstart/Blacklist: nur Filme der letzten xx Tage laden
    public static final int SYSTEM_LOAD_FILMLIST_MIN_DURATION = 30; // Filter Programmstart: nur Filme mit mind. xx Minuten länge laden

    public static final int SYSTEM_FILTER_MAX_WAIT_TIME = 2_000; // 1.000 ms


    // prüfen ob es eine neue Filmliste gibt: alle ... Min. oder ... nach dem Programmstart
    public static final int CHECK_FILMLIST_UPDATE = 30 * 60; // 30 Minuten

//    // beim Programmstart wird die Liste geladen wenn sie älter ist als ..
//    public static final int ALTER_FILMLISTE_SEKUNDEN_FUER_AUTOUPDATE = 4 * 60 * 60;

//    // Uhrzeit ab der die Diffliste alle Änderungen abdeckt, die Filmliste darf also nicht vor xx erstellt worden sein
//    public static final String TIME_MAX_AGE_FOR_DIFF = "09";

//    // MediathekView URLs
//    public static final String ADRESSE_FILMLISTEN_SERVER_DIFF = "http://res.mediathekview.de/diff.xml";
//    public static final String ADRESSE_FILMLISTEN_SERVER_AKT = "http://res.mediathekview.de/akt.xml";

    // Website MTViewer
    public static final String URL_WEBSITE = "https://www.p2tools.de";
    public static final String URL_WEBSITE_MTVIEWER = "https://www.p2tools.de/mtviewer/";
    public static final String URL_WEBSITE_DOWNLOAD = "https://www.p2tools.de/mtviewer/download.html";
    public static final String URL_WEBSITE_HELP = "https://www.p2tools.de/mtviewer/manual/";

    // ProgrammUrls
    public static final String ADRESSE_WEBSITE_VLC = "https://www.videolan.org";
    public static final String ADRESSE_WEBSITE_ffmpeg = "https://ffmpeg.org/";

    public static final String FILE_PROG_ICON = "/de/p2tools/mtviewer/res/P2.png";

    // Dateien/Verzeichnisse
    public static final String JSON_DATEI_FILME = "filme.json";

    public static final double GUI_FILME_DIVIDER_LOCATION = 0.7;

    public final static int MAX_COPY_OF_BACKUPFILE = 5; // Maximum number of backup files to be stored.

    public static final int MIN_TABLE_HEIGHT = 200;
    public static final int MIN_TEXTAREA_HEIGHT_LOW = 50;

    public static final String FILTER_ALL = "alles"; // im config bei "alles" steht das dann
    public static final int MAX_SENDER_FILME_LADEN = 2;
    public static final int MIN_DATEI_GROESSE_FILM = 256 * 1000;
    public static final int MAX_DEST_PATH_IN_DIALOG_DOWNLOAD = 10;
    public static final double GUI_DOWNLOAD_FILTER_DIVIDER_LOCATION = 0.3;
    public static final int LAENGE_DATEINAME_MAX = 200; // Standardwert für die Länge des Zieldateinamens
    public static final int LAENGE_FELD_MAX = 100; // Standardwert für die Länge des Feldes des


//    public static final String DREISAT = "3Sat";
//    public static final String ARD = "ARD";
//
//    public static final String ARTE_DE = "ARTE.DE";
//    public static final String ARTE_EN = "ARTE.EN";
//    public static final String ARTE_ES = "ARTE.ES";
//    public static final String ARTE_FR = "ARTE.FR";
//    public static final String ARTE_IT = "ARTE.IT";
//    public static final String ARTE_PL = "ARTE.PL";
//
//    public static final String BR = "BR";
//    public static final String DW = "DW";
//    public static final String HR = "HR";
//    public static final String KIKA = "KiKA";
//    public static final String MDR = "MDR";
//    public static final String NDR = "NDR";
//    public static final String ORF = "ORF";
//    public static final String PHOENIX = "PHOENIX";
//    public static final String RBB = "RBB";
//    public static final String RBB_TV = "rbtv";
//    public static final String SR = "SR";
//    public static final String SRF = "SRF";
//    public static final String SRF_PODCAST = "SRF.Podcast";
//    public static final String SWR = "SWR";
//    public static final String WDR = "WDR";
//    public static final String ZDF = "ZDF";
//    public static final String ZDF_TIVI = "ZDF-tivi";
//
//    public static final String[] SENDER = {DREISAT, ARD,
//            ARTE_DE, ARTE_EN, ARTE_ES, ARTE_FR, ARTE_IT, ARTE_PL,
//            BR, DW, HR, KIKA, MDR, NDR, ORF, PHOENIX, RBB, RBB_TV, SR,
//            SRF, SRF_PODCAST, SWR, WDR, ZDF, ZDF_TIVI};
}
