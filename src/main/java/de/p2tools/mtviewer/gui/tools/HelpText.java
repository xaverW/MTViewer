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

package de.p2tools.mtviewer.gui.tools;

import de.p2tools.p2Lib.P2LibConst;

public class HelpText {

    public static final String FILTER_INFO =
            "Die Menüs, Textfelder und Schieberegler erlauben ein detailliertes " +
                    "Durchsuchen und Filtern der vorhandenen Filme." +
                    "\n" +
                    "\n" +

                    "Mit dem Filter \"Sender\" können ein oder mehrere Sender vorgegeben werden, " +
                    "in denen die Sendungen gesucht werden." +
                    "\n" +
                    "Die Filter \"Thema\" und \"Titel\" suchen im jeweiligen Feld: Thema oder Titel." +
                    "\n" +
                    "Der Filter \"Irgendwo\" sucht in den Felder: Thema, Titel, Beschreibung und " +
                    "Datum (z.B. nach \"20.11.\")." +
                    "\n" +
                    "Mit dem Filtern \"Zeitraum\" und \"Nur neue Filme\" können die Ergebnisse auf " +
                    "einen kleineren Zeitraum oder auf nur \"neue Filme\" eingeschränkt werden." +
                    "\n" +
                    "Nur Live-Streams\" zeigt auch nur \"Live-Streams\" an." +
                    "\n" +
                    "\n" +
                    "[=====================================]" +
                    "\n" +


                    "Mit den Textfeldern kann nach einem wörtlichen Suchtext (Suchtext muss enthalten sein/" +
                    "oder darf nicht enthalten sein) oder nach " +
                    "regulären Ausdrücken (RegExp) gesucht werden.\n" +
                    "\n" +
                    "Groß- und Kleinschreibung wird bei beiden Arten der Suche nicht unterschieden.\n" +
                    "\n" +
                    "-- Wörtlicher Suchtext muss enthalten sein --\n" +
                    "Ein wörtlicher Suchtext findet alle Dateien bei denen der Suchtext an beliebiger " +
                    "Stelle im durchsuchten Bereich enthalten ist.\n" +
                    "\n" +
                    "Um mehrere Begriffe zu suchen müssen diese durch Komma oder Doppelpunkt " +
                    "getrennt werden. Das Komma verknüpft die Begriffe mit ODER (=> mindestens einer der Begriffe " +
                    "muss vorkommen), der Doppelpunkt mit UND (=> alle Begriffe müssen vorkommen).\n" +
                    "\n" +
                    "\n" +

                    "-- Wörtlicher Suchtext darf nicht enthalten sein --\n" +
                    "Der Suchtext muss mit '!:' (ohne die ' ') beginnen.\n" +
                    "Ein wörtlicher Suchtext schließt alle Dateien aus, bei denen der Suchtext an beliebiger " +
                    "Stelle im durchsuchten Bereich enthalten ist.\n" +
                    "\n" +
                    "Um mehrere Begriffe auszuschließen, müssen diese durch Komma oder Doppelpunkt " +
                    "getrennt werden. Das Komma verknüpft die Begriffe mit ODER (=> mindestens einer der Begriffe " +
                    "muss enthalten sein). Der Doppelpunkt verknüpft die Begriffe mit UND " +
                    "(=> alle Begriffe müssen enthalten sein, damit der Beitrag ein Treffer ist).\n" +
                    "\n" +
                    "\n" +

                    "Suchtext und Suchbegriffe dürfen Leerzeichen enthalten, aber kein Komma und keinen Doppelpunkt.\n" +
                    "\n" +
                    "Beispiele:\n" +
                    "'Tagesschau' findet u.a. 'Tagesschau, 12:00 Uhr', 'ARD Tagesschau Livestream', 'Bei Logo und der Tagesschau'.\n" +
                    "'Sport,Fussball' (Komma-getrennt) findet Filme bei denen 'Sport' oder 'Fussball' " +
                    "oder beides vorkommt, u.a. 'Wintersport im Mumintal' und 'Wie wird man Fussballprofi?'.\n" +
                    "'Sport:Fussball' (Doppelpunkt-getrennt) findet nur Dateien bei denen " +
                    "beides ('Sport' und 'Fussball') vorkommt, z.B. 'Wintersport, Fussball und Formel 1'.\n" +
                    "\n" +
                    "'!:Auto,Motorrad', es werden nur Beiträge gefunden, die weder \"Auto\" noch \"Motorrad\" " +
                    "enthalten.\n" +
                    "'!:Auto:Motorrad', es werden Beiträge gefunden, die \"Auto\" *und* \"Motorrad\" " +
                    "*nicht* enthalten.\n" +

