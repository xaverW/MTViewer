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

import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.data.download.DownloadData;
import de.p2tools.mtviewer.controller.data.download.DownloadFactory;
import de.p2tools.p2lib.mediathek.filmdata.FilmData;
import de.p2tools.p2lib.mediathek.filmdata.FilmDataXml;
import de.p2tools.p2lib.mediathek.tools.FileNameUtils;
import de.p2tools.p2lib.tools.P2InfoFactory;
import de.p2tools.p2lib.tools.date.P2DateConst;
import de.p2tools.p2lib.tools.file.P2FileUtils;
import de.p2tools.p2lib.tools.log.P2Log;
import de.p2tools.p2lib.tools.net.PUrlTools;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.util.Date;

public class DownloadFileNameFactory {
    private DownloadFileNameFactory() {
    }

    public static String buildFileName(String url, String parameter) {
        // Tags ersetzen
        return parameter.replace("%f", url);
    }

    public static void buildFileNamePath(DownloadData downloadData) {
        String name, path;

        // ##############################################
        // Name
        // ##############################################
        name = ProgConfig.DOWNLOAD_FILE_NAME.getValueSafe();
        // Tags ersetzen
        if (downloadData.getFilm() != null) {
            name = replaceString(downloadData, name); // %D ... ersetzen
        }

        String suff = "";
        if (name.contains(".")) {
            // Suffix (und den . ) nicht ändern
            suff = name.substring(name.lastIndexOf("."));
            if (suff.length() <= 4 && suff.length() > 1) {
                // dann ist es sonst was??
                name = name.substring(0, name.lastIndexOf("."));
            } else {
                suff = "";
            }
        }

        name = replaceEmptyFileName(name,
                false /* pfad */,
                ProgConfig.SYSTEM_USE_REPLACETABLE.getValue(),
                ProgConfig.SYSTEM_ONLY_ASCII.getValue());
        name = name + suff;

        // prüfen ob das Suffix 2x vorkommt
        if (name.length() > 8) {
            final String suf1 = name.substring(name.length() - 8, name.length() - 4);
            final String suf2 = name.substring(name.length() - 4);
            if (suf1.startsWith(".") && suf2.startsWith(".")) {
                if (suf1.equalsIgnoreCase(suf2)) {
                    name = name.substring(0, name.length() - 4);
                }
            }
        }

        // Kürzen
        if (ProgConfig.SYSTEM_SAVE_MAX_SIZE.getValue() > 0) {
            int length = ProgConfig.SYSTEM_SAVE_MAX_SIZE.getValue();
            name = P2FileUtils.cutName(name, length);
        }

        // ##############################################
        // Pfad
        // ##############################################
        path = ProgConfig.DOWNLOAD_FILE_PATH.getValueSafe();
        path = replaceString(downloadData, path); // %D ... ersetzen
        if (path.endsWith(File.separator)) {
            path = path.substring(0, path.length() - 1);
        }

        // ###########################################################
        // zur Sicherheit bei Unsinn im Set
        if (path.isEmpty()) {
            path = P2InfoFactory.getStandardDownloadPath();
        }
        if (name.isEmpty()) {
            name = getToday_yyyyMMdd() + "_" + downloadData.getTheme() + "-" + downloadData.getTitle() + ".mp4";
        }

        // in Win dürfen die Pfade nicht länger als 255 Zeichen haben (für die Infodatei kommen noch
        // ".txt" dazu)
        final String[] pathName = {path, name};
        P2FileUtils.checkLengthPath(pathName);

        downloadData.setDestFileName(pathName[1]);
        downloadData.setDestPath(pathName[0]);
//        downloadData.setDestPathFile(P2FileUtils.addsPath(pathName[0], pathName[1]));
    }

