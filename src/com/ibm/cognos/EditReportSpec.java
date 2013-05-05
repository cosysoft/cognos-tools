package com.ibm.cognos;

/** 
 Licensed Materials - Property of IBM

 IBM Cognos Products: DOCS

 (C) Copyright IBM Corp. 2005, 2008

 US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with
 IBM Corp.
 */
/**
 * EditReportSpec
 *
 * Copyright (C) 2008 Cognos ULC, an IBM Company. All rights reserved.
 * Cognos (R) is a trademark of Cognos ULC, (formerly Cognos Incorporated).
 * 
 * EditReportSpec class contains methods for modifying a simple list 
 * report
 *
 */

import com.cognos.developer.schemas.bibus._3.AsynchDetailReportObject;
import com.cognos.developer.schemas.bibus._3.AsynchReply;
import com.cognos.developer.schemas.bibus._3.AsynchReplyStatusEnum;
import com.cognos.developer.schemas.bibus._3.Option;
import com.cognos.developer.schemas.bibus._3.ParameterValue;
import com.cognos.developer.schemas.bibus._3.ReportServiceQueryOptionBoolean;
import com.cognos.developer.schemas.bibus._3.ReportServiceQueryOptionEnum;
import com.cognos.developer.schemas.bibus._3.ReportServiceQueryOptionSpecificationFormat;
import com.cognos.developer.schemas.bibus._3.SearchPathSingleObject;
import com.cognos.developer.schemas.bibus._3.SpecificationFormatEnum;

public class EditReportSpec {

	// get the Report Spec
	public String getReportSpec(CRNConnect connect, BaseClassWrapper report) {
		String reportSpec = "";

		if ((connect.getReportService() != null) && (report != null)) {
			// sn_dg_prm_smpl_modifyreport_P1_start_0
			try {

				String reportPath = report.getBaseClassObject().getSearchPath()
						.getValue();

				Option[] qOpts = new Option[2];

				ReportServiceQueryOptionBoolean upgradeSpecFlag = new ReportServiceQueryOptionBoolean();
				upgradeSpecFlag.setName(ReportServiceQueryOptionEnum.upgrade);
				upgradeSpecFlag.setValue(true);

				ReportServiceQueryOptionSpecificationFormat specFormat = new ReportServiceQueryOptionSpecificationFormat();
				specFormat
						.setName(ReportServiceQueryOptionEnum.specificationFormat);
				specFormat.setValue(SpecificationFormatEnum.report);

				qOpts[0] = upgradeSpecFlag;
				qOpts[1] = specFormat;

				// sn_dg_sdk_method_reportService_query_start_1
				AsynchReply qResult = connect.getReportService().query(
						new SearchPathSingleObject(reportPath),
						new ParameterValue[] {}, qOpts);
				// sn_dg_sdk_method_reportService_query_end_1

				if ((qResult.getStatus() == AsynchReplyStatusEnum.working)
						|| (qResult.getStatus() == AsynchReplyStatusEnum.stillWorking)) {
					while ((qResult.getStatus() == AsynchReplyStatusEnum.working)
							|| (qResult.getStatus() == AsynchReplyStatusEnum.stillWorking)) {
						qResult = connect.getReportService().wait(
								qResult.getPrimaryRequest(),
								new ParameterValue[] {}, new Option[] {});
					}
				}

				// sn_dg_sdk_method_reportService_query_start_2

				// extract the report spec
				if (qResult.getDetails() != null) {
					for (int i = 0; i < qResult.getDetails().length; i++) {
						if (qResult.getDetails()[i] instanceof AsynchDetailReportObject) {
							reportSpec = ((AsynchDetailReportObject) qResult
									.getDetails()[i]).getReport()
									.getSpecification().getValue();
						}
					}
				}
				// sn_dg_sdk_method_reportService_query_end_2

			}
			// sn_dg_prm_smpl_modifyreport_P1_end_0
			catch (java.rmi.RemoteException remoteEx) {
				System.out.println(remoteEx.getMessage());
				remoteEx.printStackTrace();
			}
		}
		return reportSpec;
	}
}
