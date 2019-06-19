package org.jp.server;

import java.io.*;
import java.util.Hashtable;
import java.util.LinkedList;
import org.apache.log4j.Logger;
import org.rsna.server.HttpRequest;
import org.rsna.server.HttpResponse;
import org.rsna.servlets.Servlet;
import org.rsna.util.Cache;
import org.rsna.util.XmlUtil;
import org.w3c.dom.*;

/**
 * A servlet to access the Airports database.
 */
public class AirportsServlet extends Servlet {

	static final Logger logger = Logger.getLogger(AirportsServlet.class);
	static Hashtable<String,AirportSearchCriteria> lastCriteria = new Hashtable<String,AirportSearchCriteria>();
	
	/**
	 * Construct an AirportsServlet.
	 * @param root the root directory of the server.
	 * @param context the path identifying the servlet.
	 */
	public AirportsServlet(File root, String context) {
		super(root, context);
	}

	/**
	 * The GET handler: respond to requests for airport information.
	 * @param req the request object
	 * @param res the response object
	 */
	public void doGet(HttpRequest req, HttpResponse res) {
		res.disableCaching();
		if (req.isFromAuthenticatedUser()) {
			try {
				String id = req.getParameter("id");
				if (id != null) {
					//This is an AJAX request for flight waypoint information
					StringBuffer sb = new StringBuffer();
					Flight flight = Database.getInstance().getFlight(id);
					if (flight != null) {
						Aircraft ac = Database.getInstance().getAircraft(flight.acid);
						String model = ((ac != null) ? ac.model : "Aircraft not in database");
						sb.append(flight.date + ": " + flight.acid + " (" + model + ")\n\n");
						Airports aps = Airports.getInstance();
						String route = flight.route;
						String[] wps = route.split(" ");
						for (String wp : wps) {
							Airport ap = aps.getAirport(wp);
							if (ap != null) {
								double dist = 0.0;
								sb.append(ap.toString(true));
							}
							else {
								sb.append(wp + " not found in database\n");
							}
							sb.append("\n");
						}
						double xcDist = aps.getXCDistance(route);
						if (xcDist > 49.0) {
							sb.append(String.format("Cross country: %.1f nm.\n", xcDist));
						}
					}
					else {
						sb.append("Flight "+id+" not found in the database\n");
					}
					res.write(sb.toString());
					res.setContentType("txt");
				}
				else {
					//This is a request for the search page
					Document doc = XmlUtil.getDocument();
					Element root = doc.createElement("Request");
					doc.appendChild(root);
					String username = req.getUser().getUsername();
					AirportSearchCriteria sc = lastCriteria.get(username);
					if (sc != null) root.appendChild(sc.getElement(root));
					Document xsl = XmlUtil.getDocument( Cache.getInstance().getFile("AirportsServlet.xsl" ) );
					res.write( XmlUtil.getTransformedText(doc, xsl, null) );
					res.disableCaching();
					res.setContentType("html");
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
	
	/**
	 * The POST handler
	 * @param req the request object
	 * @param res the response object
	 */
	public void doPost(HttpRequest req, HttpResponse res) {
		res.disableCaching();
		if (req.isFromAuthenticatedUser()) {
			try {
				AirportSearchCriteria sc = new AirportSearchCriteria(req);
				String username = req.getUser().getUsername();
				lastCriteria.put(username, sc);
				LinkedList<Airport> aps = Airports.getInstance().search(sc);
				Document doc = XmlUtil.getDocument();
				Element root = doc.createElement("Airports");
				doc.appendChild(root);
				for (Airport ap : aps) {
					root.appendChild(ap.getElement(root));
				}
				Document xsl = XmlUtil.getDocument( Cache.getInstance().getFile("AirportsServlet.xsl" ) );
				res.write( XmlUtil.getTransformedText(doc, xsl, null) );
				res.disableCaching();
				res.setContentType("html");
			}
			catch (Exception ex) {
				res.setResponseCode(res.servererror);
			}
		}
		else {
			res.setResponseCode(res.forbidden);
		}
		res.send();
		
	}

}