                    "\n" +
                    "-- Reguläre Ausdrücke --\n" +
                    "Ein Suchtext aus regulären Ausdrücken (RegExp) muss mit '#:' (ohne die ' ') beginnen.\n" +
                    "\n" +
                    "Beliebige Zeichen mit in die Suche aufnehmen (als '.+', '.*' oder '.?'') macht einen Unterschied!\n" +
                    "\n" +
                    "Beispiele:\n" +
                    "'#:Burger' oder '#:^burger$' -- beide finden 'Burger' und 'burger' und nichts sonst.\n" +
                    "'#:Burger.+' findet nicht 'Burger', aber alles was nach 'Burger' noch Zeichen " +
                    "enthält (z.B. 'Burger für die Straubing Tigers').\n" +
                    "'#:.+burger' findet alles was auf 'burger' oder 'Burger' " +
                    "endet (z.B. 'Der Hamburger' und '1. Burger').\n" +
                    "\n" +
                    "Mehr zu regulären Ausdrücken:\n" +
                    "https://de.wikipedia.org/wiki/Regul%C3%A4rer_Ausdruck" +
                    "\n";

    public static final String PROG_PATH_VLC =
            "Hier muss ein Standardprogramme zum Ansehen der Filme eingetragen werden. VLC ist " +
                    "ein gutes Programm dafür.\n" +
                    "\n" +
                    "Wenn der Pfad nicht automatisch erkannt wird, kann man ihn auch per Hand auswählen.\n" +
                    "\n" +
                    "Installation unter Linux:\n" +
                    "Am einfachsten über die Paketverwaltung.\n" +
                    "\n" +
                    "Installation unter Windows:\n" +
                    "VLC kann aus dem Internet geladen werden, Downloadquelle:\n" +
                    "http://www.videolan.org\n" +
                    "\n";

    public static final String PROG_PATH_FFMPEG =
            "Hier muss ein Standardprogramme zum Download von Videostreams eingetragen werden. FFmpeg ist " +
                    "dafür vorgesehen.\n" +
                    "\n" +
                    "Wenn der Pfad nicht automatisch erkannt wird, kann man ihn auch per Hand auswählen.\n" +
                    "\n" +
                    "Installation unter Linux:\n" +
                    "Am einfachsten über die Paketverwaltung.\n" +
                    "\n" +
                    "Installation unter Windows:\n" +
                    "Da wird das Programm mitgeliefert und muss nicht installiert werden.\n" +
                    "\n";

    public static final String GUI_DOWNLOAD_FILTER =
            "Die Filter erlauben ein detailliertes " +
                    "Durchsuchen und Filtern der vorhandenen Downloads.\n" +
                    "\n" +
                    "===================================\n" +
                    "Filter [Sender] und [Status] sucht Downloads mit dem Sender " +
                    "oder mit dem Status (gestartet, nicht gestartet, fertig).\n" +
                    "\n" +
                    "Mit den Schiebereglern: [gleichzeitige Downloads] und [max. Bandbreite] " +
                    "kann die Anzahl der gleichzeitigen Downloads die geladen werden, " +
                    "festgelegt werden. Die vorgegebene " +
                    "maximale Bandbreite gilt pro Download.\n" +
                    "\n" +
                    "===================================\n" +
                    "Besonderheiten:\n" +
                    "Für die maximale Anzahl an Downloads gibt es noch eine Einschränkung: Es können maximal 2 " +
                    "Downloads pro Server (ist meist dann auch pro Sender) geladen werden. Wenn z.B. alle " +
                    "Downloads vom ZDF sind, werden " +
                    "maximal 2 Downloads gestartet auch wenn als Maximum mehr vorgegeben ist." +
                    "\n" +
                    "(Ausgenommen davon sind explizite Downloadfarmen wie z.B. Akamai die ARTE benutzt.)" +
                    "\n";

