package org.jp.printer;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.util.*;
import org.w3c.dom.*;

/**
 * A class representing a cell in a table
 */
public class Cell {
	
	static final int LEFT = -1;
	static final int CENTER = 0;
	static final int RIGHT = 1;
	
	String text;
	Font font;
	int align;
	int padding;
	int width = 0;
	int height = 0;
	int maxAscent = 0;
	int maxDescent = 0;
	int assignedWidth = 0;
	
	public Cell(String text, Font font, int align, int padding) {
		this.text = text;
		this.font = font;
		this.align = align;
		this.padding = padding;
	}
	
	public void setSize(Graphics g) {
		FontMetrics fm = g.getFontMetrics(font);
		width = 2 * padding + fm.stringWidth(text);
		maxAscent = fm.getMaxAscent();
		maxDescent = fm.getMaxDescent();
		height = maxAscent + maxDescent;
	}
	
	public void setAssignedWidth(int width) {
		assignedWidth = width;
	}
	
	public int getMaxAscent() {
		return maxAscent;
	}
	
	public int getMaxDescent() {
		return maxDescent;
	}
	
	public int getAssignedWidth() {
		return assignedWidth;
	}
	
 	public int getMinWidth() {
		return width;
	}
	
	public int getMinHeight() {
		return height;
	}
	
	public void print(Graphics g, int x, int y) {
		Font f = g.getFont();
		g.setFont(font);
		if (align == LEFT) x = x + padding;
		else if (align == CENTER) x = x + (assignedWidth - width)/2 + padding;
		else x = x + assignedWidth - width + padding;
		g.drawString(text, x, y);
		g.setFont(f);		
	}
	
}
	