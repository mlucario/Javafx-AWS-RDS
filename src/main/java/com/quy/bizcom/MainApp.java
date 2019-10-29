package com.quy.bizcom;

import com.quy.database.DBHandler;

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
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/SignInScene.fxml"));
        
        Scene scene = new Scene(root);
//        scene.getStylesheets().add("/styles/Styles.css");
        
        stage.setTitle("JavaFX and Maven");
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
//        stage.setResizable(false);
        stage.show();
    }


    public static void main(String[] args) {
    	DBHandler db = new DBHandler();
    	 db.getConnectionAWS();
        launch(args);
    }

}

