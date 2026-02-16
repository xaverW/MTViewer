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

public class TipListSet {

    private TipListSet() {
    }

    public static List<TipData> getTips() {
        List<TipData> pToolTipList = new ArrayList<>();

        String text = "Die Einstellungen zum Abspielen eines Films finden sich " +
                "in den Programmeinstellungen->Filme." +
                "\n\n" +
                "Dort kann ein Programm zum Abspielen angegeben werden. Es kann dort auch " +
                "die Auflösung (HD, Hoch, Klein) vorgegeben werden.";
        String image = "/de/p2tools/mtviewer/res/tips/set/set-1.png";
        TipData pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);

        text = "Zum Speichern eines Films finden sich die Einstellungen unter " +
                "Einstellungen->Download->Programm." +
                "\n\n" +
                "Hier kann ein Zielpfad vorgegeben werden. Der wird dann im Downloaddialog " +
                "als Vorgabe angegeben. Auch die Vorgabe des Dateinamens ist hier möglich. " +
                "Dafür können Parameter angegeben werden die dann z.B. mit dem Filmtitel ersetzt werden." +
                "\n\n" +
                "Das Programm das den Download macht wird auch hier eingestellt. Filme die auf eine \"mp4-Datei\" " +
                "verweisen macht das Programm selbst. Streams (m3u8) werden dann über das angegebene Programm " +
                "erledigt.";
        image = "/de/p2tools/mtviewer/res/tips/set/set-2.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);

        return pToolTipList;
    }
}
