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

import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.filmfilter.FilmFilter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Worker {

    final FilmFilter sfTemp = new FilmFilter();
    private final ProgData progData;
    private ObservableList<String> allChannelList = FXCollections.observableArrayList("");

    public Worker(ProgData progData) {
        this.progData = progData;
        ProgConfig.SYSTEM_SHOW_MEDIATHEK.addListener((u, o, n) -> progData.loadFactory.setList());
        ProgConfig.SYSTEM_SHOW_AUDIOTHEK.addListener((u, o, n) -> progData.loadFactory.setList());
    }

    public void saveFilter() {
        progData.actFilmFilterWorker.getActFilterSettings().copyTo(sfTemp);
    }

    public void resetFilter() {
        ArrayList<String> sender = new ArrayList<>(Arrays.asList(progData.filmlist.sender));
        List<String> audio = Arrays.asList(progData.audioList.sender);
        audio.forEach(a -> {
            if (!sender.contains(a)) {
                sender.add(a);
            }
        });

        allChannelList.setAll(sender);
        sfTemp.copyTo(progData.actFilmFilterWorker.getActFilterSettings());
    }

    public ObservableList<String> getAllChannelList() {
        return allChannelList;
    }
}
