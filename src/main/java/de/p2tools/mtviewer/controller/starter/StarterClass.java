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

package de.p2tools.mtviewer.controller.starter;

import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgConst;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.data.download.DownloadConstants;
import de.p2tools.mtviewer.controller.data.download.DownloadData;
import de.p2tools.mtviewer.controller.downloadTools.DirectHttpDownload;
import de.p2tools.mtviewer.controller.filmlist.loadFilmlist.ListenerFilmlistLoadEvent;
import de.p2tools.mtviewer.controller.filmlist.loadFilmlist.ListenerLoadFilmlist;
import de.p2tools.p2Lib.MTDownload.SizeTools;
import de.p2tools.p2Lib.P2LibConst;
import de.p2tools.p2Lib.guiTools.pNotification.PNotification;
import de.p2tools.p2Lib.tools.date.PDate;
import de.p2tools.p2Lib.tools.date.PDateFactory;
import de.p2tools.p2Lib.tools.log.PLog;

import java.io.File;
import java.util.ArrayList;

public class StarterClass {
    private final ProgData progData;
    private StarterThread starterThread = null;
    private boolean paused = false;
    private boolean searchFilms = true; // beim Programmstart muss zuerst die Filmliste galaden werden
    private boolean checkQuitAfterDownload = false; // Prüfen, ob automode aktiv ist

    // ===================================
    // Public
    // ===================================
    public StarterClass(ProgData progData) {
        this.progData = progData;
        starterThread = new StarterThread();
        starterThread.start();

        progData.loadFilmlist.addListenerLoadFilmlist(new ListenerLoadFilmlist() {
            @Override
            public void start(ListenerFilmlistLoadEvent event) {
                searchFilms = true;
            }

            @Override
            public void finished(ListenerFilmlistLoadEvent event) {
                searchFilms = false;
            }
        });
    }

    public static boolean check(ProgData progData, DownloadData download) {
        // prüfen, ob der Download geklappt hat und die Datei existiert und eine min. Größe hat
        boolean ret = false;

        final double progress = download.getProgress();

        if (progress > DownloadConstants.PROGRESS_NOT_STARTED && progress < DownloadConstants.PROGRESS_NEARLY_FINISHED) {
            // *progress* Prozent werden berechnet und es wurde vor 99,5% abgebrochen
            PLog.errorLog(696510258, "Download fehlgeschlagen: 99,5% wurden nicht erreicht: " + progress + "%, " + download.getDestPathFile());
            return false;
        }

        final File file = new File(download.getDestPathFile());
        if (!file.exists()) {
            PLog.errorLog(550236231, "Download fehlgeschlagen: Datei existiert nicht: " + download.getDestPathFile());
        } else if (file.length() < ProgConst.MIN_DATEI_GROESSE_FILM) {
            PLog.errorLog(795632500, "Download fehlgeschlagen: Datei zu klein: " + download.getDestPathFile());
        } else {
            ret = true;
        }
        return ret;
    }

    /**
     * Delete the file if filesize is less that a constant value.
     *
     * @param file The file which is to be deleted.
     */
    public static void deleteIfEmpty(File file) {
        try {
            if (file.exists()) {
                // zum Wiederstarten/Aufräumen die leer/zu kleine Datei löschen, alles auf Anfang
                if (file.length() == 0) {
                    // zum Wiederstarten/Aufräumen die leer/zu kleine Datei löschen, alles auf Anfang
                    PLog.sysLog(new String[]{"Restart/Aufräumen: leere Datei löschen", file.getAbsolutePath()});
                    if (!file.delete()) {
                        throw new Exception();
                    }
                } else if (file.length() < ProgConst.MIN_DATEI_GROESSE_FILM) {
                    PLog.sysLog(new String[]{"Restart/Aufräumen: Zu kleine Datei löschen", file.getAbsolutePath()});
                    if (!file.delete()) {
                        throw new Exception();
                    }
                }
            }
        } catch (final Exception ex) {
            PLog.errorLog(795632500, "Fehler beim löschen" + file.getAbsolutePath());
        }
    }

