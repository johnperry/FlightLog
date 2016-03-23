package org.jp.importer;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import org.jp.config.Configuration;
import org.rsna.ui.*;
import org.rsna.util.*;
import org.w3c.dom.*;

/**
 * A program to assemble an XML structure containing
 * airport identifiers and lat/long coordinates.
 */
public class AirportImporter extends JFrame {

	String windowTitle = "AirportImporter";
	ColorPane cpProgress;
	ColorPane cpXML;
	ColorPane cpTXT;
	FooterPanel footer;
    Color background = new Color(0xC6D8F9);
    
    String[] states = {
		"AK",
		"AL",
		"AR",
		"AS",
		"AZ",
		"CA",
		"CO",
		"CT",
		"DC",
		"DE",
		"FL",
		"FM",
		"GA",
		"GU",
		"HI",
		"IA",
		"ID",
		"IL",
		"IN",
		"KS",
		"KY",
		"LA",
		"MA",
		"MD",
		"ME",
		"MH",
		"MI",
		"MN",
		"MO",
		"MP",
		"MS",
		"NC",
		"ND",
		"NE",
		"NH",
		"NJ",
		"NM",
		"NV",
		"NY",
		"OH",
		"OK",
		"OR",
		"PA",
		"PR",
		"PW",
		"RI",
		"SC",
		"SD",
		"TN",
		"TX",
		"UM",
		"UT",
		"VA",
		"VI",
		"VT",
		"WA",
		"WI",
		"WV",
		"WY"
	};
    
