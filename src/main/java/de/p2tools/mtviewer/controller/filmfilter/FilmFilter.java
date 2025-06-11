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

package de.p2tools.mtviewer.controller.filmfilter;

import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.p2lib.mediathek.filter.FilterCheck;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.animation.PauseTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.util.Duration;

public final class FilmFilter extends FilmFilterProps {

    private final BooleanProperty filterChange = new SimpleBooleanProperty(false);
    private final PauseTransition pause = new PauseTransition(Duration.millis(200));
    private boolean reportChange = true;


    public FilmFilter() {
        initFilter();
        setName("Filter");
    }

    public FilmFilter(String name) {
        initFilter();
        setName(name);
    }

    public FilmFilter(String name, String tag) {
        initFilter();
        setName(name);
        setTag(tag);
    }

    public boolean isReportChange() {
        return reportChange;
    }

    public void setReportChange(boolean reportChange) {
        this.reportChange = reportChange;
    }

    public BooleanProperty filterChangeProperty() {
        return filterChange;
    }

    public void reportFilterReturn() {
        P2Log.debugLog("reportFilterReturn");
        pause.stop();
        filterChange.setValue(!filterChange.getValue());
    }

    private void initFilter() {
        pause.setOnFinished(event -> reportFilterChange());
        pause.setDuration(Duration.millis(ProgConfig.SYSTEM_FILTER_WAIT_TIME.getValue()));
        ProgConfig.SYSTEM_FILTER_WAIT_TIME.addListener((observable, oldValue, newValue) -> {
            P2Log.debugLog("SYSTEM_FILTER_WAIT_TIME: " + ProgConfig.SYSTEM_FILTER_WAIT_TIME.getValue());
            pause.setDuration(Duration.millis(ProgConfig.SYSTEM_FILTER_WAIT_TIME.getValue()));
        });

        clearFilter();
        listFilmProperty().addListener(l -> setFilterChange());
        listAudioProperty().addListener(l -> setFilterChange());
        
        nameProperty().addListener(l -> setFilterChange());
        channelProperty().addListener(l -> setFilterChange());
        themeProperty().addListener(l -> setTxtFilterChange());
        titleProperty().addListener(l -> setTxtFilterChange());
        somewhereProperty().addListener(l -> setTxtFilterChange());
        timeRangeProperty().addListener(l -> setFilterChange());
        minDurProperty().addListener(l -> setFilterChange());
        maxDurProperty().addListener(l -> setFilterChange());
        onlyNewProperty().addListener(l -> setFilterChange());
        onlyLiveProperty().addListener(l -> setFilterChange());
    }

    private void setTxtFilterChange() {
        //wird auch ausgelöst durch Eintrag in die FilterHistory, da wird ein neuer SelectedFilter angelegt
        P2Log.debugLog("setTxtFilterChange");
        if (ProgConfig.SYSTEM_FILTER_RETURN.getValue()) {
            //dann wird erst nach "RETURN" gestartet
            pause.stop();

        } else {
            pause.playFromStart();
        }
    }

    private void setFilterChange() {
        //wird auch ausgelöst durch Eintrag in die FilterHistory, da wird ein neuer SelectedFilter angelegt
        P2Log.debugLog("setFilterChange");
        pause.playFromStart();
    }

    private void reportFilterChange() {
        if (reportChange) {
            filterChange.setValue(!filterChange.getValue());
        }
    }

    public void clearFilter() {
        // alle Filter löschen, Button Black bleibt, wie er ist
        setChannel("");
        setTheme("");
        setTitle("");
        setSomewhere("");
        setTimeRange(FilterCheck.FILTER_ALL_OR_MIN);
        setMinDur(FilterCheck.FILTER_ALL_OR_MIN);
        setMaxDur(FilterCheck.FILTER_DURATION_MAX_MINUTE);
        setOnlyNew(false);
        setOnlyLive(false);
    }
}
