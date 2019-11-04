package com.quy.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBHandler {

	private Connection dbconnection;
	private PreparedStatement pst;
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
			ResultSet rs = pst.executeQuery();

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
				dbconnection.close();
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
				dbconnection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
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

			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				result = true;
				System.out.println(username + " is exist!.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			try {
				pst.close();
				dbconnection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

		return result;
	}

	// Fetch all model controllers from database
	public List<String> getAllModels() {

		String query = "SELECT Model FROM models";
		List<String> result = new ArrayList<>();
		try {
			dbconnection = getConnectionAWS();
			pst = dbconnection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				result.add(rs.getString("Model").trim().replaceAll(" +", " ").toUpperCase());
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			try {
				pst.close();
				dbconnection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

		return result;
	}
}
