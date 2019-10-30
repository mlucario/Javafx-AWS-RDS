package com.quy.controllers;

import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
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
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
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

	private DBHandler dbHandler;
	private double x, y;
	private int count;

	private static SignInController instance;

	public SignInController() {
		instance = this;
	}

	public static SignInController getInstance() {
		return instance;
	}

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
	void login(ActionEvent event) {
		if (txtUsername.validate() && txtPassword.validate()) {
			String username = txtUsername.getText();
			String password = txtPassword.getText();
			ArrayList<String> result = dbHandler.getPasswordAndSaltKey(username);
			if (result.size() == 3) {
				if (verifyPassword(password, result.get(1), result.get(0))) {
					// Go to dashboard
					String type = result.get(2);
					if(type.equalsIgnoreCase("admin")) {
						goToScene(ADMIN_DASHBOARD_SCENE, btnSignIn, true);	
					}else {
						goToScene(USER_DASHBOARD_SCENE, btnSignIn, true);	
					}
					

				} else {
					count++;
					System.out.println("Fail Sign In");

					if (count == 3) {
						warningAlert("You failed 3 times. Application will close.");
						close(event);
					} else {
						warningAlert("Your username or password are not corrected. Please Sign In again!.");
						txtPassword.setText("");
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

		// Loading Screen Until Connected To Database

		PauseTransition pt = new PauseTransition();
		pt.setDuration(Duration.seconds(1));
		pt.setOnFinished(e -> {

			String content = "There is a problem with the database or connection. Please close application and contact admin.";
			try {
				Connection connection = dbHandler.getConnectionAWS();

				if (connection.isValid(1)) {
					loginScreen();

				} else {
					warningAlert("Cannot connect to database!");
				}

			} catch (Exception e1) {
				loadingScreen();
				e1.printStackTrace();
				txtLoading.setText("Database Fail");
				txtLoading.setFill(Color.RED);
				txtLoading.setTextAlignment(TextAlignment.CENTER);
				warningAlert(content);
			}
		});
		pt.play();
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				txtUsername.requestFocus();
			}
		});
		txtUsername.setOnAction(e -> {
			txtPassword.requestFocus();
		});

		txtPassword.setOnAction(e -> {
			login(e);
		});

		
	}

	public void loadingScreen() {
		txtUsername.setDisable(true);
		txtPassword.setDisable(true);
		btnSignIn.setVisible(false);
		btnSignUp.setDisable(true);
		txtSuccess.setDisable(true);

	}

	public void loginScreen() {
		txtLoading.setText("Connected!");
		txtUsername.setDisable(false);
		txtPassword.setDisable(false);
		btnSignIn.setDisable(false);
		btnSignUp.setDisable(false);
		txtSuccess.setVisible(true);
		txtLoading.setVisible(false);
		txtSpinner.setVisible(false);
		
		txtUsername.setText("a1956");
		txtPassword.setText("1234567Aa");

	}
}
