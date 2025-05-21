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

import de.p2tools.mtviewer.controller.config.PEvents;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.p2lib.p2event.P2Listener;
import de.p2tools.p2lib.tools.duration.P2Duration;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.application.Platform;

import java.util.concurrent.atomic.AtomicBoolean;

public class FilmFilterRunner {
    private static final AtomicBoolean search = new AtomicBoolean(false);
    private static final AtomicBoolean research = new AtomicBoolean(false);
    private final ProgData progData;
    int count = 0;

    /**
     * hier wird das Filtern der Filmliste "angestoßen"
     *
     * @param progData
     */
    public FilmFilterRunner(ProgData progData) {
        this.progData = progData;

        progData.actFilmFilterWorker.filterChangeProperty().addListener((observable, oldValue, newValue) -> filter()); // Filmfilter (User) haben sich geändert
        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_BLACKLIST_CHANGED) {
            @Override
            public void pingGui() {
                filterList();
            }
        });
        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_DIACRITIC_CHANGED) {
            @Override
            public void pingGui() {
                filterList();
            }
        });
    }

    public void filter() {
        Platform.runLater(() -> filterList());
    }

    private void filterList() {
        // ist etwas "umständlich", scheint aber am flüssigsten zu laufen
        if (!search.getAndSet(true)) {
            research.set(false);
            try {
                Platform.runLater(() -> {
                    P2Log.debugLog("========================================");
                    P2Log.debugLog("         === Filter: " + count++ + " ===");
                    P2Log.debugLog("========================================");

                    P2Duration.counterStart("FilmFilterRunner.filterList");
                    progData.filmlistUsed.filteredListSetPred(
                            PredicateFactory.getPredicate(progData.actFilmFilterWorker.getActFilterSettings()));
                    P2Duration.counterStop("FilmFilterRunner.filterList");

                    search.set(false);
                    if (research.get()) {
                        filterList();
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace(); //todo???
            }
        } else {
            research.set(true);
        }
    }
}
