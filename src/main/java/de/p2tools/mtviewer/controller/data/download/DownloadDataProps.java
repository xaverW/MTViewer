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

import de.p2tools.p2Lib.configFile.config.*;
import de.p2tools.p2Lib.configFile.pData.PDataSample;
import de.p2tools.p2Lib.mtDownload.DownloadSize;
import de.p2tools.p2Lib.mtFilm.tools.Data;
import de.p2tools.p2Lib.tools.date.PDate;
import de.p2tools.p2Lib.tools.date.PDateFactory;
import de.p2tools.p2Lib.tools.date.PDateProperty;
import de.p2tools.p2Lib.tools.file.PFileUtils;
import javafx.application.Platform;
import javafx.beans.property.*;

import java.util.ArrayList;

public class DownloadDataProps extends PDataSample<DownloadData> {

    public static final String TAG = "DownloadData";
    private final IntegerProperty no = new SimpleIntegerProperty(DownloadConstants.DOWNLOAD_NUMBER_NOT_STARTED);
    private final IntegerProperty filmNr = new SimpleIntegerProperty(DownloadConstants.FILM_NUMBER_NOT_FOUND);
    private final StringProperty channel = new SimpleStringProperty("");
    private final StringProperty theme = new SimpleStringProperty("");
    private final StringProperty title = new SimpleStringProperty("");
    private final IntegerProperty state = new SimpleIntegerProperty(DownloadConstants.STATE_INIT);
    private final IntegerProperty guiState = new SimpleIntegerProperty(DownloadConstants.STATE_INIT);
    private final DoubleProperty progress = new SimpleDoubleProperty(DownloadConstants.PROGRESS_NOT_STARTED);
    private final DoubleProperty guiProgress = new SimpleDoubleProperty(DownloadConstants.PROGRESS_NOT_STARTED);
    private final StringProperty remaining = new SimpleStringProperty("");
    private final StringProperty bandwidth = new SimpleStringProperty("");
    private final DownloadSize downloadSize = new DownloadSize();
    private final PDateProperty filmDate = new PDateProperty(new PDate(0));
    private final StringProperty time = new SimpleStringProperty("");
    private final IntegerProperty durationMinute = new SimpleIntegerProperty(0);
    private final BooleanProperty hd = new SimpleBooleanProperty(false);
    private final BooleanProperty ut = new SimpleBooleanProperty(false);
    private final BooleanProperty geoBlocked = new SimpleBooleanProperty(false);
    private final StringProperty filmUrl = new SimpleStringProperty(""); //in normaler Auflösung
    private final StringProperty url = new SimpleStringProperty(""); //in der gewählte Auflösung
    private final StringProperty urlSubtitle = new SimpleStringProperty("");
    private final StringProperty destFileName = new SimpleStringProperty("");
    private final StringProperty destPath = new SimpleStringProperty("");
    //    private final StringProperty destPathFile = new SimpleStringProperty("");
    private final BooleanProperty placedBack = new SimpleBooleanProperty(false);
    private final BooleanProperty infoFile = new SimpleBooleanProperty(false);
    private final BooleanProperty subtitle = new SimpleBooleanProperty(false);
    public final Property[] properties = {no, filmNr, channel, theme, title,
            state, progress, remaining, bandwidth, downloadSize,
            filmDate, time, durationMinute,
            hd, ut, geoBlocked, filmUrl, url, urlSubtitle,
            destFileName, destPath, /*destPathFile,*/
            placedBack, infoFile, subtitle};

