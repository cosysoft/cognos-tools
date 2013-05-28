package com.ewell.ui.handler;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Pane;

import com.ewell.cognos.content.ContentItem;
import com.ewell.ui.util.FXMLLoaderUtil;
import com.ewell.ui.view.App;
import com.ewell.ui.view.ReportSpecEdit;

public class ContentSpecHandler implements EventHandler<ActionEvent> {

	private ContentItem content;

	private TreeItem<ContentItem> item;

	@Override
	public void handle(ActionEvent arg0) {
		final TabPane mainTab = App.mainTab;
		Tab tab = new Tab(content.getName() + "XML规范");
		mainTab.getTabs().add(tab);

		ContentItem p = item.getParent().getValue();
		ReportSpecEdit edit = new ReportSpecEdit();
		edit.setTarget(content, p);
		final Pane ta = FXMLLoaderUtil.load(
				ReportSpecEdit.class.getResource("ReportSpecEdit.fxml"), edit);

		tab.setContent(ta);
		mainTab.getSelectionModel().select(tab);

	}

	public ContentSpecHandler(ContentItem content, TreeItem<ContentItem> item) {
		super();
		this.content = content;
		this.item = item;
	}

}
