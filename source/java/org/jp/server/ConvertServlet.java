package org.jp.server;

import java.io.File;
import org.apache.log4j.Logger;
import org.rsna.server.HttpRequest;
import org.rsna.server.HttpResponse;
import org.rsna.servlets.Servlet;

/**
 * The servlet to make conversions in both the Aircraft table
 * and the Flights table.
 */
public class ConvertServlet extends Servlet {

	static final Logger logger = Logger.getLogger(SaveServlet.class);
	
	/**
	 * Construct a ConvertServlet.
	 * @param root the root directory of the server.
	 * @param context the path identifying the servlet.
	 */
	public ConvertServlet(File root, String context) {
		super(root, context);
	}

	/**
	 * The GET handler
	 * @param req the request object
	 * @param res the response object
	 */
	public void doGet(HttpRequest req, HttpResponse res) {
		if (req.userHasRole("admin")) {
			Database db = Database.getInstance();
			if (db.convert()) {
				res.write("The database was modified successfully.");
			}
			else {
				res.write("The database could not be modified.");
			}
			res.disableCaching();
			res.setContentType("txt");
		}
		else {
			res.setResponseCode(res.forbidden);
		}
		res.send();
	}
}
