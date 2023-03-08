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
import de.p2tools.mtviewer.controller.film.LoadFilmFactory;
import de.p2tools.mtviewer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.PButton;
import de.p2tools.p2lib.guitools.PColumnConstraints;
import de.p2tools.p2lib.guitools.ptoggleswitch.PToggleSwitch;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Collection;

public class PaneFilmLoad {

    private final PToggleSwitch tglLoad = new PToggleSwitch("Filmliste beim Programmstart laden");
    private final PToggleSwitch tglRemoveDiacritic = new PToggleSwitch("Diakritische Zeichen ändern");
    private final BooleanProperty diacriticChanged;
    private final Stage stage;

    public PaneFilmLoad(Stage stage, BooleanProperty diacriticChanged) {
        this.stage = stage;
        this.diacriticChanged = diacriticChanged;
    }

    public void close() {
        tglLoad.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_LOAD_FILMS_ON_START);
        tglRemoveDiacritic.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_REMOVE_DIACRITICS);
    }

    public TitledPane make(Collection<TitledPane> result) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.DIST_EDGE));

        tglLoad.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_LOAD_FILMS_ON_START);
        final Button btnHelpLoad = PButton.helpButton(stage, "Filmliste laden",
                HelpText.LOAD_FILMLIST_PROGRAMSTART);

        //Diacritic
        tglRemoveDiacritic.setMaxWidth(Double.MAX_VALUE);
        tglRemoveDiacritic.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_REMOVE_DIACRITICS);
        tglRemoveDiacritic.selectedProperty().addListener((u, o, n) -> diacriticChanged.setValue(true));
        final Button btnHelpDia = PButton.helpButton(stage, "Diakritische Zeichen",
                HelpText.DIAKRITISCHE_ZEICHEN);

        Button btnLoad = new Button("_Filmliste mit dieser Einstellung neu laden");
        btnLoad.setTooltip(new Tooltip("Eine komplette neue Filmliste laden.\n" +
                "Geänderte Einstellungen für das Laden der Filmliste werden so sofort übernommen"));
        btnLoad.setOnAction(event -> LoadFilmFactory.getInstance().loadList(true));

        int row = 0;
        gridPane.add(tglLoad, 0, row);
        gridPane.add(btnHelpLoad, 1, row);

        ++row;
        gridPane.add(tglRemoveDiacritic, 0, ++row);
        gridPane.add(btnHelpDia, 1, row);

        gridPane.add(new Label(" "), 0, ++row);
        gridPane.add(btnLoad, 0, ++row, 2, 1);
        GridPane.setHalignment(btnLoad, HPos.RIGHT);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSizeAndHgrow(),
                PColumnConstraints.getCcPrefSize()
        );

        TitledPane tpConfig = new TitledPane("Filmliste laden", gridPane);
        result.add(tpConfig);
        return tpConfig;
    }
}