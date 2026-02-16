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

public class TipListDownload {

    private TipListDownload() {
    }

    public static List<TipData> getTips() {
        List<TipData> pToolTipList = new ArrayList<>();

        String text = "Unter der Tabelle im Tab Download werden alle aktuellen " +
                "Downloads angezeigt. Hier können " +
                "Downloads gestartet und gelöscht " +
                "oder geändert werden.";
        String image = "/de/p2tools/mtviewer/res/tips/download/download-1.png";
        TipData pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);

        text = "Mit den Button in der Tabelle der Downloads können Downloads gestartet (Dreieck nach unten), " +
                "gestoppt (Pause-Zeichen) und gelöscht (X) werden." +
                "\n\n" +
                "Ist der Download abgeschlossen, kann der " +
                "Filme abgespielt (Dreieck nach rechts) werden. Der Button mit dem Ordnersymbol " +
                "öffnet den Speicherordner." +
                "\n\n" +
                "Solange der Download noch nicht läuft, kann er mit einem Doppelklick geändert werden.";
        image = "/de/p2tools/mtviewer/res/tips/download/download-2.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);

        text = "Mit den Button neben der Tabelle werden alle Downloads " +
                "gestartet (erster Button). Der zweite Button stoppt alle Downloads und " +
                "der letzte Button (Besen) räumt die Tabelle auf. Es werden abgeschlossene " +
                "Downloads entfernt und fehlerhafte zurückgesetzt.";
        image = "/de/p2tools/mtviewer/res/tips/download/download-3.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);

        text = "Wird ein neuer Download angelegt oder ein Download geändert, erscheint " +
                "dieser Dialog. Dort kann oben die Auflösung (HD, Normal, Klein) ausgewählt werden." +
                "\n\n" +
                "Im mittleren Feld wird der Dateiname angezeigt und kann angepasst werden. Unten wird der Speicherordner " +
                "angezeigt und kann auch geändert werden.";
        image = "/de/p2tools/mtviewer/res/tips/download/download-4.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);

        return pToolTipList;
    }
}
