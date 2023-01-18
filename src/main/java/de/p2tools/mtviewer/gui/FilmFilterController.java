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
import de.p2tools.mtviewer.controller.filmFilter.FilmFilterCheck;
import de.p2tools.mtviewer.controller.filmFilter.FilterCheckRegEx;
import de.p2tools.p2Lib.guiTools.PGuiTools;
import de.p2tools.p2Lib.tools.duration.PDuration;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilmFilterController extends VBox {

    public static final int FILTER_SPACING_TEXTFILTER = 10;
    private final MenuButton mbChannel = new MenuButton("");
    private final ComboBox<String> cboTheme = new ComboBox();
    private final ComboBox<String> cboTitle = new ComboBox();
    private final ComboBox<String> cboSomewhere = new ComboBox();
    private final Slider slTimeRange = new Slider();
    private final Label lblTimeRange = new Label("Zeitraum:");
    private final Label lblTimeRangeValue = new Label();
    private final Button btnClearFilter = new Button();
    private final Button btnGoBack = new Button("");
    private final Button btnGoForward = new Button("");
    private final ArrayList<MenuItemClass> menuItemsList = new ArrayList<>();
    private final ProgData progData;

    public FilmFilterController() {
        progData = ProgData.getInstance();

        setPadding(new Insets(10, 15, 5, 15));
        setSpacing(FILTER_SPACING_TEXTFILTER);

        // Sender, Thema, ..
        initButton();
        initSenderFilter();
        initStringFilter();
        addFilter();
    }

    private void initButton() {
        btnGoBack.setGraphic(ProgIcons.Icons.ICON_BUTTON_BACKWARD.getImageView());
        btnGoBack.setOnAction(a -> progData.actFilmFilterWorker.goBackward());
        btnGoBack.disableProperty().bind(progData.actFilmFilterWorker.backwardPossibleProperty().not());
        btnGoBack.setTooltip(new Tooltip("letzte Filtereinstellung wieder herstellen"));
        btnGoForward.setGraphic(ProgIcons.Icons.ICON_BUTTON_FORWARD.getImageView());
        btnGoForward.setOnAction(a -> progData.actFilmFilterWorker.goForward());
        btnGoForward.disableProperty().bind(progData.actFilmFilterWorker.forwardPossibleProperty().not());
        progData.actFilmFilterWorker.forwardPossibleProperty().addListener((v, o, n) -> System.out.println(progData.actFilmFilterWorker.forwardPossibleProperty().getValue().toString()));
        btnGoForward.setTooltip(new Tooltip("letzte Filtereinstellung wieder herstellen"));

        btnClearFilter.setGraphic(de.p2tools.p2Lib.ProgIcons.Icons.ICON_BUTTON_CLEAR_FILTER.getImageView());
        btnClearFilter.setTooltip(new Tooltip("Filter löschen"));
        btnClearFilter.setOnAction(a -> {
            PDuration.onlyPing("Filter löschen");
            progData.actFilmFilterWorker.clearFilter();
        });
    }

    private void initSenderFilter() {
        mbChannel.setMaxWidth(Double.MAX_VALUE);
        initChannelMenu();
        progData.actFilmFilterWorker.getActFilterSettings().channelProperty().addListener((observable, oldValue, newValue) -> {
            initChannelMenu();
        });
        progData.worker.getAllChannelList().addListener((ListChangeListener<String>) c -> initChannelMenu());
        mbChannel.textProperty().bindBidirectional(progData.actFilmFilterWorker.getActFilterSettings().channelProperty());
    }

    private void initChannelMenu() {
        mbChannel.getItems().clear();
        menuItemsList.clear();

        List<String> channelFilterList = new ArrayList<>();
        String channelFilter = progData.actFilmFilterWorker.getActFilterSettings().channelProperty().get();
        if (channelFilter != null) {
            if (channelFilter.contains(",")) {
                channelFilterList.addAll(Arrays.asList(channelFilter.replace(" ", "").toLowerCase().split(",")));
            } else {
                channelFilterList.add(channelFilter.toLowerCase());
            }
            channelFilterList.stream().forEach(s -> s = s.trim());
        }

        CheckBox miCheckAll = new CheckBox();
        miCheckAll.setVisible(false);

        Button btnAll = new Button("Auswahl löschen");
        btnAll.setMaxWidth(Double.MAX_VALUE);
        btnAll.setOnAction(e -> {
            clearMenuText();
            mbChannel.hide();
        });

        HBox hBoxAll = new HBox(10);
        hBoxAll.setAlignment(Pos.CENTER_LEFT);
        hBoxAll.getChildren().addAll(miCheckAll, btnAll);

        CustomMenuItem cmiAll = new CustomMenuItem(hBoxAll);
        mbChannel.getItems().add(cmiAll);

        for (String s : progData.worker.getAllChannelList()) {
            if (s.isEmpty()) {
                continue;
            }

            CheckBox miCheck = new CheckBox();
            if (channelFilterList.contains(s.toLowerCase())) {
                miCheck.setSelected(true);
            }
            miCheck.setOnAction(a -> setMenuText());

            MenuItemClass menuItemClass = new MenuItemClass(s, miCheck);
            menuItemsList.add(menuItemClass);

            Button btnChannel = new Button(s);
            btnChannel.setMaxWidth(Double.MAX_VALUE);
            btnChannel.setOnAction(e -> {
                setCheckBoxAndMenuText(menuItemClass);
                mbChannel.hide();
            });

            HBox hBox = new HBox(10);
            hBox.prefWidthProperty().bind(hBoxAll.widthProperty());
            hBox.setAlignment(Pos.CENTER_LEFT);
            hBox.getChildren().addAll(miCheck, btnChannel);
            HBox.setHgrow(btnChannel, Priority.ALWAYS);

            CustomMenuItem cmi = new CustomMenuItem(hBox);
            mbChannel.getItems().add(cmi);
        }
    }

    private void setCheckBoxAndMenuText(MenuItemClass cmi) {
        for (MenuItemClass cm : menuItemsList) {
            cm.getCheckBox().setSelected(false);
        }
        cmi.getCheckBox().setSelected(true);
        setMenuText();
    }

    private void clearMenuText() {
        for (MenuItemClass cmi : menuItemsList) {
            cmi.getCheckBox().setSelected(false);
        }
        mbChannel.setText("");
    }

    private void setMenuText() {
        String text = "";
        for (MenuItemClass cmi : menuItemsList) {
            if (cmi.getCheckBox().isSelected()) {
                text = text + (text.isEmpty() ? "" : ", ") + cmi.getText();
            }
        }
        mbChannel.setText(text);
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
        initDaysFilter();
        VBox vBox;
        vBox = new VBox(2);
        vBox.setPadding(new Insets(0, 10, 0, 10));
        HBox h = new HBox(lblTimeRange, PGuiTools.getHBoxGrower(), lblTimeRangeValue);
        lblTimeRange.setMinWidth(0);
        vBox.getChildren().addAll(h, slTimeRange);
        getChildren().addAll(vBox);
        return vBox;
    }

    private void initDaysFilter() {
        slTimeRange.setMin(FilmFilterCheck.FILTER_TIME_RANGE_MIN_VALUE);
        slTimeRange.setMax(FilmFilterCheck.FILTER_TIME_RANGE_MAX_VALUE);
        slTimeRange.setShowTickLabels(true);

        slTimeRange.setMajorTickUnit(10);
        slTimeRange.setBlockIncrement(5);

        slTimeRange.setLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Double x) {
                if (x == FilmFilterCheck.FILTER_TIME_RANGE_ALL_VALUE) return "alles";

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

    private void setLabelSlider() {
        final String txtAll = "alles";

        int i = (int) slTimeRange.getValue();
        String tNr = i + "";

        if (i == FilmFilterCheck.FILTER_TIME_RANGE_ALL_VALUE) {
            lblTimeRangeValue.setText(txtAll);
        } else {
            lblTimeRangeValue.setText(tNr + (i == 1 ? " Tag" : " Tage"));
        }
    }

    private void addFilter() {
        //erste Zeile
        final GridPane gridPaneLine1 = new GridPane();
        gridPaneLine1.setHgap(15);
        gridPaneLine1.setVgap(15);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(25);
        gridPaneLine1.getColumnConstraints().addAll(col1, col1, col1, col1);

        VBox vBox;
        vBox = addTxt("Sender", mbChannel);
        GridPane.setHgrow(vBox, Priority.ALWAYS);
        gridPaneLine1.add(vBox, 0, 0);

        vBox = addTxt("Thema", cboTheme);
        GridPane.setHgrow(vBox, Priority.ALWAYS);
        gridPaneLine1.add(vBox, 1, 0);

        vBox = addTxt("Titel", cboTitle);
        GridPane.setHgrow(vBox, Priority.ALWAYS);
        gridPaneLine1.add(vBox, 2, 0);

        vBox = addTxt("Irgendwo", cboSomewhere);
        GridPane.setHgrow(vBox, Priority.ALWAYS);
        gridPaneLine1.add(vBox, 3, 0);


        //zweite Zeile
        HBox hBoxLine2 = new HBox();
        hBoxLine2.setSpacing(20);

        vBox = addSlider();
        hBoxLine2.getChildren().add(vBox);

        CheckBox chkOnlyNew = new CheckBox();
        chkOnlyNew.selectedProperty().bindBidirectional(progData.actFilmFilterWorker.getActFilterSettings().onlyNewProperty());
        HBox hBoxNew = new HBox(5, new Label("nur neue Filme:"), chkOnlyNew);
        hBoxNew.setAlignment(Pos.CENTER);
        hBoxLine2.getChildren().add(hBoxNew);

        CheckBox chkLive = new CheckBox();
        chkLive.setSelected(progData.actFilmFilterWorker.getActFilterSettings().onlyLiveProperty().get());
        chkLive.setOnAction(a -> {
            if (chkLive.isSelected()) {
                progData.actFilmFilterWorker.getActFilterSettings().clearFilter();
                progData.actFilmFilterWorker.getActFilterSettings().setOnlyLive(true);
            } else {
                progData.actFilmFilterWorker.getActFilterSettings().setOnlyLive(false);
            }
        });
        HBox hBoxLive = new HBox(5, new Label("nur Live-Streams:"), chkLive);
        hBoxLive.setAlignment(Pos.CENTER);
        progData.actFilmFilterWorker.getActFilterSettings().onlyLiveProperty().addListener((u, o, n) -> {
            chkLive.setSelected(n.booleanValue());
        });
        hBoxLine2.getChildren().add(hBoxLive);

        HBox hBoxClear = new HBox(10);
        hBoxClear.setAlignment(Pos.CENTER_RIGHT);
        hBoxClear.getChildren().addAll(PGuiTools.getHBoxGrower(), btnGoBack, btnGoForward,
                btnClearFilter, new ProgMenu());
        hBoxLine2.getChildren().addAll(PGuiTools.getHBoxGrower(), hBoxClear);

        getChildren().add(gridPaneLine1);
        getChildren().add(hBoxLine2);
    }

    private VBox addTxt(String txt, Control control) {
        VBox vBox = new VBox(2);
        vBox.setMaxWidth(Double.MAX_VALUE);
        Label label = new Label(txt);
        vBox.getChildren().addAll(label, control);
        return vBox;
    }

    private class MenuItemClass {
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
