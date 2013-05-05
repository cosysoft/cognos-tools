package com.ewell.ui.cell;

import java.net.URLEncoder;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItemBuilder;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeCell;
import javafx.scene.web.WebView;

import com.cognos.developer.schemas.bibus._3.BaseClass;
import com.ewell.ui.App;
import com.ewell.ui.BiBusHelper;
import com.ibm.cognos.CRNConnect;

/**
 * 
 * @author Bluesky Yao
 * 
 */
public class DefaultContentCell extends TreeCell<BaseClass> {

	public DefaultContentCell() {
		itemProperty().addListener(new ChangeListener<BaseClass>() {

			@Override
			public void changed(ObservableValue<? extends BaseClass> arg0,
					BaseClass arg1, final BaseClass arg2) {

				if (arg2 == null)
					setText(" ");
				else {
					setText(arg2.getDefaultName().getValue());

					try {

						ContextMenu cm = new ContextMenu(MenuItemBuilder
								.create().text("预览")
								.onAction(new EventHandler<ActionEvent>() {

									@Override
									public void handle(ActionEvent arg0) {

										TabPane mainTab = App.mainTab;
										Tab tab = new Tab(arg2.getDefaultName()
												.getValue());

										mainTab.getTabs().add(tab);

										WebView view = new WebView();
										tab.setContent(view);

										try {
											String url = CRNConnect.GATEWAY_URL
													+ "&ui.object="
													+ URLEncoder.encode(arg2
															.getSearchPath()
															.getValue(),
															"utf-8")
													+ "&ui.name="
													+ URLEncoder.encode(arg2
															.getDefaultName()
															.getValue(),
															"utf-8")
													+ "&m_passportID="
													+ BiBusHelper.connection
															.getPassPort();

											System.out.println(url);
											view.getEngine().load(url);

										} catch (Exception e) {

										}

									}
								}).build());

						// if
						// (arg2.getObjectClass().equals(ClassEnum.value136)){
						System.out.println(arg2.getObjectClass().getClass());
						System.out.println(arg2.getObjectClass());

						// }
						setContextMenu(cm);

						// setTooltip(new Tooltip(url));

					} catch (Exception e) {
						e.printStackTrace();
					}

				}

			}
		});
	}
}
