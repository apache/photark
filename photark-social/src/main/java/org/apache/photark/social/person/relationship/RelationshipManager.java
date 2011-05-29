package org.apache.photark.social.person.relationship;

import org.apache.photark.social.exception.PhotArkSocialException;

public interface RelationshipManager {

	public static final String RELATIONSHIP_FRIEND = "friend";
	public static final String RELATIONHIP_FRIEND_REQUEST_PENDING = "friend request pending";
	public static final String RELATIONHIP_FRIEND_REQUEST_RECEIVED = "friend request received";
	public static final String RELATIONHIP_NONE = "none";

	/**
	 * Returns the relationship status between two users
	 * 
	 * @param veiwer
	 *            ID of the person who views owner's pages
	 * @param owner
	 *            ID of the person whom the pages/resources belongs to
	 * @return one of {RELATIONSHIP_FRIEND,RELATIONHIP_FRIEND_REQUEST_PENDING,
	 *         RELATIONHIP_FRIEND_REQUEST_RECEIVED ,RELATIONHIP_NONE}
	 * @throws PhotArkSocialException
	 */

	String getRelationshipStatus(String veiwer, String owner)
			throws PhotArkSocialException;

	/**
	 * Sends/creates a friend request from viewer to owner
	 * 
	 * @param viewer
	 *            ID of the person who requests for the relationship
	 * @param owner
	 *            ID of the person for whom viewer sends the relationship
	 *            request
	 * @return true if the request was successful; else false
	 * @throws PhotArkSocialException
	 */
	boolean requestRelationship(String viewer, String owner)
			throws PhotArkSocialException;

	/**
	 * Accepts a relationship request from owner
	 * 
	 * @param viewer
	 *            ID of the person who has sent the relationship request
	 * @param owner
	 *            ID of the person who has received the relationship request
	 *            from viewer
	 * @return true if a relationship was created between viewer and owner;
	 *         otherwise false
	 * @throws PhotArkSocialException
	 */
	boolean acceptRelationshipRequest(String viewer, String owner)
			throws PhotArkSocialException;

	/**
	 * Ignores a relationship request
	 * 
	 * @param viewer
	 *            ID of the person who has sent the relationship request
	 * @param owner
	 *            ID of the person who has received the relationship request
	 *            from viewer
	 * @return true if the operation was successful
	 * @throws PhotArkSocialException
	 */
	boolean ignoreRelationship(String viewer, String owner)
			throws PhotArkSocialException;

	/**
	 * Removing the relationship - i.e: un-friending
	 * 
	 * @param owner
	 *            ID of the person with whom the viewer wants to remove the
	 *            relationship
	 * @param viewer
	 *            ID of the person who wants to remove the relationship with
	 *            owner
	 * @return true if the operation was successful
	 * @throws PhotArkSocialException
	 */
	boolean removeRelationship(String owner, String viewer)
			throws PhotArkSocialException;

	/**
	 * Retrieves the list relationships the user has
	 * 
	 * @param loggedUser
	 *            ID of the user logged in
	 * @return array of Strings with the IDs of the persons who has relationship
	 *         with loggedUser
	 * @throws PhotArkSocialException
	 */
	String[] getRelationshipList(String loggedUser)
			throws PhotArkSocialException;

	/**
	 * Retrieves the list of relationship requests
	 * 
	 * @param owner
	 *            ID of the person who has received the relationship requests
	 * @return array of Strings with the IDs of the persons who has sent
	 *         relationship requests to the owner
	 * @throws PhotArkSocialException
	 */
	String[] getPendingRelationshipRequests(String owner)
			throws PhotArkSocialException;
}
