/*
 * P2tools Copyright (C) 2022 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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

import de.p2tools.mtviewer.controller.data.film.FilmData;
import de.p2tools.mtviewer.controller.data.film.FilmDataXml;

import java.util.regex.Pattern;

public class FilmFilterCheck {

    public static final int FILTER_TIME_RANGE_ALL_VALUE = 0;
    public static final int FILTER_TIME_RANGE_MIN_VALUE = 0;
    public static final int FILTER_TIME_RANGE_MAX_VALUE = 50;

    private FilmFilterCheck() {
    }

    public static boolean checkChannelSmart(Filter sender, FilmData film) {
        // nur ein Suchbegriff muss passen
        for (final String s : sender.filterArr) {
            // dann jeden Suchbegriff checken
            if (s.equalsIgnoreCase(film.arr[FilmDataXml.FILM_CHANNEL])) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkTheme(Filter theme, FilmData film) {
        if (!check(theme, film.arr[FilmDataXml.FILM_THEME])) {
            return false;
        }
        return true;
    }

    public static boolean checkTitle(Filter title, FilmData film) {
        if (!check(title, film.arr[FilmDataXml.FILM_TITLE])) {
            return false;
        }
        return true;
    }

    public static boolean checkSomewhere(Filter somewhere, FilmData film) {
        if (!check(somewhere, film.arr[FilmDataXml.FILM_DATE])
                && !check(somewhere, film.arr[FilmDataXml.FILM_THEME])
                && !check(somewhere, film.arr[FilmDataXml.FILM_TITLE])
                && !check(somewhere, film.arr[FilmDataXml.FILM_DESCRIPTION])) {
            return false;
        }
        return true;
    }

    public static boolean checkDays(long days, FilmData film) {
        if (days == 0) {
            return true;
        }

        final long filmTime = film.filmDate.getTime();
        if (filmTime != 0 && filmTime < days) {
            return false;
        }

        return true;
    }

    private static boolean check(Filter filter, String im) {
        // wenn einer passt, dann ists gut
        if (filter.filterArr.length == 1) {
            return check(filter.filterArr[0], filter.pattern, im);
        }

        if (filter.filterAnd) {
            // Suchbegriffe müssen alle passen
            for (final String s : filter.filterArr) {
                // dann jeden Suchbegriff checken
                if (!im.toLowerCase().contains(s)) {
                    return false;
                }
            }
            return true;

        } else {
            // nur ein Suchbegriff muss passen
            for (final String s : filter.filterArr) {
                // dann jeden Suchbegriff checken
                if (im.toLowerCase().contains(s)) {
                    return true;
                }
            }
        }

        // nix wars
        return false;
    }

    private static boolean check(String filter, Pattern pattern, String im) {
        if (pattern != null) {
            // dann ists eine RegEx
            return (pattern.matcher(im).matches());
        }
        if (im.toLowerCase().contains(filter)) {
            // wenn einer passt, dann ists gut
            return true;
        }

        // nix wars
        return false;
    }
}
