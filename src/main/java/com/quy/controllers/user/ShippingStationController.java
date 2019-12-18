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
	private String listWork = "";
	private int count;
	private String aString = "";
	private boolean flag = true;
	@FXML
	void addToList() {
		if (isValidInput().isEmpty()) {
			String serialNumber = getStringJFXTextField(txtControllerBarcode);
			String currentLastestStation = dbHandler.getStatusDone(COL_CURRENT_STATION_CONTROLER, serialNumber);
			String model = dbHandler.getStatusDone(COL_MODEL_CONTROLER, serialNumber);

			if (currentLastestStation.equalsIgnoreCase(PACKING_STATION)) {
				
				barcode.forEach((obj) -> {
					if (obj.getSerialNumber().getValue().equalsIgnoreCase(serialNumber)) {
						flag = false;
					}
				});
				if (flag) {

					addBarcodeToTable(barcode, serialNumber, model);
					count++;
					shippingList.put(serialNumber, dbHandler.getWork(serialNumber));
				} else {
					warningAlert("Serial Number is in the list!");
				}
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
		flag = true;
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
		if (!shippingList.isEmpty()) {
			shippingList.forEach((serialNumber, v) -> {
				String result = dbHandler.shipping(serialNumber, getCurrentTimeStamp());
				listSerialNumber += serialNumber + ";";
				for (String w : v) {
					if (w.equalsIgnoreCase("Added to waiting list burn in")) {
						continue;
					}
					listWork += w + ",";
				}
				listWork += SHIPPING_STATION + ";";
				if (result.equalsIgnoreCase(serialNumber)) {
					dbHandler.addToHistoryRecord(currentUser, SHIPPING_STATION, getCurrentTimeStamp(), serialNumber,
							"Shipped!", true);
				}
			});

			listSerialNumber = listSerialNumber.substring(0, listSerialNumber.length() - 1);

			listWork = listWork.substring(0, listWork.length() - 1);

			HashMap<String, Integer> mapWorks = new HashMap<>();
			String[] tempString = listWork.split(";");
			for (String s : tempString) {
				s = s.trim();
				if ((s.charAt(s.length() - 1)) == ';' || (s.charAt(s.length() - 1)) == ',') {
					s = s.substring(0, s.length() - 1);
				}
				if (s.charAt(0) == ' ') {
					s = s.substring(1, s.length());
				}
				if (mapWorks.containsKey(s)) {
					mapWorks.put(s, mapWorks.get(s) + 1);
				} else {
					mapWorks.put(s, 1);
				}
			}

			mapWorks.forEach((aKey, aValue) -> {
				aString += String.format("%s : %d\r\n", aKey, aValue);
			});

			boolean rs = dbHandler.finishShipping(generatorLotId(), getCurrentTimeStamp(), barcode.size(), currentUser,
					listSerialNumber, aString, txtInfo.getText());

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
		SMCController.stt = 1;
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
