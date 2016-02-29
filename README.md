# FlightLog
A Pilot's Flight Logbook Server and Printer

This repository builds FlightLog-installer.jar. The program is a self-extracting installer for the FlightLogServer and FlightLogPrinter programs. It creates a directory called FlightLog and puts the necessary files in it.

The program can be configured by putting a file called config.properties in the FlightLog directory. The configuration parameters are:

port - the port on which the server is to be opened

name - the pilot's name to be printed on each page of the log book

userhome - the path to the user's home directory

Defaults are provided which configure the program for me, so while I don't need a config file, you do.

The FlightLogServer program opens a server on the specified port. The server requires a login. A user (admin) with password (password) is provided. After logging in, you can change the password or make a different user through the User Manager link in the left pane.

Clicking the Save link in the left pane causes the system to save the contents of the database as an XML file in the FlightLog directory. If the userhome parameter points to a directory containing a "Google drive" folder, the XML file is copied to that folder as well, providing a cloud backup. If you ever need to re-install everything, install the program, copy the FlightLog.xml file to the FlightLog directory, and click the Initialize link in the left pane. That will import the entire backup. Initialization is not allowed unless the existing database is empty.

The FlightLogServer program can be run as a Windows service. Launch a command window with admin privileges, navigate to the FlightLog/windows directory and enter the command: install.bat. The uninstall.bat file uninstalls the service.

The FlightLogPrinter program prints the flights in the FlightLog.xml file, so before printing, make sure you save the database.

