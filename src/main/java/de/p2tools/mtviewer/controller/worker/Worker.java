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

package de.p2tools.mtviewer.controller.worker;

import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.filmfilter.FilmFilter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Arrays;

public class Worker {

    final FilmFilter sfTemp = new FilmFilter();
    private final ProgData progData;
    private ObservableList<String> allChannelList = FXCollections.observableArrayList("");

    public Worker(ProgData progData) {
        this.progData = progData;
    }

    public void saveFilter() {
        progData.actFilmFilterWorker.getActFilterSettings().copyTo(sfTemp);
    }

    public void resetFilter() {
        allChannelList.setAll(Arrays.asList(progData.filmlist.sender));//alle Sender laden
        sfTemp.copyTo(progData.actFilmFilterWorker.getActFilterSettings());
    }

    public ObservableList<String> getAllChannelList() {
        return allChannelList;
    }
}
