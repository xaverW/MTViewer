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

import de.p2tools.mtviewer.controller.downloadtools.DownloadState;
import de.p2tools.mtviewer.controller.filmfilter.ActFilmFilterWorker;
import de.p2tools.mtviewer.controller.filmfilter.FilmFilter;
import de.p2tools.p2lib.configfile.ConfigFile;
import de.p2tools.p2lib.configfile.pdata.P2Data;
import de.p2tools.p2lib.configfile.pdata.P2DataProgConfig;
import de.p2tools.p2lib.mediathek.download.GetProgramStandardPath;
import de.p2tools.p2lib.mediathek.filmdata.FilmData;
import de.p2tools.p2lib.tools.P2InfoFactory;
import javafx.beans.property.*;
import org.apache.commons.lang3.SystemUtils;

public class ProgConfig extends P2DataProgConfig {
    private static ProgConfig instance;

    private ProgConfig() {
        super("ProgConfig");
    }

    public static final ProgConfig getInstance() {
        return instance == null ? instance = new ProgConfig() : instance;
    }

    public static void addConfigData(ConfigFile configFile) {
        ProgData progData = ProgData.getInstance();

        // Configs der Programmversion, nur damit sie (zur Update-Suche) im Config-File stehen
        ProgConfig.SYSTEM_PROG_VERSION.set(P2InfoFactory.getProgVersion());
        ProgConfig.SYSTEM_PROG_BUILD_NO.set(P2InfoFactory.getBuildNo());
        ProgConfig.SYSTEM_PROG_BUILD_DATE.set(P2InfoFactory.getBuildDateR());

        configFile.addConfigs(ProgConfig.getInstance());
        configFile.addConfigs(ProgColorList.getInstance());

        final FilmFilter actFilter = progData.actFilmFilterWorker.getActFilterSettings();
        actFilter.setName(ActFilmFilterWorker.SELECTED_FILTER_NAME); // nur zur Info im Config-File
        configFile.addConfigs(actFilter);

        configFile.addConfigs(progData.actFilmFilterWorker.getStoredFilter1());
        configFile.addConfigs(progData.actFilmFilterWorker.getStoredFilter2());
        configFile.addConfigs(progData.actFilmFilterWorker.getStoredFilter3());
        configFile.addConfigs(progData.actFilmFilterWorker.getStoredFilter4());

        configFile.addConfigs(progData.replaceList);
        configFile.addConfigs(progData.downloadList);
    }

    // Programm-Configs, änderbar nur im Konfig-File
    // ============================================
    // 250 Sekunden, wie bei Firefox
    public static int SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND_INIT = 250;
    public static IntegerProperty SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND = addIntProp("__system-parameter__download-timeout-second_250__", SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND_INIT);
    // max. Startversuche für fehlgeschlagene Downloads (insgesamt: restart * restart_http Versuche)
    public static int SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_INIT = 3;
    public static IntegerProperty SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART = addIntProp("__system-parameter__download-max-restart_5__", SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_INIT);
    // max. Startversuche für fehlgeschlagene Downloads, direkt beim Download
    public static int SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP_INIT = 5;
    public static IntegerProperty SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP = addIntProp("__system-parameter__download-max-restart-http_10__", SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP_INIT);
    // Beim Dialog "Download weiterführen" wird nach dieser Zeit der Download weitergeführt
    public static int SYSTEM_PARAMETER_DOWNLOAD_CONTINUE_IN_SECONDS_INIT = 60;
    public static IntegerProperty SYSTEM_PARAMETER_DOWNLOAD_CONTINUE_IN_SECONDS = addIntProp("__system-parameter__download-continue-second_60__", SYSTEM_PARAMETER_DOWNLOAD_CONTINUE_IN_SECONDS_INIT);
    // Beim Dialog "Automode" wird nach dieser Zeit der das Programm beendet
    public static int SYSTEM_PARAMETER_AUTOMODE_QUITT_IN_SECONDS_INIT = 15;
    public static IntegerProperty SYSTEM_PARAMETER_AUTOMODE_QUITT_IN_SECONDS = addIntProp("__system-parameter__automode-quitt-second_60__", SYSTEM_PARAMETER_AUTOMODE_QUITT_IN_SECONDS_INIT);
    // Downloadfehlermeldung wird xx Sedunden lang angezeigt
    public static int SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SECOND_INIT = 30;
    public static IntegerProperty SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SECOND = addIntProp("__system-parameter__download-errormsg-in-second_30__", SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SECOND_INIT);
    // Downloadprogress im Terminal anzeigen
    public static BooleanProperty SYSTEM_PARAMETER_DOWNLOAD_PROGRESS = addBoolProp("__system-parameter__download_progress_", Boolean.TRUE);

