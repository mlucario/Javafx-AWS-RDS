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

public class DBHandler {

	private Connection dbconnection;
	private PreparedStatement pst;
	private ResultSet rs;
	private final String STANDARD_USER = "user";

	public Connection getConnectionAWS() {

//		 System.out.println("----MySQL JDBC Connection Testing -------");

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
//		        System.out.println("Where is your MySQL JDBC Driver?");
			e.printStackTrace();
			return null;
		}

//		    System.out.println("MySQL JDBC Driver Registered!");

		try {
			dbconnection = DriverManager.getConnection(
					"jdbc:mysql://" + Configs.dbHost + ":" + Configs.dbPort + "/" + Configs.dbName, Configs.dbUsername,
					Configs.dbPassword);
		} catch (SQLException e) {
//		        System.out.println("Connection Failed!:\n" + e.getMessage());
		}

		if (dbconnection != null) {
			System.out.println("SUCCESS!!!! You made it, take control your database now!");
		} else {
			System.out.println("FAILURE! Failed to make connection!");
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

		try {
			Class.forName("com.mysql.jdbc.Driver");

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			dbconnection = DriverManager.getConnection(connectionString, Configs.dbuser, Configs.dbpass);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return dbconnection;

	}

	public ArrayList<String> getPasswordAndSaltKey(String username) {
		ArrayList<String> result = new ArrayList<>();

		String query = "SELECT hashing_password,salt_key,user_type FROM users WHERE username=?";
		try {
			dbconnection = getConnectionAWS();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, username);
			rs = pst.executeQuery();

			while (rs.next()) {
				result.add(rs.getString("salt_key"));
				result.add(rs.getString("hashing_password"));
				result.add(rs.getString("user_type"));

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

	public boolean signup(String username, String hashingPassword, String saltKey, String created_at) {
		boolean result = false;

		String query = "INSERT INTO users(username,hashing_password,salt_key,user_type,active,created_at) VALUES (?,?,?,?,?,?)";
		try {
			dbconnection = getConnectionAWS();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, username);
			pst.setString(2, hashingPassword);
			pst.setString(3, saltKey);
			pst.setString(4, STANDARD_USER);
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
			dbconnection = getConnectionAWS();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, username);

			rs = pst.executeQuery();

			if (rs.next()) {
				result = true;
				System.out.println(username + " is exist!.");
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
			dbconnection = getConnectionAWS();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, controller_barcode);

			rs = pst.executeQuery();

			if (rs.next()) {
				result = true;
				System.out.println(controller_barcode + " is exist!.");
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
			dbconnection = getConnectionAWS();
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
	public String assembly(String barcode, String timestamp) {
		String result = "";
		
		
		String query = "UPDATE controllers SET ";
		try {
			dbconnection = getConnectionAWS();
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

	// Insert to history record
	public String addToHistoryRecord(String tester, String station_tested, String time, String controller_barcode,
			String note) {
		String result = "";

		String query = "INSERT INTO history(tester,station_tested,time,controller_barcode,note) VALUES (?,?,?,?,?)";
		try {
			dbconnection = getConnectionAWS();
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
			dbconnection = getConnectionAWS();
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
			dbconnection = getConnectionAWS();
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

	public String getStatusDone(String column,String barcode) {
		String result = "";
		
		String query = "SELECT "+column+" FROM controllers WHERE controller_barcode=?";
		
		try {
			dbconnection = getConnectionAWS();
			pst = dbconnection.prepareStatement(query);
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
	public void shutdown() {
		try {
			dbconnection.close();
			System.out.println("====Database close====");
		} catch (SQLException e) {			
			e.printStackTrace();
		}

	}

}
