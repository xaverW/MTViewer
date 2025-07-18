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


package de.p2tools.mtviewer.controller.filmfilter;


import de.p2tools.p2lib.mediathek.filmdata.FilmData;
import de.p2tools.p2lib.mediathek.filter.FilmFilterCheck;
import de.p2tools.p2lib.mediathek.filter.Filter;
import de.p2tools.p2lib.mediathek.filter.FilterCheck;

import java.util.function.Predicate;

public class PredicateFactory {
    private PredicateFactory() {
    }

    public static Predicate<FilmData> getPredicate(FilmFilter filmFilter) {

        Filter fChannel;
        Filter fTheme;
        Filter fTitle;
        Filter fSomewhere;

        String filterChannel = filmFilter.getChannel();
        String filterTheme = filmFilter.getTheme();
        String filterTitle = filmFilter.getTitle();
        String filterSomewhere = filmFilter.getSomewhere();

        // Sender
        fChannel = new Filter(filterChannel, true);
        // Thema
        fTheme = new Filter(filterTheme, true);
        // Titel
        fTitle = new Filter(filterTitle, true);
        // Irgendwo
        fSomewhere = new Filter(filterSomewhere, true);

        //Sendedatum
        final boolean onlyNew = filmFilter.isOnlyNew();
        final boolean onlyLive = filmFilter.isOnlyLive();
        long days;
        try {
            if (filmFilter.getTimeRange() == FilterCheck.FILTER_ALL_OR_MIN) {
                days = 0;
            } else {
                final long max = 1000L * 60L * 60L * 24L * filmFilter.getTimeRange();
                days = System.currentTimeMillis() - max;
            }
        } catch (final Exception ex) {
            days = 0;
        }

        Predicate<FilmData> predicate = film -> true;

        if (onlyNew) {
            predicate = predicate.and(f -> f.isNewFilm());
        }
        if (onlyLive) {
            predicate = predicate.and(f -> f.isLive());
        }

        //anz Tage Sendezeit
        if (days != 0) {
            final long d = days;
            predicate = predicate.and(f -> FilmFilterCheck.checkDays(d, f));
        }

        //Filmlänge
        if (filmFilter.getMinDur() != FilterCheck.FILTER_ALL_OR_MIN) {
            predicate = predicate.and(f -> FilmFilterCheck.checkMatchMinDur(filmFilter.getMinDur(), f));
        }
        if (filmFilter.getMaxDur() != FilterCheck.FILTER_DURATION_MAX_MINUTE) {
            predicate = predicate.and(f -> FilmFilterCheck.checkMatchMaxDur(filmFilter.getMaxDur(), f));
        }

        //Textfilter
        if (!fChannel.isEmpty) {
            predicate = predicate.and(f -> FilmFilterCheck.checkMatchChannelSmart(fChannel, f));
        }

        if (!fTheme.isEmpty) {
            predicate = predicate.and(f -> FilmFilterCheck.checkMatchTheme(fTheme, f));
        }

        if (!fTitle.isEmpty) {
            predicate = predicate.and(f -> FilmFilterCheck.checkMatchTitle(fTitle, f));
        }

        if (!fSomewhere.isEmpty) {
            predicate = predicate.and(f -> FilmFilterCheck.checkMatchSomewhere(fSomewhere, f));
        }

        return predicate;
    }
}
