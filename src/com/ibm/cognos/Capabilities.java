package com.ibm.cognos;

/** 
 Licensed Materials - Property of IBM

 IBM Cognos Products: DOCS

 (C) Copyright IBM Corp. 2005, 2008

 US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with
 IBM Corp.
 */
/**
 * Capabilities.java
 *
 * Copyright (C) 2008 Cognos ULC, an IBM Company. All rights reserved.
 * Cognos (R) is a trademark of Cognos ULC, (formerly Cognos Incorporated).
 *
 */

import java.rmi.RemoteException;

import org.apache.axis.AxisFault;
import org.apache.axis.client.Stub;
import org.apache.axis.message.SOAPHeaderElement;

import com.cognos.developer.schemas.bibus._3.AccessEnum;
import com.cognos.developer.schemas.bibus._3.BaseClass;
import com.cognos.developer.schemas.bibus._3.Permission;
import com.cognos.developer.schemas.bibus._3.Policy;
import com.cognos.developer.schemas.bibus._3.PolicyArrayProp;
import com.cognos.developer.schemas.bibus._3.PropEnum;
import com.cognos.developer.schemas.bibus._3.QueryOptions;
import com.cognos.developer.schemas.bibus._3.SearchPathMultipleObject;
import com.cognos.developer.schemas.bibus._3.SecuredFunction;
import com.cognos.developer.schemas.bibus._3.Sort;
import com.cognos.developer.schemas.bibus._3.UpdateOptions;
import com.cognos.developer.schemas.bibus._3.UserCapabilityEnum;
import com.cognos.developer.schemas.bibus._3.UserCapabilityEnumArrayProp;
import com.cognos.developer.schemas.bibus._3.UserCapabilityPermission;
import com.cognos.developer.schemas.bibus._3.UserCapabilityPolicy;
import com.cognos.developer.schemas.bibus._3.UserCapabilityPolicyArrayProp;
import com.cognos.developer.schemas.bibus._3._package;

public class Capabilities {
	/**
	 * Modifies capability privileges at both global and package level.
	 * 
	 * @param connection
	 *            Connection to Server.
	 * @param securityObject
	 *            Account, Group, or Role to be granted access
	 * @param targetPackage
	 *            package to be updated.
	 * @param secFuncPath
	 *            capability to grant access to, as search path.
	 * @param capToUpdate
	 *            capability to grant access to, as Enumeration set value.
	 * 
	 */
	public String updateSecuredFunction(CRNConnect connection,
			BaseClassWrapper securityObject, BaseClassWrapper targetPackage,
			String secFuncPath, UserCapabilityEnum capToUpdate) {

		// add account, group, or role to capability at global level
		if (!(addPolicyToCapability(connection, securityObject, secFuncPath))) {
			return "unable to grant selected account or role global "
					+ capToUpdate + " capability";
		}

		// add a policy to package for the specified security object and
		// capability,
		if (!(addPolicyToPackage(connection, securityObject, targetPackage,
				UserCapabilityEnum.canUseReportStudio))) {
			return "unable to grant " + securityObject + " " + capToUpdate
					+ " capability on package " + targetPackage + ".";
		}

		// Both succeeded
		return "Added " + capToUpdate + " to " + securityObject
				+ " for package " + targetPackage + ".";

	}

	/**
	 * Modifies capability privileges at both global and package level.
	 * 
	 * @param connection
	 *            Connection to Server.
	 * @param securityObject
	 *            Account, Group, or Role to be granted access
	 * @param capability
	 *            Capability to be updated (search path)
	 * 
	 */
	public boolean addPolicyToCapability(CRNConnect connection,
			BaseClassWrapper securityObject, String capability) {
		boolean result = false;

		BaseClass results[] = new BaseClass[] {};
		SecuredFunction securedFunction = null;
		Policy pol;

		// set up a policy with the required permissions and account
		Policy newPol = new Policy();

		Permission execPermission = new Permission();
		Permission travPermission = new Permission();

		execPermission.setName("execute");
		execPermission.setAccess(AccessEnum.grant);
		travPermission.setName("traverse");
		travPermission.setAccess(AccessEnum.grant);

		Permission[] polPermissions = { execPermission, travPermission };

		newPol.setPermissions(polPermissions);
		newPol.setSecurityObject(securityObject.getBaseClassObject());

		// Retrieve the capability, including policies
		try {
			results = connection.getCMService().query(
					new SearchPathMultipleObject(capability),
					new PropEnum[] { PropEnum.searchPath, PropEnum.policies,
							PropEnum.defaultName }, new Sort[] {},
					new QueryOptions());
			securedFunction = (SecuredFunction) results[0];
		} catch (java.rmi.RemoteException remoteEx) {
			remoteEx.printStackTrace();
			return false;
		}

		// create two new sets of policies - one same size, one plus one
		// if user already in place, use the first, replacing that policy
		// with the new one. If user not already in place, add the new
		// one at the end.
		int numPolicies = securedFunction.getPolicies().getValue().length;

		Policy[] tmpPoliciesSame = new Policy[numPolicies];
		Policy[] tmpPoliciesPlus = new Policy[numPolicies + 1];
		boolean matchFound = false;

		for (int i = 0; i < numPolicies; i++) {
			pol = securedFunction.getPolicies().getValue()[i];
			String polSecPath = pol.getSecurityObject().getSearchPath()
					.getValue();
			if (polSecPath.equals(securityObject.getBaseClassObject()
					.getSearchPath().getValue())) {
				tmpPoliciesSame[i] = newPol;
				matchFound = true;
			} else {
				tmpPoliciesSame[i] = pol;
				tmpPoliciesPlus[i] = pol;
			}
		}

		// Update the policies property on the capability
		PolicyArrayProp policyPropForUpdate = new PolicyArrayProp();

		if (matchFound) {
			// Update with tmpPoliciesSame
			policyPropForUpdate.setValue(tmpPoliciesSame);
		} else {
			// add new policy and update with tmpPoliciesPlus
			tmpPoliciesPlus[numPolicies] = newPol;
			policyPropForUpdate.setValue(tmpPoliciesPlus);
		}

		securedFunction.setPolicies(policyPropForUpdate);

		// Update the capability in the content store
		try {
			connection.getCMService().update(
					new BaseClass[] { securedFunction }, new UpdateOptions());

			result = true;
		} catch (java.rmi.RemoteException remoteEx) {
			remoteEx.printStackTrace();
			result = false;
		}

		return result;

	}

