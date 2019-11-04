package com.quy.bizcom;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainApp extends Application {
//	private double x, y;

//	@FXML
//	void dragged(MouseEvent event) {
//		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//		stage.setX(event.getScreenX() - x);
//		stage.setY(event.getScreenY() - y);
//	}
//
//	@FXML
//	void pressed(MouseEvent event) {
//		x = event.getSceneX();
//		y = event.getSceneY();
//	}

	@Override
	public void start(Stage stage) throws Exception {

//        Parent root = FXMLLoader.load(getClass().getResource("/fxml/SignInScene.fxml"));
		Parent root = FXMLLoader.load(getClass().getResource("/fxml/UserDashboardScene.fxml"));

		Scene scene = new Scene(root);
//		scene.getStylesheets().add(MainApp.class.getResource("/styles/fonts.css").toExternalForm());
		scene.getStylesheets().add(MainApp.class.getResource("/styles/Styles.css").toExternalForm());
		stage.setTitle("SMC Controller Management");
		stage.setScene(scene);
		stage.initStyle(StageStyle.TRANSPARENT);
//        stage.setResizable(false);
		stage.setMinWidth(700);
		stage.setMinHeight(800);
//		stage.setAlwaysOnTop(true);
		stage.show();

	}

	public static void main(String[] args) {

		launch(args);
	}

}
