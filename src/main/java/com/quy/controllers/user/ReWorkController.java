package com.quy.controllers.user;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.quy.controllers.Controller;
import com.quy.controllers.SignInController;
import com.quy.database.DBHandler;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class ReWorkController extends Controller implements Initializable {
	@FXML
	private JFXTextField txtControllerBarcode;

	@FXML
	private JFXButton btnRework;

	private DBHandler dbHandler;
	protected String currentUser = SignInController.getInstance().username();

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

	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		dbHandler = new DBHandler();
		btnRework.setDisable(true);
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
					result = "\r\n Controller has been shipped. Please ask manager INTERMEDIATELY!!!";
				} else {
					switch (currentStatus) {
					case ASSEMBLY_STATION:
					case FIRMWARE_UPDATE_STATION:
					case REPAIR_STATION:
						result = "";
						break;
					case WAIT_TO_BURN_IN:
						result = "\r\n Controller stayed in Burn_In System and Wait to Burn";
						break;
					case BURN_IN_STATION:
						result = "\r\n Controller is BURN_IN!";
						break;
					case RECEIVING_STATION:
						result = "\r\n Please go to " + ASSEMBLY_STATION;
						break;
					case PACKING_STATION:
						result = "\r\n Please go to " + RE_WORK_STATION;
						break;
					default:
						LOGGER.info("There is nothing here.");
					}
				}
			}
		}

		return result;

	}

}
