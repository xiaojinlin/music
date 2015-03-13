package com.example.kingmusic.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Media;

import com.example.kingmusic.entity.Mp4Info;

public class Mp4Dao {
	private Context context;
	ContentResolver cr;
	
	public Mp4Dao(Context context) {
		this.context = context;
	}

	public List<Mp4Info> getList() {
		List<Mp4Info> list = null;
		cr = context.getContentResolver();
		if (context != null) {
			Cursor cursor = context.getContentResolver().query(
					MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null,
					null, null);
			if (cursor != null) {
				list = new ArrayList<Mp4Info>();
				while (cursor.moveToNext()) {
					int _id = cursor.getInt(cursor.getColumnIndex(Media._ID));
					String title = cursor.getString(cursor
							.getColumnIndex(Media.TITLE));
					String album = cursor.getString(cursor
							.getColumnIndex(Media.ALBUM));
					String artist = cursor.getString(cursor
							.getColumnIndex(Media.ARTIST));
					String displayName = cursor.getString(cursor
							.getColumnIndex(Media.DISPLAY_NAME));
					String mimeType = cursor.getString(cursor
							.getColumnIndex(MediaStore.Video.Media.MIME_TYPE));
					String path = cursor.getString(cursor
							.getColumnIndex(MediaStore.Video.Media.DATA));
					long duration = cursor.getInt(cursor
							.getColumnIndex(MediaStore.Video.Media.DURATION));
					long size = cursor.getLong(cursor
							.getColumnIndex(MediaStore.Video.Media.SIZE));
					Mp4Info info = new Mp4Info(_id, title, album, artist,
							displayName, mimeType, path, duration,size);
					list.add(info);
				}
				cursor.close();
			}
		}
		return list;
	}
}
