package com.quy.controllers.user;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.quy.bizcom.SMCController;
import com.quy.controllers.Controller;
import com.quy.database.DBHandler;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class ReceivingController extends Controller implements Initializable {
	@FXML
	private Text txtCompleted;

	@FXML
	private JFXComboBox<String> comboModel;

	@FXML
	private JFXTextField txtBoxBarcode;

	@FXML
	private JFXTextField txtControllerBarcode;

	@FXML
	private ImageView imgGuide;

	@FXML
	private JFXTreeTableView<SMCController> treeView;

	private Executor exec;
	private DBHandler dbHandler;
	private List<String> listModels;
	private ObservableList<SMCController> barcode = FXCollections.observableArrayList();

	@FXML
	void submit(ActionEvent event) {
		// check if model is selected

	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dbHandler = new DBHandler();
		listModels = new ArrayList<>();
		textFieldFormat(txtBoxBarcode, "Box barcode is required", true);
		textFieldFormat(txtControllerBarcode, "Controller barcode is required", true);
		setImage("model.JPG", imgGuide);

		// create executor that uses daemon threads:
		exec = Executors.newCachedThreadPool(runnable -> {
			Thread t = new Thread(runnable);
			t.setDaemon(true);
			return t;
		});

		Task<List<String>> getModelsTask = new Task<List<String>>() {
			@Override
			public List<String> call() throws Exception {
				return dbHandler.getAllModels();
			}
		};

		getModelsTask.setOnFailed(e -> {
			getModelsTask.getException().printStackTrace();

			warningAlert("Cannot fetch models");
		});

		getModelsTask.setOnSucceeded(e -> {
			System.out.println("get all models");
			listModels.addAll(getModelsTask.getValue());
			// set for JFXComboBox
			comboModel.getItems().addAll(listModels);
		});

		exec.execute(getModelsTask);
		comboModel.setOnAction(e -> {
			txtBoxBarcode.requestFocus();
		});

		txtBoxBarcode.setOnAction(e -> {
			txtControllerBarcode.requestFocus();
		});
//		comboModel.getSelectionModel().selectedIndexProperty().addListener((options, oldValue, newValue) -> {
//			if (comboModel.getSelectionModel().getSelectedItem() == null) {
//				warningAlert("Please select one model to continute!");
//			} else {
//				System.out.println(newValue);
//			}
//		});

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

		barcode.add(new SMCController("1111"));
		barcode.add(new SMCController("22222"));

		final TreeItem<SMCController> root = new RecursiveTreeItem<SMCController>(barcode,
				RecursiveTreeObject::getChildren);
		treeView.getColumns().setAll(controlBarcode);
		treeView.setRoot(root);
		treeView.setShowRoot(false);

	}

}
