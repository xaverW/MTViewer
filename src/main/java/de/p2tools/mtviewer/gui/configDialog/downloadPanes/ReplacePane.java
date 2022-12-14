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

package de.p2tools.mtviewer.gui.configDialog.downloadPanes;

import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgConst;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.data.ProgIcons;
import de.p2tools.mtviewer.controller.data.ReplaceData;
import de.p2tools.mtviewer.gui.tools.HelpText;
import de.p2tools.p2Lib.alert.PAlert;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Collection;

public class ReplacePane {

    private final VBox vBox = new VBox();
    private final TextField txtFrom = new TextField();
    private final TextField txtTo = new TextField();
    private final GridPane gridPane = new GridPane();
    private final PToggleSwitch tglAscii = new PToggleSwitch("nur ASCII-Zeichen erlauben");
    private final PToggleSwitch tglReplace = new PToggleSwitch("Ersetzungstabelle");
    private final Stage stage;
    TableView<ReplaceData> tableView = new TableView<>();
    BooleanProperty propAscii = ProgConfig.SYSTEM_ONLY_ASCII;
    BooleanProperty propReplace = ProgConfig.SYSTEM_USE_REPLACETABLE;
    private ReplaceData replaceData = null;


    public ReplacePane(Stage stage) {
        this.stage = stage;
        make();
    }

    public void makePane(Collection<TitledPane> result) {
        TitledPane tpReplace = new TitledPane("Ersetzungstabelle", vBox);
        result.add(tpReplace);
        tpReplace.setMaxHeight(Double.MAX_VALUE);
    }

    public void close() {
        unbindText();
        tglAscii.selectedProperty().unbindBidirectional(propAscii);
        tglReplace.selectedProperty().unbindBidirectional(propReplace);
    }

    private void make() {
        vBox.setFillWidth(true);
        vBox.setSpacing(10);

        makeAscii(vBox);
        initTable(vBox);
        addConfigs(vBox);
    }

