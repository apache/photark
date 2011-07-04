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

package org.apache.photark.social;

public final class PhotArkSocialConstants {

	/* Constants related to Person */
	public static final String PERSON_USERID = "userId";
	public static final String PERSON_FIRSTNAME = "firstname";
	public static final String PERSON_LASTNAME = "lastname";
	public static final String PERSON_DISPLAYNAME = "displayname";
	public static final String PERSON_ABOUTME = "aboutme";
	public static final String PERSON_ADDRESS = "address";
	public static final String PERSON_ACTIVITIES = "activities";
	public static final String PERSON_BIRTHDAY = "birthday";
	public static final String PERSON_EMAILS = "emails";
	public static final String PERSON_GENDER = "gender";
	public static final String PERSON_NICKNAME = "nickname";
	public static final String PERSON_ORGANIZATIONS = "organizations";
	public static final String PERSON_PHONENUMBERS = "phonenumbers";
	public static final String PERSON_THUMBNAILURL = "thumbnailUrl";

	/* Constants related to Relationship */
	// received requests; pending approval
	public static final String PENDING_RELATIONSHIP_REQUEST = "pending_requests";
	// no relationship exists with the other user
	public static final String RELATIONSHIP_NONE = "none";
	// a relationship already exists with the other user
	public static final String RELATIONSHIP_FRIEND = "friend";
	// a relationship request is received from the other user
	public static final String RELATIONSHIP_REQUEST_RECEIVED = "relationship_request_received";
	// a relationship request is sent to the other user
	public static final String RELATIONSHIP_REQUEST_PENDING = "relationship_request_pending";

	/* Constants related to Activity */
	public static final String ACTIVITY_BODY = "body";
	public static final String ACTIVITY_ID = "activityId";
	public static final String ACTIVITY_POSTEDTIME = "postedTime";
	public static final String ACTIVITY_TITLE = "title";
	public static final String ACTIVITY_URL = "url";

	/* Constants related to Message Collection */
	public static final String MESSAGE_COLL_ID = "messageCollectionId";
	public static final String MESSAGE_COLL_TITLE = "title";
	public static final String MESSAGE_COLL_TOTAL_COUNT = "totalCount";
	public static final String MESSAGE_COLL_UNREAD_COUNT = "unreadCount";
	public static final String MESSAGE_COLL_LAST_UPDATED = "lastUpdated";
	public static final String MESSAGE_COLL_URLS = "urls";

	/* Constants related to Message */
	public static final String MESSAGE_BODY = "messageBody";
	public static final String MESSAGE_ID = "messageId";
	public static final String MESSAGE_IN_REPLY_TO = "inReplyTo";
	public static final String MESSAGE_RECIPIENTS = "recipients";
	public static final String MESSAGE_REPLIES = "replies";
	public static final String MESSAGE_SENDER_ID = "senderId";
	public static final String MESSAGE_STATUS = "status";
	public static final String MESSAGE_TIME_SENT = "timeSent";
	public static final String MESSAGE_TITLE = "title";
	public static final String MESSAGE_UPDATED = "updated";
	public static final String MESSAGE_URL = "url";

	private PhotArkSocialConstants() {

	}
}
