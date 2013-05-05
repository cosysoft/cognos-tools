package com.ibm.cognos;

/** 
 Licensed Materials - Property of IBM

 IBM Cognos Products: DOCS

 (C) Copyright IBM Corp. 2005, 2008

 US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with
 IBM Corp.
 */
/**
 * ReportObject.java
 *
 * Copyright (C) 2008 Cognos ULC, an IBM Company. All rights reserved.
 * Cognos (R) is a trademark of Cognos ULC, (formerly Cognos Incorporated).
 * 
 * This class contains methods for creating different types of reports.
 * 
 */

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.cognos.developer.schemas.bibus._3.AddOptions;
import com.cognos.developer.schemas.bibus._3.AnyTypeProp;
import com.cognos.developer.schemas.bibus._3.AsynchDetailReportMetadata;
import com.cognos.developer.schemas.bibus._3.AsynchDetailReportOutput;
import com.cognos.developer.schemas.bibus._3.AsynchOptionEnum;
import com.cognos.developer.schemas.bibus._3.AsynchOptionInt;
import com.cognos.developer.schemas.bibus._3.AsynchReply;
import com.cognos.developer.schemas.bibus._3.AsynchReplyStatusEnum;
import com.cognos.developer.schemas.bibus._3.AsynchRequest;
import com.cognos.developer.schemas.bibus._3.BaseClass;
import com.cognos.developer.schemas.bibus._3.FormFieldVar;
import com.cognos.developer.schemas.bibus._3.FormatEnum;
import com.cognos.developer.schemas.bibus._3.Option;
import com.cognos.developer.schemas.bibus._3.ParameterValue;
import com.cognos.developer.schemas.bibus._3.PropEnum;
import com.cognos.developer.schemas.bibus._3.QueryOptions;
import com.cognos.developer.schemas.bibus._3.Report;
import com.cognos.developer.schemas.bibus._3.ReportServiceMetadataSpecification;
import com.cognos.developer.schemas.bibus._3.RunOptionAnyURI;
import com.cognos.developer.schemas.bibus._3.RunOptionBoolean;
import com.cognos.developer.schemas.bibus._3.RunOptionEnum;
import com.cognos.developer.schemas.bibus._3.RunOptionStringArray;
import com.cognos.developer.schemas.bibus._3.SearchPathMultipleObject;
import com.cognos.developer.schemas.bibus._3.SearchPathSingleObject;
import com.cognos.developer.schemas.bibus._3.Sort;
import com.cognos.developer.schemas.bibus._3.Specification;
import com.cognos.developer.schemas.bibus._3.TokenProp;
import com.cognos.developer.schemas.bibus._3.UpdateActionEnum;

public class ReportObject {
	public API oAPI;
	private String sModel;

	public ReportObject(CRNConnect connection, String p_sModelConnectionName,
			String reportSpec) {
		if (p_sModelConnectionName != null) {
			sModel = p_sModelConnectionName;
		}
		if (reportSpec != null) {
			oAPI = new API(reportSpec);
		}
	}

	/**
	 * This function returns a boolean that reflect the fact that this user's
	 * session is ready to interface with the servers.
	 */
	public boolean createReport(String p_sModelConnectionName) {
		sModel = p_sModelConnectionName;
		return createReport(sModel, true);
	}

	public void setModel(String p_sModelConnectionName) {
		sModel = p_sModelConnectionName;
	}

