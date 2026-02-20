package de.p2tools.mtviewer.gui;

import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2GuiTools;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PTimePicker extends VBox {
    private Spinner<Integer> spHour = new Spinner<>(0, 23, 0);
    private Spinner<Integer> spMinute = new Spinner<>(0, 59, 0);

    public PTimePicker(int hour, int minute) {
        spHour = new Spinner<>(0, 23, hour);
        spMinute = new Spinner<>(0, 59, minute);

        make();
    }

    public ReadOnlyObjectProperty<Integer> hourPropProperty() {
        return spHour.valueProperty();
    }

    public ReadOnlyObjectProperty<Integer> minutePropProperty() {
        return spMinute.valueProperty();
    }

    private void make() {
        spHour.setEditable(true);
        spMinute.setEditable(true);

        HBox hBox = new HBox(P2LibConst.SPACING_HBOX);
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(spHour, P2GuiTools.getHDistance(20), spMinute, new Label(" (HH:MM)"));
        getChildren().add(hBox);
    }
}