    // ===============================================================
    // ====== SYSTEM =================================================
    // ===============================================================
    static {
        addComment("Prog-Version");
    }

    // ===========================================
    // Configs der Programmversion, nur damit sie (zur Update-Suche) im Config-File stehen
    public static StringProperty SYSTEM_PROG_VERSION = addStrProp("system-prog-version", P2InfoFactory.getProgVersion());
    public static StringProperty SYSTEM_PROG_BUILD_NO = addStrProp("system-prog-build-no", P2InfoFactory.getBuildNo());
    public static StringProperty SYSTEM_PROG_BUILD_DATE = addStrProp("system-prog-build-date", P2InfoFactory.getBuildDateR());//z.B.: 27.07.2

    static {
        addComment("ProgrammUpdateSuche");
    }

    // Configs zum Aktualisieren beim Programmupdate
    public static StringProperty SYSTEM_UPDATE_DATE = addStrProp("system-update-date"); // Datum der letzten Prüfung
    public static BooleanProperty SYSTEM_UPDATE_SEARCH_ACT = addBoolProp("system-update-search-act", Boolean.TRUE); //Infos und Programm
    public static StringProperty SYSTEM_SEARCH_UPDATE_LAST_DATE = addStrProp("system-search-update-last-date"); // Datum der letzten Prüfung
    public static BooleanProperty SYSTEM_SEARCH_UPDATE = addBoolProp("system-search-update" + P2Data.TAGGER + "system-update-search-act", Boolean.TRUE); // nach einem Update suchen
    public static BooleanProperty SYSTEM_UPDATE_SEARCH_BETA = addBoolProp("system-update-search-beta", Boolean.FALSE); //beta suchen
    public static BooleanProperty SYSTEM_UPDATE_SEARCH_DAILY = addBoolProp("system-update-search-daily", Boolean.FALSE); //daily suchen

    static {
        addEmptyLine();
    }

    public static StringProperty SYSTEM_AUDIOLIST_DATE_TIME = addStrProp("system-audiolist-date", ""); // DateTimeFormatter DT_FORMATTER_dd_MM_yyyy___HH__mm
    public static BooleanProperty SYSTEM_AFTER_UPDATE_FILTER = addBoolProp("system-after-update-filter", Boolean.FALSE);
    public static BooleanProperty SYSTEM_CHANGE_LOG_DIR = addBoolProp("system-change-log-dir", Boolean.FALSE);

    //Configs zur Anzeige der Diacritics in der Filmliste
    //TRUE: dann werden Diacritics entfernt
    public static BooleanProperty SYSTEM_REMOVE_DIACRITICS = addBoolProp("system-remove-diacritics", Boolean.FALSE);

    // ConfigDialog, Dialog nach Start immer gleich öffnen
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_TAB = new SimpleIntegerProperty(0);
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_CONFIG = new SimpleIntegerProperty(-1);
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_FILM = new SimpleIntegerProperty(-1);
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_DOWNLOAD = new SimpleIntegerProperty(-1);
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_PLAY = new SimpleIntegerProperty(-1);
    public static BooleanProperty SYSTEM_SMALL_ROW_TABLE_FILM = addBoolProp("system-small-row-table-film", Boolean.FALSE);
    public static BooleanProperty SYSTEM_SMALL_ROW_TABLE_DOWNLOAD = addBoolProp("system-small-row-table-download", Boolean.TRUE);

