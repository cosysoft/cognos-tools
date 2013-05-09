package com.ewell.ui.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

import com.cognos.developer.schemas.bibus._3.BaseClass;
import com.ewell.ui.BiBusHelper;

public class ReportSpec extends AnchorPane {

	private BaseClass preport;
	private BaseClass report;

	// @FXML
	private TextArea content;

	public ReportSpec() {
		super();
		// FXMLLoader fxmlLoader = new FXMLLoader(new url);
		// fxmlLoader.setRoot(this);

		// try {
		// fxmlLoader.load();
		// } catch (IOException exception) {
		// throw new RuntimeException(exception);
		// }

		content = new TextArea();
		reload();
		Button saveBtn = new Button("save");

		saveBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				ReportSpec.this.save();

			}
		});

		getChildren().addAll(content, saveBtn);
	}

	public void setTarget(BaseClass report, BaseClass preport) {

		this.report = report;
		this.preport = preport;
		reload();

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

	// @FXML
	public void save() {
		BiBusHelper.getInstance().editSpec(report, preport, content.getText());
	}

	public BaseClass getPreport() {
		return preport;
	}

	public void setPreport(BaseClass preport) {
		this.preport = preport;
	}

}
