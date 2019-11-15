package com.quy.controllers.admin;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.Notifications;

import com.jfoenix.controls.JFXButton;
import com.quy.bizcom.User;
import com.quy.database.DBHandler;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class UserListViewCell extends ListCell<User> {

	@FXML
	private GridPane gridPane;

	@FXML
	private Label lbSTT;

	@FXML
	private Label lbUsername;

	@FXML
	private Label lbUserStatus;

	@FXML
	private Label lbCreatedAt;

	@FXML
	private JFXButton btnActive;

	@FXML
	private JFXButton btnDisable;

	private FXMLLoader mLLoader;

	private DBHandler dbhandler;
	protected static final Logger LOGGER = LogManager.getLogger("Controller");

	@Override
	protected void updateItem(User user, boolean empty) {
		super.updateItem(user, empty);

		if (empty || user == null) {

			setText(null);
			setGraphic(null);

		} else {
			if (mLLoader == null) {

				mLLoader = new FXMLLoader(getClass().getResource("/fxml/ui/admin/UserListCell.fxml"));
				mLLoader.setController(this);

				try {
					mLLoader.load();
				} catch (IOException e) {
					LOGGER.error("listcell" + " ", e.getMessage());
				}

			}
			String username = user.getUsername().getValue();
			btnDisable.setOnAction(e -> {
				dbhandler = new DBHandler();
				if (dbhandler.activeOrLockUser(username, false).equalsIgnoreCase(username)) {
					Notifications notification = Notifications.create().title("Account Locked")
							.text(username + " is locked!").graphic(null).hideAfter(Duration.seconds(2))
							.position(Pos.CENTER).onAction(new EventHandler<ActionEvent>() {

								@Override
								public void handle(ActionEvent e) {
									System.out.println("Notification clicked. No any action needed so far.");

								}
							});
					notification.showInformation();
					btnDisable.setDisable(true);
					btnActive.setDisable(false);
				}

			});
			btnActive.setOnAction(e -> {
				dbhandler = new DBHandler();
				if (dbhandler.activeOrLockUser(username, true).equalsIgnoreCase(username)) {
					Notifications notification = Notifications.create().title("Account Activated")
							.text(username + " is activated!").graphic(null).hideAfter(Duration.seconds(2))
							.position(Pos.CENTER).onAction(new EventHandler<ActionEvent>() {

								@Override
								public void handle(ActionEvent e) {
									System.out.println("Notification clicked. No any action needed so far.");

								}
							});
					notification.showInformation();

					btnDisable.setDisable(false);
					btnActive.setDisable(true);

				}

			});

			lbSTT.setText(user.getStudentID());
			lbUsername.setText(username.toUpperCase());
			String statusAcc = formatStatus(user.getActive().getValue().toString());
			if (statusAcc.equalsIgnoreCase("Active")) {
				lbUserStatus.setTextFill(Color.web("#1B5E20"));
				btnDisable.setDisable(false);
				btnActive.setDisable(true);
			} else {
				lbUserStatus.setTextFill(Color.web("#b71c1c"));
				btnDisable.setDisable(true);
				btnActive.setDisable(false);
			}
			lbUserStatus.setText(statusAcc);
			lbCreatedAt.setText(formatDate(user.getCreatedAt().getValue()));

			setText(null);
			setGraphic(gridPane);
		}

	}

	public String formatStatus(String status) {
		if (status.equalsIgnoreCase("true")) {
			return "ACTIVE";
		} else {
			return "LOCKED";
		}
	}

	public String formatDate(String date) {
		String[] temp1 = date.split(" ");
		if (temp1.length == 0) {
			return "";
		} else {
			String[] temp2 = temp1[0].split("-");
			return temp2[0] + "/" + temp2[1] + "/" + temp2[2];
		}

	}

}
