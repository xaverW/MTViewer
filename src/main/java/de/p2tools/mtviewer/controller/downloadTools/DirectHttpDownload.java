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

package de.p2tools.mtviewer.controller.downloadTools;

import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.config.ProgInfos;
import de.p2tools.mtviewer.controller.data.download.DownloadData;
import de.p2tools.mtviewer.controller.starter.StarterClass;
import de.p2tools.mtviewer.gui.dialog.DownloadContinueDialogController;
import de.p2tools.mtviewer.gui.dialog.DownloadErrorDialogController;
import de.p2tools.p2Lib.mtDownload.DownloadFactory;
import de.p2tools.p2Lib.P2LibConst;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Timer;

public class DirectHttpDownload extends Thread {

    /**
     * HTTP Timeout in milliseconds.
     */
    private final ProgData progData;
    private final DownloadData download;
    private final Timer bandwidthCalculationTimer;
    private HttpURLConnection conn = null;
    private LongProperty downloaded = new SimpleLongProperty(0);
    private File file = null;
    private String responseCode;
    private String exMessage;
    private boolean retBreak;
    private boolean dialogBreakIsVis;

    public DirectHttpDownload(ProgData progData, DownloadData d, Timer bandwidthCalculationTimer) {
        super();
        this.progData = progData;
        this.bandwidthCalculationTimer = bandwidthCalculationTimer;
        download = d;
        setName("DIRECT DL THREAD: " + d.getTitle());
        download.setStateStartedRun();
    }

