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
package org.apache.photark.services.gallery;

import org.apache.photark.Image;
import org.apache.photark.services.album.Album;
import org.oasisopen.sca.annotation.Remotable;

@Remotable
public interface RemoteGallery {

    void addAlbum(String albumName, String albumType);

    boolean hasAlbum(String albumName, String type);

    void deleteAlbum(String albumName, String type);

    String[] getAlbumPictures(String albumName, String type);

    String[] getAlbumPicturesToUser(String albumName, String securityToken, String type);

    String getAlbumCover(String albumName, String type);

    String getAlbumCoverToUser(String albumName, String securityToken, String type);

    Album[] getAlbums(String type);

    Album[] getAlbumsToUser(String securityToken, String type);

    Album[] getAlbumsToSetPermission(String securityToken);

    void imageAdded(String albumName, Image image);

    void imageRemoved(String albumName, Image image);

}