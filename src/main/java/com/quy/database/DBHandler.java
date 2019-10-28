package com.quy.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;



public class DBHandler {
//	jdbc:driver://hostname:port/dbName?user=userName&password=password
	
	private Connection dbconnection;
	
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
		
		
	}
}
