package org.jp.server;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.Hashtable;
import java.util.LinkedList;
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
			logger.info("Airports database loaded: "+String.format("%,d",airports.size())+" airports found.");
		}
		else {
			logger.info("Unable to load the airports database.");
			throw new Exception("Unable to load the airports database.");
		}
	}
	
	public Airport getAirport(String id) {
		Airport ap = airports.get(id);
		if (ap == null) ap = airports.get("K"+id);
		return ap;
	}
	
	public LinkedList<Airport> search(AirportSearchCriteria sc) {
		LinkedList<Airport> aps = new LinkedList<Airport>();
		for (Airport ap : airports.values()) {
			if (ap.matches(sc)) {
				aps.add(ap);
			}
		}
		return aps;
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
}