    public static void startMsg(DownloadData download) {
        final ArrayList<String> list = new ArrayList<>();
        list.add(PLog.LILNE3);
        if (download.getStart().getRestartCounter() > 0) {
            list.add("Download starten - Restart (Summe Starts: " + download.getStart().getRestartCounter() + ')');
        } else {
            list.add("Download starten");
        }
        list.add("Ziel: " + download.getDestPathFile());
        list.add("URL: " + download.getUrl());
        list.add("Startzeit: " + PDateFactory.F_FORMAT_HH_mm_ss.format(download.getStart().getStartTime()));
        list.add(DownloadConstants.TYPE_DOWNLOAD);
        list.add(PLog.LILNE_EMPTY);
        PLog.sysLog(list.toArray(new String[list.size()]));
    }

    private static void finishedMsg(final DownloadData download) {
        final Start start = download.getStart();
        final ArrayList<String> list = new ArrayList<>();
        list.add(PLog.LILNE3);
        if (download.isStateStoped()) {
            list.add("Download wurde abgebrochen");

        } else {
            if (download.isStateFinished()) {
                // dann ists gut
                list.add("Download ist fertig und hat geklappt");
            } else if (download.isStateError()) {
                list.add("Download ist fertig und war fehlerhaft");
            }
            list.add("Ziel: " + download.getDestPathFile());
        }

        list.add("Startzeit: " + PDateFactory.F_FORMAT_HH_mm_ss.format(start.getStartTime()));
        list.add("Endzeit: " + PDateFactory.F_FORMAT_HH_mm_ss.format(new PDate().getTime()));

        if (start.getRestartCounter() > 0) {
            list.add("Restarts: " + start.getRestartCounter());
        }

        final long dauer = start.getStartTime().diffInMinutes();
        if (dauer == 0) {
            list.add("Dauer: " + start.getStartTime().diffInSeconds() + " s");
            //list.add("Dauer: <1 Min.");
        } else {
            list.add("Dauer: " + start.getStartTime().diffInMinutes() + " Min");
        }

        if (start.getInputStream() != null) {
            list.add("Bytes gelesen: " + SizeTools.humanReadableByteCount(start.getInputStream().getSumByte(), true));
            list.add("Bandbreite: " + SizeTools.humanReadableByteCount(start.getInputStream().getSumBandwidth(), true));
        }
        list.add("URL: " + download.getUrl());
        list.add(DownloadConstants.TYPE_DOWNLOAD);
        list.add(PLog.LILNE_EMPTY);
        PLog.sysLog(list);

        if (ProgConfig.DOWNLOAD_SHOW_NOTIFICATION.get() && !download.isStateStoped()) {
            //war ein Abo und wurde nicht abgebrochen
            addNotification(download);
        }
    }

    public static void addNotification(DownloadData download) {
        boolean error = download.isStateError();
        String text = ("Film:   " + download.getTitle() + P2LibConst.LINE_SEPARATOR +
                "Sender: " + download.getChannel() + P2LibConst.LINE_SEPARATOR +
                "Größe:  " + SizeTools.humanReadableByteCount(download.getDownloadSize().getFilmSize(), true) + P2LibConst.LINE_SEPARATOR +
                (error ? "Download war fehlerhaft" : "Download war erfolgreich"));
        PNotification.addNotification("Download beendet", text, error);
    }

    public static void finalizeDownload(DownloadData download) {

        final Start start = download.getStart();
        deleteIfEmpty(new File(download.getDestPathFile()));
        setFileSize(download);

        finishedMsg(download);

        if (download.isStateError()) {
            download.setProgress(DownloadConstants.PROGRESS_NOT_STARTED);
        } else if (!download.isStateStoped()) {
            //dann ist er gelaufen
            start.setTimeLeftSeconds(0);
            download.setProgress(DownloadConstants.PROGRESS_FINISHED);
            download.getDownloadSize().setActFileSize(-1);

            if (start.getInputStream() != null) {
                download.setBandwidth("Ø " + SizeTools.humanReadableByteCount(start.getInputStream().getSumBandwidth(), true));
            }

            final long dauer = start.getStartTime().diffInMinutes();
            if (dauer == 0) {
                download.setRemaining("Dauer: " + start.getStartTime().diffInSeconds() + " s");
            } else {
                download.setRemaining("Dauer: " + start.getStartTime().diffInMinutes() + " Min");
            }
        }

        download.setNo(DownloadConstants.DOWNLOAD_NUMBER_NOT_STARTED);
        start.setProcess(null);
        start.setInputStream(null);
        start.setStartTime(null);

    }

