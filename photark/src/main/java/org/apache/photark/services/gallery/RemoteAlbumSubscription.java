package org.apache.photark.services.gallery;

/**
 * Created by IntelliJ IDEA.
 * User: subash
 * Date: Jan 27, 2011
 * Time: 9:27:59 AM
 * To change this template use File | Settings | File Templates.
 */
import org.apache.photark.Image;
import org.apache.tuscany.sca.data.collection.Entry;
import org.oasisopen.sca.annotation.Remotable;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: siddhi
 * Date: Oct 6, 2010
 * Time: 3:26:24 PM
 * To change this template use File | Settings | File Templates.
 */
@Remotable
public interface RemoteAlbumSubscription {

    Entry<String, Image>[] addRemoteAlbum(String url,String alb,String albDesc,String type,String uname,String passwd);

}