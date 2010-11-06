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

package org.apache.photark.providers;

import java.util.List;

import org.apache.photark.AlbumConfig;
import org.apache.photark.Image;
import org.apache.photark.services.PhotarkRuntimeException;

public interface PhotoStreamProvider {

    String getProviderType();

    List<Image> getImages(AlbumConfig album) throws PhotarkRuntimeException;

    Image getImage(AlbumConfig album, String Id) throws PhotarkRuntimeException;

    String addImage(AlbumConfig album, Image image) throws PhotarkRuntimeException;

    void updateImage(AlbumConfig album, Image image)  throws PhotarkRuntimeException;

    void deleteImage(AlbumConfig album, String Id) throws PhotarkRuntimeException;
}
