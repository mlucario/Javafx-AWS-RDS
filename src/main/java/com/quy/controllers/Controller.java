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

import org.controlsfx.control.Notifications;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import com.quy.bizcom.MainApp;
import com.quy.bizcom.SMCController;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
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
import javafx.util.Duration;

public class Controller {
	protected final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
	protected final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
	protected Date date;
	protected java.sql.Date sqlDate;
	protected java.sql.Timestamp sqlTime;
	protected final String PATTERN_MODEL = "(SMC-)\\w+\\s{1}\\w+";
	protected final String PATTERN_BARCODE = "(30N0)\\d{8}";

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

	// Controllers Column
	protected final String MODEL = "model";
	protected final String CONTROLLER_BARCODE = "controller_barcode";
	protected final String CURRENT_STATION = "current_station";
	protected final String TIME_RECEIVED = "time_received";
	protected final String TIME_START_ASSEMBLY = "time_start_assembly";
	protected final String TIME_START_BURN_IN = "time_start_burn_in";
	protected final String TIME_FINISH_BURN_IN = "time_finish_burn_in";
	protected final String TIME_PACKED = "time_packed";
	protected final String TIME_SHIPPED = "time_shipped";
	protected final String IS_RECEIVED = "Is_Received";
	protected final String IS_ASSEMBLED = "Is_Assembled";
	protected final String IS_BURN_IN_PROCESSING = "Is_Burn_In_Processing";
	protected final String IS_BURN_IN_DONE = "Is_Burn_In_Done";
	protected final String IS_PASSED = "Is_Passed";
	protected final String IS_REPAIRED = "Is_Repaired";
	protected final String IS_PACKED = "Is_Packed";
	protected final String IS_SHIPPED = "is_Shipped";

	// Needed Notification
	protected Notifications notification;
	protected Node graphic;

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

	// Notification Builder

	public Notifications notificatioBuilder(Pos pos, Node graphic, String title, String text, double timeToStay) {
		return Notifications.create().title(title).text(text).graphic(graphic).hideAfter(Duration.seconds(timeToStay))
				.position(pos).onAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent e) {
						System.out.println("Notification clicked. No any action needed so far.");

					}
				});

	}

	// Alert Style

	// =============================================
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

	// Comfirm warning
	public boolean warningComfirmAlert(String headerText, String contentText) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation alert");
		alert.setHeaderText(null);
		alert.setHeaderText(headerText);
		alert.setContentText(contentText);
		final Runnable runnable = (Runnable) Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.exclamation");
		if (runnable != null)
			runnable.run();

		alert.getButtonTypes().clear();
		alert.getButtonTypes().add(ButtonType.OK);
		alert.getButtonTypes().add(ButtonType.CLOSE);
		Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
		okButton.setDefaultButton(false);

		Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
		if (result.get() == javafx.scene.control.ButtonType.OK) {
			return true;
		} else {
			return false;
		}
	}
	// =============================================

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
			scene.getStylesheets().add(MainApp.class.getResource("/styles/Styles.css").toExternalForm());
			home.setScene(scene);
			home.initStyle(StageStyle.TRANSPARENT);
			if (isFullScene) {
				home.setX(bounds.getMinX());
				home.setY(bounds.getMinY());
				home.setWidth(bounds.getWidth());
				home.setHeight(bounds.getHeight());
				home.setTitle("SMC Controller Management");
				home.setMinWidth(1100);
				home.setMinHeight(600);

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

	public String getDate() {
		date = new Date();
		sqlDate = new java.sql.Date(date.getTime());
		return formatter.format(sqlDate);
	}

	public String getCurrentTimeStamp() {
		date = new Date();
		sqlTime = new java.sql.Timestamp(date.getTime());
		return sqlTime.toString();
	}

	public boolean isBarcodeValid(String controller_barcode) {
		return controller_barcode.matches(PATTERN_BARCODE);
	}

	public boolean isModelValid(String model) {
		return model.matches(PATTERN_MODEL);
	}

	public String getStringJFXTextField(JFXTextField txt) {
		return txt.getText().trim().toUpperCase();
	}

	public void addBarcodeToTable(ObservableList<SMCController> barcode, String controller_barcode) {
		barcode.add(new SMCController(controller_barcode));
	}

	public String isModelvalid(JFXTextField txtModel) {
		String result = "";

		if (txtModel.validate()) {
			String model = txtModel.getText().toUpperCase().trim();
			if (!isModelValid(model)) {
				result = "Your model is not valid. It should be style as SMC-XX Rx\r\n.";
			}
		} else {
			result = "Controller model is missing! Enter valid model.\r\n";
		}

		return result;
	}

	public String isBarcodevalid(JFXTextField txtBarcode) {
		String result = "";

		if (txtBarcode.validate()) {
			String barcode = txtBarcode.getText().toUpperCase().trim();
			if (!isBarcodeValid(barcode)) {
				result = "Your barcode is not valid. It should be style as 30N0xxxx";
			}
		} else {
			result = "Controller barcode is missing! Enter valid barcode.";
		}

		return result;
	}
}
