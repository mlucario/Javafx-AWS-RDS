package com.quy.controllers.user;

import java.net.URL;
import java.util.ResourceBundle;

import com.quy.bizcom.SMCController;
import com.quy.controllers.Controller;
import com.quy.controllers.SignInController;
import com.quy.database.DBHandler;

import javafx.fxml.Initializable;
import javafx.geometry.Pos;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

public class PackingStationController extends Controller implements Initializable {
	@FXML
	private JFXTreeTableView<SMCController> treeView;

	@FXML
	private JFXTextField txtControllerBarcode;

	@FXML
	private JFXButton btnSubmit;
	private DBHandler dbHandler;
	private String currentUser = SignInController.getInstance().username();
	private ObservableList<SMCController> barcode = FXCollections.observableArrayList();

	@FXML
	void keyPressValidate() {
		boolean result = false;
		if (txtControllerBarcode.validate()) {
			result = true;
		}
		btnSubmit.setDisable(!result);
	}

	public String isValidInput() {
		String result = "";
		result = isBarcodeValid(txtControllerBarcode);
		if (result.isEmpty()) {
			String serialNumber = getStringJFXTextField(txtControllerBarcode);
			String currentStatus = dbHandler.getStatusDone(COL_CURRENT_STATION_CONTROLER, serialNumber);
			if (!dbHandler.isBarcodeExist(serialNumber)) {
				result = "\r\n Serial Number does not exist!";
			} else {
				if (currentStatus.equalsIgnoreCase(SHIPPING_STATION)) {
					result = "\r\n Controller has been shipped. Please ask manager intermediately.";
				} else {
					switch (currentStatus) {

					case PACKING_STATION:
						result = "\r\n Controller has been PACKED!";
						break;
					case RECEIVING_STATION:
					case ASSEMBLY_STATION:
					case FIRMWARE_UPDATE_STATION:
						result = "\r\n Controller is not ready to pack. Check with manager";
						break;
					case REPAIR_STATION:
					case "UNREPAIRABLE":
					case BURN_IN_STATION:
						result = "";
						break;
					default:
						LOGGER.info("There is nothing here.");
					}
				}
			}
		}

		return result;
	}

	@FXML
	void submit() {
		if (isValidInput().isEmpty()) {
			// Check if ready to pack or not
			String serialNumber = getStringJFXTextField(txtControllerBarcode);
			String currentLastestStation = dbHandler.getStatusDone(COL_CURRENT_STATION_CONTROLER, serialNumber);
			String timestamp = getCurrentTimeStamp();
			String iD = dbHandler.getStatusDone(COL_ID_CONTROLER, serialNumber);
			boolean isPass = dbHandler.getStatusDone(COL_IS_PASSED_CONTROLER, serialNumber).equalsIgnoreCase("1");
			// Case1: Unrepairable
			if (currentLastestStation.equalsIgnoreCase("UNREPAIRABLE")) {
				String result = dbHandler.packed(iD, timestamp);
				if (result.equalsIgnoreCase(iD)) {
					addBarcodeToTable(barcode, serialNumber);
					String history = dbHandler.addToHistoryRecord(currentUser, PACKING_STATION, timestamp, serialNumber,
							"Package is ready!");
					if (!history.equalsIgnoreCase(serialNumber)) {
						warningAlert(history);
					} else {
						notificationx();
					}
				} else {
					warningAlert(result);
				}
			} else if (currentLastestStation.equalsIgnoreCase(RESULT_STATION)) {
				if (isPass) {
					String result = dbHandler.packed(iD, timestamp);
					if (result.equalsIgnoreCase(iD)) {
						addBarcodeToTable(barcode, serialNumber);
						String history = dbHandler.addToHistoryRecord(currentUser, PACKING_STATION, timestamp,
								serialNumber, "Package is ready!");
						if (!history.equalsIgnoreCase(serialNumber)) {
							warningAlert(history);
						} else {
							notificationx();
						}
					} else {
						warningAlert(result);
					}
				} else {
					warningAlert("Controller is fail. Please verify with REAPAIR STATION first.");
				}
			}
		} else {
			warningAlert(isValidInput());

		}
		txtControllerBarcode.clear();
		txtControllerBarcode.requestFocus();
	}

	public void notificationx() {
		notification = notificatioBuilder(Pos.BOTTOM_RIGHT, graphic, null, "Pack Successfully", 2);
		notification.showInformation();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dbHandler = new DBHandler();
		btnSubmit.setDisable(true);
		textFieldFormat(txtControllerBarcode, "Controller barcode is required", true);
		Platform.runLater(() -> txtControllerBarcode.requestFocus());
		txtControllerBarcode.setOnAction(e -> {

			submit();

		});

		treeviewTableBuilder(treeView, barcode, PACKING_STATION);

	}

}
