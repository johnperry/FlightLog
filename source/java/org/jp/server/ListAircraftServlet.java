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
 * The servlet to list the aircraft in the database.
 */
public class ListAircraftServlet extends Servlet {

	static final Logger logger = Logger.getLogger(ListAircraftServlet.class);

	/**
	 * Construct a ListAircraftServlet.
	 * @param root the root directory of the server.
	 * @param context the path identifying the servlet.
	 */
	public ListAircraftServlet(File root, String context) {
		super(root, context);
	}

	/**
	 * The GET handler: return the list aircraft page.
	 * @param req the request object
	 * @param res the response object
	 */
	public void doGet(HttpRequest req, HttpResponse res) {
		if (req.isFromAuthenticatedUser()) {
			try {
				Database db = Database.getInstance();
				LinkedList<Aircraft> list = db.getAircraftList();
				Document doc = XmlUtil.getDocument();
				Element root = doc.createElement("Aircraft");
				doc.appendChild(root);
				for (Aircraft ac : list) root.appendChild(ac.getElement(root));
				Document xsl = XmlUtil.getDocument( Cache.getInstance().getFile("ListAircraftServlet.xsl" ) );
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
