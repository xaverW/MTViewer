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

import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.data.download.DownloadData;
import de.p2tools.mtviewer.gui.help.table.TableDownload;
import de.p2tools.p2lib.mtfilm.film.FilmData;
import de.p2tools.p2lib.tools.P2ToolsFactory;
import javafx.scene.control.*;

public class DownloadTableContextMenu {

    private final ProgData progData;
    private final PaneDownloadInfo paneDownloadInfo;
    private final TableDownload tableView;

    public DownloadTableContextMenu(final ProgData progData, final PaneDownloadInfo paneDownloadInfo, final TableDownload tableView) {
        this.progData = progData;
        this.paneDownloadInfo = paneDownloadInfo;
        this.tableView = tableView;
    }

    public ContextMenu getContextMenu(final DownloadData download) {
        final ContextMenu contextMenu = new ContextMenu();
        getMenu(contextMenu, download);
        return contextMenu;
    }

    private void getMenu(final ContextMenu contextMenu, final DownloadData download) {
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


        contextMenu.getItems().add(new SeparatorMenuItem());
        final Menu submenuDownload = new Menu("Downloads");
        submenuDownload.setDisable(download == null);
        submenuDownload.getItems().addAll(miPrefer, miPutBack, miRemove);
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
        contextMenu.getItems().addAll(submenuFilm);

        final MenuItem miPlayUrl = new MenuItem("Film (URL) abspielen");
        miPlayUrl.setOnAction(a -> paneDownloadInfo.playUrl());
        miPlayUrl.setDisable(download == null);
        contextMenu.getItems().addAll(miPlayUrl);


        contextMenu.getItems().add(new SeparatorMenuItem());
        Menu mCopyUrl = copyInfos(download);
        contextMenu.getItems().addAll(mCopyUrl);


        contextMenu.getItems().add(new SeparatorMenuItem());
        final MenuItem miSelectAll = new MenuItem("Alles auswählen");
        miSelectAll.setOnAction(a -> tableView.getSelectionModel().selectAll());
        miSelectAll.setDisable(download == null);
        final MenuItem miSelection = new MenuItem("Auswahl umkehren");
        miSelection.setOnAction(a -> paneDownloadInfo.invertSelection());
        miSelection.setDisable(download == null);
        contextMenu.getItems().addAll(miSelectAll, miSelection);


        contextMenu.getItems().add(new SeparatorMenuItem());
        CheckMenuItem smallTableRow = new CheckMenuItem("Nur kleine Button anzeigen");
        smallTableRow.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_SMALL_ROW_TABLE_DOWNLOAD);
        CheckMenuItem toolTipTable = new CheckMenuItem("Infos beim Überfahren einer Zeile anzeigen");
        toolTipTable.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_GUI_SHOW_TABLE_TOOL_TIP);
        MenuItem resetTable = new MenuItem("Tabelle zurücksetzen");
        resetTable.setOnAction(a -> tableView.resetTable());
        contextMenu.getItems().addAll(smallTableRow, toolTipTable, resetTable);
    }

    public static Menu copyInfos(DownloadData downloadData) {
        final Menu subMenuURL = new Menu("Film-Infos kopieren");
        if (downloadData == null) {
            subMenuURL.setDisable(true);
            return subMenuURL;
        }

        FilmData film = downloadData.getFilm();

        final MenuItem miCopyTheme = new MenuItem("Thema");
        miCopyTheme.setOnAction(a -> P2ToolsFactory.copyToClipboard(downloadData.getTheme()));

        final MenuItem miCopyName = new MenuItem("Titel");
        miCopyName.setOnAction(a -> P2ToolsFactory.copyToClipboard(downloadData.getTitle()));

        subMenuURL.getItems().addAll(miCopyTheme, miCopyName);

        if (film != null) {
            final MenuItem miCopyWeb = new MenuItem("Website-URL");
            miCopyWeb.setOnAction(a -> P2ToolsFactory.copyToClipboard(film.getWebsite()));
            subMenuURL.getItems().addAll(miCopyWeb);

            final String uNormal = film.getUrlForResolution(FilmData.RESOLUTION_NORMAL);
            String uHd = film.getUrlForResolution(FilmData.RESOLUTION_HD);
            String uLow = film.getUrlForResolution(FilmData.RESOLUTION_SMALL);
            String uSub = film.getUrlSubtitle();

            if (uHd.equals(uNormal)) {
                uHd = ""; // dann gibts keine
            }
            if (uLow.equals(uNormal)) {
                uLow = ""; // dann gibts keine
            }

            MenuItem item;
            if (!uHd.isEmpty() || !uLow.isEmpty() || !uSub.isEmpty()) {
                subMenuURL.getItems().add(new SeparatorMenuItem());
                // HD
                if (!uHd.isEmpty()) {
                    item = new MenuItem("Film-URL in HD-Auflösung");
                    item.setOnAction(a -> P2ToolsFactory.copyToClipboard(film.getUrlForResolution(FilmData.RESOLUTION_HD)));
                    subMenuURL.getItems().add(item);
                }

                // normale Auflösung, gibts immer
                item = new MenuItem("Film-URL in hoher Auflösung");
                item.setOnAction(a -> P2ToolsFactory.copyToClipboard(film.getUrlForResolution(FilmData.RESOLUTION_NORMAL)));
                subMenuURL.getItems().add(item);

                // kleine Auflösung
                if (!uLow.isEmpty()) {
                    item = new MenuItem("Film-URL in kleiner Auflösung");
                    item.setOnAction(a -> P2ToolsFactory.copyToClipboard(film.getUrlForResolution(FilmData.RESOLUTION_SMALL)));
                    subMenuURL.getItems().add(item);
                }

            } else {
                item = new MenuItem("Film-URL");
                item.setOnAction(a -> P2ToolsFactory.copyToClipboard(film.getUrlForResolution(FilmData.RESOLUTION_NORMAL)));
                subMenuURL.getItems().add(item);
            }

        } else {
            MenuItem item = new MenuItem("Download-URL");
            item.setOnAction(a -> P2ToolsFactory.copyToClipboard(downloadData.getUrl()));
            subMenuURL.getItems().add(item);
        }

        // Untertitel
        if (!downloadData.getUrlSubtitle().isEmpty()) {
            subMenuURL.getItems().add(new SeparatorMenuItem());
            MenuItem item = new MenuItem("Untertitel-URL");
            item.setOnAction(a -> P2ToolsFactory.copyToClipboard(downloadData.getUrlSubtitle()));
            subMenuURL.getItems().add(item);
        }

        return subMenuURL;
    }
}
