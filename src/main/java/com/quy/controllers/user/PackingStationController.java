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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.text.Text;

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
	private JFXTextField txtSN;
	@FXML
	private Text txtCounter;

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
			boolean isBurnInDone = dbHandler.getStatusDone(COL_IS_BURIN_IN_DONE_CONTROLER, serialNumber)
					.equalsIgnoreCase("1");
			String timestamp = getCurrentTimeStamp();
			String model = dbHandler.getStatusDone(COL_MODEL_CONTROLER, serialNumber);

			if (isBurnInDone) {
				String result = dbHandler.packed(serialNumber, timestamp);
				if (result.equalsIgnoreCase(serialNumber)) {
					addBarcodeToTable(barcode, serialNumber, model);
					String history = dbHandler.addToHistoryRecord(currentUser, PACKING_STATION, timestamp, serialNumber,
							"Package is ready!",false);
					if (!history.equalsIgnoreCase(serialNumber)) {
						warningAlert(history);
					} else {
						notificationx();
					}
				} else {
					warningAlert(result);
				}
			} else {
				warningAlert("This controller is not ready to pack.");
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
		barcode.addAll(dbHandler.getAllPacked());
		txtCounter.setText(barcode.size() + "");
		treeviewTableBuilder(treeView, barcode);

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
