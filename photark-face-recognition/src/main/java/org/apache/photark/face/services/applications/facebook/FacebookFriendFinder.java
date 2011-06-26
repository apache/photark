package org.apache.photark.face.services.applications.facebook;

import org.apache.tuscany.sca.data.collection.Entry;
import org.oasisopen.sca.annotation.Remotable;

/**
 * Created by IntelliJ IDEA.
 * User: subash
 * Date: Jun 14, 2011
 * Time: 11:53:30 PM
 * To change this template use File | Settings | File Templates.
 */
@Remotable
public interface FacebookFriendFinder {

    public Entry<String, String>[] getAllMyFBFriendsInThisPicture(String pathToFile);

    public void setFacebookAuth(String facebookId, String fbAccessToken);

}
