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
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.PColumnConstraints;
import de.p2tools.p2lib.guitools.PHyperlink;
import de.p2tools.p2lib.mtfilm.film.FilmData;
import de.p2tools.p2lib.mtfilm.film.FilmDataXml;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class FilmInfoController extends VBox {
    private final SplitPane splitPane = new SplitPane();
    private final VBox vBoxLeft = new VBox();

    private final TextArea textArea = new TextArea();
    private final Button btnReset = new Button("@");
    private final Label lblTheme = new Label("");
    private final Label lblTitle = new Label("");
    private final HBox hBoxUrl = new HBox(10);
    private final Label lblUrl = new Label("zur Website: ");

    private final Label lblDate = new Label();
    private final Label lblTime = new Label();
    private final Label lblDuration = new Label();
    private final Label lblSize = new Label();

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
        btnReset.setTooltip(new Tooltip("Beschreibung zurücksetzen"));
        btnReset.setVisible(false);

        lblTheme.setFont(Font.font(null, FontWeight.BOLD, -1));
        hBoxUrl.setAlignment(Pos.CENTER_LEFT);
        lblUrl.setMinWidth(Region.USE_PREF_SIZE);

        textArea.setWrapText(true);
        textArea.setPrefRowCount(4);
        textArea.textProperty().addListener((a, b, c) -> setFilmDescription());


        VBox v = new VBox();
        v.setSpacing(0);
        v.getChildren().addAll(lblTheme, lblTitle);
        vBoxLeft.setSpacing(2);
        vBoxLeft.setPadding(new Insets(P2LibConst.DIST_EDGE));
        vBoxLeft.getChildren().addAll(v, stackPane, hBoxUrl);

        final GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("extra-pane-info");
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.DIST_EDGE));
        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(), PColumnConstraints.getCcComputedSizeAndHgrow());

        int row = 0;
        gridPane.add(new Label("Datum: "), 0, row);
        gridPane.add(lblDate, 1, row);
        gridPane.add(new Label("Zeit: "), 0, ++row);
        gridPane.add(lblTime, 1, row);
        gridPane.add(new Label("Dauer: "), 0, ++row);
        gridPane.add(lblDuration, 1, row);
        gridPane.add(new Label("Größe: "), 0, ++row);
        gridPane.add(lblSize, 1, row);

        splitPane.getItems().addAll(vBoxLeft, gridPane);
        splitPane.getDividers().get(0).positionProperty().bindBidirectional(ProgConfig.FILM_GUI_INFO_DIVIDER);
        SplitPane.setResizableWithParent(gridPane, false);

        setSpacing(0);
        setPadding(new Insets(0));
        VBox.setVgrow(splitPane, Priority.ALWAYS);
        getChildren().add(splitPane);
    }

    public void setFilm(FilmData film) {
        hBoxUrl.getChildren().clear();

        if (film == null) {
            this.film = null;
            lblTheme.setText("");
            lblTitle.setText("");
            textArea.clear();
            oldDescription = "";
            btnReset.setVisible(false);

            lblDate.setText("");
            lblTime.setText("");
            lblDuration.setText("");
            lblSize.setText("");
            return;
        }

        this.film = film;

        lblTheme.setText(film.arr[FilmDataXml.FILM_CHANNEL] + "  -  " + film.arr[FilmDataXml.FILM_THEME]);
        lblTitle.setText(film.arr[FilmDataXml.FILM_TITLE]);
        textArea.setText(film.getDescription());
        oldDescription = film.getDescription();
        btnReset.setVisible(false);

        if (!film.arr[FilmDataXml.FILM_WEBSITE].isEmpty()) {
            PHyperlink hyperlink = new PHyperlink(film.arr[FilmDataXml.FILM_WEBSITE],
                    ProgConfig.SYSTEM_PROG_OPEN_URL, ProgIcons.ICON_BUTTON_FILE_OPEN.getImageView());
            hBoxUrl.getChildren().addAll(lblUrl, hyperlink);
        }

        lblDate.setText(film.getDate().get_dd_MM_yyyy());
        lblTime.setText(film.getTime());
        lblDuration.setText(film.getDuration() + " [min]");
        lblSize.setText(film.getFilmSize().toString() + " [MB]");
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
