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

import de.p2tools.mtviewer.controller.downloadTools.DownloadState;
import de.p2tools.mtviewer.controller.filmFilter.ActFilmFilterWorker;
import de.p2tools.mtviewer.controller.filmFilter.FilmFilter;
import de.p2tools.p2Lib.P2LibConst;
import de.p2tools.p2Lib.configFile.ConfigFile;
import de.p2tools.p2Lib.configFile.config.Config;
import de.p2tools.p2Lib.configFile.pData.PDataProgConfig;
import de.p2tools.p2Lib.mtDownload.BandwidthTokenBucket;
import de.p2tools.p2Lib.mtDownload.GetProgramStandardPath;
import de.p2tools.p2Lib.mtFilm.film.FilmData;
import de.p2tools.p2Lib.mtFilm.filmlistUrls.FilmlistUrlList;
import de.p2tools.p2Lib.tools.PStringUtils;
import de.p2tools.p2Lib.tools.PSystemUtils;
import de.p2tools.p2Lib.tools.ProgramToolsFactory;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.beans.property.*;
import org.apache.commons.lang3.SystemUtils;

import java.util.ArrayList;

public class ProgConfig extends PDataProgConfig {

    public static final String SYSTEM = "system";
    private static final ArrayList<Config> arrayList = new ArrayList<>();
    // Programm-Configs, änderbar nur im Konfig-File
    // ============================================
    // 250 Sekunden, wie bei Firefox
    public static int SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND_INIT = 250;
    public static IntegerProperty SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND = addInt("__system-parameter__download-timeout-second_250__", SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND_INIT);
    // max. Startversuche für fehlgeschlagene Downloads (insgesamt: restart * restart_http Versuche)
    public static int SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_INIT = 3;
    public static IntegerProperty SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART = addInt("__system-parameter__download-max-restart_5__", SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_INIT);
    // max. Startversuche für fehlgeschlagene Downloads, direkt beim Download
    public static int SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP_INIT = 5;
    public static IntegerProperty SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP = addInt("__system-parameter__download-max-restart-http_10__", SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP_INIT);
    // Beim Dialog "Download weiterführen" wird nach dieser Zeit der Download weitergeführt
    public static int SYSTEM_PARAMETER_DOWNLOAD_CONTINUE_IN_SECONDS_INIT = 60;
    public static IntegerProperty SYSTEM_PARAMETER_DOWNLOAD_CONTINUE_IN_SECONDS = addInt("__system-parameter__download-continue-second_60__", SYSTEM_PARAMETER_DOWNLOAD_CONTINUE_IN_SECONDS_INIT);
    // Beim Dialog "Automode" wird nach dieser Zeit der das Programm beendet
    public static int SYSTEM_PARAMETER_AUTOMODE_QUITT_IN_SECONDS_INIT = 15;
    public static IntegerProperty SYSTEM_PARAMETER_AUTOMODE_QUITT_IN_SECONDS = addInt("__system-parameter__automode-quitt-second_60__", SYSTEM_PARAMETER_AUTOMODE_QUITT_IN_SECONDS_INIT);
    // Downloadfehlermeldung wird xx Sedunden lang angezeigt
    public static int SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SECOND_INIT = 30;
    public static IntegerProperty SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SECOND = addInt("__system-parameter__download-errormsg-in-second_30__", SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SECOND_INIT);
    // Downloadprogress im Terminal anzeigen
    public static BooleanProperty SYSTEM_PARAMETER_DOWNLOAD_PROGRESS = addBool("__system-parameter__download_progress_", Boolean.TRUE);
    public static String PARAMETER_INFO = P2LibConst.LINE_SEPARATOR + "\t"
            + "\"__system-parameter__xxx\" können nur im Konfigfile geändert werden" + P2LibConst.LINE_SEPARATOR
            + "\t" + "und sind auch nicht für ständige Änderungen gedacht." + P2LibConst.LINE_SEPARATOR
            + "\t" + "Wird eine Zeile gelöscht, wird der Parameter wieder mit dem Standardwert angelegt."
            + P2LibConst.LINE_SEPARATOR
            + P2LibConst.LINE_SEPARATOR

            + "*" + "\t" + "Timeout für direkte Downloads, Standardwert: "
            + SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND.getValue() + P2LibConst.LINE_SEPARATOR

