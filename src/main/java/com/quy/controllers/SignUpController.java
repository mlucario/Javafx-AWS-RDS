package com.quy.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.quy.database.DBHandler;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class SignUpController extends Controller implements Initializable{
    @FXML
    private JFXTextField txtUsername;

    @FXML
    private JFXPasswordField txtPassword1;

    @FXML
    private JFXPasswordField txtPassword2;
    @FXML
    private JFXButton btnSignUp;

    @FXML
    private JFXButton btnSignIn;
    private DBHandler dbHandler;
 	private double x, y;
 	
 	
 	
 	@FXML
	void dragged(MouseEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setX(event.getScreenX() - x);
		stage.setY(event.getScreenY() - y);
	}

	@FXML
	void pressed(MouseEvent event) {
		x = event.getSceneX();
		y = event.getSceneY();
	}
    @FXML
    void signInPressed(ActionEvent event) {
    	System.out.println("Go back sign in");
    	goToScene(LOGIN_SCENE,btnSignIn, false);
    }

    @FXML
    void signUpAction(ActionEvent event) {
    	
    }
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dbHandler = new DBHandler();
		textFieldFormat(txtPassword1, "Password Required");
		textFieldFormat(txtPassword2, "Confirm Password Required");
		textFieldFormat(txtUsername,"Username Required",false);
		
		 Platform.runLater(new Runnable() {
		        @Override
		        public void run() {
		        	txtUsername.requestFocus();
		        }
		    });
		 
		 txtUsername.setOnAction(e -> {
			 txtPassword1.requestFocus();
		 });
		 
		 txtPassword1.set
	}

}
