package org.apache.photark.social.activity;

import java.util.Set;

import org.apache.photark.social.exception.PhotArkSocialException;
import org.apache.photark.social.util.FilterOptions;

public class ActivityManagerImpl implements ActivityManager {

	public Activity createActivity(String userId, String groupId,
			Set<String> fields) throws PhotArkSocialException {
		// TODO Auto-generated method stub
		return null;
	}

	public void saveActivity(String userId, Activity activity)
			throws PhotArkSocialException {
		// TODO Auto-generated method stub
		
	}

	public void updateActivity(String userId, Activity activity)
			throws PhotArkSocialException {
		// TODO Auto-generated method stub
		
	}

	public void deleteActivity(String userId, String activityId)
			throws PhotArkSocialException {
		// TODO Auto-generated method stub
		
	}

	public void deleteActivities(String userId, String groupId,
			Set<String> activityIds) throws PhotArkSocialException {
		// TODO Auto-generated method stub
		
	}

	public Activity[] getActivities(String[] userIds, String groupId,
			Set<String> fields, FilterOptions filters)
			throws PhotArkSocialException {
		// TODO Auto-generated method stub
		return null;
	}

	public Activity[] getActivities(String userId, String groupId,
			Set<String> fields, FilterOptions filters, String[] activityIds)
			throws PhotArkSocialException {
		// TODO Auto-generated method stub
		return null;
	}

	public Activity getActivity(String userId, String groupId,
			Set<String> fields, String activityId)
			throws PhotArkSocialException {
		// TODO Auto-generated method stub
		return null;
	}


}
