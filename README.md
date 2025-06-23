[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](http://www.gnu.org/licenses/gpl-3.0)

# MTViewer
Das Programm MTViewer ist eine Art Suchmaschine für Filme der Mediatheken der Öffentlich-Rechtlichen Sender. Das Programm stellt eine Liste mit Links zu den Filmen zur Verfügung. Es ist möglich, diese URLs an externe Programme weiterzugeben. Mit diesen Programmen können dann diese Filme angesehen oder aufgezeichnet werden.

Das Programm MTViewer ist eine Alternative zu MTPlayer und damit wird ebenfalls die Filmliste des Projekts MediathekView.de durchsucht. Die Liste kann mit verschiedenen Filtern nach Beiträgen durchsucht werden. Mit einem Programm eigener Wahl können die Filme angesehen und aufgezeichnet werden. Es beschränkt sich aber auf das Nötigste (“nur mal schnell einen Film suchen”) und möchte so möglichst einfach und schnell zu bedienen sein. 
<br />


## Infos

Das Programm nutzt den Ordner ".p2Mtviewer" unter Linux oder den versteckten Ordner "p2Mtviewer" unter Windows als Konfig-Ordner, es kann also parallel zu MTPlayer benutzt werden. Man kann dem Programm auch einen Ordner für die Einstellungen mitgeben (und es z.B. auf einem USB-Stick verwenden):

```
java -jar MTViewer.jar ORDNER 
```

## Systemvoraussetzungen

Unterstützt wird Windows und Linux. Das Programm benötigt eine aktuelle Java-VM ab Version: Java 17. Für Linux-Benutzer wird OpenJDK17 empfohlen. (FX-Runtime bringt das Programm bereits mit und muss nicht installiert werden).
<br />


## Download
Das Programm wird in fünf Paketen angeboten. Diese unterscheiden sich nur im “Zubehör”, das Programm selbst ist in allen Paketen identisch: 

* **MTViewer-XX__Windows==SETUP__DATUM.exe**  
Mit diesem Programmpaket kann das Programm auf Windows installiert werden: Doppelklick und alles wird eingerichtet, auch ein Startbutton auf dem Desktop. Es muss auch kein Java auf dem System installiert sein. (Die Java-Laufzeitumgebung ist enthalten).

* **MTViewer-XX__DATUM.zip**  
Das Programmpaket bringt nur das Programm und die benötigten Hilfsprogramme aber kein Java mit. Auf dem Rechner muss eine Java-Laufzeitumgebung ab Java17 installiert sein. Dieses Programmpaket kann auf allen Betriebssystemen verwendet werden. Es bringt Startdateien für Linux und Windows mit. Zip entpacken und Programm Starten.

* **MTViewer-XX__Linux+Java__DATUM.zip**  
**MTViewer-XX__Win+Java__DATUM.zip**  
Diese Programmpakete bringen die Java-Laufzeitumgebung mit und sind nur für das angegebene Betriebssystem: Linux oder Windows. Es muss kein Java auf dem System installiert sein. (Die Java-Laufzeitumgebung liegt im Ordner: "Java" und kommt von jdk.java.net). Zip entpacken und Programm starten.

* **MTViewer-XX__Raspberry__DATUM.zip**  
Das ist ein Programmpaket, das auf einem Raspberry verwendet werden kann. Es muss ein aktueller Raspberry mit einer 64Bit CPU mit AArch64 Architektur sein. Zip entpacken und Programm Starten.


Windows:  
Der VLC-Player muss installiert sein.  
Linux:  
Der VLC-Player und ffmpeg müssen installiert sein.  

zum Download:  
[github.com/xaverW/MTViewer/releases](https://github.com/xaverW/MTViewer/releases)  

und hier können auch BETA-Versionen und Dailys geladen werden:  
[https://www.p2tools.de/mtviewer/download/](https://www.p2tools.de/mtviewer/download/)
<br />


## Installation
MTViewer-XX__Windows==SETUP__DATUM.exe wird durch einen Doppelklick darauf installiert. Die anderen Versionen müssen nicht installiert werden, das Entpacken der heruntergeladenen ZIP-Datei ist quasi die Installation. Die heruntergeladene ZIP-Datei entpacken und den entpackten Ordner “MTViewer...” ins Benutzerverzeichnis verschieben. Das Programm kann dann mit Doppelklick auf:  
Linux: “MTViewer__Linux.sh” oder  
Windows: “MTViewer__Windows.exe”  
gestartet werden.
<br />


## Infos

Weitere Infos zum Programm (Start und Benutzung) sind im Download-Paket enthalten oder können hier gefunden werden:
[https://www.p2tools.de/mtviewer/](https://www.p2tools.de/mtviewer/)  


## Website

[www.p2tools.de]( https://www.p2tools.de)


