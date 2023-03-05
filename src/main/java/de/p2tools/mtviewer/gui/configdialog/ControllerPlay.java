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
import de.p2tools.mtviewer.gui.configdialog.configpanes.PaneEditFilter;
import de.p2tools.mtviewer.gui.configdialog.configpanes.PanePlay;
import de.p2tools.p2lib.dialogs.accordion.PAccordionPane;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collection;

public class ControllerPlay extends PAccordionPane {

    private final Stage stage;

    private PanePlay panePlay;
    private PaneEditFilter paneEditFilter;

    public ControllerPlay(Stage stage) {
        super(ProgConfig.CONFIG_DIALOG_ACCORDION, ProgConfig.SYSTEM_CONFIG_DIALOG_PLAY);
        this.stage = stage;

        init();
    }

    @Override
    public void close() {
        super.close();
        panePlay.close();
        paneEditFilter.close();
    }

    @Override
    public Collection<TitledPane> createPanes() {
        Collection<TitledPane> result = new ArrayList<TitledPane>();
        panePlay = new PanePlay(stage);
        panePlay.make(result);
        paneEditFilter = new PaneEditFilter(stage);
        paneEditFilter.make(result);

        return result;
    }
}
