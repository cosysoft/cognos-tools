package com.ibm.cognos;

/** 
 Licensed Materials - Property of IBM

 IBM Cognos Products: DOCS

 (C) Copyright IBM Corp. 2005, 2008

 US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with
 IBM Corp.
 */
/**
 * GroupsAndRoles.java
 *
 * Copyright (C) 2008 Cognos ULC, an IBM Company. All rights reserved.
 * Cognos (R) is a trademark of Cognos ULC, (formerly Cognos Incorporated).
 *
 * Description: This file contains methods for handling groups and roles
 *
 */

import com.cognos.developer.schemas.bibus._3.BaseClass;
import com.cognos.developer.schemas.bibus._3.BaseClassArrayProp;
import com.cognos.developer.schemas.bibus._3.Group;
import com.cognos.developer.schemas.bibus._3.Locale;
import com.cognos.developer.schemas.bibus._3.MultilingualToken;
import com.cognos.developer.schemas.bibus._3.MultilingualTokenProp;
import com.cognos.developer.schemas.bibus._3.PropEnum;
import com.cognos.developer.schemas.bibus._3.QueryOptions;
import com.cognos.developer.schemas.bibus._3.Role;
import com.cognos.developer.schemas.bibus._3.SearchPathMultipleObject;
import com.cognos.developer.schemas.bibus._3.Sort;
import com.cognos.developer.schemas.bibus._3.StringProp;

public class GroupsAndRoles {

	CSHandlers csHandler = new CSHandlers();

	/**
	 * Create a new Cognos group.
	 * 
	 * @param connection
	 *            Connection to Server
	 * @param groupName
	 *            Name of new group
	 * @param selectedNamespace
	 *            Namespace in which new group is to be created
	 * @return A string containing successful status information.
	 * 
	 */
	public String createGroup(CRNConnect connection, String groupName,
			String selectedNamespace) throws java.rmi.RemoteException {
		// Create a new group.
		BaseClass group = new Group();
		String path = selectedNamespace;

		// Note that the defaultName of the new object will be set to
		// the first (in this case, only) name token.
		MultilingualToken[] names = new MultilingualToken[1];
		names[0] = new MultilingualToken();
		Locale[] locales = csHandler.getConfiguration(connection);
		names[0].setLocale(locales[0].getLocale());
		names[0].setValue(groupName);
		group.setName(new MultilingualTokenProp());
		group.getName().setValue(names);

		// Add the new group.
		csHandler.addObjectToCS(connection, group, path);

		return ("Successfully created group " + groupName + " in the Cognos namespace");
	}

	/**
	 * Create a new Cognos role.
	 * 
	 * @param connection
	 *            Connection to Server
	 * @return A string containing successful status information.
	 * 
	 */
	public String createRole(CRNConnect connection, String roleName,
			String nameSpace) throws java.rmi.RemoteException {
		// Create a new role.
		BaseClass role = new Role();
		String path = nameSpace;

		// Note that the defaultName of the new object will be set to
		// the first (in this case, only) name token.
		MultilingualToken[] names = new MultilingualToken[1];
		names[0] = new MultilingualToken();
		Locale[] locales = csHandler.getConfiguration(connection);
		names[0].setLocale(locales[0].getLocale());
		names[0].setValue(roleName);
		role.setName(new MultilingualTokenProp());
		role.getName().setValue(names);

		// Add the new role.
		csHandler.addObjectToCS(connection, role, path);

		return ("Successfully created role " + roleName + " in the Cognos namespace");
	}

	/**
	 * Add the specified user to the specified group or role.
	 * 
	 * @param connection
	 *            Connection to Server
	 * @param pathOfUser
	 *            Search path to user to be added
	 * @param pathOfGroup
	 *            Search path to the group
	 * 
	 * @return A string containing successful status information.
	 * 
	 */
	public String addUserToGroup(CRNConnect connection, String pathOfUser,
			String pathOfGroup) throws java.rmi.RemoteException {
		// get the source objects
		BaseClass user[] = csHandler.queryObjectInCS(connection, pathOfUser);

		try {
			addToGroup(connection, pathOfGroup, user);
		} catch (java.rmi.RemoteException remoteEx) {
			remoteEx.printStackTrace();
			return "Exception caught: " + remoteEx.getMessage();
		}

		return "Successfully added " + pathOfUser + " to " + pathOfGroup;
	}