    DownloadDataProps() {
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public String getComment() {
        return "DownloadData";
    }

    @Override
    public Config[] getConfigsArr() {
        ArrayList<Config> list = new ArrayList<>();
        list.add(new ConfigExtra_intProp("no", DownloadFieldNames.DOWNLOAD_NO, no));
        list.add(new ConfigExtra_intProp("filmNr", DownloadFieldNames.DOWNLOAD_FILM_NO, filmNr));
        list.add(new ConfigExtra_stringProp("channel", DownloadFieldNames.DOWNLOAD_CHANNEL, channel));
        list.add(new ConfigExtra_stringProp("theme", DownloadFieldNames.DOWNLOAD_THEME, theme));
        list.add(new ConfigExtra_stringProp("title", DownloadFieldNames.DOWNLOAD_TITLE, title));
        list.add(new ConfigExtra_intProp("state", DownloadFieldNames.DOWNLOAD_STATE, state));
        list.add(new ConfigExtra_doubleProp("progress", DownloadFieldNames.DOWNLOAD_PROGRESS, progress));
        list.add(new ConfigExtra_stringProp("remaining", DownloadFieldNames.DOWNLOAD_REMAINING_TIME, remaining));
        list.add(new ConfigExtra_stringProp("bandwidth", DownloadFieldNames.DOWNLOAD_BANDWIDTH, bandwidth));
        list.add(new ConfigExtra_stringProp("time", DownloadFieldNames.DOWNLOAD_TIME, time));
        list.add(new ConfigExtra_intProp("durationMinute", DownloadFieldNames.DOWNLOAD_DURATION, durationMinute));
        list.add(new ConfigExtra_boolProp("hd", DownloadFieldNames.DOWNLOAD_HD, hd));
        list.add(new ConfigExtra_boolProp("ut", DownloadFieldNames.DOWNLOAD_UT, ut));
        list.add(new ConfigExtra_boolProp("geoBlocked", DownloadFieldNames.DOWNLOAD_GEO, geoBlocked));
        list.add(new ConfigExtra_stringProp("filmUrl", DownloadFieldNames.DOWNLOAD_FILM_URL, filmUrl));
        list.add(new ConfigExtra_stringProp("url", DownloadFieldNames.DOWNLOAD_URL, url));
        list.add(new ConfigExtra_stringProp("urlSubtitle", DownloadFieldNames.DOWNLOAD_URL_SUBTITLE, urlSubtitle));
        list.add(new ConfigExtra_stringProp("destFileName", DownloadFieldNames.DOWNLOAD_DEST_FILE_NAME, destFileName));
        list.add(new ConfigExtra_stringProp("destPath", DownloadFieldNames.DOWNLOAD_DEST_PATH, destPath));
//        list.add(new ConfigExtra_stringProp("destPathFile", DownloadFieldNames.DOWNLOAD_DEST_PATH_FILE_NAME, destPathFile));
        list.add(new ConfigExtra_boolProp("placedBack", DownloadFieldNames.DOWNLOAD_PLACED_BACK, placedBack));
        list.add(new ConfigExtra_boolProp("infoFile", DownloadFieldNames.DOWNLOAD_INFO_FILE, infoFile));
        list.add(new ConfigExtra_boolProp("subtitle", DownloadFieldNames.DOWNLOAD_SUBTITLE, subtitle));

        return list.toArray(new Config[]{});
    }

    @Override
    public int compareTo(DownloadData arg0) {
        int ret;
        if ((ret = sorter.compare(getChannel(), arg0.getChannel())) == 0) {
            return sorter.compare(getTheme(), arg0.getTheme());
        }
        return ret;
    }

    public PDate getFilmDate() {
        return filmDate.get();
    }

    public void setFilmDate(PDate filmDate) {
        this.filmDate.set(filmDate);
    }

    public ObjectProperty<PDate> filmDateProperty() {
        return filmDate;
    }

    public void setFilmDate(String date, String time) {
        PDate d = new PDate();
        d.setPDate(date, time, PDateFactory.F_FORMAT_dd_MM_yyyy, PDateFactory.F_FORMAT_dd_MM_yyyyHH_mm_ss);
        this.filmDate.setValue(d);
    }

    // GuiProps
    public int getGuiState() {
        return guiState.get();
    }

    public IntegerProperty guiStateProperty() {
        return guiState;
    }

    public double getGuiProgress() {
        return guiProgress.get();
    }

    public DoubleProperty guiProgressProperty() {
        return guiProgress;
    }

    public int getNo() {
        return no.get();
    }

    public void setNo(int no) {
        this.no.set(no);
    }

    public IntegerProperty noProperty() {
        return no;
    }

    public int getFilmNr() {
        return filmNr.get();
    }

    public void setFilmNr(int filmNr) {
        this.filmNr.set(filmNr);
    }

    public IntegerProperty filmNrProperty() {
        return filmNr;
    }

    public String getChannel() {
        return channel.get();
    }

    public void setChannel(String channel) {
        this.channel.set(channel);
    }

    public StringProperty channelProperty() {
        return channel;
    }

    public String getTheme() {
        return theme.get();
    }

    public void setTheme(String theme) {
        this.theme.set(theme);
    }

    public StringProperty themeProperty() {
        return theme;
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public StringProperty titleProperty() {
        return title;
    }

    public int getState() {
        return state.get();
    }

    public void setState(int state) {
        this.state.set(state);
        Platform.runLater(() -> guiState.setValue(state));
    }

    public IntegerProperty stateProperty() {
        return state;
    }

    public Double getProgress() {
        return progress.getValue();
    }

    public void setProgress(double progress) {
        this.progress.setValue(progress);
        Platform.runLater(() -> guiProgress.setValue(progress));
    }

    public DoubleProperty progressProperty() {
        return progress;
    }

    public String getRemaining() {
        return remaining.get();
    }

    public void setRemaining(String remaining) {
        this.remaining.set(remaining);
    }

    public StringProperty remainingProperty() {
        return remaining;
    }

    public String getBandwidth() {
        return bandwidth.get();
    }

    public void setBandwidth(String bandwidth) {
        this.bandwidth.set(bandwidth);
    }

    public StringProperty bandwidthProperty() {
        return bandwidth;
    }

    public DownloadSize getDownloadSize() {
        return downloadSize;
    }

    public DownloadSize downloadSizeProperty() {
        return downloadSize;
    }

    public String getTime() {
        return time.get();
    }

    public void setTime(String time) {
        this.time.set(time);
    }

    public StringProperty timeProperty() {
        return time;
    }

    public int getDurationMinute() {
        return durationMinute.get();
    }

    public void setDurationMinute(int durationMinute) {
        this.durationMinute.set(durationMinute);
    }

    public IntegerProperty durationMinuteProperty() {
        return durationMinute;
    }

    public boolean isHd() {
        return hd.get();
    }

    public void setHd(boolean hd) {
        this.hd.set(hd);
    }

    public BooleanProperty hdProperty() {
        return hd;
    }

    public boolean isUt() {
        return ut.get();
    }

    public void setUt(boolean ut) {
        this.ut.set(ut);
    }

    public BooleanProperty utProperty() {
        return ut;
    }

    public boolean isGeoBlocked() {
        return geoBlocked.get();
    }

    public void setGeoBlocked(boolean geoBlocked) {
        this.geoBlocked.set(geoBlocked);
    }

    public BooleanProperty geoBlockedProperty() {
        return geoBlocked;
    }

    public String getFilmUrl() {
        return filmUrl.get();
    }

    public void setFilmUrl(String filmUrl) {
        this.filmUrl.set(filmUrl);
    }

    public StringProperty filmUrlProperty() {
        return filmUrl;
    }

    public String getUrl() {
        return url.get();
    }

    public void setUrl(String url) {
        this.url.set(url);
    }

    public StringProperty urlProperty() {
        return url;
    }

    public String getUrlSubtitle() {
        return urlSubtitle.get();
    }

    public void setUrlSubtitle(String urlSubtitle) {
        this.urlSubtitle.set(urlSubtitle);
    }

    public StringProperty urlSubtitleProperty() {
        return urlSubtitle;
    }

    public String getDestFileName() {
        return destFileName.get();
    }

    public void setDestFileName(String destFileName) {
        this.destFileName.set(destFileName);
    }

    public StringProperty destFileNameProperty() {
        return destFileName;
    }

    public String getDestPath() {
        return destPath.get();
    }

    public void setDestPath(String destPath) {
        this.destPath.set(destPath);
    }

    public StringProperty destPathProperty() {
        return destPath;
    }

    public String getDestPathFile() {
        return PFileUtils.addsPath(destPath.getValueSafe(), destFileName.getValueSafe());
    }

//    public StringProperty destPathFileProperty() {
//        return destPathFile;
//    }

    public boolean isPlacedBack() {
        return placedBack.get();
    }

    public void setPlacedBack(boolean placedBack) {
        this.placedBack.set(placedBack);
    }

    public BooleanProperty placedBackProperty() {
        return placedBack;
    }

    public boolean isInfoFile() {
        return infoFile.get();
    }

    public void setInfoFile(boolean infoFile) {
        this.infoFile.set(infoFile);
    }

    public BooleanProperty infoFileProperty() {
        return infoFile;
    }

    public boolean isSubtitle() {
        return subtitle.get();
    }

    public void setSubtitle(boolean subtitle) {
        this.subtitle.set(subtitle);
    }

    public BooleanProperty subtitleProperty() {
        return subtitle;
    }

    public int compareTo(DownloadDataProps arg0) {
        return Data.sorter.compare(getChannel(), arg0.getChannel());
    }

}
