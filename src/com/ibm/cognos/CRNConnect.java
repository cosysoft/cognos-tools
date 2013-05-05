package com.ibm.cognos;

/** 
 Licensed Materials - Property of IBM

 IBM Cognos Products: DOCS

 (C) Copyright IBM Corp. 2005, 2010

 US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with
 IBM Corp.
 */
/**
 * CRNConnect.java
 *
 * Copyright (C) 2008 Cognos ULC, an IBM Company. All rights reserved.
 * Cognos (R) is a trademark of Cognos ULC, (formerly Cognos Incorporated).
 *
 * Description: This code sample demonstrates how to establish a connection to
 *              the IBM Cognos services
 */

import java.net.MalformedURLException;

import javax.swing.JOptionPane;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Stub;
import org.apache.axis.message.SOAPHeaderElement;

import com.cognos.developer.schemas.bibus._3.AgentService_PortType;
import com.cognos.developer.schemas.bibus._3.AgentService_ServiceLocator;
import com.cognos.developer.schemas.bibus._3.BatchReportService_PortType;
import com.cognos.developer.schemas.bibus._3.BatchReportService_ServiceLocator;
import com.cognos.developer.schemas.bibus._3.BiBusHeader;
import com.cognos.developer.schemas.bibus._3.ContentManagerService_PortType;
import com.cognos.developer.schemas.bibus._3.ContentManagerService_ServiceLocator;
import com.cognos.developer.schemas.bibus._3.DataIntegrationService_PortType;
import com.cognos.developer.schemas.bibus._3.DataIntegrationService_ServiceLocator;
import com.cognos.developer.schemas.bibus._3.DeliveryService_PortType;
import com.cognos.developer.schemas.bibus._3.DeliveryService_ServiceLocator;
import com.cognos.developer.schemas.bibus._3.DimensionManagementService_PortType;
import com.cognos.developer.schemas.bibus._3.DimensionManagementService_ServiceLocator;
import com.cognos.developer.schemas.bibus._3.Dispatcher_PortType;
import com.cognos.developer.schemas.bibus._3.Dispatcher_ServiceLocator;
import com.cognos.developer.schemas.bibus._3.EventManagementService_PortType;
import com.cognos.developer.schemas.bibus._3.EventManagementService_ServiceLocator;
import com.cognos.developer.schemas.bibus._3.JobService_PortType;
import com.cognos.developer.schemas.bibus._3.JobService_ServiceLocator;
import com.cognos.developer.schemas.bibus._3.MetadataService_PortType;
import com.cognos.developer.schemas.bibus._3.MetadataService_ServiceLocator;
import com.cognos.developer.schemas.bibus._3.MonitorService_PortType;
import com.cognos.developer.schemas.bibus._3.MonitorService_ServiceLocator;
import com.cognos.developer.schemas.bibus._3.PropEnum;
import com.cognos.developer.schemas.bibus._3.QueryOptions;
import com.cognos.developer.schemas.bibus._3.ReportService_PortType;
import com.cognos.developer.schemas.bibus._3.ReportService_ServiceLocator;
import com.cognos.developer.schemas.bibus._3.SearchPathMultipleObject;
import com.cognos.developer.schemas.bibus._3.Sort;
import com.cognos.developer.schemas.bibus._3.SystemService_PortType;
import com.cognos.developer.schemas.bibus._3.SystemService_ServiceLocator;

public class CRNConnect {
	// Create the objects that provide the connections to the services.

