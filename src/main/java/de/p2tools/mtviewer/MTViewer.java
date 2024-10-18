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
import de.p2tools.mtviewer.controller.config.ProgColorList;
import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgConst;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.p2lib.P2LibInit;
import de.p2tools.p2lib.guitools.P2GuiSize;
import de.p2tools.p2lib.tools.duration.P2Duration;
import de.p2tools.p2lib.tools.shortcut.P2ShortcutKey;
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

        ProgStartBeforeGui.workBeforeGui();
        initP2lib();

        initRootLayout();
        ProgStartAfterGui.doWorkAfterGui();

        P2Duration.onlyPing("Gui steht!");
        P2Duration.counterStop(LOG_TEXT_PROGRAMSTART);
    }

    private void initP2lib() {
        P2LibInit.initLib(primaryStage, ProgConst.PROGRAM_NAME,
                "", ProgConfig.SYSTEM_DARK_THEME, null, ProgConfig.SYSTEM_THEME_CHANGED,
                ProgConst.CSS_FILE, ProgConst.CSS_FILE_DARK_THEME, ProgConfig.SYSTEM_FONT_SIZE,
                ProgData.debug, ProgData.duration);
    }

    private void initRootLayout() {
        try {
            progData.mtViewerController = new MTViewerController();

            scene = new Scene(progData.mtViewerController,
                    P2GuiSize.getWidth(ProgConfig.SYSTEM_SIZE_GUI),
                    P2GuiSize.getHeight(ProgConfig.SYSTEM_SIZE_GUI));//Größe der scene!= Größe stage!!!
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

            P2LibInit.addP2CssToScene(scene); // und jetzt noch CSS einstellen
            ProgConfig.SYSTEM_DARK_THEME.addListener((u, o, n) -> {
                ProgColorList.setColorTheme();
            });

            primaryStage.show();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
