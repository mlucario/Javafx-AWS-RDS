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

public class AssemblyController extends Controller implements Initializable {
	@FXML
	private JFXTextField txtControllerBarcode;
	@FXML
	private Text txtCounter;
	@FXML
	private JFXTreeTableView<SMCController> treeView;
	@FXML
	private JFXButton btnSubmit;
	private DBHandler dbHandler;
	private String currentUser = SignInController.getInstance().username();
	private ObservableList<SMCController> barcode = FXCollections.observableArrayList();
	private int count;

	@FXML
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
					case FIRMWARE_UPDATE_STATION:
					case PACKING_STATION:
					case BURN_IN_STATION:
					case WAIT_TO_BURN_IN:
						result = "\r\n Controller has Assemsbly DONE!";
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
		btnSubmit.setDisable(!result);
	}

	@FXML
	void submit(ActionEvent event) {

		if (isValidInput().isEmpty()) {
			String serialNumber = getStringJFXTextField(txtControllerBarcode);
			String currentLastestStation = dbHandler.getStatusDone(COL_CURRENT_STATION_CONTROLER, serialNumber);
			int reworkCount = Integer.parseInt(dbHandler.getStatusDone(COL_REWORK_COUNT_CONTROLER, serialNumber));
			String timestamp = getCurrentTimeStamp();

			if (currentLastestStation.equalsIgnoreCase(RECEIVING_STATION)) {
				String result = dbHandler.assembly(serialNumber, timestamp, reworkCount, false);
				if (result.equalsIgnoreCase(serialNumber)) {
					count++;
					addBarcodeToTable(barcode, serialNumber);
					String history = dbHandler.addToHistoryRecord(currentUser, ASSEMBLY_STATION, timestamp,
							serialNumber, "Assembler Controller Serial Number : " + serialNumber);
					if (!history.equalsIgnoreCase(serialNumber)) {
						warningAlert(history);
					} else {
						notification = notificatioBuilder(Pos.BOTTOM_RIGHT, graphic, null, "Assembler Successfully", 2);
						notification.showInformation();
					}
				} else {
					warningAlert(result);
				}

			} else {
				// Check if it is updated
				boolean isAssembler = dbHandler.getStatusDone(COL_IS_ASSEMBLY_DONE_CONTROLER, serialNumber)
						.equalsIgnoreCase("1");
				if (isAssembler) {
					warningAlert("Controller (SN: " + serialNumber + ") did assembly. Please use re-work to redo it!");
				} else {
					warningAlert("WRONG STATION");
				}

			}

		} else {
			warningAlert(isValidInput());
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
			submit(e);

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
