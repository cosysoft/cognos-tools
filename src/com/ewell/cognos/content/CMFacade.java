package com.ewell.cognos.content;

import java.util.List;

import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;

/**
 * 
 * @author Bluesky Yao
 * 
 */
public interface CMFacade {

	ContentItem getFolder(String serachPath);

	ContentItem buildContentItem();

	TreeItem<ContentItem> buildContentTree();

	CheckBoxTreeItem<ContentItem> buildContentTreeCK();

	ContentItem getContentItem(String searchPath);

	String getReportSpec(ContentItem report);

	int editReportSpec(ContentItem oldReport, ContentItem preport,
			String reportSpec);

	int[] editReportSpecBatch(List<ContentItem> oldReports,
			List<ContentItem> preports, List<String> reportSpecs);
}
