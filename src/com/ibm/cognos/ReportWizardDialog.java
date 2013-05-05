package com.ibm.cognos;

/** 
 Licensed Materials - Property of IBM

 IBM Cognos Products: DOCS

 (C) Copyright IBM Corp. 2005

 US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with
 IBM Corp.
 */
/**
 * ReportWizardDialog.java
 *
 * Copyright (C) 2005 Cognos ULC, an IBM Company. All rights reserved.
 * Cognos (R) is a trademark of Cognos ULC, (formerly Cognos Incorporated).
 *
 * Description: This code sample creates a dialog box for adding 
 * columns to a report. 
 */

import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

public class ReportWizardDialog extends JDialog {
	// Define the components of the dialog box.
	private JLabel tablesLabel;
	private JComboBox tablesComboBox;
	private JLabel columnsLabel;
	private JList availableColumnsList;
	private JLabel selectedLabel;
	private JList selectedColumnsList;
	private JButton okButton;
	private JButton cancelButton;
	private JButton addButton;
	private JButton deleteButton;

	private JButton modifyColumnTitleButton;

	// Define the vector that will contain the tables and columns for
	// the metadata in the package.
	public Vector m_MetaData;
	// Define the vector that will contain the column names to be displayed
	// in the list of selected columns.
	public Vector m_selectedColumns;
	private Vector m_originalSelectedColumns;
	// Define the vector that will contain the fully qualified names of the
	// columns used to create the report.
	// Each column name in this vector is associated with a column name in
	// the m_selectedColumns vector.
	public Vector m_qualifiedColumns;
	public Vector m_columnTitles;

	public Vector m_columnTitleToChange;
	public Vector m_columnTitleNewName;

	boolean m_bShowModifyTitleButton;

