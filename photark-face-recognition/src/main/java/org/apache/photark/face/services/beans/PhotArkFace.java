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

import java.io.Serializable;
import java.util.List;

public class PhotArkFace implements Serializable {

    private String tid;
    private String gender;
    private PhotArkGuess guess;
    private List<PhotArkGuess> guesses;

    public PhotArkFace(String tid, String gender) {
        this.tid = tid;
        this.gender = gender;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public void setGuesses(List<PhotArkGuess> guesses) {
        this.guesses = guesses;
    }

    public void setGuess(PhotArkGuess guess) {
        this.guess = guess;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public PhotArkGuess getGuess() {
        return guess;
    }

    public List<PhotArkGuess> getGuesses() {
        return guesses;
    }

    public String toString(){
     return guess.toString();   
    }

}