            + "*" + "\t" + "max. Startversuche für fehlgeschlagene Downloads, am Ende aller Downloads" + P2LibConst.LINE_SEPARATOR
            + "\t" + "(Versuche insgesamt: DOWNLOAD_MAX_RESTART * DOWNLOAD_MAX_RESTART_HTTP), Standardwert: " +
            SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART.getValue() + P2LibConst.LINE_SEPARATOR

            + "*" + "\t" + "max. Startversuche für fehlgeschlagene Downloads, direkt beim Download," + P2LibConst.LINE_SEPARATOR
            + "\t" + "(Versuche insgesamt: DOWNLOAD_MAX_RESTART * DOWNLOAD_MAX_RESTART_HTTP), Standardwert: "
            + SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP.getValue() + P2LibConst.LINE_SEPARATOR

            + "*" + "\t" + "Beim Dialog \"Download weiterführen\" wird nach dieser Zeit der Download weitergeführt, Standardwert: "
            + SYSTEM_PARAMETER_DOWNLOAD_CONTINUE_IN_SECONDS.getValue() + P2LibConst.LINE_SEPARATOR

            + "*" + "\t" + "Beim Dialog \"Automode\" wird nach dieser Zeit der das Programm beendet, Standardwert: "
            + SYSTEM_PARAMETER_AUTOMODE_QUITT_IN_SECONDS.getValue() + P2LibConst.LINE_SEPARATOR

            + "*" + "\t" + "Downloadfehlermeldung wird xx Sedunden lang angezeigt, Standardwert: "
            + SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SECOND.getValue() + P2LibConst.LINE_SEPARATOR

            + "*" + "\t" + "Downloadprogress im Terminal (-auto) anzeigen: "
            + SYSTEM_PARAMETER_DOWNLOAD_PROGRESS.getValue() + P2LibConst.LINE_SEPARATOR;
    // ===========================================
    // Configs der Programmversion, nur damit sie (zur Update-Suche) im Config-File stehen
    public static StringProperty SYSTEM_PROG_VERSION = addStr("system-prog-version", ProgramToolsFactory.getProgVersion());
    public static StringProperty SYSTEM_PROG_BUILD_NO = addStr("system-prog-build-no", ProgramToolsFactory.getBuild());
    public static StringProperty SYSTEM_PROG_BUILD_DATE = addStr("system-prog-build-date", ProgramToolsFactory.getCompileDate());//z.B.: 27.07.2

    //Configs zur Anzeige der Diacritics in der Filmliste
    //TRUE: dann werden Diacritics entfernt
    public static BooleanProperty SYSTEM_REMOVE_DIACRITICS = addBool("system-remove-diacritics", Boolean.FALSE);

    // Configs zum Aktualisieren beim Programmupdate
    public static BooleanProperty SYSTEM_AFTER_UPDATE_FILTER = addBool("system-after-update-filter", Boolean.FALSE);

    // Configs zur Programmupdatesuche
    public static StringProperty SYSTEM_UPDATE_DATE = addStr("system-update-date"); // Datum der letzten Prüfung

    public static BooleanProperty SYSTEM_UPDATE_SEARCH_ACT = addBool("system-update-search-act", Boolean.TRUE); //Infos und Programm
    public static BooleanProperty SYSTEM_UPDATE_SEARCH_BETA = addBool("system-update-search-beta", Boolean.FALSE); //beta suchen
    public static BooleanProperty SYSTEM_UPDATE_SEARCH_DAILY = addBool("system-update-search-daily", Boolean.FALSE); //daily suchen

    public static StringProperty SYSTEM_UPDATE_LAST_INFO = addStr("system-update-last-info");
    public static StringProperty SYSTEM_UPDATE_LAST_ACT = addStr("system-update-last-act");
    public static StringProperty SYSTEM_UPDATE_LAST_BETA = addStr("system-update-last-beta");
    public static StringProperty SYSTEM_UPDATE_LAST_DAILY = addStr("system-update-last-daily");

    // ConfigDialog, Dialog nach Start immer gleich öffnen
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_TAB = new SimpleIntegerProperty(0);
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_CONFIG = new SimpleIntegerProperty(-1);
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_FILM = new SimpleIntegerProperty(-1);
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_DOWNLOAD = new SimpleIntegerProperty(-1);
    public static IntegerProperty SYSTEM_CONFIG_DIALOG_PLAY = new SimpleIntegerProperty(-1);