    public static final String CONFIG_GEO =
            "Nicht alle Filme lassen sich aus allen Ländern abrufen (Geoblocking). Man kann hier " +
                    "seinen Standort angeben, wenn geblockte Filme in der Liste markiert werden sollen.\n" +
                    "\n" +
                    "Manche Sender überprüfen die abfragende URL, so dass man trotz zum Senderland " +
                    "passender Einstellung, nicht auf geblockte Downloads zugreifen kann.\n" +
                    "\n" +
                    "Ein Downloadversuch geblockter Sendungen bricht sofort ab und der Download " +
                    "wird als 'fehlerhaft' angezeigt.\n" +
                    "\n" +
                    "MTViewer kennt aber nicht alle Muster für geogeblockte Sendungen. Ob ein fehlerhafter " +
                    "Download auf Geoblocking zurückzuführen ist, zeigt sich beim Klick auf den Link zur " +
                    "Sendung ('zur Website' ganz unten in der Ansicht 'Filme'). Wenn die Sendung auch auf der " +
                    "Website des Senders nicht abgespielt werden kann, liegt fast immer Geoblocking vor." +
                    "\n";

    public static final String CONFIG_STYLE =
            "Die Schriftgröße sollte sich automatisch auf die vorgegebene Größe im " +
                    "Betriebssystem, einstellen. Sie kann hier eingestellt werden, wenn " +
                    "die Automatik nicht funktioniert oder eine andere Größe gewünscht wird.\n" +
                    "\n" +
                    "Damit die Änderung wirksam wird, ist evtl. ein Neustart des Programms erforderlich." +
                    "\n";

    public static final String CONFIG_SEARCH_UPDATE =
            "Beim Programmstart wird geprüft, ob es eine neue Version des Programms gibt. " +
                    "Ist eine aktualisierte Version vorhanden, dann wird das gemeldet."
                    + P2LibConst.LINE_SEPARATORx2 +
                    "Das Programm wird aber nicht ungefragt ersetzt." +
                    "\n";

    public static final String CONFIG_SEARCH_UPDATE_DAILY =
            "Das sind \"Zwischenschritte\" auf dem Weg zur nächsten Version. Hier ist die " +
                    "Entwicklung noch nicht abgeschlossen und das Programm kann noch Fehler enthalten. Wer Lust hat, " +
                    "einen Blick auf die nächste Version zu werfen, ist eingeladen, die Vorabversionen zu testen." +
                    P2LibConst.LINE_SEPARATORx2 +
                    "Ist eine aktualisierte Vorabversion vorhanden, dann wird das gemeldet."
                    + P2LibConst.LINE_SEPARATORx2 +
                    "Das Programm wird aber nicht ungefragt ersetzt." +
                    "\n";

    public static final String GUI_FILMS_EDIT_FILTER =
            "Suchbeginn verzögern: Hier kann eine Zeit eingestellt werden, die den Start der " +
                    "Suche verzögert." +
                    "\n" +
                    "\n" +
                    "\"Return\": Diese Einstellung startet die Suche in den Textfeldern erst nach Eingabe " +
                    "der Return-Taste." +
                    "\n" +
                    "\n" +
                    "In den Textfeldern wird die Suche immer sofort nach Eingabe der Return-Taste " +
                    "gestartet. Mit der Einstellung \"Return\" aber ausschließlich. " +
                    "Bei den anderen Suchfeldern (Zeitraum, ..) startet die " +
                    "Suche immer sofort nach der Einstellung." +
                    "\n" +
                    "\n" +
                    "Ich kann eine Suche also starten wenn ich ein Suchfeld " +
                    "(egal welches) ändere und die Wartezeit abwarte. " +
                    "In einem Textfeld (egal welchem) kann ich zusätzlich die Suche starten " +
                    "wenn ich \"Return\" tippe." +
                    "\n";

    public static final String USE_RESOLUTION =
            "Nicht jede Auflösung wird von jedem Sender angeboten. Wenn die gewünschte " +
                    "Auflösung nicht verfügbar ist, wird automatisch die hohe Auflösung abgerufen.\n" +
                    "\n";

    public static final String DOWNLOAD_REPLACELIST =
            "Die Tabelle wird von oben nach unten abgearbeitet. Es ist also möglich, " +
                    "dass eine Ersetzung durch eine weitere ganz oder teilweise " +
                    "rückgängig gemacht wird!" +
                    "\n";

