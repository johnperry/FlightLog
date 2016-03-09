package org.jp.server;

import org.rsna.server.HttpRequest;
import org.w3c.dom.*;

/**
 * A class encapsulating flight search criteria.
 */
public class SearchCriteria {
	
	public String earliestDate = "";
	public String latestDate = "";
	public String route = "";
	public String acid = "";
	public String model = "";
	public boolean asel = false;
	public boolean ases = false;
	public boolean amel = false;
	public boolean ames = false;
	public boolean glider = false;
	public boolean helicopter = false;
	public boolean tailwheel = false;
	public boolean retractable = false;
	public boolean complex = false;
	public String notes = "";
	
	boolean acceptAllClasses = true;
	
	public SearchCriteria() { }
	
	public SearchCriteria(HttpRequest req) {
		this(
				req.getParameter("earliestDate"),
				req.getParameter("latestDate"),
				req.getParameter("route"),
				req.getParameter("acid"),
				req.getParameter("model"),
				req.getParameter("asel"),
				req.getParameter("ases"),
				req.getParameter("amel"),
				req.getParameter("ames"),
				req.getParameter("glider"),
				req.getParameter("helicopter"),
				req.getParameter("tailwheel"),
				req.getParameter("retractable"),
				req.getParameter("complex"),
				req.getParameter("notes")
			);
	}
	
	public SearchCriteria(
				String earliestDate,
				String latestDate,
				String route,
				String acid,
				String model,
				String asel,
				String ases,
				String amel,
				String ames,
				String glider,
				String helicopter,
				String tailwheel,
				String retractable,
				String complex,
				String notes) {
		this.earliestDate = denullLC(earliestDate);
		this.latestDate = denullLC(latestDate);
		this.route = denullLC(route);
		this.acid = Aircraft.fixACID(acid);
		this.model = denullLC(model);
		this.asel = denullBoolean(asel);
		this.ases = denullBoolean(ases);
		this.amel = denullBoolean(amel);
		this.ames = denullBoolean(ames);
		this.glider = denullBoolean(glider);
		this.helicopter = denullBoolean(helicopter);
		this.tailwheel = denullBoolean(tailwheel);
		this.retractable = denullBoolean(retractable);
		this.complex = denullBoolean(complex);
		this.notes = Flight.trim(notes).toLowerCase();
	}
	
	private String denullLC(String s) {
		return (s != null) ? s.trim().toLowerCase() : "";
	}
	
	private String denullUC(String s) {
		return (s != null) ? s.trim().toUpperCase() : "";
	}
	
	private boolean denullBoolean(String s) {
		return (s != null);
	}
	
	public boolean matches(Flight flight) {
		acceptAllClasses = !(this.asel || this.ases || this.amel || this.ames || this.glider || this.helicopter);
		boolean result = 
				(earliestDate.equals("") || (flight.date.compareTo(earliestDate) >= 0))
			&&	(latestDate.equals("") || (flight.date.compareTo(latestDate) <= 0))
			&&	(route.equals("") || flight.route.toLowerCase().contains(route)
					|| (route.equals("#") && isRoundTrip(flight))
					|| (route.equals("!") && isXC(flight)))
			&&	(acid.equals("") || flight.acid.equals(acid))
			&&	(notes.equals("") || flight.notes.toLowerCase().contains(notes) 
					|| (notes.equals("*") && (flight.notes.length() > 0))
					|| (notes.equals("#") && isRoute(flight.notes)));
		
		if (result) {
			Database db = Database.getInstance();
			Aircraft aircraft = db.getAircraft(flight.acid);
			if (aircraft != null) {
				result = result
					&&	(model.equals("") || checkModel(aircraft))
					&&	(acceptAllClasses || checkClass(aircraft))
					&&	(!tailwheel || aircraft.tailwheel.equals("yes"))
					&&	(!retractable || aircraft.retractable.equals("yes"))
					&&	(!complex || aircraft.complex.equals("yes"));
			}
		}
		return result;
	}
	
	private boolean isXC(Flight flight) {
		Airports aps = Airports.getInstance();
		return (aps.getXCDistance(flight.route) >= 50.0);
	}
	
	private boolean isRoundTrip(Flight flight) {
		if (flight.ldg != 2) return false;
		return (flight.route.split(" ").length == 2);
	}
	
	private boolean isRoute(String s) {
		String[] wps = s.split(" ");
		if (wps.length < 2) return false;
		for (String wp : wps) {
			wp = wp.trim();
			int len = wp.length();
			if ((len < 3) || len > 6) return false;
			if (wp.replaceAll("[0-9A-Z]", "").length() != 0) return false;
		}
		return true;
	}
	
	private boolean checkModel(Aircraft ac) {
		String m = ac.model.toLowerCase();
		int k = model.indexOf("*");
		if (k == -1) return m.equals(model);
		else return m.startsWith(model.substring(0,k));
	}
	
	private boolean checkClass(Aircraft ac) {
		return
				(asel && ac.category.equals("ASEL"))
			||	(ases && ac.category.equals("ASES"))
			||	(amel && ac.category.equals("AMEL"))
			||	(ames && ac.category.equals("AMES"))
			||	(glider && ac.category.equals("Glider"))
			||	(helicopter && ac.category.equals("Helicopter"));
	}
	
	public Element getElement(Element parent) {
		Document doc = parent.getOwnerDocument();
		Element sc = doc.createElement("SC");
		sc.setAttribute("earliestDate", earliestDate);
		sc.setAttribute("latestDate", latestDate);
		sc.setAttribute("route", route);
		sc.setAttribute("acid", acid);
		sc.setAttribute("model", model);
		if (asel) sc.setAttribute("asel", "yes");
		if (ases) sc.setAttribute("ases", "yes");
		if (amel) sc.setAttribute("amel", "yes");
		if (ames) sc.setAttribute("ames", "yes");
		if (glider) sc.setAttribute("glider", "yes");
		if (helicopter) sc.setAttribute("helicopter", "yes");
		if (tailwheel) sc.setAttribute("tailwheel", "yes");
		if (retractable) sc.setAttribute("retractable", "yes");
		if (complex) sc.setAttribute("complex", "yes");
		sc.setAttribute("notes", notes);
		return sc;
	}
	
	public void print() {
		acceptAllClasses = !(this.asel || this.ases || this.amel || this.ames || this.glider || this.helicopter);
		System.out.println("SearchCriteria:");
		System.out.println("  earliestDate: \""+earliestDate+"\"");
		System.out.println("  latestDate:   \""+latestDate+"\"");
		System.out.println("  route:         \""+route+"\"");
		System.out.println("  acid:         \""+acid+"\"");
		System.out.println("  model:        \""+model+"\"");
		System.out.println("  acceptAll:    "+acceptAllClasses);
		System.out.println("  asel:         "+asel);
		System.out.println("  ases:         "+ases);
		System.out.println("  amel:         "+amel);
		System.out.println("  ames:         "+ames);
		System.out.println("  glider:       "+glider);
		System.out.println("  helicopter:   "+helicopter);
		System.out.println("  tailwheel:    "+tailwheel);
		System.out.println("  retractable:  "+retractable);
		System.out.println("  complex:      "+complex);
		System.out.println("  notes:        \""+notes+"\"");
	}
		
}
