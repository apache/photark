/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.photark.social.services.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jcr.Node;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.apache.photark.services.jcr.JCRRepositoryManager;
import org.apache.photark.social.PhotArkSocialConstants;
import org.apache.photark.social.PhotArkSocialException;
import org.apache.photark.social.services.RelationshipService;
import org.apache.photark.social.util.PhotArkSocialUtil;
import org.oasisopen.sca.annotation.Scope;

@Scope("COMPOSITE")
public class JCRRelationshipServiceImpl implements RelationshipService {

	private JCRRepositoryManager repositoryManager;

	private static final Logger logger = Logger
			.getLogger(JCRRelationshipServiceImpl.class.getName());

	public JCRRelationshipServiceImpl() throws IOException {
		repositoryManager = new JCRRepositoryManager();
	}

	public String getRelationshipStatus(String viewer, String owner)
			throws PhotArkSocialException {
		Node viewerProfileNode = PhotArkSocialUtil.getPersonProfileRootNode(
				repositoryManager, viewer, false);
		Node ownerProfileNode = PhotArkSocialUtil.getPersonProfileRootNode(
				repositoryManager, owner, false);
		if (viewerProfileNode == null || ownerProfileNode == null) {
			String user = viewerProfileNode == null ? viewer : owner;
			throw new PhotArkSocialException("User :" + user
					+ "'s profile does not exists");
		} else {
			try {
				String[] propertyValues = null;
				if (viewerProfileNode
						.hasProperty(PhotArkSocialConstants.RELATIONSHIP_FRIEND)) {
					propertyValues = convertValueToString(
							viewerProfileNode.getProperty(
									PhotArkSocialConstants.RELATIONSHIP_FRIEND)
									.getValues(), false);
					if (arrayContains(propertyValues, owner)) {
						return PhotArkSocialConstants.RELATIONSHIP_FRIEND;
					}
				}
				if (viewerProfileNode
						.hasProperty(PhotArkSocialConstants.RELATIONSHIP_REQUEST_RECEIVED)) {
					propertyValues = convertValueToString(
							viewerProfileNode
									.getProperty(
											PhotArkSocialConstants.RELATIONSHIP_REQUEST_RECEIVED)
									.getValues(), false);
					if (arrayContains(propertyValues, owner)) {
						return PhotArkSocialConstants.RELATIONSHIP_REQUEST_RECEIVED;
					}
				}
				if (ownerProfileNode
						.hasProperty(PhotArkSocialConstants.RELATIONSHIP_REQUEST_RECEIVED)) {
					propertyValues = convertValueToString(
							ownerProfileNode
									.getProperty(
											PhotArkSocialConstants.RELATIONSHIP_REQUEST_RECEIVED)
									.getValues(), false);
					if (arrayContains(propertyValues, viewer)) {
						return PhotArkSocialConstants.RELATIONSHIP_REQUEST_PENDING;
					}

				}
			} catch (RepositoryException e) {
				logger.log(Level.SEVERE,
						"Error requesting relationship status between " + owner
								+ " and " + viewer + e.getMessage(), e);
				throw new PhotArkSocialException(
						"Error requesting relationship between status " + owner
								+ " and " + viewer + e.getMessage(), e);
			}
		}
		return PhotArkSocialConstants.RELATIONSHIP_NONE;
	}

