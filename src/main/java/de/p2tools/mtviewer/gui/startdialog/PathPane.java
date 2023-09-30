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

package de.p2tools.mtviewer.gui.startdialog;

import de.p2tools.mtviewer.controller.config.ProgColorList;
import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgConst;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.data.ProgIconsMTViewer;
import de.p2tools.mtviewer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.PDirFileChooser;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.guitools.P2Hyperlink;
import de.p2tools.p2lib.mtdownload.GetProgramStandardPath;
import de.p2tools.p2lib.tools.PSystemUtils;
import de.p2tools.p2lib.tools.ProgramToolsFactory;
import javafx.beans.property.StringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PathPane {
    private final Stage stage;
    private GridPane gridPane = new GridPane();
    private int row = 0;
    private List<UnBind> unbindList = new ArrayList<>();

    public PathPane(Stage stage) {
        this.stage = stage;
    }

    public void close() {
        unbindList.stream().forEach(unBind -> unBind.unbind());
    }

    public TitledPane makePath() {
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.DIST_EDGE));
        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcComputedSizeAndHgrow());

        gridPane.add(new Label(), 2, row);
        gridPane.add(new Label(), 2, row);

        addDownloadPath();
        addVlc();
        addFFmpeg();

        TitledPane tpConfig = new TitledPane("Programmpfade", gridPane);
        return tpConfig;
    }

    private void addDownloadPath() {
        Text text;
        TextField txtDownloadPath = new TextField();
        final Button btnFind = new Button("suchen");

        text = new Text("Pfad für Downloads auswählen");
        btnFind.setOnAction(event -> {
            ProgConfig.DOWNLOAD_FILE_PATH.setValue("");
            txtDownloadPath.setText(PSystemUtils.getStandardDownloadPath());
        });

        text.setStyle("-fx-font-weight: bold");
        txtDownloadPath.textProperty().bindBidirectional(ProgConfig.DOWNLOAD_FILE_PATH);
        unbindList.add(new UnBind(txtDownloadPath, ProgConfig.DOWNLOAD_FILE_PATH));

        if (ProgData.debug) {
            //dann einen anderen Downloadpfad
            ProgConfig.DOWNLOAD_FILE_PATH.setValue("/tmp/Download");
        }
        if (txtDownloadPath.getText().isEmpty()) {
            txtDownloadPath.setText(PSystemUtils.getStandardDownloadPath());
        }

        final Button btnFile = new Button();
        btnFile.setOnAction(event -> {
            PDirFileChooser.DirChooser(stage, txtDownloadPath);
        });
        btnFile.setGraphic(ProgIconsMTViewer.ICON_BUTTON_FILE_OPEN.getImageView());
        btnFile.setTooltip(new Tooltip("Speicherordner auswählen"));

        gridPane.add(text, 0, row);
        gridPane.add(txtDownloadPath, 0, ++row);
        gridPane.add(btnFile, 1, row);
        gridPane.add(btnFind, 2, row);
        gridPane.add(new Label(), 1, ++row);
        gridPane.add(new Label(), 1, ++row);
    }

    private void addVlc() {
        Text text;
        P2Hyperlink hyperlink;
        TextField txtPlayer = new TextField();
        final Button btnFind = new Button("suchen");

        text = new Text("Pfad zum VLC-Player auswählen");
        btnFind.setOnAction(event -> {
            ProgConfig.SYSTEM_PROG_PLAY.setValue("");
            txtPlayer.setText(GetProgramStandardPath.getTemplatePathVlc());
        });
        hyperlink = new P2Hyperlink(stage,
                ProgConst.ADRESSE_WEBSITE_VLC,
                ProgConfig.SYSTEM_PROG_OPEN_URL, ProgIconsMTViewer.ICON_BUTTON_FILE_OPEN.getImageView());

        text.setStyle("-fx-font-weight: bold");

        txtPlayer.textProperty().addListener((observable, oldValue, newValue) -> {
            File file = new File(txtPlayer.getText());
            if (!file.exists() || !file.isFile()) {
                txtPlayer.setStyle(ProgColorList.ERROR.getCssBackground());
            } else {
                txtPlayer.setStyle("");
            }
        });
        txtPlayer.textProperty().bindBidirectional(ProgConfig.SYSTEM_PROG_PLAY);
        unbindList.add(new UnBind(txtPlayer, ProgConfig.SYSTEM_PROG_PLAY));

        final Button btnFile = new Button();
        btnFile.setOnAction(event -> {
            PDirFileChooser.FileChooserOpenFile(stage, txtPlayer);
        });
        btnFile.setGraphic(ProgIconsMTViewer.ICON_BUTTON_FILE_OPEN.getImageView());
        btnFile.setTooltip(new Tooltip("Programmdatei auswählen"));

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().addAll(new Label("Website"), hyperlink);

        final Button btnHelp = P2Button.helpButton(stage,
                "Videoplayer", HelpText.PROG_PATH_VLC);
        GridPane.setHalignment(btnHelp, HPos.RIGHT);

        gridPane.add(text, 0, row);
        gridPane.add(txtPlayer, 0, ++row);
        gridPane.add(btnFile, 1, row);
        gridPane.add(btnFind, 2, row);
        gridPane.add(hBox, 0, ++row, 2, 1);
        gridPane.add(btnHelp, 2, row);
        gridPane.add(new Label(), 1, ++row);
        gridPane.add(new Label(), 1, ++row);
    }

    private void addFFmpeg() {
        ProgramToolsFactory.OperatingSystemType op = ProgramToolsFactory.getOs();
        if (op.equals(ProgramToolsFactory.OperatingSystemType.WIN32) ||
                op.equals(ProgramToolsFactory.OperatingSystemType.WIN64)) {
            //da wirds mitgeliefert und passt
            return;
        }

        Text text;
        P2Hyperlink hyperlink;
        TextField txtSave = new TextField();
        final Button btnFind = new Button("suchen");

        text = new Text("Pfad zu ffmpeg auswählen");
        btnFind.setOnAction(event -> {
            ProgConfig.SYSTEM_PROG_SAVE.setValue("");
            txtSave.setText(GetProgramStandardPath.getTemplatePathFFmpeg());
        });
        hyperlink = new P2Hyperlink(stage,
                ProgConst.ADRESSE_WEBSITE_ffmpeg,
                ProgConfig.SYSTEM_PROG_OPEN_URL, ProgIconsMTViewer.ICON_BUTTON_FILE_OPEN.getImageView());

        text.setStyle("-fx-font-weight: bold");

        txtSave.textProperty().addListener((observable, oldValue, newValue) -> {
            File file = new File(txtSave.getText());
            if (!file.exists() || !file.isFile()) {
                txtSave.setStyle(ProgColorList.ERROR.getCssBackground());
            } else {
                txtSave.setStyle("");
            }
        });
        txtSave.textProperty().bindBidirectional(ProgConfig.SYSTEM_PROG_SAVE);
        unbindList.add(new UnBind(txtSave, ProgConfig.SYSTEM_PROG_SAVE));

        final Button btnFile = new Button();
        btnFile.setOnAction(event -> {
            PDirFileChooser.FileChooserOpenFile(stage, txtSave);
        });
        btnFile.setGraphic(ProgIconsMTViewer.ICON_BUTTON_FILE_OPEN.getImageView());
        btnFile.setTooltip(new Tooltip("Programmdatei auswählen"));

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().addAll(new Label("Website"), hyperlink);

        final Button btnHelp = P2Button.helpButton(stage,
                "Download Hilfsprogramm", HelpText.PROG_PATH_FFMPEG);
        GridPane.setHalignment(btnHelp, HPos.RIGHT);

        gridPane.add(text, 0, row);
        gridPane.add(txtSave, 0, ++row);
        gridPane.add(btnFile, 1, row);
        gridPane.add(btnFind, 2, row);
        gridPane.add(hBox, 0, ++row, 2, 1);
        gridPane.add(btnHelp, 2, row);
    }

    private class UnBind {
        private TextField txt;
        private StringProperty property;

        UnBind(TextField txt, StringProperty property) {
            this.txt = txt;
            this.property = property;
        }

        void unbind() {
            txt.textProperty().unbindBidirectional(property);
        }
    }
}
