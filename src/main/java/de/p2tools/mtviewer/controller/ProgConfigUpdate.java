/*
 * P2tools Copyright (C) 2019 W. Xaver W.Xaver[at]googlemail.com
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


import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.config.ProgInfos;
import de.p2tools.p2lib.mediathek.filter.FilterCheck;

public class ProgConfigUpdate {
    // hier werden geänderte Programmeinstellungen/Funktionen angepasst,
    // muss immer nur einmal laufen!!
    private ProgConfigUpdate() {
    }

    public static void setUpdateDone() {
        ProgConfig.SYSTEM_AFTER_UPDATE_FILTER.setValue(true);
        ProgConfig.SYSTEM_CHANGE_LOG_DIR.setValue(true); // für Version 17
    }

    public static void update() {
        if (!ProgConfig.SYSTEM_AFTER_UPDATE_FILTER.getValue()) {
            // dann müssen die gespeicherten Filter aktualisiert werden
            final int FILTER_DAYS_MAX__OLD = 30; // ist der alte Wert für "alles"

            if (ProgData.getInstance().actFilmFilterWorker.getActFilterSettings().getTimeRange() == FILTER_DAYS_MAX__OLD) {
                ProgData.getInstance().actFilmFilterWorker.getActFilterSettings().setTimeRange(FilterCheck.FILTER_ALL_OR_MIN);
            }
        }

        if (!ProgConfig.SYSTEM_CHANGE_LOG_DIR.getValue()) {
            // dann sind noch alte LogDir Einstellungen gespeichert
            final String logDir = ProgConfig.SYSTEM_LOG_DIR.getValueSafe();
            final String standardDir = ProgInfos.getStandardLogDirectory_String();
            if (logDir.equals(standardDir)) {
                // wenn eh der StandardPfad drin steht, dann löschen
                ProgConfig.SYSTEM_LOG_DIR.setValue("");
            }
        }

        setUpdateDone();
    }
}
