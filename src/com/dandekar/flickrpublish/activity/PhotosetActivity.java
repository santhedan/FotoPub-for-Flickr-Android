package com.dandekar.flickrpublish.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.dandekar.flickrpublish.Constants;
import com.dandekar.flickrpublish.R;
import com.dandekar.flickrpublish.VolleySingleton;
import com.dandekar.flickrpublish.flickr.PhotosetGetList;
import com.dandekar.flickrpublish.model.PhotoSet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class PhotosetActivity extends BaseActivity implements OnItemClickListener {

	private GridView photoSetGrid;

	private List<PhotoSet> photosets;

	private LayoutInflater inflater;

	private ImageLoader imageLoader;

	private PhotosetAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photoset);
		// Get the grid view
		this.photoSetGrid = (GridView) findViewById(R.id.photosetgrid);
		// Set item click listener
		this.photoSetGrid.setOnItemClickListener(this);
		// Get the layout inflater
		this.inflater = LayoutInflater.from(this);
		// Configure adapter
		adapter = new PhotosetAdapter();
		photoSetGrid.setAdapter(adapter);
		// Get the ImageLoader through your singleton class.
		imageLoader = VolleySingleton.getInstance(this).getDiskImageLoader();
		// Create the photoset get request
		PhotosetGetList photosetGetList = new PhotosetGetList(Constants.API_KEY, session.hmacSha1Key,
				session.flickrToken, session.nsid);
		// Get the URL of the request
		String url = photosetGetList.getUrl();
		Log.i("FOTOPUB", "url -> " + url);
		JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						Log.i("FOTOPUB", "response -> " + response.toString());
						parsePhotosets(response);
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.i("FOTOPUB", "There was an error - " + error.getMessage());
					}
				});
		// Access the RequestQueue through your singleton class.
		VolleySingleton.getInstance(this).addToRequestQueue(request);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_groups:
			Log.i("FOTOPUB", "Show groups");
			break;

		case R.id.action_explore:
			Log.i("FOTOPUB", "Show explore");
			break;

		case R.id.action_following:
			Log.i("FOTOPUB", "Show following");
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void parsePhotosets(JSONObject response) {
		photosets = new ArrayList<PhotoSet>();
		try {
			String stat = response.getString("stat");
			if ("ok".equals(stat)) {
				JSONObject jsonphotosets = response.getJSONObject("photosets");
				// Extract the array
				JSONArray sets = jsonphotosets.getJSONArray("photoset");
				// Now extract each set
				if (sets != null && sets.length() > 0) {
					for (int i = 0; i < sets.length(); i++) {
						JSONObject set = sets.getJSONObject(i);
						JSONObject title = set.getJSONObject("title");
						JSONObject extras = set.getJSONObject("primary_photo_extras");
						PhotoSet pset = new PhotoSet(set.getString("id"), title.getString("_content"),
								set.getString("photos"), Integer.toString(set.getInt("videos")),
								Integer.toString(set.getInt("count_views")),
								extras.getString("url_s").replace("_m.jpg", "_q.jpg"));
						Log.i("FOTOPUB", "pset -> " + pset.toString());
						photosets.add(pset);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// Load the adapter
		adapter.notifyDataSetChanged();
	}

	private class PhotosetAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (photosets != null) {
				return photosets.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (photosets != null) {
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
			Log.i("FOTOPUB", "***pset -> " + set.toString());
			ViewHolder holder = null;
			if (convertView == null) {
				Log.i("FOTOPUB", "convertView IS NULL");
				// Inflate the layout
				convertView = inflater.inflate(R.layout.photoset_cell, null);
				// Create view holder & assign its members
				holder = new ViewHolder();
				holder.photosetImage = (NetworkImageView) convertView.findViewById(R.id.networkImageView);
				holder.photos = (TextView) convertView.findViewById(R.id.photosetPhotos);
				holder.videos = (TextView) convertView.findViewById(R.id.photosetVideos);
				holder.views = (TextView) convertView.findViewById(R.id.photosetViews);
				holder.photosetName = (TextView) convertView.findViewById(R.id.photosetName);
				// Set tag
				convertView.setTag(holder);
			} else {
				Log.i("FOTOPUB", "convertView IS NOT NULL");
				holder = (ViewHolder) convertView.getTag();
			}
			if (holder.photosetName == null) {
				Log.i("FOTOPUB", "holder.photosetName IS NULL");
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

	static class ViewHolder {
		NetworkImageView photosetImage;
		TextView photosetName;
		TextView photos;
		TextView videos;
		TextView views;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// Start the thumbnail view activity using for viewing photoset photos
		Intent intent = new Intent(this, ThumbnailActivity.class);
		intent.putExtra(Constants.PHOTO_TYPE_EXTRA, ThumbnailActivity.PhotoType.PHOTOSET_PHOTOS.getNumVal());
		intent.putExtra(Constants.PHOTOSET_ID_EXTRA, photosets.get(position).id);
		startActivity(intent);
	}
}
