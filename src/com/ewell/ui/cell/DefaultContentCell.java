package com.ewell.ui.cell;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItemBuilder;
import javafx.scene.control.TreeCell;

import com.cognos.developer.schemas.bibus._3.BaseClass;
import com.cognos.developer.schemas.bibus._3.ClassEnum;
import com.ewell.ui.handler.ContentHtmlHandler;
import com.ewell.ui.handler.ContentSpecHandler;

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

					if (arg2.getObjectClass().getValue().getValue()
							.equals(ClassEnum.value136.getValue())) {
						ContextMenu cm = new ContextMenu(
								MenuItemBuilder.create().text("预览")
										.onAction(new ContentHtmlHandler(arg2))
										.build(), MenuItemBuilder
										.create()
										.text("预览XML")
										.onAction(
												new ContentSpecHandler(arg2,
														getTreeItem())).build());
						setContextMenu(cm);
					}

					// setTooltip(new Tooltip(url));

				}

			}
		});
	}
}
