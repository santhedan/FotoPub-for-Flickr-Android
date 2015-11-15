package com.dandekar.flickrpublish.flickr;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class RequestToken extends BaseRequest
{

	private String callbackUrl;

	public RequestToken(String key, String secret, String callbackUrl)
	{
		// Call base class CTOR
		super();
		//
		this.httpVerb = "GET";
		this.url = "https://www.flickr.com/services/oauth/request_token";
		//
		this.consumerKey = String.format("oauth_consumer_key=%s", key);
		this.signatureMethod = "oauth_signature_method=HMAC-SHA1";
		this.version = "oauth_version=1.0";
		// Encode callback URL
		this.callbackUrl = String.format("oauth_callback=%s", encodeUrl(callbackUrl));
		// The key is the concatenated values of the Consumer Secret and Token Secret, separated by an '&'.
		// We only have consumer secret here hence the training &
		this.consumerSecret = String.format("%s&", secret);
		//
		List<String> parameters = new ArrayList<String>();
		parameters.add(this.nonce);
		parameters.add(this.timeStamp);
		parameters.add(this.consumerKey);
		parameters.add(this.signatureMethod);
		parameters.add(this.version);
		parameters.add(this.callbackUrl);
		//
		String signow = calculateSignature(parameters, this.consumerSecret, false);
		this.signature = String.format("oauth_signature=%s", signow);
	}

	@Override
	public String getUrl()
	{
		String url = String.format("%s?%s&%s&%s&%s&%s&%s&%s", this.url, this.nonce, this.timeStamp, this.consumerKey, this.signatureMethod, this.version, this.callbackUrl, this.signature);
		Log.i("PHOTOPUB", "URL -> " + url);
		return url;
	}

}
