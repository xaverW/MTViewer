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
import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.config.ProgInfos;
import de.p2tools.mtviewer.gui.tools.TipOfDayFactory;
import de.p2tools.p2lib.mtfilm.film.FilmlistFactory;
import de.p2tools.p2lib.mtfilm.loadfilmlist.ListenerFilmlistLoadEvent;
import de.p2tools.p2lib.mtfilm.loadfilmlist.ListenerLoadFilmlist;
import de.p2tools.p2lib.mtfilm.loadfilmlist.LoadFilmlist;
import de.p2tools.p2lib.mtfilm.tools.LoadFactoryConst;

public class LoadFilmFactory {
    private static LoadFilmFactory instance;
    public LoadFilmlist loadFilmlist; //erledigt das Update der Filmliste
    private static boolean doneAtProgramStart = false;

    private LoadFilmFactory() {
        loadFilmlist = new LoadFilmlist();
        loadFilmlist.addListenerLoadFilmlist(new ListenerLoadFilmlist() {
            @Override
            public void start(ListenerFilmlistLoadEvent event) {
                if (event.progress == ListenerLoadFilmlist.PROGRESS_INDETERMINATE) {
                    // ist dann die gespeicherte Filmliste
                    ProgData.getInstance().maskerPane.setMaskerVisible(true, false, false);
                } else {
                    ProgData.getInstance().maskerPane.setMaskerVisible();
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
                ProgData.getInstance().maskerPane.setMaskerVisible(true, false, false);
                ProgData.getInstance().maskerPane.setMaskerProgress(ListenerLoadFilmlist.PROGRESS_INDETERMINATE, "Filmliste verarbeiten");
            }

            @Override
            public void finished(ListenerFilmlistLoadEvent event) {
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
        loadFilmlist.loadNewFilmlistFromWeb(alwaysLoadNew, ProgInfos.getFilmListFile());
    }

    public void initLoadFactoryConst() {
        LoadFactoryConst.debug = ProgData.debug;

        LoadFactoryConst.GEO_HOME_PLACE = ProgConfig.SYSTEM_GEO_HOME_PLACE.getValue();
        LoadFactoryConst.SYSTEM_LOAD_NOT_SENDER = ProgConfig.SYSTEM_LOAD_NOT_SENDER.getValue();
        LoadFactoryConst.DOWNLOAD_MAX_BANDWIDTH_KBYTE = ProgConfig.DOWNLOAD_MAX_BANDWIDTH_KBYTE;
        LoadFactoryConst.downloadMaxBandwidth = ProgConfig.DOWNLOAD_MAX_BANDWIDTH_KBYTE.getValue();

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

    public synchronized static final LoadFilmFactory getInstance() {
        return instance == null ? instance = new LoadFilmFactory() : instance;
    }
}
