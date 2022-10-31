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

package de.p2tools.mtviewer.gui.startDialog;

import de.p2tools.p2Lib.P2LibConst;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class StartPane {
    private final Stage stage;

    public StartPane(Stage stage) {
        this.stage = stage;
    }

    public void close() {
    }

    public TitledPane makeStart1() {
        HBox hBox = new HBox();
        hBox.setSpacing(25);
        hBox.setPadding(new Insets(20));

        ImageView iv = new ImageView();
        Image im = getHelpScreen1();
        iv.setSmooth(true);
        iv.setImage(im);

        hBox.getChildren().addAll(iv);
        Label text = new Label("1) Die Filmliste kann hier" + P2LibConst.LINE_SEPARATOR +
                "zeitlich eingeschränkt werden." +

                P2LibConst.LINE_SEPARATORx2 +
                "2) Das sind Filter, die das Suchen" + P2LibConst.LINE_SEPARATOR +
                "in der Filmliste ermöglichen." +

                P2LibConst.LINE_SEPARATORx2 +
                "3) Hier können alte Filereinstellungen" + P2LibConst.LINE_SEPARATOR +
                "wiederhergestellt und der" + P2LibConst.LINE_SEPARATOR +
                "Filter gelöscht werden." +

                P2LibConst.LINE_SEPARATORx2 +
                "4) Das ist das Programm-Menü" + P2LibConst.LINE_SEPARATOR +
                "mit den Einstellungen und Infos über" + P2LibConst.LINE_SEPARATOR +
                "das Programm." +

                P2LibConst.LINE_SEPARATORx2 +
                "5) Ein Film oder ein Download kann" + P2LibConst.LINE_SEPARATOR +
                "hier gestartet werden." +

                P2LibConst.LINE_SEPARATORx2 +
                "6) Infos zum markierten Film" + P2LibConst.LINE_SEPARATOR +
                "werden hier angezeigt.");

        hBox.getChildren().add(text);

        TitledPane tpConfig = new TitledPane("Infos zur Programmoberfläche", hBox);
        return tpConfig;
    }

    public TitledPane makeStart2() {
        HBox hBox = new HBox();
        hBox.setSpacing(25);
        hBox.setPadding(new Insets(20));

        ImageView iv = new ImageView();
        Image im = getHelpScreen2();
        iv.setSmooth(true);
        iv.setImage(im);

        hBox.getChildren().addAll(iv);

        Label text = new Label("1) Hier werden die" + P2LibConst.LINE_SEPARATOR +
                "Downloads angezeigt." +

                P2LibConst.LINE_SEPARATORx2 +
                "2) Das ist die Liste aller" + P2LibConst.LINE_SEPARATOR +
                "angelegter Downloads.");
        hBox.getChildren().add(text);

        TitledPane tpConfig = new TitledPane("Infos zum Filmfilter", hBox);
        return tpConfig;
    }

    private javafx.scene.image.Image getHelpScreen1() {
        final String path = "/de/p2tools/mtviewer/res/mtviewer-startpage-1.png";
        return new javafx.scene.image.Image(path, 600,
                600,
                true, true);
    }

    private javafx.scene.image.Image getHelpScreen2() {
        final String path = "/de/p2tools/mtviewer/res/mtviewer-startpage-2.png";
        return new javafx.scene.image.Image(path, 600,
                600,
                true, true);
    }
}