	public boolean requestRelationship(String viewer, String owner)
			throws PhotArkSocialException {
		Node ownerProfileNode = PhotArkSocialUtil.getPersonProfileRootNode(
				repositoryManager, owner, false);
		if (ownerProfileNode == null) {
			throw new PhotArkSocialException("Owner :" + owner
					+ "'s profile does not exists");
		}
		String[] newPropertyValues = null;
		try {
			if (!ownerProfileNode
					.hasProperty(PhotArkSocialConstants.RELATIONSHIP_REQUEST_RECEIVED)) {
				newPropertyValues = new String[1];
				newPropertyValues[0] = viewer;
			} else {
				Value[] props = ownerProfileNode.getProperty(
						PhotArkSocialConstants.RELATIONSHIP_REQUEST_RECEIVED)
						.getValues();
				newPropertyValues = convertValueToString(props, true);
				newPropertyValues[props.length] = viewer;
			}
			ownerProfileNode.setProperty(
					PhotArkSocialConstants.RELATIONSHIP_REQUEST_RECEIVED,
					newPropertyValues, PropertyType.STRING);
			repositoryManager.getSession().save();
		} catch (RepositoryException e) {
			logger.log(Level.SEVERE, "Error requesting relationship between "
					+ owner + " and " + viewer + e.getMessage(), e);
			throw new PhotArkSocialException(
					"Error requesting relationship between " + owner + " and "
							+ viewer + e.getMessage(), e);
		}
		return true;
	}

	public boolean acceptRelationshipRequest(String viewer, String owner)
			throws PhotArkSocialException {
		Node viewerProfileNode = PhotArkSocialUtil.getPersonProfileRootNode(
				repositoryManager, viewer, false);
		Node ownerProfileNode = PhotArkSocialUtil.getPersonProfileRootNode(
				repositoryManager, owner, false);
		if (viewerProfileNode == null) {
			throw new PhotArkSocialException("Viewer :" + viewer
					+ "'s profile does not exists");
		}
		if (ownerProfileNode == null) {
			throw new PhotArkSocialException("Owner :" + owner
					+ "'s profile does not exists");
		}
		try {
			if (!getRelationshipStatus(viewer, owner).equals(
					PhotArkSocialConstants.RELATIONSHIP_REQUEST_RECEIVED)) {
				throw new PhotArkSocialException(viewer
						+ " has not received a relationship request from "
						+ owner);
			}
			// remove from viewer's received relationship list
			String[] pendingRequest = getPendingRelationshipRequests(viewer);
			String[] newProps = new String[pendingRequest.length - 1];
			int index = 0;
			if (newProps.length > 0) {
				for (String user : pendingRequest) {
					if (!(user.equals(owner))) {
						newProps[index++] = user;
					}
				}
				viewerProfileNode.setProperty(
						PhotArkSocialConstants.RELATIONSHIP_REQUEST_RECEIVED,
						newProps, PropertyType.STRING);
			} else {
				viewerProfileNode.setProperty(
						PhotArkSocialConstants.RELATIONSHIP_REQUEST_RECEIVED,
						(String[]) null, PropertyType.STRING);

			}

			String[] newPropertyValues = null;
			// create a reference between ownerProfileNode and viewerProfileNode
			if (!ownerProfileNode
					.hasProperty(PhotArkSocialConstants.RELATIONSHIP_FRIEND)) {
				newPropertyValues = new String[1];
				newPropertyValues[0] = viewer;
				ownerProfileNode.setProperty(
						PhotArkSocialConstants.RELATIONSHIP_FRIEND,
						newPropertyValues, PropertyType.STRING);
			} else {
				Value[] props = ownerProfileNode.getProperty(
						RELATIONSHIP_FRIEND).getValues();
				newPropertyValues = convertValueToString(props, true);
				newPropertyValues[props.length] = viewer;
				ownerProfileNode.setProperty(
						PhotArkSocialConstants.RELATIONSHIP_FRIEND,
						newPropertyValues, PropertyType.STRING);
			}
			if (!viewerProfileNode
					.hasProperty(PhotArkSocialConstants.RELATIONSHIP_FRIEND)) {
				newPropertyValues = new String[1];
				newPropertyValues[0] = owner;
				viewerProfileNode.setProperty(
						PhotArkSocialConstants.RELATIONSHIP_FRIEND,
						newPropertyValues, PropertyType.STRING);
			} else {
				Value[] props = viewerProfileNode.getProperty(
						RELATIONSHIP_FRIEND).getValues();
				newPropertyValues = convertValueToString(props, true);
				newPropertyValues[props.length] = owner;
				viewerProfileNode.setProperty(
						PhotArkSocialConstants.RELATIONSHIP_FRIEND,
						newPropertyValues, PropertyType.STRING);

			}
			repositoryManager.getSession().save();
		} catch (RepositoryException e) {
			logger.log(Level.SEVERE, "Error creating relationship between "
					+ owner + " and " + viewer + e.getMessage(), e);
			throw new PhotArkSocialException(
					"Error creating relationship between " + owner + " and "
							+ viewer + e.getMessage(), e);
		}
		return true;
	}

