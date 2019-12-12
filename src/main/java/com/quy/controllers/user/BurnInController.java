package com.quy.controllers.user;

import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
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
	private JFXTextArea txtNote;
	@FXML
	private Text txtID;
	@FXML
	private JFXTextField txtSN;
	@FXML
	private Text txtNumber;
	private DBHandler dbHandler;
	protected String currentUser = SignInController.getInstance().username();
	private ObservableList<SMCController> barcode = FXCollections.observableArrayList();
	private String listBurnInSN;
	private String idBurnIn;

	@FXML
	void addToBurnInList(ActionEvent event) {

		if (isValidIinput().isEmpty()) {
			String serialNumber = getStringJFXTextField(txtControllerBarcode);
			String model = dbHandler.getStatusDone(COL_MODEL_CONTROLER, serialNumber);
			String result = dbHandler.addToBurnInWaitingList(serialNumber);
			if (result.equals(serialNumber)) {
				addBarcodeToTable(barcode, serialNumber, model);
				listBurnInSN += serialNumber + ";";
				btnStart.setDisable(false);
//				String history = dbHandler.addToHistoryRecord(currentUser, "Added to waiting list burn in",
//						getCurrentTimeStamp(), serialNumber, "Added to burn in system. SN: " + serialNumber, false);
//				if (!history.equalsIgnoreCase(serialNumber)) {
//					warningAlert(history);
//				} else {
				notification = notificatioBuilder(Pos.BOTTOM_RIGHT, graphic, null, "Add to burn-in list Successfully",
						2);
				notification.showInformation();
//				}

			} else {
				warningAlert(result);
			}
		} else {
			warningAlert(isValidIinput());
		}
		txtNumber.textProperty().bind(Bindings.format("%d", barcode.size()));
		txtControllerBarcode.clear();
		txtControllerBarcode.requestFocus();
		btnAdd.setDisable(true);

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dbHandler = new DBHandler();
		listBurnInSN = "";

		btnAdd.setDisable(true);
		btnStart.setDisable(true);
		textFieldFormat(txtControllerBarcode, "Barcode input is required", true);
		barcode.addAll(dbHandler.getAllReadyToBurn());

		txtNumber.textProperty().bind(Bindings.format("%d", barcode.size()));
		Platform.runLater(() -> txtControllerBarcode.requestFocus());

		if (!txtNumber.getText().equalsIgnoreCase("0")) {

			btnStart.setDisable(false);
		} else {
			btnStart.setDisable(true);
		}

		treeviewTableBuilder(treeview, barcode);
		txtControllerBarcode.setOnAction(e -> addToBurnInList(e));
		txtSN.textProperty().addListener((o, oldVal, newVal) -> {
			if (!oldVal.equalsIgnoreCase(newVal)) {
				treeview.setPredicate(controller -> {
					final SMCController aController = controller.getValue();
					return aController.getSerialNumber().getValue().contains(newVal);
				});
			}
		});
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
					case RESULT_STATION:
						result = "\r\n Controller is burn-in done!";
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
		idBurnIn = generateBurnInID();
		txtID.setText(idBurnIn);
		String timeStamp = getCurrentTimeStamp();
		int count = 0;
		if (listBurnInSN.endsWith(";")) {
			listBurnInSN = listBurnInSN.substring(0, listBurnInSN.length() - 1);
		}

		if (!barcode.isEmpty()) {
			boolean flag = false;
			ArrayList<SMCController> myList = new ArrayList<>();
			myList.addAll(dbHandler.getAllReadyToBurn());
			for (SMCController c : myList) {

				String serialNumber = c.getSerialNumber().getValue();
				String result = dbHandler.burnIn(serialNumber, timeStamp, idBurnIn);
				if (result.equalsIgnoreCase(serialNumber)) {
					count++;
					String history = dbHandler.addToHistoryRecord(currentUser, "Burn In Station", getCurrentTimeStamp(),
							serialNumber, "Started Burn In Process SN " + serialNumber, false);

					if (!history.equalsIgnoreCase(serialNumber)) {
						warningAlert(history);
					} else {
						flag = true;
					}

				} else {
					warningAlert(result);
				}
			}

			if (flag) {
				notification = notificatioBuilder(Pos.CENTER, graphic, null, count + " Added to Burn in Successfully",
						3);
				notification.showInformation();
				// update to burn in db table

				dbHandler.updateBurnInTable(txtID.getText(), listBurnInSN, count, getCurrentTimeStamp(), false);
				barcode.clear();
				SMCController.stt = 1;
				txtNumber.textProperty().bind(Bindings.format("%d", barcode.size()));
			}

		}
	}

	public String generateBurnInID() {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		return timestamp.getTime() + "";
	}

}
