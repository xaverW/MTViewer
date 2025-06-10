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

import de.p2tools.mtviewer.MTViewerController;
import de.p2tools.mtviewer.controller.data.MTShortcut;
import de.p2tools.mtviewer.controller.data.ReplaceList;
import de.p2tools.mtviewer.controller.data.download.DownloadInfos;
import de.p2tools.mtviewer.controller.data.download.DownloadList;
import de.p2tools.mtviewer.controller.data.film.FilmListMtc;
import de.p2tools.mtviewer.controller.filmfilter.ActFilmFilterWorker;
import de.p2tools.mtviewer.controller.filmfilter.FilmFilterRunner;
import de.p2tools.mtviewer.controller.starter.StarterClass;
import de.p2tools.mtviewer.controller.worker.CheckForNewFilmlist;
import de.p2tools.mtviewer.controller.worker.Worker;
import de.p2tools.mtviewer.gui.FilmGuiPack;
import de.p2tools.mtviewer.gui.dialog.QuitDialogController;
import de.p2tools.p2lib.guitools.pmask.P2MaskerPane;
import de.p2tools.p2lib.mediathek.filmdata.FilmData;
import de.p2tools.p2lib.mediathek.filmdata.Filmlist;
import de.p2tools.p2lib.p2event.P2EventHandler;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.stage.Stage;

public class ProgData {
    private static ProgData instance;

    // flags
    public static boolean debug = false; // Debugmodus
    public static boolean duration = false; // Duration ausgeben
    public static boolean reset = false; // Programm auf Starteinstellungen zurücksetzen
    public static boolean raspberry = false; // läuft auf einem Raspberry
    public static boolean firstProgramStart = false; // ist der allererste Programmstart: Init wird gemacht
    public static BooleanProperty FILMLIST_IS_DOWNLOADING = new SimpleBooleanProperty(Boolean.FALSE); // dann wird eine Filmliste geladen
    public static BooleanProperty AUDIOLIST_IS_DOWNLOADING = new SimpleBooleanProperty(Boolean.FALSE); // dann wird eine Audioliste geladen

    public P2EventHandler pEventHandler;

    // Infos
    public static String configDir = ""; // Verzeichnis zum Speichern der Programmeinstellungen
    public static String filmListUrl = ""; //URL von der die Filmliste geladen werde soll

    public MTShortcut mtShortcut; // verwendete Shortcuts
    public ActFilmFilterWorker actFilmFilterWorker; // gespeicherte Filterprofile
    public FilmFilterRunner filmFilterRunner;
    public DownloadList downloadList; // Filme die als "Download" geladen werden sollen
    public StarterClass starterClass; // Klasse zum Ausführen der Programme (für die Downloads): VLC, flvstreamer, ...

    // Gui
    public Stage primaryStage = null;
    public P2MaskerPane maskerPane = new P2MaskerPane();
    public MTViewerController mtViewerController = null;
    public FilmGuiPack filmGuiPack = null; // Tab mit den Filmen
    public QuitDialogController quitDialogController = null;
    // Worker
    public Worker worker; // Liste aller Sender, Themen, ...
    public CheckForNewFilmlist checkForNewFilmlist;

    public FilmListMtc filmlist; // ist die komplette Filmliste
    public Filmlist<FilmData> audioList; // ist die komplette Audioliste
    public FilmListMtc filmlistUsed; // ist die verwendete Filmliste

    public DownloadInfos downloadInfos;
    public ReplaceList replaceList;

    private ProgData() {
        pEventHandler = new P2EventHandler(false);
        mtShortcut = new MTShortcut();
        replaceList = new ReplaceList();

        actFilmFilterWorker = new ActFilmFilterWorker(this);

        filmlist = new FilmListMtc();
        audioList = new Filmlist<>();
        filmlistUsed = new FilmListMtc();

        downloadList = new DownloadList(this);

        starterClass = new StarterClass(this);
        downloadInfos = new DownloadInfos(this);
        filmFilterRunner = new FilmFilterRunner(this);
        worker = new Worker(this);
        checkForNewFilmlist = new CheckForNewFilmlist(this);
    }

    public synchronized static final ProgData getInstance(String dir) {
        if (!dir.isEmpty()) {
            configDir = dir;
        }
        return getInstance();
    }

    public synchronized static final ProgData getInstance() {
        return instance == null ? instance = new ProgData() : instance;
    }
}
