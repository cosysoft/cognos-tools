package com.ewell.ui.menu.action;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;

import com.ewell.ui.util.FXMLLoaderUtil;
import com.ewell.ui.view.App;
import com.ewell.ui.view.ReportSpecEditBatch;

public class BatchReportEdit implements EventHandler<ActionEvent> {

	@Override
	public void handle(ActionEvent arg0) {
		final TabPane mainTab = App.mainTab;
		Tab tab = new Tab("XML规范");
		mainTab.getTabs().add(tab);

		ReportSpecEditBatch edit = new ReportSpecEditBatch();
		final Pane ta = FXMLLoaderUtil.load(
				ReportSpecEditBatch.class.getResource("ReportSpecEditBatch.fxml"), edit);

		tab.setContent(ta);
		mainTab.getSelectionModel().select(tab);

	}

}
