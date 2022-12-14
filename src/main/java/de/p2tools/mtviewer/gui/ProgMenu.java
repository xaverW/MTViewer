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
import de.p2tools.mtviewer.controller.film.LoadFilmFactory;
import de.p2tools.mtviewer.controller.update.SearchProgramUpdate;
import de.p2tools.mtviewer.gui.configDialog.ConfigDialogController;
import de.p2tools.mtviewer.gui.dialog.AboutDialogController;
import de.p2tools.mtviewer.gui.dialog.ResetDialogController;
import de.p2tools.mtviewer.gui.tools.TipOfDay;
import de.p2tools.p2Lib.guiTools.POpen;
import de.p2tools.p2Lib.tools.shortcut.PShortcutWorker;
import javafx.scene.control.*;


public class ProgMenu extends MenuButton {

    public ProgMenu() {
        makeMenue();
    }

    private void makeMenue() {
        ProgData progData = ProgData.getInstance();

        setTooltip(new Tooltip("Filmmenü anzeigen"));
        setGraphic(ProgIcons.Icons.FX_ICON_TOOLBAR_MENU.getImageView());
        getStyleClass().add("btnFunctionWide");

        //=========================
        //Filme
        final Menu mFilm = new Menu("Filme");
        final MenuItem miPlay = new MenuItem("Film abspielen");
        miPlay.setOnAction(a -> progData.filmGuiController.playFilm());
        PShortcutWorker.addShortCut(miPlay, MTShortcut.SHORTCUT_PLAY_FILM);
        final MenuItem miSave = new MenuItem("Film speichern");
        miSave.setOnAction(a -> progData.filmGuiController.saveFilm());
        PShortcutWorker.addShortCut(miSave, MTShortcut.SHORTCUT_SAVE_FILM);
        final MenuItem miFilmInfo = new MenuItem("Filminformation anzeigen");
        miFilmInfo.setOnAction(a -> progData.filmGuiController.showFilmInfo());
        PShortcutWorker.addShortCut(miFilmInfo, MTShortcut.SHORTCUT_INFO_FILM);
        mFilm.getItems().addAll(miPlay, miSave, miFilmInfo);

        //=========================
        //Filmliste, Info, Einstellungen
        final MenuItem miLoad = new MenuItem("Eine neue Filmliste laden");
        miLoad.setOnAction(e -> LoadFilmFactory.getInstance().loadList(false));
        final MenuItem miShowInfo = new MenuItem("Infobereich unter der Tabelle ein-/ausblenden");
        miShowInfo.setOnAction(a -> progData.mtViewerPlayerController.setInfos());
        PShortcutWorker.addShortCut(miShowInfo, MTShortcut.SHORTCUT_SHOW_INFOS);
        final MenuItem miConfig = new MenuItem("Einstellungen");
        miConfig.setOnAction(e -> new ConfigDialogController(ProgData.getInstance()).showDialog());
        getItems().addAll(mFilm, miLoad, miShowInfo, miConfig, new SeparatorMenuItem());

        //=========================
        //Hilfe
        final MenuItem miUrlHelp = new MenuItem("Anleitung im Web");
        miUrlHelp.setOnAction(event -> {
            POpen.openURL(ProgConst.URL_WEBSITE_HELP,
                    ProgConfig.SYSTEM_PROG_OPEN_URL, ProgIcons.Icons.ICON_BUTTON_FILE_OPEN.getImageView());
        });
        final MenuItem miReset = new MenuItem("Alle Programmeinstellungen zurücksetzen");
        miReset.setOnAction(event -> new ResetDialogController(progData));
        final MenuItem miToolTip = new MenuItem("Tip des Tages");
        miToolTip.setOnAction(a -> new TipOfDay().showDialog(progData, true));
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
        miQuit.setOnAction(e -> ProgQuit.quit(false));
        PShortcutWorker.addShortCut(miQuit, MTShortcut.SHORTCUT_QUIT_PROGRAM);

        getItems().addAll(miQuit);
    }
}
