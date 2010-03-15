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

package org.apache.photark.jcr.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.photark.Image;
import org.apache.photark.jcr.util.ArchiveFileExtractor;
import org.apache.photark.services.album.Album;
import org.apache.photark.services.album.jcr.AlbumImpl;
import org.apache.photark.services.gallery.Gallery;
import org.apache.photark.services.gallery.jcr.GalleryImpl;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Scope;
import org.oasisopen.sca.annotation.Service;

/**
 * Servlet responsible for receiving image uploads
 * Album name is passed with the post, and should be created in case of new album 
 */
@Service(Servlet.class)
@Scope("COMPOSITE")
public class JCRImageUploadServiceImpl extends HttpServlet implements Servlet /*ImageUploadService*/ {
    private static final Logger logger = Logger.getLogger(JCRImageUploadServiceImpl.class.getName());

    private static final long serialVersionUID = -7842318322982743234L;
    public static final long MAX_UPLOAD_ZIP_IN_MEGS = 30;
    
    private String supportedImageTypes[] = {".jpg", ".jpeg", ".png", ".gif"};
    
    private ServletFileUpload upload;
    
    /**
     * Initialize the component.
     */
    @Init
    public void initialize() throws IOException {
        upload = new ServletFileUpload(new DiskFileItemFactory());
        upload.setSizeMax(MAX_UPLOAD_ZIP_IN_MEGS * 1024 * 1024);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        
        PrintWriter out = response.getWriter();
        out.write("<html><body><h1>Photark Upload Service</h1></body></html>");
    }


    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        boolean isMultipartContent = ServletFileUpload.isMultipartContent(request);
        if (!isMultipartContent) {
            return;
        }

        try {
            List<FileItem> fields = (List<FileItem>) upload.parseRequest(request);
            if(logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, "Number of fields: " + fields.size());
            }
            
            Iterator<FileItem> fileItems = fields.iterator();

            if (!fileItems.hasNext()) {
                if(logger.isLoggable(Level.INFO)) {
                    logger.log(Level.INFO, "No fields found");
                }
                return;
            }

            String albumName = "";
            StringBuffer sb = new StringBuffer();
            while (fileItems.hasNext()) {
                FileItem fileItem = fileItems.next();

                if (fileItem.getFieldName().equalsIgnoreCase("albumName")) {
                    albumName = fileItem.getString();
                }
                boolean isFormField = fileItem.isFormField();
                
                if (!isFormField) {
                    String fileName = fileItem.getName();
                    
                    if(logger.isLoggable(Level.INFO)) {
                        logger.log(Level.INFO, "fileName:"+fileName);
                    }

                    InputStream inStream = fileItem.getInputStream();
                    List<Image> pictures = new ArrayList<Image>();

                    if (isArchive(inStream)) {
                        ArchiveFileExtractor archiveFileExtractor = new ArchiveFileExtractor(supportedImageTypes);
                        pictures = archiveFileExtractor.extractArchive(inStream);
                    } else {
                        // this is a picture file and not the archive file
                        Image picture = new Image(fileName, new Date(), inStream);
                        pictures.add(picture);
                    }

                    for (Image picture : pictures) {
                        addPictureToAlbum(albumName, picture);
                    }
                    sb.append("file=uploaded/" + fileName);
                    sb.append(",name=" + fileName);
                    //sb.append(",error=Not recognized file type");
                }
            }
            PrintWriter out = response.getWriter();
            out.write(sb.toString());

        } catch (FileUploadException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @param albumName String
     * @param picture Picture
     */
    private void addPictureToAlbum(String albumName, Image image) {
    	Gallery gallery = new GalleryImpl();
    	gallery.addAlbum(albumName);
        Album album = new AlbumImpl(albumName);
        album.addPicture(image);
    }
    
    /**
     * Test whether this stream is of archive or not
     * 
     * @param inStream InputStream
     * @return boolean
     */
    private static boolean isArchive(InputStream inStream) {
        ArchiveStreamFactory streamFactory = new ArchiveStreamFactory();
        try {
            streamFactory.createArchiveInputStream(inStream);
            return true;
        } catch (ArchiveException e) {
            e.printStackTrace();
        }
        return false;
    }
}
