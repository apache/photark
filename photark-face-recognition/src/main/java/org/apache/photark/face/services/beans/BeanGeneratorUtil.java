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
package org.apache.photark.face.services.beans;

import com.github.mhendred.face4j.model.Face;
import com.github.mhendred.face4j.model.Guess;
import com.github.mhendred.face4j.model.Photo;
import java.util.ArrayList;
import java.util.List;

public class BeanGeneratorUtil {


    public static PhotarkPhoto createPhotarkPhoto(Photo photo) {

        PhotarkPhoto photarkPhoto = new PhotarkPhoto(photo.getPID(), photo.getURL(), photo.getFaceCount());
        photarkPhoto.setPhotArkFace(createPhotArkFace(photo.getFace()));
        List<PhotArkFace> photArkFaceList = new ArrayList<PhotArkFace>();

        for (Face f : photo.getFaces()) {
            photArkFaceList.add(createPhotArkFace(f));
        }
        photarkPhoto.setPhotArkFaces(photArkFaceList);
        return photarkPhoto;
    }

    public static PhotArkFace createPhotArkFace(Face face) {

        PhotArkFace photArkFace = new PhotArkFace(face.getTID(), getGender(face));
        photArkFace.setGuess(createPhotArkGuess(face.getGuess()));
        List<PhotArkGuess> photArkGuessList = new ArrayList<PhotArkGuess>();

        for (Guess g : face.getGuesses()) {
            photArkGuessList.add(createPhotArkGuess(g));
        }
        photArkFace.setGuesses(photArkGuessList);
        return photArkFace;
    }

    public static PhotArkGuess createPhotArkGuess(Guess guess) {
        if (guess == null) {
            return new PhotArkGuess(null, null);
        } else {
            return new PhotArkGuess(guess.second.toString(), guess.first.toString());
        }
    }

    private static String getGender(Face face) {
        if (face.getGender() != null) {
            return face.getGender().name();
        } else {
            return null;
        }
    }


}
