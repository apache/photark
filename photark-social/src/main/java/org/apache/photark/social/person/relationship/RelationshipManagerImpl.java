package org.apache.photark.social.person.relationship;

import org.apache.photark.social.exception.PhotArkSocialException;

public class RelationshipManagerImpl implements RelationshipManager{

	public String getRelationshipStatus(String veiwer, String owner)
			throws PhotArkSocialException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean requestRelationship(String viewer, String owner)
			throws PhotArkSocialException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean acceptRelationshipRequest(String viewer, String owner)
			throws PhotArkSocialException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean ignoreRelationship(String viewer, String owner)
			throws PhotArkSocialException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean removeRelationship(String owner, String viewer)
			throws PhotArkSocialException {
		// TODO Auto-generated method stub
		return false;
	}

	public String[] getRelationshipList(String loggedUser)
			throws PhotArkSocialException {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getPendingRelationshipRequests(String owner)
			throws PhotArkSocialException {
		// TODO Auto-generated method stub
		return null;
	}

}
