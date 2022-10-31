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

import de.p2tools.mtviewer.controller.ProgSave;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.filmlist.loadFilmlist.ListenerFilmlistLoadEvent;
import de.p2tools.mtviewer.controller.filmlist.loadFilmlist.ListenerLoadFilmlist;
import de.p2tools.mtviewer.tools.filmFilter.FilmFilter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Arrays;

public class Worker {

    final FilmFilter sfTemp = new FilmFilter();
    private final ProgData progData;
    private ObservableList<String> allChannelList = FXCollections.observableArrayList("");

    public Worker(ProgData progData) {
        this.progData = progData;

        progData.loadFilmlist.addListenerLoadFilmlist(new ListenerLoadFilmlist() {
            @Override
            public void start(ListenerFilmlistLoadEvent event) {
                if (event.progress == ListenerLoadFilmlist.PROGRESS_INDETERMINATE) {
                    // ist dann die gespeicherte Filmliste
                    progData.maskerPane.setMaskerVisible(true, false);
                } else {
                    progData.maskerPane.setMaskerVisible(true, true);
                }
                progData.maskerPane.setMaskerProgress(event.progress, event.text);

                // the channel combo will be reseted, therefore save the filter
                saveFilter();
            }

            @Override
            public void progress(ListenerFilmlistLoadEvent event) {
                progData.maskerPane.setMaskerProgress(event.progress, event.text);
            }

            @Override
            public void loaded(ListenerFilmlistLoadEvent event) {
                progData.maskerPane.setMaskerVisible(true, false);
                progData.maskerPane.setMaskerProgress(ListenerLoadFilmlist.PROGRESS_INDETERMINATE, "Filmliste verarbeiten");
            }

            @Override
            public void finished(ListenerFilmlistLoadEvent event) {
                new ProgSave().saveAll(); // damit nichts verloren geht
                allChannelList.setAll(Arrays.asList(progData.filmlist.sender));//alle Sender laden
                progData.maskerPane.setMaskerVisible(false);

                // activate the saved filter
                resetFilter();
            }
        });
    }

    private void saveFilter() {
        progData.actFilmFilterWorker.getActFilterSettings().copyTo(sfTemp);
    }

    private void resetFilter() {
        sfTemp.copyTo(progData.actFilmFilterWorker.getActFilterSettings());
    }

    public ObservableList<String> getAllChannelList() {
        return allChannelList;
    }
}
