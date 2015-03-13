package com.example.kingmusic.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.kingmusic.MainActivity;
import com.example.kingmusic.R;
import com.example.kingmusic.broadcastreceiver.PlayBroadcastReceiver;
import com.example.kingmusic.dao.Mp3Dao;
import com.example.kingmusic.entity.Mp3Info;
import com.example.kingmusic.iface.IMusic;
import com.example.kingmusic.provider.MyAppWidgetProvider;
import com.example.kingmusic.util.BgUtil;
import com.example.kingmusic.util.Mp3Util;

public class Mp3Service extends Service {
	private static final String TAG = "Mp3Service";
	private MediaPlayer mp;
	private Mp3Info info;
	private MusicService mService;
	private List<Mp3Info> lists;
	private Mp3Dao dao;
	private Timer timer = new Timer(); // 定时器
	private PlayBroadcastReceiver receiver;
	// 指定任务
	private TimerTask task = new TimerTask() {

		@Override
		public void run() {

			walkup();
			if (info != null ) {
				getNotification();
			}
		}
	};

	@Override
	public void onCreate() {
		receiver = new PlayBroadcastReceiver();
		dao = new Mp3Dao(getApplicationContext());
		lists = dao.getAllmp3();
		// 音乐播放
		mp = new MediaPlayer();
		// 1.重置
		mp.reset();
		mService = new MusicService(this);
		// 调度任务 delay延时 period间隔
		timer.schedule(task, 0, 100);
		Log.i(TAG, "onCreate========");
		super.onCreate();
	}

	private void walkup() {

		// 显示到控件 控件所在的xml
		RemoteViews views = new RemoteViews(getApplicationContext()
				.getPackageName(), R.layout.my_appwidget);
		if (mp.isPlaying()) {
			views.setImageViewResource(R.id.appwidget_play,
					R.drawable.kg_ic_playing_bar_pause_default);
		} else {
			views.setImageViewResource(R.id.appwidget_play,
					R.drawable.kg_ic_playing_bar_play_default);
		}
		if (info != null) {
			views.setTextViewText(R.id.appwidget_text, info.title);
			Bitmap bitmap = BgUtil.getArtworkFromFile(getApplicationContext(),
					info._id, info.albumid);
			if (bitmap != null) {
				Bitmap bitmap1 = BgUtil.resizeImage1(bitmap, 80, 80);
				views.setImageViewBitmap(R.id.appwidget_icon, bitmap1);
			} else {
				views.setImageViewResource(R.id.appwidget_icon,
						R.drawable.ic_launcher);
			}
			Intent mIntentNext = new Intent(Mp3Service.this,
					PlayBroadcastReceiver.class);
			mIntentNext.putExtra("operInfo", Mp3Util.nextTo(lists, info));
			PendingIntent intentNext = PendingIntent.getBroadcast(this, 1,
					mIntentNext, PendingIntent.FLAG_CANCEL_CURRENT);

			views.setOnClickPendingIntent(R.id.appwidget_next, intentNext);

			Intent mIntentPlay = new Intent(Mp3Service.this,
					PlayBroadcastReceiver.class);
			mIntentPlay.putExtra("operInfo", info);
			PendingIntent intentPlay = PendingIntent.getBroadcast(this, 2,
					mIntentPlay, PendingIntent.FLAG_CANCEL_CURRENT);
			views.setOnClickPendingIntent(R.id.appwidget_play, intentPlay);

			Intent mIntentPrev = new Intent(Mp3Service.this,
					PlayBroadcastReceiver.class);
			mIntentPrev.putExtra("operInfo", Mp3Util.upTo(lists, info));
			PendingIntent intentPrev = PendingIntent.getBroadcast(this, 3,
					mIntentPrev, PendingIntent.FLAG_CANCEL_CURRENT);

			views.setOnClickPendingIntent(R.id.appwidget_shang, intentPrev);
		} else {
			views.setImageViewResource(R.id.appwidget_icon,
					R.drawable.ic_launcher);
			views.setImageViewResource(R.id.appwidget_play,
					R.drawable.kg_ic_playing_bar_play_default);
			views.setTextViewText(R.id.appwidget_text, "王者音乐");
		}

		// 点击事件
		PendingIntent pendingIntent = PendingIntent.getActivity(
				getApplicationContext(), 100, new Intent(
						getApplicationContext(), MainActivity.class), 0);
		views.setOnClickPendingIntent(R.id.appwidget_icon, pendingIntent);

		// 更新
		AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(getApplicationContext());
		// appWidgetManager.updateAppWidget(appWidgetIds, views);
		ComponentName componentName = new ComponentName(
				getApplicationContext(), MyAppWidgetProvider.class);

		appWidgetManager.updateAppWidget(componentName, views);

		// 主界面-->广播-->服务-->广播
	}