    //Download
    public static StringProperty DOWNLOAD_DIALOG_PATH_SAVING = addStr("download-dialog-path-saving"); // gesammelten Downloadpfade im Downloaddialog
    public static StringProperty DOWNLOAD_ADD_DIALOG_SIZE = addStr("download-add-dialog-size", "800:600");
    public static IntegerProperty DOWNLOAD_MAX_BANDWIDTH_KBYTE = addInt("download-max-bandwidth-kilobyte", BandwidthTokenBucket.BANDWIDTH_MAX_KBYTE);
    public static StringProperty DOWNLOAD_DIALOG_ERROR_SIZE = addStr("download-dialog-error-size", "");
    public static IntegerProperty DOWNLOAD_MAX_DOWNLOADS = addInt("download-max-downloads", 1);
    public static IntegerProperty DOWNLOAD_CONTINUE = addInt("download-contineu", DownloadState.DOWNLOAD_RESTART__ASK);
    public static StringProperty DOWNLOAD_DIALOG_CONTINUE_SIZE = addStr("download-dialog-continue-size");
    public static BooleanProperty DOWNLOAD_SHOW_NOTIFICATION = addBool("download-show-notification", Boolean.TRUE);
    public static BooleanProperty DOWNLOAD_DIALOG_START_DOWNLOAD_NOW = addBool("download-dialog-start-download-now", Boolean.TRUE);
    public static BooleanProperty DOWNLOAD_DIALOG_START_DOWNLOAD_NOT = addBool("download-dialog-start-download-not", Boolean.FALSE);
    public static IntegerProperty DOWNLOAD_BANDWIDTH_KBYTE = addInt("download-bandwidth-byte"); // da wird die genutzte Bandbreite gespeichert
    public static DoubleProperty DOWNLOAD_GUI_FILTER_DIVIDER = addDouble("download-gui-filter-divider", ProgConst.GUI_DOWNLOAD_FILTER_DIVIDER_LOCATION);
    public static BooleanProperty DOWNLOAD_INFO_DIALOG_SHOW_URL = addBool("download-info-dialog-show-url", Boolean.TRUE);
    public static StringProperty DOWNLOAD_DIALOG_EDIT_SIZE = addStr("download-dialog-edit-size", "800:800");
    public static StringProperty DOWNLOAD_GUI_TABLE_WIDTH = addStr("download-gui-table-width");
    public static StringProperty DOWNLOAD_GUI_TABLE_SORT = addStr("download-gui-table-sort");
    public static StringProperty DOWNLOAD_GUI_TABLE_UP_DOWN = addStr("download-gui-table-up-down");
    public static StringProperty DOWNLOAD_GUI_TABLE_VIS = addStr("download-gui-table-vis");
    public static StringProperty DOWNLOAD_GUI_TABLE_ORDER = addStr("download-gui-table-order");
    public static BooleanProperty DOWNLOAD_START_NOW = addBool("download-start-now", Boolean.FALSE);

    //Download-SetDate
    public static String DOWNLOAD_FILE_PATH_INIT = PSystemUtils.getStandardDownloadPath();
    public static StringProperty DOWNLOAD_FILE_PATH = addStr("download-file-path", DOWNLOAD_FILE_PATH_INIT);
    public static String DOWNLOAD_FILE_NAME_INIT = "%t-%T-%Z.mp4";
    public static StringProperty DOWNLOAD_FILE_NAME = addStr("download-file-name", DOWNLOAD_FILE_NAME_INIT);
    public static StringProperty DOWNLOAD_RESOLUTION = addStr("download-resolution", FilmData.RESOLUTION_NORMAL);
    public static BooleanProperty DOWNLOAD_SUBTITLE = addBool("download-subtitle", false);
    public static BooleanProperty DOWNLOAD_INFO_FILE = addBool("download-info-file", false);

