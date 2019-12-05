package com.quy.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class AdminDashboardController extends Controller implements Initializable {
	@FXML
	private Text txtUsername;

	@FXML
	private StackPane panelAdmin;

	@FXML
	private JFXButton btnUsers;

	@FXML
	private VBox vBoxPanel;
	private String currentStation;
	private AnchorPane tempPane;

	@FXML
	public void usersManagemenOnClick() {
		switchScence(ADMIN_USERS_MANAGEMENT_SCENE, ADMIN_PANEL_USERS_MANAGEMENT);
	}
	
    @FXML
    void controllersManagementOnlClick() {
    	switchScence(ADMIN_CONTROLLERS_MANAGEMENT_SCENE, ADMIN_CONTROLLER_MANAGEMENT);
    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		currentStation = "";
//		setUsername(SignInController.getInstance().username());
		tempPane = new AnchorPane();

	}

	// Switch to scene when user click on button
	public void switchScence(String scene, String station) {
		if (!currentStation.equalsIgnoreCase(station)) {

			tempPane.getChildren().clear();
			// Load fxml into loadPane
			try {
				tempPane = FXMLLoader.load(getClass().getResource(scene));
				vBoxPanel.getChildren().clear();

				vBoxPanel.getChildren().add(tempPane);

				currentStation = station;
			} catch (IOException e) {

				LOGGER.error("Cannot Run Scene {} ", e.getMessage());

			}
		}

	}
}
