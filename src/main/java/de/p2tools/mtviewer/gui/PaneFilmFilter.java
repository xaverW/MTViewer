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
import de.p2tools.mtviewer.controller.data.ProgIcons;
import de.p2tools.mtviewer.gui.help.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2ButtonClearFilterFactory;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.P2MenuButton;
import de.p2tools.p2lib.guitools.prange.P2RangeBox;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import de.p2tools.p2lib.mediathek.filter.FilterCheck;
import de.p2tools.p2lib.mediathek.filter.FilterCheckRegEx;
import de.p2tools.p2lib.tools.duration.P2Duration;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.util.StringConverter;

public class PaneFilmFilter extends VBox {

    private final P2ToggleSwitch tglMediathek = new P2ToggleSwitch("Mediathek");
    private final P2ToggleSwitch tglAudiothek = new P2ToggleSwitch("Audiothek");

    private final P2MenuButton mbChannel;
    private final ComboBox<String> cboTheme = new ComboBox<>();
    private final ComboBox<String> cboTitle = new ComboBox<>();
    private final ComboBox<String> cboSomewhere = new ComboBox<>();
    private final Slider slTimeRange = new Slider();
    private final Label lblTimeRangeValue = new Label();
    private final P2RangeBox slDur = new P2RangeBox("Filmlänge:", true, FilterCheck.FILTER_ALL_OR_MIN,
            FilterCheck.FILTER_DURATION_MAX_MINUTE);

    private final Button btnClearFilter = P2ButtonClearFilterFactory.getPButtonClearSmall();
    private final Button btnGoBack = new Button("");
    private final Button btnGoForward = new Button("");

    private final Button btnFilter1 = new Button("");
    private final Button btnFilter2 = new Button("");
    private final Button btnFilter3 = new Button("");
    private final Button btnFilter4 = new Button("");
    private final Button btnAddFilter1 = new Button("");
    private final Button btnAddFilter2 = new Button("");
    private final Button btnAddFilter3 = new Button("");
    private final Button btnAddFilter4 = new Button("");
    private final ProgData progData;

    public PaneFilmFilter() {
        this.progData = ProgData.getInstance();
        this.mbChannel = new P2MenuButton(progData.actFilmFilterWorker.getActFilterSettings().channelProperty(),
                progData.worker.getAllChannelList());

        setPadding(new Insets(10));
        setSpacing(10);

        // Sender, Thema, ..
        initButton();
        initDaysFilter();
        initDurFilter();
        initStringFilter();
        addFilter();
    }

