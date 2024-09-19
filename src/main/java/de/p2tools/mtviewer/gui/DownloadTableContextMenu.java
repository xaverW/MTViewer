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

import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.data.download.DownloadData;
import de.p2tools.mtviewer.controller.data.download.DownloadFactory;
import de.p2tools.mtviewer.gui.tools.table.TableDownload;
import de.p2tools.p2lib.tools.P2SystemUtils;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DownloadTableContextMenu {

    private final ProgData progData;
    private final PaneDownloadInfo paneDownloadInfo;
    private final TableDownload tableView;
    private final Slider sliderBandwidth = new Slider();
    private final Label lblBandwidth = new Label();

    public DownloadTableContextMenu(final ProgData progData, final PaneDownloadInfo paneDownloadInfo, final TableDownload tableView) {
        this.progData = progData;
        this.paneDownloadInfo = paneDownloadInfo;
        this.tableView = tableView;
        initBandwidth();
    }

    public ContextMenu getContextMenu(final DownloadData download) {
        final ContextMenu contextMenu = new ContextMenu();
        getMenu(contextMenu, download);
        return contextMenu;
    }

    private void getMenu(final ContextMenu contextMenu, final DownloadData download) {
        //erst mal die Einstellung der Bandbreite
        HBox hBox = new HBox(10);
        hBox.getChildren().addAll(sliderBandwidth, lblBandwidth);
        VBox vBox = new VBox(10);
        vBox.getChildren().addAll(new Label("max. Bandbreite:"), hBox);
        CustomMenuItem customMenuItem = new CustomMenuItem(vBox);
        customMenuItem.setHideOnClick(false);

        contextMenu.getItems().addAll(customMenuItem, new SeparatorMenuItem());

        //dann die "echten" Menüpunkte
        final MenuItem miStart = new MenuItem("Download starten");
        miStart.setOnAction(a -> paneDownloadInfo.startDownload(false));
        final MenuItem miStop = new MenuItem("Download stoppen");
        miStop.setOnAction(a -> paneDownloadInfo.stopDownload(false));
        final MenuItem miChange = new MenuItem("Download ändern");
        miChange.setOnAction(a -> paneDownloadInfo.changeDownload());

        miStart.setDisable(download == null);
        miStop.setDisable(download == null);
        miChange.setDisable(download == null);
        contextMenu.getItems().addAll(miStart, miStop, miChange);

        // Submenü "Download"
        final MenuItem miPrefer = new MenuItem("Downloads vorziehen");
        miPrefer.setOnAction(a -> paneDownloadInfo.preferDownload());
        final MenuItem miPutBack = new MenuItem("Downloads zurückstellen");
        miPutBack.setOnAction(a -> paneDownloadInfo.moveDownloadBack());
        final MenuItem miRemove = new MenuItem("Downloads aus Liste entfernen");
        miRemove.setOnAction(a -> paneDownloadInfo.deleteDownloads());

        final Menu submenuDownload = new Menu("Downloads");
        submenuDownload.setDisable(download == null);
        submenuDownload.getItems().addAll(miPrefer, miPutBack, miRemove);
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(submenuDownload);


        // Submenü "alle Downloads"
        final MenuItem miStartAll = new MenuItem("Alle Downloads starten");
        miStartAll.setOnAction(a -> paneDownloadInfo.startDownload(true /* alle */));

        final MenuItem miStopAll = new MenuItem("Alle Downloads stoppen");
        miStopAll.setOnAction(a -> paneDownloadInfo.stopDownload(true /* alle */));
        final MenuItem miStopWaiting = new MenuItem("Alle wartenden Downloads stoppen");
        miStopWaiting.setOnAction(a -> paneDownloadInfo.stopWaitingDownloads());

        miStartAll.setDisable(download == null);
        miStopAll.setDisable(download == null);
        miStopWaiting.setDisable(download == null);

        final Menu submenuAllDownloads = new Menu("Alle Downloads");
        submenuAllDownloads.getItems().addAll(miStartAll, miStopAll, miStopWaiting);
        contextMenu.getItems().addAll(submenuAllDownloads);


        // Submenü "gespeicherte Filme"
        final MenuItem miPlayerDownload = new MenuItem("Gespeicherten Film (Datei) abspielen");
        miPlayerDownload.setOnAction(a -> paneDownloadInfo.playFilm());
        final MenuItem miDeleteDownload = new MenuItem("Gespeicherten Film (Datei) löschen");
        miDeleteDownload.setOnAction(a -> paneDownloadInfo.deleteFilmFile());
        final MenuItem miOpenDir = new MenuItem("Zielordner öffnen");
        miOpenDir.setOnAction(e -> paneDownloadInfo.openDestinationDir());

        final Menu submenuFilm = new Menu("Gespeicherten Film");
        submenuFilm.setDisable(download == null);
        submenuFilm.getItems().addAll(miPlayerDownload, miDeleteDownload, miOpenDir);
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(submenuFilm);


        final MenuItem miFilmInfo = new MenuItem("Filminformation anzeigen");
        miFilmInfo.setOnAction(a -> paneDownloadInfo.showFilmInfo());
        final MenuItem miPlayUrl = new MenuItem("Film (URL) abspielen");
        miPlayUrl.setOnAction(a -> paneDownloadInfo.playUrl());
        final MenuItem miCopyUrl = new MenuItem("Download (URL) kopieren");
        miCopyUrl.setOnAction(a -> paneDownloadInfo.copyUrl());


        final MenuItem miCopyName = new MenuItem("Titel in die Zwischenablage kopieren");
        miCopyName.setOnAction(a -> {
            P2SystemUtils.copyToClipboard(download.getTitle());
        });
        final MenuItem miCopyTheme = new MenuItem("Thema in die Zwischenablage kopieren");
        miCopyTheme.setOnAction(a -> {
            P2SystemUtils.copyToClipboard(download.getTheme());
        });

        miFilmInfo.setDisable(download == null);
        miPlayUrl.setDisable(download == null);
        miCopyUrl.setDisable(download == null);
        miCopyName.setDisable(download == null);
        miCopyTheme.setDisable(download == null);

        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(miFilmInfo, miPlayUrl, miCopyUrl, miCopyName, miCopyTheme);


        final MenuItem miSelectAll = new MenuItem("Alles auswählen");
        miSelectAll.setOnAction(a -> tableView.getSelectionModel().selectAll());
        final MenuItem miSelection = new MenuItem("Auswahl umkehren");
        miSelection.setOnAction(a -> paneDownloadInfo.invertSelection());
        final MenuItem resetTable = new MenuItem("Tabelle zurücksetzen");
        resetTable.setOnAction(a -> tableView.resetTable());

        miSelectAll.setDisable(download == null);
        miSelection.setDisable(download == null);

        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(miSelectAll, miSelection, resetTable);
    }

    private void initBandwidth() {
        Label lblText = new Label("Max. Bandbreite: ");
        lblText.setMinWidth(0);
        lblText.setTooltip(new Tooltip("Maximale Bandbreite die ein einzelner Download beanspruchen darf \n" +
                "oder unbegrenzt wenn \"aus\""));
        sliderBandwidth.setTooltip(new Tooltip("Maximale Bandbreite die ein einzelner Download beanspruchen darf \n" +
                "oder unbegrenzt wenn \"aus\""));

        DownloadFactory.initBandwidth(sliderBandwidth, lblBandwidth);
    }
}
