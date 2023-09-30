module mtviewer {
    opens de.p2tools.mtviewer;
    exports de.p2tools.mtviewer;

    opens de.p2tools.mtviewer.controller.data;
    opens de.p2tools.mtviewer.controller.data.download;

    requires de.p2tools.p2lib;
    requires javafx.controls;
    requires org.controlsfx.controls;

    requires java.logging;
    requires java.desktop;

    requires commons.cli;
    requires com.fasterxml.jackson.core;
    requires org.tukaani.xz;

    requires okhttp3;
    requires org.apache.commons.lang3;
    requires org.apache.commons.io;
}

