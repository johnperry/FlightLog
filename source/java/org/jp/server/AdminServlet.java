package org.jp.server;

import java.io.File;
import org.apache.log4j.Logger;
import org.rsna.server.HttpRequest;
import org.rsna.server.HttpResponse;
import org.rsna.server.Path;
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
public class AdminServlet extends Servlet {

	static final Logger logger = Logger.getLogger(AdminServlet.class);

	/**
	 * Construct a AdminServlet.
	 * @param root the root directory of the server.
	 * @param context the path identifying the servlet.
	 */
	public AdminServlet(File root, String context) {
		super(root, context);
	}

	/**
	 * The GET handler: return the admin page.
	 * @param req the request object
	 * @param res the response object
	 */
	public void doGet(HttpRequest req, HttpResponse res) {
		if (req.userHasRole("admin")) {
			Path path = req.getParsedPath();
			if (path.length() == 1) {
				try {
					Document doc = XmlUtil.getDocument();
					Element root = doc.createElement("Request");
					doc.appendChild(root);
					Document xsl = XmlUtil.getDocument( Cache.getInstance().getFile("AdminServlet.xsl" ) );
					res.write( XmlUtil.getTransformedText(doc, xsl, null) );
					res.disableCaching();
					res.setContentType("html");
				}
				catch (Exception unable) {
					unable.printStackTrace();
					res.setResponseCode(res.servererror);
				}
			}
			else if (path.element(1).equals("shutdown")) {
				res.write("Goodbye");
				res.send();
				System.exit(0);
			}
		}
		else {
			res.redirect("/flightlog");
		}
		res.send();
	}
}
