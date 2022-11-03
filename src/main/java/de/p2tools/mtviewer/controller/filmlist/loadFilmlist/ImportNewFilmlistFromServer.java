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

package de.p2tools.mtviewer.controller.filmlist.loadFilmlist;

import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.data.film.Filmlist;
import de.p2tools.mtviewer.controller.filmlist.filmlistUrls.SearchFilmListUrls;

import javax.swing.event.EventListenerList;
import java.util.ArrayList;
import java.util.List;

enum STATE {
    COMPLETE, DIFF
}

public class ImportNewFilmlistFromServer {

    private final EventListenerList eventListenerList;
    private final ProgData progData;

    public ImportNewFilmlistFromServer(ProgData progData) {
        this.progData = progData;
        eventListenerList = new EventListenerList();
    }

    // #########################################################
    // Filmliste importieren, URL automatisch wählen
    // #########################################################
    public void importFilmListAuto(List<String> logList, Filmlist filmlist, Filmlist filmlistDiff) {
        STATE state;
        boolean ret;
        if (filmlist.isTooOldForDiff()) {
            // dann eine komplette Liste laden
            state = STATE.COMPLETE;
            filmlist.clear();
            logList.add("komplette Filmliste laden");
            ret = loadList(logList, filmlist, state);
        } else {
            // nur ein Update laden
            state = STATE.DIFF;
            logList.add("Diffliste laden");
            ret = loadList(logList, filmlistDiff, state);
            if (!ret || filmlistDiff.isEmpty()) {
                // wenn diff, dann nochmal mit einer kompletten Liste versuchen
                state = STATE.COMPLETE;
                filmlist.clear();
                filmlistDiff.clear();
                logList.add("Diffliste war leer, komplette Filmliste laden");
                ret = loadList(logList, filmlist, state);
            }
        }
        if (!ret) {
            logList.add(951235497, "Es konnten keine Filme geladen werden!");
        }
        reportFinished(ret);
    }

    private synchronized void reportFinished(boolean ok) {
        for (final ListenerLoadFilmlist l : eventListenerList.getListeners(ListenerLoadFilmlist.class)) {
            l.finished(new ListenerFilmlistLoadEvent("", "", 0, 0, !ok));
        }
    }

    private boolean loadList(List<String> logList, Filmlist list, STATE state) {
        boolean ret = false;
        final ArrayList<String> usedUrls = new ArrayList<>();
        String updateUrl;
        final int maxRetries = (state == STATE.DIFF ? 2 : 3); // 3x (bei diff nur 2x) probieren, eine Liste zu laden

        for (int i = 0; i < maxRetries; ++i) {
            updateUrl = getUpdateUrl(state, usedUrls);
            if (updateUrl.isEmpty()) {
                return false;
            }

            new ReadFilmlist().readFilmlist(logList, updateUrl, list);
            ret = !list.isEmpty();

            if (ret && i < 1 && list.isOlderThan(5 * 60 * 60 /* sekunden */)) {
                // Laden hat geklappt ABER: Liste zu alt, dann gibts einen 2. Versuch
                logList.add("Filmliste zu alt, neuer Versuch");
                ret = false;
            }

            if (ret) {
                // hat geklappt, nix wie weiter
                return true;
            }

            if (ProgData.getInstance().loadFilmlist.isStop()) {
                // wenn abgebrochen wurde, nicht weitermachen
                return false;
            }

            // dann hat das Laden schon mal nicht geklappt
            SearchFilmListUrls.setUpdateFilmlistUrls(); // für die DownloadURLs "aktualisieren" setzen
        }

        return ret;
    }

    private String getUpdateUrl(STATE state, ArrayList<String> usedUrls) {
        // nächste Adresse in der Liste wählen
        final String updateUrl;

        switch (state) {
            case DIFF:
                updateUrl = progData.searchFilmListUrls.getFilmlistUrlForDiffList(usedUrls);
                break;
            case COMPLETE:
            default:
                updateUrl = progData.searchFilmListUrls.getFilmlistUrlForCompleteList(usedUrls);
                break;
        }

        return updateUrl;
    }
}
