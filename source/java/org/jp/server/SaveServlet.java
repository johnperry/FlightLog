package org.jp.server;

import java.io.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.zip.*;
import org.apache.log4j.Logger;
import org.jp.config.Configuration;
import org.rsna.server.HttpRequest;
import org.rsna.server.HttpResponse;
import org.rsna.servlets.Servlet;
import org.rsna.util.FileUtil;
import org.rsna.util.StringUtil;
import org.rsna.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The servlet to save the database as an XML file.
 */
public class SaveServlet extends Servlet {

	static final Logger logger = Logger.getLogger(SaveServlet.class);
	
	/**
	 * Construct a SaveServlet.
	 * @param root the root directory of the server.
	 * @param context the path identifying the servlet.
	 */
	public SaveServlet(File root, String context) {
		super(root, context);
	}

	/**
	 * The GET handler: save the database as an XML file.
	 * @param req the request object
	 * @param res the response object
	 */
	public void doGet(HttpRequest req, HttpResponse res) {
		if (req.isFromAuthenticatedUser()) {
			res.setContentType("html");
			res.write("<html><body><pre>");
			try {
				//Create the XML structure
				Database db = Database.getInstance();
				db.commit();
				Document doc = XmlUtil.getDocument();
				Element root = doc.createElement("FlightLog");
				doc.appendChild(root);
				Element aircraft = doc.createElement("AircraftList");
				root.appendChild(aircraft);
				for (Aircraft ac : db.getAircraftList()) aircraft.appendChild(ac.getElement(aircraft));
				Element flights = doc.createElement("FlightList");
				root.appendChild(flights);
				
				//Get the flights and sort them in date order
				LinkedList<Flight> list = db.getFlightList();
				Collections.sort(list);
				for (Flight ft : list) {
					flights.appendChild(ft.getElement(flights));
				}
				
				//Save it in the program root directory
				String xml = XmlUtil.toPrettyString(doc);
				xml = xml.replace("    ", " ");
				File backupFile = new File("FlightLog.xml");
				backup(backupFile); //copy the previous backup
				FileUtil.setText(backupFile, xml);
				res.write("Backup file created ["+backupFile.getAbsolutePath()+"]");
				
				//Make a zip file containing the XML and the images
				File zipFile = new File("FlightLog.zip");
				if (createZipBackup(zipFile)) {
					res.write("\nBackup zip file created ["+zipFile.getAbsolutePath()+"]");
					
					//Copy it to the cloud drive, if available
					String cloud = Configuration.getInstance().getProperty("cloud", "C:\\Users\\John\\Google Drive");
					File cloudDir = new File(cloud);
					File cloudFile = null;
					if (cloudDir.exists()) {
						cloudFile = new File(cloudDir, zipFile.getName());
						if (FileUtil.copy(zipFile, cloudFile)) res.write("\nBackup zip file copied ");
						else res.write("\nBackup zip file could not be copied ");
						res.write("to the cloud drive ["+cloudFile.getAbsolutePath()+"]");
					}
				}
			}
			catch (Exception ex) {
				StringWriter sw = new StringWriter();
				ex.printStackTrace(new PrintWriter(sw));
				res.write(sw.toString());
				res.setResponseCode(res.servererror);
			}
		}
		else {
			res.setResponseCode(res.forbidden);
		}
		res.write("</pre></body></html>");
		res.send();
	}
	
	private void backup(File targetFile) {
		targetFile = targetFile.getAbsoluteFile();
		File parent = targetFile.getParentFile();
		if (targetFile.exists()) {
			String name = targetFile.getName();
			int k = name.lastIndexOf(".");
			String target = name.substring(0,k) + "[";
			int tlen = target.length();
			String ext = name.substring(k);

			int n = 0;
			File[] files = parent.listFiles();
			if (files != null) {
				for (File file : files) {
					String fname = file.getName();
					if (fname.startsWith(target)) {
						int kk = fname.indexOf("]", tlen);
						if (kk > tlen) {
							int nn = StringUtil.getInt(fname.substring(tlen, kk), 0);
							if (nn > n) n = nn;
						}
					}
				}
			}
			n++;
			File backup = new File(parent, target + n + "]" + ext);
			backup.delete(); //shouldn't be there, but just in case.
			FileUtil.copy(targetFile, backup);
		}
	}

	public static boolean createZipBackup(File zipFile) {
		try {
			File dir = new File(System.getProperty("user.dir"));
			int rootLength = dir.getAbsolutePath().length();

			FileOutputStream fout = new FileOutputStream(zipFile);
			ZipOutputStream zout = new ZipOutputStream(fout);

			zipFile(zout, new File(dir, "FlightLog.xml"), rootLength);
			
			File rootDir = new File(dir, "ROOT");
			File imagesDir = new File(rootDir, "images");
			File[] list = imagesDir.listFiles();
			for (File file : list) {
				zipFile(zout, file, rootLength);
			}
			zout.close();
			return true;
		}
		catch (Exception ex) { return false; }
	}

	private static synchronized void zipFile(ZipOutputStream zout, File file, int rootLength)
												throws Exception {
		FileInputStream fin;
		ZipEntry ze;
		byte[] buffer = new byte[10000];
		int bytesread;
		String entryname = file.getAbsolutePath().substring(rootLength);
		entryname = entryname.replaceAll("\\\\", "/");
		ze = new ZipEntry(entryname);
		if (file.exists()) {
			fin = new FileInputStream(file);
			zout.putNextEntry(ze);
			while ((bytesread = fin.read(buffer)) > 0) zout.write(buffer,0,bytesread);
			zout.closeEntry();
			fin.close();
		}
	}
}
