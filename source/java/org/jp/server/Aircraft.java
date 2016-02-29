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

	public Aircraft() { }
	
	public Aircraft(HttpRequest req) {
		acid = fixACID(req.getParameter("acid", ""));
		model = req.getParameter("model", "");
		category = req.getParameter("category", "");
		tailwheel = yesblank(req.getParameter("tailwheel", "no"));
		retractable = yesblank(req.getParameter("retractable", "no"));
		complex = yesblank(req.getParameter("complex", "no"));
	}

	public Aircraft(Element el) {
		acid = fixACID(el.getAttribute("acid"));
		model = el.getAttribute("model");
		category = el.getAttribute("category");
		tailwheel = yesblank(el.getAttribute("tailwheel"));
		retractable = yesblank(el.getAttribute("retractable"));
		complex = yesblank(el.getAttribute("complex"));
	}

	public void set(String name, String value) {
		if (name.equals("acid")) acid = fixACID(value);
		else if (name.equals("model")) model = fixModel(value);
		else if (name.equals("category")) category = value;
		else if (name.equals("tailwheel")) tailwheel = yesblank(value);
		else if (name.equals("retractable")) retractable = yesblank(value);
		else if (name.equals("complex")) complex = yesblank(value);
	}
	
	public static String fixACID(String id) {
		id = (id != null) ? id.trim() : "";
		if (id.length() > 0) {
			id = id.toUpperCase();
			char c = id.charAt(0);
			if (Character.isDigit(c)) id = "N" + id;
			int k = id.indexOf("/");
			if (k >= 0) {
				k++;
				String s = id.substring(k);
				if (s.equals("SEA")) s = "s";
				else s = s.toLowerCase();
				id = id.substring(0, k) + s;
			}
		}
		return id;
	}
	
	public static String fixModel(String model) {
		model = (model != null) ? model.trim() : "";
		if (model.length() > 0) {
			int k = model.indexOf("/");
			if (k >= 0) {
				k++;
				String s = model.substring(k);
				if (s.toUpperCase().equals("SEA")) s = "s";
				else s = s.toLowerCase();
				model = model.substring(0, k) + s;
			}
		}
		return model;
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
