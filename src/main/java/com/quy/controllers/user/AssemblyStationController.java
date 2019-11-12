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
import javafx.scene.text.Text;

public class AssemblyStationController extends Controller implements Initializable {
	@FXML
	private JFXTextField txtControllerBarcode;
	@FXML
	private Text txtCounter;
	@FXML
	private JFXTreeTableView<SMCController> treeView;
	@FXML
	private JFXButton btnSubmit;
	private DBHandler dbHandler;
	protected String currentUser = SignInController.getInstance().username();
	private ObservableList<SMCController> barcode = FXCollections.observableArrayList();
	private int count;
	private boolean currentInputValid;

	@FXML
	public boolean isValidInput() {
		boolean result = false;

		if (isBarcodeValid(txtControllerBarcode).isEmpty()) {
			result = true;
		}
		btnSubmit.setDisable(!result);
		currentInputValid = result;
		return result;
	}

	@FXML
	void submit(ActionEvent event) {
		if (!currentInputValid) {
			warningAlert(isBarcodeValid(txtControllerBarcode));
			txtControllerBarcode.clear();
			txtControllerBarcode.requestFocus();
		} else {

			String serialNumber = getStringJFXTextField(txtControllerBarcode);
			String currentStation = dbHandler.getStatusDone(CURRENT_STATION, serialNumber);

			if (currentStation.equalsIgnoreCase(ASSEMBLY_STATION)) {
				warningAlert(serialNumber + " is finised assembly step. Go to burn in station.");
			} else {
				int reworkCount = Integer.parseInt(dbHandler.getStatusDone(COL_REWORK_COUNT_CONTROLER, serialNumber));
				// Run follow sequence
				if (currentStation.equalsIgnoreCase(RECEIVING_STATION)) {
					String result = dbHandler.assembly(serialNumber, getCurrentTimeStamp(), reworkCount);
					if (result.equalsIgnoreCase(serialNumber)) {
						count++;
						addBarcodeToTable(barcode, serialNumber);
						dbHandler.addToHistoryRecord(currentUser, ASSEMBLY_STATION, getCurrentTimeStamp(), serialNumber,
								"");
					} else {
						warningAlert(result);
					}

				} else {
					// Rework sequence
					

				}

			}

		}

		txtControllerBarcode.clear();
		txtControllerBarcode.requestFocus();
		btnSubmit.setDisable(true);
		txtCounter.setText(count + "");

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dbHandler = new DBHandler();
		btnSubmit.setDisable(true);
		count = 0;
		textFieldFormat(txtControllerBarcode, "Controller barcode is required", true);
		Platform.runLater(() -> txtControllerBarcode.requestFocus());
		txtControllerBarcode.setOnAction(e -> {
			String result = isBarcodeValid(txtControllerBarcode);
			if (result.isEmpty()) {
				submit(e);
			} else {
				warningAlert(result);
				txtControllerBarcode.clear();
				txtControllerBarcode.requestFocus();
			}
		});
		// setup tree view
		treeviewTableBuilder(treeView, barcode, ASSEMBLY_STATION);
		count = dbHandler.getAllAssemblyDone().size();
		txtCounter.setText(count + "");
//		treeView.setOnMousePressed(new EventHandler<MouseEvent>() {
//
//			@Override
//			public void handle(MouseEvent event) {
//				if (event.getButton().equals(MouseButton.PRIMARY)) {
//					if (event.getClickCount() == 2) {
//						currentSelectedBarcode = treeView.getSelectionModel().getSelectedItem().getValue();
//						System.out.println("current : " + currentSelectedBarcode.getControllerBarcode().getValue().toString());
//						String currentSelectedBarcode = treeView.getSelectionModel().getSelectedItem().getValue()
//								.getControllerBarcode().getValue().toString();
//						txtControllerBarcode.setText(currentSelectedBarcode);
//						isValidInput();
//					} else {
//						currentSelectedBarcode = null;
//						txtControllerBarcode.clear();
//					}
//				}
//
//			}
//
//		});

	}

}
