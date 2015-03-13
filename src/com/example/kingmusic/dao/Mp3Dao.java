package com.example.kingmusic.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Audio.Media;

import com.example.kingmusic.entity.Mp3Info;

public class Mp3Dao {
	Context context;
	ContentResolver cr;

	public Mp3Dao(Context context) {
		super();
		this.context = context;
	}

	public List<Mp3Info> getAllmp3() {
		List<Mp3Info> lists = new ArrayList<Mp3Info>();
		cr = context.getContentResolver();

		Cursor cs = cr.query(Media.EXTERNAL_CONTENT_URI, new String[] {
				Media._ID, Media.DURATION, Media.TITLE, Media.DISPLAY_NAME,
				Media.ARTIST, Media.ALBUM_ID, Media.DATA }, null, null, null);
		if (cs != null) {
			while (cs.moveToNext()) {
				int _id = cs.getInt(cs.getColumnIndex(Media._ID));
				String name = cs.getString(cs.getColumnIndex(Media.TITLE));
				int duration = cs.getInt(cs.getColumnIndex(Media.DURATION));

				String fileName = cs.getString(cs
						.getColumnIndex(Media.DISPLAY_NAME));
				String autor = cs.getString(cs.getColumnIndex(Media.ARTIST));
				long albumid = cs.getLong(cs.getColumnIndex(Media.ALBUM_ID));
				String path = cs.getString(cs.getColumnIndex(Media.DATA));
				Mp3Info info = new Mp3Info(_id, name, duration, fileName,
						autor, albumid, path);
				if (duration > 30000) {
					lists.add(info);
				}
			}
			cs.close();
		}
		return lists;
	}
	
	//变成Json
		public String getJson(List<Mp3Info> lists){
			
			StringBuffer sb = new StringBuffer();
			sb.append("[");
			for (Mp3Info mp3Info : lists) {
				sb.append("{").append("mp3Title:").append("'").append(mp3Info.title).append("'");
				sb.append(",");
				sb.append("mp3Id:").append("'").append(mp3Info._id).append("'");
				sb.append("}");
				sb.append(",");
			}
			//删除最后一个逗号
			sb.deleteCharAt(sb.length()-1);
			sb.append("]");
			System.out.println(sb.toString());
			return sb.toString();
		}
}
