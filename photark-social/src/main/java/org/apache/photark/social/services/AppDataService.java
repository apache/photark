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

package org.apache.photark.social.services;

import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.photark.social.PhotArkSocialException;
import org.oasisopen.sca.annotation.Remotable;

@Remotable
public interface AppDataService {

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
	@GET
	@Path("/people/{userId}/appData/{appId}")
	@Produces(MediaType.APPLICATION_JSON)
	Map<String, String> getPersonData(@PathParam("userId") String userId,
			String groupId) throws PhotArkSocialException;

	/**
	 * @param userIds
	 *            IDs of the users the data belong to
	 * @param groupId
	 *            optional; ID of the group the data belongs to
	 * @return map of name-value pairs for each user
	 * @throws PhotArkSocialException
	 */
	Map<String, Map<String, String>> getPeopleData(String[] userIds,
			String groupId) throws PhotArkSocialException;

	/**
	 * @param userId
	 *            ID of the user the data belongs to
	 * @param values
	 *            map of name-value pairs
	 * @throws PhotArkSocialException
	 */
	@POST
	@Path("/people/{userId}/appData/{appId}")
	@Consumes(MediaType.APPLICATION_JSON)
	void savePersonData(@PathParam("userId") String userId,
			Map<String, String> values) throws PhotArkSocialException;

	/**
	 * @param userId
	 * @param groupId
	 * @param fields
	 * @throws PhotArkSocialException
	 */
	@DELETE
	@Path("/people/{userId}/appdata/{appId}")
	void deletePersonData(@PathParam("userId") String userId, String groupId,
			Set<String> fields) throws PhotArkSocialException;

	/**
	 * @param userId
	 * @param groupId
	 * @param values
	 * @throws PhotArkSocialException
	 */
	@PUT
	@Path("/people/{userId}/appData/{appId}")
	@Consumes(MediaType.APPLICATION_JSON)
	void updatePersonData(@PathParam("userId") String userId, String groupId,
			String appId, Set<String> fields, Map<String, String> values)
			throws PhotArkSocialException;
}
