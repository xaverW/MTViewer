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

import de.p2tools.mtviewer.MTViewerPlayerController;
import de.p2tools.mtviewer.controller.data.MTShortcut;
import de.p2tools.mtviewer.controller.data.ReplaceList;
import de.p2tools.mtviewer.controller.data.download.DownloadInfos;
import de.p2tools.mtviewer.controller.data.download.DownloadList;
import de.p2tools.mtviewer.controller.filmFilter.ActFilmFilterWorker;
import de.p2tools.mtviewer.controller.filmFilter.FilmFilterRunner;
import de.p2tools.mtviewer.controller.starter.StarterClass;
import de.p2tools.mtviewer.controller.worker.Worker;
import de.p2tools.mtviewer.gui.FilmGuiController;
import de.p2tools.mtviewer.gui.dialog.QuitDialogController;
import de.p2tools.mtviewer.gui.tools.Listener;
import de.p2tools.p2Lib.guiTools.pMask.PMaskerPane;
import de.p2tools.p2Lib.mtFilm.film.Filmlist;
import de.p2tools.p2Lib.mtFilm.filmlistUrls.SearchFilmListUrls;
import de.p2tools.p2Lib.tools.duration.PDuration;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ProgData {

    // flags
    public static boolean debug = false; // Debugmodus
    public static boolean duration = false; // Duration ausgeben
    public static boolean reset = false; // Programm auf Starteinstellungen zurücksetzen

    // Infos
    public static String configDir = ""; // Verzeichnis zum Speichern der Programmeinstellungen
    private static ProgData instance;
    // zentrale Klassen
//    public LoadFilmlist loadFilmlist; // erledigt das updaten der Filmliste
//    public LoadFilmFactory loadFilmFactory;
    public SearchFilmListUrls searchFilmListUrls; // da werden die DownloadURLs der Filmliste verwaltet
    public MTShortcut mtShortcut; // verwendete Shortcuts
    public ActFilmFilterWorker actFilmFilterWorker; // gespeicherte Filterprofile
    public FilmFilterRunner filmFilterRunner;
    public DownloadList downloadList; // Filme die als "Download" geladen werden sollen
    public StarterClass starterClass; // Klasse zum Ausführen der Programme (für die Downloads): VLC, flvstreamer, ...

    // Gui
    public Stage primaryStage = null;
    public PMaskerPane maskerPane = new PMaskerPane();
    public MTViewerPlayerController mtViewerPlayerController = null;
    public FilmGuiController filmGuiController = null; // Tab mit den Filmen
    public QuitDialogController quitDialogController = null;
    // Worker
    public Worker worker; // Liste aller Sender, Themen, ...

    // Programmdaten
    public Filmlist filmlist; // ist die komplette Filmliste
    public DownloadInfos downloadInfos;
    public ReplaceList replaceList;
    boolean oneSecond = false;

    private ProgData() {
        mtShortcut = new MTShortcut();
        replaceList = new ReplaceList();
        searchFilmListUrls = new SearchFilmListUrls();

//        loadFilmlist = new de.p2tools.p2Lib.mtFilm.loadFilmlist.LoadFilmlist();
//        loadFilmlist = new LoadFilmlist(this);
//        loadFilmFactory = new LoadFilmFactory();
        actFilmFilterWorker = new ActFilmFilterWorker(this);
        filmlist = new Filmlist();
        downloadList = new DownloadList(this);
        starterClass = new StarterClass(this);
        downloadInfos = new DownloadInfos(this);
        filmFilterRunner = new FilmFilterRunner(this);
        worker = new Worker(this);
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

    public void startTimer() {
        // extra starten, damit er im Einrichtungsdialog nicht dazwischen funkt
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(500), ae -> {

            oneSecond = !oneSecond;
            if (oneSecond) {
                doTimerWorkOneSecond();
            }
            doTimerWorkHalfSecond();

        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.setDelay(Duration.seconds(5));
        timeline.play();
        PDuration.onlyPing("Timer gestartet");
    }

    private void doTimerWorkOneSecond() {
        Listener.notify(Listener.EVENT_TIMER, ProgData.class.getName());
    }

    private void doTimerWorkHalfSecond() {
        Listener.notify(Listener.EVENT_TIMER_HALF_SECOND, ProgData.class.getName());
    }
}
