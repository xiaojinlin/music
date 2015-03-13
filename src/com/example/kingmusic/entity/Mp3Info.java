package com.example.kingmusic.entity;

import java.io.Serializable;

public class Mp3Info implements Serializable{
	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getAlbumid() {
		return albumid;
	}

	public void setAlbumid(long albumid) {
		this.albumid = albumid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public int _id;
	/**歌名后来*/
	public String title;
	/**时长*/
	public int duration;
	/**歌曲路径*/
	public String path;
	/**歌名+后缀 后来.mp3*/
	public String name;
	/**艺术家*/
	public String artist;
	/**专辑ID*/
	public long albumid;
	public Mp3Info() {
		// TODO Auto-generated constructor stub
	}

	public Mp3Info(int _id, String title, int duration, String name,String artist,long albumid,String path) {
		super();
		this._id = _id;
		this.title = title;
		this.duration = duration;
		this.name = name;
		this.artist=artist;
		this.albumid=albumid;
		this.path = path;
	}

	@Override
	public String toString() {
		return "Mp3Info [_id=" + _id + ", title=" + title + ", duration="
				+ duration + ", path=" + path + ", name=" + name + ", artist="
				+ artist + ", albumid=" + albumid + "]";
	}

}
