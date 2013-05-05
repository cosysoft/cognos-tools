package com.ibm.cognos;

/** 
 Licensed Materials - Property of IBM

 IBM Cognos Products: DOCS

 (C) Copyright IBM Corp. 2005, 2008

 US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with
 IBM Corp.
 */
/**
 * Logon.java
 *
 * Copyright (C) 2008 Cognos ULC, an IBM Company. All rights reserved.
 * Cognos (R) is a trademark of Cognos ULC, (formerly Cognos Incorporated).
 *
 * Description:  This code sample demonstrates how to log on  
 *               and how to log off using the following methods:
 *
 *             - logon
 *               Use this method to log on through the SDK. If authenticated 
 *               by a third party security provider, the action is 
 *               successful and a passport is created in the biBusHeader.
 *             - logoff
 *               Use this method to log off through the SDK. If the action is 
 *               successful, the passport is removed from the biBusHeader.
 *             - query
 *               Use this method to request objects from the content store. 
 */

import org.apache.axis.client.Stub;
import org.apache.axis.message.SOAPHeaderElement;

import com.cognos.developer.schemas.bibus._3.Account;
import com.cognos.developer.schemas.bibus._3.BaseClass;
import com.cognos.developer.schemas.bibus._3.BiBusHeader;
import com.cognos.developer.schemas.bibus._3.DisplayObject;
import com.cognos.developer.schemas.bibus._3.PromptOption;
import com.cognos.developer.schemas.bibus._3.PropEnum;
import com.cognos.developer.schemas.bibus._3.QueryOptions;
import com.cognos.developer.schemas.bibus._3.SearchPathMultipleObject;
import com.cognos.developer.schemas.bibus._3.SearchPathSingleObject;
import com.cognos.developer.schemas.bibus._3.Sort;
import com.cognos.developer.schemas.bibus._3.XmlEncodedXML;

public class Logon {

	private static String userID = "";
	private static String password = "";
	private static String nameSpace = "";
	private static String credentialString = "";

	/**
	 * Use this Java method to log on, bypassing any prompts.
	 * 
	 * @param connection
	 *            Connection to Server
	 * 
	 * @param namespace
	 *            Specifies the namespace where the user ID is stored.
	 * @param uid
	 *            Specifies the ID of the user.
	 * @param pwd
	 *            Specifies the password of the user.
	 * @return Returns a string containing status information.
	 */
	public String quickLogon(CRNConnect connection, String namespace,
			String uid, String pwd) throws Exception {
		// sn_dg_prm_sdk_method_contentManagerService_logon_start_1
		StringBuffer credentialXML = new StringBuffer();

		credentialXML.append("<credential>");

		credentialXML.append("<namespace>");
		credentialXML.append(namespace);
		credentialXML.append("</namespace>");

		credentialXML.append("<username>");
		credentialXML.append(uid);
		credentialXML.append("</username>");

		credentialXML.append("<password>");
		credentialXML.append(pwd);
		credentialXML.append("</password>");

		credentialXML.append("</credential>");

		String encodedCredentials = credentialXML.toString();
		credentialString = encodedCredentials;

		connection.getCMService().logon(new XmlEncodedXML(encodedCredentials),
				new SearchPathSingleObject[] {});
		// sn_dg_prm_sdk_method_contentManagerService_logon_end_1

		return ("Logon successful as " + uid);
	}

