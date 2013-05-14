package com.ewell.ui.handler;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Pane;

import com.cognos.developer.schemas.bibus._3.BaseClass;
import com.ewell.ui.util.FXMLLoaderUtil;
import com.ewell.ui.view.App;
import com.ewell.ui.view.ReportSpecEdit;

public class ContentSpecHandler implements EventHandler<ActionEvent> {

	private BaseClass content;

	private TreeItem<BaseClass> item;

	@Override
	public void handle(ActionEvent arg0) {
		final TabPane mainTab = App.mainTab;
		Tab tab = new Tab(content.getDefaultName().getValue() + "XML规范");
		mainTab.getTabs().add(tab);

		BaseClass p = item.getParent().getValue();
		ReportSpecEdit edit = new ReportSpecEdit();
		edit.setTarget(content, p);
		final  Pane ta =   FXMLLoaderUtil.load(
				ReportSpecEdit.class.getResource("ReportSpecEdit.fxml"), edit);

//		mainTab.widthProperty().addListener(new InvalidationListener() {
//			@Override
//			public void invalidated(Observable arg0) {
//				ta.setPrefHeight(mainTab.getHeight());
//			}
//		});

		tab.setContent(ta);
		mainTab.getSelectionModel().select(tab);

	}

	public ContentSpecHandler(BaseClass content, TreeItem<BaseClass> item) {
		super();
		this.content = content;
		this.item = item;
	}

}
