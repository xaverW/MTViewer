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

package de.p2tools.mtviewer.gui.configDialog.configPanes;

import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.gui.tools.HelpText;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Collection;

public class LoadFilmsPane {

    private final PToggleSwitch tglLoad = new PToggleSwitch("Filmliste beim Programmstart laden");
    private final ProgData progData;
    private final Stage stage;
    BooleanProperty propLoad = ProgConfig.SYSTEM_LOAD_FILMS_ON_START;

    public LoadFilmsPane(Stage stage) {
        this.stage = stage;
        progData = ProgData.getInstance();
    }

    public void close() {
        tglLoad.selectedProperty().unbindBidirectional(propLoad);
    }

    public TitledPane make() {
        return make(null);
    }

    public TitledPane make(Collection<TitledPane> result) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(5);
        gridPane.setPadding(new Insets(20));

        tglLoad.selectedProperty().bindBidirectional(propLoad);
        final Button btnHelpLoad = PButton.helpButton(stage, "Filmliste laden",
                HelpText.LOAD_FILMLIST_PROGRAMSTART);


        //Diacritic
        PToggleSwitch tglRemoveDiacritic = new PToggleSwitch("Diakritische Zeichen ändern");
        tglRemoveDiacritic.setMaxWidth(Double.MAX_VALUE);
        tglRemoveDiacritic.setSelected(!ProgConfig.SYSTEM_SHOW_DIACRITICS.getValue());
        tglRemoveDiacritic.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            ProgConfig.SYSTEM_SHOW_DIACRITICS.setValue(!tglRemoveDiacritic.isSelected());
        });
        final Button btnHelpDia = PButton.helpButton(stage, "Diakritische Zeichen",
                HelpText.DIAKRITISCHE_ZEICHEN);

        Separator sp2 = new Separator();
        sp2.getStyleClass().add("pseperator2");
        sp2.setMinHeight(0);

        int row = 0;
        gridPane.add(tglLoad, 0, ++row, 2, 1);
        gridPane.add(btnHelpLoad, 2, row);

        gridPane.add(new Label(" "), 0, ++row);
        gridPane.add(tglRemoveDiacritic, 0, ++row, 2, 1);
        gridPane.add(btnHelpDia, 2, row);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSizeAndHgrow(),
                PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcPrefSize());

        TitledPane tpConfig = new TitledPane("Filmliste laden", gridPane);
        result.add(tpConfig);
        return tpConfig;
    }
}