	/**
	 * Use this Java method to logon.
	 * 
	 * @param connection
	 *            Connection to Server
	 * 
	 * @return Returns a string containing status information.
	 */
	public String logon(CRNConnect connection) {
		// NOTE: If you are already logged on, you must first log off
		// before you can log on as a different user.
		logoff(connection);

		String namespaceInfo[] = getNamespaces(connection);

		// namespaceInfo is name/ID pairs -- always even
		String namespaces[] = new String[namespaceInfo.length / 2];
		String namespaceIDs[] = new String[namespaceInfo.length / 2];

		for (int j = 0, k = 0; k < namespaceInfo.length; j++, k++) {
			namespaces[j] = namespaceInfo[k++];
			namespaceIDs[j] = namespaceInfo[k];
		}

		userID = "admin";
		password = "123";
		nameSpace = "BLRMYY";
		StringBuffer credentialXML = new StringBuffer();
		credentialXML.append("<credential>");

		credentialXML.append("<namespace>");
		credentialXML.append(nameSpace);
		credentialXML.append("</namespace>");

		credentialXML.append("<username>");
		credentialXML.append(userID);
		credentialXML.append("</username>");

		credentialXML.append("<password>");
		credentialXML.append(password);
		credentialXML.append("</password>");

		credentialXML.append("</credential>");

		String encodedCredentials = credentialXML.toString();

		credentialString = encodedCredentials;

		try {
			connection.getCMService().logon(
					new XmlEncodedXML(encodedCredentials),
					new SearchPathSingleObject[] {});
			SOAPHeaderElement x = ((Stub) connection.getCMService())
					.getResponseHeader(
							"http://developer.cognos.com/schemas/bibus/3/",
							"biBusHeader");
			((Stub) connection.getCMService()).setHeader(x);

			password = "";
		} catch (java.rmi.RemoteException remoteEx) {
			userID = "";
			password = "";
			nameSpace = "";
			credentialString = "";
			return "";
		}

		System.out.println("登陆成功.................");
		return ("Logon successful.");
	}

	/**
	 * Use this Java method to log off.
	 * 
	 * @param connection
	 *            Connection to Server
	 * 
	 * @return Returns a string containing status information.
	 */
	public String logoff(CRNConnect connection) {
		nameSpace = "";
		userID = "";
		password = "";

		try {
			// sn_dg_sdk_method_contentManagerService_logoff_start_1
			connection.getCMService().logoff();
			// sn_dg_sdk_method_contentManagerService_logoff_end_1
		} catch (java.rmi.RemoteException remoteEx) {
			return (remoteEx.toString());
		}
		return ("Logoff successful.");
	}

	public static boolean loggedIn(CRNConnect connection) {
		return (userID != "" || userID == null || doTestForAnonymous(connection));
	}

	/**
	 * Use this Java method to get account information for the current user.
	 * 
	 * @param connection
	 *            Specifies the object that provides the connection to the
	 *            server.
	 * @return Returns a string containing user information.
	 */
	public String logonInfo(CRNConnect connection) {
		String output = new String();

		if (connection != null) {
			Account myAccount = getLogonAccount(connection);
			if (myAccount == null) {
				output = "You are not currently logged on.\n";
				return output;
			}

			String logonName = myAccount.getDefaultName().getValue();
			if (logonName == null) {
				output = "You are not currently logged on.\n";
				return output;
			}

			output = output.concat("You are currently logged on as: "
					+ logonName + "\n");

			if (myAccount.getUserName().getValue() != null) {
				output = output.concat("Your user name is: "
						+ myAccount.getUserName().getValue() + "\n");
			}

			output = output.concat("Your searchPath is: "
					+ myAccount.getSearchPath().getValue() + "\n");

			if (myAccount.getNotificationEMail().getValue() == null) {
				output = output
						.concat("You do not have a notification email address defined.\n");
			} else {
				// sn_dg_sdk_task_querycontent_start_2
				output = output.concat("Your alert email address is: "
						+ myAccount.getNotificationEMail().getValue());
				// sn_dg_sdk_task_querycontent_end_2
			}

		} else {
			output = output
					.concat("Invalid parameter passed to function logon.");
		}
		return output;
	}

	/**
	 * Use this Java method to find out if Anonymous access is enabled
	 * 
	 * @param connection
	 *            Connection to Server
	 * 
	 * @return Returns a boolean indicating whether or not Anonymous access is
	 *         enabled (true) or disabled (false).
	 */
	public static boolean doTestForAnonymous(CRNConnect connection) {

		boolean doTestForAnonymous = false;
		try {
			BaseClass bc[] = connection.getCMService().query(
					new SearchPathMultipleObject("/content"),
					new PropEnum[] {}, new Sort[] {}, new QueryOptions());
			if (bc != null) {
				doTestForAnonymous = true;
			} else {
				doTestForAnonymous = false;
			}
		} catch (java.rmi.RemoteException remoteEx) {
			System.out.println("");
			// Ignore this, it means that Anonymous access is denied...
		}

		return doTestForAnonymous;
	}

