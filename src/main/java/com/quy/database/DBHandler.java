package com.quy.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



public class DBHandler {

	private Connection dbconnection;
	private PreparedStatement pst;

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
			dbconnection = DriverManager.getConnection("jdbc:mysql://" + Configs.dbHost + ":3306/bizcom", "admin",
					"1202Amazon");
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
	
	
	
	public boolean login(String username, String password) {
		boolean result = false;

		String query = "SELECT * from users where username=? and password=?";
		try {
			dbconnection = getConnectionAWS();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, username);
			pst.setString(2, password);

			ResultSet rs = pst.executeQuery();

			int count = 0;

			while (rs.next()) {
				count = count + 1;
			}

			if (count == 1) {
				result = true;
			} else {
				result = false;
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
	
	public boolean signup(String username, String password, String created_at) {
		boolean result = false;
		
		String query = "INSERT INTO users(username,password,user_type,active,created_at) VALUES (?,?,?,?,?)";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, username);
			pst.setString(2, password);
			pst.setString(3, "admin");
			pst.setBoolean(4, true);
			pst.setString(5, created_at);
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
}
