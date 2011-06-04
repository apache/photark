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

package org.apache.photark.services;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.photark.Album;
import org.apache.photark.AlbumList;
import org.oasisopen.sca.annotation.Remotable;

/**
 * Gallery facade service
 *
 * @version $Rev$ $Date$
 */
@Remotable
public interface GalleryService {

    /**
     * Retrieve a list of all Albums from the Gallery
     * @return
     */
    @GET
    @Path("/albums")
    @Produces(MediaType.APPLICATION_JSON)
    AlbumList getAlbums() throws PhotarkRuntimeException;

    /**
     * Retrieve a specific album using the albumId as key
     * @param albumId album key
     * @return
     * @throws PhotarkRuntimeException
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/albums/{id}")
    Album getAlbum(@PathParam("id") String albumId) throws PhotarkRuntimeException;

    /**
     * Create a new album
     * @param newAlbum the album to be created
     * @throws PhotarkRuntimeException
     */
    @POST
    @Path("/albums")
    @Consumes(MediaType.APPLICATION_JSON)
    void addAlbum(Album newAlbum) throws PhotarkRuntimeException;

    /**
     * Update an existing album
     * @param album the album to be updated
     * @throws PhotarkRuntimeException
     */
    @PUT
    @Path("/albums/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    void updateAlbum(@PathParam("id") String albumId, Album album) throws PhotarkRuntimeException;

    /**
     * Delete an existing album
     * @param albumId the album id
     * @throws PhotarkRuntimeException
     */
    @DELETE
    @Path("/albums/{id}")
    void removeAlbum(@PathParam("id") String albumId) throws PhotarkRuntimeException;
}