    public static final String DOWNLOAD_ONLY_ASCII =
            "Es werden alle Zeichen über ASCII 127 ersetzt. Umlaute werden aufgelöst (z.B. 'ö' -> 'oe').\n" +
                    "\n" +
                    "Wenn die Ersetzungstabelle aktiv ist wird sie vorher abgearbeitet." +
                    "\n";

    public static final String DOWNLOAD_SSL_ALWAYS_TRUE =
            "Bei Downloads mit \"https-URL\" wird die Verbindung über SSL " +
                    "aufgebaut. Wenn SSL-Zertifikate auf dem Rechner fehlen oder das Server-Zertifikat fehlerhaft ist, kommt es " +
                    "zu Download-Fehlern. Der Download bricht mit einer Fehlermeldung ab. " +
                    P2LibConst.LINE_SEPARATORx2 +
                    "Die Überprüfung der Zertifikate kann mit dieser Funktion abgeschaltet werden." +
                    "\n";

    public static final String DOWNLOAD_FINISHED =
            "Wenn ein Download erfolgreich beendet ist, wird mit einem Fenster informiert." +
                    "\n";

    public static final String DOWNLOAD_CONTINUE =
            "Wenn ein bereits teilweise geladener Download neu startet, " +
                    "kann er weitergeführt oder von Anfang an, neu gestartet werden." +
                    P2LibConst.LINE_SEPARATORx2 +
                    "Hier kann ausgewählt werden, was gemacht werden soll:\n" +
                    "* Jedes mal vorher fragen\n" +
                    "* Sofort weiterführen\n" +
                    "* Immer von Anfang an neu starten." +
                    "\n";

    public static final String LOAD_ONLY_FILMS =
            "\"Nur Filme der letzten Tage laden:\" Die Filmliste enthält nur Filme aus diesem Zeitraum. " +
                    "Filme ohne Datum sind immer enthalten.\n" +
                    "\n" +
                    "\"Nur Filme mit Mindestlänge laden:\" Die Filmliste enthält nur Filme von " +
                    "mindestens dieser Dauer. Filme ohne Längenangabe sind immer enthalten.\n" +
                    "\n" +
                    "Bei 'alles laden' sind alle Filme enthalten.\n" +
                    "\n" +
                    "Das Filtern der Filmliste kann bei älteren Rechnern mit wenig Speicher " +
                    "hilfreich sein: Bei 'maximal 250 Tage' und 'mindestens 5 Minuten' ist die " +
                    "Filmliste nur etwa 1/4 so groß (~ 120.000 Filme).\n" +
                    "\n" +
                    "Auswirkung hat das Filtern erst nach dem " +
                    "Neuladen der Filmliste." +
                    "\n";

    public static final String LOAD_FILMLIST_SENDER =
            "Filme der markierten Sender werden aus der Filmliste ausgeschlossen.\n" +
                    "\n" +
                    "Wirksam erst nach dem Neuladen der kompletten Filmliste." +
                    "\n";

    public static final String LOAD_FILMLIST_PROGRAMSTART =
            "Die Filmliste wird beim Programmstart automatisch geladen, " +
                    "wenn sie älter als 3 Stunden ist. Sie kann auch über den " +
                    "Menüpunkt \"Eine neue Filmliste laden\" im Programmmenü aktualisiert werden.\n" +
                    "\n" +
                    "Zum Update werden dann nur noch Differenzlisten geladen " +
                    "(diese enthalten nur neu hinzugekommene Filme)." +
                    "\n";

    public static final String DIAKRITISCHE_ZEICHEN =
            "\"Diakritische Zeichen ändern\" meint, dass bestimmte Zeichen in den " +
                    "Filmfeldern: \"Titel, Thema und Beschreibung\" " +
                    "angepasst werden. Aus z.B.\n\n" +
                    "\"äöü ń ǹ ň ñ ṅ ņ ṇ ṋ ç č c\" wird dann\n" +
                    "\"äöü n n n n n n n n c c c\".\n" +
                    "\n" +
                    "Das Programm arbeitet dann mit der angepassten " +
                    "Filmliste. Beim Suchen nach Filmen werden die *angepassten " +
                    "Zeichen* in Titel, Thema und Beschreibung verwendet. Es werden dann z.B. " +
                    "\"Dvořak\", \"Noël\" und \"Niño\" nicht mehr gefunden, aber stattdessen " +
                    "\"Dvorak\", \"Noel\" und \"Nino\".\n" +
                    "\n\n" +
                    "Nach dem Einschalten der Funktion und dem " +
                    "Schließen des Dialogs wird die Filmliste sofort geändert. " +
                    "Wird die Funktion dagegen wieder *ausgeschaltet*, werden die Änderungen erst " +
                    "nach dem Neuladen einer Filmliste wirksam.";

