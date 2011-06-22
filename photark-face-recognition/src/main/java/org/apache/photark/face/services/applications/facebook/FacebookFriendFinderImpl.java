package org.apache.photark.face.services.applications.facebook;

import com.github.mhendred.face4j.exception.FaceClientException;
import com.github.mhendred.face4j.exception.FaceServerException;
import com.github.mhendred.face4j.model.Face;
import com.github.mhendred.face4j.model.Photo;
import org.apache.photark.face.services.FaceRecognitionService;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Scope;

import java.io.File;

@Scope("COMPOSITE")
public class FacebookFriendFinderImpl implements FacebookFriendFinder {

    private FaceRecognitionService faceRecognitionService;

    @Reference(name = "faceRecognitionService")
    protected void setFaceRecognitionService(FaceRecognitionService faceRecognitionService) {
           this.faceRecognitionService = faceRecognitionService;
    }


    public String[] getAllMyFBFriendsInThisPicture(String pathToFile) {
        return new String[0];
    }

    public void setFacebookAuth(String facebookId, String fbAccessToken) {
        faceRecognitionService.setFacebookOauth2(facebookId,fbAccessToken);
    }

    private String[] processFBFriends(String filePath) {

        try {

       Photo p = faceRecognitionService.recognizeFromFile(new File(filePath),"friends@facebook.com");

          for(Face face : p.getFaces()) {
            if(face.getGuess() != null) {
                System.out.println("***Identified*** "+face.getGuess().toString());
            } else {
                System.out.println("??? Unidentified ..");
            }
         }

        } catch (FaceClientException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (FaceServerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    return null; //TODO return Facebook IDs of recognized friends
    }

}
