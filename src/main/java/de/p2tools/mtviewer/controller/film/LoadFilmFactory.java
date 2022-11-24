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
import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.config.ProgInfos;
import de.p2tools.p2Lib.mtFilm.loadFilmlist.ListenerFilmlistLoadEvent;
import de.p2tools.p2Lib.mtFilm.loadFilmlist.ListenerLoadFilmlist;
import de.p2tools.p2Lib.mtFilm.loadFilmlist.LoadFilmlist;
import de.p2tools.p2Lib.mtFilm.tools.LoadFactoryConst;

public class LoadFilmFactory {
    private static LoadFilmFactory instance;
    public LoadFilmlist loadFilmlist; //erledigt das Update der Filmliste

    private LoadFilmFactory() {
        loadFilmlist = new LoadFilmlist();
        LoadFilmFactory.getInstance().loadFilmlist.addListenerLoadFilmlist(new ListenerLoadFilmlist() {
            @Override
            public void start(ListenerFilmlistLoadEvent event) {
                if (event.progress == ListenerLoadFilmlist.PROGRESS_INDETERMINATE) {
                    // ist dann die gespeicherte Filmliste
                    ProgData.getInstance().maskerPane.setMaskerVisible(true, false);
                } else {
                    ProgData.getInstance().maskerPane.setMaskerVisible(true, true);
                }
                ProgData.getInstance().maskerPane.setMaskerProgress(event.progress, event.text);

                // the channel combo will be reseted, therefore save the filter
                ProgData.getInstance().worker.saveFilter();
            }

            @Override
            public void progress(ListenerFilmlistLoadEvent event) {
                ProgData.getInstance().maskerPane.setMaskerProgress(event.progress, event.text);
            }

            @Override
            public void loaded(ListenerFilmlistLoadEvent event) {
                ProgData.getInstance().maskerPane.setMaskerVisible(true, false);
                ProgData.getInstance().maskerPane.setMaskerProgress(ListenerLoadFilmlist.PROGRESS_INDETERMINATE, "Filmliste verarbeiten");
            }

            @Override
            public void finished(ListenerFilmlistLoadEvent event) {
                new ProgSave().saveAll(); // damit nichts verloren geht
                // activate the saved filter
                ProgData.getInstance().worker.resetFilter();
                ProgData.getInstance().filmFilterRunner.filter();
                ProgData.getInstance().maskerPane.setMaskerVisible(false);
            }
        });
    }

    public void loadProgStart(boolean firstProgramStart) {
        initLoadFactoryConst();
        loadFilmlist.loadFilmlistProgStart(firstProgramStart,
                ProgInfos.getFilmListFile(), ProgConfig.SYSTEM_LOAD_FILMS_ON_START.getValue());
    }

    public void loadList(boolean alwaysLoadNew) {
        initLoadFactoryConst();
        loadFilmlist.loadNewFilmlist(alwaysLoadNew, ProgInfos.getFilmListFile());
    }

    public void initLoadFactoryConst() {
        LoadFactoryConst.GEO_HOME_PLACE = ProgConfig.SYSTEM_GEO_HOME_PLACE.getValue();
        LoadFactoryConst.debug = ProgData.debug;
        LoadFactoryConst.SYSTEM_LOAD_NOT_SENDER = ProgConfig.SYSTEM_LOAD_NOT_SENDER.getValue();
        LoadFactoryConst.DOWNLOAD_MAX_BANDWIDTH_KBYTE = ProgConfig.DOWNLOAD_MAX_BANDWIDTH_KBYTE.getValue();
        LoadFactoryConst.SYSTEM_LOAD_FILMLIST_MAX_DAYS = ProgConfig.SYSTEM_LOAD_FILMLIST_MAX_DAYS.getValue();
        LoadFactoryConst.SYSTEM_LOAD_FILMLIST_MIN_DURATION = ProgConfig.SYSTEM_LOAD_FILMLIST_MIN_DURATION.getValue();
        LoadFactoryConst.filmlist = ProgData.getInstance().filmlist;
        LoadFactoryConst.userAgent = ProgConfig.SYSTEM_USERAGENT.getValue();
        LoadFactoryConst.loadFilmlist = loadFilmlist;
        LoadFactoryConst.primaryStage = ProgData.getInstance().primaryStage;
        LoadFactoryConst.removeDiacritic = ProgConfig.SYSTEM_REMOVE_DIACRITICS.getValue();
    }

    public synchronized static final LoadFilmFactory getInstance() {
        return instance == null ? instance = new LoadFilmFactory() : instance;
    }
}
