package com.quy.controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXButton;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class UserDashboardController extends Controller implements Initializable {

	@FXML
	private Text txtUsername;

	@FXML
	private JFXButton btnReceiving;

	@FXML
	private JFXButton btnAssembly;

	@FXML
	private JFXButton btnBurnIn;

	@FXML
	private JFXButton btnResult;

	@FXML
	private JFXButton btnRepair;

	@FXML
	private JFXButton btnPacking;

	@FXML
	private JFXButton btnShipping;

	@FXML
	private JFXButton btnLogout;

	@FXML
	private Text txtTitleStation;

	@FXML
	private StackPane paneIntro;

	@FXML
	private ImageView imgIntro;
	@FXML
	private Text txtDate;

	@FXML
	private Text txtTime;

	@FXML
	private VBox vboxLoad;

	private String currentStation;
//	private double x, y;
	private AnchorPane tempPane;
//	private Executor exec;
//	private DBHandler dbHandler;

	@FXML
	void assemblyOnClick(ActionEvent event) {

		switchScence(ASSEMBLY_STATION_SCENE, ASSEMBLY_STATION);

	}

	@FXML
	void burnInOnClick(ActionEvent event) {
		switchScence(BURN_IN_STATION_SCENE, BURN_IN_STATION);
	}

	@FXML
	void logout(ActionEvent event) {

	}

	@FXML
	void packingOnClick(ActionEvent event) {

	}

	@FXML
	void receivingOnClick(ActionEvent event) {
		switchScence(RECEIVING_STATION_SCENE, RECEIVING_STATION);
	}

	@FXML
	void repairOnClick(ActionEvent event) {

	}

	@FXML
	void resultOnClick(ActionEvent event) {
		switchScence(RESULT_STATION_SCENE, RESULT_STATION);
	}

	@FXML
	void shippingOnClick(ActionEvent event) {

	}

	@FXML
	void dragged(MouseEvent event) {
//		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//		stage.setX(event.getScreenX() - x);
//		stage.setY(event.getScreenY() - y);
	}

	@FXML
	void pressed(MouseEvent event) {
//    	System.out.println("Clicked");
//		x = event.getSceneX();
//		y = event.getSceneY();
	}

	@FXML
	void submit(ActionEvent event) {

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
//		dbHandler = new DBHandler();
		currentStation = "";
		setUsername(SignInController.getInstance().username());

		// Load Image Diagram
		imgIntro.setImage(new Image(getClass().getResource(IMAGE_PATH + "diagram.png").toString()));
		tempPane = new AnchorPane();

		// Setup Date and Time
		// =========================

		txtDate.setText(getDate());
		Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
			LocalTime currentTime = LocalTime.now();
			String value = currentTime.format(dtf);
			txtTime.setText(value);
		}), new KeyFrame(Duration.seconds(1)));
		clock.setCycleCount(Animation.INDEFINITE);
		clock.play();
		// =========================

	}

	private void setUsername(String username) {
		txtUsername.setText("Current user: " + username.toUpperCase());

	}

	public void hideIntroView() {
		imgIntro.setVisible(false);
	}

	// Switch to scene when user click on button
	public void switchScence(String scene, String station) {
		if (!currentStation.equalsIgnoreCase(station)) {
//			System.out.println("Show " + station);
			txtTitleStation.setText(station);
			// Hide diagram and view
			hideIntroView();
			tempPane.getChildren().clear();
			// Load fxml into loadPane
			try {
				tempPane = FXMLLoader.load(getClass().getResource(scene));
				vboxLoad.getChildren().add(tempPane);
//				paneIntro.getChildren().add(tempPane);
				currentStation = station;
			} catch (IOException e) {
				
				warningAlert("Cannot Run Scene. Please Contact Admin");
				e.printStackTrace();
			}
		}

	}
}
