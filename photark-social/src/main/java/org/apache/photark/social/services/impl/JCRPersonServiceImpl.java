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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.apache.photark.services.PhotarkRuntimeException;
import org.apache.photark.services.jcr.JCRRepositoryManager;
import org.apache.photark.social.Person;
import org.apache.photark.social.PhotArkSocialConstants;
import org.apache.photark.social.PhotArkSocialException;
import org.apache.photark.social.services.PersonService;
import org.apache.photark.social.util.PhotArkSocialUtil;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Scope;

@Scope("COMPOSITE")
public class JCRPersonServiceImpl implements PersonService {
	private JCRRepositoryManager repositoryManager;

	private static final Logger logger = Logger
			.getLogger(JCRPersonServiceImpl.class.getName());

	public JCRPersonServiceImpl() throws IOException {
		repositoryManager = new JCRRepositoryManager();
	}

	public JCRPersonServiceImpl(
			@Reference(name = "repositoryManager")JCRRepositoryManager repositoryManager) {
		this.repositoryManager = repositoryManager;
	}

	public void savePerson(String personId, Person person)
			throws PhotArkSocialException {
		if (person == null) {
			throw new PhotArkSocialException(
					"Unable to save person. Given Person object is null");
		}
		Node socialDataRootNode = null;
		Node personProfileNode = null;

		try {
			socialDataRootNode = PhotArkSocialUtil
					.getSocialDataRoot(repositoryManager);
			if (socialDataRootNode.hasNode(personId)) {
				// if such userId already exists, return error message
				throw new PhotArkSocialException("UserId " + personId
						+ " already exists");
			}
			personProfileNode = PhotArkSocialUtil.getPersonProfileRootNode(
					repositoryManager, person.getId(), true);
			personProfileNode.setProperty(PhotArkSocialConstants.PERSON_USERID,
					person.getId());
			personProfileNode = createPersonNodeFromPersonObj(person,
					personProfileNode);
			repositoryManager.getSession().save();
		} catch (RepositoryException e) {
			logger.log(
					Level.SEVERE,
					"Error saving person data to photark repository :"
							+ e.getMessage(), e);
			throw new PhotArkSocialException(
					"Error saving person data to photark repository :"
							+ e.getMessage(), e);
		} catch (PhotarkRuntimeException e) {
			logger.log(
					Level.SEVERE,
					"Error saving person data to photark repository :"
							+ e.getMessage(), e);
			throw new PhotArkSocialException(
					"Error saving person data to photark repository :"
							+ e.getMessage(), e);
		}

	}

	public void updatePerson(String personId, Person person)
			throws PhotArkSocialException {
		Node socialDataRootNode = PhotArkSocialUtil
				.getSocialDataRoot(repositoryManager);
		try {
			if (!socialDataRootNode.hasNode(personId)) {
				throw new PhotArkSocialException(
						"Profile for user with user ID " + personId
								+ " doesn't exist");
			}
		} catch (RepositoryException e) {
			logger.log(Level.SEVERE,
					"Error retrieving social data root from photark repository :"
							+ e.getMessage(), e);
			throw new PhotArkSocialException(
					"Error retrieving social data root from photark repository :"
							+ e.getMessage(), e);
		}
		savePerson(personId, person);

	}

	public void removePerson(String personId) throws PhotArkSocialException {
		Node personProfileNode = PhotArkSocialUtil.getPersonProfileRootNode(
				repositoryManager, personId, false);
		if (personProfileNode != null) { // node exists
			try {
				personProfileNode.remove();
				repositoryManager.getSession().save();
			} catch (RepositoryException e) {
				logger.log(Level.SEVERE,
						"Error removing person data from photark repository :"
								+ e.getMessage(), e);
				throw new PhotArkSocialException(
						"Error removing person data from photark repository :"
								+ e.getMessage(), e);
			}
		} else {
			throw new PhotArkSocialException("Profile for user with user ID "
					+ personId + " doesn't exist");
		}

	}

/*	public Person getPerson(String personId) throws PhotArkSocialException {
		return getPerson(personId, null);

	}*/

