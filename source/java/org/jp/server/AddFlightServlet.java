package org.jp.server;

import java.io.File;
import java.util.Hashtable;
import org.apache.log4j.Logger;
import org.rsna.server.HttpRequest;
import org.rsna.server.HttpResponse;
import org.rsna.servlets.Servlet;
import org.rsna.util.Cache;
import org.rsna.util.FileUtil;
import org.rsna.util.StringUtil;
import org.rsna.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The servlet to add a flight.
 */
public class AddFlightServlet extends Servlet {

	static final Logger logger = Logger.getLogger(AddFlightServlet.class);
	static Hashtable<String,Flight> lastFlights = new Hashtable<String,Flight>();

	/**
	 * Construct a AddFlightServlet.
	 * @param root the root directory of the server.
	 * @param context the path identifying the servlet.
	 */
	public AddFlightServlet(File root, String context) {
		super(root, context);
	}

	/**
	 * The GET handler: return the add flight page.
	 * @param req the request object
	 * @param res the response object
	 */
	public void doGet(HttpRequest req, HttpResponse res) {
		if (req.isFromAuthenticatedUser()) {
			try {
				Database db = Database.getInstance();
				Document doc = XmlUtil.getDocument();
				Element root = doc.createElement("Request");
				doc.appendChild(root);
				String id = req.getParameter("id", "");
				Flight flight = null;
				if (!id.equals("")) {
					flight = db.getFlight(id);
				}
				if (flight == null) {
					String username = req.getUser().getUsername();
					Flight lastFlight = lastFlights.get(username);
					flight = new Flight();
					if (lastFlight == null) {
						flight.set("date", StringUtil.getDate("."));
						flight.set("acid", "N32CP");
						flight.set("route", "68IS");
					}
					else {
						flight.set("date", lastFlight.date);
						flight.set("acid", lastFlight.acid);
						flight.set("route", lastFlight.route);
						Aircraft lastAircraft = db.getAircraft(lastFlight.acid);
						if ((lastAircraft != null) && lastAircraft.category.equals("Helicopter")) flight.ldg = 0;
					}
				}
				root.appendChild(flight.getElement(root));
				Document xsl = XmlUtil.getDocument( Cache.getInstance().getFile("AddFlightServlet.xsl" ) );
				res.write( XmlUtil.getTransformedText(doc, xsl, null) );
				res.disableCaching();
				res.setContentType("html");
			}
			catch (Exception unable) {
				unable.printStackTrace();
				res.setResponseCode(res.servererror);
			}
		}
		else {
			res.setResponseCode(res.forbidden);
		}
		res.send();
	}

	/**
	 * The POST handler: update the flight log and return the add flight page.
	 * @param req the request object
	 * @param res the response object
	 */
	public void doPost(HttpRequest req, HttpResponse res) {
		if (req.isFromAuthenticatedUser() && req.isReferredFrom(context)) {
			Flight flight = new Flight(req);
			Database db = Database.getInstance();
			db.addFlight(flight);
			String username = req.getUser().getUsername();
			lastFlights.put(username, flight);
		}
		doGet(req, res);
	}
}
