/*
 * P2tools Copyright (C) 2021 W. Xaver W.Xaver[at]googlemail.com
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


package de.p2tools.mtviewer.controller.tips;

import java.util.ArrayList;
import java.util.List;

public class TipListFilm {

    private TipListFilm() {
    }

    public static List<TipData> getTips() {
        List<TipData> pToolTipList = new ArrayList<>();

        String text = "Hier wird die Liste " +
                "aller Filme und Audios angezeigt." +
                "\n\n" +
                "Links neben " +
                "der Tabelle sind die Filter, mit " +
                "denen die Filme gefiltert werden " +
                "können." +
                "\n\n" +
                "In der Tabelle können Filme gestartet " +
                "und gespeichert werden. Unter der " +
                "Tabelle sind Infos zum " +
                "ausgewählten Film.";
        String image = "/de/p2tools/mtviewer/res/tips/film/film-1.png";
        TipData pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);

        text = "Mit den Button in der Tabelle können Filme gestartet " +
                "(Dreieck) " +
                "und gespeichert (Kreis) werden.";
        image = "/de/p2tools/mtviewer/res/tips/film/film-2.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);

        return pToolTipList;
    }
}