	// sn_dg_prm_smpl_connect_start_0
	private AgentService_ServiceLocator agentServiceLocator = null;
	// sn_dg_prm_smpl_connect_end_0
	private BatchReportService_ServiceLocator batchRepServiceLocator = null;
	private ContentManagerService_ServiceLocator cmServiceLocator = null;
	private DataIntegrationService_ServiceLocator dataIntServiceLocator = null;
	private DeliveryService_ServiceLocator deliveryServiceLocator = null;
	private EventManagementService_ServiceLocator eventMgmtServiceLocator = null;
	private JobService_ServiceLocator jobServiceLocator = null;
	private MonitorService_ServiceLocator monitorServiceLocator = null;
	private ReportService_ServiceLocator reportServiceLocator = null;
	private SystemService_ServiceLocator systemServiceLocator = null;
	private Dispatcher_ServiceLocator dispatcherServiceLocator = null;
	private DimensionManagementService_ServiceLocator dimensionMgmtServiceLocator = null;
	private MetadataService_ServiceLocator metadataServiceLocator = null;

	// There is an interface class for each service named
	// <servicename>_Port. The implementation class for each interface
	// is named <servicename>Stub. The stub class implements the methods
	// in the interface, and can be used to access the functionality provided
	// by the service. However, as it is a common practice, this sample
	// programs to the interfaces, instantiating instances of the
	// <servicename>_Port classes.

	// sn_dg_prm_smpl_connect_start_1
	private AgentService_PortType agentService = null;
	// sn_dg_prm_smpl_connect_end_1
	private BatchReportService_PortType batchRepService = null;
	private ContentManagerService_PortType cmService = null;
	private DataIntegrationService_PortType dataIntService = null;
	private DeliveryService_PortType deliveryService = null;
	private EventManagementService_PortType eventMgmtService = null;
	private JobService_PortType jobService = null;
	private MonitorService_PortType monitorService = null;
	private ReportService_PortType repService = null;
	private SystemService_PortType sysService = null;
	private Dispatcher_PortType dispatchService = null;
	private DimensionManagementService_PortType dimensionMgmtService = null;
	private MetadataService_PortType metadataService = null;

	// Set the location of the sample reports.
	private String curDir = System.getProperty("user.dir");
	// private String CRN_HOME =
	// curDir.substring(0,curDir.lastIndexOf("sdk")-1);
	// private String REPORT_PATH = CRN_HOME + "/webcontent/samples";

	// Create a variable that contains the default URL for Content Manager.
	// sn_dg_prm_smpl_connect_start_2
	public static String CM_URL = "http://biserver:9300/p2pd/servlet/dispatch";
	public static String GATEWAY_URL = "http://gateway2/cognos10/cgi-bin/cognos.cgi?b_action=xts.run&m=portal/launch.xts&ui.tool=CognosViewer&ui=h1h2h3h4&ui.action=run&run.outputFormat=&run.prompt=true";

	// sn_dg_prm_smpl_connect_end_2

