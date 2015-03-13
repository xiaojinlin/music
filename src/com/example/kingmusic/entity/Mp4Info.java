package com.example.kingmusic.entity;

import java.io.Serializable;

public class Mp4Info implements Serializable {
	/**歌曲*/
	public int _id;
	/**歌名*/
	public String title;
	/**专辑*/
	public String album;
	/**艺术家*/
	public String artist;
	/**歌名加后缀*/
	public String displayName;
	/**类型*/
	public String mimeType;
	/**路径*/
	public String path;
	/**时长*/
	public long duration;
	public long size;
	/**专辑封面*/
    private LoadImage image;
	public Mp4Info(int _id, String title, String album, String artist,
			String displayName, String mimeType, String path, long duration,
			long size) {
		super();
		this._id = _id;
		this.title = title;
		this.album = album;
		this.artist = artist;
		this.displayName = displayName;
		this.mimeType = mimeType;
		this.path = path;
		this.duration = duration;
		this.size = size;
	}
	@Override
	public String toString() {
		return "Mp4Info [_id=" + _id + ", title=" + title + ", album=" + album
				+ ", artist=" + artist + ", displayName=" + displayName
				+ ", mimeType=" + mimeType + ", path=" + path + ", duration="
				+ duration + ", size=" + size + "]";
	}
	public Mp4Info() {
		super();
		// TODO Auto-generated constructor stub
	}
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	
}
