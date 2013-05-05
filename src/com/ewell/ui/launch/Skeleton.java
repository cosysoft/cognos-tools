package com.ewell.ui.launch;

import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Window;

import com.cognos.developer.schemas.bibus._3.BaseClass;

/**
 * 
 * @author comfort
 * 
 */
public interface Skeleton {

	/**
	 * connection public folder
	 * 
	 * @return
	 */
	TreeItem<BaseClass> getContentTree();

	/**
	 * app main window
	 * 
	 * @return
	 */
	Window getMainWindow();

	TreeView<BaseClass> getContentNav();

	SplitPane getMainPane();

	Scene getMainScene();

}
