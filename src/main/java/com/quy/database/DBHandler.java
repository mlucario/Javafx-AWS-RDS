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

import com.quy.bizcom.SMCController;
import com.quy.bizcom.User;

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
	private static final String FIRMWARE_UPDATE_STATION = "Firmware Update Station";
	private static final String BURN_IN_STATION = "Burn In Station";
	private static final String RESULT_STATION = "Result Station";
	private static final String REPAIR_STATION = "Repair Station";
	private static final String PACKING_STATION = "Packing Station";
	private static final String SHIPPING_STATION = "Shipping Station";

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

		String query = "SELECT hashing_password,salt_key,user_type,active FROM users WHERE username=?";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, username);
			rs = pst.executeQuery();

			if (rs.next()) {
				result.add(rs.getString("salt_key"));
				result.add(rs.getString("hashing_password"));
				result.add(rs.getString("user_type"));
				result.add(rs.getString("active"));

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

	// CHange password
	public boolean changePassword(String username, String hashingPassword, String saltKey) {
		boolean result = false;

		String query = "UPDATE users SET hashing_password=?,salt_key=? WHERE username=?";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, hashingPassword);
			pst.setString(2, saltKey);
			pst.setString(3, username);

			if (pst.executeUpdate() != 0) {
				System.out.println("Change pass woed done");
				result = true;
			}

		} catch (SQLException e) {
			LOGGER.error("changePassword" + " " + CONNECTION_FAIL, e.getMessage());
		} finally {

			try {

				pst.close();

				shutdown();
			} catch (SQLException e) {
				LOGGER.error("changePassword" + " " + CLOSE_CONNECTION_FAIL, e.getMessage());
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

	public List<User> getAllUsers() {

		ArrayList<User> result = new ArrayList<>();
		String query = "SELECT username,user_type,active,created_at FROM users WHERE user_type=?  ORDER BY ID";

		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, "User");
			rs = pst.executeQuery();
			while (rs.next()) {
				String userName = rs.getString("username");
				String useType = rs.getString("user_type");
				boolean isActive = rs.getBoolean("active");
				String createdAt = rs.getString("created_at");
				result.add(new User(userName, useType, isActive, createdAt));
			}

		} catch (SQLException e) {
			LOGGER.error(" getAllUsers " + CONNECTION_FAIL, e.getMessage());
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
				LOGGER.error(" getAllUsers " + CLOSE_CONNECTION_FAIL, e.getMessage());
			}

		}

		return result;
	}

	public int countControllers(String where, boolean value) {
		int result = 0;
		String query = "SELECT COUNT(Serial_Number) FROM controllers " + where;

		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setBoolean(1, value);
			rs = pst.executeQuery();
			if (rs.next()) {
				result = rs.getInt(1);
			}

		} catch (SQLException e) {
			LOGGER.error(" getAllUsers " + CONNECTION_FAIL, e.getMessage());
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
				LOGGER.error(" getAllUsers " + CLOSE_CONNECTION_FAIL, e.getMessage());
			}

		}
		return result;
	}

	public String getWork(String serialNumber) {
		String result = "";
		String query = "SELECT Station FROM history WHERE Controller_Serial_Number=? AND isPaid=?";

		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, serialNumber);
			pst.setBoolean(2, false);
			rs = pst.executeQuery();
			result = serialNumber + " : ";
			while (rs.next()) {
				result += rs.getString("Station") + ";";
			}

		} catch (SQLException e) {
			LOGGER.error(" getAllUsers " + CONNECTION_FAIL, e.getMessage());
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
				LOGGER.error(" getAllUsers " + CLOSE_CONNECTION_FAIL, e.getMessage());
			}

		}

		return result;
	}

	public List<SMCController> getAllControllers() {

		ArrayList<SMCController> result = new ArrayList<>();
		String query = "SELECT Model,Lot_ID,Serial_Number,Current_Station,Burn_In_Result,Re_Work_Count FROM controllers WHERE Is_Shipping_Done=?  ORDER BY ID";

		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setBoolean(1, false);
			rs = pst.executeQuery();
			while (rs.next()) {
				String model = rs.getString("Model");
				String lotID = rs.getString("Lot_ID");
				String sn = rs.getString("Serial_Number");
				String currentStation = rs.getString("Current_Station");
				String burnInResult = rs.getString("Burn_In_Result");
				int reWorkCoung = rs.getInt("Re_Work_Count");
				result.add(new SMCController(model, lotID, sn, currentStation, burnInResult, reWorkCoung));
			}

		} catch (SQLException e) {
			LOGGER.error(" getAllUsers " + CONNECTION_FAIL, e.getMessage());
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
				LOGGER.error(" getAllUsers " + CLOSE_CONNECTION_FAIL, e.getMessage());
			}

		}

		return result;
	}

	public String activeOrLockUser(String username, boolean isActive) {
		String result = "";

		String query = "UPDATE users SET active=? WHERE username=?";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setBoolean(1, isActive);
			pst.setString(2, username);

			if (pst.executeUpdate() != 0) {
				result = username;
			}

		} catch (SQLException e) {
			LOGGER.error("activeOrLockUser" + " " + CONNECTION_FAIL, e.getMessage());
		} finally {

			try {

				pst.close();

				shutdown();
			} catch (SQLException e) {
				LOGGER.error("activeOrLockUser" + " " + CLOSE_CONNECTION_FAIL, e.getMessage());
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

		String query = "SELECT " + column + " FROM controllers WHERE Serial_Number=?";

		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, serialNumber);
			rs = pst.executeQuery();

			if (rs.next()) {
				result = rs.getString(column).trim();
//				System.out.println("RESULLTTT " + result);
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

	public boolean updateCurrentStation(String id, String value) {
		boolean result = false;

		String query = "UPDATE controllers SET Current_Station=? WHERE ID=?";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, value);
			pst.setString(2, id);

			if (pst.executeUpdate() != 0) {
				result = true;
			}

		} catch (SQLException e) {
			LOGGER.error("updateCurrentStation" + " " + CONNECTION_FAIL, e.getMessage());
		} finally {

			try {

				pst.close();

				shutdown();
			} catch (SQLException e) {
				LOGGER.error("updateCurrentStation" + " " + CLOSE_CONNECTION_FAIL, e.getMessage());
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
				if (pst != null) {
					pst.close();
				}
				if (rs != null) {
					rs.close();
				}

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

	// Fetch all Controller Which are ready to burn in
	public List<SMCController> getAllReadyToBurn() {
		ArrayList<SMCController> result = new ArrayList<>();
		SMCController.stt = 1;
		String query = "SELECT Model,Serial_Number FROM controllers WHERE Current_Station=? AND Is_Received=? and Is_Assembly_Done=?";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, "Wait_To_Burn_In");
			pst.setBoolean(2, true);
			pst.setBoolean(3, true);

			rs = pst.executeQuery();

			while (rs.next()) {
				String model = rs.getString("Model");
				String serialNumber = rs.getString("Serial_Number");
				result.add(new SMCController(serialNumber, model));
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

	// GET ALL CURRENT FIRMWARE UPDATED
	public List<SMCController> getAllFirmwareUpdated() {
		ArrayList<SMCController> result = new ArrayList<>();
		SMCController.stt = 1;
		String query = "SELECT Model,Serial_Number FROM controllers WHERE Current_Station=? AND Is_Received=? AND Is_Firmware_Updated=?";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, FIRMWARE_UPDATE_STATION);
			pst.setBoolean(2, true);
			pst.setBoolean(3, true);

			rs = pst.executeQuery();

			while (rs.next()) {
				String model = rs.getString("Model");
				String serialNumber = rs.getString("Serial_Number");
				result.add(new SMCController(serialNumber, model));
			}

		} catch (SQLException e) {
			LOGGER.error("getAllFirmwareUpdated " + CONNECTION_FAIL, e.getMessage());
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
				LOGGER.error("getAllFirmwareUpdated " + CLOSE_CONNECTION_FAIL, e.getMessage());
			}

		}

		return result;
	}

//	public boolean finishShipping(String timestamp, int quality, String qa, String listSerialNumber, String info, String listWork) {
//		boolean result = false;
//		String query = "INSERT INTO shipping(Shipping_Date,Quality,Shipper,List_Serial_Number,Info,Work_Count) VALUE=(?,?,?,?,?) ";
//
//		try {
//			dbconnection = getConnection();
//			pst = dbconnection.prepareStatement(query);
//			pst.setString(1, timestamp);
//			pst.setInt(2, quality);
//			pst.setString(1, timestamp);
//			if (pst.executeUpdate() != 0) {
//				result = true;
//			}
//
//		} catch (SQLException e) {
//			LOGGER.error("duplicateRow " + CONNECTION_FAIL, e.getMessage());
//
//		} finally {
//
//			try {
//				pst.close();
//				shutdown();
//			} catch (SQLException e) {
//				LOGGER.error("duplicateRow " + CLOSE_CONNECTION_FAIL, e.getMessage());
//			}
//		}
//		return result;
//	}

	public boolean duplicateRow(String serialNumber, String Id) {
		boolean result = false;
		String query = "INSERT INTO controllers(Lot_ID,Model,Serial_Number,Receiving_Time,Is_Received,Re_Work_Count) "
				+ "SELECT Lot_ID,Model,Serial_Number,Receiving_Time,Is_Received,Re_Work_Count FROM controllers WHERE Serial_Number=? AND ID=?";

		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, serialNumber);
			pst.setString(2, Id);

			if (pst.executeUpdate() != 0) {
				result = true;
			}

		} catch (SQLException e) {
			LOGGER.error("duplicateRow " + CONNECTION_FAIL, e.getMessage());

		} finally {

			try {
				pst.close();
				shutdown();
			} catch (SQLException e) {
				LOGGER.error("duplicateRow " + CLOSE_CONNECTION_FAIL, e.getMessage());
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
	public String assembly(String serialNumber, String timestamp, int reworkCount, boolean isRework) {
		String result = "";

		String query = "UPDATE controllers SET Current_Station=?,Assembly_Time=?,Is_Assembly_Done=?,Re_work_count=?,Is_ReWork=? WHERE Serial_Number=?";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, ASSEMBLY_STATION);
			pst.setString(2, timestamp);
			pst.setBoolean(3, true);
			pst.setInt(4, reworkCount);
			pst.setBoolean(5, isRework);
			pst.setString(6, serialNumber);
			if (pst.executeUpdate() != 0) {
				result = serialNumber;
			} else {
				result = "Update Assembly Station Fail. Please check with manager.";
			}

		} catch (SQLException e) {
			LOGGER.error("RE_" + ASSEMBLY_STATION + " " + CONNECTION_FAIL, e.getMessage());
		} finally {

			try {
				if (pst != null) {
					pst.close();
				}
				shutdown();
			} catch (SQLException e) {
				LOGGER.error("RE_" + ASSEMBLY_STATION + " " + CLOSE_CONNECTION_FAIL, e.getMessage());
			}

		}
		return result;
	}

	// ADD TO BURN IN WAITING LIST
	public String addToBurnInWaitingList(String serialNumber) {
		String result = "";

		String query = "UPDATE controllers SET Current_Station=? WHERE  Serial_Number = ?";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, "Wait_To_Burn_In");
			pst.setString(2, serialNumber);

			if (pst.executeUpdate() != 0) {
				result = serialNumber;
			} else {
				result = "Cannot add to burn in waiting list! Please as manager to help";
			}

		} catch (SQLException e) {
			LOGGER.error("addToBurnInWaitingList " + CONNECTION_FAIL, e.getMessage());

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

	// BURN IN STATION
	public String burnIn(String serialNumber, String timestamp) {
		String result = "";

		String query = "UPDATE controllers SET Current_Station=?,Burn_In_Start=?,Is_Burn_In_Processing=?,Burn_In_Result=? WHERE Serial_Number=?";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, BURN_IN_STATION);
			pst.setString(2, timestamp);
			pst.setBoolean(3, true);
			pst.setString(4, "Burn In Processing");
			pst.setString(5, serialNumber);
			if (pst.executeUpdate() != 0) {
				result = serialNumber;

			} else {
				result = "Cannot Start Burn_In with serial number" + serialNumber + "\r\b";

			}

		} catch (SQLException e) {
			LOGGER.error("burn_in " + CONNECTION_FAIL, e.getMessage());

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

	// RESULT STATION
	public String setResult(String serialNumber, String timestamp, boolean isPassed, String symptoms) {
		String result = "";

		String query = "UPDATE controllers SET Current_Station=?,Burn_In_End=?,Burn_In_Result=?,Is_Burn_In_Done=?,Is_Passed=?,Symptoms_Fail=? WHERE Serial_Number=?";
		try {
			String rx = isPassed ? "PASS" : "FAIL";

			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, RESULT_STATION);
			pst.setString(2, timestamp);
			pst.setString(3, rx);
			pst.setBoolean(4, true);
			pst.setBoolean(5, isPassed);
			pst.setString(6, symptoms);
			pst.setString(7, serialNumber);
			if (pst.executeUpdate() != 0) {
				result = serialNumber;

			} else {
				result = "Cannot change/update into database.";

			}

		} catch (SQLException e) {
			LOGGER.error("setResult " + CONNECTION_FAIL, e.getMessage());
			result = e.getMessage();
		} finally {

			try {
				if (pst != null) {
					pst.close();
				}

				shutdown();
			} catch (SQLException e) {
				LOGGER.error("setResult " + CLOSE_CONNECTION_FAIL, e.getMessage());
			}

		}

		return result;
	}

	// FIRMWARE UPDATED
	public String firmwareUpdate(String serialNumber, String timestamp) {
		String result = "";

		String query = "UPDATE controllers SET Current_Station=?,Firmware_Update_Time=?,Is_Firmware_Updated=? WHERE Serial_Number=?";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, FIRMWARE_UPDATE_STATION);
			pst.setString(2, timestamp);
			pst.setBoolean(3, true);
			pst.setString(4, serialNumber);
			if (pst.executeUpdate() != 0) {
				result = serialNumber;
			} else {
				result = "Update Fimware Station Fail. Please check with manager.";
			}

		} catch (SQLException e) {
			LOGGER.error(FIRMWARE_UPDATE_STATION + " " + CONNECTION_FAIL, e.getMessage());
		} finally {

			try {
				if (pst != null) {
					pst.close();
				}
				shutdown();
			} catch (SQLException e) {
				LOGGER.error(FIRMWARE_UPDATE_STATION + " " + CLOSE_CONNECTION_FAIL, e.getMessage());
			}

		}
		return result;
	}

	// REPAIR
	public String repairController(String serialNumber, String timestamp) {
		String result = "";

		String query = "UPDATE controllers SET Current_Station=?,Repair_Time=?,Is_Repaired_Done=?,Burn_In_Start=?,Burn_In_End=?,Burn_In_Result=?,Is_Burn_In_Processing=?,Is_Burn_In_Done=?, Is_Passed=?  WHERE Serial_Number=?";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, REPAIR_STATION);
			pst.setString(2, timestamp);
			pst.setBoolean(3, true);
			pst.setTimestamp(4, null);
			pst.setTimestamp(5, null);
			pst.setString(6, null);
			pst.setBoolean(7, false);
			pst.setBoolean(8, false);
			pst.setBoolean(8, false);
			pst.setString(9, serialNumber);
			if (pst.executeUpdate() != 0) {
				System.out.println("Repair sucessfully .." + serialNumber);
				result = serialNumber;
			} else {
				result = "Update Database Repair FAIL. Please check with manager.";
			}

		} catch (SQLException e) {
			LOGGER.error(REPAIR_STATION + " " + CONNECTION_FAIL, e.getMessage());
		} finally {

			try {
				if (pst != null) {
					pst.close();
				}
				shutdown();
			} catch (SQLException e) {
				LOGGER.error(REPAIR_STATION + " " + CLOSE_CONNECTION_FAIL, e.getMessage());
			}

		}
		return result;
	}

	// UNREPAIRABLE
	public String unRepairable(String serialNumber, String timestamp) {
		String result = "";

		String query = "UPDATE controllers SET Current_Station=?,Repair_Time=?,Is_Repaired_Done=?,Is_Passed=?,Burn_In_Result=? WHERE Serial_Number=?";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, REPAIR_STATION);
			pst.setString(2, timestamp);
			pst.setBoolean(3, true);
			pst.setBoolean(4, false);
			pst.setString(5, "Unrepairable");
			pst.setString(6, serialNumber);
			if (pst.executeUpdate() != 0) {
				result = serialNumber;
			} else {
				result = "Update Database UNREPAIRABLE FAIL. Please check with manager.";
			}

		} catch (SQLException e) {
			LOGGER.error(REPAIR_STATION + " " + CONNECTION_FAIL, e.getMessage());
		} finally {

			try {
				if (pst != null) {
					pst.close();
				}
				shutdown();
			} catch (SQLException e) {
				LOGGER.error(REPAIR_STATION + " " + CLOSE_CONNECTION_FAIL, e.getMessage());
			}

		}
		return result;
	}

	// Insert to history record
	public String addToHistoryRecord(String QA, String station, String time, String serialNumber, String note,
			boolean isRework) {
		String result = "";

		String query = "INSERT INTO history(QA,Station,Time,Controller_Serial_Number,Note,Is_Re_Work) VALUES (?,?,?,?,?,?)";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, QA);
			pst.setString(2, station);
			pst.setString(3, time);
			pst.setString(4, serialNumber);
			pst.setString(5, note);
			pst.setBoolean(6, isRework);
			if (pst.executeUpdate() != 0) {
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

	// PACKAGE
	public String packed(String serialNumber, String timestamp) {
		String result = "";

		String query = "UPDATE controllers SET Current_Station=?,Packing_Time=?,Is_Packing_Done=? WHERE Serial_Number=?";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, PACKING_STATION);
			pst.setString(2, timestamp);
			pst.setBoolean(3, true);
			pst.setString(4, serialNumber);
			if (pst.executeUpdate() != 0) {
				result = serialNumber;
			} else {
				result = "Update Database PACKAGE FAIL. Please check with manager.";
			}

		} catch (SQLException e) {
			LOGGER.error(PACKING_STATION + " " + CONNECTION_FAIL, e.getMessage());
		} finally {

			try {
				if (pst != null) {
					pst.close();
				}
				shutdown();
			} catch (SQLException e) {
				LOGGER.error(PACKING_STATION + " " + CLOSE_CONNECTION_FAIL, e.getMessage());
			}

		}
		return result;
	}

	// SHIPPING STATION
	public String shipping(String serialNumber, String timestamp) {
		String result = "";

		String query = "UPDATE controllers SET Current_Station=?,Shipping_Time=?,Is_Shipping_Done=? WHERE Serial_Number=?";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, SHIPPING_STATION);
			pst.setString(2, timestamp);
			pst.setBoolean(3, true);
			pst.setString(4, serialNumber);
			if (pst.executeUpdate() != 0) {
				result = serialNumber;
			} else {
				result = "Update Shipping Station Fail. Please check with manager.";
			}

		} catch (SQLException e) {
			LOGGER.error(SHIPPING_STATION + " " + CONNECTION_FAIL, e.getMessage());
		} finally {

			try {
				if (pst != null) {
					pst.close();
				}
				shutdown();
			} catch (SQLException e) {
				LOGGER.error(SHIPPING_STATION + " " + CLOSE_CONNECTION_FAIL, e.getMessage());
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
	public List<SMCController> getAllReceived() {
		ArrayList<SMCController> result = new ArrayList<>();
		String query = "SELECT Model,Serial_Number FROM controllers WHERE Current_Station=? AND Is_Received=?";
		SMCController.stt = 1;
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, RECEIVING_STATION);
			pst.setBoolean(2, true);

			rs = pst.executeQuery();

			while (rs.next()) {
				String model = rs.getString("Model");
				String serialNumber = rs.getString("Serial_Number");
				result.add(new SMCController(serialNumber, model));
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
	public List<SMCController> getAllAssemblyDone() {
		ArrayList<SMCController> result = new ArrayList<>();
		String query = "SELECT Model,Serial_Number FROM controllers WHERE current_station=? AND Is_Received=? AND Is_Assembly_Done=?";
		SMCController.stt = 1;
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, ASSEMBLY_STATION);
			pst.setBoolean(2, true);
			pst.setBoolean(3, true);

			rs = pst.executeQuery();

			while (rs.next()) {
				String model = rs.getString("Model");
				String serialNumber = rs.getString("Serial_Number");
				result.add(new SMCController(serialNumber, model));
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
		String query = "SELECT Serial_Number FROM controllers WHERE Current_Station=? AND Burn_In_Result=?";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, "Burn In Station");
			pst.setString(2, "Burn In Processing");
			rs = pst.executeQuery();

			while (rs.next()) {
				result.add(rs.getString("Serial_Number").trim().replaceAll(" +", " ").toUpperCase());
			}

			LOGGER.info("All BURN IN Controller is fetched");

		} catch (SQLException e) {
			LOGGER.error(" getAllBurning " + CONNECTION_FAIL, e.getMessage());
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
				LOGGER.error(" getAllBurning " + CLOSE_CONNECTION_FAIL, e.getMessage());
			}

		}

		return result;
	}

	// Fetch all Controller Which are passed / fail
	public List<String> getAllPassedOrFail(boolean isPassed) {
		ArrayList<String> result = new ArrayList<>();
		String query = "SELECT Serial_Number FROM controllers WHERE Current_Station=? AND Is_Received=? AND Is_Assembly_Done=? AND Is_Burn_In_Done=? AND Is_Passed=?";
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

			LOGGER.info("All PASSED/FAIL controller is fetched");

		} catch (SQLException e) {
			LOGGER.error(" getAllPassedOrFail " + CONNECTION_FAIL, e.getMessage());
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
				LOGGER.error(" getAllPassedOrFail " + CLOSE_CONNECTION_FAIL, e.getMessage());
			}

		}

		return result;
	}

	// Fetch all packed
	public List<String> getAllPacked() {
		ArrayList<String> result = new ArrayList<>();
		String query = "SELECT Serial_Number FROM controllers WHERE Current_Station=? AND Is_Received=? AND Is_Packing_Done=?";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, PACKING_STATION);
			pst.setBoolean(2, true);
			pst.setBoolean(3, true);

			rs = pst.executeQuery();

			while (rs.next()) {
				result.add(rs.getString("Serial_Number").trim().replaceAll(" +", " ").toUpperCase());
			}

		} catch (SQLException e) {
			LOGGER.error("getAllPacked " + CONNECTION_FAIL, e.getMessage());
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
				LOGGER.error("getAllPacked " + CLOSE_CONNECTION_FAIL, e.getMessage());
			}

		}

		return result;
	}
	// ==========================================================

	public String getCurrentStartedBuring() {
		String result = "";
		String query = "SELECT Burn_In_Start FROM controllers WHERE Current_Station=? AND Is_Received=? AND Is_Burn_In_Processing=? ORDER BY Burn_In_Start DESC";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, BURN_IN_STATION);
			pst.setBoolean(2, true);
			pst.setBoolean(3, true);

			rs = pst.executeQuery();

			if (rs.next()) {
				result = rs.getString("Burn_In_Start").trim().replaceAll(" +", " ").toUpperCase();
			}

		} catch (SQLException e) {
			LOGGER.error("getAllPacked " + CONNECTION_FAIL, e.getMessage());
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
				LOGGER.error("getAllPacked " + CLOSE_CONNECTION_FAIL, e.getMessage());
			}

		}

		return result;

	}

	public String rework(String serialNumber, String timestamp, int reworkCount) {
		String result = "";

		String query = "UPDATE controllers SET Current_Station=?,Re_Work_Start=?,Assembly_Time=?,Burn_In_Start=?, Burn_In_End=?, Packing_Time=? ,Shipping_Time=?, Burn_In_Result=?,"
				+ "Firmware_Update_Time=?,Repair_Time=?,Is_Assembly_Done=?, Is_Firmware_Updated=?, Is_Burn_In_Processing=?, Is_Burn_In_Done=?,"
				+ "Is_Packing_Done=?, Is_Shipping_Done=?, Is_Repaired_Done=?, Is_ReWork=?, Is_Passed=?, Symptoms_Fail=?, Re_work_count=? WHERE Serial_Number=?";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, RECEIVING_STATION);
			pst.setString(2, timestamp);
			pst.setString(3, null);
			pst.setString(4, null);
			pst.setString(5, null);
			pst.setString(6, null);
			pst.setString(7, null);
			pst.setString(8, null);
			pst.setString(9, null);
			pst.setString(10, null);
			pst.setBoolean(11, false);
			pst.setBoolean(12, false);
			pst.setBoolean(13, false);
			pst.setBoolean(14, false);
			pst.setBoolean(15, false);
			pst.setBoolean(16, false);
			pst.setBoolean(17, false);
			pst.setBoolean(18, false);
			pst.setBoolean(19, false);
			pst.setString(20, null);
			pst.setInt(21, reworkCount);
			pst.setString(22, serialNumber);

			if (pst.executeUpdate() != 0) {
				result = serialNumber;
			} else {
				result = "Update Re_Work. Please check with manager.";
			}

		} catch (SQLException e) {
			LOGGER.error(SHIPPING_STATION + " " + CONNECTION_FAIL, e.getMessage());
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
				shutdown();
			} catch (SQLException e) {
				LOGGER.error(SHIPPING_STATION + " " + CLOSE_CONNECTION_FAIL, e.getMessage());
			}

		}

		return result;
	}
}
