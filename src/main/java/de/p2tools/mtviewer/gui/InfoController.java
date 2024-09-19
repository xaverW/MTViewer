/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
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
import javafx.scene.Node;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class InfoController extends VBox {

    private final TabPane tabPane = new TabPane();
    private PaneFilmInfo paneFilmInfo;
    private PaneDownloadInfo paneDownloadInfo;

    public InfoController() {
        initInfoPane();
    }

    public PaneFilmInfo getPaneFilmInfo() {
        return paneFilmInfo;
    }

    public PaneDownloadInfo getPaneDownloadInfo() {
        return paneDownloadInfo;
    }

    public boolean arePanesShowing() {
        // dann wird wenigsten eins angezeigt
        return !ProgConfig.PANE_FILM_INFO_IS_RIP.get() ||
                !ProgConfig.PANE_DOWNLOAD_INFO_IS_RIP.getValue();
    }

    private void initInfoPane() {
        paneFilmInfo = new PaneFilmInfo();
        paneDownloadInfo = new PaneDownloadInfo();

        if (ProgConfig.PANE_FILM_INFO_IS_RIP.get()) {
            dialogFilmInfo();
        }
        ProgConfig.PANE_FILM_INFO_IS_RIP.addListener((u, o, n) -> {
            if (n) {
                dialogFilmInfo();
            } else {
                ProgConfig.GUI_INFO_IS_SHOWING.set(true);
            }
            setTabs();
        });

        if (ProgConfig.PANE_DOWNLOAD_INFO_IS_RIP.get()) {
            dialogDownloadInfo();
        }
        ProgConfig.PANE_DOWNLOAD_INFO_IS_RIP.addListener((u, o, n) -> {
            if (n) {
                dialogDownloadInfo();
            } else {
                ProgConfig.GUI_INFO_IS_SHOWING.set(true);
            }
            setTabs();
        });

        setTabs();
    }

    private void dialogFilmInfo() {
        new InfoPaneDialog(paneFilmInfo, "Filminfos",
                ProgConfig.PANE_DIALOG_FILM_INFO_SIZE,
                ProgConfig.PANE_FILM_INFO_IS_RIP);
    }

    private void dialogDownloadInfo() {
        new InfoPaneDialog(paneDownloadInfo, "Downloads",
                ProgConfig.PANE_DIALOG_DOWNLOAD_INFO_SIZE,
                ProgConfig.PANE_DOWNLOAD_INFO_IS_RIP);
    }

    private void setTabs() {
        tabPane.getTabs().clear();

        if (!ProgConfig.PANE_FILM_INFO_IS_RIP.get()) {
            tabPane.getTabs().add(
                    InfoPaneFactory.makeTab(paneFilmInfo, "Filminfos",
                            ProgConfig.GUI_INFO_IS_SHOWING, ProgConfig.PANE_FILM_INFO_IS_RIP));
        }
        if (!ProgConfig.PANE_DOWNLOAD_INFO_IS_RIP.get()) {
            tabPane.getTabs().add(
                    InfoPaneFactory.makeTab(paneDownloadInfo, "Downloads",
                            ProgConfig.GUI_INFO_IS_SHOWING, ProgConfig.PANE_DOWNLOAD_INFO_IS_RIP));
        }

        if (tabPane.getTabs().isEmpty()) {
            // keine Tabs

        } else if (tabPane.getTabs().size() == 1) {
            // dann gibts einen Tab
            final Node node = tabPane.getTabs().get(0).getContent();
            tabPane.getTabs().remove(0);
            getChildren().setAll(node);
            VBox.setVgrow(node, Priority.ALWAYS);

        } else {
            // dann gibts mehre Tabs
            getChildren().setAll(tabPane);
            VBox.setVgrow(tabPane, Priority.ALWAYS);
        }
    }
}
