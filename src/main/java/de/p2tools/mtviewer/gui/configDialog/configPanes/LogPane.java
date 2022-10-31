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

package de.p2tools.mtviewer.gui.configDialog.configPanes;

import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.config.ProgInfos;
import de.p2tools.mtviewer.controller.data.ProgIcons;
import de.p2tools.mtviewer.gui.tools.HelpText;
import de.p2tools.p2Lib.dialogs.PDirFileChooser;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import de.p2tools.p2Lib.tools.log.PLogger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Collection;

public class LogPane {

    private final ProgData progData;

    private final PToggleSwitch tglEnableLog = new PToggleSwitch("Ein Logfile anlegen:");
    private final Stage stage;
    BooleanProperty logfileChanged = new SimpleBooleanProperty(false);
    BooleanProperty propLog = ProgConfig.SYSTEM_LOG_ON;
    StringProperty propLogDir = ProgConfig.SYSTEM_LOG_DIR;
    private TextField txtLogFile;

    public LogPane(Stage stage) {
        this.stage = stage;
        progData = ProgData.getInstance();
    }

    public void close() {
        tglEnableLog.selectedProperty().unbindBidirectional(propLog);
        txtLogFile.textProperty().unbindBidirectional(propLogDir);
    }

    public TitledPane make(Collection<TitledPane> result) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20));

        tglEnableLog.selectedProperty().bindBidirectional(propLog);
        tglEnableLog.selectedProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            if (newValue) {
                PLogger.setFileHandler(ProgInfos.getLogDirectory_String());
            } else {
                PLogger.removeFileHandler();
            }
        }));

        final Button btnHelp = PButton.helpButton(stage, "Logfile", HelpText.LOGFILE);

        txtLogFile = new TextField();
        txtLogFile.textProperty().bindBidirectional(propLogDir);
        if (txtLogFile.getText().isEmpty()) {
            txtLogFile.setText(ProgInfos.getLogDirectory_String());
        }

        final Button btnFile = new Button();
        btnFile.setTooltip(new Tooltip("Einen Ordner für das Logfile auswählen"));
        btnFile.setOnAction(event -> {
            PDirFileChooser.DirChooser(ProgData.getInstance().primaryStage, txtLogFile);
        });
        btnFile.setGraphic(ProgIcons.Icons.ICON_BUTTON_FILE_OPEN.getImageView());

        final Button btnReset = new Button();
        btnReset.setGraphic(ProgIcons.Icons.ICON_BUTTON_RESET.getImageView());
        btnReset.setTooltip(new Tooltip("Standardpfad für das Logfile wieder herstellen"));
        btnReset.setOnAction(event -> {
            txtLogFile.setText(ProgInfos.getStandardLogDirectory_String());
        });

        final Button btnChange = new Button("_Pfad zum Logfile jetzt schon ändern");
        btnChange.setTooltip(new Tooltip("Den geänderten Pfad für das Logfile\n" +
                "jetzt schon verwenden, \n\n" +
                "ansonsten wird er erst beim nächsten\n" +
                "Programmstart verwendet"));
        btnChange.setOnAction(event -> {
            PLogger.setFileHandler(ProgInfos.getLogDirectory_String());
            logfileChanged.setValue(false);
        });

        int row = 0;
        gridPane.add(tglEnableLog, 0, row, 3, 1);
        gridPane.add(btnHelp, 3, row);

        gridPane.add(new Label(""), 0, ++row);

        gridPane.add(new Label("Ordner:"), 0, ++row);
        gridPane.add(txtLogFile, 1, row);
        gridPane.add(btnFile, 2, row);
        gridPane.add(btnReset, 3, row);

        gridPane.add(btnChange, 0, ++row, 2, 1);
        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow(),
                PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcPrefSize());

        txtLogFile.disableProperty().bind(tglEnableLog.selectedProperty().not());
        btnFile.disableProperty().bind(tglEnableLog.selectedProperty().not());
        btnReset.disableProperty().bind(tglEnableLog.selectedProperty().not());
        btnChange.disableProperty().bind(tglEnableLog.selectedProperty().not().or(logfileChanged.not()));

        txtLogFile.textProperty().addListener((observable, oldValue, newValue) -> logfileChanged.setValue(true));

        TitledPane tpConfig = new TitledPane("Logfile", gridPane);
        result.add(tpConfig);
        return tpConfig;
    }
}
