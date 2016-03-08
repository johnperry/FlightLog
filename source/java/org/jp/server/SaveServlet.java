package org.jp.server;

import java.io.*;
import java.util.Collections;
import java.util.LinkedList;
import org.apache.log4j.Logger;
import org.jp.config.Configuration;
import org.rsna.server.HttpRequest;
import org.rsna.server.HttpResponse;
import org.rsna.servlets.Servlet;
import org.rsna.util.FileUtil;
import org.rsna.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The servlet to save the database as an XML file.
 */
public class SaveServlet extends Servlet {

	static final Logger logger = Logger.getLogger(SaveServlet.class);
	
	/**
	 * Construct a SaveServlet.
	 * @param root the root directory of the server.
	 * @param context the path identifying the servlet.
	 */
	public SaveServlet(File root, String context) {
		super(root, context);
	}

	/**
	 * The GET handler: save the database as an XML file.
	 * @param req the request object
	 * @param res the response object
	 */
	public void doGet(HttpRequest req, HttpResponse res) {
		if (req.isFromAuthenticatedUser()) {
			try {
				//Create the XML structure
				Database db = Database.getInstance();
				db.commit();
				Document doc = XmlUtil.getDocument();
				Element root = doc.createElement("FlightLog");
				doc.appendChild(root);
				Element aircraft = doc.createElement("AircraftList");
				root.appendChild(aircraft);
				for (Aircraft ac : db.getAircraftList()) aircraft.appendChild(ac.getElement(aircraft));
				Element flights = doc.createElement("FlightList");
				root.appendChild(flights);
				
				//Get the flights and sort them in date order
				LinkedList<Flight> list = db.getFlightList();
				Collections.sort(list);
				for (Flight ft : list) {
					flights.appendChild(ft.getElement(flights));
				}
				
				//Save it in the program root directory
				String xml = XmlUtil.toPrettyString(doc);
				xml = xml.replace("    ", " ");
				File backupFile = new File("FlightLog.xml");
				FileUtil.setText(backupFile, xml);
				
				//Copy it to Google Drive, if available
				String cloud = Configuration.getInstance().getProperty("cloud", "C:\\Users\\John\\Google Drive");
				File cloudDir = new File(cloud);
				File cloudFile = null;
				if (cloudDir.exists()) {
					cloudFile = new File(cloudDir, backupFile.getName());
					FileUtil.copy(backupFile, cloudFile);
				}
				
				//Send a response
				res.disableCaching();
				res.setContentType("txt");
				res.write("Backup file created ["+backupFile.getAbsolutePath()+"]");
				if (cloudFile != null) res.write("\nand copied to the cloud drive ["+cloudDir.getAbsolutePath()+"]");
			}
			catch (Exception ex) {
				StringWriter sw = new StringWriter();
				ex.printStackTrace(new PrintWriter(sw));
				res.write(sw.toString());
				res.setResponseCode(res.servererror);
			}
		}
		else {
			res.setResponseCode(res.forbidden);
		}
		res.send();
	}
}
