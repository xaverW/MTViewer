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
import de.p2tools.p2Lib.P2LibConst;
import de.p2tools.p2Lib.guiTools.pTipOfDay.PTipOfDay;
import de.p2tools.p2Lib.guiTools.pTipOfDay.PTipOfDayDialog;
import de.p2tools.p2Lib.guiTools.pTipOfDay.PTipOfDayFactory;
import de.p2tools.p2Lib.tools.date.DateFactory;
import de.p2tools.p2Lib.tools.log.PLog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TipOfDay {

    private final String START = "                                                     " + P2LibConst.LINE_SEPARATOR;
    private final int listSize = 17;

    public TipOfDay() {
    }

    public void showDialog(ProgData progData, boolean showAlways) {
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

    private void addTips(List<PTipOfDay> pToolTipList) {
        // private final int listSize = 1
        String text = START;
        text += "Der Infobereich unter der Filmliste\n" +
                "kann mit dem Tastenk??rzel \"alt+i\"\n" +
                "oder ??ber das Men??\n" +
                "ein- und ausgeblendet werden.";
        String image = "/de/p2tools/mtviewer/res/toolTips/Info.png";
        PTipOfDay pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "Hier k??nnen vorherige Filtereinstellung\n" +
                "zur??ckgeholt werden. Der Filter kann\n" +
                "hier auch gel??scht werden.";
        image = "/de/p2tools/mtviewer/res/toolTips/ClearFilter.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "In den Programmeinstellungen\n" +
                "(erreichbar ??ber das Men??)\n" +
                "k??nnen spezielle Einstellungen\n" +
                "zum Filtern vorgenommen werden.";
        image = "/de/p2tools/mtviewer/res/toolTips/Filter.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "In den Programmeinstellungen\n" +
                "(erreichbar ??ber das Men??)\n" +
                "k??nnen Download-Einstellungen\n" +
                "(Pfad, Zieldateiname, ..)\n" +
                "vorgenommen werden.";
        image = "/de/p2tools/mtviewer/res/toolTips/DownloadPath.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        pToolTipList.add(PTipOfDay.getTipWebsite(ProgConfig.SYSTEM_PROG_OPEN_URL));

        text = START;
        text += "In den Programmeinstellungen\n" +
                "(erreichbar ??ber das Men??)\n" +
                "kann die verwendete\n" +
                "Schriftgr????e im Programm,\n" +
                "eingestellt werden.";
        image = "/de/p2tools/mtviewer/res/toolTips/Keysize.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "In den Programmeinstellungen\n" +
                "(erreichbar ??ber das Men??)\n" +
                "kann die Filmliste (und damit die\n" +
                "Anzahl der Filme) eingeschr??nkt\n" +
                "werden. Bei schwachen Rechnern\n" +
                "reagiert das Programm dann\n" +
                "schneller.";
        image = "/de/p2tools/mtviewer/res/toolTips/LoadFilmlist.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "In den Programmeinstellungen\n" +
                "(erreichbar ??ber das Men??)\n" +
                "kann auch der Videoplayer\n" +
                "zum Abspielen der Filme\n" +
                "ge??ndert werden.";
        image = "/de/p2tools/mtviewer/res/toolTips/Videoplayer.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "Im Men?? (unter \"Hilfe\") kann\n" +
                "der Dialog:\n" +
                "\"Einstellungen zur??cksetzen\"\n" +
                "ge??ffnet werden. Damit k??nnen\n" +
                "alle Programmeinstellungen\n" +
                "zur??ckgesetzt werden. Das\n" +
                "Programm startet dann wieder\n" +
                "wie beim ersten Mal.";
        image = "/de/p2tools/mtviewer/res/toolTips/Reset.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);
    }
}
