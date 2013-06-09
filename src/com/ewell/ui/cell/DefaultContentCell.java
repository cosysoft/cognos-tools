package com.ewell.ui.cell;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItemBuilder;
import javafx.scene.control.TreeCell;

import com.cognos.developer.schemas.bibus._3.ClassEnum;
import com.ewell.cognos.content.ContentItem;
import com.ewell.ui.handler.ContentHtmlHandler;
import com.ewell.ui.handler.ContentSpecHandler;

/**
 * 
 * @author Bluesky Yao
 * 
 */
public class DefaultContentCell extends TreeCell<ContentItem> {

	public DefaultContentCell() {
		
		itemProperty().addListener(new ChangeListener<ContentItem>() {

			@Override
			public void changed(ObservableValue<? extends ContentItem> arg0,
					ContentItem arg1, final ContentItem arg2) {

				if (arg2 == null)
					setText(" ");
				else {
					setText(arg2.getName());

					if (arg2.getType().equals(ClassEnum.value138.getValue())) {
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
