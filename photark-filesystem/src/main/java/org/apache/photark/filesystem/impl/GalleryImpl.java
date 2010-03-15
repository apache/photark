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
import java.net.URL;

import org.apache.photark.services.album.Album;
import org.apache.photark.services.gallery.AbsGalleryImpl;
import org.apache.photark.services.gallery.Gallery;
import org.oasisopen.sca.annotation.Init;

public class GalleryImpl extends AbsGalleryImpl implements Gallery {
   
	public GalleryImpl(){
		
	}
	
	public GalleryImpl(String name){
		super(name);
	}
	
	
	@Init
	public void init() {
		System.out.println(">>> Initializing fileSystem Gallery");
		try {
			URL galleryURL = this.getClass().getClassLoader().getResource(name);
			if(galleryURL == null) {
				// Accomodate for J2EE classpath that starts in WEB-INF\classes
				galleryURL = this.getClass().getClassLoader().getResource("../../" + name);
			}
			if(galleryURL == null) {
				// Workaroud for Google apps Engine 
				String galleryDir = System.getProperty("user.dir") + "/"  + name;
				galleryURL = new URL("file://" + galleryDir);
			}

			if(galleryURL != null) {
				File album = new File(galleryURL.toURI());
				if (album.isDirectory() && album.exists()) {
					File[] albums = album.listFiles();
					for(File albumFile : albums) {
						if(! albumFile.getName().startsWith(".")) {
							if(albumFile.isDirectory() && albumFile.exists()) {
								Album newAlbum = new org.apache.photark.filesystem.impl.AlbumImpl();
								newAlbum.setName(albumFile.getName());
								newAlbum.setLocation(albumFile.getPath());
								this.albums.add(newAlbum);
							}                                
						}
					}
				}
			}
		} catch (Exception e) {
			// FIXME: ignore for now
			e.printStackTrace();
		}
		initialized = true;
	}
	
	public void addAlbum(String albumName){
		 
	}
}