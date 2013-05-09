package com.ewell.ui;

import javafx.scene.control.TreeItem;

import com.cognos.developer.schemas.bibus._3.AsynchDetailReportObject;
import com.cognos.developer.schemas.bibus._3.AsynchReply;
import com.cognos.developer.schemas.bibus._3.AsynchReplyStatusEnum;
import com.cognos.developer.schemas.bibus._3.BaseClass;
import com.cognos.developer.schemas.bibus._3.Option;
import com.cognos.developer.schemas.bibus._3.OrderEnum;
import com.cognos.developer.schemas.bibus._3.ParameterValue;
import com.cognos.developer.schemas.bibus._3.PropEnum;
import com.cognos.developer.schemas.bibus._3.QueryOptions;
import com.cognos.developer.schemas.bibus._3.ReportServiceQueryOptionBoolean;
import com.cognos.developer.schemas.bibus._3.ReportServiceQueryOptionEnum;
import com.cognos.developer.schemas.bibus._3.ReportServiceQueryOptionSpecificationFormat;
import com.cognos.developer.schemas.bibus._3.SearchPathMultipleObject;
import com.cognos.developer.schemas.bibus._3.SearchPathSingleObject;
import com.cognos.developer.schemas.bibus._3.Sort;
import com.cognos.developer.schemas.bibus._3.SpecificationFormatEnum;
import com.ibm.cognos.CRNConnect;
import com.ibm.cognos.Logon;
import com.ibm.cognos.ReportObject;

/**
 * 
 * @author cosysoft
 * 
 */
public class BiBusHelper {

	private CRNConnect connect = new CRNConnect();

	public CRNConnect getConnect() {
		return connect;
	}

	public void setConnect(CRNConnect connect) {
		this.connect = connect;
	}

	static String searchPath = "/content"; // / root

	static String passportID;

	private static BiBusHelper instance;

	public static BiBusHelper getInstance() {
		if (instance == null) {
			instance = new BiBusHelper();
		}
		return instance;
	}

	public TreeItem<BaseClass> buildContentTree() {

		Logon sessionLogon = new Logon();
		connect.connectToCognosServer();

		while (!Logon.loggedIn(connect)) {
			sessionLogon.logon(connect);
		}

		TreeItem<BaseClass> root = null;
		BaseClass myCMObject = null;

		try {
			myCMObject = BiBusHelper.getInstance().getCMSignleObject(
					searchPath, connect);
			if (myCMObject != null) {
				root = new TreeItem<BaseClass>(myCMObject);
			}
			if (myCMObject.getHasChildren().isValue()) {
				innerBuild(root, connect);
			}
		} catch (Exception remoteEx) {
			remoteEx.printStackTrace();
		}

		System.out.println(myCMObject.getDefaultName());

		return root;
	}

	void innerBuild(TreeItem<BaseClass> item, CRNConnect connect)
			throws Exception {
		if (!item.getValue().getHasChildren().isValue()) {
			return;
		}
		String searchPath = item.getValue().getSearchPath().getValue();

		BaseClass[] children = BiBusHelper.getInstance().getCMMultipleObject(
				searchPath, connect);

		for (int i = 0; i < children.length; i++) {
			item.getChildren().add(new TreeItem<BaseClass>(children[i]));

		}

		for (TreeItem<BaseClass> ii : item.getChildren()) {
			if (ii.getValue().getHasChildren().isValue()) {
				innerBuild(ii, connect);
			}
		}

	}

	public BaseClass[] getCMMultipleObject(String searchPath, CRNConnect connect)
			throws Exception {

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
		children = connect.getCMService().query(cmSearchPath, properties,
				nodeSorts, new QueryOptions());

		return children;
	}

	public BaseClass getCMSignleObject(String searchPath, CRNConnect connect)
			throws Exception {
		BaseClass myCMObject = null;

		SearchPathMultipleObject cmSearchPath = new SearchPathMultipleObject(
				searchPath);
		PropEnum[] properties = { PropEnum.defaultName, PropEnum.searchPath,
				PropEnum.objectClass, PropEnum.hasChildren, PropEnum.iconURI,
				PropEnum.storeID };
		myCMObject = (connect.getCMService().query(cmSearchPath, properties,
				new Sort[] {}, new QueryOptions()))[0];

		return myCMObject;
	}

	public String getReportSpec(BaseClass report) {
		String reportSpec = "";

		if ((connect.getReportService() != null) && (report != null)
		// && (connect.getDefaultSavePath() != null)
		) {
			// sn_dg_prm_smpl_modifyreport_P1_start_0
			try {

				String reportPath = report.getSearchPath().getValue();

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

	public String editSpec(BaseClass oldReport, BaseClass preport,
			String reportSpec) {
		String output = "";

		String packageName = getPackageSearchPath(oldReport);

		// Create a new report object which uses DOM to traverse the
		// report specification.
		ReportObject newReport = new ReportObject(connect, packageName,
				reportSpec);

		newReport.updateReportNS();
		// newReport.saveReport(connect, preport, oldReport);
		newReport.updateReport(connect, preport.getSearchPath().getValue(),
				oldReport.getDefaultName().getValue());
		output = "Report: " + oldReport + " updated successfully";
		return output;
	}

	private String getPackageSearchPath(BaseClass report) {

		String reportSearchPath = report.getSearchPath().getValue();
		String packagePath = "";

		try {
			AsynchReply response = connect.getReportService().query(
					new SearchPathSingleObject(reportSearchPath),
					new ParameterValue[] {}, new Option[] {});

			for (int i = 0; i < response.getDetails().length; i++) {
				if (response.getDetails()[i] instanceof AsynchDetailReportObject)

				{
					AsynchDetailReportObject det = (AsynchDetailReportObject) response
							.getDetails()[i];
					packagePath = det.getReport().getMetadataModel().getValue()[0]
							.getSearchPath().getValue();
				}
			}

			return packagePath;
		}

		catch (Exception e) {
			System.out
					.println("An error occurred in the getPackageSearchPath Java method.\n"
							+ e);
			return "An error occurred in the getPackageSearchPath Java method.";
		}

		// return packagePath;
	}

}
