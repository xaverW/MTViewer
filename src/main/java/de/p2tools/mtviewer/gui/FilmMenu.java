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
import de.p2tools.mtviewer.gui.configDialog.ConfigDialogController;
import de.p2tools.mtviewer.gui.dialog.AboutDialogController;
import de.p2tools.mtviewer.gui.dialog.ResetDialogController;
import de.p2tools.mtviewer.gui.tools.ProgTipOfDay;
import de.p2tools.mtviewer.tools.update.SearchProgramUpdate;
import de.p2tools.p2Lib.guiTools.POpen;
import de.p2tools.p2Lib.tools.shortcut.PShortcutWorker;
import javafx.scene.control.*;


public class FilmMenu {

    private FilmMenu() {
    }

    public static MenuButton getFilmMenu(ProgData progData) {
        final MenuButton mb = new MenuButton("");
        mb.setTooltip(new Tooltip("Filmmenü anzeigen"));
        mb.setGraphic(ProgIcons.Icons.FX_ICON_TOOLBAR_MENU.getImageView());
        mb.getStyleClass().add("btnFunctionWide");

        //=========================
        //Filme
        final Menu mFilm = new Menu("Filme");

        final MenuItem miPlay = new MenuItem("Film abspielen");
        miPlay.setOnAction(a -> progData.filmGuiController.playFilmUrl());
        PShortcutWorker.addShortCut(miPlay, MTShortcut.SHORTCUT_PLAY_FILM);

        final MenuItem miFilmInfo = new MenuItem("Filminformation anzeigen");
        miFilmInfo.setOnAction(a -> progData.filmGuiController.showFilmInfo());
        PShortcutWorker.addShortCut(miFilmInfo, MTShortcut.SHORTCUT_INFO_FILM);

        final MenuItem miShowInfo = new MenuItem("Infobereich unter der Tabelle ein-/ausblenden");
        miShowInfo.setOnAction(a -> progData.mtViewerPlayerController.setInfos());
        PShortcutWorker.addShortCut(miShowInfo, MTShortcut.SHORTCUT_SHOW_INFOS);
        mFilm.getItems().addAll(miPlay, miFilmInfo, miShowInfo);
        mb.getItems().add(mFilm);

        //=========================
        //Filmliste/Einstellungen
        final MenuItem miLoad = new MenuItem("Eine neue Filmliste laden");
        miLoad.setOnAction(e -> progData.loadFilmlist.loadNewFilmlist(false));

        final MenuItem miConfig = new MenuItem("Einstellungen des Programms");
        miConfig.setOnAction(e -> new ConfigDialogController(ProgData.getInstance()).showDialog());
        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().addAll(miLoad, miConfig);


        //=========================
        //Hilfe
        final MenuItem miUrlHelp = new MenuItem("Anleitung im Web");
        miUrlHelp.setOnAction(event -> {
            POpen.openURL(ProgConst.URL_WEBSITE_HELP,
                    ProgConfig.SYSTEM_PROG_OPEN_URL, ProgIcons.Icons.ICON_BUTTON_FILE_OPEN.getImageView());
        });
        final MenuItem miReset = new MenuItem("Einstellungen zurücksetzen");
        miReset.setOnAction(event -> new ResetDialogController(progData));
        final MenuItem miToolTip = new MenuItem("Tip des Tages");
        miToolTip.setOnAction(a -> new ProgTipOfDay().showDialog(progData, true));
        final MenuItem miSearchUpdate = new MenuItem("Gibts ein Update?");
        miSearchUpdate.setOnAction(a -> new SearchProgramUpdate(progData, progData.primaryStage).searchNewProgramVersion(true));
        final MenuItem miAbout = new MenuItem("Über dieses Programm");
        miAbout.setOnAction(event -> new AboutDialogController(ProgData.getInstance()));

        final Menu mHelp = new Menu("Hilfe");
        mHelp.getItems().addAll(miUrlHelp, miReset, miToolTip, miSearchUpdate, new SeparatorMenuItem(), miAbout);
        mb.getItems().add(new SeparatorMenuItem());
        mb.getItems().add(mHelp);

        //=========================
        //Quitt
        final MenuItem miQuit = new MenuItem("Beenden");
        miQuit.setOnAction(e -> ProgQuit.quit(false));
        PShortcutWorker.addShortCut(miQuit, MTShortcut.SHORTCUT_QUIT_PROGRAM);
        mb.getItems().add(miQuit);

        return mb;
    }
}
