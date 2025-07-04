/*
 * P2tools Copyright (C) 2021 W. Xaver W.Xaver[at]googlemail.com
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


package de.p2tools.mtviewer.gui.help.table;

import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.p2lib.mediathek.filmdata.FilmData;
import javafx.scene.control.TableRow;
import javafx.scene.control.Tooltip;


public class TableRowFilm<T> extends TableRow<T> {


    public TableRowFilm() {
    }

    @Override
    public void updateItem(T f, boolean empty) {
        super.updateItem(f, empty);

        FilmData film = (FilmData) f;
        if (film == null || empty) {
            setStyle("");
        } else {
            if (ProgConfig.FILM_GUI_SHOW_TABLE_TOOL_TIP.getValue()) {
                setTooltip(new Tooltip(film.getTheme() + "\n" + film.getTitle()));
            }
        }
    }
}
