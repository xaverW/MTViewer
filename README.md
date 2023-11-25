[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](http://www.gnu.org/licenses/gpl-3.0)

# MTViewer

Das Programm MTPViewer ist eine Art Suchmaschine für Filme der Mediatheken der Öffentlich-Rechtlichen Sender. Das Programm stellt eine Liste mit Links zu den Filmen zur Verfügung. Es ist möglich, diese URLs an externe Programme weiterzugeben. Mit diesen Programmen können dann diese Filme angesehen oder aufgezeichnet werden.

Das Programm MTViewer ist eine Alternative zu meinem Programm MTPlayer. MTViewer konzentriert sich auf das Nötigste: Film ansehen und Film speichern. Es ist daher schnell und einfach zu bedienen.
<br />


## Infos

Das Programm nutzt den Ordner ".p2Mtviewer" unter Linux oder den versteckten Ordner "p2Mtviewer" unter Windows als Konfig-Ordner, es kann also parallel zu MTPlayer benutzt werden. Man kann dem Programm auch einen Ordner für die Einstellungen mitgeben (und es z.B. auf einem USB-Stick verwenden):

```
java -jar MTViewer.jar ORDNER 
```

https://www.p2tools.de
<br />


## Systemvoraussetzungen

Unterstützt wird Windows und Linux. Das Programm benötigt eine aktuelle Java-VM ab Version: Java 17. Für Linux-Benutzer wird OpenJDK17 empfohlen. (FX-Runtime bringt das Programm bereits mit und muss nicht installiert werden).
<br />


## Download

Das Programm wird in drei Paketen angeboten. Diese unterscheiden sich nur im “Zubehör”, das Programm selbst ist in allen Paketen identisch:

MTViewer-XX.zip

Das Programmpaket bringt nur das Programm und die benötigten Hilfsprogramme aber kein Java mit. Auf dem Rechner muss eine Java-Laufzeitumgebung ab Java11 installiert sein. Dieses Programmpaket kann auf allen Betriebssystemen verwendet werden. Es bringt Startdateien für Linux und Windows mit.


MTViewer-XX__Linux+Java.zip  
MTPlayer-XX__Windows+Java.zip

Diese Programmpakete bringen die Java-Laufzeitumgebung mit und sind nur für das angegebene     Betriebssystem: Linux oder Windows. Es muss kein Java auf dem System installiert sein. (Die Java-Laufzeitumgebung liegt im Ordner “Java” und kommt von jdk.java.net).

Windows:  
Der VLC-Player muss installiert sein.  
Linux:  
Der VLC-Player und ffmpeg müssen installiert sein.  

Weitere Infos zum Programm (Start und Benutzung) sind im Download-Paket enthalten oder können hier gefunden werden:
[instructions/install.md](instructions/install.md)  



zum Download:  
[github.com/xaverW/MTViewer/releases](https://github.com/xaverW/MTViewer/releases)  
[https://www.p2tools.de/download/](https://www.p2tools.de/download/)
<br />


## Installation

MTViewer muss nicht installiert werden, das Entpacken der heruntergeladenen ZIP-Datei ist quasi die Installation. Die heruntergeladene ZIP-Datei entpacken und den entpackten Ordner “MTViewer...” ins Benutzerverzeichnis verschieben. Das Programm kann dann mit Doppelklick auf:  
Linux: “MTViewer__Linux.sh” oder  
Windows: “MTViewer__Windows.exe”  
gestartet werden.
<br />


## Website

[www.p2tools.de]( https://www.p2tools.de)


