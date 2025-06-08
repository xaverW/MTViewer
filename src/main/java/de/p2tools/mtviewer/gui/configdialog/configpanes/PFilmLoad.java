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
import de.p2tools.mtviewer.gui.help.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PFilmLoad extends VBox {

    private final P2ToggleSwitch tglLoad = new P2ToggleSwitch("Filmliste/Audioliste beim Programmstart laden");
    private final P2ToggleSwitch tglRemoveDiacritic = new P2ToggleSwitch("Diakritische Zeichen ändern");
    private final BooleanProperty diacriticChanged;
    private final Stage stage;

    public PFilmLoad(Stage stage, BooleanProperty diacriticChanged) {
        this.stage = stage;
        this.diacriticChanged = diacriticChanged;
        make();
    }

    public void close() {
        tglLoad.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_LOAD_FILMS_ON_START);
        tglRemoveDiacritic.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_REMOVE_DIACRITICS);
    }

    private void make() {
        setSpacing(P2LibConst.SPACING_VBOX);
        setPadding(new Insets(P2LibConst.PADDING_VBOX));
        getChildren().addAll(P2GuiTools.getVDistance(5));

        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);

        tglLoad.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_LOAD_FILMS_ON_START);
        final Button btnHelpLoad = P2Button.helpButton(stage, "Filmliste/Audioliste laden",
                HelpText.LOAD_FILMLIST_PROGRAMSTART);

        //Diacritic
        tglRemoveDiacritic.setMaxWidth(Double.MAX_VALUE);
        tglRemoveDiacritic.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_REMOVE_DIACRITICS);
        tglRemoveDiacritic.selectedProperty().addListener((u, o, n) -> diacriticChanged.setValue(true));
        final Button btnHelpDia = P2Button.helpButton(stage, "Diakritische Zeichen",
                HelpText.DIAKRITISCHE_ZEICHEN);

        int row = 0;
        gridPane.add(tglLoad, 0, row);
        gridPane.add(btnHelpLoad, 1, row);

        ++row;
        gridPane.add(tglRemoveDiacritic, 0, ++row);
        gridPane.add(btnHelpDia, 1, row);
        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcComputedSizeAndHgrow(),
                P2ColumnConstraints.getCcPrefSize()
        );

        getChildren().addAll(gridPane);
    }
}