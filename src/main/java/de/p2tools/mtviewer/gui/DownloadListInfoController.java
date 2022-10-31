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
import de.p2tools.mtviewer.controller.data.ProgIcons;
import de.p2tools.mtviewer.controller.data.download.DownloadData;
import de.p2tools.mtviewer.controller.data.download.DownloadDataFactory;
import de.p2tools.mtviewer.controller.data.film.FilmData;
import de.p2tools.mtviewer.controller.data.film.FilmTools;
import de.p2tools.mtviewer.gui.dialog.DownloadAddDialogController;
import de.p2tools.mtviewer.gui.dialog.FilmInfoDialogController;
import de.p2tools.mtviewer.gui.tools.table.Table;
import de.p2tools.mtviewer.gui.tools.table.TableDownload;
import de.p2tools.mtviewer.gui.tools.table.TableRowDownload;
import de.p2tools.p2Lib.alert.PAlert;
import de.p2tools.p2Lib.guiTools.POpen;
import de.p2tools.p2Lib.guiTools.PTableFactory;
import de.p2tools.p2Lib.tools.PSystemUtils;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Optional;

public class DownloadListInfoController extends AnchorPane {

    private final VBox vBoxFilter = new VBox();
    private final VBox vBoxTable = new VBox();
    private final TableDownload tableView;
    private final ScrollPane scrollPane = new ScrollPane();
    private final ProgData progData;
    private final SortedList<DownloadData> sortedDownloads;
    DoubleProperty doubleProperty; //sonst geht die Ref verloren

    public DownloadListInfoController() {
        progData = ProgData.getInstance();
        tableView = new TableDownload(Table.TABLE_ENUM.DOWNLOAD);
        this.doubleProperty = ProgConfig.DOWNLOAD_GUI_FILTER_DIVIDER;
        sortedDownloads = new SortedList<>(progData.downloadList);

        AnchorPane.setLeftAnchor(vBoxTable, 0.0);
        AnchorPane.setBottomAnchor(vBoxTable, 0.0);
        AnchorPane.setRightAnchor(vBoxTable, 0.0);
        AnchorPane.setTopAnchor(vBoxTable, 0.0);
        getChildren().add(vBoxTable);
        make();
    }

    public void tableRefresh() {
        tableView.refresh();
    }

    public void startDownload(boolean all) {
        downloadStartAgain(all);
    }

    private void downloadStartAgain(boolean all) {
        // bezieht sich auf "alle" oder nur die markierten Filme
        // der/die noch nicht gestartet sind, werden gestartet
        // Filme dessen Start schon auf fehler steht werden wieder gestartet

        final ArrayList<DownloadData> startDownloadsList = new ArrayList<>();
        startDownloadsList.addAll(all ? tableView.getItems() : getSelList());
        progData.downloadList.startDownloads(startDownloadsList, true);
    }

    public void stopDownload(boolean all) {
        stopDownloads(all);
    }

    private void stopDownloads(boolean all) {
        // bezieht sich auf "alle" oder nur die markierten Filme
        final ArrayList<DownloadData> listDownloadsSelected = new ArrayList<>();
        // die URLs sammeln
        listDownloadsSelected.addAll(all ? tableView.getItems() : getSelList());
        progData.downloadList.stopDownloads(listDownloadsSelected);
    }

    public void preferDownload() {
        progData.downloadList.preferDownloads(getSelList());
    }

    public void moveDownloadBack() {
        progData.downloadList.putBackDownloads(getSelList());
    }

    public void stopWaitingDownloads() {
        stopWaiting();
    }

    private void stopWaiting() {
        // es werden alle noch nicht gestarteten Downloads gestoppt
        final ArrayList<DownloadData> listStopDownload = new ArrayList<>();
        tableView.getItems().stream().filter(download -> download.isStateStartedWaiting()).forEach(download -> {
            listStopDownload.add(download);
        });
        progData.downloadList.stopDownloads(listStopDownload);
    }

    public void deleteFilmFile() {
        // Download nur löschen wenn er nicht läuft
        final Optional<DownloadData> download = getSel();
        if (!download.isPresent()) {
            return;
        }
        DownloadDataFactory.deleteFilmFile(download.get());
    }

    public void openDestinationDir() {
        final Optional<DownloadData> download = getSel();
        if (!download.isPresent()) {
            return;
        }

        String s = download.get().getDestPath();
        POpen.openDir(s, ProgConfig.SYSTEM_PROG_OPEN_DIR, ProgIcons.Icons.ICON_BUTTON_FILE_OPEN.getImageView());
    }

    public void showFilmInfo() {
        FilmInfoDialogController.getInstanceAndShow().showFilmInfo();
    }

