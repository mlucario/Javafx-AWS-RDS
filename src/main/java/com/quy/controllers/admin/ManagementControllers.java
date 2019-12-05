package com.quy.controllers.admin;

import java.net.URL;
import java.util.ResourceBundle;

import com.quy.controllers.Controller;
import com.quy.database.DBHandler;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.quy.bizcom.SMCController;
import com.quy.bizcom.User;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;

public class ManagementControllers extends Controller implements Initializable {
	@FXML
	private JFXTreeTableView<SMCController> tableViewInventory;
	private ObservableList<SMCController> listAllControllers = FXCollections.observableArrayList();
	@FXML
	private Label txtTotalInven;

	@FXML
	private Label txtPassed;

	@FXML
	private Label txtfail;
	private DBHandler dbHandler;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dbHandler = new DBHandler();
		txtTotalInven.setText(dbHandler.countControllers("WHERE Is_Shipping_Done=?", false) + "");
		txtPassed.setText(dbHandler.countControllers("WHERE Is_Passed=?", true) + "");
		txtfail.setText(dbHandler.countControllers("WHERE Is_Passed=?", false) + "");
		JFXTreeTableColumn<SMCController, Number> sttCol = new JFXTreeTableColumn<>("STT");
		sttCol.prefWidthProperty().bind(tableViewInventory.widthProperty().multiply(0.1));
		sttCol.setResizable(false);
		sttCol.setSortable(false);
		sttCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<SMCController, Number> param) -> {
			if (sttCol.validateValue(param))
				return param.getValue().getValue().getSTT();
			else
				return sttCol.getComputedValue(param);
		});

		JFXTreeTableColumn<SMCController, String> serialNumberCol = new JFXTreeTableColumn<>("Serial Number");
		serialNumberCol.prefWidthProperty().bind(tableViewInventory.widthProperty().multiply(0.3));
		serialNumberCol.setResizable(false);
		serialNumberCol.setSortable(false);
		serialNumberCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<SMCController, String> param) -> {
			if (serialNumberCol.validateValue(param))
				return param.getValue().getValue().getSerialNumber();
			else
				return serialNumberCol.getComputedValue(param);
		});

		JFXTreeTableColumn<SMCController, String> burnInResultCol = new JFXTreeTableColumn<>("Burn In Result");
		burnInResultCol.prefWidthProperty().bind(tableViewInventory.widthProperty().multiply(0.2));
		burnInResultCol.setResizable(false);
		burnInResultCol.setSortable(false);
		burnInResultCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<SMCController, String> param) -> {
			if (burnInResultCol.validateValue(param))
				return param.getValue().getValue().getResultBurnIn();
			else
				return burnInResultCol.getComputedValue(param);
		});
		listAllControllers.addAll(dbHandler.getAllControllers());
		final TreeItem<SMCController> root = new RecursiveTreeItem<>(listAllControllers,
				RecursiveTreeObject::getChildren);
		tableViewInventory.setRoot(root);
		tableViewInventory.setShowRoot(false);
		tableViewInventory.getColumns().setAll(sttCol, serialNumberCol, burnInResultCol);
	}

}
