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

import static org.apache.photark.security.utils.Constants.ACCESS_LIST;
import static org.apache.photark.security.utils.Constants.ALBUM_ADD_IMAGES_PERMISSION;
import static org.apache.photark.security.utils.Constants.ALBUM_CREATE_PERMISSION;
import static org.apache.photark.security.utils.Constants.ALBUM_DELETE_IMAGES_PERMISSION;
import static org.apache.photark.security.utils.Constants.ALBUM_EDIT_ALBUM_DESCRIPTION_PERMISSION;

import java.io.BufferedInputStream;
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
import org.apache.photark.jcr.JCRRepositoryManager;
import org.apache.photark.jcr.util.ArchiveFileExtractor;
import org.apache.photark.security.authorization.AccessList;
import org.apache.photark.security.authorization.services.AccessManager;
import org.apache.photark.services.album.Album;
import org.apache.photark.services.gallery.Gallery;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Reference;
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

    private JCRRepositoryManager repositoryManager;

    private  static AccessManager accessManager;

    private ServletFileUpload upload;

    private Gallery gallery;

    /**
     * Initialize the component.
     */
    @Init
    public void initialize() throws IOException {
        upload = new ServletFileUpload(new DiskFileItemFactory());
        upload.setSizeMax(MAX_UPLOAD_ZIP_IN_MEGS * 1024 * 1024);
    }

    public JCRImageUploadServiceImpl() {

    }

    @Reference(name="repositoryManager")
    protected void setRepositoryManager(JCRRepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    @Reference(name="accessmanager")
    protected void setAccessService(AccessManager accessManager) {
        this.accessManager = accessManager;
    }

    @Reference(name="gallery")
    protected void setGallery(Gallery gallery) {
        this.gallery = gallery;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.write("<html><body><h1>Photark Upload Service</h1></body></html>");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        boolean isMultipartContent = ServletFileUpload.isMultipartContent(request);
        if (!isMultipartContent) {
            try {
                String albumName ="";
                String albumDescription ="";
                //StringBuffer sb = new StringBuffer();
                albumName=  (String) request.getParameter("albumName");
                albumDescription=  (String) request.getParameter("addAlbumDesc");

                if(albumDescription!=null){
                    PrintWriter out = response.getWriter();
                    if(addDescToAlbum(albumName,albumDescription,request)){
                        out.write("albumDescription updated in " + albumName+" with "+albumDescription);
                    }else{
                        out.write("No permission to add albumDescription in " + albumName);
                    }
                    out.close();
                    return;
                }else{
                    return;
                }
            } catch (Exception e) {
                logger.info("Error adding albumDesc : " + e.getMessage());
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error adding albumDesc : " + e.getMessage());
            }
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
            String albumDescription= "";
            StringBuffer sb = new StringBuffer();
            while (fileItems.hasNext()) {
                FileItem fileItem = fileItems.next();
                if (fileItem.getFieldName().equalsIgnoreCase("albumName")) {
                    albumName = fileItem.getString();
                }

                if (fileItem.getFieldName().equalsIgnoreCase("albumDescription")) {
                    albumDescription = fileItem.getString();
                }

                if (fileItem.getFieldName().equalsIgnoreCase("securityToken")&&request.getSession().getAttribute(ACCESS_LIST)==null) {
                    request.getSession().setAttribute(ACCESS_LIST, accessManager.getAccessListFromSecurityToken(fileItem.getString())) ;
                }

                boolean isFormField = fileItem.isFormField();

                if (!isFormField) {
                    String fileName = fileItem.getName();

                    if(logger.isLoggable(Level.INFO)) {
                        logger.log(Level.INFO, "fileName:"+fileName);
                    }


                    InputStream inStream = new BufferedInputStream(fileItem.getInputStream());
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
                        addPictureToAlbum(albumName,albumDescription, picture,request);  //todo
                    }
                    sb.append("file=uploaded/" + fileName);
                    sb.append(",name=" + fileName);
                    //sb.append(",error=Not recognized file type");
                }
            }

            PrintWriter out = response.getWriter();
            out.write(sb.toString());

        } catch (FileUploadException e) {
            logger.info("Error uploading file : " + e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error uploading file : " + e.getMessage());
        } catch (Exception e) {
            logger.info("Error uploading file : " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error uploading file : " + e.getMessage());
        }
    }



    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        String albumName ="";
        String imageName ="";
        //StringBuffer sb = new StringBuffer();
        albumName=  (String) request.getParameter("albumName");
        imageName=  (String) request.getParameter("imageName");


        deleteNode(albumName, imageName,request);   //todo
        PrintWriter out = response.getWriter();

        //sb.append("deleted " + albumName+"/"+imageName);
        //out.write(sb.toString());
        out.write("deleted " + albumName+"/"+imageName);
        out.close();

    }

    /**
     * @param albumName String
     * @param albumDescription Picture
     * @param image albumDescription
     */
    private void addPictureToAlbum(String albumName, String albumDescription, Image image, HttpServletRequest request) {
        Album album = new JCRAlbumImpl(repositoryManager, albumName);
        String userId= ((AccessList) request.getSession().getAttribute(ACCESS_LIST)).getUserId();
        if (!gallery.hasAlbum(albumName)) {
            if (accessManager.isPermitted(userId, albumName, new String[]{ALBUM_CREATE_PERMISSION})){
                gallery.addAlbum(albumName);
                album.addOwner(userId);
            }
        }


        if (accessManager.isPermitted(userId, albumName, new String[]{ALBUM_ADD_IMAGES_PERMISSION})) {
            album.addPicture(image);
            this.gallery.imageAdded(albumName, image);
        }
        if (accessManager.isPermitted(userId, albumName, new String[]{ALBUM_EDIT_ALBUM_DESCRIPTION_PERMISSION})) {
            album.setDescription(albumDescription);
        }

    }

    /**
     * @param albumName the name of the album
     * @param albumDescription the album description that need to be added
     * @param request the HttpServletRequest
     * @return boolean
     */
    private boolean addDescToAlbum(String albumName, String albumDescription, HttpServletRequest request) {
        //   AccessList accessList= jsonSecurityManager.getAccessListFromUserId(((AccessList) request.getSession().getAttribute(ACCESS_LIST)).getUserId());

        if (accessManager.isPermitted(((AccessList) request.getSession().getAttribute(ACCESS_LIST)).getUserId(), albumName, new String[]{ALBUM_EDIT_ALBUM_DESCRIPTION_PERMISSION})) {
            gallery.addAlbum(albumName);
            Album album = new JCRAlbumImpl(repositoryManager, albumName);
            album.setDescription(albumDescription);
            if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, "album description updated in " + albumName + " with " + albumDescription);
            }
            return true;
        }
        return false;
    }

    /**
     *
     * @param  albumName
     * @param  imageName
     */
    private void deleteNode(String albumName, String imageName, HttpServletRequest request) {
        String userId= ((AccessList) request.getSession().getAttribute(ACCESS_LIST)).getUserId();
        if(imageName==null){
            if (accessManager.isPermitted(userId, albumName, null)) {

                gallery.deleteAlbum(albumName);
            }
        }else{
            if (accessManager.isPermitted(userId, albumName, new String[]{ALBUM_DELETE_IMAGES_PERMISSION})) {

                Album album = new JCRAlbumImpl(repositoryManager, albumName);
                album.deletePicture(imageName);
            }
        }
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
            logger.info("File is not an archive");
        }
        return false;
    }
}