	/**
	 * Use this method to connect to the server. The user will be prompted to
	 * confirm the Content Manager URL
	 * 
	 * @return A connection to the server
	 */
	public ContentManagerService_PortType connectToCognosServer() {
		BiBusHeader bibus = null;
		while (bibus == null) {
			// sn_dg_prm_smpl_connect_start_3
			// Create the service locators

			agentServiceLocator = new AgentService_ServiceLocator();
			// sn_dg_prm_smpl_connect_end_3
			batchRepServiceLocator = new BatchReportService_ServiceLocator();
			cmServiceLocator = new ContentManagerService_ServiceLocator();
			dataIntServiceLocator = new DataIntegrationService_ServiceLocator();
			deliveryServiceLocator = new DeliveryService_ServiceLocator();
			eventMgmtServiceLocator = new EventManagementService_ServiceLocator();
			jobServiceLocator = new JobService_ServiceLocator();
			monitorServiceLocator = new MonitorService_ServiceLocator();
			reportServiceLocator = new ReportService_ServiceLocator();
			systemServiceLocator = new SystemService_ServiceLocator();
			dispatcherServiceLocator = new Dispatcher_ServiceLocator();
			dimensionMgmtServiceLocator = new DimensionManagementService_ServiceLocator();
			metadataServiceLocator = new MetadataService_ServiceLocator();

			try {
				// sn_dg_prm_smpl_connect_start_4
				java.net.URL serverURL = new java.net.URL(CM_URL);

				// acquire references to the services

				agentService = agentServiceLocator.getagentService(serverURL);
				// sn_dg_prm_smpl_connect_end_4
				batchRepService = batchRepServiceLocator
						.getbatchReportService(serverURL);
				cmService = cmServiceLocator
						.getcontentManagerService(serverURL);
				dataIntService = dataIntServiceLocator
						.getdataIntegrationService(serverURL);
				deliveryService = deliveryServiceLocator
						.getdeliveryService(serverURL);
				eventMgmtService = eventMgmtServiceLocator
						.geteventManagementService(serverURL);
				jobService = jobServiceLocator.getjobService(serverURL);
				monitorService = monitorServiceLocator
						.getmonitorService(serverURL);
				repService = reportServiceLocator.getreportService(serverURL);
				sysService = systemServiceLocator.getsystemService(serverURL);
				dispatchService = dispatcherServiceLocator
						.getdispatcher(serverURL);
				dimensionMgmtService = dimensionMgmtServiceLocator
						.getdimensionManagementService(serverURL);
				metadataService = metadataServiceLocator
						.getmetadataService(serverURL);

			} // ... catch expected exceptions after this point
			catch (MalformedURLException e) {
				System.out.println("Malformed URL:\n" + e.getMessage());
				return null;
			} catch (ServiceException e) {
				System.out.println("Service Exception:\n" + e.getMessage());
				return null;
			}

			try {
				cmService.query(new SearchPathMultipleObject("/"),
						new PropEnum[] {}, new Sort[] {}, new QueryOptions());
			} catch (java.rmi.RemoteException remoteEx) {
				System.out.println("");
				// If authentication is required, this will generate an
				// exception
				// At this point, this exception can safely be ignored
			} catch (java.lang.NullPointerException nullEx) {
				JOptionPane.showMessageDialog(null,
						"Unable to connect at the URL: " + CM_URL
								+ ". Please make sure the service is running.");
				return null;
			}

			// Retrieve the biBusHeader SOAP:Header that contains
			// the logon information.
			SOAPHeaderElement x = ((Stub) cmService).getResponseHeader(
					"http://developer.cognos.com/schemas/bibus/3/",
					"biBusHeader");
			bibus = BIBusHeaderHelper.getHeaderObject(x, true, "");

			if (bibus != null) {
				((Stub) cmService).setHeader(
						"http://developer.cognos.com/schemas/bibus/3/",
						"biBusHeader", bibus);
				return cmService;
			}

			JOptionPane.showMessageDialog(null, "Connect Failed. Try again.");
		}
		return null;
	}

	/**
	 * Use this method to connect to the server, bypassing any prompts.
	 * 
	 * @param CMURL
	 *            The URL for the server
	 * @return A connection to the server
	 */
	public ContentManagerService_PortType connectToCognosServer(String CMURL) {
		CM_URL = CMURL;

		// Create the service locators

		agentServiceLocator = new AgentService_ServiceLocator();
		batchRepServiceLocator = new BatchReportService_ServiceLocator();
		cmServiceLocator = new ContentManagerService_ServiceLocator();
		dataIntServiceLocator = new DataIntegrationService_ServiceLocator();
		deliveryServiceLocator = new DeliveryService_ServiceLocator();
		eventMgmtServiceLocator = new EventManagementService_ServiceLocator();
		jobServiceLocator = new JobService_ServiceLocator();
		monitorServiceLocator = new MonitorService_ServiceLocator();
		reportServiceLocator = new ReportService_ServiceLocator();
		systemServiceLocator = new SystemService_ServiceLocator();
		dispatcherServiceLocator = new Dispatcher_ServiceLocator();
		dimensionMgmtServiceLocator = new DimensionManagementService_ServiceLocator();
		metadataServiceLocator = new MetadataService_ServiceLocator();

		try {
			java.net.URL serverURL = new java.net.URL(CMURL);

			// acquire references to services
			//

			agentService = agentServiceLocator.getagentService(serverURL);
			batchRepService = batchRepServiceLocator
					.getbatchReportService(serverURL);
			cmService = cmServiceLocator.getcontentManagerService(serverURL);
			dataIntService = dataIntServiceLocator
					.getdataIntegrationService(serverURL);
			deliveryService = deliveryServiceLocator
					.getdeliveryService(serverURL);
			eventMgmtService = eventMgmtServiceLocator
					.geteventManagementService(serverURL);
			jobService = jobServiceLocator.getjobService(serverURL);
			monitorService = monitorServiceLocator.getmonitorService(serverURL);
			repService = reportServiceLocator.getreportService(serverURL);
			sysService = systemServiceLocator.getsystemService(serverURL);
			dispatchService = dispatcherServiceLocator.getdispatcher(serverURL);
			dimensionMgmtService = dimensionMgmtServiceLocator
					.getdimensionManagementService(serverURL);
			metadataService = metadataServiceLocator
					.getmetadataService(serverURL);

			return cmService;
		}
		// handle uncaught exceptions
		catch (MalformedURLException e) {
			System.out.println("Malformed URL:\n" + e.getMessage());
			return null;
		} catch (ServiceException e) {
			System.out.println("Service Exception:\n" + e.getMessage());
			return null;
		}
	}

