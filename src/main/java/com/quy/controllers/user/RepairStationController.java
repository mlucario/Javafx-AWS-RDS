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
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class RepairStationController extends Controller implements Initializable {
	@FXML
	private JFXTextField txtControllerBarcode;

	@FXML
	private JFXTextArea txtRepairStep;

	@FXML
	private JFXButton btnRepair;
	@FXML
	private JFXTreeTableView<SMCController> treeView;
	@FXML
	private JFXTextField txtSN;
	@FXML
	private Text txtCounter;
	@FXML
	private JFXButton btnUnrepairable;
	@FXML
	private Label txtFailRemain;
	@FXML
	private JFXTextArea txtSymptoms;

	private DBHandler dbHandler;
	private String currentUser = SignInController.getInstance().username();
	private int currentFailRemain;
	private ObservableList<SMCController> barcode = FXCollections.observableArrayList();

	@FXML
	void canNotRepair() {
		if (!isValidInput().isEmpty()) {
			warningAlert(isValidInput());
			txtRepairStep.requestFocus();
		} else {
			String serialNumber = getStringJFXTextField(txtControllerBarcode);
			String timestamp = getCurrentTimeStamp();
			String result = dbHandler.unRepairable(serialNumber, timestamp);
			if (result.equalsIgnoreCase(serialNumber)) {
				currentFailRemain--;
				removeBarcode(barcode,serialNumber);
				notification = notificatioBuilder(Pos.BOTTOM_RIGHT, graphic, null,
						"Update Unrepairable Staion Successfully", 2);
				notification.showInformation();

				txtCounter.textProperty().bind(Bindings.format("%d", currentFailRemain));
			} else {
				warningAlert(result);
			}

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
			String timestamp = getCurrentTimeStamp();

			String result = dbHandler.repairController(serialNumber, timestamp);
			if (result.equalsIgnoreCase(serialNumber)) {
				currentFailRemain--;
				removeBarcode(barcode,serialNumber);
				String history = dbHandler.addToHistoryRecord(currentUser, REPAIR_STATION, timestamp, serialNumber,
						"REPAIR: " + txtRepairStep.getText());
				if (!history.equalsIgnoreCase(serialNumber)) {
					warningAlert(history);
				} else {
					notification = notificatioBuilder(Pos.BOTTOM_RIGHT, graphic, null,
							"Repaired Successfully. Ready for Burn In.", 2);
					notification.showInformation();

					txtCounter.textProperty().bind(Bindings.format("%d", currentFailRemain));
				}
			} else {
				warningAlert(result);
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
		barcode.addAll(dbHandler.getAllFailResult());

		treeviewTableBuilder(treeView, barcode);

		txtSN.textProperty().addListener((o, oldVal, newVal) -> {
			if (!oldVal.equalsIgnoreCase(newVal)) {
				treeView.setPredicate(controller -> {
					final SMCController aController = controller.getValue();
					return aController.getSerialNumber().getValue().contains(newVal);
				});
			}
		});
		currentFailRemain = barcode.size();
		txtCounter.textProperty().bind(Bindings.format("%d", currentFailRemain));
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
