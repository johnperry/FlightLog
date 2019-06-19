package org.jp.importer;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import org.rsna.ui.*;
import org.rsna.util.*;
import org.w3c.dom.*;

/**
 * A program to assemble an XML structure containing
 * airport identifiers and lat/long coordinates.
 */
public class AirportImporter extends JFrame {

	String windowTitle = "Airport Importer";
	ColorPane cpProgress;
	ColorPane cpXML;
	ColorPane cpTXT;
	FooterPanel footer;
    Color background = new Color(0xC6D8F9);
    
    JFileChooser chooser = null;
    File aptFile = new File("APT.txt");
    File txtFile = new File("Airports.txt");
    File xmlFile = new File("Airports.xml");
    
	Hashtable<String,Airport> index = null;
	
	public static void main(String args[]) {
		new AirportImporter();
	}

	/**
	 * Constructor
	 */
	public AirportImporter() {
		super();
		initComponents();
		setVisible(true);
		
		int count = 0;
		index = new Hashtable<String,Airport>();
		try {
			if (aptFile.exists()) {
				BufferedReader reader = new BufferedReader(new FileReader(aptFile));
				String line;
				while ( (line = reader.readLine()) != null ) {
					String type = line.substring(0,3);
					String lineID = line.substring(3,14);
					if (type.equals("APT") && line.substring(14,21).equals("AIRPORT")) {
						Airport airport = new Airport(line);
						index.put(lineID, airport);
						footer.setMessage((++count)+": "+airport.id+" "+airport.state + " " + lineID);
					}
					else if (type.equals("RWY")) {
						Runway runway = new Runway(line);
						Airport airport = index.get(lineID);
						if (airport != null) airport.add(runway);
					}
				}
				Airport[] airports = new Airport[index.size()];
				airports = index.values().toArray(airports);
				Arrays.sort(airports);
				String text = getText(airports);
				cpTXT.append(text);
				FileUtil.setText(txtFile, text);
				Document doc = getXML(airports);
				text = XmlUtil.toPrettyString(doc);
				text = text.replace("    ", " ");
				cpXML.setText(text);
				FileUtil.setText(xmlFile, text);
				cpProgress.println("\nDone.");
			}
			else footer.setMessage("File "+aptFile+" not found.");
		}
		catch (Exception ex) {
			try {
				StringWriter sw = new StringWriter();
				ex.printStackTrace(new PrintWriter(sw));
				cpProgress.println(sw.toString());
			}
			catch (Exception x) { }			
		}
	}
	
	private String getText(Airport[] airports) {
		StringBuffer sb = new StringBuffer();
		for (Airport a : airports) {
			sb.append(a.toString());
		}
		return sb.toString();
	}
	
	private Document getXML(Airport[] airports) throws Exception {
		Document doc = XmlUtil.getDocument();
		Element root = doc.createElement("Airports");
		doc.appendChild(root);
		for (Airport a : airports) {
			root.appendChild(a.toXML(doc));
		}
		return doc;
	}
	
	class Airport implements Comparable<Airport> {
		String lineID;
		String id;
		String name;
		String city;
		String state;
		LinkedList<Runway> runways;
		String lat = "lat";
		String lon = "lon";
		String elev = "elev";
		String var = "var";
		
		public Airport(String line) {
			lineID = line.substring(3, 14);
			id = line.substring(21,31).trim();
			state = line.substring(91,93);
			city = capitalize(line.substring(93,133), state);
			name = capitalize(line.substring(133,183), state);
			elev = line.substring(578, 583).trim();
			lat = getLat(line.substring(523, 537));
			lon = getLon(line.substring(550, 565));
			var = line.substring(586,589).trim();
			if (var.startsWith("0")) var = var.substring(1);
			runways = new LinkedList<Runway>();
		}
		
		//42-08-54.9590N
		private String getLat(String s) {
			double deg = Double.parseDouble(s.substring(0,2));
			double min = Double.parseDouble(s.substring(3,5));
			double sec = Double.parseDouble(s.substring(6,13));
			double lat = deg + min/60 + sec/3600;
			if (s.charAt(13) == 'S') lat = -lat;
			return String.format("%.4f",lat);
		}
		
		//088-33-43.0095W
		private String getLon(String s) {
			double deg = Double.parseDouble(s.substring(0,3));
			double min = Double.parseDouble(s.substring(4,6));
			double sec = Double.parseDouble(s.substring(7,14));
			double lon = deg + min/60 + sec/3600;
			if (s.charAt(14) == 'W') lon = -lon;
			return String.format("%.4f",lon);
		}
		
		public void add(Runway runway) {
			if (!runway.length.equals("0") 
				  && !runway.id.endsWith("X")
					&& !runway.id.endsWith("W")) runways.add(runway);
		}
		
		public int compareTo(Airport a) {
			return id.compareTo(a.id);
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append(id + "|");
			sb.append(name + "|");
			sb.append(city + "|");
			sb.append(state + "|");
			sb.append(lat + "," + lon + "|");
			sb.append(elev + "|");
			StringBuffer rwys = new StringBuffer();
			for (Runway runway : runways) {
				if (rwys.length() > 0) rwys.append(";");
				rwys.append(runway.toString());
			}
			sb.append(rwys.toString() + "|");
			sb.append(var);
			sb.append("\n");
			return sb.toString();
		}
		

