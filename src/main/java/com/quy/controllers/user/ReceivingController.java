
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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.text.Text;

public class ReceivingController extends Controller implements Initializable {
	@FXML
	private Text txtCompleted;
	@FXML
	private Text txtCounter;

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
	private int count;

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

				String result = dbHandler.addNewController(model, serialNumber, getCurrentTimeStamp(), lotId, 0);
				if (result.equalsIgnoreCase(serialNumber)) {
					count++;
					addBarcodeToTable(barcode, serialNumber);
					notification = notificatioBuilder(Pos.BOTTOM_RIGHT, graphic, null,
							"Add New Controller Successfully", 2);
					notification.showInformation();
					dbHandler.addToHistoryRecord(currentUser, RECEIVING_STATION, getCurrentTimeStamp(), serialNumber,
							"Received Controller Serial Number : " + serialNumber, false);
				} else {
					warningAlert(result);
				}
			} else {

				warningAlert("Controller serial number is exist. Please go to RE-WORK!");

			}
		}

		txtCounter.setText(count + "");
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
			String tempModel = isModelValid(txtModel);
			if (tempModel.isEmpty()) {
				txtBoxBarcode.requestFocus();
			} else {
				warningAlert(tempModel);
				txtModel.clear();
				txtModel.requestFocus();
			}

		});
		txtBoxBarcode.setOnAction(e -> {
			String tempboxBarcode = isBarcodeValid(txtBoxBarcode);
			if (tempboxBarcode.isEmpty()) {
				txtControllerBarcode.requestFocus();
			} else {
				warningAlert(tempboxBarcode);
				txtBoxBarcode.clear();
				txtBoxBarcode.requestFocus();
			}
		});
		txtControllerBarcode.setOnAction(e -> {
			String tempControllerBarcode = isBarcodeValid(txtControllerBarcode);
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
		Platform.runLater(() -> txtModel.requestFocus());
		// setup tree view
		treeviewTableBuilder(treeView, barcode, RECEIVING_STATION);

		// TODO find the other way to improve this one
		// don't have to fetch database 2 times

		count = dbHandler.getAllReceived().size();
		txtCounter.setText(count + "");

	}

	public boolean keyPressedAction() {
		boolean result = false;
		String temp1 = isModelValid(this.txtModel);
		String temp2 = isBarcodeValid(this.txtControllerBarcode);
		String temp3 = isBarcodeValid(this.txtBoxBarcode);
		if (temp1.isEmpty() && temp2.isEmpty() && temp3.isEmpty() && (getStringJFXTextField(this.txtBoxBarcode)
				.equalsIgnoreCase(getStringJFXTextField(this.txtControllerBarcode)))) {

			result = true;

		}
		btnSubmit.setDisable(!result);
		return result;

	}

}
