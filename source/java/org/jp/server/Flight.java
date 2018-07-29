package org.jp.server;

import java.io.Serializable;
import java.math.*;
import java.util.GregorianCalendar;
import org.rsna.server.HttpRequest;
import org.rsna.util.*;
import org.w3c.dom.*;

/**
 * A class encapsulating an individual flight.
 */
public class Flight implements Serializable, Comparable<Flight> {

	static final long serialVersionUID = 1L;

	public String	id		= "";
	public String	date	= today();
	public String	acid	= "";
	public String	route	= "";
	public int		ldg		= 1;
	public int		app		= 0;
	public Time		tach	= Time.zero();
	public Time		total	= Time.zero();
	public Time		txc		= Time.zero();
	public Time		tday	= Time.zero();
	public Time		tnt		= Time.zero();
	public Time		inst	= Time.zero();
	public Time		hood	= Time.zero();
	public Time		dual	= Time.zero();
	public Time		pic		= Time.zero();
	public String	notes	= "";
	public String	images	= "";

	public Flight() { }

	public Flight(HttpRequest req) {
		id = req.getParameter("id", "");
		date = req.getParameter("date");
		acid = Aircraft.fixACID(req.getParameter("acid"));
		route = req.getParameter("route").toUpperCase();
		ldg = StringUtil.getInt(req.getParameter("ldg"));
		app = StringUtil.getInt(req.getParameter("app"));
		tach = getTime(req.getParameter("tach"));
		total = getTime(req.getParameter("total"));
		txc = getTime(req.getParameter("txc"));
		tday = getTime(req.getParameter("tday"));
		tnt = getTime(req.getParameter("tnt"));
		inst = getTime(req.getParameter("inst"));
		hood = getTime(req.getParameter("hood"));
		dual = getTime(req.getParameter("dual"));
		pic = getTime(req.getParameter("pic"));
		notes = trim(req.getParameter("notes", ""));
		
		//Set the images from the existing Flight, if it exists
		if (!id.equals("")) {
			try {
				Database db = Database.getInstance();
				Flight flight = db.getFlight(id);
				if (flight != null) images = flight.images;
			}
			catch (Exception ex) { images = ""; }
		}
		
		if (!date.equals("") && total.isZero() && !tach.isZero()) {
			Flight prev = Database.getInstance().getPrevFlight(date, acid);
			if ((prev != null) && !prev.tach.isZero()) {
				total = new Time(tach.subtract(prev.tach));
			}
		}
		if (tday.isZero() && !total.isZero()) tday = new Time(total.subtract(tnt));
		if (pic.isZero()) pic = new Time(total.subtract(dual));
		if (txc.isZero() && isXC()) txc = total;
	}
	
	public Flight(Element el) {
		id = el.getAttribute("id");
		date = el.getAttribute("date");
		acid = Aircraft.fixACID(el.getAttribute("acid"));
		route = el.getAttribute("route").toUpperCase();
		ldg = StringUtil.getInt(el.getAttribute("ldg"));
		app = StringUtil.getInt(el.getAttribute("app"));
		tach = getTime(el.getAttribute("tach"));
		total = getTime(el.getAttribute("total"));
		txc = getTime(el.getAttribute("txc"));
		tday = getTime(el.getAttribute("tday"));
		tnt = getTime(el.getAttribute("tnt"));
		inst = getTime(el.getAttribute("inst"));
		hood = getTime(el.getAttribute("hood"));
		dual = getTime(el.getAttribute("dual"));
		pic = getTime(el.getAttribute("pic"));
		notes = trim(el.getAttribute("notes"));
		images = el.getAttribute("images").trim();
	}

	public void set(String name, String value) {
		if (name.equals("id"))			id		= value;
		else if (name.equals("date")) 	date 	= value;
		else if (name.equals("acid")) 	acid	= Aircraft.fixACID(value);
		else if (name.equals("route")) 	route 	= value.toUpperCase();
		else if (name.equals("ldg")) 	ldg 	= StringUtil.getInt(value);
		else if (name.equals("app")) 	app 	= StringUtil.getInt(value);
		else if (name.equals("tach"))	tach 	= Time.valueOf(value);
		else if (name.equals("total"))	total 	= Time.valueOf(value);
		else if (name.equals("txc")) 	txc 	= Time.valueOf(value);
		else if (name.equals("tday")) 	tday 	= Time.valueOf(value);
		else if (name.equals("tnt")) 	tnt 	= Time.valueOf(value);
		else if (name.equals("inst")) 	inst 	= Time.valueOf(value);
		else if (name.equals("hood")) 	hood 	= Time.valueOf(value);
		else if (name.equals("dual")) 	dual 	= Time.valueOf(value);
		else if (name.equals("pic")) 	pic 	= Time.valueOf(value);
		else if (name.equals("notes")) 	notes 	= trim(value);
		else if (name.equals("images")) images	= value.trim();
	}
	