	public ReportWizardDialog(Frame owner, String title, boolean modal,
			Vector md, Vector displayColumns, Vector fullNameColumns,
			Vector columnTitles, Vector columnTitleExisting,
			Vector columnTitleNew, boolean bShowModifyTitleButton) {
		super(owner, title, modal);

		// Initialize the arrays that contain the table and column data.
		m_selectedColumns = displayColumns;
		m_originalSelectedColumns = (Vector) displayColumns.clone();
		m_MetaData = md;
		m_qualifiedColumns = fullNameColumns;
		m_columnTitles = columnTitles;
		m_columnTitleToChange = columnTitleExisting;
		m_columnTitleNewName = columnTitleNew;
		m_bShowModifyTitleButton = bShowModifyTitleButton;

		// Place all components in the window and given each component
		// an initial value.
		buildDialogWindow(getContentPane());

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				cancelButtonClicked();
			}
		});
		setSize(580, 430);
	}

	/**
	 * This Java method adds the components to the user interface.
	 * 
	 * @param aContainer
	 *            Specifies a container for the components, such as the content
	 *            pane of the window.
	 */
	private void buildDialogWindow(Container aContainer) {
		// Specify that a layout manager is not needed to determine how
		// components are sized and positioned within the container.
		aContainer.setLayout(null);

		// Create a label that introduces the tables
		// and set its location, size, and contents.
		tablesLabel = new JLabel("Tables:");
		tablesLabel.setLocation(10, 1);
		tablesLabel.setSize(80, 30);
		aContainer.add(tablesLabel);

		// Create a vector that contains the tables from
		// the metadata in the package.
		Vector tables = new Vector();
		for (int i = 0; i < m_MetaData.size(); i++) {
			tables.add(i,
					((MetaData) m_MetaData.elementAt(i)).getQuerySubject());
		}

		// Create a combo box and set its location, size, and the event handler
		// to respond to the user's actions, such as a mouse click.
		tablesComboBox = new JComboBox(tables);
		tablesComboBox.setLocation(10, 30);
		tablesComboBox.setSize(250, 25);
		tablesComboBox.addActionListener(new tablesComboBoxListener());
		aContainer.add(tablesComboBox);

		// Create a list of available columns from the metadata in the package.
		// Set the size, location, and scrolling of the list.
		// Initialize the list with the columns from the first table.
		MetaData md = (MetaData) m_MetaData.elementAt(0);
		availableColumnsList = new JList(md.getQueryItem());
		JScrollPane scrollPane = new JScrollPane(availableColumnsList);
		scrollPane.setLocation(10, 100);
		scrollPane.setSize(200, 230);
		aContainer.add(scrollPane);

		// Create a label that introduces the available columns and set
		// its location, size, and contents.
		columnsLabel = new JLabel("Available columns:");
		columnsLabel.setLocation(10, 70);
		columnsLabel.setSize(120, 30);
		aContainer.add(columnsLabel);

		// Create a list of selected columns and set the size and location
		// of the list.
		selectedColumnsList = new JList(m_selectedColumns);
		JScrollPane scrollPane2 = new JScrollPane(selectedColumnsList);
		scrollPane2.setLocation(325, 100);
		scrollPane2.setSize(200, 230);
		aContainer.add(scrollPane2);

		// Create a label that introduces the selected columns and set
		// its location, size, and contents.
		selectedLabel = new JLabel("Selected columns:");
		selectedLabel.setLocation(325, 70);
		selectedLabel.setSize(120, 30);
		aContainer.add(selectedLabel);

		// Create an Add button and set
		// its location, size, and the event handler to respond to the user's
		// actions, such as a mouse click.
		addButton = new JButton(">>>");
		addButton.setLocation(230, 150);
		addButton.setSize(75, 30);
		addButton.addActionListener(new addButtonListener());
		aContainer.add(addButton);

		// Create a Delete button and set
		// its location, size, and the event handler to respond to the user's
		// actions, such as a mouse click.
		deleteButton = new JButton("<<<");
		deleteButton.setLocation(230, 220);
		deleteButton.setSize(75, 30);
		deleteButton.addActionListener(new deleteButtonListener());
		aContainer.add(deleteButton);

		// Create a Modify Column Title button and set
		// its location, size, and the event handler to respond to the user's
		// actions, such as a mouse click.
		if (m_bShowModifyTitleButton) {
			modifyColumnTitleButton = new JButton("Modify Column Title");
			modifyColumnTitleButton.setLocation(325, 30);
			modifyColumnTitleButton.setSize(150, 30);
			modifyColumnTitleButton
					.addActionListener(new modifyColumnTitleButtonListener());
			aContainer.add(modifyColumnTitleButton);
		}

		// Create an OK button and set
		// its location, size, and the event handler to respond to the user's
		// actions, such as a mouse click.
		okButton = new JButton("Ok");
		okButton.setLocation(325, 350);
		okButton.setSize(85, 30);
		okButton.addActionListener(new okButtonListener());
		aContainer.add(okButton);

		// Create a Cancel button and set
		// its location, size, and the event handler to respond to the user's
		// actions, such as a mouse click.
		cancelButton = new JButton("Cancel");
		cancelButton.setLocation(440, 350);
		cancelButton.setSize(85, 30);
		cancelButton.addActionListener(new cancelButtonListener());
		aContainer.add(cancelButton);
	}

	/**
	 * This Java class is an event handler that runs when a user selects a
	 * table. It adds a column to the array of available columns for each column
	 * in the table.
	 **/
	private class tablesComboBoxListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String selectedTable = (String) tablesComboBox.getSelectedItem();
			System.out.println("Selected item=" + selectedTable);
			for (int i = 0; i < m_MetaData.size(); i++) {
				String findTable = ((MetaData) m_MetaData.elementAt(i))
						.getQuerySubject();
				if (selectedTable.equals(findTable)) {
					availableColumnsList.setListData(((MetaData) m_MetaData
							.elementAt(i)).getQueryItem());
				}
			}
		}
	}

	/**
	 * This Java class is an event handler that runs when a user clicks the Add
	 * button. It adds the selected columns to the array of available columns.
	 **/
	private class addButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String selectedColumn = (String) availableColumnsList
					.getSelectedValue();
			String selectedTable = (String) tablesComboBox.getSelectedItem();

			if (selectedColumn == null) {
				return;
			}

			m_selectedColumns.add(selectedColumn);
			m_columnTitles.add(selectedColumn);
			selectedColumnsList.setListData(m_selectedColumns);

			// When we add to the display list, also add to the fullName list.
			for (int i = 0; i < m_MetaData.size(); i++) {
				String findTable = ((MetaData) m_MetaData.elementAt(i))
						.getQuerySubject();
				// This is a matching table.
				if (selectedTable.equals(findTable)) {
					// Find a matching column.
					Vector columnsList = ((MetaData) m_MetaData.elementAt(i))
							.getQueryItem();
					for (int j = 0; j < columnsList.size(); j++) {
						String findColumn = (String) columnsList.elementAt(j);
						if (selectedColumn.equals(findColumn)) {
							MetaData m_MetaDataElement = (MetaData) m_MetaData
									.elementAt(i);
							String columnFullName = (String) m_MetaDataElement
									.getFullNameQueryItems().elementAt(j);
							m_qualifiedColumns.add(columnFullName);
							return;
						}
					}
				}
			}

		}
	}

	/**
	 * This Java class is an event handler that runs when a user clicks the
	 * Delete button. It removes the full names of the selected columns from the
	 * array of columns used to create the report.
	 **/
	private class deleteButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String selectedColumn = (String) selectedColumnsList
					.getSelectedValue();

			if (selectedColumn != null) {
				int index = m_selectedColumns.indexOf(selectedColumn);

				m_selectedColumns.remove(selectedColumn);
				m_qualifiedColumns.removeElementAt(index);
				m_columnTitles.removeElementAt(index);

				selectedColumnsList.setListData(m_selectedColumns);
			}
		}
	}

	/**
	 * This Java class is an event handler that runs when a user clicks the OK
	 * button. The event handler calls a Java method that releases the resources
	 * for the dialog box.
	 **/
	private class okButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			okButtonClicked();
		}
	}

	/**
	 * This Java class is an event handler that runs when a user selects the
	 * title of a column. It opens an input dialog box that prompts user to
	 * enter a new title for the column, and then removed the old title from the
	 * array of column titles and adds the new title.
	 **/
	private class modifyColumnTitleButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String selectedColumn = (String) selectedColumnsList
					.getSelectedValue();

			if (selectedColumn != null) {
				int index = m_selectedColumns.indexOf(selectedColumn);
				String columnTitleToChange = (String) m_columnTitles.get(index);
				String columnTitleNewName = null;
				do {
					columnTitleNewName = JOptionPane
							.showInputDialog("Please enter a new column "
									+ "name for column < "
									+ columnTitleToChange + " >");
				} while (columnTitleNewName == null);

				m_columnTitleToChange.add(columnTitleToChange);
				m_columnTitleNewName.add(columnTitleNewName);
				int index2 = m_columnTitles.indexOf(columnTitleToChange);
				m_columnTitles.remove(index2);
				m_columnTitles.add(index2, columnTitleNewName);
			}
		}
	}

	/**
	 * This Java class is an event handler that runs when a user clicks the
	 * Cancel button. The event handler calls a Java method that cancels the
	 * current action.
	 **/
	private class cancelButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			cancelButtonClicked();
		}
	}

	/**
	 * This Java method releases all screen resources used by this dialog box,
	 * its subcomponents, and all of its children.
	 **/
	private void okButtonClicked() {
		dispose();
	}

	/**
	 * This Java method removes the elements in the array that contains the
	 * fully qualified names of the columns used to create the report and
	 * releases all screen resources used by this dialog box, its subcomponents,
	 * and all of its children.
	 **/
	private void cancelButtonClicked() {
		// restore the orginal columns to clear any changes that might have been
		// made
		m_qualifiedColumns.removeAllElements();
		m_qualifiedColumns.addAll((Vector) m_originalSelectedColumns.clone());
		dispose();
	}
}
