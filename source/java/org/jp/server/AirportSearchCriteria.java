package org.jp.server;

import org.rsna.server.HttpRequest;
import org.w3c.dom.*;

/**
 * A class encapsulating airport search criteria.
 */
public class AirportSearchCriteria {
	
	public String id = "";
	public String name = "";
	public String city = "";
	public String state = "";
	
	public AirportSearchCriteria() { }
	
	public AirportSearchCriteria(HttpRequest req) {
		this(
				req.getParameter("apid"),
				req.getParameter("name"),
				req.getParameter("city"),
				req.getParameter("state")
			);
	}
	
	public AirportSearchCriteria(
				String id,
				String name,
				String city,
				String state) {
		this.id = denullLC(id);
		this.name = denullLC(name);
		this.city = denullLC(city);
		this.state = denullLC(state);
	}
	
	private String denullLC(String s) {
		return (s != null) ? s.trim().toLowerCase() : "";
	}
	
	public boolean matches(Airport ap) {
		return 
				(id.equals("") || ap.id.toLowerCase().startsWith(id))
			&&	(name.equals("") || ap.name.toLowerCase().contains(name))
			&&	(city.equals("") || ap.city.toLowerCase().contains(city))
			&&	(state.equals("") || ap.state.toLowerCase().startsWith(state));
	}
		
	public Element getElement(Element parent) {
		Document doc = parent.getOwnerDocument();
		Element sc = doc.createElement("ASC");
		sc.setAttribute("id", id);
		sc.setAttribute("name", name);
		sc.setAttribute("city", city);
		sc.setAttribute("state", state);
		return sc;
	}
	
	public void print() {
		System.out.println("AirpoortSearchCriteria:");
		System.out.println("  id:    \""+id+"\"");
		System.out.println("  name:  \""+name+"\"");
		System.out.println("  city:  \""+city+"\"");
		System.out.println("  state: \""+state+"\"");
	}
		
}