    private void initButton() {
        btnGoBack.setGraphic(ProgIcons.ICON_BUTTON_BACKWARD.getImageView());
        btnGoBack.setOnAction(a -> progData.actFilmFilterWorker.goBackward());
        btnGoBack.disableProperty().bind(progData.actFilmFilterWorker.backwardPossibleProperty().not());
        btnGoBack.setTooltip(new Tooltip("letzte Filtereinstellung wieder herstellen"));
        btnGoForward.setGraphic(ProgIcons.ICON_BUTTON_FORWARD.getImageView());
        btnGoForward.setOnAction(a -> progData.actFilmFilterWorker.goForward());
        btnGoForward.disableProperty().bind(progData.actFilmFilterWorker.forwardPossibleProperty().not());
        progData.actFilmFilterWorker.forwardPossibleProperty().addListener((v, o, n) -> System.out.println(progData.actFilmFilterWorker.forwardPossibleProperty().getValue().toString()));
        btnGoForward.setTooltip(new Tooltip("letzte Filtereinstellung wieder herstellen"));

        btnClearFilter.setOnAction(a -> {
            P2Duration.onlyPing("Filter löschen");
            progData.actFilmFilterWorker.clearFilter();
        });

        tglMediathek.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_SHOW_MEDIATHEK);
        tglAudiothek.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_SHOW_AUDIOTHEK);

        btnFilter1.setGraphic(ProgIcons.ICON_BUTTON_UP.getImageView());
        btnFilter1.getStyleClass().add("buttonVerySmall");
        btnFilter1.setTooltip(new Tooltip("Gespeicherten Filter setzen"));
        btnFilter1.setOnAction(a -> {
            progData.actFilmFilterWorker.setStoredFilter(1);
        });
        btnFilter2.setGraphic(ProgIcons.ICON_BUTTON_UP.getImageView());
        btnFilter2.getStyleClass().add("buttonVerySmall");
        btnFilter2.setTooltip(new Tooltip("Gespeicherten Filter setzen"));
        btnFilter2.setOnAction(a -> {
            progData.actFilmFilterWorker.setStoredFilter(2);
        });
        btnFilter3.setGraphic(ProgIcons.ICON_BUTTON_UP.getImageView());
        btnFilter3.getStyleClass().add("buttonVerySmall");
        btnFilter3.setTooltip(new Tooltip("Gespeicherten Filter setzen"));
        btnFilter3.setOnAction(a -> {
            progData.actFilmFilterWorker.setStoredFilter(3);
        });
        btnFilter4.setGraphic(ProgIcons.ICON_BUTTON_UP.getImageView());
        btnFilter4.getStyleClass().add("buttonVerySmall");
        btnFilter4.setTooltip(new Tooltip("Gespeicherten Filter setzen"));
        btnFilter4.setOnAction(a -> {
            progData.actFilmFilterWorker.setStoredFilter(4);
        });

        PauseTransition transition = new PauseTransition(Duration.seconds(1.0));
        transition.setOnFinished(event -> {
            btnAddFilter1.getStyleClass().removeAll("animated-button");
            btnAddFilter2.getStyleClass().removeAll("animated-button");
            btnAddFilter3.getStyleClass().removeAll("animated-button");
            btnAddFilter4.getStyleClass().removeAll("animated-button");
        });

        btnAddFilter1.setGraphic(ProgIcons.ICON_BUTTON_REC.getImageView());
        btnAddFilter1.getStyleClass().add("buttonVerySmall");
        btnAddFilter1.setTooltip(new Tooltip("Aktuellen Filter speichern"));
        btnAddFilter1.setOnAction(a -> {
            btnAddFilter1.getStyleClass().add("animated-button");
            transition.playFromStart();
            setEffect(btnAddFilter1);
            progData.actFilmFilterWorker.storeFilter(1);
        });
        btnAddFilter2.setGraphic(ProgIcons.ICON_BUTTON_REC.getImageView());
        btnAddFilter2.getStyleClass().add("buttonVerySmall");
        btnAddFilter2.setTooltip(new Tooltip("Aktuellen Filter speichern"));
        btnAddFilter2.setOnAction(a -> {
            btnAddFilter2.getStyleClass().add("animated-button");
            transition.playFromStart();
            setEffect(btnAddFilter2);
            progData.actFilmFilterWorker.storeFilter(2);
        });
        btnAddFilter3.setGraphic(ProgIcons.ICON_BUTTON_REC.getImageView());
        btnAddFilter3.getStyleClass().add("buttonVerySmall");
        btnAddFilter3.setTooltip(new Tooltip("Aktuellen Filter speichern"));
        btnAddFilter3.setOnAction(a -> {
            btnAddFilter3.getStyleClass().add("animated-button");
            transition.playFromStart();
            setEffect(btnAddFilter3);
            progData.actFilmFilterWorker.storeFilter(3);
        });
        btnAddFilter4.setGraphic(ProgIcons.ICON_BUTTON_REC.getImageView());
        btnAddFilter4.getStyleClass().add("buttonVerySmall");
        btnAddFilter4.setTooltip(new Tooltip("Aktuellen Filter speichern"));
        btnAddFilter4.setOnAction(a -> {
            btnAddFilter4.getStyleClass().add("animated-button");
            transition.playFromStart();
            setEffect(btnAddFilter4);
            progData.actFilmFilterWorker.storeFilter(4);
        });
    }

    private void setEffect(Node target) {
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.DARKBLUE);
        shadow.setSpread(0.1);
        Timeline shadowAnimation = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(shadow.radiusProperty(), 0d)),
                new KeyFrame(Duration.seconds(0.15), new KeyValue(shadow.radiusProperty(), 20d)));
        target.setEffect(shadow);
        shadowAnimation.setOnFinished(evt -> target.setEffect(null));
        shadowAnimation.play();
    }

    private void initDaysFilter() {
        slTimeRange.setMin(FilterCheck.FILTER_ALL_OR_MIN);
        slTimeRange.setMax(FilterCheck.FILTER_TIME_RANGE_MAX_VALUE);
        slTimeRange.setShowTickLabels(true);

        slTimeRange.setMajorTickUnit(10);
        slTimeRange.setBlockIncrement(5);

        slTimeRange.setLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Double x) {
                if (x == FilterCheck.FILTER_ALL_OR_MIN) return "alles";

                return x.intValue() + "";
            }

            @Override
            public Double fromString(String string) {
                return null;
            }
        });

        slTimeRange.setValue(progData.actFilmFilterWorker.getActFilterSettings().getTimeRange());
        setLabelSlider();
        progData.actFilmFilterWorker.getActFilterSettings().timeRangeProperty().addListener(
                l -> slTimeRange.setValue(progData.actFilmFilterWorker.getActFilterSettings().getTimeRange()));

        // kein direktes binding wegen: valueChangingProperty, nur melden wenn "steht"
        slTimeRange.valueProperty().addListener((o, oldV, newV) -> {
            setLabelSlider();
            if (!slTimeRange.isValueChanging()) {
                progData.actFilmFilterWorker.getActFilterSettings().setTimeRange((int) slTimeRange.getValue());
            }
        });

        slTimeRange.valueChangingProperty().addListener((observable, oldvalue, newvalue) -> {
                    if (!newvalue) {
                        progData.actFilmFilterWorker.getActFilterSettings().setTimeRange((int) slTimeRange.getValue());
                    }
                }
        );
    }

    private void initDurFilter() {
        slDur.minValueProperty().bindBidirectional(progData.actFilmFilterWorker.getActFilterSettings().minDurProperty());
        slDur.maxValueProperty().bindBidirectional(progData.actFilmFilterWorker.getActFilterSettings().maxDurProperty());
// todo       slDur.setValuePrefix("");
    }

    private void initStringFilter() {
        //Theme
        addTextFilter(cboTheme, progData.actFilmFilterWorker.getLastThemaTitleFilter(),
                progData.actFilmFilterWorker.getActFilterSettings().themeProperty());

        //Title
        addTextFilter(cboTitle, progData.actFilmFilterWorker.getLastTitleFilter(),
                progData.actFilmFilterWorker.getActFilterSettings().titleProperty());

        //Somewhere
        addTextFilter(cboSomewhere, progData.actFilmFilterWorker.getLastSomewhereFilter(),
                progData.actFilmFilterWorker.getActFilterSettings().somewhereProperty());

        FilterCheckRegEx fTT = new FilterCheckRegEx(cboTheme.getEditor());
        cboTheme.getEditor().textProperty().addListener((observable, oldValue, newValue) -> fTT.checkPattern());
        FilterCheckRegEx fT = new FilterCheckRegEx(cboTitle.getEditor());
        cboTitle.getEditor().textProperty().addListener((observable, oldValue, newValue) -> fT.checkPattern());
        FilterCheckRegEx fS = new FilterCheckRegEx(cboSomewhere.getEditor());
        cboSomewhere.getEditor().textProperty().addListener((observable, oldValue, newValue) -> fS.checkPattern());
    }

    private void addTextFilter(ComboBox<String> cbo, ObservableList<String> items, StringProperty strProp) {
        cbo.setEditable(true);
        cbo.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        cbo.setVisibleRowCount(15);
        cbo.setItems(items);
        cbo.getEditor().setText(strProp.getValue());

        cbo.getEditor().textProperty().addListener((u, o, n) -> {
            if (strProp.getValueSafe().equals(cbo.getEditor().getText())) {
                return;
            }
            strProp.setValue(cbo.getEditor().getText());
        });
        cbo.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                    if (cbo.getSelectionModel().getSelectedIndex() >= 0) {
                        if (ProgConfig.SYSTEM_FILTER_RETURN.getValue()) {
                            //dann wird erst nach "RETURN" gestartet
                            progData.actFilmFilterWorker.getActFilterSettings().reportFilterReturn();
                        }
                    }
                }
        );

        cbo.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                progData.actFilmFilterWorker.getActFilterSettings().reportFilterReturn();
            }
        });
        cbo.getEditor().setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                cbo.getEditor().clear();
            }
        });
        strProp.addListener((u, o, n) -> cbo.valueProperty().setValue(strProp.getValueSafe()));
    }

    private VBox addSlider() {
        VBox vBox;
        vBox = new VBox(2);
        HBox h = new HBox(new Label("Zeitraum:"), P2GuiTools.getHBoxGrower(), lblTimeRangeValue);
        vBox.getChildren().addAll(h, slTimeRange);
        getChildren().addAll(vBox);
        return vBox;
    }

    private void setLabelSlider() {
        final String txtAll = "alles";

        int i = (int) slTimeRange.getValue();
        String tNr = i + "";

        if (i == FilterCheck.FILTER_ALL_OR_MIN) {
            lblTimeRangeValue.setText(txtAll);
        } else {
            lblTimeRangeValue.setText(tNr + (i == 1 ? " Tag" : " Tage"));
        }
    }

    private void addFilter() {
        final Button btnHelpFilter = P2Button.helpButton(progData.primaryStage, "Infos über die Filter",
                HelpText.FILTER_INFO);

        final VBox vBox = new VBox();
        vBox.setSpacing(10);

        vBox.getChildren().add(tglMediathek);
        vBox.getChildren().add(tglAudiothek);

        addTxt("Sender", mbChannel, vBox);
        addTxt("Thema", cboTheme, vBox);
        addTxt("Titel", cboTitle, vBox);
        addTxt("Irgendwo", cboSomewhere, vBox);

        // Slider
        vBox.getChildren().add(addSlider());
        vBox.getChildren().add(slDur);

        // checkbox
        P2ToggleSwitch chkOnlyNew = new P2ToggleSwitch("Nur neue Filme:");
        chkOnlyNew.selectedProperty().bindBidirectional(progData.actFilmFilterWorker.getActFilterSettings().onlyNewProperty());
        vBox.getChildren().add(chkOnlyNew);

        P2ToggleSwitch chkLive = new P2ToggleSwitch("Nur Live-Streams:");
        chkLive.setSelected(progData.actFilmFilterWorker.getActFilterSettings().onlyLiveProperty().get());
        chkLive.selectedProperty().addListener((u, o, n) -> {
            if (chkLive.isSelected()) {
                progData.actFilmFilterWorker.getActFilterSettings().clearFilter();
                progData.actFilmFilterWorker.getActFilterSettings().setOnlyLive(true);
            } else {
                progData.actFilmFilterWorker.getActFilterSettings().setOnlyLive(false);
            }
        });
        progData.actFilmFilterWorker.getActFilterSettings().onlyLiveProperty().addListener((u, o, n) -> {
            chkLive.setSelected(n);
        });
        vBox.getChildren().add(chkLive);

        // ==================
        // Speicher
        VBox vBoxSaved = new VBox();
        HBox hBoxSet = new HBox();
        hBoxSet.getChildren().addAll(btnFilter1, btnFilter2, btnFilter3, btnFilter4);
        btnFilter1.setMaxWidth(Double.MAX_VALUE);
        btnFilter2.setMaxWidth(Double.MAX_VALUE);
        btnFilter3.setMaxWidth(Double.MAX_VALUE);
        btnFilter4.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnFilter1, Priority.ALWAYS);
        HBox.setHgrow(btnFilter2, Priority.ALWAYS);
        HBox.setHgrow(btnFilter3, Priority.ALWAYS);
        HBox.setHgrow(btnFilter4, Priority.ALWAYS);

        HBox hBoxStore = new HBox();
        hBoxStore.getChildren().addAll(btnAddFilter1, btnAddFilter2, btnAddFilter3, btnAddFilter4);
        btnAddFilter1.setMaxWidth(Double.MAX_VALUE);
        btnAddFilter2.setMaxWidth(Double.MAX_VALUE);
        btnAddFilter3.setMaxWidth(Double.MAX_VALUE);
        btnAddFilter4.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnAddFilter1, Priority.ALWAYS);
        HBox.setHgrow(btnAddFilter2, Priority.ALWAYS);
        HBox.setHgrow(btnAddFilter3, Priority.ALWAYS);
        HBox.setHgrow(btnAddFilter4, Priority.ALWAYS);

        Label lbl = new Label("Filter einstellen / sichern:");
        lbl.setPadding(new Insets(0, 0, 2, 0));
        vBoxSaved.getChildren().addAll(lbl, hBoxSet, hBoxStore);

        // ==================
        // clear
        HBox hBoxClear = new HBox(P2LibConst.DIST_BUTTON);
        hBoxClear.setAlignment(Pos.CENTER_RIGHT);
        hBoxClear.getChildren().addAll(btnGoBack, btnGoForward,
                P2GuiTools.getHBoxGrower(),
                btnClearFilter, btnHelpFilter);


        // ===============
        getChildren().addAll(vBox, P2GuiTools.getVBoxGrower(), vBoxSaved, hBoxClear);
        VBox.setVgrow(this, Priority.ALWAYS);
    }

    private void addTxt(String txt, Node control, VBox vBoxComplete) {
        VBox vBox = new VBox(2);
        Label label = new Label(txt);
        vBox.getChildren().addAll(label, control);
        vBoxComplete.getChildren().add(vBox);
    }
}