    // Configs
    public static StringProperty SYSTEM_PROG_OPEN_DIR = addStrProp("system-prog-open-dir");
    public static StringProperty SYSTEM_PROG_OPEN_URL = addStrProp("system-prog-open-url");
    public static StringProperty SYSTEM_PROG_PLAY = addStrProp("system-prog-play", GetProgramStandardPath.getTemplatePathVlc());
    public static String SYSTEM_PROG_PLAY_PARAMETER_INIT = "%f";
    public static StringProperty SYSTEM_PROG_PLAY_PARAMETER = addStrProp("system-prog-play-parameter", SYSTEM_PROG_PLAY_PARAMETER_INIT);
    public static String SYSTEM_PROG_SAVE_INIT = GetProgramStandardPath.getTemplatePathFFmpeg();
    public static StringProperty SYSTEM_PROG_SAVE = addStrProp("system-prog-save", SYSTEM_PROG_SAVE_INIT);
    public static String SYSTEM_PROG_SAVE_PARAMETER_INIT = "-user_agent \"Mozilla/5.0\" -i %f -c copy -bsf:a aac_adtstoasc **";
    public static StringProperty SYSTEM_PROG_SAVE_PARAMETER = addStrProp("system-prog-save-parameter", SYSTEM_PROG_SAVE_PARAMETER_INIT);
    public static IntegerProperty SYSTEM_SAVE_MAX_SIZE = addIntProp("system-save-max-size", 150);
    public static IntegerProperty SYSTEM_SAVE_MAX_FIELD = addIntProp("system-save-max-field", 50);
    public static BooleanProperty SYSTEM_DARK_THEME_START = addBoolProp("system-dark-theme-start", Boolean.FALSE);
    public static BooleanProperty SYSTEM_BLACK_WHITE_ICON_START = addBoolProp("system-black-white-icon-start", Boolean.FALSE);

    public static StringProperty SYSTEM_FILMLIST_DATE = addStrProp("system-filmlist-date", "");
    public static StringProperty SYSTEM_USERAGENT = addStrProp("system-useragent", ProgConst.USER_AGENT_DEFAULT); //Useragent für direkte Downloads
    public static BooleanProperty SYSTEM_USE_REPLACETABLE = addBoolProp("system-use-replacetable", SystemUtils.IS_OS_LINUX ? Boolean.TRUE : Boolean.FALSE);
    public static BooleanProperty SYSTEM_ONLY_ASCII = addBoolProp("system-only-ascii", Boolean.FALSE);
    public static BooleanProperty SYSTEM_MARK_GEO = addBoolProp("system-mark-geo", Boolean.TRUE);
    public static StringProperty SYSTEM_GEO_HOME_PLACE = addStrProp("system-geo-home-place", FilmData.GEO_DE);

    public static IntegerProperty SYSTEM_FONT_SIZE = addIntProp("system-style-size", 0);
    public static BooleanProperty SYSTEM_FONT_SIZE_CHANGE = addBoolProp("system-font-size-change", Boolean.FALSE); // für die Schriftgröße

