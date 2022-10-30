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

package de.p2tools.mtviewer.gui;

import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.data.download.DownloadData;
import de.p2tools.mtviewer.gui.tools.table.TableDownload;
import de.p2tools.p2Lib.MTDownload.BandwidthTokenBucket;
import de.p2tools.p2Lib.tools.PSystemUtils;
import javafx.beans.property.IntegerProperty;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class DownloadGuiTableContextMenu {

    private final ProgData progData;
    private final DownloadListInfoController downloadListInfoController;
    private final TableDownload tableView;
    private final Slider sliderBandwidth = new Slider();
    private final Label lblBandwidth = new Label();
    IntegerProperty bandwidthValue = ProgConfig.DOWNLOAD_MAX_BANDWIDTH_KBYTE;

    public DownloadGuiTableContextMenu(final ProgData progData, final DownloadListInfoController downloadListInfoController, final TableDownload tableView) {
        this.progData = progData;
        this.downloadListInfoController = downloadListInfoController;
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

        final MenuItem miCleanUp = new MenuItem("Liste der Downloads aufräumen");
        miCleanUp.setOnAction(e -> downloadListInfoController.cleanUp());

        contextMenu.getItems().addAll(customMenuItem, new SeparatorMenuItem(), miCleanUp, new SeparatorMenuItem());


        //dann die "echten" Menüpunkte
        final MenuItem miStart = new MenuItem("Download starten");
        miStart.setOnAction(a -> downloadListInfoController.startDownload(false));
        final MenuItem miStop = new MenuItem("Download stoppen");
        miStop.setOnAction(a -> downloadListInfoController.stopDownload(false));
        final MenuItem miChange = new MenuItem("Download ändern");
        miChange.setOnAction(a -> downloadListInfoController.changeDownload());

        miStart.setDisable(download == null);
        miStop.setDisable(download == null);
        miChange.setDisable(download == null);
        contextMenu.getItems().addAll(miStart, miStop, miChange);

        // Submenü "Download"
        final MenuItem miPrefer = new MenuItem("Downloads vorziehen");
        miPrefer.setOnAction(a -> downloadListInfoController.preferDownload());
        final MenuItem miPutBack = new MenuItem("Downloads zurückstellen");
        miPutBack.setOnAction(a -> downloadListInfoController.moveDownloadBack());
        final MenuItem miRemove = new MenuItem("Downloads aus Liste entfernen");
        miRemove.setOnAction(a -> downloadListInfoController.deleteDownloads());

        final Menu submenuDownload = new Menu("Downloads");
        submenuDownload.setDisable(download == null);
        submenuDownload.getItems().addAll(miPrefer, miPutBack, miRemove);
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(submenuDownload);


        // Submenü "alle Downloads"
        final MenuItem miStartAll = new MenuItem("Alle Downloads starten");
        miStartAll.setOnAction(a -> downloadListInfoController.startDownload(true /* alle */));

        final MenuItem miStopAll = new MenuItem("Alle Downloads stoppen");
        miStopAll.setOnAction(a -> downloadListInfoController.stopDownload(true /* alle */));
        final MenuItem miStopWaiting = new MenuItem("Alle wartenden Downloads stoppen");
        miStopWaiting.setOnAction(a -> downloadListInfoController.stopWaitingDownloads());

        miStartAll.setDisable(download == null);
        miStopAll.setDisable(download == null);
        miStopWaiting.setDisable(download == null);

        final Menu submenuAllDownloads = new Menu("Alle Downloads");
        submenuAllDownloads.getItems().addAll(miStartAll, miStopAll, miStopWaiting);
        contextMenu.getItems().addAll(submenuAllDownloads);


        // Submenü "gespeicherte Filme"
        final MenuItem miPlayerDownload = new MenuItem("Gespeicherten Film (Datei) abspielen");
        miPlayerDownload.setOnAction(a -> downloadListInfoController.playFilm());
        final MenuItem miDeleteDownload = new MenuItem("Gespeicherten Film (Datei) löschen");
        miDeleteDownload.setOnAction(a -> downloadListInfoController.deleteFilmFile());
        final MenuItem miOpenDir = new MenuItem("Zielordner öffnen");
        miOpenDir.setOnAction(e -> downloadListInfoController.openDestinationDir());

        final Menu submenuFilm = new Menu("Gespeicherten Film");
        submenuFilm.setDisable(download == null);
        submenuFilm.getItems().addAll(miPlayerDownload, miDeleteDownload, miOpenDir);
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(submenuFilm);


        final MenuItem miFilmInfo = new MenuItem("Filminformation anzeigen");
        miFilmInfo.setOnAction(a -> downloadListInfoController.showFilmInfo());
        final MenuItem miPlayUrl = new MenuItem("Film (URL) abspielen");
        miPlayUrl.setOnAction(a -> downloadListInfoController.playUrl());
        final MenuItem miCopyUrl = new MenuItem("Download (URL) kopieren");
        miCopyUrl.setOnAction(a -> downloadListInfoController.copyUrl());


        final MenuItem miCopyName = new MenuItem("Titel in die Zwischenablage kopieren");
        miCopyName.setOnAction(a -> {
            PSystemUtils.copyToClipboard(download.getTitle());
        });
        final MenuItem miCopyTheme = new MenuItem("Thema in die Zwischenablage kopieren");
        miCopyTheme.setOnAction(a -> {
            PSystemUtils.copyToClipboard(download.getTheme());
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
        miSelection.setOnAction(a -> downloadListInfoController.invertSelection());
        final MenuItem resetTable = new MenuItem("Tabelle zurücksetzen");
        resetTable.setOnAction(a -> tableView.resetTable());

        miSelectAll.setDisable(download == null);
        miSelection.setDisable(download == null);

        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(miSelectAll, miSelection, resetTable);
    }

    private void initBandwidth() {
        Label lblText = new Label("max. Bandbreite: ");
        lblText.setMinWidth(0);
        lblText.setTooltip(new Tooltip("Maximale Bandbreite die ein einzelner Dowload beanspruchen darf \n" +
                "oder unbegrenzt wenn \"aus\""));
        sliderBandwidth.setTooltip(new Tooltip("Maximale Bandbreite die ein einzelner Dowload beanspruchen darf \n" +
                "oder unbegrenzt wenn \"aus\""));

        sliderBandwidth.setMin(50);
        sliderBandwidth.setMax(BandwidthTokenBucket.BANDWIDTH_MAX_KBYTE);
        sliderBandwidth.setShowTickLabels(true);
        sliderBandwidth.setMinorTickCount(9);
        sliderBandwidth.setMajorTickUnit(250);
        sliderBandwidth.setBlockIncrement(25);
        sliderBandwidth.setSnapToTicks(true);

        sliderBandwidth.setLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Double x) {
                if (x == BandwidthTokenBucket.BANDWIDTH_MAX_KBYTE) {
                    return "alles";
                }

                return x.intValue() + "";
            }

            @Override
            public Double fromString(String string) {
                return null;
            }
        });

        sliderBandwidth.valueProperty().bindBidirectional(bandwidthValue);
        setTextBandwidth();

        sliderBandwidth.valueProperty().addListener((obs, oldValue, newValue) -> {
            setTextBandwidth();
        });
    }

    private void setTextBandwidth() {
        int bandwidthKByte;
        String ret;
        bandwidthKByte = ProgConfig.DOWNLOAD_MAX_BANDWIDTH_KBYTE.getValue();
        if (bandwidthKByte == BandwidthTokenBucket.BANDWIDTH_MAX_KBYTE) {
            ret = "alles";
        } else {
            ret = bandwidthKByte + " kB/s";
        }
        lblBandwidth.setText(ret);
    }
}
