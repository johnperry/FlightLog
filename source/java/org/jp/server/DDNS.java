package org.jp.server;

import java.io.File;
import java.net.HttpURLConnection;
import org.apache.log4j.Logger;
import org.jp.config.Configuration;
import org.rsna.util.FileUtil;
import org.rsna.util.HttpUtil;

/**
 * A class to encapsulate interaction with the Dynamic DNS.
 */
public class DDNS extends Thread {

	static Logger logger = Logger.getLogger(DDNS.class);
	static final long oneSecond = 1000;
	static final long oneMinute = 60 * oneSecond;
	static final long oneHour = 60 * oneMinute;
	long interval = 2 * oneHour;
	boolean success = false;
	private String url;

	/**
	 * Constructor.
	 */
	public DDNS() {
		super("DDNS");
		url = Configuration.getInstance().getProperty("ddnsURL");
	}

	public void run() {
		if (url != null) {
			while (!interrupted()) {
				try {
					HttpURLConnection conn = HttpUtil.getConnection(url);
					conn.setRequestMethod("GET");
					conn.connect();

					String result = FileUtil.getText( conn.getInputStream() );
					if (result.startsWith("OK")) {
						if (!success) {
							logger.info("DDNS result: "+result);
							success = true;
						}
					}
					else {
						logger.warn("DDNS result: "+result);
						success = false;
					}
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
				try { Thread.sleep(interval); }
				catch (Exception ex) { }
			}
		}
	}

}
