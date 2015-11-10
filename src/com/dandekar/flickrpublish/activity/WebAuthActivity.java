package com.dandekar.flickrpublish.activity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dandekar.flickrpublish.Constants;
import com.dandekar.flickrpublish.R;
import com.dandekar.flickrpublish.flickr.AccessToken;
import com.dandekar.flickrpublish.model.Session;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebAuthActivity extends BaseActivity
{

	private WebView webView;
	
	private Handler handler;
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// Assign layout
		setContentView(R.layout.webauth);
		// Create handler
		handler = new Handler();
		// Get URL from intent
		String url = getIntent().getStringExtra(Constants.URL_EXTRA);
		// Get handle to the webView
		webView = (WebView) findViewById(R.id.webView);
		webView.getSettings().setJavaScriptEnabled(true);
		// Set the current activity as the request handler
		webView.setWebViewClient(new WebViewClient()
		{
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url)
			{
				Log.e("FotoPub", "URL -> " + url);
				if (url != null && url.startsWith(Constants.CALL_BACK_URL))
				{
					// The URL is of the form https://flickrpublish/?oauth_token=<token>&oauth_verifier=<verifier>
					// So remove the first part
					String params = url.substring("https://flickrpublish/?".length());
					Log.i("PHOTOPUB", "params -> " + params);
					// split the string using &
					String[] parameters = params.split("&");
					// Now split using = and get the second part
					final String token = parameters[0].split("=")[1];
					final String verifier = parameters[1].split("=")[1];
					//
					handler.post(new Runnable()
					{
						@Override
						public void run()
						{
							getAccessToken(token, verifier);
						}
					});
					return true;
				}
				return false;
			}
		});
		// Load URL
		webView.loadUrl(url);
	}
	
	private void getAccessToken(String token, String verifier)
	{
		// Create new secret
		String secret = String.format("%s&%s", Constants.SECRET, session.flickrSecret);
		// Now create AccessToken object
		AccessToken accessToken = new AccessToken(Constants.API_KEY, secret, token, verifier);
		// Get the URL
		String url = accessToken.getUrl();
		StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>()
		{
			@Override
			public void onResponse(String response)
			{
				Log.i("FOTOPUB", "Response is: " + response);
				// First split the response by &
				String[] components = response.split("&");
				// Now iterate over the components and get token and secret
				for (String component : components)
				{
					//split the component using =
					String[] keyValue = component.split("=");
					String key = keyValue[0];
					String value = keyValue[1];
					//
					if (key.contentEquals("oauth_token"))
					{
						session.flickrToken = value;
					}
					if (key.contentEquals("oauth_token_secret"))
					{
						session.flickrSecret = value;
					}
					if (key.contentEquals("fullname"))
					{
						session.fullName = value;
					}
					if (key.contentEquals("user_nsid"))
					{
						session.nsid = value;
					}
					if (key.contentEquals("username"))
					{
						session.userName = value;
					}
				}
				// Log the session
				Log.i("FOTOPUB", "session -> " + session.toString());
				// Now start the web auth activity
				Intent intent = new Intent(getApplicationContext(), PhotosetActivity.class);
				startActivity(intent);
				// Start the save pref async task
				SavePreferenceData spd = new SavePreferenceData();
				spd.execute(new Session[] {session});
				// Now finish this activity
				finish();
			}
		}, new Response.ErrorListener()
		{
			@Override
			public void onErrorResponse(VolleyError error)
			{
				Log.i("FOTOPUB", "That didn't work!");
			}
		});
		// Add the request to the RequestQueue.
		queue.add(stringRequest);
	}
	
	private class SavePreferenceData extends AsyncTask<Session, Void, Void> {
		
		@Override
		protected Void doInBackground(Session... params) {
			// Get Session object
			Session session = params[0];
			Log.i("FOTOPUB", "Saving session = " + session.toString());
			// Get access to the shared preference and fetch flickr token
			SharedPreferences preferences = WebAuthActivity.this.getSharedPreferences("FOTOPUB", Context.MODE_PRIVATE);
			Editor editor = preferences.edit();
			// Now store the values from session
			editor.putString("flickrToken", session.flickrToken);
			editor.putString("flickrSecret", session.flickrSecret);
			editor.putString("fullName", session.fullName);
			editor.putString("nsid", session.nsid);
			editor.putString("userName", session.userName);
			session.hmacSha1Key = Constants.SECRET + "&" + session.flickrSecret;
			editor.putString("hmacSha1Key", session.hmacSha1Key);
			editor.putBoolean("isAuthenticated", true);
			editor.commit();
			Log.i("FOTOPUB", "Session data saved");
			return null;
		}
		
	}
	
}