	public ContentManagerService_PortType connectionChange(String endPoint) {
		try {
			java.net.URL endPointURL = new java.net.URL(endPoint);

			agentService = agentServiceLocator.getagentService(endPointURL);
			batchRepService = batchRepServiceLocator
					.getbatchReportService(endPointURL);
			cmService = cmServiceLocator.getcontentManagerService(endPointURL);
			dataIntService = dataIntServiceLocator
					.getdataIntegrationService(endPointURL);
			deliveryService = deliveryServiceLocator
					.getdeliveryService(endPointURL);
			eventMgmtService = eventMgmtServiceLocator
					.geteventManagementService(endPointURL);
			jobService = jobServiceLocator.getjobService(endPointURL);
			monitorService = monitorServiceLocator
					.getmonitorService(endPointURL);
			repService = reportServiceLocator.getreportService(endPointURL);
			sysService = systemServiceLocator.getsystemService(endPointURL);
			dispatchService = dispatcherServiceLocator
					.getdispatcher(endPointURL);
			dimensionMgmtService = dimensionMgmtServiceLocator
					.getdimensionManagementService(endPointURL);
			metadataService = metadataServiceLocator
					.getmetadataService(endPointURL);

			return cmService;
		} catch (MalformedURLException eMalformed) {
			System.out.println(eMalformed.getMessage());
			return null;
		} catch (ServiceException eService) {
			System.out.println(eService.getMessage());
			return null;
		}
	}

	// public String getDefaultSavePath()
	// {
	// return REPORT_PATH;
	// }
	//
	// public void setDefaultSavePath(String newReportPath)
	// {
	// REPORT_PATH = newReportPath;
	// }

	// handle service requests that do not specify new conversation for
	// backwards compatibility
	public AgentService_PortType getAgentService() {

		return getAgentService(false, "");

	}

	public AgentService_PortType getAgentService(boolean isNewConversation,
			String RSGroup) {
		BiBusHeader bibus = null;
		bibus = BIBusHeaderHelper.getHeaderObject(((Stub) agentService)
				.getResponseHeader(
						"http://developer.cognos.com/schemas/bibus/3/",
						"biBusHeader"), isNewConversation, RSGroup);

		if (bibus == null) {
			BiBusHeader CMbibus = null;
			CMbibus = BIBusHeaderHelper.getHeaderObject(((Stub) cmService)
					.getResponseHeader(
							"http://developer.cognos.com/schemas/bibus/3/",
							"biBusHeader"), true, RSGroup);

			((Stub) agentService).clearHeaders();
			((Stub) agentService).setHeader(
					"http://developer.cognos.com/schemas/bibus/3/",
					"biBusHeader", CMbibus);
		} else {
			((Stub) agentService).clearHeaders();
			((Stub) agentService).setHeader(
					"http://developer.cognos.com/schemas/bibus/3/",
					"biBusHeader", bibus);

		}
		return agentService;
	}

