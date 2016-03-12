package org.jp.printer;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.util.*;
import org.w3c.dom.*;

/**
 * A class representing a row in a FlightLog table
 */
public class Row {
	
	ArrayList<Cell> cells;
	Font font;
	int padding;
	int height = 0;
	int maxAscent = 0;
	int maxDescent = 0;
	Font numberFont = new Font("SansSerif", Font.PLAIN, 6);
	
	public Row(Font font, int padding) {
		this.font = font;
		this.padding = padding;
		cells = new ArrayList<Cell>();
	}
	
	public void addCell(String text, int align) {
		cells.add(new Cell(text, font, align, padding));
	}
	
	public void addCell(String text, int align, int padding, Font font) {
		cells.add(new Cell(text, font, align, padding));
	}
	
	public void addFlight(Element flight, int flightNumber) {
		String date = flight.getAttribute("date");
		date = date.substring(0, Math.min(date.length(), 10));
		addCell(Integer.toString(flightNumber), Cell.RIGHT, 3, numberFont);
		addCell(date, Cell.LEFT);
		addCell(flight.getAttribute("acid"), Cell.LEFT);
		addCell(flight.getAttribute("route"), Cell.LEFT);
 		addCell(flight.getAttribute("total"), Cell.RIGHT);
		addCell(flight.getAttribute("ldg"), Cell.RIGHT);
		addCell(flight.getAttribute("app"), Cell.RIGHT);
		addCell(flight.getAttribute("tday"), Cell.RIGHT);
		addCell(flight.getAttribute("tnt"), Cell.RIGHT);
		addCell(flight.getAttribute("txc"), Cell.RIGHT);
		addCell(flight.getAttribute("inst"), Cell.RIGHT);
		addCell(flight.getAttribute("hood"), Cell.RIGHT);
		addCell(flight.getAttribute("dual"), Cell.RIGHT);
		addCell(flight.getAttribute("pic"), Cell.RIGHT);
	}
	
	public ArrayList<Cell> getCells() {
		return cells;
	}
	
	public int getNumberOfColumns() {
		return cells.size();
	}
	
	public int[] getSizes(Graphics g, int[] widths) {
		height = 0;
		maxAscent = 0;
		maxDescent = 0;
		for (int i=0; i<cells.size(); i++) {
			Cell cell = cells.get(i);
			cell.setSize(g);
			widths[i] = Math.max( widths[i], cell.getMinWidth() );
			height = Math.max(height, cell.getMinHeight());
			maxAscent = Math.max(maxAscent, cell.getMaxAscent());
			maxDescent = Math.max(maxDescent, cell.getMaxDescent());
		}
		return widths;
	}
	
	public int getWidth() {
		int width = 0;
		for (int i=0; i<cells.size(); i++) {
			Cell cell = cells.get(i);
			width += cell.getAssignedWidth();
		}
		return width;
	}
	
	public int getMinHeight() {
		return height;
	}
	
	public int getMaxAscent() {
		return maxAscent;
	}
	
	public int getMaxDescent() {
		return maxDescent;
	}
	
	public void setAssignedWidths(int[] widths) {
		for (int i=0; i<cells.size(); i++) {
			cells.get(i).setAssignedWidth(widths[i]);
		}
	}
	
	public void print(Graphics g, int y) {
		int x = 1;
		for (int i=0; i<cells.size(); i++) {
			Cell cell = cells.get(i);
			cell.print(g, x, y);
			x += cell.getAssignedWidth();
		}
	}
	
	public Cell getCell(int n) {
		return cells.get(n);
	}
	
	public static Row getHeaderRow(Font font, int padding) {
		Row row = new Row(font, padding);
		row.addCell(" ", Cell.RIGHT);
		row.addCell("Date", Cell.LEFT);
		row.addCell("A/C ID", Cell.LEFT);
		row.addCell("Route", Cell.LEFT);
		row.addCell("Total", Cell.RIGHT);
		row.addCell("Ldg", Cell.RIGHT);
		row.addCell("App", Cell.RIGHT);
		row.addCell("Day", Cell.RIGHT);
		row.addCell("Night", Cell.RIGHT);
		row.addCell("XC", Cell.RIGHT);
		row.addCell("Inst", Cell.RIGHT);
		row.addCell("Hood", Cell.RIGHT);
		row.addCell("Dual", Cell.RIGHT);
		row.addCell("PIC", Cell.RIGHT);
		return row;
	}
}
	