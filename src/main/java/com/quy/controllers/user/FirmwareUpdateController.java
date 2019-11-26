package com.quy.controllers.user;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import com.quy.bizcom.SMCController;
import com.quy.controllers.Controller;
import com.quy.controllers.SignInController;
import com.quy.database.DBHandler;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
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
			String timestamp = getCurrentTimeStamp();
			String model = dbHandler.getStatusDone(COL_MODEL_CONTROLER, serialNumber);
			if (currentLastestStation.equalsIgnoreCase(ASSEMBLY_STATION)) {
				String result = dbHandler.firmwareUpdate(serialNumber, timestamp);
				if (result.equalsIgnoreCase(serialNumber)) {
					if (!listAdded.contains(serialNumber)) {
						addBarcodeToTable(barcode, serialNumber, model);
					}

					String history = dbHandler.addToHistoryRecord(currentUser, FIRMWARE_UPDATE_STATION, timestamp,
							serialNumber, "Firmware updated SN: " + serialNumber, false);
					if (!history.equalsIgnoreCase(serialNumber)) {
						warningAlert(history);
					} else {
						notification = notificatioBuilder(Pos.BOTTOM_RIGHT, graphic, null,
								"Firmware Update Successfully", 2);
						notification.showInformation();
					}
				} else {
					warningAlert(result);
				}

			} else {
				// Check if it is updated
				boolean isFirmwareUpdated = dbHandler.getStatusDone(COL_IS_FIRMWARE_UPDATED_CONTROLER, serialNumber)
						.equalsIgnoreCase("1");
				if (isFirmwareUpdated) {
					warningAlert("Controller (SN: " + serialNumber
							+ ") was updated firmware. Please use re-work to re_update!");
				} else if (!currentLastestStation.equalsIgnoreCase(ASSEMBLY_STATION)) {
					warningAlert(ASSEMBLY_STATION + " is required!");
				} else {
					warningAlert("WRONG STATION");
				}

			}

		} else {
			warningAlert(isValidInput());
		}

		txtControllerBarcode.clear();
		txtControllerBarcode.requestFocus();
		btnSubmit.setDisable(true);
		txtCounter.textProperty().bind(Bindings.format("%d", barcode.size()));

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
		barcode.addAll(dbHandler.getAllFirmwareUpdated());
		treeviewTableBuilder(treeView, barcode);
		txtCounter.textProperty().bind(Bindings.format("%d", barcode.size()));
	}

}
