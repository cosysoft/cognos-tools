package com.ibm.cognos;

/** 
 Licensed Materials - Property of IBM

 IBM Cognos Products: DOCS

 (C) Copyright IBM Corp. 2005, 2008

 US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with
 IBM Corp.
 */
/**
 * reportrunner.java
 *
 * Copyright (C) 2008 Cognos ULC, an IBM Company. All rights reserved.
 * Cognos (R) is a trademark of Cognos ULC, (formerly Cognos Incorporated).
 *
 */

import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.rpc.ServiceException;

import org.apache.axis.AxisFault;
import org.apache.axis.client.Stub;

import com.cognos.developer.schemas.bibus._3.AsynchDetailReportOutput;
import com.cognos.developer.schemas.bibus._3.AsynchOptionEnum;
import com.cognos.developer.schemas.bibus._3.AsynchOptionInt;
import com.cognos.developer.schemas.bibus._3.AsynchReply;
import com.cognos.developer.schemas.bibus._3.AsynchReplyStatusEnum;
import com.cognos.developer.schemas.bibus._3.BiBusHeader;
import com.cognos.developer.schemas.bibus._3.CAM;
import com.cognos.developer.schemas.bibus._3.FormFieldVar;
import com.cognos.developer.schemas.bibus._3.FormatEnum;
import com.cognos.developer.schemas.bibus._3.HdrSession;
import com.cognos.developer.schemas.bibus._3.Option;
import com.cognos.developer.schemas.bibus._3.OutputEncapsulationEnum;
import com.cognos.developer.schemas.bibus._3.ParameterValue;
import com.cognos.developer.schemas.bibus._3.ReportService_PortType;
import com.cognos.developer.schemas.bibus._3.ReportService_ServiceLocator;
import com.cognos.developer.schemas.bibus._3.RunOptionBoolean;
import com.cognos.developer.schemas.bibus._3.RunOptionEnum;
import com.cognos.developer.schemas.bibus._3.RunOptionOutputEncapsulation;
import com.cognos.developer.schemas.bibus._3.RunOptionStringArray;
import com.cognos.developer.schemas.bibus._3.SearchPathSingleObject;

/**
 * Run a report from the samples deployment archive, and save its output as
 * HTML.
 * 
 * Note that this application does no error handling; errors will cause ugly
 * exception stack traces to appear on the console, and the application will
 * exit ungracefully.
 */
public class reportrunner {

	/**
	 * This function will prepare the biBusHeader.
	 * 
	 * @param repService
	 *            An initialized report service object.
	 * @param user
	 *            Log in as this User ID in the specified namespace.
	 * @param pass
	 *            Password for user in the specified namespace.
	 * @param name
	 *            The security namespace to log on to.
	 */
	static void setUpHeader(ReportService_PortType repService, String user,
			String pass, String name) {
		// Scrub the header to remove the conversation context.
		BiBusHeader bibus = BIBusHeaderHelper
				.getHeaderObject(((Stub) repService).getResponseHeader("",
						"biBusHeader"));

		if (bibus != null) {
			if (bibus.getTracking() != null) {
				if (bibus.getTracking().getConversationContext() != null) {
					bibus.getTracking().setConversationContext(null);
				}
			}

			return;
		}

		// Set up a new biBusHeader for the "logon" action.
		bibus = new BiBusHeader();
		bibus.setCAM(new CAM());
		bibus.getCAM().setAction("logonAs");
		bibus.setHdrSession(new HdrSession());

		FormFieldVar ffs[] = new FormFieldVar[3];

		ffs[0] = new FormFieldVar();
		ffs[0].setName("CAMUsername");
		ffs[0].setValue(user);
		ffs[0].setFormat(FormatEnum.not_encrypted);

		ffs[1] = new FormFieldVar();
		ffs[1].setName("CAMPassword");
		ffs[1].setValue(pass);
		ffs[1].setFormat(FormatEnum.not_encrypted);

		ffs[2] = new FormFieldVar();
		ffs[2].setName("CAMNamespace");
		ffs[2].setValue(name);
		ffs[2].setFormat(FormatEnum.not_encrypted);

		bibus.getHdrSession().setFormFieldVars(ffs);

		((Stub) repService).setHeader(
				"http://developer.cognos.com/schemas/bibus/3/", "biBusHeader",
				bibus);
	}

