package org.jp.printer;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import org.jp.config.Configuration;
import org.rsna.ui.*;
import org.rsna.util.*;
import org.w3c.dom.*;

/**
 * A program to print the FlightLog.xml file saved by FlightLogServer.
 */
public class FlightLogPrinter extends JFrame {

	String windowTitle = "FlightLog Printer";
	JFileChooser chooser = null;
	File currentFile;
	JScrollPane jsp;
	ColorPane cp;
    Color mainBkg = new Color(0xC6D8F9);
    
	JMenuBar menuBar;
	JMenu fileMenu;
	JMenuItem openItem;
	JMenuItem pageItem;
	JMenuItem printItem;
	JSeparator separator;
	JMenuItem exitItem;
	
	Document doc = null;
	
	PrinterJob job = null;
	FlightPrinter printer = null;
	PageFormat pageFormat =  null;

	public static void main(String args[]) {
		new FlightLogPrinter();
	}

	/**
	 * Constructor
	 */
	public FlightLogPrinter() {
		super();
		initComponents();
		setVisible(true);
		openFile(new File("FlightLog.xml"));
	}

	private void openItemActionPerformed(ActionEvent event) {
		if (chooser == null) {
			chooser = new JFileChooser();
			File dir = new File(".");
			chooser.setCurrentDirectory(dir);
		}
		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			openFile(file);
		}
	}
	
	private void openFile(File file) {
		try {
			doc = XmlUtil.getDocument(file);
			cp.setText(XmlUtil.toPrettyString(doc));
			job = PrinterJob.getPrinterJob();
			pageFormat = job.defaultPage();
			pageFormat.setOrientation(PageFormat.LANDSCAPE);
			
			Paper paper = new Paper();
			double marginV = 36;
			double marginH = 72;
			paper.setImageableArea(marginH, marginV, paper.getWidth() - 2*marginH, paper.getHeight() - 2*marginV);
			pageFormat.setPaper(paper);
			
			printer = new FlightPrinter(doc);
			job.setPrintable(printer, pageFormat);
			job.setPageable(printer);
			pageItem.setEnabled(true);
			printItem.setEnabled(true);
			scrollToTop();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			pageItem.setEnabled(false);
			printItem.setEnabled(false);
			doc = null;
			cp.clear();
		}
	}
	
	private void scrollToTop() {
		SwingUtilities.invokeLater(
			new Runnable() { 
				public void run() {
					jsp.getVerticalScrollBar().setValue(0);
				}
			}
		);
	}

	private void pageItemActionPerformed(ActionEvent event) {
		if (job != null) {
			pageFormat = job.pageDialog(pageFormat);
			job.setPrintable(printer, pageFormat);
			job.setPageable(printer);
		}
	}
	
	private void printItemActionPerformed(ActionEvent event) {
		if (doc != null) {
			boolean doPrint = job.printDialog();
			if (doPrint) {
				try { job.print(); }
				catch (Exception ex) {
					JOptionPane.showMessageDialog(this, "Print failed");
				}
			}
		}
	}
	
	class FlightPrinter implements Pageable, Printable {
		
		String name;
		Font headerFont = new Font("SansSerif", Font.BOLD, 14);
 		Font flightFont = new Font("SansSerif", Font.PLAIN, 12);
		Font titleFont = new Font("SansSerif", Font.BOLD, 14);
		Font numberFont = new Font("SansSerif", Font.PLAIN, 10);
		int marginTop = 9;
		int headerPadding = 4;
		int flightPadding = 4;
		int flightsPerPage = 25;
		int nFlights;
		Row headerRow;
		ArrayList<Row> rows;
		
		public FlightPrinter(Document doc) {
			name = Configuration.getInstance().getProperty("name");
			Element root = doc.getDocumentElement();
			NodeList flights = root.getElementsByTagName("Flight");
			nFlights = flights.getLength();
			headerRow = Row.getHeaderRow(headerFont, headerPadding);
			rows = new ArrayList<Row>();
			for (int i=0; i<flights.getLength(); i++) {
				Row row = new Row(flightFont, flightPadding);
				row.addFlight( (Element)flights.item(i), i+1 );
				rows.add(row);
			}

		}
		
		private void setSizes(Graphics g) {
			int nColumns = headerRow.getNumberOfColumns();
			int[] widths = new int[nColumns];
			for (int i=0; i<nColumns; i++) widths[i] = 0;
			widths = headerRow.getSizes(g, widths);
			for (Row row : rows) widths = row.getSizes(g, widths);
			headerRow.setAssignedWidths(widths);
			for (Row row : rows) row.setAssignedWidths(widths);
		}			
		
		private int getFlightsPerPage() {
			return flightsPerPage;
		}

		//Pageable i/f
		public int getNumberOfPages() {
			return (nFlights - 1) / flightsPerPage + 1;
		}
		public PageFormat getPageFormat(int pageIndex) {
			return pageFormat;
		}
		public Printable getPrintable(int pageIndex) {
			return this;
		}
		//End of Pageable i/f
		
		//Printable i/f
		public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
			setSizes(g);
			int firstRow = page * flightsPerPage;
			if ((firstRow >= 0) && (firstRow < nFlights)) {
				int lastRow = firstRow + flightsPerPage - 1;
				if (lastRow >= nFlights) lastRow = nFlights - 1;
				Graphics2D g2d = (Graphics2D)g;
				g2d.translate(pf.getImageableX(), pf.getImageableY());

				int y = marginTop + headerRow.getMaxAscent();
				headerRow.print(g, y);
				y += headerRow.getMinHeight() - 1;
				for (int i=firstRow; i<=lastRow; i++) {
					Row row = rows.get(i);
					row.print(g, y);
					y += row.getMinHeight();
				}
				
				//Put in the footer
				int bottomY = (int)pf.getImageableHeight() - 10;
				int rightX = (int)pf.getImageableWidth();
				Font f = g.getFont();
				g.setFont(titleFont);
				g.drawString(name, 0, bottomY);
				g.setFont(numberFont);
				FontMetrics fm = g.getFontMetrics();
				String pageNumber = Integer.toString(page + 1);
				int numberWidth = fm.stringWidth(pageNumber);
				g.drawString(pageNumber, rightX-numberWidth, bottomY);
				g.setFont(f);
				
				//Now draw in the frame
				Color c = g.getColor();
				g.setColor(Color.lightGray);
				int tableWidth = headerRow.getWidth();
				int bodyTop = marginTop + headerRow.getMinHeight() + 1;
				y = bodyTop;
				int firstX = headerRow.getCell(0).getAssignedWidth();
				g.drawLine(firstX, y, tableWidth, y);
				for (int i=firstRow; i<=lastRow; i++) {
					Row row = rows.get(i);
					y += row.getMinHeight();
					g.drawLine(firstX, y, tableWidth, y);
				}
				int tableBottom = y;
				ArrayList<Cell> cells = headerRow.getCells();
				g.drawLine(firstX, bodyTop, firstX, tableBottom);
				int x = firstX;
				for (int i=1; i<cells.size(); i++) {
					Cell cell = cells.get(i);
					x += cell.getAssignedWidth();
					g.drawLine(x, bodyTop, x, tableBottom);
				}
				g.setColor(c);
				return Printable.PAGE_EXISTS;
			}
			else return NO_SUCH_PAGE;
		}
		//End of Printable i/f
	}

	void initComponents() {
		setTitle(windowTitle);
        setJMenuBar(getAppMenuBar());

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBackground(mainBkg);
		mainPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		cp = new ColorPane();
		jsp = new JScrollPane();
		jsp.setViewportView(cp);
		mainPanel.add(jsp, BorderLayout.CENTER);
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		pack();
		centerFrame();
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				exit(evt);
			}
		});
	}
	
	JMenuBar getAppMenuBar() {
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		openItem = new JMenuItem("Open...");
		pageItem = new JMenuItem("Page setup...");
		printItem = new JMenuItem("Print...");
		separator = new JSeparator();
		exitItem = new JMenuItem("Exit");
		
        openItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                openItemActionPerformed(evt);
            }
        }
        );
        openItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_O,
                InputEvent.CTRL_MASK));
        fileMenu.add(openItem);

        pageItem.setEnabled(false);
        pageItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                pageItemActionPerformed(evt);
            }
        }
        );
        pageItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_F,
                InputEvent.CTRL_MASK));
        fileMenu.add(pageItem);

        printItem.setEnabled(false);
        printItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                printItemActionPerformed(evt);
            }
        }
        );
        printItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_P,
                InputEvent.CTRL_MASK));
        fileMenu.add(printItem);

        fileMenu.add(separator);

        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                exitItemActionPerformed(evt);
            }
        }
        );
        exitItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Q,
                InputEvent.CTRL_MASK));
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);
        return menuBar;
	}

	void centerFrame() {
		Toolkit t = getToolkit();
		Dimension scr = t.getScreenSize ();
		setSize(scr.width/2, scr.height/2);
		setLocation (new Point ((scr.width-getSize().width)/2,
								(scr.height-getSize().height)/2));
	}

	void exitItemActionPerformed(ActionEvent event) {
		System.exit(0);
	}

	void exit(java.awt.event.WindowEvent evt) {
		System.exit(0);
	}
}
