package org.jp.server;

import java.io.File;
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
 * The servlet to add an aircraft.
 */
public class AddAircraftServlet extends Servlet {

	static final Logger logger = Logger.getLogger(AddAircraftServlet.class);

	/**
	 * Construct a AddAircraftServlet.
	 * @param root the root directory of the server.
	 * @param context the path identifying the servlet.
	 */
	public AddAircraftServlet(File root, String context) {
		super(root, context);
	}

	/**
	 * The GET handler: return the add aircraft page.
	 * @param req the request object
	 * @param res the response object
	 */
	public void doGet(HttpRequest req, HttpResponse res) {
		if (req.isFromAuthenticatedUser()) {
			try {
				Document doc = XmlUtil.getDocument();
				Element root = doc.createElement("Request");
				doc.appendChild(root);
				String acid = Aircraft.fixACID(req.getParameter("acid"));
				if (!acid.equals("")) {
					Database db = Database.getInstance();
					Aircraft ac = db.getAircraft(acid);
					if (ac != null) {
						root.appendChild(ac.getElement(root));
					}
				}
				Document xsl = XmlUtil.getDocument( Cache.getInstance().getFile("AddAircraftServlet.xsl" ) );
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
	 * The POST handler: update the database and return the add aircraft page.
	 * @param req the request object
	 * @param res the response object
	 */
	public void doPost(HttpRequest req, HttpResponse res) {
		if (req.isFromAuthenticatedUser() && req.isReferredFrom(context)) {
			Aircraft ac = new Aircraft(req);
			String id = ac.acid;
			if ((id != null) && !id.equals("")) {
				Database db = Database.getInstance();
				db.addAircraft(ac);
			}
		}
		doGet(req, res);
	}
}
