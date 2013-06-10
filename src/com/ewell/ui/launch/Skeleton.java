package com.ewell.ui.launch;

import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeView;
import javafx.stage.Window;

import com.ewell.cognos.content.CMFacade;
import com.ewell.cognos.content.ContentItem;
import com.ibm.cognos.CRNConnect;

/**
 * 
 * @author comfort
 * 
 */
public interface Skeleton {

	/**
	 * app main window
	 * 
	 * @return
	 */
	Window getMainWindow();

	TreeView<ContentItem> getContentNav();

	SplitPane getMainPane();

	Scene getMainScene();

	CMFacade getCMFacade();
	
	CRNConnect getConnect();
	
	ContentItem getRootContent();
	
	TreeView<ContentItem> getChoiceView();
	

}
