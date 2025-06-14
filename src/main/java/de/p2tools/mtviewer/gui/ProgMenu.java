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

package de.p2tools.mtviewer.gui;

import de.p2tools.mtviewer.controller.ProgQuit;
import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgConst;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.data.MTShortcut;
import de.p2tools.mtviewer.controller.data.ProgIcons;
import de.p2tools.mtviewer.controller.update.SearchProgramUpdate;
import de.p2tools.mtviewer.gui.configdialog.ConfigDialogController;
import de.p2tools.mtviewer.gui.dialog.AboutDialogController;
import de.p2tools.mtviewer.gui.dialog.ResetDialogController;
import de.p2tools.mtviewer.gui.help.TipOfDayFactory;
import de.p2tools.p2lib.guitools.P2Open;
import de.p2tools.p2lib.tools.shortcut.P2ShortcutWorker;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;


public class ProgMenu extends MenuButton {

    public ProgMenu() {
        makeMenue();
    }

    private void makeMenue() {
        ProgData progData = ProgData.getInstance();

        setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                ProgConfig.SYSTEM_DARK_THEME.setValue(!ProgConfig.SYSTEM_DARK_THEME.getValue());

            }
        });

        setTooltip(new Tooltip("Filmmenü anzeigen"));
        setGraphic(ProgIcons.FX_ICON_TOOLBAR_MENU_TOP.getImageView());
        getStyleClass().addAll("btnFunction", "btnFunc-1");

        //=========================
        //Filme
        final Menu mFilm = new Menu("Filme");
        final MenuItem miPlay = new MenuItem("Film abspielen");
        miPlay.setOnAction(a -> progData.filmGuiPack.getFilmGuiController().playFilm());
        P2ShortcutWorker.addShortCut(miPlay, MTShortcut.SHORTCUT_PLAY_FILM);
        final MenuItem miSave = new MenuItem("Film speichern");
        miSave.setOnAction(a -> progData.filmGuiPack.getFilmGuiController().saveFilm());
        P2ShortcutWorker.addShortCut(miSave, MTShortcut.SHORTCUT_SAVE_FILM);
        final MenuItem miFilmInfo = new MenuItem("Filminformation anzeigen");
        miFilmInfo.setOnAction(a -> progData.filmGuiPack.getFilmGuiController().showFilmInfo());
        P2ShortcutWorker.addShortCut(miFilmInfo, MTShortcut.SHORTCUT_INFO_FILM);
        mFilm.getItems().addAll(miPlay, miSave, miFilmInfo);

        //=========================
        // Info, Einstellungen
        final CheckMenuItem miDarkMode = new CheckMenuItem("Dark Mode");
        miDarkMode.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_DARK_THEME);

        final CheckMenuItem miShowInfo = new CheckMenuItem("Infobereich anzeigen");
        miShowInfo.selectedProperty().bindBidirectional(ProgConfig.INFO__IS_SHOWING);
        miShowInfo.disableProperty().bind(ProgConfig.INFO__PANE_INFO_IS_RIP
                .and(ProgConfig.INFO__PANE_DOWNLOAD_IS_RIP));
        P2ShortcutWorker.addShortCut(miShowInfo, MTShortcut.SHORTCUT_SHOW_INFOS);

        final CheckMenuItem miShowFilter = new CheckMenuItem("Filter anzeigen");
        miShowFilter.selectedProperty().bindBidirectional(ProgConfig.FILTER__IS_SHOWING);
        miShowFilter.disableProperty().bind(ProgConfig.FILTER__PANE_FILTER_IS_RIP);
        P2ShortcutWorker.addShortCut(miShowFilter, MTShortcut.SHORTCUT_SHOW_FILTER);

        final MenuItem miConfig = new MenuItem("Einstellungen");
        miConfig.setOnAction(e -> new ConfigDialogController(ProgData.getInstance()).showDialog());
        getItems().addAll(mFilm, miDarkMode, miShowInfo, miShowFilter, miConfig, new SeparatorMenuItem());

        //=========================
        //Hilfe
        final MenuItem miUrlHelp = new MenuItem("Anleitung im Web");
        miUrlHelp.setOnAction(event -> {
            P2Open.openURL(ProgConst.URL_WEBSITE_HELP,
                    ProgConfig.SYSTEM_PROG_OPEN_URL, ProgIcons.ICON_BUTTON_FILE_OPEN.getImageView());
        });
        final MenuItem miReset = new MenuItem("Alle Programmeinstellungen zurücksetzen");
        miReset.setOnAction(event -> new ResetDialogController(progData));
        final MenuItem miToolTip = new MenuItem("Tip des Tages");
        miToolTip.setOnAction(a -> TipOfDayFactory.showDialog(progData, true));
        final MenuItem miSearchUpdate = new MenuItem("Gibt's ein Update?");
        miSearchUpdate.setOnAction(a -> new SearchProgramUpdate(progData, progData.primaryStage).searchNewProgramVersion(true));
        final MenuItem miAbout = new MenuItem("Über dieses Programm");
        miAbout.setOnAction(event -> new AboutDialogController(ProgData.getInstance()).showDialog());

        final Menu mHelp = new Menu("Hilfe");
        mHelp.getItems().addAll(miUrlHelp, miReset, miToolTip, miSearchUpdate, new SeparatorMenuItem(), miAbout);
        getItems().addAll(mHelp);

        //=========================
        //Quitt
        final MenuItem miQuit = new MenuItem("Beenden");
        miQuit.setOnAction(e -> ProgQuit.quit());
        P2ShortcutWorker.addShortCut(miQuit, MTShortcut.SHORTCUT_QUIT_PROGRAM);

        getItems().addAll(miQuit);
    }
}
