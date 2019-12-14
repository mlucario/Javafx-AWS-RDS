
package com.quy.controllers.user;

import java.net.URL;
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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.text.Text;

public class ReceivingController extends Controller implements Initializable {

	@FXML
	private Text txtCounter;

	@FXML
	private JFXTextField txtModel;

	@FXML
	private JFXTextField txtBoxBarcode;

	@FXML
	private JFXTextField txtControllerBarcode;
	@FXML
	private JFXButton btnSubmit;

	@FXML
	private JFXTreeTableView<SMCController> treeView;

	@FXML
	private JFXTextField txtSN;
	private DBHandler dbHandler;
	protected String currentUser = SignInController.getInstance().username();
	private ObservableList<SMCController> barcode = FXCollections.observableArrayList();
	private final String SERIAL_EXIST = "Serial number exist!";

	public String isValidInput() {
		String result = "";
		if (keyPressedAction()) {
			String serialNumber = getStringJFXTextField(txtControllerBarcode);
			String currentStatus = dbHandler.getStatusDone(COL_CURRENT_STATION_CONTROLER, serialNumber);
			if (!dbHandler.isBarcodeExist(serialNumber)) {
				result = "0";
			} else {
				switch (currentStatus) {
				case SHIPPING_STATION:
					result = "1";
					break;
				case RECEIVING_STATION:
				case RE_WORK_STATION:
				case ASSEMBLY_STATION:
				case FIRMWARE_UPDATE_STATION:
				case PACKING_STATION:
				case BURN_IN_STATION:
				case WAIT_TO_BURN_IN:
				case RESULT_STATION:
				case REPAIR_STATION:
					result = SERIAL_EXIST;
					break;
				default:
					result = "0";
					LOGGER.info("There is nothing here.");
					break;
				}
			}
		} else {
			if (!txtModel.validate()) {
				result = "Model is missing! Enter controller model.";
			} else if (!txtBoxBarcode.validate()) {
				result = "Box barcode is missing! Enter box barcode.";

			} else if (!txtControllerBarcode.validate()) {
				result = "Controller barcode is missing! Enter Controller barcode.";

			} else if (!txtBoxBarcode.getText().equalsIgnoreCase(txtControllerBarcode.getText())) {
				result = "Box and controller serial numbers DO NOT MATCH";
			} else {
				result = "Something was wrong!";
			}
		}

		return result;
	}

	@FXML
	void submit(ActionEvent event) {
		String test = isValidInput();
		if (test.equals(SERIAL_EXIST)) {
			warningAlert(test);
		}

		// If serial number does not exist , we add to system
		else {
			String serialNumber = getStringJFXTextField(txtControllerBarcode);
			String model = getStringJFXTextField(txtModel);
			String lotId = generatorLotId();
			
			if (test.equals("0")) {
				String result = dbHandler.addNewController(model, serialNumber, getCurrentTimeStamp(), lotId);
				if (result.equalsIgnoreCase(serialNumber)) {
					addBarcodeToTable(barcode, serialNumber, model);
					result = dbHandler.addToHistoryRecord(currentUser, RECEIVING_STATION, getCurrentTimeStamp(),
							serialNumber, "Received Controller Serial Number : " + serialNumber);
					if (result.equalsIgnoreCase(serialNumber)) {
						notification = notificatioBuilder(Pos.BOTTOM_RIGHT, graphic, null,
								"Add New Controller Successfully", 2);
						notification.showInformation();
					} else {
						warningAlert(result);
					}
				} else {
					warningAlert(result);
				}
			} else {
				if (test.equals("1")) {
					String result = dbHandler.deleteController(serialNumber);
					if (result.equalsIgnoreCase(serialNumber)) {
						result = dbHandler.addNewController(model, serialNumber, getCurrentTimeStamp(), lotId);
						if (result.equalsIgnoreCase(serialNumber)) {
							addBarcodeToTable(barcode, serialNumber, model);
							notification = notificatioBuilder(Pos.BOTTOM_RIGHT, graphic, null,
									"Add New Controller Successfully", 2);
							notification.showInformation();
						} else {
							warningAlert(result);
						}
					} else {
						warningAlert(result);
					}
				} else {
					// Something was wrong
					warningAlert(test);
				}

			}
		}

		txtCounter.textProperty().bind(Bindings.format("%d", barcode.size()));
		txtBoxBarcode.clear();
		txtControllerBarcode.clear();
		txtBoxBarcode.requestFocus();

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dbHandler = new DBHandler();
		textFieldFormat(txtBoxBarcode, "Box barcode is required", true);
		textFieldFormat(txtControllerBarcode, "Controller barcode is required", true);
		textFieldFormat(txtModel, "Controller Model is required", true);
		btnSubmit.setDisable(true);

		txtModel.setOnAction(e -> {
			String tempModel = isModelValid(txtModel);
			if (tempModel.isEmpty()) {
				txtBoxBarcode.requestFocus();
			} else {
				warningAlert(tempModel);
				txtModel.clear();
				txtModel.requestFocus();
			}

		});
		txtBoxBarcode.setOnAction(e -> {
			String tempboxBarcode = isBarcodeValid(txtBoxBarcode);
			if (tempboxBarcode.isEmpty()) {
				txtControllerBarcode.requestFocus();
			} else {
				warningAlert(tempboxBarcode);
				txtBoxBarcode.clear();
				txtBoxBarcode.requestFocus();
			}
		});
		txtControllerBarcode.setOnAction(e -> {
			String tempControllerBarcode = isBarcodeValid(txtControllerBarcode);
			if (tempControllerBarcode.isEmpty()) {
				if (getStringJFXTextField(txtBoxBarcode)
						.equalsIgnoreCase(getStringJFXTextField(txtControllerBarcode))) {
					submit(e);
				} else {
					warningAlert("Two barcodes are different");
					txtBoxBarcode.clear();
					txtControllerBarcode.clear();
					txtBoxBarcode.requestFocus();
				}
			} else {
				warningAlert(tempControllerBarcode);
				txtControllerBarcode.clear();
				txtControllerBarcode.requestFocus();
			}
		});
		Platform.runLater(() -> txtModel.requestFocus());
		// setup tree view
		barcode.addAll(dbHandler.getAllReceived());
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

	public boolean keyPressedAction() {
		boolean result = false;
		String temp1 = isModelValid(this.txtModel);
		String temp2 = isBarcodeValid(this.txtControllerBarcode);
		String temp3 = isBarcodeValid(this.txtBoxBarcode);
		if (temp1.isEmpty() && temp2.isEmpty() && temp3.isEmpty() && (getStringJFXTextField(this.txtBoxBarcode)
				.equalsIgnoreCase(getStringJFXTextField(this.txtControllerBarcode)))) {

			result = true;

		}
		btnSubmit.setDisable(!result);
		return result;

	}

}
