package com.ewell.ui.view;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import com.ewell.cognos.content.ContentItem;
import com.ewell.ui.CognosTools;

public class ReportSpecEdit {

	private ContentItem preport;
	private ContentItem report;
	
	@FXML
	private TextArea content;

	public ReportSpecEdit() {
		super();
	}

	public void setTarget(ContentItem report, ContentItem preport) {
		this.report = report;
		this.preport = preport;
	}

	public ContentItem getReport() {
		return report;
	}

	public void setReport(ContentItem report) {
		this.report = report;
	}

	public void reload() {
		String xml = CognosTools.skeleton.getCMFacade().getReportSpec(report);
		content.setText(xml);
	}

	@FXML
	public void save() {
		CognosTools.skeleton.getCMFacade().editReportSpec(report, preport, content.getText());
	}

	public ContentItem getPreport() {
		return preport;
	}

	public void setPreport(ContentItem preport) {
		this.preport = preport;
	}

	@FXML
	void initialize() {
		reload();
	}
}
