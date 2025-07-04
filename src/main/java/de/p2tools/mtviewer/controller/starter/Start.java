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

import de.p2tools.mtviewer.controller.data.download.DownloadConstants;
import de.p2tools.mtviewer.controller.data.download.DownloadData;
import de.p2tools.p2lib.mediathek.download.MtInputStream;
import de.p2tools.p2lib.mediathek.tools.P2SizeTools;
import de.p2tools.p2lib.tools.date.P2Date;

public class Start {

    private int startCounter = 0;
    private int restartCounter = 0; // zählt die Anzahl der Neustarts bei einem Downloadfeheler->Summe Starts = erster Start + Restarts

    private boolean startViewing = false;

    private long bandwidth = -1; // Downloadbandbreite: bytes per second
    private long timeLeftSeconds = -1; // restliche Laufzeit [s] des Downloads

    private Process process = null; //Prozess des Download
    private P2Date startTime = null;
    private MtInputStream inputStream = null;

    private DownloadData download; //Referenz auf den Download dazu

    public Start(DownloadData download) {
        this.download = download;
    }

    public long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(long bandwidth) {
        this.bandwidth = bandwidth;
        download.setBandwidth(P2SizeTools.humanReadableByteCount(bandwidth, true));
    }

    public long getTimeLeftSeconds() {
        return timeLeftSeconds;
    }

    public void setTimeLeftSeconds(long timeLeftSeconds) {
        this.timeLeftSeconds = timeLeftSeconds;
        if (download.isStateStartedRun() && getTimeLeftSeconds() > 0) {
            download.setRemaining(DownloadConstants.getTimeLeft(timeLeftSeconds));
        } else {
            download.setRemaining("");
        }
    }

    public void startDownload() {
        setStartTime(new P2Date());
    }

    public boolean isStartViewing() {
        return startViewing;
    }

    public void setStartViewing(boolean startViewing) {
        this.startViewing = startViewing;
    }

    public P2Date getStartTime() {
        return startTime;
    }

    public void setStartTime(P2Date startTime) {
        this.startTime = startTime;
    }

    public MtInputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(MtInputStream inputStream) {
        this.inputStream = inputStream;
    }

    public int getStartCounter() {
        return startCounter;
    }

    public void setStartCounter(int startCounter) {
        this.startCounter = startCounter;
    }

    public int getRestartCounter() {
        return restartCounter;
    }

    public void setRestartCounter(int restartCounter) {
        this.restartCounter = restartCounter;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }
}
