package com.ewell.ui;

import javafx.scene.control.TreeItem;

import com.cognos.developer.schemas.bibus._3.BaseClass;
import com.cognos.developer.schemas.bibus._3.OrderEnum;
import com.cognos.developer.schemas.bibus._3.PropEnum;
import com.cognos.developer.schemas.bibus._3.QueryOptions;
import com.cognos.developer.schemas.bibus._3.SearchPathMultipleObject;
import com.cognos.developer.schemas.bibus._3.Sort;
import com.ibm.cognos.CRNConnect;
import com.ibm.cognos.Logon;

/**
 * 
 * @author cosysoft
 * 
 */
public class BiBusHelper {

	public static CRNConnect connection = new CRNConnect();

	static String searchPath = "/content"; // / root

	static String passportID;

	public static TreeItem<BaseClass> buildContentTree() {

		Logon sessionLogon = new Logon();
		connection.connectToCognosServer();

		while (!Logon.loggedIn(connection)) {
			sessionLogon.logon(connection);
		}

		TreeItem<BaseClass> root = null;
		BaseClass myCMObject = null;

		try {
			myCMObject = BiBusHelper.getCMSignleObject(searchPath, connection);
			if (myCMObject != null) {
				root = new TreeItem<BaseClass>(myCMObject);
			}
			if (myCMObject.getHasChildren().isValue()) {
				innerBuild(root, connection);
			}
		} catch (Exception remoteEx) {
			remoteEx.printStackTrace();
		}

		System.out.println(myCMObject.getDefaultName());

		return root;
	}

	static void innerBuild(TreeItem<BaseClass> item, CRNConnect connection)
			throws Exception {
		if (!item.getValue().getHasChildren().isValue()) {
			return;
		}
		String searchPath = item.getValue().getSearchPath().getValue();

		BaseClass[] children = BiBusHelper.getCMMultipleObject(searchPath,
				connection);

		for (int i = 0; i < children.length; i++) {
			item.getChildren().add(new TreeItem<BaseClass>(children[i]));

		}

		for (TreeItem<BaseClass> ii : item.getChildren()) {
			if (ii.getValue().getHasChildren().isValue()) {
				innerBuild(ii, connection);
			}
		}

	}

	public static BaseClass[] getCMMultipleObject(String searchPath,
			CRNConnect connection) throws Exception {

		String appendString = "";

		if (searchPath.lastIndexOf("/") == (searchPath.length() - 1)) {
			appendString = "*";
		} else if (searchPath.lastIndexOf("*") == (searchPath.length() - 1)) {
			appendString = "";
		} else {
			appendString = "/*";
		}
		BaseClass[] children = null;

		SearchPathMultipleObject cmSearchPath = new SearchPathMultipleObject(
				searchPath);

		PropEnum[] properties = { PropEnum.defaultName, PropEnum.searchPath,
				PropEnum.objectClass, PropEnum.hasChildren, PropEnum.iconURI,
				PropEnum.storeID };

		Sort nodeSortType = new Sort();
		Sort nodeSortName = new Sort();

		nodeSortType.setOrder(OrderEnum.ascending);
		nodeSortType.setPropName(PropEnum.objectClass);
		nodeSortName.setOrder(OrderEnum.ascending);
		nodeSortName.setPropName(PropEnum.defaultName);
		Sort[] nodeSorts = new Sort[] { nodeSortType, nodeSortName };

		cmSearchPath.set_value(searchPath + appendString);
		children = connection.getCMService().query(cmSearchPath, properties,
				nodeSorts, new QueryOptions());

		return children;
	}

	public static BaseClass getCMSignleObject(String searchPath,
			CRNConnect connection) throws Exception {
		BaseClass myCMObject = null;

		SearchPathMultipleObject cmSearchPath = new SearchPathMultipleObject(
				searchPath);
		PropEnum[] properties = { PropEnum.defaultName, PropEnum.searchPath,
				PropEnum.objectClass, PropEnum.hasChildren, PropEnum.iconURI,
				PropEnum.storeID };
		myCMObject = (connection.getCMService().query(cmSearchPath, properties,
				new Sort[] {}, new QueryOptions()))[0];

		return myCMObject;
	}
}
