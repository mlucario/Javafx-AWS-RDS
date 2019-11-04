package com.quy.controllers.user;

import java.net.URL;
import java.util.ResourceBundle;

import com.quy.controllers.Controller;

import javafx.fxml.Initializable;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class BurnInController extends Controller implements Initializable {
	@FXML
	private Text txtCompleted;

	@FXML
	private JFXTextField txtControllerBarcode;

	@FXML
	private JFXListView<?> listBarcode;

	@FXML
	void addToBurnInList(ActionEvent event) {

	}

	@FXML
	void startBurnIn(ActionEvent event) {

	}
	
	private boolean isBurnInStarted;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		isBurnInStarted = false;

	}

}
