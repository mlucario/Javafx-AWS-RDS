package com.quy.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class SignUpController extends Controller implements Initializable{
    @FXML
    private JFXTextField txtUsername;

    @FXML
    private JFXPasswordField txtPassword1;

    @FXML
    private JFXPasswordField txtPassword2;

    @FXML
    void signInPressed(ActionEvent event) {

    }

    @FXML
    void signUpAction(ActionEvent event) {

    }
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}

}