	// handle service requests that do not specify new conversation for
	// backwards compatibility
	public BatchReportService_PortType getBatchRepService() {

		return getBatchRepService(false, "");

	}

	public BatchReportService_PortType getBatchRepService(
			boolean isNewConversation, String RSGroup) {
		BiBusHeader bibus = null;
		bibus = BIBusHeaderHelper.getHeaderObject(((Stub) batchRepService)
				.getResponseHeader(
						"http://developer.cognos.com/schemas/bibus/3/",
						"biBusHeader"), isNewConversation, RSGroup);

		if (bibus == null) {
			BiBusHeader CMbibus = null;
			CMbibus = BIBusHeaderHelper.getHeaderObject(((Stub) cmService)
					.getResponseHeader(
							"http://developer.cognos.com/schemas/bibus/3/",
							"biBusHeader"), true, RSGroup);

			((Stub) batchRepService).clearHeaders();
			((Stub) batchRepService).setHeader(
					"http://developer.cognos.com/schemas/bibus/3/",
					"biBusHeader", CMbibus);
		} else {
			((Stub) batchRepService).clearHeaders();
			((Stub) batchRepService).setHeader(
					"http://developer.cognos.com/schemas/bibus/3/",
					"biBusHeader", bibus);

		}
		return batchRepService;
	}

	// handle service requests that do not specify new conversation for
	// backwards compatibility
	public ContentManagerService_PortType getCMService() {

		return getCMService(false, "");

	}

	public ContentManagerService_PortType getCMService(
			boolean isNewConversation, String RSGroup) {
		BiBusHeader bibus = null;
		bibus = BIBusHeaderHelper.getHeaderObject(((Stub) cmService)
				.getResponseHeader(
						"http://developer.cognos.com/schemas/bibus/3/",
						"biBusHeader"), isNewConversation, RSGroup);

		if (!(bibus == null)) {
			((Stub) cmService).clearHeaders();
			((Stub) cmService).setHeader(
					"http://developer.cognos.com/schemas/bibus/3/",
					"biBusHeader", bibus);
			((Stub) cmService).setTimeout(10000);

		}

		return cmService;
	}

	// handle service requests that do not specify new conversation for
	// backwards compatibility
	public DataIntegrationService_PortType getDataIntService() {

		return getDataIntService(false, "");

	}

	public DataIntegrationService_PortType getDataIntService(
			boolean isNewConversation, String RSGroup) {
		BiBusHeader bibus = null;
		bibus = BIBusHeaderHelper.getHeaderObject(((Stub) dataIntService)
				.getResponseHeader(
						"http://developer.cognos.com/schemas/bibus/3/",
						"biBusHeader"), isNewConversation, RSGroup);

		if (bibus == null) {
			BiBusHeader CMbibus = null;
			CMbibus = BIBusHeaderHelper.getHeaderObject(((Stub) cmService)
					.getResponseHeader(
							"http://developer.cognos.com/schemas/bibus/3/",
							"biBusHeader"), true, RSGroup);

			((Stub) dataIntService).clearHeaders();
			((Stub) dataIntService).setHeader(
					"http://developer.cognos.com/schemas/bibus/3/",
					"biBusHeader", CMbibus);
		} else {
			((Stub) dataIntService).clearHeaders();
			((Stub) dataIntService).setHeader(
					"http://developer.cognos.com/schemas/bibus/3/",
					"biBusHeader", bibus);

		}
		return dataIntService;
	}

	// handle service requests that do not specify new conversation for
	// backwards compatibility
	public DeliveryService_PortType getDeliveryService() {

		return getDeliveryService(false, "");

	}

