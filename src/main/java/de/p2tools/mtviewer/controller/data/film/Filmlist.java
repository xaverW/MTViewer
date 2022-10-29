/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
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

package de.p2tools.mtviewer.controller.data.film;

import de.p2tools.mtviewer.controller.config.ProgConst;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.p2Lib.tools.duration.PDuration;
import de.p2tools.p2Lib.tools.log.PDebugLog;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import org.apache.commons.lang3.time.FastDateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;

@SuppressWarnings("serial")
public class Filmlist extends SimpleListProperty<FilmData> {

    private static final String DATE_TIME_FORMAT = "dd.MM.yyyy, HH:mm";
    private static final SimpleDateFormat sdfUtc = new SimpleDateFormat(DATE_TIME_FORMAT);
    private static final SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
    public int nr = 1;
    public String[] metaData = new String[]{"", "", "", "", ""};
    public String[] sender = {""};
    int count = 0;
    int countDouble = 0;
    private FilteredList<FilmData> filteredList = null;
    private SortedList<FilmData> sortedList = null;

    {
        sdfUtc.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
    }

    public Filmlist() {
        super(FXCollections.observableArrayList());
    }

    public static synchronized String genDate(String[] metaData) {
        // Tag, Zeit in lokaler Zeit wann die Filmliste erstellt wurde
        // in der Form "dd.MM.yyyy, HH:mm"
        String ret;
        String date;

        if (metaData[FilmlistXml.FILMLIST_DATE_GMT_NR].isEmpty()) {
            // noch eine alte Filmliste
            return metaData[FilmlistXml.FILMLIST_DATE_NR];

        } else {
            date = metaData[FilmlistXml.FILMLIST_DATE_GMT_NR];
            //sdfUtc.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
            Date filmDate = null;
            try {
                filmDate = sdfUtc.parse(date);
            } catch (final ParseException ignored) {
            }

            if (filmDate == null) {
                ret = metaData[FilmlistXml.FILMLIST_DATE_GMT_NR];

            } else {
                final FastDateFormat formatter = FastDateFormat.getInstance(DATE_TIME_FORMAT);
                ret = formatter.format(filmDate);
            }
        }

        return ret;
    }

    public SortedList<FilmData> getSortedList() {
        if (sortedList == null || filteredList == null) {
            filteredList = new FilteredList<FilmData>(this, p -> true);
            sortedList = new SortedList<>(filteredList);
        }
        return sortedList;
    }

    public FilteredList<FilmData> getFilteredList() {
        if (sortedList == null || filteredList == null) {
            filteredList = new FilteredList<>(this, p -> true);
            sortedList = new SortedList<>(filteredList);
        }
        return filteredList;
    }

    public synchronized void filteredListSetPred(Predicate<FilmData> predicate) {
        PDebugLog.sysLog("=================> Filter: " + ++count);
        PDuration.counterStart("FilmList.filteredListSetPred");
        getFilteredList().setPredicate(predicate);
        PDuration.counterStop("FilmList.filteredListSetPred");
    }

    public String getFilmlistId() {
        return metaData[FilmlistXml.FILMLIST_ID_NR];
    }

    public synchronized void saveFilm(FilmData film) {
        FilmTools.saveFilm(film);
    }

    public synchronized boolean importFilmOnlyWithNr(FilmData film) {
        // hier nur beim Laden aus einer fertigen Filmliste mit der GUI
        // die Filme sind schon sortiert, nur die Nummer muss noch ergänzt werden
        film.no = nr++;
        return add(film);
    }

    private void addHash(FilmData f, HashSet<String> hash, boolean index) {
        if (f.arr[FilmDataXml.FILM_CHANNEL].equals(ProgConst.KIKA)) {
            // beim KIKA ändern sich die URLs laufend
            hash.add(f.arr[FilmDataXml.FILM_THEME] + f.arr[FilmDataXml.FILM_TITLE]);
        } else if (index) {
            hash.add(f.getIndex());
        } else {
            hash.add(f.getUrlForHash());
        }
    }

