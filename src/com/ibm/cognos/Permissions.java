package com.ibm.cognos;

/** 
 Licensed Materials - Property of IBM

 IBM Cognos Products: DOCS

 (C) Copyright IBM Corp. 2005, 2008

 US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with
 IBM Corp.
 */
/**
 * Permissions.java
 *
 * Copyright (C) 2008 Cognos ULC, an IBM Company. All rights reserved.
 * Cognos (R) is a trademark of Cognos ULC, (formerly Cognos Incorporated).
 *
 * Description: This file contains methods for handling permissions
 *
 */

import com.cognos.developer.schemas.bibus._3.AccessEnum;
import com.cognos.developer.schemas.bibus._3.Account;
import com.cognos.developer.schemas.bibus._3.BaseClass;
import com.cognos.developer.schemas.bibus._3.Folder;
import com.cognos.developer.schemas.bibus._3.Permission;
import com.cognos.developer.schemas.bibus._3.Policy;
import com.cognos.developer.schemas.bibus._3.PropEnum;
import com.cognos.developer.schemas.bibus._3.QueryOptions;
import com.cognos.developer.schemas.bibus._3.SearchPathMultipleObject;
import com.cognos.developer.schemas.bibus._3.Sort;
import com.cognos.developer.schemas.bibus._3.UpdateOptions;

public class Permissions {
	/**
	 * Modifies permissions.
	 * 
	 * @param connection
	 *            connection to server
	 * 
	 */
	public String modifyPermissions(CRNConnect connection) {
		BaseClass results[] = new BaseClass[] {};
		Folder csFolder = null;
		Account myAccount = null;
		String subfolder = "/folder[@name='My Folders']";
		Permission newPermission = null;

		try {
			myAccount = Logon.getLogonAccount(connection);
			String folder = myAccount.getSearchPath().getValue() + subfolder;
			results = connection.getCMService().query(
					new SearchPathMultipleObject(folder),
					new PropEnum[] { PropEnum.searchPath, PropEnum.policies },
					new Sort[] {}, new QueryOptions());
		} catch (java.rmi.RemoteException remoteEx) {
			System.out.println("Caught Remote Exception:\n");
			remoteEx.printStackTrace();
		}

		if (results.length > 0) {
			csFolder = (Folder) results[0];
		} else {
			return ("Expected folder \"" + subfolder
					+ "\" not found for the current user.\n" + "Please create this folder before running this sample.");
		}

		Policy pol;
		for (int i = 0; i < csFolder.getPolicies().getValue().length; i++) {
			pol = csFolder.getPolicies().getValue()[i];
			//
			if (pol.getSecurityObject().getSearchPath().getValue()
					.equals(myAccount.getSearchPath().getValue())) {

				newPermission = new Permission();
				newPermission.setName("read");

				boolean found = false;
				Permission[] permissions = pol.getPermissions();
				for (int j = 0; j < permissions.length; j++) {
					if (permissions[j].getName().compareTo("read") == 0) {
						if (permissions[j].getAccess().equals(AccessEnum.deny)) {
							newPermission.setAccess(AccessEnum.grant);
						} else {

							newPermission.setAccess(AccessEnum.deny);
						}
						permissions[j] = newPermission;
						found = true;
					}
				}
				if (!found) {

					int k = permissions.length + 1;
					Permission replacements[] = new Permission[k];
					for (int r = 0; r < k; r++) {
						replacements[r] = permissions[r];
					}
					replacements[k - 1] = newPermission;
					pol.setPermissions(replacements);
				}

				try {
					BaseClass[] updatedItems = connection.getCMService()
							.update(new BaseClass[] { csFolder },
									new UpdateOptions());
					if (updatedItems.length > 0) {

						return ("Successfully changed "
								+ newPermission.getName()
								+ " permission on folder \"" + subfolder
								+ "\" for "
								+ myAccount.getDefaultName().getValue()
								+ " to " + newPermission.getAccess().getValue());
					}
				} catch (java.rmi.RemoteException remoteEx) {
					remoteEx.printStackTrace();
					return "Exception Caught:\n" + remoteEx.getMessage();
				}
			}
		}
		return ("Unable to find \"" + subfolder + "\" for " + myAccount
				.getDefaultName().getValue());
	}
}
