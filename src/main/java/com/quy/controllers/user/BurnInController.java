package com.quy.controllers.user;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import com.quy.bizcom.SMCController;
import com.quy.controllers.Controller;
import com.quy.controllers.SignInController;
import com.quy.database.DBHandler;

import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.text.Text;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

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
	private boolean isBurnInStarted;

	@FXML
	void addToBurnInList(ActionEvent event) {
		if (!txtControllerBarcode.validate()) {
			warningAlert("Controller barcode is missing! Enter Controller barcode.");
			txtControllerBarcode.clear();
			txtControllerBarcode.requestFocus();
		} else if (isValidIinput()) {
			// add to database
			String controller_barcode = getStringJFXTextField(txtControllerBarcode);

			String result = dbHandler.addToBurnInWaitingList(controller_barcode);
			if (result.equalsIgnoreCase(controller_barcode)) {
				addBarcodeToTable(barcode, controller_barcode);
				currentReadyToBurn.add(controller_barcode);
				totalInList++;
				txtNumber.setText(totalInList + "");
//				addBarcodeToTable(this.barcode, controller_barcode);
				// Remove barcode if submit successful
//				int index = 0;
//				if(currentSelectedBarcode == null) {
//					for(SMCController smc : barcode) {
//						if(smc.getControllerBarcode().getValue().equalsIgnoreCase(controller_barcode)) {
//							break;
//						}else {
//							index++;
//						}
//					}
//					barcode.remove(index);
//				}
//				barcode.remove(currentSelectedBarcode);

				dbHandler.addToHistoryRecord(currentUser, "Added to waiting list burn in", getCurrentTimeStamp(),
						controller_barcode, "Get ready to burn in.");
				btnStart.setDisable(false);
			} else {
				warningAlert(result);
			}
		} else {
			warningAlert("Controller serial number has problem. Check with manager.");
		}

		txtControllerBarcode.clear();
		txtControllerBarcode.requestFocus();
		btnAdd.setDisable(true);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dbHandler = new DBHandler();
		isBurnInStarted = false;

		btnAdd.setDisable(true);
		btnStart.setDisable(true);
		textFieldFormat(txtControllerBarcode, "Barcode input is required", true);
		currentReadyToBurn = new ArrayList<>();
		currentReadyToBurn.addAll(dbHandler.getAllReadyToBurn());
		totalInList = currentReadyToBurn.size();
		txtNumber.setText(totalInList + "");

		if (totalInList != 0) {
			
			btnStart.setDisable(false);
		} else {
			btnStart.setDisable(true);
		}
		treeviewTableBuilder(treeview, barcode, currentReadyToBurn);
		txtControllerBarcode.setOnAction(e -> {
			addToBurnInList(e);
		});

	}

	@FXML
	public boolean isValidIinput() {
		boolean result = false;

		String temp = isBarcodeValid(this.txtControllerBarcode);

		if (temp.isEmpty()) {
			String barcode = getStringJFXTextField(txtControllerBarcode);
			if (dbHandler.isBarcodeExist(barcode)) {
				// Check Does it Received

				if (dbHandler.getStatusDone(CURRENT_STATION, barcode).equalsIgnoreCase(ASSEMBLY_STATION)) {
					result = true;
				} else {
					warningAlert("This controller DID NOT DO ASSEMBLY YET! Please go to assembly station to add.");
					txtControllerBarcode.clear();
					txtControllerBarcode.requestFocus();
				}
			} else {
				result = false;
			}
		}
		// Enable button if barcode valid
		btnAdd.setDisable(!result);
		return result;
	}

	@FXML
	void startBurnIn(ActionEvent event) {
		String timeStamp = getCurrentTimeStamp();
		boolean flag = false;
		if (!currentReadyToBurn.isEmpty()) {
			for (String barcode : currentReadyToBurn) {
				String result = dbHandler.burn_in(barcode, timeStamp);
				if (result.equalsIgnoreCase(barcode)) {

					dbHandler.addToHistoryRecord(currentUser, "Burn In Station", getCurrentTimeStamp(), barcode,
							"Added to burn in station");
					flag = true;
				} else {
					warningAlert(result);
				}
			}
			if (flag) {
				isBurnInStarted = true;
				notification = notificatioBuilder(Pos.BOTTOM_RIGHT, graphic, null, "Add to Burn in Successfully", 3);
				notification.showInformation();
				txtNumber.setText(dbHandler.getAllReadyToBurn().size() + "");
				barcode.clear();
			}
		}
	}

}
