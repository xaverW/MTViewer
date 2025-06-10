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

package de.p2tools.mtviewer.gui.help.table;

import de.p2tools.mtviewer.controller.config.PEvents;
import de.p2tools.mtviewer.controller.config.ProgColorList;
import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.p2lib.guitools.P2TableFactory;
import de.p2tools.p2lib.mediathek.film.FilmSize;
import de.p2tools.p2lib.mediathek.filmdata.FilmData;
import de.p2tools.p2lib.p2event.P2Listener;
import de.p2tools.p2lib.tools.date.P2Date;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class TableFilm extends PTable<FilmData> {

    public TableFilm(Table.TABLE_ENUM table_enum, ProgData progData) {
        super(table_enum);
        this.table_enum = table_enum;
        initColumn();
    }

    public Table.TABLE_ENUM getETable() {
        return table_enum;
    }

    public void resetTable() {
        initColumn();
        Table.resetTable(this);
    }

    private void refreshTable() {
        P2TableFactory.refreshTable(this);
    }

    private void initColumn() {
        getColumns().clear();

        setTableMenuButtonVisible(true);
        setEditable(false);
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // bei Farbänderung der Schriftfarbe klappt es damit besser: Table.refresh_table(table)
        ProgConfig.SYSTEM_THEME_CHANGED.addListener((u, o, n) -> P2TableFactory.refreshTable(this));
        ProgColorList.FILM_LIVESTREAM.colorProperty().addListener((a, b, c) -> P2TableFactory.refreshTable(this));
        ProgColorList.FILM_GEOBLOCK.colorProperty().addListener((a, b, c) -> P2TableFactory.refreshTable(this));
        ProgColorList.FILM_NEW.colorProperty().addListener((a, b, c) -> P2TableFactory.refreshTable(this));
        ProgConfig.SYSTEM_SMALL_ROW_TABLE_FILM.addListener((observableValue, s, t1) -> refresh());
        ProgConfig.FILM_GUI_SHOW_TABLE_TOOL_TIP.addListener((observableValue, s, t1) -> refresh());
        ProgData.getInstance().pEventHandler.addListener(new P2Listener(PEvents.EVENT_REFRESH_TABLE) {
            @Override
            public void pingGui() {
                refreshTable();
            }
        });
        final TableColumn<FilmData, Boolean> markColumn = new TableColumn<>("Film");
        markColumn.setCellValueFactory(new PropertyValueFactory<>("mark"));
        markColumn.getStyleClass().add("alignCenter");
        TableFilmFactory.columnFactoryBoolean(markColumn);

        final TableColumn<FilmData, Integer> nrColumn = new TableColumn<>("Nr");
        nrColumn.setCellValueFactory(new PropertyValueFactory<>("no"));
        nrColumn.getStyleClass().add("alignCenterRightPadding_10");
        TableFilmFactory.columnFactoryInteger(nrColumn);

        final TableColumn<FilmData, String> senderColumn = new TableColumn<>("Sender");
        senderColumn.setCellValueFactory(new PropertyValueFactory<>("channel"));
        senderColumn.getStyleClass().add("alignCenter");
        TableFilmFactory.columnFactoryString(senderColumn);

        final TableColumn<FilmData, String> themeColumn = new TableColumn<>("Thema");
        themeColumn.setCellValueFactory(new PropertyValueFactory<>("theme"));
        themeColumn.getStyleClass().add("alignCenterLeft");
        TableFilmFactory.columnFactoryString(themeColumn);

        final TableColumn<FilmData, String> titleColumn = new TableColumn<>("Titel");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.getStyleClass().add("alignCenterLeft");
        TableFilmFactory.columnFactoryString(titleColumn);

        final TableColumn<FilmData, String> startColumn = new TableColumn<>("");
        startColumn.getStyleClass().add("alignCenter");
        TableFilmFactory.columnFactoryButton(startColumn);

        final TableColumn<FilmData, P2Date> datumColumn = new TableColumn<>("Datum");
        datumColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        datumColumn.getStyleClass().add("alignCenter");
        TableFilmFactory.columnFactoryP2Date(datumColumn);

        final TableColumn<FilmData, String> timeColumn = new TableColumn<>("Zeit");
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        timeColumn.getStyleClass().add("alignCenter");
        TableFilmFactory.columnFactoryString(timeColumn);

        final TableColumn<FilmData, Integer> durationColumn = new TableColumn<>("Dauer [min]");
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("durationMinute"));
        durationColumn.getStyleClass().add("alignCenterRightPadding_25");
        TableFilmFactory.columnFactoryInteger(durationColumn);

        final TableColumn<FilmData, FilmSize> sizeColumn = new TableColumn<>("Größe [MB]");
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("filmSize"));
        sizeColumn.getStyleClass().add("alignCenterRightPadding_25");
        TableFilmFactory.columnFactoryFilmSize(sizeColumn);

        final TableColumn<FilmData, Boolean> hdColumn = new TableColumn<>("HD");
        hdColumn.setCellValueFactory(new PropertyValueFactory<>("hd"));
        hdColumn.getStyleClass().add("alignCenter");
        TableFilmFactory.columnFactoryBoolean(hdColumn);

        final TableColumn<FilmData, Boolean> utColumn = new TableColumn<>("UT");
        utColumn.setCellValueFactory(new PropertyValueFactory<>("ut"));
        utColumn.getStyleClass().add("alignCenter");
        TableFilmFactory.columnFactoryBoolean(utColumn);

        final TableColumn<FilmData, String> geoColumn = new TableColumn<>("Geo");
        geoColumn.setCellValueFactory(new PropertyValueFactory<>("geo"));
        geoColumn.getStyleClass().add("alignCenter");
        TableFilmFactory.columnFactoryString(geoColumn);

        nrColumn.setPrefWidth(50);
        senderColumn.setPrefWidth(80);
        themeColumn.setPrefWidth(180);
        titleColumn.setPrefWidth(230);

        getColumns().addAll(
                markColumn, nrColumn,
                senderColumn, themeColumn, titleColumn,
                startColumn,
                datumColumn, timeColumn, durationColumn, sizeColumn,
                hdColumn, utColumn,
                geoColumn);
    }
}
