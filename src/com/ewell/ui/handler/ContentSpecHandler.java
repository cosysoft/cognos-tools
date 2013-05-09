package com.ewell.ui.handler;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;

import com.cognos.developer.schemas.bibus._3.BaseClass;
import com.ewell.ui.App;
import com.ewell.ui.view.ReportSpec;

public class ContentSpecHandler implements EventHandler<ActionEvent> {

	private BaseClass content;

	private TreeItem<BaseClass> item;

	@Override
	public void handle(ActionEvent arg0) {
		TabPane mainTab = App.mainTab;
		Tab tab = new Tab(content.getDefaultName().getValue() + "XML规范");
		mainTab.getTabs().add(tab);

		BaseClass p = item.getParent().getValue();
		ReportSpec ta = new ReportSpec();
		ta.setTarget(content, p);
		tab.setContent(ta);
		mainTab.getSelectionModel().select(tab);
	}

	public ContentSpecHandler(BaseClass content, TreeItem<BaseClass> item) {
		super();
		this.content = content;
		this.item = item;
	}

}
