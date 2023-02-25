/*
 * P2tools Copyright (C) 2022 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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

import de.p2tools.mtviewer.controller.data.download.DownloadData;

import java.io.File;

public class HttpDownloadFactory {
    private HttpDownloadFactory() {
    }

    public static boolean checkPathWritable(String path) {
        boolean ret = false;
        final File testPath = new File(path);
        try {
            if (!testPath.exists()) {
                testPath.mkdirs();
            }
            if (path.isEmpty()) {
            } else if (!testPath.isDirectory()) {
            } else if (testPath.canWrite()) {
                final File tmpFile = File.createTempFile("mtviewer", "tmp", testPath);
                tmpFile.delete();
                ret = true;
            }
        } catch (final Exception ignored) {
        }
        return ret;
    }

    public static void canAlreadyStarted(DownloadData downloadData) {
        if (downloadData.getFilm() != null && downloadData.isStateStartedRun()) {

            if (downloadData.getFilm().getDurationMinute() > 0
                    && downloadData.getStart().getTimeLeftSeconds() > 0
                    && downloadData.getDownloadSize().getActFileSize() > 0
                    && downloadData.getDownloadSize().getSize() > 0) {

                // macht nur dann Sinn
                final long filetimeAlreadyLoadedSeconds = downloadData.getFilm().getDurationMinute() * 60
                        * downloadData.getDownloadSize().getActFileSize()
                        / downloadData.getDownloadSize().getSize();

                if (filetimeAlreadyLoadedSeconds > (downloadData.getStart().getTimeLeftSeconds() * 1.1 /* plus 10% zur Sicherheit */)) {
                    downloadData.getStart().setStartViewing(true);
                }
            }
        }
    }
}
