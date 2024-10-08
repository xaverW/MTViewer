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

import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.File;

public class DeleteFilmFileDialogController extends P2DialogExtra {

    private final CheckBox chkFilm = new CheckBox("Film löschen");
    private final CheckBox chkInfo = new CheckBox("Infodatei löschen");
    private final CheckBox chkSubtitle = new CheckBox("Untertiteldatei löschen");
    private final CheckBox chkSubtitleSrt = new CheckBox("Untertiteldatei löschen");
    private final File filmFile, infoFile, subtitleFile, subtitleFileSrt;
    private final String downloadPath;
    private VBox vBoxCont;
    private Button btnOk = new Button("_Ok");
    private Button btnCancel = new Button("_Abbrechen");
    private Label lblFilm = new Label("");
    private Label lblInfoFile = new Label();
    private Label lblSubtitle = new Label();
    private Label lblSubtitleSrt = new Label();
    private GridPane gridPane = new GridPane();

    public DeleteFilmFileDialogController(String downloadPath, File filmFile, File infoFile, File subtitleFile, File subtitleFileSrt) {
        super(ProgData.getInstance().primaryStage, null, "Datei löschen", true, false);

        this.downloadPath = downloadPath;
        this.filmFile = filmFile;
        this.infoFile = infoFile;
        this.subtitleFile = subtitleFile;
        this.subtitleFileSrt = subtitleFileSrt;

        vBoxCont = getVBoxCont();
        init(true);
    }

    @Override
    public void make() {
        vBoxCont.setPadding(new Insets(5));
        vBoxCont.setSpacing(10);
        vBoxCont.getChildren().addAll(gridPane);
        addOkCancelButtons(btnOk, btnCancel);

        gridPane.setMaxWidth(Double.MAX_VALUE);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setHgap(25);
        gridPane.setVgap(10);
        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcComputedSizeAndHgrow());

        int row = 0;
        Label lblPath = new Label("Pfad:");
        lblPath.setStyle("-fx-font-weight: bold;");

        Label lblDownloadPath = new Label(downloadPath);
        lblDownloadPath.setStyle("-fx-font-weight: bold;");

        gridPane.add(lblPath, 0, row);
        gridPane.add(lblDownloadPath, 1, row);
        gridPane.add(new Label(" "), 0, ++row);

        if (filmFile != null && filmFile.exists()) {
            lblFilm.setText(filmFile.getName());
            chkFilm.setSelected(true);
        } else {
            chkFilm.setSelected(false);
            chkFilm.setDisable(true);
        }
        gridPane.add(chkFilm, 0, ++row);
        gridPane.add(lblFilm, 1, row);


        if (infoFile != null && infoFile.exists()) {
            lblInfoFile.setText(infoFile.getName());
            chkInfo.setSelected(true);
        } else {
            chkInfo.setSelected(false);
            chkInfo.setDisable(true);
        }
        gridPane.add(chkInfo, 0, ++row);
        gridPane.add(lblInfoFile, 1, row);


        if (subtitleFile != null && subtitleFile.exists()) {
            lblSubtitle.setText(subtitleFile.getName());
            chkSubtitle.setSelected(true);
            // nur anzeigen wenn vorhanden
            gridPane.add(chkSubtitle, 0, ++row);
            gridPane.add(lblSubtitle, 1, row);
        } else {
            chkSubtitle.setSelected(false);
        }


        if (subtitleFileSrt != null && subtitleFileSrt.exists()) {
            lblSubtitleSrt.setText(subtitleFileSrt.getName());
            chkSubtitleSrt.setSelected(true);
            // nur anzeigen wenn vorhanden
            gridPane.add(chkSubtitleSrt, 0, ++row);
            gridPane.add(lblSubtitleSrt, 1, row);
        } else {
            chkSubtitleSrt.setSelected(false);
        }


        btnOk.disableProperty().bind(chkFilm.selectedProperty().not()
                .and(chkInfo.selectedProperty().not()
                        .and(chkSubtitle.selectedProperty().not()
                                .and(chkSubtitleSrt.selectedProperty().not()))));

        btnOk.setOnAction(event -> {
            if (deleteFile()) {
                quit();
            }
        });

        btnCancel.setOnAction(event -> quit());
    }

    private boolean deleteFile() {
        String delFile = "";
        boolean ret = true;
        try {

            if (chkFilm.isSelected()) {
                delFile = filmFile.getAbsolutePath();
                P2Log.sysLog(new String[]{"Datei löschen: ", delFile});

                if (!filmFile.delete()) {
                    throw new Exception();
                }
            }

            if (chkInfo.isSelected()) {
                delFile = infoFile.getAbsolutePath();
                P2Log.sysLog(new String[]{"Datei löschen: ", delFile});

                if (!infoFile.delete()) {
                    throw new Exception();
                }
            }
            if (chkSubtitle.isSelected()) {
                delFile = subtitleFile.getAbsolutePath();
                P2Log.sysLog(new String[]{"Datei löschen: ", delFile});

                if (!subtitleFile.delete()) {
                    throw new Exception();
                }
            }
            if (chkSubtitleSrt.isSelected()) {
                delFile = subtitleFileSrt.getAbsolutePath();
                P2Log.sysLog(new String[]{"Datei löschen: ", delFile});

                if (!subtitleFileSrt.delete()) {
                    throw new Exception();
                }
            }

        } catch (Exception ex) {
            ret = false;
            P2Alert.showErrorAlert("Datei löschen",
                    "Konnte die Datei nicht löschen!",
                    "Fehler beim löschen von:" + P2LibConst.LINE_SEPARATORx2 + delFile);
            P2Log.errorLog(302020149, "Fehler beim löschen: " + delFile);
        }

        return ret;
    }

    private void quit() {
        close();
    }
}
