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

import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.film.FilmTools;
import de.p2tools.mtviewer.controller.film.LoadFilmFactory;
import de.p2tools.mtviewer.gui.tools.Listener;
import de.p2tools.p2Lib.mtFilm.loadFilmlist.ListenerFilmlistLoadEvent;
import de.p2tools.p2Lib.mtFilm.loadFilmlist.ListenerLoadFilmlist;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class StatusBarController extends AnchorPane {

    private final Label lblSelFilm = new Label();
    private final Label lblLeftFilm = new Label();
    private final Label lblRightFilm = new Label();

    private final HBox filmPane;
    private final ProgData progData;
    private boolean stopTimer = false;

    public StatusBarController(ProgData progData) {
        this.progData = progData;

        filmPane = getHbox(lblSelFilm, lblLeftFilm, lblRightFilm);
        getChildren().addAll(filmPane);
        AnchorPane.setLeftAnchor(filmPane, 0.0);
        AnchorPane.setBottomAnchor(filmPane, 0.0);
        AnchorPane.setRightAnchor(filmPane, 0.0);
        AnchorPane.setTopAnchor(filmPane, 0.0);
        make();
    }

    private HBox getHbox(Label lblSel, Label lblLeft, Label lblRight) {
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(2, 5, 2, 5));
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_RIGHT);

        lblSel.setPadding(new Insets(0, 10, 0, 0));
        lblSel.getStyleClass().add("lblSelectedLines");

        lblLeft.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(lblLeft, Priority.ALWAYS);

        hBox.getChildren().addAll(lblSel, lblLeft, lblRight);
        hBox.setStyle("-fx-background-color: -fx-background;");
        return hBox;
    }

    private void make() {
        setInfoFilm();
        setTextForRightDisplay();
        LoadFilmFactory.getInstance().loadFilmlist.addListenerLoadFilmlist(new ListenerLoadFilmlist() {
            @Override
            public void start(ListenerFilmlistLoadEvent event) {
                stopTimer = true;
            }

            @Override
            public void finished(ListenerFilmlistLoadEvent event) {
                stopTimer = false;
                setStatusbarIndex();
            }
        });
        Listener.addListener(new Listener(Listener.EVENT_TIMER, StatusBarController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                try {
                    if (!stopTimer) {
                        setStatusbarIndex();
                    }
                } catch (final Exception ex) {
                    PLog.errorLog(936251087, ex);
                }
            }
        });
        progData.checkForNewFilmlist.foundNewListProperty().addListener((u, o, n) -> {
            if (progData.checkForNewFilmlist.isFoundNewList()) {
                lblRightFilm.setStyle("-fx-underline: true;");
            } else {
                lblRightFilm.setStyle("-fx-underline: false;");
            }
        });
    }

    public void setStatusbarIndex() {
        setInfoFilm();
        setTextForRightDisplay();
    }

    private void setInfoFilm() {
        lblLeftFilm.setText(FilmTools.getStatusInfosFilm());
        final int selCount = progData.filmGuiController.getSelCount();
        lblSelFilm.setText(selCount > 0 ? selCount + "" : " ");
    }

    private void setTextForRightDisplay() {
        // Text rechts: alter/neuladenIn anzeigen
        String strText = "Filmliste erstellt: ";
        strText += progData.filmlist.genDate();
        strText += " Uhr  ";

        final int second = progData.filmlist.getAge();
        if (second != 0) {
            strText += "||  Alter: ";
            final int minute = second / 60;
            String strSecond = String.valueOf(second % 60);
            String strMinute = String.valueOf(minute % 60);
            String strHour = String.valueOf(minute / 60);
            if (strSecond.length() < 2) {
                strSecond = '0' + strSecond;
            }
            if (strMinute.length() < 2) {
                strMinute = '0' + strMinute;
            }
            if (strHour.length() < 2) {
                strHour = '0' + strHour;
            }
            strText += strHour + ':' + strMinute + ':' + strSecond + ' ';
        }
        // Infopanel setzen
        lblRightFilm.setText(strText);
    }
}
