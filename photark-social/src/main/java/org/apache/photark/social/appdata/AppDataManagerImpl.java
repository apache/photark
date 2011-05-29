package org.apache.photark.social.appdata;

import java.util.Map;
import java.util.Set;

import org.apache.photark.social.exception.PhotArkSocialException;

public class AppDataManagerImpl implements AppDataManager{

	public Map<String, String> getPersonData(String userId, String groupId)
			throws PhotArkSocialException {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Map<String, String>> getPeopleData(String[] userIds,
			String groupId) throws PhotArkSocialException {
		// TODO Auto-generated method stub
		return null;
	}

	public void savePersonData(String userId, Map<String, String> values)
			throws PhotArkSocialException {
		// TODO Auto-generated method stub
		
	}

	public void deletePersonData(String userId, String groupId,
			Set<String> fields) throws PhotArkSocialException {
		// TODO Auto-generated method stub
		
	}

	public void updatePersonData(String userId, String groupId, String appId,
			Set<String> fields, Map<String, String> values)
			throws PhotArkSocialException {
		// TODO Auto-generated method stub
		
	}

	

}