	public static String trim(String s) {
		if (s == null) s = "";
		if (s.length() > 0) {
			char c = s.charAt(0);
			if (c == 160) s = s.substring(1);
			s = s.replaceAll("\\s+", " ");
		}
		return s.trim();
	}

	private static String today() {
		GregorianCalendar gc = new GregorianCalendar();
		return
			gc.get(gc.YEAR)
				+ "." + two(gc.get(gc.MONTH) + 1)
					+ "." + two(gc.get(gc.DAY_OF_MONTH));
	}

	private static String two(int k) {
		if (k < 10) return "0" + k;
		return "" + k;
	}

	private Time getTime(String value) {
		return Time.valueOf(value);
	}

	public int compareTo(Flight fl) {
		//Compare the dates.
		//Note: the date may have an index indicating the 
		//number of the flight on the day.
		int c;
		if ((date.length() <= 10) || (fl.date.length() <= 10)) {
			//This case is where at least one date does not
			//contain an index, so we can just compare the strings.
			c = date.compareTo(fl.date);
		}
		else {
			//This case is where both dates contain an index.
			//First compare the date parts.
			String dateString = date.substring(0, 10);
			String fldateString = fl.date.substring(0, 10);
			c = dateString.compareTo(fldateString);
			if (c == 0) {
				//The date parts are the same; now compare the indexes.
				if (date.length() > 11) {
					String dateIndexString = date.substring(11);
					String fldateIndexString = fl.date.substring(11);
					try { 
						c = new Integer(dateIndexString)
										.compareTo(
												new Integer(fldateIndexString) );
					}
					catch (Exception ex) {
						//At least one index didn't parse. This can happen if 
						//the index was entered as, for example, ".a". For
						//this case, just compare the index strings.
						c = dateIndexString.compareTo(fldateIndexString);
					}
				}
			}
		}
		if (c != 0) return c;
		
		//If no order can be found so far, use the id attributes.
		//This can be wrong if flights with the same date are entered out of order.
		int iThis = StringUtil.getInt(id);
		int iOther = StringUtil.getInt(fl.id);
		return (iThis - iOther);
	}
	
	public boolean matches(SearchCriteria criteria) {
		return criteria.matches(this);
	}
	
	public boolean isXC() {
		return (Airports.getInstance().getXCDistance(route) >= 50.0);
	}

	public Element getElement(Element parent) {
		Document doc = parent.getOwnerDocument();
		Element flight = doc.createElement("Flight");
		flight.setAttribute("id", id);
		flight.setAttribute("date", date);
		flight.setAttribute("acid", acid);
		flight.setAttribute("route", route);
		if (!notes.equals("")) flight.setAttribute("notes", notes);
		if (!images.equals("")) flight.setAttribute("images", images);
		if (ldg > 0) flight.setAttribute("ldg", Integer.toString(ldg));
		if (app > 0) flight.setAttribute("app", Integer.toString(app));
		flight.setAttribute("total", total.toString());
		if (!tach.isZero()) flight.setAttribute("tach", tach.toString());
		if (!txc.isZero()) flight.setAttribute("txc", txc.toString());
		if (!tday.isZero()) flight.setAttribute("tday", tday.toString());
		if (!tnt.isZero()) flight.setAttribute("tnt", tnt.toString());
		if (!inst.isZero()) flight.setAttribute("inst", inst.toString());
		if (!hood.isZero()) flight.setAttribute("hood", hood.toString());
		if (!dual.isZero()) flight.setAttribute("dual", dual.toString());
		if (!pic.isZero()) flight.setAttribute("pic", pic.toString());
		return flight;
	}
	
	public static String fixAirportID(String id) {
		id = id.toUpperCase();
		if (id.startsWith("K")) id = id.substring(1);
		return id;
	}
	
	public boolean deleteImage(String name) {
		String[] imgs = images.split("/");
		for (int i=0; i<imgs.length; i++) {
			if (imgs[i].equals(name)) {
				StringBuffer sb = new StringBuffer();
				for (int k=0; k<imgs.length; k++) {
					if (k != i) {
						if (sb.length() != 0) sb.append("/");
						sb.append(imgs[k]);
					}
				}
				images = sb.toString();
				return true;
			}
		}
		return false;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(id+"/");
		sb.append(date+"/");
		sb.append(acid+"/");
		sb.append(route+"/");
		sb.append(total.toString());
		return sb.toString();
	}
}
