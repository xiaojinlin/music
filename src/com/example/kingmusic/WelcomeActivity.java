package com.example.kingmusic;

import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kingmusic.adapter.ViewPagerAdapter;
import com.example.kingmusic.service.ViewService;

public class WelcomeActivity extends Activity implements OnClickListener {
	private static final String TAG = "WelcomeActivity";
	private ViewPager vp_welcome;
	private ViewPagerAdapter mAdapter;
	private List<View> lists;
	private ViewService mService;
	private LayoutInflater mInflater;
	private Button ib_moveon;
	private MediaPlayer mPlayer;
	private String SHORTCUT_NAME = "王者音乐";
	private int[] res = { R.drawable.welcome, R.drawable.welcome1,
			R.drawable.welcome2, R.drawable.welcome3, R.drawable.welcome4,
			R.drawable.welcome5, R.drawable.welcome6, R.drawable.welcome7,
			R.drawable.welcome8, R.drawable.welcome9, R.drawable.welcome10,
			R.drawable.welcome11, R.drawable.welcome12, R.drawable.welcome13,
			R.drawable.welcome14, R.drawable.welcome15, R.drawable.welcome16,
			R.drawable.welcome17, R.drawable.welcome18, R.drawable.welcome19,
			R.drawable.welcome20, R.drawable.welcome21, R.drawable.welcome22,
			R.drawable.welcome23, R.drawable.welcome24, R.drawable.welcome25,
			R.drawable.welcome26 };
	private ImageView iv_welcome;
	private int i;;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_welcome);
		createTubiao();
		Random random = new Random();
		i = random.nextInt(27);
		init();
		// 随机产生一个数
		Log.i(TAG, i + "");
		mPlayer = MediaPlayer.create(this, R.raw.ring);
		mPlayer.start();
		new Thread() {
			public void run() {
				if (mPlayer.isPlaying()) {
					SystemClock.sleep(4500);
					Message message = new Message();
					message.what = 1;
					mHandler.sendMessage(message);
				}
			};
		}.start();
		// fragment操作
		// DrawableOperation();
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int id = msg.what;
			switch (id) {
			case 1:
				Intent intent = new Intent(WelcomeActivity.this,
						MainActivity.class);
				mPlayer.stop();
				startActivity(intent);
				finish();
				break;

			default:
				break;
			}
		};
	};

	private void DrawableOperation() {
		// mService = new ViewService(this);
		// mAdapter = new ViewPagerAdapter(mService.getPagerView());
		// vp_welcome.setAdapter(mAdapter);
	}

	private void init() {
		// vp_welcome = (ViewPager) findViewById(R.id.vp_welcome);
		iv_welcome = (ImageView) findViewById(R.id.iv_welcome);
		// 设置默认图片
		iv_welcome.setBackgroundResource(res[i]);
	}

	private void createTubiao() {
		if (isExist()) {
			return;
		}
		Intent intent = new Intent();
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		Parcelable value = Intent.ShortcutIconResource.fromContext(
				getApplicationContext(), R.drawable.icon);
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, value);
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, SHORTCUT_NAME);
		Intent targetIntent = new Intent();
		targetIntent.setAction(Intent.ACTION_MAIN);
		targetIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		ComponentName componentName = new ComponentName(this,
				WelcomeActivity.class);
		targetIntent.setComponent(componentName);
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, targetIntent);
		sendBroadcast(intent);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.ib_moveon:
			Toast.makeText(this, "haha ", Toast.LENGTH_LONG).show();
			break;

		default:
			break;
		}
	}

	// 判断是否有快捷图标
	// 判断快捷图标是否存在
	private boolean isExist() {
		boolean isExist = false;
		Uri uri = null;
		if (getSDKVersion() >= 8) {
			uri = Uri
					.parse("content://com.android.launcher2.settings/favorites");
		} else {
			uri = Uri
					.parse("content://com.android.launcher.settings/favorites");
		}
		String selection = "title = ?";
		String[] selectionArgs = new String[] { SHORTCUT_NAME };
		Cursor cursor = getContentResolver().query(uri, null, selection,
				selectionArgs, null);
		if (cursor.moveToFirst()) {
			isExist = true;
		}
		cursor.close();
		return isExist;
	}

	// 得到当前系统sdk的版本
	private int getSDKVersion() {
		return android.os.Build.VERSION.SDK_INT;
	}

}
