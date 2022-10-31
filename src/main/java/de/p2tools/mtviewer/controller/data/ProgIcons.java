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

import de.p2tools.mtviewer.controller.config.ProgConfig;
import de.p2tools.p2Lib.icons.GetIcon;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ProgIcons {
    public static String ICON_PATH = "/de/p2tools/mtviewer/res/program/";


    public enum Icons {
        ICON_DIALOG_ON("dialog-ein.png", "dialog-ein-sw.png"),
        IMAGE_ACHTUNG_64("achtung_64.png"),

        ICON_BUTTON_RESET("button-reset.png", 16, 16),
        ICON_BUTTON_PROPOSE("button-propose.png", 16, 16),
        ICON_BUTTON_BACKWARD("button-backward.png", 16, 16),
        ICON_BUTTON_FORWARD("button-forward.png", 16, 16),
        ICON_BUTTON_QUIT("button-quit.png", 16, 16),
        ICON_BUTTON_FILE_OPEN("button-file-open.png", 16, 16),
        ICON_DIALOG_QUIT("dialog-quit.png", 64, 64),
        IMAGE_TABLE_FILM_PLAY("table-film-play.png", 14, 14),
        IMAGE_TABLE_FILM_SAVE("table-film-save.png", 14, 14),
        IMAGE_TABLE_DOWNLOAD_START("table-download-start.png", 14, 14),
        IMAGE_TABLE_DOWNLOAD_DEL("table-download-del.png", 14, 14),
        IMAGE_TABLE_DOWNLOAD_STOP("table-download-stop.png", 14, 14),
        IMAGE_TABLE_DOWNLOAD_OPEN_DIR("table-download-open-dir.png", 14, 14),

        ICON_BUTTON_STOP("button-stop.png", 16, 16),
        ICON_BUTTON_NEXT("button-next.png", 16, 16),
        ICON_BUTTON_PREV("button-prev.png", 16, 16),
        ICON_BUTTON_REMOVE("button-remove.png", 16, 16),
        ICON_BUTTON_ADD("button-add.png", 16, 16),
        ICON_BUTTON_MOVE_DOWN("button-move-down.png", 16, 16),
        ICON_BUTTON_MOVE_UP("button-move-up.png", 16, 16),
        ICON_BUTTON_MOVE_TOP("button-move-top.png", 16, 16),
        ICON_BUTTON_MOVE_BOTTOM("button-move-bottom.png", 16, 16),

        FX_ICON_TOOLBAR_MENU("toolbar-menu.png", 18, 15);

        private String fileName;
        private String fileNameDark = "";
        private int w = 0;
        private int h = 0;

        Icons(String fileName, int w, int h) {
            this.fileName = fileName;
            this.w = w;
            this.h = h;
        }

        Icons(String fileName, String fileNameDark, int w, int h) {
            this.fileName = fileName;
            this.fileNameDark = fileNameDark;
            this.w = w;
            this.h = h;
        }

        Icons(String fileName) {
            this.fileName = fileName;
        }

        Icons(String fileName, String fileNameDark) {
            this.fileName = fileName;
            this.fileNameDark = fileNameDark;
        }

        public ImageView getImageView() {
            if (ProgConfig.SYSTEM_DARK_THEME.get() && !fileNameDark.isEmpty()) {
                return GetIcon.getImageView(fileNameDark, ICON_PATH, w, h);
            }
            return GetIcon.getImageView(fileName, ICON_PATH, w, h);
        }

        public Image getImage() {
            if (ProgConfig.SYSTEM_DARK_THEME.get() && !fileNameDark.isEmpty()) {
                return GetIcon.getImage(fileNameDark, ICON_PATH, w, h);
            }
            return GetIcon.getImage(fileName, ICON_PATH, w, h);
        }
    }
}
