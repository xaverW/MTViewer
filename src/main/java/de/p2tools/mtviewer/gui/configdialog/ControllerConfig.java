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

package de.p2tools.mtviewer.gui.configdialog;

import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.gui.configdialog.configpanes.*;
import de.p2tools.p2lib.dialogs.accordion.P2AccordionPane;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collection;

public class ControllerConfig extends P2AccordionPane {

    private final ProgData progData;
    private final Stage stage;

    private PaneLog paneLog;
    private PaneColor paneColor;
    private PaneShortcut paneShortcut;
    private PaneGeo paneGeo;
    private PaneKeySize paneKeySize;
    private PaneProg paneProg;
    private PaneConfig paneConfig;
    private PaneUpdate paneUpdate;

    public ControllerConfig(Stage stage) {
        super(ProgConfig.CONFIG_DIALOG_ACCORDION, ProgConfig.SYSTEM_CONFIG_DIALOG_CONFIG);
        this.stage = stage;
        progData = ProgData.getInstance();

        init();
    }

    @Override
    public void close() {
        super.close();
        paneConfig.close();
        paneColor.close();
        paneGeo.close();
        paneKeySize.close();
        paneShortcut.close();
        paneProg.close();
        paneLog.close();
        paneUpdate.close();
    }

    @Override
    public Collection<TitledPane> createPanes() {
        Collection<TitledPane> result = new ArrayList<TitledPane>();
        paneConfig = new PaneConfig(stage);
        paneConfig.make(result);
        paneColor = new PaneColor(stage);
        paneColor.makeColor(result);
        paneGeo = new PaneGeo(stage);
        paneGeo.makeGeo(result);
        paneKeySize = new PaneKeySize(stage, progData);
        paneKeySize.makeStyle(result);
        paneShortcut = new PaneShortcut(stage);
        paneShortcut.makeShortcut(result);
        paneProg = new PaneProg(stage);
        paneProg.make(result);
        paneLog = new PaneLog(stage);
        paneLog.make(result);
        paneUpdate = new PaneUpdate(stage);
        paneUpdate.make(result);
        return result;
    }
}
