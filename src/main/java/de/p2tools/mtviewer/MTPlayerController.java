/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
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
import de.p2tools.mtviewer.gui.FilmGuiPack;
import de.p2tools.mtviewer.gui.StatusBarController;
import de.p2tools.p2Lib.guiTools.pMask.PMaskerPane;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MTPlayerController extends StackPane {

    private final ProgData progData;
    FilmGuiPack filmGuiPack = new FilmGuiPack();
    private PMaskerPane maskerPane = new PMaskerPane();


    public MTPlayerController() {
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
            this.getChildren().addAll(vBox, maskerPane);
            initMaskerPane();
        } catch (Exception ex) {
            PLog.errorLog(597841023, ex);
        }
    }

    private void initMaskerPane() {
        StackPane.setAlignment(maskerPane, Pos.CENTER);
        progData.maskerPane = maskerPane;
        maskerPane.setPadding(new Insets(4, 1, 1, 1));
        maskerPane.toFront();
        Button btnStop = maskerPane.getButton();
        maskerPane.setButtonText("");
        btnStop.setGraphic(ProgIcons.Icons.ICON_BUTTON_STOP.getImageView());
        btnStop.setOnAction(a -> progData.loadFilmlist.setStop(true));
    }

    public void setInfos() {
        ProgConfig.FILM_GUI_DIVIDER_ON.setValue(!ProgConfig.FILM_GUI_DIVIDER_ON.getValue());
    }

    public void setFocus() {
        progData.filmGuiController.isShown();
    }
}
