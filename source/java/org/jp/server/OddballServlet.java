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
 * The servlet to search the database for odd things.
 */
public class OddballServlet extends Servlet {

	static final Logger logger = Logger.getLogger(OddballServlet.class);

	/**
	 * Construct an OddballServlet.
	 * @param root the root directory of the server.
	 * @param context the path identifying the servlet.
	 */
	public OddballServlet(File root, String context) {
		super(root, context);
	}

	/**
	 * The GET handler: return the search page.
	 * @param req the request object
	 * @param res the response object
	 */
	public void doGet(HttpRequest req, HttpResponse res) {
		if (req.userHasRole("admin")) {
			try {
				Database db = Database.getInstance();
				LinkedList<Flight> list = db.getFlightList();
				Document doc = XmlUtil.getDocument();
				Element root = doc.createElement("Flights");
				root.setAttribute("title", "Oddballs");
				doc.appendChild(root);
				for (Flight flight : list) {
					double totalTime = flight.total.doubleValue();
					double dayTime = flight.tday.doubleValue();
					double picTime = flight.pic.doubleValue();
					if ((dayTime > totalTime) || (picTime > totalTime)){
						root.appendChild(flight.getElement(root));
					}
				}
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
