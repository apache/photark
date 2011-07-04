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

package org.apache.photark.social.message;

import java.util.List;
import java.util.Set;

import org.apache.photark.social.exception.PhotArkSocialException;
import org.apache.photark.social.util.FilterOptions;

public interface MessageManager {

    /**
     * Posts a message to the user's specified message collection, to be sent to
     * the set of recipients specified in the message
     * 
     * @param userId The id of the user posting the message
     * @param msgCollId The message collection Id to post to, default @outbox
     * @param message he message to post
     * @throws PhotArkSocialException
     */
    void createMessage(String userId, String msgCollId, Message message) throws PhotArkSocialException;

    /**
     * Creates a new message collection for the given arguments
     * 
     * @param userId The userId to create the message collection for
     * @param messageColl A message collection that is to be created
     * @throws PhotArkSocialException
     */

    void createMessageCollection(String userId, MessageCollection messageColl) throws PhotArkSocialException;

    /**
     * Deletes a message collection for the given arguments
     * 
     * @param userId The userId to create the message collection for
     * @param msgCollId ID of the message collection to be deleted
     * @throws PhotArkSocialException
     */
    void deleteMessageCollection(String userId, String msgCollId) throws PhotArkSocialException;

    /**
     * Deletes a set of messages for a given user/message collection
     * 
     * @param userId The id of the user to delete for
     * @param msgCollId The Message Collection ID to delete from, default @all
     * @param msgIds List of IDs to delete
     * @throws PhotArkSocialException
     */
    void deleteMessages(String userId, String msgCollId, List<String> msgIds) throws PhotArkSocialException;

    /**
     * Returns a list of message collections corresponding to the given user
     * 
     * @param userId The ID of user to fetch for
     * @param fileds The fields to fetch for the message collections
     * @param filters Pagination options etc
     * @return a list of message collections corresponding to the given user
     * @throws PhotArkSocialException
     */
    List<MessageCollection> getMessageCollections(String userId, Set<String> fileds, FilterOptions filters)
        throws PhotArkSocialException;

    /**
     * Returns a list of messages that correspond to the passed in data
     * 
     * @param userId The ID of user to fetch for
     * @param msgCollId The message Collection ID to fetch from, default @all
     * @param fields The fields to fetch for the messages
     * @param msgIds An explicit set of message ids to fetch
     * @param filters Options to control the fetch
     * @return a list of messages that correspond to the passed in data
     * @throws PhotArkSocialException
     */
    List<Message> getMessages(String userId,
                              String msgCollId,
                              Set<String> fields,
                              List<String> msgIds,
                              FilterOptions filters) throws PhotArkSocialException;

    /**
     * Modifies/Updates a specific message with new data
     * 
     * @param userId The of the user to modify for
     * @param msgCollId The Message Collection ID to modify from, default @all
     * @param msgId The messageId to modify
     * @param message The message details to modify
     * @throws PhotArkSocialException
     */
    void modifyMessage(String userId, String msgCollId, String msgId, Message message) throws PhotArkSocialException;

    /**
     * Modifies/Updates a message collection for the given arguments
     * 
     * @param userId The userId to modify the message collection for
     * @param messageCollection Data for the message collection to be modified
     * @throws PhotArkSocialException
     */
    void modifyMessageCollections(String userId, MessageCollection messageCollection) throws PhotArkSocialException;

}
