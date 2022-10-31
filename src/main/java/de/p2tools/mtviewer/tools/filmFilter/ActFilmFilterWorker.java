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

package de.p2tools.mtviewer.tools.filmFilter;

import de.p2tools.mtviewer.controller.config.ProgData;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public final class ActFilmFilterWorker {

    // ist der aktuell angezeigte Filter
    public static final String SELECTED_FILTER_NAME = "aktuelle Einstellung"; // dient nur der Info im Config-File
    final int MAX_FILTER_HISTORY = 10;
    final int MAX_FILTER_GO_BACK = 5;
    private final ProgData progData;
    private final BooleanProperty filterChange = new SimpleBooleanProperty(true);
    private final BooleanProperty backwardPossible = new SimpleBooleanProperty(false);
    private final BooleanProperty forwardPossible = new SimpleBooleanProperty(false);

    // ist die Liste der zuletzt verwendeten Filter
    private final ObservableList<FilmFilter> filmFilterListBackward =
            FXCollections.observableList(new ArrayList<>() {
                @Override
                public void add(int index, FilmFilter e) {
                    while (this.size() > MAX_FILTER_GO_BACK) {
                        remove(0);
                    }
                    super.add(e);
                }
            }, (FilmFilter tp) -> new Observable[]{tp.nameProperty()});
    private final ObservableList<FilmFilter> filmFilterListForward =
            FXCollections.observableList(new ArrayList<>() {
                @Override
                public void add(int index, FilmFilter e) {
                    while (this.size() > MAX_FILTER_GO_BACK) {
                        remove(0);
                    }
                    super.add(e);
                }
            }, (FilmFilter tp) -> new Observable[]{tp.nameProperty()});

    private final ObservableList<String> lastThemaTitleFilter = FXCollections.observableArrayList("");
    private final ObservableList<String> lastTitleFilter = FXCollections.observableArrayList("");
    private final ObservableList<String> lastSomewhereFilter = FXCollections.observableArrayList("");
    private final ChangeListener<Boolean> filterChangeListener;
    private FilmFilter actFilterSettings = new FilmFilter(SELECTED_FILTER_NAME); // ist der "aktuelle" Filter im Programm
    private FilmFilter oldActFilterSettings = new FilmFilter(SELECTED_FILTER_NAME); // ist der "aktuelle" Filter im Programm

    private boolean theme = false, themeTitle = false, title = false, somewhere = false;

    public ActFilmFilterWorker(ProgData progData) {
        this.progData = progData;

        filterChangeListener = (observable, oldValue, newValue) -> {
            postFilterChange();
        };
        actFilterSettings.filterChangeProperty().addListener(filterChangeListener); // wenn der User den Filter ändert
        filmFilterListBackward.addListener((ListChangeListener<FilmFilter>) c -> {
            if (filmFilterListBackward.size() > 1) {
                backwardPossible.setValue(true);
            } else {
                backwardPossible.setValue(false);
            }
        });
        filmFilterListForward.addListener((ListChangeListener<FilmFilter>) c -> {
            if (filmFilterListForward.size() > 0) {
                forwardPossible.setValue(true);
            } else {
                forwardPossible.setValue(false);
            }
        });
    }

    public void initFilter() {
        addBackward();
    }

    public BooleanProperty filterChangeProperty() {
        return filterChange;
    }

    public BooleanProperty backwardPossibleProperty() {
        return backwardPossible;
    }

    public BooleanProperty forwardPossibleProperty() {
        return forwardPossible;
    }

    /**
     * liefert den aktuell angezeigten Filter
     *
     * @return
     */
    public FilmFilter getActFilterSettings() {
        return actFilterSettings;
    }

    /**
     * setzt die aktuellen Filtereinstellungen aus einem Filter (gespeicherten Filter)
     *
     * @param sf
     */
    public synchronized void setActFilterSettings(FilmFilter sf) {
        if (sf == null) {
            return;
        }
        actFilterSettings.filterChangeProperty().removeListener(filterChangeListener);
        sf.copyTo(actFilterSettings);
        postFilterChange();
        actFilterSettings.filterChangeProperty().addListener(filterChangeListener);
    }

    public synchronized void clearFilter() {
        actFilterSettings.filterChangeProperty().removeListener(filterChangeListener);
        actFilterSettings.clearFilter();
        filmFilterListForward.clear();
        filmFilterListBackward.clear();

        postFilterChange();
        actFilterSettings.filterChangeProperty().addListener(filterChangeListener);
    }

    public void goBackward() {
        if (filmFilterListBackward.size() <= 1) {
            // dann gibts noch keine oder ist nur die aktuelle Einstellung drin
            return;
        }

        FilmFilter sf = filmFilterListBackward.remove(filmFilterListBackward.size() - 1); // ist die aktuelle Einstellung
        filmFilterListForward.add(sf);
        sf = filmFilterListBackward.remove(filmFilterListBackward.size() - 1); // ist die davor
        setActFilterSettings(sf);
    }

    public void goForward() {
        if (filmFilterListForward.isEmpty()) {
            // dann gibts keine
            return;
        }

        final FilmFilter sf = filmFilterListForward.remove(filmFilterListForward.size() - 1);
        setActFilterSettings(sf);
    }

    public ObservableList<String> getLastThemaTitleFilter() {
        return lastThemaTitleFilter;
    }

    public synchronized void addLastThemeTitleFilter(String filter) {
        addLastFilter(lastThemaTitleFilter, filter);
    }

    public ObservableList<String> getLastTitleFilter() {
        return lastTitleFilter;
    }

    public synchronized void addLastTitleFilter(String filter) {
        addLastFilter(lastTitleFilter, filter);
    }

    public ObservableList<String> getLastSomewhereFilter() {
        return lastSomewhereFilter;
    }

    public synchronized void addLastSomewhereFilter(String filter) {
        addLastFilter(lastSomewhereFilter, filter);
    }

    private synchronized void addLastFilter(ObservableList<String> list, String filter) {
        if (filter.isEmpty()) {
            return;
        }

        if (!list.stream().filter(f -> f.equals(filter)).findAny().isPresent()) {
            list.add(filter);
        }
        while (list.size() >= MAX_FILTER_HISTORY) {
            list.remove(1);
        }
    }

    private void setFilterChange() {
        addLastThemeTitleFilter(progData.actFilmFilterWorker.getActFilterSettings().getTheme());
        addLastTitleFilter(progData.actFilmFilterWorker.getActFilterSettings().getTitle());
        addLastSomewhereFilter(progData.actFilmFilterWorker.getActFilterSettings().getSomewhere());

        //hier erst mal die actFilter vergleichen, ob geändert
        if (!oldActFilterSettings.isSame(actFilterSettings, true)) {
            actFilterSettings.copyTo(oldActFilterSettings);
            this.filterChange.set(!filterChange.get());
        }
    }

    private void addBackward() {
        final FilmFilter sf = new FilmFilter();
        actFilterSettings.copyTo(sf);
        if (filmFilterListBackward.isEmpty()) {
            filmFilterListBackward.add(sf);
            return;
        }

        FilmFilter sfB = filmFilterListBackward.get(filmFilterListBackward.size() - 1);
        if (sf.isSame(sfB, false)) {
            // dann hat sich nichts geändert (z.B. mehrmals gelöscht)
            return;
        }

        //Textfilter
//        if (!sf.isThemeExact() && checkText(sfB.themeProperty(), sf.themeProperty(), sfB, sf, theme)) {
//            setFalse();
//            theme = true;
//            return;
//        }
        if (checkText(sfB.themeProperty(), sf.themeProperty(), sfB, sf, themeTitle)) {
            setFalse();
            themeTitle = true;
            return;
        }
        if (checkText(sfB.titleProperty(), sf.titleProperty(), sfB, sf, title)) {
            setFalse();
            title = true;
            return;
        }
        if (checkText(sfB.somewhereProperty(), sf.somewhereProperty(), sfB, sf, somewhere)) {
            setFalse();
            somewhere = true;
            return;
        }

        //dann wars kein Textfilter
        filmFilterListBackward.add(sf);
    }

    private void setFalse() {
        theme = false;
        themeTitle = false;
        title = false;
        somewhere = false;
    }

    private boolean checkText(StringProperty old, StringProperty nnew, FilmFilter oldSf, FilmFilter newSf,
                              boolean check) {
        if (old.get().equals(nnew.get())) {
            return false;
        }
        if (check && !old.get().isEmpty() && !nnew.get().isEmpty() &&
                (old.get().contains(nnew.get()) || nnew.get().contains(old.get()))) {
            // dann hat sich nur ein Teil geändert und wird ersetzt
            old.setValue(nnew.getValue());
        } else {
            filmFilterListBackward.add(newSf);
        }
        return true;
    }

    private void postFilterChange() {
        addBackward();
        setFilterChange();
    }
}
