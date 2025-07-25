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

package de.p2tools.mtviewer.controller.data.download;

import de.p2tools.mtviewer.controller.config.ProgConst;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.p2lib.configfile.pdata.P2DataList;
import de.p2tools.p2lib.mediathek.filmdata.Filmlist;
import de.p2tools.p2lib.tools.duration.P2Duration;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

public class DownloadList extends SimpleListProperty<DownloadData> implements P2DataList<DownloadData> {

    public static final String TAG = "DownloadList";
    private final ProgData progData;
    private final DownloadListStarts downloadListStarts;
    private final DownloadListStartStop downloadListStartStop;
    private final ObservableList<DownloadData> undoList = FXCollections.observableArrayList();
    ;
    private BooleanProperty downloadsChanged = new SimpleBooleanProperty(true);

    public DownloadList(ProgData progData) {
        super(FXCollections.observableArrayList());
        this.progData = progData;
        this.downloadListStarts = new DownloadListStarts(progData, this);
        this.downloadListStartStop = new DownloadListStartStop(progData, this);
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public String getComment() {
        return "Liste aller Downloads";
    }

    @Override
    public DownloadData getNewItem() {
        return new DownloadData();
    }

    @Override
    public void addNewItem(Object obj) {
        if (obj.getClass().equals(DownloadData.class)) {
            add((DownloadData) obj);
        }
    }

    public ObservableList<DownloadData> getUndoList() {
        return undoList;
    }

    public synchronized void addDownloadUndoList(List<DownloadData> list) {
        undoList.clear();
        undoList.addAll(list);
    }

    public boolean getDownloadsChanged() {
        return downloadsChanged.get();
    }

    synchronized void setDownloadsChanged() {
        downloadsChanged.set(!downloadsChanged.get());
    }

    public BooleanProperty downloadsChangedProperty() {
        return downloadsChanged;
    }

    public void sort() {
        Collections.sort(this);
    }

    @Override
    public synchronized boolean add(DownloadData d) {
        return super.add(d);
    }

    @Override
    public synchronized boolean addAll(Collection<? extends DownloadData> elements) {
        return super.addAll(elements);
    }

    public synchronized boolean addWithNr(DownloadData e) {
        final boolean ret = super.add(e);
        setNumbersInList();
        return ret;
    }

    public synchronized void addWithNr(List<DownloadData> list) {
        list.stream().forEach(download -> super.add(download));
        setNumbersInList();
    }

    @Override
    public synchronized boolean removeAll(Collection<?> objects) {
        return super.removeAll(objects);
    }

    public synchronized int countStartedAndRunningDownloads() {
        // es wird nach noch nicht fertigen, gestarteten Downloads gesucht
        int ret = 0;
        for (final DownloadData download : this) {
            if (download.isStateStartedWaiting() || download.isStateStartedRun()) {
                ++ret;
            }
        }
        return ret;
    }


    public synchronized void addFilmInList(Filmlist filmlist) {
        // bei einmal Downloads nach einem Programmstart/Neuladen der Filmliste
        // den Film wieder eintragen
        P2Duration.counterStart("DownloadList.addFilmInList");

        int counter = 50; //todo das dauert sonst viel zu lang
        for (DownloadData d : this) {
            --counter;
            if (counter < 0) {
                break;
            }
            d.setFilm(filmlist.getFilmByUrl_small_high_hd(d.getUrl())); //todo sollen da wirklich alle Filmfelder gesetzt werden??
            d.setSizeDownloadFromFilm();
        }

        P2Duration.counterStop("DownloadList.addFilmInList");
    }

    public synchronized void preferDownloads(ArrayList<DownloadData> prefDownList) {
        // macht nur Sinn, wenn der Download auf Laden wartet: Init
        // todo auch bei noch nicht gestarteten ermöglichen
        prefDownList.removeIf(d -> d.getState() != DownloadConstants.STATE_STARTED_WAITING);
        if (prefDownList.isEmpty()) {
            return;
        }

        // zum neu nummerieren der alten Downloads
        List<DownloadData> list = new ArrayList<>();
        for (final DownloadData download : this) {
            final int i = download.getNo();
            if (i < ProgConst.NUMBER_NOT_EXISTS) {
                list.add(download);
            }
        }
        prefDownList.stream().forEach(d -> list.remove(d));
        Collections.sort(list, new Comparator<DownloadData>() {
            @Override
            public int compare(DownloadData d1, DownloadData d2) {
                return (d1.getNo() < d2.getNo()) ? -1 : 1;
            }
        });
        int addNr = prefDownList.size();
        for (final DownloadData download : list) {
            ++addNr;
            download.setNo(addNr);
        }

        // und jetzt die vorgezogenen Downloads nummerieren
        int i = 1;
        for (final DownloadData dataDownload : prefDownList) {
            dataDownload.setNo(i++);
        }
    }

    public synchronized DownloadData getDownloadByUrl(String url) {
        DownloadData ret = null;
        for (final DownloadData download : this) {
            if (download.getUrl().equals(url)) {
                ret = download;
                break;
            }
        }
        return ret;
    }


    public synchronized DownloadData getDownloadUrlFilm(String urlFilm) {
        for (final DownloadData dataDownload : this) {
            if (dataDownload.getFilmUrl().equals(urlFilm)) {
                return dataDownload;
            }
        }
        return null;
    }

    public synchronized void cleanUpList() {
        // fertige Downloads löschen, fehlerhafte zurücksetzen
        boolean found = false;
        Iterator<DownloadData> it = this.iterator();
        while (it.hasNext()) {
            DownloadData download = it.next();
            if (download.isStateInit() ||
                    download.isStateStoped()) {
                continue;
            }
            if (download.isStateFinished()) {
                // alles was fertig/fehlerhaft ist, kommt beim putzen weg
                it.remove();
                found = true;
            } else if (download.isStateError()) {
                // fehlerhafte werden zurückgesetzt
                download.resetDownload();
                found = true;
            }
        }

        if (found) {
            setDownloadsChanged();
        }
    }

    public synchronized List<DownloadData> getListOfStartsNotFinished() {
        return downloadListStarts.getListOfStartsNotFinished();
    }

    public synchronized List<DownloadData> getListOfStartsNotLoading() {
        return downloadListStarts.getListOfStartsNotLoading();
    }

    public synchronized DownloadData getRestartDownload() {
        return downloadListStarts.getRestartDownload();
    }

    public synchronized DownloadData getNextStart() {
        return downloadListStarts.getNextStart();
    }

    public synchronized void resetPlacedBack() {
        // zurückgestellte wieder aktivieren
        forEach(d -> d.setPlacedBack(false));
    }

    // ==============================
    // DownloadListStartStop
    public synchronized void stopDownloads(ArrayList<DownloadData> list) {
        if (downloadListStartStop.stopDownloads(list)) {
            setDownloadsChanged();
        }
    }

    public synchronized void delDownloads(DownloadData download) {
        downloadListStartStop.delDownloads(download);
    }

    public synchronized void delDownloads(ArrayList<DownloadData> list) {
        if (downloadListStartStop.delDownloads(list)) {
            setDownloadsChanged();
        }
    }

    public synchronized void putBackDownloads(ArrayList<DownloadData> list) {
        if (downloadListStartStop.putBackDownloads(list)) {
            setDownloadsChanged();
        }
    }

    public void startDownloads(DownloadData download) {
        downloadListStartStop.startDownloads(download);
        setDownloadsChanged();
    }

    public void startDownloads() {
        startDownloads(this, false);
    }

    public void startDownloads(Collection<DownloadData> list) {
        if (downloadListStartStop.startDownloads(list, false)) {
            setDownloadsChanged();
        }
    }

    public void startDownloads(Collection<DownloadData> list, boolean alsoFinished) {
        if (downloadListStartStop.startDownloads(list, alsoFinished)) {
            setDownloadsChanged();
        }
    }

    public synchronized void setNumbersInList() {
        int i = getNextNumber();
        for (final DownloadData download : this) {
            if (download.isStarted()) {
                // gestartete Downloads ohne!! Nummer nummerieren
                if (download.getNo() == ProgConst.NUMBER_NOT_EXISTS) {
                    download.setNo(i++);
                }

            } else {
                // nicht gestartete Downloads
                download.setNo(ProgConst.NUMBER_NOT_EXISTS);
            }
        }
    }

    public synchronized void renumberList(int addNr) {
        for (final DownloadData download : this) {
            final int i = download.getNo();
            if (i < ProgConst.NUMBER_NOT_EXISTS) {
                download.setNo(i + addNr);
            }
        }
    }

    private int getNextNumber() {
        int i = 1;
        for (final DownloadData download : this) {
            if (download.getNo() < ProgConst.NUMBER_NOT_EXISTS && download.getNo() >= i) {
                i = download.getNo() + 1;
            }
        }
        return i;
    }

    public synchronized void addNumber(ArrayList<DownloadData> downloads) {
        int i = getNextNumber();
        for (DownloadData download : downloads) {
            download.setNo(i++);
        }
    }
}