    public static StringProperty SYSTEM_LOG_DIR = addStrProp("system-log-dir", "");
    public static BooleanProperty SYSTEM_LOG_ON = addBoolProp("system-log-on", Boolean.TRUE);
    public static BooleanProperty SYSTEM_DARK_THEME = addBoolProp("system-dark-theme", Boolean.FALSE);
    public static BooleanProperty SYSTEM_BLACK_WHITE_ICON = addBoolProp("system-black-white-icon", Boolean.FALSE);
    public static BooleanProperty SYSTEM_THEME_CHANGED = addBoolProp("system-theme-changed");
    public static BooleanProperty SYSTEM_SSL_ALWAYS_TRUE = addBoolProp("system-ssl-always-true");
    public static BooleanProperty TIP_OF_DAY_SHOW = addBoolProp("tip-of-day-show", Boolean.TRUE);//Tips anzeigen
    public static StringProperty TIP_OF_DAY_WAS_SHOWN = addStrProp("tip-of-day-was-shown");//bereits angezeigte Tips
    public static StringProperty TIP_OF_DAY_DATE = addStrProp("tip-of-day-date"); //Datum des letzten Tips
    public static IntegerProperty SYSTEM_FILTER_WAIT_TIME = addIntProp("system-filter-wait-time", 100);
    public static BooleanProperty SYSTEM_FILTER_RETURN = addBoolProp("system-filter-return", Boolean.FALSE);
    public static StringProperty SYSTEM_DOWNLOAD_DIR_NEW_VERSION = addStrProp("system-download-dir-new-version", "");
    public static BooleanProperty SYSTEM_SHOW_MEDIATHEK = addBoolProp("system-show-mediathek", Boolean.TRUE);
    public static BooleanProperty SYSTEM_SHOW_AUDIOTHEK = addBoolProp("system-show-auiothek", Boolean.FALSE);
    public static BooleanProperty SYSTEM_FILMLIST_REMOVE_DOUBLE = addBoolProp("system-filmlist-remove-double", Boolean.TRUE);
    public static IntegerProperty SYSTEM_FILMLIST_COUNT_DOUBLE = addIntProp("system-filmlist-count-double", 0); // Anzahl der doppelten Filme
    public static IntegerProperty SYSTEM_AUDIOLIST_COUNT_DOUBLE = addIntProp("system-audiolist-count-double", 0); // Anzahl der doppelten Filme
    public static BooleanProperty SYSTEM_FILMLIST_DOUBLE_WITH_THEME_TITLE = addBoolProp("system-filmlist-double-with-theme-title", Boolean.FALSE);

    // Einstellungen Filmliste
    public static BooleanProperty SYSTEM_LOAD_FILMS_ON_START = addBoolProp("system-load-films-on-start", Boolean.TRUE);
    public static StringProperty SYSTEM_LOAD_NOT_SENDER = addStrProp("system-load-not-sender", "");
    public static IntegerProperty SYSTEM_LOAD_FILMLIST_MAX_DAYS = addIntProp("system-load-filmlist-max-days", 0); //es werden nur die x letzten Tage geladen
    public static IntegerProperty SYSTEM_LOAD_FILMLIST_MIN_DURATION = addIntProp("system-load-filmlist-min-duration", 0); //es werden nur Filme mit mind. x Minuten geladen

    // Fenstereinstellungen
    public static StringProperty SYSTEM_SIZE_GUI = addStrProp("system-size-gui", "1000:800");
    public static StringProperty SYSTEM_SIZE_DIALOG_FILMINFO = addStrProp("system-size-dialog-filminfo", "600:800");

    // INFO
    public static BooleanProperty INFO__IS_SHOWING = addBoolProp("info--is-showing", Boolean.TRUE);
    public static DoubleProperty INFO__DIVIDER = addDoubleProp("info--divider", 0.7);
    public static StringProperty INFO__PANE_INFO_DIALOG_SIZE = addStrProp("info--pane-info-dialog-size", "400:400");
    public static BooleanProperty INFO__PANE_INFO_IS_RIP = addBoolProp("info--pane-info-is-rip", Boolean.FALSE);

    public static StringProperty INFO__PANE_DOWNLOAD_DIALOG_SIZE = addStrProp("info--pane-download-dialog-size", "400:400");
    public static BooleanProperty INFO__PANE_DOWNLOAD_IS_RIP = addBoolProp("info--pane-download-is-rip", Boolean.FALSE);

    // FILTER
    public static BooleanProperty FILTER__IS_SHOWING = addBoolProp("filter--is-showing", Boolean.TRUE);
    public static DoubleProperty FILTER__DIVIDER = addDoubleProp("filter--divider", 0.3);
    public static StringProperty FILTER__PANE_FILTER_DIALOG_SIZE = addStrProp("filter--pane-filter-dialog-size", "400:600");
    public static BooleanProperty FILTER__PANE_FILTER_IS_RIP = addBoolProp("filter--pane-filter-is-rip", Boolean.FALSE);