    private static String replaceString(DownloadData downloadData, String replStr) {
        // hier wird nur ersetzt!
        // Felder mit variabler Länge, evtl. vorher kürzen

        FilmData film = downloadData.getFilm();
        if (film == null) {
            return replStr;
        }

        int length = ProgConfig.SYSTEM_SAVE_MAX_FIELD.getValue();
        replStr = replStr.replace("%t", getField(film.arr[FilmDataXml.FILM_THEME], length));
        replStr = replStr.replace("%T", getField(film.arr[FilmDataXml.FILM_TITLE], length));
        replStr = replStr.replace("%s", getField(film.arr[FilmDataXml.FILM_CHANNEL], length));
        replStr = replStr.replace("%N", getField(PUrlTools.getFileName(downloadData.getUrl()), length));

        // Felder mit fester Länge werden immer ganz geschrieben
        replStr = replStr.replace("%D",
                film.arr[FilmDataXml.FILM_DATE].equals("") ? getToday_yyyyMMdd()
                        : cleanDate(turnDate(film.arr[FilmDataXml.FILM_DATE])));
        replStr = replStr.replace("%d",
                film.arr[FilmDataXml.FILM_TIME].equals("") ? getNow_HHMMSS()
                        : cleanDate(film.arr[FilmDataXml.FILM_TIME]));
        replStr = replStr.replace("%H", getToday_yyyyMMdd());
        replStr = replStr.replace("%h", getNow_HHMMSS());

        replStr = replStr.replace("%1",
                getDMY("%1", film.arr[FilmDataXml.FILM_DATE].equals("") ? getToday__yyyy_o_MM_o_dd() : film.arr[FilmDataXml.FILM_DATE]));
        replStr = replStr.replace("%2",
                getDMY("%2", film.arr[FilmDataXml.FILM_DATE].equals("") ? getToday__yyyy_o_MM_o_dd() : film.arr[FilmDataXml.FILM_DATE]));
        replStr = replStr.replace("%3",
                getDMY("%3", film.arr[FilmDataXml.FILM_DATE].equals("") ? getToday__yyyy_o_MM_o_dd() : film.arr[FilmDataXml.FILM_DATE]));

        replStr = replStr.replace("%4",
                getHMS("%4", film.arr[FilmDataXml.FILM_TIME].equals("") ? getNow_HH_MM_SS() : film.arr[FilmDataXml.FILM_TIME]));
        replStr = replStr.replace("%5",
                getHMS("%5", film.arr[FilmDataXml.FILM_TIME].equals("") ? getNow_HH_MM_SS() : film.arr[FilmDataXml.FILM_TIME]));
        replStr = replStr.replace("%6",
                getHMS("%6", film.arr[FilmDataXml.FILM_TIME].equals("") ? getNow_HH_MM_SS() : film.arr[FilmDataXml.FILM_TIME]));

        replStr = replStr.replace("%i", String.valueOf(film.no));

        String res = "";
        if (downloadData.getUrl().equals(film.getUrlForResolution(FilmData.RESOLUTION_NORMAL))) {
            res = "H";
        } else if (downloadData.getUrl().equals(film.getUrlForResolution(FilmData.RESOLUTION_HD))) {
            res = "HD";
        } else if (downloadData.getUrl().equals(film.getUrlForResolution(FilmData.RESOLUTION_SMALL))) {
            res = "L";
        }
        replStr = replStr.replace("%q", res); // %q Qualität des Films ("HD", "H", "L")

        replStr = replStr.replace("%S", PUrlTools.getSuffixFromUrl(downloadData.getUrl()));
        replStr = replStr.replace("%Z", P2FileUtils.getHash(downloadData.getUrl()));
        replStr = replStr.replace("%z",
                P2FileUtils.getHash(downloadData.getUrl()) + "."
                        + PUrlTools.getSuffixFromUrl(downloadData.getUrl()));

        return replStr;
    }

    private static String getToday__yyyy_o_MM_o_dd() {
        return P2DateConst.F_FORMAT_dd_MM_yyyy.format(new Date());
    }

    private static String getNow_HHMMSS() {
        return P2DateConst.F_FORMAT_HHmm_ss.format(new Date());
    }

    private static String getNow_HH_MM_SS() {
        return P2DateConst.F_FORMAT_HH__mm__ss.format(new Date());
    }

