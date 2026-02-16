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

public class TipListFilter {

    private TipListFilter() {
    }

    public static List<TipData> getTips() {
        List<TipData> pToolTipList = new ArrayList<>();

        String text = "Der Filter ist in " +
                "mehrere Bereiche geteilt. " +
                "Oben sind die Textfilter " +
                "(z.B.: Thema oder Titel)." +
                "\n\n" +
                "Danach " +
                "kommen die Filter die nach " +
                "Filmeigenschaften (z.B.: " +
                "Filmlänge) suchen." +
                "\n\n" +
                "Unten können Filtereinstellungen " +
                "gespeichert und " +
                "wieder abgerufen werden.";
        String image = "/de/p2tools/mtviewer/res/tips/filter/filter-1.png";
        TipData pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);

        text = "Mit dem Button (Kreis) werden die aktuellen Filtereinstellungen " +
                "gespeichert. Der Button darüber (Pfeil nach oben) stellt die gespeicherten " +
                "Einstellungen wieder her." +
                "\n\n" +
                "Mit den beiden Pfeilen unter (rechts, links) kann in den verwendeten " +
                "Filter zurück- und vor geblättert werden. Der rechte Button unten löscht " +
                "den Filter.";
        image = "/de/p2tools/mtviewer/res/tips/filter/filter-2.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);

        text = "In den Textfiltern kann auch mit RegEx gesucht werden. Die Farbe " +
                "gibt dann an, ob die RegEx korrekt ist. Weitere Infos dazu gibts " +
                "in der Anleitung auf der Website.";
        image = "/de/p2tools/mtviewer/res/tips/filter/filter-3.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        return pToolTipList;
    }
}
