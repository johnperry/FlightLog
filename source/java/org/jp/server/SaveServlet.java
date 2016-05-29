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
import org.rsna.util.StringUtil;
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
				backup(backupFile); //copy the previous backup
				FileUtil.setText(backupFile, xml);
				res.write("Backup file created ["+backupFile.getAbsolutePath()+"]");
				
				//Copy it to the cloud drive, if available
				String cloud = Configuration.getInstance().getProperty("cloud", "C:\\Users\\John\\Google Drive");
				File cloudDir = new File(cloud);
				File cloudFile = null;
				if (cloudDir.exists()) {
					cloudFile = new File(cloudDir, backupFile.getName());
					/*
					if (cloudFile.exists()) {
						if (cloudFile.delete()) res.write("\nOld cloud backup file deleted.");
						else res.write("\nOld cloud backup file could not be deleted.");
					}
					else res.write("\nOld cloud backup file does not exist.");
					*/
					if (FileUtil.copy(backupFile, cloudFile)) res.write("\nNew backup copied ");
					else res.write("\nBackup file could not be copied ");
					res.write("to the cloud drive ["+cloudFile.getAbsolutePath()+"]");
				}
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
	
	private void backup(File targetFile) {
		targetFile = targetFile.getAbsoluteFile();
		File parent = targetFile.getParentFile();
		if (targetFile.exists()) {
			String name = targetFile.getName();
			int k = name.lastIndexOf(".");
			String target = name.substring(0,k) + "[";
			int tlen = target.length();
			String ext = name.substring(k);

			int n = 0;
			File[] files = parent.listFiles();
			if (files != null) {
				for (File file : files) {
					String fname = file.getName();
					if (fname.startsWith(target)) {
						int kk = fname.indexOf("]", tlen);
						if (kk > tlen) {
							int nn = StringUtil.getInt(fname.substring(tlen, kk), 0);
							if (nn > n) n = nn;
						}
					}
				}
			}
			n++;
			File backup = new File(parent, target + n + "]" + ext);
			backup.delete(); //shouldn't be there, but just in case.
			FileUtil.copy(targetFile, backup);
		}
	}

}
