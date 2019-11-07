
package com.quy.controllers.user;

import java.net.URL;
import java.util.ResourceBundle;

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
			String controller_barcode = txtControllerBarcode.getText().trim().toUpperCase();
			String model = txtModel.getText().trim().toUpperCase();

			if (!dbHandler.isBarcodeExist(controller_barcode)) {
				String result = dbHandler.addNewController(model, controller_barcode, getCurrentTimeStamp());
				if (result.equalsIgnoreCase(controller_barcode)) {
					dbHandler.addToHistoryRecord(currentUser, "Receiving Station", getCurrentTimeStamp(),
							controller_barcode, "");
				} else {
					warningAlert(result);
				}
			} else {
				warningAlert(controller_barcode + " already added!");
			}
		}

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dbHandler = new DBHandler();
		textFieldFormat(txtBoxBarcode, "Box barcode is required", true);
		textFieldFormat(txtControllerBarcode, "Controller barcode is required", true);
		textFieldFormat(txtModel, "Controller Model is required", true);
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
			if (!isModelValid(txtModel.getText())) {
				String headerText = (txtModel.getText())
						+ " is not good style(SMC-xxx Rxx). Are you sure it is corrected?";
				String contentText = "OK to keep your input. Cancel to avoid it.";
				if (!warningComfirmAlert(headerText, contentText)) {
					System.out.println("Model is not style");
					txtModel.clear();
					txtModel.requestFocus();
				}

			}
			txtBoxBarcode.requestFocus();

		});
		txtBoxBarcode.setOnAction(e -> {
			if (!isBarcodeValid(txtBoxBarcode.getText())) {
				String headerText = (txtBoxBarcode.getText())
						+ " is not good style(30N0XXXXXXXX). Are you sure it is corrected?";
				String contentText = "OK to keep your input. Cancel to avoid/CLEAR it.";
				if (!warningComfirmAlert(headerText, contentText)) {
					txtBoxBarcode.clear();
					txtBoxBarcode.requestFocus();
				} else {

				}
			}
			txtControllerBarcode.requestFocus();

		});
		txtControllerBarcode.setOnAction(e -> {
			if (!isBarcodeValid(txtControllerBarcode.getText())) {
				String headerText = (txtControllerBarcode.getText())
						+ " is not good style(30N0XXXXXXXX). Are you sure it is corrected?";
				String contentText = "OK to keep your input. Cancel to avoid/CLEAR it.";
				if (!warningComfirmAlert(headerText, contentText)) {
					txtControllerBarcode.clear();
					txtControllerBarcode.requestFocus();
				}
			}
			submit(e);
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
	
	public String isModelvalid() {
		String result="";
		
		if(txtModel.validate()) {
			String model = txtModel.getText().toUpperCase().trim();
			if(!isModelValid(model)) {
				result = "Your model is not valid. It should be style as SMC-XX Rx";
			}
		}else {
			result = "Controller model is missing! Enter valid model.";
		}
		
		return result;
	}

	public String isBarcodevalid( JFXTextField txtBarcode) {
		String result="";
		
		if(txtBarcode.validate()) {
			String barcode = txtBarcode.getText().toUpperCase().trim();
			if(!isBarcodeValid(barcode)) {
				result = "Your barcode is not valid. It should be style as 30N0xxxx";
			}
		}else {
			result = "Controller barcode is missing! Enter valid barcode.";
		}
		
		return result;
	}
}
