package org.jp.server;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import jdbm.htree.HTree;
import jdbm.helper.FastIterator;
import jdbm.RecordManager;
import org.apache.log4j.Logger;
import org.rsna.util.*;
import org.w3c.dom.*;

/**
 * A class to encapsulate an integer count for landings and approaches.
 */
public class Database {

	static final Logger logger = Logger.getLogger(Database.class);

	private static Database database = null;
	
	private static final String databaseName = "FlightLog";
	private static final String idTableName = "ID"; //contains the next available id
	private static final String flightsTableName = "FLIGHTS";
	private static final String aircraftTableName = "AIRCRAFT";
	private static final String nextIDIndex = "NEXTID";

	private RecordManager recman;
	private HTree idTable = null;
	private HTree flightsTable = null;
	private HTree aircraftTable = null;

	public static synchronized Database getInstance() {
		if (database == null) {
			database = new Database();
		}
		return database;
	}
	
	protected Database() {
		try {
			File dbFile = new File(databaseName);
			recman = JdbmUtil.getRecordManager(dbFile.getCanonicalPath());
			idTable = JdbmUtil.getHTree(recman, idTableName);
			aircraftTable = JdbmUtil.getHTree(recman, aircraftTableName);
			flightsTable = JdbmUtil.getHTree(recman, flightsTableName);
		}
		catch (Exception ex) {
			logger.warn("Unable to instantiate the database", ex);
		}
	}

	public synchronized void commit() {
		if (recman != null) {
			try { recman.commit(); }
			catch (Exception ignore) { }
		}
	}

	// Close the index.
	public synchronized void close() {
		if (recman != null) {
			try {
				recman.commit();
				recman.close();
				recman = null;
				logger.info("Database closed");
			}
			catch (Exception ignore) { }
		}
	}

	public synchronized void addAircraft(Aircraft aircraft) {
		try {
			aircraftTable.put(aircraft.acid, aircraft);
			commit();
		}
		catch (Exception unable) {
			logger.warn("Unable to insert aircraft "+aircraft.acid);
		}
	}
	
	public synchronized void removeAircraft(String acid) {
		try {
			aircraftTable.remove(acid);
			commit();
		}
		catch (Exception unable) {
			logger.warn("Unable to remove aircraft "+acid);
		}
	}
	
	public synchronized Aircraft getAircraft(String acid) {
		try {
			return (Aircraft)aircraftTable.get(acid);
		}
		catch (Exception unable) {
			logger.warn("Unable to access aircraft table for "+acid);
		}
		return null;
	}
	
	public synchronized LinkedList<Aircraft> getAircraftList() {
		try {
			LinkedList<Aircraft> list = new LinkedList<Aircraft>();
			FastIterator fit = aircraftTable.values();
			Aircraft ac;
			while ( (ac=(Aircraft)fit.next()) != null ) list.add(ac);
			return list;
		}
		catch (Exception ex) { return null; }
	}
	
	public synchronized int getNumberOfAircraft() {
		try {
			int n = 0;
			FastIterator fit = aircraftTable.values();
			String key;
			while ( (key=(String)fit.next()) != null ) n++;
			return n;
		}
		catch (Exception ex) { return -1; }
	}
	
	public synchronized void addFlight(Flight flight) {
		try {
			//See if this is flight has an id
			if (!flight.id.equals("")) {
				//Yes, see if this is an add or delete
				Integer idInt = new Integer(flight.id);
				if (!flight.date.equals("")) {
					//Add the flight
					flightsTable.put(idInt, flight);
				}
				else {
					//Delete the flight
					flightsTable.remove(idInt);
				}
			}
			else {
				//The flight doesn't have an id; create it and add the flight
				Integer idInt = (Integer)idTable.get(nextIDIndex);
				if (idInt == null) idInt = new Integer(0);
				int nextid = idInt.intValue();
				flight.id = Integer.toString(nextid);
				flightsTable.put(idInt, flight);
				nextid++;
				idTable.put(nextIDIndex, new Integer(nextid));
			}
			commit();
		}
		catch (Exception unable) {
			logger.warn("Unable to insert flight ("+flight.id+")");
		}
	}
	
	public synchronized Flight getFlight(String id) throws Exception {
		return getFlight(new Integer(id));
	}
	
	public synchronized Flight getFlight(Integer id) {
		try {
			return (Flight)flightsTable.get(id);
		}
		catch (Exception ex) {
			logger.warn("Unable to access flights table for "+id.intValue());
		}
		return null;		
	}
	
	public synchronized Flight getPrevFlight(String date, String acid) {
		try {
			SearchCriteria sc = new SearchCriteria();
			sc.latestDate = date;
			sc.acid = acid;
			LinkedList<Flight> list = getFlightList(sc);
			Collections.sort(list, Collections.reverseOrder());
			Flight flight = null;
			while (list.size() > 0) {
				flight = list.pop();
				if (flight.date.compareTo(date) < 0) return flight;
			}
		}
		catch (Exception ex) {
			logger.warn("Unable to access flights table", ex);
		}
		return null;		
	}
	
	public synchronized LinkedList<Flight> getFlightList() {
		try {
			LinkedList<Flight> list = new LinkedList<Flight>();
			FastIterator fit = flightsTable.values();
			Flight flight;
			while ( (flight=(Flight)fit.next()) != null ) list.add(flight);
			return list;
		}
		catch (Exception ex) { return null; }
	}
	
	public synchronized LinkedList<Flight> getFlightList(SearchCriteria criteria) {
		try {
			LinkedList<Flight> list = new LinkedList<Flight>();
			FastIterator fit = flightsTable.values();
			Flight flight;
			while ( (flight=(Flight)fit.next()) != null ) {
				if (flight.matches(criteria)) list.add(flight);
			}
			return list;
		}
		catch (Exception ex) { return null; }
	}
	
	public synchronized int getNumberOfFlights() {
		try {
			int n = 0;
			FastIterator fit = flightsTable.values();
			String key;
			while ( (key=(String)fit.next()) != null ) n++;
			return n;
		}
		catch (Exception ex) { return -1; }
	}
	
	public synchronized boolean convert() {
		boolean ok = true;
		try {
			//First fix the aircraft table
			LinkedList<Aircraft> acs = getAircraftList();
			for ( Aircraft ac : acs ) {
				if (ac.acid.contains("/") || ac.model.contains("/")) {
					aircraftTable.remove(ac.acid);
					ac.acid = Aircraft.fixACID(ac.acid);
					ac.model = Aircraft.fixModel(ac.model);
					addAircraft(ac);
				}
			}
			//Now fix the flights table
			LinkedList<Flight> flights = getFlightList();
			for ( Flight flight : flights ) {
				boolean update = false;
				if (flight.acid.contains("/")) {
					flight.acid = Aircraft.fixACID(flight.acid);
					update = true;
				}
				if (update) addFlight(flight);
			}
		}
		catch (Exception ex) { 
			logger.warn("Unable to convert the database", ex);
			ok = false;
		}
		return ok;
	}
	
}
