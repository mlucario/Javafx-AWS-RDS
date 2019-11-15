package com.quy.controllers.admin;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXListView;
import com.quy.bizcom.User;
import com.quy.controllers.Controller;
import com.quy.database.DBHandler;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;

public class UsersManagementController extends Controller implements Initializable{


    @FXML
    private JFXListView<User> listviewUsers;
    private ObservableList<User> listUsers = FXCollections.observableArrayList();
//	private ArrayList<User> currentUsersList;
	private DBHandler dbHandler;
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dbHandler = new DBHandler();
//		currentUsersList = new ArrayList<>();
//		currentUsersList.addAll();
		listUsers.addAll(dbHandler.getAllUsers());
		listviewUsers.setItems(listUsers);
		listviewUsers.setCellFactory(user -> new UserListViewCell());
		
	}

}
