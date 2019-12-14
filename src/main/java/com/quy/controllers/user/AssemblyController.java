package com.quy.controllers.user;

import java.net.URL;
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

public class AssemblyController extends Controller implements Initializable {
	@FXML
	private JFXTextField txtControllerBarcode;
	@FXML
	private Text txtCounter;
	@FXML
	private JFXTreeTableView<SMCController> treeView;
	@FXML
	private JFXButton btnSubmit;
	@FXML
	private JFXTextArea txtNote;
	@FXML
	private JFXTextField txtSN;
	private DBHandler dbHandler;
	private String currentUser = SignInController.getInstance().username();
	private ObservableList<SMCController> barcode = FXCollections.observableArrayList();

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
					case RE_WORK_STATION:
					case RECEIVING_STATION:
					case PACKING_STATION:
						result = "";
						break;
					case ASSEMBLY_STATION:
					case FIRMWARE_UPDATE_STATION:
					case BURN_IN_STATION:
					case WAIT_TO_BURN_IN:
					case RESULT_STATION:
					case REPAIR_STATION:
						result = "\r\n Assemsbly was DONE! Go Re_Work or check with manager.";
						break;
					default:
						result = "Special Case";
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

			String timestamp = getCurrentTimeStamp();
			String model = dbHandler.getStatusDone(COL_MODEL_CONTROLER, serialNumber);
			int count = Integer.parseInt(dbHandler.getStatusDone(COL_ASSEMBLY_COUNT_CONTROLER, serialNumber));
			String result = dbHandler.assembly(serialNumber, timestamp, ++count);
			if (result.equalsIgnoreCase(serialNumber)) {

				addBarcodeToTable(barcode, serialNumber, model);
				String history = dbHandler.addToHistoryRecord(currentUser, ASSEMBLY_STATION, timestamp, serialNumber,
						"Assembler Controller Serial Number : " + serialNumber + ". " + txtNote.getText());
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
			warningAlert(isValidInput());
		}

		txtControllerBarcode.clear();
		txtControllerBarcode.requestFocus();
		btnSubmit.setDisable(true);
		txtCounter.textProperty().bind(Bindings.format("%d", barcode.size()));

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dbHandler = new DBHandler();
		btnSubmit.setDisable(true);

		textFieldFormat(txtControllerBarcode, "Controller barcode is required", true);
		Platform.runLater(() -> txtControllerBarcode.requestFocus());
		txtControllerBarcode.setOnAction(e -> {
			submit(e);

		});
		// setup tree view
		barcode.addAll(dbHandler.getAllAssemblyDone());
		treeviewTableBuilder(treeView, barcode);

		txtCounter.textProperty().bind(Bindings.format("%d", barcode.size()));
		txtSN.textProperty().addListener((o, oldVal, newVal) -> {
			if (!oldVal.equalsIgnoreCase(newVal)) {
				treeView.setPredicate(controller -> {
					final SMCController aController = controller.getValue();
					return aController.getSerialNumber().getValue().contains(newVal);
				});
			}
		});
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
