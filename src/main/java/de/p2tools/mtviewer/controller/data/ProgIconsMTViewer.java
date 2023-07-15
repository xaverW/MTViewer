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


package de.p2tools.mtviewer.controller.data;

import de.p2tools.mtviewer.MTViewerController;
import de.p2tools.mtviewer.controller.config.ProgConst;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.icons.P2Icon;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ProgIconsMTViewer {
    public static String ICON_PATH = "res/program/";
    public static String ICON_PATH_LONG = "de/p2tools/mtviewer/res/program/";

    private static final List<P2IconMTViewer> iconList = new ArrayList<>();

    public static P2IconMTViewer ICON_DIALOG_ON = new P2IconMTViewer(ICON_PATH_LONG, ICON_PATH, "dialog-ein.png", 16, 16);
    public static P2IconMTViewer IMAGE_ACHTUNG_64 = new P2IconMTViewer(ICON_PATH_LONG, ICON_PATH, "achtung_64.png", 64, 64);

    public static P2IconMTViewer ICON_BUTTON_RESET = new P2IconMTViewer(ICON_PATH_LONG, ICON_PATH, "button-reset.png", 16, 16);
    public static P2IconMTViewer ICON_BUTTON_PROPOSE = new P2IconMTViewer(ICON_PATH_LONG, ICON_PATH, "button-propose.png", 16, 16);
    public static P2IconMTViewer ICON_BUTTON_BACKWARD = new P2IconMTViewer(ICON_PATH_LONG, ICON_PATH, "button-backward.png", 16, 16);
    public static P2IconMTViewer ICON_BUTTON_FORWARD = new P2IconMTViewer(ICON_PATH_LONG, ICON_PATH, "button-forward.png", 16, 16);
    public static P2IconMTViewer ICON_BUTTON_QUIT = new P2IconMTViewer(ICON_PATH_LONG, ICON_PATH, "button-quit.png", 16, 16);
    public static P2IconMTViewer ICON_BUTTON_FILE_OPEN = new P2IconMTViewer(ICON_PATH_LONG, ICON_PATH, "button-file-open.png", 16, 16);
    public static P2IconMTViewer ICON_DIALOG_QUIT = new P2IconMTViewer(ICON_PATH_LONG, ICON_PATH, "dialog-quit.png", 64, 64);
    public static P2IconMTViewer IMAGE_TABLE_FILM_PLAY = new P2IconMTViewer(ICON_PATH_LONG, ICON_PATH, "table-film-play.png", 14, 14);
    public static P2IconMTViewer IMAGE_TABLE_FILM_SAVE = new P2IconMTViewer(ICON_PATH_LONG, ICON_PATH, "table-film-save.png", 14, 14);
    public static P2IconMTViewer IMAGE_TABLE_DOWNLOAD_START = new P2IconMTViewer(ICON_PATH_LONG, ICON_PATH, "table-download-start.png", 14, 14);
    public static P2IconMTViewer IMAGE_TABLE_DOWNLOAD_DEL = new P2IconMTViewer(ICON_PATH_LONG, ICON_PATH, "table-download-del.png", 14, 14);
    public static P2IconMTViewer IMAGE_TABLE_DOWNLOAD_STOP = new P2IconMTViewer(ICON_PATH_LONG, ICON_PATH, "table-download-stop.png", 14, 14);
    public static P2IconMTViewer IMAGE_TABLE_DOWNLOAD_OPEN_DIR = new P2IconMTViewer(ICON_PATH_LONG, ICON_PATH, "table-download-open-dir.png", 14, 14);

    public static P2IconMTViewer ICON_BUTTON_STOP = new P2IconMTViewer(ICON_PATH_LONG, ICON_PATH, "button-stop.png", 16, 16);
    public static P2IconMTViewer ICON_BUTTON_NEXT = new P2IconMTViewer(ICON_PATH_LONG, ICON_PATH, "button-next.png", 16, 16);
    public static P2IconMTViewer ICON_BUTTON_PREV = new P2IconMTViewer(ICON_PATH_LONG, ICON_PATH, "button-prev.png", 16, 16);
    public static P2IconMTViewer ICON_BUTTON_REMOVE = new P2IconMTViewer(ICON_PATH_LONG, ICON_PATH, "button-remove.png", 16, 16);
    public static P2IconMTViewer ICON_BUTTON_ADD = new P2IconMTViewer(ICON_PATH_LONG, ICON_PATH, "button-add.png", 16, 16);
    public static P2IconMTViewer ICON_BUTTON_MOVE_DOWN = new P2IconMTViewer(ICON_PATH_LONG, ICON_PATH, "button-move-down.png", 16, 16);
    public static P2IconMTViewer ICON_BUTTON_MOVE_UP = new P2IconMTViewer(ICON_PATH_LONG, ICON_PATH, "button-move-up.png", 16, 16);
    public static P2IconMTViewer ICON_BUTTON_MOVE_TOP = new P2IconMTViewer(ICON_PATH_LONG, ICON_PATH, "button-move-top.png", 16, 16);
    public static P2IconMTViewer ICON_BUTTON_MOVE_BOTTOM = new P2IconMTViewer(ICON_PATH_LONG, ICON_PATH, "button-move-bottom.png", 16, 16);
    public static P2IconMTViewer ICON_BUTTON_CLEAN = new P2IconMTViewer(ICON_PATH_LONG, ICON_PATH, "clean_16.png", 16, 16);
    public static P2IconMTViewer ICON_BUTTON_START_ALL = new P2IconMTViewer(ICON_PATH_LONG, ICON_PATH, "button-start-all.png", 16, 16);
    public static P2IconMTViewer ICON_BUTTON_STOP_ALL = new P2IconMTViewer(ICON_PATH_LONG, ICON_PATH, "button-stop-all.png", 16, 16);

    public static P2IconMTViewer FX_ICON_TOOLBAR_MENU = new P2IconMTViewer(ICON_PATH_LONG, ICON_PATH, "toolbar-menu.png", 18, 15);

    public static void initIcons() {
        iconList.forEach(p -> {
            String url = p.genUrl(P2IconMTViewer.class, MTViewerController.class, ProgConst.class, ProgIconsMTViewer.class, P2LibConst.class);
            if (url.isEmpty()) {
                // dann wurde keine gefunden
                System.out.println("ProgIconsInfo: keine URL, icon: " + p.getPathFileNameDark() + " - " + p.getFileName());
            }
        });
    }

    public static class P2IconMTViewer extends P2Icon {
        public P2IconMTViewer(String longPath, String path, String fileName, int w, int h) {
            super(longPath, path, fileName, w, h);
            iconList.add(this);
        }

        public boolean searchUrl(String p, Class<?>... clazzAr) {
            URL url;
            url = MTViewerController.class.getResource(p);
            if (set(url, p, "P2InfoController.class.getResource")) return true;
            url = ProgConst.class.getResource(p);
            if (set(url, p, "ProgConst.class.getResource")) return true;
            url = ProgIconsMTViewer.class.getResource(p);
            if (set(url, p, "ProgIconsInfo.class.getResource")) return true;
            url = this.getClass().getResource(p);
            if (set(url, p, "this.getClass().getResource")) return true;

            url = ClassLoader.getSystemResource(p);
            if (set(url, p, "ClassLoader.getSystemResource")) return true;
            url = P2LibConst.class.getClassLoader().getResource(p);
            if (set(url, p, "P2LibConst.class.getClassLoader().getResource")) return true;
            url = ProgConst.class.getClassLoader().getResource(p);
            if (set(url, p, "ProgConst.class.getClassLoader().getResource")) return true;
            url = this.getClass().getClassLoader().getResource(p);
            if (set(url, p, "this.getClass().getClassLoader().getResource")) return true;

            return false;
        }
    }
}
