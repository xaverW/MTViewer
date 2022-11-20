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
import de.p2tools.mtviewer.gui.tools.table.TableFilm;
import de.p2tools.p2Lib.mtFilm.film.FilmData;
import de.p2tools.p2Lib.tools.PSystemUtils;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

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

        Menu mFilter = addFilter(film);// Filter
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(mFilter);

        Menu mCopyUrl = copyUrl(film);// URL kopieren
        contextMenu.getItems().addAll(mCopyUrl);

        MenuItem miFilmInfo = new MenuItem("Filminformation anzeigen");
        miFilmInfo.setOnAction(a -> filmGuiController.showFilmInfo());
        miFilmInfo.setDisable(film == null);

        final MenuItem miCopyName = new MenuItem("Titel in die Zwischenablage kopieren");
        miCopyName.setOnAction(a -> {
            PSystemUtils.copyToClipboard(film.getTitle());
        });
        final MenuItem miCopyTheme = new MenuItem("Thema in die Zwischenablage kopieren");
        miCopyTheme.setOnAction(a -> {
            PSystemUtils.copyToClipboard(film.getTheme());
        });

        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(/*miFilmsSetShown,*/ miFilmInfo, miCopyName, miCopyTheme);


        MenuItem resetTable = new MenuItem("Tabelle zurücksetzen");
        resetTable.setOnAction(a -> tableView.resetTable());
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().addAll(resetTable);
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

        final MenuItem miFilterChannelThemeTitle = new MenuItem("nach Sender, und Titel filtern");
        miFilterChannelThemeTitle.setOnAction(event -> {
            progData.actFilmFilterWorker.getActFilterSettings().setChannel(film.getChannel());
            progData.actFilmFilterWorker.getActFilterSettings().setTitle(film.getTitle());
        });

        submenuFilter.getItems().addAll(miFilterChannel, miFilterTheme, miFilterTitle, miFilterChannelTheme, miFilterChannelThemeTitle);
        return submenuFilter;
    }

    private Menu copyUrl(FilmData filmData) {
        final Menu subMenuURL = new Menu("Film-URL kopieren");
        if (filmData == null) {
            subMenuURL.setDisable(true);
            return subMenuURL;
        }

        final String uNormal = filmData.getUrlForResolution(FilmData.RESOLUTION_NORMAL);
        String uHd = filmData.getUrlForResolution(FilmData.RESOLUTION_HD);
        String uLow = filmData.getUrlForResolution(FilmData.RESOLUTION_SMALL);
        String uSub = filmData.getUrlSubtitle();
        if (uHd.equals(uNormal)) {
            uHd = ""; // dann gibts keine
        }
        if (uLow.equals(uNormal)) {
            uLow = ""; // dann gibts keine
        }

        MenuItem item;
        if (!uHd.isEmpty() || !uLow.isEmpty() || !uSub.isEmpty()) {
            // HD
            if (!uHd.isEmpty()) {
                item = new MenuItem("in HD-Auflösung");
                item.setOnAction(a -> PSystemUtils.copyToClipboard(filmData.getUrlForResolution(FilmData.RESOLUTION_HD)));
                subMenuURL.getItems().add(item);
            }

            // normale Auflösung, gibts immer
            item = new MenuItem("in hoher Auflösung");
            item.setOnAction(a -> PSystemUtils.copyToClipboard(filmData.getUrlForResolution(FilmData.RESOLUTION_NORMAL)));
            subMenuURL.getItems().add(item);

            // kleine Auflösung
            if (!uLow.isEmpty()) {
                item = new MenuItem("in geringer Auflösung");
                item.setOnAction(a -> PSystemUtils.copyToClipboard(filmData.getUrlForResolution(FilmData.RESOLUTION_SMALL)));
                subMenuURL.getItems().add(item);
            }

            // Untertitel
            if (!filmData.getUrlSubtitle().isEmpty()) {
                item = new MenuItem("Untertitel-URL kopieren");
                item.setOnAction(a -> PSystemUtils.copyToClipboard(filmData.getUrlSubtitle()));
                subMenuURL.getItems().addAll(new SeparatorMenuItem(), item);
            }

        } else {
            item = new MenuItem("Film-URL kopieren");
            item.setOnAction(a -> PSystemUtils.copyToClipboard(filmData.getUrlForResolution(FilmData.RESOLUTION_NORMAL)));
            subMenuURL.getItems().add(item);
        }

        return subMenuURL;
    }
}
