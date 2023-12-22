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
import de.p2tools.mtviewer.controller.config.ProgConst;
import de.p2tools.mtviewer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.util.Collection;

public class PaneEditFilter {

    private final Stage stage;

    public PaneEditFilter(Stage stage) {
        this.stage = stage;
    }

    public void close() {
    }

    public TitledPane make(Collection<TitledPane> result) {
        TitledPane tpConfig = new TitledPane("Einstellungen des Filmfilters", init());
        result.add(tpConfig);
        return tpConfig;
    }

    private GridPane init() {
        //Wartezeit
        CheckBox cbkReturn = new CheckBox("Suchbeginn erst mit \"Return\" starten");
        cbkReturn.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_FILTER_RETURN);

        Label lblValue = new Label();
        lblValue.setMinWidth(Region.USE_COMPUTED_SIZE);

        Slider slider = new Slider();
        slider.setMinWidth(400);
        slider.setMaxWidth(400);
        slider.setMin(0);
        slider.setMax(ProgConst.SYSTEM_FILTER_MAX_WAIT_TIME);
        slider.setMinorTickCount(4);//dann 5 Teile, 500/5=alle 100 kann eingeloggt werden :)
        slider.setBlockIncrement(200);//Bedienung über die Tastatur
        slider.setMajorTickUnit(500);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setSnapToTicks(true);
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            ProgConfig.SYSTEM_FILTER_WAIT_TIME.setValue(Double.valueOf(slider.getValue()).intValue());
            setLabel(lblValue);
        });
        slider.setValue(ProgConfig.SYSTEM_FILTER_WAIT_TIME.getValue());
        setLabel(lblValue);

        final Button btnHelp = P2Button.helpButton(stage, "Filtereinstellungen",
                HelpText.GUI_FILMS_EDIT_FILTER);


        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.PADDING));

        int row = 0;
        HBox hBox = new HBox(20);
        hBox.getChildren().addAll(new Label("Suchbeginn nach Eingabe verzögern  um: "), lblValue);
        gridPane.add(hBox, 0, row);
        gridPane.add(slider, 0, ++row);

        gridPane.add(new Label(), 0, ++row);
        gridPane.add(new Label("In Textfeldern:"), 0, ++row);
        gridPane.add(cbkReturn, 0, ++row);

        gridPane.add(new Label(), 0, ++row);
        gridPane.add(btnHelp, 2, ++row);
        GridPane.setHalignment(btnHelp, HPos.RIGHT);

        gridPane.getColumnConstraints().addAll(
                P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcComputedSizeAndHgrow());
        return gridPane;
    }

    private int setLabel(Label lblValue) {
        int intValue = ProgConfig.SYSTEM_FILTER_WAIT_TIME.getValue();
        lblValue.setText("  " + intValue + " ms");
        return intValue;
    }
}
