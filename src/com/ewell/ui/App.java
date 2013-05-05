package com.ewell.ui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.Popup;
import javafx.util.Callback;

import com.cognos.developer.schemas.bibus._3.BaseClass;
import com.ewell.ui.cell.DefaultContentCell;

/**
 * 
 * @author Bluesky Yao
 * 
 */
public class App extends AnchorPane implements Initializable {

	@FXML
	private TreeView<BaseClass> contentNav;
	@FXML
	private WebView browser;
	@FXML
	private ProgressBar progressBar;

	@FXML
	public static TabPane mainTab;

	@FXML
	public void show(ActionEvent e) {
		Popup popup = new Popup();
		popup.setHeight(500);
		popup.setWidth(600);
		popup.getScene().setRoot(
				HBoxBuilder.create().style("-fx-background-color:red;")
						.prefHeight(300).prefWidth(300).build());

		popup.show(CognosTools.skeleton.getMainWindow());
		popup.centerOnScreen();

	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		contentNav
				.setCellFactory(new Callback<TreeView<BaseClass>, TreeCell<BaseClass>>() {
					@Override
					public TreeCell<BaseClass> call(TreeView<BaseClass> arg0) {
						return new DefaultContentCell();
					}
				});

		browser.getEngine().load("http://cosysoft.github.io/index.zh_CN.html");
		browser.getEngine().setOnStatusChanged(
				new EventHandler<WebEvent<String>>() {

					@Override
					public void handle(WebEvent<String> arg0) {

					}
				});
		progressBar.setProgress(.8);

	}

}