	@Override
	public IBinder onBind(Intent intent) {
		// 能获到数据
		info = (Mp3Info) intent.getSerializableExtra("info");
		if (info != null) {
			Uri uri = ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI,
					info._id);
			try {
				mp.reset();
				// 设置播放的路径：可以是指定的SDCARD也可是媒体库中 URI
				mp.setDataSource(this, uri);
				mp.prepare();
				mp.start();
				getNotification();
				mService.addMyZuijin(info);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return new MyMusic();
	}

	private class MyMusic extends Binder implements IMusic {

		@Override
		public Mp3Info pause() {
			if (mp != null) {
				mp.pause();// 暂停的方法
			}
			return info;
		}

		@Override
		public void moveon() {
			if (mp != null) {
				mp.start();// 播放的方法
			}

		}

		@Override
		public void stop() {
			if (mp != null) {
				if (mp.isPlaying()) {
					mp.stop();
				}
			}

		}

		@Override
		public void seekTo(int aMsec) {
			if (mp != null) {
				int a = aMsec;
				Log.i(TAG, a + "=========");
				mp.seekTo(aMsec * 1000);
			}
		}

		@Override
		public int getCurrentPosition() {
			int a = 0;
			if (mp != null) {
				// if (mp.isPlaying()) {
				// return mp.getCurrentPosition();
				// }
				a = mp.getCurrentPosition();
			}
			return a;
		}

		@Override
		public void play(Mp3Info info) {
			Mp3Service.this.info = info;
			// 能获到数据
			if (info != null) {
				Uri uri = ContentUris.withAppendedId(
						Media.EXTERNAL_CONTENT_URI, info._id);
				try {
					mp.reset();
					// 设置播放的路径：可以是指定的SDCARD也可是媒体库中 URI
					mp.setDataSource(Mp3Service.this, uri);
					mp.prepare();
					mp.start();
					getNotification();
					mService.addMyZuijin(info);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		/** 下一首 */
		@Override
		public Mp3Info nextTo(List<Mp3Info> list, Mp3Info info) {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).get_id() == info._id) {
					int s = i;
					if (i == list.size()) {
						s = 0;
					} else {
						s += 1;
					}
					if (s >= 0 && s < list.size()) {
						info = list.get(s);
					}
					return info;
				}

			}
			return null;
		}

		@Override
		public Mp3Info getInfo() {
			if (mp != null) {
				return info;
			}
			return null;
		}

		@Override
		public int getProgressMax() {
			if (mp != null) {
				int a = info.duration / 1000;
				return a;
			}
			return 0;
		}

		@Override
		public boolean getCurrIsPlaying() {
			if (mp != null) {
				return mp.isPlaying();
			}
			return false;
		}

		@Override
		public Mp3Info upTo(List<Mp3Info> list, Mp3Info info) {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).get_id() == info._id) {
					int s = i;
					if (i == list.size()) {
						s = 0;
					} else {
						s -= 1;
					}
					if (s >= 0 && s <= list.size()) {
						info = list.get(s);
					}
					return info;
				}

			}
			return null;
		}

	}

	@Override
	public void onDestroy() {

		if (mp != null) {
			mp.stop();
			info = null;
			walkup();
			timer.cancel();
		}

		super.onDestroy();
	}

	public void getNotification() {
		// 1.Get a reference to the NotificationManager:
		final NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// 2.Instantiate the Notification:
		final Notification nf = new Notification();
		nf.icon = R.drawable.ic_launcher;// 图标
		nf.tickerText = "正在播放...";// 提示
		nf.when = System.currentTimeMillis();// 时间

		// 1.Create the XML layout for the notification. For example, the
		// following layout is called
		// 2.RemoveViews
		RemoteViews views = new RemoteViews(getApplicationContext()
				.getPackageName(), R.layout.my_appwidget);
		if (mp.isPlaying()) {
			views.setImageViewResource(R.id.appwidget_play,
					R.drawable.kg_ic_playing_bar_pause_default);
		} else {
			views.setImageViewResource(R.id.appwidget_play,
					R.drawable.kg_ic_playing_bar_play_default);
		}
		if (info != null) {
			views.setTextViewText(R.id.appwidget_text, info.title);
			Bitmap bitmap = BgUtil.getArtworkFromFile(getApplicationContext(),
					info._id, info.albumid);
			if (bitmap != null) {
				Bitmap bitmap1 = BgUtil.resizeImage1(bitmap, 80, 80);
				views.setImageViewBitmap(R.id.appwidget_icon, bitmap1);
			} else {
				views.setImageViewResource(R.id.appwidget_icon,
						R.drawable.ic_launcher);
			}
			Intent mIntentNext = new Intent(Mp3Service.this,
					PlayBroadcastReceiver.class);
			mIntentNext.putExtra("operInfo", Mp3Util.nextTo(lists, info));
			PendingIntent intentNext = PendingIntent.getBroadcast(this, 11,
					mIntentNext, PendingIntent.FLAG_CANCEL_CURRENT);

			views.setOnClickPendingIntent(R.id.appwidget_next, intentNext);

			Intent mIntentPlay = new Intent(Mp3Service.this,
					PlayBroadcastReceiver.class);
			mIntentPlay.putExtra("operInfo", info);
			PendingIntent intentPlay = PendingIntent.getBroadcast(this, 12,
					mIntentPlay, PendingIntent.FLAG_CANCEL_CURRENT);
			views.setOnClickPendingIntent(R.id.appwidget_play, intentPlay);

			Intent mIntentPrev = new Intent(Mp3Service.this,
					PlayBroadcastReceiver.class);
			mIntentPrev.putExtra("operInfo", Mp3Util.upTo(lists, info));
			PendingIntent intentPrev = PendingIntent.getBroadcast(this, 13,
					mIntentPrev, PendingIntent.FLAG_CANCEL_CURRENT);

			views.setOnClickPendingIntent(R.id.appwidget_shang, intentPrev);
		} else {
			views.setImageViewResource(R.id.appwidget_icon,
					R.drawable.ic_launcher);
			views.setImageViewResource(R.id.appwidget_play,
					R.drawable.kg_ic_playing_bar_play_default);
			views.setTextViewText(R.id.appwidget_text, "王者音乐");
		}

		
		nf.contentView =views;
		// 3.设置延时意图
		Intent contentIntent = new Intent(this, MainActivity.class);
		contentIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// 延时意图：对上面的意进行封装
		PendingIntent pendingIntent1 = PendingIntent.getActivity(this, 100,
				contentIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		// 指定延时意图
		nf.contentIntent = pendingIntent1;
		// 做操作
		// 4.启动
		nm.notify(100, nf);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// 收到广播来的内容
		Mp3Info s = (Mp3Info) intent.getSerializableExtra("caozuoInfo");
		if (s != null && info != null) {
			if (info.get_id() != s.get_id()) {
				info = s;
				Uri uri = ContentUris.withAppendedId(
						Media.EXTERNAL_CONTENT_URI, info.get_id());
				mp.reset();
				try {
					mp.setDataSource(Mp3Service.this, uri);
					mp.prepare();
					mp.start();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				if (mp.isPlaying()) {
					mp.pause();
					getNotification();
				} else {
					mp.start();
					getNotification();
				}
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

}
