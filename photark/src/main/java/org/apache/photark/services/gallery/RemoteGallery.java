package org.apache.photark.services.gallery;

import org.apache.photark.Image;
import org.apache.photark.services.album.Album;
import org.oasisopen.sca.annotation.Remotable;

/**
 * Created by IntelliJ IDEA.
 * User: subash
 * Date: Jan 27, 2011
 * Time: 4:43:26 PM
 * To change this template use File | Settings | File Templates.
 */
@Remotable
public interface RemoteGallery {

    void addAlbum(String albumName, String albumType);

    boolean hasAlbum(String albumName, String type);

    void deleteAlbum(String albumName, String type);

    String[] getAlbumPictures(String albumName, String type);

    String[] getAlbumPicturesToUser(String albumName, String securityToken, String type);

    String getAlbumCover(String albumName, String type);

    String getAlbumCoverToUser(String albumName, String securityToken, String type);

    Album[] getAlbums(String type);

    Album[] getAlbumsToUser(String securityToken, String type);

    Album[] getAlbumsToSetPermission(String securityToken);

    void imageAdded(String albumName, Image image);

    void imageRemoved(String albumName, Image image);

}