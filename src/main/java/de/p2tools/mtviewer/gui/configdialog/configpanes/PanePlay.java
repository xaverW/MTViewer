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

package de.p2tools.mtviewer.gui.configdialog.configpanes;

import de.p2tools.mtviewer.controller.config.ProgColorList;
import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.data.ProgIcons;
import de.p2tools.mtviewer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.PDirFileChooser;
import de.p2tools.p2lib.guitools.PButton;
import de.p2tools.p2lib.guitools.PColumnConstraints;
import de.p2tools.p2lib.mtdownload.GetProgramStandardPath;
import de.p2tools.p2lib.mtfilm.film.FilmData;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.File;
import java.util.Collection;

public class PanePlay {

    private final Stage stage;
    private final RadioButton rbAsk = new RadioButton("Immer vorher fragen");
    private final RadioButton rbHd = new RadioButton("Film in HD abspielen");
    private final RadioButton rbHeight = new RadioButton("Film in hoher Auflösung abspielen");
    private final RadioButton rbLow = new RadioButton("Film in kleiner Auflösung abspielen");
    StringProperty propProgram = ProgConfig.SYSTEM_PROG_PLAY;
    StringProperty propParameter = ProgConfig.SYSTEM_PROG_PLAY_PARAMETER;

    public PanePlay(Stage stage) {
        this.stage = stage;
    }

    public void close() {
    }

    public TitledPane make(Collection<TitledPane> result) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.DIST_EDGE));
        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow(), PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcPrefSize());

        int row = addVideoPlayer(gridPane, 0);
        makeResolution(gridPane, row);

        TitledPane tpConfig = new TitledPane("Programme", gridPane);
        result.add(tpConfig);
        return tpConfig;
    }

    private int addVideoPlayer(GridPane gridPane, int row) {
        TextField txtPlay = new TextField();
        txtPlay.textProperty().bindBidirectional(propProgram);
        txtPlay.textProperty().addListener((l, o, n) -> {
            File file = new File(txtPlay.getText());
            if (!file.exists() || !file.isFile()) {
                txtPlay.setStyle(ProgColorList.ERROR.getCssBackground());
            } else {
                txtPlay.setStyle("");
            }
        });

        TextField txtParameter = new TextField();
        txtParameter.textProperty().bindBidirectional(propParameter);

        final Button btnFile = new Button();
        btnFile.setOnAction(event -> {
            PDirFileChooser.FileChooserOpenFile(ProgData.getInstance().primaryStage, txtPlay);
        });
        btnFile.setGraphic(ProgIcons.Icons.ICON_BUTTON_FILE_OPEN.getImageView());
        btnFile.setTooltip(new Tooltip("Einen Videoplayer zum Abspielen der Filme auswählen"));

        final Button btnProgramReset = new Button();
        btnProgramReset.setGraphic(ProgIcons.Icons.ICON_BUTTON_RESET.getImageView());
        btnProgramReset.setTooltip(new Tooltip("Die Init-Parameter wieder herstellen"));
        btnProgramReset.setOnAction(event -> txtPlay.setText(GetProgramStandardPath.getTemplatePathVlc()));

        final Button btnParameterReset = new Button();
        btnParameterReset.setGraphic(ProgIcons.Icons.ICON_BUTTON_RESET.getImageView());
        btnParameterReset.setTooltip(new Tooltip("Die Init-Parameter wieder herstellen"));
        btnParameterReset.setOnAction(event -> txtParameter.setText(ProgConfig.SYSTEM_PROG_PLAY_PARAMETER_INIT));

        final Button btnHelpProgram = PButton.helpButton(stage, "Videoplayer", HelpText.VIDEOPLAYER);
        final Button btnHelpParameter = PButton.helpButton(stage, "Videoplayer", HelpText.PLAY_FILE_HELP_PARAMETER);

        gridPane.add(new Label("Videoplayer zum Abspielen der Filme"), 0, row, 2, 1);

        gridPane.add(new Label("Programm:"), 0, ++row);
        gridPane.add(txtPlay, 1, row);
        gridPane.add(btnProgramReset, 2, row);
        gridPane.add(btnFile, 3, row);
        gridPane.add(btnHelpProgram, 4, row);

        gridPane.add(new Label("Parameter:"), 0, ++row);
        gridPane.add(txtParameter, 1, row);
        gridPane.add(btnParameterReset, 2, row);
        gridPane.add(btnHelpParameter, 4, row);

        return row;
    }

    private void makeResolution(GridPane gridPane, int row) {
        // Auflösung
        switch (ProgConfig.FILM_RESOLUTION.getValueSafe()) {
            case FilmData.RESOLUTION_ASK:
                rbAsk.setSelected(true);
                break;
            case FilmData.RESOLUTION_HD:
                rbHd.setSelected(true);
                break;
            case FilmData.RESOLUTION_SMALL:
                rbLow.setSelected(true);
                break;
            default:
                rbHeight.setSelected(true);
                break;
        }

        ToggleGroup tg = new ToggleGroup();
        rbAsk.setToggleGroup(tg);
        rbHd.setToggleGroup(tg);
        rbHeight.setToggleGroup(tg);
        rbLow.setToggleGroup(tg);
        rbAsk.setOnAction(event -> setResolution());
        rbHd.setOnAction(event -> setResolution());
        rbHeight.setOnAction(event -> setResolution());
        rbLow.setOnAction(event -> setResolution());

        rbHd.setPadding(new Insets(0, 0, 0, 15));
        rbHeight.setPadding(new Insets(0, 0, 0, 15));
        rbLow.setPadding(new Insets(0, 0, 0, 15));

        final Button btnHelpRes = PButton.helpButton(stage, "Auflösung",
                HelpText.USE_RESOLUTION);

        gridPane.add(new Label(""), 0, ++row);
        gridPane.add(new Label("Auflösung des Films beim Abspielen"), 0, ++row, 2, 1);

        gridPane.add(rbAsk, 0, ++row, 2, 1);//Platz nach oben!
        gridPane.add(btnHelpRes, 4, row);
        gridPane.add(rbHd, 0, ++row, 2, 1);//Platz nach oben!
        gridPane.add(rbHeight, 0, ++row, 2, 1);
        gridPane.add(rbLow, 0, ++row, 2, 1);
        setResolution();
    }

    private void setResolution() {
        if (rbAsk.isSelected()) {
            ProgConfig.FILM_RESOLUTION.setValue(FilmData.RESOLUTION_ASK);
        } else if (rbHd.isSelected()) {
            ProgConfig.FILM_RESOLUTION.setValue(FilmData.RESOLUTION_HD);
        } else if (rbLow.isSelected()) {
            ProgConfig.FILM_RESOLUTION.setValue(FilmData.RESOLUTION_SMALL);
        } else {
            ProgConfig.FILM_RESOLUTION.setValue(FilmData.RESOLUTION_NORMAL);
        }
    }
}
