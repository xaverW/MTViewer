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
import de.p2tools.mtviewer.controller.data.ProgIcons;
import de.p2tools.p2Lib.guiTools.PHyperlink;
import de.p2tools.p2Lib.mtFilm.film.FilmData;
import de.p2tools.p2Lib.mtFilm.film.FilmDataXml;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class FilmInfoController extends VBox {
    private final TextArea textArea = new TextArea();
    private final Button btnReset = new Button("@");
    private final Label lblTitle = new Label("");
    private final HBox hBox = new HBox(10);
    private final Label lblUrl = new Label("zur Website: ");

    private FilmData film = null;
    private String oldDescription = "";

    public FilmInfoController() {
        setSpacing(10);
        setPadding(new Insets(10));

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(textArea, btnReset);
        StackPane.setAlignment(btnReset, Pos.BOTTOM_RIGHT);
        stackPane.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(stackPane, Priority.ALWAYS);

        btnReset.setOnAction(a -> resetFilmDescription());
        btnReset.setTooltip(new Tooltip("Beschreibung zur??cksetzen"));
        btnReset.setVisible(false);

        lblTitle.setFont(Font.font(null, FontWeight.BOLD, -1));
        hBox.setAlignment(Pos.CENTER_LEFT);
        lblUrl.setMinWidth(Region.USE_PREF_SIZE);

        textArea.setWrapText(true);
        textArea.setPrefRowCount(4);
        textArea.textProperty().addListener((a, b, c) -> setFilmDescription());

        getChildren().add(lblTitle);
        getChildren().add(stackPane);
        getChildren().add(hBox);
    }

    public void setFilm(FilmData film) {
        hBox.getChildren().clear();

        if (film == null) {
            this.film = null;
            lblTitle.setText("");
            textArea.setText("");
            oldDescription = "";
            btnReset.setVisible(false);
            return;
        }

        this.film = film;

        lblTitle.setText(film.arr[FilmDataXml.FILM_CHANNEL] + "  -  " + film.arr[FilmDataXml.FILM_TITLE]);
        textArea.setText(film.getDescription());
        oldDescription = film.getDescription();
        btnReset.setVisible(false);

        if (!film.arr[FilmDataXml.FILM_WEBSITE].isEmpty()) {
            PHyperlink hyperlink = new PHyperlink(film.arr[FilmDataXml.FILM_WEBSITE],
                    ProgConfig.SYSTEM_PROG_OPEN_URL, ProgIcons.Icons.ICON_BUTTON_FILE_OPEN.getImageView());
            hBox.getChildren().addAll(lblUrl, hyperlink);
        }
    }

    private void setFilmDescription() {
        if (film != null) {
            btnReset.setVisible(true);
            film.setDescription(textArea.getText());
        }
    }

    private void resetFilmDescription() {
        if (film != null) {
            film.setDescription(oldDescription);
            textArea.setText(film.getDescription());
            btnReset.setVisible(false);
        }
    }
}

