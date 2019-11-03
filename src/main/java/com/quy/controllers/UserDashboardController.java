package com.quy.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


public class UserDashboardController extends Controller implements Initializable{

    @FXML
    private Label txtUsername;

    @FXML
    private JFXButton btnReceiving;

    @FXML
    private Text txtTitleStation;

    @FXML
    private VBox resultView;

    @FXML
    private JFXTextField txtControllerBarcode1;

    @FXML
    private JFXButton btnAddReceiving1;

    @FXML
    private HBox receivingView;

    @FXML
    private VBox vBox01;

    @FXML
    private JFXTextField txtModelReceiving;

    @FXML
    private JFXTextField txtBoxBarcodeReceiving;

    @FXML
    private HBox txtControllerBarcodeReceiving;

    @FXML
    private JFXTextField txtControllerBarcode;

    @FXML
    private JFXButton btnAddReceiving;

    @FXML
    private Text txtCompletedStatus;

    @FXML
    private ImageView imgViewGuide;

    @FXML
    private JFXTreeTableColumn<?, ?> colControllerBarcode;

    @FXML
    void addToReceivingStock(ActionEvent event) {

    }

    @FXML
    void receivingOnClick(ActionEvent event) {

    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}

}
