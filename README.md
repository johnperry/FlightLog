# FlightLog
A Pilot's Flight Logbook Server and Printer

This repository builds FlightLog-installer.jar. The program is a self-extracting installer for the FlightLogServer and FlightLogPrinter programs. It creates a directory called FlightLog and puts the necessary files in it.

The FlightLog-installer.jar program can be downloaded without having to build it. It is in the products directory in the repository.

The program can be configured by putting a file called config.properties in the FlightLog directory. The configuration parameters are:

port - the port on which the server is to be opened

name - the pilot's name to be printed on each page of the log book

cloud - the path to the cloud drive (used when copying a backup to Google drive)

Defaults are provided which configure the program for me, so while I don't need a config file, you do.

The FlightLogServer program opens a server on the specified port. The server requires a login. A user (admin) with password (password) is provided. After logging in, you can change the password or make a different user through the User Manager link in the left pane.

If the Save link in the left pane is clicked, the system saves the contents of the database as an XML file (FlightLog.xml) in the FlightLog directory. It also creates a zip file (FlightLog.zip) containing the XML backup plus the contents of the ROOT/images directory. If the cloud parameter exists, the zip file is copied to that folder as well, providing a cloud backup. 

If you ever need to re-install everything from scratch:
<ol>
<li>install the program
<li>copy the FlightLog.zip file to the FlightLog directory and unpack it
<li>delete the FlightLog.db and FlightLog.lg files if they are present
<li>start FlightLogServer.jar
<li>launch your browser and go to the FlightLog home page
<li>click the Initialize link in the left pane.
</ol>
That will import the entire backup. Initialization is not allowed unless the database is missing or empty.

The FlightLogServer program can be run as a Windows service. Launch a command window with admin privileges, navigate to the FlightLog/windows directory and enter the command: install.bat. The uninstall.bat file uninstalls the service.

The FlightLogPrinter program prints the flights in the FlightLog.xml file, so before printing, make sure you save the database.

Note: The installer includes the version of the FAA airports database that was current at the time the installer was built. If you want to update to a newer version, go to:

https://www.faa.gov/air_traffic/flight_info/aeronav/aero_data/NASR_Subscription/

and download the zip file you want. The files are called <tt>28DaySubscription_Effective_[date].zip</tt>. Put the file in the FlightLog directory and then run the FAAImporter.jar program. The program creates an <tt>FAAFiles</tt> working directory that can be deleted after the program is finished. It creates the Airports.xml file that is used by the FlightLogServer program to find airports. It also creates the Airports.txt file, which can be deleted (that file is used by a completely separate Android app, and it was convenient to make all the files in one program).

Note: If you run FlightLogServer as a service and specify a cloud drive, then make sure that you run the service under your account and that your account is enabled to run as a service. If you don't, then saves to the cloud will fail, although local saves will succeed. To configure the service user, add the ServiceUser and ServicePassword parameters to the FlightLog/windows/install.bat file like this:

FlightLog.exe ^<br>
 //IS//FlightLog ^<br>
 --Install="${home}"\windows\FlightLog.exe ^<br>
 --Description="Flight Log" ^<br>
 --Startup=auto ^<br>
 --Jvm=auto ^<br>
 --ServiceUser=.\MyUsername ^<br>
 --ServicePassword=MyPassword ^<br>
 ...
