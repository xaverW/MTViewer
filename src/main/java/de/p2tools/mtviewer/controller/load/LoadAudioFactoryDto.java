/*
 * P2Tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
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

import de.p2tools.mtviewer.controller.load.loadaudiolist.LoadAudioList;
import de.p2tools.p2lib.mtfilm.film.FilmData;
import de.p2tools.p2lib.mtfilm.film.Filmlist;
import javafx.beans.property.StringProperty;
import javafx.stage.Stage;

import java.util.HashSet;

public class LoadAudioFactoryDto {
    public static boolean debug = false;
    public static StringProperty audioListDate;
    public static boolean firstProgramStart = false;
    public static String localFilmListFile = "";
    public static boolean loadNewAudioListOnProgramStart = true;

    public static int SYSTEM_LOAD_MAX_DAYS = 0;
    public static int SYSTEM_LOAD_MIN_DURATION = 0;
    public static boolean removeDiacritic = false;

    public static Filmlist<FilmData> audioListAkt;
    public static Filmlist<FilmData> audioListNew;

    public static String userAgent = "";
    public static LoadAudioList loadAudiolist;
    public static HashSet<String> hashSet = new HashSet<>();
    public static Stage primaryStage = null;
}
