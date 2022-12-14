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

import de.p2tools.mtviewer.controller.config.*;
import de.p2tools.mtviewer.controller.film.LoadFilmFactory;
import de.p2tools.mtviewer.controller.update.SearchProgramUpdate;
import de.p2tools.mtviewer.gui.tools.TipOfDay;
import de.p2tools.p2Lib.P2LibConst;
import de.p2tools.p2Lib.alert.PAlert;
import de.p2tools.p2Lib.icons.GetIcon;
import de.p2tools.p2Lib.mtFilm.loadFilmlist.ListenerFilmlistLoadEvent;
import de.p2tools.p2Lib.mtFilm.loadFilmlist.ListenerLoadFilmlist;
import de.p2tools.p2Lib.tools.ProgramToolsFactory;
import de.p2tools.p2Lib.tools.date.DateFactory;
import de.p2tools.p2Lib.tools.duration.PDuration;
import de.p2tools.p2Lib.tools.log.LogMessage;
import de.p2tools.p2Lib.tools.log.PLog;
import de.p2tools.p2Lib.tools.log.PLogger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProgStart {
    private boolean doneAtProgramstart = false;

    public ProgStart() {
    }

    public static void shortStartMsg() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Verzeichnisse:");
        list.add("Programmpfad: " + ProgInfos.getPathJar());
        list.add("Verzeichnis Einstellungen: " + ProgInfos.getSettingsDirectory_String());

        LogMessage.startMsg(ProgConst.PROGRAM_NAME, list);
        PLog.sysLog(list);
    }

    public static void startMsg() {
        shortStartMsg();
        ProgConfig.logAllConfigs();
    }

    private static boolean updateCheckTodayDone() {
        return ProgConfig.SYSTEM_UPDATE_DATE.get().equals(DateFactory.F_FORMAT_yyyy_MM_dd.format(new Date()));
    }

    /**
     * alles was nach der GUI gemacht werden soll z.B.
     * Filmliste beim Programmstart!! laden
     *
     * @param firstProgramStart
     */
    public void doWorkAfterGui(ProgData progData, boolean firstProgramStart) {
        GetIcon.addWindowP2Icon(progData.primaryStage);
        startMsg();
        setTitle(progData);
        progData.startTimer();
        LoadFilmFactory.getInstance().loadFilmlist.addListenerLoadFilmlist(new ListenerLoadFilmlist() {
            @Override
            public void finished(ListenerFilmlistLoadEvent event) {
                if (!doneAtProgramstart) {
                    doneAtProgramstart = true;
                    checkProgUpdate(progData);
                    new TipOfDay().showDialog(progData, false);
                }
            }
        });

        LoadFilmFactory.getInstance().loadProgStart(firstProgramStart);
    }

    /**
     * Config beim  Programmstart laden
     *
     * @return
     */
    public boolean loadAll() {
        boolean loadOk = load();
        if (!loadOk) {
            //dann mit der alten Verison versuchen
            loadOk = load__oldVersion();
        }

        if (ProgConfig.SYSTEM_LOG_ON.getValue()) {
            PLogger.setFileHandler(ProgInfos.getLogDirectory_String());
        }

        if (!loadOk) {
            PLog.sysLog("Weder Konfig noch Backup konnte geladen werden!");
            // teils geladene Reste entfernen
            clearConfig();
            return false;
        }
        PLog.sysLog("Konfig wurde gelesen!");
        UpdateConfig.update(); // falls es ein Programmupdate gab, Configs anpassen
        ProgColorList.setColorTheme(); // Farben einrichten
        return true;
    }

    private void clearConfig() {
        ProgData progData = ProgData.getInstance();
        progData.replaceList.clear();
    }

    private boolean load() {
        boolean ret = false;
        ProgData progData = ProgData.getInstance();
        final Path xmlFilePath = new ProgInfos().getSettingsFile();

        try {
            if (Files.exists(xmlFilePath)) {
                if (ProgLoadFactory.loadProgConfigData(progData, xmlFilePath)) {
                    return true;
                } else {
                    // dann hat das Laden nicht geklappt
                    PLog.sysLog("Konfig konnte nicht gelesen werden!");
                }
            } else {
                // dann hat das Laden nicht geklappt
                PLog.sysLog("Konfig existiert nicht!");
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
        }

        // versuchen das Backup zu laden
        if (loadBackup()) {
            ret = true;
        }
        return ret;
    }

    private boolean loadBackup() {
        ProgData progData = ProgData.getInstance();
        boolean ret = false;
        final ArrayList<Path> path = new ArrayList<>();
        new ProgInfos().getMTViewerXmlCopyFilePath(path);
        if (path.isEmpty()) {
            PLog.sysLog("Es gibt kein Backup");
            return false;
        }

        // dann gibts ein Backup
        PLog.sysLog("Es gibt ein Backup");

        // stage bzw. scene gibts noch nicht
        if (PAlert.BUTTON.YES != PAlert.showAlert_yes_no(null, "Gesicherte Einstellungen laden?",
                "Die Einstellungen sind besch??digt" + P2LibConst.LINE_SEPARATOR +
                        "und k??nnen nicht geladen werden.",
                "Soll versucht werden, mit gesicherten" + P2LibConst.LINE_SEPARATOR
                        + "Einstellungen zu starten?" + P2LibConst.LINE_SEPARATORx2
                        + "(ansonsten startet das Programm mit" + P2LibConst.LINE_SEPARATOR
                        + "Standardeinstellungen)")) {

            PLog.sysLog("User will kein Backup laden.");
            return false;
        }

        for (final Path p : path) {
            // teils geladene Reste entfernen
            clearConfig();
            PLog.sysLog(new String[]{"Versuch Backup zu laden:", p.toString()});
            try {
                if (ProgLoadFactory.loadProgConfigData(progData, p)) {
                    PLog.sysLog(new String[]{"Backup hat geklappt:", p.toString()});
                    ret = true;
                    break;
                }
            } catch (final Exception ex) {
                ex.printStackTrace();
            }

        }
        return ret;
    }

    private void checkProgUpdate(ProgData progData) {
        // Pr??fen obs ein Programmupdate gibt
        PDuration.onlyPing("checkProgUpdate");

      /*  if (ProgData.debug) {
            // damits bei jedem Start gemacht wird
            PLog.sysLog("DEBUG: Update-Check");
            runUpdateCheck(progData, true);

        } else*/

        if (ProgConfig.SYSTEM_UPDATE_SEARCH_ACT.getValue() &&
                !updateCheckTodayDone()) {
            // nach Updates suchen
            runUpdateCheck(progData, false);

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

    private void runUpdateCheck(ProgData progData, boolean showAlways) {
        ProgConfig.SYSTEM_UPDATE_DATE.setValue(DateFactory.F_FORMAT_yyyy_MM_dd.format(new Date()));
        new SearchProgramUpdate(progData).searchNewProgramVersion(showAlways);
    }

    private void setTitle(ProgData progData) {
        if (ProgData.debug) {
            progData.primaryStage.setTitle(ProgConst.PROGRAM_NAME + " " + ProgramToolsFactory.getProgVersion() + " / DEBUG");
        } else {
            progData.primaryStage.setTitle(ProgConst.PROGRAM_NAME + " " + ProgramToolsFactory.getProgVersion());
        }
    }

    private boolean load__oldVersion() {
        PLog.sysLog("Konfig von OLD-VERSION importieren!");
        ProgData progData = ProgData.getInstance();
        boolean ret = false;
        final Path xmlFilePath = new ProgInfos().getSettingsFileOld();

        try (IoReadXml reader = new IoReadXml(progData)) {
            if (Files.exists(xmlFilePath)) {
                if (reader.readConfiguration(xmlFilePath)) {
                    return true;
                } else {
                    // dann hat das Laden nicht geklappt
                    PLog.sysLog("Konfig konnte nicht gelesen werden!");
                }
            } else {
                // dann hat das Laden nicht geklappt
                PLog.sysLog("Konfig existiert nicht!");
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
        }

        return ret;
    }
}
