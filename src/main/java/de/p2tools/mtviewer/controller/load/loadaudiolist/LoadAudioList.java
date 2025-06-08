/*
 * P2Tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
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

package de.p2tools.mtviewer.controller.load.loadaudiolist;

import de.p2tools.mtviewer.controller.config.PEvents;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.config.ProgInfos;
import de.p2tools.mtviewer.controller.load.LoadAudioFactory;
import de.p2tools.mtviewer.controller.load.LoadAudioFactoryDto;
import de.p2tools.p2lib.mtfilm.tools.LoadFactoryConst;
import de.p2tools.p2lib.p2event.P2Event;
import de.p2tools.p2lib.tools.date.P2LDateFactory;
import de.p2tools.p2lib.tools.duration.P2Duration;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoadAudioList {

    private static final AtomicBoolean stop = new AtomicBoolean(false); // damit kann das Laden gestoppt werden kann

    public LoadAudioList() {
    }

    public synchronized boolean isStop() {
        return stop.get();
    }

    public synchronized void setStop(boolean set) {
        stop.set(set);
    }

    public synchronized void setMax(int max) {
    }

    /**
     * Audioliste beim Programmstart laden
     */
    public void loadAudioListAtProgStart() {
        // nur einmal direkt nach dem Programmstart
        new Thread(() -> {
            P2Duration.counterStart("loadAudioListAtProgStart");
            ProgData.getInstance().pEventHandler.notifyListener(
                    new P2Event(PEvents.LOAD_AUDIO_LIST_START, "Programmstart, Liste laden", LoadAudioFactory.PROGRESS_INDETERMINATE));
            final List<String> logList = new ArrayList<>();

            logList.add("## " + P2Log.LILNE1);
            logList.add("## " + P2Log.LILNE1);
            logList.add("## Audioliste laden");
            logList.add("## Audioliste beim **Programmstart** laden - start");
            logList.add("## ");

            loadAudioListAtProgStart(logList);
            afterLoading(logList);

            logList.add("## Audioliste beim Programmstart laden - ende");
            logList.add("## " + P2Log.LILNE1);
            logList.add("## " + P2Log.LILNE1);
            logList.add("");

            P2Log.emptyLine();
            P2Log.sysLog(logList);
            P2Log.emptyLine();
            P2Duration.counterStop("loadAudioListAtProgStart");
        }).start();
    }

    public void loadNewAudioListFromWeb() {
        // aus dem Menü oder Button in den Einstellungen
        new Thread(() -> {
            //damit wird eine neue Liste (Web) geladen UND auch gleich im Config-Ordner gespeichert
            P2Duration.counterStart("loadNewAudioListFromWeb");
            ProgData.getInstance().pEventHandler.notifyListener(
                    new P2Event(PEvents.LOAD_AUDIO_LIST_START, "Audioliste aus dem Web laden", LoadAudioFactory.PROGRESS_INDETERMINATE));
            final List<String> logList = new ArrayList<>();

            logList.add("## " + P2Log.LILNE1);
            logList.add("## " + P2Log.LILNE1);
            logList.add("## Audioliste laden");
            logList.add("## Audioliste aus dem Web laden - start");
            logList.add("## Alte Liste erstellt  am: " + LoadAudioFactoryDto.audioListDate.getValueSafe());
            logList.add("##            Anzahl Beiträge: " + LoadAudioFactoryDto.audioListAkt.size());
            logList.add("##");

            LoadFactoryConst.audioInitNecessary = true;
            new ReadWebAudioList(logList).readWebList(ProgInfos.getAndMakeAudioListFile());
            afterLoading(logList);

            logList.add("## Audioliste aus dem Web laden - ende");
            logList.add("## " + P2Log.LILNE1);
            logList.add("## " + P2Log.LILNE1);
            logList.add("");
            P2Log.emptyLine();
            P2Log.sysLog(logList);
            P2Log.emptyLine();

            P2Duration.counterStop("loadNewAudioListFromWeb");
        }).start();
    }

    /**
     * Audioliste beim Programmstart laden
     */
    private void loadAudioListAtProgStart(List<String> logList) {
        // ProgStart, hier wird die gespeicherte Audioliste geladen und
        // wenn zu alt, wird eine neue aus dem Web geladen

        // ====
        // erster Start
        if (LoadAudioFactoryDto.firstProgramStart) {
            // gespeicherte Audioliste laden, macht beim ersten Programmstart keinen Sinn
            logList.add("## Erster Programmstart -> Liste aus dem Web laden");

            LoadFactoryConst.audioInitNecessary = true;
            new ReadWebAudioList(logList).readWebList(ProgInfos.getAndMakeAudioListFile());

            P2Duration.onlyPing("Erster Programmstart: Neu Audioliste aus dem Web geladen");
            ProgData.getInstance().pEventHandler.notifyListener(
                    new P2Event(PEvents.LOAD_AUDIO_LIST_LOADED, "Audios verarbeiten", LoadAudioFactory.PROGRESS_INDETERMINATE));
            return;
        }


        // ====
        // gespeicherte Liste -> User will kein Update
        if (!LoadAudioFactoryDto.loadNewAudioListOnProgramStart) {
            logList.add("## Beim Programmstart soll keine neue Liste geladen werden");
            logList.add("## dann gespeicherte Liste laden");

            LoadFactoryConst.audioInitNecessary = true;
            new ReadLocalAudioList(logList).readLocalList(ProgInfos.getAndMakeAudioListFile());

            logList.add("## Gespeicherte Liste geladen");
            ProgData.getInstance().pEventHandler.notifyListener(
                    new P2Event(PEvents.LOAD_AUDIO_LIST_LOADED, "Audios verarbeiten", LoadAudioFactory.PROGRESS_INDETERMINATE));
            return;
        }


        // ===
        // laden mit evtl. Web-Update
        boolean loadFromWeb = false;
        //dann soll eine neue Liste beim Programmstart geladen, wenn nötig
        if (LoadAudioFactory.isNotFromToday(LoadAudioFactoryDto.audioListDate.getValueSafe())) {
            //gespeicherte Liste zu alt > Hash
            logList.add("## Gespeicherte Audioliste ist zu alt: " + LoadAudioFactoryDto.audioListDate.getValueSafe());
            logList.add("## Zuerst gespeicherte Liste laden");

            loadFromWeb = true;
            LoadFactoryConst.audioInitNecessary = false;
            new ReadLocalAudioList(logList).readLocalList(ProgInfos.getAndMakeAudioListFile()); // Liste in new laden

            logList.add("## Programmstart: Gespeicherte Liste geladen");

        } else {
            // nicht zu alt
            logList.add("## Gespeicherte Audioliste ist nicht zu alt: " + LoadAudioFactoryDto.audioListDate.getValueSafe());
            logList.add("## Gespeicherte Liste laden");

            LoadFactoryConst.audioInitNecessary = true;
            new ReadLocalAudioList(logList).readLocalList(ProgInfos.getAndMakeAudioListFile()); // Liste in new laden

            logList.add("## Programmstart: Gespeicherte Liste geladen");
        }


        if (LoadAudioFactoryDto.audioListNew.isEmpty() || loadFromWeb) {
            //dann war sie zu alt oder ist leer
            logList.add("## " + P2Log.LILNE3);
            logList.add("## Gespeicherte Audioliste ist leer oder zu alt -> neue Audioliste aus dem Web laden");
            ProgData.getInstance().pEventHandler.notifyListener(
                    new P2Event(PEvents.LOAD_AUDIO_LIST_PROGRESS, "Audioliste ist zu alt, eine neue laden", LoadAudioFactory.PROGRESS_INDETERMINATE));

            LoadFactoryConst.audioInitNecessary = true;
            new ReadWebAudioList(logList).readWebList(ProgInfos.getAndMakeAudioListFile());

            P2Duration.onlyPing("Programmstart: Neu Audioliste aus dem Web geladen");
        }


        if (LoadAudioFactoryDto.audioListNew.isEmpty()) {
            // dann hat alles nicht geklappt?
            logList.add("## Das Laden der Liste hat nicht geklappt");
            logList.add("## Noch ein Versuch: Gespeicherte Liste laden");

            LoadFactoryConst.audioInitNecessary = true;
            new ReadLocalAudioList(logList).readLocalList(ProgInfos.getAndMakeAudioListFile());

            logList.add("## Gespeicherte Liste geladen");
        }

        ProgData.getInstance().pEventHandler.notifyListener(
                new P2Event(PEvents.LOAD_AUDIO_LIST_LOADED, "Audios verarbeiten", LoadAudioFactory.PROGRESS_INDETERMINATE));
    }

    // #######################################
    // #######################################
    private void afterLoading(List<String> logList) {
        logList.add("##");
        logList.add("## Jetzige Liste erstellt am: " + P2LDateFactory.getNowString());
        logList.add("##   Anzahl Audios: " + LoadAudioFactoryDto.audioListNew.size());
        logList.add("##");
        logList.add("## " + P2Log.LILNE2);
        logList.add("##");

        ProgData.getInstance().pEventHandler.notifyListener(
                new P2Event(PEvents.LOAD_AUDIO_LIST_LOADED, "Audios markieren, Themen suchen", LoadAudioFactory.PROGRESS_INDETERMINATE));

        //die List wieder füllen
        logList.add("## ==> und jetzt die Audioliste wieder füllen :)");
        Platform.runLater(() -> {
            LoadAudioFactoryDto.audioListAkt.sender = LoadAudioFactoryDto.audioListNew.sender;
            LoadAudioFactoryDto.audioListAkt.metaData = LoadAudioFactoryDto.audioListNew.metaData;
            LoadAudioFactoryDto.audioListAkt.setAll(LoadAudioFactoryDto.audioListNew);
            LoadAudioFactoryDto.audioListNew.clear();
            ProgData.getInstance().pEventHandler.notifyListener(new P2Event(PEvents.LOAD_AUDIO_LIST_FINISHED));
        });
    }
}