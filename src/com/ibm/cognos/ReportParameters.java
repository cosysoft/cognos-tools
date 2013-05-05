package com.ibm.cognos;

/** 
 Licensed Materials - Property of IBM

 IBM Cognos Products: DOCS

 (C) Copyright IBM Corp. 2005, 2008

 US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with
 IBM Corp.
 */
/**
 * ReportParameters.java
 *
 * Copyright (C) 2008 Cognos ULC, an IBM Company. All rights reserved.
 * Cognos (R) is a trademark of Cognos ULC, (formerly Cognos Incorporated).
 *
 * Description: This code sample demonstrates how to run and print a report with  
 *              prompts using the getParameters method.
 *              Use this method to return the list of parameters used by the 
 *              report, including optional parameters. This method also returns
 *              parameters from the model and stored procedures that are used 
 *              by the report. 
 */

import javax.swing.JOptionPane;

import com.cognos.developer.schemas.bibus._3.AsynchDetailParameters;
import com.cognos.developer.schemas.bibus._3.AsynchReply;
import com.cognos.developer.schemas.bibus._3.AsynchReplyStatusEnum;
import com.cognos.developer.schemas.bibus._3.BaseParameter;
import com.cognos.developer.schemas.bibus._3.Option;
import com.cognos.developer.schemas.bibus._3.Parameter;
import com.cognos.developer.schemas.bibus._3.ParameterValue;
import com.cognos.developer.schemas.bibus._3.ParmValueItem;
import com.cognos.developer.schemas.bibus._3.SearchPathSingleObject;
import com.cognos.developer.schemas.bibus._3.SimpleParmValueItem;

public class ReportParameters {
	/**
	 * 
	 * This method calls the getParameters method to to return the list of
	 * parameters used by the report.
	 * 
	 * @param connection
	 *            Specifies the object that provides the Connection to the
	 *            Server.
	 * @param reportPath
	 *            Specifies the search path of the report.
	 * @return Returns an array of report parameters.
	 */
	public BaseParameter[] getReportParameters(BaseClassWrapper report,
			CRNConnect connection) throws java.rmi.RemoteException {

		BaseParameter params[] = new Parameter[] {};
		AsynchReply response;
		String reportPathString = report.getBaseClassObject().getSearchPath()
				.getValue();
		SearchPathSingleObject reportPath = new SearchPathSingleObject();
		reportPath.set_value(reportPathString);

		// sn_dg_sdk_method_reportService_getParameters_start_1
		response = connection.getReportService().getParameters(reportPath,
				new ParameterValue[] {}, new Option[] {});
		// sn_dg_sdk_method_reportService_getParameters_end_1

		// If response is not immediately complete, call wait until complete
		if (!response.getStatus().equals(
				AsynchReplyStatusEnum.conversationComplete)) {
			while (!response.getStatus().equals(
					AsynchReplyStatusEnum.conversationComplete)) {
				response = connection.getReportService().wait(
						response.getPrimaryRequest(), new ParameterValue[] {},
						new Option[] {});
			}

		}

		// sn_dg_sdk_method_reportService_getParameters_start_2
		for (int i = 0; i < response.getDetails().length; i++) {
			if (response.getDetails()[i] instanceof AsynchDetailParameters)

			{
				params = ((AsynchDetailParameters) response.getDetails()[i])
						.getParameters();
			}
		}
		// sn_dg_sdk_method_reportService_getParameters_end_2

		return params;

	}

	/**
	 * This Java method assigns values to each parameter for the specified
	 * report.
	 * 
	 * @param reportName
	 *            Specifies the name of the report.
	 * @param prm
	 *            Specifies an array of parameters.
	 * @return params Returns an array of parameter values.
	 */
	// sn_dg_prm_smpl_runreport_P2_start_0
	public static ParameterValue[] setReportParameters(BaseParameter[] prm) {
		try {
			int numberOfParameters = 0;

			// Select the parameter values for the specified report.
			if (prm.length > 0) {
				numberOfParameters = prm.length;

				ParameterValue[] params = new ParameterValue[numberOfParameters];

				// Repeat for each parameter.
				for (int i = 0; i < prm.length; i++) {
					// Prompt the user to type a value for the parameter.
					// If the value is DateTime, the format must be in the ISO
					// 8601
					// format. For example, a date and time of
					// 2001-05-31T14:39:25.035Z
					// represents the thirty-first day of May in the year 2001.
					// The time,
					// measured in Coordinated Universal Time (UTC) as indicated
					// by the Z,
					// is 14 hours, 39 minutes, 25 seconds, and 35 milliseconds.
					String modelFilterItem = ((Parameter) prm[i])
							.getModelFilterItem();
					String item = modelFilterItem.substring(
							modelFilterItem.lastIndexOf("["),
							modelFilterItem.lastIndexOf("]") + 1);
					String inputValue = JOptionPane
							.showInputDialog("Please input a value for " + item
									+ " of datatype ["
									+ prm[i].getType().getValue() + "]");

					SimpleParmValueItem item1 = new SimpleParmValueItem();
					item1.setUse(inputValue);

					// Create a new array to contains the values for the
					// parameter.
					ParmValueItem pvi[] = new ParmValueItem[1];
					pvi[0] = item1;

					// Assign the values to the parameter.
					params[i] = new ParameterValue();
					params[i].setName(prm[i].getName());
					params[i].setValue(pvi);
				}
				return params;
				// sn_dg_prm_smpl_runreport_P2_end_0
			} else {
				return null;
			}
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}

}
