package org.apache.photark.face.services.applications.facebook;

import com.github.mhendred.face4j.exception.FaceClientException;
import com.github.mhendred.face4j.exception.FaceServerException;
import com.github.mhendred.face4j.model.Face;
import com.github.mhendred.face4j.model.Photo;
import org.apache.photark.face.services.FaceRecognitionService;
import org.apache.tuscany.sca.data.collection.Entry;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Scope;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Scope("COMPOSITE")
public class FacebookFriendFinderImpl implements FacebookFriendFinder {

    private FaceRecognitionService faceRecognitionService;

    @Reference(name = "faceRecognitionService")
    protected void setFaceRecognitionService(FaceRecognitionService faceRecognitionService) {
        this.faceRecognitionService = faceRecognitionService;
    }

    public Entry<String, String>[] getAllMyFBFriendsInThisPicture(String pathToFile) {
        return processFBFriends(pathToFile);
    }

    public void setFacebookAuth(String facebookId, String fbAccessToken) {
        faceRecognitionService.setFacebookOauth2(facebookId, fbAccessToken);
    }

    private Entry<String, String>[] processFBFriends(String filePath) {
        List<Entry<String, String>> detectedFriends = new ArrayList<Entry<String, String>>();
        try {

            Photo p = faceRecognitionService.recognizeFromFile(new File(filePath), "friends@facebook.com");

            for (Face face : p.getFaces()) {
                String uid = "";
                String confidence = "";
                if (face.getGuess() != null) {
                    System.out.println("***Identified*** " + face.getGuess().toString());
                    uid = getFBUID(face.getGuess().toString());
                    confidence = getFaceConfidence(face.getGuess().toString());
                    detectedFriends.add(new Entry<String, String>(uid, confidence));
                } else {
                    System.out.println("??? Unidentified ..");
                }
            }

        } catch (FaceClientException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (FaceServerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        Entry<String, String>[] imageArray = new Entry[detectedFriends.size()];

        return detectedFriends.toArray(imageArray);
    }

    private String getFBUID(String s) {
        return s.substring(1, s.length() - 1).split(",")[1].trim().split("=")[1].split("@")[0];
    }

    private String getFaceConfidence(String s) {
        return s.substring(1, s.length() - 1).split(",")[0].trim().split("=")[1];
    }

}