    public static final String TIP_OF_DAY =
            "Beim Programmstart wird (einmal täglich) ein Tip zur Verwendung " +
                    "des Programms angezeigt. Das passiert so oft, bis alle Tips " +
                    "einmal angezeigt wurden." +
                    "\n";

    public static final String DARK_THEME =
            "Das Programm wird damit mit einer dunklen Programmoberfläche angezeigt. " +
                    "Damit alle Elemente der Programmoberfläche geändert werden, kann ein " +
                    "Programmneustart notwendig sein." +
                    "\n";

    public static final String SHORTCUT =
            "Zum Ändern eines Tastenkürzels, seinen \"Ändern\"-Button klicken und dann " +
                    "die gewünschten neuen Tasten drücken.\n" +
                    "\n" +
                    "Der \"Zurücksetzen\"-Button stellt den Originalzustand wieder her.\n" +
                    "\n" +
                    "Damit die Änderungen wirksam werden, muss das Programm neu gestartet werden." +
                    "\n";

    public static final String USER_AGENT =
            "Hier kann ein User Agent angegeben werden, der bei Downloads als Absender " +
                    "verwendet wird. Bleibt das Feld leer, wird kein User Agent verwendet.\n" +
                    "\n" +
                    "Solange alles funktioniert, kann das Feld leer bleiben. Ansonsten wäre " +
                    "das z.B. eine Möglichkeit: 'Mozilla/5.0'.\n" +
                    "\n" +
                    "Es sind nur ASCII-Zeichen erlaubt und die Textlänge ist begrenzt auf 100 Zeichen." +
                    "\n";

    public static final String LOGFILE =
            "Im Logfile wird der Programmverlauf aufgezeichnet. Das kann hilfreich sein, " +
                    "wenn das Programm nicht wie erwartet funktioniert.\n" +
                    "\n" +
                    "Der Standardordner für das Log ist 'Log' im Konfigurations-Ordner des " +
                    "Programms. Der Ort kann geändert werden.\n" +
                    "\n" +
                    "Ein geänderter Pfad zum Logfile wird erst nach einem Neustart des " +
                    "Programms genutzt; mit dem Button \"Pfad zum Logfile jetzt schon verwenden\" wird " +
                    "sofort ins neue Log geschrieben." +
                    "\n";

    public static final String VIDEOPLAYER =
            "Um einen Film in einem Videoplayer abzuspielen, kann hier ein Player angegeben werden. " +
                    "Empfohlen ist der VLC.\n\n" +
                    "Wird nichts angegeben, wird versucht den Videoplayer des " +
                    "Betriebssystems zu verwenden. Klappt das aber nicht, können die Filme nicht abgespielt " +
                    "werden." +
                    "\n";

    public static final String PLAY_FILE_HELP_PARAMETER =
            "Hier werden die Parameter zum gewählten Programm eingetragen.\n" +
                    "\n" +
                    "[Programm:] In dem Feld steht NUR das Programm: ('Pfad/Programmdatei', " +
                    "Windows: 'Pfad\\Programmdatei') " +
                    "und keine Argumente (Schalter, Optionen, etc.)!\n" +
                    "\n" +
                    "[Parameter:] In diesem Feld werden die Argumente (Schalter, Optionen, etc.) des gewählten " +
                    "Programms angegeben. Sie sollten in dessen Dokumentation zu finden sein.\n" +
                    "Meist reicht: '%f'\n\n" +
                    "Von MTViewer kann folgender Parameter genutzt werden:\n" +
                    "\n" +
                    "%f Original-URL des Films\n" +
                    "\n" +
                    "Beispiel für VLC:\n" +
                    "===============\n" +
                    "Programm: '/usr/bin/vlc' (Windows: '%PROGRAMFILES%\\VideoLAN\\VLC\\vlc.exe')\n" +
                    "Parameter: '%f\n" +
                    "\n" +
                    "Hier wird %f durch die URL des Films ersetzt. Es resultiert:\n" +
                    "'/usr/bin/vlc URL' bzw. 'C:\\VideoLAN\\VLC\\vlc.exe URL'.\n" +
                    "\n" +
                    "Mit dem Parametern (für Linux):\n" +
                    "’%f --qt-minimal-view’\n" +
                    "wird der VLC mit minimalem Gui gestartet.";


