package com.ewell.ui.task;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;

import com.cognos.developer.schemas.bibus._3.BaseClass;
import com.ewell.ui.BiBusHelper;

/**
 * 
 * @author Bluesky Yao
 * 
 */
public class ContentTreeService extends Service<TreeItem<BaseClass>> {

	@Override
	protected Task<TreeItem<BaseClass>> createTask() {
		return new Task<TreeItem<BaseClass>>() {

			@Override
			protected TreeItem<BaseClass> call() throws Exception {
				return BiBusHelper.buildContentTree();
			}
		};
	}

}
