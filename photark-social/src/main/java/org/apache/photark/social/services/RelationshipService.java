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

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.photark.social.PhotArkSocialException;
import org.oasisopen.sca.annotation.Remotable;

@Remotable
public interface RelationshipService {

	public static final String RELATIONSHIP_FRIEND = "friend";
	public static final String RELATIONHIP_FRIEND_REQUEST_PENDING = "friend request pending";
	public static final String RELATIONHIP_FRIEND_REQUEST_RECEIVED = "friend request received";
	public static final String RELATIONHIP_NONE = "none";

	/**
	 * Returns the relationship status between two users
	 * 
	 * @param veiwer
	 *            ID of the person who views owner's pages
	 * @param owner
	 *            ID of the person whom the pages/resources belongs to
	 * @return one of {RELATIONSHIP_FRIEND,RELATIONHIP_FRIEND_REQUEST_PENDING,
	 *         RELATIONHIP_FRIEND_REQUEST_RECEIVED ,RELATIONHIP_NONE}
	 * @throws PhotArkSocialException
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/people/relationship/{ownerId}/{viewerId}")
	String getRelationshipStatus(@PathParam("viewerId") String veiwer,
			@PathParam("ownerId") String owner) throws PhotArkSocialException;

	/**
	 * Sends/creates a friend request from viewer to owner
	 * 
	 * @param viewer
	 *            ID of the person who requests for the relationship
	 * @param owner
	 *            ID of the person for whom viewer sends the relationship
	 *            request
	 * @return true if the request was successful; else false
	 * @throws PhotArkSocialException
	 */
	@POST
	@Path("/people/relationship/{ownerId}/{viewerId}/request")
	boolean requestRelationship(@PathParam("viewerId") String viewer,
			@PathParam("ownerId") String owner) throws PhotArkSocialException;

	/**
	 * Accepts a relationship request from owner
	 * 
	 * @param viewer
	 *            ID of the person who has sent the relationship request
	 * @param owner
	 *            ID of the person who has received the relationship request
	 *            from viewer
	 * @return true if a relationship was created between viewer and owner;
	 *         otherwise false
	 * @throws PhotArkSocialException
	 */
	@POST
	@Path("/people/relationship/{ownerId}/{viewerId}/accept")
	boolean acceptRelationshipRequest(@PathParam("viewerId") String viewer,
			@PathParam("ownerId") String owner) throws PhotArkSocialException;

	/**
	 * Ignores a relationship request
	 * 
	 * @param viewer
	 *            ID of the person who has sent the relationship request
	 * @param owner
	 *            ID of the person who has received the relationship request
	 *            from viewer
	 * @return true if the operation was successful
	 * @throws PhotArkSocialException
	 */
	@POST
	@Path("/people/relationship/{ownerId}/{viewerId}/ignore")
	boolean ignoreRelationship(@PathParam("viewerId") String viewer,
			@PathParam("ownerId") String owner) throws PhotArkSocialException;

	/**
	 * Removing the relationship - i.e: un-friending
	 * 
	 * @param owner
	 *            ID of the person with whom the viewer wants to remove the
	 *            relationship
	 * @param viewer
	 *            ID of the person who wants to remove the relationship with
	 *            owner
	 * @return true if the operation was successful
	 * @throws PhotArkSocialException
	 */
	@DELETE
	@Path("/people/realtionship/{ownerId}/{viewerId}")
	boolean removeRelationship(@PathParam("ownerId") String owner,
			@PathParam("viewerId") String viewer) throws PhotArkSocialException;

	/**
	 * Retrieves the list relationships the user has
	 * 
	 * @param loggedUser
	 *            ID of the user logged in
	 * @return array of Strings with the IDs of the persons who has relationship
	 *         with loggedUser
	 * @throws PhotArkSocialException
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/people/relationship/{ownerId}/list")
	String[] getRelationshipList(@PathParam("ownerId") String loggedUser)
			throws PhotArkSocialException;

	/**
	 * Retrieves the list of relationship requests
	 * 
	 * @param owner
	 *            ID of the person who has received the relationship requests
	 * @return array of Strings with the IDs of the persons who has sent
	 *         relationship requests to the owner
	 * @throws PhotArkSocialException
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/people/relationship/{ownerId}/pending")
	String[] getPendingRelationshipRequests(@PathParam("ownerId") String owner)
			throws PhotArkSocialException;
}