	// Get account information for the current user.
	public static Account getLogonAccount(CRNConnect connection) {
		// sn_dg_sdk_task_querycontent_start_0
		PropEnum props[] = new PropEnum[] { PropEnum.searchPath,
				PropEnum.defaultName, PropEnum.policies, PropEnum.userName,
				PropEnum.notificationEMail };
		Account myAccount = null;
		// sn_dg_sdk_task_querycontent_end_0

		if (connection.getCMService() == null) {
			System.out.println("Invalid parameter passed to function logon.");
			return myAccount;
		}

		try {
			// sn_dg_sdk_task_querycontent_start_1
			BaseClass bc[] = connection.getCMService().query(
					new SearchPathMultipleObject("~"), props, new Sort[] {},
					new QueryOptions());

			if ((bc != null) && (bc.length > 0)) {
				for (int i = 0; i < bc.length; i++) {
					myAccount = (Account) bc[i];
				}
			}
			// sn_dg_sdk_task_querycontent_end_1
		} catch (java.rmi.RemoteException remoteEx) {
			// An exception here likely indicates the client is not currently
			// logged in, so the query fails.
			System.out.println("Caught RemoteException:\n"
					+ remoteEx.getMessage());
		}

		return myAccount;
	}

	/**
	 * Use this Java method to retrieve the available namespaces.
	 * 
	 * @param connection
	 *            Connection to Server
	 * 
	 * @return Returns an array of strings containing all available namespaces.
	 */
	public String[] getNamespaces(CRNConnect connection)
	// throws Exception
	{
		// This call to the query method provides the logon information.
		// The authentication will fail and the SOAP:Header
		// will contain all the information required to log on.
		try {
			connection.getCMService().query(
					new SearchPathMultipleObject("/content"),
					new PropEnum[] {}, new Sort[] {}, new QueryOptions());
		} catch (java.rmi.RemoteException remoteEx) {
			// Ignore this exception because the query was expected to fail.
		}

		// Retrieve the biBusHeader SOAP:Header that contains
		// the logon information.
		BiBusHeader bibus = BIBusHeaderHelper
				.getHeaderObject(((Stub) connection.getCMService())
						.getResponseHeader(
								"http://developer.cognos.com/schemas/bibus/3/",
								"biBusHeader"));

		// Initialize the return values container
		String[] namespaces = new String[] {};

		// Look in the displayObjects for namespace prompt options and capture
		// all the namespaces defined there.
		try {
			DisplayObject[] dob = bibus.getCAM().getException().getPromptInfo()
					.getDisplayObjects();

			for (int i = 0; i < dob.length; i++) {
				if (dob[i].getName().equalsIgnoreCase("CAMNamespace")) {
					PromptOption[] pop = dob[i].getPromptOptions();
					// Check to see how many namespaces exist.
					// If there is an array, there are many namespaces.
					// Otherwise there is only one namespace.
					if (pop != null) {
						namespaces = new String[pop.length * 2];

						for (int j = 0, k = 0; k < pop.length; j++, k++) {
							namespaces[j] = pop[k].getValue();
							namespaces[++j] = pop[k].getId();
						}
					} else // There is only one namespace.
					{
						if (dob[i + 1].getName().equalsIgnoreCase(
								("CAMNamespaceDisplayName"))) {
							namespaces = new String[2];
							namespaces[0] = dob[i + 1].getValue();
							namespaces[1] = dob[i].getValue();
						} else {
							namespaces = null;
						}

					}
				}
			}
		} catch (NullPointerException npe) {
			// This exception may occur if we have a malformed header.
			// If this happens, return an empty array.
			namespaces = null;
		}

		// Clear the header so no information from this call remains.
		((Stub) connection.getCMService()).clearHeaders();

		return namespaces;
	}

	public static String getCredentialString() {
		if (credentialString.compareTo("") == 0) {
			StringBuffer credentialXML = new StringBuffer();

			credentialXML.append("<credential>");

			credentialXML.append("<namespace>");
			credentialXML.append(nameSpace);
			credentialXML.append("</namespace>");

			credentialXML.append("<username>");
			credentialXML.append(userID);
			credentialXML.append("</username>");

			credentialXML.append("</credential>");

			return credentialXML.toString();
		}

		return credentialString;
	}
}
