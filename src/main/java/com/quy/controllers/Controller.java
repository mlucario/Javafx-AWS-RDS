package com.quy.controllers;

import java.awt.Toolkit;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.Notifications;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.jfoenix.validation.RequiredFieldValidator;
import com.quy.bizcom.MainApp;
import com.quy.bizcom.SMCController;
import com.quy.database.DBHandler;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class Controller {
	protected Stage home;
	protected final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
	protected static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
	protected static final DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("MMddyy");
	protected Date date;
	protected java.sql.Date sqlDate;
	protected java.sql.Timestamp sqlTime;
	protected static final String PATTERN_MODEL = "(SMC-)\\w+\\s{1}\\w+";
	protected static final String PATTERN_BARCODE = "(30N0)\\d{8}";
	protected static final Logger LOGGER = LogManager.getLogger("Controller");
	// List of scene
	protected static final String LOGIN_SCENE = "SignInScene";
	protected static final String SIGNUP_SCENE = "SignUpScene";
	protected static final String USER_DASHBOARD_SCENE = "UserDashboardScene";
	protected static final String ADMIN_DASHBOARD_SCENE = "AdminDashboardScene";
	protected static final String RECEIVING_STATION_SCENE = "/fxml/ui/users/ReceivingStationScene.fxml";
	protected static final String ASSEMBLY_STATION_SCENE = "/fxml/ui/users/AssemblyStationScene.fxml";
	protected static final String BURN_IN_STATION_SCENE = "/fxml/ui/users/BurnInStationScene.fxml";
	protected static final String RESULT_STATION_SCENE = "/fxml/ui/users/ResultStationScene.fxml";
	protected static final String FIRMWARE_UPDATE_STATION_SCENE = "/fxml/ui/users/FirmwareUpdateStation.fxml";
	protected static final String REPAIR_STATION_SCENE = "/fxml/ui/users/RepairStationScene.fxml";
	protected static final String PACKING_STATION_SCENE = "/fxml/ui/users/PackingStation.fxml";
	protected static final String SHIPPING_STATION_SCENE = "/fxml/ui/users/ShippingStation.fxml";
	protected static final String RE_WORK_STATION_SCENE = "/fxml/ui/users/ReWorkScene.fxml";

	// Admin Scene
	protected static final String ADMIN_USERS_MANAGEMENT_SCENE = "/fxml/ui/admin/UsersManagementScene.fxml";
	protected static final String ADMIN_CONTROLLERS_MANAGEMENT_SCENE = "/fxml/ui/admin/ControllerCurrentStatusScene.fxml";

	// Hashing Password
	private static final SecureRandom RAND = new SecureRandom();
	private static final int ITERATIONS = 65536;
	private static final int KEY_LENGTH = 512;
	private static final String ALGORITHM = "PBKDF2WithHmacSHA512";

	// List Resource Path
	protected static final String IMAGE_PATH = "/images/";

	// List Stations
	protected static final String RECEIVING_STATION = "Receiving Station";
	protected static final String ASSEMBLY_STATION = "Assembly Station";
	protected static final String RE_WORK_STATION = "Re_Work Station";
	protected static final String BURN_IN_STATION = "Burn In Station";
	protected static final String RESULT_STATION = "Result Station";
	protected static final String REPAIR_STATION = "Repair Station";
	protected static final String PACKING_STATION = "Packing Station";
	protected static final String SHIPPING_STATION = "Shipping Station";
	protected static final String WAIT_TO_BURN_IN = "Wait_To_Burn_In";
	protected static final String FIRMWARE_UPDATE_STATION = "Firmware Update Station";

	// Admin Panel
	protected static final String ADMIN_PANEL_USERS_MANAGEMENT = "User Management";
	protected static final String ADMIN_CONTROLLER_MANAGEMENT = "Controller Management";

	// Controllers Column
	protected static final String MODEL = "model";
	protected static final String CONTROLLER_BARCODE = "controller_barcode";
	protected static final String CURRENT_STATION = "current_station";
	protected static final String TIME_RECEIVED = "time_received";
	protected static final String TIME_START_ASSEMBLY = "time_start_assembly";
	protected static final String TIME_START_RE_ASSEMBLY = "time_start_re_assembly";
	protected static final String TIME_START_BURN_IN = "time_start_burn_in";
	protected static final String TIME_FINISH_BURN_IN = "time_finish_burn_in";
	protected static final String TIME_PACKED = "time_packed";
	protected static final String TIME_SHIPPED = "time_shipped";
	protected static final String IS_RECEIVED = "Is_Received";
	protected static final String IS_ASSEMBLED = "Is_Assembled";
	protected static final String IS_RE_ASSEMBLED = "Is_Re_Assembled";
	protected static final String IS_BURN_IN_PROCESSING = "Is_Burn_In_Processing";
	protected static final String IS_BURN_IN_DONE = "Is_Burn_In_Done";
	protected static final String IS_PASSED = "Is_Passed";
	protected static final String IS_REPAIRED = "Is_Repaired";
	protected static final String IS_PACKED = "Is_Packed";
	protected static final String IS_SHIPPED = "is_Shipped";

	// History Table
	protected static final String TABLE_HISTORY = "history";
	protected static final String COL_QA_HISTORY = "QA";
	protected static final String COL_TIME_HISTORY = "Time";
	protected static final String COL_STATION_HISTORY = "Station";
	protected static final String COL_SERIAL_NUMBER_HISTORY = "Controller_Serial_Number";
	protected static final String COL_NOTE_HISTORY = "Note";

	// Controler Table
	protected static final String TABLE_CONTROLER = "controllers";
	protected static final String COL_ID_CONTROLER = "ID";
	protected static final String COL_MODEL_CONTROLER = "Model";
	protected static final String COL_SERIAL_NUMBER_CONTROLER = "Serial_Number";
	protected static final String COL_CURRENT_STATION_CONTROLER = "Current_Station";
	protected static final String COL_RECEIVING_TIME_CONTROLER = "Receiving_Time";
	protected static final String COL_ASSEMBLY_TIME_CONTROLER = "Assembly_Time";
	protected static final String COL_RE_ASSEMBLY_TIME_CONTROLER = "Re_Assembly_Time";
	protected static final String COL_BURN_IN_START_TIME_CONTROLER = "Burn_In_Start";
	protected static final String COL_BURN_IN_END_TIME_CONTROLER = "Burn_In_End";
	protected static final String COL_PACKING_TIME_CONTROLER = "Packing_Time";
	protected static final String COL_SHIPPING_TIME_CONTROLER = "Shipping_Time";
	protected static final String COL_REPAIR_TIME_CONTROLER = "Repair_Time";
	protected static final String COL_BURN_IN_RESULT_CONTROLER = "Burn_In_Result";
	protected static final String COL_LOT_ID_CONTROLER = "Lot_ID";
	protected static final String COL_IS_RECEIVIING_CONTROLER = "Is_Received";
	protected static final String COL_IS_ASSEMBLY_DONE_CONTROLER = "Is_Assembly_Done";
	protected static final String COL_IS_RE_ASSEMBLY_DONE_CONTROLER = "Is_Re_Assembly_Done";
	protected static final String COL_IS_BURIN_IN_DONE_CONTROLER = "Is_Burn_In_Done";
	protected static final String COL_IS_BURIN_IN_PROCESSING_CONTROLER = "Is_Burn_In_Processing";
	protected static final String COL_IS_PACKING_DONE_CONTROLER = "Is_Packing_Done";
	protected static final String COL_IS_SHIPPING_DONE_CONTROLER = "Is_Shipping_Done";
	protected static final String COL_IS_REPAIR_DONE_CONTROLER = "Is_Repaired_Done";
	protected static final String COL_IS_PASSED_CONTROLER = "Is_Passed";
	protected static final String COL_SYMPTOM_FAIL_CONTROLER = "Symptoms_Fail";
	protected static final String COL_REWORK_COUNT_CONTROLER = "Re_Work_Count";
	protected static final String COL_FIRMWARE_UPDATE_TIME_CONTROLER = "Firmware_Update_Time";
	protected static final String COL_IS_FIRMWARE_UPDATED_CONTROLER = "Is_Firmware_Updated";

	// User Table
	protected static final String TABLE_USER = "users";
	protected static final String COL_USERNAME_USER = "username";
	protected static final String COL_HASHING_PASSWORD_USER = "hashing_password";
	protected static final String COL_SALT_KEY_USER = "salt_key";
	protected static final String COL_TYPE_USER = "user_type";
	protected static final String COL_IS_ACTIVE_USER = "active";
	protected static final String COL_CREATE_AT_USER = "created_at";

	// Needed Notification
	protected Notifications notification;
	protected Node graphic;

	// Some helper value

	private ExecutorService exec;
	private DBHandler dbHandler;

	// HashMap error code
	protected static final Map<String, String> errorCodes;

	static {
		errorCodes = new HashMap<>();
		errorCodes.put("er001", "Serial Number doesn't exist.");
		errorCodes.put("ar02", "Some article");
	}

	public String errorMessage(String errorCode) {
		return errorCodes.get(errorCode);
	}

	public void textFieldFormat(JFXTextField txt, String warning, boolean isUpperCase) {
//		txt.setStyle("-fx-text-inner-color: #212121;");
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
//		txt.setStyle("-fx-text-inner-color: #212121;");
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

	// Generate LOT_ID by date
	public String generatorLotId() {
		date = new Date();
		sqlTime = new java.sql.Timestamp(date.getTime());
		return dtf2.format(sqlTime.toLocalDateTime());
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
		DialogPane root = alert.getDialogPane();
		root.getStylesheets().add(getClass().getResource("/styles/Styles.css").toExternalForm());
		root.getStyleClass().add("warningPanelDialog");
		Stage dialogStage = new Stage(StageStyle.UTILITY);
		root.getScene().setRoot(new Group());

		root.setPadding(new Insets(10, 0, 10, 0));
		Scene scene = new Scene(root);
		final Runnable runnable = (Runnable) Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.exclamation");

		if (runnable != null)
			runnable.run();

		alert.getButtonTypes().clear();
		alert.getButtonTypes().add(ButtonType.CLOSE);
		Button closeButton = (Button) alert.getDialogPane().lookupButton(ButtonType.CLOSE);
		closeButton.setDefaultButton(false);
		for (ButtonType buttonType : root.getButtonTypes()) {
			ButtonBase button = (ButtonBase) root.lookupButton(buttonType);
			button.setOnAction(evt -> {
				root.setUserData(buttonType);
				dialogStage.close();
			});
		}

		dialogStage.setScene(scene);
		dialogStage.initModality(Modality.APPLICATION_MODAL);
		dialogStage.setAlwaysOnTop(true);
		dialogStage.setResizable(false);
		dialogStage.showAndWait();
//		alert.show();
	}

	// Comfirm warning
	public boolean warningComfirmAlert(String headerText, String contentText, boolean isSetDefault) {
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
		okButton.setDefaultButton(isSetDefault);

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
		home = new Stage();
//		Stage home = new Stage();
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
			LOGGER.error("Cannot generate Scene {} ", e1.getMessage());
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

	public boolean isBarcodeValid(String serialNumber) {
		return serialNumber.matches(PATTERN_BARCODE);
	}

	public boolean isModelValid(String model) {
		return model.matches(PATTERN_MODEL);
	}

	public String getStringJFXTextField(JFXTextField txt) {
		return txt.getText().trim().toUpperCase();
	}

	public void addBarcodeToTable(ObservableList<SMCController> barcode, String serialNumber) {
		barcode.add(new SMCController(serialNumber));
	}
	public void addBarcodeToTable(ObservableList<SMCController> barcode, String serialNumber, String model) {
		barcode.add(new SMCController(serialNumber, model));
	}
	public String isModelValid(JFXTextField txtModel) {
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

	public String isBarcodeValid(JFXTextField txtBarcode) {
		String result = "";

		if (txtBarcode.validate()) {
			String barcode = txtBarcode.getText().toUpperCase().trim();
			if (!isBarcodeValid(barcode)) {
				result = "Your barcode is not valid. It should be style as 30N0xxxx\r\n";
			}
		} else {
			result = "Controller barcode is missing! Enter valid barcode.\r\n";
		}

		return result;
	}

	// Treeview Builder
	@SuppressWarnings("unchecked")
	public void treeviewTableBuilder(JFXTreeTableView<SMCController> treeView, ObservableList<SMCController> barcode,
			String station) {

		JFXTreeTableColumn<SMCController, String> controlBarcode = new JFXTreeTableColumn<>("Serial Number");

		controlBarcode.prefWidthProperty().bind(treeView.widthProperty().multiply(0.97));
		controlBarcode.setResizable(false);
		controlBarcode.setSortable(false);
		controlBarcode.setCellValueFactory((TreeTableColumn.CellDataFeatures<SMCController, String> param) -> {
			if (controlBarcode.validateValue(param))
				return param.getValue().getValue().getControllerBarcode();
			else
				return controlBarcode.getComputedValue(param);
		});

		final TreeItem<SMCController> root = new RecursiveTreeItem<>(barcode, RecursiveTreeObject::getChildren);
		treeView.getColumns().setAll(controlBarcode);
		treeView.setRoot(root);
		treeView.setShowRoot(false);

		ArrayList<String> listBarcodeReceived = new ArrayList<>();

		exec = Executors.newCachedThreadPool(runnable -> {
			Thread t = new Thread(runnable);
			t.setDaemon(true);
			return t;
		});

		Task<List<String>> getAllReceivedTask = new Task<List<String>>() {
			@Override
			public List<String> call() throws Exception {
				dbHandler = new DBHandler();
				ArrayList<String> temp = new ArrayList<>();
				switch (station) {
				case RECEIVING_STATION:
//					temp.addAll(dbHandler.getAllReceived());
					break;
				case ASSEMBLY_STATION:
					temp.addAll(dbHandler.getAllAssemblyDone());
					break;
				case BURN_IN_STATION:
					temp.addAll(dbHandler.getAllReadyToBurn());
					break;
				case FIRMWARE_UPDATE_STATION:
					temp.addAll(dbHandler.getAllFirmwareUpdated());
					break;
				case PACKING_STATION:
					temp.addAll(dbHandler.getAllPacked());
					break;
				default:
					temp.clear();
				}
				return temp;
			}
		};

		getAllReceivedTask.setOnFailed(e -> {
			getAllReceivedTask.getException().printStackTrace();

			warningAlert("Cannot fetch barcode");
		});

		getAllReceivedTask.setOnSucceeded(e -> {

			listBarcodeReceived.addAll(getAllReceivedTask.getValue());

			for (String s : listBarcodeReceived) {

				addBarcodeToTable(barcode, s);
			}
		});

		exec.execute(getAllReceivedTask);

	}
	@SuppressWarnings("unchecked")
	public void treeviewTableBuilder(JFXTreeTableView<SMCController> treeView, ObservableList<SMCController> barcode) {
		
		
		JFXTreeTableColumn<SMCController, Number> sttCol = new JFXTreeTableColumn<>("STT");
		sttCol.prefWidthProperty().bind(treeView.widthProperty().multiply(0.2));
		sttCol.setResizable(false);
		sttCol.setSortable(false);
		sttCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<SMCController, Number> param) -> {
			if (sttCol.validateValue(param))
				return param.getValue().getValue().getSTT();
			else
				return sttCol.getComputedValue(param);
		});
		
		JFXTreeTableColumn<SMCController, String> serialCol = new JFXTreeTableColumn<>("Serial Number");
		serialCol.prefWidthProperty().bind(treeView.widthProperty().multiply(0.4));
		serialCol.setResizable(false);
		serialCol.setSortable(false);
		serialCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<SMCController, String> param) -> {
			if (serialCol.validateValue(param))
				return param.getValue().getValue().getControllerBarcode();
			else
				return serialCol.getComputedValue(param);
		});
		
		JFXTreeTableColumn<SMCController, String> modelCol = new JFXTreeTableColumn<>("Model");
		modelCol.prefWidthProperty().bind(treeView.widthProperty().multiply(0.4));
		modelCol.setResizable(false);
		modelCol.setSortable(false);
		modelCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<SMCController, String> param) -> {
			if (modelCol.validateValue(param))
				return param.getValue().getValue().getModel();
			else
				return modelCol.getComputedValue(param);
		});
		
		
		final TreeItem<SMCController> root = new RecursiveTreeItem<>(barcode, RecursiveTreeObject::getChildren);
		treeView.getColumns().setAll(sttCol,modelCol,serialCol);
		treeView.setRoot(root);
		treeView.setShowRoot(false);
		
		
		
	}

	// Treeview Builder
	@SuppressWarnings("unchecked")
	public void treeviewTableBuilder(JFXTreeTableView<SMCController> treeView, ObservableList<SMCController> barcode,
			List<String> listControllers) {
		JFXTreeTableColumn<SMCController, String> controlBarcode = new JFXTreeTableColumn<>("Serial Number");

		controlBarcode.prefWidthProperty().bind(treeView.widthProperty().multiply(0.97));
		controlBarcode.setResizable(false);
		controlBarcode.setSortable(false);
		controlBarcode.setCellValueFactory((TreeTableColumn.CellDataFeatures<SMCController, String> param) -> {
			if (controlBarcode.validateValue(param))
				return param.getValue().getValue().getControllerBarcode();
			else
				return controlBarcode.getComputedValue(param);
		});

		final TreeItem<SMCController> root = new RecursiveTreeItem<SMCController>(barcode,
				RecursiveTreeObject::getChildren);
		treeView.getColumns().setAll(controlBarcode);
		treeView.setRoot(null);
		treeView.setRoot(root);
		treeView.setShowRoot(false);

		for (String s : listControllers) {
			addBarcodeToTable(barcode, s);
		}

	}
}
