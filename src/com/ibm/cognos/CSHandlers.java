package com.ibm.cognos;

/** 
 Licensed Materials - Property of IBM

 IBM Cognos Products: DOCS

 (C) Copyright IBM Corp. 2005, 2008

 US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with
 IBM Corp.
 */
/**
 * CSHandlers.java
 *
 * Copyright (C) 2008 Cognos ULC, an IBM Company. All rights reserved.
 * Cognos (R) is a trademark of Cognos ULC, (formerly Cognos Incorporated).
 *
 * Description: This code sample demonstrates how to add, update, query
 * 				and delete objects and properties in the content store
 * 				using the following methods:
 * 				- add (parentPath, objects, options)
 * 				Use this method to add objects, such as reports, to the content store.
 *				- delete (objects, options)
 *				Use this method to delete objects from the content store.
 *				- update (objects)
 *				Use this method to modify existing objects in the content store.
 *				- move (objects, target, options)
 *				Use this implementation of the method to move objects within the content store.
 *				- copy (objects, target, options)
 *				Use this implementation of the method to copy objects within the content store.
 *				- query (searchPath, properties, sortBy, options)
 *				Use this method to retrieve objects from the content store.
 *				- getConfiguration (properties)
 *				Use this method to retrieve global configuration data.
 */

import com.cognos.developer.schemas.bibus._3.Account;
import com.cognos.developer.schemas.bibus._3.AddOptions;
import com.cognos.developer.schemas.bibus._3.AuthoredReport;
import com.cognos.developer.schemas.bibus._3.BaseClass;
import com.cognos.developer.schemas.bibus._3.ConfigurationData;
import com.cognos.developer.schemas.bibus._3.ConfigurationDataEnum;
import com.cognos.developer.schemas.bibus._3.CopyOptions;
import com.cognos.developer.schemas.bibus._3.DeleteOptions;
import com.cognos.developer.schemas.bibus._3.Folder;
import com.cognos.developer.schemas.bibus._3.Locale;
import com.cognos.developer.schemas.bibus._3.MoveOptions;
import com.cognos.developer.schemas.bibus._3.PropEnum;
import com.cognos.developer.schemas.bibus._3.QueryOptions;
import com.cognos.developer.schemas.bibus._3.Report;
import com.cognos.developer.schemas.bibus._3.SearchPathMultipleObject;
import com.cognos.developer.schemas.bibus._3.SearchPathSingleObject;
import com.cognos.developer.schemas.bibus._3.Sort;
import com.cognos.developer.schemas.bibus._3.StringProp;
import com.cognos.developer.schemas.bibus._3.TokenProp;
import com.cognos.developer.schemas.bibus._3.UpdateActionEnum;
import com.cognos.developer.schemas.bibus._3.UpdateOptions;

public class CSHandlers {
	/**
	 * Add an object to the Content Store.
	 * 
	 * @param connection
	 *            Connection to Server
	 * @param bc
	 *            An object that extends baseClass, such as a Report.
	 * @param path
	 *            Search path that will contain the new object.
	 * 
	 * @return The new object.
	 * 
	 */
	public BaseClass addObjectToCS(CRNConnect connection, BaseClass bc,
			String path) throws java.rmi.RemoteException {
		// sn_dg_sdk_method_contentManagerService_add_start_1
		AddOptions ao = new AddOptions();
		ao.setUpdateAction(UpdateActionEnum.replace);

		return connection.getCMService().add(new SearchPathSingleObject(path),
				new BaseClass[] { bc }, ao)[0];
		// sn_dg_sdk_method_contentManagerService_add_end_1
	}

	/**
	 * Add a Report to the Content Store.
	 * 
	 * @param connection
	 *            Connection to Server
	 * @param rprt
	 *            An AuthoredReport object.
	 * @param path
	 *            Search path that will contain the new object.
	 * 
	 * @return The new Report object.
	 * 
	 */
	public AuthoredReport addReportToCS(CRNConnect connection, Report rprt,
			String path) throws java.rmi.RemoteException {
		// sn_dg_sdk_method_reportService_add_start_1
		AddOptions ao = new AddOptions();
		ao.setUpdateAction(UpdateActionEnum.replace);

		return connection.getReportService().add(
				new SearchPathSingleObject(path), rprt, ao);
		// sn_dg_sdk_method_reportService_add_end_1
	}

