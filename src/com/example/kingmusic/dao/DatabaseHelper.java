package com.example.kingmusic.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "music.db";
	private static final int VERSION =3;
	private static DatabaseHelper dbHelper;
	//1.创建数据库  私有化
	private DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	//提供一个公共的静态的方法
	public static  DatabaseHelper getDBHelperInstance (Context context){
		if(dbHelper == null){
			dbHelper = new DatabaseHelper(context, DATABASE_NAME, null, VERSION);
		}
		return dbHelper;
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table mylove(_id Integer primary key autoincrement,music_id Integer,title text,duration long ,artist text,albumid long,path text)";
		//
		String sql1 = "create table myzuijin(_id Integer primary key autoincrement,music_id Integer,title text,duration long ,artist text,albumid long,path text)";
		//
		db.execSQL(sql);
		db.execSQL(sql1);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
