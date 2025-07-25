/*
 * P2tools Copyright (C) 2018 W. Xaver W.Xaver[at]googlemail.com
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


package de.p2tools.mtviewer.controller.config;


import de.p2tools.p2lib.colordata.P2ColorData;
import de.p2tools.p2lib.colordata.P2ColorList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import java.util.Comparator;
import java.util.List;

public class ProgColorList extends P2ColorList {

    // Tabelle Filme
    public static final P2ColorData FILM_LIVESTREAM = addNewKey("COLOR_FILM_LIVESTREAM",
            Color.rgb(130, 0, 0), Color.rgb(100, 0, 0), "Tabelle Filme, Livestreams");
    public static final P2ColorData FILM_NEW = addNewKey("COLOR_FILM_NEW",
            Color.rgb(0, 0, 240), Color.rgb(0, 0, 240), "Tabelle Filme, neue");
    public static final P2ColorData FILM_GEOBLOCK = addNewKey("COLOR_FILM_GEOBLOCK_BACKGROUND",
            Color.rgb(255, 168, 0), Color.rgb(236, 153, 0), "Tabelle Filme, geogeblockt");

    // Filter wenn RegEx
    public static final P2ColorData ERROR = new P2ColorData("COLOR_ERROR", Color.rgb(255, 233, 233), Color.rgb(163, 82, 82));
    // DialogDownload
    public static final P2ColorData DOWNLOAD_NAME_ERROR = new P2ColorData("COLOR_DOWNLOAD_NAME_ERROR",
            Color.rgb(255, 233, 233), Color.rgb(200, 183, 183));

    // Tabelle Downloads
    public static final P2ColorData DOWNLOAD_WAIT = addNewKey("COLOR_DOWNLOAD_WAIT",
            Color.rgb(239, 244, 255), Color.rgb(99, 100, 105), "Tabelle Download, noch nicht gestartet");
    public static final P2ColorData DOWNLOAD_RUN = addNewKey("COLOR_DOWNLOAD_RUN",
            Color.rgb(255, 245, 176), Color.rgb(174, 150, 85), "Tabelle Download, läuft");
    public static final P2ColorData DOWNLOAD_FINISHED = addNewKey("COLOR_DOWNLOAD_FINISHED",
            Color.rgb(206, 255, 202), Color.rgb(79, 129, 74), "Tabelle Download, fertig");
    public static final P2ColorData DOWNLOAD_ERROR = addNewKey("COLOR_DOWNLOAD_ERROR", Color.rgb(255, 233, 233), Color.rgb(163, 82, 82), "Tabelle Download, fehlerhaft");

    //=======================================================================================
    //Liste der Schrift-Farben -> Rest sind Hintergrundfarben
    public static final List<P2ColorData> FRONT_COLOR = List.of(FILM_LIVESTREAM, FILM_NEW, FILM_GEOBLOCK);

    private ProgColorList() {
        super();
    }

    public static void setColorTheme() {
        final boolean dark = ProgConfig.SYSTEM_DARK_THEME.get();
        for (int i = 0; i < getInstance().size(); ++i) {
            getInstance().get(i).setColorTheme(dark);
        }
    }

    public static ObservableList<P2ColorData> getColorListFront() {
        ObservableList<P2ColorData> list = FXCollections.observableArrayList();
        ObservableList<P2ColorData> pColorData = getInstance();
        pColorData.stream().filter(pc -> FRONT_COLOR.contains(pc)).forEach(pc -> list.add(pc));

        Comparator<P2ColorData> comparator = Comparator.comparing(P2ColorData::getText);
        FXCollections.sort(list, comparator);

        return list;
    }

    public static ObservableList<P2ColorData> getColorListBackground() {
        ObservableList<P2ColorData> list = FXCollections.observableArrayList();
        ObservableList<P2ColorData> pColorData = getInstance();
        pColorData.stream().filter(pc -> !FRONT_COLOR.contains(pc)).forEach(pc -> list.add(pc));

        Comparator<P2ColorData> comparator = Comparator.comparing(P2ColorData::getText);
        FXCollections.sort(list, comparator);

        return list;
    }

    public synchronized static P2ColorList getInstance() {
        return P2ColorList.getInst();
    }
}
