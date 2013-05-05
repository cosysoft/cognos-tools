package com.ibm.cognos;

/** 
 Licensed Materials - Property of IBM

 IBM Cognos Products: DOCS

 (C) Copyright IBM Corp. 2005, 2008

 US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with
 IBM Corp.
 */
/**
 * ViewPackages.java
 *
 * Copyright (C) 2008 Cognos ULC, an IBM Company. All rights reserved.
 * Cognos (R) is a trademark of Cognos ULC, (formerly Cognos Incorporated).
 *
 * Description: This code sample demonstrates how to display all the 
 * 		packages in the content store using the following methods:
 *		- query (search, properties, sortBy, options)
 *		Use this method to request objects from the content store.
 *              
 */

import com.cognos.developer.schemas.bibus._3.BaseClass;
import com.cognos.developer.schemas.bibus._3.OrderEnum;
import com.cognos.developer.schemas.bibus._3.PropEnum;
import com.cognos.developer.schemas.bibus._3.QueryOptions;
import com.cognos.developer.schemas.bibus._3.SearchPathMultipleObject;
import com.cognos.developer.schemas.bibus._3.Sort;

public class ViewPackages {
	/**
	 * Use this method to show the packages in the content store.
	 * 
	 * @param connection
	 *            Connection to Server
	 * @return Returns a string that either shows the name of each package in
	 *         the content store or displays a message to indicate that no
	 *         packages have been published.
	 */

	public String viewPackages(CRNConnect connection) {
		String output = new String();

		if (connection.getCMService() != null) {
			BaseClass bc[] = this.getPackages(connection);

			// If packages exist in the content store, the output shows the
			// package name
			// on one line, followed by a second line that shows the search path
			// of the package.

			if (bc != null) {
				for (int i = 0; i < bc.length; i++) {
					System.out
							.println("  " + bc[i].getDefaultName().getValue());
					System.out.println("      "
							+ bc[i].getSearchPath().getValue() + "\n");

					output = output.concat("  "
							+ bc[i].getDefaultName().getValue() + "\n");
					output = output.concat("      "
							+ bc[i].getSearchPath().getValue() + "\n\n");
				}
			} else {
				output = output
						.concat("There are currently no published packages to display.");
				System.out
						.println("\n\nThere are currently no published packages to display..");
			}
		}
		return output;
	}

	public BaseClass[] getPackages(CRNConnect connection) {
		PropEnum props[] = new PropEnum[] { PropEnum.searchPath,
				PropEnum.defaultName };

		if (connection.getCMService() != null) {
			Sort s[] = { new Sort() };
			s[0].setOrder(OrderEnum.ascending);
			s[0].setPropName(PropEnum.defaultName);

			try {
				/**
				 * Use this method to query the packages in the content store.
				 * 
				 * @param "/content//package" Specifies the search path string
				 *        so that Content Manager can locate the requested
				 *        objects, which are packages in this example.
				 * @param props
				 *            Specifies alternate properties that you want
				 *            returned for the package object. When no
				 *            properties are specified, as in this example, the
				 *            default properties of searchPath and defaultName
				 *            are provided.
				 * @param s
				 *            Specifies the sort criteria in an array.
				 * @param QueryOptions
				 *            Specifies any options for this method.
				 * @return Returns an array of packages.
				 */

				BaseClass bc[] = connection.getCMService().query(
						new SearchPathMultipleObject("/content//package"),
						props, s, new QueryOptions());

				if (bc != null) {
					if (bc.length > 0) {
						return bc;
					}
				} else {
					System.out
							.println("\n\nError occurred in function viewPackages.");
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		} else {
			System.out
					.println("\n\nInvalid parameter passed to function viewPackages.");
		}
		return null;
	}
}
