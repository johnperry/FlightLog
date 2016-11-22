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
 * The servlet to list the images referenced by flights in the database.
 */
public class ListImagesServlet extends Servlet {

	static final Logger logger = Logger.getLogger(ListImagesServlet.class);

	/**
	 * Construct a ListImagesServlet.
	 * @param root the root directory of the server.
	 * @param context the path identifying the servlet.
	 */
	public ListImagesServlet(File root, String context) {
		super(root, context);
	}

	/**
	 * The GET handler: return the list images page.
	 * @param req the request object
	 * @param res the response object
	 */
	public void doGet(HttpRequest req, HttpResponse res) {
		if (req.isFromAuthenticatedUser()) {
			try {
				Database db = Database.getInstance();
				LinkedList<Flight> list = db.getFlightList();
				Document doc = XmlUtil.getDocument();
				Element root = doc.createElement("Images");
				doc.appendChild(root);
				for (Flight flight : list) {
					if (!flight.images.equals("")) {
						Element flightEl = doc.createElement("Flight");
						flightEl.setAttribute("date", flight.date);
						flightEl.setAttribute("id", flight.id);
						String[] images = flight.images.split("/");
						for (String image : images) {
							Element imageEl = doc.createElement("Image");
							imageEl.setAttribute("name", image);
							flightEl.appendChild(imageEl);
						}
						root.appendChild(flightEl);
					}
				}
				Document xsl = XmlUtil.getDocument( Cache.getInstance().getFile("ListImagesServlet.xsl") );
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
