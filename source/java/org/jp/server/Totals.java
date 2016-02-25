package org.jp.server;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Totals {
	int ldg = 0;
	int app = 0;
	Time total = Time.zero();
	Time tday = Time.zero();
	Time tnt = Time.zero();
	Time txc = Time.zero();
	Time inst = Time.zero();
	Time hood = Time.zero();
	Time dual = Time.zero();
	Time pic = Time.zero();

	public Totals() { }

	public void add(Flight flight) {
		ldg += flight.ldg;
		app += flight.app;
		total = total.add(flight.total);
		tday = tday.add(flight.tday);
		tnt = tnt.add(flight.tnt);
		txc = txc.add(flight.txc);
		inst = inst.add(flight.inst);
		hood = hood.add(flight.hood);
		dual = dual.add(flight.dual);
		pic = pic.add(flight.pic);
	}

	public Element getElement(Element parent) {
		Document doc = parent.getOwnerDocument();
		Element totals = doc.createElement("Totals");
		totals.setAttribute("ldg", Integer.toString(ldg));
		if (app > 0) totals.setAttribute("app", Integer.toString(app));
		totals.setAttribute("total", total.toString());
		if (!txc.isZero()) totals.setAttribute("txc", txc.toString());
		if (!tday.isZero()) totals.setAttribute("tday", tday.toString());
		if (!tnt.isZero()) totals.setAttribute("tnt", tnt.toString());
		if (!inst.isZero()) totals.setAttribute("inst", inst.toString());
		if (!hood.isZero()) totals.setAttribute("hood", hood.toString());
		if (!dual.isZero()) totals.setAttribute("dual", dual.toString());
		if (!pic.isZero()) totals.setAttribute("pic", pic.toString());
		return totals;
	}
}
