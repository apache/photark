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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.photark.social.Person;
import org.apache.photark.social.PhotArkSocialException;
import org.oasisopen.sca.annotation.Remotable;

@Remotable
public interface PersonService {
	/**
	 * Persists the Person data object
	 * 
	 * @param personId
	 *            ID of the Person data object
	 * @param person
	 *            Person data object
	 * @throws PhotArkSocialException
	 */
	@POST
	@Path("/people/{userId}")
	@Consumes(MediaType.APPLICATION_JSON)
	void savePerson(@PathParam("userId") String personId, Person person)
			throws PhotArkSocialException;

	/**
	 * Updates the Person data object with updated fields
	 * 
	 * @param personId
	 *            ID of the Person data object
	 * @param person
	 *            Person data object with filed to update
	 * @throws PhotArkSocialException
	 */
	@PUT
	@Path("/people/{userId}")
	@Consumes(MediaType.APPLICATION_JSON)
	void updatePerson(@PathParam("userId") String personId, Person person)
			throws PhotArkSocialException;

	/**
	 * Removes a Person data object from persistence
	 * 
	 * @param personId
	 *            ID of the Person data object to be removed
	 * @throws PhotArkSocialException
	 */

	@DELETE
	@Path("/people/{userId}")
	void removePerson(@PathParam("userId") String personId)
			throws PhotArkSocialException;

	/**
	 * Retrieves the Person data object for the given ID
	 * 
	 * @param personId
	 *            ID of the Person data object to be retrieved
	 * @return the Person data object for the given ID
	 * @throws PhotArkSocialException
	 */

	/*@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/people/{userId}/@self")
	Person getPerson(@PathParam("userId") String personId)
			throws PhotArkSocialException;*/

	/**
	 * Retrieves the Person data object for the given ID with specified fields
	 * 
	 * @param personId
	 *            personId ID of the Person data object to be retrieved
	 * @param fields
	 *            the fields of the Person data object to be retrieved
	 * @return the Person data object for the given ID with the specified fields
	 * @throws PhotArkSocialException
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/people/{userId}/self")
	Person getPerson(@PathParam("userId") String personId)
			throws PhotArkSocialException;

	/**
	 * Retrieves array of Person data objects for the given person IDs and group
	 * 
	 * @param personIds
	 *            String array of IDs of the persons
	 * @param groupId
	 *            the ID of the group the persons belongs to; optional
	 * @param fields
	 *            the fields of the Person data object to be retrieved
	 * @return an array of Person data objects
	 * @throws PhotArkSocialException
	 */
	/*@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/people/{userId}/@friends")
	Person[] getPeople(String[] personIds, String groupId, String[] fields)
			throws PhotArkSocialException;*/

}
