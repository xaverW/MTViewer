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

package de.p2tools.mtviewer.controller.worker;

import de.p2tools.mtviewer.controller.ProgSave;
import de.p2tools.mtviewer.controller.UpdateCheckFactory;
import de.p2tools.mtviewer.controller.config.PEvents;
import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.filmfilter.FilmFilter;
import de.p2tools.mtviewer.gui.help.TipOfDayFactory;
import de.p2tools.p2lib.mediathek.film.P2FilmlistFactory;
import de.p2tools.p2lib.mediathek.filmdata.FilmData;
import de.p2tools.p2lib.mediathek.filmdata.Filmlist;
import de.p2tools.p2lib.mediathek.filmlistload.P2LoadFilmlist;
import de.p2tools.p2lib.p2event.P2Event;
import de.p2tools.p2lib.p2event.P2Events;
import de.p2tools.p2lib.p2event.P2Listener;
import de.p2tools.p2lib.tools.duration.P2Duration;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Worker {

    public static final double PROGRESS_MIN = 0.0;
    public static final double PROGRESS_MAX = 1.0;
    public static final double PROGRESS_INDETERMINATE = -1.0;

    private static boolean doneAtProgramStart = false;

    final FilmFilter sfTemp = new FilmFilter();
    private final ProgData progData;
    private final ObservableList<String> allChannelList = FXCollections.observableArrayList("");

    public Worker(ProgData progData) {
        this.progData = progData;
        ProgConfig.SYSTEM_SHOW_MEDIATHEK.addListener((u, o, n) -> setList());
        ProgConfig.SYSTEM_SHOW_AUDIOTHEK.addListener((u, o, n) -> setList());
        addMediathek();
        addAudiothek();
    }

    public ObservableList<String> getAllChannelList() {
        return allChannelList;
    }

    private void addMediathek() {
        // =================
        // Mediathek
        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_FILMLIST_LOAD_START) {
            @Override
            public void pingGui(P2Event event) {
                progData.filmlistUsed.clear(); // muss dann ja auf jeden Fall gebaut werden

                if (event.getAct() == P2LoadFilmlist.PROGRESS_INDETERMINATE) {
                    // ist dann die gespeicherte Filmliste
                    progData.maskerPane.setMaskerVisible(true, true, false);
                } else {
                    progData.maskerPane.setMaskerVisible(true, true, true);
                }

                progData.maskerPane.setMaskerProgress(event.getAct(), event.getText());

                // the channel combo will be reseted, therefore save the filter
                progData.worker.saveFilter();
            }
        });
        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_FILMLIST_LOAD_PROGRESS) {
            @Override
            public void pingGui(P2Event event) {
                progData.maskerPane.setMaskerProgress(event.getAct(), event.getText());
            }
        });
        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_FILMLIST_LOAD_LOADED) {
            @Override
            public void pingGui() {
                // todo kommt da beim Laden 2x vorbei???
                progData.maskerPane.setMaskerVisible(true, false, false);
                progData.maskerPane.setMaskerProgress(PROGRESS_INDETERMINATE, "Filmliste verarbeiten");
            }
        });
        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_FILMLIST_LOAD_FINISHED) {
            @Override
            public void pingGui() {
                if (ProgData.firstProgramStart) {
                    ProgSave.saveAll(); // damit nichts verloren geht
                }
                String filmDate = P2FilmlistFactory.getAgeAsStringDate(ProgData.getInstance().filmlist.metaData);
                ProgConfig.SYSTEM_FILMLIST_DATE.setValue(ProgData.getInstance().filmlist.isEmpty() ? "" : filmDate);

                if (!doneAtProgramStart) {
                    doneAtProgramStart = true;
                    UpdateCheckFactory.checkProgUpdate();
                    TipOfDayFactory.showDialog(ProgData.getInstance(), false);
                }
                // MARK markiert dass es die Filmliste ist!
                progData.filmlist.forEach(f -> f.setMark(true));

                ProgData.FILMLIST_IS_DOWNLOADING.set(false);
                if (!ProgData.AUDIOLIST_IS_DOWNLOADING.get()) {
                    setNo();
                    setList();

                    // activate the saved filter
                    progData.worker.resetFilter();
                    progData.filmFilterRunner.filter();

                    progData.maskerPane.switchOffMasker();
                }
            }
        });
    }

    private void addAudiothek() {
        // ===========
        // Audiothek
        progData.pEventHandler.addListener(new P2Listener(P2Events.LOAD_AUDIO_LIST_START) {
            @Override
            public void pingGui(P2Event event) {
                progData.filmlistUsed.clear(); // muss dann ja auf jeden Fall gebaut werden

                if (event.getAct() == PROGRESS_INDETERMINATE) {
                    // ist dann die gespeicherte Audioliste
                    progData.maskerPane.setMaskerVisible(true, false, false);
                } else {
                    progData.maskerPane.setMaskerVisible();
                }
                progData.maskerPane.setMaskerProgress(event.getAct(), event.getText());

                // the channel combo will be reseted, therefore save the filter
                progData.worker.saveFilter();
            }
        });
        progData.pEventHandler.addListener(new P2Listener(PEvents.LOAD_AUDIO_LIST_PROGRESS) {
            @Override
            public void pingGui(P2Event event) {
                progData.maskerPane.setMaskerProgress(event.getAct(), event.getText());
            }
        });
        progData.pEventHandler.addListener(new P2Listener(PEvents.LOAD_AUDIO_LIST_LOADED) {
            @Override
            public void pingGui(P2Event event) { // todo kommt da beim Laden 2x vorbei???
                progData.maskerPane.setMaskerVisible(true, false, false);
                progData.maskerPane.setMaskerProgress(PROGRESS_INDETERMINATE, "Audioliste verarbeiten");
            }
        });
        progData.pEventHandler.addListener(new P2Listener(PEvents.LOAD_AUDIO_LIST_FINISHED) {
            @Override
            public void pingGui(P2Event event) {
                if (ProgData.firstProgramStart) {
                    ProgSave.saveAll(); // damit nichts verloren geht
                }
                // MARK markiert dass es die Filmliste ist!
                progData.audioList.forEach(f -> f.setMark(false));

                ProgData.AUDIOLIST_IS_DOWNLOADING.set(false);
                if (!ProgData.FILMLIST_IS_DOWNLOADING.get()) {
                    setNo();
                    setList();

                    // activate the saved filter
                    progData.worker.resetFilter();
                    progData.filmFilterRunner.filter();

                    progData.maskerPane.switchOffMasker();
                }
            }
        });
    }

    private void setNo() {
        int n = 0;
        for (FilmData f : progData.filmlist) {
            f.no = ++n;
        }
        for (FilmData a : progData.audioList) {
            a.no = ++n;
        }
    }

    public void setList() {
        P2Duration.counterStart("setList");
        progData.filmlistUsed.clear();

        if (ProgConfig.SYSTEM_SHOW_MEDIATHEK.get() && ProgConfig.SYSTEM_SHOW_AUDIOTHEK.get()) {
            // beide
            if (progData.filmlistUsed.size() != progData.filmlist.size() + progData.audioList.size()) {
                Filmlist<FilmData> tmp = new Filmlist<>();
                tmp.addAll(progData.filmlist);
                tmp.addAll(progData.audioList);
                progData.filmlistUsed.addAll(tmp);
                tmp.clear();
            }

        } else if (ProgConfig.SYSTEM_SHOW_MEDIATHEK.get()) {
            // nur Mediathek
            if (progData.filmlistUsed.size() != progData.filmlist.size()) {
                progData.filmlistUsed.addAll(ProgData.getInstance().filmlist);
            }
        } else if (ProgConfig.SYSTEM_SHOW_AUDIOTHEK.get()) {
            // nur Audiothek
            if (progData.filmlistUsed.size() != progData.audioList.size()) {
                progData.filmlistUsed.addAll(ProgData.getInstance().audioList);
            }
        }

        P2Duration.counterStop("setList");
    }

    private void saveFilter() {
        progData.actFilmFilterWorker.getActFilterSettings().copyTo(sfTemp);
    }

    private void resetFilter() {
        ArrayList<String> sender = new ArrayList<>(Arrays.asList(progData.filmlist.sender));
        List<String> audio = Arrays.asList(progData.audioList.sender);
        audio.forEach(a -> {
            if (!sender.contains(a)) {
                sender.add(a);
            }
        });

        allChannelList.setAll(sender);
        sfTemp.copyTo(progData.actFilmFilterWorker.getActFilterSettings());
    }
}
