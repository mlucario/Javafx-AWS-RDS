package com.quy.controllers;

import java.net.URL;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.quy.database.DBHandler;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class SignUpController extends Controller implements Initializable {
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
		goToScene(LOGIN_SCENE, btnSignIn, false);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		dbHandler = new DBHandler();
		
		textFieldFormat(txtUsername, "Username is required!",false);
		textFieldFormat(txtPassword1, "Password is required!");
		textFieldFormat(txtPassword2, "Confirm Password is required!");
		
		txtUsername.setOnAction(e -> {
			txtPassword1.requestFocus();
		});
		
		txtPassword1.setOnAction(e -> {
			txtPassword2.requestFocus();
		});
		txtPassword2.setOnAction(e -> {
			signUpAction(e);
		});
		
	}

	@FXML
	void signUpAction(ActionEvent event) {
		String username = txtUsername.getText().toLowerCase();
		String password1 = txtPassword1.getText();
		String password2 = txtPassword2.getText();
		String time = formatter.format(sqlDate);
		

		if(txtUsername.validate() ==false) {
			warningAlert("Username is required. Enter valid username");
			txtUsername.requestFocus();
		}else if(txtPassword1.validate() ==false) {			
			warningAlert("Password is required. Enter valid Password");
			txtPassword1.requestFocus();
		}else if(txtPassword2.validate() ==false) {
			warningAlert("Confirm Password is required. Enter valid Confirm Password");
			txtPassword2.requestFocus();
		}else if(!password1.equals(password2)) {			
			warningAlert("Two Password are different. Enter valid Confirm Password");
			txtPassword2.setText("");
			txtPassword2.requestFocus();
		}else if(dbHandler.isUserExist(username)){
			warningAlert("Username is exist!. Enter other username");
			txtUsername.setText("");
			txtUsername.requestFocus();
		}else {
			Random rd = new Random();
			int tempRandomInt =rd.nextInt(256)+1;
			System.out.println("random number : " + tempRandomInt);
			
			Optional<String> tempSalt = generateSalt(tempRandomInt);
			String tempStringSalt = tempSalt.get();
			System.out.println("tempStringSalt passowrd" + tempStringSalt);
			Optional<String> hashPassword = hashPassword(password1, tempStringSalt);
			String hashingPassword = hashPassword.get();
			System.out.println("Hashing passowrd" + hashingPassword);
			
			boolean result = dbHandler.signup(username, hashingPassword, tempStringSalt, time);
			if(result) {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Create Account Successfully");
				alert.setHeaderText(null);
				alert.setContentText("Your account created. Please take note your account \r\n"
						+ "Username: "+ username
						+ "\r\nPassword: " + password1
						+"\r\nOK to go back Login. Cancel to close application.");

				Optional<javafx.scene.control.ButtonType> resultAlert = alert.showAndWait();
				if (resultAlert.get() == javafx.scene.control.ButtonType.OK) {
					goToScene(LOGIN_SCENE, btnSignUp, false);
				} else {
					close(event);
				}
				
			}else {
				warningAlert("Cannot create your account. Contact with manager to help.");
			}
			
		}
		


	}

	
}
