/*
 * P2Tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
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
package de.p2tools.mtviewer.controller.audio;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import de.p2tools.p2lib.atdata.AudioData;
import de.p2tools.p2lib.atdata.AudioDataXml;
import de.p2tools.p2lib.atdata.AudioList;

import java.io.IOException;
import java.io.OutputStream;

public class WriteAudioListJson {

    public void writeJson(JsonGenerator jg, AudioList audioList) throws IOException {
        String sender = "", genre = "", theme = "";

        jg.writeStartObject();

        //=======================================
        // Infos zur Audioliste
        jg.writeArrayFieldStart(AudioList.AUDIO_LIST_TAG);
        for (int i = 0; i < AudioList.AUDIO_LIST_META_MAX_ELEM; ++i) {
            jg.writeString(audioList.metaData[i]);
        }
        jg.writeEndArray();

        //=======================================
        // Infos der Felder in der Audioliste
        jg.writeArrayFieldStart(AudioDataXml.JSON_TAG);
        for (int i = 0; i < AudioDataXml.JSON_COLUMN_NAMES.length; ++i) {
            jg.writeString(AudioDataXml.JSON_COLUMN_NAMES[i]);
        }
        jg.writeEndArray();

        //=======================================
        //Audios schreiben
        for (AudioData audioData : audioList) {
            audioData.arr[AudioDataXml.AUDIO_NEW] = Boolean.toString(audioData.isNewAudio()); // damit wirds beim nächsten Programmstart noch wissen
            audioData.arr[AudioDataXml.AUDIO_PODCAST] = Boolean.toString(audioData.isPodcast()); // damit wirds beim nächsten Programmstart noch wissen
            audioData.arr[AudioDataXml.AUDIO_DOUBLE] = Boolean.toString(audioData.isDoubleUrl()); // damit wirds beim nächsten Programmstart noch wissen

            jg.writeArrayFieldStart(AudioDataXml.JSON_TAG);
            for (int i = 0; i < AudioDataXml.JSON_MAX_ELEM; ++i) {
                switch (i) {
                    case AudioDataXml.JSON_AUDIO_CHANNEL:
                        if (audioData.arr[AudioDataXml.AUDIO_CHANNEL].equals(sender)) {
                            jg.writeString("");
                        } else {
                            sender = audioData.arr[AudioDataXml.AUDIO_CHANNEL];
                            jg.writeString(audioData.arr[AudioDataXml.AUDIO_CHANNEL]);
                        }
                        break;
                    case AudioDataXml.JSON_AUDIO_GENRE:
                        if (audioData.arr[AudioDataXml.AUDIO_GENRE].equals(genre)) {
                            jg.writeString("");
                        } else {
                            genre = audioData.arr[AudioDataXml.AUDIO_GENRE];
                            jg.writeString(audioData.arr[AudioDataXml.AUDIO_GENRE]);
                        }
                        break;
                    case AudioDataXml.JSON_AUDIO_THEME:
                        if (audioData.arr[AudioDataXml.AUDIO_THEME].equals(theme)) {
                            jg.writeString("");
                        } else {
                            theme = audioData.arr[AudioDataXml.AUDIO_THEME];
                            jg.writeString(audioData.arr[AudioDataXml.AUDIO_THEME]);
                        }
                        break;
                    case AudioDataXml.JSON_AUDIO_TITLE:
                        jg.writeString(audioData.arr[AudioDataXml.AUDIO_TITLE]);
                        break;
                    case AudioDataXml.JSON_AUDIO_DATE:
                        jg.writeString(audioData.arr[AudioDataXml.AUDIO_DATE]);
                        break;
                    case AudioDataXml.JSON_AUDIO_TIME:
                        jg.writeString(audioData.arr[AudioDataXml.AUDIO_TIME]);
                        break;
                    case AudioDataXml.JSON_AUDIO_DURATION:
                        jg.writeString(audioData.arr[AudioDataXml.AUDIO_DURATION]);
                        break;
                    case AudioDataXml.JSON_AUDIO_SIZE_MB:
                        jg.writeString(audioData.arr[AudioDataXml.AUDIO_SIZE_MB]);
                        break;


                    case AudioDataXml.JSON_AUDIO_DESCRIPTION:
                        jg.writeString(audioData.arr[AudioDataXml.AUDIO_DESCRIPTION]);
                        break;
                    case AudioDataXml.JSON_AUDIO_URL:
                        jg.writeString(audioData.arr[AudioDataXml.AUDIO_URL]);
                        break;
                    case AudioDataXml.JSON_AUDIO_WEBSITE:
                        jg.writeString(audioData.arr[AudioDataXml.AUDIO_WEBSITE]);
                        break;
                    case AudioDataXml.JSON_AUDIO_NEW:
                        jg.writeString(audioData.arr[AudioDataXml.AUDIO_NEW]);
                        break;
                    case AudioDataXml.JSON_AUDIO_PODCAST:
                        jg.writeString(audioData.arr[AudioDataXml.AUDIO_PODCAST]);
                        break;
                    case AudioDataXml.JSON_AUDIO_DOUBLE:
                        jg.writeString(audioData.arr[AudioDataXml.AUDIO_DOUBLE]);
                        break;
                }
            }
            jg.writeEndArray();
        }
        jg.writeEndObject();
        jg.flush();
    }

    public JsonGenerator getJsonGenerator(OutputStream os) throws IOException {
        JsonFactory jsonF = new JsonFactory();
        JsonGenerator jg = jsonF.createGenerator(os, JsonEncoding.UTF8);
        jg.useDefaultPrettyPrinter(); // enable indentation just to make debug/testing easier
        return jg;
    }
}