	public BaseClass[] createDirectoryInCS(CRNConnect connection,
			String parentPath, String directoryName)
			throws java.rmi.RemoteException {
		TokenProp directoryNameTokenProp = new TokenProp();
		directoryNameTokenProp.setValue(directoryName);

		Folder directory = new Folder();
		directory.setDefaultName(directoryNameTokenProp);

		BaseClass[] directoryList = new BaseClass[] { directory };

		AddOptions addOpts = new AddOptions();
		addOpts.setUpdateAction(UpdateActionEnum.update);

		return connection.getCMService().add(
				new SearchPathSingleObject(parentPath), directoryList, addOpts);
	}

	/**
	 * Delete an object from the Content Store.
	 * 
	 * @param connection
	 *            Connection to Server
	 * @param bc
	 *            Specifies the object to be deleted from the content store.
	 * @return True if successful, false otherwise.
	 * 
	 */
	public boolean deleteObjectFromCS(CRNConnect connection, BaseClass bc)
			throws java.rmi.RemoteException {
		// sn_dg_sdk_method_contentManagerService_delete_start_1
		DeleteOptions del = new DeleteOptions();
		del.setForce(true);

		int i = connection.getCMService().delete(new BaseClass[] { bc }, del);
		// sn_dg_sdk_method_contentManagerService_delete_end_1

		return (i > 0);
	}

	/**
	 * Save modified object(s) to the Content Store.
	 * 
	 * @param connection
	 *            Connection to Server
	 * @param bc
	 *            An object that extends baseClass. Specifies the objects and
	 *            properties to be updated. If you do not include a property for
	 *            an object, the property is not modified for that object. If
	 *            you include a property for an object but you don't specify a
	 *            value, the value of that property is deleted from the object.
	 *            If the value of an acquired property is deleted from an
	 *            object, such as the policies property, the value will be
	 *            acquired from an ancestor of the object.
	 * 
	 * @return An array of BaseClass objects. An error is not returned if no
	 *         objects are selected.
	 * 
	 */
	public BaseClass[] updateObjectInCS(CRNConnect connection, BaseClass[] bc)
			throws java.rmi.RemoteException

	{
		// sn_dg_sdk_method_contentManagerService_update_start_1
		return connection.getCMService().update(bc, new UpdateOptions());
		// sn_dg_sdk_method_contentManagerService_update_end_1
	}

	/**
	 * Move object in the Content Store.
	 * 
	 * @param connection
	 *            Connection to Server
	 * @param bc
	 *            Specifies the objects to be moved to a new location in the
	 *            content store.
	 * @param targetPath
	 *            Specifies the target location for the moved objects. This
	 *            parameter must select a single container object that is
	 *            writable in the current security context.
	 * 
	 * @return The new object. An error is not returned if no objects are
	 *         selected.
	 * 
	 */
	public BaseClass[] moveObjectsInCS(CRNConnect connection, BaseClass[] bc,
			String targetPath) {
		try {
			// sn_dg_sdk_method_contentManagerService_move_start_1
			return connection.getCMService().move(bc,
					new SearchPathSingleObject(targetPath), new MoveOptions());
			// sn_dg_sdk_method_contentManagerService_move_end_1
		} catch (java.rmi.RemoteException remoteEx) {
			remoteEx.printStackTrace();
			return null;
		}
	}

