package com.quy.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



public class DBHandler {
//	jdbc:driver://hostname:port/dbName?user=userName&password=password
	
	private Connection dbconnection;
	private PreparedStatement pst;
	public Connection getConnection() {

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
		    	dbconnection = DriverManager.
		                getConnection("jdbc:mysql://" +  Configs.dbHost + ":3306/bizcom", "admin", "1202Amazon");
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
	
	
	public boolean login(String username, String password) {
		boolean result = false;
		
		String query = "SELECT * from users where Username=? and UserPassword=?";
		try {
			dbconnection = getConnection();
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
	
	public boolean signUp(String username, String password, String time ) {
		boolean result = false;
		
		String query = "INSERT INTO users (username,password,user_type,create_at) VALUES (?,?,?,?)";
		try {
			dbconnection = getConnection();
			pst = dbconnection.prepareStatement(query);
			pst.setString(1, username);
			pst.setString(2, password);
			pst.setString(3, "user");
			pst.setString(4,time);
			pst.executeUpdate();

			result = true;

		} catch (SQLException e) {
			e.printStackTrace();
			result = false;
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