    private void makeAscii(VBox vBox) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20));
        vBox.getChildren().add(gridPane);

        tglAscii.selectedProperty().bindBidirectional(propAscii);
        final Button btnHelpAscii = PButton.helpButton(stage, "Nur ASCII-Zeichen",
                HelpText.DOWNLOAD_ONLY_ASCII);

        tglReplace.selectedProperty().bindBidirectional(propReplace);
        final Button btnHelpReplace = PButton.helpButton(stage, "Ersetzungstabelle",
                HelpText.DOWNLOAD_REPLACELIST);

        gridPane.add(tglAscii, 0, 0);
        gridPane.add(btnHelpAscii, 1, 0);

        gridPane.add(tglReplace, 0, 1);
        gridPane.add(btnHelpReplace, 1, 1);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSizeAndHgrow(),
                PColumnConstraints.getCcPrefSize());
    }

    private void initTable(VBox vBox) {
        final TableColumn<ReplaceData, String> fromColumn = new TableColumn<>("Von");
        fromColumn.setEditable(true);
        fromColumn.setCellValueFactory(new PropertyValueFactory<>("from"));

        final TableColumn<ReplaceData, String> toColumn = new TableColumn<>("Nach");
        toColumn.setCellValueFactory(new PropertyValueFactory<>("to"));

        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tableView.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        tableView.getColumns().addAll(fromColumn, toColumn);
        tableView.setItems(ProgData.getInstance().replaceList);
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                Platform.runLater(this::setActReplaceData));

        VBox.setVgrow(tableView, Priority.ALWAYS);
        vBox.getChildren().addAll(tableView);

        Button btnDel = new Button("");
        btnDel.setTooltip(new Tooltip("Eintrag l??schen"));
        btnDel.setGraphic(ProgIcons.Icons.ICON_BUTTON_REMOVE.getImageView());
        btnDel.setOnAction(event -> {
            final ObservableList<ReplaceData> sels = tableView.getSelectionModel().getSelectedItems();

            if (sels == null || sels.isEmpty()) {
                PAlert.showInfoNoSelection();
            } else {
                ProgData.getInstance().replaceList.removeAll(sels);
                tableView.getSelectionModel().clearSelection();
            }
        });

        Button btnNew = new Button("");
        btnNew.setTooltip(new Tooltip("Einen neuen Eintrag erstellen"));
        btnNew.setGraphic(ProgIcons.Icons.ICON_BUTTON_ADD.getImageView());
        btnNew.setOnAction(event -> {
            ReplaceData replaceData = new ReplaceData();
            ProgData.getInstance().replaceList.add(replaceData);

            tableView.getSelectionModel().clearSelection();
            tableView.getSelectionModel().select(replaceData);
            tableView.scrollTo(replaceData);
        });

        Button btnUp = new Button("");
        btnUp.setTooltip(new Tooltip("Eintrag nach oben schieben"));
        btnUp.setGraphic(ProgIcons.Icons.ICON_BUTTON_MOVE_UP.getImageView());
        btnUp.setOnAction(event -> {
            final int sel = tableView.getSelectionModel().getSelectedIndex();
            if (sel < 0) {
                PAlert.showInfoNoSelection();
            } else {
                int res = ProgData.getInstance().replaceList.up(sel, true);
                tableView.getSelectionModel().select(res);
            }
        });

        Button btnDown = new Button("");
        btnDown.setTooltip(new Tooltip("Eintrag nach unten schieben"));
        btnDown.setGraphic(ProgIcons.Icons.ICON_BUTTON_MOVE_DOWN.getImageView());
        btnDown.setOnAction(event -> {
            final int sel = tableView.getSelectionModel().getSelectedIndex();
            if (sel < 0) {
                PAlert.showInfoNoSelection();
            } else {
                int res = ProgData.getInstance().replaceList.up(sel, false);
                tableView.getSelectionModel().select(res);
            }
        });

        Button btnTop = new Button();
        btnTop.setTooltip(new Tooltip("Eintrag an den Anfang verschieben"));
        btnTop.setGraphic(ProgIcons.Icons.ICON_BUTTON_MOVE_TOP.getImageView());
        btnTop.setOnAction(event -> {
            final int sel = tableView.getSelectionModel().getSelectedIndex();
            if (sel < 0) {
                PAlert.showInfoNoSelection();
            } else {
                int res = ProgData.getInstance().replaceList.top(sel, true);
                tableView.getSelectionModel().select(res);
            }
        });

        Button btnBottom = new Button();
        btnBottom.setTooltip(new Tooltip("Eintrag an das Ende verschieben"));
        btnBottom.setGraphic(ProgIcons.Icons.ICON_BUTTON_MOVE_BOTTOM.getImageView());
        btnBottom.setOnAction(event -> {
            final int sel = tableView.getSelectionModel().getSelectedIndex();
            if (sel < 0) {
                PAlert.showInfoNoSelection();
            } else {
                int res = ProgData.getInstance().replaceList.top(sel, false);
                tableView.getSelectionModel().select(res);
            }
        });

        Button btnReset = new Button("_Tabelle zur??cksetzen");
        btnReset.setTooltip(new Tooltip("Alle Eintr??ge l??schen und Standardeintr??ge wieder herstellen"));
        btnReset.setOnAction(event -> {
            ProgData.getInstance().replaceList.init();
        });

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.getChildren().addAll(btnNew, btnDel, btnTop, btnUp, btnDown, btnBottom, btnReset);
        vBox.getChildren().addAll(hBox);

    }

    private void addConfigs(VBox vBox) {
        gridPane.getStyleClass().add("extra-pane");
        gridPane.setHgap(15);
        gridPane.setVgap(5);
        gridPane.setPadding(new Insets(20));

        gridPane.add(new Label("Von: "), 0, 0);
        gridPane.add(txtFrom, 1, 0);
        gridPane.add(new Label("Nach: "), 0, 1);
        gridPane.add(txtTo, 1, 1);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(), PColumnConstraints.getCcComputedSizeAndHgrow());
        vBox.getChildren().add(gridPane);
        gridPane.setDisable(true);
    }

    private void setActReplaceData() {
        ReplaceData replaceDataAct = tableView.getSelectionModel().getSelectedItem();
        if (replaceDataAct == replaceData) {
            return;
        }

        unbindText();

        replaceData = replaceDataAct;
        gridPane.setDisable(replaceData == null);
        if (replaceData != null) {
            txtFrom.textProperty().bindBidirectional(replaceData.fromProperty());
            txtTo.textProperty().bindBidirectional(replaceData.toProperty());
        }
    }

    private void unbindText() {
        if (replaceData != null) {
            txtFrom.textProperty().unbindBidirectional(replaceData.fromProperty());
            txtTo.textProperty().unbindBidirectional(replaceData.toProperty());
        }
    }
}