	/**
	 * Move reports in the Content Store.
	 * 
	 * @param connection
	 *            Connection to Server
	 * @param reportPath
	 *            Search path to an AuthoredReport object.
	 * @param targetPath
	 *            Search path that will contain the moved object.
	 * 
	 * @return
	 * 
	 */
	public void moveReports(CRNConnect connection, String[] reportPath,
			String targetPath) {
		// This code moves the prompt report to the public package
		BaseClass[] obj = new BaseClass[1];
		obj[0] = new Report();
		StringProp path = new StringProp();
		path.setValue(reportPath[0]);
		obj[0].setSearchPath(path);

		obj = moveObjectsInCS(connection, obj, targetPath);
		System.out.println("");
		System.out.println("Here is the list of items that were moved.");
		if (obj != null) {
			for (int i = 0; i < obj.length; i++) {
				System.out
						.println("Name:" + obj[i].getDefaultName().getValue());
			}
		} else {
			System.out.println("No items were moved.");
		}
	}

	/**
	 * Copy an object in the Content Store.
	 * 
	 * @param connection
	 *            Connection to Server
	 * @param bc
	 *            Specifies the objects to be copied.
	 * @param targetPath
	 *            Search path that will contain the new object. Must select a
	 *            single container object that must be writable in the current
	 *            security context.
	 * 
	 * @return The new objects. An error is not returned if no objects are
	 *         selected.
	 * 
	 */
	public BaseClass[] copyObjectsInCS(CRNConnect connection, BaseClass[] bc,
			String targetPath) {
		try {
			// sn_dg_sdk_method_contentManagerService_copy_start_1
			return connection.getCMService().copy(bc,
					new SearchPathSingleObject(targetPath), new CopyOptions());
			// sn_dg_sdk_method_contentManagerService_copy_end_1
		} catch (java.rmi.RemoteException remoteEx) {
			remoteEx.printStackTrace();
			return null;
		}
	}

	/**
	 * Copy report in the Content Store.
	 * 
	 * @param connection
	 *            Connection to Server
	 * @param reportPath
	 *            Search path to an AuthoredReport object.
	 * @param targetPath
	 *            Search path that will contain the new object.
	 * 
	 * @return true if the reports were copied and false otherwise.
	 * 
	 */
	public boolean copyReports(CRNConnect connection, String[] reportPath,
			String targetPath) {
		BaseClass[] obj = new BaseClass[1];
		StringProp path = new StringProp();
		path.setValue(reportPath[0]);
		obj[0] = new Report();
		obj[0].setSearchPath(path);

		obj = copyObjectsInCS(connection, obj, targetPath);

		if (obj != null) {
			System.out.println("Here is the list of items that were copied.");
			for (int i = 0; i < obj.length; i++) {
				System.out
						.println("Name:" + obj[i].getDefaultName().getValue());
			}
			return true;
		} else {
			System.out.println("No items were copied.");
			return false;
		}
	}

	/**
	 * Take ownership of an object in the Content Store.
	 * 
	 * @param connection
	 *            Connection to Server
	 * @param bc
	 *            An object that extends baseClass, such as a Report.
	 * 
	 * @return
	 * 
	 */
	public void takeOwnerShip(CRNConnect connection, BaseClass bc) {
		Account me = Logon.getLogonAccount(connection);
		bc.setOwner(me.getOwner());
	}

	/**
	 * Take ownership of a report in the Content Store.
	 * 
	 * @param connection
	 *            Connection to Server
	 * @param reportPath
	 *            Search path to report
	 * 
	 * @return
	 * 
	 */
	public void takeOwnerShipOfReport(CRNConnect connection, String[] reportPath) {
		BaseClass[] bc = new BaseClass[1];
		StringProp path = new StringProp();
		path.setValue(reportPath[0]);
		bc[0] = new Report();
		bc[0].setSearchPath(path);
		takeOwnerShip(connection, bc[0]);
	}

