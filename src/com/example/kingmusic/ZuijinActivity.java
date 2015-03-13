package com.example.kingmusic;

import java.util.List;

import com.example.kingmusic.adapter.LoveAdapter;
import com.example.kingmusic.entity.Mp3Info;
import com.example.kingmusic.iface.IMusic;
import com.example.kingmusic.service.MusicService;

import android.os.Bundle;
import android.os.IBinder;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ZuijinActivity extends Activity implements OnClickListener{

	private ListView lv_love_lists;
	private MusicService mService;
	private LoveAdapter mAdapter;
	private Mp3Info info;
	private Intent intent;
	private LinearLayout ll_list_back;
	private TextView tv_love_love;
	private IBinder binder;
	private IMusic iMusic;
	private List<Mp3Info> lists;

	@Override
	public void onBackPressed() {
		intent.putExtra("currInfo", info);
		setResult(100, intent);
		finish();
	}

	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_zuijin);
		init();
		tv_love_love.setText("最近播放");
		mService = new MusicService(this);
		intent = getIntent();
		Bundle bundle = intent.getBundleExtra("mBundle1");
		binder = bundle.getBinder("mBinder1");
		iMusic = (IMusic) binder;
		try {
			lists = mService.findAllZuijin();
			mAdapter = new LoveAdapter(lists, this);
			lv_love_lists.setAdapter(mAdapter);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		lv_love_lists.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				if (iMusic != null) {
					info = (Mp3Info) mAdapter.getItem(position);
					iMusic.play(info);

				}
			}
		});

	}

	private void init() {
		lv_love_lists = (ListView) findViewById(R.id.lv_love_lists);
		ll_list_back = (LinearLayout) findViewById(R.id.ll_list_back);
		tv_love_love = (TextView) findViewById(R.id.tv_love_love);
		ll_list_back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.ll_list_back:
			intent.putExtra("currInfo", info);
			setResult(100, intent);
			finish();
			break;

		default:
			break;
		}
	}
}
