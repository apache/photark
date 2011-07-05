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

package org.apache.photark.services.jcr;

import java.io.InputStream;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;

import org.apache.photark.services.ImageUploadService;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Scope;

@Scope("COMPOSITE")
public class JCRImageUploadService implements ImageUploadService {
    private static final Logger logger = Logger.getLogger(JCRImageUploadService.class.getName());

    private static String supportedImageTypes[] = {".jpg", ".jpeg", ".png", ".gif"};

    @Reference(name="repositoryManager")
    private JCRRepositoryManager repositoryManager;

    public void uploadImage(String albumId, InputStream imageStream) {
        
        System.out.println(">>> Inside upload image");

    }

    public Response getImage(String imageKey) {
        // TODO Auto-generated method stub
        return null;
    }


}