    public static StringProperty SYSTEM_PROG_OPEN_DIR = addStr("system-prog-open-dir");
    public static StringProperty SYSTEM_PROG_OPEN_URL = addStr("system-prog-open-url");
    public static StringProperty SYSTEM_PROG_PLAY = addStr("system-prog-play", GetProgramStandardPath.getTemplatePathVlc());
    public static String SYSTEM_PROG_PLAY_PARAMETER_INIT = "%f";
    public static StringProperty SYSTEM_PROG_PLAY_PARAMETER = addStr("system-prog-play-parameter", SYSTEM_PROG_PLAY_PARAMETER_INIT);
    public static String SYSTEM_PROG_SAVE_INIT = GetProgramStandardPath.getTemplatePathFFmpeg();
    public static StringProperty SYSTEM_PROG_SAVE = addStr("system-prog-save", SYSTEM_PROG_SAVE_INIT);
    public static String SYSTEM_PROG_SAVE_PARAMETER_INIT = "-user_agent \"Mozilla/5.0\" -i %f -c copy -bsf:a aac_adtstoasc **";
    public static StringProperty SYSTEM_PROG_SAVE_PARAMETER = addStr("system-prog-save-parameter", SYSTEM_PROG_SAVE_PARAMETER_INIT);
    public static IntegerProperty SYSTEM_SAVE_MAX_SIZE = addInt("system-save-max-size", 150);
    public static IntegerProperty SYSTEM_SAVE_MAX_FIELD = addInt("system-save-max-field", 50);

    // Configs
    public static StringProperty SYSTEM_USERAGENT = addStr("system-useragent", ProgConst.USER_AGENT_DEFAULT); //Useragent für direkte Downloads
    public static BooleanProperty SYSTEM_USE_REPLACETABLE = addBool("system-use-replacetable", SystemUtils.IS_OS_LINUX ? Boolean.TRUE : Boolean.FALSE);
    public static BooleanProperty SYSTEM_ONLY_ASCII = addBool("system-only-ascii", Boolean.FALSE);
    public static BooleanProperty SYSTEM_MARK_GEO = addBool("system-mark-geo", Boolean.TRUE);
    public static StringProperty SYSTEM_GEO_HOME_PLACE = addStr("system-geo-home-place", FilmData.GEO_DE);
    public static BooleanProperty SYSTEM_STYLE = addBool("system-style", Boolean.FALSE);
    public static IntegerProperty SYSTEM_STYLE_SIZE = addInt("system-style-size", 14);
    public static StringProperty SYSTEM_LOG_DIR = addStr("system-log-dir", "");
    public static BooleanProperty SYSTEM_LOG_ON = addBool("system-log-on", Boolean.TRUE);
    public static BooleanProperty SYSTEM_DARK_THEME = addBool("system-dark-theme", Boolean.FALSE);
    public static BooleanProperty SYSTEM_THEME_CHANGED = addBool("system-theme-changed");
    public static BooleanProperty SYSTEM_SSL_ALWAYS_TRUE = addBool("system-ssl-always-true");
    public static BooleanProperty TIP_OF_DAY_SHOW = addBool("tip-of-day-show", Boolean.TRUE);//Tips anzeigen
    public static StringProperty TIP_OF_DAY_WAS_SHOWN = addStr("tip-of-day-was-shown");//bereits angezeigte Tips
    public static StringProperty TIP_OF_DAY_DATE = addStr("tip-of-day-date"); //Datum des letzten Tips
    public static IntegerProperty SYSTEM_FILTER_WAIT_TIME = addInt("system-filter-wait-time", 100);
    public static BooleanProperty SYSTEM_FILTER_RETURN = addBool("system-filter-return", Boolean.FALSE);
    public static StringProperty SYSTEM_DOWNLOAD_DIR_NEW_VERSION = addStr("system-download-dir-new-version", "");

    // Fenstereinstellungen
    public static StringProperty SYSTEM_SIZE_GUI = addStr("system-size-gui", "1000:800");
    public static StringProperty SYSTEM_SIZE_DIALOG_FILMINFO = addStr("system-size-dialog-filminfo", "600:800");

    // Einstellungen Filmliste
    public static BooleanProperty SYSTEM_LOAD_FILMS_ON_START = addBool("system-load-films-on-start", Boolean.TRUE);
    public static StringProperty SYSTEM_LOAD_NOT_SENDER = addStr("system-load-not-sender", "");
    public static IntegerProperty SYSTEM_LOAD_FILMLIST_MAX_DAYS = addInt("system-load-filmlist-max-days", 0); //es werden nur die x letzten Tage geladen
    public static IntegerProperty SYSTEM_LOAD_FILMLIST_MIN_DURATION = addInt("system-load-filmlist-min-duration", 0); //es werden nur Filme mit mind. x Minuten geladen
    public static StringProperty SYSTEM_PATH_VLC = addStr("system-path-vlc", GetProgramStandardPath.getTemplatePathVlc());

