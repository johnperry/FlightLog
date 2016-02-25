package org.jp.server;

import java.io.Serializable;
import org.rsna.server.HttpRequest;
import org.w3c.dom.*;

/**
 * A class encapsulating an individual aircraft.
 */
public class Aircraft implements Serializable, Comparable<Aircraft> {

	static final long serialVersionUID = 1L;

	public String acid			= "";
	public String model			= "";
	public String category 		= "";
	public String tailwheel		= "";
	public String retractable	= "";
	public String complex		= "";

	public Aircraft(HttpRequest req) {
		acid = fixACID(req.getParameter("acid", ""));
		model = req.getParameter("model", "");
		category = req.getParameter("category", "");
		tailwheel = yesblank(req.getParameter("tailwheel", "no"));
		retractable = yesblank(req.getParameter("retractable", "no"));
		complex = yesblank(req.getParameter("complex", "no"));
	}

	public Aircraft() { }

	public void set(String name, String value) {
		if (name.equals("acid")) acid = fixACID(value);
		else if (name.equals("model")) model = value;
		else if (name.equals("category")) category = value;
		else if (name.equals("tailwheel")) tailwheel = yesblank(value);
		else if (name.equals("retractable")) retractable = yesblank(value);
		else if (name.equals("complex")) complex = yesblank(value);
	}
	
	public static String fixACID(String id) {
		id = (id != null) ? id.trim() : "";
		if (id.length() > 0) {
			char c = id.charAt(0);
			if ( (c != 'n') && (c != 'N') ) {
				if (Character.isDigit(c)) id = "N" + id;
			}
			id = id.toUpperCase();
		}
		return id;
	}
	
	public String yesblank(String s) {
		s = (s != null) ? s.trim().toLowerCase() : "";
		return s.equals("yes") ? s : "";
	}

	public int compareTo(Aircraft ac) {
		int k ;
		if ((k=category.compareTo(ac.category)) != 0) return k;
		if ((k=model.compareTo(ac.model)) != 0) return k;
		return acid.compareTo(ac.acid);
	}

	public Element getElement(Element parent) {
		Document doc = parent.getOwnerDocument();
		Element ac = doc.createElement("Aircraft");
		ac.setAttribute("acid", acid);
		ac.setAttribute("model", model);
		ac.setAttribute("category", category);
		ac.setAttribute("tailwheel", tailwheel);
		ac.setAttribute("retractable", retractable);
		ac.setAttribute("complex", complex);
		return ac;
	}
}
