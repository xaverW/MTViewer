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
import de.p2tools.mtviewer.controller.film.FilmTools;
import de.p2tools.mtviewer.gui.dialog.FilmInfoDialogController;
import de.p2tools.mtviewer.gui.tools.Listener;
import de.p2tools.mtviewer.gui.tools.table.Table;
import de.p2tools.mtviewer.gui.tools.table.TableFilm;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.guitools.PTableFactory;
import de.p2tools.p2lib.guitools.pclosepane.PClosePaneH;
import de.p2tools.p2lib.mtfilm.film.FilmData;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Optional;

public class FilmGuiController extends AnchorPane {

    private final SplitPane splitPane = new SplitPane();
    private final ScrollPane scrollPaneTableFilm = new ScrollPane();
    private final PClosePaneH pClosePaneHInfo;
    private final TabPane tabPaneInfo;
    private final TableFilm tableView;
    private final ProgData progData;
    private final SortedList<FilmData> sortedList;
    private final KeyCombination STRG_A = new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_ANY);
    DoubleProperty splitPaneProperty = ProgConfig.FILM_GUI_DIVIDER;
    BooleanProperty boolInfoOn = ProgConfig.FILM_GUI_DIVIDER_ON;
    private FilmData lastShownFilmData = null;
    private boolean boundSplitPaneDivPos = false;

    private FilmInfoController filmInfoController;
    private DownloadInfoController downloadInfoController;

    public FilmGuiController() {
        progData = ProgData.getInstance();
        sortedList = progData.filmlist.getSortedList();
        pClosePaneHInfo = new PClosePaneH(ProgConfig.FILM_GUI_DIVIDER_ON, true);
        tabPaneInfo = new TabPane();
        tableView = new TableFilm(Table.TABLE_ENUM.FILM, progData);

        downloadInfoController = new DownloadInfoController();

        AnchorPane.setLeftAnchor(splitPane, 0.0);
        AnchorPane.setBottomAnchor(splitPane, 0.0);
        AnchorPane.setRightAnchor(splitPane, 0.0);
        AnchorPane.setTopAnchor(splitPane, 0.0);
        splitPane.setOrientation(Orientation.VERTICAL);
        getChildren().addAll(splitPane);

        scrollPaneTableFilm.setFitToHeight(true);
        scrollPaneTableFilm.setFitToWidth(true);
        scrollPaneTableFilm.setContent(tableView);

        initInfoPane();
        setInfoPane();
        initTable();
        initListener();
    }

    public void isShown() {
        setFilmInfos();
        tableView.requestFocus();
    }

    public int getFilmCount() {
        return tableView.getItems().size();
    }

    public int getSelCount() {
        return tableView.getSelectionModel().getSelectedItems().size();
    }

    public void showFilmInfo() {
        FilmInfoDialogController.getInstanceAndShow().showFilmInfo();
    }

    public synchronized void playFilm() {
        // Menü/Button Film (URL) abspielen
        final Optional<FilmData> filmSelection = getSel();
        if (filmSelection.isPresent()) {
            FilmTools.playFilm(filmSelection.get());
        }
    }

    public synchronized void saveFilm() {
        final Optional<FilmData> filmSelection = getSel();
        if (filmSelection.isPresent()) {
            FilmTools.saveFilm(filmSelection.get());
        }
    }

    public void saveTable() {
        Table.saveTable(tableView, Table.TABLE_ENUM.FILM);
        downloadInfoController.saveTable();
    }

    public void refreshTable() {
        PTableFactory.refreshTable(tableView);
    }

    public ArrayList<FilmData> getSelList() {
        final ArrayList<FilmData> ret = new ArrayList<>();
        ret.addAll(tableView.getSelectionModel().getSelectedItems());
        if (ret.isEmpty()) {
            PAlert.showInfoNoSelection();
        }
        return ret;
    }

    public Optional<FilmData> getSel() {
        return getSel(true);
    }

    public Optional<FilmData> getSel(boolean show) {
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

    private void initListener() {
        Listener.addListener(new Listener(new int[]{Listener.EVENT_GUI_HISTORY_CHANGED},
                FilmGuiController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                PTableFactory.refreshTable(tableView);
            }
        });
        Listener.addListener(new Listener(Listener.EVENT_BLACKLIST_CHANGED, this.getClass().getSimpleName()) {
            @Override
            public void pingFx() {
                lastShownFilmData = null;
            }

        });
    }

    private void selectFilm() {
        Platform.runLater(() -> {
            if ((tableView.getItems().size() == 0)) {
                return;
            }
            if (lastShownFilmData != null) {
                tableView.getSelectionModel().clearSelection();
                tableView.getSelectionModel().select(lastShownFilmData);
                tableView.scrollTo(lastShownFilmData);

            } else {
                FilmData selFilm = tableView.getSelectionModel().getSelectedItem();
                if (selFilm != null) {
                    tableView.scrollTo(selFilm);
                } else {
                    tableView.getSelectionModel().clearSelection();
                    tableView.getSelectionModel().select(0);
                    tableView.scrollTo(0);
                }
            }
        });
    }

    private void initTable() {
        Table.setTable(tableView);

        tableView.setItems(sortedList);
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setOnMousePressed(m -> {
            if (m.getButton().equals(MouseButton.SECONDARY)) {
                final Optional<FilmData> optionalFilm = getSel(false);
                FilmData film;
                if (optionalFilm.isPresent()) {
                    film = optionalFilm.get();
                } else {
                    film = null;
                }
                ContextMenu contextMenu = new FilmTableContextMenu(progData, this, tableView).getContextMenu(film);
                tableView.setContextMenu(contextMenu);
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

            if (STRG_A.match(event) && tableView.getItems().size() > 3_000) {
                //macht eingentlich keine Sinn???
                PLog.sysLog("STRG-A: lange Liste -> verhindern");
                event.consume();
            }
        });

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                Platform.runLater(this::setFilmInfos));
    }

    private void setFilmInfos() {
        FilmData film = tableView.getSelectionModel().getSelectedItem();
        filmInfoController.setFilm(film);
        FilmInfoDialogController.getInstance().setFilm(film);
    }

    private void initInfoPane() {
        filmInfoController = new FilmInfoController();
        boolInfoOn.addListener((observable, oldValue, newValue) -> setInfoPane());
    }

    private void setInfoPane() {
        if (boolInfoOn.getValue()) {
            boundSplitPaneDivPos = true;
            setInfoTabPane();
            splitPane.getDividers().get(0).positionProperty().bindBidirectional(splitPaneProperty);

        } else {
            if (boundSplitPaneDivPos) {
                splitPane.getDividers().get(0).positionProperty().unbindBidirectional(splitPaneProperty);
            }

            if (splitPane.getItems().size() != 1) {
                splitPane.getItems().clear();
                splitPane.getItems().add(scrollPaneTableFilm);
            }
        }
    }

    private void setInfoTabPane() {
        if (splitPane.getItems().size() != 2) {
            //erst mal splitPane einrichten, dass Tabelle und Info angezeigt werden
            splitPane.getItems().clear();
            splitPane.getItems().addAll(scrollPaneTableFilm, pClosePaneHInfo);
            SplitPane.setResizableWithParent(pClosePaneHInfo, false);
        }

        Tab tabInfo = new Tab("Infos");
        tabInfo.setClosable(false);
        tabInfo.setContent(filmInfoController);

        Tab tabDownloads = new Tab("Downloads");
        tabDownloads.setClosable(false);
        tabDownloads.setContent(downloadInfoController);

        tabPaneInfo.getTabs().clear();
        tabPaneInfo.getTabs().addAll(tabInfo, tabDownloads);

        pClosePaneHInfo.getVBoxAll().getChildren().clear();
        pClosePaneHInfo.getVBoxAll().getChildren().add(tabPaneInfo);
        VBox.setVgrow(tabPaneInfo, Priority.ALWAYS);
    }
}
