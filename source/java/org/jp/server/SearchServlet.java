package org.jp.server;

import java.io.File;
import java.util.Hashtable;
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
 * The servlet to search the database.
 */
public class SearchServlet extends Servlet {

	static final Logger logger = Logger.getLogger(SearchServlet.class);
	static Hashtable<String,SearchCriteria> lastCriteria = new Hashtable<String,SearchCriteria>();

	/**
	 * Construct a SearchServlet.
	 * @param root the root directory of the server.
	 * @param context the path identifying the servlet.
	 */
	public SearchServlet(File root, String context) {
		super(root, context);
	}

	/**
	 * The GET handler: return the search page.
	 * @param req the request object
	 * @param res the response object
	 */
	public void doGet(HttpRequest req, HttpResponse res) {
		if (req.isFromAuthenticatedUser()) {
			try {
				Document doc = XmlUtil.getDocument();
				Element root = doc.createElement("Request");
				doc.appendChild(root);
				String username = req.getUser().getUsername();
				SearchCriteria sc = lastCriteria.get(username);
				if (sc != null) root.appendChild(sc.getElement(root));
				Document xsl = XmlUtil.getDocument( Cache.getInstance().getFile("SearchServlet.xsl" ) );
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
	 * The POST handler: search the database and return the matching flights.
	 * @param req the request object
	 * @param res the response object
	 */
	public void doPost(HttpRequest req, HttpResponse res) {
		if (req.isFromAuthenticatedUser() && req.isReferredFrom(context)) {
			try {
				SearchCriteria criteria = new SearchCriteria(req);
				String username = req.getUser().getUsername();
				lastCriteria.put(username, criteria);
				Database db = Database.getInstance();
				LinkedList<Flight> list = db.getFlightList(criteria);
				Totals totals = new Totals();
				Document doc = XmlUtil.getDocument();
				Element root = doc.createElement("Flights");
				root.setAttribute("title", "Search Results");
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
			return;
		}
		res.send();
	}
}
