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
import de.p2tools.mtviewer.controller.data.ProgIcons;
import de.p2tools.mtviewer.controller.film.LoadFilmFactory;
import de.p2tools.mtviewer.gui.tools.Listener;
import de.p2tools.p2Lib.dialogs.dialog.PDialogExtra;
import de.p2tools.p2Lib.mtFilm.film.FilmFactory;
import de.p2tools.p2Lib.mtFilm.loadFilmlist.ListenerFilmlistLoadEvent;
import de.p2tools.p2Lib.mtFilm.loadFilmlist.ListenerLoadFilmlist;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;


public class ConfigDialogController extends PDialogExtra {

    private final ProgData progData;
    IntegerProperty propSelectedTab = ProgConfig.SYSTEM_CONFIG_DIALOG_TAB;
    ConfigPaneController configPaneController;
    PlayPaneController playPaneController;
    FilmPaneController filmPaneController;
    DownloadPaneController downloadPaneController;
    private ListenerLoadFilmlist listener;
    private TabPane tabPane = new TabPane();
    private Button btnOk = new Button("_Ok");
    private String geo = ProgConfig.SYSTEM_GEO_HOME_PLACE.get();
    private BooleanProperty diacriticChanged = new SimpleBooleanProperty();

    public ConfigDialogController(ProgData progData) {
        super(progData.primaryStage, ProgConfig.CONFIG_DIALOG_SIZE, "Einstellungen",
                true, false, DECO.NONE, true);

        this.progData = progData;
        this.diacriticChanged.setValue(ProgConfig.SYSTEM_REMOVE_DIACRITICS.getValue());
        init(false);
    }

    @Override
    public void make() {
        setMaskerPane();
        progData.maskerPane.visibleProperty().addListener((u, o, n) -> {
            setMaskerPane();
        });
        Button btnStop = getMaskerPane().getButton();
        getMaskerPane().setButtonText("");
        btnStop.setGraphic(ProgIcons.Icons.ICON_BUTTON_STOP.getImageView());
        btnStop.setOnAction(a -> LoadFilmFactory.getInstance().loadFilmlist.setStop(true));
        listener = new ListenerLoadFilmlist() {
            @Override
            public void start(ListenerFilmlistLoadEvent event) {
                if (event.progress == ListenerLoadFilmlist.PROGRESS_INDETERMINATE) {
                    // ist dann die gespeicherte Filmliste
                    getMaskerPane().setMaskerVisible(true, false);
                } else {
                    getMaskerPane().setMaskerVisible(true, true);
                }
                getMaskerPane().setMaskerProgress(event.progress, event.text);
            }

            @Override
            public void progress(ListenerFilmlistLoadEvent event) {
                getMaskerPane().setMaskerProgress(event.progress, event.text);
            }

            @Override
            public void loaded(ListenerFilmlistLoadEvent event) {
                getMaskerPane().setMaskerVisible(true, false);
                getMaskerPane().setMaskerProgress(ListenerLoadFilmlist.PROGRESS_INDETERMINATE, "Filmliste verarbeiten");
            }

            @Override
            public void finished(ListenerFilmlistLoadEvent event) {
                getMaskerPane().setMaskerVisible(false);
            }
        };
        LoadFilmFactory.getInstance().loadFilmlist.addListenerLoadFilmlist(listener);

        VBox.setVgrow(tabPane, Priority.ALWAYS);
        getvBoxCont().getChildren().add(tabPane);
        getvBoxCont().setPadding(new Insets(0));

        addOkButton(btnOk);
        btnOk.setOnAction(a -> close());

        ProgConfig.SYSTEM_THEME_CHANGED.addListener((u, o, n) -> updateCss());
        initPanel();
    }

    @Override
    public void close() {
        if (!geo.equals(ProgConfig.SYSTEM_GEO_HOME_PLACE.get())) {
            // dann hat sich der Geo-Standort ge??ndert
            progData.filmlist.markGeoBlocked();
        }

        if (diacriticChanged.getValue() != ProgConfig.SYSTEM_REMOVE_DIACRITICS.getValue()) {
            //dann hats sich ge??ndert!!!
            ProgData.getInstance().maskerPane.setMaskerVisible(true, false);
            ProgData.getInstance().maskerPane.setMaskerText("");
            Thread th = new Thread(() -> {
                FilmFactory.setDiacritic(progData.filmlist, ProgConfig.SYSTEM_REMOVE_DIACRITICS.getValue());
                Listener.notify(Listener.EVENT_DIACRITIC_CHANGED, ConfigDialogController.class.getSimpleName());
                ProgData.getInstance().maskerPane.setMaskerVisible(false);
            });
            th.setName("generateDiacritic");
            th.start();
        }

        configPaneController.close();
        playPaneController.close();
        filmPaneController.close();
        downloadPaneController.close();

        LoadFilmFactory.getInstance().loadFilmlist.removeListenerLoadFilmlist(listener);
        Listener.notify(Listener.EVEMT_SETDATA_CHANGED, ConfigDialogController.class.getSimpleName());
        super.close();
    }

    private void setMaskerPane() {
        if (progData.maskerPane.isVisible()) {
            this.setMaskerVisible(true, true);
        } else {
            this.setMaskerVisible(false);
        }
    }

    private void initPanel() {
        try {
            configPaneController = new ConfigPaneController(getStage());
            Tab tab = new Tab("Allgemein");
            tab.setClosable(false);
            tab.setContent(configPaneController);
            tabPane.getTabs().add(tab);

            playPaneController = new PlayPaneController(getStage());
            tab = new Tab("Filme");
            tab.setClosable(false);
            tab.setContent(playPaneController);
            tabPane.getTabs().add(tab);

            filmPaneController = new FilmPaneController(getStage());
            tab = new Tab("Filmliste laden");
            tab.setClosable(false);
            tab.setContent(filmPaneController);
            tabPane.getTabs().add(tab);

            downloadPaneController = new DownloadPaneController(getStage());
            tab = new Tab("Download");
            tab.setClosable(false);
            tab.setContent(downloadPaneController);
            tabPane.getTabs().add(tab);

            tabPane.getSelectionModel().select(propSelectedTab.get());
            tabPane.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                // readOnlyBinding!!
                propSelectedTab.setValue(newValue);
            });

        } catch (final Exception ex) {
            PLog.errorLog(784459510, ex);
        }
    }
}
