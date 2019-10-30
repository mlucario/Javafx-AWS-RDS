package com.quy.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.quy.database.DBHandler;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class SignUpController extends Controller implements Initializable{
    @FXML
    private JFXTextField txtUsername;

    @FXML
    private JFXPasswordField txtPassword1;

    @FXML
    private JFXPasswordField txtPassword2;

    private DBHandler dbHandler;
    
    @FXML
    void signInPressed(ActionEvent event) {

    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		dbHandler = new DBHandler();
	}
	
	

    @FXML
    void signUpAction(ActionEvent event) {
    	String username = txtUsername.getText().toLowerCase();
    	String password = txtPassword1.getText().toLowerCase();
    	String time = sqlDate.toString();
    	
    	boolean result = dbHandler.signUp(username, password, time);
    	
    	if(result == true) {
    		System.out.println("Created");
    	}else {
    		System.out.println("Fail create account");	
    	}
    	
    }
}