    public static final String WEBBROWSER =
            "Wenn das Programm versucht, einen Link zu öffnen (z.B. \"Anleitung im Web\" im " +
                    "Programm-Menü unter \"Hilfe\") und der Standardbrowser nicht startet, " +
                    "kann damit ein Programm (Firefox, Chromium, …) ausgewählt und fest " +
                    "zugeordnet werden." +
                    "\n";

    public static final String RESET_DIALOG =
            "-- Nichts ändern --\n" +
                    "Der Dialog wird ohne eine Änderung geschlossen.\n" +
                    "\n" +
                    "-- Alle Einstellungen zurücksetzen --\n" +
                    "ALLE EINSTELLUNGEN WERDEN GELÖSCHT! Das Programm wird in den " +
                    "Ursprungszustand zurückgesetzt.\n" +
                    "Es beendet sich und muss neu gestartet werden.\n" +
                    "Der neue Start beginnt mit dem Einrichtungsdialog." +
                    "\n";
    public static final String PSET_DEST_FILE_SIZE =
            "Die Länge des Dateinamens eines Downloads kann beschränkt werden.\n" +
                    "\n" +
                    "\"Länge des ganzen Dateinamens:\" Der gesamte Dateiname wird ermittelt und falls nötig gekürzt.\n" +
                    "\n" +
                    "\"Länge einzelner Felder:\" Die Länge einzelner Felder (Parameter) des Dateinamens wird begrenzt.\n" +
                    "Das bezieht sich nur auf die Felder mit variabler Länge:\n" +
                    "%t, %T, %s, %N (Thema, Titel, Sender, Originaldateiname).\n";

    public static final String PSET_FILE_NAME =
            "Beim Dateinamen sind diese Parameter möglich:\n" +
                    "\n" +
                    "%D   Sendedatum des Films, wenn leer von 'heute'\n" +
                    "%d   Sendezeit des Films, wenn leer von 'jetzt'\n" +
                    "%H   'heute', aktuelles Datum im Format JJJJMMTT, z.B. '20090815' am 15.08.2009\n" +
                    "%h   'jetzt', aktuelle Uhrzeit im Format HHMMss, z.B. '152059' um 15:20:59 Uhr\n" +
                    "\n" +
                    "%1   Tag, vom Sendedatum des Films, wenn leer von 'heute'\n" +
                    "%2   Monat, ebenso\n" +
                    "%3   Jahr, ebenso\n" +
                    "\n" +
                    "%4   Stunde, von der Sendezeit des Films, wenn leer von 'jetzt'\n" +
                    "%5   Minute, ebenso\n" +
                    "%6   Sekunde, ebenso\n" +
                    "\n" +
                    "%s   Sender des Films\n" +
                    "%T   Titel des Films\n" +
                    "%t   Thema des Films\n" +
                    "\n" +
                    "%N   Originaldateiname des Films (der kann sehr kryptisch und lang sein)\n" +
                    "%S   Suffix des Originaldateinamens des Films (z.B. 'mp4')\n" +
                    "\n" +
                    "%i   Filmnummer (ändert sich beim Neuladen der Filmliste!)\n" +
                    "%q   Qualität des Films ('HD', 'H', 'L')\n" +
                    "\n" +
                    "%Z   Hashwert der URL, z.B.: '1433245578'\n" +
                    "%z   Hashwert der URL, angehängtes Suffix (entspricht '%Z.%S'), z.B.: '1433245578.mp4'\n" +
                    "\n" +
                    "Beispiele:\n" +
                    "Am 10.05.2021 liefert '%H__%t__%T' z.B. '20210510__Natur__Wildes Shetland' (kein Suffix)\n" +
                    "und '%H__%t__%T.S' liefert z.B. '20210510__Natur__Wildes Shetland.xxx' (mit dem Originalsuffix)";