	/**
	 * Add the specified user to the specified group or role.
	 * 
	 * @param connection
	 *            Connection to Server
	 * @param pathOfUser
	 *            Search path to user to be added.
	 * @param pathOfRole
	 *            Search path to the role.
	 * 
	 * @return A string containing successful status information.
	 * 
	 */
	public String addUserToRole(CRNConnect connection, String pathOfUser,
			String pathOfRole) throws java.rmi.RemoteException {
		// get the source objects
		BaseClass user[] = csHandler.queryObjectInCS(connection, pathOfUser);

		try {
			addToRole(connection, pathOfRole, user);
		} catch (java.rmi.RemoteException remoteEx) {
			remoteEx.printStackTrace();
			return "Exception caught: " + remoteEx.getMessage();
		}

		return ("Successfully added " + pathOfUser + " to " + pathOfRole);
	}

	/**
	 * Add the specified member to the specified group.
	 * 
	 * @param connection
	 *            Connection to Server
	 * @param pathOfGroup
	 *            Search path to the group.
	 * @param member
	 *            User or group to be added.
	 * 
	 */
	public void addToGroup(CRNConnect connection, String pathOfGroup,
			BaseClass[] member) throws java.rmi.RemoteException {
		// Get the current group membership.
		PropEnum[] props = { PropEnum.defaultName, PropEnum.searchPath,
				PropEnum.members };

		Group group = (Group) csHandler.queryObjectInCS(connection,
				pathOfGroup, props)[0];

		if (group.getMembers().getValue() == null) {
			group.setMembers(new BaseClassArrayProp());
			group.getMembers().setValue(member);
			csHandler.updateObjectInCS(connection, new BaseClass[] { group });
		} else {
			// Preserve all the existing members.
			BaseClass[] newMembers = new BaseClass[group.getMembers()
					.getValue().length + 1];
			int index = 0;
			BaseClass obj = null;
			for (int i = 0; i < group.getMembers().getValue().length; i++) {
				obj = group.getMembers().getValue()[i];

				// BaseClass[] memberProps =
				csHandler.queryObjectInCS(connection, obj.getSearchPath()
						.getValue());

				newMembers[index] = obj;
				index++;
			}

			newMembers[index] = member[0];

			group.setMembers(new BaseClassArrayProp());
			group.getMembers().setValue(newMembers);

			// Update the membership.
			csHandler.updateObjectInCS(connection, new BaseClass[] { group });
		}
	}

	/**
	 * Add the specified member to the specified role.
	 * 
	 * @param connection
	 *            Connection to Server
	 * @param pathOfRole
	 *            Search path to the role.
	 * @param member
	 *            User, group or role to be added.
	 * 
	 */
	public void addToRole(CRNConnect connection, String pathOfRole,
			BaseClass[] member) throws java.rmi.RemoteException {
		// Get the current role membership.
		PropEnum[] props = { PropEnum.defaultName, PropEnum.searchPath,
				PropEnum.members };

		Role role = (Role) csHandler.queryObjectInCS(connection, pathOfRole,
				props)[0];

		if (role.getMembers().getValue() == null) {
			role.setMembers(new BaseClassArrayProp());
			role.getMembers().setValue(member);
			csHandler.updateObjectInCS(connection, new BaseClass[] { role });
		} else {
			// Preserve all the existing members.
			BaseClass[] newMembers = new BaseClass[role.getMembers().getValue().length + 1];
			int index = 0;
			BaseClass obj = null;
			for (int i = 0; i < role.getMembers().getValue().length; i++) {
				obj = role.getMembers().getValue()[i];

				csHandler.queryObjectInCS(connection, obj.getSearchPath()
						.getValue());

				newMembers[index] = obj;
				index++;
			}

			newMembers[index] = member[0];

			role.setMembers(new BaseClassArrayProp());
			role.getMembers().setValue(newMembers);

			// Update the membership.
			csHandler.updateObjectInCS(connection, new BaseClass[] { role });
		}
	}

	/**
	 * Delete a user from a group.
	 * 
	 * @param connection
	 *            Connection to Server
	 * @param groupSearchPath
	 *            Search path to group.
	 * @param userSearchPath
	 *            Search path to the user.
	 * 
	 * @return A string containing successful status information.
	 * 
	 */
	public String deleteUserFromGroup(CRNConnect connection,
			String groupSearchPath, String userSearchPath)
			throws java.rmi.RemoteException {
		removeFromGroup(connection, groupSearchPath,
				(csHandler.queryObjectInCS(connection, userSearchPath))[0]);

		return ("Successfully deleted " + userSearchPath + " from " + groupSearchPath);
	}

