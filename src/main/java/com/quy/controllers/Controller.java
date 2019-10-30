package com.quy.controllers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextFormatter;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Controller {
	protected final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
	protected final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
	protected final Date date = new Date();
	protected final java.sql.Date sqlDate = new java.sql.Date(date.getTime());
	protected final java.sql.Timestamp sqlTime = new java.sql.Timestamp(date.getTime());

	// List of scene
	protected final String LOGIN_SCENE = "SignInScene";
	protected final String SIGNUP_SCENE = "SignUpScene";
	protected final String DASHBOARD_SCENE = "DashboardScene";

	public void textFieldFormat(JFXTextField txt, String warning, boolean isUpperCase) {
		txt.setStyle("-fx-text-inner-color: #8e44ad;");
		if (isUpperCase) {
			// This code will change all text to Upper Case
			txt.setTextFormatter(new TextFormatter<>((change) -> {
				change.setText(change.getText().toUpperCase());
				return change;
			}));
		}

		RequiredFieldValidator validator = new RequiredFieldValidator();
		validator.setMessage(warning);
		txt.getValidators().add(validator);
		txt.focusedProperty().addListener((o, oldVal, newVal) -> {
			if (!newVal)
				txt.validate();
		});
	}

	public void textFieldFormat(JFXPasswordField txt, String warning) {
		txt.setStyle("-fx-text-inner-color: #8e44ad;");
		RequiredFieldValidator validator = new RequiredFieldValidator();
		validator.setMessage(warning);
		txt.getValidators().add(validator);
		txt.focusedProperty().addListener((o, oldVal, newVal) -> {
			if (!newVal)
				txt.validate();
		});
	}

	public void warningAlert(String msm) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(null);
		alert.setContentText(msm);
		alert.show();
	}

	// GotoScene method will take user go to other scene
	// buttonOfCurentScene a button of current scene
	// sceneName name of file fxml we want to go
	public void goToScene(String sceneName, JFXButton buttonOfCurentScene, boolean isFullScene) {
		buttonOfCurentScene.getScene().getWindow().hide();

		Stage home = new Stage();
		try {

			Screen screen = Screen.getPrimary();
			Rectangle2D bounds = screen.getVisualBounds();

			Parent root = FXMLLoader.load(getClass().getResource("/FXML/" + sceneName + ".fxml"));
			Scene scene = new Scene(root);
			home.setScene(scene);
			home.initStyle(StageStyle.TRANSPARENT);
			if (isFullScene) {
				home.setX(bounds.getMinX());
				home.setY(bounds.getMinY());
				home.setWidth(bounds.getWidth());
				home.setHeight(bounds.getHeight());
			}
			home.show();

		} catch (IOException e1) {

			e1.printStackTrace();
		}
	}
}