    public synchronized void updateList(Filmlist addList,
                                        boolean index /* Vergleich über Index, sonst nur URL */,
                                        boolean replace) {
        // in eine vorhandene Liste soll eine andere Filmliste einsortiert werden
        // es werden nur Filme die noch nicht vorhanden sind, einsortiert
        // "ersetzen": true: dann werden gleiche (index/URL) in der Liste durch neue ersetzt
        final HashSet<String> hash = new HashSet<>(addList.size() + 1, 0.75F);

        if (replace) {
            addList.forEach((FilmData f) -> addHash(f, hash, index));

            final Iterator<FilmData> it = iterator();
            while (it.hasNext()) {
                final FilmData f = it.next();
                if (f.arr[FilmDataXml.FILM_CHANNEL].equals(ProgConst.KIKA)) {
                    // beim KIKA ändern sich die URLs laufend
                    if (hash.contains(f.arr[FilmDataXml.FILM_THEME] + f.arr[FilmDataXml.FILM_TITLE])) {
                        it.remove();
                    }
                } else if (index) {
                    if (hash.contains(f.getIndex())) {
                        it.remove();
                    }
                } else if (hash.contains(f.getUrlForHash())) {
                    it.remove();
                }
            }

            addList.forEach(this::addInit);
        } else {
            // ==============================================
            forEach(f -> addHash(f, hash, index));

            for (final FilmData f : addList) {
                if (f.arr[FilmDataXml.FILM_CHANNEL].equals(ProgConst.KIKA)) {
                    if (!hash.contains(f.arr[FilmDataXml.FILM_THEME] + f.arr[FilmDataXml.FILM_TITLE])) {
                        addInit(f);
                    }
                } else if (index) {
                    if (!hash.contains(f.getIndex())) {
                        addInit(f);
                    }
                } else if (!hash.contains(f.getUrlForHash())) {
                    addInit(f);
                }
            }
        }
        hash.clear();
    }

    public synchronized void markGeoBlocked() {
        // geblockte Filme markieren
        this.parallelStream().forEach((FilmData f) -> f.setGeoBlocked());
    }

    public synchronized int markFilms() {
        // läuft direkt nach dem Laden der Filmliste!
        // doppelte Filme (URL), Geo, InFuture markieren
        // viele Filme sind bei mehreren Sendern vorhanden

        final HashSet<String> urlHashSet = new HashSet<>(size(), 0.75F);

        // todo exception parallel?? Unterschied ~10ms (bei Gesamt: 110ms)
        PDuration.counterStart("Filme markieren");
        try {
            countDouble = 0;
            this.stream().forEach((FilmData f) -> {

                f.setGeoBlocked();
                f.setInFuture();

                if (!urlHashSet.add(f.getUrl())) {
                    ++countDouble;
                    f.setDoubleUrl(true);
                }

            });

        } catch (Exception ex) {
            PLog.errorLog(951024789, ex);
        }
        PDuration.counterStop("Filme markieren");

        urlHashSet.clear();
        return countDouble;
    }

    private boolean addInit(FilmData film) {
//        film.init(); todo
        return add(film);
    }

    @Override
    public synchronized void clear() {
        nr = 1;
        super.clear();
    }

    public synchronized void sort() {
        Collections.sort(this);
        // und jetzt noch die Nummerierung in Ordnung bringen
        int i = 1;
        for (final FilmData film : this) {
            film.no = i++;
        }
    }

    public synchronized void setMeta(Filmlist filmlist) {
        System.arraycopy(filmlist.metaData, 0, metaData, 0, FilmlistXml.MAX_ELEM);
    }

    public synchronized FilmData getFilmByUrl(final String url) {
        final Optional<FilmData> opt =
                parallelStream().filter(f -> f.arr[FilmDataXml.FILM_URL].equalsIgnoreCase(url)).findAny();
        return opt.orElse(null);
    }

    public synchronized void getTheme(String sender, LinkedList<String> list) {
        stream().filter(film -> film.arr[FilmDataXml.FILM_CHANNEL].equals(sender))
                .filter(film -> !list.contains(film.arr[FilmDataXml.FILM_THEME]))
                .forEach(film -> list.add(film.arr[FilmDataXml.FILM_THEME]));
    }

    public synchronized FilmData getFilmByUrl_small_high_hd(String url) {
        // Problem wegen gleicher URLs
        // wird versucht, einen Film mit einer kleinen/Hoher/HD-URL zu finden

        return parallelStream().filter(f ->

                f.arr[FilmDataXml.FILM_URL].equals(url) ||
                        f.getUrlForResolution(FilmData.RESOLUTION_HD).equals(url) ||
                        f.getUrlForResolution(FilmData.RESOLUTION_SMALL).equals(url)

        ).findFirst().orElse(null);

    }

    public synchronized String genDate() {
        return genDate(metaData);
    }

    /**
     * Get the age of the film list.
     *
     * @return Age in seconds.
     */
    public int getAge() {
        int ret = 0;
        final Date now = new Date(System.currentTimeMillis());
        final Date filmDate = getAgeAsDate();
        if (filmDate != null) {
            ret = Math.round((now.getTime() - filmDate.getTime()) / (1000));
            if (ret < 0) {
                ret = 0;
            }
        }
        return ret;
    }

