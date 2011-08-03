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
package org.apache.photark.face.services.beans;

import java.io.Serializable;
import java.util.List;

public class PhotarkPhoto implements Serializable {

    private String pid;
    private String url;
    private int faceCount;
    private PhotArkFace photArkFace;
    private List<PhotArkFace> photArkFaces;

    public PhotarkPhoto(String pid, String url, int faceCount) {
        this.pid = pid;
        this.url = url;
        this.faceCount = faceCount;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setFaceCount(int faceCount) {
        this.faceCount = faceCount;
    }

    public void setPhotArkFace(PhotArkFace photArkFace) {
        this.photArkFace = photArkFace;
    }

    public void setPhotArkFaces(List<PhotArkFace> photArkFaces) {
        this.photArkFaces = photArkFaces;
    }

    public String getPid() {
        return pid;
    }

    public String getUrl() {
        return url;
    }

    public int getFaceCount() {
        return faceCount;
    }

    public PhotArkFace getPhotArkFace() {
        return photArkFace;
    }

    public List<PhotArkFace> getPhotArkFaces() {
        return photArkFaces;
    }

}