	public Person getPerson(String personId)
			throws PhotArkSocialException {
		Node personProfileNode = null;
		Person personObj = null;
		try {
			personProfileNode = PhotArkSocialUtil.getPersonProfileRootNode(
					repositoryManager, personId, false);
			if (personProfileNode != null) {
				personObj = createPersonObjFromProfileNode(personProfileNode);
			}
		} catch (PhotarkRuntimeException e) {
			logger.log(Level.SEVERE,
					"Error retrieving person data from photark repository :"
							+ e.getMessage(), e);
			throw new PhotArkSocialException(
					"Error retrieving person data from photark repository :"
							+ e.getMessage(), e);
		} catch (RepositoryException e) {
			logger.log(Level.SEVERE,
					"Error retrieving person data from photark repository :"
							+ e.getMessage(), e);
			throw new PhotArkSocialException(
					"Error retrieving person data from photark repository :"
							+ e.getMessage(), e);
		}
		return personObj;
	}

/*	public Person[] getPeople(String[] personIds, String groupId,
			String[] fields) throws PhotArkSocialException {
		// TODO Auto-generated method stub
		return null;
	}*/

	/**
	 * Creates a Person object from the properties of the profile node of that
	 * person
	 * 
	 * @param profileNode
	 *            "profile" node of the person
	 * @return a Person object, with all the properties set as attributes; null
	 *         if the "userId" property is not set
	 * @throws RepositoryException
	 */

	private Person createPersonObjFromProfileNode(Node profileNode)
			throws RepositoryException {
		Person personObj = null;
		// creates a person object only if atleast the userId property is set
		if (profileNode.hasProperty(PhotArkSocialConstants.PERSON_USERID)) {
			personObj = new Person();
			personObj.setId(profileNode
					.getProperty(PhotArkSocialConstants.PERSON_USERID)
					.getValue().getString());
		}
		if (profileNode.hasProperty(PhotArkSocialConstants.PERSON_FIRSTNAME)) {
			personObj.setFirstName(profileNode
					.getProperty(PhotArkSocialConstants.PERSON_FIRSTNAME)
					.getValue().getString());
		}
		if (profileNode.hasProperty(PhotArkSocialConstants.PERSON_LASTNAME)) {
			personObj.setLastName(profileNode
					.getProperty(PhotArkSocialConstants.PERSON_LASTNAME)
					.getValue().getString());
		}
		if (profileNode.hasProperty(PhotArkSocialConstants.PERSON_DISPLAYNAME)) {
			personObj.setDisplayName(profileNode
					.getProperty(PhotArkSocialConstants.PERSON_DISPLAYNAME)
					.getValue().getString());
		}
		if (profileNode.hasProperty(PhotArkSocialConstants.PERSON_ABOUTME)) {
			personObj.setAboutMe(profileNode
					.getProperty(PhotArkSocialConstants.PERSON_ABOUTME)
					.getValue().getString());
		}
		if (profileNode.hasProperty(PhotArkSocialConstants.PERSON_ADDRESS)) {
			personObj.setAddress(profileNode
					.getProperty(PhotArkSocialConstants.PERSON_ADDRESS)
					.getValue().getString());
		}
		if (profileNode.hasProperty(PhotArkSocialConstants.PERSON_BIRTHDAY)) {
			personObj.setBirthday(new Date(profileNode
					.getProperty(PhotArkSocialConstants.PERSON_BIRTHDAY)
					.getValue().getLong()));
		}
		if (profileNode.hasProperty(PhotArkSocialConstants.PERSON_GENDER)) {
			personObj.setGender(profileNode
					.getProperty(PhotArkSocialConstants.PERSON_GENDER)
					.getValue().getString());
		}
		if (profileNode.hasProperty(PhotArkSocialConstants.PERSON_NICKNAME)) {
			personObj.setNickname(profileNode
					.getProperty(PhotArkSocialConstants.PERSON_NICKNAME)
					.getValue().getString());
		}
		if (profileNode.hasProperty(PhotArkSocialConstants.PERSON_THUMBNAILURL)) {
			personObj.setThumbnailUrl(profileNode
					.getProperty(PhotArkSocialConstants.PERSON_THUMBNAILURL)
					.getValue().getString());
		}
		if (profileNode.hasProperty(PhotArkSocialConstants.PERSON_ACTIVITIES)) {
			Value[] values = profileNode.getProperty(
					PhotArkSocialConstants.PERSON_ACTIVITIES).getValues();
			List<String> activities = new ArrayList<String>();
			for (Value val : values) {
				activities.add(val.getString());
			}
			personObj.setActivities(activities);
		}
		if (profileNode.hasProperty(PhotArkSocialConstants.PERSON_EMAILS)) {
			Value[] values = profileNode.getProperty(
					PhotArkSocialConstants.PERSON_EMAILS).getValues();
			List<String> emails = new ArrayList<String>();
			for (Value val : values) {
				emails.add(val.getString());
			}
			personObj.setEmails(emails);
		}
		if (profileNode
				.hasProperty(PhotArkSocialConstants.PERSON_ORGANIZATIONS)) {
			Value[] values = profileNode.getProperty(
					PhotArkSocialConstants.PERSON_ORGANIZATIONS).getValues();
			List<String> orgs = new ArrayList<String>();
			for (Value val : values) {
				orgs.add(val.getString());
			}
			personObj.setOrganizations(orgs);
		}
		if (profileNode.hasProperty(PhotArkSocialConstants.PERSON_PHONENUMBERS)) {
			Value[] values = profileNode.getProperty(
					PhotArkSocialConstants.PERSON_PHONENUMBERS).getValues();
			List<String> phoneNumbers = new ArrayList<String>();
			for (Value val : values) {
				phoneNumbers.add(val.getString());
			}
			personObj.setPhoneNumbers(phoneNumbers);
		}
		return personObj;
	}

