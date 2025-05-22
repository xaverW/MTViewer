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
import de.p2tools.mtviewer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2ButtonClearFilterFactory;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.P2MenuButton;
import de.p2tools.p2lib.guitools.prange.P2RangeBox;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import de.p2tools.p2lib.mtfilter.FilterCheck;
import de.p2tools.p2lib.mtfilter.FilterCheckRegEx;
import de.p2tools.p2lib.tools.duration.P2Duration;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.ArrayList;

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
    private final ArrayList<MenuItemClass> menuItemsList = new ArrayList<>();
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

        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        int row = 0;

        gridPane.add(tglMediathek, 0, row);
        gridPane.add(tglAudiothek, 0, ++row);

        ++row;
        VBox vBox;
        vBox = addTxt("Sender", mbChannel);
        GridPane.setHgrow(vBox, Priority.ALWAYS);
        gridPane.add(vBox, 0, ++row);

        vBox = addTxt("Thema", cboTheme);
        GridPane.setHgrow(vBox, Priority.ALWAYS);
        gridPane.add(vBox, 0, ++row);

        vBox = addTxt("Titel", cboTitle);
        GridPane.setHgrow(vBox, Priority.ALWAYS);
        gridPane.add(vBox, 0, ++row);

        vBox = addTxt("Irgendwo", cboSomewhere);
        GridPane.setHgrow(vBox, Priority.ALWAYS);
        gridPane.add(vBox, 0, ++row);

        // Slider
        gridPane.add(new Label(""), 0, ++row);

        vBox = addSlider();
        gridPane.add(vBox, 0, ++row);
        GridPane.setHgrow(vBox, Priority.ALWAYS);

        vBox = slDur;
        gridPane.add(vBox, 0, ++row);
        GridPane.setHgrow(vBox, Priority.ALWAYS);

        // checkbox
        gridPane.add(new Label(""), 0, ++row);

        P2ToggleSwitch chkOnlyNew = new P2ToggleSwitch("Nur neue Filme:");
        chkOnlyNew.selectedProperty().bindBidirectional(progData.actFilmFilterWorker.getActFilterSettings().onlyNewProperty());
        gridPane.add(chkOnlyNew, 0, ++row);

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
        gridPane.add(chkLive, 0, ++row);

        // ==================
        // clear
        HBox hBoxClear = new HBox(P2LibConst.DIST_BUTTON);
        hBoxClear.setAlignment(Pos.CENTER_RIGHT);
        hBoxClear.getChildren().addAll(btnGoBack, btnGoForward,
                P2GuiTools.getHBoxGrower(),
                btnClearFilter, btnHelpFilter);


        // ===============
        getChildren().addAll(gridPane, P2GuiTools.getVBoxGrower(), hBoxClear);
        VBox.setVgrow(this, Priority.ALWAYS);
    }

    private VBox addTxt(String txt, Control control) {
        VBox vBox = new VBox(2);
        vBox.setMaxWidth(Double.MAX_VALUE);
        Label label = new Label(txt);
        vBox.getChildren().addAll(label, control);
        return vBox;
    }

    private static class MenuItemClass {
        private final String text;
        private final CheckBox checkBox;

        MenuItemClass(String text, CheckBox checkbox) {
            this.text = text;
            this.checkBox = checkbox;
        }

        public String getText() {
            return text;
        }

        public CheckBox getCheckBox() {
            return checkBox;
        }
    }
}