	public boolean createReport(String p_sModelConnectionName, boolean isList) {
		sModel = p_sModelConnectionName;
		try {
			oAPI = new API();
			oAPI.addModelPath(p_sModelConnectionName);
			oAPI.addQueries();
			oAPI.addQuery("Query1");
			oAPI.addLayouts();
			oAPI.addLayout();
			oAPI.addReportPages();
			oAPI.addPage("Page1");
			oAPI.addPageBody();
			oAPI.addPageBodyContents();

			if (isList) {
				oAPI.addList("Query1");
				oAPI.addCSS("border-collapse:collapse");
				oAPI.addListColumns();
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void addDataItem(String p_sColumnTitle, String p_sExpression) {
		boolean bAggregate = false;
		oAPI.addDataItem(p_sColumnTitle, p_sExpression, bAggregate);
	}

	// sn_dg_prm_smpl_modifyreport_P3_start_0
	public void addColumn(String p_sColumnTitle, String p_sExpression,
			int position) {
		addDataItem(p_sColumnTitle, p_sExpression);
		oAPI.addListColumn(p_sColumnTitle, position);
	}

	// sn_dg_prm_smpl_modifyreport_P3_end_0

	public void addColumns(Vector columnsTitle, Vector columnExpression) {
		for (int i = 0; i < columnsTitle.size(); i++) {
			String sColumnTitle = (String) columnsTitle.elementAt(i);
			String sExpression = (String) columnExpression.elementAt(i);
			addDataItem(sColumnTitle, sExpression);
			oAPI.addListColumn(sColumnTitle, 0);
		}
	}

	public void modifyColumnTitles(Vector columnTitleToChange,
			Vector columnTitleNewName) {
		// modify any column names that were changed.
		for (int k = 0; k < columnTitleToChange.size(); k++) {
			modifyColumnTitle((String) columnTitleToChange.elementAt(k),
					(String) columnTitleNewName.elementAt(k));
		}
	}

	public void modifyColumns(Vector originalColumnExpression,
			Vector originalColumns, Vector originalColumnTitle,
			Vector columnExpression, Vector columnsTitle) {
		// check to see which columns need to be removed from the report
		for (int i = 0; i < originalColumns.size(); i++) {
			String sOriginalColumnReference = (String) originalColumns
					.elementAt(i);
			String sOriginalColumnExpression = (String) originalColumnExpression
					.elementAt(i);
			String sOriginalColumnTitle = (String) originalColumnTitle
					.elementAt(i);

			boolean columnFound = false;
			for (int j = 0; j < columnExpression.size(); j++) {
				if (sOriginalColumnExpression.equals(columnExpression
						.elementAt(j))) {
					columnFound = true;
					break;
				} else {
					columnFound = false;
				}
			}
			if (columnFound == false) // we have a column to remove here.
			{
				System.out.println("remove the column \""
						+ sOriginalColumnExpression + "\"");
				removeColumn(sOriginalColumnExpression,
						sOriginalColumnReference, sOriginalColumnTitle);
			}
		}

		// check to see which columns need to be added to the report
		for (int i = 0; i < columnExpression.size(); i++) {
			String sColumnExpression = (String) columnExpression.elementAt(i);
			String sColumnTitle = (String) columnsTitle.elementAt(i);
			boolean columnFound = false;
			for (int j = 0; j < originalColumnExpression.size(); j++) {
				if (sColumnExpression.equals(originalColumnExpression
						.elementAt(j))) {
					columnFound = true;
					break;
				} else {
					columnFound = false;
				}
			}
			if (columnFound == false) {
				// we have a column to add here.
				System.out.println("add the column = " + sColumnExpression);
				addColumn(sColumnTitle, sColumnExpression, 0);
			}
		}
	}

	public void modifyColumnTitle(String oldTitle, String newTitle) {
		oAPI.modifyTitle(oldTitle, newTitle);
	}

	public void removeColumn(String colExpression, String colRef,
			String colTitle) {
		oAPI.removeColumn(colRef, colExpression, colTitle);
	}

	public void addFilter(String filter) {
		oAPI.addFilterExpression(filter);
	}

	public void sortOn(String p_sColumns[], String p_sSorts[]) {
		String sColumns[] = p_sColumns;
		String sSorts[] = p_sSorts;
		if (sColumns.length == 0) {
			return;
		}
		for (int i = 0; i < sColumns.length; i++) {
			oAPI.addDataItemSort(sColumns[i], sSorts[i]);
		}
	}

	public String getXML() {
		return oAPI.getXML();
	}

	public Document getMetadata(CRNConnect connection, String p_sModel) {
		Option[] options = new Option[2];
		AsynchOptionInt primaryThreshold = new AsynchOptionInt();
		AsynchOptionInt secondaryThreshold = new AsynchOptionInt();

		String sModel = p_sModel;

		String sXML = "<metadataRequest connection=\"" + sModel + "\">"
				+ "<Metadata Depth='' " + "no_collections='1'>"
				+ "<Properties>" + "<Property name='*/@name'/>"
				+ "<Property name='*/@datatype'/>"
				+ "<Property name='*/@_path'/>" + "<Property name='*/@_ref'/>"
				+ "<Property name='*/@usage'/>" + "<Property name='./folder'/>"
				+ "<Property name='./querySubject'/>"
				+ "<Property name='./queryItem'/>" + "</Properties>"
				+ "</Metadata>" + "</metadataRequest>";

		primaryThreshold.setName(AsynchOptionEnum.primaryWaitThreshold);
		primaryThreshold.setValue(0);
		secondaryThreshold.setName(AsynchOptionEnum.secondaryWaitThreshold);
		secondaryThreshold.setValue(0);

		options[0] = primaryThreshold;
		options[1] = secondaryThreshold;

		Document oDom = null;
		try {
			boolean foundMetadata = false;
			AsynchReply runResponse;
			String sMetaData = "";
			ReportServiceMetadataSpecification metadataSpec = new ReportServiceMetadataSpecification();
			AsynchDetailReportMetadata reportMetadata = new AsynchDetailReportMetadata();

			metadataSpec.setValue(new Specification(sXML));

			// Since both primary and secondary wait thresholds are 0,
			// this request will not return until it is complete.

			// sn_dg_sdk_method_reportService_runSpecification_metadata_start_1
			runResponse = connection.getReportService().runSpecification(
					metadataSpec, new ParameterValue[] {}, options);

			for (int i = 0; i < runResponse.getDetails().length
					&& !foundMetadata; i++) {
				if (runResponse.getDetails()[i] instanceof AsynchDetailReportMetadata) {
					foundMetadata = true;
					reportMetadata = (AsynchDetailReportMetadata) runResponse
							.getDetails()[i];
					sMetaData = reportMetadata.getMetadata().toString();
				}
			}
			// sn_dg_sdk_method_reportService_runSpecification_metadata_end_1

			// sn_dg_prm_smpl_modifyreport_P2_start_0
			// This strips out the leading XML declaration
			sMetaData = sMetaData.substring(sMetaData.indexOf("?>") + 2);
			SAXReader xmlReader = new SAXReader();
			ByteArrayInputStream bais = new ByteArrayInputStream(
					sMetaData.getBytes("UTF-8"));
			oDom = xmlReader.read((InputStream) bais);
			// sn_dg_prm_smpl_modifyreport_P2_end_0

		} catch (Exception e) {
			e.printStackTrace();
		}
		return oDom;
	}

	private static FormFieldVar[] makeFormFieldVars(String[] pairs) {
		Vector allffv = new Vector();
		for (int i = 0; i < pairs.length - 1; i++) {
			FormFieldVar ffv = new FormFieldVar();
			ffv.setName(pairs[i]);
			ffv.setValue(pairs[++i]);
			ffv.setFormat(FormatEnum.not_encrypted);
			allffv.add(ffv);
		}
		FormFieldVar[] ffv = new FormFieldVar[allffv.size()];
		allffv.copyInto(ffv);

		return ffv;
	}

	public String renderReport(CRNConnect connection, String reportSearchPath) {
		// Provide the appropriate run options
		// - do not save output
		// - request for XHTML output format
		// - request that the null.xsl be used to process the raw data
		AsynchReply rsr = null;
		RunOptionBoolean saveOutput = new RunOptionBoolean();
		saveOutput.setName(RunOptionEnum.saveOutput);
		saveOutput.setValue(false);

		RunOptionStringArray outputFormat = new RunOptionStringArray();
		outputFormat.setName(RunOptionEnum.outputFormat);
		outputFormat.setValue(new String[] { "XHTML" });

		RunOptionAnyURI xslURL = new RunOptionAnyURI();
		xslURL.setName(RunOptionEnum.xslURL);
		xslURL.setValue("null.xsl");

		try {
			SearchPathSingleObject reportPath = new SearchPathSingleObject(
					reportSearchPath);
			rsr = connection.getReportService().run(reportPath,
					new ParameterValue[] {},
					new Option[] { saveOutput, outputFormat, xslURL });

			AsynchRequest rsq = null;

			while ((rsr.getStatus() != AsynchReplyStatusEnum.complete)
					&& (rsr.getStatus() != AsynchReplyStatusEnum.conversationComplete)) {
				rsq = rsr.getPrimaryRequest();
				rsr = connection.getReportService().wait(rsq,
						new ParameterValue[] {}, new Option[] {});
			}

			AsynchDetailReportOutput reportOutput = null;
			for (int i = 0; i < rsr.getDetails().length; i++) {
				if (AsynchDetailReportOutput.class
						.isInstance(rsr.getDetails()[i])) {
					reportOutput = (AsynchDetailReportOutput) rsr.getDetails()[i];
				}
			}

			return reportOutput.getOutputPages()[0];

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error during report execution");
			return null;
		}

	}

	public void updateReportNS() {
		// add the xmlns back into the modified report spec
		oAPI.updateXMLNS();
	}

	public Vector getColumnNames() {
		Vector items = oAPI.getDataItemReferences();
		return items;
	}

	public Vector getColumnExpressions() {
		Vector items = oAPI.getDataItemExpressions();
		return items;
	}

	public Vector getColumnTitles() {
		Vector items = oAPI.getColumnTitles();
		return items;
	}

	public boolean saveReport(CRNConnect connection, BaseClass parent,
			String reportName) {
		// sn_dg_prm_smpl_modifyreport_P4_start_0
		Report rpt = new Report();
		AddOptions addOpts = new AddOptions();
		TokenProp rptDefaultName = new TokenProp();
		AnyTypeProp ap = new AnyTypeProp();
		rptDefaultName.setValue(reportName);
		String reportXML = getXML();

		int iStartReport = reportXML.indexOf("<report");
		String reportOut = reportXML.substring(iStartReport);

		ap.setValue(reportOut);
		rpt.setDefaultName(rptDefaultName);
		rpt.setSpecification(ap);
		addOpts.setUpdateAction(UpdateActionEnum.replace);

		String parentPath = parent.getSearchPath().getValue();
		try {
			connection.getReportService().add(
					new SearchPathSingleObject(parentPath), rpt, addOpts);
		}
		// sn_dg_prm_smpl_modifyreport_P4_end_0
		catch (Exception e) {
			System.out.println("error during save " + e);
			return false;
		}
		return true;
	}

	public boolean updateReport(CRNConnect connection, String parentSearchPath,
			String p_sName) {
		String sName = p_sName;
		Report rpt = new Report();
		AddOptions opt = new AddOptions();
		TokenProp sp = new TokenProp();
		AnyTypeProp ap = new AnyTypeProp();
		sp.setValue(sName);
		String reportXML = getXML();

		int iStartReport = reportXML.indexOf("<report");
		String reportOut = reportXML.substring(iStartReport);

		ap.setValue(reportOut);
		rpt.setDefaultName(sp);
		rpt.setSpecification(ap);
		opt.setUpdateAction(UpdateActionEnum.replace);

		try {
			connection.getReportService().add(
					new SearchPathSingleObject(parentSearchPath), rpt, opt);
		} catch (Exception e) {
			System.out.println("error during update " + e);
		}
		return true;
	}

	public Document getPackages(CRNConnect connection, String sPath) {
		Document oDom = null;
		Element packagesElement;
		try {
			com.cognos.developer.schemas.bibus._3.BaseClass oBase[];
			oBase = connection.getCMService().query(
					new SearchPathMultipleObject(sPath),
					new PropEnum[] { PropEnum.defaultName, PropEnum.source,
							PropEnum.dispatcherPath, PropEnum.searchPath },
					new Sort[] {}, new QueryOptions());
			packagesElement = DocumentHelper.createElement("packages");
			oDom = DocumentHelper.createDocument(packagesElement);
			for (int i = 0; i < oBase.length; i++) {
				if (oBase[i]
						.getClass()
						.getName()
						.toString()
						.equals("com.cognos.developer.schemas.bibus._3._package")) {
					Element oElement = DocumentHelper.createElement("package");
					String oBaseName = oBase[i].getDefaultName().getValue();
					String oBaseSearchPath = oBase[i].getSearchPath()
							.getValue();
					oElement.addElement("name").setText(oBaseName);
					oElement.addElement("searchPath").setText(oBaseSearchPath);
					oDom.getRootElement().add(oElement);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return oDom;
	}

}