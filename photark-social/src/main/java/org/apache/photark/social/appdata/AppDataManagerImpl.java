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

package org.apache.photark.social.appdata;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.photark.services.PhotarkRuntimeException;
import org.apache.photark.services.jcr.JCRRepositoryManager;
import org.apache.photark.social.exception.PhotArkSocialException;
import org.apache.photark.social.util.PhotArkSocialUtil;

public class AppDataManagerImpl implements AppDataManager {
    private JCRRepositoryManager repositoryManager;
    private static final Logger logger = Logger.getLogger(AppDataManagerImpl.class.getName());
    private static final String JCR_APPDATA_ROOT_NODE = "appdata";

    public AppDataManagerImpl(JCRRepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    public AppDataManagerImpl() {

    }

    public Map<String, String> getPersonData(String userId, String groupId) throws PhotArkSocialException {
        Map<String, String> personDataMap = new HashMap<String, String>();
        Node appDataRootNode = null;
        appDataRootNode = getAppDataRootNode(repositoryManager, userId, true);

        try {
            PropertyIterator props = appDataRootNode.getProperties();
            Property prop = props.nextProperty();
            while (props.hasNext()) {
                personDataMap.put(prop.getName(), prop.getValue().getString());
                prop = props.nextProperty();
            }
        } catch (RepositoryException e) {
            logger.log(Level.SEVERE, "Error retrieving app data for user ID: " + userId
                + " to photark repository: "
                + e.getMessage(), e);
            throw new PhotArkSocialException("Error retrieving app data for user ID: " + userId
                + " to photark repository: "
                + e.getMessage(), e);

        }
        return personDataMap;
    }

    public Map<String, Map<String, String>> getPeopleData(String[] userIds, String groupId)
        throws PhotArkSocialException {
        Map<String, Map<String, String>> peopleDataMap = new HashMap<String, Map<String, String>>();
        for (String userId : userIds) {
            peopleDataMap.put(userId, getPersonData(userId, null));
        }
        return peopleDataMap;

    }

    public void savePersonData(String userId, Map<String, String> values) throws PhotArkSocialException {
        if (values == null) {
            throw new PhotArkSocialException("Unable to save appdata. Given appdata map is null");
        }
        Node appDataRootNode = null;
        appDataRootNode = getAppDataRootNode(repositoryManager, userId, true);

        try {
            for (String key : values.keySet()) {
                appDataRootNode.setProperty(key, values.get(key));
            }
            repositoryManager.getSession().save();
        } catch (RepositoryException e) {
            logger.log(Level.SEVERE,
                       "Error saving app data for user ID: " + userId + " to photark repository: " + e.getMessage(),
                       e);
            throw new PhotArkSocialException("Error saving app data for user ID: " + userId
                + " to photark repository: "
                + e.getMessage(), e);

        }

    }

    public void deletePersonData(String userId, String groupId, Set<String> fields) throws PhotArkSocialException {
        Node appDataRootNode = null;
        appDataRootNode = getAppDataRootNode(repositoryManager, userId, true);

        try {
            for (String key : fields) {
                if (appDataRootNode.hasProperty(key)) {
                    // deletes only if the key already exists
                    // sets the property value to null, to remove the property
                    appDataRootNode.setProperty(key, (String)null);
                }
            }
            repositoryManager.getSession().save();
        } catch (RepositoryException e) {
            logger.log(Level.SEVERE,
                       "Error removing app data for user ID: " + userId + " to photark repository: " + e.getMessage(),
                       e);
            throw new PhotArkSocialException("Error removing app data for user ID: " + userId
                + " to photark repository: "
                + e.getMessage(), e);

        }
    }

    public void updatePersonData(String userId,
                                 String groupId,
                                 String appId,
                                 Set<String> fields,
                                 Map<String, String> values) throws PhotArkSocialException {
        Node appDataRootNode = null;
        appDataRootNode = getAppDataRootNode(repositoryManager, userId, true);

        try {
            for (String key : fields) {
                if (appDataRootNode.hasProperty(key)) {
                    // updates only if the key already exists
                    appDataRootNode.setProperty(key, values.get(key));
                }
            }
            repositoryManager.getSession().save();
        } catch (RepositoryException e) {
            logger.log(Level.SEVERE,
                       "Error updating app data for user ID: " + userId + " to photark repository: " + e.getMessage(),
                       e);
            throw new PhotArkSocialException("Error updating app data for user ID: " + userId
                + " to photark repository: "
                + e.getMessage(), e);

        }

    }

    /**
     * Retrieves the "appdata" node for the given user
     * 
     * @param repositoryManager JCRRepositoryManager object
     * @param username UserId of the Person to whom the apdpata belongs to
     * @param create If true, creates the node when it doesn't exist; If false,
     *            return null if the node doesn't exist
     * @return the root node of "appdata" for given user
     * @throws PhotarkRuntimeException
     */

    private Node getAppDataRootNode(JCRRepositoryManager repositoryManager, String username, boolean create)
        throws PhotarkRuntimeException {
        Session session = null;
        Node personRootNode = null;
        Node appDataRootNode = null;
        try {
            session = repositoryManager.getSession();
            personRootNode = PhotArkSocialUtil.getPersonRootNode(repositoryManager, username, create);
            if (personRootNode.hasNode(JCR_APPDATA_ROOT_NODE)) {
                appDataRootNode = personRootNode.getNode(JCR_APPDATA_ROOT_NODE);
            } else {
                if (create) {
                    appDataRootNode = personRootNode.addNode(JCR_APPDATA_ROOT_NODE);
                    session.save();
                }
            }
        } catch (LoginException e) {
            logger.log(Level.SEVERE, "Error loging in to photark repository :" + e.getMessage(), e);
            throw new PhotarkRuntimeException("Error loging in to photark repository  :" + e.getMessage(), e);
        } catch (RepositoryException e) {
            logger.log(Level.SEVERE, "Error retrieving appdata root node for user: " + username
                + " from the repository :"
                + e.getMessage(), e);
            throw new PhotarkRuntimeException("Error retrieving appdata root node for user: " + username
                + " from the repository :"
                + e.getMessage(), e);
        }

        return appDataRootNode;
    }

}
