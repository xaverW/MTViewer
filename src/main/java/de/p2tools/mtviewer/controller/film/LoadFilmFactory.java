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


package de.p2tools.mtviewer.controller.film;

import de.p2tools.mtviewer.controller.ProgSave;
import de.p2tools.mtviewer.controller.UpdateCheckFactory;
import de.p2tools.mtviewer.controller.config.PEvents;
import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.config.ProgInfos;
import de.p2tools.mtviewer.gui.tools.TipOfDayFactory;
import de.p2tools.p2lib.mtfilm.film.FilmlistFactory;
import de.p2tools.p2lib.mtfilm.loadfilmlist.LoadFilmlist;
import de.p2tools.p2lib.mtfilm.tools.LoadFactoryConst;
import de.p2tools.p2lib.p2event.P2Event;
import de.p2tools.p2lib.p2event.P2Listener;

public class LoadFilmFactory {
    public static final double PROGRESS_MIN = 0.0;
    public static final double PROGRESS_MAX = 1.0;
    public static final double PROGRESS_INDETERMINATE = -1.0;

    private static LoadFilmFactory instance;
    public LoadFilmlist loadFilmlist; //erledigt das Update der Filmliste
    private static boolean doneAtProgramStart = false;
    private final ProgData progData;

    public LoadFilmFactory(ProgData progData) {
        this.progData = progData;
        loadFilmlist = new LoadFilmlist(progData.pEventHandler);
        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_FILMLIST_LOAD_START) {
            @Override
            public void pingGui(P2Event event) {
                ProgData.FILMLIST_IS_DOWNLOADING.setValue(true);
                if (event.getAct() == PROGRESS_INDETERMINATE) {
                    // ist dann die gespeicherte Filmliste
                    ProgData.getInstance().maskerPane.setMaskerVisible(true, false, false);
                } else {
                    ProgData.getInstance().maskerPane.setMaskerVisible();
                }
                ProgData.getInstance().maskerPane.setMaskerProgress(event.getAct(), event.getText());

                // the channel combo will be reseted, therefore save the filter
                ProgData.getInstance().worker.saveFilter();
            }
        });
        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_FILMLIST_LOAD_PROGRESS) {
            @Override
            public void pingGui(P2Event event) {
                ProgData.getInstance().maskerPane.setMaskerProgress(event.getAct(), event.getText());
            }
        });
        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_FILMLIST_LOAD_LOADED) {
            @Override
            public void pingGui() {
                // todo kommt da beim Laden 2x vorbei???
                ProgData.getInstance().maskerPane.setMaskerVisible(true, false, false);
                ProgData.getInstance().maskerPane.setMaskerProgress(PROGRESS_INDETERMINATE, "Filmliste verarbeiten");
                ProgData.FILMLIST_IS_DOWNLOADING.setValue(false);
            }
        });
        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_FILMLIST_LOAD_FINISHED) {
            @Override
            public void pingGui() {
                if (ProgData.firstProgramStart) {
                    ProgSave.saveAll(); // damit nichts verloren geht
                }
                // activate the saved filter
                ProgData.getInstance().worker.resetFilter();
                ProgData.getInstance().filmFilterRunner.filter();
                ProgData.getInstance().maskerPane.switchOffMasker();

                String filmDate = FilmlistFactory.getAgeAsStringDate(ProgData.getInstance().filmlist.metaData);
                ProgConfig.SYSTEM_FILMLIST_DATE.setValue(ProgData.getInstance().filmlist.isEmpty() ? "" : filmDate);

                if (!doneAtProgramStart) {
                    doneAtProgramStart = true;
                    UpdateCheckFactory.checkProgUpdate();
                    TipOfDayFactory.showDialog(ProgData.getInstance(), false);
                }
            }
        });
    }

    public void loadProgStart() {
        initLoadFactoryConst();
        loadFilmlist.loadFilmlistProgStart();
    }

    public void loadList(boolean alwaysLoadNew) {
        initLoadFactoryConst();
        loadFilmlist.loadNewFilmlistFromWeb(alwaysLoadNew/*, ProgInfos.getFilmListFile()*/);
    }

    public void initLoadFactoryConst() {
        LoadFactoryConst.debug = ProgData.debug;

        LoadFactoryConst.GEO_HOME_PLACE = ProgConfig.SYSTEM_GEO_HOME_PLACE.getValue();
        LoadFactoryConst.SYSTEM_LOAD_NOT_SENDER = ProgConfig.SYSTEM_LOAD_NOT_SENDER.getValue();

        LoadFactoryConst.dateStoredFilmlist = ProgConfig.SYSTEM_FILMLIST_DATE.getValue();
        LoadFactoryConst.firstProgramStart = ProgData.firstProgramStart;
        LoadFactoryConst.localFilmListFile = ProgInfos.getFilmListFile();
        LoadFactoryConst.loadNewFilmlistOnProgramStart = ProgConfig.SYSTEM_LOAD_FILMS_ON_START.getValue();
        LoadFactoryConst.SYSTEM_LOAD_FILMLIST_MAX_DAYS = ProgConfig.SYSTEM_LOAD_FILMLIST_MAX_DAYS.getValue();
        LoadFactoryConst.SYSTEM_LOAD_FILMLIST_MIN_DURATION = ProgConfig.SYSTEM_LOAD_FILMLIST_MIN_DURATION.getValue();
        LoadFactoryConst.removeDiacritic = ProgConfig.SYSTEM_REMOVE_DIACRITICS.getValue();
        LoadFactoryConst.userAgent = ProgConfig.SYSTEM_USERAGENT.getValue();
        LoadFactoryConst.filmlist = ProgData.getInstance().filmlist;

        LoadFactoryConst.loadFilmlist = loadFilmlist;
        LoadFactoryConst.primaryStage = ProgData.getInstance().primaryStage;
        LoadFactoryConst.filmListUrl = ProgData.filmListUrl;
    }
}
