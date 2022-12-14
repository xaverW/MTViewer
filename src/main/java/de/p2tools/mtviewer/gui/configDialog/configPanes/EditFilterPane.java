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
import de.p2tools.mtviewer.controller.config.ProgConst;
import de.p2tools.mtviewer.gui.tools.HelpText;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import javafx.beans.property.IntegerProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.util.Collection;

public class EditFilterPane {

    private final Stage stage;
    IntegerProperty waitTime = ProgConfig.SYSTEM_FILTER_WAIT_TIME;

    public EditFilterPane(Stage stage) {
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
        slider.setBlockIncrement(200);//Bedienung ??ber die Tastatur
        slider.setMajorTickUnit(500);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setSnapToTicks(true);
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            waitTime.setValue(Double.valueOf(slider.getValue()).intValue());
            setLabel(lblValue);
        });
        slider.setValue(waitTime.getValue());
        setLabel(lblValue);

        final Button btnHelp = PButton.helpButton(stage, "Filtereinstellungen",
                HelpText.GUI_FILMS_EDIT_FILTER);


        final GridPane gridPane = new GridPane();
//        gridPane.setGridLinesVisible(true);
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20));

        int row = 0;
        HBox hBox = new HBox(20);
        hBox.getChildren().addAll(new Label("Suchbeginn nach Eingabe verz??gern  um: "), lblValue);
        gridPane.add(hBox, 0, row);

//        gridPane.add(new Label("Suchbeginn nach Eingabe verz??gern  um  "), 0, row);
//        gridPane.add(lblValue, 1, row);
        gridPane.add(slider, 0, ++row);
//        GridPane.setHgrow(slider, Priority.ALWAYS);

        gridPane.add(new Label(), 0, ++row);

        gridPane.add(new Label("In Textfeldern:"), 0, ++row);
        gridPane.add(cbkReturn, 0, ++row);

        gridPane.add(new Label(), 0, ++row);

        gridPane.add(btnHelp, 2, ++row);
        GridPane.setHalignment(btnHelp, HPos.RIGHT);

        gridPane.getColumnConstraints().addAll(
                PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow());


        return gridPane;
    }

    private int setLabel(Label lblValue) {
        int intValue = waitTime.getValue();
        lblValue.setText("  " + intValue + " ms");
        return intValue;
    }
}
