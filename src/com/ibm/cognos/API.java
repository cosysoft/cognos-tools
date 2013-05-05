package com.ibm.cognos;

/** 
 Licensed Materials - Property of IBM

 IBM Cognos Products: DOCS

 (C) Copyright IBM Corp. 2005, 2010

 US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with
 IBM Corp.
 */
/**
 *
 * API.java
 *
 * Copyright (C) 2008 Cognos ULC, an IBM Company. All rights reserved.
 * Cognos (R) is a trademark of Cognos ULC, (formerly Cognos Incorporated).
 *
 * This class is used by a DOM parser to add the appropriate element
 * tags to a new report.
 */

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.io.SAXReader;

public class API /* implements ReportBuilder */
{
	private String sReportSpec;

	public static final String sREPORT = "report";

	private Document oDocument;

	public API(String sReportSpec) {
		try {
			// when creating a dom document, temporarily remove the default
			// namespace from the XML otherwise selecting nodes will fail.
			String start = null;
			String end = null;
			int index = sReportSpec.indexOf("xmlns=", 0);
			if (index >= 0) {
				start = sReportSpec.substring(0, index);
				end = sReportSpec
						.substring(sReportSpec
								.indexOf("http://developer.cognos.com/schemas/report/8.0/") + 48);
				sReportSpec = start + end;
			}
			// load the spec into the DOM
			SAXReader xmlReader = new SAXReader();
			ByteArrayInputStream bais = new ByteArrayInputStream(
					sReportSpec.getBytes("UTF-8"));
			oDocument = xmlReader.read(bais);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public API() {
		Element reportElement = DocumentHelper.createElement("report");

		// In dom4j 1.6.1, can no longer add xmlns with addAttribute
		reportElement.addNamespace("",
				"http://developer.cognos.com/schemas/report/8.0/");
		reportElement.addAttribute("expressionLocale", "en-us");
		oDocument = DocumentHelper.createDocument(reportElement);
	}

	public void updateXMLNS() {
		Element e = oDocument.getRootElement();

		// In dom4j 1.6.1, can no longer add xmlns with addAttribute
		e.addNamespace("", "http://developer.cognos.com/schemas/report/8.0/");
	}

	public String getXML() {
		String strXML = oDocument.asXML();

		// strip out extraneous empty xmlns attributes added by use of
		// addAttribute
		strXML = strXML.replaceAll(" xmlns=\"\"", "");

		return strXML;
	}

	public void addLayouts() {
		Element e = DocumentHelper.createElement("layouts");
		oDocument.getRootElement().add(e);
	}

	public void addLayout() {
		Element n = (Element) oDocument.selectSingleNode("/report/layouts");
		if (n == null) {
			addLayouts();
			n = (Element) oDocument.selectSingleNode("/report/layouts");
		}

		Element e = DocumentHelper.createElement("layout");
		n.add(e);
	}

	public void addReportPages() {
		Element n = (Element) oDocument
				.selectSingleNode("/report/layouts/layout");
		if (n == null) {
			addLayout();
			n = (Element) oDocument.selectSingleNode("/report/layouts/layout");
		}

		Element e = DocumentHelper.createElement("reportPages");
		n.add(e);
	}

	public void addPage(String p_sName) {
		Element n = (Element) oDocument
				.selectSingleNode("/report/layouts/layout/reportPages");
		if (n == null) {
			addReportPages();
			n = (Element) oDocument
					.selectSingleNode("/report/layouts/layout/reportPages");
		}

		Element e = DocumentHelper.createElement("page");
		Element eStyle = buildStyle("pg");

		e.addAttribute("name", p_sName);
		e.add(eStyle);
		n.add(e);
	}

	public void addPageBody() {
		Element n = (Element) oDocument
				.selectSingleNode("/report/layouts/layout/reportPages/page");
		if (n == null) {
			addPage("Page1");
			n = (Element) oDocument
					.selectSingleNode("/report/layouts/layout/reportPages/page");
		}

		Element e = DocumentHelper.createElement("pageBody");
		Element eStyle = buildStyle("pb");

		e.add(eStyle);
		n.add(e);
	}

	public void addPageBodyContents() {
		Element n = (Element) oDocument
				.selectSingleNode("/report/layouts/layout/reportPages/page/pageBody");
		if (n == null) {
			addPageBody();
			n = (Element) oDocument
					.selectSingleNode("/report/layouts/layout/reportPages/page/pageBody");
		}

		Element e = DocumentHelper.createElement("contents");
		n.add(e);

	}

	public void addList() {
		addList("Query1");
	}

	public void addList(String p_sName) {
		Element n = (Element) oDocument
				.selectSingleNode("/report/layouts/layout/reportPages/page/pageBody/contents");
		if (n == null) {
			addPageBodyContents();
			n = (Element) oDocument
					.selectSingleNode("/report/layouts/layout/reportPages/page/pageBody/contents");
		}

		Element e = DocumentHelper.createElement("list");

		Element eStyle = buildStyle("ls");

		e.addAttribute("refQuery", p_sName);
		e.add(eStyle);

		n.add(e);
	}

	public void addStyle() {
		addStyle("Query1");
	}

	public void addStyle(String p_sName) {
		String sName = p_sName;
		String quotChar = "\'";
		if (sName.indexOf(quotChar) >= 0) {
			quotChar = "\"";
		}
		String elementString = "/report/layouts/layout/reportPages/page/pageBody/contents/list[@refQuery="
				+ quotChar + sName + quotChar + "]";
		Element n = (Element) oDocument.selectSingleNode(elementString);
		if (n == null) {
			addList(p_sName);
			n = (Element) oDocument.selectSingleNode(elementString);
		}
		Element e = DocumentHelper.createElement("style");
		n.add(e);
	}

	public void addCSS(String p_sName) {
		Element n = (Element) oDocument
				.selectSingleNode("/report/layouts/layout/reportPages/page/pageBody/contents/list/style");
		if (n == null) {
			addStyle();
			n = (Element) oDocument
					.selectSingleNode("/report/layouts/layout/reportPages/page/pageBody/contents/list/style");
		}

		Element e = DocumentHelper.createElement("CSS");
		e.addAttribute("value", p_sName);
		n.add(e);
	}

	public void addQueries() {
		Element e = DocumentHelper.createElement("queries");
		oDocument.getRootElement().add(e);
	}

	public Element buildStyle(String sName) {
		Element eStyle = DocumentHelper.createElement("style");
		Element eDefaultStyles = DocumentHelper.createElement("defaultStyles");
		Element eDefinedStyle = DocumentHelper.createElement("defaultStyle");
		eDefinedStyle.addAttribute("refStyle", sName);

		eDefaultStyles.add(eDefinedStyle);
		eStyle.add(eDefaultStyles);

		return eStyle;
	}

	public void getQueries() {
		List n = (List) oDocument.selectSingleNode("/report/queries/query");
		if (n != null) {
			for (int i = 0; i < n.size(); i++) {
				String x = ((Element) n.get(i)).getName();
				System.out.println(x);
			}
		}
	}

	public void addQuery() {
		addQuery("Query1");
	}

	public void addQuery(String p_sName) {
		Element n = (Element) oDocument.selectSingleNode("/report/queries");
		if (n == null) {
			addQueries();
			n = (Element) oDocument.selectSingleNode("/report/queries");
		}

		Element eModel = DocumentHelper.createElement("model");
		Element eSource = DocumentHelper.createElement("source");

		Element e = DocumentHelper.createElement("query");
		eSource.add(eModel);
		e.add(eSource);

		e.addAttribute("name", p_sName);
		n.add(e);

	}

	public void addSelection() {
		Element n = (Element) oDocument
				.selectSingleNode("/report/queries/query");
		if (n == null) {
			addQuery();
			n = (Element) oDocument.selectSingleNode("/report/queries/query");
		}

		Element eSelection = DocumentHelper.createElement("selection");
		n.add(eSelection);
	}

	/**
	 * addDataItem
	 * 
	 * @param p_sName
	 * @param p_sExpression
	 */
	public void addDataItem(String p_sName, String p_sExpression) {
		addDataItem(p_sName, p_sExpression, false);
	}

	public void addDataItem(String p_sName, String p_sExpression,
			boolean p_bAggregate) {
		addDataItem(p_sName, p_sExpression, p_bAggregate, null);
	}

	public void addDataItem(String p_sName, String p_sExpression,
			boolean p_bAggregate, String p_sSort) {
		Element nSelection = (Element) oDocument
				.selectSingleNode("/report/queries/query/selection");
		if (nSelection == null) {
			addSelection();
			nSelection = (Element) oDocument
					.selectSingleNode("/report/queries/query/selection");
		}

		// Create the dataItem element
		Element eDataItem = DocumentHelper.createElement("dataItem");
		eDataItem.addAttribute("name", p_sName);

		// Add an aggregation element, if necessary
		if (p_bAggregate) {
			eDataItem.addAttribute("aggregate", "true");
		}

		// Add the expression for the dataItem
		Element eExpression = DocumentHelper.createElement("expression");
		eExpression.setText(p_sExpression);
		eDataItem.add(eExpression);

		// Add the dataItem to the selection element
		nSelection.add(eDataItem);

		// add the sort item to the report, if necessary
		if (p_sSort != null && !p_sSort.equals("")) {
			addDataItemSort(p_sName, p_sSort);
		}
	}

	public void addModelPath(String p_sName) {
		Element n = (Element) oDocument.getRootElement();

		Element eModelPath = DocumentHelper.createElement("modelPath");
		eModelPath.setText(p_sName + "/model[@name='model']");

		n.add(eModelPath);
	}

	public void addModel() {
		Element n = (Element) oDocument
				.selectSingleNode("/report/queries/query");
		if (n == null) {
			addQuery();
			n = (Element) oDocument.selectSingleNode("/report/queries/query");
		}

		Element e = DocumentHelper.createElement("model");
		n.add(e);
	}

	public void addListColumnRowSpan(String p_sName) {
		String sName = p_sName;
		String quotChar = "\'";
		if (sName.indexOf(quotChar) >= 0) {
			quotChar = "\"";
		}

		// Need to find the column corresponding to the referenced data item
		String elementString = "/report/layouts/layout/reportPages/page/pageBody/contents/list/"
				+ "listColumns/listColumn/listColumnBody/contents/textItem/dataSource/"
				+ "dataItemValue[@refDataItem="
				+ quotChar
				+ sName
				+ quotChar
				+ "]";
		Element n = (Element) oDocument.selectSingleNode(elementString);

		// Need to add a listColumnRowSpan node to listColumnBody for that
		// column (4 levels up)
		Element eColumnBody = n.getParent().getParent().getParent().getParent();
		Element eListColumnRowSpan = DocumentHelper
				.createElement("listColumnRowSpan");
		eListColumnRowSpan.addAttribute("refDataItem", sName);
	}

	public void addListGroups() {

		Element n = (Element) oDocument
				.selectSingleNode("/report/layouts/layout/reportPages/page/pageBody/contents/list/");
		if (n == null) {
			addList();
			n = (Element) oDocument
					.selectSingleNode("/report/layouts/layout/reportPages/page/pageBody/contents/list/");
		}

		Element eListGroups = DocumentHelper.createElement("listGroups");
		n.add(eListGroups);

	}

	public void addListGroup(String p_sName) {

		Element n = (Element) oDocument
				.selectSingleNode("/report/layouts/layout/reportPages/page/pageBody/contents/list/"
						+ "listColumns/listGroups");
		if (n == null) {
			addListGroups();
			n = (Element) oDocument
					.selectSingleNode("/report/layouts/layout/reportPages/page/pageBody/contents/list/"
							+ "listColumns/listGroups");
		}

		// Add the listGroup node to listGroups
		Element eListGroup = DocumentHelper.createElement("listGroup");
		n.addAttribute("refDataItem", p_sName);
		n.add(eListGroup);
	}

	public void removeColumn(String sColumnReference, String sColumnExpression,
			String sColumnTitle) {
		Element nDataItem = null;
		Element nRefItem = null;
		Element nColumn = null;

		String quotChar = "\'";
		if (sColumnExpression.indexOf(quotChar) >= 0) {
			quotChar = "\"";
		}
		nDataItem = (Element) (oDocument
				.selectSingleNode("/report/queries/query/selection/dataItem[@name="
						+ quotChar + sColumnReference + quotChar + "]"));

		if (nDataItem != null) {
			nDataItem.detach();
		} else {
			System.out.println("Remove column failed for column "
					+ sColumnExpression);
			// return;
		}

		quotChar = "\'";
		if (sColumnReference.indexOf(quotChar) >= 0) {
			quotChar = "\"";
		}

		quotChar = "\'";
		if (sColumnReference.indexOf(quotChar) >= 0) {
			quotChar = "\"";
		}
		// remove the list column
		nColumn = (Element) oDocument
				.selectSingleNode("/report/layouts/layout/reportPages/page/pageBody/contents/"
						+ "list/listColumns/listColumn/listColumnTitle/contents/textItem/"
						+ "dataSource/dataItemLabel[@refDataItem="
						+ quotChar
						+ sColumnReference + quotChar + "]");

		if (nColumn != null) {
			nColumn.getParent().getParent().getParent().getParent().getParent()
					.detach();
		} else {
			System.out
					.println("Remove list column failed for columnReference: "
							+ sColumnReference);
		}

	}

	public void addDetailFilter() {
		Element n = (Element) oDocument
				.selectSingleNode("/report/queries/query");
		if (n == null) {
			addQuery();
			n = (Element) oDocument.selectSingleNode("/report/queries/query");
		}

		Element eDetailFilter = DocumentHelper.createElement("detailFilter");
		n.add(eDetailFilter);
	}

	public void addFilter() {
		Element n = (Element) oDocument
				.selectSingleNode("/report/queries/query/detailFilter");
		if (n == null) {
			addDetailFilter();
			n = (Element) oDocument
					.selectSingleNode("/report/queries/query/detailFilter");
		}

		Element eFilter = DocumentHelper.createElement("filter");
		eFilter.addAttribute("use", "required");
		n.add(eFilter);
	}

	public void addFilterExpression(String filter) {
		Element n = (Element) oDocument
				.selectSingleNode("/report/queries/query/detailFilter/filter");
		if (n == null) {
			addFilter();
			n = (Element) oDocument
					.selectSingleNode("/report/queries/query/detailFilter/filter");
		}

		Element eFilterExpression = DocumentHelper
				.createElement("filterExpression");
		eFilterExpression.setText(filter);
		n.add(eFilterExpression);
	}

	public void addSortList() {
		Element n = (Element) oDocument
				.selectSingleNode("/report/layouts/layout/reportPages/page/pageBody/contents/list");
		if (n == null) {
			addList();
			n = (Element) oDocument
					.selectSingleNode("/report/layouts/layout/reportPages/page/pageBody/contents/list");
		}

		Element eSortList = DocumentHelper.createElement("sortList");
		n.add(eSortList);
	}

	public void addDataItemSort(String p_sElementName, String p_sSort) {
		Element n = (Element) oDocument
				.selectSingleNode("/report/layouts/layout/reportPages/page/pageBody/contents/list/sortList");
		if (n == null) {
			addSortList();
			n = (Element) oDocument
					.selectSingleNode("/report/layouts/layout/reportPages/page/pageBody/contents/list/sortList");
		}

		Element eSort = DocumentHelper.createElement("sortItem");
		eSort.addAttribute("refDataItem", p_sElementName);
		eSort.addAttribute("sortOrder", p_sSort);
		n.add(eSort);
	}

	public Vector getDataItemReferences() {
		Vector columnsList = new Vector();
		Vector fullNameColumnsList = new Vector();
		List columnList = (List) oDocument
				.selectNodes("/report/queries/query/selection/dataItem[@name]");

		for (int i = 0; i < columnList.size(); i++) {
			Element eColumn = (Element) columnList.get(i);
			String sColumn = eColumn.attributeValue("name");
			columnsList.add(sColumn);
		}
		return columnsList;
	}

	public Vector getDataItemExpressions() {
		Vector fullNameColumnsList = new Vector();
		List columnList = (List) oDocument
				.selectNodes("/report/queries/query/selection/dataItem/expression");

		for (int i = 0; i < columnList.size(); i++) {
			Element eColumn = (Element) columnList.get(i);
			String sColumn = eColumn.getText();
			fullNameColumnsList.add(sColumn);
		}
		return fullNameColumnsList;
	}

	public Vector getColumnTitles() {
		Vector columnTitles = new Vector();

		// Check for column titles based on dataItemLabel
		List columnTitleList = (List) oDocument
				.selectNodes("/report/layouts/layout/reportPages/page/pageBody/contents/"
						+ "list/listColumns/listColumn/listColumnTitle/"
						+ "contents/textItem/dataSource/dataItemLabel");
		for (int i = 0; i < columnTitleList.size(); i++) {
			Element e = (Element) columnTitleList.get(i);
			String sColumnTitle = null;
			sColumnTitle = e.attributeValue(new QName("refDataItem"));
			columnTitles.add(sColumnTitle);
		}

		// Check for columnTitles base on staticValue
		List columnStaticTitleList = (List) oDocument
				.selectNodes("/report/layouts/layout/reportPages/page/pageBody/contents/"
						+ "list/listColumns/listColumn/listColumnTitle/"
						+ "contents/textItem/dataSource/staticValue");
		for (int i = 0; i < columnStaticTitleList.size(); i++) {
			Element e = (Element) columnStaticTitleList.get(i);
			String sColumnTitle = null;
			sColumnTitle = e.getText();
			columnTitles.add(sColumnTitle);
		}
		return columnTitles;
	}

	public void modifyTitle(String title, String newTitle) {
		Element n = null;

		String quotChar = "\'";
		if (title.indexOf(quotChar) >= 0) {
			quotChar = "\"";
		}
		String nodeDataItemTitle = "/report/layouts/layout/reportPages/page/pageBody/contents/list/listColumns/"
				+ "listColumn/listColumnTitle/contents/textItem/dataSource/dataItemLabel[@refDataItem="
				+ quotChar + title + quotChar + "]";
		String nodeStaticTitle = "/report/layouts/layout/reportPages/page/pageBody/contents/list/listColumns/"
				+ "listColumn/listColumnTitle/contents/textItem/dataSource/staticValue";

		n = (Element) oDocument.selectSingleNode(nodeDataItemTitle);
		if (n == null) {
			n = (Element) oDocument.selectSingleNode(nodeStaticTitle);
			if (n == null) {
				System.out.println("Modify column title failed for " + title
						+ " title not found.");
				return;
			}
		}
		Element nDataSource = n.getParent();
		n.detach();

		Element eTitle = DocumentHelper.createElement("staticValue");
		eTitle.setText(newTitle);

		nDataSource.add(eTitle);
	}

	/**
	 * addListColumn
	 * 
	 * @param p_sName
	 * @param position
	 *            (to insert in the default position pass 0 to this method)
	 *            (default position = insert after the last child element.)
	 */
	public void addListColumn(String p_sName, int position) {
		Element n = null;
		n = (Element) oDocument
				.selectSingleNode("/report/layouts/layout/reportPages/page/pageBody/contents"
						+ "/list/listColumns");

		// Create an empty column node
		Element eCol = DocumentHelper.createElement("listColumn");

		// Prepare all the bits to contain the column title
		Element eTitle = DocumentHelper.createElement("listColumnTitle");
		Element eStyleTitle = buildStyle("lt");

		Element eTContents = DocumentHelper.createElement("contents");
		Element eTText = DocumentHelper.createElement("textItem");
		Element eTSrc = DocumentHelper.createElement("dataSource");
		Element eLabel = DocumentHelper.createElement("dataItemLabel");
		eLabel.addAttribute("refDataItem", p_sName);

		// Prepare all the bits to contain the column data
		Element eBody = DocumentHelper.createElement("listColumnBody");
		Element eStyle = buildStyle("lm");

		Element eBContents = DocumentHelper.createElement("contents");
		Element eBText = DocumentHelper.createElement("textItem");
		Element eBSrc = DocumentHelper.createElement("dataSource");
		Element eValue = DocumentHelper.createElement("dataItemValue");
		eValue.addAttribute("refDataItem", p_sName);

		// Piece the Title together in the right order
		eTSrc.add(eLabel);
		eTText.add(eTSrc);
		eTContents.add(eTText);
		eTitle.add(eStyleTitle);
		eTitle.add(eTContents);

		// Piece the Body together
		eBSrc.add(eValue);
		eBText.add(eBSrc);
		eBContents.add(eBText);
		eBody.add(eStyle);
		eBody.add(eBContents);

		// Add the title and body to the column
		eCol.add(eTitle);
		eCol.add(eBody);

		if (position > 0) {
			n.content().add(position - 1, eCol);
		} else {
			n.add(eCol);
		}
	}

	public void addListColumns() {
		Element n = (Element) oDocument
				.selectSingleNode("/report/layouts/layout/reportPages/page/pageBody/contents/list");
		Element e = DocumentHelper.createElement("listColumns");
		n.add(e);
	}

}
