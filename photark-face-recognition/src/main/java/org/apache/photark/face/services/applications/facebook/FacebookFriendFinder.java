package org.apache.photark.face.services.applications.facebook;

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

    public String[] getAllMyFBFriendsInThisPicture(String pathToFile);

    public void setFacebookAuth(String facebookId, String fbAccessToken);

}
