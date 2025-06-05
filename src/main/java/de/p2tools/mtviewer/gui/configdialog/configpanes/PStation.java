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
import de.p2tools.mtviewer.gui.help.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.mtfilm.loadfilmlist.P2LoadFactory;
import de.p2tools.p2lib.mtfilm.tools.LoadFactoryConst;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class PStation extends VBox {

    private final Button btnClearAll = new Button("_Wieder alle Sender laden");
    private final Stage stage;

    public PStation(Stage stage) {
        this.stage = stage;
        make();
    }

    public void close() {
    }

    private void make() {
        setSpacing(P2LibConst.SPACING_VBOX);
        setPadding(new Insets(P2LibConst.PADDING_VBOX));
        getChildren().addAll(P2GuiTools.getVDistance(5));

        HBox hBox = new HBox();
        hBox.getStyleClass().add("extra-pane");
        hBox.setPadding(new Insets(P2LibConst.PADDING));
        hBox.setMaxWidth(Double.MAX_VALUE);
        hBox.setMinHeight(Region.USE_PREF_SIZE);
        Label lbl = new Label("Hier können Sender die *nicht* interessieren, beim Laden " +
                "der Filmliste, ausgenommen werden.");
        lbl.setWrapText(true);
        lbl.setPrefWidth(500);
        hBox.getChildren().add(lbl);
        getChildren().addAll(hBox, P2GuiTools.getVDistance(20));


        final Button btnHelpSender = P2Button.helpButton(stage, "Filmliste beim Laden filtern",
                HelpText.LOAD_FILMLIST_SENDER_STARTDIALOG);
        HBox hBoxStation = new HBox(P2LibConst.DIST_BUTTON);
        hBoxStation.setAlignment(Pos.CENTER_LEFT);
        Label lblStation = new Label("Diese Sender  *nicht*  laden:");
        hBoxStation.getChildren().addAll(lblStation, P2GuiTools.getHBoxGrower(), btnClearAll, btnHelpSender);
        getChildren().add(hBoxStation);

        final TilePane tilePaneSender = getTilePaneSender();
        getChildren().addAll(tilePaneSender);
    }

    private TilePane getTilePaneSender() {
        final TilePane tilePaneSender = new TilePane();
        tilePaneSender.setHgap(5);
        tilePaneSender.setVgap(5);
        ArrayList<String> aListChannel = P2LoadFactory.getSenderListNotToLoad();
        ArrayList<CheckBox> aListCb = new ArrayList<>();
        for (int i = 0; i < LoadFactoryConst.SENDER.length; ++i) {
            String s = LoadFactoryConst.SENDER[i];
            String s_ = LoadFactoryConst.SENDER_[i];

            final CheckBox cb = new CheckBox(s);
            cb.setTooltip(new Tooltip(s_));
            aListCb.add(cb);
            cb.setSelected(aListChannel.contains(s));
            cb.setOnAction(a -> {
                makePropSender(aListCb);
                // und noch prüfen, dass nicht alle ausgeschaltet sind
                P2LoadFactory.checkAllSenderSelectedNotToLoad(stage);
            });

            tilePaneSender.getChildren().add(cb);
            TilePane.setAlignment(cb, Pos.CENTER_LEFT);
        }
        btnClearAll.setMinWidth(Region.USE_PREF_SIZE);
        btnClearAll.setOnAction(a -> {
            aListCb.stream().forEach(checkBox -> checkBox.setSelected(false));
            makePropSender(aListCb);
        });
        checkPropSender(aListCb);

        return tilePaneSender;
    }

    private void checkPropSender(ArrayList<CheckBox> aListCb) {
        boolean noneChecked = true;
        for (CheckBox cb : aListCb) {
            if (cb.isSelected()) {
                noneChecked = false;
                break;
            }
        }

        btnClearAll.setDisable(noneChecked);
    }

    private void makePropSender(ArrayList<CheckBox> aListCb) {
        String str = "";
        for (CheckBox cb : aListCb) {
            if (!cb.isSelected()) {
                continue;
            }

            String s = cb.getText();
            str = str.isEmpty() ? s : str + "," + s;
        }
        ProgConfig.SYSTEM_LOAD_NOT_SENDER.setValue(str);
        checkPropSender(aListCb);
    }
}
