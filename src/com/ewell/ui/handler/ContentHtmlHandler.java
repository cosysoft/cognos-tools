package com.ewell.ui.handler;

import java.net.URLEncoder;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.web.WebView;

import com.cognos.developer.schemas.bibus._3.BaseClass;
import com.ewell.ui.BiBusHelper;
import com.ewell.ui.view.App;
import com.ibm.cognos.CRNConnect;

public class ContentHtmlHandler implements EventHandler<ActionEvent> {

	private BaseClass content;

	@Override
	public void handle(ActionEvent arg0) {
		TabPane mainTab = App.mainTab;
		Tab tab = new Tab(content.getDefaultName().getValue());

		mainTab.getTabs().add(tab);

		WebView view = new WebView();
		tab.setContent(view);

		mainTab.getSelectionModel().select(tab);

		try {
			String url = CRNConnect.GATEWAY_URL
					+ "&ui.object="
					+ URLEncoder.encode(content.getSearchPath().getValue(),
							"utf-8")
					+ "&ui.name="
					+ URLEncoder.encode(content.getDefaultName().getValue(),
							"utf-8") + "&m_passportID="
					+ BiBusHelper.getInstance().getConnect().getPassPort();

			System.out.println(url);
			view.getEngine().load(url);

		} catch (Exception e) {

		}
	}

	public ContentHtmlHandler(BaseClass content) {
		super();
		this.content = content;
	}

}
