package org.jp.server;

import java.io.File;
import java.util.Hashtable;
import java.util.LinkedList;
import org.apache.log4j.Logger;
import org.rsna.multipart.UploadedFile;
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
 * The servlet to add an image to a flight.
 */
public class AddImageServlet extends Servlet {

	static final Logger logger = Logger.getLogger(AddImageServlet.class);

	/**
	 * Construct an AddImageServlet.
	 * @param root the root directory of the server.
	 * @param context the path identifying the servlet.
	 */
	public AddImageServlet(File root, String context) {
		super(root, context);
	}

	/**
	 * The GET handler: return the add flight page.
	 * @param req the request object
	 * @param res the response object
	 */
	public void doGet(HttpRequest req, HttpResponse res) {
		if (req.isFromAuthenticatedUser()) {
			try {
				Document doc = XmlUtil.getDocument();
				Element root = doc.createElement("Request");
				doc.appendChild(root);
				String id = req.getParameter("id", "");
				Database db = Database.getInstance();
				Flight flight = db.getFlight(id);
				root.appendChild(flight.getElement(root));
				Document xsl = XmlUtil.getDocument( Cache.getInstance().getFile("AddImageServlet.xsl" ) );
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
	 * The POST handler: update the flight log and return the add flight page.
	 * @param req the request object
	 * @param res the response object
	 */
	public void doPost(HttpRequest req, HttpResponse res) {
		if (req.isFromAuthenticatedUser() && req.isReferredFrom(context)) {
			
			try {
				//Get the posted files
				File tempDir = new File("temp");
				tempDir.mkdirs();
				int maxsize = 10*1024*1024; //10 megabytes
				LinkedList<UploadedFile> uFiles = req.getParts(tempDir, maxsize);

				String id = req.getParameter("id", "");
				Database db = Database.getInstance();
				Flight flight = db.getFlight(id);

				File imagesDir = new File(root, "images");
				imagesDir.mkdirs();
				for (UploadedFile uFile : uFiles) {
					File inFile = uFile.getFile();
					String name = inFile.getName();
					String ext = "";
					int k = name.lastIndexOf(".");
					if (k >= 0) ext = name.substring(k);
					File outFile = File.createTempFile("FLS-", ext, imagesDir);
					FileUtil.copy(inFile, outFile);
					if (flight.images.equals("")) flight.images = outFile.getName();
					else flight.images += "/" + outFile.getName();
				}
				db.addFlight(flight);
				res.redirect("/addflight?id="+id);
			}
			catch (Exception ex) {
				res.write("Unable to save the image.");
				res.setContentType("txt");
				res.send();
			}
		}
	}
}
