package com.ewell.cognos.content;

import java.util.List;

import com.cognos.developer.schemas.bibus._3.BaseClass;

/**
 * 
 * @author Bluesky Yao
 * 
 */
public class ContentItem {

	private BaseClass content;

	private List<ContentItem> children;

	public ContentItem(BaseClass content) {
		super();
		this.content = content;
	}

	public String getSearchPath() {
		if (content != null) {
			return content.getSearchPath().getValue();
		}
		return null;
	}

	public String getName() {
		if (content != null) {
			return content.getDefaultName().getValue();
		}
		return null;
	}

	public List<ContentItem> getChildren() {
		return children;
	}

	public void setChildren(List<ContentItem> children) {
		this.children = children;
	}

	public boolean isChildrenHas() {
		if (content != null) {
			return content.getHasChildren().isValue();
		}
		return false;
	}

	public String getType() {
		if (content != null) {
			return content.getObjectClass().getValue().getValue();
		}
		return null;
	}
	
	public String getStoreId(){
		if (content != null) {
			return content.getStoreID().getValue().get_value();
		}
		return null;
	}

	public BaseClass getContent() {
		return content;
	}
	
	
}
