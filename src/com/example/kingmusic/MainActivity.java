package com.example.kingmusic;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kingmusic.dao.DatabaseHelper;
import com.example.kingmusic.dao.Mp3Dao;
import com.example.kingmusic.entity.Mp3Info;
import com.example.kingmusic.fragment.FindFragment;
import com.example.kingmusic.fragment.MineFragment;
import com.example.kingmusic.fragment.MoreFragment;
import com.example.kingmusic.iface.IMusic;
import com.example.kingmusic.iface.MyIBinder;
import com.example.kingmusic.service.Mp3Service;
import com.example.kingmusic.service.MusicService;
import com.example.kingmusic.util.BgUtil;

public class MainActivity extends FragmentActivity implements OnClickListener {
	private static final String TAG = "MainActivity";
	private ImageButton ib_play;
	private ViewPager vp_main;
	private List<Fragment> lists;
	private FragmentPagerAdapter mAdapter;
	private LinearLayout ll_mine, ll_find, ll_more, ll_bottom;
	private ImageButton iv_mine, iv_find, iv_more, ib_playlist_next;
	private TextView tv_mine, tv_find, tv_more, tv_main_title, tv_main_autor;
	private IBinder iBinder;
	private MyServiceConnection conn;
	private Mp3Info info = null;
	private IMusic iMusic;
	private List<Mp3Info> lists1;
	private Mp3Dao dao;
	private int currIndex;
	private Boolean isPlay = false;
	private MusicService mService;
	private Thread mThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		init();
		dao = new Mp3Dao(this);
		lists1 = dao.getAllmp3();
		Log.i(TAG, lists1 + "===============");
		mService = new MusicService(this);
		try {
			List<Mp3Info> findAllZuijin = mService.findAllZuijin();
			Log.i(TAG, findAllZuijin.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// fragment操作
		FragmentOperation();
		getLlIvOnClick();
		Intent intentz = new Intent(this, Mp3Service.class);
		intentz.putExtra("info", info);
		conn = new MyServiceConnection();
		System.out.println(conn + "=========");
		bindService(intentz, conn, BIND_AUTO_CREATE);
		DatabaseHelper dbHelper = DatabaseHelper.getDBHelperInstance(this);
		dbHelper.getReadableDatabase();
		if (mThread == null ) {
			mThread = new Thread() {
				@Override
				public void run() {
					super.run();
					
					while (true) {
						if (iMusic != null) {
							Message message = new Message();
							message.what = 111;// 识别
							// 第三步：发送
							mHandler.sendMessage(message);

						}
						SystemClock.sleep(200);
						// Log.i(TAG, iMusic.getCurrentPosition() + "当前位置");
					}
				}
			};
			mThread.start();
		}
	}
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 111:
				if (iMusic != null && info !=null) {
					if (iMusic.getCurrIsPlaying()) {
						setPause();
					} else {
						setPlay();
					}
					tv_main_title.setText(iMusic.getInfo().title);
					tv_main_autor.setText(iMusic.getInfo().artist);
				}
				break;

			default:
				break;
			}

		};
	};
	@Override	
	protected void onDestroy() {
		if (conn != null) {
			unbindService(conn);
			if (iMusic != null) {
				iMusic.stop();
			}
		}
		finish();
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		Mp3Info info2 = (Mp3Info) arg2.getSerializableExtra("currInfo");
		if (info2 != null) {
			currIndex = arg2.getIntExtra("currIndex", 0);
			info = info2;
			tv_main_title.setText(info.title);
			tv_main_autor.setText(info.artist);

			isPlay = arg2.getBooleanExtra("isPlay", false);
			if (iMusic.getCurrIsPlaying()) {
				setPause();
			} else {
				setPlay();
			}
		}
		if (info2 != null) {
			currIndex = arg2.getIntExtra("currIndex", 0);
			info = info2;
			tv_main_title.setText(iMusic.getInfo().title);
			tv_main_autor.setText(iMusic.getInfo().artist);
			isPlay = arg2.getBooleanExtra("isPlay", false);
			if (iMusic.getCurrIsPlaying()) {
				setPause();
			} else {
				setPlay();
			}
		}
	}

	private class MyServiceConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			iBinder = service;
			iMusic = (IMusic) service;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

	}

	private void FragmentOperation() {
		// 创建一个fragment队列
		lists = new ArrayList<Fragment>();
		MineFragment mf = new MineFragment();
		mf.setMYIBinder(new MyIBinder() {

			@Override
			public IBinder getIBinder() {
				// TODO Auto-generated method stub
				return iBinder;
			}
		});
		FindFragment lf = new FindFragment();
		MoreFragment df = new MoreFragment();
		// 将fragment放入队列中
		lists.add(mf);
		lists.add(lf);
		lists.add(df);
		// 设置适配器
		mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return lists.size();
			}

			@Override
			public Fragment getItem(int arg0) {
				// TODO Auto-generated method stub
				return lists.get(arg0);
			}
		};
		// 将viewpager与适配器绑定
		vp_main.setAdapter(mAdapter);
		vp_main.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				getDfaultView();
				switch (arg0) {
				case 0:
					iv_mine.setBackgroundResource(R.drawable.my_music_self_clicked);
					tv_mine.setTextColor(Color.parseColor("#FFFFFF"));
					ll_mine.setBackgroundColor(Color.parseColor("#1ABBFF"));
					break;
				case 1:
					iv_find.setBackgroundResource(R.drawable.my_music_box_clicked);
					tv_find.setTextColor(Color.parseColor("#FFFFFF"));
					ll_find.setBackgroundColor(Color.parseColor("#1ABBFF"));
					break;
				case 2:
					iv_more.setBackgroundResource(R.drawable.my_music_setting_clicked);
					tv_more.setTextColor(Color.parseColor("#FFFFFF"));
					ll_more.setBackgroundColor(Color.parseColor("#1ABBFF"));
					break;

				default:
					break;
				}
			}

			private void getDfaultView() {
				// 初始化底栏所有图标
				tv_mine.setTextColor(Color.parseColor("#949494"));
				tv_find.setTextColor(Color.parseColor("#949494"));
				tv_more.setTextColor(Color.parseColor("#949494"));
				ll_mine.setBackgroundColor(Color.parseColor("#EEEEEE"));
				ll_find.setBackgroundColor(Color.parseColor("#EEEEEE"));
				ll_more.setBackgroundColor(Color.parseColor("#EEEEEE"));
				iv_mine.setBackgroundResource(R.drawable.my_music_self_normal);
				iv_find.setBackgroundResource(R.drawable.my_music_box_normal);
				iv_more.setBackgroundResource(R.drawable.my_music_setting_normal);

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	private void getLlIvOnClick() {
		ll_mine.setOnClickListener(this);
		ll_find.setOnClickListener(this);
		ll_more.setOnClickListener(this);
		iv_mine.setOnClickListener(this);
		iv_find.setOnClickListener(this);
		iv_more.setOnClickListener(this);
	}

	private void init() {
		vp_main = (ViewPager) findViewById(R.id.vp_main);
		ll_mine = (LinearLayout) findViewById(R.id.ll_mine);
		ll_find = (LinearLayout) findViewById(R.id.ll_find);
		ll_more = (LinearLayout) findViewById(R.id.ll_more);
		iv_mine = (ImageButton) findViewById(R.id.iv_mine);
		iv_find = (ImageButton) findViewById(R.id.iv_find);
		iv_more = (ImageButton) findViewById(R.id.iv_more);
		tv_mine = (TextView) findViewById(R.id.tv_mine);
		tv_find = (TextView) findViewById(R.id.tv_find);
		tv_more = (TextView) findViewById(R.id.tv_more);
		ib_play = (ImageButton) findViewById(R.id.ib_play);
		tv_main_title = (TextView) findViewById(R.id.tv_main_title);
		tv_main_autor = (TextView) findViewById(R.id.tv_main_autor);
		ib_playlist_next = (ImageButton) findViewById(R.id.ib_playlist_next);
		ll_bottom = (LinearLayout) findViewById(R.id.ll_bottom);
		ib_playlist_next.setOnClickListener(this);
		ll_bottom.setOnClickListener(this);
		ib_play.setOnClickListener(this);
	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.ll_mine || id == R.id.iv_mine) {
			vp_main.setCurrentItem(0);
		} else if (id == R.id.ll_find || id == R.id.iv_find) {
			vp_main.setCurrentItem(1);
		} else if (id == R.id.ll_more || id == R.id.iv_more) {
			vp_main.setCurrentItem(2);
		}
		switch (id) {
		case R.id.ib_play:
			if (info != null) {
				if (isPlay) {
					setPause();
					iMusic.moveon();
				} else {
					setPlay();
					iMusic.pause();
				}
			} else {
				Toast.makeText(this, "请选择歌曲播放啊", Toast.LENGTH_LONG).show();
			}

			break;
		case R.id.ib_playlist_next:
			if (info != null) {
				info = iMusic.nextTo(lists1, info);
				tv_main_title.setText(info.title);
				tv_main_autor.setText(info.artist);
				iMusic.play(info);
			}
			break;
		case R.id.ll_bottom:
			Intent intent = new Intent(this, PlayActivity.class);
			Bundle bundle = new Bundle();
			bundle.putBinder("myBinder", iBinder);
			intent.putExtra("mBundle", bundle);
			startActivityForResult(intent, 200);
			break;
		default:
			break;
		}
	}

	private void setPlay() {
		ib_play.setBackgroundResource(R.drawable.playmusic_selector);
		isPlay = true;
	}

	private void setPause() {

		ib_play.setBackgroundResource(R.drawable.pausemusic_selector);
		isPlay = false;
	}

	/**
	 * 菜单、返回键响应
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitBy2Click(); // 调用双击退出函数
		}
		return false;
	}

	/**
	 * 双击退出函数
	 */
	private static Boolean isExit = false;

	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true; // 准备退出
			Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // 取消退出
				}
			}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

		} else {
			finish();
			System.exit(0);
		}
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
		final RemoteViews remoteViews = new RemoteViews(getPackageName(),
				R.layout.mynotifiy);
		// 为控件设置参数
		Bitmap bitmap = BgUtil.getArtworkFromFile(this, info.get_id(),
				info.getAlbumid());
		if (bitmap == null) {
			BitmapDrawable bd = (BitmapDrawable) getResources().getDrawable(
					R.drawable.ic_launcher);
			bitmap = bd.getBitmap();
		}
		// 为图片 哪个控件id 哪个资源
		remoteViews.setImageViewBitmap(R.id.iv_icon, bitmap);
		remoteViews.setTextViewText(R.id.tv_name, info.title);
		remoteViews.setTextViewText(R.id.tv_artist, info.artist);
		// 指定布局
		nf.contentView = remoteViews;
		// 3.设置延时意图
		Intent contentIntent = new Intent(this, MainActivity.class);
		contentIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// 延时意图：对上面的意进行封装
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 100,
				contentIntent, PendingIntent.FLAG_ONE_SHOT);
		// 指定延时意图
		nf.contentIntent = pendingIntent;
		// 做操作
		// 4.启动
		nm.notify(100, nf);
	}
}
