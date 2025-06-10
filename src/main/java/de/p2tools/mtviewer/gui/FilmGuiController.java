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
import de.p2tools.mtviewer.controller.config.PEvents;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.gui.dialog.FilmInfoDialogController;
import de.p2tools.mtviewer.gui.help.table.Table;
import de.p2tools.mtviewer.gui.help.table.TableFilm;
import de.p2tools.mtviewer.gui.help.table.TableRowFilm;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.guitools.P2TableFactory;
import de.p2tools.p2lib.mediathek.filmdata.FilmData;
import de.p2tools.p2lib.p2event.P2Listener;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.application.Platform;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Optional;

public class FilmGuiController extends VBox {

    private final ScrollPane scrollPane = new ScrollPane();
    private final TableFilm tableView;
    private final ProgData progData;
    private final SortedList sortedList;
    private final KeyCombination STRG_A = new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_ANY);

    public FilmGuiController() {
        progData = ProgData.getInstance();

        sortedList = progData.filmlistUsed.getSortedList();
        tableView = new TableFilm(Table.TABLE_ENUM.FILM, progData);

        scrollPane.setContent(tableView);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        getChildren().addAll(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

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
    }

    public void refreshTable() {
        P2TableFactory.refreshTable(tableView);
    }

    public ArrayList<FilmData> getSelList() {
        final ArrayList<FilmData> ret = new ArrayList<>();
        ret.addAll(tableView.getSelectionModel().getSelectedItems());
        if (ret.isEmpty()) {
            P2Alert.showInfoNoSelection();
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
                P2Alert.showInfoNoSelection();
            }
            return Optional.empty();
        }
    }

    private void initListener() {
        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_GUI_HISTORY_CHANGED) {
            @Override
            public void pingGui() {
                P2TableFactory.refreshTable(tableView);
            }
        });
    }

    private void initTable() {
        Table.setTable(tableView);
        tableView.setItems(sortedList);
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setRowFactory(tv -> {
            TableRowFilm<FilmData> row = new TableRowFilm<>();

            row.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                    FilmInfoDialogController.getInstanceAndShow().showFilmInfo();
                }
            });

            row.hoverProperty().addListener((observable) -> {
                final FilmData filmData = row.getItem();
                if (row.isHover() && filmData != null) { // null bei den leeren Zeilen unterhalb
                    setFilmInfos(filmData);
                } else if (filmData == null) {
                    setFilmInfos(tableView.getSelectionModel().getSelectedItem());
                }
            });
            return row;
        });
        tableView.hoverProperty().addListener((o) -> {
            if (!tableView.isHover()) {
                setFilmInfos(tableView.getSelectionModel().getSelectedItem());
            }
        });
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                Platform.runLater(this::setFilmInfos));
        tableView.setOnMousePressed(m -> {
            if (m.getButton().equals(MouseButton.SECONDARY)) {
                final Optional<FilmData> optionalFilm = getSel(false);
                FilmData film;
                film = optionalFilm.orElse(null);
                ContextMenu contextMenu = new FilmTableContextMenu(progData, this, tableView).getContextMenu(film);
                tableView.setContextMenu(contextMenu);
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

            if (STRG_A.match(event) && tableView.getItems().size() > 3_000) {
                //macht eingentlich keine Sinn???
                P2Log.sysLog("STRG-A: lange Liste -> verhindern");
                event.consume();
            }
        });
    }

    private void setFilmInfos() {
        FilmData film = tableView.getSelectionModel().getSelectedItem();
        progData.filmGuiPack.getPaneFilmInfo().setFilm(film);
        FilmInfoDialogController.getInstance().setFilm(film);
    }

    private void setFilmInfos(FilmData film) {
        progData.filmGuiPack.getPaneFilmInfo().setFilm(film);
        FilmInfoDialogController.getInstance().setFilm(film);
    }
}