	/**
	 * Use to access objects. This is similar to a filepath on the OS. The
	 * default properties are searchPath and defaultName.
	 * 
	 * Use this method if you want to retrieve objects from the content store.
	 * Examples of content store objects are Folder, Report, URL, Package and so
	 * on. Since these are returned as an array (or BaseClasses), use the
	 * following to determine which type of object it is:
	 * 
	 * if(bc[i] instanceof Report) // This is a report. if(bc[i] instanceof URL)
	 * // This is a URL. if(bc[i] instanceof Folder) // This is a folder.
	 * if(bc[i] instanceof Package_) // This is a package. if(bc[i] instanceof
	 * Query) // This is a query.
	 * 
	 * @param connection
	 *            Connection to Server
	 * @param path
	 *            This is the search path. It is needed to access objects in the
	 *            Content Store and is similar to a filepath on the OS.
	 * 
	 * @return An array of BaseClass objects.
	 * 
	 */
	public BaseClass[] queryObjectInCS(CRNConnect connection, String path)
			throws java.rmi.RemoteException {
		// Set up the properties.
		PropEnum properties[] = new PropEnum[] { PropEnum.defaultName,
				PropEnum.searchPath };

		// Call the other version of this method.
		return queryObjectInCS(connection, path, properties);
	}

	/**
	 * Use to access objects. This is similar to a filepath on the OS. The
	 * default properties are searchPath and defaultName.
	 * 
	 * Use this method if you want to retrieve objects from the content store.
	 * Examples of content store objects are Folder, Report, URL, Package and so
	 * on. Since these are returned as an array (or BaseClasses), use the
	 * following to determine which type of object it is:
	 * 
	 * if(bc[i] instanceof Report) // This is a report. if(bc[i] instanceof URL)
	 * // This is a URL. if(bc[i] instanceof Folder) // This is a folder.
	 * if(bc[i] instanceof Package_) // This is a package. if(bc[i] instanceof
	 * Query) // This is a query.
	 * 
	 * @param connection
	 *            Connection to Server
	 * @param path
	 *            This is the search path. It is needed to access objects in the
	 *            Content Store and is similar to a filepath on the OS.
	 * @param properties
	 *            A list of alternate properties you MAY wish to ask for on each
	 *            object. The default properties are searchPath and defaultName.
	 * 
	 * @return An array of BaseClass objects.
	 * 
	 */
	public BaseClass[] queryObjectInCS(CRNConnect connection, String path,
			PropEnum[] properties) throws java.rmi.RemoteException {
		// Used to determine which property
		// the objects are sorted by.
		// The default is "defaultName".
		Sort sort[] = new Sort[] { new Sort() };

		return queryObjectInCS(connection, path, properties, sort);
	}

	/**
	 * Use to access objects. This is similar to a filepath on the OS. The
	 * default properties are searchPath and defaultName.
	 * 
	 * Use this method if you want to retrieve objects from the content store.
	 * Examples of content store objects are Folder, Report, URL, Package and so
	 * on. Since these are returned as an array (or BaseClasses), use the
	 * following to determine which type of object it is:
	 * 
	 * if(bc[i] instanceof Report) // This is a report. if(bc[i] instanceof URL)
	 * // This is a URL. if(bc[i] instanceof Folder) // This is a folder.
	 * if(bc[i] instanceof Package_) // This is a package. if(bc[i] instanceof
	 * Query) // This is a query.
	 * 
	 * @param connection
	 *            Connection to Server
	 * @param path
	 *            This is the search path. It is needed to access objects in the
	 *            Content Store and is similar to a filepath on the OS.
	 * @param properties
	 *            A list of alternate properties you MAY wish to ask for on each
	 *            object. The default properties are searchPath and defaultName.
	 * @param sort
	 *            A list of sorting options.
	 * 
	 * @return An array of BaseClass objects.
	 * 
	 */
	public BaseClass[] queryObjectInCS(CRNConnect connection, String path,
			PropEnum[] properties, Sort sort[]) throws java.rmi.RemoteException {
		// Used to give instructions such as max objects to
		// be returned, size of certain properties and so on.
		// In this case, we do nothing with it.
		QueryOptions qop = new QueryOptions();

		// Call the actual query method and return the base class array.
		return connection.getCMService().query(
				new SearchPathMultipleObject(path), properties, sort, qop);
	}

