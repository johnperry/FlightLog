package org.jp.server;

import java.io.File;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.LinkedList;
import org.apache.log4j.Logger;
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
 * The servlet to list the totals required for insurance.
 */
public class SummaryServlet extends Servlet {

	static final Logger logger = Logger.getLogger(SummaryServlet.class);

	/**
	 * Construct a SummaryServlet.
	 * @param root the root directory of the server.
	 * @param context the path identifying the servlet.
	 */
	public SummaryServlet(File root, String context) {
		super(root, context);
	}

	/**
	 * The GET handler: return the summary page.
	 * @param req the request object
	 * @param res the response object
	 */
	public void doGet(HttpRequest req, HttpResponse res) {
		if (req.isFromAuthenticatedUser()) {
			try {
				Document doc = XmlUtil.getDocument();
				Element root = doc.createElement("Summary");
				root.setAttribute("date", StringUtil.getDate("."));
				doc.appendChild(root);
				
				//Get the total time
				SearchCriteria sc = new SearchCriteria();
				root.appendChild(search(root, "All Aircraft", sc));
				
				//Get the total in EA300L*
				sc = new SearchCriteria();
				sc.model = "ea300*"; //Note: must be lower case
				root.appendChild(search(root, "Extra 300L", sc));
				
				//Get the total in EA200
				sc = new SearchCriteria();
				sc.model = "ea200"; //Note: must be lower case
				root.appendChild(search(root, "Extra 200", sc));
				
				//Get the total in all Extras
				sc = new SearchCriteria();
				sc.model = "ea*"; //Note: must be lower case
				root.appendChild(search(root, "All Extras", sc));
				
				//Get the total in J3 Cubs
				sc = new SearchCriteria();
				sc.model = "j3"; //Note: must be lower case
				root.appendChild(search(root, "J3 Cub", sc));
				
				//Get the total in all Cubs
				sc = new SearchCriteria();
				sc.model = "j*"; //Note: must be lower case
				root.appendChild(search(root, "All Cubs", sc));
				
				//Get the total tailwheel time
				sc = new SearchCriteria();
				sc.tailwheel = true;
				root.appendChild(search(root, "Tailwheel", sc));
				
				//Get the total in Mooney 201
				sc = new SearchCriteria();
				sc.model = "m20j"; //Note: must be lower case
				root.appendChild(search(root, "Mooney 201", sc));
				
				//Get the total retractable time
				sc = new SearchCriteria();
				sc.retractable = true;
				root.appendChild(search(root, "Retractable", sc));
				
				//Get the total in 172s
				sc = new SearchCriteria();
				sc.model = "c172*"; //Note: must be lower case
				root.appendChild(search(root, "C172", sc));

				//Get the total landplane time
				sc = new SearchCriteria();
				sc.asel = true;
				root.appendChild(search(root, "ASEL", sc));
				
				//Get the total seaplane time
				sc = new SearchCriteria();
				sc.ases = true;
				root.appendChild(search(root, "ASES", sc));
				
				//Get the total glider time
				sc = new SearchCriteria();
				sc.glider = true;
				root.appendChild(search(root, "Glider", sc));
				
				//Get the total helicopter time
				sc = new SearchCriteria();
				sc.helicopter = true;
				root.appendChild(search(root, "Helicopter", sc));
				
 				//Get the recent time
				sc = new SearchCriteria();
				sc.earliestDate = sixMonthsAgo();
				root.appendChild(search(root, "Last 6 Months", sc));
				
				sc = new SearchCriteria();
				sc.earliestDate = oneYearAgo();
				root.appendChild(search(root, "Last 12 Months", sc));
				
				Document xsl;
				if (req.hasParameter("insurance")) {
					xsl = XmlUtil.getDocument( Cache.getInstance().getFile("InsuranceSummary.xsl" ) );
				}
				else {
					xsl = XmlUtil.getDocument( Cache.getInstance().getFile("SummaryServlet.xsl" ) );
				}
					
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
	
	private String sixMonthsAgo() {
		GregorianCalendar gc = new GregorianCalendar();
		gc.roll(gc.MONTH, -6);
		if (gc.get(gc.MONTH) >= 6) gc.roll(gc.YEAR, -1);
		return String.format("%4d.%02d.%02d", gc.get(gc.YEAR), (gc.get(gc.MONTH)+1), gc.get(gc.DAY_OF_MONTH));
	}

	private String oneYearAgo() {
		GregorianCalendar gc = new GregorianCalendar();
		gc.roll(gc.YEAR, -1);
		return String.format("%4d.%02d.%02d", gc.get(gc.YEAR), (gc.get(gc.MONTH)+1), gc.get(gc.DAY_OF_MONTH));
	}

	private Element search(Element parent, String title, SearchCriteria criteria) throws Exception {
		Database db = Database.getInstance();
		LinkedList<Flight> list = db.getFlightList(criteria);
		Totals totals = new Totals();
		for (Flight flight : list) {
			totals.add(flight);
		}
		Element el = totals.getElement(parent);
		el.setAttribute("title", title);
		return el;
	}

}
