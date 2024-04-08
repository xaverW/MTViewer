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
import de.p2tools.mtviewer.gui.startdialog.StartDialogController;
import de.p2tools.p2lib.configfile.ConfigFile;
import de.p2tools.p2lib.configfile.ConfigReadFile;
import de.p2tools.p2lib.tools.duration.P2Duration;
import de.p2tools.p2lib.tools.log.P2Log;
import de.p2tools.p2lib.tools.log.P2LogMessage;
import de.p2tools.p2lib.tools.log.P2Logger;
import javafx.application.Platform;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class ProgStartBeforeGui {

    private ProgStartBeforeGui() {
    }

    public static void workBeforeGui() {
        if (!loadAll()) {
            P2Duration.onlyPing("Erster Start");
            ProgData.firstProgramStart = true;

            UpdateConfig.setUpdateDone(); //dann ists ja kein Programmupdate
            ProgData.getInstance().replaceList.init(); //einmal ein Muster anlegen, für Linux ist es bereits aktiv!

            StartDialogController startDialogController = new StartDialogController();
            if (!startDialogController.isOk()) {
                // dann jetzt beenden -> Tschüss
                Platform.exit();
                System.exit(0);
            }
        }
    }

    /**
     * Config beim  Programmstart laden
     *
     * @return
     */
    private static boolean loadAll() {
        if (ProgConfig.SYSTEM_LOG_ON.getValue()) {
            P2Logger.setFileHandler(ProgInfos.getLogDirectory_String());
        }

        if (!load()) {
            P2Log.sysLog("Weder Konfig noch Backup konnte geladen werden!");
            // teils geladene Reste entfernen
            clearTheConfigs();
            return false;
        }
        return true;
    }

    public static void shortStartMsg() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Verzeichnisse:");
        list.add("Programmpfad: " + ProgInfos.getPathJar());
        list.add("Verzeichnis Einstellungen: " + ProgInfos.getSettingsDirectory_String());

        P2LogMessage.startMsg(ProgConst.PROGRAM_NAME, list);
        P2Log.sysLog(list);
    }

    private static void clearTheConfigs() {
        ProgData progData = ProgData.getInstance();
        progData.replaceList.clear();
    }

    private static boolean load() {
        final Path xmlFilePath = new ProgInfos().getSettingsFile();
        try {
            if (!Files.exists(xmlFilePath)) {
                //dann gibts das Konfig-File gar nicht
                P2Log.sysLog("Konfig existiert nicht!");
                return false;
            }

            P2Log.sysLog("Programmstart und ProgConfig laden von: " + xmlFilePath);
            ConfigFile configFile = new ConfigFile(xmlFilePath.toString(), true) {
                @Override
                public void clearConfigFile() {
                    clearTheConfigs();
                }
            };

            ProgConfig.addConfigData(configFile);
            if (ConfigReadFile.readConfig(configFile)) {
                UpdateConfig.update();
                P2Log.sysLog("Konfig wurde geladen!");
                return true;

            } else {
                // dann hat das Laden nicht geklappt
                P2Log.sysLog("Konfig konnte nicht geladen werden!");
                return false;
            }
        } catch (final Exception ex) {
            P2Log.errorLog(915470101, ex);
        }
        return false;
    }
}
