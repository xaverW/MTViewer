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


package de.p2tools.mtviewer.controller.data;

import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.p2Lib.tools.shortcut.PShortcut;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashSet;

public class MTShortcut {

    // Menü
    public static final PShortcut SHORTCUT_QUIT_PROGRAM =
            new PShortcut(ProgConfig.SHORTCUT_QUIT_PROGRAM, ProgConfig.SHORTCUT_QUIT_PROGRAM_INIT,
                    "Programm beenden",
                    "Das Programm wird beendet. Wenn noch ein Download läuft, wird in einem Dialog abgefragt, was getan werden soll.");

    // Tabelle Filme
    public static final PShortcut SHORTCUT_SHOW_INFOS =
            new PShortcut(ProgConfig.SHORTCUT_SHOW_INFOS, ProgConfig.SHORTCUT_SHOW_INFOS_INIT,
                    "Infos anzeigen",
                    "Unter der Tabelle \"Filme\" die Infos anzeigen.");

    public static final PShortcut SHORTCUT_INFO_FILM =
            new PShortcut(ProgConfig.SHORTCUT_INFO_FILM, ProgConfig.SHORTCUT_INFO_FILM_INIT,
                    "Filminformation anzeigen",
                    "In der Tabelle \"Filme\" die Infos des markierten Films anzeigen.");

    public static final PShortcut SHORTCUT_PLAY_FILM =
            new PShortcut(ProgConfig.SHORTCUT_PLAY_FILM, ProgConfig.SHORTCUT_PLAY_FILM_INIT,
                    "Film abspielen",
                    "Der markierte Film in der Tabelle \"Filme\" wird abgespielt.");

    private static ObservableList<PShortcut> shortcutList = FXCollections.observableArrayList();

    public MTShortcut() {
        shortcutList.add(SHORTCUT_QUIT_PROGRAM);

        shortcutList.add(SHORTCUT_SHOW_INFOS);
        shortcutList.add(SHORTCUT_INFO_FILM);
        shortcutList.add(SHORTCUT_PLAY_FILM);
    }

    public static synchronized ObservableList<PShortcut> getShortcutList() {
        return shortcutList;
    }

    public static synchronized boolean checkDoubleShortcutList() {
        HashSet<String> hashSet = new HashSet<>();
        for (PShortcut ps : shortcutList) {
            if (!hashSet.add(ps.getActShortcut())) {
                return true;
            }
        }
        return false;
    }
}
