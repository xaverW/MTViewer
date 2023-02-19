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

package de.p2tools.mtviewer.gui.configDialog;

import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.gui.configDialog.downloadPanes.PaneDownload;
import de.p2tools.mtviewer.gui.configDialog.downloadPanes.PaneReplace;
import de.p2tools.mtviewer.gui.configDialog.downloadPanes.PaneSet;
import de.p2tools.p2Lib.dialogs.accordion.PAccordionPane;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collection;

public class ControllerDownload extends PAccordionPane {
    private final ProgData progData;
    private final Stage stage;
    private PaneDownload paneDownload;
    private PaneSet paneSet;
    private PaneReplace paneReplace;

    public ControllerDownload(Stage stage) {
        super(stage, ProgConfig.CONFIG_DIALOG_ACCORDION, ProgConfig.SYSTEM_CONFIG_DIALOG_DOWNLOAD);
        this.stage = stage;
        progData = ProgData.getInstance();
        init();
    }

    @Override
    public void close() {
        super.close();
        paneDownload.close();
        paneSet.close();
        paneReplace.close();
    }

    @Override
    public Collection<TitledPane> createPanes() {
        Collection<TitledPane> titledPanes = new ArrayList<>();
        paneDownload = new PaneDownload(stage);
        paneDownload.makePane(titledPanes);
        paneSet = new PaneSet(stage);
        paneSet.makePane(titledPanes);
        paneReplace = new PaneReplace(stage);
        paneReplace.makePane(titledPanes);
        return titledPanes;
    }
}