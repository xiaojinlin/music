package com.example.kingmusic;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kingmusic.adapter.Mp3Adapter;
import com.example.kingmusic.dao.Mp3Dao;
import com.example.kingmusic.entity.Mp3Info;
import com.example.kingmusic.iface.IMusic;

public class PlayListActivity extends Activity implements OnClickListener {
	private static final String TAG = "PlayListActivity";
	private ListView lv_play_lists;
	private Mp3Adapter mp3Adapter;
	private Intent intent;
	private IMusic iMusic;
	// private MyServiceConnection conn;
	private boolean isUnbind = true;
	private ImageButton ib_play, ib_playlist_next;
	private SeekBar sb_playlist;
	private int i = 0;
	private Thread myThread = null;
	private int currIndex = 0;
	private List<Mp3Info> lists;
	private RelativeLayout rl_bottom_main;
	private Mp3Info info = null;
	private IBinder iBinder;
	private LinearLayout ll_list_back;
	private Intent intent2;
	private TextView tv_main_title, tv_main_autor;
	private Thread mThread;

	/** 重写返回键 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		intent2.putExtra("currInfo", info);
		intent2.putExtra("currIndex", currIndex);
		intent2.putExtra("isPlay", isPlay);
		setResult(100, intent2);
		finish();
	}
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
	}
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_play_list);
		init();
		intent2 = getIntent();
		Bundle mBundle = intent2.getBundleExtra("mBundle");
		iBinder = mBundle.getBinder("mBinder");
		iMusic = (IMusic) iBinder;
		Mp3Dao dao = new Mp3Dao(this);
		lists = dao.getAllmp3();
		mp3Adapter = new Mp3Adapter(lists, this);
		lv_play_lists.setAdapter(mp3Adapter);
		registerForContextMenu(lv_play_lists);
		lv_play_lists.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				currIndex = position;
				startMp3(position);
				Log.i(TAG, currIndex + "===" + info.name);
				// notification();
			}
		});
		sb_playlist.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				iMusic.seekTo(seekBar.getProgress());
				if (!isPlay) {
					// if (iMusic != null) {
					setPause();
					iMusic.moveon();
					// }

				}

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (iMusic != null) {
					if (fromUser == true) {
						// iMusic.seekTo(progress);
						// j = progress;
						int m = progress / 60;
						int s = progress % 60;
						String sm = m + "";
						String ss = s + "";
						if (sm.length() == 1) {
							sm = "0" + sm;
						}
						if (ss.length() == 1) {
							ss = "0" + ss;
						}
						// Log.i(TAG, sm+":"+ss+"===============");
						Toast.makeText(PlayListActivity.this, sm + ":" + ss,
								Toast.LENGTH_SHORT).show();
					}
				}

			}
		});
		if (mThread == null) {
			mThread = new Thread() {
				@Override
				public void run() {
					super.run();
					if (iMusic.getInfo() == null) {
						return;
					}
					while (true) {
						if (iMusic != null) {
							Message message = new Message();
							message.what = 111;// 识别
							message.obj = iMusic.getCurrentPosition() / 1000;// 传值
							// 第三步：发送
							mHandler3.sendMessage(message);

						}
						SystemClock.sleep(200);
						// Log.i(TAG, iMusic.getCurrentPosition() + "当前位置");
					}
				}
			};
			mThread.start();
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		menu.add(0, 0, 0, "设置为手机铃声");
		// super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int position = info.position;
		Mp3Info info2 = (Mp3Info) mp3Adapter.getItem(position);
		setRingtone(info2._id);
		Toast.makeText(getApplicationContext(), "设置完成", Toast.LENGTH_LONG)
				.show();
		return super.onContextItemSelected(item);
	}

	private void setRingtone(int _id) {
		// TODO Auto-generated method stub
		// 1.拼成新的uri
		Uri uri = ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, _id);
		RingtoneManager.setActualDefaultRingtoneUri(getApplicationContext(),
				RingtoneManager.TYPE_RINGTONE, uri);
	}

	private Handler mHandler3 = new Handler() {
		public void handleMessage(Message msg) {
			sb_playlist.setMax(iMusic.getProgressMax());
			int a = (Integer) msg.obj;
			switch (msg.what) {
			case 111:

				sb_playlist.setProgress(a);
				if (iMusic.getCurrIsPlaying()) {
					setPause();
				} else {
					setPlay();
				}
				if (iMusic.getInfo() != info) {
					info = iMusic.getInfo();
					if (info != null) {
						tv_main_title.setText(info.title);
						tv_main_autor.setText(info.artist);
					}
				}
				tv_main_title.setText(info.title);
				tv_main_autor.setText(info.artist);
				if (a == info.duration / 1000) {
					// Log.i(TAG, currIndex + "===" + info.name);
					if (currIndex + 1 == lists.size()) {
						Toast.makeText(PlayListActivity.this, "播放完毕",
								Toast.LENGTH_LONG).show();
						if (iMusic != null) {
							iMusic.stop();
							isUnbind = false;
						}
						sb_playlist.setProgress(0);
						return;
					}
					currIndex = currIndex + 1;
					// Log.i(TAG, currIndex + "播放前");
					// startMp3(currIndex);
				}

				break;

			default:
				break;
			}

		};
	};

	private void startMp3(int position) {
		info = (Mp3Info) mp3Adapter.getItem(position);
		i = info.duration / 1000;
		sb_playlist.setMax(i);
		if (iMusic != null) {
			iMusic.stop();
			isUnbind = false;
		}
		iMusic.play(info);
		tv_main_title.setText(info.title);
		tv_main_autor.setText(info.artist);
		// Intent intent = new Intent(PlayListActivity.this,
		// PlayActivity.class);
		// intent.putExtra("info", info);
		// startActivity(intent);

		setPause();
		if (myThread != null) {
			if (myThread.isAlive()) {
				return;
			}
		}
		// Log.i(TAG, iMusic.getCurrentPosition()+"ASDASD");
		myThread = new Thread() {
			public void run() {
				while (true) {
					if (iMusic != null) {
						Message message = new Message();
						message.what = 1;// 识别
						message.obj = iMusic.getCurrentPosition() / 1000;// 传值
						// 第三步：发送
						mHandler.sendMessage(message);

					}
					SystemClock.sleep(200);
					// Log.i(TAG, iMusic.getCurrentPosition() + "当前位置");
				}
				// todo
				// while (j <= i) {
				// // tv_number.setText(i+"");
				// // 第二步：创建一个消息对象
				// Message message = new Message();
				// message.what = 1;// 识别
				// message.obj = j;// 传值
				// // 第三步：发送
				// mHandler.sendMessage(message);// 去handelMessage
				//
				// SystemClock.sleep(1000);
				// if (!isPlay) {
				// j=iMusic.getCurrentPosition();
				// }
				// if (j == i) {
				// Message message2 = new Message();
				// message2.what = 10;
				// mHandler2.sendMessage(message2);
				// }
				// }
			}
		};
		myThread.start();
	}

	private void init() {
		lv_play_lists = (ListView) findViewById(R.id.lv_play_lists);
		ib_play = (ImageButton) findViewById(R.id.ib_play);
		sb_playlist = (SeekBar) findViewById(R.id.sb_playlist);
		ib_playlist_next = (ImageButton) findViewById(R.id.ib_playlist_next);
		rl_bottom_main = (RelativeLayout) findViewById(R.id.rl_bottom_main);
		ll_list_back = (LinearLayout) findViewById(R.id.ll_list_back);
		tv_main_title = (TextView) findViewById(R.id.tv_main_title);
		tv_main_autor = (TextView) findViewById(R.id.tv_main_autor);
		ll_list_back.setOnClickListener(this);
		ib_play.setOnClickListener(this);
		rl_bottom_main.setOnClickListener(this);
		ib_playlist_next.setOnClickListener(this);
	}

	Boolean isPlay = true;

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.ib_play:
			if (iMusic == null) {
				Toast.makeText(this, "请选择歌曲播放啊", Toast.LENGTH_LONG).show();
				return;
			}
			if (isPlay) {
				// if (iMusic != null) {
				setPause();
				iMusic.moveon();
				// }

			} else {
				// if (iMusic != null) {
				setPlay();
				iMusic.pause();
				// }
			}
			break;
		case R.id.ib_playlist_next:
			if (currIndex + 1 < lists.size() && iMusic != null) {
				currIndex++;
				// Log.i(TAG, currIndex + "");
				startMp3(currIndex);
			} else if (iMusic == null) {
				Toast.makeText(this, "请播放歌曲", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, "当前已经是最后一首歌曲了", Toast.LENGTH_SHORT).show();
			}
			break;
		// case R.id.ib_playlist_shang:
		// if (currIndex > 0 && iMusic != null) {
		// currIndex--;
		// // Log.i(TAG, currIndex + "");
		// startMp3(currIndex);
		// } else if (iMusic == null) {
		// Toast.makeText(this, "请播放歌曲", Toast.LENGTH_SHORT).show();
		// } else {
		// Toast.makeText(this, "当前已经是第一首歌曲了", Toast.LENGTH_SHORT).show();
		// }
		// break;
		case R.id.rl_bottom_main:
			Intent intent = new Intent(this, PlayActivity.class);
			Bundle bundle = new Bundle();
			bundle.putBinder("myBinder", iBinder);
			intent.putExtra("mBundle", bundle);
			startActivityForResult(intent, 200);
			break;
		case R.id.ll_list_back:
			intent2.putExtra("currInfo", info);
			intent2.putExtra("currIndex", currIndex);
			intent2.putExtra("isPlay", isPlay);
			setResult(100, intent2);
			finish();
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
		// Log.i(TAG, id+"===============");
		isPlay = false;
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int a = (Integer) msg.obj;
			switch (msg.what) {
			case 1:
				sb_playlist.setProgress(a);
				// if (iMusic.getInfo() != info) {
				// info = iMusic.getInfo();
				// if (info != null) {
				// tv_main_title.setText(info.title);
				// tv_main_autor.setText(info.artist);
				// }
				// }
				tv_main_title.setText(iMusic.getInfo().title);
				tv_main_autor.setText(iMusic.getInfo().artist);
				if (a == info.duration / 1000) {
					// Log.i(TAG, currIndex + "===" + info.name);
					if (currIndex + 1 == lists.size()) {
//						Toast.makeText(PlayListActivity.this, "播放完毕",
//								Toast.LENGTH_LONG).show();
						if (iMusic != null) {
							iMusic.stop();
							isUnbind = false;
						}
						sb_playlist.setProgress(0);
						return;
					}
					currIndex = currIndex + 1;
					// Log.i(TAG, currIndex + "播放前");
					startMp3(currIndex);
				}

				break;

			default:
				break;
			}
		};
	};

	private Handler mHandler2 = new Handler() {
		public void handleMessage(Message msg) {
			int id = msg.what;
			switch (id) {
			case 10:
				ib_play.setBackgroundResource(R.drawable.playmusic_selector);
				if (iMusic != null) {
					iMusic.stop();
					iMusic = null;
					isUnbind = false;
				}
				break;

			default:
				break;
			}
		};
	};

	// private class MyServiceConnection implements ServiceConnection {
	//
	// @Override
	// public void onServiceConnected(ComponentName name, IBinder service) {
	// // TODO Auto-generated method stub
	// iBinder = service;
	// }
	//
	// @Override
	// public void onServiceDisconnected(ComponentName name) {
	//
	// }

	// }
	// 通知栏
	// private void notification() {
	// // 1.Get a reference to the NotificationManager:
	// final NotificationManager nm = (NotificationManager)
	// getSystemService(Context.NOTIFICATION_SERVICE);
	// // 2.Instantiate the Notification:
	// final Notification nf = new Notification();
	// nf.icon = R.drawable.ic_launcher;// 图标
	// nf.tickerText = "你有新的消息...";// 提示
	// nf.when = System.currentTimeMillis();// 时间
	//
	// // 1.Create the XML layout for the notification. For example, the
	// // following layout is called
	// // 2.RemoveViews
	// final RemoteViews remoteViews = new RemoteViews(getPackageName(),
	// R.layout.mynotifiy);
	// // 为控件设置参数
	// // 为图片 哪个控件id 哪个资源
	// remoteViews.setImageViewResource(R.id.iv_icon, R.drawable.ic_launcher);
	// remoteViews.setTextViewText(R.id.tv_name, info.name);
	// // 为按钮添加事件监听 通知服务上一首，下一首
	// Intent mIntentPrev = new Intent(this, PlayBroadcastReceiver.class);
	// mIntentPrev.setAction("prev");
	// mIntentPrev.putExtra("position", currIndex);
	// PendingIntent intentPrev = PendingIntent.getBroadcast(this, 1,
	// mIntentPrev, PendingIntent.FLAG_ONE_SHOT);
	// remoteViews.setOnClickPendingIntent(R.id.iv_prev, intentPrev);
	//
	// Intent mIntentNext = new Intent(this, PlayBroadcastReceiver.class);
	// mIntentNext.setAction("next");
	// mIntentNext.putExtra("position", currIndex);
	// PendingIntent intentNext = PendingIntent.getBroadcast(this, 1,
	// mIntentNext, PendingIntent.FLAG_ONE_SHOT);
	//
	// remoteViews.setOnClickPendingIntent(R.id.iv_next, intentNext);
	//
	// // 指定布局
	// nf.contentView = remoteViews;
	// // 3.设置延时意图
	// Intent contentIntent = new Intent(this, PlayActivity.class);
	// contentIntent.putExtra("mp3Name", info.name);
	// // 延时意图：对上面的意进行封装
	// PendingIntent pendingIntent = PendingIntent.getActivity(this, 100,
	// contentIntent, PendingIntent.FLAG_ONE_SHOT);
	// // 指定延时意图
	// nf.contentIntent = pendingIntent;
	// // 做操作
	// new Thread() {
	// public void run() {
	// int i = 1;
	// while (i <= info.duration / 1000) {
	// remoteViews.setProgressBar(R.id.sb, info.duration / 1000, i++, false);//
	// false确定进度条
	// // 发一次通知
	// nm.notify(100, nf);
	// SystemClock.sleep(1000);
	// }
	// // 4.启动
	// nm.notify(100, nf);
	// };
	// }.start();
	// }
}
