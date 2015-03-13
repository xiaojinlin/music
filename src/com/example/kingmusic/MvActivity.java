package com.example.kingmusic;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.example.kingmusic.entity.Mp4Info;

public class MvActivity extends Activity {
	private MediaPlayer mp;
	private SurfaceView sv;
	private SeekBar sb;
	private TextView et_name;
	private SurfaceHolder holder;
	Mp4Info info;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mv);
		et_name = (TextView) findViewById(R.id.et_name);
		mp = new MediaPlayer();
		Intent intent = getIntent();
		info = (Mp4Info) intent.getSerializableExtra("info");
		et_name.setText(info.title);
		
		sb = (SeekBar) findViewById(R.id.sb);
		// 展现图像的控件
		sv = (SurfaceView) findViewById(R.id.sv);
		// 必须要SurfaceHolder
		holder = sv.getHolder();
		// 设置相关属性
		holder.setKeepScreenOn(true);// 保持屏幕高亮
		// 不设置缓存
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		//
		holder.setFixedSize(176, 144);// 内置分辨率
		//为进度条添加事件监听
		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				//拉进度条停止判断之前是播的还是暂停的
				//播的位置就是进度条拉到的位置
				mp.seekTo(seekBar.getProgress());
				if(!isPause){
					mp.start();
				}
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				//拉的过程中 播的就停止
				
			}
		});
	}
//
	/** 播放 */
	public void play(View view) {
		mp.reset();
		try {
			mp.setDataSource(info.path);
			mp.prepare();
			mp.start();
			sb.setMax(mp.getDuration());//设置sb为音乐播放的时长
			// 让mp在holder中显示
			mp.setDisplay(holder);
			go();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** 暂停 */
	boolean isPause = true;// 暂停

	public void pause(View view) {
		if (mp.isPlaying()) {
			mp.pause();
			isPause = true;
		} else {
			if (isPause) {
				mp.start();
				isPause = false;
			}
		}
	}

	/** 停止 */
	public void stop(View view) {
		mp.stop();
	}
	/**重置*/
	public void reset(View view) {
		mp.stop();
		try {
			mp.prepare();
		} catch (Exception e) {
			e.printStackTrace();
		}
		mp.seekTo(0);
		mp.start();
	}
	
	public void go() {
		// /**进度条走动的代码用Timer*/
		// //放到Service
		// MusicDetailActivity.sb.setMax(max);
		// 定时器
		Timer timer = new Timer();
		//定时器任务(指定 )
		TimerTask timetask = new TimerTask() {
			@Override
			public void run() {
				//
				if (mp != null && mp.isPlaying()) {
					sb.setProgress(mp.getCurrentPosition());
				}
			}
		};
		//200毫秒调一次
		timer.schedule(timetask, 0, 200);
		/** 拖动进度条的事件 */
		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						int pro = seekBar.getProgress();// 得到进度条停止拖动时的位置
						mp.seekTo(pro);// 改变播放器的进度
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {

					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
					}
				});
	}
}

