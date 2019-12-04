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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

public class ShippingStationController extends Controller implements Initializable {

	@FXML
	private JFXTextField txtControllerBarcode;

	@FXML
	private JFXButton btnSubmit;
	@FXML
	private JFXButton btnAdd;
	@FXML
	private JFXTreeTableView<SMCController> treeView;

	@FXML
	private Text txtCounter;
	private DBHandler dbHandler;
	private String currentUser = SignInController.getInstance().username();
	private ObservableList<SMCController> barcode = FXCollections.observableArrayList();
	private ArrayList<String> listSerialNumber = new ArrayList<>();
	private ArrayList<String> listWork = new ArrayList<>();
	private int count;
    @FXML
    private JFXButton btnCancel;
	@FXML
	void addToList() {
		if (isValidInput().isEmpty()) {
			String serialNumber = getStringJFXTextField(txtControllerBarcode);
			String currentLastestStation = dbHandler.getStatusDone(COL_CURRENT_STATION_CONTROLER, serialNumber);

			if (currentLastestStation.equalsIgnoreCase(PACKING_STATION)) {
//				String result = dbHandler.shipping(serialNumber, timestamp);
				addBarcodeToTable(barcode, serialNumber);
				listWork.add(dbHandler.getWork(serialNumber));
				listSerialNumber.add(serialNumber);

			} else {
				warningAlert("Shipping Fail. Please check with MANAGER");
			}
		} else {
			warningAlert(isValidInput());
		}

		btnAdd.setDisable(true);
		txtControllerBarcode.clear();
		txtControllerBarcode.requestFocus();
		btnSubmit.setDisable(false);
		txtCounter.setText(count + "");
	}

	@FXML
	void keyPressValidate() {
		boolean result = false;
		if (txtControllerBarcode.validate()) {
			result = true;
		}
		btnAdd.setDisable(!result);
	}

	// TODO : check the case later
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
					case ASSEMBLY_STATION:
					case BURN_IN_STATION:
					case WAIT_TO_BURN_IN:
					case REPAIR_STATION:
					case RECEIVING_STATION:
					case RESULT_STATION:
					case "UNREPAIRABLE":
						result = "\r\n Controller is not ready to ship out!";
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
		String timestamp = getCurrentTimeStamp();
		String today = getCurrentTimeStamp();

		if (!listSerialNumber.isEmpty()) {
			int quality = listSerialNumber.size();

			String listStringSN = "";
			for (String s : listSerialNumber) {
				listStringSN += s + ";";
				String result = dbHandler.shipping(s, timestamp);
				if (!result.equalsIgnoreCase(s)) {
					warningAlert("Fail to update Shippng to database");
					break;
				}
			}
			String info = "";
			for (String ss : listWork) {
				info += ss;
			}

			String result = "";
		} else {
			warningAlert("No any controller to shipping list");
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dbHandler = new DBHandler();
		btnSubmit.setDisable(true);
		btnAdd.setDisable(true);
		count = 0;
		textFieldFormat(txtControllerBarcode, "Controller barcode is required", true);
		Platform.runLater(() -> txtControllerBarcode.requestFocus());
		txtControllerBarcode.setOnAction(e -> {

			addToList();

		});

		treeviewTableBuilder(treeView, barcode);
		txtCounter.setText(count + "");
	}

}
