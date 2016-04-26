package org.jp.server;

import java.io.File;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.LinkedList;
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
				if ((sc != null) && req.hasParameter("repeat")) {
					res.write(search(sc));
				}
				else if (req.hasParameter("date")) {
					sc = new SearchCriteria();
					String date = req.getParameter("date");
					sc.earliestDate = getDate(date, -15);
					sc.latestDate = getDate(date, 15);
					res.write(search(sc));
				}
				else if (req.hasParameter("recent")) {
					sc = new SearchCriteria();
					sc.earliestDate = getDate(null, -30);
					sc.latestDate = getDate(null, 1);
					logger.info("recent: "+sc.earliestDate+" - "+sc.latestDate);
					res.write(search(sc));					
				}
				else {
					if (sc != null) root.appendChild(sc.getElement(root));
					Document xsl = XmlUtil.getDocument( Cache.getInstance().getFile("SearchServlet.xsl" ) );
					res.write( XmlUtil.getTransformedText(doc, xsl, null) );
				}
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
				res.write(search(criteria));
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
	
	private String search(SearchCriteria criteria) throws Exception {
		Database db = Database.getInstance();
		LinkedList<Flight> list = db.getFlightList(criteria);
		Totals totals = new Totals();
		Document doc = XmlUtil.getDocument();
		Element root = doc.createElement("Flights");
		root.setAttribute("title", "Search Results");
		doc.appendChild(root);
		if (list != null) {
			for (Flight flight : list) {
				root.appendChild(flight.getElement(root));
				totals.add(flight);
			}
		}
		root.appendChild(totals.getElement(root));
		Document xsl = XmlUtil.getDocument( Cache.getInstance().getFile("ListFlightsServlet.xsl" ) );
		return XmlUtil.getTransformedText(doc, xsl, null);
	}
	
	private String getDate(String date, int inc) {
		GregorianCalendar gc;
		if (date != null) {
			int year = StringUtil.getInt(date.substring(0,4));
			int month = StringUtil.getInt(date.substring(5,7)) - 1;
			int day = StringUtil.getInt(date.substring(8,10));
			gc = new GregorianCalendar(year, month, day);
		}
		else gc = new GregorianCalendar();
		long time = gc.getTimeInMillis();
		time += 24 * 60 * 60 * 1000 * (long)inc;
		gc.setTimeInMillis(time);
		return String.format("%4d.%02d.%02d", gc.get(gc.YEAR), (gc.get(gc.MONTH)+1), gc.get(gc.DAY_OF_MONTH));
	}
}
