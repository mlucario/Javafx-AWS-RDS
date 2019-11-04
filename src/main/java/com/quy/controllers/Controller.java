package com.quy.controllers;

import java.awt.Toolkit;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
	protected final String USER_DASHBOARD_SCENE = "UserDashboardScene";
	protected final String ADMIN_DASHBOARD_SCENE = "AdminDashboardScene";
	protected final String RECEIVING_STATION_SCENE = "/fxml/ui/users/ReceivingStationScene.fxml";
	protected final String ASSEMBLY_STATION_SCENE = "/fxml/ui/users/AssemblyStationScene.fxml";
	protected final String BURN_IN_STATION_SCENE = "/fxml/ui/users/BurnInStationScene.fxml";

	// Hashing Password
	private static final SecureRandom RAND = new SecureRandom();
	private static final int ITERATIONS = 65536;
	private static final int KEY_LENGTH = 512;
	private static final String ALGORITHM = "PBKDF2WithHmacSHA512";

	// List Resource Path
	protected final String IMAGE_PATH = "/images/";

	// List Stations
	protected final String RECEIVING_STATION = "Receiving Station";
	protected final String ASSEMBLY_STATION = "Assembly Station";
	protected final String BURN_IN_STATION = "Burn In Station";
	protected final String RESULT_STATION = "Set Result Station";
	protected final String REPAIR_STATION = "Repair Station";
	protected final String PACKING_STATION = "Packing Station";
	protected final String SHIPPING_STATION = "Shipping Station";

	public void textFieldFormat(JFXTextField txt, String warning, boolean isUpperCase) {
//		txt.setStyle("-fx-text-inner-color: #8e44ad;");
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

	// Close application
	public void close(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

	// Show warning alert
	public void warningAlert(String msm) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(null);
		alert.setContentText(msm);

		final Runnable runnable = (Runnable) Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.exclamation");
		if (runnable != null)
			runnable.run();

		alert.getButtonTypes().clear();
		alert.getButtonTypes().add(ButtonType.CLOSE);
		Button closeButton = (Button) alert.getDialogPane().lookupButton(ButtonType.CLOSE);
		closeButton.setDefaultButton(false);

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
//			home.setAlwaysOnTop(true);
			home.show();

		} catch (IOException e1) {

			e1.printStackTrace();
		}
	}

	// Password hash
	public Optional<String> generateSalt(final int length) {

		if (length < 1) {
			System.err.println("error in generateSalt: length must be > 0");
			return Optional.empty();
		}

		byte[] salt = new byte[length];
		RAND.nextBytes(salt);

		return Optional.of(Base64.getEncoder().encodeToString(salt));
	}

	public Optional<String> hashPassword(String password, String salt) {

		char[] chars = password.toCharArray();
		byte[] bytes = salt.getBytes();

		PBEKeySpec spec = new PBEKeySpec(chars, bytes, ITERATIONS, KEY_LENGTH);

		Arrays.fill(chars, Character.MIN_VALUE);

		try {
			SecretKeyFactory fac = SecretKeyFactory.getInstance(ALGORITHM);
			byte[] securePassword = fac.generateSecret(spec).getEncoded();
			return Optional.of(Base64.getEncoder().encodeToString(securePassword));

		} catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
			System.err.println("Exception encountered in hashPassword()");
			return Optional.empty();

		} finally {
			spec.clearPassword();
		}
	}

	public boolean verifyPassword(String password, String key, String salt) {
		Optional<String> optEncrypted = hashPassword(password, salt);
		if (!optEncrypted.isPresent())
			return false;
		return optEncrypted.get().equals(key);
	}

	public void setImage(String filename, ImageView imgGuide) {
		Image image = new Image(getClass().getResource(IMAGE_PATH + filename).toString());
		imgGuide.setFitHeight(250);
		imgGuide.setFitWidth(250);
		imgGuide.setImage(image);
	}
}
