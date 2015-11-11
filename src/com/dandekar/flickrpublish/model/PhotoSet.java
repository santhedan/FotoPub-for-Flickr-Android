package com.dandekar.flickrpublish.model;

public class PhotoSet
{
	
	public String id;

	public String name;

	public String photos;

	public String videos;

	public String views;

	public String photosetPhotoUrl;

	public PhotoSet(String id, String name, String photos, String videos, String views, String photosetPhotoUrl)
	{
		super();
		this.id = id;
		this.name = name;
		this.photos = photos;
		this.videos = videos;
		this.views = views;
		this.photosetPhotoUrl = photosetPhotoUrl;
	}

	@Override
	public String toString() {
		return "PhotoSet [id=" + id + ", name=" + name + ", photos=" + photos + ", videos=" + videos + ", views="
				+ views + ", photosetPhotoUrl=" + photosetPhotoUrl + "]";
	}
	
}
