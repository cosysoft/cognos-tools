package com.ibm.cognos;

/** 
 Licensed Materials - Property of IBM

 IBM Cognos Products: DOCS

 (C) Copyright IBM Corp. 2005, 2008

 US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with
 IBM Corp.
 */
/**
 * Credentials.java
 *
 * Copyright (C) 2008 Cognos ULC, an IBM Company. All rights reserved.
 * Cognos (R) is a trademark of Cognos ULC, (formerly Cognos Incorporated).
 *
 * Description: This file contains methods for creating credentials
 *
 */

import org.apache.axis.client.Stub;

import com.cognos.developer.schemas.bibus._3.AnyTypeProp;
import com.cognos.developer.schemas.bibus._3.BaseClass;
import com.cognos.developer.schemas.bibus._3.BiBusHeader;
import com.cognos.developer.schemas.bibus._3.CAM;
import com.cognos.developer.schemas.bibus._3.Credential;
import com.cognos.developer.schemas.bibus._3.Locale;
import com.cognos.developer.schemas.bibus._3.MultilingualToken;
import com.cognos.developer.schemas.bibus._3.MultilingualTokenProp;
import com.cognos.developer.schemas.bibus._3.PropEnum;
import com.cognos.developer.schemas.bibus._3.QueryOptions;
import com.cognos.developer.schemas.bibus._3.SearchPathMultipleObject;
import com.cognos.developer.schemas.bibus._3.Sort;

public class Credentials {
	public void setCredential(CRNConnect connection) {
		BiBusHeader bibus = null;
		try {
			Credential credential = new Credential();
			String search = "~/*";
			PropEnum[] props = { PropEnum.searchPath, PropEnum.name,
					PropEnum.defaultName };
			BaseClass[] objects = connection.getCMService().query(
					new SearchPathMultipleObject(search), props, new Sort[] {},
					new QueryOptions());

			if (objects != null) {
				for (int i = 0; i < objects.length; i++) {
					if (objects[i].getClass() == Credential.class) {
						credential.setSearchPath(objects[i].getSearchPath());
						credential.setName(objects[i].getName());
						credential.setDefaultName(objects[i].getDefaultName());
						bibus = BIBusHeaderHelper
								.getHeaderObject(((Stub) connection
										.getCMService()).getResponseHeader("",
										"biBusHeader"));

						if (bibus != null) {
							CAM newCam = bibus.getCAM();
							if (newCam != null) {
								newCam.setCAMCredentialPath(objects[i]
										.getSearchPath().getValue());
								bibus.setCAM(newCam);
							}

							((Stub) connection.getCMService()).setHeader("",
									"biBusHeader", bibus);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean hasCredential(CRNConnect connection)
			throws java.rmi.RemoteException {
		String search = "~/*";
		PropEnum[] props = { PropEnum.searchPath, PropEnum.name,
				PropEnum.defaultName };
		BaseClass[] objects = null;
		objects = connection.getCMService().query(
				new SearchPathMultipleObject(search), props, new Sort[] {},
				new QueryOptions());

		if (objects != null) {
			for (int i = 0; i < objects.length; i++) {
				if (objects[i].getClass() == Credential.class) {
					return true;
				}
			}
		}
		return false;
	}

	public void addCredential(CRNConnect connection) {

		CSHandlers csHandler = new CSHandlers();
		Credential credential = new Credential();

		// Prepare the credentials for the new credential object
		AnyTypeProp credentials = new AnyTypeProp();
		credentials.setValue(Logon.getCredentialString());

		// Prepare the name property for the new credential object
		MultilingualToken[] names = new MultilingualToken[1];
		names[0] = new MultilingualToken();
		Locale[] locales = csHandler.getConfiguration(connection);
		names[0].setLocale(locales[0].getLocale());
		names[0].setValue("Credential");
		MultilingualTokenProp credNameTokenProp = new MultilingualTokenProp();
		credNameTokenProp.setValue(names);

		// Add the searchPath, name and defaultname to the new credential object
		credential.setName(credNameTokenProp);
		credential.setCredentials(credentials);

		try {
			csHandler.addObjectToCS(connection, credential, Logon
					.getLogonAccount(connection).getSearchPath().getValue());
		} catch (java.rmi.RemoteException remoteEx) {
			remoteEx.printStackTrace();
		}

	}

}
