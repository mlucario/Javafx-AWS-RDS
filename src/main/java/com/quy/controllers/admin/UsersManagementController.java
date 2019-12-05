package com.quy.controllers.admin;

import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.quy.bizcom.User;
import com.quy.controllers.Controller;
import com.quy.database.DBHandler;

import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class UsersManagementController extends Controller implements Initializable {

	@FXML
	private JFXTextField txtUser;

	@FXML
	private JFXTreeTableView<User> treetableUsers;
	@FXML
	private VBox vBoxuserPanel;

	@FXML
	private JFXButton btnActivate;

	@FXML
	private JFXButton btnLock;
	@FXML
	private JFXButton btnChangePass;

	@FXML
	private JFXTextField txtNewPassword;

	private ObservableList<User> listUsers = FXCollections.observableArrayList();
	private boolean isCurrentUserActivate;
	private String currentUserNameClicked;
	private DBHandler dbHandler;
	private Random rd;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dbHandler = new DBHandler();
		vBoxuserPanel.setVisible(false);
		textFieldFormat(txtNewPassword, "Password is required!", false);
		btnActivate.setDisable(true);
		btnLock.setDisable(true);
//		btnChangePass.setDisable(true);
		currentUserNameClicked = "";
		JFXTreeTableColumn<User, Number> sttCol = new JFXTreeTableColumn<>("STT");
		sttCol.prefWidthProperty().bind(treetableUsers.widthProperty().multiply(0.1));
		sttCol.setResizable(false);
		sttCol.setSortable(false);
		sttCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<User, Number> param) -> {
			if (sttCol.validateValue(param))
				return param.getValue().getValue().getStudentID();
			else
				return sttCol.getComputedValue(param);
		});

		JFXTreeTableColumn<User, String> usernameCol = new JFXTreeTableColumn<>("User Name");
		usernameCol.prefWidthProperty().bind(treetableUsers.widthProperty().multiply(0.4));
		usernameCol.setResizable(false);
		usernameCol.setSortable(false);
		usernameCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<User, String> param) -> {
			if (usernameCol.validateValue(param))
				return param.getValue().getValue().getUsername();
			else
				return usernameCol.getComputedValue(param);
		});
		JFXTreeTableColumn<User, Boolean> statusCol = new JFXTreeTableColumn<>("Status");
		statusCol.prefWidthProperty().bind(treetableUsers.widthProperty().multiply(0.15));
		statusCol.setResizable(false);
		statusCol.setSortable(false);
		statusCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<User, Boolean> param) -> {
			if (statusCol.validateValue(param))
				return param.getValue().getValue().getActive();
			else
				return statusCol.getComputedValue(param);
		});
		JFXTreeTableColumn<User, String> createdAtCol = new JFXTreeTableColumn<>("Created At");
		createdAtCol.prefWidthProperty().bind(treetableUsers.widthProperty().multiply(0.3));
		createdAtCol.setResizable(false);
		createdAtCol.setSortable(false);
		createdAtCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<User, String> param) -> {
			if (createdAtCol.validateValue(param))
				return (param.getValue().getValue().getCreatedAt());
			else
				return createdAtCol.getComputedValue(param);
		});

		// Get all list from database
		listUsers.addAll(dbHandler.getAllUsers());
		final TreeItem<User> root = new RecursiveTreeItem<>(listUsers, RecursiveTreeObject::getChildren);
		treetableUsers.setRoot(root);
		treetableUsers.setShowRoot(false);
		treetableUsers.getColumns().setAll(sttCol, usernameCol, statusCol, createdAtCol);

		treetableUsers.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.getButton().equals(MouseButton.PRIMARY)) {
					if (event.getClickCount() == 1) {

						currentUserNameClicked = treetableUsers.getSelectionModel().getSelectedItem().getValue()
								.getUsername().getValue();
						isCurrentUserActivate = treetableUsers.getSelectionModel().getSelectedItem().getValue()
								.getActive().getValue();
						vBoxuserPanel.setVisible(true);

						if (isCurrentUserActivate) {
							btnLock.setDisable(false);
							btnActivate.setDisable(true);
						} else {
							btnLock.setDisable(true);
							btnActivate.setDisable(false);
						}

						btnActivate.setOnAction(e -> {
							dbHandler.activeOrLockUser(currentUserNameClicked, true);

							listUsers.clear();
							listUsers.addAll(dbHandler.getAllUsers());

							btnLock.setDisable(false);
							btnActivate.setDisable(true);
						});

						btnLock.setOnAction(e -> {
							dbHandler.activeOrLockUser(currentUserNameClicked, false);
							listUsers.clear();
							listUsers.addAll(dbHandler.getAllUsers());
							btnLock.setDisable(true);
							btnActivate.setDisable(false);
						});

						if (!btnChangePass.isDisable()) {

							btnChangePass.setOnAction(e -> {

								try {
									changePassword(currentUserNameClicked, txtNewPassword.getText());
									System.out.println("newPassword    " + txtNewPassword.getText());
									btnChangePass.setDisable(true);
								} catch (NoSuchAlgorithmException e1) {
									//
									e1.printStackTrace();
								}
								txtNewPassword.clear();
							});

						}
//						String currentSelectedBarcode = treeView.getSelectionModel().getSelectedItem().getValue()
//								.getControllerBarcode().getValue().toString();
//						txtControllerBarcode.setText(currentSelectedBarcode);
//						isValidInput();
					} else {
//						currentSelectedBarcode = null;
//						txtControllerBarcode.clear();
					}
				}

			}

		});
		txtUser.textProperty().addListener((o, oldVal, newVal) -> {
			treetableUsers.setPredicate(userProp -> {
				final User user = userProp.getValue();
				return user.getUsername().getValue().contains(newVal);
			});
		});

	}

	public String formatDate(StringProperty date) {

		String[] temp1 = date.getValue().split(" ");
		if (temp1.length == 0) {
			return "";
		} else {
			String[] temp2 = temp1[0].split("-");
			return temp2[0] + "/" + temp2[1] + "/" + temp2[2];
		}

	}

	public void keyPressed() {
		boolean result = false;
		if (txtNewPassword.validate()) {
			result = true;
		}
		btnChangePass.setDisable(!result);
	}

	public void changePassword(String username, String newPassword) throws NoSuchAlgorithmException {
		rd = SecureRandom.getInstanceStrong();
		int tempRandomInt = rd.nextInt(256) + 1;
		Optional<String> tempSalt = generateSalt(tempRandomInt);
		if (tempSalt.isPresent()) {
			String tempStringSalt = tempSalt.get();
			Optional<String> hashPassword = hashPassword(newPassword, tempStringSalt);
			if (hashPassword.isPresent()) {
				String hashingPasswordKey = hashPassword.get();
				boolean result = dbHandler.changePassword(username, hashingPasswordKey, tempStringSalt);
				if (result) {
					notification = notificatioBuilder(Pos.BOTTOM_RIGHT, graphic, null, "Change Password Successfully",
							2);
					notification.showInformation();
				} else {
					warningAlert("Cannot connect and change password on database!");
				}
			}

		} else {
			warningAlert("Can not change password! Database connections fails");
			txtNewPassword.clear();
			txtNewPassword.requestFocus();

		}

	}

}
