package com.dandekar.flickrpublish.activity;

import java.util.List;

import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.dandekar.flickrpublish.Constants;
import com.dandekar.flickrpublish.R;
import com.dandekar.flickrpublish.VolleySingleton;
import com.dandekar.flickrpublish.flickr.PhotosetGetList;
import com.dandekar.flickrpublish.model.PhotoSet;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class PhotosetActivity extends BaseActivity
{
	
	private GridView photoSetGrid;
	
	private List<PhotoSet> photosets;
	
	private LayoutInflater inflater;
	
	private ImageLoader imageLoader;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photoset);
		// Get the grid view
		this.photoSetGrid = (GridView) findViewById(R.id.photosetgrid);
		// Get the layout inflater
		this.inflater = this.getLayoutInflater();
		// Get the ImageLoader through your singleton class.
		imageLoader = VolleySingleton.getInstance(this).getImageLoader();
		// Create the photoset get request
		PhotosetGetList photosetGetList = new PhotosetGetList(Constants.API_KEY, session.hmacSha1Key, session.flickrToken, session.nsid);
		// Get the URL of the request
		String url = photosetGetList.getUrl();
		Log.i("FOTOPUB", "url -> " + url);
		JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

		    @Override
		    public void onResponse(JSONObject response)
		    {
		    	Log.i("FOTOPUB", "response -> " + response.toString());
		    }
		}, new Response.ErrorListener() {

		    @Override
		    public void onErrorResponse(VolleyError error)
		    {
		    	Log.i("FOTOPUB", "There was an error - " + error.getMessage());
		    }
		});
		// Access the RequestQueue through your singleton class.
		VolleySingleton.getInstance(this).addToRequestQueue(request);
	}

	private class PhotosetAdapter extends BaseAdapter
	{
		
		@Override
		public int getCount() {
			if (photosets != null)
			{
				return photosets.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (photosets != null)
			{
				return photosets.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// Get PhotoSet
			PhotoSet set = photosets.get(position);
			ViewHolder holder = null;
			if (convertView == null)
			{
				// Inflate the layout
				convertView = inflater.inflate(R.layout.photoset_cell, parent, false);
				// Create view holder & assign its members
				holder = new ViewHolder();
				holder.photosetImage = (NetworkImageView) findViewById(R.id.networkImageView);
				holder.photos = (TextView) findViewById(R.id.photosetPhotos);
				holder.videos = (TextView) findViewById(R.id.photosetVideos);
				holder.videos = (TextView) findViewById(R.id.photosetViews);
				holder.photosetName = (TextView) findViewById(R.id.photosetName);
				// Set tag
				convertView.setTag(holder);
			}
			else
			{
				holder = (ViewHolder) convertView.getTag();
			}
			// Now assign the values
			holder.photosetImage.setImageUrl(set.photosetPhotoUrl, imageLoader);
			holder.photosetName.setText(set.name);
			holder.photos.setText(set.photos);
			holder.videos.setText(set.videos);
			holder.views.setText(set.views);
			// Return the view
			return convertView;
		}
		
	}
	
	static class ViewHolder
	{
		NetworkImageView photosetImage;
		TextView photosetName;
		TextView photos;
		TextView videos;
		TextView views;
	}
}
