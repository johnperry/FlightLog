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
	 * @param args command line arguments
	 */
	public static void startService(String[] args) {
		thread = new Thread(new FlightLogServer());
		thread.start();
		while (thread.isAlive()) {
			try { thread.join(); }
			catch (InterruptedException ignore) { }
		}
		String dateTime = StringUtil.getDateTime(" ");
		System.out.println(dateTime+" startService: exit");
		logger.info("startService: exit");
		System.exit(0);
	}

	/**
	 * The shutdown method.
	 * This method is used when running as a Windows service.
	 * @param args command line arguments
	 */
	public static void stopService(String[] args) {
		String dateTime = StringUtil.getDateTime(" ");
		System.out.println(dateTime+" stopService: stopping the service");
		logger.info("stopService: stopping the service");
		if (thread != null) {
			Database.getInstance().close();
			System.out.println(dateTime+" stopService: database closed");
			logger.info("stopService: database closed");
			System.out.println(dateTime+" stopService: thread interrupted");
			logger.info("stopService: thread interrupted");
			thread.interrupt();
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
	 * @param args command line arguments
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

		//Instantiate the singleton Cache and clear it.
		//Files will be loaded as required.
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
		selector.addServlet("addaircraft",	AddAircraftServlet.class);
		selector.addServlet("addflight",	AddFlightServlet.class);
		selector.addServlet("addimage",		AddImageServlet.class);
		selector.addServlet("admin",		AdminServlet.class);
		selector.addServlet("airports",		AirportsServlet.class);
		selector.addServlet("attacklog",	AttackLogServlet.class);
		selector.addServlet("convert",		ConvertServlet.class);
		selector.addServlet("environment",	EnvironmentServlet.class);
		selector.addServlet("flightlog",	FlightLogServlet.class);
		selector.addServlet("initialize",	InitializeServlet.class);
		selector.addServlet("listaircraft",	ListAircraftServlet.class);
		selector.addServlet("listflights",	ListFlightsServlet.class);
		selector.addServlet("listimages",	ListImagesServlet.class);
		selector.addServlet("level",		LoggerLevelServlet.class);
		selector.addServlet("login",		LoginServlet.class);
		selector.addServlet("logs",			LogServlet.class);
		selector.addServlet("oddballs",		OddballServlet.class);
		selector.addServlet("ping",			PingServlet.class);
		selector.addServlet("save",			SaveServlet.class);
		selector.addServlet("search",		SearchServlet.class);
		selector.addServlet("summary",		SummaryServlet.class);
		selector.addServlet("svrsts",		ServerStatusServlet.class);
		selector.addServlet("system",		SysPropsServlet.class);
		selector.addServlet("user",			UserServlet.class);
		selector.addServlet("users",		UserManagerServlet.class);
		
		//Hook the shutdown and close the database
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				Database.getInstance().close();
			}
		});
		
		//List the configuration
		Configuration config = Configuration.getInstance();
		logger.info("Configuration:");
		logger.info("    port       : " + config.getProperty("port"));
		logger.info("    name       : " + config.getProperty("name"));
		logger.info("    baseAP     : " + config.getProperty("baseAP"));
		logger.info("    defNNumber : " + config.getProperty("defNNumber"));
		logger.info("    cloud      : " + config.getProperty("cloud"));
		logger.info("    ddnsURL    : " + config.getProperty("ddnsURL"));

		//Set the server parameters
		int port = StringUtil.getInt(config.getProperty("port"), 8080);
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
		
		//Load the Airports database in case it has to be downloaded
		Airports.getInstance();
	}
}