	/**
	 * Modifies a package to grant a capability for a user, group, or role.
	 * 
	 * @param connection
	 *            Connection to Server.
	 * @param targetSecObject
	 *            Account, Group, or Role to be granted access
	 * @param targetPackage
	 *            Package to be updated
	 * @param capToAdd
	 *            Capability to be updated (enumeration)
	 * 
	 */
	public boolean addPolicyToPackage(CRNConnect connection,
			BaseClassWrapper targetSecObject, BaseClassWrapper targetPackage,
			UserCapabilityEnum capToAdd) {
		boolean result = false;
		BaseClass results[] = new BaseClass[] {};
		_package myPackage = (_package) targetPackage.getBaseClassObject();
		UserCapabilityPolicy pol;

		// Set up a policy with the required permissions and security object
		UserCapabilityPolicy newUCPol = newUCPolicy(targetSecObject, capToAdd,
				AccessEnum.grant);

		// Create two new arrays of UserCapabilityPolicies - one same size, one
		// plus one
		// If there is already a policy for the account, group, or role is
		// already,
		// use the first, and include an updated policy for that security
		// object.
		// If a policy does not already exist, add the new one at the end.

		int numPolicies = myPackage.getUserCapabilityPolicies().getValue().length;

		UserCapabilityPolicy[] tmpPoliciesSame = new UserCapabilityPolicy[numPolicies];
		UserCapabilityPolicy[] tmpPoliciesPlus = new UserCapabilityPolicy[numPolicies + 1];
		boolean matchFound = false;

		for (int i = 0; i < numPolicies; i++) {
			pol = myPackage.getUserCapabilityPolicies().getValue()[i];
			String polSecPath = pol.getSecurityObject().getSearchPath()
					.getValue();

			// Check for match on account
			// If match on account, must edit permissions set for that account
			if (polSecPath.equals(targetSecObject.getBaseClassObject()
					.getSearchPath().getValue())) {
				tmpPoliciesSame[i] = updateUCPermissions(pol, capToAdd,
						AccessEnum.grant);
				matchFound = true;
			} else {
				tmpPoliciesSame[i] = pol;
				tmpPoliciesPlus[i] = pol;
			}
		}

		// Update the userCapabilities property of the package
		UserCapabilityPolicyArrayProp policyPropForUpdate = new UserCapabilityPolicyArrayProp();

		if (matchFound) {
			// Update with tmpPoliciesSame
			policyPropForUpdate.setValue(tmpPoliciesSame);
		} else {
			// add new policy and update with tmpPoliciesPlus
			tmpPoliciesPlus[numPolicies] = newUCPol;
			policyPropForUpdate.setValue(tmpPoliciesPlus);
		}
		myPackage.setUserCapabilityPolicies(policyPropForUpdate);

		// Update the package in the content store
		try {
			connection.getCMService().update(new BaseClass[] { myPackage },
					new UpdateOptions());

			result = true;
		} catch (java.rmi.RemoteException remoteEx) {
			remoteEx.printStackTrace();
			result = false;
		}

		return result;

	}

	/**
	 * Construct a UserCapabilityPolicy object
	 * 
	 * @param secObject
	 *            Account, Group, or Role
	 * @param capToSet
	 *            Capability (as enumeration)
	 * @param grantOrDeny
	 *            Access setting to apply
	 * 
	 */
	public UserCapabilityPolicy newUCPolicy(BaseClassWrapper secObject,
			UserCapabilityEnum capToSet, AccessEnum grantOrDeny) {

		UserCapabilityPolicy UCPolicy = new UserCapabilityPolicy();

		UserCapabilityPermission ucPermission = new UserCapabilityPermission();

		ucPermission.setAccess(grantOrDeny);
		ucPermission.setUserCapability(capToSet);

		UserCapabilityPermission[] ucPermissions = { ucPermission };

		UCPolicy.setPermissions(ucPermissions);
		UCPolicy.setSecurityObject(secObject.getBaseClassObject());

		return UCPolicy;

	}