	public DeliveryService_PortType getDeliveryService(
			boolean isNewConversation, String RSGroup) {
		BiBusHeader bibus = null;
		bibus = BIBusHeaderHelper.getHeaderObject(((Stub) deliveryService)
				.getResponseHeader(
						"http://developer.cognos.com/schemas/bibus/3/",
						"biBusHeader"), isNewConversation, RSGroup);

		if (bibus == null) {
			BiBusHeader CMbibus = null;
			CMbibus = BIBusHeaderHelper.getHeaderObject(((Stub) cmService)
					.getResponseHeader(
							"http://developer.cognos.com/schemas/bibus/3/",
							"biBusHeader"), true, RSGroup);

			((Stub) deliveryService).clearHeaders();
			((Stub) deliveryService).setHeader(
					"http://developer.cognos.com/schemas/bibus/3/",
					"biBusHeader", CMbibus);
		} else {
			((Stub) deliveryService).clearHeaders();
			((Stub) deliveryService).setHeader(
					"http://developer.cognos.com/schemas/bibus/3/",
					"biBusHeader", bibus);

		}
		return deliveryService;
	}

	// handle service requests that do not specify new conversation for
	// backwards compatibility
	public DimensionManagementService_PortType getDimensionManagementService() {

		return getDimensionManagementService(false, "");

	}

	public DimensionManagementService_PortType getDimensionManagementService(
			boolean isNewConversation, String RSGroup) {
		BiBusHeader bibus = null;
		bibus = BIBusHeaderHelper.getHeaderObject(((Stub) dimensionMgmtService)
				.getResponseHeader(
						"http://developer.cognos.com/schemas/bibus/3/",
						"biBusHeader"), isNewConversation, RSGroup);

		if (bibus == null) {
			BiBusHeader CMbibus = null;
			CMbibus = BIBusHeaderHelper.getHeaderObject(((Stub) cmService)
					.getResponseHeader(
							"http://developer.cognos.com/schemas/bibus/3/",
							"biBusHeader"), true, RSGroup);

			((Stub) dimensionMgmtService).clearHeaders();
			((Stub) dimensionMgmtService).setHeader(
					"http://developer.cognos.com/schemas/bibus/3/",
					"biBusHeader", CMbibus);
		} else {
			((Stub) dimensionMgmtService).clearHeaders();
			((Stub) dimensionMgmtService).setHeader(
					"http://developer.cognos.com/schemas/bibus/3/",
					"biBusHeader", bibus);

		}
		return dimensionMgmtService;
	}

	// handle service requests that do not specify new conversation for
	// backwards compatibility
	public EventManagementService_PortType getEventMgmtService() {

		return getEventMgmtService(false, "");

	}

	public EventManagementService_PortType getEventMgmtService(
			boolean isNewConversation, String RSGroup) {
		BiBusHeader bibus = null;
		bibus = BIBusHeaderHelper.getHeaderObject(((Stub) eventMgmtService)
				.getResponseHeader(
						"http://developer.cognos.com/schemas/bibus/3/",
						"biBusHeader"), isNewConversation, RSGroup);

		if (bibus == null) {
			BiBusHeader CMbibus = null;
			CMbibus = BIBusHeaderHelper.getHeaderObject(((Stub) cmService)
					.getResponseHeader(
							"http://developer.cognos.com/schemas/bibus/3/",
							"biBusHeader"), true, RSGroup);

			((Stub) eventMgmtService).clearHeaders();
			((Stub) eventMgmtService).setHeader(
					"http://developer.cognos.com/schemas/bibus/3/",
					"biBusHeader", CMbibus);
		} else {
			((Stub) eventMgmtService).clearHeaders();
			((Stub) eventMgmtService).setHeader(
					"http://developer.cognos.com/schemas/bibus/3/",
					"biBusHeader", bibus);

		}
		return eventMgmtService;
	}

	// handle service requests that do not specify new conversation for
	// backwards compatibility
	public JobService_PortType getJobService() {

		return getJobService(false, "");

	}

