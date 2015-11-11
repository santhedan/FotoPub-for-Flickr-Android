package com.dandekar.flickrpublish.flickr;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class AccessToken extends BaseRequest {

	private String token;
	
	private String verifier;
	
	public AccessToken(String key, String secret, String token, String verifier)
	{
		// Call base class CTOR
		super();
		//
		this.httpVerb = "GET";
		this.url = "https://www.flickr.com/services/oauth/access_token";
		//
		this.consumerKey = String.format("oauth_consumer_key=%s", key);
		this.signatureMethod = "oauth_signature_method=HMAC-SHA1";
		this.version = "oauth_version=1.0";
		//
        this.consumerSecret = secret;
        //
        this.token = String.format("oauth_token=%s", token);
        this.verifier = String.format("oauth_verifier=%s", verifier);
        //		//
		List<String> parameters = new ArrayList<String>();
		parameters.add(this.nonce);
		parameters.add(this.timeStamp);
		parameters.add(this.verifier);
		parameters.add(this.consumerKey);
		parameters.add(this.signatureMethod);
		parameters.add(this.version);
		parameters.add(this.token);
		//
		String signow = calculateSignature(parameters, this.consumerSecret, false);
		this.signature = String.format("oauth_signature=%s", signow);

	}
	
	@Override
	public String getUrl()
	{
		String url = String.format("%s?%s&%s&%s&%s&%s&%s&%s&%s",this.url, this.nonce, this.timeStamp, this.verifier, this.consumerKey, this.signatureMethod, this.version, this.token, this.signature);
		Log.i("PHOTOPUB", "URL -> " + url);
		return url;
	}

}
