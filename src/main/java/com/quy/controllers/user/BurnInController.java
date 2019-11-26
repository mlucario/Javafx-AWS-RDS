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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.text.Text;

public class BurnInController extends Controller implements Initializable {

	@FXML
	private JFXTextField txtControllerBarcode;

	@FXML
	private JFXButton btnAdd;

	@FXML
	private JFXTreeTableView<SMCController> treeview;

	@FXML
	private JFXButton btnStart;

	@FXML
	private Text txtNumber;
	private DBHandler dbHandler;
	protected String currentUser = SignInController.getInstance().username();
	private ObservableList<SMCController> barcode = FXCollections.observableArrayList();
	private ArrayList<String> currentReadyToBurn;
	private int totalInList = 0;

	@FXML
	void addToBurnInList(ActionEvent event) {

		if (isValidIinput().isEmpty()) {
			String serialNumber = getStringJFXTextField(txtControllerBarcode);

			String result = dbHandler.addToBurnInWaitingList(serialNumber);
			if (result.equals(serialNumber)) {
				addBarcodeToTable(barcode, serialNumber);
				currentReadyToBurn.add(serialNumber);
				totalInList++;
				txtNumber.setText(totalInList + "");
				dbHandler.addToHistoryRecord(currentUser, "Added to waiting list burn in", getCurrentTimeStamp(),
						serialNumber, "Added to burn in system. SN: " + serialNumber, false);
				btnStart.setDisable(false);
			} else {
				warningAlert(result);
			}
		} else {
			warningAlert(isValidIinput());
		}

		txtControllerBarcode.clear();
		txtControllerBarcode.requestFocus();
		btnAdd.setDisable(true);

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dbHandler = new DBHandler();

		btnAdd.setDisable(true);
		btnStart.setDisable(true);
		textFieldFormat(txtControllerBarcode, "Barcode input is required", true);
		currentReadyToBurn = new ArrayList<>();
		currentReadyToBurn.addAll(dbHandler.getAllReadyToBurn());
		totalInList = currentReadyToBurn.size();
		txtNumber.setText(totalInList + "");
		Platform.runLater(() -> txtControllerBarcode.requestFocus());

		if (totalInList != 0) {

			btnStart.setDisable(false);
		} else {
			btnStart.setDisable(true);
		}

//		treeviewTableBuilder(treeview, barcode, currentReadyToBurn);
		txtControllerBarcode.setOnAction(e -> addToBurnInList(e));

	}

	@FXML
	public String isValidIinput() {
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

	public void keyPressValidate() {
		boolean result = false;
		if (txtControllerBarcode.validate()) {
			result = true;
		}
		btnAdd.setDisable(!result);
	}

	@FXML
	void startBurnIn(ActionEvent event) {
		String timeStamp = getCurrentTimeStamp();
		int count = 0;
		boolean flag = false;
		if (!currentReadyToBurn.isEmpty()) {
			for (String serialNumber : currentReadyToBurn) {

				String result = dbHandler.burnIn(serialNumber, timeStamp);
				if (result.equalsIgnoreCase(serialNumber)) {
					count++;
					dbHandler.addToHistoryRecord(currentUser, "Burn In Station", getCurrentTimeStamp(), serialNumber,
							"Started Burn In Process SN " + serialNumber, false);
					flag = true;
				} else {
					warningAlert(result);
				}
			}
			if (flag) {

				notification = notificatioBuilder(Pos.CENTER, graphic, null, count + " Added to Burn in Successfully",
						3);
				notification.showInformation();
				txtNumber.setText(dbHandler.getAllReadyToBurn().size() + "");
				barcode.clear();
			}
		}
	}

}
