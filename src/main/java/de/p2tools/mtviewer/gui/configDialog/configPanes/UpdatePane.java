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
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.data.ProgIcons;
import de.p2tools.mtviewer.controller.update.SearchProgramUpdate;
import de.p2tools.mtviewer.gui.tools.HelpText;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.PHyperlink;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.Collection;

public class UpdatePane {

    private final ProgData progData;

    private final PToggleSwitch tglSearch = new PToggleSwitch("einmal am Tag nach einer neuen Programmversion suchen");
    private final PToggleSwitch tglSearchBeta = new PToggleSwitch("auch nach neuen Vorabversionen suchen");
    private final CheckBox chkDaily = new CheckBox("Zwischenschritte (Dailys) mit einbeziehen");
    private final Button btnNow = new Button("_Jetzt suchen");
    private final Stage stage;
    BooleanProperty propUpdateSearch = ProgConfig.SYSTEM_UPDATE_SEARCH_ACT;
    BooleanProperty propUpdateBetaSearch = ProgConfig.SYSTEM_UPDATE_SEARCH_BETA;
    BooleanProperty propUpdateDailySearch = ProgConfig.SYSTEM_UPDATE_SEARCH_DAILY;
    private Button btnHelpBeta;

    public UpdatePane(Stage stage) {
        this.stage = stage;
        progData = ProgData.getInstance();
    }

    public void close() {
        tglSearch.selectedProperty().unbindBidirectional(propUpdateSearch);
        tglSearchBeta.selectedProperty().unbindBidirectional(propUpdateBetaSearch);
    }

    public TitledPane make(Collection<TitledPane> result) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20));

        //einmal am Tag Update suchen
        tglSearch.selectedProperty().bindBidirectional(propUpdateSearch);
        final Button btnHelp = PButton.helpButton(stage, "Programmupdate suchen", HelpText.CONFIG_SEARCH_UPDATE);

        tglSearchBeta.selectedProperty().bindBidirectional(propUpdateBetaSearch);
        chkDaily.selectedProperty().bindBidirectional(propUpdateDailySearch);
        btnHelpBeta = PButton.helpButton(stage, "Vorabversionen suchen", HelpText.CONFIG_SEARCH_UPDATE_DAILY);

        //jetzt suchen
        checkBeta();
        tglSearch.selectedProperty().addListener((ob, ol, ne) -> checkBeta());
        tglSearchBeta.selectedProperty().addListener((ob, ol, ne) -> checkBeta());

        btnNow.setOnAction(event -> new SearchProgramUpdate(progData, stage).searchNewProgramVersion(true));
        PHyperlink hyperlink = new PHyperlink(ProgConst.URL_WEBSITE_MTVIEWER,
                ProgConfig.SYSTEM_PROG_OPEN_URL, ProgIcons.Icons.ICON_BUTTON_FILE_OPEN.getImageView());
        HBox hBoxHyper = new HBox();
        hBoxHyper.setAlignment(Pos.CENTER_LEFT);
        hBoxHyper.setPadding(new Insets(10, 0, 0, 0));
        hBoxHyper.setSpacing(10);
        hBoxHyper.getChildren().addAll(new Label("Infos auch auf der Website:"), hyperlink);

        int row = 0;
        gridPane.add(tglSearch, 0, row);
        gridPane.add(btnHelp, 1, row);

        gridPane.add(tglSearchBeta, 0, ++row);
        gridPane.add(btnHelpBeta, 1, row);
        gridPane.add(chkDaily, 0, ++row, 2, 1);
        GridPane.setHalignment(chkDaily, HPos.RIGHT);

        gridPane.add(btnNow, 0, ++row);

        gridPane.add(new Label(" "), 0, ++row);
        gridPane.add(hBoxHyper, 0, ++row);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSizeAndHgrow(),
                PColumnConstraints.getCcPrefSize());
        gridPane.getRowConstraints().addAll(PColumnConstraints.getRcPrefSize(), PColumnConstraints.getRcPrefSize(),
                PColumnConstraints.getRcPrefSize(), PColumnConstraints.getRcVgrow(), PColumnConstraints.getRcPrefSize());

        TitledPane tpConfig = new TitledPane("Programmupdate", gridPane);
        result.add(tpConfig);
        return tpConfig;
    }

    private void checkBeta() {
        tglSearchBeta.setDisable(!tglSearch.isSelected());
        btnHelpBeta.setDisable(!tglSearch.isSelected());

        if (!tglSearchBeta.isSelected()) {
            chkDaily.setSelected(false);
        }
        chkDaily.setDisable(!tglSearchBeta.isSelected() || tglSearchBeta.isDisabled());
    }
}
