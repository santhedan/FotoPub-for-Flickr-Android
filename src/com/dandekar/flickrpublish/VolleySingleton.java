package com.dandekar.flickrpublish;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;

public class VolleySingleton {

	private static VolleySingleton mInstance;
	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;
	private ImageLoader mDiskImageLoader;
	private static Context mCtx;

	private VolleySingleton(Context context) {
		mCtx = context;
		mRequestQueue = getRequestQueue();

		mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
			private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(100);

			@Override
			public Bitmap getBitmap(String url) {
				return cache.get(url);
			}

			@Override
			public void putBitmap(String url, Bitmap bitmap) {
				cache.put(url, bitmap);
			}
		});
		mDiskImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
			private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(100);

			@Override
			public Bitmap getBitmap(String url) {
				return cache.get(url);
			}

			@Override
			public void putBitmap(String url, Bitmap bitmap) {
				cache.put(url, bitmap);
			}
		}, true);
	}

	public static synchronized VolleySingleton getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new VolleySingleton(context);
		}
		return mInstance;
	}

	public static synchronized VolleySingleton getInstance() {
		return mInstance;
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			// getApplicationContext() is key, it keeps you from leaking the
			// Activity or BroadcastReceiver if someone passes one in.
			mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());

		}
		return mRequestQueue;
	}

	public <T> void addToRequestQueue(Request<T> req) {
		getRequestQueue().add(req);
	}

	public ImageLoader getImageLoader() {
		return mImageLoader;
	}

	public ImageLoader getDiskImageLoader() {
		return mDiskImageLoader;
	}

	public void saveBitmapFile(String fullPath, Bitmap bitmap) throws IOException {
		File filesDir = mCtx.getFilesDir();
		Log.i("FOTOPUB", "filesDir -> " + filesDir.toString());
		// Create full path
		String finalFullPath = filesDir.getAbsolutePath() + fullPath;
		Log.i("FOTOPUB", "finalFullPath -> " + finalFullPath);
		// Get file object for finalFullPath
		File finalFullPathFile = new File(finalFullPath);
		// Get parent
		File parent = finalFullPathFile.getParentFile();
		if (!parent.exists() && !parent.mkdirs()) {
			throw new FileNotFoundException("Couldn't create dir: " + parent);
		}
		Log.i("FOTOPUB", "parent -> " + parent.toString());
		// Get bitmap bytes
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		// Create output stream and save data
		FileOutputStream fos = new FileOutputStream(new File(finalFullPath));
		fos.write(byteArray);
		// Close streams
		fos.close();
		stream.close();
	}

	public Bitmap getBitmap(String fullPath) throws IOException {
		File filesDir = mCtx.getFilesDir();
		Log.i("FOTOPUB", "filesDir -> " + filesDir.toString());
		// Create full path
		String finalFullPath = filesDir.getAbsolutePath() + fullPath;
		Log.i("FOTOPUB", "finalFullPath -> " + finalFullPath);
		// Get file object for finalFullPath
		File finalFullPathFile = new File(finalFullPath);
		// Check if the file exists
		if (finalFullPathFile.exists()) {
			Log.i("FOTOPUB", "Bit map exists - loading");
			FileInputStream fIn = new FileInputStream(finalFullPathFile);
			Bitmap bitmap = BitmapFactory.decodeStream(fIn);
			fIn.close();
			return bitmap;
		}
		Log.i("FOTOPUB", "Bit map does not exists");
		return null;
	}
}
