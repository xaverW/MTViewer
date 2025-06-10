package de.p2tools.mtviewer.controller.data.film;

import de.p2tools.p2lib.mtfilm.film.FilmData;
import de.p2tools.p2lib.mtfilm.film.Filmlist;

import java.util.List;

public class FilmListMtc extends Filmlist<FilmData> {

    @Override
    public synchronized int markFilms(List<String> logList) {
        // läuft direkt nach dem Laden der Filmliste!
        // doppelte Filme (URL), Geo, InFuture markieren
        // viele Filme sind bei mehreren Sendern vorhanden
        return FilmToolsFactory.markFilms(logList, this);
    }
}
