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

package org.apache.photark.services.album.jcr;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.photark.Picture;
import org.apache.photark.services.album.Album;
import org.apache.photark.services.album.ImageFilter;
import org.apache.photark.services.gallery.jcr.JCRSession;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Property;


public class AlbumImpl implements Album {
    private String gallery;
    private String name;
    private String location;
    private Session session=JCRSession.getSession();
    private boolean initialized;
    private static Map<String, Album> albums = new HashMap<String, Album>();
    
    public synchronized static Album createAlbum(String name)
    {
    	if (!albums.containsKey(name)) {
    		albums.put(name, new AlbumImpl(name));
		}
    	return albums.get(name);
    }

    public AlbumImpl(String name){
    	this.name = name;
    }
    
    @Init
    public void init() {
        System.out.println(">>> Initializing JCR Album");
        try {
            URL albumURL = this.getClass().getClassLoader().getResource(getLocation());
            if(albumURL == null){
            	String loc = "../../" + getLocation();
            	albumURL = this.getClass().getClassLoader().getResource(loc);
            }

            if(albumURL != null) {
                try {
                  File album = new File(albumURL.toURI());
                  if (album.isDirectory() && album.exists()) {
                      String[] listPictures = album.list(new ImageFilter(".jpg"));
                      if(listPictures !=null && listPictures.length > 0){
                    	  Node albumNode = getAlbumNode(name);                    	  
                    	  for(String image : listPictures) {
                    		  if(!albumNode.hasNode(image))
                    		  {
                    			  Node picNode=albumNode.addNode(image);
                    			  String imagePath = albumURL.getPath() + image;
                    			  InputStream imageContent = new FileInputStream(new File(imagePath));
                    			  picNode.setProperty("imageContent", imageContent );
                    			  picNode.setProperty("name", image);
                    			  picNode.setProperty("location", image);
                    		  }
                    	  }
                      }
                  }

                  session.save();
                }catch (Exception e){
                    // FIXME: ignore for now
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            // FIXME: ignore for now
            e.printStackTrace();
        }
        initialized = true;
    }
    

    @Property
    public void setGallery(String gallery) {
        this.gallery = gallery;
        this.location = null;
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
        if (location == null) {
            location = gallery + "/" + name + "/";
        }
        return location;

    }
    
    public void setLocation(String location) {
    	System.out.println("inside setLocation:location:"+location);
        this.location = location;
    }

    public synchronized String[] getPictures() {
    	if(!initialized){
    		init();
    	}
      List<String> pictures = new ArrayList<String>();
      try{
    	Node root = session.getRootNode();
    	Node albumNode = root.getNode(name);
        NodeIterator nodes = albumNode.getNodes();
        
        while(nodes.hasNext()){
        	Node node=nodes.nextNode();
        	if(node.getPath().equals("/jcr:system")) continue;
        	pictures.add(node.getProperty("location").getString());
        }
      }catch (Exception e) {
          // FIXME: ignore for now
          e.printStackTrace();
      }

      String[] pictureArray = new String[pictures.size()];
      pictures.toArray(pictureArray);
      return pictureArray;
    }


    public void removeNodes(){
      try{
    	Node root=session.getRootNode();
        NodeIterator nodes = root.getNodes();
        while(nodes.hasNext()){
        	Node node=nodes.nextNode();
        	if(node.getPath().equals("/jcr:system")) continue;
        	else node.remove();
        }
        session.save();
      }catch (Exception e) {
          // FIXME: ignore for now
          e.printStackTrace();
      }

    }
    
    public void addPicture(Picture picture){
    	try {
			Node root = session.getRootNode();
			Node albumNode = root.getNode(name);
			Node picNode = albumNode.addNode(picture.getName());
			picture.getInputStream();
			picNode.setProperty("imageContent", picture.getInputStream());
			picNode.setProperty("name", picture.getName());
			picNode.setProperty("location", picture.getName());
			session.save();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
    }
    
    public void deletePicture(Picture picture){
    	try {
			Node root = session.getRootNode();
			Node albumNode = root.getNode(name);
			Node picNode = albumNode.addNode(picture.getName());
			picNode.remove();
			session.save();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
    }
    
    
    /**
     * This method create new album node in case it does not exists in repository or return older album node otherwise.
     *  
     * @param albumName
     * @return
     * @throws RepositoryException
     */
    private Node getAlbumNode(String name) throws RepositoryException{
    	Node root = session.getRootNode();
  	  	if(root.hasNode(name))
  	  		return root.getNode(name);
  	  	else
  	  		 return root.addNode(name);
    }
}