	public boolean ignoreRelationship(String viewer, String owner)
			throws PhotArkSocialException {
		// viewer is going to ignore the request from owner
		Node viewerProfileNode = PhotArkSocialUtil.getPersonProfileRootNode(
				repositoryManager, viewer, false);
		if (viewerProfileNode == null) {
			throw new PhotArkSocialException("User :" + viewer
					+ "'s profile does not exists");
		}
		try {
			if (!getRelationshipStatus(viewer, owner).equals(
					PhotArkSocialConstants.RELATIONSHIP_REQUEST_RECEIVED)) {
				throw new PhotArkSocialException("User " + viewer
						+ " has not received relationship request from "
						+ owner);
			} else {
				String[] props = convertValueToString(
						viewerProfileNode
								.getProperty(
										PhotArkSocialConstants.RELATIONSHIP_REQUEST_RECEIVED)
								.getValues(), false);
				List<String> propsList = Arrays.asList(props);
				String[] newProps = new String[props.length - 1];
				int index = 0;
				if (newProps.length > 0) {
					for (String prop : propsList) {
						if (!prop.equals(owner)) {
							newProps[index++] = prop;
						}
					}

					viewerProfileNode
							.setProperty(
									PhotArkSocialConstants.RELATIONSHIP_REQUEST_RECEIVED,
									newProps, PropertyType.STRING);
				} else {
					viewerProfileNode
							.setProperty(
									PhotArkSocialConstants.RELATIONSHIP_REQUEST_RECEIVED,
									(String[]) null, PropertyType.STRING);
				}

			}
			repositoryManager.getSession().save();
		} catch (RepositoryException e) {
			logger.log(Level.SEVERE, "Error ignoring relationship between "
					+ owner + " and " + viewer + e.getMessage(), e);
			throw new PhotArkSocialException(
					"Error ignoring relationship between " + owner + " and "
							+ viewer + e.getMessage(), e);
		}
		return true;
	}

	public boolean removeRelationship(String owner, String viewer)
			throws PhotArkSocialException {
		Node ownerProfileNode = PhotArkSocialUtil.getPersonProfileRootNode(
				repositoryManager, owner, false);
		Node viewerProfileNode = PhotArkSocialUtil.getPersonProfileRootNode(
				repositoryManager, viewer, false);
		if (viewerProfileNode == null || ownerProfileNode == null) {
			String user = viewerProfileNode == null ? viewer : owner;
			throw new PhotArkSocialException("User :" + user
					+ "'s profile does not exists");
		}
		try {
			if (!getRelationshipStatus(viewer, owner).equals(
					PhotArkSocialConstants.RELATIONSHIP_FRIEND)) {
				throw new PhotArkSocialException("User " + viewer
						+ " donesn't have friend relationship with user "
						+ owner);
			} else {
				String[] props = convertValueToString(
						viewerProfileNode.getProperty(
								PhotArkSocialConstants.RELATIONSHIP_FRIEND)
								.getValues(), false);
				List<String> propsList = Arrays.asList(props);
				String[] newProps = new String[props.length - 1];
				int index = 0;
				if (newProps.length > 0) {
					for (String prop : propsList) {
						if ((!prop.equals(owner))) {
							newProps[index++] = prop;
						}
					}
					viewerProfileNode.setProperty(
							PhotArkSocialConstants.RELATIONSHIP_FRIEND,
							newProps, PropertyType.STRING);
				} else {
					viewerProfileNode.setProperty(
							PhotArkSocialConstants.RELATIONSHIP_FRIEND,
							(String[]) null, PropertyType.STRING);
				}
				props = convertValueToString(
						ownerProfileNode.getProperty(
								PhotArkSocialConstants.RELATIONSHIP_FRIEND)
								.getValues(), false);
				propsList = Arrays.asList(props);
				newProps = new String[props.length - 1];
				index = 0;
				if (newProps.length > 0) {
					for (String prop : propsList) {
						if ((!prop.equals(owner))
								&& newProps.length >= index + 1) {
							newProps[index++] = prop;
						}
					}
					ownerProfileNode.setProperty(
							PhotArkSocialConstants.RELATIONSHIP_FRIEND,
							newProps, PropertyType.STRING);
				} else {
					ownerProfileNode.setProperty(
							PhotArkSocialConstants.RELATIONSHIP_FRIEND,
							(String[]) null, PropertyType.STRING);
				}

			}
			repositoryManager.getSession().save();
		} catch (RepositoryException e) {
			logger.log(Level.SEVERE, "Error removing relationship between "
					+ owner + " and " + viewer + " " + e.getMessage(), e);
			throw new PhotArkSocialException(
					"Error removing relationship between " + owner + " and "
							+ viewer + " " + e.getMessage(), e);
		}
		return true;
	}

