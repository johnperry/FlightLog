package org.jp.server;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.Hashtable;
import org.apache.log4j.Logger;
import org.rsna.util.*;
import org.w3c.dom.*;

/**
 * Singleton database of airports.
 */
public class Airports {
	
	static final Logger logger = Logger.getLogger(Airports.class);
	static final File airportsXMLFile = new File("Airports.xml");
	
	private static Airports instance = null;
	private Hashtable<String,Airport> airports = null;

	public static Airports getInstance() {
		if (instance == null) {
			try { instance = new Airports(); }
			catch (Exception ex) { }
		}
		return instance;
	}
	
	protected Airports() throws Exception {
		if (airportsXMLFile.exists()) {
			airports = new Hashtable<String,Airport>();
			Document doc = XmlUtil.getDocument(airportsXMLFile);
			Element root = doc.getDocumentElement();
			Node child = root.getFirstChild();
			while (child != null) {
				if (child.getNodeName().equals("Airport")) {
					Airport ap = new Airport((Element)child);
					airports.put(ap.id, ap);
				}
				child = child.getNextSibling();
			}
		}
		else {
			load();
			throw new Exception("Loading...");
		}
	}
	
	public void load() {
		new Importer().start();
	}
	
	public Airport getAirport(String id) {
		Airport ap = airports.get(id);
		if (ap == null) ap = airports.get("K"+id);
		return ap;
	}
	
	public double getDistance(String fromID, String toID) {
		Airport fromAP = airports.get(fromID);
		Airport toAP = airports.get(toID);
		if ((fromAP != null) && (toAP != null)) {
			V3 fromV3 = new V3(fromAP.lat, fromAP.lon);
			V3 toV3 = new V3(toAP.lat, toAP.lon);
			double dotProduct = fromV3.dot(toV3);
			double angle = Math.acos(dotProduct);
			return angle * 3440.0; //scale to nm
		}
		return 0.0;
	}
	
	public double getXCDistance(String route) {
		String[] wps = route.split(" ");
		if (wps.length > 1) {
			double total = 0.0;
			boolean isXC = false;
			for (int i=1; i < wps.length; i++) {
				total += getDistance(wps[i-1], wps[i]);
				if (getDistance(wps[0], wps[i]) >= 50.0) isXC = true;
			}
			if (isXC) return total;
		}
		return 0.0;
	}
	
	class V3 {
		double x;
		double y;
		double z;
		public V3(double lat, double lon) {
			lat = lat * Math.PI / 180.0;
			lon = lon * Math.PI / 180.0;
			z = Math.sin(lat);
			x = Math.cos(lat) * Math.cos(lon);
			y = Math.cos(lat) * Math.sin(lon);
		}
		public double dot(V3 v) {
			return x * v.x + y * v.y + z * v.z;
		}
	}
		
	class Importer extends Thread {
		String[] states = { "AK", "AL", "AR", "AS", "AZ", "CA", "CO", "CT", "DC", "DE", "FL",
							"FM", "GA", "GU", "HI", "IA", "ID", "IL", "IN", "KS", "KY", "LA",
							"MA", "MD", "ME", "MH", "MI", "MN", "MO", "MP", "MS", "MO", "NC",
							"ND", "NE", "NH", "NJ", "NM", "NV", "NY", "OH", "OK", "OR", "PA",
							"PR", "PW", "RI", "SC", "SD", "TN", "TX", "UM", "UT", "VA", "VI",
							"VT", "WA", "WI", "WV", "WY"
						  };
						  
		public Importer() {
			super("Importer");
		}
		
		public void run() {
			try {
				logger.info("Airports download starting");
				Document doc = XmlUtil.getDocument();
				Element root = doc.createElement("Airports");
				doc.appendChild(root);
				for (String state : states) {
					int stateCount = 0;
					String table = getTable(state);
					table = table.replace("&", "&amp;");
					if (table != null) {
						Document tableDoc = XmlUtil.getDocument(table);
						Element tableRoot = tableDoc.getDocumentElement();
						NodeList trs = tableRoot.getElementsByTagName("tr");
						for (int i=1; i<trs.getLength(); i++) { //(skip the title row)
							Element tr = (Element)doc.importNode(trs.item(i), true);
							NodeList tds = tr.getElementsByTagName("td");
							if (tds.getLength() >= 8) {
								Element ap = doc.createElement("Airport");
								ap.setAttribute("state", state);
								ap.setAttribute("id", tds.item(2).getTextContent().trim());
								ap.setAttribute("city", tds.item(4).getTextContent().trim());
								ap.setAttribute("name", tds.item(5).getTextContent().trim());
								ap.setAttribute("lat", filter(tds.item(6).getTextContent().trim()));
								ap.setAttribute("lon", filter(tds.item(7).getTextContent().trim()));
								root.appendChild(ap);
							}
						}
					}
				}
				String xml = XmlUtil.toPrettyString(doc);
				xml = xml.replace("    ", " ");
				FileUtil.setText(airportsXMLFile, xml);
				instance = new Airports();
				logger.info("Airports download succeeded");
			}
			catch (Exception ex) {
				logger.warn("Airports download failed", ex);
			}
		}
		
		private String getTable(String state) throws Exception {
			String url = "http://www.fallingrain.com/world/US/"+state+"/airports.html";
			HttpURLConnection conn = HttpUtil.getConnection(url);
			conn.setRequestMethod("GET");
			conn.connect();
			String page = FileUtil.getText( conn.getInputStream() );
			int tableStart = page.indexOf("<table");
			if (tableStart >= 0) {
				int tableEnd = page.indexOf("</table>", tableStart);
				if (tableEnd > tableStart) {
					return page.substring(tableStart, tableEnd+8);
				}
			}
			return null;			
		}
		
		private String filter(String s) {
			s = s.trim();
			int k = s.indexOf("(");
			if (k >= 0) s = s.substring(0, k).trim();
			return s;
		}
	}

}
