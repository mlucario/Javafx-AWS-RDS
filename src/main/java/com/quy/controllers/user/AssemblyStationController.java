package com.quy.controllers.user;

import java.net.URL;
import java.util.ResourceBundle;

import com.quy.bizcom.SMCController;
import com.quy.controllers.Controller;
import com.quy.controllers.SignInController;
import com.quy.database.DBHandler;

import javafx.fxml.Initializable;
import javafx.geometry.Pos;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class AssemblyStationController extends Controller implements Initializable {
	@FXML
	private JFXTextField txtControllerBarcode;

	@FXML
	private JFXListView<?> listBarcode;
	@FXML
	private JFXButton btnSubmit;
	private DBHandler dbHandler;
	protected String currentUser = SignInController.getInstance().username();
	private ObservableList<SMCController> barcode = FXCollections.observableArrayList();

	public boolean isValidInput() {
		boolean result = false;

		String temp = isBarcodevalid(this.txtControllerBarcode);

		if (temp.isEmpty()) {
			String barcode = this.txtControllerBarcode.getText().trim();
			if (dbHandler.isBarcodeExist(barcode)) {
				// Check Does it Recceived
				if(dbHandler.getStatusDone(IS_RECEIVED, barcode).equalsIgnoreCase("1")) {
					result = true;
				}
				else {
					warningAlert("Barcode does not exist. Please take to receiving station.");
				}
			} else {
				result = false;
			}
		}
		// Enable button if barcode valid
		btnSubmit.setDisable(!result);
		return result;
	}

	@FXML
	void submit(ActionEvent event) {
		 if(!txtControllerBarcode.validate()) {
				warningAlert("Controller barcode is missing! Enter Controller barcode.");
				txtControllerBarcode.clear();
				txtControllerBarcode.requestFocus();
		 }else if(isValidInput()) {
			 //add to dataabase
		 }else {
			 // notification error
		 }

		
			txtControllerBarcode.clear();
			txtControllerBarcode.requestFocus();
	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dbHandler = new DBHandler();
		btnSubmit.setDisable(true);
		textFieldFormat(txtControllerBarcode, "Controller barcode is required", true);
		
		

	}

}
