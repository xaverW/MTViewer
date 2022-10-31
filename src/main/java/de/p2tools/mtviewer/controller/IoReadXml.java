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


package de.p2tools.mtviewer.controller;

import de.p2tools.mtviewer.controller.config.ProgColorList;
import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.mtviewer.controller.config.ProgData;
import de.p2tools.mtviewer.controller.data.ReplaceData;
import de.p2tools.mtviewer.controller.data.film.FilmData;
import de.p2tools.mtviewer.controller.filmlist.filmlistUrls.FilmlistUrlData;
import de.p2tools.mtviewer.tools.filmFilter.FilmFilter;
import de.p2tools.mtviewer.tools.filmFilter.FilmFilterToXml;
import de.p2tools.p2Lib.configFile.config.Config;
import de.p2tools.p2Lib.tools.duration.PDuration;
import de.p2tools.p2Lib.tools.log.PLog;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class IoReadXml implements AutoCloseable {

    private XMLInputFactory inFactory = null;
    private ProgData progData = null;

    public IoReadXml(ProgData progData) {
        this.progData = progData;

        inFactory = XMLInputFactory.newInstance();
        inFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
    }

    public boolean readConfiguration(Path xmlFilePath) {
        boolean ret = readConfig(xmlFilePath);
        return ret;
    }

    private boolean readConfig(Path xmlFilePath) {
        PDuration.counterStart("readConfig");
        boolean ret = false;
        int filtercount = 0;

        if (Files.exists(xmlFilePath)) {
            XMLStreamReader parser = null;
            try (InputStream is = Files.newInputStream(xmlFilePath);
                 InputStreamReader in = new InputStreamReader(is, StandardCharsets.UTF_8)) {

                parser = inFactory.createXMLStreamReader(in);
                while (parser.hasNext()) {
                    final int event = parser.next();
                    if (event == XMLStreamConstants.START_ELEMENT) {
                        switch (parser.getLocalName()) {

                            case ProgConfig.SYSTEM:
                                // System
                                getConfig(parser, ProgConfig.SYSTEM);
                                break;

                            case "Ersetzungstabelle":
                                // Ersetzungstabelle
                                final ReplaceData replaceData = new ReplaceData();
                                if (get(parser, "Ersetzungstabelle", new String[]{"von", "to"}, replaceData.arr)) {
                                    replaceData.setPropsFromXml();
                                    this.progData.replaceList.add(replaceData);
                                }
                                break;

                            case FilmFilterToXml.TAG:
                                // Filter
                                final FilmFilter sf = new FilmFilter();
                                final String[] ar = FilmFilterToXml.getEmptyArray();
                                if (get(parser, FilmFilterToXml.TAG, FilmFilterToXml.getXmlArray(), ar)) {
                                    FilmFilterToXml.setValueArray(sf, ar);
                                    if (filtercount == 0) {
                                        // damit das nicht schon gemeldet wird
                                        this.progData.actFilmFilterWorker.getActFilterSettings().setReportChange(false);
                                        sf.copyTo(this.progData.actFilmFilterWorker.getActFilterSettings());
                                        this.progData.actFilmFilterWorker.getActFilterSettings().setReportChange(true);
                                    } else {
//                                        this.progData.actFilmFilterWorker.getStoredFilterList().add(sf);
                                    }
                                    ++filtercount;
                                }
                                break;

                            case "filmlist-update-server":
                                // Urls Filmlisten
                                final FilmlistUrlData filmlistUrlData = new FilmlistUrlData();
                                if (get(parser, "filmlist-update-server",
                                        FilmlistUrlData.FILMLIST_URL_DATA_COLUMN_NAMES, filmlistUrlData.arr)) {
                                    filmlistUrlData.setPropsFromXml();
                                    switch (filmlistUrlData.arr[FilmlistUrlData.FILMLIST_URL_DATA_TYPE_NO]) {
                                        case FilmlistUrlData.SERVER_TYPE_ACT:
                                            this.progData.searchFilmListUrls.getFilmlistUrlList_akt().addWithCheck(filmlistUrlData);
                                            break;
                                        case FilmlistUrlData.SERVER_TYPE_DIFF:
                                            this.progData.searchFilmListUrls.getFilmlistUrlList_diff().addWithCheck(filmlistUrlData);
                                            break;
                                    }
                                }
                                break;
                        }
                    }
                }
                ret = true;

            } catch (final Exception ex) {
                ret = false;
                PLog.errorLog(392840096, ex);

            } finally {
                try {
                    if (parser != null) {
                        parser.close();
                    }
                } catch (final Exception ignored) {
                }
            }
        }

        PDuration.counterStop("readConfig");
        return ret;
    }


    private boolean getConfig(XMLStreamReader parser, String xmlElem) {
        boolean ret = true;
        try {
            while (parser.hasNext()) {
                final int event = parser.next();
                if (event == XMLStreamConstants.END_ELEMENT) {
                    if (parser.getLocalName().equals(xmlElem)) {
                        break;
                    }
                }
                if (event == XMLStreamConstants.START_ELEMENT) {
                    final String s = parser.getLocalName();
                    final String n = parser.getElementText();
                    setConfigData(s, n);
                }
            }
        } catch (final Exception ex) {
            ret = false;
            PLog.errorLog(945120369, ex);
        }
        return ret;
    }

    private void setConfigData(String key, String value) {
        if (key.equals("system-geo-home-place")) {
            try {
                ProgConfig.SYSTEM_STYLE_SIZE.setValue(Integer.parseInt(value));
            } catch (Exception ex) {
                ProgConfig.SYSTEM_STYLE_SIZE.setValue(14);
            }
            ProgConfig.SYSTEM_GEO_HOME_PLACE.setValue(FilmData.GEO_DE);//war ein Fehler
            return;
        }
        if (key.equals("path-vlc")) {
            ProgConfig.SYSTEM_PROG_PLAY.setValue(value);
            return;
        }

        Config[] configs = ProgConfig.getInstance().getConfigsArr();
        for (Config config : configs) {
            if (config.getKey().equals(key)) {
                config.setActValue(value);
            }

            if (key.startsWith("COLOR_") && !value.isEmpty()) {
                ProgColorList.setColorData(key, value);
            }
        }
    }

    private int getInt(String key) {
        try {
            int i = Integer.parseInt(key);
            return i;
        } catch (Exception ex) {
            return 0;
        }
    }

    private boolean get(XMLStreamReader parser, String xmlElem, String[] xmlNames, String[] strRet) {
        boolean ret = true;
        final int maxElem = strRet.length;
        for (int i = 0; i < maxElem; ++i) {
            if (strRet[i] == null) {
                // damit Vorgaben nicht verschwinden!
                strRet[i] = "";
            }
        }
        try {
            while (parser.hasNext()) {
                final int event = parser.next();
                if (event == XMLStreamConstants.END_ELEMENT) {
                    if (parser.getLocalName().equals(xmlElem)) {
                        break;
                    }
                }
                if (event == XMLStreamConstants.START_ELEMENT) {
                    for (int i = 0; i < maxElem; ++i) {
                        if (parser.getLocalName().equals(xmlNames[i])) {
                            strRet[i] = parser.getElementText();
                            break;
                        }
                    }
                }
            }
        } catch (final Exception ex) {
            ret = false;
            PLog.errorLog(739530149, ex);
        }
        return ret;
    }

    @Override
    public void close() throws Exception {
    }
}
