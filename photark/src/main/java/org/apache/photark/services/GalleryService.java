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
    @Produces(MediaType.APPLICATION_JSON)
    AlbumList getAlbums();
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    Album getAlbum(@PathParam("id") String albumId);

    @POST
    void addAlbum(Album newAlbum) throws PhotarkRuntimeException;
    
    @PUT
    void updateAlbum(Album album) throws PhotarkRuntimeException;
    
    @DELETE
    void removeAlbum(String albumId) throws PhotarkRuntimeException;
    
}
