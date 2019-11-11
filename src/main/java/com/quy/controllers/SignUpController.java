package com.quy.controllers;

import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
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
	private double x;
	private double y;
	private Random rd;

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
		LOGGER.info("Go back sign in");
		goToScene(LOGIN_SCENE, btnSignIn, false);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dbHandler = new DBHandler();
		textFieldFormat(txtUsername, "Username is required!", false);
		textFieldFormat(txtPassword1, "Password is required!");
		textFieldFormat(txtPassword2, "Confirm Password is required!");
		btnSignUp.setDisable(true);
		txtUsername.setOnAction(e -> txtPassword1.requestFocus());

		txtPassword1.setOnAction(e -> txtPassword2.requestFocus());
		txtPassword2.setOnAction(e -> signUpAction(e));

	}

	@FXML
	boolean signUpAction(ActionEvent event) {

		String username = txtUsername.getText().toLowerCase();
		String password1 = txtPassword1.getText();
		String password2 = txtPassword2.getText();
		String time = getCurrentTimeStamp();
		String restInput = checkInput(username, password1, password2);
		boolean flag = false;
		if (restInput.isEmpty()) {
			try {
				rd = SecureRandom.getInstanceStrong();
				int tempRandomInt = rd.nextInt(256) + 1;
				LOGGER.debug("random number : %d", tempRandomInt);

				Optional<String> tempSalt = generateSalt(tempRandomInt);
				if (tempSalt.isPresent()) {
					String tempStringSalt = tempSalt.get();
					LOGGER.debug("tempStringSalt passowrd %s", tempStringSalt);

					Optional<String> hashPassword = hashPassword(password1, tempStringSalt);
					if (hashPassword.isPresent()) {
						String hashingPassword = hashPassword.get();
						LOGGER.debug("Hashing passowrd %s", hashingPassword);
						boolean result = dbHandler.signup(username, hashingPassword, tempStringSalt, time);
						if (result) {
							flag = true;
							Alert alert = new Alert(AlertType.CONFIRMATION);
							alert.setTitle("Create Account Successfully");
							alert.setHeaderText(null);
							alert.setContentText("Your account created. Please take note your account \r\n"
									+ "Username: " + username + "\r\nPassword: " + password1
									+ "\r\nOK to go back Login. Cancel to close application.");

							Optional<javafx.scene.control.ButtonType> resultAlert = alert.showAndWait();
							if (resultAlert.isPresent()) {
								if (resultAlert.get() == javafx.scene.control.ButtonType.OK) {
									goToScene(LOGIN_SCENE, btnSignUp, false);
								} else {
									close(event);
								}
							} else {
								LOGGER.error("Cannot modify Ok/Cancel button Default!");
							}

						} else {
							warningAlert("Cannot create your account. Contact with manager to help.");
						}

					} else {
						LOGGER.error("FAIL to encrypte password ( Hashing fail)!");
					}

				} else {
					LOGGER.error("FAIL to generate Salt Key!");
				}

			} catch (NoSuchAlgorithmException e) {

				LOGGER.error("Cannot create random object : %s", e.getMessage());
			}

		} else {
			warningAlert(restInput);
		}

		txtPassword1.clear();
		txtPassword2.clear();
		txtUsername.clear();
		return flag;

	}

	public String checkInput(String username, String password1, String password2) {
		String result = "";
		if (dbHandler.isUserExist(username)) {
			result += username + " is exist. Please choice other username!\r\n";
		}
		if (!password1.equalsIgnoreCase(password2)) {
			result += "Two passwords are different! Type it agains.";
		}

		return result;
	}

	public boolean isValidInput() {
		boolean flag = false;

		if (txtUsername.validate() && txtPassword1.validate() && txtPassword2.validate()) {
			flag = true;
		}
		btnSignUp.setDisable(!flag);
		return flag;
	}

}
