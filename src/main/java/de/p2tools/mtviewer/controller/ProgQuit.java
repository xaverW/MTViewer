/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
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

import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.p2Lib.tools.PShutDown;
import de.p2tools.p2Lib.tools.log.LogMessage;
import javafx.application.Platform;

public class ProgQuit {

    private ProgQuit() {
    }

    /**
     * Quit the MTPlayer application
     */
    public static void quit() {
        saveConfig();
        exitProg();
    }

    /**
     * Quit the MTPlayer application and shutDown the computer
     */
    public static void quitShutDown() {
        saveConfig();
        PShutDown.shutDown();
        exitProg();
    }

    /**
     * Quit the MTPlayer application and show QuitDialog
     *
     * @param startWithWaiting starts the dialog with the masker pane
     */
    public static void quit(boolean startWithWaiting) {
        final ProgData progData = ProgData.getInstance();
        //dann Programm beenden
        saveConfig();
        exitProg();
    }

    private static void saveConfig() {
        stopAllDownloads();
        writeTabSettings();
        new ProgSave().saveAll();
        LogMessage.endMsg();
    }

    private static void exitProg() {
        // dann jetzt beenden -> Thüss
        Platform.runLater(() -> {
            Platform.exit();
            System.exit(0);
        });
    }

    private static void stopAllDownloads() {
        //erst mal alle Downloads stoppen
        ProgData.getInstance().downloadList.forEach(download -> {
            if (download.isStateStartedRun()) {
                download.stopDownload();
            }
        });

        //unterbrochene werden gespeichert, dass die Info "Interrupt" erhalten bleibt
        ProgData.getInstance().downloadList.removeIf(download ->
                (!download.isStateStoped() && download.isStateFinished()));
    }

    private static void writeTabSettings() {
        // Tabelleneinstellungen merken
        final ProgData progData = ProgData.getInstance();
        progData.filmGuiController.saveTable();
    }
}
