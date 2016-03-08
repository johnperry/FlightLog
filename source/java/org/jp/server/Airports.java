package org.jp.server;

import java.io.File;
import java.util.Hashtable;
import org.rsna.util.XmlUtil;
import org.w3c.dom.*;

/**
 * Singleton database of airports.
 */
public class Airports {
	
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
		airports = new Hashtable<String,Airport>();
		Document doc = XmlUtil.getDocument(new File("Airports.xml"));
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
