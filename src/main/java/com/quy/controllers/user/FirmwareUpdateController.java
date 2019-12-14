package com.quy.controllers.user;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
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

	@FXML
	private JFXTextArea txtNote;
	@FXML
	private JFXTextField txtSN;
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
					case RECEIVING_STATION:
						result = "Controller doesn't pass Assembly Station!. Please Add to Assembly Station now.";
						break;
					case ASSEMBLY_STATION:
					case FIRMWARE_UPDATE_STATION:
					case WAIT_TO_BURN_IN:
					case RE_WORK_STATION:
					case BURN_IN_STATION:
					case RESULT_STATION:
					case REPAIR_STATION:
					case PACKING_STATION:
						result = "";
						break;
					default:
						result = "Something was wrong!";
						break;
					}
				}
			}
		}

		return result;
	}

	@FXML
	void submit() {
		if (isValidInput().isEmpty()) {
			String serialNumber = getStringJFXTextField(txtControllerBarcode);
			String timestamp = getCurrentTimeStamp();
			String model = dbHandler.getStatusDone(COL_MODEL_CONTROLER, serialNumber);
			int count = Integer.parseInt(dbHandler.getStatusDone(COL_FIRMWARE_UPDATED_COUNT_CONTROLER, serialNumber));
			String result = dbHandler.firmwareUpdate(serialNumber, timestamp, count);			
			if (result.equalsIgnoreCase(serialNumber)) {
				addBarcodeToTable(barcode, serialNumber, model);
				String history = dbHandler.addToHistoryRecord(currentUser, FIRMWARE_UPDATE_STATION, timestamp,
						serialNumber, "Firmware updated SN: " + serialNumber + ". " + txtNote.getText());
				if (!history.equalsIgnoreCase(serialNumber)) {
					warningAlert(history);
				} else {
					notification = notificatioBuilder(Pos.BOTTOM_RIGHT, graphic, null, "Firmware Update Successfully",
							2);
					notification.showInformation();
				}
			} else {
				warningAlert(result);
			}

		} else

		{
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
		txtControllerBarcode.setOnAction(e -> submit()

		);
		barcode.addAll(dbHandler.getAllFirmwareUpdated());
		treeviewTableBuilder(treeView, barcode);
		txtCounter.textProperty().bind(Bindings.format("%d", barcode.size()));
		txtSN.textProperty().addListener((o, oldVal, newVal) -> {
			if (!oldVal.equalsIgnoreCase(newVal)) {
				treeView.setPredicate(controller -> {
					final SMCController aController = controller.getValue();
					return aController.getSerialNumber().getValue().contains(newVal);
				});
			}
		});
	}

}