	/**
	 * Delete a user from a role.
	 * 
	 * @param connection
	 *            Connection to Server
	 * @param roleSearchPath
	 *            Search path to role.
	 * @param memberSearchPath
	 *            Search path to the user.
	 * 
	 * @return A string containing successful status information.
	 * 
	 */
	public String deleteUserFromRole(CRNConnect connection,
			String roleSearchPath, String memberSearchPath)
			throws java.rmi.RemoteException {

		removeFromRole(connection, roleSearchPath,
				(csHandler.queryObjectInCS(connection, memberSearchPath))[0]);

		return ("Successfully deleted " + memberSearchPath + " from " + roleSearchPath);
	}

	/**
	 * Remove the specified member from the specified group.
	 * 
	 * @param connection
	 *            Connection to Server
	 * @param pathOfGroup
	 *            Search path to the group.
	 * @param member
	 *            An account or group.
	 * 
	 */
	public void removeFromGroup(CRNConnect connection, String pathOfGroup,
			BaseClass member) throws java.rmi.RemoteException {
		// Get the current group membership.
		PropEnum[] props = { PropEnum.defaultName, PropEnum.searchPath,
				PropEnum.members };

		BaseClass[] objects = csHandler.queryObjectInCS(connection,
				pathOfGroup, props);
		Group group = (Group) objects[0];

		// Preserve all of the members except the specified member.
		BaseClass[] newMembers = new BaseClass[group.getMembers().getValue().length - 1];
		int index = 0;
		BaseClass obj = null;
		for (int i = 0; i < group.getMembers().getValue().length; i++) {
			obj = group.getMembers().getValue()[i];

			BaseClass[] memberProps = csHandler.queryObjectInCS(connection, obj
					.getSearchPath().getValue());

			if (memberProps[0].getDefaultName().getValue()
					.compareTo(member.getDefaultName().getValue()) != 0
					&& memberProps[0].getSearchPath().getValue()
							.compareTo(member.getSearchPath().getValue()) != 0) {
				newMembers[index] = obj;
				index++;
			}
		}

		group.setMembers(new BaseClassArrayProp());
		group.getMembers().setValue(newMembers);

		// Update the membership.
		csHandler.updateObjectInCS(connection, objects);
	}

	/**
	 * Remove the specified member from the specified role.
	 * 
	 * @param connection
	 *            Connection to Server
	 * @param pathOfRole
	 *            Search path to the role.
	 * @param member
	 *            An account, group or role.
	 * 
	 */
	public void removeFromRole(CRNConnect connection, String pathOfRole,
			BaseClass member) throws java.rmi.RemoteException {
		// Get the current role membership.
		PropEnum[] props = { PropEnum.defaultName, PropEnum.searchPath,
				PropEnum.members };

		BaseClass[] objects = csHandler.queryObjectInCS(connection, pathOfRole,
				props);
		Role role = (Role) objects[0];
		// Preserve all of the members except the specified member.
		BaseClass[] newMembers = new BaseClass[role.getMembers().getValue().length - 1];
		int index = 0;
		String csMember;
		String csMemberPath;
		BaseClass obj = null;
		for (int i = 0; i < role.getMembers().getValue().length; i++) {
			obj = role.getMembers().getValue()[i];

			BaseClass[] memberProps = csHandler.queryObjectInCS(connection, obj
					.getSearchPath().getValue());

			csMember = memberProps[0].getDefaultName().getValue();
			csMemberPath = memberProps[0].getSearchPath().getValue();
			if ((csMember.compareTo(member.getDefaultName().getValue()) != 0)
					&& (csMemberPath.compareTo(member.getSearchPath()
							.getValue()) != 0)) {
				newMembers[index] = obj;
				index++;
			}
		}

		role.setMembers(new BaseClassArrayProp());
		role.getMembers().setValue(newMembers);

		// Update the membership.
		csHandler.updateObjectInCS(connection, objects);
	}

