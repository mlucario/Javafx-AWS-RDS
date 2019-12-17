package com.quy.controllers.user;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.quy.controllers.Controller;
import com.quy.controllers.SignInController;
import com.quy.database.DBHandler;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;

public class ReWorkController extends Controller implements Initializable {
	@FXML
	private JFXTextField txtControllerBarcode;

	@FXML
	private JFXButton btnRework;
	@FXML
	private JFXTextArea txtInfo;

	@FXML
	private JFXTextField txtMins;
	private DBHandler dbHandler;
	protected String currentUser = SignInController.getInstance().username();
	private double time;

	@FXML
	void keyPressValidate() {
		boolean flag = false;
		if (txtControllerBarcode.validate()) {
			flag = true;
		}
		btnRework.setDisable(!flag);
	}

	@FXML
	void reWorkAction() {
		if (isValidInput().isEmpty()) {
			String serialNumber = getStringJFXTextField(txtControllerBarcode);
//			String currentLastestStation = dbHandler.getStatusDone(COL_CURRENT_STATION_CONTROLER, serialNumber);
			int reworkCount = Integer.parseInt(dbHandler.getStatusDone(COL_REWORK_COUNT_CONTROLER, serialNumber));
			String timestamp = getCurrentTimeStamp();
			String reason = txtInfo.getText();
			String result = dbHandler.rework(serialNumber, timestamp, ++reworkCount,
					Integer.parseInt(txtMins.getText()), reason);
			if (result.equalsIgnoreCase(serialNumber)) {
				String history = dbHandler.addToHistoryRecord(currentUser, RE_WORK_STATION, timestamp, serialNumber,
						reason, false);
				if (!history.equalsIgnoreCase(serialNumber)) {
					warningAlert(history);
				} else {
					notification = notificatioBuilder(Pos.BOTTOM_RIGHT, graphic, null, "Rework Successfully", 2);
					notification.showInformation();
				}
			} else {
				warningAlert(result);
			}

		} else {
			warningAlert(isValidInput());
		}

		txtControllerBarcode.clear();
		txtControllerBarcode.requestFocus();
		btnRework.setDisable(true);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		dbHandler = new DBHandler();
		time = 0;
		btnRework.setDisable(true);
		textFieldFormat(txtControllerBarcode, "Serial Number is required", true);
		textFieldFormat(txtMins, "Time is required", true);

		btnRework.setOnAction(e -> reWorkAction());
		Platform.runLater(() -> txtControllerBarcode.requestFocus());
		txtMins.textProperty().addListener((value, oldval, newVal) -> {
			if (!oldval.equalsIgnoreCase(newVal) && newVal != null) {

				try {
					double time = Double.parseDouble(newVal);
				} catch (NumberFormatException e) {
					warningAlert("Your input is not a number!");
				}

			}
		});
	}

	public String isValidInput() {
		String result = "";
		result = isBarcodeValid(txtControllerBarcode);
		if (result.isEmpty()) {
			String serialNumber = getStringJFXTextField(txtControllerBarcode);
			if (dbHandler.isBarcodeExist(serialNumber)) {
				String currentStatus = dbHandler.getStatusDone(COL_CURRENT_STATION_CONTROLER, serialNumber);
				switch (currentStatus) {

				case SHIPPING_STATION:
					result = "Go to Receiving Station!";
					break;
				case RECEIVING_STATION:
					result = "Controller was received. Go to other staions.";
					break;
				case FIRMWARE_UPDATE_STATION:
				case REPAIR_STATION:
				case WAIT_TO_BURN_IN:
				case BURN_IN_STATION:
				case PACKING_STATION:
				case RESULT_STATION:
				case "Unrepairable":
				case ASSEMBLY_STATION:
				case RE_WORK_STATION:
					result = "";
					break;
				default:
					result = "Special Case. Ask manager for help!";
					LOGGER.info("NF");
				}

			} else {
				result = "This controller is never worked before.";
			}
		}

		if (txtInfo.getText().isEmpty()) {
			result = "Reason and Instruction are required!";
		}

		if (txtMins.getText().isEmpty()) {
			result = "Time Work is required! Please enter a number!.";
		}
		return result;

	}

}
