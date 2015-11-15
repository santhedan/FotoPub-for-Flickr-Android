package com.dandekar.flickrpublish.activity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dandekar.flickrpublish.Constants;
import com.dandekar.flickrpublish.R;
import com.dandekar.flickrpublish.VolleySingleton;
import com.dandekar.flickrpublish.flickr.RequestToken;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FlickrAuthActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("FOTOPUB", "FlickrAuthActivity.onCreate");
		// Set layout
		setContentView(R.layout.flickrauth);
		// Get button and add handler
		final Button button = (Button) findViewById(R.id.cmdOpenWebPage);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Disable button
				button.setEnabled(false);
				// Create RequestToken object
				RequestToken reqToken = new RequestToken(Constants.API_KEY, Constants.SECRET, Constants.CALL_BACK_URL);
				// Get the URL from request token
				String url = reqToken.getUrl();
				// Now use volley library to execute the get request
				// Request a string response from the provided URL.
				StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
						new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Log.i("FOTOPUB", "Response is: " + response);
						// First split the response by &
						String[] components = response.split("&");
						// Now iterate over the components and get token and
						// secret
						for (String component : components) {
							// split the component using =
							String[] keyValue = component.split("=");
							String key = keyValue[0];
							String value = keyValue[1];
							//
							if (key.contentEquals("oauth_token")) {
								session.flickrToken = value;
							}
							if (key.contentEquals("oauth_token_secret")) {
								session.flickrSecret = value;
							}
						}
						// form url from the above
						String url = String.format("https://www.flickr.com/services/oauth/authorize?oauth_token=%s",
								session.flickrToken);
						// Now start the web auth activity
						Intent intent = new Intent(getApplicationContext(), WebAuthActivity.class);
						intent.putExtra(Constants.URL_EXTRA, url);
						startActivity(intent);
						// Now finish this activity
						finish();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.i("FOTOPUB", "That didn't work!");
						error.printStackTrace();
					}
				});
				// Add the request to the RequestQueue.
				// Access the RequestQueue through your singleton class.
				VolleySingleton.getInstance(FlickrAuthActivity.this).addToRequestQueue(stringRequest);
			}
		});
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		// Set the intent
		Uri uri = intent.getData();
		Log.e("FOTOPUB", "URI=" + uri);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Get intent
		Intent intent = getIntent();
		Log.e("FOTOPUB", "intent.getScheme() -> " + intent.getScheme());
		Log.e("FOTOPUB", "intent.getDataString() -> " + intent.getDataString());
	}
}
