package org.jp.server;

import java.io.File;
import java.util.Hashtable;
import org.rsna.util.XmlUtil;
import org.w3c.dom.*;

/**
 * A class to encapsulate an airport.
 */
public class Airport {
	
	public String id;
	public String name;
	public String city;
	public String state;
	public double lat;
	public double lon;
	
	public Airport(Element ap) {
		id = ap.getAttribute("id");
		name = ap.getAttribute("name");
		city = ap.getAttribute("city");
		state = ap.getAttribute("state");
		lat = Double.parseDouble(ap.getAttribute("lat"));
		lon = Double.parseDouble(ap.getAttribute("lon"));
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(id + "\n");
		sb.append("    " + name + "\n");
		sb.append("    " + city + ", " + state + "\n");
		sb.append("    " + "("+lat+","+lon+")" + "\n");
		return sb.toString();
	}
	
}