	public JobService_PortType getJobService(boolean isNewConversation,
			String RSGroup) {
		BiBusHeader bibus = null;
		bibus = BIBusHeaderHelper.getHeaderObject(((Stub) jobService)
				.getResponseHeader(
						"http://developer.cognos.com/schemas/bibus/3/",
						"biBusHeader"), isNewConversation, RSGroup);

		if (bibus == null) {
			BiBusHeader CMbibus = null;
			CMbibus = BIBusHeaderHelper.getHeaderObject(((Stub) cmService)
					.getResponseHeader(
							"http://developer.cognos.com/schemas/bibus/3/",
							"biBusHeader"), true, RSGroup);

			((Stub) jobService).clearHeaders();
			((Stub) jobService).setHeader(
					"http://developer.cognos.com/schemas/bibus/3/",
					"biBusHeader", CMbibus);
		} else {
			((Stub) jobService).clearHeaders();
			((Stub) jobService).setHeader(
					"http://developer.cognos.com/schemas/bibus/3/",
					"biBusHeader", bibus);

		}
		return jobService;
	}

	// handle service requests that do not specify new conversation for
	// backwards compatibility
	public MonitorService_PortType getMonitorService() {

		return getMonitorService(false, "");

	}

	// handle service requests that do not specify new conversation for
	// backwards compatibility
	public MetadataService_PortType getMetadataService() {

		return getMetadataService(false, "");

	}

	public MetadataService_PortType getMetadataService(
			boolean isNewConversation, String RSGroup) {
		BiBusHeader bibus = null;
		bibus = BIBusHeaderHelper.getHeaderObject(((Stub) metadataService)
				.getResponseHeader(
						"http://developer.cognos.com/schemas/bibus/3/",
						"biBusHeader"), isNewConversation, RSGroup);

		if (bibus == null) {
			BiBusHeader CMbibus = null;
			CMbibus = BIBusHeaderHelper.getHeaderObject(((Stub) cmService)
					.getResponseHeader(
							"http://developer.cognos.com/schemas/bibus/3/",
							"biBusHeader"), true, RSGroup);

			((Stub) metadataService).clearHeaders();
			((Stub) metadataService).setHeader(
					"http://developer.cognos.com/schemas/bibus/3/",
					"biBusHeader", CMbibus);
		} else {
			((Stub) metadataService).clearHeaders();
			((Stub) metadataService).setHeader(
					"http://developer.cognos.com/schemas/bibus/3/",
					"biBusHeader", bibus);

		}
		return metadataService;
	}

	public MonitorService_PortType getMonitorService(boolean isNewConversation,
			String RSGroup) {
		BiBusHeader bibus = null;
		bibus = BIBusHeaderHelper.getHeaderObject(((Stub) monitorService)
				.getResponseHeader(
						"http://developer.cognos.com/schemas/bibus/3/",
						"biBusHeader"), isNewConversation, RSGroup);

		if (bibus == null) {
			BiBusHeader CMbibus = null;
			CMbibus = BIBusHeaderHelper.getHeaderObject(((Stub) cmService)
					.getResponseHeader(
							"http://developer.cognos.com/schemas/bibus/3/",
							"biBusHeader"), true, RSGroup);

			((Stub) monitorService).clearHeaders();
			((Stub) monitorService).setHeader(
					"http://developer.cognos.com/schemas/bibus/3/",
					"biBusHeader", CMbibus);
		} else {
			((Stub) monitorService).clearHeaders();
			((Stub) monitorService).setHeader(
					"http://developer.cognos.com/schemas/bibus/3/",
					"biBusHeader", bibus);

		}
		return monitorService;
	}

	// handle service requests that do not specify new conversation for
	// backwards compatibility
	public ReportService_PortType getReportService() {

		return getReportService(false, "");

	}

