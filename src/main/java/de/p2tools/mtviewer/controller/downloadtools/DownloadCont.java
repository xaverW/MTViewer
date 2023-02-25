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

import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.config.ProgInfos;
import de.p2tools.mtviewer.controller.data.download.DownloadData;
import de.p2tools.mtviewer.controller.data.download.DownloadDataFactory;
import de.p2tools.p2lib.mtdownload.MTInfoFile;
import de.p2tools.p2lib.mtdownload.MTSubtitle;
import de.p2tools.p2lib.tools.PSystemUtils;
import de.p2tools.p2lib.tools.date.DateFactory;
import javafx.beans.property.LongProperty;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.Timer;

public class DownloadCont {

    /**
     * Start the actual download process here.
     *
     * @throws Exception
     */
    public void downloadContent(DirectHttpDownload directHttpDownload, ProgData progData, DownloadData download,
                                HttpURLConnection conn, Timer bandwidthCalculationTimer,
                                File file, LongProperty downloaded) throws Exception {

        if (download.getDestPath().isEmpty()) {
            download.setDestPath(PSystemUtils.getStandardDownloadPath());
        }
        if (download.getDestFileName().isEmpty()) {
            download.setDestFileName(DateFactory.F_FORMAT_yyyyMMdd.format(new Date())
                    + "_" + download.getTheme() + "-" + download.getTitle() + ".mp4");
        }

        if (download.isInfoFile()) {
            //Infofile laden
            if (download.getFilm() == null) {
                MTInfoFile.writeInfoFile(download.getDestPath(), download.getDestPathFile(), download.getFileNameWithoutSuffix(),
                        download.getUrl(), download.getDownloadSize().toString(),
                        download.getChannel(), download.getTheme(), download.getTitle(),
                        download.getFilmDate().toString(), download.getFilmTime(), download.getDurationMinute() + "",
                        "", "");

            } else {
                MTInfoFile.writeInfoFile(download.getDestPath(), download.getDestPathFile(), download.getFileNameWithoutSuffix(),
                        download.getUrl(), download.getDownloadSize().toString(),
                        download.getFilm().getChannel(), download.getFilm().getTheme(), download.getFilm().getTitle(),
                        download.getFilm().getDate().toString(), download.getFilm().getTime(), download.getFilm().getDuration(),
                        download.getFilm().getWebsite(), download.getFilm().getDescription());
            }
        }

        if (download.isSubtitle()) {
            //Untertitel laden
            new MTSubtitle().writeSubtitle(download.getUrlSubtitle(), download.getFileNameWithoutSuffix(), download.getDestPath(),
                    ProgInfos.getUserAgent());
        }

        if (DownloadDataFactory.checkDownloadM3U8(download.getUrl())) {
            //dann ists ein http.m3u8
            if (conn != null) {
                conn.disconnect();
            }
            new ExternalProgramDownload(directHttpDownload, progData, download).startExt();

        } else {
            //dann ists ein http.mp4
            new DownloadMp4(progData, download).download(conn, bandwidthCalculationTimer, file, downloaded);
        }
    }
}
