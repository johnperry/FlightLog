package org.jp.config;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * The FlightLog Configuration.
 */
public class Configuration extends Properties {
	
	private static Configuration instance = null;
	private static String filename = "config.properties";
	
	File file = null;

	public static Configuration getInstance() {
		if (instance == null) {
			instance = new Configuration( new File(filename) );
		}
		return instance;
	}
	
	protected Configuration(File file) {
		super();
		this.file = file;
		load();
	}

	/**
	 * Load the properties file.
	 * @return true if the load was successful; false otherwise.
	 */
	public boolean load() {
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(file);
			super.load(stream);
			stream.close();
			return true;
		}
		catch (Exception e) {
			System.out.println("Unable to load the configuration:");
			System.out.println("File: "+file);
			e.printStackTrace();
			if (stream != null) {
				try { stream.close(); }
				catch (Exception ignore) { }
			}
			return false;
		}
	}

}
