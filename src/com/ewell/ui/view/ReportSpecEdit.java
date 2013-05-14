package com.ewell.ui.view;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import com.cognos.developer.schemas.bibus._3.BaseClass;
import com.ewell.ui.BiBusHelper;

public class ReportSpecEdit {

	private BaseClass preport;
	private BaseClass report;
	
	@FXML
	private TextArea content;

	public ReportSpecEdit() {
		super();
	}

	public void setTarget(BaseClass report, BaseClass preport) {
		this.report = report;
		this.preport = preport;
	}

	public BaseClass getReport() {
		return report;
	}

	public void setReport(BaseClass report) {
		this.report = report;
	}

	public void reload() {
		String xml = BiBusHelper.getInstance().getReportSpec(report);
		content.setText(xml);
	}

	@FXML
	public void save() {
		BiBusHelper.getInstance().editSpec(report, preport, content.getText());
	}

	public BaseClass getPreport() {
		return preport;
	}

	public void setPreport(BaseClass preport) {
		this.preport = preport;
	}

	@FXML
	void initialize() {
		reload();
	}
}