    // Gui Film
    public static StringProperty FILM_GUI_TABLE_WIDTH = addStrProp("film-gui-table-width");
    public static StringProperty FILM_GUI_TABLE_SORT = addStrProp("film-gui-table-sort");
    public static StringProperty FILM_GUI_TABLE_UP_DOWN = addStrProp("film-gui-table-up-down");
    public static StringProperty FILM_GUI_TABLE_VIS = addStrProp("film-gui-table-vis");
    public static StringProperty FILM_GUI_TABLE_ORDER = addStrProp("film-gui-table-order");
    public static StringProperty FILM_RESOLUTION = addStrProp("film-resolution", FilmData.RESOLUTION_NORMAL);
    public static StringProperty FILM_PLAY_DIALOG_SIZE = addStrProp("film-play-dialog-size");
    public static DoubleProperty PANE_FILM_INFO__DIVIDER = addDoubleProp("pane-film-info--divider", 0.7);
    public static BooleanProperty FILM_GUI_SHOW_TABLE_TOOL_TIP = addBoolProp("film-gui-show-table-tool-tip", Boolean.TRUE);
    public static BooleanProperty DOWNLOAD_GUI_SHOW_TABLE_TOOL_TIP = addBoolProp("download-gui-show-table-tool-tip", Boolean.TRUE);

    // ConfigDialog
    public static StringProperty CONFIG_DIALOG_SIZE = addStrProp("config-dialog-size", "900:700");
    public static BooleanProperty CONFIG_DIALOG_ACCORDION = addBoolProp("config_dialog-accordion", Boolean.TRUE);

    // StartDialog
    public static StringProperty START_DIALOG_DOWNLOAD_PATH = addStrProp("start-dialog-download-path", P2InfoFactory.getStandardDownloadPath());

    // FilmInfoDialog
    public static BooleanProperty FILM_INFO_DIALOG_SHOW_URL = addBoolProp("film-info-dialog-show-url", Boolean.FALSE);
    public static BooleanProperty FILM_INFO_DIALOG_SHOW_WEBSITE_URL = addBoolProp("film-info-dialog-show-website-url", Boolean.FALSE);
    public static BooleanProperty FILM_INFO_DIALOG_SHOW_DESCRIPTION = addBoolProp("film-info-dialog-show-description", Boolean.TRUE);

    //Download
    public static StringProperty DOWNLOAD_DIALOG_PATH_SAVING = addStrProp("download-dialog-path-saving"); // gesammelten Downloadpfade im Downloaddialog
    public static StringProperty DOWNLOAD_ADD_DIALOG_SIZE = addStrProp("download-add-dialog-size", "800:600");
    public static StringProperty DOWNLOAD_DIALOG_ERROR_SIZE = addStrProp("download-dialog-error-size", "");
    public static IntegerProperty DOWNLOAD_MAX_DOWNLOADS = addIntProp("download-max-downloads", 1);
    public static IntegerProperty DOWNLOAD_CONTINUE = addIntProp("download-contineu", DownloadState.DOWNLOAD_RESTART__ASK);
    public static StringProperty DOWNLOAD_DIALOG_CONTINUE_SIZE = addStrProp("download-dialog-continue-size");
    public static BooleanProperty DOWNLOAD_SHOW_NOTIFICATION = addBoolProp("download-show-notification", Boolean.TRUE);
    public static BooleanProperty DOWNLOAD_DIALOG_START_DOWNLOAD_NOW = addBoolProp("download-dialog-start-download-now", Boolean.TRUE);
    public static BooleanProperty DOWNLOAD_DIALOG_START_DOWNLOAD_NOT = addBoolProp("download-dialog-start-download-not", Boolean.FALSE);
    public static IntegerProperty DOWNLOAD_BANDWIDTH_KBYTE = addIntProp("download-bandwidth-byte"); // da wird die genutzte Bandbreite gespeichert
    public static DoubleProperty DOWNLOAD_GUI_FILTER_DIVIDER = addDoubleProp("download-gui-filter-divider", ProgConst.GUI_DOWNLOAD_FILTER_DIVIDER_LOCATION);
    public static StringProperty DOWNLOAD_GUI_TABLE_WIDTH = addStrProp("download-gui-table-width");
    public static StringProperty DOWNLOAD_GUI_TABLE_SORT = addStrProp("download-gui-table-sort");
    public static StringProperty DOWNLOAD_GUI_TABLE_UP_DOWN = addStrProp("download-gui-table-up-down");
    public static StringProperty DOWNLOAD_GUI_TABLE_VIS = addStrProp("download-gui-table-vis");
    public static StringProperty DOWNLOAD_GUI_TABLE_ORDER = addStrProp("download-gui-table-order");

