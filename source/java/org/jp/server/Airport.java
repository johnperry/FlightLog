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
	public String rwy;
	public String var;
	
	public Airport(Element ap) {
		id = ap.getAttribute("id");
		name = ap.getAttribute("name");
		city = ap.getAttribute("city");
		state = ap.getAttribute("state");
		lat = Double.parseDouble(ap.getAttribute("lat"));
		lon = Double.parseDouble(ap.getAttribute("lon"));
		elev = ap.getAttribute("elev");
		rwy = ap.getAttribute("rwy");
		var = ap.getAttribute("var");
	}
	
	public Airport(String id,
				   String name,
				   String city,
				   String state,
				   String lat,
				   String lon,
				   String elev,
				   String rwy,
				   String var) {
		this.id = id;
		this.name = name;
		this.city = city;
		this.state = state;
		this.lat = Double.parseDouble(lat);
		this.lon = Double.parseDouble(lon);
		this.elev = elev;
		this.rwy = rwy;
		this.var = var;
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
		ap.setAttribute("elev", elev.trim());
		ap.setAttribute("rwy", rwy.trim());
		ap.setAttribute("var", var.trim());
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
		sb.append("    Elev: " + elev + "\n");
		sb.append("    Rwy:  " + rwy + "\n");
		sb.append("    Var:  " + var + "\n");
		return sb.toString();
	}
	
}