    @Override
    public synchronized void run() {
        StarterClass.startMsg(download);
        try {
            Files.createDirectories(Paths.get(download.getDestPath()));
        } catch (final IOException ignored) {
        }

        int restartCount = 0;
        boolean restart = true;
        while (restart) {
            restart = false;
            try {
                file = new File(download.getDestPathFile());
                if (!cancelDownload()) {
                    checkConn();
                }
                //und wenn klar, dann jetzt laden :)
                if (download.isStateStartedRun()) {
                    new DownloadCont().downloadContent(this, progData, download, conn,
                            bandwidthCalculationTimer, file, downloaded);
                }

            } catch (final Exception ex) {
                if ((ex instanceof IOException)
                        && restartCount < ProgConfig.SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP.getValue()) {
                    if (ex instanceof java.net.SocketTimeoutException) {
                        // Timeout Fehlermeldung für zxd :)
                        final ArrayList<String> text = new ArrayList<>();
                        text.add("Timeout, Download Restarts: " + restartCount);
                        text.add("Ziel: " + download.getDestPathFile());
                        text.add("URL: " + download.getUrl());
                        PLog.sysLog(text.toArray(new String[text.size()]));
                    }
                    restartCount++;
                    restart = true;

                } else {
                    // dann weiß der Geier!
                    exMessage = ex.getMessage();
                    PLog.errorLog(316598941, ex, "Fehler");
                    if (download.getStart().getRestartCounter() == 0) {
                        // nur beim ersten Mal melden -> nervt sonst
                        Platform.runLater(() -> new DownloadErrorDialogController(download, exMessage));
                    }
                    download.setErrorMessage(exMessage);
                    download.setStateError();
                }
            }
        }

        try {
            if (download.getStart().getInputStream() != null) {
                download.getStart().getInputStream().close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        } catch (final Exception ignored) {
        }

        StarterClass.finalizeDownload(download);
    }

    private void checkConn() throws IOException {
        final URL url = new URL(download.getUrl());
        //If the server uses self-signed X.509 certificate, we will get SSLHandshakeException -> BR!!
        //https://nakov.com/blog/2009/07/16/disable-certificate-validation-in-java-ssl-connections/

        download.getDownloadSize().setFileSize(DownloadFactory.getContentLength(url));
        download.getDownloadSize().setActFileSize(0);
        conn = DownloadFactory.getConn(url, ProgInfos.getUserAgent(),
                ProgConfig.SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND.getValue(), downloaded.getValue(),
                ProgConfig.SYSTEM_SSL_ALWAYS_TRUE.getValue());
        conn.connect();
        final int httpResponseCode = conn.getResponseCode();
        if (httpResponseCode >= HttpURLConnection.HTTP_BAD_REQUEST) {
            // Range passt nicht, also neue Verbindung versuchen...
            if (httpResponseCode == 416) {
                conn.disconnect();
                downloaded.setValue(0);
                conn = DownloadFactory.getConn(url, ProgInfos.getUserAgent(),
                        ProgConfig.SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND.getValue(), downloaded.getValue(),
                        ProgConfig.SYSTEM_SSL_ALWAYS_TRUE.getValue());
                conn.connect();

                // hier war es dann nun wirklich...
                if (conn.getResponseCode() >= HttpURLConnection.HTTP_BAD_REQUEST) {
                    download.setStateError();
                }
            } else {
                // ==================================
                // dann wars das
                responseCode = "Responsecode: " + conn.getResponseCode() + P2LibConst.LINE_SEPARATOR + conn.getResponseMessage();
                PLog.errorLog(915236798, "HTTP-Fehler: " + conn.getResponseCode() + ' ' + conn.getResponseMessage());
                if (download.getStart().getRestartCounter() == 0) {
                    // nur beim ersten Mal melden -> nervt sonst
                    Platform.runLater(() -> new DownloadErrorDialogController(download, responseCode));
                }
                download.setErrorMessage(responseCode);
                download.setStateError();
            }
        }
    }


    private boolean cancelDownload() {
        if (!file.exists()) {
            // dann ist alles OK
            return false;
        }
        dialogBreakIsVis = true;
        retBreak = true;
        Platform.runLater(() -> {
            retBreak = break_();
            dialogBreakIsVis = false;
        });
        while (dialogBreakIsVis) {
            try {
                wait(100);
            } catch (final Exception ignored) {

            }
        }
        return retBreak;
    }

    private boolean break_() {
        boolean cancel = false;
        if (file.exists()) {

            DownloadState.ContinueDownload result;
            boolean isNewName = false;

            if (ProgConfig.DOWNLOAD_CONTINUE.getValue() == DownloadState.DOWNLOAD_RESTART__CONTINUE) {
                //weiterführen
                result = DownloadState.ContinueDownload.CONTINUE_DOWNLOAD;

            } else if (ProgConfig.DOWNLOAD_CONTINUE.getValue() == DownloadState.DOWNLOAD_RESTART__RESTART) {
                //neu starten
                result = DownloadState.ContinueDownload.RESTART_DOWNLOAD;

            } else {
                //vorher fragen
                DownloadContinueDialogController downloadContinueDialogController =
                        new DownloadContinueDialogController(ProgConfig.DOWNLOAD_DIALOG_CONTINUE_SIZE,
                                progData, download, true /* weiterführen */);
                result = downloadContinueDialogController.getResult();
            }

            switch (result) {
                case CANCEL_DOWNLOAD:
                    // dann wars das
                    download.stopDownload();
                    cancel = true;
                    break;

                case CONTINUE_DOWNLOAD:
                    downloaded.set(file.length());
                    break;

                case RESTART_DOWNLOAD:
                    if (!isNewName) {
                        // dann mit gleichem Namen und Datei vorher löschen
                        try {
                            Files.deleteIfExists(file.toPath());
                            file = new File(download.getDestPathFile());
                        } catch (final Exception ex) {
                            // kann nicht gelöscht werden, evtl. klappt ja das Überschreiben
                            PLog.errorLog(915263654, ex,
                                    "file exists: " + download.getDestPathFile());
                        }
                    } else {
                        // dann mit neuem Namen
                        try {
                            Files.createDirectories(Paths.get(download.getDestPath()));
                        } catch (final IOException ignored) {
                        }
                        file = new File(download.getDestPathFile());
                    }
                    break;
            }
        }
        return cancel;
    }
}