	/**
	 * This method accepts a Report Search Path and returns only the parent name
	 * 
	 * @param connection
	 *            Connection to Server
	 * 
	 * @param searchPath
	 *            Search path of child object
	 * 
	 * @return Search path of parent object
	 */
	public String getParentPath(CRNConnect connection, String searchPath) {
		String parentPath = "";
		Sort sortArray[] = { new Sort() };
		QueryOptions queryOptions = new QueryOptions();
		PropEnum props[] = new PropEnum[] { PropEnum.searchPath,
				PropEnum.defaultName, PropEnum.parent };
		try {
			BaseClass child[];
			child = connection.getCMService().query(
					new SearchPathMultipleObject(searchPath), props, sortArray,
					queryOptions);
			if (child != null && (child.length > 0)) {
				String[] parents = new String[child.length];
				for (int i = 0; i < child.length; i++) {
					parents[i] = child[i].getParent().getValue()[0]
							.getSearchPath().getValue();
				}
				parentPath = parents[0];
			}
		} catch (java.rmi.RemoteException remoteEx) {
			return "";
		}

		return parentPath;
	}

	/**
	 * This method accepts a Report Name and searches amd returns a string array
	 * of search paths of all reports with that name If more than one
	 * package/folder contains a report with the same name, all packages/folders
	 * will be returned
	 * 
	 * @param connection
	 *            Connection to Server
	 * 
	 * @param reportName
	 * 
	 * @return reportPath
	 * 
	 */
	public String[] getReportPath(CRNConnect connection, String reportName) {
		Sort sortArray[] = { new Sort() };
		QueryOptions queryOptions = new QueryOptions();
		PropEnum props[] = new PropEnum[] { PropEnum.searchPath,
				PropEnum.defaultName };
		try {
			BaseClass repPth[];
			String quotChar = "\'";
			if (reportName.indexOf(quotChar) >= 0) {
				quotChar = "\"";
			}
			repPth = connection.getCMService().query(
					new SearchPathMultipleObject("/content//report[@name="
							+ quotChar + reportName + quotChar + "]"), props,
					sortArray, queryOptions);
			if (repPth != null && (repPth.length > 0)) {
				String[] reportPath = new String[repPth.length];
				for (int i = 0; i < repPth.length; i++) {
					reportPath[i] = repPth[i].getSearchPath().getValue();
				}
				return reportPath;
			} else {
				quotChar = "\'";
				if (reportName.indexOf(quotChar) >= 0) {
					quotChar = "\"";
				}
				repPth = connection.getCMService().query(
						new SearchPathMultipleObject("/content//query[@name="
								+ quotChar + reportName + quotChar + "]"),
						props, sortArray, queryOptions);
				if (repPth != null && (repPth.length > 0)) {
					// This will delete the first occurence of the report with
					// given
					// Name if there are many. If all reports have to be deleted
					// use
					String[] reportPath = new String[repPth.length];
					for (int i = 0; i < repPth.length; i++) {
						reportPath[i] = repPth[i].getSearchPath().getValue();
					}
					return reportPath;
				}
			}

			return null;
		} catch (java.rmi.RemoteException remoteEx) {
			System.out.println(remoteEx.getMessage());
			return null;
		}
	}

	/**
	 * Get the server locale setting.
	 * 
	 * @param connection
	 *            Connection to Server
	 * 
	 * @return Server Locale.
	 * 
	 */
	public Locale[] getConfiguration(CRNConnect connection) {
		ConfigurationData data = null;
		Locale[] locales = null;

		ConfigurationDataEnum[] config = new ConfigurationDataEnum[1];

		config[0] = ConfigurationDataEnum.fromString("serverLocale");

		try {
			// sn_dg_sdk_method_systemService_getConfiguration_start_1
			data = connection.getSystemService().getConfiguration(config);
			locales = data.getServerLocale();
			// sn_dg_sdk_method_systemService_getConfiguration_end_1

			if (locales == null) {
				System.out.println("No serverLocale configured!");
			}
		} catch (java.rmi.RemoteException remoteEx) {
			remoteEx.printStackTrace();
		}
		return locales;
	}

}
