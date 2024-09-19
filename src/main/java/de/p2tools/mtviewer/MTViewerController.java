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

package de.p2tools.mtviewer;

import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.data.ProgIcons;
import de.p2tools.mtviewer.controller.film.LoadFilmFactory;
import de.p2tools.mtviewer.gui.FilmGuiPack;
import de.p2tools.mtviewer.gui.StatusBarController;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MTViewerController extends StackPane {

    private final ProgData progData;
    FilmGuiPack filmGuiPack = new FilmGuiPack();

    public MTViewerController() {
        progData = ProgData.getInstance();
        init();
    }

    private void init() {
        try {
            // Center
            VBox vBoxFilm = filmGuiPack.pack();
            StatusBarController statusBarController;
            statusBarController = new StatusBarController(progData);

            // Gui zusammenbauen
            VBox vBox = new VBox();
            vBox.getChildren().addAll(vBoxFilm, statusBarController);
            VBox.setVgrow(vBoxFilm, Priority.ALWAYS);

            this.setPadding(new Insets(0));
            this.getChildren().addAll(vBox, progData.maskerPane);
            initMaskerPane();
        } catch (Exception ex) {
            P2Log.errorLog(597841023, ex);
        }
    }

    private void initMaskerPane() {
        StackPane.setAlignment(progData.maskerPane, Pos.CENTER);
        progData.maskerPane.setPadding(new Insets(4, 1, 1, 1));
        progData.maskerPane.toFront();
        Button btnStop = progData.maskerPane.getButton();
        progData.maskerPane.setButtonText("");
        btnStop.setGraphic(ProgIcons.ICON_BUTTON_STOP.getImageView());
        btnStop.setOnAction(a -> LoadFilmFactory.getInstance().loadFilmlist.setStop(true));
    }

    public void setInfos() {
        ProgConfig.GUI_INFO_IS_SHOWING.setValue(!ProgConfig.GUI_INFO_IS_SHOWING.getValue());
    }

    public void setFocus() {
        progData.filmGuiController.isShown();
    }
}
