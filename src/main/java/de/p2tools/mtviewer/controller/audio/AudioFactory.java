package de.p2tools.mtviewer.controller.audio;

import de.p2tools.p2lib.atdata.AudioData;
import de.p2tools.p2lib.mtfilm.film.FilmData;

public class AudioFactory {
    private AudioFactory() {
    }

    public static FilmData getFilmData(AudioData audioData) {
        FilmData filmData = new FilmData();

        filmData.arr[FilmData.FILM_CHANNEL] = audioData.getChannel();
        filmData.arr[FilmData.FILM_THEME] = audioData.getTheme();
        filmData.arr[FilmData.FILM_TITLE] = audioData.getTitle();

        return filmData;
    }
}
