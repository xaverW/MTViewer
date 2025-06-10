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
import de.p2tools.mtviewer.controller.data.download.DownloadData;
import de.p2tools.p2lib.guitools.P2TableFactory;
import de.p2tools.p2lib.guitools.ptable.P2CellCheckBox;
import de.p2tools.p2lib.mediathek.download.DownloadSize;
import de.p2tools.p2lib.p2event.P2Listener;
import de.p2tools.p2lib.tools.GermanStringIntSorter;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.Comparator;

public class TableDownload extends PTable<DownloadData> {

    public TableDownload(Table.TABLE_ENUM table_enum) {
        super(table_enum);
        this.table_enum = table_enum;
        initFileRunnerColumn();
    }

    @Override
    public Table.TABLE_ENUM getETable() {
        return table_enum;
    }

    public void resetTable() {
        initFileRunnerColumn();
        Table.resetTable(this);
    }

    private void refreshTable() {
        P2TableFactory.refreshTable(this);
    }

    private void initFileRunnerColumn() {
        getColumns().clear();

        setTableMenuButtonVisible(true);
        setEditable(false);
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        final Comparator<String> sorter = GermanStringIntSorter.getInstance();
        ProgColorList.FILM_GEOBLOCK.colorProperty().addListener((a, b, c) -> refresh());
        ProgColorList.DOWNLOAD_WAIT.colorProperty().addListener((a, b, c) -> refresh());
        ProgColorList.DOWNLOAD_RUN.colorProperty().addListener((a, b, c) -> refresh());
        ProgColorList.DOWNLOAD_FINISHED.colorProperty().addListener((a, b, c) -> refresh());
        ProgColorList.DOWNLOAD_ERROR.colorProperty().addListener((a, b, c) -> refresh());
        ProgConfig.SYSTEM_SMALL_ROW_TABLE_DOWNLOAD.addListener((observableValue, s, t1) -> P2TableFactory.refreshTable(this));
        ProgConfig.DOWNLOAD_GUI_SHOW_TABLE_TOOL_TIP.addListener((observableValue, s, t1) -> P2TableFactory.refreshTable(this));
        ProgData.getInstance().pEventHandler.addListener(new P2Listener(PEvents.EVENT_REFRESH_TABLE) {
            @Override
            public void pingGui() {
                refreshTable();
            }
        });

        final TableColumn<DownloadData, Integer> nrColumn = new TableColumn<>("Nr");
        nrColumn.setCellValueFactory(new PropertyValueFactory<>("no"));
        nrColumn.setCellFactory(new CellNo<>().cellFactory);
        nrColumn.getStyleClass().add("alignCenterRightPadding_10");

        final TableColumn<DownloadData, Integer> filmNrColumn = new TableColumn<>("Filmnr");
        filmNrColumn.setCellValueFactory(new PropertyValueFactory<>("filmNr"));
        filmNrColumn.setCellFactory(new CellNo<>().cellFactory);
        filmNrColumn.getStyleClass().add("alignCenterRightPadding_10");

        final TableColumn<DownloadData, String> senderColumn = new TableColumn<>("Sender");
        senderColumn.setCellValueFactory(new PropertyValueFactory<>("channel"));
        senderColumn.getStyleClass().add("alignCenter");

        final TableColumn<DownloadData, String> themeColumn = new TableColumn<>("Thema");
        themeColumn.setCellValueFactory(new PropertyValueFactory<>("theme"));
        themeColumn.getStyleClass().add("alignCenterLeft");
        themeColumn.setComparator(sorter);

        final TableColumn<DownloadData, String> titleColumn = new TableColumn<>("Titel");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.getStyleClass().add("alignCenterLeft");
        titleColumn.setComparator(sorter);

        // die zwei Spalten mit eigenen propertys
        final TableColumn<DownloadData, Integer> startColumn = new TableColumn<>("");
        startColumn.setCellValueFactory(new PropertyValueFactory<>("guiState"));
        startColumn.setCellFactory(new CellStartDownload<>().cellFactory);
        startColumn.getStyleClass().add("alignCenter");

        final TableColumn<DownloadData, Double> progressColumn = new TableColumn<>("Fortschritt"); //müssen sich unterscheiden!!
        progressColumn.setCellValueFactory(new PropertyValueFactory<>("guiProgress"));
        progressColumn.setCellFactory(new CellProgress<>().cellFactory);
        progressColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<DownloadData, Integer> remainingColumn = new TableColumn<>("Restzeit");
        remainingColumn.setCellValueFactory(new PropertyValueFactory<>("remaining"));
        remainingColumn.getStyleClass().add("alignCenterRightPadding_25");

        final TableColumn<DownloadData, Integer> speedColumn = new TableColumn<>("Geschwindigkeit");
        speedColumn.setCellValueFactory(new PropertyValueFactory<>("bandwidth"));
        speedColumn.getStyleClass().add("alignCenterRightPadding_25");

        final TableColumn<DownloadData, DownloadSize> sizeColumn = new TableColumn<>("Größe [MB]");
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("downloadSize"));
        sizeColumn.getStyleClass().add("alignCenterRightPadding_25");

