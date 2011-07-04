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

package org.apache.photark.social.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.photark.services.PhotarkRuntimeException;
import org.apache.photark.services.jcr.JCRRepositoryManager;

public class PhotArkSocialUtil {

	private static final String JCR_SOCIAL_DATA_ROOT_NODE = "socialdata";
	private static final String JCR_PROFILE_ROOT_NODE = "profile";

	private static final Logger logger = Logger
			.getLogger(PhotArkSocialUtil.class.getName());

	/**
	 * Retrieves the "socialdata" node
	 * 
	 * @param repositoryManager
	 *            JCRRepositoryManager object
	 * @return the "socialdata" node
	 * @throws PhotarkRuntimeException
	 */
	public static Node getSocialDataRoot(JCRRepositoryManager repositoryManager)
			throws PhotarkRuntimeException {
		Session session = null;
		Node root = null;
		Node socialDataRoot = null;
		try {
			session = repositoryManager.getSession();
			root = session.getRootNode();
			// check whether the social data root node exists
			if (root.hasNode(JCR_SOCIAL_DATA_ROOT_NODE)) {
				socialDataRoot = root.getNode(JCR_SOCIAL_DATA_ROOT_NODE);
			} else {
				socialDataRoot = root.addNode(JCR_SOCIAL_DATA_ROOT_NODE);
				session.save();
			}

		} catch (LoginException e) {
			logger.log(Level.SEVERE, "Error loging in to photark repository :"
					+ e.getMessage(), e);
			throw new PhotarkRuntimeException(
					"Error loging in to photark repository  :" + e.getMessage(),
					e);
		} catch (RepositoryException e) {
			logger.log(
					Level.SEVERE,
					"Error retrieving social data from the repository :"
							+ e.getMessage(), e);
			throw new PhotarkRuntimeException(
					"Error retrieving social data from the repository :"
							+ e.getMessage(), e);
		}

		return socialDataRoot;
	}

	/**
	 * Retrieves the node for the given user, under the "socialdata" node
	 * 
	 * @param repositoryManager
	 *            JCRRepositoryManager object
	 * @param username
	 *            UserId of the Person
	 * @param create
	 *            If true, creates the node when it doesn't exist; If false,
	 *            return null if the node doesn't exist
	 * @return the node for the given user name
	 * @throws PhotarkRuntimeException
	 */
	public static Node getPersonRootNode(
			JCRRepositoryManager repositoryManager, String username,
			boolean create) throws PhotarkRuntimeException {
		Node socialDataRootNode = null;
		Node personRootNode = null;
		Session session = null;
		try {
			session = repositoryManager.getSession();
			socialDataRootNode = getSocialDataRoot(repositoryManager);
			if (socialDataRootNode.hasNode(username)) {
				personRootNode = socialDataRootNode.getNode(username);
			} else {
				if (create) {
					personRootNode = socialDataRootNode.addNode(username);
					session.save();
				}
			}
		} catch (LoginException e) {
			logger.log(Level.SEVERE, "Error loging in to photark repository :"
					+ e.getMessage(), e);
			throw new PhotarkRuntimeException(
					"Error loging in to photark repository  :" + e.getMessage(),
					e);
		} catch (RepositoryException e) {
			logger.log(Level.SEVERE,
					"Error retrieving person root node from the repository :"
							+ e.getMessage(), e);
			throw new PhotarkRuntimeException(
					"Error retrieving person root node from the repository :"
							+ e.getMessage(), e);
		}

		return personRootNode;
	}

	/**
	 * Retrieves the "profile" node for the given user
	 * 
	 * @param repositoryManager
	 *            JCRRepositoryManager object
	 * @param username
	 *            UserId of the Person
	 * @param create
	 *            If true, creates the node when it doesn't exist; If false,
	 *            return null if the node doesn't exist
	 * @return "profile" node for the given user name
	 * @throws PhotarkRuntimeException
	 */
	public static Node getPersonProfileRootNode(
			JCRRepositoryManager repositoryManager, String username,
			boolean create) throws PhotarkRuntimeException {
		Session session = null;
		Node personRootNode = null;
		Node personProfileRootNode = null;
		try {
			session = repositoryManager.getSession();
			personRootNode = getPersonRootNode(repositoryManager, username,
					create);
			if (personRootNode.hasNode(JCR_PROFILE_ROOT_NODE)) {
				personProfileRootNode = personRootNode
						.getNode(JCR_PROFILE_ROOT_NODE);
			} else {
				if (create) {
					personProfileRootNode = personRootNode
							.addNode(JCR_PROFILE_ROOT_NODE);
					session.save();
				}
			}
		} catch (LoginException e) {
			logger.log(Level.SEVERE, "Error loging in to photark repository :"
					+ e.getMessage(), e);
			throw new PhotarkRuntimeException(
					"Error loging in to photark repository  :" + e.getMessage(),
					e);
		} catch (RepositoryException e) {
			logger.log(Level.SEVERE,
					"Error retrieving person profile node from the repository :"
							+ e.getMessage(), e);
			throw new PhotarkRuntimeException(
					"Error retrieving person profile node from the repository :"
							+ e.getMessage(), e);
		}

		return personProfileRootNode;
	}
}