	Document doc = null;
	Element root = null;
	
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
		new Importer().start();
	}

	class Importer extends Thread {
		public Importer() {
			super("Importer");
		}
		public void run() {
			try {
				int count = 0;
				Hashtable<String,String> index = new Hashtable<String,String>();
				StringBuffer txtBuffer = new StringBuffer();
				doc = XmlUtil.getDocument();
				root = doc.createElement("Airports");
				doc.appendChild(root);
				for (String state : states) {
					int stateCount = 0;
					footer.setMessage("Processing "+state);
					Element table = getStateTable(state);
					if (table != null) {
						NodeList trs = table.getElementsByTagName("tr");
						for (int i=1; i<trs.getLength(); i++) { //(skip the title row)
							Element tr = (Element)doc.importNode(trs.item(i), true);
							NodeList tds = tr.getElementsByTagName("td");
							if (tds.getLength() >= 8) {
								String id = tds.item(2).getTextContent().trim().toUpperCase();
								if (id.equals("")) {
									id = tds.item(1).getTextContent().trim().toUpperCase();
								}
								footer.setMessage("Processing "+state+": "+id);

								String city = capitalize(tds.item(4).getTextContent().trim(), state);
								String name = capitalize(tds.item(5).getTextContent().trim(), state);
								String lat = filter(tds.item(6).getTextContent());
								String lon = filter(tds.item(7).getTextContent());
								String rwy = filter(tds.item(8).getTextContent());
								
								String elev = "";
								String var = "";
								Element airportTable = getAirportTable((Element)tds.item(5));
								NodeList aptrs = airportTable.getElementsByTagName("tr");
								if (aptrs.getLength() > 1) {
									NodeList aptds = ((Element)aptrs.item(1)).getElementsByTagName("td");
									if (aptds.getLength() > 2) {
										String s = filter(aptds.item(2).getTextContent());
										if (!s.equals("")) elev = s;
									}
									if (aptrs.getLength() > 2) {
										aptds = ((Element)aptrs.item(2)).getElementsByTagName("td");
										if (aptds.getLength() > 2) {
											String s = aptds.item(0).getTextContent().trim();
											String sign = s.endsWith("W") ? "-" : "+";
											s = filter(s);
											if (!s.equals("")) {
												try {
													double d = Double.parseDouble(sign + s);
													var = String.format("%.1f", d);
												}
												catch (Exception unable) { }
											}
										}
									}
								}
								
								Element ap = doc.createElement("Airport");
								ap.setAttribute("state", state);
								ap.setAttribute("id", id);
								ap.setAttribute("city", city);
								ap.setAttribute("name", name);
								ap.setAttribute("lat", lat);
								ap.setAttribute("lon", lon);
								ap.setAttribute("elev", elev);
								ap.setAttribute("rwy", rwy);
								ap.setAttribute("var", var);
								
								StringBuffer sb = new StringBuffer();
								sb.append(id + "|");
								sb.append(name + "|");
								sb.append(city + "|");
								sb.append(state + "|");
								sb.append(lat + "," +lon + "|");
								sb.append(elev + "|");
								sb.append(rwy + "|");
								sb.append(var);
								sb.append("\n");
								String apString = sb.toString().trim();
								
								if (!id.equals("")) {
									String s = index.get(id);
									if (s != null) {
										if (!s.equals(apString)) {
											cpProgress.println("    Non-identical duplicate for ID "+id);
											cpProgress.println("    (1): "+s);
											cpProgress.println("    (2): "+apString);
										}
									}
									else {
										index.put(id, apString);
										root.appendChild(ap);
										txtBuffer.append(apString + "\n");
										cpTXT.println(apString);
										count++;
										stateCount++;
									}
								}
								else {
									cpProgress.println("    Empty ID: \""+apString+"\"");
								}
							}
						}
						cpProgress.println("Done importing "+state+": "+stateCount+" airport"+((stateCount!=1)?"s":""));
					}
					else cpProgress.println("Null table received for "+state+".");
				}
				String xml = XmlUtil.toPrettyString(doc);
				cpXML.println(xml);
				xml = xml.replace("    ", " ");
				FileUtil.setText(new File("Airports.xml"), xml);
				FileUtil.setText(new File("Airports.txt"), txtBuffer.toString());
				cpProgress.println(count + " airports in all");
			}
			catch (Exception ex) {
				StringWriter sw = new StringWriter();
				ex.printStackTrace(new PrintWriter(sw));
				cpProgress.println(sw.toString());
			}
			footer.setMessage("Done");
		}
		private Element getStateTable(String state) {
			Element table = null;
			String url = "http://www.fallingrain.com/world/US/"+state+"/airports.html";
			try {
				HttpURLConnection conn = HttpUtil.getConnection(url);
				conn.setRequestMethod("GET");
				conn.connect();
				String page = FileUtil.getText( conn.getInputStream() );
				int tableStart = page.indexOf("<table");
				if (tableStart >= 0) {
					int tableEnd = page.indexOf("</table>", tableStart);
					if (tableEnd > tableStart) {
						String tableText = page.substring(tableStart, tableEnd+8);
						tableText = tableText.replace("&", "&amp;");
						Document doc = XmlUtil.getDocument(tableText);
						table = doc.getDocumentElement();
					}
				}
			}
			catch (Exception unable) { }
			return table;			
		}
		private Element getAirportTable(Element td) {
			Element table = null;
			String tableText = null;
			try {
				NodeList nl = td.getElementsByTagName("a");
				if (nl.getLength() > 0) {
					String path = ((Element)nl.item(0)).getAttribute("href");
					String url = "http://www.fallingrain.com" + path;
					HttpURLConnection conn = HttpUtil.getConnection(url);
					conn.setRequestMethod("GET");
					conn.connect();
					String page = FileUtil.getText( conn.getInputStream() );
					int tableStart = page.indexOf("<table");
					if (tableStart >= 0) {
						int tableEnd = page.indexOf("</table>", tableStart);
						if (tableEnd > tableStart) {
							int k = page.indexOf("</tr>", tableStart) + 5;
							k = page.indexOf("</tr>", k) + 5;
							k = page.indexOf("</tr>", k) + 5;
							
							tableText = page.substring(tableStart, k) 
											+ page.substring(tableEnd, tableEnd+8);
							tableText = filterAmpersands(tableText);
							Document doc = XmlUtil.getDocument(tableText);
							table = doc.getDocumentElement();
						}
					}
				}
			}
			catch (Exception unable) { 
				System.out.println(tableText);
			}
			return table;			
		}
		private String filter(String s) {
			return s.replaceAll("[^0-9\\.+-]", "").trim();
		}
		private String filterAmpersands(String s) {
			return s.replaceAll("&([^agl#])", "&amp;$1");
		}		
		private String capitalize(String s, String state) {
			if (s == null) s = "";
			s = s.replace("\"", "");
			//s = s.replace("'", "");
			s = s.replace("/", " / ");
			s = s.replaceAll("\\s+"," ");
			s = s.trim();
			String[] words = s.split("\\s");
			StringBuffer sb = new StringBuffer();
			for (String w : words) {
				w = w.toUpperCase();
				if (w.length() > 0) {
					if (w.matches("'[A-Z]'") || w.equals(state)) {
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
