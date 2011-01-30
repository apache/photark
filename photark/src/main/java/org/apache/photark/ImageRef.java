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

package org.apache.photark;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

/**
 * Summary info for Images
 * @version $Rev$ $Date$
 */
public class ImageRef implements Serializable {

    private static final long serialVersionUID = -3663988501067415961L;

    private String title;
    private List<Link> links = new ArrayList<Link>();

    /**
     * Get image title
     * @return image title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set image title
     * @param title image title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get cover image reference
     * @return the cover image url reference
     */
    @XmlElement(name="link", namespace="http://www.w3.org/2005/Atom")
    public List<Link> getLinks() {
        return this.links;
    }


    @Override
    public String toString() {
        return "ImageRef [title=" + title + ", links=" + links + "]";
    }

    /**
     * Utility method to create an ImageRef from an Image
     * @param album
     * @return
     */
    public static ImageRef createImageRef(Image image) {
        ImageRef imageRef = new ImageRef();

        imageRef.setTitle(image.getTitle());
        imageRef.getLinks().add(Link.to("thumbnail", image.getThumbnailLocation()));
        imageRef.getLinks().add(Link.to("url", image.getLocation()));

        return imageRef;
    }
}
