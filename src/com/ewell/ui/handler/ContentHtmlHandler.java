package com.ewell.ui.handler;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.web.WebView;

import com.ewell.cognos.content.ContentItem;
import com.ewell.ui.CognosTools;
import com.ewell.ui.util.CMUtil;
import com.ewell.ui.view.App;

public class ContentHtmlHandler implements EventHandler<ActionEvent> {

	private ContentItem content;

	@Override
	public void handle(ActionEvent arg0) {
		TabPane mainTab = App.mainTab;
		Tab tab = new Tab(content.getName());

		mainTab.getTabs().add(tab);

		WebView view = new WebView();
		tab.setContent(view);

		mainTab.getSelectionModel().select(tab);
		String url = CMUtil.encodeContentUrl(content,CognosTools.skeleton.getConnect());
		view.getEngine().load(url);

	}

	public ContentHtmlHandler(ContentItem content) {
		super();
		this.content = content;
	}

}
