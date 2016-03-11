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
	
	public Airport(Element ap) {
		id = ap.getAttribute("id");
		name = ap.getAttribute("name");
		city = ap.getAttribute("city");
		state = ap.getAttribute("state");
		lat = Double.parseDouble(ap.getAttribute("lat"));
		lon = Double.parseDouble(ap.getAttribute("lon"));
	}
	
	public Airport(String id,
				   String name,
				   String city,
				   String state,
				   String lat,
				   String lon) {
		this.id = id;
		this.name = name;
		this.city = city;
		this.state = state;
		this.lat = Double.parseDouble(lat);
		this.lon = Double.parseDouble(lon);
	}
	
	public boolean matches(AirportSearchCriteria sc) {
		return sc.matches(this);
	}
	
	public Element getElement(Element parent) {
		Document doc = parent.getOwnerDocument();
		Element ap = doc.createElement("Airport");
		ap.setAttribute("id", id.trim());
		ap.setAttribute("city", city.trim());
		ap.setAttribute("name", name.trim());
		ap.setAttribute("state", state.trim());
		ap.setAttribute("lat", String.format("%.3f",lat));
		ap.setAttribute("lon", String.format("%.3f",lon));
		return ap;
	}
	
	public int compareTo(Airport ap) {
		int k ;
		if ((k=state.compareTo(ap.state)) != 0) return k;
		return id.compareTo(ap.id);
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
