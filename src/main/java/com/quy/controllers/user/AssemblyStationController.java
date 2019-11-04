package com.quy.controllers.user;

import java.net.URL;
import java.util.ResourceBundle;

import com.quy.controllers.Controller;

import javafx.fxml.Initializable;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class AssemblyStationController extends Controller implements Initializable {
    @FXML
    private JFXTextField txtControllerBarcode;

    @FXML
    private JFXListView<?> listBarcode;

    @FXML
    void submit(ActionEvent event) {

    }
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}

}
