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

package de.p2tools.mtviewer.gui;

import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgData;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class FilmGuiPack {

    final FilmFilterController filterController = new FilmFilterController();
    final FilmGuiController filmGuiController;
    ProgData progData;
    private VBox vBox = new VBox();

    public FilmGuiPack() {
        progData = ProgData.getInstance();
        filmGuiController = new FilmGuiController();
        progData.filmGuiController = filmGuiController;
    }

    public VBox pack() {
        ProgConfig.FILM_GUI_FILTER_DIVIDER_ON.setValue(true);
        ProgConfig.FILM_GUI_FILTER_DIVIDER_ON.addListener((observable, oldValue, newValue) -> setFilterPane());

        vBox.getChildren().addAll(filterController, filmGuiController);
        VBox.setVgrow(filmGuiController, Priority.ALWAYS);
        return vBox;
    }

    private void setFilterPane() {
        if (ProgConfig.FILM_GUI_FILTER_DIVIDER_ON.getValue()) {
            vBox.getChildren().clear();
            vBox.getChildren().addAll(filterController, filmGuiController);
            VBox.setVgrow(filmGuiController, Priority.ALWAYS);

        } else {
            vBox.getChildren().clear();
            vBox.getChildren().addAll(filmGuiController);
            VBox.setVgrow(filmGuiController, Priority.ALWAYS);
        }
    }
}
