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

import de.p2tools.mtviewer.controller.FilmTools;
import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.data.ProgIcons;
import de.p2tools.mtviewer.controller.data.download.DownloadData;
import de.p2tools.mtviewer.controller.data.download.DownloadDataFactory;
import de.p2tools.mtviewer.gui.dialog.DownloadAddDialogController;
import de.p2tools.mtviewer.gui.dialog.FilmInfoDialogController;
import de.p2tools.mtviewer.gui.help.table.Table;
import de.p2tools.mtviewer.gui.help.table.TableDownload;
import de.p2tools.mtviewer.gui.help.table.TableRowDownload;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.guitools.P2Open;
import de.p2tools.p2lib.guitools.P2TableFactory;
import de.p2tools.p2lib.mediathek.filmdata.FilmData;
import de.p2tools.p2lib.tools.P2ToolsFactory;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Optional;

public class PaneDownloadInfo extends AnchorPane {

    private final HBox hBoxAll = new HBox();
    private final VBox vBoxTable = new VBox();
    private final TableDownload tableView;
    private final ScrollPane scrollPane = new ScrollPane();
    private final ProgData progData;
    private final SortedList<DownloadData> sortedDownloads;

    DoubleProperty doubleProperty; //sonst geht die Ref verloren

    public PaneDownloadInfo() {
        progData = ProgData.getInstance();
        VBox.setVgrow(this, Priority.ALWAYS);
        tableView = new TableDownload(Table.TABLE_ENUM.DOWNLOAD);
        this.doubleProperty = ProgConfig.DOWNLOAD_GUI_FILTER_DIVIDER;
        sortedDownloads = new SortedList<>(progData.downloadList);

        AnchorPane.setLeftAnchor(hBoxAll, 0.0);
        AnchorPane.setBottomAnchor(hBoxAll, 0.0);
        AnchorPane.setRightAnchor(hBoxAll, 0.0);
        AnchorPane.setTopAnchor(hBoxAll, 0.0);
        getChildren().add(hBoxAll);
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
        P2Open.openDir(s, ProgConfig.SYSTEM_PROG_OPEN_DIR, ProgIcons.ICON_BUTTON_FILE_OPEN.getImageView());
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
        P2ToolsFactory.copyToClipboard(download.get().getUrl());
    }

    public void invertSelection() {
        P2TableFactory.invertSelection(tableView);
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
        hBoxAll.setSpacing(P2LibConst.DIST_BUTTON);
        hBoxAll.setPadding(new Insets(P2LibConst.PADDING));

        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(tableView);
        vBoxTable.getChildren().add(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        Button btnClearFilter = new Button();
        Button btnStartAll = new Button();
        Button btnStopAll = new Button();

        VBox vBoxButton = new VBox(P2LibConst.DIST_BUTTON);
        vBoxButton.setAlignment(Pos.TOP_CENTER);
        vBoxButton.getChildren().addAll(btnStartAll, btnStopAll, btnClearFilter);

        hBoxAll.getChildren().addAll(vBoxTable, vBoxButton);

        btnClearFilter.setGraphic(ProgIcons.ICON_BUTTON_RESET.getImageView());
        btnClearFilter.setTooltip(new Tooltip("Tabelle aufräumen"));
        btnClearFilter.getStyleClass().add("buttonSmall");
        btnClearFilter.setOnAction(a -> cleanUp());

        btnStartAll.setGraphic(ProgIcons.ICON_BUTTON_START_ALL.getImageView());
        btnStartAll.setTooltip(new Tooltip("Alle Downloads starten"));
        btnStartAll.getStyleClass().add("buttonSmall");
        btnStartAll.setOnAction(a -> startDownload(true /* alle */));

        btnStopAll.setGraphic(ProgIcons.ICON_BUTTON_STOP_ALL.getImageView());
        btnStopAll.setTooltip(new Tooltip("Alle Downloads stoppen"));
        btnStopAll.getStyleClass().add("buttonSmall");
        btnStopAll.setOnAction(a -> stopDownload(true /* alle */));

        initTable();
    }

    private ArrayList<DownloadData> getSelList() {
        // todo observableList -> abo
        final ArrayList<DownloadData> ret = new ArrayList<>();
        ret.addAll(tableView.getSelectionModel().getSelectedItems());
        if (ret.isEmpty()) {
            P2Alert.showInfoNoSelection();
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
                ContextMenu contextMenu = new DownloadTableContextMenu(progData, this, tableView).getContextMenu(download);
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
            if (P2TableFactory.SPACE.match(event)) {
                P2TableFactory.scrollVisibleRangeDown(tableView);
                event.consume();
            }
            if (P2TableFactory.SPACE_SHIFT.match(event)) {
                P2TableFactory.scrollVisibleRangeUp(tableView);
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
                P2Alert.showInfoNoSelection();
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
