package com.quy.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class TitleBarController implements Initializable{

  
    @FXML
    void close(MouseEvent event) {
    	 Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    	 System.exit(0);
		stage.close();
    }

    @FXML
    void maximize(MouseEvent event) {
    	 Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    	 if(stage.isFullScreen()) {
    		 stage.setFullScreen(false);
    	 }else {
    		 stage.setFullScreen(true);
    	 }
    	
    }

    @FXML
    void minimize(MouseEvent event) {
    	 Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    	stage.setIconified(true);
    }
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		
	}

}
