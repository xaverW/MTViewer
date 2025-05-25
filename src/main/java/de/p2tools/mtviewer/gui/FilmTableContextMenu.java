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
import de.p2tools.mtviewer.gui.help.table.TableFilm;
import de.p2tools.p2lib.mtfilm.film.FilmData;
import de.p2tools.p2lib.tools.P2ToolsFactory;
import javafx.scene.control.*;

public class FilmTableContextMenu {

    private final ProgData progData;
    private final FilmGuiController filmGuiController;
    private final TableFilm tableView;

    public FilmTableContextMenu(ProgData progData, FilmGuiController filmGuiController, TableFilm tableView) {
        this.progData = progData;
        this.filmGuiController = filmGuiController;
        this.tableView = tableView;
    }

    public ContextMenu getContextMenu(FilmData film) {
        final ContextMenu contextMenu = new ContextMenu();
        getMenu(contextMenu, film);
        return contextMenu;
    }

    private void getMenu(ContextMenu contextMenu, FilmData film) {
        // Start/Save
        MenuItem miStart = new MenuItem("Film abspielen");
        miStart.setOnAction(a -> filmGuiController.playFilm());
        miStart.setDisable(film == null);
        MenuItem miSave = new MenuItem("Film speichern");
        miSave.setOnAction(a -> filmGuiController.saveFilm());
        miSave.setDisable(film == null);
        contextMenu.getItems().addAll(miStart, miSave);

        // Filter
        Menu mFilter = addFilter(film);
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(mFilter);

        // URL kopieren
        Menu mCopyUrl = FilmTableContextMenu.copyInfos(film);
        contextMenu.getItems().addAll(mCopyUrl);

        MenuItem miFilmInfo = new MenuItem("Filminformation anzeigen");
        miFilmInfo.setOnAction(a -> filmGuiController.showFilmInfo());
        miFilmInfo.setDisable(film == null);

        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(miFilmInfo);

        contextMenu.getItems().add(new SeparatorMenuItem());
        CheckMenuItem smallTableRow = new CheckMenuItem("Nur kleine Button anzeigen");
        smallTableRow.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_SMALL_ROW_TABLE_FILM);
        CheckMenuItem toolTipTable = new CheckMenuItem("Infos beim Überfahren einer Zeile anzeigen");
        toolTipTable.selectedProperty().bindBidirectional(ProgConfig.FILM_GUI_SHOW_TABLE_TOOL_TIP);
        MenuItem resetTable = new MenuItem("Tabelle zurücksetzen");
        resetTable.setOnAction(a -> tableView.resetTable());
        contextMenu.getItems().addAll(smallTableRow, toolTipTable, resetTable);
    }

    private Menu addFilter(FilmData film) {
        Menu submenuFilter = new Menu("Filter");
        if (film == null) {
            submenuFilter.setDisable(true);
            return submenuFilter;
        }

        final MenuItem miFilterChannel = new MenuItem("nach Sender filtern");
        miFilterChannel.setOnAction(event -> progData.actFilmFilterWorker.getActFilterSettings().setChannel(film.getChannel()));

        final MenuItem miFilterTheme = new MenuItem("nach Thema filtern");
        miFilterTheme.setOnAction(event -> progData.actFilmFilterWorker.getActFilterSettings().setTheme(film.getTheme()));

        final MenuItem miFilterTitle = new MenuItem("nach Titel filtern");
        miFilterTheme.setOnAction(event -> progData.actFilmFilterWorker.getActFilterSettings().setTitle(film.getTheme()));

        final MenuItem miFilterChannelTheme = new MenuItem("nach Sender und Thema filtern");
        miFilterChannelTheme.setOnAction(event -> {
            progData.actFilmFilterWorker.getActFilterSettings().setChannel(film.getChannel());
            progData.actFilmFilterWorker.getActFilterSettings().setTheme(film.getTheme());
        });

        final MenuItem miFilterChannelThemeTitle = new MenuItem("nach Sender und Titel filtern");
        miFilterChannelThemeTitle.setOnAction(event -> {
            progData.actFilmFilterWorker.getActFilterSettings().setChannel(film.getChannel());
            progData.actFilmFilterWorker.getActFilterSettings().setTitle(film.getTitle());
        });

        submenuFilter.getItems().addAll(miFilterChannel, miFilterTheme, miFilterTitle, miFilterChannelTheme, miFilterChannelThemeTitle);
        return submenuFilter;
    }

    public static Menu copyInfos(FilmData film) {
        final Menu subMenuURL = new Menu("Film-Infos kopieren");
        subMenuURL.setDisable(film == null);

        final MenuItem miCopyTheme = new MenuItem("Thema");
        miCopyTheme.setDisable(film == null);
        miCopyTheme.setOnAction(a -> P2ToolsFactory.copyToClipboard(film.getTheme()));

        final MenuItem miCopyName = new MenuItem("Titel");
        miCopyName.setDisable(film == null);
        miCopyName.setOnAction(a -> P2ToolsFactory.copyToClipboard(film.getTitle()));

        final MenuItem miCopyWeb = new MenuItem("Website-URL");
        miCopyWeb.setDisable(film == null);
        miCopyWeb.setOnAction(a -> P2ToolsFactory.copyToClipboard(film.getWebsite()));

        subMenuURL.getItems().addAll(miCopyTheme, miCopyName, miCopyWeb);


        final String uNormal = film == null ? "" : film.getUrlForResolution(FilmData.RESOLUTION_NORMAL);
        String uHd = film == null ? "" : film.getUrlForResolution(FilmData.RESOLUTION_HD);
        String uLow = film == null ? "" : film.getUrlForResolution(FilmData.RESOLUTION_SMALL);
        String uSub = film == null ? "" : film.getUrlSubtitle();

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
                item = new MenuItem("URL in HD-Auflösung");
                item.setOnAction(a -> P2ToolsFactory.copyToClipboard(film.getUrlForResolution(FilmData.RESOLUTION_HD)));
                subMenuURL.getItems().add(item);
            }

            // normale Auflösung, gibts immer
            item = new MenuItem("URL in hoher Auflösung");
            item.setOnAction(a -> P2ToolsFactory.copyToClipboard(film.getUrlForResolution(FilmData.RESOLUTION_NORMAL)));
            subMenuURL.getItems().add(item);

            // kleine Auflösung
            if (!uLow.isEmpty()) {
                item = new MenuItem("URL in kleiner Auflösung");
                item.setOnAction(a -> P2ToolsFactory.copyToClipboard(film.getUrlForResolution(FilmData.RESOLUTION_SMALL)));
                subMenuURL.getItems().add(item);
            }

            // Untertitel
            if (!film.getUrlSubtitle().isEmpty()) {
                subMenuURL.getItems().add(new SeparatorMenuItem());
                item = new MenuItem("Untertitel-URL");
                item.setOnAction(a -> P2ToolsFactory.copyToClipboard(film.getUrlSubtitle()));
                subMenuURL.getItems().add(item);
            }

        } else {
            item = new MenuItem("Film-URL");
            item.setDisable(film == null);
            item.setOnAction(a -> P2ToolsFactory.copyToClipboard(film.getUrlForResolution(FilmData.RESOLUTION_NORMAL)));
            subMenuURL.getItems().add(item);
        }

        return subMenuURL;
    }


}
