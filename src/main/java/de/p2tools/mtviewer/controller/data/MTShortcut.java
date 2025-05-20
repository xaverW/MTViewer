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
import de.p2tools.p2lib.tools.shortcut.P2ShortcutKey;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashSet;

public class MTShortcut {

    // Menü
    public static final P2ShortcutKey SHORTCUT_QUIT_PROGRAM =
            new P2ShortcutKey(ProgConfig.SHORTCUT_QUIT_PROGRAM, ProgConfig.SHORTCUT_QUIT_PROGRAM_INIT,
                    "Programm beenden",
                    "Das Programm wird beendet. Wenn noch ein Download läuft, wird in einem Dialog abgefragt, was getan werden soll.");

    // Tabelle Filme
    public static final P2ShortcutKey SHORTCUT_SHOW_INFOS =
            new P2ShortcutKey(ProgConfig.SHORTCUT_SHOW_INFOS, ProgConfig.SHORTCUT_SHOW_INFOS_INIT,
                    "Infos anzeigen",
                    "Unter der Tabelle \"Filme\" die Infos anzeigen.");

    public static final P2ShortcutKey SHORTCUT_SHOW_FILTER =
            new P2ShortcutKey(ProgConfig.SHORTCUT_SHOW_FILTER, ProgConfig.SHORTCUT_SHOW_FILTER_INIT,
                    "Filter anzeigen",
                    "Neben der Tabelle \"Filme\" die Filter anzeigen.");

    public static final P2ShortcutKey SHORTCUT_INFO_FILM =
            new P2ShortcutKey(ProgConfig.SHORTCUT_INFO_FILM, ProgConfig.SHORTCUT_INFO_FILM_INIT,
                    "Filminformation anzeigen",
                    "In der Tabelle \"Filme\" die Infos des markierten Films anzeigen.");

    public static final P2ShortcutKey SHORTCUT_PLAY_FILM =
            new P2ShortcutKey(ProgConfig.SHORTCUT_PLAY_FILM, ProgConfig.SHORTCUT_PLAY_FILM_INIT,
                    "Film abspielen",
                    "Der markierte Film in der Tabelle \"Filme\" wird abgespielt.");
    public static final P2ShortcutKey SHORTCUT_SAVE_FILM =
            new P2ShortcutKey(ProgConfig.SHORTCUT_SAVE_FILM, ProgConfig.SHORTCUT_SAVE_FILM_INIT,
                    "Film speichern",
                    "Der markierte Film in der Tabelle \"Filme\" wird gespeichert.");

    private static ObservableList<P2ShortcutKey> shortcutList = FXCollections.observableArrayList();

    public MTShortcut() {
        shortcutList.add(SHORTCUT_QUIT_PROGRAM);

        shortcutList.add(SHORTCUT_SHOW_INFOS);
        shortcutList.add(SHORTCUT_INFO_FILM);
        shortcutList.add(SHORTCUT_PLAY_FILM);
    }

    public static synchronized ObservableList<P2ShortcutKey> getShortcutList() {
        return shortcutList;
    }

    public static synchronized boolean checkDoubleShortcutList() {
        HashSet<String> hashSet = new HashSet<>();
        for (P2ShortcutKey ps : shortcutList) {
            if (!hashSet.add(ps.getActShortcut())) {
                return true;
            }
        }
        return false;
    }
}
