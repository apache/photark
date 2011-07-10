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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import org.apache.photark.services.PhotarkRuntimeException;
import org.apache.photark.services.jcr.JCRRepositoryManager;
import org.apache.photark.social.Message;
import org.apache.photark.social.MessageCollection;
import org.apache.photark.social.PhotArkSocialConstants;
import org.apache.photark.social.PhotArkSocialException;
import org.apache.photark.social.services.MessageService;
import org.apache.photark.social.util.FilterOptions;
import org.apache.photark.social.util.PhotArkSocialUtil;

public class JCRMessageServiceImpl implements MessageService {
    private JCRRepositoryManager repositoryManager;
    private static final Logger logger = Logger.getLogger(JCRMessageServiceImpl.class.getName());
    private static final String JCR_MESSAGE_ROOT_NODE = "message";
    private static final String NEXT_MESSAGE_ID = "nextMessageId";
    private boolean updateColl = false;

    public JCRMessageServiceImpl() {

    }

    public JCRMessageServiceImpl(JCRRepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    public void createMessage(String userId, String msgCollId, Message message) throws PhotArkSocialException {
        if (message == null) {
            throw new PhotArkSocialException("Unable to create message. Given message object is null");
        }
        Node messageNode = null;
        Node messageCollNode = null;
        int currentMessageId = -1;
        String messageId = null;
        try {
            messageCollNode = getMessageCollNode(repositoryManager, userId, msgCollId, true);
            if (message.getId() == null) {
                currentMessageId = (int)messageCollNode.getProperty(NEXT_MESSAGE_ID).getValue().getLong();
                messageId = currentMessageId + "";
                messageCollNode.setProperty(NEXT_MESSAGE_ID, (currentMessageId + 1) + "", PropertyType.LONG);
            } else {
                messageId = message.getId();
            }
            messageNode = getMessageNode(repositoryManager, userId, msgCollId, messageId, true);
            messageNode = convertMessageObjToNode(message, messageNode);
            repositoryManager.getSession().save();
        } catch (RepositoryException e) {
            logger.log(Level.SEVERE, "Error creating message  in photark repository for message ID: " + messageId
                + " of message collection ID: "
                + msgCollId
                + " for user ID: "
                + userId
                + e.getMessage(), e);
            throw new PhotArkSocialException(
                                             "Error creating message  in photark repository for message ID: " + messageId
                                                 + " of message collection ID: "
                                                 + msgCollId
                                                 + " for user ID: "
                                                 + userId
                                                 + e.getMessage(), e);
        }
    }

    public void createMessageCollection(String userId, MessageCollection messageColl) throws PhotArkSocialException {
        if (messageColl == null) {
            throw new PhotArkSocialException(
                                             "Unable to create message collection. Given message collection object is null");
        }
        Node messageRootNode = null;
        Node messageCollNode = null;
        try {
            messageRootNode = getMessageRootNode(repositoryManager, userId, true);
            if (updateColl == false && messageRootNode.hasNode(messageColl.getId())) {
                throw new PhotArkSocialException("Message collection with ID: " + messageColl.getId()
                    + " already exists");
            }
            if (!updateColl) {
                messageRootNode.addNode(messageColl.getId());
                repositoryManager.getSession().save();
            }
            updateColl = false;
            messageCollNode = messageRootNode.getNode(messageColl.getId());
            // add property
            messageCollNode = convertMessageCollObjToNode(messageColl, messageCollNode);
            messageCollNode.setProperty(NEXT_MESSAGE_ID, 0 + "", PropertyType.LONG);
            repositoryManager.getSession().save();
        } catch (RepositoryException e) {
            logger
                .log(Level.SEVERE,
                     "Error creating message collection in photark repository for message Collection ID: " + messageColl
                         .getId() + " of user ID: " + userId + e.getMessage(),
                     e);
            throw new PhotArkSocialException(
                                             "Error creating message collection in photark repository for message Collection ID: " + messageColl.getId()
                                                 + " of user ID: "
                                                 + userId
                                                 + e.getMessage(), e);
        }
    }

    public void deleteMessageCollection(String userId, String msgCollId) throws PhotArkSocialException {
        Node messageRootNode = null;
        Node messageCollNode = null;
        try {
            messageRootNode = getMessageRootNode(repositoryManager, userId, false);
            if (messageRootNode != null && messageRootNode.hasNode(msgCollId)) {
                messageCollNode = messageRootNode.getNode(msgCollId);
                messageCollNode.remove();
            }
            repositoryManager.getSession().save();
        } catch (RepositoryException e) {
            logger
                .log(Level.SEVERE,
                     "Error removing message collection in photark repository for message Collection ID: " + msgCollId
                         + " of user ID: "
                         + userId
                         + e.getMessage(),
                     e);
            throw new PhotArkSocialException(
                                             "Error removing message collection in photark repository for message Collection ID: " + msgCollId
                                                 + " of user ID: "
                                                 + userId
                                                 + e.getMessage(), e);
        }

    }

    public void deleteMessages(String userId, String msgCollId, List<String> msgIds) throws PhotArkSocialException {
        Node messageNode = null;
        try {
            for (String id : msgIds) {
                messageNode = getMessageNode(repositoryManager, userId, msgCollId, id, false);
                if (messageNode != null) {
                    messageNode.remove();
                }
            }
            repositoryManager.getSession().save();
        } catch (RepositoryException e) {
            logger.log(Level.SEVERE,
                       "Error removing messages in photark repository for message Collection ID: " + msgCollId
                           + " of user ID: "
                           + userId
                           + e.getMessage(),
                       e);
            throw new PhotArkSocialException(
                                             "Error removing messages in photark repository for message Collection ID: " + msgCollId
                                                 + " of user ID: "
                                                 + userId
                                                 + e.getMessage(), e);
        }
    }

    public List<MessageCollection> getMessageCollections(String userId, Set<String> fields, FilterOptions filters)
        throws PhotArkSocialException {
        Node messageRootNode = null;
        List<MessageCollection> msgCollections = new ArrayList<MessageCollection>();
        MessageCollection msgCollObj = null;
        try {
            messageRootNode = getMessageRootNode(repositoryManager, userId, false);
            NodeIterator msgCollNodes = messageRootNode.getNodes();
            Node msgCollNode = null;
            while (msgCollNodes.hasNext()) { // iterate all message collections
                msgCollNode = msgCollNodes.nextNode();
                msgCollObj = convertNodeToMessageCollObj(msgCollNode);
                msgCollObj.setId(msgCollNode.getName());
                msgCollections.add(msgCollObj);
            }

        } catch (RepositoryException e) {
            logger.log(Level.SEVERE,
                       "Error retrieving message collections in photark repository for user ID: " + userId
                           + e.getMessage(),
                       e);
            throw new PhotArkSocialException(
                                             "Error removing message collection in photark repository for user ID: " + userId
                                                 + e.getMessage(), e);
        }

        return msgCollections;
    }

    public List<Message> getMessages(String userId,
                                     String msgCollId,
                                     Set<String> fields,
                                     List<String> msgIds,
                                     FilterOptions filters) throws PhotArkSocialException {
        List<Message> messageList = new ArrayList<Message>();
        Node messageNode = null;
        try {
            for (String id : msgIds) {
                messageNode = getMessageNode(repositoryManager, userId, msgCollId, id, false);
                if (messageNode != null) {
                    messageList.add(convertMessageNodeToMessageObj(messageNode));
                }
            }
        } catch (RepositoryException e) {
            logger.log(Level.SEVERE, "Error retrieving messages in collection ID: " + msgCollId
                + " in photark repository for user ID: "
                + userId
                + e.getMessage(), e);
            throw new PhotArkSocialException("Error retrieving messages in collection ID: " + msgCollId
                + " in photark repository for user ID: "
                + userId
                + e.getMessage(), e);
        }
        return messageList;
    }

    public void modifyMessage(String userId, String msgCollId, String msgId, Message message)
        throws PhotArkSocialException {
        message.setId(msgId);
        createMessage(userId, msgCollId, message);
    }

    public void modifyMessageCollections(String userId, MessageCollection messageCollection)
        throws PhotArkSocialException {
        updateColl = true;
        createMessageCollection(userId, messageCollection);

    }

    private static Node getMessageRootNode(JCRRepositoryManager repositoryManager, String username, boolean create)
        throws PhotarkRuntimeException {
        Session session = null;
        Node personRootNode = null;
        Node messageRootNode = null;
        try {
            session = repositoryManager.getSession();
            personRootNode = PhotArkSocialUtil.getPersonRootNode(repositoryManager, username, create);
            if (personRootNode.hasNode(JCR_MESSAGE_ROOT_NODE)) {
                messageRootNode = personRootNode.getNode(JCR_MESSAGE_ROOT_NODE);
            } else {
                if (create) {
                    messageRootNode = personRootNode.addNode(JCR_MESSAGE_ROOT_NODE);
                    session.save();
                }
            }
        } catch (LoginException e) {
            logger.log(Level.SEVERE, "Error loging in to photark repository :" + e.getMessage(), e);
            throw new PhotarkRuntimeException("Error loging in to photark repository  :" + e.getMessage(), e);
        } catch (RepositoryException e) {
            logger.log(Level.SEVERE, "Error retrieving message root node for user: " + username
                + " from the repository :"
                + e.getMessage(), e);
            throw new PhotarkRuntimeException("Error retrieving message root node for user: " + username
                + " from the repository :"
                + e.getMessage(), e);
        }

        return messageRootNode;
    }

    private Node getMessageCollNode(JCRRepositoryManager repositoryManager,
                                    String username,
                                    String msgCollId,
                                    boolean create) throws PhotarkRuntimeException {
        Session session = null;
        Node msgCollNode = null;
        Node messageRootNode = null;
        try {
            session = repositoryManager.getSession();
            messageRootNode = getMessageRootNode(repositoryManager, username, create);
            if (messageRootNode.hasNode(msgCollId)) {
                msgCollNode = messageRootNode.getNode(msgCollId);
            } else {
                if (create) {
                    msgCollNode = messageRootNode.addNode(msgCollId);
                    session.save();
                }
            }

        } catch (LoginException e) {
            logger.log(Level.SEVERE, "Error loging in to photark repository :" + e.getMessage(), e);
            throw new PhotarkRuntimeException("Error loging in to photark repository  :" + e.getMessage(), e);
        } catch (RepositoryException e) {
            logger.log(Level.SEVERE, "Error retrieving message collection node for collection ID: " + msgCollId
                + " for user: "
                + username
                + " from the repository :"
                + e.getMessage(), e);
            throw new PhotarkRuntimeException(
                                              "Error retrieving message collection node for collection ID: " + msgCollId
                                                  + " for user: "
                                                  + username
                                                  + " from the repository :"
                                                  + e.getMessage(), e);
        }

        return msgCollNode;
    }

    private Node getMessageNode(JCRRepositoryManager repositoryManager,
                                String username,
                                String msgCollId,
                                String messageId,
                                boolean create) throws PhotarkRuntimeException {
        Session session = null;
        Node messageNode = null;
        Node msgCollNode = null;
        try {
            session = repositoryManager.getSession();
            msgCollNode = getMessageCollNode(repositoryManager, username, msgCollId, create);
            if (msgCollNode.hasNode(messageId)) {
                messageNode = msgCollNode.getNode(messageId);
            } else {
                if (create) {
                    messageNode = msgCollNode.addNode(messageId);
                    session.save();
                }
            }
        } catch (LoginException e) {
            logger.log(Level.SEVERE, "Error loging in to photark repository :" + e.getMessage(), e);
            throw new PhotarkRuntimeException("Error loging in to photark repository  :" + e.getMessage(), e);
        } catch (RepositoryException e) {
            logger.log(Level.SEVERE, "Error retrieving message node for message ID: " + messageId
                + "of collection ID: "
                + msgCollId
                + " for user: "
                + username
                + " from the repository :"
                + e.getMessage(), e);
            throw new PhotarkRuntimeException("Error retrieving message node for message ID: " + messageId
                + "of collection ID: "
                + msgCollId
                + " for user: "
                + username
                + " from the repository :"
                + e.getMessage(), e);
        }

        return messageNode;
    }

    private Node convertMessageCollObjToNode(MessageCollection messageCollObj, Node messageCollNode)
        throws RepositoryException {
        if (messageCollObj.getTitle() != null) {
            messageCollNode.setProperty(PhotArkSocialConstants.MESSAGE_COLL_TITLE, messageCollObj.getTitle());
        }
        if (messageCollObj.getTotalCount() >= 0) {
            messageCollNode.setProperty(PhotArkSocialConstants.MESSAGE_COLL_TOTAL_COUNT,
                                        messageCollObj.getTotalCount() + "",
                                        PropertyType.LONG);
        }
        if (messageCollObj.getUnreadCount() >= 0) {
            messageCollNode.setProperty(PhotArkSocialConstants.MESSAGE_COLL_UNREAD_COUNT,
                                        messageCollObj.getUnreadCount() + "",
                                        PropertyType.LONG);
        }
        if (messageCollObj.getLastUpdated() != null) {
            messageCollNode.setProperty(PhotArkSocialConstants.MESSAGE_COLL_LAST_UPDATED, messageCollObj
                .getLastUpdated().getTime() + "", PropertyType.LONG);
        }
        if (messageCollObj.getUrls() != null && messageCollObj.getUrls().size() > 0) {
            String[] urls = new String[messageCollObj.getUrls().size()];
            messageCollNode.setProperty(PhotArkSocialConstants.MESSAGE_COLL_URLS, urls);
        }
        return messageCollNode;
    }

    private MessageCollection convertNodeToMessageCollObj(Node messageCollNode) throws RepositoryException {
        MessageCollection msgCollObj = new MessageCollection();
        if (messageCollNode.hasProperty(PhotArkSocialConstants.MESSAGE_COLL_TITLE)) {
            msgCollObj.setTitle(messageCollNode.getProperty(PhotArkSocialConstants.MESSAGE_COLL_TITLE).getValue()
                .getString());
        }
        if (messageCollNode.hasProperty(PhotArkSocialConstants.MESSAGE_COLL_TOTAL_COUNT)) {
            msgCollObj.setTotalCount((int)messageCollNode.getProperty(PhotArkSocialConstants.MESSAGE_COLL_TOTAL_COUNT)
                .getValue().getLong());
        }
        if (messageCollNode.hasProperty(PhotArkSocialConstants.MESSAGE_COLL_UNREAD_COUNT)) {
            msgCollObj.setUnreadCount((int)messageCollNode
                .getProperty(PhotArkSocialConstants.MESSAGE_COLL_UNREAD_COUNT).getValue().getLong());
        }
        if (messageCollNode.hasProperty(PhotArkSocialConstants.MESSAGE_COLL_LAST_UPDATED)) {
            msgCollObj.setLastUpdated(new Date(messageCollNode
                .getProperty(PhotArkSocialConstants.MESSAGE_COLL_LAST_UPDATED).getValue().getLong()));
        }
        if (messageCollNode.hasProperty(PhotArkSocialConstants.MESSAGE_COLL_URLS)) {
            Value[] urlValues = messageCollNode.getProperty(PhotArkSocialConstants.MESSAGE_COLL_URLS).getValues();
            List<String> urlList = new ArrayList<String>();
            for (Value val : urlValues) {
                urlList.add(val.getString());
            }
        }
        return msgCollObj;
    }

    private Node convertMessageObjToNode(Message msgObj, Node msgNode) throws RepositoryException {

        if (msgObj.getBody() != null) {
            msgNode.setProperty(PhotArkSocialConstants.MESSAGE_BODY, msgObj.getBody());
        }
        if (msgObj.getId() != null) {
            msgNode.setProperty(PhotArkSocialConstants.MESSAGE_ID, msgObj.getId());
        }
        if (msgObj.getInReplyTo() != null) {
            msgNode.setProperty(PhotArkSocialConstants.MESSAGE_IN_REPLY_TO, msgObj.getInReplyTo());
        }
        if (msgObj.getRecepients() != null && msgObj.getRecepients().size() > 0) {
            String[] recipientsArray = new String[msgObj.getRecepients().size()];
            msgNode.setProperty(PhotArkSocialConstants.MESSAGE_RECIPIENTS,
                                msgObj.getRecepients().toArray(recipientsArray));
        }
        if (msgObj.getReplies() != null && msgObj.getReplies().size() > 0) {
            String[] repliesArray = new String[msgObj.getReplies().size()];
            msgNode.setProperty(PhotArkSocialConstants.MESSAGE_REPLIES, msgObj.getReplies().toArray(repliesArray));
        }
        if (msgObj.getSenderId() != null) {
            msgNode.setProperty(PhotArkSocialConstants.MESSAGE_SENDER_ID, msgObj.getSenderId());
        }
        if (msgObj.getStatus() != null) {
            msgNode.setProperty(PhotArkSocialConstants.MESSAGE_STATUS, msgObj.getStatus());
        }
        if (msgObj.getTimeSent() != null) {
            msgNode.setProperty(PhotArkSocialConstants.MESSAGE_TIME_SENT,
                                msgObj.getTimeSent().getTime() + "",
                                PropertyType.LONG);
        }
        if (msgObj.getTitle() != null) {
            msgNode.setProperty(PhotArkSocialConstants.MESSAGE_TITLE, msgObj.getTitle());
        }
        if (msgObj.getUpdated() != null) {
            msgNode.setProperty(PhotArkSocialConstants.MESSAGE_UPDATED,
                                msgObj.getUpdated().getTime() + "",
                                PropertyType.LONG);
        }
        if (msgObj.getUrl() != null) {
            msgNode.setProperty(PhotArkSocialConstants.MESSAGE_URL, msgObj.getUrl());
        }
        return msgNode;
    }

    private Message convertMessageNodeToMessageObj(Node msgNode) throws RepositoryException {
        Message msgObj = new Message();
        if (msgNode.hasProperty(PhotArkSocialConstants.MESSAGE_BODY)) {
            msgObj.setBody(msgNode.getProperty(PhotArkSocialConstants.MESSAGE_BODY).getValue().getString());
        }
        if (msgNode.hasProperty(PhotArkSocialConstants.MESSAGE_IN_REPLY_TO)) {
            msgObj.setInReplyTo(msgNode.getProperty(PhotArkSocialConstants.MESSAGE_IN_REPLY_TO).getValue().getString());
        }
        if (msgNode.hasProperty(PhotArkSocialConstants.MESSAGE_RECIPIENTS)) {
            List<String> recipientsList = new ArrayList<String>();
            Value[] values = msgNode.getProperty(PhotArkSocialConstants.MESSAGE_RECIPIENTS).getValues();
            for (Value value : values) {
                recipientsList.add(value.getString());
            }
            msgObj.setRecepients(recipientsList);
        }
        if (msgNode.hasProperty(PhotArkSocialConstants.MESSAGE_REPLIES)) {
            List<String> repliesList = new ArrayList<String>();
            Value[] values = msgNode.getProperty(PhotArkSocialConstants.MESSAGE_REPLIES).getValues();
            for (Value value : values) {
                repliesList.add(value.getString());
            }
            msgObj.setReplies(repliesList);
        }
        if (msgNode.hasProperty(PhotArkSocialConstants.MESSAGE_SENDER_ID)) {
            msgObj.setSenderId(msgNode.getProperty(PhotArkSocialConstants.MESSAGE_SENDER_ID).getValue().getString());
        }
        if (msgNode.hasProperty(PhotArkSocialConstants.MESSAGE_STATUS)) {
            msgObj.setInReplyTo(msgNode.getProperty(PhotArkSocialConstants.MESSAGE_STATUS).getValue().getString());
        }
        if (msgNode.hasProperty(PhotArkSocialConstants.MESSAGE_TIME_SENT)) {
            msgObj.setTimeSent(new Date(msgNode.getProperty(PhotArkSocialConstants.MESSAGE_TIME_SENT).getValue()
                .getLong()));
        }
        if (msgNode.hasProperty(PhotArkSocialConstants.MESSAGE_TITLE)) {
            msgObj.setTitle(msgNode.getProperty(PhotArkSocialConstants.MESSAGE_TITLE).getValue().getString());
        }
        if (msgNode.hasProperty(PhotArkSocialConstants.MESSAGE_UPDATED)) {
            msgObj
                .setUpdated(new Date(msgNode.getProperty(PhotArkSocialConstants.MESSAGE_UPDATED).getValue().getLong()));
        }
        if (msgNode.hasProperty(PhotArkSocialConstants.MESSAGE_URL)) {
            msgObj.setUrl(msgNode.getProperty(PhotArkSocialConstants.MESSAGE_URL).getValue().getString());
        }

        return msgObj;
    }
}
