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

import de.p2tools.mtviewer.controller.FilmTools;
import de.p2tools.mtviewer.controller.config.PEvents;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.p2lib.p2event.P2Event;
import de.p2tools.p2lib.p2event.P2Listener;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class StatusBarController extends AnchorPane {

    private final Label lblLeftFilm = new Label();
    private final Label lblRightFilm = new Label();

    private final HBox filmPane;
    private final ProgData progData;
    private boolean stopTimer = false;

    public StatusBarController(ProgData progData) {
        this.progData = progData;

        filmPane = getHbox(lblLeftFilm, lblRightFilm);
        getChildren().addAll(filmPane);
        AnchorPane.setLeftAnchor(filmPane, 0.0);
        AnchorPane.setBottomAnchor(filmPane, 0.0);
        AnchorPane.setRightAnchor(filmPane, 0.0);
        AnchorPane.setTopAnchor(filmPane, 0.0);
        make();
    }

    private HBox getHbox(Label lblLeft, Label lblRight) {
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(2, 5, 2, 5));
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_RIGHT);

        lblLeft.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(lblLeft, Priority.ALWAYS);

        hBox.getChildren().addAll(lblLeft, lblRight);
        hBox.setStyle("-fx-background-color: -fx-background;");
        return hBox;
    }

    private void make() {
        setInfoFilm();
        setTextForRightDisplay();
        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_FILMLIST_LOAD_START) {
            @Override
            public void pingGui(P2Event event) {
                stopTimer = true;
            }
        });
        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_FILMLIST_LOAD_FINISHED) {
            @Override
            public void pingGui(P2Event event) {
                stopTimer = false;
                setStatusbarIndex();
            }
        });
        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_TIMER_SECOND) {
            @Override
            public void pingGui() {
                try {
                    if (!stopTimer) {
                        setStatusbarIndex();
                    }
                } catch (final Exception ex) {
                    P2Log.errorLog(936251087, ex);
                }
            }
        });
    }

    public void setStatusbarIndex() {
        setInfoFilm();
        setTextForRightDisplay();
    }

    private void setInfoFilm() {
        lblLeftFilm.setText(FilmTools.getStatusInfosFilm());
    }

    private void setTextForRightDisplay() {
        // Mediathek
        String strText = "Mediathek: ";
        strText += progData.filmlist.genDate();
        strText += " Uhr";

        // Audiothek
        strText += "  /  Audiothek: ";
        strText += progData.audioList.genDate();
        strText += " Uhr";

        // Infopanel setzen
        lblRightFilm.setText(strText);
    }
}
