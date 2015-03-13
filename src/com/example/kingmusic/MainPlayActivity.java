package com.example.kingmusic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.example.kingmusic.entity.Mp3Info;

public class MainPlayActivity extends Activity {

	private static final String TAG = "MainPlayActivity";
	int j = 0;
	int i = 0;
	private Intent intent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main_play);
		Intent intent = getIntent();
		Mp3Info info = (Mp3Info) intent.getSerializableExtra("currInfo");
		j = intent.getIntExtra("j", 0);
		Log.i(TAG, info.title + "=====" + j);
		i = info.duration / 1000;
		new Thread() {
			public void run() {
				// todo
				while (j <= i) {
					// tv_number.setText(i+"");
					// 第二步：创建一个消息对象
					Message message = new Message();
					message.what = 1;// 识别
					message.obj = j;// 传值
					// 第三步：发送
					mHandler.sendMessage(message);// 去handelMessage

					SystemClock.sleep(1000);
					j++;
				}
			};
		}.start();
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int a = (Integer) msg.obj;
			switch (msg.what) {
			case 1:
				Log.i(TAG, j + "====");
				break;
			default:
				break;
			}
		};
	};
	
	public void back(View view){
		intent = new Intent(this, PlayListActivity.class);
		intent.putExtra("mainJ", j);
		Log.i(TAG, j+"aaaaaaaaaaa");
		startActivity(intent);
		finish();
	}

}
