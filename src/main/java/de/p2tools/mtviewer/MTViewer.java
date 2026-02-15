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
import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.p2lib.css.P2CssFactory;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.guitools.P2GuiSize;
import de.p2tools.p2lib.tools.P2InfoFactory;
import de.p2tools.p2lib.tools.duration.P2Duration;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MTViewer extends Application {

    private static final String LOG_TEXT_PROGRAMSTART = "Dauer Programmstart";
    protected ProgData progData;
    private Stage primaryStage;
    private boolean done = false;

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

        initRootLayout();
        ProgStartAfterGui.doWorkAfterGui();

        P2Duration.onlyPing("Gui steht!");
        P2Duration.counterStop(LOG_TEXT_PROGRAMSTART);
    }

    private void initRootLayout() {
        try {
            progData.mtViewerController = new MTViewerController();

            Scene scene = new Scene(progData.mtViewerController,
                    P2GuiSize.getSceneSize(ProgConfig.SYSTEM_SIZE_GUI, true),
                    P2GuiSize.getSceneSize(ProgConfig.SYSTEM_SIZE_GUI, false));
            primaryStage.setScene(scene);
            primaryStage.setOnCloseRequest(e -> {
                //beim Beenden
                e.consume();
                ProgQuit.quit();
            });


            if (P2InfoFactory.getOs() == P2InfoFactory.OperatingSystemType.LINUX) {
                // braucht's bei aktuellem GNOME
                if (ProgData.firstProgramStart) {
                    P2Log.sysLog("FirstProgramStart & LINUX: Resizable: false");
                    primaryStage.setResizable(false);
                    scene.setOnMouseEntered(mouseEvent -> {
                        Platform.runLater(() -> {
                            if (!done) {
                                done = true;
                                P2GuiSize.setSizePos(ProgConfig.SYSTEM_SIZE_GUI, primaryStage, null);
                                primaryStage.setResizable(true);
                                P2Log.sysLog("FirstProgramStart & LINUX: Resizable: true");
                            }
                        });
                    });
                }
            }

            if (ProgConfig.SYSTEM_GUI_LAST_START_WAS_MAXIMISED.get()) {
                //========= MAXIMISED ===========
                // dann wars maximiert oder soll immer so gestartet werden
                P2GuiSize.setPos(ProgConfig.SYSTEM_SIZE_GUI, primaryStage);
                primaryStage.setMaximized(true);

                if (P2InfoFactory.getOs() == P2InfoFactory.OperatingSystemType.LINUX) {
                    primaryStage.setOnShown(e -> {
                        startMaximised();
                    });
                }

            } else {
                //========= !MAXIMISED ===========
                primaryStage.setOnShowing(e -> P2GuiSize.setSizePos(ProgConfig.SYSTEM_SIZE_GUI, primaryStage, null));
                primaryStage.setOnShown(e -> P2GuiSize.setSizePos(ProgConfig.SYSTEM_SIZE_GUI, primaryStage, null));
            }
            primaryStage.setOnCloseRequest(e -> {
                //beim Beenden
                e.consume();
                ProgQuit.quit();
            });

            P2CssFactory.addP2CssToScene(scene); // und jetzt noch CSS einstellen
            primaryStage.iconifiedProperty().addListener((u, o, n) -> {
                P2DialogExtra.getDialogList().forEach(d -> d.getStage().setIconified(n));
            });
            primaryStage.show();

            if (ProgData.firstProgramStart) {
                // dann gabs den Startdialog
                ProgConfig.SYSTEM_DARK_THEME.set(ProgConfig.SYSTEM_DARK_START.get());
                ProgConfig.SYSTEM_GUI_THEME_1.set(ProgConfig.SYSTEM_GUI_THEME_1_START.get());
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private void startMaximised() {
        // KDE braucht da ein EXTRA!
        new Thread(new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    wait(1_000);
                } catch (Exception ignore) {
                }
                Platform.runLater(() -> {
                    if (ProgData.getInstance().primaryStage.isShowing()) {
                        P2GuiSize.getSize(ProgConfig.SYSTEM_SIZE_GUI, ProgData.getInstance().primaryStage);
                        P2GuiSize.setSizePos(ProgConfig.SYSTEM_SIZE_GUI, primaryStage, null);
                        // primaryStage.setMaximized(false); // geht in GNOME wieder nicht
                    }
                });
                return null;
            }
        }).start();
    }
}
