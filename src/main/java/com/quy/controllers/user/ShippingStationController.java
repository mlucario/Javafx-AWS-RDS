package com.quy.controllers.user;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class ShippingStationController extends Controller implements Initializable {

	@FXML
	private JFXTextField txtControllerBarcode;

	@FXML
	private JFXButton btnSubmit;
	@FXML
	private JFXButton btnAdd;

	@FXML
	private JFXButton btnCancel;
	@FXML
	private JFXTreeTableView<SMCController> treeView;

	@FXML
	private Text txtCounter;

	@FXML
	private JFXTextField txtSN;

	@FXML
	private Label shippindID;

	@FXML
	private JFXTextArea txtInfo;

	private DBHandler dbHandler;
	private String currentUser = SignInController.getInstance().username();
	private ObservableList<SMCController> barcode = FXCollections.observableArrayList();
	private HashMap<String, ArrayList<String>> shippingList = new HashMap<>();
	private String listSerialNumber = "";
	private String listWork ="";
	private int count;

	@FXML
	void addToList() {
		if (isValidInput().isEmpty()) {
			String serialNumber = getStringJFXTextField(txtControllerBarcode);
			String currentLastestStation = dbHandler.getStatusDone(COL_CURRENT_STATION_CONTROLER, serialNumber);
			String model = dbHandler.getStatusDone(COL_MODEL_CONTROLER, serialNumber);

			if (currentLastestStation.equalsIgnoreCase(PACKING_STATION)) {
//				String result = dbHandler.shipping(serialNumber, timestamp);
				SMCController.stt = 1;
				addBarcodeToTable(barcode, serialNumber, model);
				count++;
				shippingList.put(serialNumber, dbHandler.getWork(serialNumber));
			} else {
				warningAlert("Shipping Fail. Please check with MANAGER");
			}
		} else {
			warningAlert(isValidInput());
		}
		btnCancel.setDisable(false);
		btnAdd.setDisable(true);
		txtControllerBarcode.clear();
		txtControllerBarcode.requestFocus();
		txtCounter.setText(count + "");
		btnSubmit.setDisable(false);
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
					case FIRMWARE_UPDATE_STATION:
					case "UNREPAIRABLE":
						result = "\r\n Controller is not ready to ship out!";
						break;
					case PACKING_STATION:
						result = "";
						break;
					default:
						result = "Something was wrong!";
						LOGGER.info("There is nothing here.");
					}
				}
			}
		}

		return result;
	}

	@FXML
	void submit() {
//		String timestamp = getCurrentTimeStamp();
//		String today = getCurrentTimeStamp();
//
//		if (!listSerialNumber.isEmpty()) {
//			int quality = listSerialNumber.size();
//
//			String listStringSN = "";
//			for (String s : listSerialNumber) {
//				listStringSN += s + ";";
//				String result = dbHandler.shipping(s, timestamp);
//				if (!result.equalsIgnoreCase(s)) {
//					warningAlert("Fail to update Shippng to database");
//					break;
//				}
//			}
//			String info = "";
//			for (String ss : listWork) {
//				info += ss;
//			}
//
//			String result = "";
//		} else {
//			warningAlert("No any controller to shipping list");
//		}
//		
//		btnSubmit.setDisable(true);

		if (!shippingList.isEmpty()) {
			shippingList.forEach((serialNumber, v) -> {
				String result = dbHandler.shipping(serialNumber, getCurrentTimeStamp());
				listSerialNumber += serialNumber + ";";
				for (String w : v) {
					listWork += w + ",";
				}
				listWork += ";";
				if (result.equalsIgnoreCase(serialNumber)) {
					dbHandler.addToHistoryRecord(currentUser, SHIPPING_STATION, getCurrentTimeStamp(), serialNumber,
							"Shipped!");
				}
			});

			listSerialNumber = listSerialNumber.substring(0, listSerialNumber.length() - 1);
			System.out.println("listSerialNumber : "  + listSerialNumber);
			listWork = listWork.substring(0, listWork.length() - 2);
			System.out.println("listWork : "  + listWork);
			boolean rs = dbHandler.finishShipping(generatorLotId(), getCurrentTimeStamp(), barcode.size(), currentUser,
					listSerialNumber, listWork, txtInfo.getText());

			if (rs) {
				notification = notificatioBuilder(Pos.BOTTOM_RIGHT, graphic, null, "Ship Successfully", 2);
				notification.showInformation();
			} else {
				warningAlert("Shipping System Down!");
			}
		} else {
			warningAlert("Error logic!");
		}
		
		listSerialNumber = listWork = "";

		btnAdd.setDisable(true);
		btnCancel.setDisable(true);
		btnSubmit.setDisable(true);
		barcode.clear();
		txtCounter.setText("0");
		barcode.clear();
		shippingList.clear();

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dbHandler = new DBHandler();
		btnSubmit.setDisable(true);
		btnAdd.setDisable(true);
		btnCancel.setDisable(true);
		count = 0;
		textFieldFormat(txtControllerBarcode, "Controller barcode is required", true);
		Platform.runLater(() -> txtControllerBarcode.requestFocus());
		txtControllerBarcode.setOnAction(e -> {
			addToList();
		});

		treeviewTableBuilder(treeView, barcode);
		txtSN.textProperty().addListener((o, oldVal, newVal) -> {
			if (!oldVal.equalsIgnoreCase(newVal)) {
				treeView.setPredicate(controller -> {
					final SMCController aController = controller.getValue();
					return aController.getSerialNumber().getValue().contains(newVal);
				});
			}
		});

		txtCounter.setText(count + "");
		shippindID.setText(generatorLotId());
	}

	@FXML
	void cancelAction() {
		btnAdd.setDisable(true);
		btnSubmit.setDisable(true);
		btnCancel.setDisable(true);
		shippingList.clear();
		barcode.clear();
		txtCounter.setText("0");
	}

}