    //Download-SetDate
    public static String DOWNLOAD_FILE_PATH_INIT = P2InfoFactory.getStandardDownloadPath();
    public static StringProperty DOWNLOAD_FILE_PATH = addStrProp("download-file-path", DOWNLOAD_FILE_PATH_INIT);
    public static String DOWNLOAD_FILE_NAME_INIT = "%t-%T-%Z.mp4";
    public static StringProperty DOWNLOAD_FILE_NAME = addStrProp("download-file-name", DOWNLOAD_FILE_NAME_INIT);
    public static StringProperty DOWNLOAD_RESOLUTION = addStrProp("download-resolution", FilmData.RESOLUTION_NORMAL);
    public static BooleanProperty DOWNLOAD_SUBTITLE = addBoolProp("download-subtitle", false);
    public static BooleanProperty DOWNLOAD_INFO_FILE = addBoolProp("download-info-file", false);

    static {
        addComment("Shortcuts");
    }

    // Shortcuts Hauptmenü
    public static String SHORTCUT_QUIT_PROGRAM_INIT = "Ctrl+Q";
    public static StringProperty SHORTCUT_QUIT_PROGRAM = addStrProp("SHORTCUT_QUIT_PROGRAM", SHORTCUT_QUIT_PROGRAM_INIT);

    // Shortcuts Filmmenü
    public static String SHORTCUT_SHOW_INFOS_INIT = "Alt+I";
    public static StringProperty SHORTCUT_SHOW_INFOS = addStrProp("SHORTCUT_SHOW_INFO", SHORTCUT_SHOW_INFOS_INIT);

    public static String SHORTCUT_SHOW_FILTER_INIT = "Alt+F";
    public static StringProperty SHORTCUT_SHOW_FILTER = addStrProp("SHORTCUT_SHOW_FILTER", SHORTCUT_SHOW_FILTER_INIT);

    public static String SHORTCUT_INFO_FILM_INIT = "Ctrl+I";
    public static StringProperty SHORTCUT_INFO_FILM = addStrProp("SHORTCUT_INFO_FILM", SHORTCUT_INFO_FILM_INIT);

    public static String SHORTCUT_PLAY_FILM_INIT = "Ctrl+P";
    public static StringProperty SHORTCUT_PLAY_FILM = addStrProp("SHORTCUT_PLAY_FILM", SHORTCUT_PLAY_FILM_INIT);

    public static String SHORTCUT_SAVE_FILM_INIT = "Ctrl+S";
    public static StringProperty SHORTCUT_SAVE_FILM = addStrProp("SHORTCUT_SAVE_FILM", SHORTCUT_SAVE_FILM_INIT);

    // =====================================================
    // =====================================================
    static {
        check(SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND, SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND_INIT, 5, 200);
        check(SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART, SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_INIT, 0, 10);
        check(SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP, SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP_INIT, 0, 10);
        check(SYSTEM_PARAMETER_DOWNLOAD_CONTINUE_IN_SECONDS, SYSTEM_PARAMETER_DOWNLOAD_CONTINUE_IN_SECONDS_INIT, 5, 200);
        check(SYSTEM_PARAMETER_AUTOMODE_QUITT_IN_SECONDS, SYSTEM_PARAMETER_AUTOMODE_QUITT_IN_SECONDS_INIT, 5, 200);
        check(SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SECOND, SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SECOND_INIT, 5, 200);
    }

    private static synchronized void check(IntegerProperty mlConfigs, int init, int min, int max) {
        final int v = mlConfigs.getValue();
        if (v < min || v > max) {
            mlConfigs.setValue(init);
        }
    }
}
