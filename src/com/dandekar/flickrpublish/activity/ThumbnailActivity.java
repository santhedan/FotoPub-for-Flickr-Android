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
import com.dandekar.flickrpublish.flickr.PhotosetGetPhotos;
import com.dandekar.flickrpublish.model.Photo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

public class ThumbnailActivity extends BaseActivity {

	public enum PhotoType {
		INVALID_TYPE(0), PHOTOSET_PHOTOS(1), GROUP_PHOTOS(2), USER_PHOTOS(3), EXPLORE_PHOTOS(4);

		private int numVal;

		private PhotoType(int numVal) {
			this.numVal = numVal;
		}

		public int getNumVal() {
			return this.numVal;
		}

		public static PhotoType getType(int numVal) {
			switch (numVal) {
			case 1:
				return PhotoType.PHOTOSET_PHOTOS;
			case 2:
				return PhotoType.GROUP_PHOTOS;
			case 3:
				return PhotoType.USER_PHOTOS;
			case 4:
				return PhotoType.EXPLORE_PHOTOS;
			}
			return PhotoType.INVALID_TYPE;
		}
	}

	private PhotoType photoType;

	private String photosetId;

	private GridView gridView;

	private List<Photo> photos;

	private PhotoAdapter adapter;

	private LayoutInflater inflater;

	private ImageLoader imageLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Assign layout
		setContentView(R.layout.thumbnail);
		// Get grid view
		gridView = (GridView) findViewById(R.id.photoGrid);
		// Get the layout inflater
		this.inflater = LayoutInflater.from(this);
		// Get the ImageLoader through your singleton class.
		imageLoader = VolleySingleton.getInstance(this).getDiskImageLoader();
		// Create adapter and assign it to grid
		adapter = new PhotoAdapter();
		gridView.setAdapter(adapter);
		// Get the intent and find out type and type specific data
		photoType = PhotoType
				.getType(getIntent().getIntExtra(Constants.PHOTO_TYPE_EXTRA, PhotoType.PHOTOSET_PHOTOS.getNumVal()));
		if (photoType == PhotoType.PHOTOSET_PHOTOS) {
			photosetId = getIntent().getStringExtra(Constants.PHOTOSET_ID_EXTRA);
			// Now create the get photoset photo request
			PhotosetGetPhotos photosetGetPhotos = new PhotosetGetPhotos(Constants.API_KEY, session.hmacSha1Key,
					session.flickrToken, session.nsid, photosetId);
			// Get the URL of the request
			String url = photosetGetPhotos.getUrl();
			Log.i("FOTOPUB", "url -> " + url);
			JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
					new Response.Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject response) {
							Log.i("FOTOPUB", "response -> " + response.toString());
							parsePhotosetPhotos(response);
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
	}

	protected void parsePhotosetPhotos(JSONObject response) {
		photos = new ArrayList<Photo>();
		try {
			String stat = response.getString("stat");
			if ("ok".equals(stat)) {
				JSONObject jsonphotosets = response.getJSONObject("photoset");
				// Extract the array
				JSONArray photoArr = jsonphotosets.getJSONArray("photo");
				// Now extract each set
				if (photoArr != null && photoArr.length() > 0) {
					for (int i = 0; i < photoArr.length(); i++) {
						JSONObject photoObj = photoArr.getJSONObject(i);
						Photo photo = new Photo(photoObj.getString("id"), photoObj.getString("title"),
								photoObj.getString("url_s").replace("_m.jpg", "_q.jpg"),
								jsonphotosets.getString("owner"), jsonphotosets.getString("ownername"),
								Integer.valueOf(photoObj.getString("height_s")),
								Integer.valueOf(photoObj.getString("width_s")),
								Integer.valueOf(photoObj.getString("views")), 0, 0, false);
						photos.add(photo);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// Load the adapter
		adapter.notifyDataSetChanged();
	}

	private class PhotoAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (photos != null) {
				return photos.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (photos != null) {
				return photos.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Photo p = photos.get(position);
			Log.i("FOTOPUB", "Photo -> " + p.toString());
			ViewHolder holder = null;
			if (convertView == null) {
				// Inflate the layout
				convertView = inflater.inflate(R.layout.photo_cell, null);
				holder = new ViewHolder();
				holder.photosetImage = (NetworkImageView) convertView.findViewById(R.id.thumbnailPhoto);
				holder.viewCount = (TextView) convertView.findViewById(R.id.viewCount);
				holder.photoName = (TextView) convertView.findViewById(R.id.photoName);
				holder.cmdView = (Button) convertView.findViewById(R.id.viewCmd);
				holder.cmdView.setTag(new Integer(position));
				// Set tag
				convertView.setTag(holder);
			} else {
				Log.i("FOTOPUB", "convertView IS NOT NULL");
				holder = (ViewHolder) convertView.getTag();
				holder.cmdView.setTag(new Integer(position));
			}
			// Now assign value
			holder.viewCount.setText(String.valueOf(p.views));
			holder.photoName.setText(p.name);
			holder.photosetImage.setImageUrl(p.smallImageURL, imageLoader);
			//
			return convertView;
		}

	}

	static class ViewHolder {
		NetworkImageView photosetImage;
		TextView viewCount;
		TextView photoName;
		Button cmdView;
	}

}
