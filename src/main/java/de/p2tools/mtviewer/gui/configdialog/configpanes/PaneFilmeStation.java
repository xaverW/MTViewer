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
import de.p2tools.mtviewer.controller.data.film.FilmToolsFactory;
import de.p2tools.mtviewer.controller.picon.PIconFactory;
import de.p2tools.mtviewer.gui.help.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.P2Text;
import de.p2tools.p2lib.mediathek.filmlistload.P2LoadConst;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collection;

public class PaneFilmeStation {

    final Button btnSetAll = new Button("_Alle Sender laden");
    final Button btnClearAll = new Button("_Keinen Sender laden");
    private final Stage stage;

    public PaneFilmeStation(Stage stage) {
        this.stage = stage;
    }

    public void close() {
    }

    public TitledPane make(Collection<TitledPane> result) {
        final VBox vBox = new VBox(10);
        vBox.setPadding(new Insets(P2LibConst.PADDING));

        HBox hBox = new HBox();
        hBox.setPadding(new Insets(P2LibConst.PADDING));
        hBox.getStyleClass().add("extra-pane");
        hBox.setMaxWidth(Double.MAX_VALUE);
        hBox.setMinHeight(Region.USE_PREF_SIZE);
        Label lbl = new Label("Hier können die Sender ausgewählt werden, die geladen werden sollen.");
        lbl.setWrapText(true);
        lbl.setPrefWidth(500);
        hBox.getChildren().add(lbl);
        vBox.getChildren().addAll(hBox, P2GuiTools.getVDistance(20));

        makeSender(vBox);
        vBox.getChildren().add(new PLoadNewList());
        TitledPane tpConfig = new TitledPane("Sender auswählen", vBox);
        if (result != null) {
            result.add(tpConfig);
        }
        return tpConfig;
    }

    private void makeSender(VBox vBox) {
        final Button btnHelpSender = PIconFactory.getHelpButton(stage, "Film/Audio-Liste beim Laden filtern",
                HelpText.LOAD_FILMLIST_SENDER);
        HBox hBox = new HBox(P2LibConst.DIST_BUTTON);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().addAll(P2Text.getLblTextBold("Sender die geladen werden:"));

        HBox hBoxB = new HBox(P2LibConst.DIST_BUTTON);
        hBoxB.getChildren().addAll(btnClearAll, btnSetAll, P2GuiTools.getHBoxGrower(), btnHelpSender);
        vBox.getChildren().addAll(P2GuiTools.getHDistance(5), hBox,
                getTilePaneSender(), P2GuiTools.getHDistance(5), hBoxB);
    }

    private GridPane getTilePaneSender() {
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(20);
        int row = 0;
        int col = 0;

        ArrayList<String> aListChannel = FilmToolsFactory.getSenderListNotToLoad();
        ArrayList<CheckBox> aListCb = new ArrayList<>();

        for (int i = 0; i < P2LoadConst.SENDER.length; ++i) {
            String s = P2LoadConst.SENDER[i];
            String s_ = P2LoadConst.SENDER_[i];

            final CheckBox cb = new CheckBox(s);
            cb.setTooltip(new Tooltip(s_));
            aListCb.add(cb);
            cb.setSelected(!aListChannel.contains(s));
            cb.setOnAction(a -> {
                makePropSender(aListCb);
                // und noch prüfen, dass nicht alle ausgeschaltet sind
                // FilmToolsFactory.checkAllSenderSelectedNotToLoad(stage);
            });

            gridPane.add(cb, col, row);
            ++col;
            if (col > 5) {
                col = 0;
                ++row;
            }
        }
        btnSetAll.setMinWidth(Region.USE_PREF_SIZE);
        btnSetAll.setOnAction(a -> {
            aListCb.forEach(checkBox -> checkBox.setSelected(true));
            makePropSender(aListCb);
        });
        btnClearAll.setMinWidth(Region.USE_PREF_SIZE);
        btnClearAll.setOnAction(a -> {
            aListCb.forEach(checkBox -> checkBox.setSelected(false));
            makePropSender(aListCb);
        });
        checkPropSender(aListCb);

        return gridPane;
    }

    private void checkPropSender(ArrayList<CheckBox> aListCb) {
        boolean allChecked = true;
        for (CheckBox cb : aListCb) {
            if (!cb.isSelected()) {
                allChecked = false;
                break;
            }
        }
        btnSetAll.setDisable(allChecked);

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
            if (cb.isSelected()) {
                continue;
            }

            String s = cb.getText();
            str = str.isEmpty() ? s : str + "," + s;
        }

        ProgConfig.SYSTEM_LOAD_NOT_SENDER.setValue(str);
        checkPropSender(aListCb);
    }
}
