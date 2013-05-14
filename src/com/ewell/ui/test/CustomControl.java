package com.ewell.ui.test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import com.ewell.ui.util.FXMLLoaderUtil;

public class CustomControl extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		Pane a = (Pane) FXMLLoaderUtil.load(ViewController.class.getResource("View.fxml"),new ViewController());
	
		Scene scene = new Scene(a);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
