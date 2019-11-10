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
	private boolean inputUsername;
	private boolean inputPassword;
	private boolean inputConfirmPassword;
	private boolean isValidInput = inputUsername && inputPassword && inputConfirmPassword;

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
		inputUsername = true;
		inputPassword = true;
		inputConfirmPassword = true;
		btnSignUp.setDisable(true);
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
		try {
			rd = SecureRandom.getInstanceStrong();

			if (isValidInput) {
				String username = txtUsername.getText().toLowerCase();
				String password1 = txtPassword1.getText();
				String time = formatter.format(sqlDate);

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

			} else {
				warningAlert("Cannot create your account. Inputs are invalid.");
				LOGGER.error("Cannot create acccount");
			}
		} catch (NoSuchAlgorithmException e) {

			LOGGER.error("Cannot create random object : %s", e.getMessage());
		}

		txtPassword1.clear();
		txtPassword2.clear();
		txtUsername.clear();

	}

	public boolean isInputUsernameValid() {
		boolean flag = false;
		if (txtUsername.validate()) {
			if (dbHandler.isUserExist(txtUsername.getText().toLowerCase())) {
				warningAlert("Username is exist!. Enter other username");
				txtUsername.clear();
				txtUsername.requestFocus();
			} else {
				flag = true;

			}
		} else {
			warningAlert("Please enter Username to continute");
		}
		this.inputUsername = flag;
		btnSignUp.setDisable(!isValidInput);
		return flag;
	}

	public boolean isPasswordValid() {

		boolean flag = false;
		// TODO Can implement safe password at here
		if (txtPassword1.validate()) {
			flag = true;

		}
		this.inputPassword = flag;
		btnSignUp.setDisable(!isValidInput);
		return flag;
	}

	public boolean isConfirmPasswordValid() {
		boolean flag = false;
		if (txtPassword2.validate() && isPasswordValid()) {

			if (txtPassword1.getText().equals(txtPassword2.getText())) {
				flag = true;
			} else {
				warningAlert("Two Password are different. Enter valid Confirm Password");
				txtPassword2.clear();
				txtPassword2.requestFocus();
			}
		}
		this.inputConfirmPassword = flag;
		btnSignUp.setDisable(!isValidInput);
		return flag;
	}

}
