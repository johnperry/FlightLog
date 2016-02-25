package org.jp.config;

import java.io.File;
import org.rsna.ui.*;

/**
 * The FlightLog Configuration.
 */
public class Configuration extends PropertiesFile {
	
	private static Configuration instance = null;

	public static Configuration getInstance() {
		if (instance == null) {
			instance = new Configuration();
		}
		return instance;
	}
	
	protected Configuration() {
		super(new File("config.properties"));
	}
}
