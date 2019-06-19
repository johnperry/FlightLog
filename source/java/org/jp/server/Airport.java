package org.jp.server;

import java.io.File;
import java.util.Hashtable;
import org.rsna.util.XmlUtil;
import org.w3c.dom.*;

/**
 * A class to encapsulate an airport.
 */
public class Airport implements Comparable<Airport> {
	
	public String id;
	public String name;
	public String city;
	public String state;
	public double lat;
	public double lon;
	public String elev;
	public String var;
	Element airport = null;
	
	public Airport(Element ap) {
		id = ap.getAttribute("id");
		name = ap.getAttribute("name");
		city = ap.getAttribute("city");
		state = ap.getAttribute("state");
		lat = Double.parseDouble(ap.getAttribute("lat"));
		lon = Double.parseDouble(ap.getAttribute("lon"));
		elev = ap.getAttribute("elev");
		var = ap.getAttribute("var");
		airport = ap;
	}
	
	public boolean matches(AirportSearchCriteria sc) {
		return sc.matches(this);
	}
	
	public Element getElement(Element parent) {
		Document doc = parent.getOwnerDocument();
		return (Element)doc.importNode(airport, true);
	}
	
	public int compareTo(Airport ap) {
		int k ;
		if ((k=state.compareTo(ap.state)) != 0) return k;
		return id.compareTo(ap.id);
	}

	public String toString(boolean includeRunways) {
		StringBuffer sb = new StringBuffer();
		sb.append(id + "\n");
		sb.append("    " + name + "\n");
		sb.append("    " + city + ", " + state + "\n");
		sb.append("    " + "("+lat+","+lon+")" + "\n");
		sb.append("    Elev: " + elev + "\n");
		sb.append("    Var:  " + var + "\n");
		if (includeRunways) {
			Node child = airport.getFirstChild();
			while (child != null) {
				if (child instanceof Element) {
					Element e = (Element)child;
					if (e.getNodeName().equals("rwy")) {
						sb.append("    Rwy:  " + e.getAttribute("id") + ": " +
												 e.getAttribute("len") + "x" +
												 e.getAttribute("wid") + " " +
												 e.getAttribute("type") + "\n");
					}
				}
				child = child.getNextSibling();
			}
		}
		return sb.toString();
	}
}
