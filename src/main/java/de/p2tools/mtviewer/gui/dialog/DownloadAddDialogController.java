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

import de.p2tools.mtviewer.controller.config.ProgColorList;
import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.data.ProgIconsMTViewer;
import de.p2tools.mtviewer.controller.data.download.DownloadConstants;
import de.p2tools.mtviewer.controller.data.download.DownloadData;
import de.p2tools.mtviewer.controller.downloadtools.HttpDownloadFactory;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.dialogs.P2DirFileChooser;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.guitools.P2Hyperlink;
import de.p2tools.p2lib.mtfilm.film.FilmData;
import de.p2tools.p2lib.mtfilm.film.FilmFactory;
import de.p2tools.p2lib.mtfilm.tools.FileNameUtils;
import de.p2tools.p2lib.tools.PSystemUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.File;

public class DownloadAddDialogController extends P2DialogExtra {

    final String[] storedPath = ProgConfig.DOWNLOAD_DIALOG_PATH_SAVING.get().split("<>");
    private final ProgData progData;

    private final HBox hBoxSize = new HBox();
    private final ComboBox<String> cboPath = new ComboBox<>();
    private final Button btnDest = new Button("Pfad");
    private final Button btnPropose = new Button("Vorschlag");
    private final Button btnOk = new Button("_Ok");
    private final Button btnCancel = new Button("_Abbrechen");
    private final TextField txtName = new TextField();
    private final RadioButton rbNot = new RadioButton("noch nicht");
    private final RadioButton rbStart = new RadioButton("sofort");
    private final CheckBox chkInfo = new CheckBox("Infodatei anlegen: \"Filmname.txt\"");
    private final CheckBox chkSubtitle = new CheckBox("Untertitel speichern: \"Filmname.xxx\"");
    private final RadioButton rbHd = new RadioButton("HD");
    private final RadioButton rbHigh = new RadioButton("Hoch");
    private final RadioButton rbSmall = new RadioButton("Klein");
    private final Label lblFree = new Label("4M noch frei");
    private final boolean onlyChange;
    private P2Hyperlink pHyperlinkUrlFilm =
            new P2Hyperlink("", ProgConfig.SYSTEM_PROG_OPEN_URL, ProgIconsMTViewer.ICON_BUTTON_FILE_OPEN.getImageView());
    private boolean ok = false;
    private FilmData filmData;
    private DownloadData downloadData;
    private String path = "";
    private String fileSize_HD = "";
    private String fileSize_high = "";
    private String fileSize_small = "";


    public DownloadAddDialogController(ProgData progData, DownloadData downloadData, FilmData filmData, boolean onlyChange) {
        super(progData.primaryStage, ProgConfig.DOWNLOAD_ADD_DIALOG_SIZE,
                onlyChange ? "Download ändern" : "Download anlegen",
                true, false);

        this.progData = progData;
        this.filmData = filmData;
        this.downloadData = downloadData;
        this.onlyChange = onlyChange;
        if (this.downloadData == null) {
            this.downloadData = new DownloadData(filmData);
        }

        init(true);
    }

    @Override
    public void make() {
        initCont();
        initArrays();
        initButton();
        initPathAndName();
        pathNameBase();
        initResolution();
        initCheckBox();

        if (this.downloadData.isStateStartedRun() || this.downloadData.isStateFinished()) {
            cboPath.setDisable(true);
            btnDest.setDisable(true);
            btnPropose.setDisable(true);
            btnOk.setDisable(true);
            txtName.setDisable(true);
            rbNot.setDisable(true);
            rbStart.setDisable(true);
            chkInfo.setDisable(true);
            chkSubtitle.setDisable(true);
            rbHd.setDisable(true);
            rbHigh.setDisable(true);
            rbSmall.setDisable(true);
        }
    }

    public boolean isOk() {
        return ok;
    }

