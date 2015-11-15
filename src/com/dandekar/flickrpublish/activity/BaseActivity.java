package com.dandekar.flickrpublish.activity;

import com.dandekar.flickrpublish.model.Session;

import android.app.Activity;
import android.os.Bundle;

public abstract class BaseActivity extends Activity {

	protected Session session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Get session
		this.session = ((FotoPubApplication) getApplication()).session;
	}
}
