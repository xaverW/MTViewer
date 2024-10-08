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

package de.p2tools.mtviewer.gui.dialog;

import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.data.ProgIcons;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.guitools.P2Hyperlink;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import de.p2tools.p2lib.mtfilm.film.FilmData;
import de.p2tools.p2lib.mtfilm.film.FilmDataXml;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class FilmInfoDialogController extends P2DialogExtra {
    private static FilmInfoDialogController instance;

    private final Text[] textTitle = new Text[FilmDataXml.MAX_ELEM];
    private final Label[] lblCont = new Label[FilmDataXml.MAX_ELEM];
    private final TextArea textArea = new TextArea();

    private final GridPane gridPane = new GridPane();
    private final Button btnOk = new Button("_Ok");

    private final ImageView ivHD = new ImageView();
    private final ImageView ivUT = new ImageView();
    private final ImageView ivNew = new ImageView();
    private final P2ToggleSwitch tglUrl = new P2ToggleSwitch("URL's anzeigen");

    private final P2Hyperlink pHyperlinkUrl = new P2Hyperlink("",
            ProgConfig.SYSTEM_PROG_OPEN_URL);

    private final P2Hyperlink pHyperlinkWebsite = new P2Hyperlink("",
            ProgConfig.SYSTEM_PROG_OPEN_URL);

    BooleanProperty urlProperty = ProgConfig.FILM_INFO_DIALOG_SHOW_URL;

    private FilmInfoDialogController() {
        super(ProgData.getInstance().primaryStage, ProgConfig.SYSTEM_SIZE_DIALOG_FILMINFO,
                "Filminfos", false, false, DECO.BORDER_SMALL);

        init(false);
    }

    public synchronized static final FilmInfoDialogController getInstance() {
        if (instance == null) {
            instance = new FilmInfoDialogController();
        }
        return instance;
    }

    public synchronized static final FilmInfoDialogController getInstanceAndShow() {
        if (instance == null) {
            instance = new FilmInfoDialogController();
        }

        if (!instance.isShowing()) {
            instance.showDialog();
        }
        instance.getStage().toFront();

        return instance;
    }

    public void showFilmInfo() {
        showDialog();
    }

    public void setFilm(FilmData film) {
        Platform.runLater(() -> {
            //braucht es aktuell (noch) nicht: Platform ....
            for (int i = 0; i < FilmDataXml.MAX_ELEM; ++i) {
                if (film == null) {
                    lblCont[i].setText("");
                    textArea.setText("");
                    ivHD.setImage(null);
                    ivUT.setImage(null);
                    ivNew.setImage(null);
                    pHyperlinkUrl.setUrl("");
                    pHyperlinkWebsite.setUrl("");
                } else {
                    switch (i) {
                        case FilmDataXml.FILM_NR:
                            lblCont[i].setText(film.getNo() + "");
                            break;
                        case FilmDataXml.FILM_DURATION:
                            lblCont[i].setText(film.getDurationMinute() + "");
                            break;
                        case FilmDataXml.FILM_URL:
                            pHyperlinkUrl.setUrl(film.arr[FilmDataXml.FILM_URL]);
                            break;
                        case FilmDataXml.FILM_WEBSITE:
                            pHyperlinkWebsite.setUrl(film.arr[FilmDataXml.FILM_WEBSITE]);
                            break;
                        case FilmDataXml.FILM_DESCRIPTION:
                            textArea.setText(film.arr[i]);
                            break;
                        case FilmDataXml.FILM_HD:
                            if (film.isHd()) {
                                ivHD.setImage(ProgIcons.ICON_DIALOG_ON.getImage());
                            } else {
                                ivHD.setImage(null);
                            }
                            break;
                        case FilmDataXml.FILM_UT:
                            if (film.isUt()) {
                                ivUT.setImage(ProgIcons.ICON_DIALOG_ON.getImage());
                            } else {
                                ivUT.setImage(null);
                            }
                            break;
                        case FilmDataXml.FILM_NEW:
                            if (film.isNewFilm()) {
                                ivNew.setImage(ProgIcons.ICON_DIALOG_ON.getImage());
                            } else {
                                ivNew.setImage(null);
                            }
                            break;

                        default:
                            lblCont[i].setText(film.arr[i]);
                    }
                }
            }
        });
    }

    @Override
    public void make() {
        ProgConfig.SYSTEM_THEME_CHANGED.addListener((u, o, n) -> updateCss());
        addOkButton(btnOk);

        getHboxLeft().getChildren().add(tglUrl);
        tglUrl.setTooltip(new Tooltip("URL anzeigen"));
        tglUrl.selectedProperty().bindBidirectional(urlProperty);
        tglUrl.selectedProperty().addListener((observable, oldValue, newValue) -> setUrl());

        btnOk.setOnAction(a -> close());
        getVBoxCont().getChildren().add(gridPane);

        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.PADDING));
        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcComputedSizeAndHgrow());

        int row = 0;
        for (int i = 0; i < FilmDataXml.MAX_ELEM; ++i) {
            textTitle[i] = new Text(FilmDataXml.COLUMN_NAMES[i] + ":");
            textTitle[i].setFont(Font.font(null, FontWeight.BOLD, -1));
            lblCont[i] = new Label("");
            lblCont[i].setWrapText(true);

            switch (i) {
                case FilmDataXml.FILM_DATE_LONG:
                case FilmDataXml.FILM_PLAY:
                case FilmDataXml.FILM_RECORD:
                case FilmDataXml.FILM_URL_HD:
                case FilmDataXml.FILM_URL_HISTORY:
                case FilmDataXml.FILM_URL_SMALL:
                case FilmDataXml.FILM_URL_SUBTITLE:
                    // bis hier nicht anzeigen
                    break;

                case FilmDataXml.FILM_HD:
                    gridPane.add(textTitle[i], 0, row);
                    gridPane.add(ivHD, 1, row++);
                    break;
                case FilmDataXml.FILM_UT:
                    gridPane.add(textTitle[i], 0, row);
                    gridPane.add(ivUT, 1, row++);
                    break;
                case FilmDataXml.FILM_NEW:
                    gridPane.add(textTitle[i], 0, row);
                    gridPane.add(ivNew, 1, row++);
                    break;
                case FilmDataXml.FILM_URL:
                    gridPane.add(textTitle[i], 0, row);
                    gridPane.add(pHyperlinkUrl, 1, row++);
                    break;
                case FilmDataXml.FILM_WEBSITE:
                    gridPane.add(textTitle[i], 0, row);
                    gridPane.add(pHyperlinkWebsite, 1, row++);
                    break;
                case FilmDataXml.FILM_DESCRIPTION:
                    textArea.setMaxHeight(Double.MAX_VALUE);
                    textArea.setPrefRowCount(6);
                    textArea.setWrapText(true);
                    textArea.setEditable(false);
                    gridPane.add(textTitle[i], 0, row);
                    gridPane.add(textArea, 1, row++);
                    break;
                default:
                    gridPane.add(textTitle[i], 0, row);
                    gridPane.add(lblCont[i], 1, row++);
                    final int ii = i;
                    lblCont[i].setOnContextMenuRequested(event ->
                            getMenu(lblCont[ii], event));
            }
        }
        setUrl();
    }

    private void setUrl() {
        textTitle[FilmDataXml.FILM_URL].setVisible(urlProperty.get());
        textTitle[FilmDataXml.FILM_URL].setManaged(urlProperty.get());

        pHyperlinkUrl.setVisible(urlProperty.get());
        pHyperlinkUrl.setManaged(urlProperty.get());
        pHyperlinkUrl.setWrapText(true);
        pHyperlinkUrl.setMinHeight(Region.USE_PREF_SIZE);
        pHyperlinkUrl.setPadding(new Insets(5));

        textTitle[FilmDataXml.FILM_WEBSITE].setVisible(urlProperty.get());
        textTitle[FilmDataXml.FILM_WEBSITE].setManaged(urlProperty.get());

        pHyperlinkWebsite.setVisible(urlProperty.get());
        pHyperlinkWebsite.setManaged(urlProperty.get());
        pHyperlinkWebsite.setWrapText(true);
        pHyperlinkWebsite.setMinHeight(Region.USE_PREF_SIZE);
        pHyperlinkWebsite.setPadding(new Insets(5));
    }

    private void getMenu(Label lbl, ContextMenuEvent event) {
        final ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem = new MenuItem("kopieren");
        menuItem.setOnAction(a -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(lbl.getText());
            clipboard.setContent(content);
        });
        contextMenu.getItems().addAll(menuItem);
        contextMenu.show(lbl, event.getScreenX(), event.getScreenY());
    }
}
