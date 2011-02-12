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

package org.apache.photark.filesystem.services;


public class FileSystemAlbumImpl implements org.apache.photark.services.album.Album {
	private String name;
	private String location;
	private String description;
	private boolean initialized;
	private java.util.List<String> pictures = new java.util.ArrayList<String>();

	@org.oasisopen.sca.annotation.Init
	public void init() {
		try {
			/*
            URL albumURL = this.getClass().getClassLoader().getResource(name);
            if(albumURL == null) {
                // Accomodate for J2EE classpath that starts in WEB-INF\classes
                albumURL = this.getClass().getClassLoader().getResource("../../" + getLocation());
            }*/

			if(location != null) {
				java.io.File album = new java.io.File(location);
				if (album.isDirectory() && album.exists()) {
					String[] listPictures = album.list(new org.apache.photark.services.album.ImageFilter(".jpg"));
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

	@org.oasisopen.sca.annotation.Property
	public void setName(String name) {
		this.name = name;
		this.location = null;
	}

	public String getLocation() {
		return location;
	}

	@org.oasisopen.sca.annotation.Property
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

	public void addPicture(org.apache.photark.Image picture){
		throw new UnsupportedOperationException("Not implemented");
	}

	public void deletePicture(org.apache.photark.Image picture){
		throw new UnsupportedOperationException("Not implemented");
	}

    public void addOwner(String owner) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public String[] getOwners() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
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

