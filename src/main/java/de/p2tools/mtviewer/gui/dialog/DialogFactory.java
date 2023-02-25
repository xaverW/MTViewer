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


package de.p2tools.mtviewer.gui.dialog;

import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgConst;
import de.p2tools.mtviewer.controller.data.download.DownloadTools;
import de.p2tools.p2lib.mtdownload.SizeTools;
import de.p2tools.p2lib.tools.PStringUtils;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import java.io.File;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;

public class DialogFactory {
    private DialogFactory() {

    }

    public static void saveComboPath(ComboBox<String> cbo, String path, StringProperty stringProperty) {
        final ArrayList<String> pathList = new ArrayList<>(cbo.getItems());
        //und den eingestellten Downloadpfad an den Anfang stellen
        if (pathList.contains(path)) {
            pathList.remove(path);
        }
        pathList.add(0, path);

        //jetzt alle gesammelten Pfade speichern
        final ArrayList<String> path2 = new ArrayList<>();
        for (String s1 : pathList) {
            //um doppelte auszusortieren
            final String s2 = StringUtils.removeEnd(s1, File.separator);
            if (!path2.contains(s1) && !path2.contains(s2)) {
                path2.add(s2);
            }

            if (path2.size() > ProgConst.MAX_DEST_PATH_IN_DIALOG_DOWNLOAD) {
                // die Anzahl der Einträge begrenzen
                break;
            }
        }

        String savePath = PStringUtils.appendList(path2, "<>", true, true);
        stringProperty.setValue(savePath);
    }

    /**
     * Calculate free disk space on volume and checkIfExists if the movies can be safely downloaded.
     */
    public static void calculateAndCheckDiskSpace(String path, Label lblFree,
                                                  String fileSize_HD, String fileSize_high, String fileSize_small) {
        if (path == null || path.isEmpty()) {
            return;
        }
        try {
            String noSize = "";
            String sizeFree = "";

            long usableSpace = getFreeDiskSpace(path);
            if (usableSpace > 0) {
                sizeFree = SizeTools.humanReadableByteCount(usableSpace, true);
            }

            // jetzt noch prüfen, obs auf die Platte passt
            usableSpace /= 1_000_000;
            if (usableSpace <= 0) {
                lblFree.setText("");

            } else {
                int size;
                if (!fileSize_HD.isEmpty()) {
                    size = Integer.parseInt(fileSize_HD);
                    if (size > usableSpace) {
                        noSize = ", nicht genug für HD";

                    }
                }
                if (!fileSize_high.isEmpty()) {
                    size = Integer.parseInt(fileSize_high);
                    if (size > usableSpace) {
                        noSize = ", nicht genug für \"hoch\"";
                    }
                }
                if (!fileSize_small.isEmpty()) {
                    size = Integer.parseInt(fileSize_small);
                    if (size > usableSpace) {
                        noSize = ", nicht genug für \"klein\"";
                    }
                }

                if (noSize.isEmpty()) {
                    lblFree.setText(" [ noch frei: " + sizeFree + " ]");
                } else {
                    lblFree.setText(" [ noch frei: " + sizeFree + noSize + " ]");
                }
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Get the free disk space for a selected path.
     *
     * @return Free disk space in bytes.
     */
    private static long getFreeDiskSpace(final String strPath) {
        long usableSpace = 0;
        if (!strPath.isEmpty()) {
            try {
                Path path = Paths.get(strPath);
                if (!Files.exists(path)) {
                    path = path.getParent();
                }
                final FileStore fileStore = Files.getFileStore(path);
                usableSpace = fileStore.getUsableSpace();
            } catch (final Exception ignore) {
            }
        }
        return usableSpace;
    }

    public static String getNextName(String stdPath, String actDownPath, String theme) {
        String ret = actDownPath;

        theme = DownloadTools.replaceEmptyFileName(theme,
                ProgConfig.SYSTEM_USE_REPLACETABLE.getValue(),
                ProgConfig.SYSTEM_ONLY_ASCII.getValue());

        if (actDownPath.endsWith(File.separator)) {
            ret = actDownPath.substring(0, actDownPath.length() - File.separator.length());
        }

        try {
            final String date = FastDateFormat.getInstance("yyyyMMdd").format(new Date());
            final boolean isDate = getTime(ret, FastDateFormat.getInstance("yyyyMMdd"));
            final boolean isTheme = ret.endsWith(theme) && !theme.isEmpty();
            final boolean isStandard = actDownPath.equals(stdPath);

            if (isStandard) {
                Path path = Paths.get(stdPath, (theme.isEmpty() ? date : theme));
                ret = path.toString();

            } else if (isTheme) {
                Path path = Paths.get(stdPath, date);
                ret = path.toString();

            } else if (isDate) {
                Path path = Paths.get(stdPath);
                ret = path.toString();

            } else {
                Path path = Paths.get(stdPath);
                ret = path.toString();

            }
        } catch (Exception ex) {
            PLog.errorLog(978451203, ex);
            ret = stdPath;
        }
        return ret;
    }

    private static boolean getTime(String name, FastDateFormat format) {
        String ret = "";
        Date d;
        try {
            ret = name.substring(name.lastIndexOf(File.separator) + 1);
            d = new Date(format.parse(ret).getTime());
        } catch (Exception ignore) {
            d = null;
        }

        if (d != null && format.getPattern().length() == ret.length()) {
            return true;
        }
        return false;
    }
}
