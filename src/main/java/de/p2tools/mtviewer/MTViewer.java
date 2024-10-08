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
package de.p2tools.mtviewer;

import de.p2tools.mtviewer.controller.ProgQuit;
import de.p2tools.mtviewer.controller.ProgStartAfterGui;
import de.p2tools.mtviewer.controller.ProgStartBeforeGui;
import de.p2tools.mtviewer.controller.config.*;
import de.p2tools.mtviewer.controller.data.ProgIcons;
import de.p2tools.p2lib.P2LibInit;
import de.p2tools.p2lib.guitools.P2GuiSize;
import de.p2tools.p2lib.tools.IoReadWriteStyle;
import de.p2tools.p2lib.tools.duration.P2Duration;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MTViewer extends Application {

    private static final String LOG_TEXT_PROGRAMSTART = "Dauer Programmstart";
    protected ProgData progData;
    Scene scene = null;
    private Stage primaryStage;
    private boolean firstProgramStart = false; // ist der allererste Programmstart: Programminit wird gemacht

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        P2Duration.counterStart(LOG_TEXT_PROGRAMSTART);
        progData = ProgData.getInstance();
        progData.primaryStage = primaryStage;

        initP2lib();
        ProgStartBeforeGui.workBeforeGui();
        initRootLayout();
        ProgStartAfterGui.doWorkAfterGui();

        P2Duration.onlyPing("Gui steht!");
        P2Duration.counterStop(LOG_TEXT_PROGRAMSTART);
    }

    private void initP2lib() {
        ProgIcons.initIcons();
        P2LibInit.initLib(primaryStage, ProgConst.PROGRAM_NAME,
                "",
                ProgConfig.SYSTEM_DARK_THEME, null,
                ProgData.debug, ProgData.duration);
        P2LibInit.addCssFile(ProgConst.CSS_FILE);
    }

    private void initRootLayout() {
        try {
            addThemeCss(); // damit es für die 2 schon mal stimmt
            progData.mtViewerController = new MTViewerController();

            scene = new Scene(progData.mtViewerController,
                    P2GuiSize.getWidth(ProgConfig.SYSTEM_SIZE_GUI),
                    P2GuiSize.getHeight(ProgConfig.SYSTEM_SIZE_GUI));//Größe der scene!= Größe stage!!!
            addThemeCss(); // und jetzt noch für die neue Scene
            ProgColorList.setColorTheme(); // Farben einrichten

            if (ProgConfig.SYSTEM_STYLE.getValue()) {
                P2LibInit.setStyleFile(ProgInfos.getStyleFile().toString());
                IoReadWriteStyle.readStyle(ProgInfos.getStyleFile(), scene);
            }

            ProgConfig.SYSTEM_DARK_THEME.addListener((u, o, n) -> {
                addThemeCss();
                //erst css ändern, dann
                ProgColorList.setColorTheme();
                ProgConfig.SYSTEM_THEME_CHANGED.setValue(!ProgConfig.SYSTEM_THEME_CHANGED.getValue());
            });

            primaryStage.setScene(scene);
            primaryStage.setOnCloseRequest(e -> {
                //beim Beenden
                e.consume();
                ProgQuit.quit(false);
            });

            //Pos setzen
            P2GuiSize.setOnlyPos(ProgConfig.SYSTEM_SIZE_GUI, primaryStage);

            scene.heightProperty().addListener((v, o, n) -> P2GuiSize.getSizeScene(ProgConfig.SYSTEM_SIZE_GUI, primaryStage, scene));
            scene.widthProperty().addListener((v, o, n) -> P2GuiSize.getSizeScene(ProgConfig.SYSTEM_SIZE_GUI, primaryStage, scene));
            primaryStage.xProperty().addListener((v, o, n) -> P2GuiSize.getSizeScene(ProgConfig.SYSTEM_SIZE_GUI, primaryStage, scene));
            primaryStage.yProperty().addListener((v, o, n) -> P2GuiSize.getSizeScene(ProgConfig.SYSTEM_SIZE_GUI, primaryStage, scene));
            primaryStage.show();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private void addThemeCss() {
        if (ProgConfig.SYSTEM_DARK_THEME.getValue()) {
            P2LibInit.addCssFile(ProgConst.CSS_FILE_DARK_THEME);
        } else {
            P2LibInit.removeCssFile(ProgConst.CSS_FILE_DARK_THEME);
        }
        P2LibInit.addP2CssToScene(scene);
    }
}
