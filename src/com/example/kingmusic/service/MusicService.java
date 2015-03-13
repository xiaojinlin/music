package com.example.kingmusic.service;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.kingmusic.dao.DatabaseHelper;
import com.example.kingmusic.entity.Mp3Info;

//数据库服务类
public class MusicService {
	private static final String MYLOVE_MUSIC_ID = "music_id";
	private static final String MYLOVE_MUSIC_DURATION = "duration";
	private static final String MYLOVE_MUSIC_TITLE = "title";
	private static final String MYLOVE_MUSIC_ARTIST = "artist";
	private static final String TABLE_NAME_MUSCI = "mylove";
	private static final String TABLE_NAME_ZUIJIN = "myzuijin";
	private static final String MYLOVE_MUSIC_ALBUMID = "albumid";
	private static final String MYLOVE_MUSIC_PATH = "path";
	private final String NULLCOLUMNHACK = "_id";
	private Context context;
	private DatabaseHelper dbHelper;

	public MusicService(Context context) {
		this.context = context;
		dbHelper = DatabaseHelper.getDBHelperInstance(context);
	}// 添加 .连数据库

	/** 添加我喜欢数据 */
	public boolean addMyZuijin(Mp3Info info) throws Exception {
		// 读取数据库
		SQLiteDatabase sdb = dbHelper.getWritableDatabase();
		// 如果数据库是打开的
		if (sdb.isOpen()) {
			ContentValues values = new ContentValues();
			values.put(MYLOVE_MUSIC_ID, info._id);
			values.put(MYLOVE_MUSIC_TITLE, info.title);
			values.put(MYLOVE_MUSIC_DURATION, info.duration);
			values.put(MYLOVE_MUSIC_ARTIST, info.artist);
			values.put(MYLOVE_MUSIC_ALBUMID, info.albumid);
			values.put(MYLOVE_MUSIC_PATH, info.path);
			long id = sdb.insert(TABLE_NAME_ZUIJIN, NULLCOLUMNHACK, values);
			return id > 0 ? true : false;
		}
		return false;
	}
	
	/** 添加我喜欢数据 */
	public boolean addMyLove(Mp3Info info) throws Exception {
		// 读取数据库
		SQLiteDatabase sdb = dbHelper.getWritableDatabase();
		// 如果数据库是打开的
		if (sdb.isOpen()) {
			ContentValues values = new ContentValues();
			values.put(MYLOVE_MUSIC_ID, info._id);
			values.put(MYLOVE_MUSIC_TITLE, info.title);
			values.put(MYLOVE_MUSIC_DURATION, info.duration);
			values.put(MYLOVE_MUSIC_ARTIST, info.artist);
			values.put(MYLOVE_MUSIC_ALBUMID, info.albumid);
			values.put(MYLOVE_MUSIC_PATH, info.path);
			long id = sdb.insert(TABLE_NAME_MUSCI, NULLCOLUMNHACK, values);
			return id > 0 ? true : false;
		}
		return false;
	}

	// 删除
	public void delByMusicId(int musicId) throws Exception {
		// 读取数据库
		SQLiteDatabase sdb = dbHelper.getWritableDatabase();
		// 如果数据库是打开的
		if (sdb.isOpen()) {
			String sql = "delete from mylove where music_id = ?";
			// 为问号赋值
			sdb.execSQL(sql, new Object[] { musicId });
		}
	}

	// 查询
	public List<Mp3Info> findAll() throws Exception {
		List<Mp3Info> lists = new ArrayList<Mp3Info>();
		// 读取数据库
		SQLiteDatabase sdb = dbHelper.getReadableDatabase();
		// 如果数据库是打开的
		if (sdb.isOpen()) {
			String sql = "select * from mylove";
			// 为问号赋值
			Cursor cs = sdb.rawQuery(sql, null);// rs结果集
			if (cs != null) {
				while (cs.moveToNext()) {// rs.next
				// int _id = cs.getInt(cs.getColumnIndex("_id"));
					int music_id = cs.getInt(cs.getColumnIndex("music_id"));
					String title = cs.getString(cs.getColumnIndex("title"));
					int cardId = cs.getInt(cs.getColumnIndex("duration"));
					String artist = cs.getString(cs.getColumnIndex("artist"));
					long albumid = cs.getLong(cs.getColumnIndex("albumid"));
					String path = cs.getString(cs.getColumnIndex("path"));
					lists.add(new Mp3Info(music_id, title, cardId, null,
							artist, albumid,path));
				}
				// 记得关
				cs.close();
			}
		}
		return lists;
	}
	
	// 查询
		public List<Mp3Info> findAllZuijin() throws Exception {
			List<Mp3Info> lists = new ArrayList<Mp3Info>();
			// 读取数据库
			SQLiteDatabase sdb = dbHelper.getReadableDatabase();
			// 如果数据库是打开的
			if (sdb.isOpen()) {
				String sql = "select * from myzuijin order by _id desc limit 0,8";
				// 为问号赋值
				Cursor cs = sdb.rawQuery(sql, null);// rs结果集
				if (cs != null) {
					while (cs.moveToNext()) {// rs.next
					// int _id = cs.getInt(cs.getColumnIndex("_id"));
						int music_id = cs.getInt(cs.getColumnIndex("music_id"));
						String title = cs.getString(cs.getColumnIndex("title"));
						int cardId = cs.getInt(cs.getColumnIndex("duration"));
						String artist = cs.getString(cs.getColumnIndex("artist"));
						long albumid = cs.getLong(cs.getColumnIndex("albumid"));
						String path = cs.getString(cs.getColumnIndex("path"));
						lists.add(new Mp3Info(music_id, title, cardId, null,
								artist, albumid,path));
					}
					// 记得关
					cs.close();
				}
			}
			return lists;
		}
}
