package com.ibm.cognos;

/** 
 Licensed Materials - Property of IBM

 IBM Cognos Products: DOCS

 (C) Copyright IBM Corp. 2005, 2008

 US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with
 IBM Corp.
 */
/**
 *
 * Copyright (C) 2008 Cognos ULC, an IBM Company. All rights reserved.
 * Cognos (R) is a trademark of Cognos ULC, (formerly Cognos Incorporated).
 * 
 * The MetaData class represents the metaData returned from the package.  
 * Seperated into QuerySubject (tables) and QueryItems (columns), QueryItems full Name (the search path)
 * MetaData also includes a vector of selected columns to create a report with.
 */
import java.util.List;
import java.util.Vector;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

public class MetaData {
	private String m_querySubject;
	private Vector m_queryItems;
	private Vector m_fullNameQueryItems;
	private Vector m_selectedColumns;

	public String getQuerySubject() {
		return m_querySubject;
	}

	public void setQuerySubject(String querySubject) {
		m_querySubject = querySubject;
	}

	public Vector getQueryItem() {
		return m_queryItems;
	}

	public void setQueryItem(Vector queryItems) {
		m_queryItems = queryItems;
	}

	public Vector getFullNameQueryItems() {
		return m_fullNameQueryItems;
	}

	public void setFullNameQueryItems(Vector queryItems) {
		m_fullNameQueryItems = queryItems;
	}

	public Vector getSelectedColumns() {
		return m_selectedColumns;
	}

	public void setSelectedColumns(Vector queryItems) {
		m_selectedColumns = queryItems;
	}

	/**
	 * ParseMetaData method will create a vector of MetaData objects by parsing
	 * (using DOM) the metadata XML returned from the content store. Basically a
	 * list of all tables and the columns.
	 * 
	 * querySubject = tables queryItems = columns
	 * 
	 * @param connection
	 * @param newReport
	 * @param defaultPackageName
	 * @return Vector
	 */
	public Vector parseMetaData(CRNConnect connection, ReportObject newReport,
			String defaultPackageName) {
		// retrieve the metadata from the selected package to display for
		// table/column selection.
		Vector packageMetaData = new Vector();

		if (defaultPackageName != null) {
			Document doc = newReport
					.getMetadata(connection, defaultPackageName);
			// returns a list of tables from the QuerySubject tag
			List tableList = (List) doc
					.selectNodes("/ResponseRoot/folder/folder/folder/querySubject");
			for (int i = 0; i < tableList.size(); i++) {
				Element eTable = (Element) tableList.get(i);
				Attribute nameAttrQuerySubject = eTable.attribute("name");
				String sTable = nameAttrQuerySubject.getValue();
				System.out.println(sTable);

				MetaData md = new MetaData();
				md.setQuerySubject(sTable);

				// set the current node to be the current QuerySubject
				Element eCurrent = (Element) doc
						.selectSingleNode("/ResponseRoot/folder/folder/folder/querySubject[@name='"
								+ sTable + "']");
				// retrieve a list of columns in this table or queryItems from
				// the current querySubject node.
				List columnList = (List) eCurrent.selectNodes("queryItem");
				Vector metaQueryItems = new Vector();
				Vector metaFullNameQueryItems = new Vector();
				for (int j = 0; j < columnList.size(); j++) {
					Element eColumn = (Element) columnList.get(j);
					Attribute attrQueryItemName = eColumn.attribute("name");
					Attribute attrQueryItemRef = eColumn.attribute("_ref");
					String sColumn = attrQueryItemName.getValue();
					String sFullColumnName = attrQueryItemRef.getValue();
					System.out.println("           " + sColumn);
					metaQueryItems.add(sColumn);
					metaFullNameQueryItems.add(sFullColumnName);
				}
				md.setQueryItem(metaQueryItems);
				md.setFullNameQueryItems(metaFullNameQueryItems);
				packageMetaData.add(md);
			}
		} else
			System.out.println("Problem: default package name unavailable.");

		return packageMetaData;
	}

}
