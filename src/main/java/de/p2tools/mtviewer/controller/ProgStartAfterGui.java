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

package de.p2tools.mtviewer.controller;

import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgConst;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.config.ProgInfos;
import de.p2tools.mtviewer.controller.film.LoadFilmFactory;
import de.p2tools.mtviewer.controller.update.SearchProgramUpdate;
import de.p2tools.p2lib.guitools.P2WindowIcon;
import de.p2tools.p2lib.tools.ProgramToolsFactory;
import de.p2tools.p2lib.tools.date.DateFactory;
import de.p2tools.p2lib.tools.duration.PDuration;
import de.p2tools.p2lib.tools.log.LogMessage;
import de.p2tools.p2lib.tools.log.PLog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProgStartAfterGui {
    private static boolean doneAtProgramstart = false;

    private ProgStartAfterGui() {
    }

    /**
     * alles was nach der GUI gemacht werden soll z.B.
     * Filmliste beim Programmstart!! laden
     */
    public static void doWorkAfterGui() {
        P2WindowIcon.addWindowP2Icon(ProgData.getInstance().primaryStage);
        startMsg();
        setTitle();
        ProgData.getInstance().startTimer();
//        LoadFilmFactory.getInstance().loadFilmlist.addListenerLoadFilmlist(new ListenerLoadFilmlist() {
//            @Override
//            public void finished(ListenerFilmlistLoadEvent event) {
//                int age = FilmlistFactory.getAge(ProgData.getInstance().filmlist.metaData);
//                ProgConfig.SYSTEM_FILMLIST_AGE.setValue(ProgData.getInstance().filmlist.isEmpty() ? P2LibConst.NUMBER_NOT_STARTED : age);
//
//                if (!doneAtProgramstart) {
//                    doneAtProgramstart = true;
//                    checkProgUpdate();
//                    new TipOfDay().showDialog(ProgData.getInstance(), false);
//                }
//            }
//        });

        //die gespeicherte Filmliste laden
        LoadFilmFactory.getInstance().loadProgStart();
    }

    public static void startMsg() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Verzeichnisse:");
        list.add("Programmpfad: " + ProgInfos.getPathJar());
        list.add("Verzeichnis Einstellungen: " + ProgInfos.getSettingsDirectory_String());

        LogMessage.startMsg(ProgConst.PROGRAM_NAME, list);
        PLog.sysLog(list);
        ProgConfig.logAllConfigs();
    }

    private static void checkProgUpdate() {
        // Prüfen obs ein Programmupdate gibt
        PDuration.onlyPing("checkProgUpdate");
        if (ProgConfig.SYSTEM_UPDATE_SEARCH_ACT.getValue() &&
                !updateCheckTodayDone()) {
            // nach Updates suchen
            runUpdateCheck(false);

        } else {
            // will der User nicht --oder-- wurde heute schon gemacht
            List list = new ArrayList(5);
            list.add("Kein Update-Check:");
            if (!ProgConfig.SYSTEM_UPDATE_SEARCH_ACT.getValue()) {
                list.add("  der User will nicht");
            }
            if (updateCheckTodayDone()) {
                list.add("  heute schon gemacht");
            }
            PLog.sysLog(list);
        }
    }

    private static boolean updateCheckTodayDone() {
        return ProgConfig.SYSTEM_UPDATE_DATE.get().equals(DateFactory.F_FORMAT_yyyy_MM_dd.format(new Date()));
    }

    private static void runUpdateCheck(boolean showAlways) {
        ProgConfig.SYSTEM_UPDATE_DATE.setValue(DateFactory.F_FORMAT_yyyy_MM_dd.format(new Date()));
        new SearchProgramUpdate(ProgData.getInstance()).searchNewProgramVersion(showAlways);
    }

    private static void setTitle() {
        if (ProgData.debug) {
            ProgData.getInstance().primaryStage.setTitle(ProgConst.PROGRAM_NAME + " " + ProgramToolsFactory.getProgVersion() + " / DEBUG");
        } else {
            ProgData.getInstance().primaryStage.setTitle(ProgConst.PROGRAM_NAME + " " + ProgramToolsFactory.getProgVersion());
        }
    }
}
