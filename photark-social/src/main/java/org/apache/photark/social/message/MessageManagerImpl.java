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

public class MessageManagerImpl implements MessageManager{

	public void createMessage(String userId, String msgCollId, Message message)
			throws PhotArkSocialException {
		// TODO Auto-generated method stub
		
	}

	public void createMessageCollection(String userId,
			MessageCollection messageColl) throws PhotArkSocialException {
		// TODO Auto-generated method stub
		
	}

	public void deleteMessageCollection(String userId, String msgCollId)
			throws PhotArkSocialException {
		// TODO Auto-generated method stub
		
	}

	public void deleteMessages(String userId, String msgCollId,
			List<String> msgIds) throws PhotArkSocialException {
		// TODO Auto-generated method stub
		
	}

	public List<MessageCollection> getMessageCollections(String userId,
			Set<String> fileds, FilterOptions filters)
			throws PhotArkSocialException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Message> getMessages(String userId, String msgCollId,
			Set<String> fields, List<String> msgIds, FilterOptions filters)
			throws PhotArkSocialException {
		// TODO Auto-generated method stub
		return null;
	}

	public void modifyMessage(String userId, String msgCollId, String msgId,
			Message message) throws PhotArkSocialException {
		// TODO Auto-generated method stub
		
	}

	public void modifyMessageCollections(String userId,
			MessageCollection messageCollection) throws PhotArkSocialException {
		// TODO Auto-generated method stub
		
	}

}
