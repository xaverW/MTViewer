/*
 * P2Tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
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

package de.p2tools.mtviewer.controller.load.loadaudiolist;

import de.p2tools.mtviewer.controller.load.LoadAudioFactoryDto;
import de.p2tools.p2lib.mtfilm.film.FilmlistFactory;
import de.p2tools.p2lib.mtfilm.readwritefilmlist.P2ReadFilmlist;
import de.p2tools.p2lib.tools.duration.P2Duration;
import de.p2tools.p2lib.tools.log.P2Log;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ReadLocalAudioList {

    private final List<String> logList;
    private static int countDouble = 0;

    public ReadLocalAudioList(List<String> logList) {
        this.logList = logList;
    }

    public boolean readLocalList(Path path) {
        // beim Programmstart wird die gespeicherte Liste geladen
        boolean ret;
        P2Duration.counterStart("readDb");

        try {
            if (!Files.exists(path) || path.toFile().length() == 0) {
                return false;
            }

            LoadAudioFactoryDto.audioListAkt.clear();
            LoadAudioFactoryDto.audioListNew.clear();
            logList.add("## " + "Audioliste lesen");
            logList.add("## " + "   --> Lesen von: " + path);
            new P2ReadFilmlist().readFilmlistWebOrLocal(logList, LoadAudioFactoryDto.audioListNew, path.toString());
            setDateFromLocal();

            logList.add("## Filme markieren");
            final int count = LoadAudioFactoryDto.audioListNew.markFilms();

            logList.add("## Anzahl doppelte Filme: " + count);
            LoadAudioFactoryDto.audioListNew.loadSender();

            logList.add("##   Audioliste gelesen, OK");
            logList.add("##   Anzahl gelesen: " + LoadAudioFactoryDto.audioListNew.size());
            ret = true;

        } catch (final Exception ex) {
            logList.add("##   Audioliste lesen hat nicht geklappt");
            P2Log.errorLog(645891204, ex);
            ret = false;
        }
        return ret;
    }

    private void setDateFromLocal() {
        // Datum setzen, Format stimmt da ja schon!
        String date = FilmlistFactory.genDate(LoadAudioFactoryDto.audioListNew.metaData);
        LoadAudioFactoryDto.audioListDate.set(date);
    }
}
