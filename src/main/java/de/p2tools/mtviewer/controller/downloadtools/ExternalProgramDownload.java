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

package de.p2tools.mtviewer.controller.downloadtools;

import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.data.download.DownloadConstants;
import de.p2tools.mtviewer.controller.data.download.DownloadData;
import de.p2tools.mtviewer.gui.dialog.DownloadErrorDialogController;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.application.Platform;

import java.io.File;

import static de.p2tools.mtviewer.controller.starter.StarterClass.*;

/**
 * Download files via an external program.
 */
public class ExternalProgramDownload {

    private final ProgData progData;
    private final DownloadData download;
    private final int stat_start = 0;
    private final int stat_running = 1;
    private final int stat_restart = 3;
    private final int stat_checking = 4;
    // ab hier ist schluss
    private final int stat_finished_ok = 10;
    private final int stat_finished_error = 11;
    private final int stat_finished_abort = 12;
    private final int stat_end = 99;
    private File file;
    private String exMessage = "";
    private long fileSize = -1;
    private DirectHttpDownload directHttpDownload;

    public ExternalProgramDownload(DirectHttpDownload directHttpDownload, ProgData progData, DownloadData download) {
        super();
        this.directHttpDownload = directHttpDownload;
        this.progData = progData;
        this.download = download;
        this.download.setStateStartedRun();
        file = new File(this.download.getDestPathFile());
    }

    public void startExt() {
        int stat = stat_start;
        try {
            while (stat < stat_end) {
                stat = downloadLoop(stat);
            }

        } catch (final Exception ex) {
            exMessage = ex.getLocalizedMessage();
            P2Log.errorLog(395623710, ex);
            if (download.getStart().getRestartCounter() == 0) {
                // nur beim ersten Mal melden -> nervt sonst
                Platform.runLater(() -> new DownloadErrorDialogController(download, exMessage));
            }
            download.setStateError();
            download.setErrorMessage(exMessage);
        }
    }

    private int downloadLoop(int stat) {
        switch (stat) {
            case stat_start:
                // versuch das Programm zu Starten
                stat = startDownload();
                break;

            case stat_running:
                // hier läuft der Download bis zum Abbruch oder Ende
                stat = runDownload();
                break;

            case stat_restart:
                stat = restartDownload();
                break;

            case stat_checking:
                stat = checkDownload();
                break;

            case stat_finished_error:
                download.setStateError();
                stat = stat_end;
                break;

            case stat_finished_ok:
                download.setStateFinished();
                stat = stat_end;
                break;

            case stat_finished_abort:
                stat = stat_end;
                break;
        }

        return stat;
    }

    private int startDownload() {
        //versuchen das Programm zu starten,
        //die Reihenfolge: startCounter - Startmeldung ist wichtig!
        int retStat;
        download.getStart().setStartCounter(download.getStart().getStartCounter() + 1);
        startMsg(download);
        final RuntimeExec runtimeExec = new RuntimeExec(download);
        download.getStart().setProcess(runtimeExec.exec(true /* log */));
        if (download.getStart().getProcess() != null) {
            retStat = stat_running;
        } else {
            retStat = stat_restart;
        }
        return retStat;
    }

    private int runDownload() {
        // hier läuft der Download bis zum Abbruch oder Ende
        int retStatus = stat_running;
        try {
            if (download.isStateStoped()) {
                // abbrechen
                retStatus = stat_finished_abort;
                if (download.getStart().getProcess() != null) {
                    download.getStart().getProcess().destroy();
                }

            } else {
                final int exitV = download.getStart().getProcess().exitValue(); //liefert ex wenn noch nicht fertig
                if (exitV != 0) {
                    retStatus = stat_restart;
                } else {
                    retStatus = stat_checking;
                }
            }

        } catch (final Exception ex) {
            try {
                directHttpDownload.wait(2000);
            } catch (final InterruptedException ignored) {
            }
        }
        return retStatus;
    }

    private int restartDownload() {
        int retStatus;
        if (fileSize == -1) {
            // noch nichts geladen
            deleteIfEmpty(file);
            if (file.exists()) {
                // dann bestehende Datei weitermachen
                fileSize = file.length();
                retStatus = stat_start;
            } else {
                // counter prüfen und bei einem Maxwert checkIfCancelDownload, sonst endlos
                if (download.getStart().getStartCounter() < DownloadConstants.START_COUNTER_MAX) {
                    // dann nochmal von vorne
                    retStatus = stat_start;
                } else {
                    // dann wars das
                    retStatus = stat_finished_error;
                }
            }

        } else {
            // jetzt muss das File wachsen, sonst kein Restart
            if (!file.exists()) {
                // dann wars das
                retStatus = stat_finished_error;
            } else if (file.length() > fileSize) {
                // nur weitermachen wenn die Datei tasächlich wächst
                fileSize = file.length();
                retStatus = stat_start;
            } else {
                // dann wars das
                retStatus = stat_finished_error;
            }
        }
        return retStatus;
    }

    private int checkDownload() {
        if (check(progData, download)) {
            // fertig und OK
            return stat_finished_ok;
        } else {
            // fertig und fehlerhaft
            return stat_finished_error;
        }
    }
}
