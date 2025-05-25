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

package de.p2tools.mtviewer.gui.configdialog.downloadpanes;

import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.downloadtools.DownloadState;
import de.p2tools.mtviewer.gui.help.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Collection;

public class PaneDownload {

    final GridPane gridPane = new GridPane();
    private final P2ToggleSwitch tglFinished = new P2ToggleSwitch("Benachrichtigung wenn abgeschlossen");
    private final ToggleGroup group = new ToggleGroup();
    private final RadioButton rbAsk = new RadioButton("Vorher fragen");
    private final RadioButton rbContinue = new RadioButton("Immer weiterführen");
    private final RadioButton rbRestart = new RadioButton("Immer neu starten");

    private final P2ToggleSwitch tglSSL = new P2ToggleSwitch("SSL-Download-URLs: Bei Problemen SSL abschalten");
    private final ProgData progData;
    private final Stage stage;

    public PaneDownload(Stage stage) {
        this.stage = stage;
        progData = ProgData.getInstance();

        make();
        initRadio();
    }

    public void makePane(Collection<TitledPane> titledPanes) {
        TitledPane tpConfig = new TitledPane("Download", gridPane);
        titledPanes.add(tpConfig);
    }

    public void close() {
        tglFinished.selectedProperty().unbindBidirectional(ProgConfig.DOWNLOAD_SHOW_NOTIFICATION);
        tglSSL.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_SSL_ALWAYS_TRUE);
    }

    private void make() {
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.PADDING));

        tglFinished.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_SHOW_NOTIFICATION);
        final Button btnHelpFinished = P2Button.helpButton(stage, "Download",
                HelpText.DOWNLOAD_FINISHED);

        final Button btnHelpContinue = P2Button.helpButton(stage, "Download",
                HelpText.DOWNLOAD_CONTINUE);

        tglSSL.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_SSL_ALWAYS_TRUE);
        final Button btnHelpSSL = P2Button.helpButton(stage, "Download",
                HelpText.DOWNLOAD_SSL_ALWAYS_TRUE);

        GridPane.setHalignment(btnHelpFinished, HPos.RIGHT);
        GridPane.setHalignment(btnHelpContinue, HPos.RIGHT);
        GridPane.setHalignment(btnHelpSSL, HPos.RIGHT);

        int row = 0;
        gridPane.add(tglFinished, 0, row);
        gridPane.add(btnHelpFinished, 1, row);

        VBox vBox = new VBox(5);
        HBox hBox = new HBox(20);
        hBox.getChildren().addAll(new Label("            "), rbAsk, rbContinue, rbRestart);
        vBox.getChildren().addAll(new Label("Beim Neustart bereits angefangener Downloads:"), hBox);

        ++row;
        gridPane.add(vBox, 0, ++row);
        gridPane.add(btnHelpContinue, 1, row);

        ++row;
        gridPane.add(tglSSL, 0, ++row);
        gridPane.add(btnHelpSSL, 1, row);

        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcComputedSizeAndHgrow(),
                P2ColumnConstraints.getCcPrefSize());
    }

    private void initRadio() {
        rbAsk.setToggleGroup(group);
        rbContinue.setToggleGroup(group);
        rbRestart.setToggleGroup(group);
        setRadio();

        ProgConfig.DOWNLOAD_CONTINUE.addListener((v, o, n) -> setRadio());
        rbAsk.setOnAction(a -> ProgConfig.DOWNLOAD_CONTINUE.setValue(DownloadState.DOWNLOAD_RESTART__ASK));
        rbContinue.setOnAction(a -> ProgConfig.DOWNLOAD_CONTINUE.setValue(DownloadState.DOWNLOAD_RESTART__CONTINUE));
        rbRestart.setOnAction(a -> ProgConfig.DOWNLOAD_CONTINUE.setValue(DownloadState.DOWNLOAD_RESTART__RESTART));
    }

    private void setRadio() {
        switch (ProgConfig.DOWNLOAD_CONTINUE.getValue()) {
            case DownloadState.DOWNLOAD_RESTART__CONTINUE:
                rbContinue.setSelected(true);
                break;
            case DownloadState.DOWNLOAD_RESTART__RESTART:
                rbRestart.setSelected(true);
                break;
            case DownloadState.DOWNLOAD_RESTART__ASK:
            default:
                rbAsk.setSelected(true);
                break;
        }
    }
}