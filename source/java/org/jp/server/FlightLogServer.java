package org.jp.server;

import java.io.File;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jp.config.Configuration;
import org.rsna.server.Authenticator;
import org.rsna.server.HttpServer;
import org.rsna.server.ServletSelector;
import org.rsna.server.Users;
import org.rsna.servlets.*;
import org.rsna.util.Cache;
import org.rsna.util.ClasspathUtil;
import org.rsna.util.StringUtil;

/**
 * The FlightLogServer program.
 */
public class FlightLogServer implements Runnable {

	static volatile Logger logger = null;
	static volatile Thread thread;

	HttpServer httpServer = null;

	/**
	 * The startup method.
	 * This method is used when running as a Windows service.
	 * It does not return until the stopService method is called
	 * independently by the service manager.
	 */
	public static void startService(String[] args) {
		thread = new Thread(new FlightLogServer());
		thread.start();
		while (thread.isAlive()) {
			try { thread.join(); }
			catch (InterruptedException ignore) { }
		}
	}

	/**
	 * The shutdown method.
	 * This method is used when running as a Windows service.
	 */
	public static void stopService(String[] args) {
		String dateTime = StringUtil.getDateTime(" ");
		System.out.println(dateTime+" Stopping the service");
		logger.info("Stopping the service");
		if (thread != null) {
			Database.getInstance().close();
			System.out.println(dateTime+" Database closed");
			logger.info("Database closed");
			thread.interrupt();
			System.out.println(dateTime+" Thread interrupted");
			logger.info("Thread interrupted");
		}
	}
	
	/**
	 * Start the thread.
	 * This method is used when running as a Windows service.
	 */
	public void run() {
		while (!thread.interrupted()) {
			try { Thread.sleep(1000); }
			catch (InterruptedException ex) { break; }
		}
		System.out.println(StringUtil.getDateTime(" ")+" Service returned to the Service Manager");
		logger.info("Service returned to the Service Manager");
	}

	/**
	 * The main method of the FlightLogServer program.
	 */
	public static void main(String[] args) {
		new FlightLogServer();
	}

	/**
	 * The constructor of the FlightLogServer program.
	 * There is no UI presented by the program. All access to
	 * the configuration and status of the program is presented
	 * through the HTTP server.
	 */
	public FlightLogServer() {

		//Initialize Log4J
		File logs = new File("logs");
		logs.mkdirs();
		File logFile = new File(logs, "server.log");
		logFile.delete();
		File logProps = new File("log4j.properties");
		PropertyConfigurator.configure(logProps.getAbsolutePath());
		logger = Logger.getLogger(FlightLogServer.class);

		//Instantiate the singleton Cache, clear it, and preload
		//files from the jars. Other files will be loaded as required.
		Cache cache = Cache.getInstance(new File("CACHE"));
		cache.clear();
		logger.info("Cache cleared");

		//Instantiate the singleton Users class
		Users users = Users.getInstance("org.rsna.server.UsersXmlFileImpl", null);

		//Disable session timeouts for the server
		Authenticator.getInstance().setSessionTimeout( 0L );

		//Create the ServletSelector for the HttpServer
		ServletSelector selector =
				new ServletSelector(
						new File("ROOT"),
						false /*do not require authentication*/ );

		//Add in the servlets
		selector.addServlet("admin",		AdminServlet.class);
		selector.addServlet("attacklog",	AttackLogServlet.class);
		selector.addServlet("flightlog",	FlightLogServlet.class);
		selector.addServlet("addflight",	AddFlightServlet.class);
		selector.addServlet("listflights",	ListFlightsServlet.class);
		selector.addServlet("addaircraft",	AddAircraftServlet.class);
		selector.addServlet("listaircraft",	ListAircraftServlet.class);
		selector.addServlet("save",			SaveServlet.class);
		selector.addServlet("convert",		ConvertServlet.class);
		selector.addServlet("initialize",	InitializeServlet.class);
		selector.addServlet("search",		SearchServlet.class);
		selector.addServlet("summary",		SummaryServlet.class);
		selector.addServlet("oddballs",		OddballServlet.class);
		selector.addServlet("login",		LoginServlet.class);
		selector.addServlet("users",		UserManagerServlet.class);
		selector.addServlet("user",			UserServlet.class);
		selector.addServlet("logs",			LogServlet.class);
		selector.addServlet("svrsts",		ServerStatusServlet.class);
		selector.addServlet("system",		SysPropsServlet.class);
		selector.addServlet("environment",	EnvironmentServlet.class);
		selector.addServlet("level",		LoggerLevelServlet.class);
		selector.addServlet("ping",			PingServlet.class);
		
		//Hook the shutdown and close the database
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				Database.getInstance().close();
			}
		});

		//Set the server parameters
		int port = StringUtil.getInt(Configuration.getInstance().getProperty("port"), 8080);
		boolean ssl = false;
		int maxThreads = 10;

		//Start the server.
		try {
			httpServer = new HttpServer(ssl, port, maxThreads, selector);
			httpServer.start();
		}
		catch (Exception ex) {
			logger.error("Unable to instantiate the HTTP Server on port "+port, ex);
			System.exit(0);
		}

		//Start the DDNS thread
		(new DDNS()).start();
	}
}
