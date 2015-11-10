package com.dandekar.flickrpublish.flickr;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;

import android.util.Base64;

public abstract class BaseRequest
{

	public static final String UTF8 = "UTF-8";

	public String url;

	public String httpVerb;

	public String nonce;

	public String timeStamp;

	public String consumerKey;

	public String consumerSecret;

	public String signatureMethod;

	public String version;

	public String signature;
	
	public String nojsoncallback;
	
	public String format;
	
	public String extra;
	
	public String method;
	
	public String authToken;

	public abstract String getUrl();
	
	public BaseRequest()
	{
		// timestamp
		long ts = System.currentTimeMillis();
		//
		this.nonce = String.format("oauth_nonce=%d", ts);
		this.timeStamp = String.format("oauth_timestamp=%d", ts);
		//
		this.nojsoncallback = "nojsoncallback=1";
		this.format = "format=json";
	}

	public String calculateSignature(List<String> parameters, String key)
	{
		try
		{
			// First sort the incoming list
			Collections.sort(parameters);
			// Buffer to build URL
			StringBuffer buffer = new StringBuffer();
			// Add http verb
			buffer.append(httpVerb);
			buffer.append("&");
			// Append URL
			buffer.append(URLEncoder.encode(url, UTF8));
			buffer.append("&");
			// Append parameters in sorted order
			String paramJoined = StringUtils.join(parameters, "&");
			buffer.append(URLEncoder.encode(paramJoined, UTF8));
			// Compute HMACSHA1 and convert to BASE64
			String base64Hash = computeHMACSHA1BASE64(buffer.toString(), key);
			// Now replace any + with %2B
			base64Hash = base64Hash.replace("+", "%2B");
			// Return value
			return base64Hash;
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public String encodeUrl(String urlToEncode)
	{
		try
		{
			return URLEncoder.encode(urlToEncode, UTF8);
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	private String computeHMACSHA1BASE64(String input, String key)
	{
		try
		{
			// Create HMACSHA1 with supplied key
			Mac mac = Mac.getInstance("HmacSHA1");
			SecretKeySpec secret = new SecretKeySpec(key.getBytes(), mac.getAlgorithm());
			mac.init(secret);
			byte[] digest = mac.doFinal(input.getBytes());
			return Base64.encodeToString(digest, Base64.DEFAULT | Base64.NO_PADDING);
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		catch (InvalidKeyException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
