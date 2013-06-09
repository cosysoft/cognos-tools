package com.ewell.ui.task;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;

import com.ewell.cognos.content.ContentItem;
import com.ewell.ui.CognosTools;

/**
 * 
 * @author Bluesky Yao
 * 
 */
public class ContentTreeService extends Service<TreeItem<ContentItem>> {

	@Override
	protected Task<TreeItem<ContentItem>> createTask() {
		return new Task<TreeItem<ContentItem>>() {

			@Override
			protected TreeItem<ContentItem> call() throws Exception {
				return CognosTools.skeleton.getCMFacade().buildContentTree();
			}
		};
	}

}
