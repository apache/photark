package org.apache.photark.jcr.services;

import org.apache.photark.services.gallery.GalleryListener;
import org.apache.photark.services.gallery.RemoteGallery;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.jcr.*;

import org.apache.photark.Image;
import org.apache.photark.jcr.JCRRepositoryManager;
import org.apache.photark.security.authorization.services.AccessManager;
import org.apache.photark.services.album.Album;
import org.apache.photark.services.gallery.BaseGalleryImpl;
import org.apache.photark.services.gallery.Gallery;
import org.oasisopen.sca.annotation.*;
import org.oasisopen.sca.annotation.Property;

import static org.apache.photark.security.utils.Constants.*;


/**
 * Created by IntelliJ IDEA.
 * User: subash
 * Date: Jan 27, 2011
 * Time: 4:43:03 PM
 * To change this template use File | Settings | File Templates.
 */
@Scope("COMPOSITE")
public class JCRRemoteGalleryImpl implements RemoteGallery {

    private static final Logger logger = Logger.getLogger(JCRRemoteGalleryImpl.class.getName());

    @Reference(required = false)
    public GalleryListener listeners;

    private JCRRepositoryManager repositoryManager;
    private AccessManager accessManager;
    private JCRAlbumAggregator albumAggregator;
    int i = 0;
    public String name = "";

    public JCRRemoteGalleryImpl() {

    }

    @Reference(name = "repositoryManager")
    protected void setRepositoryManager(JCRRepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
        initAlbumAggregator();
        initBaseJCRStructure();
    }

    @Property
    public void setName(String name) {
        this.name = name;
    }

    @AllowsPassByReference
    public void imageAdded(String albumName, Image image) {

        if (this.listeners != null) {

//            for (GalleryListener listener : listeners) {
//                listener.imageAdded(image);
//            }
            listeners.imageAdded(albumName, image);

        }

    }

    @AllowsPassByReference
    public void imageRemoved(String albumName, Image image) {

        if (this.listeners != null) {

//            for (GalleryListener listener : listeners) {
//                listener.imageRemoved(image);
//            }
            listeners.imageRemoved(albumName, image);

        }

    }

    private void initBaseJCRStructure() {
        try {
            Session session = repositoryManager.getSession();
            Node baseRoot = session.getRootNode();
            Node rootNode;

            if (!baseRoot.hasNode("albums")) {
                rootNode = baseRoot.addNode("albums");
            } else {
                rootNode = baseRoot.getNode("albums");
            }

            if (!rootNode.hasNode("remote")) {
                rootNode.addNode("remote");
            }

            if (!rootNode.hasNode("local")) {
                rootNode.addNode("local");
            }

        } catch (RepositoryException e) {
            e.printStackTrace();
        } finally {

        }

    }

    @Reference(name = "accessmanager")
    protected void setAccessService(AccessManager accessManager) {
        this.accessManager = accessManager;
    }

    private void initAlbumAggregator() {
        albumAggregator = new JCRAlbumAggregator(repositoryManager);
    }

    @Init
    public void init() {
        albumAggregator.refreshAlbums();
    }

    public void addAlbum(String albumName, String albumType) {

        try {

            Session session = repositoryManager.getSession();
            Node rootNode = session.getRootNode().getNode("albums");

            //adding albums to local JCR

            if ((albumType != null) && (albumType.equals("remote"))) {

                Node remote = rootNode.getNode("remote");

                if (remote.hasNode(albumName)) {
                    logger.info("This album is already in gallery");
                    return;

                }

                remote.addNode(albumName);
                session.save();

            } else if ((albumType != null) && (albumType.equals("local"))) {

                Node local = rootNode.getNode("local");

                if (local.hasNode(albumName)) {
                    logger.info("This album is already in gallery");
                    return;
                }

                local.addNode(albumName);
                session.save();

            }

            Album album = JCRAlbumImpl.createAlbum(repositoryManager, albumName, albumType);
            albumAggregator.put(album, albumType);

        } catch (RepositoryException e) {
            e.printStackTrace();
        } finally {

        }
    }


    private Node getAlbumNode(String type, String name) throws RepositoryException {

        Node albumNode = null;

        try {
            Session session = repositoryManager.getSession();
            Node root = session.getRootNode().getNode("albums");

            if (type.equals("local")) {

                albumNode = root.getNode("local").getNode(name);

            } else if (type.equals("remote")) {

                albumNode = root.getNode("remote").getNode(name);
            }

            session.save();
        }
        catch (PathNotFoundException e) {
            throw new PathNotFoundException();
        }
        catch (RepositoryException e) {
            throw new RepositoryException();
        }
        return albumNode;
    }