	/**
	 * Update a UserCapabilityPolicy with permissions for specified capability
	 * 
	 * @param UserCapabilityPolicy
	 *            policy to update
	 * @param capToSet
	 *            Capability (as enumeration)
	 * @param grantOrDeny
	 *            Access setting to apply
	 * 
	 */
	public UserCapabilityPolicy updateUCPermissions(UserCapabilityPolicy pol,
			UserCapabilityEnum capToSet, AccessEnum grantOrDeny) {

		UserCapabilityPermission currentUCPermission;
		UserCapabilityPermission newUCPermission = new UserCapabilityPermission();

		newUCPermission.setAccess(grantOrDeny);
		newUCPermission.setUserCapability(capToSet);

		// Go through permissions array. If setting for capability exists,
		// replace it.
		// Otherwise, add

		int numPermissions = pol.getPermissions().length;

		UserCapabilityPermission[] tmpPermissionsSame = new UserCapabilityPermission[numPermissions];
		UserCapabilityPermission[] tmpPermissionsPlus = new UserCapabilityPermission[numPermissions + 1];
		boolean matchFound = false;

		for (int i = 0; i < numPermissions; i++) {
			currentUCPermission = pol.getPermissions()[i];

			if (currentUCPermission.getUserCapability() == capToSet) {
				tmpPermissionsSame[i] = newUCPermission;
				matchFound = true;
			} else {
				tmpPermissionsSame[i] = currentUCPermission;
				tmpPermissionsPlus[i] = currentUCPermission;
			}
		}

		if (matchFound) {
			// Update with tmpPermissionsSame
			pol.setPermissions(tmpPermissionsSame);
		} else {
			// add new permission and update with tmpPermissionsPlus
			tmpPermissionsPlus[numPermissions] = newUCPermission;
			pol.setPermissions(tmpPermissionsPlus);
		}

		return pol;

	}

	/**
	 * Check the effective user capabilities on a package for the current user.
	 * If a capability is present, the current user has this capability at both
	 * the global and package levels.
	 * 
	 * @param connection
	 *            Connection to Server.
	 * @param targetPackage
	 *            package to check (search path)
	 * @param capToCheck
	 *            Capability (as enumeration)
	 * 
	 */
	public boolean checkEffectiveCapability(CRNConnect connection,
			String targetPackage, UserCapabilityEnum capToCheck) {
		boolean result = false;

		try {
			if ((targetPackage == null) || (targetPackage.length() == 0)
					|| (targetPackage.compareTo("") == 0)) {
				return false;
			}

			// Query properties: we need effectiveUserCapabilities.
			PropEnum[] properties = { PropEnum.defaultName,
					PropEnum.searchPath, PropEnum.effectiveUserCapabilities };
			// Query options and sort; use the defaults.
			QueryOptions options = new QueryOptions();
			Sort[] sortBy = { new Sort() };

			try {
				BaseClass[] results = connection.getCMService().query(
						new SearchPathMultipleObject(targetPackage),
						properties, sortBy, options);

				_package mypackage = null;
				mypackage = (_package) results[0];

				// check for a specific capbility
				result = hasEffectiveCapability(
						mypackage.getEffectiveUserCapabilities(), capToCheck);

			}

			catch (AxisFault ex) {
				// Fault details can be found via ex.getFaultDetails(),
				// which returns an Element array.

				System.out.println("SOAP Fault:");
				System.out.println(ex.toString());
			}

			catch (RemoteException remoteEx) {
				SOAPHeaderElement theException = ((Stub) connection
						.getCMService()).getHeader("", "biBusHeader");

				// You can now use theException to find out more information
				// about the problem.

				System.out.println("The request threw an RMI exception:");
				System.out.println(remoteEx.getMessage());
				System.out.println("Stack trace:");
				remoteEx.printStackTrace();

			}
		} catch (Exception ex) {
		}

		return result;

	}

	/**
	 * Check a set of user capabilities for specified capability
	 * 
	 * @param setCapabilities
	 *            Array of user capabilities to check
	 * @param capToSet
	 *            Capability (as enumeration)
	 * 
	 */
	public boolean hasEffectiveCapability(
			UserCapabilityEnumArrayProp setCapabilities,
			UserCapabilityEnum capToCheck) {

		boolean result = false;

		UserCapabilityEnum[] capabilities = setCapabilities.getValue();

		// loop through capabilities, check for match
		for (int i = 0; i < capabilities.length; i++) {
			if (capabilities[i] == capToCheck)

			{
				return true;
			}
		}
		return false;

	}

}
