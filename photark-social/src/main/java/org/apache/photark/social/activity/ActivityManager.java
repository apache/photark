package org.apache.photark.social.activity;

import java.util.Set;

import org.apache.photark.social.exception.PhotArkSocialException;
import org.apache.photark.social.util.FilterOptions;

public interface ActivityManager {

	/**
	 * creates an Activity data object with the given fields
	 * 
	 * @param userId
	 *            ID of the user to whom the activity belongs to
	 * @param groupId
	 *            optional; ID of the group if this activity belongs to a group
	 * @param fields
	 *            attributes of the activity
	 * @return an Activity data object
	 * @throws PhotArkSocialException
	 */
	Activity createActivity(String userId, String groupId, Set<String> fields)
			throws PhotArkSocialException;

	/**
	 * persists the given Activity data object for the given user
	 * 
	 * @param userId
	 *            ID of the user to whom the activity belongs to
	 * @param activity
	 *            the Activity data object to persists
	 * @throws PhotArkSocialException
	 */
	void saveActivity(String userId, Activity activity)
			throws PhotArkSocialException;

	/**
	 * updates a persisted Activity data object
	 * 
	 * @param userId
	 *            ID of the user to whom the activity belongs to
	 * @param activity
	 *            the Activity data object to update
	 * @throws PhotArkSocialException
	 */
	void updateActivity(String userId, Activity activity)
			throws PhotArkSocialException;

	/**
	 * removes a persisted Activity data object
	 * 
	 * @param userId
	 *            ID of the user to whom the activity belongs to
	 * @param activityId
	 *            ID of the activity to be removed
	 * @throws PhotArkSocialException
	 */
	void deleteActivity(String userId, String activityId)
			throws PhotArkSocialException;

	/**
	 * removes a persisted Activity data objects for given activityIds
	 * 
	 * @param userId
	 *            ID of the user to whom the activities belongs to
	 * @param groupId
	 *            optional; ID of the group if this activity belongs to a group
	 * @param activityIds
	 *            Set of activityIds to be deleted
	 * @throws PhotArkSocialException
	 */
	void deleteActivities(String userId, String groupId, Set<String> activityIds)
			throws PhotArkSocialException;

	/**
	 * Retrieves array of Activity data objects matching the given input
	 * parameters
	 * 
	 * @param userIds
	 *            IDs of the users the activities belongs to
	 * @param groupId
	 *            optional; ID of the group, the activities belongs to
	 * @param fields
	 *            optional; fields of the activities to be returned
	 * @param filters
	 *            optional; any filter options
	 * @return array of Activity data objects
	 * @throws PhotArkSocialException
	 */
	Activity[] getActivities(String[] userIds, String groupId,
			Set<String> fields, FilterOptions filters)
			throws PhotArkSocialException;

	/**
	 * Retrieves array of Activity data objects matching the given input
	 * parameters
	 * 
	 * @param userIds
	 *            IDs of the users the activities belongs to
	 * @param groupId
	 *            optional; ID of the group, the activities belongs to
	 * @param fields
	 *            optional; fields of the activities to be returned
	 * @param filters
	 *            optional; any filter options
	 * @param activityIds
	 *            array of activityIds
	 * @return array of Activity data objects
	 * @throws PhotArkSocialException
	 */
	Activity[] getActivities(String userId, String groupId, Set<String> fields,
			FilterOptions filters, String[] activityIds)
			throws PhotArkSocialException;

	/**
	 * Retrieves an Activity data object matching the given input parameters
	 * 
	 * @param userIds
	 *            IDs of the users the activities belongs to
	 * @param groupId
	 *            optional; ID of the group, the activities belongs to
	 * @param fields
	 *            optional; fields of the activities to be returned
	 * @param activityId
	 *            ID of the Activity data object to be retrieved
	 * @return an Activity data object matching the given input parameters
	 * @throws PhotArkSocialException
	 */
	public Activity getActivity(String userId, String groupId,
			Set<String> fields, String activityId)
			throws PhotArkSocialException;

}
