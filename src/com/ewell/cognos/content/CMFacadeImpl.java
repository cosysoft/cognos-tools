package com.ewell.cognos.content;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.CheckBoxTreeItem;
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
import com.ibm.cognos.ReportObject;

public class CMFacadeImpl implements CMFacade {

	private CRNConnect connect;
	private static String searchPath = "/content";

	@Override
	public ContentItem getFolder(String searchPath) {
		try {
			BaseClass myCMObject = null;

			SearchPathMultipleObject cmSearchPath = new SearchPathMultipleObject(
					searchPath);
			PropEnum[] properties = { PropEnum.defaultName,
					PropEnum.searchPath, PropEnum.objectClass,
					PropEnum.hasChildren, PropEnum.iconURI, PropEnum.storeID };
			myCMObject = (connect.getCMService().query(cmSearchPath,
					properties, new Sort[] {}, new QueryOptions()))[0];

			return new ContentItem(myCMObject);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	private List<ContentItem> getContentItems(String searchPath) {

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
		try {
			children = connect.getCMService().query(cmSearchPath, properties,
					nodeSorts, new QueryOptions());
		} catch (RemoteException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		List<ContentItem> items = new ArrayList<ContentItem>();

		for (int i = 0; i < children.length; i++) {
			ContentItem item = new ContentItem(children[i]);
			if (item.isTypeOfReport() || item.isTypeOfFolder()) {
				items.add(item);
			}

		}
		return items;
	}

	@Override
	public ContentItem buildContentTree(ContentItem root) {
		innerBuild(root);
		return root;
	}

	private void innerBuild(ContentItem item) {
		if (!item.isChildrenHas()) {
			return;
		}
		List<ContentItem> children = this.getContentItems(item.getSearchPath());
		item.setChildren(children);
		for (ContentItem i : item.getChildren()) {
			if (i.isChildrenHas()) {
				innerBuild(i);
			}
		}
	}

	public CMFacadeImpl(CRNConnect connect) {
		this.connect = connect;
	}

	@Override
	public TreeItem<ContentItem> buildContentTree() {

		TreeItem<ContentItem> root = null;

		try {
			ContentItem item = this.getFolder(searchPath);
			if (item != null) {
				root = new TreeItem<ContentItem>(item);
			}
			if (item.isChildrenHas()) {
				innerBuild(root);
			}
		} catch (Exception remoteEx) {
			remoteEx.printStackTrace();
		}

		return root;
	}

	private void innerBuild(TreeItem<ContentItem> item) throws Exception {
		if (!item.getValue().isChildrenHas()) {
			return;
		}
		List<ContentItem> children = this.getContentItems(item.getValue()
				.getSearchPath());

		for (ContentItem c : children) {
			item.getChildren().add(new TreeItem<>(c));
		}

		for (TreeItem<ContentItem> ii : item.getChildren()) {
			if (ii.getValue().isChildrenHas()) {
				innerBuild(ii);
			}
		}

	}

	@Override
	public ContentItem getContentItem(String searchPath) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getReportSpec(ContentItem report2) {
		String reportSpec = "";

		BaseClass report = report2.getContent();

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

	@Override
	public int editReportSpec(ContentItem oldReport2, ContentItem preport,
			String reportSpec) {

		BaseClass oldReport = oldReport2.getContent();

		String packageName = getPackageSearchPath(oldReport);

		// Create a new report object which uses DOM to traverse the
		// report specification.
		ReportObject newReport = new ReportObject(connect, packageName,
				reportSpec);

		newReport.updateReportNS();
		// newReport.saveReport(connect, preport, oldReport);
		newReport.updateReport(connect, preport.getSearchPath(), oldReport
				.getDefaultName().getValue());
		return 0;
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

	@Override
	public CheckBoxTreeItem<ContentItem> buildContentTreeCK() {
		CheckBoxTreeItem<ContentItem> root = null;
		try {
			ContentItem item = this.getFolder(searchPath);
			if (item != null) {
				root = new CheckBoxTreeItem<ContentItem>(item);
			}
			if (item.isChildrenHas()) {
				innerBuild(root);
			}
		} catch (Exception remoteEx) {
			remoteEx.printStackTrace();
		}

		return root;
	}

	private void innerBuild(CheckBoxTreeItem<ContentItem> item)
			throws Exception {
		if (!item.getValue().isChildrenHas()) {
			return;
		}
		List<ContentItem> children = this.getContentItems(item.getValue()
				.getSearchPath());

		for (ContentItem c : children) {
			item.getChildren().add(new CheckBoxTreeItem<>(c));
		}

		for (TreeItem<ContentItem> ii : item.getChildren()) {
			if (ii.getValue().isChildrenHas()) {
				innerBuild(ii);
			}
		}

	}

	@Override
	public int[] editReportSpecBatch(List<ContentItem> oldReports,
			List<ContentItem> preports, List<String> reportSpecs) {
		return null;
	}
}
