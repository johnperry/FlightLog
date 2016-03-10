package org.jp.server;

import java.io.*;
import org.apache.log4j.Logger;
import org.rsna.server.HttpRequest;
import org.rsna.server.HttpResponse;
import org.rsna.servlets.Servlet;

/**
 * A servlet to access the Airports database.
 */
public class AirportServlet extends Servlet {

	static final Logger logger = Logger.getLogger(AirportServlet.class);
	
	/**
	 * Construct an AirportServlet.
	 * @param root the root directory of the server.
	 * @param context the path identifying the servlet.
	 */
	public AirportServlet(File root, String context) {
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
								sb.append(ap.toString());
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
					res.write("missing id parameter");
					res.setContentType("txt");
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
	
}
