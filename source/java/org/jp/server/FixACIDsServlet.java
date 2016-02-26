package org.jp.server;

import java.io.File;
import org.apache.log4j.Logger;
import org.rsna.server.HttpRequest;
import org.rsna.server.HttpResponse;
import org.rsna.servlets.Servlet;

/**
 * The servlet to tell the database to fix the Aircraft IDs
 * in both the Aircraft table and the Flights table.
 */
public class FixACIDsServlet extends Servlet {

	static final Logger logger = Logger.getLogger(SaveServlet.class);
	
	/**
	 * Construct a FixACIDsServlet.
	 * @param root the root directory of the server.
	 * @param context the path identifying the servlet.
	 */
	public FixACIDsServlet(File root, String context) {
		super(root, context);
	}

	/**
	 * The GET handler: tell the database to fix the Aircraft IDs.
	 * @param req the request object
	 * @param res the response object
	 */
	public void doGet(HttpRequest req, HttpResponse res) {
		if (req.userHasRole("admin")) {
			Database db = Database.getInstance();
			if (db.fixACIDs()) {
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