	private Node createPersonNodeFromPersonObj(Person personObj,
			Node personProfileNode) throws RepositoryException,
			PhotArkSocialException {
		if (personObj.getId() != null) {
			personProfileNode.setProperty(PhotArkSocialConstants.PERSON_USERID,
					personObj.getId());
		}
		if (personObj.getFirstName() != null) {
			personProfileNode.setProperty(
					PhotArkSocialConstants.PERSON_FIRSTNAME,
					personObj.getFirstName());
		}
		if (personObj.getLastName() != null) {
			personProfileNode.setProperty(
					PhotArkSocialConstants.PERSON_LASTNAME,
					personObj.getLastName());
		}
		if (personObj.getDisplayName() != null) {
			personProfileNode.setProperty(
					PhotArkSocialConstants.PERSON_DISPLAYNAME,
					personObj.getDisplayName());
		}
		if (personObj.getAboutMe() != null) {
			personProfileNode.setProperty(
					PhotArkSocialConstants.PERSON_ABOUTME,
					personObj.getAboutMe());
		}
		if (personObj.getAddress() != null) {
			personProfileNode.setProperty(
					PhotArkSocialConstants.PERSON_ADDRESS,
					personObj.getAddress());
		}
		if (personObj.getBirthday() != null) {
			personProfileNode.setProperty(
					PhotArkSocialConstants.PERSON_BIRTHDAY, personObj
							.getBirthday().getTime());
		}
		if (personObj.getGender() != null) {
			personProfileNode.setProperty(PhotArkSocialConstants.PERSON_GENDER,
					personObj.getGender());
		}
		if (personObj.getNickname() != null) {
			personProfileNode.setProperty(
					PhotArkSocialConstants.PERSON_NICKNAME,
					personObj.getNickname());
		}
		if (personObj.getThumbnailUrl() != null) {
			personProfileNode.setProperty(
					PhotArkSocialConstants.PERSON_THUMBNAILURL,
					personObj.getThumbnailUrl());
		}
		if (personObj.getActivities() != null
				&& personObj.getActivities().size() > 0) {
			String[] activities = new String[personObj.getActivities().size()];
			personObj.getActivities().toArray(activities);
			personProfileNode.setProperty(
					PhotArkSocialConstants.PERSON_ACTIVITIES, activities);
		}
		if (personObj.getEmails() != null && personObj.getEmails().size() > 0) {
			personProfileNode.setProperty(PhotArkSocialConstants.PERSON_EMAILS,
					(String[]) personObj.getEmails().toArray());
		}
		if (personObj.getOrganizations() != null
				&& personObj.getOrganizations().size() > 0) {
			personProfileNode.setProperty(
					PhotArkSocialConstants.PERSON_ORGANIZATIONS,
					(String[]) personObj.getOrganizations().toArray());
		}
		if (personObj.getPhoneNumbers() != null
				&& personObj.getPhoneNumbers().size() > 0) {
			personProfileNode.setProperty(
					PhotArkSocialConstants.PERSON_PHONENUMBERS,
					(String[]) personObj.getPhoneNumbers().toArray());
		}
		return personProfileNode;
	}
}