    public void playUrl() {
        final Optional<DownloadData> download = getSel();
        if (!download.isPresent()) {
            return;
        }

        FilmData film;
        if (download.get().getFilm() == null) {
            film = new FilmData();
        } else {
            film = download.get().getFilm().getCopy();
        }

        // und jetzt die tatsächlichen URLs des Downloads eintragen
        film.arr[FilmData.FILM_URL] = download.get().getUrl();
        film.arr[FilmData.FILM_URL_SMALL] = "";
        // und starten
        FilmTools.playFilm(film);
    }

    public void copyUrl() {
        final Optional<DownloadData> download = getSel();
        if (!download.isPresent()) {
            return;
        }
        PSystemUtils.copyToClipboard(download.get().getUrl());
    }

    public void invertSelection() {
        PTableFactory.invertSelection(tableView);
    }

    public void cleanUp() {
        progData.downloadList.cleanUpList();
    }

    public void playFilm() {
        final Optional<DownloadData> download = getSel();
        if (download.isPresent()) {
            FilmTools.playFilm(download.get().getDestPathFile());
        }
    }

    private void make() {
        vBoxFilter.setSpacing(10);
        vBoxFilter.setPadding(new Insets(10));
        vBoxTable.setSpacing(10);
        vBoxTable.setPadding(new Insets(10));

        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(tableView);
        vBoxTable.getChildren().add(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        initTable();
    }

    private ArrayList<DownloadData> getSelList() {
        // todo observableList -> abo
        final ArrayList<DownloadData> ret = new ArrayList<>();
        ret.addAll(tableView.getSelectionModel().getSelectedItems());
        if (ret.isEmpty()) {
            PAlert.showInfoNoSelection();
        }
        return ret;
    }

    public void deleteDownloads() {
        int sel = tableView.getSelectionModel().getSelectedIndex();
        progData.downloadList.delDownloads(getSelList());
        if (sel >= 0) {
            tableView.getSelectionModel().clearSelection();
            if (tableView.getItems().size() > sel) {
                tableView.getSelectionModel().select(sel);
            } else {
                tableView.getSelectionModel().selectLast();
            }
        }
    }

    public void saveTable() {
        Table.saveTable(tableView, Table.TABLE_ENUM.DOWNLOAD);
    }

    private void initTable() {
        Table.setTable(tableView);

        tableView.setItems(sortedDownloads);
        sortedDownloads.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setRowFactory(tv -> {
            TableRowDownload<DownloadData> row = new TableRowDownload<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                    changeDownload();
                }
            });
            return row;
        });
        tableView.setOnMousePressed(m -> {
            if (m.getButton().equals(MouseButton.SECONDARY)) {
                final Optional<DownloadData> optionalDownload = getSel(false);
                DownloadData download;
                if (optionalDownload.isPresent()) {
                    download = optionalDownload.get();
                } else {
                    download = null;
                }
                ContextMenu contextMenu = new DownloadGuiTableContextMenu(progData, this, tableView).getContextMenu(download);
                tableView.setContextMenu(contextMenu);
            }
        });
        tableView.getItems().addListener((ListChangeListener<DownloadData>) c -> {
            if (tableView.getItems().size() == 1) {
                // wenns nur eine Zeile gibt, dann gleich selektieren
                tableView.getSelectionModel().select(0);
            }
        });
        tableView.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (PTableFactory.SPACE.match(event)) {
                PTableFactory.scrollVisibleRangeDown(tableView);
                event.consume();
            }
            if (PTableFactory.SPACE_SHIFT.match(event)) {
                PTableFactory.scrollVisibleRangeUp(tableView);
                event.consume();
            }
        });
    }

    public void changeDownload() {
        change();
    }

    private Optional<DownloadData> getSel() {
        return getSel(true);
    }

    private Optional<DownloadData> getSel(boolean show) {
        final int selectedTableRow = tableView.getSelectionModel().getSelectedIndex();
        if (selectedTableRow >= 0) {
            return Optional.of(tableView.getSelectionModel().getSelectedItem());
        } else {
            if (show) {
                PAlert.showInfoNoSelection();
            }
            return Optional.empty();
        }
    }

    private synchronized void change() {
        final Optional<DownloadData> download = getSel();
        if (download.isPresent()) {
            DownloadData downloadCopy = download.get().getCopy();

            DownloadAddDialogController downloadAddDialogController =
                    new DownloadAddDialogController(progData, downloadCopy, downloadCopy.getFilm(), true);
            if (downloadAddDialogController.isOk()) {
                download.get().copyToMe(downloadCopy);
            }
        }
    }
}
