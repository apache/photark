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

package org.apache.photark.filesystem.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.photark.Image;
import org.apache.photark.services.album.Album;
import org.apache.photark.services.album.ImageFilter;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Property;


public class AlbumImpl implements Album {
	private String name;
	private String location;
	private String description;    
	private boolean initialized;
	private List<String> pictures = new ArrayList<String>();

	@Init
	public void init() {
		try {
			/*
            URL albumURL = this.getClass().getClassLoader().getResource(name);
            if(albumURL == null) {
                // Accomodate for J2EE classpath that starts in WEB-INF\classes
                albumURL = this.getClass().getClassLoader().getResource("../../" + getLocation());
            }*/

			if(location != null) {
				File album = new File(location);
				if (album.isDirectory() && album.exists()) {
					String[] listPictures = album.list(new ImageFilter(".jpg"));
					for(String image : listPictures) {
						pictures.add(image);
					}
				}
			}
		} catch (Exception e) {
			// FIXME: ignore for now
			e.printStackTrace();
		}
	}

	public String getName() {
		return name;
	}

	@Property
	public void setName(String name) {
		this.name = name;
		this.location = null;
	}

	public String getLocation() {
		return location;
	}

	@Property
	public void setLocation(String location) {
		this.location = location;
	}

	public String[] getPictures() {
		if( ! initialized) {
			init();
		}
		String[] pictureArray = new String[pictures.size()];
		pictures.toArray(pictureArray);
		return pictureArray;
	}

	public void addPicture(Image picture){
		throw new UnsupportedOperationException("Not implemented");
	}

	public void deletePicture(Image picture){
		throw new UnsupportedOperationException("Not implemented");
	}

	public void deletePicture(String picture){
		throw new UnsupportedOperationException("Not implemented");
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description=description;
	}
}