	static void showUsage() {
		String usage = "";
		usage += "Run a report and save the output as HTML.\n\n";
		usage += "usage:\n\n";
		usage += "-host hostName\n\tHost name of an IBM Cognos server.\n";
		usage += "\t Default: localhost\n";
		usage += "-port portNumber\n\tPort number of the server.\n";
		usage += "\t Default: 9300\n";
		usage += "-report searchPath\n\tSearch path to a report.\n";
		usage += "\t Default: "
				+ "/content/folder[@name='Samples']/folder[@name='Models']/package[@name='GO Data Warehouse (query)']"
				+ "/folder[@name='SDK Report Samples']"
				+ "/report[@name='Product Introduction List']\n";
		usage += "-output outputPath\n\tFile name for the output HTML document.\n";
		usage += "\t Default: reportrunner.html\n";
		usage += "-user userName\n\tUser to log on as. "
				+ "Must exist in 'userNamespace'.\n";
		usage += "\t Default: none\n";
		usage += "-password userPassword\n\tPassword for 'userName' ";
		usage += "in 'userNamespace'.\n";
		usage += "\t Default: none\n";
		usage += "-namespace userNamespace\n\tSecurity namespace to log on to.\n";
		usage += "\t Default: none\n";

		System.out.println(usage);
	}

	/**
	 * Replace all occurrences of "pattern" in "str" with "replace".
	 * 
	 * @param str
	 *            The source string.
	 * @param pattern
	 *            The pattern to search for.
	 * @param replace
	 *            The replacement for pattern.
	 * 
	 * @return Identical to the original string with all instances of "pattern"
	 *         replaced by "replace"
	 */
	public static String replaceSubstring(String str, String pattern,
			String replace) {
		int strLen = str.length();
		int patternLen = pattern.length();
		int start = 0, end = 0;
		StringBuffer result = new StringBuffer(strLen);
		char[] chars = new char[strLen];

		while ((end = str.indexOf(pattern, start)) >= 0) {
			str.getChars(start, end, chars, 0);
			result.append(chars, 0, end - start).append(replace);
			start = end + patternLen;
		}

		str.getChars(start, strLen, chars, 0);
		result.append(chars, 0, strLen - start);

		return result.toString();
	}

