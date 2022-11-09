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


package de.p2tools.mtviewer.controller.data.film;

import de.p2tools.p2Lib.tools.DiacriticFactory;

public class FilmDataFactory {
//    static int count = 0;

    private FilmDataFactory() {
    }

    public static void generateDiacritic(FilmData filmData) {
        //dann setzen
        //5,6s, 693MB ~15-20MB mehr durch die zusätzlichen Felder,
        //6,2s 829MB wenn alle Felder gesetzt würden

//        String s = DiacriticFactory.flattenDiacritic(filmData.getTitle());
//        if (!s.equals(filmData.getTitle())) {
//            filmData.arr[FilmData.FILM_TITLE] = s;
//        }
//        s = DiacriticFactory.flattenDiacritic(filmData.getTheme());
//        if (!s.equals(filmData.getTheme())) {
//            filmData.arr[FilmData.FILM_THEME] = s;
//        }
//        s = DiacriticFactory.flattenDiacritic(filmData.getDescription());
//        if (!s.equals(filmData.getDescription())) {
//            filmData.setDescription(s);
//        }


        filmData.arr[FilmData.FILM_TITLE] = DiacriticFactory.flattenDiacritic(filmData.getTitle());
        filmData.arr[FilmData.FILM_THEME] = DiacriticFactory.flattenDiacritic(filmData.getTheme());
        filmData.setDescription(DiacriticFactory.flattenDiacritic(filmData.getDescription()));


//        String s = DiacriticFactory.flattenDiacritic(filmData.getTitle());
//        if (!s.equals(filmData.getTitle())) {
////            ++count;
////            System.out.println("Diakrit: " + count + " - " + filmData.getChannel() + " - " + filmData.getTheme() + " - " + filmData.getTitle());
//            filmData.arr[FilmData.FILM_TITLE2] = s;
//        } else {
//            filmData.arr[FilmData.FILM_TITLE2] = "";
//        }
//        s = DiacriticFactory.flattenDiacritic(filmData.getTheme());
//        if (!s.equals(filmData.getTheme())) {
//            filmData.arr[FilmData.FILM_THEME2] = s;
//        } else {
//            filmData.arr[FilmData.FILM_THEME2] = "";
//        }
//        s = DiacriticFactory.flattenDiacritic(filmData.getDescription());
//        if (!s.equals(filmData.getDescription())) {
//            filmData.arr[FilmData.FILM_DESCRIPTION2] = s;
//        } else {
//            filmData.arr[FilmData.FILM_DESCRIPTION2] = "";
//        }
//        setDiacritic(filmData);
    }

//    public static void setDiacritic(FilmData filmData) {
//        if (ProgConfig.SYSTEM_SHOW_DIACRITICS.getValue()) {
//            //Diacritic werden angezeigt
//            if (filmData.showDiacritic) {
//                //dann passts schon
//                return;
//            }
//            //dann wird gedreht
//            filmData.showDiacritic = true;
//
//        } else {
//            //Diacritic werden *nicht* angezeigt
//            if (!filmData.showDiacritic) {
//                //dann passts schon
//                return;
//            }
//            //dann wird gedreht
//            filmData.showDiacritic = false;
//        }
//
//        change(filmData.arr, FilmData.FILM_TITLE, FilmData.FILM_TITLE2);
//        change(filmData.arr, FilmData.FILM_THEME, FilmData.FILM_THEME2);
//        change(filmData.arr, FilmData.FILM_DESCRIPTION, FilmData.FILM_DESCRIPTION2);
//    }

//    private static void change(String[] arr, int i1, int i2) {
//        if (arr[i2].isEmpty()) {
//            //dann gibts keine Diacritic
//            return;
//        }
//        final String s = arr[i1];
//        arr[i1] = arr[i2];
//        arr[i2] = s;
//    }
}
