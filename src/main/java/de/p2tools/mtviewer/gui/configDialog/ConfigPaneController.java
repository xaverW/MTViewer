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
import de.p2tools.mtviewer.gui.configDialog.configPanes.*;
import de.p2tools.p2Lib.dialogs.accordion.PAccordionPane;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collection;

public class ConfigPaneController extends PAccordionPane {

    private final ProgData progData;
    private final Stage stage;

    private LogPane logPane;
    private ColorPane colorPane;
    private ShortcutPane shortcutPane;
    private GeoPane geoPane;
    private StylePane stylePane;
    private ProgPane progPane;
    private ConfigPane configPane;
    private UpdatePane updatePane;

    public ConfigPaneController(Stage stage) {
        super(stage, ProgConfig.CONFIG_DIALOG_ACCORDION, ProgConfig.SYSTEM_CONFIG_DIALOG_CONFIG);
        this.stage = stage;
        progData = ProgData.getInstance();

        init();
    }

    @Override
    public void close() {
        super.close();
        configPane.close();
        colorPane.close();
        geoPane.close();
        stylePane.close();
        shortcutPane.close();
        progPane.close();
        logPane.close();
        updatePane.close();
    }

    @Override
    public Collection<TitledPane> createPanes() {
        Collection<TitledPane> result = new ArrayList<TitledPane>();
        configPane = new ConfigPane(stage);
        configPane.make(result);
        colorPane = new ColorPane(stage);
        colorPane.makeColor(result);
        geoPane = new GeoPane(stage);
        geoPane.makeGeo(result);
        stylePane = new StylePane(stage, progData);
        stylePane.makeStyle(result);
        shortcutPane = new ShortcutPane(stage);
        shortcutPane.makeShortcut(result);
        progPane = new ProgPane(stage);
        progPane.make(result);
        logPane = new LogPane(stage);
        logPane.make(result);
        updatePane = new UpdatePane(stage);
        updatePane.make(result);
        return result;
    }
}