    public static final String PSET_FILE_HELP_PROGRAM =
            "Hier muss ein Standardprogramme zum Download von Videostreams (URL endet mit: 'm3u8') " +
                    "eingetragen werden. FFmpeg ist " +
                    "dafür vorgesehen.\n" +
                    "\n" +
                    "Bei Linux wäre das der Pfad:\n" +
                    "'/usr/bin/ffmpeg'\n" +
                    "Bei Windows wird das eingetragen:\n" +
                    "\"bin\\ffmpeg.exe\"\n\n" +
                    "Wenn der Pfad nicht automatisch erkannt wird, kann man ihn auch per Hand auswählen.\n" +
                    "\n" +
                    "Installation unter Linux:\n" +
                    "Am einfachsten über die Paketverwaltung.\n" +
                    "\n" +
                    "Installation unter Windows:\n" +
                    "Da wird das Programm mitgeliefert und muss nicht installiert werden.\n" +
                    "\n";

    public static final String PSET_FILE_HELP_PARAMETER =
            "Hier werden die Parameter zum gewählten Programm eingetragen.\n" +
                    "\n" +
                    "[Programm:] In dem Feld steht NUR das Programm: ('Pfad/Programmdatei', " +
                    "Windows: 'Pfad\\Programmdatei') " +
                    "und keine Argumente (Schalter, Optionen, etc.)!\n" +
                    "\n" +
                    "[Parameter:] In diesem Feld werden die Argumente (Schalter, Optionen, etc.) des gewählten " +
                    "Programms angegeben. Sie sollten in dessen Dokumentation zu finden sein.\n" +
                    "Von MTViewer können folgende Parameter genutzt werden:\n" +
                    "\n" +
                    "%f Original-URL des Films\n" +
                    "%a Zielverzeichnis des Downloads\n" +
                    "%b Dateiname des Downloads\n" +
                    "** (= zwei Sterne) Zielpfad (= Zielverzeichnis mit Dateiname)\n" +
                    "'**' ist identisch mit '%a/%b' (Windows: '%a\\%b')\n" +
                    "\n" +
                    "Beispiel für VLC:\n" +
                    "===============\n" +
                    "Programm: '/usr/bin/vlc' (Windows: '%PROGRAMFILES%\\VideoLAN\\VLC\\vlc.exe')\n" +
                    "Parameter: '%f :sout=#standard{access=file,mux=ts,dst=**} -I dummy --play-and-exit'\n" +
                    "Dateiname: '%t-%T.ts'\n" +
                    "\n" +
                    "Hier wird %f durch die URL des Films ersetzt. %t und %T werden durch Thema und Titel als " +
                    "Dateinamen ersetzt und in den Programmschalter mit dem Pfad anstatt der '**' eingesetzt. Als " +
                    "Downloaddatei resultiert:\n" +
                    "'Volumes/Pfad/Thema-Titel.ts' bzw. 'C:\\Pfad\\Thema-Titel.ts'.\n" +
                    "\n" +
                    "Beispiel für ffmpeg:\n" +
                    "=====================\n" +
                    "Windows:\n" +
                    "Der Pfad wird hier relativ zur Programmdatei von MTViewer angegeben, weil MTViewer für " +
                    "Windows das Programm ffmpeg schon im Ordner 'bin' mitbringt:\n" +
                    "Programm: 'bin\\ffmpeg.exe'\n" +
                    "Parameter: '-user_agent \"Mozilla/5.0\" -i %f -c copy -bsf:a aac_adtstoasc **'\n" +
                    "Dateiname: '%t-%T.mp4'\n" +
                    "\n" +
                    "Bei Linux ist der Pfad zum Programm:\n" +
                    "Programm: '/usr/bin/ffmpeg'\n" +
                    "\n" +
                    "Hier wird %f durch die URL des Films ersetzt. %t und %T werden durch Thema und Titel als " +
                    "Dateinamen ersetzt und in den Programmschalter mit dem Pfad anstatt der '**' eingesetzt. Als " +
                    "Downloaddatei resultiert:\n" +
                    "'Volumes/Pfad/Thema-Titel.mp4' bzw. 'C:\\Pfad\\Thema-Titel.mp4'.\n";

}
