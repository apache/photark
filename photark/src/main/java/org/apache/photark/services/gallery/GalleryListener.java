package org.apache.photark.services.gallery;

import org.apache.photark.Image;
import org.oasisopen.sca.annotation.Remotable;

@Remotable
public interface GalleryListener {

    void imageAdded(String albumName, Image image);

    void imageRemoved(String albumName, Image image);

}
