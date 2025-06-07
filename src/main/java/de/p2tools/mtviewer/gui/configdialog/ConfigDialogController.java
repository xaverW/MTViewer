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

import de.p2tools.mtviewer.controller.config.PEvents;
import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.config.ProgInfos;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.mtfilm.film.FilmFactory;
import de.p2tools.p2lib.mtfilm.readwritefilmlist.P2WriteFilmlistJson;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;


public class ConfigDialogController extends P2DialogExtra {

    private final ProgData progData;
    IntegerProperty propSelectedTab = ProgConfig.SYSTEM_CONFIG_DIALOG_TAB;
    ControllerConfig controllerConfig;
    ControllerPlay controllerPlay;
    ControllerFilm controllerFilm;
    ControllerDownload controllerDownload;
    private TabPane tabPane = new TabPane();
    private Button btnOk = new Button("_Ok");
    private String geo = ProgConfig.SYSTEM_GEO_HOME_PLACE.get();
    private BooleanProperty diacriticChanged = new SimpleBooleanProperty(false);

    public ConfigDialogController(ProgData progData) {
        super(progData.primaryStage, ProgConfig.CONFIG_DIALOG_SIZE, "Einstellungen",
                true, false, DECO.NO_BORDER, true);

        this.progData = progData;
        init(false);
    }

    @Override
    public void make() {
        getMaskerPane().visibleProperty().bind(progData.maskerPane.visibleProperty());

        VBox.setVgrow(tabPane, Priority.ALWAYS);
        getVBoxCont().getChildren().add(tabPane);
        getVBoxCont().setPadding(new Insets(0));

        addOkButton(btnOk);
        btnOk.setOnAction(a -> close());
        initPanel();
    }

    @Override
    public void close() {
        if (!geo.equals(ProgConfig.SYSTEM_GEO_HOME_PLACE.get())) {
            // dann hat sich der Geo-Standort geändert
            progData.filmlist.markGeoBlocked();
        }

        if (diacriticChanged.getValue() && ProgConfig.SYSTEM_REMOVE_DIACRITICS.getValue()) {
            //hat sich geändert UND ist eingeschaltet
            //Diakritika entfernen, macht nur dann Sinn
            //zum Einfügen der Diakritika muss eine neue Filmliste geladen werden
            new Thread(() -> {
                ProgData.getInstance().maskerPane.setMaskerText("Diakritika entfernen");
                ProgData.getInstance().maskerPane.setMaskerVisible(true, true, false);

                // ändern und Liste speichern
                FilmFactory.flattenDiacritic(progData.filmlist);
                FilmFactory.flattenDiacritic(progData.audioList);
                new P2WriteFilmlistJson().write(ProgInfos.getFilmListFile(), progData.filmlist);
                new P2WriteFilmlistJson().write(ProgInfos.getAndMakeAudioListFile().toString(), progData.audioList);

                progData.pEventHandler.notifyListener(PEvents.EVENT_DIACRITIC_CHANGED);
                ProgData.getInstance().maskerPane.switchOffMasker();
            }).start();
        }

        controllerConfig.close();
        controllerPlay.close();
        controllerFilm.close();
        controllerDownload.close();

        progData.pEventHandler.notifyListener(PEvents.EVENT_SETDATA_CHANGED);
        super.close();
    }

    private void initPanel() {
        try {
            controllerConfig = new ControllerConfig(getStage());
            Tab tab = new Tab("Allgemein");
            tab.setClosable(false);
            tab.setContent(controllerConfig);
            tabPane.getTabs().add(tab);

            controllerPlay = new ControllerPlay(getStage());
            tab = new Tab("Filme");
            tab.setClosable(false);
            tab.setContent(controllerPlay);
            tabPane.getTabs().add(tab);

            controllerFilm = new ControllerFilm(getStage(), diacriticChanged);
            tab = new Tab("Filmliste laden");
            tab.setClosable(false);
            tab.setContent(controllerFilm);
            tabPane.getTabs().add(tab);

            controllerDownload = new ControllerDownload(getStage());
            tab = new Tab("Download");
            tab.setClosable(false);
            tab.setContent(controllerDownload);
            tabPane.getTabs().add(tab);

            tabPane.getSelectionModel().select(propSelectedTab.get());
            tabPane.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                // readOnlyBinding!!
                propSelectedTab.setValue(newValue);
            });

        } catch (final Exception ex) {
            P2Log.errorLog(784459510, ex);
        }
    }
}
