package com.dandekar.flickrpublish.activity;

import com.dandekar.flickrpublish.model.Session;

import android.app.Application;

public class FotoPubApplication extends Application {

	public Session session;

	@Override
	public void onCreate() {
		super.onCreate();
		// Create a new session object
		session = new Session();
	}
}
