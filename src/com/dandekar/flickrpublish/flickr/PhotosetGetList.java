package com.dandekar.flickrpublish.flickr;

import java.util.ArrayList;
import java.util.List;

public class PhotosetGetList extends BaseRequest {

	private String userId;
	
	public PhotosetGetList(String key, String secret, String token, String userId)
	{
		// Call the base CTOR
		super();
		// Store user id
		this.userId = userId;
		//
		this.httpVerb = "GET";
        this.url = "https://api.flickr.com/services/rest/";
        this.version = "oauth_version=1.0";
        this.signatureMethod = "oauth_signature_method=HMAC-SHA1";
        //
        this.consumerKey = String.format("oauth_consumer_key=%s", key);
        this.consumerSecret = secret;
        //
        this.authToken = String.format("oauth_token=%s", token);
        //
        this.userId = String.format("user_id=%s", userId);
        //
        this.method = "method=flickr.photosets.getList";
        this.extra = "primary_photo_extras=url_s";
        //
        //
		List<String> parameters = new ArrayList<String>();
		parameters.add(this.nojsoncallback);
		parameters.add(this.format);
		parameters.add(this.consumerKey);
		parameters.add(this.authToken);
		parameters.add(this.method);
		parameters.add(this.userId);
		parameters.add(this.nonce);
		parameters.add(this.timeStamp);
		parameters.add(this.signatureMethod);
		parameters.add(this.version);
		parameters.add(this.extra);
		//
		String signow = calculateSignature(parameters, this.consumerSecret, true);
		this.signature = String.format("oauth_signature=%s", signow);
	}
	
	@Override
	public String getUrl()
	{
		return String.format("%s?%s&%s&%s&%s&%s&%s&%s&%s&%s&%s&%s&%s", this.url, this.nojsoncallback, this.format, this.consumerKey, this.authToken, this.method, this.userId, this.nonce, this.timeStamp, this.signatureMethod, this.version, this.extra, this.signature);
	}

}
