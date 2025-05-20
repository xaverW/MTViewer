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
import de.p2tools.p2lib.guitools.pclosepane.P2ClosePaneController;
import de.p2tools.p2lib.guitools.pclosepane.P2ClosePaneDto;
import de.p2tools.p2lib.guitools.pclosepane.P2ClosePaneFactory;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;

import java.util.ArrayList;

public class FilmGuiPack extends HBox {

    private final SplitPane splitPaneFilter = new SplitPane();
    private final SplitPane splitPaneInfo = new SplitPane();

    private final FilmGuiController filmGuiController;
    private final PaneFilmFilter paneFilmFilter;
    private final PaneFilmInfo paneFilmInfo;
    private final PaneDownloadInfo paneDownloadInfo;

    private final P2ClosePaneController CloseControllerFilter;
    private final P2ClosePaneController closeControllerInfo;
    private final BooleanProperty boundFilter = new SimpleBooleanProperty(false);
    private final BooleanProperty boundInfo = new SimpleBooleanProperty(false);

    private final ProgData progData;

    public FilmGuiPack() {
        progData = ProgData.getInstance();

        this.paneFilmFilter = new PaneFilmFilter();
        this.filmGuiController = new FilmGuiController();
        this.paneFilmInfo = new PaneFilmInfo();
        this.paneDownloadInfo = new PaneDownloadInfo();


        P2ClosePaneDto infoDto = new P2ClosePaneDto(paneFilmFilter,
                ProgConfig.FILTER__PANE_FILTER_IS_RIP,
                ProgConfig.FILTER__PANE_FILTER_DIALOG_SIZE, new SimpleBooleanProperty(true),
                "Filter", "Filter", true,
                progData.maskerPane.getVisibleProperty());
        CloseControllerFilter = new P2ClosePaneController(infoDto, ProgConfig.FILTER__IS_SHOWING);

        ArrayList<P2ClosePaneDto> list = new ArrayList<>();
        infoDto = new P2ClosePaneDto(paneFilmInfo,
                ProgConfig.INFO__PANE_INFO_IS_RIP,
                ProgConfig.INFO__PANE_INFO_DIALOG_SIZE, new SimpleBooleanProperty(true),
                "Beschreibung", "Beschreibung", false,
                progData.maskerPane.getVisibleProperty());
        list.add(infoDto);

        infoDto = new P2ClosePaneDto(paneDownloadInfo,
                ProgConfig.INFO__PANE_DOWNLOAD_IS_RIP,
                ProgConfig.INFO__PANE_DOWNLOAD_DIALOG_SIZE, new SimpleBooleanProperty(true),
                "Download", "Download", false,
                progData.maskerPane.getVisibleProperty());
        list.add(infoDto);
        closeControllerInfo = new P2ClosePaneController(list, ProgConfig.INFO__IS_SHOWING);

        ProgConfig.FILTER__IS_SHOWING.addListener((observable, oldValue, newValue) -> setSplitFilter());
        ProgConfig.FILTER__PANE_FILTER_IS_RIP.addListener((observable, oldValue, newValue) -> setSplitFilter());

        ProgConfig.INFO__IS_SHOWING.addListener((observable, oldValue, newValue) -> setSplitInfo());
        ProgConfig.INFO__PANE_INFO_IS_RIP.addListener((observable, oldValue, newValue) -> setSplitInfo());
        ProgConfig.INFO__PANE_DOWNLOAD_IS_RIP.addListener((observable, oldValue, newValue) -> setSplitInfo());

        progData.filmGuiPack = this;
        pack();
    }

    public void saveTable() {
        filmGuiController.saveTable();
        paneDownloadInfo.saveTable();
    }

    public FilmGuiController getFilmGuiController() {
        return filmGuiController;
    }

    public PaneFilmFilter getFilmFilterController() {
        return paneFilmFilter;
    }

    public PaneFilmInfo getPaneFilmInfo() {
        return paneFilmInfo;
    }

    public PaneDownloadInfo getPaneDownloadInfo() {
        return paneDownloadInfo;
    }

    public void setInfos() {
        ProgConfig.INFO__IS_SHOWING.setValue(!ProgConfig.INFO__IS_SHOWING.getValue());
    }

    public void setFilter() {
        ProgConfig.FILTER__IS_SHOWING.setValue(!ProgConfig.FILTER__IS_SHOWING.getValue());
    }

    private void pack() {
        //Filter
        SplitPane.setResizableWithParent(CloseControllerFilter, false);
        ProgConfig.FILTER__IS_SHOWING.addListener((observable, oldValue, newValue) -> setSplitFilter());
        setSplitFilter();

        //Info
        splitPaneInfo.setOrientation(Orientation.VERTICAL);
        SplitPane.setResizableWithParent(filmGuiController, false);
        ProgConfig.INFO__IS_SHOWING.addListener((observable, oldValue, newValue) -> setSplitInfo());
        setSplitInfo();

        getChildren().addAll(splitPaneFilter);
    }

    private void setSplitFilter() {
        P2ClosePaneFactory.setSplit(boundFilter, splitPaneFilter,
                CloseControllerFilter, true, splitPaneInfo,
                ProgConfig.FILTER__DIVIDER, ProgConfig.FILTER__IS_SHOWING);
    }

    private void setSplitInfo() {
        P2ClosePaneFactory.setSplit(boundInfo, splitPaneInfo,
                closeControllerInfo, false, filmGuiController,
                ProgConfig.INFO__DIVIDER, ProgConfig.INFO__IS_SHOWING);
    }
}
