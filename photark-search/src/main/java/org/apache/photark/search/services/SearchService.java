package org.apache.photark.search.services;

import org.apache.photark.services.gallery.GalleryListener;
import org.oasisopen.sca.annotation.Remotable;

@Remotable
public interface SearchService extends GalleryListener {

    void clear();

    String[] search(String queryString);

}
