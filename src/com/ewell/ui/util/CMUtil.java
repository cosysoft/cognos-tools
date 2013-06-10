package com.ewell.ui.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import com.ewell.cognos.content.ContentItem;
import com.ibm.cognos.CRNConnect;

/**
 * @author Bluesky Yao
 * 
 */
public final class CMUtil {

	public static String encodeContentUrl(ContentItem content,
			CRNConnect connection) {
		String url = encodeContentUrl(content.getSearchPath(),
				content.getName(), connection);
		return url;

	}

	public static String encodeContentUrl(String path, String name,
			CRNConnect connection) {
		String url = "";
		try {
			url = CRNConnect.GATEWAY_URL + "&ui.object="
					+ URLEncoder.encode(path, "utf-8") + "&ui.name="
					+ URLEncoder.encode(name, "utf-8") + "&m_passportID="
					+ connection.getPassPort();
		} catch (UnsupportedEncodingException e) {

			throw new RuntimeException("", e);
		}

		return url;

	}

	public static TreeItem<ContentItem> createNode(final ContentItem f) {
		return new TreeItem<ContentItem>(f) {
			// We cache whether the ContentItem is a leaf or not. A ContentItem
			// is a leaf if
			// it is not a directory and does not have any files contained
			// within
			// it. We cache this as isLeaf() is called often, and doing the
			// actual check on ContentItem is expensive.
			private boolean isLeaf;

			// We do the children and leaf testing only once, and then set these
			// booleans to false so that we do not check again during this
			// run. A more complete implementation may need to handle more
			// dynamic file system situations (such as where a folder has files
			// added after the TreeView is shown). Again, this is left as an
			// exercise for the reader.
			private boolean isFirstTimeChildren = true;
			private boolean isFirstTimeLeaf = true;

			@Override
			public ObservableList<TreeItem<ContentItem>> getChildren() {
				if (isFirstTimeChildren) {
					isFirstTimeChildren = false;

					// First getChildren() call, so we actually go off and
					// determine the children of the ContentItem contained in
					// this TreeItem.
					super.getChildren().setAll(buildChildren(this));
				}
				return super.getChildren();
			}

			@Override
			public boolean isLeaf() {
				if (isFirstTimeLeaf) {
					isFirstTimeLeaf = false;
					ContentItem f = (ContentItem) getValue();
					isLeaf = !f.isChildrenHas();
				}

				return isLeaf;
			}

			private ObservableList<TreeItem<ContentItem>> buildChildren(
					TreeItem<ContentItem> TreeItem) {
				ContentItem f = TreeItem.getValue();
				if (f != null && f.isChildrenHas()) {
					List<ContentItem> root = f.getChildren();
					if (root != null) {
						ObservableList<TreeItem<ContentItem>> children = FXCollections
								.observableArrayList();

						for (ContentItem childContentItem : root) {
							children.add(createNode(childContentItem));
						}

						return children;
					}
				}

				return FXCollections.emptyObservableList();
			}
		};
	}
}
