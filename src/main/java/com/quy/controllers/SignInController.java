package com.quy.controllers;

import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import com.quy.database.DBHandler;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class SignInController extends Controller implements Initializable {

	@FXML
	private JFXSpinner txtSpinner;

	@FXML
	private Text txtLoading;

	@FXML
	private Text txtSuccess;

	@FXML
	private JFXTextField txtUsername;

	@FXML
	private JFXPasswordField txtPassword;

	@FXML
	private JFXButton btnSignIn;

	@FXML
	private JFXButton btnSignUp;

	@FXML
	private HBox hboxSignUp;
	@FXML
	private HBox hboxUsername;
	@FXML
	private HBox hboxPassword;

	private DBHandler dbHandler;
//	private double x;
//	private double y;
	private int count;
	private static SignInController instance;
	private String role;

	public SignInController() {
		instance = this;
	}

	public static SignInController getInstance() {
		return instance;
	}

	public String username() {
		return txtUsername.getText();
	}

	public String getRole() {
		return this.role;
	}

	@FXML
	void dragged(MouseEvent event) {
//		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//		stage.setX(event.getScreenX() - x);
//		stage.setY(event.getScreenY() - y);
	}

	@FXML
	void pressed(MouseEvent event) {
//		x = event.getSceneX();
//		y = event.getSceneY();
	}

	/**
	 * Login Method Will check the username with database and get key and password
	 * To verify and compare with verifyPassword method
	 * 
	 * @param event the event
	 */
	@FXML
	void login(ActionEvent event) {
		if (txtUsername.validate() && txtPassword.validate()) {
			String username = txtUsername.getText();
			String password = txtPassword.getText();
			List<String> result = dbHandler.login(username);
			if (result.size() == 4) {
				// Verify input
				if (verifyPassword(password, result.get(1), result.get(0))) {
					// Go to dashboard
					String type = result.get(2);
					this.role = type;
					if (type.equalsIgnoreCase("admin")) {
						goToScene(ADMIN_DASHBOARD_SCENE, btnSignIn, true);
					} else {
						if (result.get(3).equalsIgnoreCase("1")) {
							goToScene(USER_DASHBOARD_SCENE, btnSignIn, true);
						} else {
							warningAlert("Your account was LOCKED. Ask manager to unlock it.");
						}
					}

				} else {
					count++;

					if (count == 3) {
						warningAlert("You failed 3 times. Application will close.");
						close(event);
					} else {
						warningAlert("Your username or password are not corrected. Please Sign In again!.");
						txtPassword.clear();
					}
				}

			} else {
				warningAlert("Your account doesn't exist!");
			}

		} else {
			String error = "";
			if (!txtUsername.validate()) {
				error += "Missing Username\r\n";
			}
			if (!txtPassword.validate()) {
				error += "Missing Password\r\n";
			}
			warningAlert(error);
		}
	}

	@FXML
	void signUpPressed(ActionEvent event) {
		goToScene(SIGNUP_SCENE, btnSignUp, false);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dbHandler = new DBHandler();
		count = 0;
		textFieldFormat(txtUsername, "Username is required!", false);
		textFieldFormat(txtPassword, "Password is required!");
		txtSuccess.setVisible(false);
		hboxPassword.setVisible(false);
		hboxSignUp.setVisible(false);
		hboxUsername.setVisible(false);
		btnSignIn.setVisible(false);
		btnSignIn.setDisable(true);
		// Loading Screen Until Connected To Database

		PauseTransition pt = new PauseTransition();
		pt.setDuration(Duration.millis(800));
		pt.setOnFinished(e -> {

			String content = "There is a problem with the database or connection. Please close application and contact admin.";
			try {
				Connection connection = dbHandler.getConnection();
				if (connection.isValid(1)) {
					loginScreen();

				} else {
					warningAlert("Cannot connect to database!");
				}

			} catch (Exception e1) {
				loadingScreen();
				LOGGER.error("Connection Failed!: {} ", e1.getMessage());
				txtLoading.setText("Database Fail");
				txtLoading.setFill(Color.RED);
				txtLoading.setTextAlignment(TextAlignment.CENTER);
				warningAlert(content);
			}
		});

		// TODO Remove after test done
		txtUsername.setText("smc_bizcom");
		txtPassword.setText("bizcom1171");

		pt.play();
		Platform.runLater(() -> txtUsername.requestFocus());
		txtUsername.setOnAction(e -> txtPassword.requestFocus());

		txtPassword.setOnAction(e -> login(e));

	}

	public boolean isValidInput() {
		boolean result = false;

		if (txtUsername.validate() && txtPassword.validate()) {
			result = true;
		}

		btnSignIn.setDisable(!result);
		return result;

	}

	public void loadingScreen() {
		txtSuccess.setVisible(false);
		hboxPassword.setVisible(false);
		hboxSignUp.setVisible(false);
		hboxUsername.setVisible(false);
		txtSuccess.setVisible(false);
	}

	public void loginScreen() {
		txtLoading.setText("Connected!");
		btnSignIn.setVisible(true);
		hboxPassword.setVisible(true);
		hboxSignUp.setVisible(true);
		hboxUsername.setVisible(true);
		txtSuccess.setVisible(true);
		txtLoading.setVisible(false);
		txtSpinner.setVisible(false);

	}

}
