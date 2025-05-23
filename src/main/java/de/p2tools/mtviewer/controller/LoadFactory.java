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


package de.p2tools.mtviewer.controller;

import de.p2tools.mtviewer.controller.config.PEvents;
import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.gui.tools.TipOfDayFactory;
import de.p2tools.p2lib.mtfilm.film.FilmlistFactory;
import de.p2tools.p2lib.mtfilm.loadfilmlist.LoadFilmlist;
import de.p2tools.p2lib.p2event.P2Event;
import de.p2tools.p2lib.p2event.P2Listener;

public class LoadFactory {
    public static final double PROGRESS_MIN = 0.0;
    public static final double PROGRESS_MAX = 1.0;
    public static final double PROGRESS_INDETERMINATE = -1.0;

    private static LoadFactory instance;
    public LoadFilmlist loadFilmlist; //erledigt das Update der Filmliste
    private static boolean doneAtProgramStart = false;
    private final ProgData progData;

    public LoadFactory(ProgData progData) {
        this.progData = progData;

        // =================
        // Mediathek
        loadFilmlist = new LoadFilmlist(progData.pEventHandler);
        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_FILMLIST_LOAD_START) {
            @Override
            public void pingGui(P2Event event) {
                ProgData.FILMLIST_IS_DOWNLOADING.setValue(true);
                if (event.getAct() == PROGRESS_INDETERMINATE) {
                    // ist dann die gespeicherte Filmliste
                    progData.maskerPane.setMaskerVisible(true, false, false);
                } else {
                    progData.maskerPane.setMaskerVisible();
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
                ProgData.FILMLIST_IS_DOWNLOADING.setValue(false);
            }
        });
        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_FILMLIST_LOAD_FINISHED) {
            @Override
            public void pingGui() {
                ProgData.loadFilm = false;
                if (ProgData.firstProgramStart) {
                    ProgSave.saveAll(); // damit nichts verloren geht
                }
                // activate the saved filter
                progData.worker.resetFilter();
                progData.filmFilterRunner.filter();
                if (!ProgData.loadAudio) {
                    progData.maskerPane.switchOffMasker();
                }

                String filmDate = FilmlistFactory.getAgeAsStringDate(ProgData.getInstance().filmlist.metaData);
                ProgConfig.SYSTEM_FILMLIST_DATE.setValue(ProgData.getInstance().filmlist.isEmpty() ? "" : filmDate);

                if (!doneAtProgramStart) {
                    doneAtProgramStart = true;
                    UpdateCheckFactory.checkProgUpdate();
                    TipOfDayFactory.showDialog(ProgData.getInstance(), false);
                }
                // MARK markiert: Mediathek
                progData.filmlist.forEach(f -> f.setMark(true));

                setList();
            }
        });

        // ===========
        // Audiothek
        progData.pEventHandler.addListener(new P2Listener(PEvents.LOAD_AUDIO_LIST_START) {
            @Override
            public void pingGui(P2Event event) {
                ProgData.AUDIOLIST_IS_DOWNLOADING.setValue(true);
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
                ProgData.AUDIOLIST_IS_DOWNLOADING.setValue(false);
            }
        });
        progData.pEventHandler.addListener(new P2Listener(PEvents.LOAD_AUDIO_LIST_FINISHED) {
            @Override
            public void pingGui(P2Event event) {
                ProgData.loadAudio = false;
                if (ProgData.firstProgramStart) {
                    ProgSave.saveAll(); // damit nichts verloren geht
                }
                progData.worker.resetFilter();
                if (!ProgData.loadFilm) {
                    progData.maskerPane.switchOffMasker();
                }
                // MARK markiert: Mediathek
                progData.audioList.forEach(f -> f.setMark(false));
            }
        });
    }

    public void setList() {
        progData.filmlistUsed.clear();
        if (ProgConfig.SYSTEM_SHOW_MEDIATHEK.get()) {
            progData.filmlistUsed.addAll(ProgData.getInstance().filmlist);
        }
        if (ProgConfig.SYSTEM_SHOW_AUDIOTHEK.get()) {
            progData.filmlistUsed.addAll(ProgData.getInstance().audioList);
        }
    }
}
