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

if (! photark) {
	var photark = {};
}

if (! photark.constants) {
	photark.constants = {};
}

photark.constants.contextRoot = "/photark/";
photark.constants.adminContextRoot = "/photark/admin/";

photark.constants.GenericFriendFinder = photark.constants.contextRoot + "GenericFriendFinder?smd";
photark.constants.FacebookFriendFinder = photark.constants.contextRoot + "FacebookFriendFinder?smd";
photark.constants.FaceRecognitionService = photark.constants.contextRoot + "FaceRecognitionService?smd";
photark.constants.RemoteAlbumSubscription = photark.constants.contextRoot + "RemoteAlbumSubscriptionManager?smd";
photark.constants.GalleryServiceEndpoint = photark.constants.contextRoot + "GalleryService?smd";
photark.constants.RemoteGalleryServiceEndpoint = photark.constants.contextRoot + "JCRRemoteGallery?smd";
photark.constants.SearchServiceEndpoint = photark.constants.contextRoot + "SearchService?smd";
photark.constants.AccessManagerServiceEndpoint = photark.constants.contextRoot + "AccessManagerService?smd";

photark.constants.UploadEndpoint = photark.constants.adminContextRoot + "upload";
