 
### Systemvoraussetzungen

* Unterstützt wird Windows und Linux. Das Programm benötigt unter Windows und Linux eine aktuelle Java-VM ab Version: 17 (Java17 oder höher, die darüber hinaus benötigten JavaFX-Runtimes sind im Programm bereits für alle Betriebssysteme enthalten).

* Zum Ansehen und Aufzeichnen werden geeignete Zusatzprogramme benötigt. MTViewer ist vorbereitet für die Verwendung des VLC Media Player (zum Abspielen) sowie FFmpeg (zum Aufzeichnen).

* Beim ersten Start von MTViewer werden bereits die zwei Hilfsprogramme VLC Media Player und FFmpeg eingerichtet. Damit können alle Filme angesehen und aufgezeichnet werden.

#### Windows
* Für Windows muss nur der VLC Media Player installiert sein (FFmpeg wird mitgeliefert).

#### Linux
* Für Linux-Benutzer wird OpenJDK17 empfohlen.

* Bei Linux muss der VLC Media Player und FFmpeg (oder avconv) durch die Paketverwaltung installiert werden. Bei OpenSuse müssen zusätzlich zum VLC Media Player auch die vlc-codecs installiert werden.

* Wurden Alternativprogramme ausgewählt (z.B. avconv) müssen beim ersten Start diese ausgewählt werden oder später in den Einstellungen zum Download evtl. angepasst werden (der Pfad dafür ist dann wahrscheinlich:"/usr/bin/avconv").


### Installation

* MTViewer muss nicht installiert werden, das Entpacken der heruntergeladenen ZIP-Datei ist quasi die Installation.

* Das Programm wird in drei Paketen angeboten. Diese unterscheiden sich nur im "Zubehör", das Programm selbst ist in allen Paketen identisch:

	* **MTViewer-XX.zip**  
Das Programmpaket bringt nur das Programm und die benötigten Hilfsprogramme aber kein Java mit. Auf dem Rechner muss eine Java-Laufzeitumgebung ab Java17 installiert sein. Dieses Programmpaket kann auf allen Betriebssystemen verwendet werden. Es bringt Startdateien für Linux und Windows mit.

	* **MTViewer-XX__Linux+Java.zip**  
	**MTViewer-XX__Win+Java.zip**  
Diese Programmpakete bringen die Java-Laufzeitumgebung mit und sind nur für das angegebene Betriebssystem: Linux oder Windows. Es muss kein Java auf dem System installiert sein. (Die Java-Laufzeitumgebung liegt im Ordner "Java" und kommt von jdk.java.net).


#### Windows

