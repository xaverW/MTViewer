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

import de.p2tools.mtviewer.controller.load.LoadFilmFactory;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2GuiTools;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.Collection;

public class PaneFilmLoad {

    private final PFilmLoad pFilmLoad;

    public PaneFilmLoad(Stage stage, BooleanProperty diacriticChanged) {
        this.pFilmLoad = new PFilmLoad(stage, diacriticChanged);
    }

    public void close() {
        pFilmLoad.close();
    }

    public TitledPane make(Collection<TitledPane> result) {
        Button btnLoad = new Button("_Filmliste mit diesen Einstellungen neu laden");
        btnLoad.setTooltip(new Tooltip("Eine komplette neue Filmliste laden.\n" +
                "Geänderte Einstellungen für das Laden der Filmliste werden so sofort übernommen"));
        btnLoad.setOnAction(event -> LoadFilmFactory.loadList(true));
        HBox hBox = new HBox(P2LibConst.SPACING_HBOX);
        hBox.getChildren().add(btnLoad);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        pFilmLoad.getChildren().addAll(P2GuiTools.getVBoxGrower(), hBox);

        TitledPane tpConfig = new TitledPane("Filmliste laden", pFilmLoad);
        result.add(tpConfig);
        return tpConfig;
    }
}