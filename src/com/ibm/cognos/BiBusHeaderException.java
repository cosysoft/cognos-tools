package com.ibm.cognos;

/** 
 Licensed Materials - Property of IBM

 IBM Cognos Products: DOCS

 (C) Copyright IBM Corp. 2005

 US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with
 IBM Corp.
 */
/**
 * BiBusHeaderException.java
 *
 * Copyright (C) 2005 Cognos ULC, an IBM Company. All rights reserved.
 * Cognos (R) is a trademark of Cognos ULC, (formerly Cognos Incorporated).
 */

import com.cognos.developer.schemas.bibus._3.CAMException;
import com.cognos.developer.schemas.bibus._3.ContentManagerService_PortType;
import com.cognos.developer.schemas.bibus._3.Message;
import com.cognos.developer.schemas.bibus._3.PromptInfo;

/**
 * Extract the interesting bits from a biBusHeader after a biBusHeader fault.
 */
public class BiBusHeaderException {
	private CAMException _exception = null;

	/**
	 * Create a BiBusHeaderException object.
	 * 
	 * @param cmService
	 *            ContentManagerService object in use during the last exception.
	 */
	public BiBusHeaderException(ContentManagerService_PortType cmService) {
		// Pull the CAM exception out of the biBusHeader.
		// BiBusHeader bibus_header =
		// ((Stub)cmService).getHeaderObject("", "biBusHeader");
		// BiBusHeader bibus = (BiBusHeader)bibus_header.getObjectValue();
		// _exception = bibus.getCAM().getException();
		// try {

		// SOAPHeaderElement temp =
		// ((Stub)cmService).getResponseHeader("http://developer.cognos.com/schemas/bibus/3/",
		// "biBusHeader");
		// BiBusHeader bibus_header = (BiBusHeader)temp.getValueAsType(new
		// QName("http://developer.cognos.com/schemas/bibus/3/",
		// "biBusHeader"));
		// ((Stub)cmService).setHeader("http://developer.cognos.com/schemas/bibus/3/",
		// "biBusHeader", bibus_header);
		// }
		// catch(Exception e) {

		// }
	}

	/**
	 * Get the Severity string from this BiBusHeaderException.
	 * 
	 * @return The Serverity string (a severityEnum in string form).
	 */
	public String getSeverity() {
		return new String(_exception.getSeverity().toString());
	}

	/**
	 * Get the errorCodeString from this BiBusHeaderException.
	 * 
	 * @return The errorCodeString.
	 */
	public String getErrorCode() {
		return new String(_exception.getErrorCodeString());
	}

	/**
	 * Get the details (messageString), if any, from this BiBusHeaderException.
	 * 
	 * @return An array of strings containing the detail messages.
	 */
	public String[] getDetails() {
		Message msg[] = _exception.getMessages();
		if (msg == null) {
			return new String[] { "null" };
		}
		String retval[] = new String[msg.length];
		for (int idx = 0; idx < msg.length; idx++) {
			retval[idx] = new String(msg[idx].getMessageString());
		}
		return retval;
	}

	/**
	 * Get the promptInfo (and useful captions/displayObjects inside) to
	 * facilitate prompting the user, if this is a recoverable exception.
	 * 
	 * @return The promptInfo object from the exception.
	 */
	public PromptInfo getPromptInfo() {
		return _exception.getPromptInfo();
	}

	/**
	 * Convert this BiBusHeaderException into a string for printing.
	 * 
	 * @return A string representation of the BiBusHeaderException.
	 */
	public String toString() {
		StringBuffer str = new StringBuffer();
		str.append("Severity  :").append(getSeverity()).append("\n");
		str.append("ErrorCode :").append(getErrorCode()).append("\n");
		str.append("Details   :\n");
		String details[] = getDetails();
		for (int i = 0; i < details.length; i++) {
			str.append("\t").append(details[i]).append("\n");
		}
		return str.toString();
	}

	/**
	 * Convert a biBusHeader exception into a BiBusHeaderException string.
	 * 
	 * This is the same as creating a BiBusHeaderException and calling its
	 * toString() method.
	 * 
	 * @param crn
	 *            The Service object that experienced the exception.
	 * @return A string representation.
	 */
	static public String convertToString(
			ContentManagerService_PortType cmService) {
		BiBusHeaderException exception = new BiBusHeaderException(cmService);
		return exception.toString();
	}
}
