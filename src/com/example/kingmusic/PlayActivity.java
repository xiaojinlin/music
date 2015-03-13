package com.example.kingmusic;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kingmusic.dao.Mp3Dao;
import com.example.kingmusic.entity.Mp3Info;
import com.example.kingmusic.fragment.ListFragment;
import com.example.kingmusic.fragment.LrcFragment;
import com.example.kingmusic.fragment.PlayMoreFragment;
import com.example.kingmusic.iface.IMusic;
import com.example.kingmusic.iface.MyIBinder;
import com.example.kingmusic.service.MusicService;
import com.example.kingmusic.util.BgUtil;

public class PlayActivity extends FragmentActivity implements OnClickListener {
	private static final String TAG = "PlayActivity";
	private ViewPager vp_play;
	private List<Fragment> lists;
	private FragmentPagerAdapter mAdapter;
	private ImageButton ib_model, ib_shang, ib_play, ib_next, ib_love;
	private IBinder binder;
	private TextView tv_play_titlename;
	private IMusic iMusic;
	private Mp3Info info;
	private SeekBar sb_play;
	private Thread mThread;
	private Mp3Dao dao;
	private List<Mp3Info> infoLists;
	private Intent intent;
	private LinearLayout ll_list_back, ll_play;
	private MusicService musicService;

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		intent.putExtra("currInfo", info);
		setResult(100, intent);
		finish();
	}
	
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_play);
		init();
		musicService = new MusicService(this);
		dao = new Mp3Dao(this);
		infoLists = dao.getAllmp3();
		FragmentOperation();
		intent = getIntent();
		Bundle bundle = intent.getBundleExtra("mBundle");
		binder = bundle.getBinder("myBinder");
		iMusic = (IMusic) binder;
		if (iMusic.getInfo() != null) {
			info = iMusic.getInfo();
			setBg(iMusic.getInfo());
		}
		if (info != null) {
			mThread = new Thread() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					super.run();
					while (true) {
						if (iMusic != null) {
							Message message = new Message();
							message.what = 111;// 识别
							message.obj = iMusic.getCurrentPosition() / 1000;// 传值
							// 第三步：发送
							mHandler.sendMessage(message);

						}
						SystemClock.sleep(200);
						// Log.i(TAG, iMusic.getCurrentPosition() + "当前位置");
					}
				}
			};
			mThread.start();
			sb_play.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					iMusic.seekTo(seekBar.getProgress());
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					// TODO Auto-generated method stub

				}
			});
		}
		DataOpr();
	}

	@SuppressLint("NewApi")
	private void setBg(Mp3Info info) {
		Bitmap bitmap = BgUtil.getArtworkFromFile(this, info._id, info.albumid);
		BitmapDrawable bd = new BitmapDrawable(bitmap);
		if (bitmap != null) {
			ll_play.setBackground(bd);
		} else {
			ll_play.setBackgroundResource(R.drawable.bg_lockscreen_default);
		}

	}

	private void DataOpr() {
		try {
			List<Mp3Info> loveList = musicService.findAll();
			Log.i(TAG, loveList.toString() + "===========LOVE");
			if (info != null) {
				for (int i = 0; i < loveList.size(); i++) {
					if (loveList.get(i)._id == info._id) {
						ib_love.setBackgroundResource(R.drawable.img_favourite_selected);
						isLove = true;
						return;
					} else {
						ib_love.setBackgroundResource(R.drawable.img_favourite_normal);
						isLove = false;
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			sb_play.setMax(iMusic.getProgressMax());
			int a = (Integer) msg.obj;
			int id = msg.what;
			switch (id) {
			case 111:
				sb_play.setProgress(a);

				if (iMusic.getCurrIsPlaying()) {
					setPause();
				} else {
					setPlay();
				}
				if (iMusic.getInfo() != info) {
					info = iMusic.getInfo();
					if (info != null) {
						tv_play_titlename.setText(info.title + "-"
								+ info.artist);
					}
				}
				tv_play_titlename.setText(info.title + "-" + info.artist);
				if (a == info.duration / 1000) {
					// Log.i(TAG, currIndex + "===" + info.name);

					info = iMusic.nextTo(infoLists, info);
					tv_play_titlename.setText(info.title + "-" + info.artist);
					iMusic.play(info);
					DataOpr();
				}

				break;

			default:
				break;
			}
		};
	};

	private void FragmentOperation() {
		lists = new ArrayList<Fragment>();
		ListFragment lsitF = new ListFragment();
		LrcFragment lrcF = new LrcFragment();
		lrcF.setImusic(new MyIBinder() {
			
			@Override
			public IBinder getIBinder() {
				// TODO Auto-generated method stub
				return binder;
			}
		});
		PlayMoreFragment pmf = new PlayMoreFragment();
		// 将fragment放入队列中
		lists.add(lsitF);
		lists.add(lrcF);
		lists.add(pmf);
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
		vp_play.setAdapter(mAdapter);
		vp_play.setCurrentItem(1);
	}

	private void init() {
		vp_play = (ViewPager) findViewById(R.id.vp_play);
		ib_model = (ImageButton) findViewById(R.id.ib_model);
		ib_shang = (ImageButton) findViewById(R.id.ib_shang);
		ib_play = (ImageButton) findViewById(R.id.ib_play);
		ib_next = (ImageButton) findViewById(R.id.ib_next);
		ib_love = (ImageButton) findViewById(R.id.ib_love);
		tv_play_titlename = (TextView) findViewById(R.id.tv_play_titlename);
		sb_play = (SeekBar) findViewById(R.id.sb_play);
		ll_list_back = (LinearLayout) findViewById(R.id.ll_list_back);
		ll_play = (LinearLayout) findViewById(R.id.ll_play);
		ll_list_back.setOnClickListener(this);
		ib_model.setOnClickListener(this);
		ib_shang.setOnClickListener(this);
		ib_play.setOnClickListener(this);
		ib_next.setOnClickListener(this);
		ib_love.setOnClickListener(this);
	}

	private boolean isLove = false;
	private Boolean isPlay = false;

	private void setPlay() {
		ib_play.setBackgroundResource(R.drawable.play_selector);
		isPlay = true;
	}

	private void setPause() {

		ib_play.setBackgroundResource(R.drawable.pause_selector);
		isPlay = false;
	}

	/** 检测是播放的哪个列表的歌曲 */
	private void checkList(Mp3Info info) {
		if (info.name == null) {
			try {
				infoLists = musicService.findAll();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.ib_model:

			break;
		case R.id.ib_shang:
			checkList(iMusic.getInfo());
			setBg(iMusic.upTo(infoLists, iMusic.getInfo()));
			if (info != null && iMusic.getCurrIsPlaying() == true) {
				if (info != iMusic.upTo(infoLists, info)) {
					info = iMusic.upTo(infoLists, info);
					if (info != null) {
						tv_play_titlename.setText(info.title + "-"
								+ info.artist);
						iMusic.play(info);
						DataOpr();
					}
				} else {
					Toast.makeText(this, "已到第一首", Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(this, "请选择歌曲播放", Toast.LENGTH_LONG).show();
			}
			break;
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
		case R.id.ib_next:
			setBg(iMusic.nextTo(infoLists, iMusic.getInfo()));
			if (info != null && iMusic.getCurrIsPlaying() == true) {
				if (info != iMusic.nextTo(infoLists, info)) {
					info = iMusic.nextTo(infoLists, info);
					if (info != null) {
						tv_play_titlename.setText(info.title + "-"
								+ info.artist);
						iMusic.play(info);
						DataOpr();
					}
				} else {
					Toast.makeText(this, "已到最后一首", Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(this, "请选择歌曲播放", Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.ib_love:
			if (isLove == false) {
				ib_love.setBackgroundResource(R.drawable.img_favourite_selected);
				if (info != null) {
					try {
						musicService.addMyLove(info);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				Toast.makeText(this, "已添加到我喜欢", Toast.LENGTH_LONG).show();
				isLove = true;
			} else {
				ib_love.setBackgroundResource(R.drawable.img_favourite_normal);
				if (info != null) {
					try {
						musicService.delByMusicId(info._id);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				Toast.makeText(this, "已取消喜欢", Toast.LENGTH_LONG).show();
				isLove = false;
			}
			break;
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
