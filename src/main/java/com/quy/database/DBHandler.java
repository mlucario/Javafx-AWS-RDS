/*
 * 
 */
package com.quy.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
	private static final String STANDARD_USER = "User";
	private static final Logger LOGGER = LogManager.getLogger("SQL Connector");
	private static final String CONNECTION_FAIL = "Connection Failed! {}";
	private static final String CLOSE_CONNECTION_FAIL = "Cannot disconnect! {}";

	// List Stations
	private static final String RECEIVING_STATION = "Receiving Station";
	private static final String ASSEMBLY_STATION = "Assembly Station";
	private static final String RE_ASSEMBLY_STATION = "Re_Assembly Station";
	private static final String BURN_IN_STATION = "Burn In Station";
	private static final String RESULT_STATION = "Result Station";
	private static final String REPAIR_STATION = "Repair Station";
	private static final String PACKING_STATION = "Packing Station";
	private static final String SHIPPING_STATION = "Shipping Station";

	private static final String SELECT_SERIAL_NUMBER = "SELECT Serial_Number FROM controllers WHERE ";

	public Connection getConnectionAWS() {

		LOGGER.info("MySQL JDBC Driver Registered!");

		try {
			dbconnection = DriverManager.getConnection(
					"jdbc:mysql://" + Configs.dbHost + ":" + Configs.dbPort + "/" + Configs.dbName, Configs.dbUsername,
					Configs.dbPassword);
		} catch (SQLException e) {

			LOGGER.error(CONNECTION_FAIL, e.getMessage());
		}

		if (dbconnection != null) {
			LOGGER.info("SUCCESS!!!! You made it, take control your database now!");
		} else {
			LOGGER.error(CONNECTION_FAIL);
		}
		return dbconnection;

	}

	public Connection getConnection() {

		String connectionString = "jdbc:mysql://" + Configs.dbhost + ":" + Configs.dbport + "/" + Configs.dbname
				+ "?autoReconnect=true&useSSL=false";

		try {

			dbconnection = DriverManager.getConnection(connectionString, Configs.dbuser, Configs.dbpass);

		} catch (SQLException e) {

			LOGGER.error(CONNECTION_FAIL, e.getMessage());
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
			LOGGER.error(CLOSE_CONNECTION_FAIL, e.getMessage());
		}

	}

	public boolean testConnection(Connection conn, String query) {
		rs = null;
		Statement stmt = null;

		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);

			// connection object is valid: you were able to
			// connect to the database and return something useful.

			return rs.next();

			// there is no hope any more for the validity
			// of the Connection object

		} catch (Exception e) {
			LOGGER.error(CONNECTION_FAIL, e.getMessage());
			return false;
		} finally {
			// close database resources
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				conn.close();
			} catch (SQLException e) {
				LOGGER.error(CLOSE_CONNECTION_FAIL, e.getMessage());
			}
		}
	}

	// SIGN UP - SIGN IN SECTION
	// ==================================================================================================
	public List<String> login(String username) {
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
			LOGGER.error("Login " + CONNECTION_FAIL, e.getMessage());
		} finally {

			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}
				shutdown();
			} catch (SQLException e) {
				LOGGER.error("Login " + CLOSE_CONNECTION_FAIL, e.getMessage());
			}

		}

		return result;
	}

	public boolean signup(String username, String hashingPassword, String saltKey, String createdAt) {
		boolean result = false;

		String query = "INSERT INTO users(username,hashing_password,salt_key,user_type,active,created_at) VALUES (?,?,?,?,?,?)";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, username);
			pst.setString(2, hashingPassword);
			pst.setString(3, saltKey);
			pst.setString(4, STANDARD_USER);
			pst.setBoolean(5, true);
			pst.setString(6, createdAt);
			pst.executeUpdate();
			result = true;

		} catch (SQLException e) {
			LOGGER.error(CONNECTION_FAIL, e.getMessage());
		} finally {

			try {
				pst.close();
				shutdown();
			} catch (SQLException e) {
				LOGGER.error(CLOSE_CONNECTION_FAIL, e.getMessage());
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
			}

		} catch (SQLException e) {
			LOGGER.error(CONNECTION_FAIL, e.getMessage());
		} finally {

			try {
				pst.close();
				rs.close();
				shutdown();
			} catch (SQLException e) {
				LOGGER.error(CLOSE_CONNECTION_FAIL, e.getMessage());
			}
		}
		return result;
	}

	// ==================================================================================================

	// SUPPORT METHODS
	// ==================================================================================================
	// Check if barcode is exist in database
	public String getStatusDone(String column, String serialNumber) {
		String result = "";

		String query = "SELECT " + column + " FROM controllers WHERE Serial_Number=? ORDER BY Receiving_Time DESC";

		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, serialNumber);
			rs = pst.executeQuery();

			if (rs.next()) {
				result = rs.getString(column).trim();

			}

		} catch (SQLException e) {
			LOGGER.error(" getStatusDone " + CONNECTION_FAIL, e.getMessage());
		} finally {

			try {
				if (pst != null) {
					pst.close();
				}

				if (rs != null) {
					rs.close();
				}
				shutdown();
			} catch (SQLException e) {
				LOGGER.error(" getStatusDone " + CLOSE_CONNECTION_FAIL, e.getMessage());
			}

		}

		return result;
	}

	public boolean isBarcodeExist(String serialNumber) {
		boolean result = false;

		String query = "SELECT Serial_Number FROM controllers WHERE Serial_Number=?";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, serialNumber);

			rs = pst.executeQuery();

			if (rs.next()) {
				result = true;
			}

		} catch (SQLException e) {
			LOGGER.error(CONNECTION_FAIL, e.getMessage());
		} finally {

			try {

				pst.close();
				rs.close();
				shutdown();
			} catch (SQLException e) {
				LOGGER.error(CLOSE_CONNECTION_FAIL, e.getMessage());
			}

		}

		return result;
	}

	public List<String> getLastestInfo(String serialNumber) {

		ArrayList<String> result = new ArrayList<>();
		String query = "SELECT Current_Station,Re_Work_Count,Lot_ID,Is_Packing_Done,Is_Shipping_Done FROM controllers WHERE Serial_Number=? ORDER BY Re_work_count DESC";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, serialNumber);

			rs = pst.executeQuery();

			if (rs.next()) {
				result.add(rs.getInt("Re_Work_Count") + "");
				result.add(rs.getString("Lot_ID"));
				result.add(rs.getString("Current_Station"));
				result.add(rs.getString("Is_Packing_Done"));
				result.add(rs.getString("Is_Shipping_Done"));
			}

		} catch (SQLException e) {
			LOGGER.error(CONNECTION_FAIL, e.getMessage());
		} finally {

			try {

				pst.close();
				rs.close();
				shutdown();
			} catch (SQLException e) {
				LOGGER.error(CLOSE_CONNECTION_FAIL, e.getMessage());
			}

		}

		return result;
	}

	// ==================================================================================================

	// STATION HANDLER METHODS
	// ==================================================================================================
	// RECEIVING STATION

	public String addNewController(String model, String serialNumber, String timestamp, String lotId, int reworkTimes) {
		String result = "";

		String query = "INSERT INTO controllers(Model,Serial_Number,Current_Station,Receiving_Time,Is_Received,Lot_ID,Re_Work_Count) VALUES (?,?,?,?,?,?,?)";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, model);
			pst.setString(2, serialNumber);
			pst.setString(3, RECEIVING_STATION);
			pst.setString(4, timestamp);
			pst.setBoolean(5, true);
			pst.setString(6, lotId);
			pst.setInt(7, reworkTimes);

			if (pst.executeUpdate() != 0) {
				result = serialNumber;
			} else {
				result = "Fail to add rework controller!";
			}

		} catch (SQLException e) {
			LOGGER.error(CONNECTION_FAIL, e.getMessage());
			result = CONNECTION_FAIL;
		} finally {

			try {
				pst.close();
				shutdown();
			} catch (SQLException e) {
				LOGGER.error(CLOSE_CONNECTION_FAIL, e.getMessage());
			}
		}
		return result;
	}

	// ASSEMBLY STATION
	public String assembly(String serialNumber, String timestamp, int reworkCount) {
		String result = "";

		String query = "UPDATE controllers SET Current_Station=?,Assembly_Time=?,Is_Assembly_Done=?,Re_work_count=? WHERE Serial_Number=?";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, ASSEMBLY_STATION);
			pst.setString(2, timestamp);
			pst.setBoolean(3, true);
			pst.setInt(4, reworkCount);
			pst.setString(5, serialNumber);
			if (pst.executeUpdate() != 0) {
				result = serialNumber;
			} else {
				result = "Update Assembly Station Fail. Please check with manager.";
			}

		} catch (SQLException e) {
			LOGGER.error("assembly " + CONNECTION_FAIL, e.getMessage());
		} finally {

			try {
				if (pst != null) {
					pst.close();
				}
				shutdown();
			} catch (SQLException e) {
				LOGGER.error("assembly " + CLOSE_CONNECTION_FAIL, e.getMessage());
			}

		}
		return result;
	}

	// Fetch all Controller Which are ready to burn in
	public List<String> getAllReadyToBurn() {
		ArrayList<String> result = new ArrayList<>();
		String query = "SELECT Serial_Number FROM controllers WHERE Current_Station=? AND Is_Received=? AND Is_Assembly_Done=?";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, "Wait_To_Burn_In");
			pst.setBoolean(2, true);
			pst.setBoolean(3, true);

			rs = pst.executeQuery();

			while (rs.next()) {
				result.add(rs.getString("Serial_Number").trim().replaceAll(" +", " ").toUpperCase());
			}

		} catch (SQLException e) {
			LOGGER.error("getAllReadyToBurn " + CONNECTION_FAIL, e.getMessage());
		} finally {

			try {

				if (pst != null) {
					pst.close();
				}

				if (rs != null) {
					rs.close();
				}
				shutdown();
			} catch (SQLException e) {
				LOGGER.error("getAllReadyToBurn " + CLOSE_CONNECTION_FAIL, e.getMessage());
			}

		}

		return result;
	}

	// add to waiting burn in list
	public String addToBurnInWaitingList(String serialNumber) {
		String result = "";

		String query = "UPDATE controllers SET Current_Station=? WHERE Serial_Number=?";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, "Wait_To_Burn_In");
			pst.setString(2, serialNumber);
			if (pst.executeUpdate() != 0) {
				result = serialNumber;
			} else {
				result = "Cannot add to database. Please as manager to help";
			}

		} catch (SQLException e) {
			LOGGER.error("addToBurnInWaitingList " + CONNECTION_FAIL, e.getMessage());
			result = "Cannot connect to database. Please as manager to help";
		} finally {

			try {
				if (pst != null) {
					pst.close();
				}
				shutdown();
			} catch (SQLException e) {
				LOGGER.error("addToBurnInWaitingList " + CLOSE_CONNECTION_FAIL, e.getMessage());
			}

		}
		return result;
	}

	// Stat to burn in
	public String burn_in(String barcode, String timestamp) {
		String result = "";

		String query = "UPDATE controllers SET Current_Station=?,Burn_In_Start=?,Is_Burn_In_Processing=? WHERE Serial_Number=?";
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
			LOGGER.error("burn_in " + CONNECTION_FAIL, e.getMessage());
			result = "Cannot Connect to database.";

		} finally {

			try {
				if (pst != null) {
					pst.close();
				}

				shutdown();
			} catch (SQLException e) {
				LOGGER.error("burn_in " + CLOSE_CONNECTION_FAIL, e.getMessage());
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
	public String addToHistoryRecord(String QA, String station, String time, String serialNumber, String note) {
		String result = "";

		String query = "INSERT INTO history(QA,Station,time,Controller_Serial_Number,note) VALUES (?,?,?,?,?)";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, QA);
			pst.setString(2, station);
			pst.setString(3, time);
			pst.setString(4, serialNumber);
			pst.setString(5, note);
			if (pst.executeUpdate() == 1) {
				result = serialNumber;
			} else {
				result = "Cannot add to history";
			}

		} catch (SQLException e) {
			result = CONNECTION_FAIL;
			LOGGER.error(CONNECTION_FAIL, e.getMessage());
		} finally {

			try {
				pst.close();
				shutdown();
			} catch (SQLException e) {
				LOGGER.error(CLOSE_CONNECTION_FAIL, e.getMessage());
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
		String query = SELECT_SERIAL_NUMBER + " Current_Station=? AND Is_Received=?";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, RECEIVING_STATION);
			pst.setBoolean(2, true);

			rs = pst.executeQuery();

			while (rs.next()) {
				result.add(rs.getString("Serial_Number").trim().replaceAll(" +", " ").toUpperCase());
			}

			LOGGER.info("All Receving Station is fetched");

		} catch (SQLException e) {
			LOGGER.error(CONNECTION_FAIL, e.getMessage());
		} finally {

			try {
				pst.close();
				rs.close();
				shutdown();
			} catch (SQLException e) {
				LOGGER.error(CLOSE_CONNECTION_FAIL, e.getMessage());
			}

		}

		return result;
	}

	// Fetch all Controller Which are ready to burn in
	public List<String> getAllAssemblyDone() {
		ArrayList<String> result = new ArrayList<>();
		String query = "SELECT Serial_Number FROM controllers WHERE current_station=? AND Is_Received=? AND Is_Assembly_Done=?";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, ASSEMBLY_STATION);
			pst.setBoolean(2, true);
			pst.setBoolean(3, true);

			rs = pst.executeQuery();

			while (rs.next()) {
				result.add(rs.getString("Serial_Number").trim().replaceAll(" +", " ").toUpperCase());
			}

			LOGGER.info("All Controller Assemblied Serial_Number are fetched");

		} catch (SQLException e) {

			LOGGER.error(" getAllAssemblyDone " + CONNECTION_FAIL, e.getMessage());
		} finally {

			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}
				shutdown();
			} catch (SQLException e) {
				LOGGER.error(" getAllAssemblyDone " + CLOSE_CONNECTION_FAIL, e.getMessage());
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

}
