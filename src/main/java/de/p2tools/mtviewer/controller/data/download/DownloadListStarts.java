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

package de.p2tools.mtviewer.controller.data.download;

import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgConst;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.p2lib.tools.log.P2Log;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DownloadListStarts {
    private final ProgData progData;
    private final DownloadList downloadList;

    public DownloadListStarts(ProgData progData, DownloadList downloadList) {
        this.progData = progData;
        this.downloadList = downloadList;
    }

    public DownloadData getRestartDownload() {
        //Versuch einen fehlgeschlagenen Download zu finden, um ihn, wieder zu starten
        //die Fehler laufen aber einzeln, vorsichtshalber
        if (!getDown(1)) {
            // dann läuft noch einer
            return null;
        }

        for (final DownloadData download : downloadList) {
            if (download.isStateInit()) {
                continue;
            }

            if (download.isStateError()
                    && download.getStart().getRestartCounter() < ProgConfig.SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART.getValue()
                    && !maxChannelPlay(download, 1)) {

                int restarted = download.getStart().getRestartCounter();
                download.resetDownload();
                progData.downloadList.startDownloads(download);
                // UND jetzt den Restartcounter wieder setzen!!
                download.getStart().setRestartCounter(++restarted);
                return download;
            }
        }
        return null;
    }

    /**
     * Return a List of all loading but not yet finished downloads.
     *
     * @return A list with all download objects.
     */
    synchronized List<DownloadData> getListOfStartsNotFinished() {
        final List<DownloadData> activeDownloadData = new ArrayList<>();

        activeDownloadData.addAll(downloadList.stream().filter(download -> download.isStateStartedRun())
                .collect(Collectors.toList()));

        return activeDownloadData;
    }

    /**
     * Return a List of all started but not loading downloads.
     *
     * @return A list with all download objects.
     */
    synchronized List<DownloadData> getListOfStartsNotLoading() {
        final List<DownloadData> activeDownloadData = new ArrayList<>();

        activeDownloadData.addAll(downloadList.stream().filter(download -> download.isStateStartedWaiting())
                .collect(Collectors.toList()));

        return activeDownloadData;
    }

    public synchronized DownloadData getNextStart() {
        //ersten passenden Download der Liste zurückgeben oder null
        //und versuchen, dass bei mehreren laufenden Downloads ein anderer Sender gesucht wird
        DownloadData ret = null;
        if (downloadList.size() > 0 && getDown(ProgConfig.DOWNLOAD_MAX_DOWNLOADS.getValue())) {
            final DownloadData download = nextStart();
            if (download != null && download.isStateStartedWaiting()) {
                ret = download;
            }
        }
        return ret;
    }

    private DownloadData nextStart() {
        // Download mit der kleinsten Nr finden der zu Starten ist
        // erster Versuch, Start mit einem anderen Sender
        DownloadData tmpDownload = searchNextDownload(1);
        if (tmpDownload != null) {
            // einer wurde gefunden
            return tmpDownload;
        }

        // zweiter Versuch, Start mit einem passenden Sender
        tmpDownload = searchNextDownload(ProgConst.MAX_SENDER_FILME_LADEN);
        return tmpDownload;
    }

    private DownloadData searchNextDownload(int maxProChannel) {
        DownloadData tmpDownload = null;
        int nr = -1;
        for (DownloadData download : downloadList) {
            if (download.isStateStartedWaiting() &&
                    !maxChannelPlay(download, maxProChannel) &&
                    (nr == -1 || download.getNo() < nr)) {

                tmpDownload = download;
                nr = tmpDownload.getNo();
            }
        }

        return tmpDownload;
    }

    private boolean maxChannelPlay(DownloadData d, int max) {
        // true wenn bereits die maxAnzahl pro Sender läuft
        try {
            int counter = 0;
            final String host = getHost(d);
            if (host.equals("akamaihd.net")) {
                // content delivery network
                return false;
            }

            for (final DownloadData download : downloadList) {
                if (download.isStateStartedRun() && getHost(download).equalsIgnoreCase(host)) {
                    counter++;
                    if (counter >= max) {
                        return true;
                    }
                }
            }
            return false;
        } catch (final Exception ex) {
            return false;
        }
    }

    private String getHost(DownloadData download) {
        String host = "";
        try {
            try {
                String uurl = download.getUrl();
                // die funktion "getHost()" kann nur das Protokoll "http" ??!??
                if (uurl.startsWith("rtmpt:")) {
                    uurl = uurl.toLowerCase().replace("rtmpt:", "http:");
                }
                if (uurl.startsWith("rtmp:")) {
                    uurl = uurl.toLowerCase().replace("rtmp:", "http:");
                }
                if (uurl.startsWith("mms:")) {
                    uurl = uurl.toLowerCase().replace("mms:", "http:");
                }
                final URL url = new URL(uurl);
                String tmp = url.getHost();
                if (tmp.contains(".")) {
                    host = tmp.substring(tmp.lastIndexOf('.'));
                    tmp = tmp.substring(0, tmp.lastIndexOf('.'));
                    if (tmp.contains(".")) {
                        host = tmp.substring(tmp.lastIndexOf('.') + 1) + host;
                    } else if (tmp.contains("/")) {
                        host = tmp.substring(tmp.lastIndexOf('/') + 1) + host;
                    } else {
                        host = "host";
                    }
                }
            } catch (final Exception ex) {
                // für die Hosts bei denen das nicht klappt
                // Log.systemMeldung("getHost 1: " + s.download.arr[DatenDownload.DOWNLOAD_URL_NR]);
                host = "host";
            } finally {
                if (host.isEmpty()) {
                    // Log.systemMeldung("getHost 3: " + s.download.arr[DatenDownload.DOWNLOAD_URL_NR]);
                    host = "host";
                }
            }
        } catch (final Exception ex) {
            // Log.systemMeldung("getHost 4: " + s.download.arr[DatenDownload.DOWNLOAD_URL_NR]);
            host = "exception";
        }
        return host;
    }

    private boolean getDown(int max) {
        int count = 0;
        try {
            for (final DownloadData download : downloadList) {
                if (download.isStateStartedRun()) {
                    ++count;
                    if (count >= max) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception ex) {
            P2Log.errorLog(794519083, ex);
        }
        return false;
    }
}