		public Element toXML(Document doc) {
			Element a = doc.createElement("Airport");
			a.setAttribute("id", id);
			a.setAttribute("name", name);
			a.setAttribute("city", city);
			a.setAttribute("state", state);
			a.setAttribute("lat", lat);
			a.setAttribute("lon", lon);
			a.setAttribute("elev", elev);
			a.setAttribute("var", var);
			for (Runway r : runways) {
				Element rwy = doc.createElement("rwy");
				rwy.setAttribute("id", r.id);
				rwy.setAttribute("len", r.length);
				rwy.setAttribute("wid", r.width);
				rwy.setAttribute("type", r.type);
				a.appendChild(rwy);
			}
			return a;
		}
	}

	class Runway {
		String id;
		String length;
		String width;
		String type;
		
		public Runway(String line) {
			id = line.substring(16,23);
			if (id.startsWith("0")) id = id.substring(1);
			id = id.trim();
			length = line.substring(23,28).trim();
			width = line.substring(28,32).trim();
			type = line.substring(32,44).trim();
		}
		
		public String toString() {
			return id + ":" + length + "x" + width + "," + type;
		}
	}			

	private String capitalize(String s, String state) {
		s = s.replaceAll("\\s+"," ");
		s = s.trim();
		String[] words = s.split("\\s");
		StringBuffer sb = new StringBuffer();
		for (String w : words) {
			w = w.toUpperCase();
			if (w.length() > 0) {
				if (w.equals(state)) {
					sb.append(w);
				}
				else if (w.equals("OHARE")) {
					sb.append("O'Hare");
				}
				else if (w.equals("DUPAGE")) {
					sb.append("DuPage");
				}
				else if (w.equals("DEKALB")) {
					sb.append("DeKalb");
				}
				else if (w.equals("GTR")) {
					sb.append("Greater");
				}
				else if (w.equals("DE")) {
					sb.append("de");
				}
				else if (w.startsWith("O'") && (w.length() > 2)) {
					sb.append(w.substring(0,3) + w.substring(3).toLowerCase());
				}
				else {
					sb.append(w.substring(0,1));
					sb.append(w.substring(1).toLowerCase());
				}
				sb.append(" ");
			}
		}
		s = sb.toString().replace(" / ", "/").replaceAll("\\s+"," ").trim();
		if (s.endsWith("/")) s = s.substring(0, s.length()-2);
		s = s.replace("de Kalb", "DeKalb");
		int k = s.indexOf("-");
		if ((k > 0) && (k < s.length()-1)) {
			s = s.substring(0, k+1) 
					+ s.substring(k+1, k+2).toUpperCase() 
						+ s.substring(k+2);
		}
		return s;
	}
	
	void initComponents() {
		setTitle(windowTitle);
		JTabbedPane jtp = new JTabbedPane();

		JPanel progressPanel = new JPanel(new BorderLayout());
		progressPanel.setBackground(background);
		progressPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		cpProgress = new ColorPane();
		JScrollPane jsp = new JScrollPane();
		jsp.setViewportView(cpProgress);
		progressPanel.add(jsp, BorderLayout.CENTER);
		jtp.add(progressPanel, "Progress");

		JPanel xmlPanel = new JPanel(new BorderLayout());
		xmlPanel.setBackground(background);
		xmlPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		cpXML = new ColorPane();
		jsp = new JScrollPane();
		jsp.setViewportView(cpXML);
		xmlPanel.add(jsp, BorderLayout.CENTER);
		jtp.add(xmlPanel, "XML");

		JPanel txtPanel = new JPanel(new BorderLayout());
		txtPanel.setBackground(background);
		txtPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		cpTXT = new ColorPane();
		jsp = new JScrollPane();
		jsp.setViewportView(cpTXT);
		txtPanel.add(jsp, BorderLayout.CENTER);
		jtp.add(txtPanel, "Text");
		
		footer = new FooterPanel();
		getContentPane().add(jtp, BorderLayout.CENTER);
		getContentPane().add(footer, BorderLayout.SOUTH);
		pack();
		centerFrame();
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				exit(evt);
			}
		});
	}
	
	class FooterPanel extends JPanel {
		public JLabel message;
		public FooterPanel() {
			super();
			this.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			this.setLayout(new FlowLayout(FlowLayout.LEADING));
			this.setBackground(background);
			message = new JLabel(" ");
			this.add(message);
		}
		public void setMessage(String msg) {
			final String s = msg;
			Runnable display = new Runnable() {
				public void run() {
					message.setText(s);
				}
			};
			SwingUtilities.invokeLater(display);
		}
	}
	
	void centerFrame() {
		Toolkit t = getToolkit();
		Dimension scr = t.getScreenSize ();
		setSize(scr.width/2, scr.height/2);
		setLocation (new Point ((scr.width-getSize().width)/2,
								(scr.height-getSize().height)/2));
	}

	void exit(java.awt.event.WindowEvent evt) {
		System.exit(0);
	}
}
