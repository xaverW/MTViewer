/*
 * P2tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
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


package de.p2tools.mtviewer.controller.worker;

import de.p2tools.mtviewer.controller.config.PEvents;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.gui.tools.Listener;
import de.p2tools.p2lib.mtfilm.tools.SearchFilmlistUpdate;
import de.p2tools.p2lib.p2event.P2Listener;

public class CheckForNewFilmlist extends SearchFilmlistUpdate {

    public CheckForNewFilmlist(ProgData progData) {
        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_FILMLIST_LOAD_FINISHED) {
            @Override
            public void pingGui() {
                //dann wird wieder gesucht
                setFoundNewList(false);
            }
        });
        Listener.addListener(new Listener(Listener.EVENT_TIMER, CheckForNewFilmlist.class.getSimpleName()) {
            @Override
            public void pingFx() {
                hasNewFilmlist(ProgData.getInstance().filmlist.getFilmlistId());
            }
        });
    }
}
