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
	public String	from	= "";
	public String	to		= "";
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

	public static GregorianCalendar gc = new GregorianCalendar();

	public Flight() { }

	public Flight(HttpRequest req) {
		id = req.getParameter("id", "");
		date = req.getParameter("date");
		acid = Aircraft.fixACID(req.getParameter("acid"));
		from = req.getParameter("from").toUpperCase();
		to = req.getParameter("to").toUpperCase();
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
		
		if (!date.equals("") && total.isZero() && !tach.isZero()) {
			Flight prev = Database.getInstance().getPrevFlight(date, acid);
			if ((prev != null) && !prev.tach.isZero()) {
				total = new Time(tach.subtract(prev.tach));
			}
		}
		if (tday.isZero() && !total.isZero()) tday = new Time(total.subtract(tnt));
		if (pic.isZero()) pic = new Time(total.subtract(dual));
		if (to.toUpperCase().equals("LOCAL")) to = "";
		if (to.equals(from)) to = "";
	}
	
	public Flight(Element el) {
		id = el.getAttribute("id");
		date = el.getAttribute("date");
		acid = Aircraft.fixACID(el.getAttribute("acid"));
		from = el.getAttribute("from").toUpperCase();
		to = el.getAttribute("to").toUpperCase();
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
	}

	public void set(String name, String value) {
		if (name.equals("id"))			id		= value;
		else if (name.equals("date")) 	date 	= value;
		else if (name.equals("acid")) 	acid	= Aircraft.fixACID(value);
		else if (name.equals("from")) 	from 	= value.toUpperCase();
		else if (name.equals("to")) 	to 		= value.toUpperCase();
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
		
		if (to.equals("LOCAL")) to = "local";
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
		int c = date.compareTo(fl.date);
		if (c != 0) return c;
		int iThis = StringUtil.getInt(id);
		int iOther = StringUtil.getInt(fl.id);
		return (iThis - iOther);
	}
	
	public boolean matches(SearchCriteria criteria) {
		return criteria.matches(this);
	}

	public Element getElement(Element parent) {
		Document doc = parent.getOwnerDocument();
		Element flight = doc.createElement("Flight");
		flight.setAttribute("id", id);
		flight.setAttribute("date", date);
		flight.setAttribute("acid", acid);
		flight.setAttribute("from", from);
		if (!to.equals("")) flight.setAttribute("to", to);
		if (!notes.equals("")) flight.setAttribute("notes", notes);
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
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(id+"/");
		sb.append(date+"/");
		sb.append(acid+"/");
		sb.append(from+"/");
		sb.append(to+"/");
		sb.append(total.toString());
		return sb.toString();
	}
}
