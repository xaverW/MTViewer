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

package de.p2tools.mtviewer.gui.configdialog.configpanes;

import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.picon.PIconFactory;
import de.p2tools.mtviewer.gui.PTimePicker;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.P2Text;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Collection;

public class PaneChangeColorTime {
    private final Stage stage;

    private final CheckBox chkSwitch = new CheckBox("Dark-Theme automatisch wechseln");
    private final PTimePicker timePickerDark;

    private Button btnHelp;
    private final VBox vBox = new VBox(P2LibConst.SPACING_VBOX);

    public PaneChangeColorTime(Stage stage) {
        this.stage = stage;
        timePickerDark = new PTimePicker(ProgConfig.SYSTEM_CHANGE_TO_DARK_THEME_HOUR.get(),
                ProgConfig.SYSTEM_CHANGE_TO_DARK_THEME_MINUTE.get());
    }

    public void close() {
        ProgConfig.SYSTEM_CHANGE_TO_DARK_THEME_HOUR.unbind();
        ProgConfig.SYSTEM_CHANGE_TO_DARK_THEME_MINUTE.unbind();
    }

    public void make(Collection<TitledPane> result) {
        btnHelp = PIconFactory.getHelpButton(stage, "Theme-Wechsel",
                "Das Programm startet dann immer mit dem Light-Theme. Hier kann " +
                        "dann eine Uhrzeit vorgeben, wann auf das Dark-Theme " +
                        "gewechselt werden soll.");
        chkSwitch.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_CHANGE_THEME_TIME);
        ProgConfig.SYSTEM_CHANGE_TO_DARK_THEME_HOUR.bind(timePickerDark.hourPropProperty());
        ProgConfig.SYSTEM_CHANGE_TO_DARK_THEME_MINUTE.bind(timePickerDark.minutePropProperty());

        timePickerDark.hourPropProperty().addListener((u, o, n) -> resetDone());
        timePickerDark.minutePropProperty().addListener((u, o, n) -> resetDone());
        chkSwitch.setOnAction(a -> resetDone());
        
        makeGrid();
        TitledPane tpColor = new TitledPane("Wechsel zum Dark-Theme nach Uhrzeit", vBox);
        result.add(tpColor);
    }

    private void resetDone() {
        // damit der Wechsel wieder gemacht werden kann
        ProgData.themeChangeDark = false;
    }

    private void makeGrid() {
        GridPane gridPane = new GridPane();
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);

        int row = 0;
        gridPane.add(new Label(""), 0, ++row);

        gridPane.add(P2Text.getLblTextBold("Uhrzeit des Wechsel"), 0, ++row);
        gridPane.add(timePickerDark, 0, ++row);

        gridPane.disableProperty().bind(chkSwitch.selectedProperty().not());
        HBox hBox = new HBox(P2LibConst.SPACING_HBOX);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().add(btnHelp);
        VBox.setVgrow(hBox, Priority.ALWAYS);

        vBox.getChildren().addAll(chkSwitch, gridPane, P2GuiTools.getVBoxGrower(), hBox);
    }
}
