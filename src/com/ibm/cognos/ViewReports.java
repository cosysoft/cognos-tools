package com.ibm.cognos;

/** 
 Licensed Materials - Property of IBM

 IBM Cognos Products: DOCS

 (C) Copyright IBM Corp. 2005, 2008

 US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with
 IBM Corp.
 */
/**
 * ViewReports.java
 * 
 * Copyright (C) 2008 Cognos ULC, an IBM Company. All rights reserved.
 * Cognos (R) is a trademark of Cognos ULC, (formerly Cognos Incorporated).
 *
 * Description: This code sample demonstrates how to display all the 
 *              reports and queries in the content store using the 
 *              following methods:
 *              - query (search, properties, sortBy, options)
 *              Use this method to request objects from the content store.
 */

import com.cognos.developer.schemas.bibus._3.BaseClass;
import com.cognos.developer.schemas.bibus._3.OrderEnum;
import com.cognos.developer.schemas.bibus._3.PropEnum;
import com.cognos.developer.schemas.bibus._3.QueryOptions;
import com.cognos.developer.schemas.bibus._3.SearchPathMultipleObject;
import com.cognos.developer.schemas.bibus._3.Sort;

public class ViewReports {
	/**
	 * Use this method to show the reports and queries in the content store.
	 * 
	 * @param connection
	 *            Connection to Server
	 * 
	 * @return Returns a string that either shows the name of each report or
	 *         query in the content store, or displays a message to indicate
	 *         that there are no reports or queries to show.
	 */

	public String viewReportsAndQueries(CRNConnect connection) {
		String output = new String();
		PropEnum props[] = new PropEnum[] { PropEnum.searchPath,
				PropEnum.defaultName };

		if (connection.getCMService() != null) {
			Sort sortOptions[] = { new Sort() };
			sortOptions[0].setOrder(OrderEnum.ascending);
			sortOptions[0].setPropName(PropEnum.defaultName);

			try {
				/**
				 * Use this method to query the reports in the content store.
				 * 
				 * @param "/content//report" Specifies the search path string so
				 *        that Content Manager can locate the requested objects,
				 *        which are reports in this example.
				 * @param props
				 *            Specifies alternate properties that you want
				 *            returned for the report object. When no properties
				 *            are specified, as in this example, the default
				 *            properties of searchPath and defaultName are
				 *            provided.
				 * @param sortOptions
				 *            Specifies the sort criteria in an array.
				 * @param QueryOptions
				 *            Specifies any options for this ReportNet method.
				 * 
				 * @return Returns an array of reports.
				 */

				BaseClass bc[] = connection.getCMService().query(
						new SearchPathMultipleObject("/content//report"),
						props, sortOptions, new QueryOptions());

				// If reports exist in the content store, the output shows the
				// report
				// name on one line, followed by a second line that shows the
				// search
				// path of the report.
				if (bc != null) {
					if (bc.length > 0) {
						output = output.concat("Reports:\n\n");

						for (int i = 0; i < bc.length; i++) {
							output = output.concat("  "
									+ bc[i].getDefaultName().getValue() + "\n");
							output = output
									.concat("      "
											+ bc[i].getSearchPath().getValue()
											+ "\n\n");
						}
					} else {
						output = output
								.concat("There are currently no reports to view.\n");
					}
				} else {
					output = output
							.concat("Error occurred in viewReportsAndQueries().");
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
				output = output.concat("View Reports:\nCannot connect to CM.\n"
						+ "Ensure that IBM Cognos is running");
			}

			try {
				/**
				 * Use this ReportNet method to query the query objects in the
				 * content store. Query objects are created when users save
				 * reports that they create using Query Studio.
				 * 
				 * @param "/content//query" Specifies the search path string so
				 *        that Content Manager can locate the requested objects,
				 *        which are queries in this example.
				 * @param props
				 *            Specifies alternate properties that you want
				 *            returned for the query object. When no properties
				 *            are specified, as in this example, the default
				 *            properties of searchPath and defaultName are
				 *            provided.
				 * @param sortOptions
				 *            Specifies the sort criteria in an array.
				 * @param QueryOptions
				 *            Specifies any options for this ReportNet method.
				 * 
				 * @return Returns an array of queries.
				 */

				BaseClass bc[] = connection.getCMService().query(
						new SearchPathMultipleObject("/content//query"), props,
						sortOptions, new QueryOptions());

				// If queries exist in the content store, the output shows the
				// query
				// name on one line, followed by a second line that shows the
				// search
				// path of the query.
				if (bc != null) {
					if (bc.length > 0) {
						output = output.concat("\n\nQueries:\n\n");

						for (int i = 0; i < bc.length; i++) {
							output = output.concat("  "
									+ bc[i].getDefaultName().getValue() + "\n");
							output = output
									.concat("      "
											+ bc[i].getSearchPath().getValue()
											+ "\n\n");
						}
					} else {
						output = output
								.concat("There are no queries to view.\n\n");
					}
				} else {
					output = output
							.concat("Error occurred in viewReportsAndQueries().\n");
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
				output = output.concat("View Reports:\nCannot connect to CM.\n"
						+ "Ensure that IBM Cognos is running\n");
			}
		} else {
			output = output
					.concat("Invalid parameter passed to viewReportsAndQueries().\n");
		}
		return output;
	}
}
