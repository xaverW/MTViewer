package de.p2tools.mtviewer.controller.load;

import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.config.ProgInfos;
import de.p2tools.p2lib.mediathek.filmlistload.P2LoadConst;

public class LoadFactory {
    private LoadFactory() {
    }

    public static void initLoadFactoryConst() {
        P2LoadConst.GEO_HOME_PLACE = ProgConfig.SYSTEM_GEO_HOME_PLACE.getValue();
        P2LoadConst.SYSTEM_LOAD_NOT_SENDER = ProgConfig.SYSTEM_LOAD_NOT_SENDER.getValue();
        P2LoadConst.SYSTEM_LOAD_FILMLIST_MAX_DAYS = ProgConfig.SYSTEM_LOAD_FILMLIST_MAX_DAYS.getValue();
        P2LoadConst.SYSTEM_LOAD_FILMLIST_MIN_DURATION = ProgConfig.SYSTEM_LOAD_FILMLIST_MIN_DURATION.getValue();
        P2LoadConst.removeDiacritic = ProgConfig.SYSTEM_REMOVE_DIACRITICS.getValue();
        P2LoadConst.userAgent = ProgConfig.SYSTEM_USERAGENT.getValue();
        P2LoadConst.firstProgramStart = ProgData.firstProgramStart;
        P2LoadConst.debug = ProgData.debug;
        P2LoadConst.primaryStage = ProgData.getInstance().primaryStage;
        P2LoadConst.p2EventHandler = ProgData.getInstance().pEventHandler;

        P2LoadConst.loadNewFilmlistOnProgramStart = ProgConfig.SYSTEM_LOAD_FILMS_ON_START.getValue();
        P2LoadConst.dateStoredAudiolist = ProgConfig.SYSTEM_AUDIOLIST_DATE_TIME;
        P2LoadConst.dateStoredFilmlist = ProgConfig.SYSTEM_FILMLIST_DATE;

        P2LoadConst.localAudioListFile = ProgInfos.getAudioListFile();
        P2LoadConst.localFilmListFile = ProgInfos.getFilmListFile();
        P2LoadConst.SYSTEM_AUDIOLIST_COUNT_DOUBLE = ProgConfig.SYSTEM_AUDIOLIST_COUNT_DOUBLE;
        P2LoadConst.SYSTEM_FILMLIST_REMOVE_DOUBLE = ProgConfig.SYSTEM_FILMLIST_REMOVE_DOUBLE.get();

        P2LoadConst.filmlistLocal = ProgData.getInstance().filmlist;
        P2LoadConst.audioListLocal = ProgData.getInstance().audioList;

        P2LoadConst.filmListUrl = ProgData.filmListUrl;
    }
}