    // Gui Film
    public static BooleanProperty FILM_GUI_FILTER_DIVIDER_ON = addBool("film-gui-filter-divider-on", Boolean.TRUE);
    public static DoubleProperty FILM_GUI_DIVIDER = addDouble("film-gui-divider", ProgConst.GUI_FILME_DIVIDER_LOCATION);
    public static BooleanProperty FILM_GUI_DIVIDER_ON = addBool("film-gui-divider-on", Boolean.TRUE);
    public static StringProperty FILM_GUI_TABLE_WIDTH = addStr("film-gui-table-width");
    public static StringProperty FILM_GUI_TABLE_SORT = addStr("film-gui-table-sort");
    public static StringProperty FILM_GUI_TABLE_UP_DOWN = addStr("film-gui-table-up-down");
    public static StringProperty FILM_GUI_TABLE_VIS = addStr("film-gui-table-vis");
    public static StringProperty FILM_GUI_TABLE_ORDER = addStr("film-gui-table-order");
    public static StringProperty FILM_RESOLUTION = addStr("film-resolution", FilmData.RESOLUTION_NORMAL);
    public static StringProperty FILM_PLAY_DIALOG_SIZE = addStr("film-play-dialog-size");

    // ConfigDialog
    public static StringProperty CONFIG_DIALOG_SIZE = addStr("config-dialog-size", "900:700");
    public static BooleanProperty CONFIG_DIALOG_ACCORDION = addBool("config_dialog-accordion", Boolean.TRUE);

    // StartDialog
    public static StringProperty START_DIALOG_DOWNLOAD_PATH = addStr("start-dialog-download-path", PSystemUtils.getStandardDownloadPath());

    //     FilmInfoDialog
    public static BooleanProperty FILM_INFO_DIALOG_SHOW_URL = addBool("film-info-dialog-show-url", Boolean.FALSE);

    // Shorcuts Hauptmenü
    public static String SHORTCUT_QUIT_PROGRAM_INIT = "Ctrl+Q";
    public static StringProperty SHORTCUT_QUIT_PROGRAM = addStr("SHORTCUT_QUIT_PROGRAM", SHORTCUT_QUIT_PROGRAM_INIT);

    // Shortcuts Filmmenü
    public static String SHORTCUT_SHOW_INFOS_INIT = "Alt+I";
    public static StringProperty SHORTCUT_SHOW_INFOS = addStr("SHORTCUT_SHOW_INFO", SHORTCUT_SHOW_INFOS_INIT);

    public static String SHORTCUT_INFO_FILM_INIT = "Ctrl+I";
    public static StringProperty SHORTCUT_INFO_FILM = addStr("SHORTCUT_INFO_FILM", SHORTCUT_INFO_FILM_INIT);

    public static String SHORTCUT_PLAY_FILM_INIT = "Ctrl+P";
    public static StringProperty SHORTCUT_PLAY_FILM = addStr("SHORTCUT_PLAY_FILM", SHORTCUT_PLAY_FILM_INIT);

    public static String SHORTCUT_SAVE_FILM_INIT = "Ctrl+S";
    public static StringProperty SHORTCUT_SAVE_FILM = addStr("SHORTCUT_SAVE_FILM", SHORTCUT_SAVE_FILM_INIT);

    private static ProgConfig instance;

    static {
        check(SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND, SYSTEM_PARAMETER_DOWNLOAD_TIMEOUT_SECOND_INIT, 5, 200);
        check(SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART, SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_INIT, 0, 10);
        check(SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP, SYSTEM_PARAMETER_DOWNLOAD_MAX_RESTART_HTTP_INIT, 0, 10);
        check(SYSTEM_PARAMETER_DOWNLOAD_CONTINUE_IN_SECONDS, SYSTEM_PARAMETER_DOWNLOAD_CONTINUE_IN_SECONDS_INIT, 5, 200);
        check(SYSTEM_PARAMETER_AUTOMODE_QUITT_IN_SECONDS, SYSTEM_PARAMETER_AUTOMODE_QUITT_IN_SECONDS_INIT, 5, 200);
        check(SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SECOND, SYSTEM_PARAMETER_DOWNLOAD_ERRORMSG_IN_SECOND_INIT, 5, 200);
    }

    private ProgConfig() {
        super(arrayList, "ProgConfig");
    }

