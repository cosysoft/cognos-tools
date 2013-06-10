package com.ewell.ui.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeView;
import javafx.stage.Popup;

import com.ewell.cognos.content.ContentItem;
import com.ewell.ui.CognosTools;

public class ReportSpecEditBatch {

	TreeView<ContentItem> ts;

	@FXML
	private TextArea srcContent;

	@FXML
	private TextArea tarContent;

	@FXML
	void reload(ActionEvent event) {
		System.out.println(ts.getSelectionModel().getSelectedItems());
	}

	@FXML
	void save(ActionEvent event) {
		if (ts == null) {
			ts = CognosTools.skeleton.getChoiceView();
		}

		Popup popup = new Popup();
		popup.getContent().add(ts);

		popup.show(CognosTools.skeleton.getMainWindow());
	}

	@FXML
	void initialize() {

	}

}