	/**
	 * Delete the specified group.
	 * 
	 * @param connection
	 *            Connection to Server
	 * @param groupSearchPath
	 *            The searchpath for the group to delete.
	 * @return A string containing successful status information.
	 * 
	 */
	public String deleteGroup(CRNConnect connection, String groupSearchPath)
			throws java.rmi.RemoteException {
		Group obj = new Group();
		obj.setSearchPath(new StringProp());
		obj.getSearchPath().setValue(groupSearchPath);

		csHandler.deleteObjectFromCS(connection, obj);

		return ("Successfully deleted " + groupSearchPath);

	}

	/**
	 * Delete the specified role.
	 * 
	 * @param connection
	 *            Connection to Server
	 * @param roleSearchPath
	 *            The role to delete.
	 * @return A string containing successful status information.
	 * 
	 */
	public String deleteRole(CRNConnect connection, String roleSearchPath)
			throws java.rmi.RemoteException {
		Role obj = new Role();
		obj.setSearchPath(new StringProp());
		obj.getSearchPath().setValue(roleSearchPath);

		csHandler.deleteObjectFromCS(connection, obj);

		return ("Successfully deleted the " + roleSearchPath);
	}

	/**
	 * Get information specific to a particular user.
	 * 
	 * @param connection
	 *            Connection to Server
	 * @param pathOfUser
	 *            Search path to user to query.
	 * @return User Information.
	 * 
	 */
	public BaseClass[] getMemberInfo(CRNConnect connection, String pathOfUser) {
		BaseClass groups[] = new BaseClass[] {};
		PropEnum props[] = new PropEnum[] { PropEnum.searchPath,
				PropEnum.defaultName };
		try {
			groups = connection.getCMService().query(
					new SearchPathMultipleObject(pathOfUser), props,
					new Sort[] {}, new QueryOptions());
		} catch (Exception e) {
			System.out.println(e);
		}
		return groups;
	}

	/**
	 * This method queries the Content Store for all the available members in a
	 * specific group.
	 * 
	 * @param connection
	 *            Connection to Server
	 * @param group
	 *            Search path to the group or role.
	 * 
	 */
	public BaseClass[] getAvailableMembers(CRNConnect connection, String group) {
		PropEnum props[] = new PropEnum[] { PropEnum.searchPath,
				PropEnum.defaultName, PropEnum.members };
		BaseClass[] availableMembers;
		try {
			availableMembers = csHandler.queryObjectInCS(connection, group,
					props);
			return availableMembers;
		} catch (java.rmi.RemoteException remoteEx) {
			remoteEx.printStackTrace();
			return null;
		}
	}

	/**
	 * This method queries the Content Store for all the available groups in a
	 * specific namespace.
	 * 
	 * @param connection
	 *            Connection to Server
	 * @param namespace
	 *            Search path to the namespace.
	 * 
	 */
	public BaseClass[] getAvailableGroups(CRNConnect connection,
			String namespace) {
		PropEnum props[] = new PropEnum[] { PropEnum.searchPath,
				PropEnum.defaultName };
		BaseClass[] availableGroups;
		try {
			availableGroups = csHandler.queryObjectInCS(connection, namespace
					+ "/group", props);
			return availableGroups;
		} catch (java.rmi.RemoteException remoteEx) {
			remoteEx.printStackTrace();
			return null;
		}
	}

	/**
	 * This method queries the Content Store for all the available roles in a
	 * specific namespace.
	 * 
	 * @param connection
	 *            Connection to Server
	 * @param namespace
	 *            Search path to the namespace.
	 * 
	 */
	public BaseClass[] getAvailableRoles(CRNConnect connection, String namespace) {
		PropEnum props[] = new PropEnum[] { PropEnum.searchPath,
				PropEnum.defaultName };
		BaseClass[] availableRoles;
		try {
			availableRoles = csHandler.queryObjectInCS(connection, namespace
					+ "/role", props);
			return availableRoles;
		} catch (java.rmi.RemoteException remoteEx) {
			remoteEx.printStackTrace();
			return null;
		}
	}

	/**
	 * This method queries the Content Store for all the available namespaces.
	 * 
	 * @param connection
	 *            Connection to Server
	 * 
	 */
	public BaseClass[] getAvailableNamespaces(CRNConnect connection) {
		PropEnum props[] = new PropEnum[] { PropEnum.searchPath,
				PropEnum.defaultName };
		BaseClass[] nameSpaces;
		try {
			nameSpaces = csHandler.queryObjectInCS(connection,
					"/directory/namespace", props);
			return nameSpaces;
		} catch (java.rmi.RemoteException remoteEx) {
			remoteEx.printStackTrace();
			return null;
		}
	}
}
