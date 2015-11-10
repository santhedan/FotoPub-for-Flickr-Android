package com.dandekar.flickrpublish.model;

public class Session
{

	public String flickrToken;
	
	public String flickrSecret;
	
	public String fullName;
	
	public String nsid;
	
	public String userName;
	
	public String hmacSha1Key;
	
	public boolean isValid()
	{
		// If we have token the session is valies
		return (flickrToken != null && flickrToken.length() > 0);
	}

	@Override
	public String toString() {
		return "Session [flickrToken=" + flickrToken + ", flickrSecret=" + flickrSecret + ", fullName=" + fullName
				+ ", nsid=" + nsid + ", userName=" + userName + ", hmacSha1Key=" + hmacSha1Key + "]";
	}
	
}
