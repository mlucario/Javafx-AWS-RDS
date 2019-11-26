
package com.quy.controllers.user;

import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import com.quy.bizcom.SMCController;
import com.quy.controllers.Controller;
import com.quy.controllers.SignInController;
import com.quy.database.DBHandler;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class ResultStationController extends Controller implements Initializable {
	@FXML
	private JFXTextField txtHours;
	@FXML
	private Text txtPass;

	@FXML
	private Text txtFail;
	@FXML
	private JFXRadioButton rdPassed;

	@FXML
	private JFXRadioButton rdFail;

	@FXML
	private JFXTextField txtControllerBarcode;

	@FXML
	private JFXTreeTableView<SMCController> treeviewPassed;

	@FXML
	private JFXTreeTableView<SMCController> treeviewFail;

	@FXML
	private ToggleGroup result;

	@FXML
	private JFXTreeTableView<SMCController> treeview;
	@FXML
	private HBox hBoxSymptoms;

	@FXML
	private JFXTextField txtSymptoms;

	@FXML
	private Label lbTimeRemain;

	private Timestamp finishTimeRemain;
	private Timestamp startedTime;

	private DBHandler dbHandler;
	private String currentUser = SignInController.getInstance().username();
	private ObservableList<SMCController> barcodePassed = FXCollections.observableArrayList();
	private ObservableList<SMCController> barcodeFail = FXCollections.observableArrayList();
	private ObservableList<SMCController> barcodeInBurnInSystem = FXCollections.observableArrayList();
	private boolean resultChoosen;
//	private int testingHour;
	private ArrayList<String> currentPassed;
	private ArrayList<String> currentFail;
	private ArrayList<String> inBurnInSystem;
	private int passedCount;
	private int failCount;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		hBoxSymptoms.setVisible(false);
		resultChoosen = true;
//		testingHour = 0;
		passedCount = 0;
		failCount = 0;
		dbHandler = new DBHandler();
		textFieldFormat(txtControllerBarcode, "Controller Barcode is required", true);
		textFieldFormat(txtHours, "Please fill testing hours", true);
		txtControllerBarcode.setDisable(true);
		currentPassed = new ArrayList<>();
		currentFail = new ArrayList<>();
		inBurnInSystem = new ArrayList<>();
		rdPassed.setToggleGroup(result);
		rdFail.setToggleGroup(result);
		rdPassed.setUserData("PASSED");
		rdFail.setUserData("FAIL");
		String startedTimeControllers = dbHandler.getCurrentStartedBuring();
		result.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				if (result.getSelectedToggle() != null) {
					resultChoosen = result.getSelectedToggle().getUserData().toString().equalsIgnoreCase("PASSED");
					if (!resultChoosen) {
						hBoxSymptoms.setVisible(true);
					} else {
						hBoxSymptoms.setVisible(false);
					}
					boolean temp = txtHours.validate() && isNumeric(txtHours.getText());
					txtControllerBarcode.setDisable(!temp);
					if (!txtControllerBarcode.isDisable()) {
						Platform.runLater(() -> txtControllerBarcode.requestFocus());
					}
				} else {
					txtControllerBarcode.setDisable(true);
					warningAlert("PLEASE SELECT PASSED or FAIL options");
				}

			}
		});
		if (!startedTimeControllers.isEmpty()) {
			startedTime = Timestamp.valueOf(startedTimeControllers);

			finishTimeRemain = Timestamp.from(startedTime.toInstant().plus(1, ChronoUnit.DAYS));

			// Set Countdown TIme

			Thread td = new Thread(new Runnable() {

				@Override
				public void run() {
					Date date = new Date();
					Date finishedDate = new Date(finishTimeRemain.getTime());
					DateFormat sdf = new SimpleDateFormat("HH:mm:ss");

					try {
						while (finishedDate.getTime() - date.getTime() > 1) {
							lbTimeRemain.setText(sdf.format(finishedDate.getTime() - date.getTime()));
							Thread.sleep(1000);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});
			td.start();
		}

		txtControllerBarcode.setOnAction(e -> {
			resultAction(e);
		});
		inBurnInSystem.addAll(dbHandler.getAllBurning());
//		treeviewTableBuilder(treeviewPassed, barcodePassed, currentPassed);
//		treeviewTableBuilder(treeviewFail, barcodeFail, currentFail);
//		treeviewTableBuilder(treeview, barcodeInBurnInSystem, inBurnInSystem);

	}

	@FXML
	public String isValidInput() {
		String resultInput = "";

		resultInput = isBarcodeValid(txtControllerBarcode);

		if (resultInput.isEmpty()) {
			String serialNumber = getStringJFXTextField(txtControllerBarcode);
			String currentStatus = dbHandler.getStatusDone(COL_CURRENT_STATION_CONTROLER, serialNumber);
			if (!dbHandler.isBarcodeExist(serialNumber)) {
				resultInput = "\r\n Serial Number does not exist!";
			} else {
				if (currentStatus.equalsIgnoreCase(SHIPPING_STATION)) {
					resultInput = "\r\n Controller has been shipped. Please ask manager intermediately.";
				} else {
					switch (currentStatus) {
					case ASSEMBLY_STATION:
					case WAIT_TO_BURN_IN:
					case RECEIVING_STATION:
					case FIRMWARE_UPDATE_STATION:
					case PACKING_STATION:
					case REPAIR_STATION:
						resultInput = "\r\n Controller need to burn in first!";
						break;
					case RESULT_STATION:
						resultInput = "\r\n Controller has been set RESULT!";
						break;
					default:
						LOGGER.info("There is nothing here.");
					}
				}
			}
		}
		return resultInput;
	}
	// TODO How to delete one row in TreeViewTable faster??

	public void resetBurnInList(String serialNumber) {

		for (SMCController smc : barcodeInBurnInSystem) {
			if (smc.getControllerBarcode().getValue().equalsIgnoreCase(serialNumber)) {
				barcodeInBurnInSystem.remove(smc);
				break;
			}
		}

	}

	@FXML
	void resultAction(ActionEvent event) {

		if (isValidInput().isEmpty()) {

			String timeStamp = getCurrentTimeStamp();
			resultChoosen = result.getSelectedToggle().getUserData().toString().equalsIgnoreCase("PASSED");
			String serialNumber = getStringJFXTextField(txtControllerBarcode);
			String currentStation = dbHandler.getStatusDone(COL_CURRENT_STATION_CONTROLER, serialNumber);
			String resultStatus = dbHandler.getStatusDone(COL_BURN_IN_RESULT_CONTROLER, serialNumber);
			if (currentStation.equalsIgnoreCase(BURN_IN_STATION)
					&& resultStatus.equalsIgnoreCase("Burn In Processing")) {
				// RESULT PASSED
				if (resultChoosen) {
					String resultAc = dbHandler.setResult(serialNumber, timeStamp, true, "No Trouble Found.");
					if (resultAc.equals(serialNumber)) {
						passedCount++;
						txtPass.setText("PASSED: " + passedCount + "");
						addBarcodeToTable(barcodePassed, serialNumber);
						String history = dbHandler.addToHistoryRecord(currentUser, RESULT_STATION, timeStamp,
								serialNumber, "Marked Passed!", false);
						if (!history.equalsIgnoreCase(serialNumber)) {
							warningAlert(history);
						} else {
							notification = notificatioBuilder(Pos.BOTTOM_RIGHT, graphic, null,
									"Set Result PASSED Successfully", 2);
							notification.showInformation();
							resetBurnInList(serialNumber);
						}
					} else {
						warningAlert(resultAc);
					}
				}
				// RESULT FAIL
				else {
					// Add auto completed at here
					if (!txtSymptoms.getText().isEmpty()) {
						String resultAc = dbHandler.setResult(serialNumber, timeStamp, false, txtSymptoms.getText());
						if (resultAc.equals(serialNumber)) {
							failCount++;
							txtFail.setText("FAIL: " + failCount + "");
							addBarcodeToTable(barcodeFail, serialNumber);
							String history = dbHandler.addToHistoryRecord(currentUser, RESULT_STATION, timeStamp,
									serialNumber, "Marked Fail: " + txtSymptoms.getText(), false);
							if (!history.equalsIgnoreCase(serialNumber)) {
								warningAlert(history);
							} else {
								notification = notificatioBuilder(Pos.BOTTOM_RIGHT, graphic, null,
										"Set Result FAIL Successfully", 2);
								notification.showInformation();
								resetBurnInList(serialNumber);
							}
						} else {
							warningAlert(resultAc);
						}
					} else {
						warningAlert("PLEASE ENTER SYMPTOMS");
						txtSymptoms.clear();
						txtSymptoms.requestFocus();
					}
				}

			} else {
				warningAlert("WRONG STATION!!!");
			}
		} else {
			warningAlert(isValidInput());
		}

		txtControllerBarcode.clear();
		txtControllerBarcode.requestFocus();
	}

	public boolean isNumeric(String strNum) {
		return strNum.matches("-?\\d+(\\.\\d+)?");
	}

	@FXML
	void inputValid() {
		boolean resultH = false;
		if (txtHours.validate() && isNumeric(txtHours.getText())) {
			resultH = true && result.getSelectedToggle() != null;
		}
		txtControllerBarcode.setDisable(!resultH);
		if (!txtControllerBarcode.isDisable()) {
			Platform.runLater(() -> txtControllerBarcode.requestFocus());
		}
	}

}
