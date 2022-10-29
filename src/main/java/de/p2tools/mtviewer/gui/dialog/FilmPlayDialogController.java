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

package de.p2tools.mtviewer.gui.dialog;

import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.data.film.FilmData;
import de.p2tools.mtviewer.controller.data.film.FilmDataXml;
import de.p2tools.p2Lib.dialogs.dialog.PDialogExtra;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class FilmPlayDialogController extends PDialogExtra {

    private final ProgData progData;
    private final FilmData filmData;

    private final RadioButton rbHd = new RadioButton("Film in HD laden");
    private final RadioButton rbHeight = new RadioButton("Film in hoher Auflösung laden");
    private final RadioButton rbLow = new RadioButton("Film in kleiner Auflösung laden");

    private Button btnCancel = new Button("_Abbrechen");
    private Button btnOK = new Button("_OK");

    private CheckBox chkSave = new CheckBox("Auflösung in Zukunft verwenden");
    private GridPane gridPane = new GridPane();
    private boolean ok = false;

    public FilmPlayDialogController(StringProperty conf, ProgData progData,
                                    FilmData filmData) {
        super(progData.primaryStage, conf, "Film ansehen", true, false);

        this.progData = progData;
        this.filmData = filmData;
        init(true);
    }

    public boolean isOk() {
        return ok;
    }

    public String getResolution() {
        if (rbHd.isSelected()) {
            return FilmData.RESOLUTION_HD;
        }
        if (rbLow.isSelected()) {
            return FilmData.RESOLUTION_SMALL;
        }
        return FilmData.RESOLUTION_NORMAL;
    }

    @Override
    public void make() {
        ToggleGroup tg = new ToggleGroup();
        rbHd.setToggleGroup(tg);
        rbHeight.setToggleGroup(tg);
        rbLow.setToggleGroup(tg);
        rbHeight.setSelected(true);

        initCont();
        initButton();
    }

    private void initCont() {
        // Gridpane
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        VBox.setVgrow(gridPane, Priority.ALWAYS);
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        int row = 0;

        Label lblFilmTitle = new Label("");
        lblFilmTitle.setStyle("-fx-font-weight: bold;");
        lblFilmTitle.setText(filmData.getTitle());
        gridPane.add(new Label(FilmDataXml.COLUMN_NAMES[FilmData.FILM_TITLE] + ':'), 0, row);
        gridPane.add(lblFilmTitle, 1, row);

        Label lblFilmDuration = new Label("");
        lblFilmDuration.setStyle("-fx-font-weight: bold;");
        lblFilmDuration.setText(filmData.getDurationMinute() + "");
        gridPane.add(new Label(FilmDataXml.COLUMN_NAMES[FilmData.FILM_DURATION] + ":"), 0, ++row);
        gridPane.add(lblFilmDuration, 1, row);

        gridPane.add(new Label(), 0, ++row);
        gridPane.add(rbHd, 0, ++row, 2, 1);
        gridPane.add(rbHeight, 0, ++row, 2, 1);
        gridPane.add(rbLow, 0, ++row, 2, 1);

        gridPane.add(new Label(), 0, ++row);
        gridPane.add(chkSave, 0, ++row, 2, 1);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow());

        getvBoxCont().setPadding(new Insets(5));
        getvBoxCont().setSpacing(20);
        getvBoxCont().getChildren().addAll(gridPane);

        addCancelButton(btnCancel);
        addOkButton(btnOK);
    }

    private void initButton() {
        btnCancel.setOnAction(event -> {
            quit();
        });

        btnOK.setOnAction(event -> {
            if (chkSave.isSelected()) {
                setResolution();
            }
            ok = true;
            quit();
        });
    }

    private void setResolution() {
        if (rbHd.isSelected()) {
            ProgConfig.FILM_RESOLUTION.setValue(FilmData.RESOLUTION_HD);
        } else if (rbLow.isSelected()) {
            ProgConfig.FILM_RESOLUTION.setValue(FilmData.RESOLUTION_SMALL);
        } else {
            ProgConfig.FILM_RESOLUTION.setValue(FilmData.RESOLUTION_NORMAL);
        }
    }

    private void quit() {
        close();
    }
}
