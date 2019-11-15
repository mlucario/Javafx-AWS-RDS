package com.quy.controllers.user;

import java.net.URL;
import java.util.ResourceBundle;

import com.quy.controllers.Controller;
import com.quy.controllers.SignInController;
import com.quy.database.DBHandler;

import javafx.fxml.Initializable;
import javafx.geometry.Pos;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import javafx.application.Platform;

import javafx.fxml.FXML;

public class RepairStationController extends Controller implements Initializable {
	@FXML
	private JFXTextField txtControllerBarcode;

	@FXML
	private JFXTextArea txtRepairStep;

	@FXML
	private JFXButton btnRepair;

	@FXML
	private JFXButton btnUnrepairable;

	@FXML
	private JFXTextArea txtSymptoms;

	private DBHandler dbHandler;
	private String currentUser = SignInController.getInstance().username();

	@FXML
	void canNotRepair() {

		String serialNumber = getStringJFXTextField(txtControllerBarcode);
		String timestamp = getCurrentTimeStamp();
		String iD = dbHandler.getStatusDone(COL_ID_CONTROLER, serialNumber);
		String result = dbHandler.unRepairable(iD, timestamp);
		if (result.equalsIgnoreCase(iD)) {
			notification = notificatioBuilder(Pos.BOTTOM_RIGHT, graphic, null,
					"Update Unrepairable Staion Successfully", 2);
			notification.showInformation();
		} else {
			warningAlert(result);
		}

		txtControllerBarcode.clear();
		txtControllerBarcode.requestFocus();

	}

	@FXML
	void keyPressValidate() {
		boolean result = false;
		if (txtControllerBarcode.validate()) {
			result = true;
		}
		btnRepair.setDisable(!result);
		btnUnrepairable.setDisable(!result);
	}

	@FXML
	void repairAction() {
		if (!isValidInput().isEmpty()) {
			warningAlert(isValidInput());
			txtRepairStep.requestFocus();
		} else {

			String serialNumber = getStringJFXTextField(txtControllerBarcode);
			int reworkCount = Integer.parseInt(dbHandler.getStatusDone(COL_REWORK_COUNT_CONTROLER, serialNumber));
			String timestamp = getCurrentTimeStamp();
			String iD = dbHandler.getStatusDone(COL_ID_CONTROLER, serialNumber);

			if (dbHandler.duplicateRow(serialNumber, iD)) {
				dbHandler.updateCurrentStation(iD, "Repaired");
				String newId = dbHandler.getStatusDone(COL_ID_CONTROLER, serialNumber);

				String result = dbHandler.repairController(newId, timestamp, ++reworkCount);
				if (result.equalsIgnoreCase(newId)) {

					String history = dbHandler.addToHistoryRecord(currentUser, REPAIR_STATION, timestamp, serialNumber,
							"REPAIR: " + txtRepairStep.getText());
					if (!history.equalsIgnoreCase(serialNumber)) {
						warningAlert(history);
					} else {
						notification = notificatioBuilder(Pos.BOTTOM_RIGHT, graphic, null, "Repaired Successfully", 2);
						notification.showInformation();
					}
				} else {
					warningAlert(result);
				}
			} else {
				warningAlert("Cannot duplicate record!");
			}
		}

		txtControllerBarcode.clear();
		txtControllerBarcode.requestFocus();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dbHandler = new DBHandler();
		btnRepair.setDisable(true);
		btnUnrepairable.setDisable(true);
		textFieldFormat(txtControllerBarcode, "Controller barcode is required", true);
		Platform.runLater(() -> txtControllerBarcode.requestFocus());
		txtControllerBarcode.setOnAction(e -> {
			if (isValidInput().isEmpty()) {
				String serialNumber = getStringJFXTextField(txtControllerBarcode);
				String symptoms = dbHandler.getStatusDone(COL_SYMPTOM_FAIL_CONTROLER, serialNumber);
				txtSymptoms.setText(symptoms);
				btnRepair.setDisable(false);
				btnUnrepairable.setDisable(false);
			} else {
				warningAlert("Serial Number is Invalid!\r\n\r\n" + isValidInput());
				txtControllerBarcode.clear();
				txtControllerBarcode.requestFocus();
			}

		});

		
	}

	public String isValidInput() {
		String result = "";
		result = isBarcodeValid(txtControllerBarcode);
		if (result.isEmpty()) {
			String serialNumber = getStringJFXTextField(txtControllerBarcode);
			String currentStatus = dbHandler.getStatusDone(COL_CURRENT_STATION_CONTROLER, serialNumber);
			boolean currentResultStatus = dbHandler.getStatusDone(COL_IS_PASSED_CONTROLER, serialNumber)
					.equalsIgnoreCase("1");
			if (!dbHandler.isBarcodeExist(serialNumber)) {
				result = "\r\n Serial Number does not exist!";
			} else {
				if (currentStatus.equalsIgnoreCase(SHIPPING_STATION)) {
					result = "\r\n Controller has been shipped. Please ask manager intermediately.";
				} else {
					switch (currentStatus) {
					case RE_ASSEMBLY_STATION:
					case RECEIVING_STATION:
					case PACKING_STATION:
					case ASSEMBLY_STATION:
					case BURN_IN_STATION:
					case WAIT_TO_BURN_IN:
						result = "\r\n Controller does not set result yet!";
						break;
					case RESULT_STATION:
						result = "";
						break;
					default:
						LOGGER.info("There is nothing here.");
					}
				}
			}
			if (currentResultStatus) {
				result += "Controller is PASSED. DON\"T NEED TO REPAIR";
			}
		}

		return result;
	}
}
