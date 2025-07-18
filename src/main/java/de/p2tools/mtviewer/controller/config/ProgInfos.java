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

package de.p2tools.mtviewer.controller.config;

import de.p2tools.mtviewer.Main;
import de.p2tools.p2lib.configfile.SettingsDirectory;
import de.p2tools.p2lib.mediathek.filmlistload.P2LoadConst;
import de.p2tools.p2lib.tools.P2Exception;
import de.p2tools.p2lib.tools.log.P2Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.ArrayList;

public class ProgInfos {

    public static String getUserAgent() {
        return ProgConfig.SYSTEM_USERAGENT.get();
    }


    /**
     * Retrieve the path to the program jar file.
     *
     * @return The program jar file path with a separator added.
     */
    public static String getPathJar() {
        // macht Probleme bei Win und Netzwerkpfaden, liefert dann Absolute Pfade zB. \\VBOXSVR\share\Mediathek\...
        final String pFilePath = "pFile";
        File propFile = new File(pFilePath);
        if (!propFile.exists()) {
            try {
                final CodeSource cS = Main.class.getProtectionDomain().getCodeSource();
                final File jarFile = new File(cS.getLocation().toURI().getPath());
                final String jarDir = jarFile.getParentFile().getPath();
                propFile = new File(jarDir + File.separator + pFilePath);
            } catch (final Exception ignored) {
            }
        }
        String s = propFile.getAbsolutePath().replace(pFilePath, "");
        if (!s.endsWith(File.separator)) {
            s = s + File.separator;
        }
        return s;
    }

    /**
     * Liefert den Pfad zur Audioliste
     *
     * @return Den Pfad als String
     */
    public static String getAudioListFile() {
        return getSettingsDirectory_String() + File.separator + P2LoadConst.AUDIO_LIST_FILE_JSON;
    }

    public static Path getAndMakeAudioListFile() {
        Path urlPath = null;
        try {
            urlPath = Paths.get(getAudioListFile());
            if (Files.notExists(urlPath)) {
                urlPath = Files.createFile(urlPath);
            }
        } catch (final IOException ex) {
            P2Log.errorLog(914579541, ex);
        }
        return urlPath;
    }

    /**
     * Liefert den Pfad zur Filmliste
     *
     * @return Den Pfad als String
     */
    public static String getFilmListFile() {
        String strFile;
        strFile = getSettingsDirectory_String() + File.separator + ProgConst.JSON_DATEI_FILME;

        return strFile;
    }

    public static String getLogDirectory_String() {
        final String logDir;
        if (ProgConfig.SYSTEM_LOG_DIR.get().isEmpty()) {
            logDir = getStandardLogDirectory_String();
        } else {
            logDir = ProgConfig.SYSTEM_LOG_DIR.get();
        }
        return logDir;
    }

    public static String getStandardLogDirectory_String() {
        return Paths.get(getSettingsDirectory_String(), ProgConst.LOG_DIR).toString();
    }

    /**
     * Return the path to "mtviewer.xml"
     *
     * @return Path object to mtviewer.xml file
     */
    public static Path getSettingsFile() {
        return SettingsDirectory.getSettingsFile(ProgData.configDir,
                ProgConst.CONFIG_DIRECTORY,
                ProgConst.CONFIG_FILE);
    }

    /**
     * Return the location of the settings directory. If it does not exist, create one.
     *
     * @return Path to the settings directory
     * @throws IllegalStateException Will be thrown if settings directory don't exist and if there is
     *                               an error on creating it.
     */
    public static Path getSettingsDirectory() throws P2Exception {
        return SettingsDirectory.getSettingsDirectory(ProgData.configDir,
                ProgConst.CONFIG_DIRECTORY);
    }

    public static String getSettingsDirectory_String() {
        return getSettingsDirectory().toString();
    }

    /**
     * Return the path to "mtviewer.xml_copy_" first copy exists
     *
     * @param xmlFilePath Path to file.
     */
    public void getMTViewerXmlCopyFilePath(ArrayList<Path> xmlFilePath) {
        for (int i = 1; i <= ProgConst.MAX_COPY_OF_BACKUPFILE; ++i) {
            final Path path = getSettingsDirectory().resolve(ProgConst.CONFIG_FILE_COPY + i);
            if (Files.exists(path)) {
                xmlFilePath.add(path);
            }
        }
    }
}
