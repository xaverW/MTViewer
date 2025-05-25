package de.p2tools.mtviewer.gui.help.table;

import de.p2tools.mtviewer.controller.FilmTools;
import de.p2tools.mtviewer.controller.config.ProgColorList;
import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.data.ProgIcons;
import de.p2tools.p2lib.mtfilm.film.FilmData;
import de.p2tools.p2lib.mtfilm.film.FilmSize;
import de.p2tools.p2lib.tools.date.P2Date;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;

public class TableFilmFactory {
    private TableFilmFactory() {

    }

    public static void columnFactoryString(TableColumn<FilmData, String> column) {
        column.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                    return;
                }

                setText(item);
                FilmData film = getTableView().getItems().get(getIndex());
                set(film, this);
            }
        });
    }

    public static void columnFactoryInteger(TableColumn<FilmData, Integer> column) {
        column.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                if (item == 0) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setGraphic(null);
                    setText(item + "");
                }

                FilmData film = getTableView().getItems().get(getIndex());
                set(film, this);
            }
        });
    }

    public static void columnFactoryBoolean(TableColumn<FilmData, Boolean> column) {
        column.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                setAlignment(Pos.CENTER);
                CheckBox box = new CheckBox();
                box.setMaxHeight(6);
                box.setMinHeight(6);
                box.setPrefSize(6, 6);
                box.setDisable(true);
                box.getStyleClass().add("checkbox-table");
                box.setSelected(item);
                setGraphic(box);

                FilmData film = getTableView().getItems().get(getIndex());
                set(film, this);
            }
        });
    }

    public static void columnFactoryP2Date(TableColumn<FilmData, P2Date> column) {
        column.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(P2Date item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                    return;
                }

                setText(item.toString());
                FilmData film = getTableView().getItems().get(getIndex());
                set(film, this);
            }
        });
    }

    public static void columnFactoryFilmSize(TableColumn<FilmData, FilmSize> column) {
        column.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(FilmSize item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                    return;
                }

                setText(item.toString());
                FilmData film = getTableView().getItems().get(getIndex());
                set(film, this);
            }
        });
    }

    public static void columnFactoryButton(TableColumn<FilmData, String> column) {
        column.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                final HBox hbox = new HBox();
                hbox.setSpacing(4);
                hbox.setAlignment(Pos.CENTER);
                hbox.setPadding(new Insets(0, 2, 0, 2));

                final Button btnPlay;
                final Button btnSave;

                btnPlay = new Button("");
                btnPlay.getStyleClass().addAll("btnFunction", "btnFuncTable");
                btnPlay.setGraphic(ProgIcons.IMAGE_TABLE_FILM_PLAY.getImageView());

                btnSave = new Button("");
                btnSave.getStyleClass().addAll("btnFunction", "btnFuncTable");
                btnSave.setGraphic(ProgIcons.IMAGE_TABLE_FILM_SAVE.getImageView());

                btnPlay.setOnAction(e -> {
                    getTableView().getSelectionModel().clearSelection();
                    getTableView().getSelectionModel().select(getIndex());

                    FilmData film = getTableView().getItems().get(getIndex());
                    FilmTools.playFilm(film);

                    getTableView().refresh();
                    getTableView().requestFocus();
                });
                btnSave.setOnAction(e -> {
                    getTableView().getSelectionModel().clearSelection();
                    getTableView().getSelectionModel().select(getIndex());

                    FilmData film = getTableView().getItems().get(getIndex());
                    FilmTools.saveFilm(film);

                    getTableView().refresh();
                    getTableView().requestFocus();
                });
                hbox.getChildren().addAll(btnPlay, btnSave);
                setGraphic(hbox);

                if (ProgConfig.SYSTEM_SMALL_ROW_TABLE_FILM.get()) {
                    btnPlay.setMaxHeight(18);
                    btnPlay.setMinHeight(18);
                    btnSave.setMaxHeight(18);
                    btnSave.setMinHeight(18);
                }

                FilmData film = getTableView().getItems().get(getIndex());
                set(film, this);
            }
        });
    }

    private static void set(FilmData film, TableCell tableCell) {
        if (film.isLive()) {
            // livestream
            tableCell.setStyle(ProgColorList.FILM_LIVESTREAM.getCssFontBold());

        } else if (ProgConfig.SYSTEM_MARK_GEO.get() && film.isGeoBlocked()) {
            // geoGeblockt
            tableCell.setStyle(ProgColorList.FILM_GEOBLOCK.getCssFontBold());

        } else if (film.isNewFilm()) {
            // neuer Film
            tableCell.setStyle(ProgColorList.FILM_NEW.getCssFont());

        } else {
            tableCell.setStyle("");
        }
    }
}
