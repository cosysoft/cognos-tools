package com.ewell.ui.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuBuilder;
import javafx.scene.control.MenuItemBuilder;
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

import com.ewell.cognos.content.ContentItem;
import com.ewell.ui.CognosTools;
import com.ewell.ui.cell.DefaultContentCell;
import com.ewell.ui.menu.action.BatchReportEdit;

/**
 * 
 * @author Bluesky Yao
 * 
 */
public class App extends AnchorPane {

	@FXML
	private TreeView<ContentItem> contentNav;
	@FXML
	private WebView browser;
	@FXML
	private ProgressBar progressBar;

	@FXML
	public static TabPane mainTab;

	@FXML
	private MenuBar menuBar;

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

	@FXML
	public void initialize() {
		contentNav
				.setCellFactory(new Callback<TreeView<ContentItem>, TreeCell<ContentItem>>() {
					@Override
					public TreeCell<ContentItem> call(TreeView<ContentItem> arg0) {
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

		menuBar.getMenus().clear();

		Menu file = MenuBuilder
				.create()
				.text("文件")
				.items(MenuItemBuilder.create().text("新建").build(),
						MenuItemBuilder.create().text("新建批量更新")
								.onAction(new BatchReportEdit()).build(),
						MenuItemBuilder.create().text("退出")
								.onAction(new EventHandler<ActionEvent>() {

									@Override
									public void handle(ActionEvent arg0) {
										System.exit(0);

									}

								}).build()).build();

		menuBar.getMenus().addAll(file);

	}

}
