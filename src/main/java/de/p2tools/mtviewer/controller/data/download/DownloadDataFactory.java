/*
 * P2tools Copyright (C) 2019 W. Xaver W.Xaver[at]googlemail.com
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


package de.p2tools.mtviewer.controller.data.download;

import de.p2tools.mtviewer.gui.dialog.DeleteFilmFileDialogController;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.mediathek.download.P2InfoFile;
import de.p2tools.p2lib.mediathek.download.P2Subtitle;
import de.p2tools.p2lib.tools.log.P2Log;

import java.io.File;
import java.nio.file.Path;

public class DownloadDataFactory {

    public static final String DOWNLOAD_PREFIX = "http";
    public static final String DOWNLOAD_SUFFIX_MP4 = "mp4,mp3,m4v,m4a";
    public static final String DOWNLOAD_SUFFIX_U3U8 = "m3u8";

    private DownloadDataFactory() {
    }

    public static void deleteFilmFile(DownloadData download) {
        // Download nur löschen, wenn er nicht läuft
        if (download == null) {
            return;
        }

        if (download.isStateStartedRun()) {
            P2Alert.showErrorAlert("Film löschen", "Download läuft noch", "Download erst stoppen!");
        }


        try {
            // Film
            File filmFile = new File(download.getDestPathFile());
            if (!filmFile.exists()) {
                P2Alert.showErrorAlert("Film löschen", "", "Die Datei existiert nicht!");
                return;
            }

            // Infofile
            File infoFile = null;
            if (download.isInfoFile()) {
                Path infoPath = P2InfoFile.getInfoFilePath(download.getFileNameWithoutSuffix());
                if (infoPath != null) {
                    infoFile = infoPath.toFile();
                }
            }

            // Unteritel
            File subtitleFile = null;
            if (download.isSubtitle()) {
                Path subtitlePath = P2Subtitle.getSubtitlePath(download.getUrlSubtitle(), download.getFileNameWithoutSuffix());
                if (subtitlePath != null) {
                    subtitleFile = subtitlePath.toFile();
                }
            }
            File subtitleFileSrt = null;
            if (download.isSubtitle()) {
                Path subtitlePathSrt = P2Subtitle.getSrtPath(download.getFileNameWithoutSuffix());
                if (subtitlePathSrt != null) {
                    subtitleFileSrt = subtitlePathSrt.toFile();
                }
            }

            String downloadPath = download.getDestPath();
            new DeleteFilmFileDialogController(downloadPath, filmFile, infoFile, subtitleFile, subtitleFileSrt);


        } catch (Exception ex) {
            P2Alert.showErrorAlert("Film löschen", "Konnte die Datei nicht löschen!", "Fehler beim löschen von:" + P2LibConst.LINE_SEPARATORx2 +
                    download.getDestPathFile());
            P2Log.errorLog(915236547, "Fehler beim löschen: " + download.getDestPathFile());
        }
    }

    public static boolean checkDownloadDirect(String url) {
        //auf direkte prüfen, pref oder suf: wenn angegeben dann muss es stimmen
        if (testPrefix(DOWNLOAD_PREFIX, url, true)
                && testPrefix(DOWNLOAD_SUFFIX_MP4, url, false)) {
            return true;
        }
        return false;
    }

    public static boolean checkDownloadM3U8(String url) {
        //auf direkte prüfen, pref oder suf: wenn angegeben dann muss es stimmen
        if (testPrefix(DOWNLOAD_PREFIX, url, true)
                && testPrefix(DOWNLOAD_SUFFIX_U3U8, url, false)) {
            return true;
        }
        return false;
    }

    public static boolean testPrefix(String str, String uurl, boolean prefix) {
        //prüfen ob url beginnt/endet mit einem Argument in str
        //wenn str leer dann true
        boolean ret = false;
        final String url = uurl.toLowerCase();
        String s1 = "";
        if (str.isEmpty()) {
            ret = true;
        } else {
            for (int i = 0; i < str.length(); ++i) {
                if (str.charAt(i) != ',') {
                    s1 += str.charAt(i);
                }
                if (str.charAt(i) == ',' || i >= str.length() - 1) {
                    if (prefix) {
                        //Präfix prüfen
                        if (url.startsWith(s1.toLowerCase())) {
                            ret = true;
                            break;
                        }
                    } else //Suffix prüfen
                        if (url.endsWith(s1.toLowerCase())) {
                            ret = true;
                            break;
                        }
                    s1 = "";
                }
            }
        }
        return ret;
    }
}