    public static final ProgConfig getInstance() {
        return instance == null ? instance = new ProgConfig() : instance;
    }

    public static void addConfigData(ConfigFile configFile) {
        ProgData progData = ProgData.getInstance();

        // Configs der Programmversion, nur damit sie (zur Update-Suche) im Config-File stehen
        ProgConfig.SYSTEM_PROG_VERSION.set(ProgramToolsFactory.getProgVersion());
        ProgConfig.SYSTEM_PROG_BUILD_NO.set(ProgramToolsFactory.getBuild());
        ProgConfig.SYSTEM_PROG_BUILD_DATE.set(ProgramToolsFactory.getCompileDate());

        configFile.addConfigs(ProgConfig.getInstance());//Progconfig
        configFile.addConfigs(ProgColorList.getInstance());//Color

        final FilmFilter akt_sf = progData.actFilmFilterWorker.getActFilterSettings();//akt-Filter
        akt_sf.setName(ActFilmFilterWorker.SELECTED_FILTER_NAME);// nur zur Info im Config-File
        configFile.addConfigs(akt_sf);

        configFile.addConfigs(progData.replaceList);
        configFile.addConfigs(progData.downloadList);

        FilmlistUrlList filmlistUrlList = progData.searchFilmListUrls.getFilmlistUrlList_akt();
        filmlistUrlList.setTag("filmlistUrlList-akt");
        configFile.addConfigs(filmlistUrlList);

        filmlistUrlList = progData.searchFilmListUrls.getFilmlistUrlList_diff();
        filmlistUrlList.setTag("filmlistUrlList-diff");
        configFile.addConfigs(filmlistUrlList);
    }

    public static void logAllConfigs() {
        final ArrayList<String> list = new ArrayList<>();

        list.add(PARAMETER_INFO);

        list.add(PLog.LILNE2);
        list.add("Programmeinstellungen");
        list.add("===========================");
        arrayList.stream().forEach(c -> {
            String s = c.getKey();
            if (s.startsWith("_")) {
                while (s.length() < 55) {
                    s += " ";
                }
            } else {
                while (s.length() < 35) {
                    s += " ";
                }
            }

            list.add(s + "  " + c.getActValueString());
        });
        list.add(PLog.LILNE2);
        PStringUtils.appendString(list, "|  ", "=");

        PLog.emptyLine();
        PLog.sysLog(list);
        PLog.emptyLine();
    }

    private static synchronized void check(IntegerProperty mlConfigs, int init, int min, int max) {
        final int v = mlConfigs.getValue();
        if (v < min || v > max) {
            mlConfigs.setValue(init);
        }
    }

    private static StringProperty addStr(String key) {
        return addStrProp(arrayList, key);
    }

    private static StringProperty addStrC(String comment, String key) {
        return addStrPropC(comment, arrayList, key);
    }

    private static StringProperty addStr(String key, String init) {
        return addStrProp(arrayList, key, init);
    }

    private static StringProperty addStrC(String comment, String key, String init) {
        return addStrPropC(comment, arrayList, key, init);
    }

    private static DoubleProperty addDouble(String key, double init) {
        return addDoubleProp(arrayList, key, init);
    }

    private static DoubleProperty addDoubleC(String comment, String key, double init) {
        return addDoublePropC(comment, arrayList, key, init);
    }

    private static IntegerProperty addInt(String key) {
        return addIntProp(arrayList, key, 0);
    }

    private static IntegerProperty addInt(String key, int init) {
        return addIntProp(arrayList, key, init);
    }

    private static IntegerProperty addIntC(String comment, String key, int init) {
        return addIntPropC(comment, arrayList, key, init);
    }

    private static LongProperty addLong(String key) {
        return addLongProp(arrayList, key, 0);
    }

    private static LongProperty addLong(String key, long init) {
        return addLongProp(arrayList, key, init);
    }

    private static LongProperty addLongC(String comment, String key, long init) {
        return addLongPropC(comment, arrayList, key, init);
    }

    private static BooleanProperty addBool(String key, boolean init) {
        return addBoolProp(arrayList, key, init);
    }

    private static BooleanProperty addBool(String key) {
        return addBoolProp(arrayList, key, Boolean.FALSE);
    }

    private static BooleanProperty addBoolC(String comment, String key, boolean init) {
        return addBoolPropC(comment, arrayList, key, init);
    }
}
