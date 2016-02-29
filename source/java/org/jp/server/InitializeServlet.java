package org.jp.server;

import java.io.*;
import java.util.Collections;
import java.util.LinkedList;
import org.apache.log4j.Logger;
import org.rsna.server.HttpRequest;
import org.rsna.server.HttpResponse;
import org.rsna.servlets.Servlet;
import org.rsna.util.FileUtil;
import org.rsna.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * The servlet to initialize the database from an XML file.
 */
public class InitializeServlet extends Servlet {

	static final Logger logger = Logger.getLogger(InitializeServlet.class);
	
	/**
	 * Construct a InitializeServlet.
	 * @param root the root directory of the server.
	 * @param context the path identifying the servlet.
	 */
	public InitializeServlet(File root, String context) {
		super(root, context);
	}

	/**
	 * The GET handler: initialize the database from an XML file.
	 * @param req the request object
	 * @param res the response object
	 */
	public void doGet(HttpRequest req, HttpResponse res) {
		if (req.userHasRole("admin")) {
			try {
				//Create the XML structure
				Database db = Database.getInstance();
				int nAircraft = db.getNumberOfAircraft();
				int nFlights = db.getNumberOfFlights();
				if ((nAircraft == 0) && (nFlights == 0)) {
					Document doc = XmlUtil.getDocument(new File("FlightLog.xml"));
					Element root = doc.getDocumentElement();
					Element acs = XmlUtil.getFirstNamedChild(root, "AircraftList");
					int acCount = 0;
					if (acs != null) {
						Node child = acs.getFirstChild();
						while (child != null) {
							if ((child instanceof Element) && child.getNodeName().equals("Aircraft")) {
								db.addAircraft(new Aircraft( (Element)child ));
								acCount++;
							}
							child = child.getNextSibling();
						}
					}
					Element fts = XmlUtil.getFirstNamedChild(root, "FlightList");
					int ftCount = 0;
					if (fts != null) {
						Node child = fts.getFirstChild();
						while (child != null) {
							if ((child instanceof Element) && child.getNodeName().equals("Flight")) {
								db.addFlight(new Flight( (Element)child ));
								ftCount++;
							}
							child = child.getNextSibling();
						}
					}
					res.write(acCount + " aircraft were imported.\n");
					res.write(ftCount + " flights were imported.\n");
				}
				else {
					res.write("The database contained "+nAircraft+" and "+nFlights+".\n");
					res.write("Flights cannot be imported into the database.\n");
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
		res.disableCaching();
		res.setContentType("txt");
		res.send();
	}
}
