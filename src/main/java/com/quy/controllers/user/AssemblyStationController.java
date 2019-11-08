package com.quy.controllers.user;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;

public class AssemblyStationController extends Controller implements Initializable {
	@FXML
	private JFXTextField txtControllerBarcode;

	@FXML
	private JFXTreeTableView<SMCController> treeView;
	@FXML
	private JFXButton btnSubmit;
	private DBHandler dbHandler;
	protected String currentUser = SignInController.getInstance().username();
	private ObservableList<SMCController> barcode = FXCollections.observableArrayList();
	private ExecutorService exec;;

	@FXML
	public boolean isValidInput() {
		boolean result = false;

		String temp = isBarcodevalid(this.txtControllerBarcode);

		if (temp.isEmpty()) {
			String barcode = getStringJFXTextField(txtControllerBarcode);
			if (dbHandler.isBarcodeExist(barcode)) {
				// Check Does it Recceived

				if (dbHandler.getStatusDone(IS_RECEIVED, barcode).equalsIgnoreCase("1")) {
					result = true;
				} else {
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
		if (!txtControllerBarcode.validate()) {
			warningAlert("Controller barcode is missing! Enter Controller barcode.");
			txtControllerBarcode.clear();
			txtControllerBarcode.requestFocus();
		} else if (isValidInput()) {
			// add to dataabase
			String controller_barcode = getStringJFXTextField(txtControllerBarcode);
			String result = dbHandler.assembly(controller_barcode, getCurrentTimeStamp());
			if (result.equalsIgnoreCase(controller_barcode)) {
				addBarcodeToTable(this.barcode, controller_barcode);
				dbHandler.addToHistoryRecord(currentUser, "Assembly Station", getCurrentTimeStamp(), controller_barcode,
						"");
			} else {
				warningAlert(result);
			}
		} else {
			warningAlert("Controller serial number has problem. Check with manager.");
		}

		txtControllerBarcode.clear();
		txtControllerBarcode.requestFocus();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dbHandler = new DBHandler();
		btnSubmit.setDisable(true);
		textFieldFormat(txtControllerBarcode, "Controller barcode is required", true);
		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

		// setup tree view
		JFXTreeTableColumn<SMCController, String> controlBarcode = new JFXTreeTableColumn<>("Controller Barcode");

		controlBarcode.prefWidthProperty().bind(treeView.widthProperty().multiply(0.97));
		controlBarcode.setResizable(false);
		controlBarcode.setSortable(false);
		controlBarcode.setCellValueFactory((TreeTableColumn.CellDataFeatures<SMCController, String> param) -> {
			if (controlBarcode.validateValue(param))
				return param.getValue().getValue().getControllerBarcode();
			else
				return controlBarcode.getComputedValue(param);
		});

		final TreeItem<SMCController> root = new RecursiveTreeItem<SMCController>(barcode,
				RecursiveTreeObject::getChildren);
		treeView.getColumns().setAll(controlBarcode);
		treeView.setRoot(root);
		treeView.setShowRoot(false);
		treeView.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.getButton().equals(MouseButton.PRIMARY)) {
					if (event.getClickCount() == 2) {
						String currentSelectedBarcode = treeView.getSelectionModel().getSelectedItem().getValue()
								.getControllerBarcode().getValue().toString();
						System.out.println(currentSelectedBarcode);
						txtControllerBarcode.setText(currentSelectedBarcode);
						isValidInput();
					} else {
						txtControllerBarcode.clear();
					}
				}

			}

		});

		ArrayList<String> listBarcodeReceived = new ArrayList<>();

		exec = Executors.newCachedThreadPool(runnable -> {
			Thread t = new Thread(runnable);
			t.setDaemon(true);
			return t;
		});

		Task<List<String>> getAllReceivedTask = new Task<List<String>>() {
			@Override
			public List<String> call() throws Exception {
				return dbHandler.getAllReceived();
			}
		};

		getAllReceivedTask.setOnFailed(e -> {
			getAllReceivedTask.getException().printStackTrace();

			warningAlert("Cannot fetch all Received Barcode");
		});

		getAllReceivedTask.setOnSucceeded(e -> {

			System.out.println("===Get all Received Barcode====");
			listBarcodeReceived.addAll(getAllReceivedTask.getValue());

			for (String s : listBarcodeReceived) {
				addBarcodeToTable(barcode, s);
			}
		});

		exec.execute(getAllReceivedTask);

	}

}