    public boolean hasAlbum(String albumName, String type) {

        boolean hasAlbum = true;
        try {
            Node rootNode = getAlbumNode(type, albumName);

        } catch (PathNotFoundException e) {
            hasAlbum = false;

        } catch (RepositoryException e) {
            hasAlbum = false;

        } finally {
            //repositoryManager.releaseSession();
        }
        return hasAlbum;
    }

    public void deleteAlbum(String albumName, String type) {
        try {
            Session session = repositoryManager.getSession();

            Node albumNode = getAlbumNode(type, albumName);
            Album album = JCRAlbumImpl.createAlbum(repositoryManager, albumName);
            albumAggregator.remove(album, type);
            albumNode.remove();
            session.save();
            logger.info("album " + albumName + " deleted");

        } catch (PathNotFoundException e) {
            logger.info("album " + albumName + " not found");
        }
        catch (RepositoryException e) {
            e.printStackTrace();
        } finally {
            //repositoryManager.releaseSession();
        }
    }

    public String[] getAlbumPictures(String albumName, String type) {
        return getAlbumPicturesToUser(albumName, accessManager.getSecurityTokenFromUserId(GUEST), type);
    }

    public String[] getAlbumPicturesToUser(String albumName, String securityToken, String type) {
        String[] permissions = new String[]{ALBUM_VIEW_IMAGES_PERMISSION};
        if (accessManager.isPermitted(accessManager.getUserIdFromSecurityToken(securityToken), albumName, permissions)) {
            Album albumLookup = getAlbumFromAggregator(albumName, type);
            if (albumLookup != null) {
                return albumLookup.getPictures();
            } else {
                // FIXME: return proper not found exception
                return new String[]{};
            }
        } else {
            return new String[]{};
        }
    }

    private Album getAlbumFromAggregator(String albumName, String type) {
        albumAggregator.refreshAlbums();
        Album album = null;
        if ((type != null) && (type.equals("local"))) {
            album = albumAggregator.getAllLocalAlbums().get(albumName);
        } else if ((type != null) && (type.equals("remote"))) {
            album = albumAggregator.getAllRemoteAlbums().get(albumName);
        }

        return album;
    }

    public String getAlbumCover(String albumName, String type) {
        return getAlbumCoverToUser(albumName, accessManager.getSecurityTokenFromUserId(GUEST), type);
    }

    public String getAlbumCoverToUser(String albumName, String securityToken, String type) {
        String[] permissions = new String[]{ALBUM_VIEW_IMAGES_PERMISSION};
        if (accessManager.isPermitted(accessManager.getUserIdFromSecurityToken(securityToken), albumName, permissions)) {
            Album albumLookup = getAlbumFromAggregator(albumName, type);

            if (albumLookup != null) {
                String[] pictures = albumLookup.getPictures();
                // this check is to avoid Exception
                if (pictures.length > 0) {
                    return albumLookup.getPictures()[0];
                } else {
                    logger.info("No Album Cover Picture found for album:" + albumName);
                    return null;
                }
            } else {
                // FIXME: return proper not found exception
                return null;
            }
        } else {
            return null;
        }
    }

    public Album[] getAlbums(String type) {
        return getAlbumsToUser(accessManager.getSecurityTokenFromUserId(GUEST), type);
    }

    public Album[] getAlbumsToUser(String securityToken, String type) {
        albumAggregator.refreshAlbums();
        List<Album> albumsN = null;
        List<Album> userAlbums = new ArrayList<Album>();

        if ((type != null) && (type.equals("local"))) {   // all albums of the specified type
            albumsN = albumAggregator.getLocalAlbumsAsList();

        } else if ((type != null) && ((type.equals("remote")))) {
            albumsN = albumAggregator.getRemoteAlbumsAsList();
        }

        for (Album album : albumsN) {
            String[] permissions = new String[]{ALBUM_VIEW_IMAGES_PERMISSION};
            if (accessManager.isPermitted(accessManager.getUserIdFromSecurityToken(securityToken), album.getName(), permissions)) {
                userAlbums.add(album);
            }
        }
        Album[] albumArray = new Album[userAlbums.size()];
        userAlbums.toArray(albumArray);
        return albumArray;
    }

    /*this method is used to get the albums, that the user can add to various roles*/

    public Album[] getAlbumsToSetPermission(String securityToken) {
        albumAggregator.refreshAlbums();
        List<Album> allAlbums = albumAggregator.getAllAlbumList();
        List<Album> userAlbums = new ArrayList<Album>();
        for (Album album : allAlbums) {
            // only the super admin and the album owner is allowed
            if (accessManager.isPermitted(accessManager.getUserIdFromSecurityToken(securityToken), album.getName(), null)) {
                userAlbums.add(album);
            }
        }
        Album[] albumArray = new Album[userAlbums.size()];
        userAlbums.toArray(albumArray);
        return albumArray;
    }


}