    private void initCont() {
        Label lblChannel_ = new Label("Sender:");
        Label lblTheme_ = new Label("Thema:");
        Label lblTitle_ = new Label("Titel:");
        Label lblUrl_ = new Label("URL:");
        lblChannel_.setStyle("-fx-font-weight: bold;");
        lblTheme_.setStyle("-fx-font-weight: bold;");
        lblTitle_.setStyle("-fx-font-weight: bold;");
        lblUrl_.setStyle("-fx-font-weight: bold;");

        pHyperlinkUrlFilm.setWrapText(true);
        pHyperlinkUrlFilm.setMinHeight(Region.USE_PREF_SIZE);
        pHyperlinkUrlFilm.setPadding(new Insets(5));

        Label lblChannel = new Label("");
        Label lblTheme = new Label("");
        Label lblTitle = new Label("");

        if (filmData != null) {
            lblChannel.setText(filmData.getChannel());
            lblTheme.setText(filmData.getTheme());
            lblTitle.setText(filmData.getTitle());
        }

        // Gridpane
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        VBox.setVgrow(gridPane, Priority.ALWAYS);

        int row = 0;
        gridPane.add(lblChannel_, 0, row);
        gridPane.add(lblChannel, 1, row);
        gridPane.add(lblTheme_, 0, ++row);
        gridPane.add(lblTheme, 1, row);
        gridPane.add(lblTitle_, 0, ++row);
        gridPane.add(lblTitle, 1, row);

        if (onlyChange) {
            Label lblProgress_ = new Label("Fortschritt:");
            lblProgress_.setStyle("-fx-font-weight: bold;");
            Label lblProgress = new Label("");
            lblProgress.setText(DownloadConstants.getTextProgress(downloadData.getState(), downloadData.getProgress()));
            gridPane.add(lblProgress_, 0, ++row);
            gridPane.add(lblProgress, 1, row);

        }
        gridPane.add(lblUrl_, 0, ++row);
        gridPane.add(pHyperlinkUrlFilm, 1, row);

        gridPane.add(new Label(""), 1, ++row);

        gridPane.add(new Label("Auflösung:"), 0, ++row);
        hBoxSize.getStyleClass().add("downloadDialog");
        hBoxSize.setSpacing(20);
        hBoxSize.setPadding(new Insets(10, 5, 10, 5));
        hBoxSize.getChildren().addAll(rbHd, rbHigh, rbSmall);
        gridPane.add(hBoxSize, 1, row, 3, 1);

        gridPane.add(new Label("Dateiname:"), 0, ++row);
        gridPane.add(txtName, 1, row, 3, 1);

        gridPane.add(new Label("Zielpfad:"), 0, ++row);
        cboPath.setMaxWidth(Double.MAX_VALUE);
        gridPane.add(cboPath, 1, row);
        gridPane.add(btnDest, 2, row);
        gridPane.add(btnPropose, 3, row);

        HBox hBox2 = new HBox();
        hBox2.getChildren().add(lblFree);
        hBox2.setAlignment(Pos.CENTER_RIGHT);
        gridPane.add(hBox2, 1, ++row, 3, 1);

        gridPane.add(chkSubtitle, 1, ++row);
        gridPane.add(chkInfo, 1, ++row);

        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcComputedSizeAndHgrow());
        getVBoxCont().setSpacing(20);
        getVBoxCont().getChildren().addAll(gridPane);

