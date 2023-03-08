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


package de.p2tools.mtviewer.gui.tools;

import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.ptipofday.PTipOfDay;
import de.p2tools.p2lib.guitools.ptipofday.PTipOfDayDialog;
import de.p2tools.p2lib.guitools.ptipofday.PTipOfDayFactory;
import de.p2tools.p2lib.tools.date.DateFactory;
import de.p2tools.p2lib.tools.log.PLog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TipOfDayFactory {

    private static final String START = "                                                     " + P2LibConst.LINE_SEPARATOR;
    private static final int listSize = 17;

    private TipOfDayFactory() {
    }

    public static void showDialog(ProgData progData, boolean showAlways) {
        if (!showAlways && !ProgConfig.TIP_OF_DAY_SHOW.getValue()) {
            //dann wills der User nicht :(
            PLog.sysLog("TipOfDay: Will der User nicht");
            return;
        }

        if (showAlways ||
                !ProgConfig.TIP_OF_DAY_DATE.get().equals(DateFactory.F_FORMAT_yyyy_MM_dd.format(new Date())) &&
                        PTipOfDayFactory.containsToolTipNotShown(ProgConfig.TIP_OF_DAY_WAS_SHOWN.get(), listSize)) {

            //nur wenn "immer" / heute noch nicht und nicht angezeigte ToolTips enthalten sind
            ProgConfig.TIP_OF_DAY_DATE.setValue(DateFactory.F_FORMAT_yyyy_MM_dd.format(new Date()));

            final List<PTipOfDay> pTipOfDayArrayList = new ArrayList<>();
            addTips(pTipOfDayArrayList);
            new PTipOfDayDialog(progData.primaryStage, pTipOfDayArrayList,
                    ProgConfig.TIP_OF_DAY_WAS_SHOWN, ProgConfig.TIP_OF_DAY_SHOW, 500);
        } else {
            PLog.sysLog("TipOfDay: Heute schon gemacht oder keine neuen Tips");
        }
    }

    private static void addTips(List<PTipOfDay> pToolTipList) {
        // private final int listSize = 1
        String text = START;
        text += "Der Infobereich unter der Filmliste\n" +
                "kann mit dem Tastenkürzel \"alt+i\"\n" +
                "oder über das Menü\n" +
                "ein- und ausgeblendet werden.";
        String image = "/de/p2tools/mtviewer/res/tooltips/Info.png";
        PTipOfDay pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "Hier können vorherige Filtereinstellung\n" +
                "zurückgeholt werden. Der Filter kann\n" +
                "hier auch gelöscht werden.";
        image = "/de/p2tools/mtviewer/res/tooltips/ClearFilter.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "In den Programmeinstellungen\n" +
                "(erreichbar über das Menü)\n" +
                "können spezielle Einstellungen\n" +
                "zum Filtern vorgenommen werden.";
        image = "/de/p2tools/mtviewer/res/tooltips/Filter.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "In den Programmeinstellungen\n" +
                "(erreichbar über das Menü)\n" +
                "können Download-Einstellungen\n" +
                "(Pfad, Zieldateiname, ..)\n" +
                "vorgenommen werden.";
        image = "/de/p2tools/mtviewer/res/tooltips/DownloadPath.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        pToolTipList.add(PTipOfDay.getTipWebsite(ProgConfig.SYSTEM_PROG_OPEN_URL));

        text = START;
        text += "In den Programmeinstellungen\n" +
                "(erreichbar über das Menü)\n" +
                "kann die verwendete\n" +
                "Schriftgröße im Programm,\n" +
                "eingestellt werden.";
        image = "/de/p2tools/mtviewer/res/tooltips/Keysize.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "In den Programmeinstellungen\n" +
                "(erreichbar über das Menü)\n" +
                "kann die Filmliste (und damit die\n" +
                "Anzahl der Filme) eingeschränkt\n" +
                "werden. Bei schwachen Rechnern\n" +
                "reagiert das Programm dann\n" +
                "schneller.";
        image = "/de/p2tools/mtviewer/res/tooltips/LoadFilmlist.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "In den Programmeinstellungen\n" +
                "(erreichbar über das Menü)\n" +
                "kann auch der Videoplayer\n" +
                "zum Abspielen der Filme\n" +
                "geändert werden.";
        image = "/de/p2tools/mtviewer/res/tooltips/Videoplayer.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "Im Menü (unter \"Hilfe\") kann\n" +
                "der Dialog:\n" +
                "\"Einstellungen zurücksetzen\"\n" +
                "geöffnet werden. Damit können\n" +
                "alle Programmeinstellungen\n" +
                "zurückgesetzt werden. Das\n" +
                "Programm startet dann wieder\n" +
                "wie beim ersten Mal.";
        image = "/de/p2tools/mtviewer/res/tooltips/Reset.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);
    }
}