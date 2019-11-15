package com.quy.controllers.user;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.geometry.Pos;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import com.quy.bizcom.SMCController;
import com.quy.controllers.Controller;
import com.quy.controllers.SignInController;
import com.quy.database.DBHandler;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class FirmwareUpdateController extends Controller implements Initializable {
	@FXML
	private JFXTextField txtControllerBarcode;

	@FXML
	private JFXButton btnSubmit;

	@FXML
	private JFXTreeTableView<SMCController> treeView;

	@FXML
	private Text txtCounter;

	private DBHandler dbHandler;
	private String currentUser = SignInController.getInstance().username();
	private ObservableList<SMCController> barcode = FXCollections.observableArrayList();
	private int count;
	private ArrayList<String> listAdded;

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
				}
			}
		}

		return result;
	}

	@FXML
	void submit() {
		if (isValidInput().isEmpty()) {
			String serialNumber = getStringJFXTextField(txtControllerBarcode);
			String currentLastestStation = dbHandler.getStatusDone(COL_CURRENT_STATION_CONTROLER, serialNumber);
			int reworkCount = Integer.parseInt(dbHandler.getStatusDone(COL_REWORK_COUNT_CONTROLER, serialNumber));
			String timestamp = getCurrentTimeStamp();
			String iD = dbHandler.getStatusDone(COL_ID_CONTROLER, serialNumber);

			// Normal sequence
			if (currentLastestStation.equalsIgnoreCase(ASSEMBLY_STATION)) {
				String result = dbHandler.firmwareUpdate(iD, timestamp, reworkCount, false);
				if (result.equalsIgnoreCase(iD)) {

					count++;
					if (!listAdded.contains(serialNumber)) {
						addBarcodeToTable(barcode, serialNumber);
					}

					String history = dbHandler.addToHistoryRecord(currentUser, FIRMWARE_UPDATE_STATION, timestamp,
							serialNumber, ASSEMBLY_STATION + " to " + FIRMWARE_UPDATE_STATION);
					if (!history.equalsIgnoreCase(serialNumber)) {
						warningAlert(history);
					} else {
						notification = notificatioBuilder(Pos.BOTTOM_RIGHT, graphic, null, "Added Successfully", 2);
						notification.showInformation();
					}
				} else {
					warningAlert(result);
				}

			} else {
				// REWORK PROCESS

				// Duplicate Row and Clear all result
				if (dbHandler.duplicateRow(serialNumber, iD)) {
					dbHandler.updateCurrentStation(iD, "Re_Work");
					String newId = dbHandler.getStatusDone(COL_ID_CONTROLER, serialNumber);
					String result = dbHandler.firmwareUpdate(newId, timestamp, ++reworkCount, true);
					if (result.equalsIgnoreCase(newId)) {
						count++;
						if (!listAdded.contains(serialNumber)) {
							addBarcodeToTable(barcode, serialNumber);
						}

						String history = dbHandler.addToHistoryRecord(currentUser, FIRMWARE_UPDATE_STATION, timestamp,
								serialNumber,
								"Rework: From" + currentLastestStation + " to " + FIRMWARE_UPDATE_STATION);
						if (!history.equalsIgnoreCase(serialNumber)) {
							warningAlert(history);
						} else {
							notification = notificatioBuilder(Pos.BOTTOM_RIGHT, graphic, null,
									"Re_Work Added Successfully", 2);
							notification.showInformation();
						}
					} else {
						warningAlert(result);
					}
				} else {
					warningAlert("Cannot duplicate record!");
				}

			}

		} else {
			warningAlert(isValidInput());
		}

		txtControllerBarcode.clear();
		txtControllerBarcode.requestFocus();
		btnSubmit.setDisable(true);
		txtCounter.setText(count + "");

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dbHandler = new DBHandler();
		btnSubmit.setDisable(true);
		count = 0;
		textFieldFormat(txtControllerBarcode, "Controller barcode is required", true);
		Platform.runLater(() -> txtControllerBarcode.requestFocus());
		txtControllerBarcode.setOnAction(e -> {

			submit();

		});
		listAdded = new ArrayList<>();
		listAdded.addAll(dbHandler.getAllFirmwareUpdated());
		treeviewTableBuilder(treeView, barcode, FIRMWARE_UPDATE_STATION);
		count = dbHandler.getAllAssemblyDone().size();
		txtCounter.setText(count + "");
	}

}
