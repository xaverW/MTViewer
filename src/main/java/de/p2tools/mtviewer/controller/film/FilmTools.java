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

package de.p2tools.mtviewer.controller.film;

import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.data.ProgIcons;
import de.p2tools.mtviewer.controller.data.download.DownloadData;
import de.p2tools.mtviewer.controller.downloadtools.DownloadProgParameterFactory;
import de.p2tools.mtviewer.gui.dialog.DownloadAddDialogController;
import de.p2tools.mtviewer.gui.dialog.FilmPlayDialogController;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.guitools.P2Open;
import de.p2tools.p2lib.mtfilm.film.FilmData;

import java.text.NumberFormat;
import java.util.Locale;

import static de.p2tools.mtviewer.controller.downloadtools.RuntimeExec.TRENNER_PROG_ARRAY;

public class FilmTools {

    private static final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.GERMANY);

    public static void playFilm(FilmData film) {
        String resolution = ProgConfig.FILM_RESOLUTION.getValueSafe();

        if (resolution.equals(FilmData.RESOLUTION_ASK)) {
            FilmPlayDialogController filmPlayDialogController =
                    new FilmPlayDialogController(ProgConfig.FILM_PLAY_DIALOG_SIZE, ProgData.getInstance(), film);
            if (!filmPlayDialogController.isOk()) {
                return;
            } else {
                resolution = filmPlayDialogController.getResolution();
            }
        }
        final String url = film.getUrlForResolution(resolution);

        String strProgCallArray = "";
        strProgCallArray = DownloadProgParameterFactory.getProgParameterArray(url);
        String[] arrProgCallArray = null;
        arrProgCallArray = strProgCallArray.split(TRENNER_PROG_ARRAY);

        P2Open.playStoredFilm(arrProgCallArray, ProgConfig.SYSTEM_PROG_PLAY,
                url, ProgIcons.ICON_BUTTON_FILE_OPEN.getImageView());
    }

    public static void playFilm(String pathFile) {
        String strProgCallArray = "";
        strProgCallArray = DownloadProgParameterFactory.getProgParameterArray(pathFile);
        String[] arrProgCallArray = null;
        arrProgCallArray = strProgCallArray.split(TRENNER_PROG_ARRAY);

        P2Open.playStoredFilm(arrProgCallArray, ProgConfig.SYSTEM_PROG_PLAY,
                pathFile, ProgIcons.ICON_BUTTON_FILE_OPEN.getImageView());
    }

    public static synchronized String getStatusInfosFilm() {
        String textLinks;
        final int sumFilmlist = ProgData.getInstance().filmlistUsed.size();
        final int sumFilmShown = ProgData.getInstance().filmGuiPack.getFilmGuiController().getFilmCount();

        String sumFilmlistStr = numberFormat.format(sumFilmShown);
        String sumFilmShownStr = numberFormat.format(sumFilmlist);

        // Anzahl der Filme
        if (sumFilmShown == 1) {
            textLinks = "1 Film";
        } else {
            textLinks = sumFilmlistStr + " Filme";
        }
        if (sumFilmlist != sumFilmShown) {
            textLinks += " (Insgesamt: " + sumFilmShownStr + " )";
        }
        return textLinks;
    }

    public static void saveFilm(FilmData film) {
        if (film == null) {
            return;
        }

        ProgData progData = ProgData.getInstance();
        // erst mal schauen obs den schon gibt
        DownloadData download = progData.downloadList.getDownloadUrlFilm(film.arr[FilmData.FILM_URL]);
        if (download != null) {
            // dann ist der Film schon in der Downloadliste
            P2Alert.BUTTON answer = P2Alert.showAlert_yes_no("Anlegen?", "Nochmal anlegen?",
                    "Download für den Film existiert bereits:" + P2LibConst.LINE_SEPARATORx2 +
                            film.getTitle() + P2LibConst.LINE_SEPARATORx2 +
                            "Nochmal anlegen?");
            switch (answer) {
                case NO:
                    // alles Abbrechen
                    return;
            }
        }

        new DownloadAddDialogController(progData, null, film, false);
    }
}


