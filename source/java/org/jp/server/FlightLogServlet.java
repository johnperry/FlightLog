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
 * The main servlet of the FlightLog website.
 */
public class FlightLogServlet extends Servlet {

	static final Logger logger = Logger.getLogger(FlightLogServlet.class);

	/**
	 * Construct a FlightLogServlet.
	 * @param root the root directory of the server.
	 * @param context the path identifying the servlet.
	 */
	public FlightLogServlet(File root, String context) {
		super(root, context);
	}

	/**
	 * The GET handler: return the main page.
	 * @param req the request object
	 * @param res the response object
	 */
	public void doGet(HttpRequest req, HttpResponse res) {
		String accountName = "Guest";
		String accountNumber = "";
		boolean admin = req.userHasRole("admin");
		boolean authenticated = req.isFromAuthenticatedUser();
		String username = (authenticated ? req.getUser().getUsername() : "Guest");
		try {
			Document doc = XmlUtil.getDocument();
			Element root = doc.createElement("Request");
			doc.appendChild(root);
			root.setAttribute("authenticated", (authenticated ? "yes" : "no"));
			root.setAttribute("admin", (admin ? "yes" : "no"));
			root.setAttribute("username", username);
			Document xsl = XmlUtil.getDocument( Cache.getInstance().getFile("FlightLogServlet.xsl" ) );
			String[] params = {
				"mobile", (req.isFromMobileDevice() ? "yes" : "no")
			};
			res.write( XmlUtil.getTransformedText(doc, xsl, params) );
			res.disableCaching();
			res.setContentType("html");
		}
		catch (Exception unable) {
			unable.printStackTrace();
			res.setResponseCode(res.servererror);
		}
		res.send();
	}

}
