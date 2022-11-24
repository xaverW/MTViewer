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

package de.p2tools.mtviewer.controller.filmFilter;

import de.p2tools.p2Lib.configFile.config.Config;
import de.p2tools.p2Lib.configFile.config.ConfigBoolPropExtra;
import de.p2tools.p2Lib.configFile.config.ConfigIntPropExtra;
import de.p2tools.p2Lib.configFile.config.ConfigStringPropExtra;
import de.p2tools.p2Lib.configFile.pData.PDataSample;
import javafx.beans.property.*;

import java.util.ArrayList;

public class FilmFilterProps extends PDataSample<FilmFilter> implements Comparable<FilmFilter> {

    public static String TAG = "SelectedFilter";

    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty channel = new SimpleStringProperty();
    private final StringProperty theme = new SimpleStringProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty somewhere = new SimpleStringProperty();

    private final IntegerProperty timeRange = new SimpleIntegerProperty(15);
    private final BooleanProperty onlyNew = new SimpleBooleanProperty(false);
    private final BooleanProperty onlyLive = new SimpleBooleanProperty(false);

    public BooleanProperty[] sfBooleanPropArr = {onlyNew, onlyLive};

    public StringProperty[] sfStringPropArr = {name, channel, theme, title, somewhere};
    public IntegerProperty[] sfIntegerPropArr = {timeRange};

    @Override
    public Config[] getConfigsArr() {
        ArrayList<Config> list = new ArrayList<>();
        list.add(new ConfigStringPropExtra("name", FilmFilterToXml.SELECTED_FILTER_NAME, name));
        list.add(new ConfigStringPropExtra("channel", FilmFilterToXml.SELECTED_FILTER_CHANNEL, channel));
        list.add(new ConfigStringPropExtra("theme", FilmFilterToXml.SELECTED_FILTER_THEME, theme));
        list.add(new ConfigStringPropExtra("title", FilmFilterToXml.SELECTED_FILTER_TITLE, title));
        list.add(new ConfigStringPropExtra("somewhere", FilmFilterToXml.SELECTED_FILTER_SOMEWHERE, somewhere));
        list.add(new ConfigIntPropExtra("timeRange", FilmFilterToXml.SELECTED_FILTER_TIME_RANGE, timeRange));
        list.add(new ConfigBoolPropExtra("onlyNew", FilmFilterToXml.SELECTED_FILTER_ONLY_NEW, onlyNew));
        list.add(new ConfigBoolPropExtra("onlyLive", FilmFilterToXml.SELECTED_FILTER_ONLY_LIVE, onlyLive));

        return list.toArray(new Config[]{});
    }

    public boolean isSame(FilmFilter sf, boolean compareName) {
        if (sf == null) {
            return false;
        }

        for (int i = 0; i < sfBooleanPropArr.length; ++i) {
            if (!this.sfBooleanPropArr[i].getValue().equals(sf.sfBooleanPropArr[i].getValue())) {
                return false;
            }
        }
        int ii = compareName ? 0 : 1;//wenn der Name mit verglichen werden soll, dann Start bei 0, sonst 1
        for (int i = ii; i < sfStringPropArr.length; ++i) {
            if (!this.sfStringPropArr[i].getValue().equals(sf.sfStringPropArr[i].getValue())) {
                return false;
            }
        }
        for (int i = 0; i < sfIntegerPropArr.length; ++i) {
            if (!this.sfIntegerPropArr[i].getValue().equals(sf.sfIntegerPropArr[i].getValue())) {
                return false;
            }
        }
        return true;
    }

    public FilmFilter getCopy() {
        FilmFilter sf = new FilmFilter();
        this.copyTo(sf);
        return sf;
    }

    public void copyTo(FilmFilter sf) {

        for (int i = 0; i < sfBooleanPropArr.length; ++i) {
            sf.sfBooleanPropArr[i].setValue(this.sfBooleanPropArr[i].getValue());
        }
        for (int i = 0; i < sfStringPropArr.length; ++i) {
            sf.sfStringPropArr[i].setValue(this.sfStringPropArr[i].getValue());
        }
        for (int i = 0; i < sfIntegerPropArr.length; ++i) {
            sf.sfIntegerPropArr[i].setValue(this.sfIntegerPropArr[i].getValue());
        }
    }

    @Override
    public String getTag() {
        return TAG;
    }


    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getChannel() {
        return channel.get() == null ? "" : channel.get();
    }

    public void setChannel(String sender) {
        this.channel.set(sender);
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

    public String getSomewhere() {
        return somewhere.get();
    }

    public void setSomewhere(String somewhere) {
        this.somewhere.set(somewhere);
    }

    public StringProperty somewhereProperty() {
        return somewhere;
    }

    public int getTimeRange() {
        return timeRange.get();
    }

    public void setTimeRange(int timeRange) {
        this.timeRange.set(timeRange);
    }

    public IntegerProperty timeRangeProperty() {
        return timeRange;
    }

    public boolean isOnlyNew() {
        return onlyNew.get();
    }

    public void setOnlyNew(boolean onlyNew) {
        this.onlyNew.set(onlyNew);
    }

    public BooleanProperty onlyNewProperty() {
        return onlyNew;
    }

    public boolean isOnlyLive() {
        return onlyLive.get();
    }

    public void setOnlyLive(boolean onlyLive) {
        this.onlyLive.set(onlyLive);
    }

    public BooleanProperty onlyLiveProperty() {
        return onlyLive;
    }

    @Override
    public String toString() {
        return name.getValue();
    }

    @Override
    public int compareTo(FilmFilter o) {
        return name.getValue().compareTo(o.getName());
    }
}
