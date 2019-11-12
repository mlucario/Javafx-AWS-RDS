
package com.quy.controllers.user;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.quy.bizcom.SMCController;
import com.quy.controllers.Controller;
import com.quy.controllers.SignInController;
import com.quy.database.DBHandler;

import javafx.fxml.Initializable;

import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Text;

public class ResultStationController extends Controller implements Initializable {
	@FXML
	private JFXTextField txtHours;
	@FXML
	private Text txtPass;

	@FXML
	private Text txtFail;
	@FXML
	private JFXRadioButton rdPassed;

	@FXML
	private JFXRadioButton rdFail;

	@FXML
	private JFXTextField txtControllerBarcode;

	@FXML
	private JFXTreeTableView<SMCController> treeviewPassed;

	@FXML
	private JFXTreeTableView<SMCController> treeviewFail;

	@FXML
	private ToggleGroup result;
	private DBHandler dbHandler;
	protected String currentUser = SignInController.getInstance().username();
	private ObservableList<SMCController> barcodePassed = FXCollections.observableArrayList();
	private ObservableList<SMCController> barcodeFail = FXCollections.observableArrayList();
	private boolean resultChoosen;
	private ArrayList<String> currentPassed;
	private ArrayList<String> currentFail;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		resultChoosen = true;
		dbHandler = new DBHandler();
		textFieldFormat(txtControllerBarcode, "Controller Barcode is required", true);
		textFieldFormat(txtHours, "Please fill testing hours", true);
		txtControllerBarcode.setDisable(true);
		currentPassed = new ArrayList<>();
		currentFail = new ArrayList<>();
		rdPassed.setToggleGroup(result);
		rdFail.setToggleGroup(result);
		rdPassed.setUserData("PASSED");
		rdFail.setUserData("FAIL");

		result.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				if (result.getSelectedToggle() != null) {
					System.out.println(result.getSelectedToggle().getUserData().toString());
					resultChoosen = result.getSelectedToggle().getUserData().toString().equalsIgnoreCase("PASSED")
							? true
							: false;
					txtControllerBarcode.setDisable(false);
				} else {
					txtControllerBarcode.setDisable(true);
					warningAlert("PLEASE SELECT PASSED or FAIL");
				}

			}
		});
		txtControllerBarcode.setOnAction(e -> {
			resultAction(e);
		});
		currentPassed.addAll(dbHandler.getAllPassedOrFail(true));
		currentFail.addAll(dbHandler.getAllPassedOrFail(false));
		treeviewTableBuilder(treeviewPassed, barcodePassed, currentPassed);
		treeviewTableBuilder(treeviewFail, barcodeFail, currentFail);

	}

	@FXML
	public boolean isValidInput() {
		boolean result = false;

		String temp = isBarcodeValid(this.txtControllerBarcode);

		if (temp.isEmpty()) {
			String barcode = getStringJFXTextField(txtControllerBarcode);
			if (dbHandler.isBarcodeExist(barcode)) {
				// Check Does it Received

				if (dbHandler.getStatusDone(CURRENT_STATION, barcode).equalsIgnoreCase(BURN_IN_STATION)) {
					result = true;
				} else {
					warningAlert("This controller DID NOT IN THE BURN IN SYSTEM. Please check again.");
					txtControllerBarcode.clear();
					txtControllerBarcode.requestFocus();
				}
			} else {
				result = false;
			}
		}
		return result;
	}

	@FXML
	void resultAction(ActionEvent event) {
		String timeStamp = getCurrentTimeStamp();
		resultChoosen = result.getSelectedToggle().getUserData().toString().equalsIgnoreCase("PASSED") ? true : false;
		if (result.getSelectedToggle() == null) {
			warningAlert("Please choise one Radio options");
		} else {
			if (!txtControllerBarcode.validate()) {
				warningAlert("Controller barcode is missing! Enter Controller barcode.");
				txtControllerBarcode.clear();
				txtControllerBarcode.requestFocus();
			} else if (isValidInput()) {
				// add to database
				String controller_barcode = getStringJFXTextField(txtControllerBarcode);

				String result = dbHandler.setResult(controller_barcode, timeStamp, resultChoosen);
				if (result.equalsIgnoreCase(controller_barcode)) {
					System.out.println("=======================");
					if (resultChoosen) {
						addBarcodeToTable(barcodePassed, controller_barcode);
						currentPassed.add(controller_barcode);
					} else {
						addBarcodeToTable(barcodeFail, controller_barcode);
						currentFail.add(controller_barcode);
					}

					dbHandler.addToHistoryRecord(currentUser, "Added to waiting list burn in", getCurrentTimeStamp(),
							controller_barcode, "Get ready to burn in.");

				} else {
					warningAlert(result);
				}
			} else {

			}
		}

		txtControllerBarcode.clear();
		txtControllerBarcode.requestFocus();
	}

}