1. Die heruntergeladene ZIP-Datei entpacken und den entpackten Ordner "MTViewer" ins Benutzerverzeichnis verschieben
2. Den eben entpackten MTViewer-Ordner öffnen, die Datei: MTViewer.exe ansteuern und per Rechtsklick in "Senden an" eine Verknüpfung auf den Desktop legen. Von dort aus kann MTViewer dann jeweils gestartet werden (oder auch mit Doppelklick auf die Datei: MTViewer.exe im Programmordner.
3. Die heruntergeladene ZIP-Datei kann nach dem Entpacken gelöscht werden

#### Linux

1. Die heruntergeladene ZIP-Datei entpacken und den entpackten Ordner "MTViewer" ins Benutzerverzeichnis verschieben
2. Mit einem Rechtsklick auf den Desktop eine neue Verknüpfung zu einem Programm anlegen und dort die Startdatei im eben entpackten MTViewer-Ordner: MTViewer.sh auswählen. Mit dieser Verknüpfung (oder direkt mit Klick auf die Datei: MTViewer.sh) kann dann das Programm gestartet werden. (Der Vorgang kann sich für unterschiedliche Distributionen etwas unterscheiden).
3. Die heruntergeladene ZIP-Datei kann nach dem Entpacken gelöscht werden


### Update

Das Update von MTViewer geschieht dadurch, dass man mit dem Inhalt der heruntergeladene ZIP-Datei die alten Dateien im Programmordner ersetzt. Die Einstellungen der vorhergehenden Programmversion werden von der neuen Version verwendet; es gibt also keinen neuen Einrichtungsdialog.


### Deinstallation

Es muss nur der Programmordner z.B. MTViewer und der Ordner mit den Einstellungen gelöscht werden. Die Einstellungen liegen im Ordner:  

|||
|:--:|:--:|
| Windows: | p2Mtviewer |
| Linux: | .p2Mtviewer |

<br />
Die Ordner werden als versteckte Ordner angelegt. Die Anzeige versteckter Ordner muss also im Dateimanager eingeschaltet sein. Es werden keine weiteren Änderungen am System vorgenommen. Werden dieser Einstellungsordner und der Programmordner gelöscht, ist das Programm wieder komplett entfernt.


### Starten

Für **Windows** (MTViewer.exe) und **Linux** (MTViewer.sh) sind eigene Startdateien enthalten, mit denen MTViewer direkt gestartet werden kann.

Ansonsten kann man das Programm auch aus der Konsole (im Programmordner) starten:  
bei Linux:  
*java -jar ./MTViewer.jar*  
*/PFAD_ZU_JAVA/java -jar ./MTViewer.jar*

oder bei Windows:  
*java -jar MTViewer.jar*  
*PFAD_ZU_JAVA\java.exe -jar MTViewer.jar*

#### Starten im portablen Modus

Für den portablen Einsatz z.B. auf einem USB-Stick stehen im Ordner "Info/Portable" entsprechende Startdateien zur Verfügung (z.B. *MTViewer__Windows__Portable.exe* für Windows). Diese Startdateien müssen in den MTViewer-Programmordner verschoben werden. Beim portablen Start wird der Ordner “Einstellungen” im MTViewer-Programmordner angelegt. Da hinein können, falls gewünscht, die bisherigen Einstellungen aus dem Ordner "p2Mtplayer" oder ".p2Mtplayer" kopiert werden.

Detailliertere Information zum portablen Modus von MTViewer kann man der Datei: Portable.txt im Ordner "Info/Portable" des MTViewer-Programmordners entnehmen. Interessierte können den Code für die Startdateien für Linux direkt den Startdateien selbst entnehmen. Die portable Startdatei für Windows enthält im Kern eine der folgende Zeilen Code:

*C:\Windows\SysWOW64\java -jar MTViewer.jar Einstellungen*  
*C:\WINDOWS\system32\java -jar MTViewer.jar Einstellungen*

#### Starten wenn Java nicht installiert werden kann oder soll

Java muss verfügbar sein, ohne kann das Programm nicht gestartet werden. Java muss aber nicht zwingend installiert werden.

Der Ablauf ist dann folgender:
* Download von Java: https://openjdk.org/
* Das Paket in einen beliebigen Ordner entpacken,
	z.B. in den Ordner c:\java oder für Linux /home/*user*/java
	
* Das Programm kann dann mit folgendem Aufruf gestartet werden:  
Windows  
*PFAD_ZU_JAVA\bin\java.exe -jar MTViewer.jar*  
*c:\java\bin\java.exe -jar MTViewer.jar*  
Linux  
*/PFAD_ZU_JAVA/bin/java -jar MTViewer.jar*  
*/home/*user*/java/bin/java -jar MTViewer.jar*

#### Starten mit zusätzlichen Parametern

*java -jar MTViewer.jar [Pfad] [Parameter]*  
*java -jar MTViewer.jar c:\temp*  
*java -jar MTViewer.jar Einstellungen*  
Im ersten Fall (c:\temp) verwendet das Programm für die Einstellungen den absoluten Ordner "c:\temp", im zweiten Fall wird der Ordner "Einstellungen" relativ zum Programmorder verwendet. (Damit kann man auch zwei Versionen des Programms parallel verwenden oder es kann auf einem USB-Stick verwendet werden.)

*java -jar MTViewer.jar -h*  
Das Programm gibt eine Hilfe mit möglichen Parametern aus.

#### Parameter, die die Einstellungen der JavaVM ändern

*java -jar -Dhttp.proxyHost=proxyserver -Dhttp.proxyPort=8080 MTViewer.jar*  
Es wird ein Proxyserver verwendet.

*java -Xms128M -Xmx1G -jar MTViewer.jar*  
*java -Xmx2G -jar MTViewer.jar*  
Die Parameter "-Xms128M" für Mindestspeicher und "-Xmx1G” für Maximalspeicher helfen bei geringem Arbeitsspeicher. Wenn dem Programm zu wenig Speicher vom Betriebssystem zugewiesen wird, kann der Parameter "-Xmx2G" helfen.

*java -Djava.net.preferIPv4Stack=true -jar MTViewer.jar*  
*java -Djava.net.preferIPv6Addresses=true -jar MTViewer.jar*  
Die Parameter “-Djava.net.preferIPv4Stack=true” und “-Djava.net.preferIPv6Addresses=true” ermöglicht eine Verbindung zum Internet, wenn der verwendete Netzwerk-Stack von Java nicht automatisch richtig erkannt wird, wodurch die Filmliste nicht geladen werden könnte.