    private static String getHMS(String s, String zeit) {
        // liefert die Zeit: Stunde, Minute, Sekunde aus "HH:mm:ss"
        // %4 - Stunde
        // %5 - Minute
        // %6 - Sekunde
        String ret = "";
        if (!zeit.equals("")) {
            try {
                if (zeit.length() == 8) {
                    switch (s) {
                        case "%4":
                            ret = zeit.substring(0, 2); // Stunde
                            break;
                        case "%5":
                            ret = zeit.substring(3, 5); // Minute
                            break;
                        case "%6":
                            ret = zeit.substring(6); // Sekunde
                            break;

                    }
                }
            } catch (final Exception ex) {
                P2Log.errorLog(775421006, ex, zeit);
            }
        }
        return ret;
    }

    private static String getDMY(String s, String datum) {
        // liefert das Datum: Jahr - Monat - Tag aus dd.MM.yyyy
        // %1 - Tag
        // %2 - Monat
        // %3 - Jahr
        String ret = "";
        if (!datum.equals("")) {
            try {
                if (datum.length() == 10) {
                    switch (s) {
                        case "%1":
                            ret = datum.substring(0, 2); // Tag
                            break;
                        case "%2":
                            ret = datum.substring(3, 5); // Monat
                            break;
                        case "%3":
                            ret = datum.substring(6); // Jahr
                            break;

                    }
                }
            } catch (final Exception ex) {
                P2Log.errorLog(775421006, ex, datum);
            }
        }
        return ret;
    }

    private static String getToday_yyyyMMdd() {
        return P2DateConst.F_FORMAT_yyyyMMdd.format(new Date());
    }

    private static String cleanDate(String date) {
        String ret;
        ret = date;
        ret = ret.replace(":", "");
        ret = ret.replace(".", "");
        return ret;
    }

    private static String turnDate(String date) {
        String ret = "";
        if (!date.equals("")) {
            try {
                if (date.length() == 10) {
                    String tmp = date.substring(6); // Jahr
                    tmp += "." + date.substring(3, 5); // Monat
                    tmp += "." + date.substring(0, 2); // Tag
                    ret = tmp;
                }
            } catch (final Exception ex) {
                P2Log.errorLog(775421006, ex, date);
            }
        }
        return ret;
    }

    private static String getField(String name, int length) {
        name = DownloadFactory.replaceEmptyFileName(name,
                ProgConfig.SYSTEM_USE_REPLACETABLE.getValue(),
                ProgConfig.SYSTEM_ONLY_ASCII.getValue());

        if (length <= 0) {
            return name;
        }

        if (name.length() > length) {
            name = name.substring(0, length);
        }
        return name;
    }

    /**
     * Entferne verbotene Zeichen aus Dateiname.
     *
     * @param name        Dateiname
     * @param isPath
     * @param userReplace
     * @param onlyAscii
     * @return Bereinigte Fassung
     */
    private static String replaceEmptyFileName(String name, boolean isPath, boolean userReplace, boolean onlyAscii) {
        String ret = name;
        boolean isWindowsPath = false;
        if (SystemUtils.IS_OS_WINDOWS && isPath && ret.length() > 1 && ret.charAt(1) == ':') {
            // damit auch "d:" und nicht nur "d:\" als Pfad geht
            isWindowsPath = true;
            ret = ret.replaceFirst(":", ""); // muss zum Schluss wieder rein, kann aber so nicht ersetzt werden
        }

        // zuerst die Ersetzungstabelle mit den Wünschen des Users
        if (userReplace) {
            ret = ProgData.getInstance().replaceList.replace(ret, isPath);
        }

        // und wenn gewünscht: "NUR Ascii-Zeichen"
        if (onlyAscii) {
            ret = FileNameUtils.convertToASCIIEncoding(ret, isPath);
        } else {
            ret = FileNameUtils.convertToNativeEncoding(ret, isPath);
        }

        if (isWindowsPath) {
            // c: wieder herstellen
            if (ret.length() == 1) {
                ret = ret + ":";
            } else if (ret.length() > 1) {
                ret = ret.charAt(0) + ":" + ret.substring(1);
            }
        }
        return ret;
    }
}
