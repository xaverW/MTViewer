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

package de.p2tools.mtviewer.controller;

import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgConst;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.config.ProgInfos;
import de.p2tools.mtviewer.controller.load.LoadAudioFactory;
import de.p2tools.mtviewer.controller.load.LoadFilmFactory;
import de.p2tools.p2lib.tools.P2InfoFactory;
import de.p2tools.p2lib.tools.log.P2Log;
import de.p2tools.p2lib.tools.log.P2LogMessage;

import java.util.ArrayList;

public class ProgStartAfterGui {

    private ProgStartAfterGui() {
    }

    /**
     * alles was nach der GUI gemacht werden soll z.B.
     * Filmliste beim Programmstart!! laden
     */
    public static void doWorkAfterGui() {
        startMsg();
        setTitle();
        ProgData.getInstance().pEventHandler.startTimer();

        //die gespeicherte Filmliste laden
        LoadFilmFactory.loadFilmListProgStart();
        LoadAudioFactory.loadAudioListProgStart();
    }

    public static void startMsg() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Verzeichnisse:");
        list.add("Programmpfad: " + ProgInfos.getPathJar());
        list.add("Verzeichnis Einstellungen: " + ProgInfos.getSettingsDirectory_String());

        P2LogMessage.startMsg(ProgConst.PROGRAM_NAME, list);
        P2Log.sysLog(list);
        ProgConfig.getInstance().writeConfigs();
        ;
    }

    private static void setTitle() {
        if (ProgData.debug) {
            ProgData.getInstance().primaryStage.setTitle(ProgConst.PROGRAM_NAME + " " + P2InfoFactory.getProgVersion() + " / DEBUG");
        } else {
            ProgData.getInstance().primaryStage.setTitle(ProgConst.PROGRAM_NAME + " " + P2InfoFactory.getProgVersion());
        }
    }
}