	// sn_dg_sdk_mng_svc_hdrs_start_1
	public ReportService_PortType getReportService(boolean isNewConversation,
			String RSGroup) {

		BiBusHeader bibus = null;
		bibus = BIBusHeaderHelper.getHeaderObject(((Stub) repService)
				.getResponseHeader(
						"http://developer.cognos.com/schemas/bibus/3/",
						"biBusHeader"), isNewConversation, RSGroup);

		if (bibus == null) {
			BiBusHeader CMbibus = null;
			CMbibus = BIBusHeaderHelper.getHeaderObject(((Stub) cmService)
					.getResponseHeader(
							"http://developer.cognos.com/schemas/bibus/3/",
							"biBusHeader"), true, RSGroup);

			((Stub) repService).clearHeaders();
			((Stub) repService).setHeader(
					"http://developer.cognos.com/schemas/bibus/3/",
					"biBusHeader", CMbibus);
		} else {
			((Stub) repService).clearHeaders();
			((Stub) repService).setHeader(
					"http://developer.cognos.com/schemas/bibus/3/",
					"biBusHeader", bibus);

		}

		return repService;
	}

	// handle service requests that do not specify new conversation for
	// backwards compatibility
	public SystemService_PortType getSystemService() {

		return getSystemService(false, "");

	}

	// sn_dg_sdk_mng_svc_hdrs_end_1

	public SystemService_PortType getSystemService(boolean isNewConversation,
			String RSGroup) {
		BiBusHeader bibus = null;
		bibus = BIBusHeaderHelper.getHeaderObject(((Stub) sysService)
				.getResponseHeader(
						"http://developer.cognos.com/schemas/bibus/3/",
						"biBusHeader"), isNewConversation, RSGroup);

		if (bibus == null) {
			BiBusHeader CMbibus = null;
			CMbibus = BIBusHeaderHelper.getHeaderObject(((Stub) cmService)
					.getResponseHeader(
							"http://developer.cognos.com/schemas/bibus/3/",
							"biBusHeader"), true, RSGroup);

			((Stub) sysService).clearHeaders();
			((Stub) sysService).setHeader(
					"http://developer.cognos.com/schemas/bibus/3/",
					"biBusHeader", CMbibus);
		} else {
			((Stub) sysService).clearHeaders();
			((Stub) sysService).setHeader(
					"http://developer.cognos.com/schemas/bibus/3/",
					"biBusHeader", bibus);

		}
		return sysService;
	}

	// handle service requests that do not specify new conversation for
	// backwards compatibility
	public Dispatcher_PortType getDispatcherService() {

		return getDispatcherService(false, "");

	}

	public Dispatcher_PortType getDispatcherService(boolean isNewConversation,
			String RSGroup) {
		BiBusHeader bibus = null;
		bibus = BIBusHeaderHelper.getHeaderObject(((Stub) dispatchService)
				.getResponseHeader(
						"http://developer.cognos.com/schemas/bibus/3/",
						"biBusHeader"), isNewConversation, RSGroup);

		if (bibus == null) {
			BiBusHeader CMbibus = null;
			CMbibus = BIBusHeaderHelper.getHeaderObject(((Stub) cmService)
					.getResponseHeader(
							"http://developer.cognos.com/schemas/bibus/3/",
							"biBusHeader"), true, RSGroup);

			((Stub) dispatchService).clearHeaders();
			((Stub) dispatchService).setHeader(
					"http://developer.cognos.com/schemas/bibus/3/",
					"biBusHeader", CMbibus);
		} else {
			((Stub) dispatchService).clearHeaders();
			((Stub) dispatchService).setHeader(
					"http://developer.cognos.com/schemas/bibus/3/",
					"biBusHeader", bibus);

		}
		return dispatchService;
	}

	public String getPassPort() {
		BiBusHeader bibus = null;
		String passport = null;
		try {
			SOAPHeaderElement x = ((Stub) cmService).getResponseHeader(
					"http://developer.cognos.com/schemas/bibus/3/",
					"biBusHeader");
			bibus = BIBusHeaderHelper.getHeaderObject(x, true, "");
			passport = bibus.getCAM().getCAMPassport().getId().toString();
			System.out.println("passport:" + passport);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return passport;
	}

}
