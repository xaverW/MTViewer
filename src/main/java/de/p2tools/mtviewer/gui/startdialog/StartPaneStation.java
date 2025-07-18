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

package de.p2tools.mtviewer.gui.startdialog;

import de.p2tools.mtviewer.gui.configdialog.configpanes.PStation;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;

public class StartPaneStation {
    private final PStation pStation;

    public StartPaneStation(Stage stage) {
        pStation = new PStation(stage);
    }

    public void close() {
        pStation.close();
    }

    public TitledPane make() {
        return new TitledPane("Sender die nicht interessieren, abschalten", pStation);
    }
}