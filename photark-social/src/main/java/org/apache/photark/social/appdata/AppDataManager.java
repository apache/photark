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

import java.util.Map;
import java.util.Set;

import org.apache.photark.social.exception.PhotArkSocialException;

public interface AppDataManager {

	/**
	 * Retrieve person data
	 * 
	 * @param userId
	 *            ID of the user the data belongs to
	 * @param groupId
	 *            optional; ID of the group the data belongs to
	 * @return map of name-value pairs
	 * @throws PhotArkSocialException
	 */
	Map<String, String> getPersonData(String userId, String groupId)
			throws PhotArkSocialException;

	/**
	 * 
	 * @param userIds	IDs of the users the data belong to
	 * @param groupId	optional; ID of the group the data belongs to
	 * @return	map of name-value pairs for each user
	 * @throws PhotArkSocialException
	 */
	Map<String, Map<String, String>> getPeopleData(String[] userIds,
			String groupId) throws PhotArkSocialException;

	/**
	 * 
	 * @param userId	ID of the user the data belongs to
	 * @param values 	map of name-value pairs
	 * @throws PhotArkSocialException
	 */
	void savePersonData(String userId, Map<String, String> values)
			throws PhotArkSocialException;

	/**
	 * 
	 * @param userId
	 * @param groupId
	 * @param fields
	 * @throws PhotArkSocialException
	 */

	void deletePersonData(String userId, String groupId,Set<String> fields) throws PhotArkSocialException;

	/**
	 * 
	 * @param userId
	 * @param groupId
	 * @param values
	 * @throws PhotArkSocialException
	 */
	void updatePersonData(String userId, String groupId, String appId,
			Set<String> fields, Map<String, String> values)
			throws PhotArkSocialException;
}