    /**
     * Get the age of the film list.
     *
     * @return Age as a {@link java.util.Date} object.
     */
    public Date getAgeAsDate() {
        if (!metaData[FilmlistXml.FILMLIST_DATE_GMT_NR].isEmpty()) {
            final String date = metaData[FilmlistXml.FILMLIST_DATE_GMT_NR];
            return getDate(date, sdfUtc);

        } else {
            final String date = metaData[FilmlistXml.FILMLIST_DATE_NR];
            return getDate(date, sdf);
        }
    }

    private Date getDate(String date, SimpleDateFormat df) {
        if (date.isEmpty()) {
            // dann ist die Filmliste noch nicht geladen
            return null;
        }

        Date filmDate = null;
        try {
            filmDate = df.parse(date);
        } catch (final Exception ignored) {
        }

        return filmDate;
    }

    /**
     * Check if available Filmlist is older than a specified value.
     *
     * @return true if too old or if the list is empty.
     */
    public synchronized boolean isTooOld() {
        if (ProgData.debug) {
            // im Debugmodus nie automatisch laden
            return false;
        }

        return (isEmpty()) || (isOlderThan(ProgConst.ALTER_FILMLISTE_SEKUNDEN_FUER_AUTOUPDATE));
    }

    /**
     * Check if Filmlist is too old for using a diff list.
     *
     * @return true if empty or too old.
     */
    public synchronized boolean isTooOldForDiff() {
        if (isEmpty()) {
            return true;
        }
        try {
            final String dateMaxDiff_str =
                    new SimpleDateFormat("yyyy.MM.dd__").format(new Date()) + ProgConst.TIME_MAX_AGE_FOR_DIFF + ":00:00";
            final Date dateMaxDiff = new SimpleDateFormat("yyyy.MM.dd__HH:mm:ss").parse(dateMaxDiff_str);
            final Date dateFilmlist = getAgeAsDate();
            if (dateFilmlist != null) {
                return dateFilmlist.getTime() < dateMaxDiff.getTime();
            }
        } catch (final Exception ignored) {
        }
        return true;
    }

    /**
     * Check if list is older than specified parameter.
     *
     * @param second The age in seconds.
     * @return true if older.
     */
    public boolean isOlderThan(int second) {
        final int ret = getAge();
        if (ret != 0) {
            PLog.addSysLog("Die Filmliste ist " + ret / 60 + " Minuten alt");
        }
        return ret > second;
    }

    public synchronized long countNewFilms() {
        return stream().filter(FilmData::isNewFilm).count();
    }

    /**
     * Erstellt ein StringArray der Themen eines Senders oder wenn "sender" leer, aller Sender. Ist
     * für die Filterfelder in GuiFilme.
     */
    public synchronized void loadSender() {
        PDuration.counterStart("Filmlist.loadSender");

        final LinkedHashSet<String> senderSet = new LinkedHashSet<>(21);
        // der erste Sender ist ""
        senderSet.add("");

        stream().forEach((film) -> senderSet.add(film.getChannel()));
        sender = senderSet.toArray(new String[senderSet.size()]);

        PDuration.counterStop("Filmlist.loadSender");
//        loadUrls();
    }

//    public synchronized void loadUrls() {
//        PDuration.counterStart("Filmlist.loadUrls");
//
//        HashMap<String, Integer> urls = new HashMap<String, Integer>();
//        stream().forEach((film) -> {
//            final String u = film.getUrl();
//            String s = u.substring(0, u.indexOf("/")) + "  -  " + u.substring(u.lastIndexOf("."));
//            Integer i = urls.get(s);
//            if (i == null) {
//                urls.put(s, 1);
//            } else {
//                urls.replace(s, Integer.valueOf(++i));
//            }
//        });
//
//        System.out.println("====================================================");
//        System.out.println("====================================================");
//        System.out.println("====================================================");
//        urls.entrySet()
//                .stream()
//                .sorted(Map.Entry.<String, Integer>comparingByValue())
//                .forEach(e -> {
//                    String va = e.getValue() + "";
//                    while (va.length() < 8) {
//                        va = " " + va;
//                    }
//
//                    System.out.println(" value: " + va + "   -    " + "key: " + e.getKey());
//                });
//        System.out.println("====================================================");
//        System.out.println("====================================================");
//        System.out.println("====================================================");
//        PDuration.counterStop("Filmlist.loadUrls");
//    }
}
