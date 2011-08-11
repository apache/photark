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

import org.apache.photark.social.Activity;
import org.apache.photark.social.PhotArkSocialException;
import org.apache.photark.social.util.FilterOptions;
import org.oasisopen.sca.annotation.Remotable;

@Remotable
public interface ActivityService {

	/**
	 * creates an Activity data object with the given fields
	 * 
	 * @param userId
	 *            ID of the user to whom the activity belongs to
	 * @param groupId
	 *            optional; ID of the group if this activity belongs to a group
	 * @param fields
	 *            attributes of the activity
	 * @return an Activity data object
	 * @throws PhotArkSocialException
	 */
	Activity createActivity(String userId, String groupId, Set<String> fields)
			throws PhotArkSocialException;

	/**
	 * persists the given Activity data object for the given user
	 * 
	 * @param userId
	 *            ID of the user to whom the activity belongs to
	 * @param activity
	 *            the Activity data object to persists
	 * @throws PhotArkSocialException
	 */
	@POST
	@Path("/people/{userId}/activities")
	@Consumes(MediaType.APPLICATION_JSON)
	void saveActivity(@PathParam("userId") String userId, Activity activity)
			throws PhotArkSocialException;

	/**
	 * updates a persisted Activity data object
	 * 
	 * @param userId
	 *            ID of the user to whom the activity belongs to
	 * @param activity
	 *            the Activity data object to update
	 * @throws PhotArkSocialException
	 */
	@PUT
	@Path("/people/{userId}/activities")
	@Consumes(MediaType.APPLICATION_JSON)
	void updateActivity(@PathParam("userId") String userId, Activity activity)
			throws PhotArkSocialException;

	/**
	 * removes a persisted Activity data object
	 * 
	 * @param userId
	 *            ID of the user to whom the activity belongs to
	 * @param activityId
	 *            ID of the activity to be removed
	 * @throws PhotArkSocialException
	 */
	@DELETE
	@Path("people/{userId}/activities/{activityId}")
	void deleteActivity(@PathParam("userId") String userId,
			@PathParam("activityId") String activityId)
			throws PhotArkSocialException;

	/**
	 * removes a persisted Activity data objects for given activityIds
	 * 
	 * @param userId
	 *            ID of the user to whom the activities belongs to
	 * @param groupId
	 *            optional; ID of the group if this activity belongs to a group
	 * @param activityIds
	 *            Set of activityIds to be deleted
	 * @throws PhotArkSocialException
	 */
	void deleteActivities(String userId, String groupId, Set<String> activityIds)
			throws PhotArkSocialException;

	/**
	 * Retrieves array of Activity data objects matching the given input
	 * parameters
	 * 
	 * @param userIds
	 *            IDs of the users the activities belongs to
	 * @param groupId
	 *            optional; ID of the group, the activities belongs to
	 * @param fields
	 *            optional; fields of the activities to be returned
	 * @param filters
	 *            optional; any filter options
	 * @return array of Activity data objects
	 * @throws PhotArkSocialException
	 */
	Activity[] getActivities(String[] userIds, String groupId,
			Set<String> fields, FilterOptions filters)
			throws PhotArkSocialException;

	/**
	 * Retrieves array of Activity data objects matching the given input
	 * parameters
	 * 
	 * @param userIds
	 *            IDs of the users the activities belongs to
	 * @param groupId
	 *            optional; ID of the group, the activities belongs to
	 * @param fields
	 *            optional; fields of the activities to be returned
	 * @param filters
	 *            optional; any filter options
	 * @param activityIds
	 *            array of activityIds
	 * @return array of Activity data objects
	 * @throws PhotArkSocialException
	 */
	Activity[] getActivities(String userId, String groupId, Set<String> fields,
			FilterOptions filters, String[] activityIds)
			throws PhotArkSocialException;

	/**
	 * Retrieves an Activity data object matching the given input parameters
	 * 
	 * @param userIds
	 *            IDs of the users the activities belongs to
	 * @param groupId
	 *            optional; ID of the group, the activities belongs to
	 * @param fields
	 *            optional; fields of the activities to be returned
	 * @param activityId
	 *            ID of the Activity data object to be retrieved
	 * @return an Activity data object matching the given input parameters
	 * @throws PhotArkSocialException
	 */
	@GET
	@Path("/people/{userId}/activities/{activityId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Activity getActivity(@PathParam("userId") String userId,
			String groupId, Set<String> fields,
			@PathParam("activityId") String activityId)
			throws PhotArkSocialException;

}
