package org.jp.server;

import java.io.File;
import java.util.LinkedList;
import org.apache.log4j.Logger;
import org.rsna.server.HttpRequest;
import org.rsna.server.HttpResponse;
import org.rsna.servlets.Servlet;
import org.rsna.util.Cache;
import org.rsna.util.FileUtil;
import org.rsna.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The servlet to list the flights in the database.
 */
public class ListFlightsServlet extends Servlet {

	static final Logger logger = Logger.getLogger(ListFlightsServlet.class);

	/**
	 * Construct a ListFlightsServlet.
	 * @param root the root directory of the server.
	 * @param context the path identifying the servlet.
	 */
	public ListFlightsServlet(File root, String context) {
		super(root, context);
	}

	/**
	 * The GET handler: return the list flights page.
	 * @param req the request object
	 * @param res the response object
	 */
	public void doGet(HttpRequest req, HttpResponse res) {
		if (req.isFromAuthenticatedUser()) {
			try {
				Database db = Database.getInstance();
				LinkedList<Flight> list = db.getFlightList();
				Totals totals = new Totals();
				Document doc = XmlUtil.getDocument();
				Element root = doc.createElement("Flights");
				root.setAttribute("title", "Flights");
				doc.appendChild(root);
				for (Flight flight : list) {
					root.appendChild(flight.getElement(root));
					totals.add(flight);
				}
				root.appendChild(totals.getElement(root));
				Document xsl = XmlUtil.getDocument( Cache.getInstance().getFile("ListFlightsServlet.xsl" ) );
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
}
