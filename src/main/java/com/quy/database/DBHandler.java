package com.quy.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DBHandler {

	private Connection dbconnection;
	private PreparedStatement pst;
	private ResultSet rs;
	private final String STANDARD_USER = "user";
	private static final Logger LOGGER = LogManager.getLogger("SQL Connector");

	public Connection getConnectionAWS() {

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your MySQL JDBC Driver?");
			e.printStackTrace();
			return null;
		}

		System.out.println("MySQL JDBC Driver Registered!");

		try {
			dbconnection = DriverManager.getConnection(
					"jdbc:mysql://" + Configs.dbHost + ":" + Configs.dbPort + "/" + Configs.dbName, Configs.dbUsername,
					Configs.dbPassword);
		} catch (SQLException e) {

			LOGGER.error("Connection Failed!: {} ", e.getMessage());
		}

		if (dbconnection != null) {
			LOGGER.info("SUCCESS!!!! You made it, take control your database now!");
		} else {
			LOGGER.error("FAILURE! Failed to make connection!");
		}
		return dbconnection;

	}

	/**
	 * Local database connection
	 * 
	 * @return
	 */
	public Connection getConnection() {

		String connectionString = "jdbc:mysql://" + Configs.dbhost + ":" + Configs.dbport + "/" + Configs.dbname
				+ "?autoReconnect=true&useSSL=false";

//		try {
//			Class.forName("com.mysql.jdbc.Driver");
//
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}

		try {
			dbconnection = DriverManager.getConnection(connectionString, Configs.dbuser, Configs.dbpass);
		} catch (SQLException e) {
			LOGGER.error("Connection Failed! %s", e.getMessage());
		}

		if (dbconnection != null) {
			LOGGER.info("SUCCESS!!!! You made it, take control your database now!");
		} else {
			LOGGER.error("FAILURE! Failed to make connection!");
		}
		return dbconnection;

	}

	public void shutdown() {
		try {
			dbconnection.close();
			LOGGER.info("====Database close====");
		} catch (SQLException e) {
			LOGGER.error("Cannot disconnect! %s", e.getMessage());
		}

	}

	public List<String> getPasswordAndSaltKey(String username) {
		ArrayList<String> result = new ArrayList<>();

		String query = "SELECT hashing_password,salt_key,user_type FROM users WHERE username=?";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, username);
			rs = pst.executeQuery();

			while (rs.next()) {
				result.add(rs.getString("salt_key"));
				result.add(rs.getString("hashing_password"));
				result.add(rs.getString("user_type"));

			}

		} catch (SQLException e) {
			LOGGER.error("Connection Failed! %s", e.getMessage());
		} finally {

			try {
				pst.close();
				rs.close();
				shutdown();
			} catch (SQLException e) {
				LOGGER.error("Cannot close connection! %s", e.getMessage());
			}

		}

		return result;
	}

	public boolean signup(String username, String hashingPassword, String saltKey, String created_at) {
		boolean result = false;

		String query = "INSERT INTO users(username,hashing_password,salt_key,user_type,active,created_at) VALUES (?,?,?,?,?,?)";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, username);
			pst.setString(2, hashingPassword);
			pst.setString(3, saltKey);
