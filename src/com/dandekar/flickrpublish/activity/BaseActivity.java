package com.dandekar.flickrpublish.activity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.dandekar.flickrpublish.model.Session;

import android.app.Activity;
import android.os.Bundle;

public abstract class BaseActivity extends Activity {

	protected Session session;

	protected RequestQueue queue;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// Get session
		this.session = ((FotoPubApplication)getApplication()).session;
		// Create request queue
		this.queue = Volley.newRequestQueue(this);
	}
}