	public String[] getRelationshipList(String loggedUser)
			throws PhotArkSocialException {
		Node userProfileNode = PhotArkSocialUtil.getPersonProfileRootNode(
				repositoryManager, loggedUser, false);
		if (userProfileNode == null) {
			throw new PhotArkSocialException("User :" + loggedUser
					+ "'s profile does not exists");
		}
		try {
			if (userProfileNode
					.getProperty(PhotArkSocialConstants.RELATIONSHIP_FRIEND) == null) {
				return new String[0];
			} else {
				Value[] props = userProfileNode.getProperty(
						PhotArkSocialConstants.RELATIONSHIP_FRIEND).getValues();

				return convertValueToString(props, false);
			}
		} catch (RepositoryException e) {
			logger.log(Level.SEVERE,
					"Error retrieving relationship lists of user" + loggedUser
							+ ". " + e.getMessage(), e);
			throw new PhotArkSocialException(
					"Error retrieving relationship lists of user" + loggedUser
							+ ". " + e.getMessage(), e);
		}

	}

	public String[] getPendingRelationshipRequests(String owner)
			throws PhotArkSocialException {
		Node ownerProfileNode = PhotArkSocialUtil.getPersonProfileRootNode(
				repositoryManager, owner, false);
		if (ownerProfileNode == null) {
			throw new PhotArkSocialException("Owner :" + owner
					+ "'s profile does not exists");
		}
		try {
			if (!ownerProfileNode
					.hasProperty(PhotArkSocialConstants.RELATIONSHIP_REQUEST_RECEIVED)) {
				return new String[0];
			} else {
				Value[] props = ownerProfileNode.getProperty(
						PhotArkSocialConstants.RELATIONSHIP_REQUEST_RECEIVED)
						.getValues();
				return convertValueToString(props, false);
			}

		} catch (RepositoryException e) {
			logger.log(Level.SEVERE,
					"Error retrieving pending relationship request for user "
							+ owner + e.getMessage(), e);
			throw new PhotArkSocialException(
					"Error retrieving pending relationship request for user "
							+ owner + e.getMessage(), e);
		}

	}

	private String[] convertValueToString(Value[] valueArray,
			boolean addOneElement) throws RepositoryException {
		String[] returnArray = null;
		if (addOneElement) {
			returnArray = new String[valueArray.length + 1];
		} else {
			returnArray = new String[valueArray.length];
		}
		int index = 0;
		for (Value value : valueArray) {
			returnArray[index++] = value.getString();
		}
		return returnArray;
	}

	private boolean arrayContains(String[] array, String queryString) {
		List<String> arrayItems = Arrays.asList(array);
		if (arrayItems.contains(queryString)) {
			return true;
		} else {
			return false;
		}
	}
}
