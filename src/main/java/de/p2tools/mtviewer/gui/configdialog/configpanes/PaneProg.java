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

import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.data.ProgIconsMTViewer;
import de.p2tools.mtviewer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.PDirFileChooser;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Collection;

public class PaneProg {

    private final ProgData progData;

    private final Stage stage;
    StringProperty propUrl = ProgConfig.SYSTEM_PROG_OPEN_URL;
    private TextField txtFileManagerWeb;

    public PaneProg(Stage stage) {
        this.stage = stage;
        progData = ProgData.getInstance();
    }

    public void close() {
        txtFileManagerWeb.textProperty().unbindBidirectional(propUrl);
    }

    public TitledPane make(Collection<TitledPane> result) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.DIST_EDGE));

        addWebbrowser(gridPane);
        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcComputedSizeAndHgrow());

        TitledPane tpConfig = new TitledPane("Programme", gridPane);
        result.add(tpConfig);
        return tpConfig;
    }

    private void addWebbrowser(GridPane gridPane) {
        txtFileManagerWeb = new TextField();
        txtFileManagerWeb.textProperty().bindBidirectional(propUrl);

        final Button btnFile = new Button();
        btnFile.setOnAction(event -> {
            PDirFileChooser.FileChooserOpenFile(ProgData.getInstance().primaryStage, txtFileManagerWeb);
        });
        btnFile.setGraphic(ProgIconsMTViewer.ICON_BUTTON_FILE_OPEN.getImageView());
        btnFile.setTooltip(new Tooltip("Einen Webbrowser zum Öffnen von URLs auswählen"));

        final Button btnHelp = P2Button.helpButton(stage, "Webbrowser", HelpText.WEBBROWSER);

        VBox vBox = new VBox(2);
        vBox.setPadding(new Insets(0));
        HBox hBox = new HBox(P2LibConst.DIST_BUTTON);
        hBox.getChildren().addAll(txtFileManagerWeb, btnFile, btnHelp);
        HBox.setHgrow(txtFileManagerWeb, Priority.ALWAYS);
        vBox.getChildren().addAll(new Label("Webbrowser zum Öffnen von URLs"), hBox);
        gridPane.add(vBox, 0, 0);
    }
}
