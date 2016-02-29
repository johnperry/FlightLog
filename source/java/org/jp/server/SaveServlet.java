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
				File backupfile = new File("FlightLog.xml");
				FileUtil.setText(backupfile, xml);
				
				//Copy it to Google Drive, if available
				String userhome = Configuration.getInstance().getProperty("userhome", "C:\\Users\\John");
				File userdir = new File(userhome);
				File googledrive = new File(userhome, "Google Drive");
				File googlefile = null;
				if (googledrive.exists()) {
					googlefile = new File(googledrive, backupfile.getName());
					FileUtil.copy(backupfile, googlefile);
				}
				
				//Send a response
				res.disableCaching();
				res.setContentType("txt");
				res.write("Backup file created ["+backupfile.getAbsolutePath()+"]");
				if (googlefile != null) res.write("\nand copied to Google Drive.");
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
