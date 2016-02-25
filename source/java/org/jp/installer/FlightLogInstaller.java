package org.jp.installer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlightLogInstaller extends JFrame {

	static String windowTitle = "FlightLog Installer";
	static String programName = "FlightLog";
	static String introString =   "<p>This program installs FlightLogServer and FlightLogPrinter.</p>"
								+ "<p><b>FlightLogServer</b> provides browser access to the flight log database.</p>"
								+ "<p><b>FlightLogPrinter</b> prints the entire flight log database.</p>";

	JPanel			mainPanel;
	JEditorPane		textPane;
	Color			background;
	JFileChooser	chooser;
	File			directory;

	String[] 		filelist;
	
	public static void main(String[] args) {
		new FlightLogInstaller();
	}

	/**
	 * Class constructor; displays a JFrame introducing the program, 
	 * allows the user to select an install directory,
	 * backs up any properties files found in the directory, and copies files
	 * from the jar into the directory.
	 *<p>
	 * If the selected directory has the same name as the program, the files are
	 * installed in the selected directory. If the selected directory has any other
	 * name, a directory with the name of the program is created in the selected
	 * directory and the files are installed there.
	 *<p>
	 * The installer program must be named [programName]-installer.jar.
	 * The files for installation must be in a directory named [programName]
	 * within the jar. If there is a directory tree of files to be installed,
	 * the root of the tree must the [programName] directory in the jar.
	 */
	public FlightLogInstaller() {
		this.windowTitle = windowTitle;
		this.programName = programName;
		this.introString = introString;
		this.filelist = filelist;
		this.getContentPane().setLayout(new BorderLayout());
		setTitle(windowTitle);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {exitForm(evt);} });
		mainPanel = new JPanel(new BorderLayout());
		this.getContentPane().add(mainPanel, BorderLayout.CENTER);
		background = new Color(0xEBEBEB);
		textPane = new JEditorPane("text/html",getWelcomePage());
		mainPanel.add(textPane,BorderLayout.CENTER);
		pack();
		centerFrame();
		setVisible(true);

		//Get the selected directory
		if ((directory=getDirectory()) == null) exit();

		//Point to the parent of the selected directory so the
		//copy process works correctly for directory trees.
		//If the user has selected a directory with the name
		//of the program, then assume that this is the directory
		//in which to install the program; otherwise, assume that
		//this is the parent of the directory in which to install
		//the program
		if (directory.getName().equals(programName)) {
			directory = directory.getParentFile();
		}

		//Find the installer program so we can get to the files.
		File installer = getInstallerProgramFile();

		//Copy the files
		int count = unpackZipFile(installer,programName,directory.getAbsolutePath());
		
		//Fix the Windows service batch job
		updateWindowsServiceInstaller();

		if (count > 0)
			JOptionPane.showMessageDialog(this,
					programName+" has been installed successfully.\n"
					+ count + " files were installed.",
					"Installation Complete",
					JOptionPane.INFORMATION_MESSAGE);
		else
			JOptionPane.showMessageDialog(this,
					programName+" could not be fully installed.",
					"Installation Failed",
					JOptionPane.INFORMATION_MESSAGE);
		exit();
	}

	private File getInstallerProgramFile() {
		File programFile;
		try { programFile = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()); }
		catch (Exception ex) {
			programFile = new File(programName+"-installer.jar");
		}
		programFile = new File( programFile.getAbsolutePath() );
		if (!programFile.exists()) {
			JOptionPane.showMessageDialog(this,
					"Unable to find the installer program file.\n"+programFile,
					"Installation Failed",
					JOptionPane.INFORMATION_MESSAGE);
			exit();
		}
		return programFile;
	}

	private void updateWindowsServiceInstaller() {
		try {
			File dir = new File(directory, programName);
			File windows = new File(dir, "windows");
			File install = new File(windows, "install.bat");
			String bat = getFileText(install);
			Properties props = new Properties();
			String home = dir.getAbsolutePath();
			home = home.replaceAll("\\\\", "\\\\\\\\");
			props.put("home", home);
			bat = replace(bat, props);
			setFileText(install, bat);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this,
					"Unable to update the windows service install.bat file.",
					"Windows Service Installer",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private String replace(String string, Properties table) {
		try {
			Pattern pattern = Pattern.compile("\\$\\{\\w+\\}");
			Matcher matcher = pattern.matcher(string);
			StringBuffer sb = new StringBuffer();
			while (matcher.find()) {
				String group = matcher.group();
				String key = group.substring(2, group.length()-1).trim();
				String repl = table.getProperty(key);
				if (repl == null) repl = matcher.quoteReplacement(group);
				matcher.appendReplacement(sb, repl);
			}
			matcher.appendTail(sb);
			return sb.toString();
		}
		catch (Exception ex) { return string; }
	}

	private String getFileText(File file) throws Exception {
		BufferedReader br = new BufferedReader(
				new InputStreamReader(
					new FileInputStream(file), "UTF-8"));
		StringWriter sw = new StringWriter();
		int n;
		char[] cbuf = new char[1024];
		while ((n=br.read(cbuf, 0, cbuf.length)) != -1) sw.write(cbuf,0,n);
		br.close();
		return sw.toString();
	}

	private void setFileText(File file, String text) throws Exception {
		BufferedWriter bw = new BufferedWriter(
				new OutputStreamWriter(
					new FileOutputStream(file), "UTF-8"));
		bw.write(text, 0, text.length());
		bw.flush();
		bw.close();
	}

	//Take a tree of files starting in a directory in a zip file
	//and copy them to a disk directory, recreating the tree.
	private int unpackZipFile(File inZipFile, String directory, String parent) {
		int count = 0;
		if (!inZipFile.exists()) return count;
		parent = parent.trim();
		if (!parent.endsWith(File.separator)) parent += File.separator;
		if (!directory.endsWith(File.separator)) directory += File.separator;
		File outFile = null;
		try {
			ZipFile zipFile = new ZipFile(inZipFile);
			Enumeration zipEntries = zipFile.entries();
			while (zipEntries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry)zipEntries.nextElement();
				String name = entry.getName().replace('/',File.separatorChar);
				if (name.startsWith(directory)) {
					outFile = new File(parent + name);
					//Create the directory, just in case
					if (name.indexOf(File.separatorChar) >= 0) {
						String p = name.substring(0,name.lastIndexOf(File.separatorChar)+1);
						File dirFile = new File(parent + p);
						dirFile.mkdirs();
					}
					if (!entry.isDirectory()) {
						//Copy the file
						BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outFile));
						BufferedInputStream in = new BufferedInputStream(zipFile.getInputStream(entry));
						int size = 1024;
						int n = 0;
						byte[] b = new byte[size];
						while ((n = in.read(b,0,size)) != -1) out.write(b,0,n);
						in.close();
						out.flush();
						out.close();
						//Count the file
						count++;
					}
				}
			}
			zipFile.close();
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this,
					"Error copying " + outFile.getName() + "\n" + e.getMessage(),
					"I/O Error", JOptionPane.INFORMATION_MESSAGE);
			return -count;
		}
		return count;
	}

	//Let the user select an installation directory.
	private File getDirectory() {
		if (chooser == null) {
			//See if there is a directory called JavaPrograms
			//in the root of the current drive. If there is,
			//set that as the current directory; otherwise,
			//use the root of the drive as the current directory.
			File currentDirectory = new File(File.separator);
			File javaPrograms = new File(currentDirectory,"JavaPrograms");
			if (javaPrograms.exists() && javaPrograms.isDirectory())
				currentDirectory = javaPrograms;
			//Now make a new chooser and set the current directory.
			chooser = new JFileChooser();
			chooser.setCurrentDirectory(currentDirectory);
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setDialogTitle("Select a directory in which to install the program");
		}
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File dir = chooser.getSelectedFile();
			return dir;
		}
		return null;
	}

	private String getWelcomePage() {
		return
				"<html><head></head><body>\n"
			+	"<center>\n"
			+	"<h1 style=\"color:red\">" + windowTitle + "</h1>\n"
			+	"</center>\n"
			+	introString
			+	"<p>This program allows you to upgrade the <b>"+programName+"</b> "
			+	"program or install a new one.</p>"
			+	"</body></html>";
	}

	private static void exit() {
		System.exit(0);
	}

	private void exitForm(java.awt.event.WindowEvent evt) {
		System.exit(0);
	}

	private void centerFrame() {
		Toolkit t = getToolkit();
		Dimension scr = t.getScreenSize ();
		setSize(scr.width/2, scr.height/2);
		int x = (scr.width-getSize().width)/2;
		int y = (scr.height-getSize().height)/2;
		setLocation(new Point(x,y));
	}
}
