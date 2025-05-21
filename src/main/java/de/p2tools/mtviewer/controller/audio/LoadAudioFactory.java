/*
 * P2Tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
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


package de.p2tools.mtviewer.controller.audio;

import de.p2tools.mtviewer.controller.ProgSave;
import de.p2tools.mtviewer.controller.config.PEvents;
import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.config.ProgInfos;
import de.p2tools.p2lib.mtfilm.film.Filmlist;
import de.p2tools.p2lib.p2event.P2Event;
import de.p2tools.p2lib.p2event.P2Listener;
import de.p2tools.p2lib.tools.date.P2DateConst;
import de.p2tools.p2lib.tools.date.P2LDateTimeFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LoadAudioFactory {

    public static final double PROGRESS_MIN = 0.0;
    public static final double PROGRESS_MAX = 1.0;
    public static final double PROGRESS_INDETERMINATE = -1.0;

    private static LoadAudioFactory instance;
    public LoadAudioList loadAudioList; //erledigt das Update der Audioliste
    private static boolean doneAtProgramStart = false;

    private LoadAudioFactory() {
        loadAudioList = new LoadAudioList();
        ProgData.getInstance().pEventHandler.addListener(new P2Listener(PEvents.LOAD_RADIO_LIST_START) {
            @Override
            public void pingGui(P2Event event) {
                ProgData.AUDIOLIST_IS_DOWNLOADING.setValue(true);
                if (event.getAct() == PROGRESS_INDETERMINATE) {
                    // ist dann die gespeicherte Audioliste
                    ProgData.getInstance().maskerPane.setMaskerVisible(true, false, false);
                } else {
                    ProgData.getInstance().maskerPane.setMaskerVisible();
                }
                ProgData.getInstance().maskerPane.setMaskerProgress(event.getAct(), event.getText());

                // the channel combo will be reseted, therefore save the filter
                ProgData.getInstance().worker.saveFilter();
            }
        });
        ProgData.getInstance().pEventHandler.addListener(new P2Listener(PEvents.LOAD_RADIO_LIST_PROGRESS) {
            @Override
            public void pingGui(P2Event event) {
                ProgData.getInstance().maskerPane.setMaskerProgress(event.getAct(), event.getText());
            }
        });
        ProgData.getInstance().pEventHandler.addListener(new P2Listener(PEvents.LOAD_RADIO_LIST_LOADED) {
            @Override
            public void pingGui(P2Event event) { // todo kommt da beim Laden 2x vorbei???
                ProgData.getInstance().maskerPane.setMaskerVisible(true, false, false);
                ProgData.getInstance().maskerPane.setMaskerProgress(PROGRESS_INDETERMINATE, "Audioliste verarbeiten");
                ProgData.AUDIOLIST_IS_DOWNLOADING.setValue(false);
            }
        });
        ProgData.getInstance().pEventHandler.addListener(new P2Listener(PEvents.LOAD_RADIO_LIST_FINISHED) {
            @Override
            public void pingGui(P2Event event) {
                if (ProgData.firstProgramStart) {
                    ProgSave.saveAll(); // damit nichts verloren geht
                }
                ProgData.getInstance().maskerPane.setMaskerText("Blacklist filtern");
//                BlacklistFilterFactory.markBlack(false);
//                AudioToolsFactory.markShownAndBookmarks();

                // activate the saved filter
                ProgData.getInstance().worker.resetFilter();
//                ProgData.getInstance().audioFilterRunner.filter();
//                ProgData.getInstance().downloadList.addAudioInList(ProgData.getInstance().audioList);
                ProgData.getInstance().maskerPane.switchOffMasker();
            }
        });
    }

    public void loadProgStart() {
        // neu einmal direkt nach dem Programmstart
        initLoadFactoryConst();
        loadAudioList.loadAtProgStart();
    }

    public void loadListButton() {
        // aus dem Menü oder Button in den Einstellungen
        initLoadFactoryConst();
        loadAudioList.loadNewListFromWeb();
    }

    public static boolean isNotFromToday(String strDate) {
        LocalDateTime listDate = P2LDateTimeFactory.fromString(strDate, P2DateConst.DT_FORMATTER_dd_MM_yyyy___HH__mm);
        LocalDate act = listDate.toLocalDate(); //2015-11-??
        LocalDate today = LocalDate.now(); //2015-11-23
        return !act.equals(today);
    }

    private void initLoadFactoryConst() {
        LoadAudioFactoryDto.debug = ProgData.debug;

        LoadAudioFactoryDto.audioListDate = ProgConfig.SYSTEM_AUDIOLIST_DATE_TIME;
        LoadAudioFactoryDto.firstProgramStart = ProgData.firstProgramStart;
        LoadAudioFactoryDto.localFilmListFile = ProgInfos.getAudioListFile();
        LoadAudioFactoryDto.loadNewAudioListOnProgramStart = ProgConfig.SYSTEM_LOAD_FILMS_ON_START.getValue();
        LoadAudioFactoryDto.SYSTEM_LOAD_FILMLIST_MAX_DAYS = ProgConfig.SYSTEM_LOAD_FILMLIST_MAX_DAYS.getValue();
        LoadAudioFactoryDto.SYSTEM_LOAD_FILMLIST_MIN_DURATION = ProgConfig.SYSTEM_LOAD_FILMLIST_MIN_DURATION.getValue();
        LoadAudioFactoryDto.removeDiacritic = ProgConfig.SYSTEM_REMOVE_DIACRITICS.getValue();
        LoadAudioFactoryDto.userAgent = ProgConfig.SYSTEM_USERAGENT.getValue();

        LoadAudioFactoryDto.audioListAkt = ProgData.getInstance().audioList;
        LoadAudioFactoryDto.audioListNew = new Filmlist<>();

        LoadAudioFactoryDto.loadFilmlist = loadAudioList;
        LoadAudioFactoryDto.primaryStage = ProgData.getInstance().primaryStage;
    }

    public synchronized static final LoadAudioFactory getInstance() {
        return instance == null ? instance = new LoadAudioFactory() : instance;
    }
}
