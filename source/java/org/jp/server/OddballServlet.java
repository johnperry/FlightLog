package org.jp.server;

import java.io.File;
import java.util.HashSet;
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
				
				//If this is a remove aircraft command, do it now 
				String removeAC = req.getParameter("removeAC");
				if (removeAC != null) db.removeAircraft(removeAC);
				
				//Make the Oddballs page
				LinkedList<Aircraft> acList = db.getAircraftList();
				Hashtable<String,Aircraft> acTable = new Hashtable<String,Aircraft>();
				HashSet<String> unreferencedAC = new HashSet<String>();
				HashSet<String> missingAC = new HashSet<String>();
				for (Aircraft ac : acList) {
					acTable.put(ac.acid, ac);
					unreferencedAC.add(ac.acid);
				}
				LinkedList<Flight> flightList = db.getFlightList();
				Document doc = XmlUtil.getDocument();
				Element root = doc.createElement("Oddballs");
				root.setAttribute("title", "Oddballs");
				doc.appendChild(root);
				for (Flight flight : flightList) {
					unreferencedAC.remove(flight.acid);
					if (!acTable.containsKey(flight.acid)) missingAC.add(flight.acid);
					double totalTime = flight.total.doubleValue();
					double dayTime = flight.tday.doubleValue();
					double picTime = flight.pic.doubleValue();
					double xcTime = flight.txc.doubleValue();
					if ((dayTime > totalTime) || (picTime > totalTime) || 
							((picTime > 1.0d) && flight.txc.isZero() && !flight.to.equals(""))){
						root.appendChild(flight.getElement(root));
					}
				}
				if (missingAC.size() > 0) {
					for (String acid : missingAC) {
						Element ac = doc.createElement("MissingAC");
						ac.setAttribute("acid", acid);
						root.appendChild(ac);
					}
				}
				if (unreferencedAC.size() > 0) {
					Element uac = doc.createElement("UnreferencedAC");
					root.appendChild(uac);
					for (String acid : unreferencedAC) {
						Aircraft ac = acTable.get(acid);
						uac.appendChild(ac.getElement(uac));
					}
				}
				//System.out.println(XmlUtil.toPrettyString(root));
				Document xsl = XmlUtil.getDocument( Cache.getInstance().getFile("OddballServlet.xsl" ) );
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
