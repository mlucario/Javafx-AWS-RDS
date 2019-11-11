
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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.text.Text;

public class ReceivingController extends Controller implements Initializable {
	@FXML
	private Text txtCompleted;

	@FXML
	private JFXTextField txtModel;

	@FXML
	private JFXTextField txtBoxBarcode;

	@FXML
	private JFXTextField txtControllerBarcode;
	@FXML
	private JFXButton btnSubmit;

	@FXML
	private JFXTreeTableView<SMCController> treeView;

	private DBHandler dbHandler;
	protected String currentUser = SignInController.getInstance().username();
	private ObservableList<SMCController> barcode = FXCollections.observableArrayList();

	@FXML
	void submit(ActionEvent event) {
		if (!txtModel.validate()) {
			warningAlert("Model is missing! Enter controller model.");
			txtModel.clear();
			txtModel.requestFocus();
		} else if (!txtBoxBarcode.validate()) {
			warningAlert("Box barcode is missing! Enter box barcode.");
			txtBoxBarcode.clear();
			txtBoxBarcode.requestFocus();
		} else if (!txtControllerBarcode.validate()) {
			warningAlert("Controller barcode is missing! Enter Controller barcode.");
			txtControllerBarcode.clear();
			txtControllerBarcode.requestFocus();
		} else if (!txtBoxBarcode.getText().equalsIgnoreCase(txtControllerBarcode.getText())) {
			warningAlert("Box and controller barcode DO NOT MATCH. Please verify them again.");
			txtControllerBarcode.requestFocus();
		}

		else {
			String serialNumber = getStringJFXTextField(txtControllerBarcode);
			String model = getStringJFXTextField(txtModel);
			String lotId = generatorLotId();
			if (!dbHandler.isBarcodeExist(serialNumber)) {
				String result = dbHandler.addNewController(model, serialNumber, getCurrentTimeStamp(), lotId);
				if (result.equalsIgnoreCase(serialNumber)) {
					addBarcodeToTable(barcode, serialNumber);
					notification = notificatioBuilder(Pos.BOTTOM_RIGHT, graphic, null,
							"Add New Controller Successfully", 2);
					notification.showInformation();
					dbHandler.addToHistoryRecord(currentUser, "Receiving Station", getCurrentTimeStamp(), serialNumber,
							"");
				} else {
					warningAlert(result);
				}
			} else {
				// If serial number added, lotId different => rework handler
				warningAlert(serialNumber + " already added! Try add other controller.");
				int reworkTimes = dbHandler.getLastestReWorkCount(serialNumber);
				System.out.println(serialNumber + " RW : " + reworkTimes);
			}
		}

		txtBoxBarcode.clear();
		txtControllerBarcode.clear();
		txtBoxBarcode.requestFocus();

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dbHandler = new DBHandler();
		textFieldFormat(txtBoxBarcode, "Box barcode is required", true);
		textFieldFormat(txtControllerBarcode, "Controller barcode is required", true);
		textFieldFormat(txtModel, "Controller Model is required", true);
		btnSubmit.setDisable(true);

		txtModel.setOnAction(e -> {
			String tempModel = isModelvalid(txtModel);
			if (tempModel.isEmpty()) {
				txtBoxBarcode.requestFocus();
			} else {
				warningAlert(tempModel);
				txtModel.clear();
				txtModel.requestFocus();
			}

		});
		txtBoxBarcode.setOnAction(e -> {
			String tempboxBarcode = isBarcodevalid(txtBoxBarcode);
			if (tempboxBarcode.isEmpty()) {
				txtControllerBarcode.requestFocus();
			} else {
				warningAlert(tempboxBarcode);
				txtBoxBarcode.clear();
				txtBoxBarcode.requestFocus();
			}
		});
		txtControllerBarcode.setOnAction(e -> {
			String tempControllerBarcode = isBarcodevalid(txtControllerBarcode);
			if (tempControllerBarcode.isEmpty()) {
				if (getStringJFXTextField(txtBoxBarcode)
						.equalsIgnoreCase(getStringJFXTextField(txtControllerBarcode))) {
					submit(e);
				} else {
					warningAlert("Two barcodes are different");
					txtBoxBarcode.clear();
					txtControllerBarcode.clear();
					txtBoxBarcode.requestFocus();
				}
			} else {
				warningAlert(tempControllerBarcode);
				txtControllerBarcode.clear();
				txtControllerBarcode.requestFocus();
			}
		});

		// setup tree view
		treeviewTableBuilder(treeView, barcode, RECEIVING_STATION);

	}

	public boolean keyPressedAction() {
		boolean result = false;
		String temp1 = isModelvalid(this.txtModel);
		String temp2 = isBarcodevalid(this.txtControllerBarcode);
		String temp3 = isBarcodevalid(this.txtBoxBarcode);
		if (temp1.isEmpty() && temp2.isEmpty() && temp3.isEmpty()) {
			if (getStringJFXTextField(this.txtBoxBarcode)
					.equalsIgnoreCase(getStringJFXTextField(this.txtControllerBarcode))) {
				result = true;
			} else {
				result = false;
			}
		}
		btnSubmit.setDisable(!result);
		return result;

	}

}
