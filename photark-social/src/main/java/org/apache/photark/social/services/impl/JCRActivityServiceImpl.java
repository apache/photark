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
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.photark.services.PhotarkRuntimeException;
import org.apache.photark.services.jcr.JCRRepositoryManager;
import org.apache.photark.social.Activity;
import org.apache.photark.social.PhotArkSocialConstants;
import org.apache.photark.social.PhotArkSocialException;
import org.apache.photark.social.services.ActivityService;
import org.apache.photark.social.util.FilterOptions;
import org.apache.photark.social.util.PhotArkSocialUtil;

public class JCRActivityServiceImpl implements ActivityService {
    private JCRRepositoryManager repositoryManager;
    private static final Logger logger = Logger.getLogger(JCRActivityServiceImpl.class.getName());
    private static final String JCR_ACTIVITY_ROOT_NODE = "activity";
    private static final String NEXT_ACTIVITY_ID = "nextActivityId";

    public JCRActivityServiceImpl() throws IOException {
        repositoryManager = new JCRRepositoryManager();
    }

    public JCRActivityServiceImpl(JCRRepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    public Activity createActivity(String userId, String groupId, Set<String> fields) throws PhotArkSocialException {
        // TODO Auto-generated method stub
        return null;
    }

    public void saveActivity(String userId, Activity activity) throws PhotArkSocialException {
        if (activity == null) {
            throw new PhotArkSocialException("Unable to save activity. Given Activity object is null");
        }
        Node activityRootNode = null;
        Node activityNode = null;
        int currentActivityId = -1;

        activityRootNode = getActivityRootNode(repositoryManager, userId, true);
        try {
            currentActivityId = Integer.parseInt(activity.getId());
        } catch (NumberFormatException e) {
            currentActivityId = -1;
        }

        try {
            if (currentActivityId == -1) { // either the activityId is null
                                           // or it is not a valid integer
                if (activityRootNode.hasProperty(NEXT_ACTIVITY_ID)) {
                    currentActivityId = (int)activityRootNode.getProperty(NEXT_ACTIVITY_ID).getValue().getLong();
                    activityRootNode.setProperty(NEXT_ACTIVITY_ID, currentActivityId + 1);
                } else {
                    currentActivityId = 1;
                    activityRootNode.setProperty(NEXT_ACTIVITY_ID, currentActivityId + 1);
                }
            }
            if (activityRootNode.hasNode(currentActivityId + "")) {
                // if such activityId already exists for this user, return error
                // message
                throw new PhotArkSocialException("Activity ID " + currentActivityId + " already exists");
            }
            // retrieves the act
            activityNode = getActivityNode(repositoryManager, userId, currentActivityId + "", true);
            activityNode = createActivityNodeFromActivityObj(activity, activityNode);
            repositoryManager.getSession().save();

        } catch (RepositoryException e) {
            logger.log(Level.SEVERE, "Error saving activity data of ID: " + currentActivityId
                + " for user ID: "
                + userId
                + " to photark repository: "
                + e.getMessage(), e);
            throw new PhotArkSocialException("Error saving activity data of ID: " + currentActivityId
                + " for user ID: "
                + userId
                + " to photark repository: "
                + e.getMessage(), e);
        }

    }

    public void updateActivity(String userId, Activity activity) throws PhotArkSocialException {
        if (getActivityNode(repositoryManager, userId, activity.getId(), false) == null) {
            throw new PhotArkSocialException("No activity exists with ID: " + activity.getId() + " for user: " + userId);
        }
        saveActivity(userId, activity);

    }

    public void deleteActivity(String userId, String activityId) throws PhotArkSocialException {
        Node activityNode = null;

        activityNode = getActivityNode(repositoryManager, userId, activityId, false);
        if (activityNode == null) {
            throw new PhotArkSocialException("No activity exists with ID: " + activityId + " for user: " + userId);
        }
        try {
            activityNode.remove();
            repositoryManager.getSession().save();
        } catch (RepositoryException e) {
            logger.log(Level.SEVERE,
                       "Error removing activity data from photark repository for activity ID: " + activityId
                           + " of user ID: "
                           + userId
                           + e.getMessage(),
                       e);
            throw new PhotArkSocialException(
                                             "Error removing activity data from photark repository for activity ID: " + activityId
                                                 + " of user ID: "
                                                 + userId
                                                 + e.getMessage(), e);
        }

    }

    public void deleteActivities(String userId, String groupId, Set<String> activityIds) throws PhotArkSocialException {
        for (String activityId : activityIds) {
            deleteActivity(userId, activityId);
        }

    }

    public Activity[] getActivities(String[] userIds, String groupId, Set<String> fields, FilterOptions filters)
        throws PhotArkSocialException {
        Activity[] activities = null;
        List<Activity> activityList = new ArrayList<Activity>();
        for (String userId : userIds) {
            for (String activityId : getActivityIds(userId)) {
                if (getActivity(userId, null, null, activityId) != null) {
                    activityList.add(getActivity(userId, null, null, activityId));
                }
            }
        }
        return activityList.toArray(activities);
    }

    public Activity[] getActivities(String userId,
                                    String groupId,
                                    Set<String> fields,
                                    FilterOptions filters,
                                    String[] activityIds) throws PhotArkSocialException {
        Activity[] activities = new Activity[activityIds.length];
        int index = 0;
        for (String id : activityIds) {
            activities[index] = getActivity(userId, groupId, fields, id);
        }
        return activities;
    }

    public Activity getActivity(String userId, String groupId, Set<String> fields, String activityId)
        throws PhotArkSocialException {
        Node activityNode = null;
        activityNode = getActivityNode(repositoryManager, userId, activityId, false);
        if (activityNode == null) {
            return null;
        }
        try {
            return createActivityObjFromActivityNode(activityNode, userId);
        } catch (RepositoryException e) {
            logger.log(Level.SEVERE, "Error retrieving activity data of ID: " + activityId
                + " for user ID: "
                + userId
                + " to photark repository: "
                + e.getMessage(), e);
            throw new PhotArkSocialException("Error retrieving activity data of ID: " + activityId
                + " for user ID: "
                + userId
                + " to photark repository: "
                + e.getMessage(), e);
        }
    }

    /**
     * Retrieves the "activity" root node for the given user
     * 
     * @param repositoryManager JCRRepositoryManager object
     * @param username UserId of the Person to whom the activity belongs to
     * @param create If true, creates the node when it doesn't exist; If false,
     *            return null if the node doesn't exist
     * @return "activity" node for the given user
     * @throws PhotarkRuntimeException
     */
    private static Node getActivityRootNode(JCRRepositoryManager repositoryManager, String username, boolean create)
        throws PhotarkRuntimeException {
        Session session = null;
        Node personRootNode = null;
        Node activityRootNode = null;
        try {
            session = repositoryManager.getSession();
            personRootNode = PhotArkSocialUtil.getPersonRootNode(repositoryManager, username, create);
            if (personRootNode.hasNode(JCR_ACTIVITY_ROOT_NODE)) {
                activityRootNode = personRootNode.getNode(JCR_ACTIVITY_ROOT_NODE);
            } else {
                if (create) {
                    activityRootNode = personRootNode.addNode(JCR_ACTIVITY_ROOT_NODE);
                    session.save();
                }
            }
        } catch (LoginException e) {
            logger.log(Level.SEVERE, "Error loging in to photark repository :" + e.getMessage(), e);
            throw new PhotarkRuntimeException("Error loging in to photark repository  :" + e.getMessage(), e);
        } catch (RepositoryException e) {
            logger.log(Level.SEVERE, "Error retrieving activity root node for user: " + username
                + " from the repository :"
                + e.getMessage(), e);
            throw new PhotarkRuntimeException("Error retrieving activity root node for user: " + username
                + " from the repository :"
                + e.getMessage(), e);
        }

        return activityRootNode;
    }

    /**
     * Retrieves the "activity" node node for the given activityId of the given
     * username
     * 
     * @param repositoryManager JCRRepositoryManager object
     * @param username UserId of the Person to whom the activity belongs to
     * @param activityId ID of the activity
     * @param create If true, creates the node when it doesn't exist; If false,
     *            return null if the node doesn't exist
     * @return the Node representing the given activity
     * @throws PhotarkRuntimeException
     */

    private Node getActivityNode(JCRRepositoryManager repositoryManager,
                                 String username,
                                 String activityId,
                                 boolean create) throws PhotarkRuntimeException {
        Session session = null;
        Node activityNode = null;
        Node activityRootNode = null;
        try {
            session = repositoryManager.getSession();
            activityRootNode = getActivityRootNode(repositoryManager, username, create);
            if (activityRootNode.hasNode(activityId)) {
                activityNode = activityRootNode.getNode(activityId);
            } else {
                if (create) {
                    activityNode = activityRootNode.addNode(activityId);
                    session.save();
                }
            }
        } catch (LoginException e) {
            logger.log(Level.SEVERE, "Error loging in to photark repository :" + e.getMessage(), e);
            throw new PhotarkRuntimeException("Error loging in to photark repository  :" + e.getMessage(), e);
        } catch (RepositoryException e) {
            logger.log(Level.SEVERE, "Error retrieving activity node for activity ID: " + activityId
                + " for user: "
                + username
                + " from the repository :"
                + e.getMessage(), e);
            throw new PhotarkRuntimeException("Error retrieving activity node for activity ID: " + activityId
                + " for user: "
                + username
                + " from the repository :"
                + e.getMessage(), e);
        }

        return activityNode;
    }

    /**
     * @param activityNode node representing an activity
     * @return Activity object, with all the properties set as attributes;
     * @throws RepositoryException
     */
    private Activity createActivityObjFromActivityNode(Node activityNode, String userId) throws RepositoryException {
        Activity activityObj = new Activity();
        if (activityNode.hasProperty(PhotArkSocialConstants.ACTIVITY_BODY)) {
            activityObj.setBody(activityNode.getProperty(PhotArkSocialConstants.ACTIVITY_BODY).getValue().getString());
        }
        if (activityNode.hasProperty(PhotArkSocialConstants.ACTIVITY_POSTEDTIME)) {
            activityObj.setPostedTime(new Date(activityNode.getProperty(PhotArkSocialConstants.ACTIVITY_POSTEDTIME)
                .getValue().getLong()));
        }
        if (activityNode.hasProperty(PhotArkSocialConstants.ACTIVITY_TITLE)) {
            activityObj
                .setTitle(activityNode.getProperty(PhotArkSocialConstants.ACTIVITY_TITLE).getValue().getString());
        }
        if (activityNode.hasProperty(PhotArkSocialConstants.ACTIVITY_URL)) {
            activityObj.setUrl(activityNode.getProperty(PhotArkSocialConstants.ACTIVITY_URL).getValue().getString());
        }
        return activityObj;
    }

    private Node createActivityNodeFromActivityObj(Activity activityObj, Node activityNode) throws RepositoryException,
        PhotArkSocialException {
        if (activityObj.getBody() != null) {
            activityNode.setProperty(PhotArkSocialConstants.ACTIVITY_BODY, activityObj.getBody());
        }
        if (activityObj.getPostedTime() != null) {
            activityNode.setProperty(PhotArkSocialConstants.ACTIVITY_POSTEDTIME, activityObj.getPostedTime().getTime());
        }
        if (activityObj.getTitle() != null) {
            activityNode.setProperty(PhotArkSocialConstants.ACTIVITY_TITLE, activityObj.getTitle());
        }
        if (activityObj.getUrl() != null) {
            activityNode.setProperty(PhotArkSocialConstants.ACTIVITY_URL, activityObj.getUrl());
        }

        return activityNode;
    }

    /**
     * Retrieves all activity IDs for this user
     * 
     * @param userId
     * @return list of activity IDs
     * @throws RepositoryException
     */
    private List<String> getActivityIds(String userId) throws PhotArkSocialException {
        List<String> activityIdsList = new ArrayList<String>();
        Node activityRootNode = getActivityRootNode(repositoryManager, userId, true);
        NodeIterator activityNodes;
        try {
            activityNodes = activityRootNode.getNodes();
            while (activityNodes.hasNext()) {
                activityIdsList.add(activityNodes.nextNode().getName());
            }
        } catch (RepositoryException e) {
            logger.log(Level.SEVERE,
                       "Error retrieving activity IDs for user: " + userId + " from the repository :" + e.getMessage(),
                       e);
            throw new PhotArkSocialException("Error retrieving activity IDs for user: " + userId
                + " from the repository :"
                + e.getMessage(), e);
        }
        return activityIdsList;
    }
}
