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

package de.p2tools.mtviewer.controller.filmlist;


import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.config.ProgInfos;
import de.p2tools.mtviewer.controller.data.film.FilmData;
import de.p2tools.mtviewer.controller.data.film.Filmlist;
import de.p2tools.mtviewer.controller.data.film.FilmlistFactory;
import de.p2tools.mtviewer.controller.filmlist.loadFilmlist.ImportNewFilmlistFromServer;
import de.p2tools.mtviewer.controller.filmlist.loadFilmlist.ListenerFilmlistLoadEvent;
import de.p2tools.mtviewer.controller.filmlist.loadFilmlist.ListenerLoadFilmlist;
import de.p2tools.mtviewer.controller.filmlist.loadFilmlist.ReadFilmlist;
import de.p2tools.mtviewer.controller.filmlist.writeFilmlist.WriteFilmlistJson;
import de.p2tools.p2Lib.alert.PAlert;
import de.p2tools.p2Lib.tools.duration.PDuration;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class LoadFilmlist {

    private static final AtomicBoolean stop = new AtomicBoolean(false); // damit kann das Laden gestoppt werden kann
    private final HashSet<String> hashSet = new HashSet<>();
    private final Filmlist filmListDiff;
    private final ProgData progData;
    private final ImportNewFilmlistFromServer importNewFilmlisteFromServer;
    private final NotifyProgress notifyProgress = new NotifyProgress();
    private BooleanProperty propLoadFilmlist = new SimpleBooleanProperty(false);

    public LoadFilmlist(ProgData progData) {
        this.progData = progData;
        filmListDiff = new Filmlist();

        importNewFilmlisteFromServer = new ImportNewFilmlistFromServer(progData);
        importNewFilmlisteFromServer.addAdListener(new ListenerLoadFilmlist() {
            @Override
            public synchronized void start(ListenerFilmlistLoadEvent event) {
                // Start ans Prog melden
                notifyProgress.notifyEvent(NotifyProgress.NOTIFY.START, event);
            }

            @Override
            public synchronized void progress(ListenerFilmlistLoadEvent event) {
                notifyProgress.notifyEvent(NotifyProgress.NOTIFY.PROGRESS, event);
            }

            @Override
            public synchronized void finished(ListenerFilmlistLoadEvent event) {
                // Laden ist durch
                notifyProgress.notifyEvent(NotifyProgress.NOTIFY.LOADED,
                        new ListenerFilmlistLoadEvent("", "Filme verarbeiten",
                                ListenerLoadFilmlist.PROGRESS_INDETERMINATE, 0, false/* Fehler */));


                PDuration.onlyPing("Filme geladen: Nachbearbeiten");
                afterImportNewFilmlistFromServer(event);
                stopMsg();

                PDuration.onlyPing("Filme nachbearbeiten: Ende");

                // alles fertig ans Prog melden
                notifyProgress.notifyEvent(NotifyProgress.NOTIFY.FINISHED, event);
            }
        });
    }

    public void addListenerLoadFilmlist(ListenerLoadFilmlist listener) {
        notifyProgress.listeners.add(ListenerLoadFilmlist.class, listener);
    }

    public void removeListenerLoadFilmlist(ListenerLoadFilmlist listener) {
        notifyProgress.listeners.remove(ListenerLoadFilmlist.class, listener);
    }

    public boolean getPropLoadFilmlist() {
        return propLoadFilmlist.get();
    }

    public void setPropLoadFilmlist(boolean propLoadFilmlist) {
        this.propLoadFilmlist.set(propLoadFilmlist);
    }

    public BooleanProperty propLoadFilmlistProperty() {
        return propLoadFilmlist;
    }

    public synchronized boolean isStop() {
        return stop.get();
    }

    public synchronized void setStop(boolean set) {
        stop.set(set);
    }

    /**
     * Filmliste beim Programmstart laden
     */
    public void loadFilmlistProgStart(boolean firstProgramStart) {
        // Start des Ladens, gibts keine Fortschrittsanzeige und kein Abbrechen
        if (LoadFactory.checkAllSenderSelectedNotToLoad(progData.primaryStage)) {
            // alle Sender sind vom Laden ausgenommen
            return;
        }

        setPropLoadFilmlist(true);
        PDuration.onlyPing("Programmstart Filmliste laden: start");
        startMsg();
        notifyProgress.notifyEvent(NotifyProgress.NOTIFY.START,
                new ListenerFilmlistLoadEvent("", "gespeicherte Filmliste laden",
                        ListenerLoadFilmlist.PROGRESS_INDETERMINATE, 0, false));

        // Gui startet ein wenig flüssiger
        Thread thread = new Thread(() -> {
            final List<String> logList = new ArrayList<>();

            if (!firstProgramStart) {
                // gespeicherte Filmliste laden, macht beim ersten Programmstart keinen Sinn
                ReadFilmlist.readSavedFilmlist(progData.filmlist);
                PDuration.onlyPing("Programmstart Filmliste laden: geladen");
            }

            //eine neue Filmliste laden wenn die gespeicherte zu alt ist
            if (progData.filmlist.isTooOld() && ProgConfig.SYSTEM_LOAD_FILMS_ON_START.getValue()) {
                final String text;

                logList.add("Filmliste zu alt, neue Filmliste laden");
                text = "Filmliste ist zu alt, eine neue downloaden";

                logList.add(PLog.LILNE3);
                PLog.addSysLog(logList);

                notifyProgress.notifyEvent(NotifyProgress.NOTIFY.PROGRESS,
                        new ListenerFilmlistLoadEvent("", text,
                                ListenerLoadFilmlist.PROGRESS_INDETERMINATE, 0, false/* Fehler */));

                PDuration.onlyPing("Programmstart Filmliste laden: neue Liste laden");
                setPropLoadFilmlist(false);
                loadNewFilmlistFromServer(false);
                PDuration.onlyPing("Programmstart Filmliste laden: neue Liste geladen");

            } else {
                notifyProgress.notifyEvent(NotifyProgress.NOTIFY.LOADED,
                        new ListenerFilmlistLoadEvent("", "Filme verarbeiten",
                                ListenerLoadFilmlist.PROGRESS_INDETERMINATE, 0, false/* Fehler */));

                afterLoadingFilmlist(logList);
                PLog.addSysLog(logList);
                stopMsg();
                notifyProgress.notifyFinishedOk();
            }
        });

        thread.setName("loadFilmlistProgStart");
        thread.start();
    }

    public void loadNewFilmlistFromServer() {
        loadNewFilmlistFromServer(false);
    }

    public void loadNewFilmlistFromServer(boolean alwaysLoadNew) {
        // damit wird eine neue Filmliste (Web) geladen UND auch gleich im Config-Ordner gespeichert
        if (LoadFactory.checkAllSenderSelectedNotToLoad(progData.primaryStage)) {
            // alle Sender sind vom Laden ausgenommen
            return;
        }

        progData.maskerPane.setButtonVisible(true);

        if (!getPropLoadFilmlist()) {
            setPropLoadFilmlist(true);
            // nicht doppelt starten

            PDuration.onlyPing("Filmliste laden: start");
            final List<String> logList = new ArrayList<>();
            startMsg();
            logList.add("");
            logList.add("Alte Liste erstellt  am: " + progData.filmlist.genDate());
            logList.add("           Anzahl Filme: " + progData.filmlist.size());
            logList.add("           Anzahl  Neue: " + progData.filmlist.countNewFilms());
            logList.add(" ");


            String fileUrl = "";//könnte in den Einstellungen mal eingetragen werden
            // Hash mit URLs füllen
            fillHash(logList, progData.filmlist);

            if (alwaysLoadNew) {
                // dann die alte Filmliste löschen, damit immer komplett geladen wird, aber erst nach dem Hash!!
                progData.filmlist.clear();
            }

            setStop(false);

            if (fileUrl.isEmpty()) {
                // Filmliste laden und Url automatisch ermitteln
                logList.add("Filmliste laden (auto)");
                importNewFilmlisteFromServer.importFilmListAuto(progData.filmlist, filmListDiff);

            } else {
                // Filmliste laden von URL/Datei
                logList.add("Filmliste mit fester URL/Datei laden");
                progData.filmlist.clear();
                importNewFilmlisteFromServer.importFilmlistFromFile(progData.filmlist, fileUrl);
            }

            PLog.addSysLog(logList);
        }
    }

    // #######################################
    // #######################################

    private void startMsg() {
        PLog.addSysLog("");
        PLog.sysLog(PLog.LILNE1);
        PLog.addSysLog("Filmliste laden");
    }

    private void stopMsg() {
        PLog.addSysLog("Filmliste geladen");
        PLog.sysLog(PLog.LILNE1);
        PLog.addSysLog("");
    }

    /**
     * wird nach dem Import einer neuen Liste gemacht
     *
     * @param event
     */
    private void afterImportNewFilmlistFromServer(ListenerFilmlistLoadEvent event) {
        final List<String> logList = new ArrayList<>();
        logList.add(PLog.LILNE3);

        if (!filmListDiff.isEmpty()) {
            // wenn nur ein Update
            progData.filmlist.updateList(filmListDiff, true/* Vergleich über Index, sonst nur URL */, true /* ersetzen */);
            progData.filmlist.metaData = filmListDiff.metaData;
            progData.filmlist.sort(); // jetzt sollte alles passen
            filmListDiff.clear();
        }

        if (event.error) {
            // Laden war fehlerhaft
            logList.add("");
            logList.add("Filmliste laden war fehlerhaft, alte Liste wird wieder geladen");
            final boolean stopped = isStop();
            Platform.runLater(() -> PAlert.showErrorAlert("Filmliste laden",
                    stopped ? "Das Laden einer neuen Filmliste wurde abgebrochen!" :
                            "Das Laden einer neuen Filmliste hat nicht geklappt!")
            );

            // dann die alte Liste wieder laden
            progData.filmlist.clear();
            setStop(false);
            ReadFilmlist.readSavedFilmlist(progData.filmlist);
            logList.add("");

        } else {
            // dann war alles OK
            findAndMarkNewFilms(logList, progData.filmlist);

            logList.add("Unicode-Zeichen korrigieren");
            FilmlistFactory.cleanFaultyCharacterFilmlist();

            logList.add("");
            logList.add("Filme schreiben (" + progData.filmlist.size() + " Filme) :");
            logList.add("   --> Start Schreiben nach: " + ProgInfos.getFilmListFile());
            new WriteFilmlistJson().write(ProgInfos.getFilmListFile(), progData.filmlist);
            logList.add("   --> geschrieben!");
            logList.add("");
        }

        afterLoadingFilmlist(logList);
        PLog.addSysLog(logList);
    }

    /**
     * alles was nach einem Neuladen oder Einlesen einer gespeicherten Filmliste ansteht
     */
    private void afterLoadingFilmlist(List<String> logList) {

        logList.add("");
        logList.add("Jetzige Liste erstellt am: " + progData.filmlist.genDate());
        logList.add("  Anzahl Filme: " + progData.filmlist.size());
        logList.add("  Anzahl Neue:  " + progData.filmlist.countNewFilms());
        logList.add("");
        logList.add(PLog.LILNE2);
        logList.add("");

        notifyProgress.notifyEvent(NotifyProgress.NOTIFY.LOADED,
                new ListenerFilmlistLoadEvent("", "Diacritics setzen/ändern, Diacritics suchen",
                        ListenerLoadFilmlist.PROGRESS_INDETERMINATE, 0, false/* Fehler */));
        FilmlistFactory.setDiacritic(progData.filmlist, false);//todo

        notifyProgress.notifyEvent(NotifyProgress.NOTIFY.LOADED,
                new ListenerFilmlistLoadEvent("", "Filme markieren, Themen suchen",
                        ListenerLoadFilmlist.PROGRESS_INDETERMINATE, 0, false/* Fehler */));

        logList.add("Filme markieren");
        final int count = progData.filmlist.markFilms();
        logList.add("Anzahl doppelte Filme: " + count);

        progData.filmlist.loadSender();

        notifyProgress.notifyEvent(NotifyProgress.NOTIFY.LOADED,
                new ListenerFilmlistLoadEvent("", "Filme in Downloads eingetragen",
                        ListenerLoadFilmlist.PROGRESS_INDETERMINATE, 0, false/* Fehler */));
        logList.add("Filme in Downloads eingetragen");
        progData.downloadList.addFilmInList();

        setPropLoadFilmlist(false);
    }

    private void fillHash(List<String> logList, Filmlist filmlist) {
        logList.add(PLog.LILNE3);
        logList.add("Hash füllen, Größe vorher: " + hashSet.size());

        hashSet.addAll(filmlist.stream().map(FilmData::getUrlHistory).collect(Collectors.toList()));
        logList.add("                  nachher: " + hashSet.size());
        logList.add(PLog.LILNE3);
    }

    private void findAndMarkNewFilms(List<String> logList, Filmlist filmlist) {
        filmlist.stream() //genauso schnell wie "parallel": ~90ms
                .peek(film -> film.setNewFilm(false))
                .filter(film -> !hashSet.contains(film.getUrlHistory()))
                .forEach(film -> film.setNewFilm(true));

        cleanHash(logList, filmlist);
    }

    private void cleanHash(List<String> logList, Filmlist filmlist) {
        logList.add(PLog.LILNE3);
        logList.add("Hash bereinigen, Größe vorher: " + hashSet.size());

        filmlist.stream().forEach(film -> hashSet.remove(film.getUrlHistory()));
        logList.add("                      nachher: " + hashSet.size());
        logList.add(PLog.LILNE3);
    }
}