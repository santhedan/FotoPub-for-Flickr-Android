package com.dandekar.flickrpublish.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.dandekar.flickrpublish.R;
import com.dandekar.flickrpublish.model.Session;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		// Create the preference task
		GetPreferenceDataTask task = new GetPreferenceDataTask();
		task.execute();
	}

	private class GetPreferenceDataTask extends AsyncTask<Void, Void, Session> {
		@Override
		protected Session doInBackground(Void... params) {
			// Get access to the shared preference and fetch flickr token
			SharedPreferences preferences = SplashActivity.this.getSharedPreferences("FOTOPUB", Context.MODE_PRIVATE);
			// Get the values of stored data is any
			Boolean isAuthenticated = preferences.getBoolean("isAuthenticated", false);
			// Is this client authenticated?
			if (isAuthenticated) {
				// Yes - Now create session object and set its values
				Session session = new Session();
				//
				session.flickrToken = preferences.getString("flickrToken", "");
				session.flickrSecret = preferences.getString("flickrSecret", "");
				session.fullName = preferences.getString("fullName", "");
				session.nsid = preferences.getString("nsid", "");
				session.userName = preferences.getString("userName", "");
				session.hmacSha1Key = preferences.getString("hmacSha1Key", "");
				// Print session object
				Log.i("FOTOPUB", "session = " + session.toString());
				// Return session object
				return session;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Session result) {
			super.onPostExecute(result);
			if (result != null && result.isValid()) {
				// Set the session object in application
				FotoPubApplication app = (FotoPubApplication) getApplication();
				app.session = result;
				// Go to photoset listing screen
				Intent intent = new Intent(getApplicationContext(), PhotosetActivity.class);
				startActivity(intent);
			} else {
				// Go to authentication screen
				Intent intent = new Intent(getApplicationContext(), FlickrAuthActivity.class);
				startActivity(intent);
			}
			// Fihish the splash activity
			Log.i("FOTOPUB", "Unloading splash screen");
			finish();
		}
	}

}
