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
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.p2lib.mediathek.tools.P2FileNameUtils;
import de.p2tools.p2lib.mediathek.tools.P2SizeTools;
import de.p2tools.p2lib.tools.P2InfoFactory;
import de.p2tools.p2lib.tools.file.P2FileUtils;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.scene.control.Label;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DownloadFactory {
    private static final DecimalFormat f1 = new DecimalFormat("##");
    private static final DecimalFormat f2 = new DecimalFormat("##.0");

    private DownloadFactory() {
    }

    /**
     * Calculate free disk space on volume and check if the movies can be safely downloaded.
     */
    public static void calculateAndCheckDiskSpace(DownloadData download, String path, Label lblSizeFree) {
        if (path == null || path.isEmpty()) {
            return;
        }
        try {
            String noSize = "";
            long usableSpace = P2FileUtils.getFreeDiskSpace(path);
            String sizeFree = "";
            if (usableSpace == 0) {
                lblSizeFree.setText("");
            } else {
                sizeFree = P2SizeTools.humanReadableByteCount(usableSpace, true);
            }

            // jetzt noch prüfen, obs auf die Platte passt
            usableSpace /= 1_000_000;
            if (usableSpace > 0) {
                long size = download.getDownloadSize().getTargetSize();
                size /= 1_000_000;
                if (size > usableSpace) {
                    noSize = " [ nicht genug Speicher: ";

                }
            }

            if (noSize.isEmpty()) {
                lblSizeFree.setText(" [ noch frei: " + sizeFree + " ]");
            } else {
                lblSizeFree.setText(noSize + sizeFree + " ]");
            }


        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String getDownloadPath() {
        return ProgConfig.START_DIALOG_DOWNLOAD_PATH.get().isEmpty() ?
                P2InfoFactory.getStandardDownloadPath() : ProgConfig.START_DIALOG_DOWNLOAD_PATH.get();
    }

    /**
     * Entferne verbotene Zeichen aus Dateiname.
     *
     * @param name        Dateiname
     * @param userReplace
     * @param onlyAscii
     * @return Bereinigte Fassung
     */
    public static String replaceEmptyFileName(String name, boolean userReplace, boolean onlyAscii) {
        String ret = name;
        // zuerst die Ersetzungstabelle mit den Wünschen des Users
        if (userReplace) {
            ret = ProgData.getInstance().replaceList.replace(ret, false);
        }

        // und wenn gewünscht: "NUR Ascii-Zeichen"
        if (onlyAscii) {
            ret = P2FileNameUtils.convertToASCIIEncoding(ret, false);
        } else {
            ret = P2FileNameUtils.convertToNativeEncoding(ret, false);
        }

        return ret;
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
    public static String replaceEmptyFileName(String name, boolean isPath, boolean userReplace, boolean onlyAscii) {
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
            ret = P2FileNameUtils.convertToASCIIEncoding(ret, isPath);
        } else {
            ret = P2FileNameUtils.convertToNativeEncoding(ret, isPath);
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

    public static void checkDoubleNames(List<DownloadData> foundDownloads, List<DownloadData> downloadList) {
        // prüfen ob schon ein Download mit dem Zieldateinamen in der Downloadliste existiert
        try {
            final List<DownloadData> alreadyDone = new ArrayList<>();

            foundDownloads.stream().forEach(download -> {
                final String oldName = download.getDestFileName();
                String newName = oldName;
                int i = 1;
                while (searchName(downloadList, newName) || searchName(alreadyDone, newName)) {
                    newName = getNewName(oldName, ++i);
                }

                if (!oldName.equals(newName)) {
                    download.setDestFileName(newName);
                }

                alreadyDone.add(download);
            });
        } catch (final Exception ex) {
            P2Log.errorLog(303021458, ex);
        }
    }

    private static String getNewName(String oldName, int i) {
        String base = FilenameUtils.getBaseName(oldName);
        String suff = FilenameUtils.getExtension(oldName);
        return base + "_" + i + "." + suff;
    }

    private static boolean searchName(List<DownloadData> searchDownloadList, String name) {
        return searchDownloadList.stream().filter(download -> download.getDestFileName().equals(name)).findAny().isPresent();
    }

}
