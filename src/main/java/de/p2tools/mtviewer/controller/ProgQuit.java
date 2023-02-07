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

import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.p2Lib.tools.PShutDown;
import de.p2tools.p2Lib.tools.log.LogMessage;
import javafx.application.Platform;

public class ProgQuit {

    private ProgQuit() {
    }

    /**
     * Quit the MTViewer application
     */
    public static void quit() {
        saveConfig();
        exitProg();
    }

    /**
     * Quit the MTViewer application and shutDown the computer
     */
    public static void quitShutDown() {
        saveConfig();
        PShutDown.shutDown();
        exitProg();
    }

    /**
     * Quit the MTViewer application and show QuitDialog
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
        ProgSave.saveAll();
        LogMessage.endMsg();
    }

    private static void exitProg() {
        // dann jetzt beenden -> Tschüss
        Platform.runLater(() -> {
            Platform.exit();
            System.exit(0);
        });
    }

    private static void stopAllDownloads() {
        //erst mal alle Downloads stoppen
        ProgData.getInstance().downloadList.forEach(download -> {
            if (download.isStateStartedRun()) {
                //laufende werden nur gestoppt
                download.stopDownload();
            }
            if (download.isStateStartedWaiting()) {
                //wartende werden komplett zurückgesetzt
                download.resetDownload();
            }
            Process p = download.getStart().getProcess();
            if (p != null) {
                //um Downloads mit ffmpeg zu stoppen!
                p.destroy();
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
