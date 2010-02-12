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

package org.apache.photark.upload;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.photark.Picture;
import org.apache.photark.services.album.Album;
import org.apache.photark.services.album.jcr.AlbumImpl;

public class PhotoUploadServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	public static final long MAX_UPLOAD_ZIP_IN_MEGS = 30;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		
		boolean isMultipartContent = ServletFileUpload.isMultipartContent(request);
		if (!isMultipartContent) {
			return;
		}

		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax(MAX_UPLOAD_ZIP_IN_MEGS * 1024 * 1024);
		
		try {
			List<FileItem> fields = upload.parseRequest(request);
			System.out.println("Number of fields: " + fields.size());
			Iterator<FileItem> it = fields.iterator();
			
			if (!it.hasNext()) {
				System.out.println("No fields found");
				return;
			}
			
			String albumName = "";
			StringBuffer sb = new StringBuffer();
			while (it.hasNext()) {
				FileItem fileItem = it.next();
				
				if(fileItem.getFieldName().equalsIgnoreCase("albumname")){
					albumName = fileItem.getString();
				}
				boolean isFormField = fileItem.isFormField();				 
				
				if(!isFormField)
				{	
					String fileName = fileItem.getName();
					
					InputStream inStream = fileItem.getInputStream();
					
					FileUploader uploader = new FileUploader();
					List<Picture> pictures = uploader.uploadFile(new BufferedInputStream(inStream), fileName);
					
					for(Picture picture :pictures){
						addPictureToAlbum(albumName, picture);
					}
					sb.append("file=uploaded/" + fileName);
					sb.append(",name="+fileName);
					sb.append(",error=Not recognized file type");
				}
			}
			PrintWriter out = response.getWriter();
			out.write(sb.toString());

		} catch (FileUploadException e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
		catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 * @param albumName String
	 * @param picture Picture
	 */
	private void addPictureToAlbum(String albumName, Picture picture){
		Album album = new AlbumImpl(albumName);
		album.addPicture(picture);
	}
}