	/*
	 * The main entry point for the application.
	 */
	public static void main(String[] args) {
		// Change this URL if your server is in another location or a
		// different port.
		String serverHost = "localhost";
		String serverPort = "9300";

		// Change this search path if you want to run a different report.
		String reportPath = "/content/folder[@name='Samples']/folder[@name='Models']/package[@name='GO Data Warehouse (query)']"
				+ "/folder[@name='SDK Report Samples']"
				+ "/report[@name='Product Introduction List']";

		// Output filename
		String outputPath = "reportrunner.html";

		// Specify a user to log in as.
		String userName = "";
		String userPassword = "";
		String userNamespace = "";

		// Parse the command-line arguments.
		for (int idx = 0; idx < args.length; idx++) {
			if (args[idx].compareTo("-host") == 0) {
				serverHost = args[idx + 1];
			} else if (args[idx].compareTo("-port") == 0) {
				serverPort = args[idx + 1];
			} else if (args[idx].compareTo("-report") == 0) {
				reportPath = args[idx + 1];
			} else if (args[idx].compareTo("-output") == 0) {
				outputPath = args[idx + 1];
			} else if (args[idx].compareTo("-user") == 0) {
				userName = args[idx + 1];
			} else if (args[idx].compareTo("-password") == 0) {
				userPassword = args[idx + 1];
			} else if (args[idx].compareTo("-namespace") == 0) {
				userNamespace = args[idx + 1];
			} else {
				System.out.println("Unknown argument: " + args[idx]);
				showUsage();
				System.exit(0);
			}
			idx++;
		}

		// Create a connection to a report server.
		String Cognos_URL = "http://" + serverHost + ":" + serverPort
				+ "/p2pd/servlet/dispatch";

		System.out.println("Creating connection to " + serverHost + ":"
				+ serverPort + "...");
		System.out.println("Server URL: " + Cognos_URL);
		ReportService_ServiceLocator reportServiceLocator = new ReportService_ServiceLocator();

		ReportService_PortType repService = null;

		try {
			repService = reportServiceLocator.getreportService(new URL(
					Cognos_URL));
		} catch (MalformedURLException ex) {
			System.out.println("Caught a MalformedURLException:\n"
					+ ex.getMessage());
			System.out.println("Server URL was: " + Cognos_URL);
			System.exit(-1);
		} catch (ServiceException ex) {
			System.out.println("Caught a ServiceException: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(-1);
		}

		System.out.println("... done.");

		// Set up the biBusHeader for a logon.
		if ((userName.length() > 0) && (userPassword.length() > 0)
				&& (userNamespace.length() > 0)) {
			System.out.println("Logging on as " + userName + " in the "
					+ userNamespace + " namespace.");
			setUpHeader(repService, userName, userPassword, userNamespace);
		}

		// Set up the report parameters.
		ParameterValue parameters[] = new ParameterValue[] {};

		Option runOptions[] = new Option[5];

		RunOptionBoolean saveOutput = new RunOptionBoolean();
		saveOutput.setName(RunOptionEnum.saveOutput);
		saveOutput.setValue(false);
		runOptions[0] = saveOutput;

		// TODO: Output format should be specified on the command-line
		RunOptionStringArray outputFormat = new RunOptionStringArray();
		outputFormat.setName(RunOptionEnum.outputFormat);
		outputFormat.setValue(new String[] { "HTML" });
		runOptions[1] = outputFormat;

		RunOptionOutputEncapsulation outputEncapsulation = new RunOptionOutputEncapsulation();
		outputEncapsulation.setName(RunOptionEnum.outputEncapsulation);
		outputEncapsulation.setValue(OutputEncapsulationEnum.none);
		runOptions[2] = outputEncapsulation;

		AsynchOptionInt primaryWait = new AsynchOptionInt();
		primaryWait.setName(AsynchOptionEnum.primaryWaitThreshold);
		primaryWait.setValue(0);
		runOptions[3] = primaryWait;

		AsynchOptionInt secondaryWait = new AsynchOptionInt();
		secondaryWait.setName(AsynchOptionEnum.secondaryWaitThreshold);
		secondaryWait.setValue(0);
		runOptions[4] = secondaryWait;

		// Now, run the report.
		try {
			System.out.println("Running the report...");
			System.out.println("Report search path is:");
			System.out.println(reportPath);

			AsynchReply res = repService.run(new SearchPathSingleObject(
					reportPath), parameters, runOptions);

			System.out.println("... done.");

			// The report is finished, let's fetch the results and save them to
			// a file.
			if (res.getStatus() == AsynchReplyStatusEnum.complete)

			{
				AsynchDetailReportOutput reportOutput = null;

				for (int i = 0; i < res.getDetails().length; i++) {
					if (res.getDetails()[i] instanceof AsynchDetailReportOutput) {
						reportOutput = (AsynchDetailReportOutput) res
								.getDetails()[i];
						break;
					}
				}

				String[] data = reportOutput.getOutputPages();

				System.out.println("Writing report output to " + outputPath
						+ "...");
				FileOutputStream fs = new FileOutputStream(outputPath);

				// URIs in the output are relative on the server; we need to
				// make them absolute so they reference the server properly.
				String uriFix = "http://" + serverHost + ":" + serverPort
						+ "/ibmcognos/";

				for (int idx = 0; idx < data.length; idx++) {
					String fixed_hunk = replaceSubstring(data[idx], "../",
							uriFix);
					fs.write(fixed_hunk.getBytes());
				}

				fs.close();
				System.out.println("... done.");
			}
		} catch (AxisFault ex) {
			String ex_str = CognosBIException.convertToString(ex);
			System.out.println("SOAP exception!\n");
			System.out.println(ex_str);
		} catch (Exception ex) {
			System.out.println("Unhandled exception!");
			System.out.println("Message: \n" + ex.getMessage());
			System.out.println("Stack trace:");
			ex.printStackTrace();
		}
	}
}
