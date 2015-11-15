package com.dandekar.flickrpublish.model;

public class Photo {

	public String id;

	public String name;

	public String smallImageURL;

	public String ownerId;

	public String ownerName;

	public int height;

	public int width;

	public int views;

	public int faves;

	public int comments;

	public boolean selected;

	public Photo(String id, String name, String smallImageURL, String ownerId, String ownerName, int height, int width,
			int views, int faves, int comments, boolean selected) {
		super();
		this.id = id;
		this.name = name;
		this.smallImageURL = smallImageURL;
		this.ownerId = ownerId;
		this.ownerName = ownerName;
		this.height = height;
		this.width = width;
		this.views = views;
		this.faves = faves;
		this.comments = comments;
		this.selected = selected;
	}

	@Override
	public String toString() {
		return "Photo [id=" + id + ", name=" + name + ", smallImageURL=" + smallImageURL + ", ownerId=" + ownerId
				+ ", ownerName=" + ownerName + ", height=" + height + ", width=" + width + ", views=" + views
				+ ", faves=" + faves + ", comments=" + comments + ", selected=" + selected + "]";
	}

}