    /**
     * tatsächliche Dateigröße eintragen
     *
     * @param download {@link DownloadData} with the info of the file
     */

    public static void setFileSize(DownloadData download) {
        try {
            final File destFile = new File(download.getDestPathFile());
            if (destFile.exists()) {
                final long length = destFile.length();
                if (length > 0)
                    if (download.getDownloadSize().getFilmSize() > 0) {
                        //nur wenn der Download schon eine Größe hatte, nicht bei m3u8!
                        download.getDownloadSize().setSize(length);
                    } else {
                        //bei m3u8 nur die aktSize setzen!
                        download.getDownloadSize().setActFileSize(length);
                    }
            }
        } catch (
                final Exception ex) {
            PLog.errorLog(461204780,
                    "Fehler beim Ermitteln der Dateigröße: " + download.getDestPathFile());
        }
    }

    public void setPaused() {
        paused = true;
    }

    private void restartMsg(DownloadData download) {
        final ArrayList<String> text = new ArrayList<>();
        text.add("Fehlerhaften Download neu starten - Restart (Summe Starts: " + download.getStart().getRestartCounter() + ')');
        text.add("Ziel: " + download.getDestPathFile());
        text.add("URL: " + download.getUrl());
        PLog.sysLog(text.toArray(new String[text.size()]));
    }

    // ********************************************
    // Hier wird dann gestartet
    // ewige Schleife die die Downloads startet
    // ********************************************
    private class StarterThread extends Thread {

        private final java.util.Timer bandwidthCalculationTimer;
        private DownloadData download;

        public StarterThread() {
            super();
            setName("DownloadStarter Daemon Thread");
            setDaemon(true);
            bandwidthCalculationTimer = new java.util.Timer("BandwidthCalculationTimer");
        }

        @Override
        public synchronized void run() {
            while (!isInterrupted()) {
                try {
                    if (searchFilms) {
                        // da machmer nix
                        sleep(5 * 1000);
                        continue;
                    }

                    while ((download = getNextStart()) != null) {
                        if (searchFilms) {
                            break;
                        }

                        startDownload(download);
                        // alle 5 Sekunden einen Download starten
                        sleep(5 * 1000);
                    }
                    if (searchFilms) {
                        continue;
                    }

                    if (!checkQuitAfterDownload) {
                        // ist für den auto mode
                        quitProgramAfterDownload();
                    }

                    sleep(3 * 1000);
                } catch (final Exception ex) {
                    PLog.errorLog(613822015, ex);
                }
            }
        }

        private void quitProgramAfterDownload() {
            checkQuitAfterDownload = true;
        }

        private synchronized DownloadData getNextStart() throws InterruptedException {
            //ersten passenden Download der Liste zurückgeben oder null und versuchen,
            //dass bei mehreren laufenden Downloads ein anderer Sender gesucht wird
            if (paused) {
                //beim Löschen der Downloads kann das Starten etwas "pausiert" werden
                //damit ein zu löschender Download nicht noch schnell gestartet wird
                sleep(5 * 1000);
                paused = false;
            }

            DownloadData download = progData.downloadList.getNextStart();
            if (download == null) {
                // dann versuchen einen Fehlerhaften nochmal zu starten
                download = progData.downloadList.getRestartDownload();
                if (download != null) {
                    restartMsg(download);
                }
            }
            return download;
        }

        /**
         * This will start the download process.
         *
         * @param download The {@link DownloadData} info object for download.
         */
        private void startDownload(DownloadData download) {
            download.getStart().startDownload();
            Thread downloadThread;

            downloadThread = new DirectHttpDownload(progData, download, bandwidthCalculationTimer);
            downloadThread.start();
        }
    }
}