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
	JScrollPane jsp;
	ColorPane cp;
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
		"MO",
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
				doc = XmlUtil.getDocument();
				root = doc.createElement("Airports");
				doc.appendChild(root);
				for (String state : states) {
					int stateCount = 0;
					footer.setMessage("Processing "+state);
					String table = getTable(state);
					table = table.replace("&", "&amp;");
					//cp.println(table);
					if (table != null) {
						Document tableDoc = XmlUtil.getDocument(table);
						Element tableRoot = tableDoc.getDocumentElement();
						NodeList trs = tableRoot.getElementsByTagName("tr");
						for (int i=1; i<trs.getLength(); i++) { //(skip the title row)
							Element tr = (Element)doc.importNode(trs.item(i), true);
							NodeList tds = tr.getElementsByTagName("td");
							if (tds.getLength() >= 8) {
								Element ap = doc.createElement("Airport");
								ap.setAttribute("state", state);
								ap.setAttribute("id", tds.item(2).getTextContent().trim());
								ap.setAttribute("city", tds.item(4).getTextContent().trim());
								ap.setAttribute("name", tds.item(5).getTextContent().trim());
								ap.setAttribute("lat", filter(tds.item(6).getTextContent().trim()));
								ap.setAttribute("lon", filter(tds.item(7).getTextContent().trim()));
								root.appendChild(ap);
								count++;
								stateCount++;
							}
						}
						cp.println("Done importing "+state+": "+stateCount+" airport"+((stateCount!=1)?"s":""));
					}
					else cp.println("Null table received for "+state+".");
				}
				String xml = XmlUtil.toPrettyString(doc);
				xml = xml.replace("    ", " ");
				FileUtil.setText(new File("Airports.xml"), xml);
				cp.println(count + " airports in all");
			}
			catch (Exception ex) {
				StringWriter sw = new StringWriter();
				ex.printStackTrace(new PrintWriter(sw));
				cp.println(sw.toString());
			}
			footer.setMessage("Done");
		}
		private String getTable(String state) throws Exception {
			String url = "http://www.fallingrain.com/world/US/"+state+"/airports.html";
			HttpURLConnection conn = HttpUtil.getConnection(url);
			conn.setRequestMethod("GET");
			conn.connect();
			String page = FileUtil.getText( conn.getInputStream() );
			int tableStart = page.indexOf("<table");
			if (tableStart >= 0) {
				int tableEnd = page.indexOf("</table>", tableStart);
				if (tableEnd > tableStart) {
					return page.substring(tableStart, tableEnd+8);
				}
			}
			return null;			
		}
		private String filter(String s) {
			s = s.trim();
			int k = s.indexOf("(");
			if (k >= 0) s = s.substring(0, k).trim();
			return s;
		}
	}
	
	void initComponents() {
		setTitle(windowTitle);
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBackground(background);
		mainPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		cp = new ColorPane();
		jsp = new JScrollPane();
		jsp.setViewportView(cp);
		mainPanel.add(jsp, BorderLayout.CENTER);
		footer = new FooterPanel();
		getContentPane().add(mainPanel, BorderLayout.CENTER);
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
