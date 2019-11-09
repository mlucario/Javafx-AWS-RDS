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
//	private ExecutorService exec;;
//	private SMCController currentSelectedBarcode;
	@FXML
	public boolean isValidInput() {
		boolean result = false;

		String temp = isBarcodevalid(this.txtControllerBarcode);

		if (temp.isEmpty()) {
			String barcode = getStringJFXTextField(txtControllerBarcode);
			if (dbHandler.isBarcodeExist(barcode)) {
				// Check Does it Recceived

				if (dbHandler.getStatusDone(CURRENT_STATION, barcode).equalsIgnoreCase(RECEIVING_STATION)) {
					result = true;
				} else {
					result = false;
					warningAlert("Controller is not ready to assembly. Please verify with manager.");
					txtControllerBarcode.clear();
					txtControllerBarcode.requestFocus();
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
			// add to database
			String controller_barcode = getStringJFXTextField(txtControllerBarcode);
			String result = dbHandler.assembly(controller_barcode, getCurrentTimeStamp());
			if (result.equalsIgnoreCase(controller_barcode)) {
//				addBarcodeToTable(this.barcode, controller_barcode);
				// Remove barcode if submit succeffull
//				int index = 0;
//				if(currentSelectedBarcode == null) {
//					for(SMCController smc : barcode) {
//						if(smc.getControllerBarcode().getValue().equalsIgnoreCase(controller_barcode)) {
//							break;
//						}else {
//							index++;
//						}
//					}
//					barcode.remove(index);
//				}
//				barcode.remove(currentSelectedBarcode);
				
				addBarcodeToTable(barcode, controller_barcode);
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
		btnSubmit.setDisable(true);
//		currentSelectedBarcode = null;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dbHandler = new DBHandler();
		btnSubmit.setDisable(true);
		textFieldFormat(txtControllerBarcode, "Controller barcode is required", true);
//		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

		// setup tree view
		treeviewTableBuilder(treeView, barcode, ASSEMBLY_STATION);
		
//		JFXTreeTableColumn<SMCController, String> controlBarcode = new JFXTreeTableColumn<>("Controller Barcode");
//
//		controlBarcode.prefWidthProperty().bind(treeView.widthProperty().multiply(0.97));
//		controlBarcode.setResizable(false);
//		controlBarcode.setSortable(false);
//		controlBarcode.setCellValueFactory((TreeTableColumn.CellDataFeatures<SMCController, String> param) -> {
//			if (controlBarcode.validateValue(param))
//				return param.getValue().getValue().getControllerBarcode();
//			else
//				return controlBarcode.getComputedValue(param);
//		});
//
//		final TreeItem<SMCController> root = new RecursiveTreeItem<SMCController>(barcode,
//				RecursiveTreeObject::getChildren);
//		treeView.getColumns().setAll(controlBarcode);
//		treeView.setRoot(root);
//		treeView.setShowRoot(false);
		
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

//		ArrayList<String> listBarcodeReceived = new ArrayList<>();
//
//		exec = Executors.newCachedThreadPool(runnable -> {
//			Thread t = new Thread(runnable);
//			t.setDaemon(true);
//			return t;
//		});
//
//		Task<List<String>> getAllReceivedTask = new Task<List<String>>() {
//			@Override
//			public List<String> call() throws Exception {
//				return dbHandler.getAllReceived();
//			}
//		};
//
//		getAllReceivedTask.setOnFailed(e -> {
//			getAllReceivedTask.getException().printStackTrace();
//
//			warningAlert("Cannot fetch all Received Barcode");
//		});
//
//		getAllReceivedTask.setOnSucceeded(e -> {
//
//			System.out.println("===Get all Received Barcode====");
//			listBarcodeReceived.addAll(getAllReceivedTask.getValue());
//
//			for (String s : listBarcodeReceived) {
//				addBarcodeToTable(barcode, s);
//			}
//		});
//
//		exec.execute(getAllReceivedTask);

	}

}
