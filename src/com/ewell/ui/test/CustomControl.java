package com.ewell.ui.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class CustomControl extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		Pane a = FXMLLoader.load(getClass().getResource("View.fxml"));
	
		Scene scene = new Scene(a);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
