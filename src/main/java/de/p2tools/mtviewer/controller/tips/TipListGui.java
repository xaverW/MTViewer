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

public class TipListGui {

    private TipListGui() {
    }

    public static List<TipData> getTips() {
        List<TipData> pToolTipList = new ArrayList<>();

        String text = "Mit dem Button, wird eine " +
                "neue Filmliste geladen. " +
                "Ist der Text unterstrichen " +
                "signalisiert das, dass es " +
                "eine neue Filmliste gibt.";
        String image = "/de/p2tools/mtviewer/res/tips/gui/gui-3.png";
        TipData pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);

        text = "Alle Infofelder und Filter können " +
                "mit dem Symbol \"x\" ausgeblendet " +
                "werden (ein- und ausblenden ist auch über " +
                "das Menü möglich). Mit dem Symbol \"Dreieck\" " +
                "kann man sie \"abreißen\" und " +
                "in einem Extrafenster anzeigen.";
        image = "/de/p2tools/mtviewer/res/tips/gui/gui-2.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);

        text = "Das Programm kann in verschiedenen " +
                "Farben angezeigt werden." +
                "\n\n" +
                "In den Programm-Einstellungen " +
                "(->Farben des Programms) sind die " +
                "Einstellungen dafür. " +
                "Die Farben können frei ausgewählt " +
                "werden.";
        image = "/de/p2tools/mtviewer/res/tips/gui/gui-1.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);

        text = "Mit einem Klick auf das Menü " +
                "(RECHTE-Maustaste) kann das " +
                "Gui zwischen Hell- und " +
                "Dunkel umgeschaltet werden." +
                "\n\n" +
                "Ein Doppelklick schaltet " +
                "zwischen den beiden Farbmodi um. " +
                "Im Menü selbst, gibt es auch " +
                "zwei Menüpunkte zum Umschalten.";
        image = "/de/p2tools/mtviewer/res/tips/gui/gui-5.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);

        return pToolTipList;
    }
}
