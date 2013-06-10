package com.ewell.ui.cell;

import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.util.StringConverter;

import com.ewell.cognos.content.ContentItem;

public class CheckBoxContentCell extends CheckBoxTreeCell<ContentItem> {

	@Override
	public void updateItem(ContentItem arg0, boolean arg1) {
		super.updateItem(arg0, arg1);
	}

	public CheckBoxContentCell() {
		super();
		this.converterProperty().set(
				new StringConverter<TreeItem<ContentItem>>() {

					@Override
					public String toString(TreeItem<ContentItem> arg0) {
						return arg0.getValue().getName();
					}

					@Override
					public TreeItem<ContentItem> fromString(String arg0) {
						return null;
					}
				});
	}

}