        final TableColumn<DownloadData, LocalDate> datumColumn = new TableColumn<>("Datum");
        datumColumn.setCellValueFactory(new PropertyValueFactory<>("filmDate"));
        datumColumn.setCellFactory(new CellLocalDate().cellFactory);
        datumColumn.getStyleClass().add("alignCenter");

        final TableColumn<DownloadData, String> timeColumn = new TableColumn<>("Zeit");
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("filmTime"));
        timeColumn.getStyleClass().add("alignCenter");

        final TableColumn<DownloadData, Integer> durationColumn = new TableColumn<>("Dauer [min]");
        durationColumn.setCellFactory(new CellDuration<DownloadData, Integer>().cellFactory);
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("durationMinute"));
        durationColumn.getStyleClass().add("alignCenterRightPadding_25");

        final TableColumn<DownloadData, Boolean> hdColumn = new TableColumn<>("HD");
        hdColumn.setCellValueFactory(new PropertyValueFactory<>("hd"));
        hdColumn.setCellFactory(new P2CellCheckBox().cellFactory);
        hdColumn.getStyleClass().add("alignCenter");

        final TableColumn<DownloadData, Boolean> utColumn = new TableColumn<>("UT");
        utColumn.setCellValueFactory(new PropertyValueFactory<>("ut"));
        utColumn.setCellFactory(new P2CellCheckBox().cellFactory);
        utColumn.getStyleClass().add("alignCenter");

        final TableColumn<DownloadData, Boolean> geoColumn = new TableColumn<>("Geo");
        geoColumn.setCellValueFactory(new PropertyValueFactory<>("geoBlocked"));
        geoColumn.setCellFactory(new P2CellCheckBox().cellFactory);
        geoColumn.getStyleClass().add("alignCenter");

        final TableColumn<DownloadData, String> urlColumn = new TableColumn<>("URL");
        urlColumn.setCellValueFactory(new PropertyValueFactory<>("url"));
        urlColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<DownloadData, String> fileNameColumn = new TableColumn<>("Dateiname");
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("destFileName"));
        fileNameColumn.getStyleClass().add("alignCenterLeft");
        fileNameColumn.setComparator(sorter);

        final TableColumn<DownloadData, String> pathColumn = new TableColumn<>("Pfad");
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("destPath"));
        pathColumn.getStyleClass().add("alignCenterLeft");
        pathColumn.setComparator(sorter);

        nrColumn.setPrefWidth(50);
        filmNrColumn.setPrefWidth(70);
        senderColumn.setPrefWidth(80);
        themeColumn.setPrefWidth(180);
        titleColumn.setPrefWidth(230);

        getColumns().addAll(
                nrColumn, filmNrColumn,
                senderColumn, themeColumn, titleColumn, startColumn,
                progressColumn, remainingColumn, speedColumn, sizeColumn,
                datumColumn, timeColumn, durationColumn,
                hdColumn, utColumn, geoColumn, urlColumn, fileNameColumn, pathColumn);
    }
}