//			pst.setString(4, STANDARD_USER);
			pst.setString(4, "Admin");
			pst.setBoolean(5, true);
			pst.setString(6, created_at);
			pst.executeUpdate();
			result = true;

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			try {
				pst.close();
				shutdown();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

		return result;
	}

	public boolean isUserExist(String username) {
		boolean result = false;

		String query = "SELECT username FROM users WHERE username=?";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, username);

			rs = pst.executeQuery();

			if (rs.next()) {
				result = true;
//				System.out.println(username + " is exist!.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			try {
				pst.close();
				rs.close();
				shutdown();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

		return result;
	}

	// Check if barcode is exist in database
	public boolean isBarcodeExist(String controller_barcode) {
		boolean result = false;

		String query = "SELECT controller_barcode FROM controllers WHERE controller_barcode=?";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, controller_barcode);

			rs = pst.executeQuery();

			if (rs.next()) {
				result = true;
//				System.out.println(controller_barcode + " is exist!.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			try {

				pst.close();
				rs.close();
				shutdown();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

		return result;
	}

	// Insert new barcode into database
	public String addNewController(String model, String controller_barcode, String timestamp) {
		String result = "";

		String query = "INSERT INTO controllers(model,controller_barcode,current_station,time_received,Is_Received) VALUES (?,?,?,?,?)";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, model);
			pst.setString(2, controller_barcode);
			pst.setString(3, "Receiving Station");
			pst.setString(4, timestamp);
			pst.setBoolean(5, true);
			if (pst.executeUpdate() == 1) {
				result = controller_barcode;
			} else {
				result = "Cannot add to database. Please as manager to help";
			}

		} catch (SQLException e) {
			e.printStackTrace();
			result = "Cannot add to database. Please as manager to help";
		} finally {

			try {
				pst.close();
				shutdown();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return result;
	}

	// Add To Assembly Staion
	public String assembly(String controller_barcode, String timestamp) {
		String result = "";

		String query = "UPDATE controllers SET current_station=?,time_start_assembly=?,Is_Assembled=? WHERE controller_barcode=?";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, "Assembly Station");
			pst.setString(2, timestamp);
			pst.setBoolean(3, true);
			pst.setString(4, controller_barcode);
			if (pst.executeUpdate() == 1) {
				result = controller_barcode;
			} else {
				result = "Update Assembly Station Fail. Please check with manager.";
			}

		} catch (SQLException e) {
			e.printStackTrace();
			result = "Cannot add to database. Please as manager to help";
		} finally {

			try {
				pst.close();
				shutdown();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return result;
	}

	// add to waiting burn in list
	public String addToBurnInWaitingList(String controller_barcode) {
		String result = "";

		String query = "UPDATE controllers SET current_station=? WHERE controller_barcode=?";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, "Wait_To_Burn_In");
			pst.setString(2, controller_barcode);
			if (pst.executeUpdate() == 1) {
				result = controller_barcode;
			} else {
				result = "Cannot add to database. Please as manager to help";
			}

		} catch (SQLException e) {
			e.printStackTrace();
			result = "Cannot connect to database. Please as manager to help";
		} finally {

			try {
				pst.close();
				shutdown();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return result;
	}

	// Add To Assembly Staion
	public String burn_in(String barcode, String timestamp) {
		String result = "";

		String query = "UPDATE controllers SET current_station=?,time_start_burn_in=?,Is_Burn_In_Processing=? WHERE controller_barcode=?";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, "Burn In Station");
			pst.setString(2, timestamp);
			pst.setBoolean(3, true);
			pst.setString(4, barcode);
			if (pst.executeUpdate() == 1) {
				result = barcode;

			} else {
				result = "Cannot change/update " + barcode + " into database.";

			}

		} catch (SQLException e) {
			e.printStackTrace();
			result = "Cannot Connect to database.";

		} finally {

			try {
				pst.close();
				shutdown();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

		return result;
	}

	// Set the result
	public String setResult(String barcode, String timestamp, boolean isPassed) {
		String result = "";

		String query = "UPDATE controllers SET current_station=?,time_finish_burn_in=?,Is_Burn_In_Done=?,Is_Passed=? WHERE controller_barcode=?";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, "Result Station");
			pst.setString(2, timestamp);
			pst.setBoolean(3, true);
			pst.setBoolean(4, isPassed);
			pst.setString(5, barcode);
			if (pst.executeUpdate() == 1) {
				result = barcode;

			} else {
				result = "Cannot change/update " + barcode + " into database.";

			}

		} catch (SQLException e) {
			e.printStackTrace();
			result = "Cannot Connect to database.";

		} finally {

			try {
				pst.close();
				shutdown();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

		return result;
	}

	// Insert to history record
	public String addToHistoryRecord(String tester, String station_tested, String time, String controller_barcode,
			String note) {
		String result = "";

		String query = "INSERT INTO history(tester,station_tested,time,controller_barcode,note) VALUES (?,?,?,?,?)";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, tester);
			pst.setString(2, station_tested);
			pst.setString(3, time);
			pst.setString(4, controller_barcode);
			pst.setString(5, note);
			if (pst.executeUpdate() == 1) {
				result = controller_barcode;
			} else {
				result = "Cannot add to database. Please as manager to help";
			}

		} catch (SQLException e) {
			e.printStackTrace();
			result = "Cannot add to database. Please as manager to help";
		} finally {

			try {
				pst.close();
				shutdown();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return result;
	}

	// =============FETCH DATA==================

	// Fetch all model
	public List<String> getAllModels() {

		String query = "SELECT model FROM controllers GROUP BY model";
		List<String> result = new ArrayList<>();
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			rs = pst.executeQuery();

			while (rs.next()) {
				result.add(rs.getString("Model").trim().replaceAll(" +", " ").toUpperCase());
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			try {
				pst.close();
				rs.close();
				shutdown();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

		return result;
	}

	// Fetch all controller_barcode
	public Set<String> getAllBarcode() {

		String query = "SELECT controller_barcode FROM controllers";
		Set<String> result = new HashSet<>();
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			rs = pst.executeQuery();

			while (rs.next()) {
				result.add(rs.getString("controller_barcode").trim().replaceAll(" +", " ").toUpperCase());
			}

			System.out.println("All barcode is fetched");

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			try {
				pst.close();
				rs.close();
				shutdown();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

		return result;
	}

	// Fetch all Controller At Receiving Station
	public List<String> getAllReceived() {
		ArrayList<String> result = new ArrayList<>();
		String query = "SELECT controller_barcode FROM controllers WHERE current_station=? AND Is_Received=?";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, "Receiving Station");
			pst.setBoolean(2, true);

			rs = pst.executeQuery();

			while (rs.next()) {
				result.add(rs.getString("controller_barcode").trim().replaceAll(" +", " ").toUpperCase());
			}

			System.out.println("All Receving Station is fetched");

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			try {
				pst.close();
				rs.close();
				shutdown();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

		return result;
	}

	// Fetch all Controller Which are ready to burn in
	public List<String> getAllAssemblyDone() {
		ArrayList<String> result = new ArrayList<>();
		String query = "SELECT controller_barcode FROM controllers WHERE current_station=? AND Is_Received=? AND Is_Assembled=?";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, "Assembly Station");
			pst.setBoolean(2, true);
			pst.setBoolean(3, true);

			rs = pst.executeQuery();

			while (rs.next()) {
				result.add(rs.getString("controller_barcode").trim().replaceAll(" +", " ").toUpperCase());
			}

			System.out.println("All Controller Assemblied is fetched");

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			try {
				pst.close();
				rs.close();
				shutdown();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

		return result;
	}

	// Fetch all Controller Which are ready to burn in
	public List<String> getAllReadyToBurn() {
		ArrayList<String> result = new ArrayList<>();
		String query = "SELECT controller_barcode FROM controllers WHERE current_station=? AND Is_Received=? AND Is_Assembled=?";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, "Wait_To_Burn_In");
			pst.setBoolean(2, true);
			pst.setBoolean(3, true);

			rs = pst.executeQuery();

			while (rs.next()) {
				result.add(rs.getString("controller_barcode").trim().replaceAll(" +", " ").toUpperCase());
			}

			System.out.println("All Controller Ready to burn is fetched");

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			try {
				pst.close();
				rs.close();
				shutdown();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

		return result;
	}

	// Fetch all Controller Which are burning in
	public List<String> getAllBurning() {
		ArrayList<String> result = new ArrayList<>();
		String query = "SELECT controller_barcode FROM controllers WHERE current_station=? AND Is_Received=? AND Is_Assembled=?";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, "Burn In Station");
			pst.setBoolean(2, true);
			pst.setBoolean(3, true);
			rs = pst.executeQuery();

			while (rs.next()) {
				result.add(rs.getString("controller_barcode").trim().replaceAll(" +", " ").toUpperCase());
			}

			System.out.println("All BURN IN Controller is fetched");

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			try {
				pst.close();
				rs.close();
				shutdown();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

		return result;
	}

	// Fetch all Controller Which are passed / fail
	public List<String> getAllPassedOrFail(boolean isPassed) {
		ArrayList<String> result = new ArrayList<>();
		String query = "SELECT controller_barcode FROM controllers WHERE current_station=? AND Is_Received=? AND Is_Assembled=? AND Is_Burn_In_Done=? AND Is_Passed=?";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, "Result Station");
			pst.setBoolean(2, true);
			pst.setBoolean(3, true);
			pst.setBoolean(4, true);
			pst.setBoolean(5, isPassed);
			rs = pst.executeQuery();

			while (rs.next()) {
				result.add(rs.getString("controller_barcode").trim().replaceAll(" +", " ").toUpperCase());
			}

			System.out.println("All PASSED/FAIL controller is fetched");

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			try {
				pst.close();
				rs.close();
				shutdown();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

		return result;
	}

	// ==========================================================
	public String getStatusDone(String column, String barcode) {
		String result = "";

		String query = "SELECT " + column + " FROM controllers WHERE controller_barcode=?";

		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, barcode);
			rs = pst.executeQuery();

			if (rs.next()) {
				result = rs.getString(column).trim();

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			try {
				pst.close();
				rs.close();
				shutdown();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

		return result;
	}

}
