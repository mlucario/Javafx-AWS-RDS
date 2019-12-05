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
import javafx.geometry.Pos;

public class ReWorkController extends Controller implements Initializable {
	@FXML
	private JFXTextField txtControllerBarcode;

	@FXML
	private JFXButton btnRework;
	@FXML
	private JFXTextField txtReason;
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
		if (isValidInput().isEmpty()) {
			String serialNumber = getStringJFXTextField(txtControllerBarcode);
//			String currentLastestStation = dbHandler.getStatusDone(COL_CURRENT_STATION_CONTROLER, serialNumber);
			int reworkCount = Integer.parseInt(dbHandler.getStatusDone(COL_REWORK_COUNT_CONTROLER, serialNumber));
			String timestamp = getCurrentTimeStamp();
			String reason = txtReason.getText();
			String result = dbHandler.rework(serialNumber, timestamp, ++reworkCount);
			if (result.equalsIgnoreCase(serialNumber)) {
				String history = dbHandler.addToHistoryRecord(currentUser, RE_WORK_STATION, timestamp, serialNumber,
						reason,true);
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
		btnRework.setDisable(true);

		btnRework.setOnAction(e -> {
			reWorkAction();
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

				case RECEIVING_STATION:
					result = "No Rework";
					break;
				case FIRMWARE_UPDATE_STATION:
				case REPAIR_STATION:
				case WAIT_TO_BURN_IN:
				case BURN_IN_STATION:
				case PACKING_STATION:
				case SHIPPING_STATION:
				case ASSEMBLY_STATION:
					result = "";
					break;
				default:
					LOGGER.info("NF");
				}

			} else {
				result = "This controller is never worked before.";
			}
		}

		return result;

	}

}
