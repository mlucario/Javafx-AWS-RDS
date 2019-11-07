
package com.quy.controllers.user;

import java.net.URL;
import java.util.ResourceBundle;

import org.controlsfx.control.Notifications;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
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
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
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

	// Needed Notification
	private Notifications notification;
	private Node graphic;

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
			String controller_barcode = getStringJFXTextField(txtControllerBarcode);
			String model = getStringJFXTextField(txtModel);

			if (!dbHandler.isBarcodeExist(controller_barcode)) {
				String result = dbHandler.addNewController(model, controller_barcode, getCurrentTimeStamp());
				if (result.equalsIgnoreCase(controller_barcode)) {

					notification = notificatioBuilder(Pos.BOTTOM_RIGHT, graphic, null,
							"Add New Controller Successfully", 2);
					notification.showInformation();
					addToTable(controller_barcode);
					dbHandler.addToHistoryRecord(currentUser, "Receiving Station", getCurrentTimeStamp(),
							controller_barcode, "");
				} else {
					warningAlert(result);
				}
			} else {
				warningAlert(controller_barcode + " already added!");
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
//		listModels = new ArrayList<>();
//
//
//		exec = Executors.newCachedThreadPool(runnable -> {
//			Thread t = new Thread(runnable);
//			t.setDaemon(true);
//			return t;
//		});
//
//		Task<List<String>> getModelsTask = new Task<List<String>>() {
//			@Override
//			public List<String> call() throws Exception {
//				return dbHandler.getAllModels();
//			}
//		};
//
//		getModelsTask.setOnFailed(e -> {
//			getModelsTask.getException().printStackTrace();
//
//			warningAlert("Cannot fetch models");
//		});
//
//		getModelsTask.setOnSucceeded(e -> {
//
//			System.out.println("get all models");
//			listModels.addAll(getModelsTask.getValue());
//			comboModel.getItems().addAll(listModels);
//		});
//
//		exec.execute(getModelsTask);
//		comboModel.setOnAction(e -> {
//			txtBoxBarcode.requestFocus();
//		});

//		txtModel.textProperty().addListener((observable, oldValue, newValue) -> {
//			if (!oldValue.equalsIgnoreCase(newValue)) {
//			
//			}
//		});

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
				if (txtBoxBarcode.getText().trim().toUpperCase()
						.equalsIgnoreCase(txtControllerBarcode.getText().trim().toUpperCase())) {
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
		JFXTreeTableColumn<SMCController, String> controlBarcode = new JFXTreeTableColumn<>("Controller Barcode");

		controlBarcode.prefWidthProperty().bind(treeView.widthProperty().multiply(0.95));
		controlBarcode.setResizable(false);
		controlBarcode.setSortable(false);

		controlBarcode.setCellValueFactory((TreeTableColumn.CellDataFeatures<SMCController, String> param) -> {
			if (controlBarcode.validateValue(param))
				return param.getValue().getValue().getControllerBarcode();
			else
				return controlBarcode.getComputedValue(param);
		});

		barcode.add(new SMCController("1111"));
		barcode.add(new SMCController("22222"));

		final TreeItem<SMCController> root = new RecursiveTreeItem<SMCController>(barcode,
				RecursiveTreeObject::getChildren);
		treeView.getColumns().setAll(controlBarcode);
		treeView.setRoot(root);
		treeView.setShowRoot(false);

	}

	public String isModelvalid(JFXTextField txtModel) {
		String result = "";

		if (txtModel.validate()) {
			String model = txtModel.getText().toUpperCase().trim();
			if (!isModelValid(model)) {
				result = "Your model is not valid. It should be style as SMC-XX Rx\r\n.";
			}
		} else {
			result = "Controller model is missing! Enter valid model.\r\n";
		}

		return result;
	}

	public String isBarcodevalid(JFXTextField txtBarcode) {
		String result = "";

		if (txtBarcode.validate()) {
			String barcode = txtBarcode.getText().toUpperCase().trim();
			if (!isBarcodeValid(barcode)) {
				result = "Your barcode is not valid. It should be style as 30N0xxxx";
			}
		} else {
			result = "Controller barcode is missing! Enter valid barcode.";
		}

		return result;
	}

	public boolean keyPressedAction() {
		boolean result = false;
		String temp1 = isModelvalid(this.txtModel);
		String temp2 = isBarcodevalid(this.txtBoxBarcode);
		String temp3 = isBarcodevalid(this.txtBoxBarcode);
		if (temp1.isEmpty() && temp2.isEmpty() && temp3.isEmpty()) {
			if (this.txtBoxBarcode.getText().trim().equalsIgnoreCase(this.txtControllerBarcode.getText().trim())) {
				result = true;
			} else {
				result = false;
			}
		}
		btnSubmit.setDisable(!result);
		return result;

	}

	public void addToTable(String barcodex) {
		barcode.add(new SMCController(barcodex));
	}
}
