package de.p2tools.mtviewer.gui;

import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.load.LoadAudioFactory;
import de.p2tools.mtviewer.controller.load.LoadFilmFactory;
import de.p2tools.p2lib.guitools.P2GuiTools;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;

public class FilmGuiTop extends HBox {
    private final ProgData progData;
    private final Button btnFilmlist = new Button("Filmliste");

    public FilmGuiTop() {
        this.progData = ProgData.getInstance();
        getChildren().addAll(btnFilmlist, P2GuiTools.getHBoxGrower(), new ProgMenu());
        setAlignment(Pos.CENTER_RIGHT);
        setPadding(new Insets(5, 15, 5, 15));
        btnFilmlist.getStyleClass().addAll("btnFunction", "btnFunc-4");

        btnFilmlist.setTooltip(new Tooltip("Eine neue Filmliste laden.\n" +
                "Wenn die Filmliste nicht zu alt ist, wird nur ein Update geladen.\n" +
                "Mit der rechten Maustaste wird immer die komplette Filmliste geladen."));

        btnFilmlist.setOnAction(e -> {
            LoadFilmFactory.loadListButton(false);
        });
        btnFilmlist.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                LoadFilmFactory.loadListButton(true);
                LoadAudioFactory.loadListButton();
            }
            if (mouseEvent.getButton().equals(MouseButton.MIDDLE)) {
                LoadAudioFactory.loadListButton();
            }
        });
    }
}