        addOkCancelButtons(btnOk, btnCancel);
        if (!onlyChange) {
            getHboxLeft().getChildren().addAll(new Label("Download starten: "), rbStart, rbNot);
        }
    }


    private void initArrays() {
        if (filmData != null) {
            fileSize_HD = filmData.isHd() ? FilmFactory.getSizeFromWeb(filmData, filmData.getUrlForResolution(FilmData.RESOLUTION_HD)) : "";
            fileSize_high = FilmFactory.getSizeFromWeb(filmData, filmData.getUrlForResolution(FilmData.RESOLUTION_NORMAL));
            fileSize_small = filmData.isSmall() ? FilmFactory.getSizeFromWeb(filmData, filmData.getUrlForResolution(FilmData.RESOLUTION_SMALL)) : "";

            // die Werte passend zum Film setzen: Auflösung
            if (ProgConfig.DOWNLOAD_RESOLUTION.get().equals(FilmData.RESOLUTION_HD) && filmData.isHd()) {
                //Dann wurde HD vorausgewählt und wird voreingestellt
                ProgConfig.DOWNLOAD_RESOLUTION.setValue(FilmData.RESOLUTION_HD);

            } else if (ProgConfig.DOWNLOAD_RESOLUTION.get().equals(FilmData.RESOLUTION_SMALL) && filmData.isSmall()) {
                //Dann wurde small vorausgewählt und wird voreingestellt
                ProgConfig.DOWNLOAD_RESOLUTION.setValue(FilmData.RESOLUTION_SMALL);

            } else {
                ProgConfig.DOWNLOAD_RESOLUTION.setValue(FilmData.RESOLUTION_NORMAL);
            }
        }
    }

    private void initButton() {
        btnDest.setGraphic(ProgIconsMTViewer.ICON_BUTTON_FILE_OPEN.getImageView());
        btnDest.setText("");
        btnDest.setTooltip(new Tooltip("Einen Pfad zum Speichern auswählen."));
        btnDest.setOnAction(event -> P2DirFileChooser.DirChooser(ProgData.getInstance().primaryStage, cboPath));

        btnPropose.setGraphic(ProgIconsMTViewer.ICON_BUTTON_PROPOSE.getImageView());
        btnPropose.setText("");
        btnPropose.setTooltip(new Tooltip("Einen Pfad zum Speichern vorschlagen lassen."));
        btnPropose.setOnAction(event -> proposeDestination());

        btnOk.setOnAction(event -> {
            if (check()) {
                quit();
            }
        });
        btnCancel.setOnAction(event -> {
            ok = false;
            quit();
        });
    }

    private void initPathAndName() {
        // gespeicherte Pfade eintragen
        cboPath.setEditable(true);
        cboPath.getItems().addAll(storedPath);

        if (path.isEmpty()) {
            cboPath.getSelectionModel().selectFirst();
            path = cboPath.getSelectionModel().getSelectedItem();
        } else {
            cboPath.getSelectionModel().select(path);
        }

        DialogFactory.calculateAndCheckDiskSpace(path, lblFree, fileSize_HD, fileSize_high, fileSize_small);
        cboPath.valueProperty().addListener((observable, oldValue, newValue) -> {
            final String s = cboPath.getSelectionModel().getSelectedItem();
            path = s;
            DialogFactory.calculateAndCheckDiskSpace(path, lblFree, fileSize_HD, fileSize_high, fileSize_small);
        });

        txtName.setText(downloadData.getDestFileName());
        txtName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!txtName.getText().equals(FileNameUtils.checkFileName(txtName.getText(), false /* pfad */))) {
                txtName.setStyle(ProgColorList.DOWNLOAD_NAME_ERROR.getCssBackground());
            } else {
                txtName.setStyle("");
            }
        });
    }

    private void pathNameBase() {
        String path = downloadData.getDestPath();
        if (!path.isEmpty()) {
            if (!cboPath.getItems().contains(path)) {
                cboPath.getItems().add(path);
            }
            cboPath.getSelectionModel().select(path);
        }

        if (cboPath.getItems().isEmpty() ||
                cboPath.getItems().size() == 1 && cboPath.getItems().get(0).isEmpty()) {
            //leer oder und ein leerer Eintrag
            cboPath.getItems().clear();

            //Home
            path = System.getProperty("user.home");
            cboPath.getItems().add(path);
            cboPath.getSelectionModel().select(path);

            //und die Vorgabe
            path = ProgConfig.DOWNLOAD_FILE_PATH.getValue();
            if (!path.isEmpty()) {
                cboPath.getItems().add(path);
                cboPath.getSelectionModel().select(path);
            }
        }

        if (!downloadData.getDestFileName().isEmpty()) {
            txtName.setText(downloadData.getDestFileName());
        } else if (txtName.getText().isEmpty()) {
            txtName.setText(downloadData.getTitle());
        }
    }

    private void initResolution() {
        final ToggleGroup toggleGroupSize = new ToggleGroup();
        rbHd.setToggleGroup(toggleGroupSize);
        rbHigh.setToggleGroup(toggleGroupSize);
        rbSmall.setToggleGroup(toggleGroupSize);

        if (filmData == null) {
            //dann gibts nix zum Ändern
            rbHd.setDisable(true);
            rbHigh.setDisable(true);
            rbSmall.setDisable(true);
            return;
        }

        rbHd.setDisable(!filmData.isHd());
        rbSmall.setDisable(!filmData.isSmall());

        if (onlyChange) {
            //dann die Vorgaben evtl. anpassen
            if (downloadData.getUrl().equals(filmData.getUrlForResolution(FilmData.RESOLUTION_HD))) {
                ProgConfig.DOWNLOAD_RESOLUTION.setValue(FilmData.RESOLUTION_HD);

            } else if (downloadData.getUrl().equals(filmData.getUrlForResolution(FilmData.RESOLUTION_SMALL))) {
                ProgConfig.DOWNLOAD_RESOLUTION.setValue(FilmData.RESOLUTION_SMALL);

            } else {
                ProgConfig.DOWNLOAD_RESOLUTION.setValue(FilmData.RESOLUTION_NORMAL);
            }
        }

        selectResolution();

        rbHd.setOnAction(a -> {
            ProgConfig.DOWNLOAD_RESOLUTION.setValue(FilmData.RESOLUTION_HD);
            setResolution();
        });
        rbHigh.setOnAction(a -> {
            ProgConfig.DOWNLOAD_RESOLUTION.setValue(FilmData.RESOLUTION_NORMAL);
            setResolution();
        });
        rbSmall.setOnAction(a -> {
            ProgConfig.DOWNLOAD_RESOLUTION.setValue(FilmData.RESOLUTION_SMALL);
            setResolution();
        });
        setResolution();
    }

    private void selectResolution() {
        if (onlyChange) {
            //dann nach dem zu änderndem Download setzen
            rbHd.setSelected(downloadData.getUrl().equals(filmData.getUrlForResolution(FilmData.RESOLUTION_HD)));
            rbSmall.setSelected(downloadData.getUrl().equals(filmData.getUrlForResolution(FilmData.RESOLUTION_SMALL)));
            rbHigh.setSelected(downloadData.getUrl().equals(filmData.getUrlForResolution(FilmData.RESOLUTION_NORMAL)));

        } else {
            //oder nach den Standardvorgaben setzen
            switch (ProgConfig.DOWNLOAD_RESOLUTION.getValueSafe()) {
                case FilmData.RESOLUTION_HD:
                    rbHd.setSelected(true);
                    break;
                case FilmData.RESOLUTION_SMALL:
                    rbSmall.setSelected(true);
                    break;
                case FilmData.RESOLUTION_NORMAL:
                default:
                    rbHigh.setSelected(true);
                    break;
            }
        }

        if (!rbHd.isDisable() && !fileSize_HD.isEmpty()) {
            rbHd.setText("HD   [ " + fileSize_HD + " MB ]");
        } else {
            rbHd.setText("HD");
        }

        if (!fileSize_high.isEmpty()) {
            rbHigh.setText("hohe Auflösung   [ " + fileSize_high + " MB ]");
        } else {
            rbHigh.setText("hohe Auflösung");
        }

        if (!rbSmall.isDisable() && !fileSize_small.isEmpty()) {
            rbSmall.setText("niedrige Auflösung   [ " + fileSize_small + " MB ]");
        } else {
            rbSmall.setText("niedrige Auflösung");
        }
    }

    private void setResolution() {
        downloadData.setUrl(filmData.getUrlForResolution(ProgConfig.DOWNLOAD_RESOLUTION.getValueSafe()));
        pHyperlinkUrlFilm.setUrl(downloadData.urlProperty().getValueSafe());
    }

    private void initCheckBox() {
        // und jetzt noch die Listener anhängen
        final ToggleGroup toggleGroupStart = new ToggleGroup();
        rbStart.setToggleGroup(toggleGroupStart);
        rbNot.setToggleGroup(toggleGroupStart);
        rbStart.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_DIALOG_START_DOWNLOAD_NOW);
        rbNot.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_DIALOG_START_DOWNLOAD_NOT);

        chkInfo.selectedProperty().bindBidirectional(downloadData.infoFileProperty());
        chkInfo.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_INFO_FILE);

        if (filmData == null || filmData.getUrlSubtitle().isEmpty()) {
            // dann gibts keinen Subtitle
            chkSubtitle.setDisable(true);
        } else {
            chkSubtitle.selectedProperty().bindBidirectional(downloadData.subtitleProperty());
            chkSubtitle.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_SUBTITLE);
        }
    }

    private boolean check() {
        ok = false;
        if (downloadData == null) {
            PAlert.showErrorAlert("Fehlerhafter Download!", "Fehlerhafter Download!",
                    "Download konnte nicht erstellt werden.");

        } else if (path.isEmpty() || downloadData.getDestFileName().isEmpty()) {
            PAlert.showErrorAlert("Fehlerhafter Pfad/Name!", "Fehlerhafter Pfad/Name!",
                    "Pfad oder Name ist leer.");

        } else {
            if (!path.substring(path.length() - 1).equals(File.separator)) {
                path += File.separator;
            }
            if (HttpDownloadFactory.checkPathWritable(path)) {
                ok = true;
            } else {
                PAlert.showErrorAlert("Fehlerhafter Pfad/Name!", "Fehlerhafter Pfad/Name!",
                        "Pfad ist nicht beschreibbar.");
            }
        }
        return ok;
    }

    private void quit() {
        if (!ok) {
            close();
            return;
        }

        downloadData.setDestFileName(txtName.getText());
        downloadData.setDestPath(cboPath.getValue());

        //damit der Focus nicht aus der Tabelle verloren geht
        progData.mtViewerController.setFocus();
        DialogFactory.saveComboPath(cboPath, path, ProgConfig.DOWNLOAD_DIALOG_PATH_SAVING);

        if (!onlyChange) {
            progData.downloadList.addWithNr(downloadData);
            if (rbStart.isSelected()) {
                // und evtl. auch gleich starten
                progData.downloadList.startDownloads(downloadData);
            }
        }

        close();
    }

    private String getFilmSize() {
        switch (ProgConfig.DOWNLOAD_RESOLUTION.getValueSafe()) {
            case FilmData.RESOLUTION_HD:
                return fileSize_HD;

            case FilmData.RESOLUTION_SMALL:
                return fileSize_small;

            case FilmData.RESOLUTION_NORMAL:
            default:
                return fileSize_high;
        }
    }

    private void proposeDestination() {
        String stdPath, actPath;
        actPath = cboPath.getSelectionModel().getSelectedItem();

        stdPath = PSystemUtils.getStandardDownloadPath();
        actPath = DialogFactory.getNextName(stdPath, actPath, downloadData.getTheme());
        if (!cboPath.getItems().contains(actPath)) {
            cboPath.getItems().add(actPath);
        }
        cboPath.getSelectionModel().select(actPath);
    }
}
