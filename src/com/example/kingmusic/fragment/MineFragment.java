package com.example.kingmusic.fragment;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.kingmusic.MyLoveActivity;
import com.example.kingmusic.PlayListActivity;
import com.example.kingmusic.R;
import com.example.kingmusic.ZuijinActivity;
import com.example.kingmusic.adapter.Mp3Adapter;
import com.example.kingmusic.dao.Mp3Dao;
import com.example.kingmusic.entity.Mp3Info;
import com.example.kingmusic.iface.IMusic;
import com.example.kingmusic.iface.MyIBinder;
import com.example.kingmusic.service.MusicService;

public class MineFragment extends Fragment implements OnClickListener {
	private View view;
	private TextView tv_login, tv_play_mine_b, TextView02,TextView023;
	private RelativeLayout rl_play_mine, rl_download_mine, rl_love_mine,
			rl_head_mine, rl_zuijin_mine;
	private Intent intent;
	private ListView lv_mine_list;
	private Mp3Adapter mp3Adapter;
	private Mp3Dao dao;
	private List<Mp3Info> lists;
	private ImageView iv_frame;
	private IMusic iMusic;
	private MusicService mService;
	private Thread mThread;
	List<Mp3Info> loveLists;
	List<Mp3Info> zuijinLists;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.mine_activity, null);
		init();
		dao = new Mp3Dao(getActivity());
		lists = dao.getAllmp3();
		mService = new MusicService(getActivity());
		try {
			loveLists = mService.findAll();
			zuijinLists = mService.findAllZuijin();
			TextView02.setText(loveLists.size() + "首");
			TextView023.setText(zuijinLists.size()+"首");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// mp3Adapter = new Mp3Adapter(lists, getActivity());
		// lv_mine_list.setAdapter(mp3Adapter);
		tv_play_mine_b.setText("本地" + lists.size() + "首");
		
		
		return view;
	}

	private void init() {
		tv_login = (TextView) view.findViewById(R.id.tv_login);
		tv_play_mine_b = (TextView) view.findViewById(R.id.tv_play_mine_b);
		iv_frame = (ImageView) view.findViewById(R.id.iv_frame);
		rl_play_mine = (RelativeLayout) view.findViewById(R.id.rl_play_mine);
		TextView02 = (TextView) view.findViewById(R.id.TextView02);
		rl_download_mine = (RelativeLayout) view
				.findViewById(R.id.rl_download_mine);
		rl_love_mine = (RelativeLayout) view.findViewById(R.id.rl_love_mine);
		rl_head_mine = (RelativeLayout) view.findViewById(R.id.rl_head_mine);
		rl_zuijin_mine = (RelativeLayout) view
				.findViewById(R.id.rl_zuijin_mine);
		TextView023 =(TextView) view.findViewById(R.id.TextView023);
		// lv_mine_list = (ListView) view.findViewById(R.id.lv_mine_list);
		rl_head_mine.getBackground().setAlpha(100);
		// rl_play_mine.getBackground().setAlpha(100);
		// rl_download_mine.getBackground().setAlpha(100);
		// rl_love_mine.getBackground().setAlpha(100);
		rl_play_mine.setOnClickListener(this);
		rl_download_mine.setOnClickListener(this);
		rl_love_mine.setOnClickListener(this);
		tv_login.setOnClickListener(this);
		rl_zuijin_mine.setOnClickListener(this);
	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.tv_login:
			break;
		case R.id.rl_download_mine:
			rl_download_mine.setBackgroundResource(R.drawable.click_mine);
			break;
		case R.id.rl_zuijin_mine:
			rl_love_mine.setBackgroundResource(R.drawable.click_mine);
			Intent intent123 = new Intent(getActivity(), ZuijinActivity.class);
			IBinder iBinder12 = mBinder.getIBinder();
			Bundle bundle12 = new Bundle();
			bundle12.putBinder("mBinder1", iBinder12);
			intent123.putExtra("mBundle1", bundle12);
			getActivity().startActivityForResult(intent123, 0);
			//
			break;
		case R.id.rl_love_mine:
			rl_love_mine.setBackgroundResource(R.drawable.click_mine);
			Intent intent12 = new Intent(getActivity(), MyLoveActivity.class);
			IBinder iBinder1 = mBinder.getIBinder();
			Bundle bundle1 = new Bundle();
			bundle1.putBinder("mBinder1", iBinder1);
			intent12.putExtra("mBundle1", bundle1);
			getActivity().startActivityForResult(intent12, 0);
			//
			break;
		case R.id.rl_play_mine:
			iv_frame.setVisibility(View.VISIBLE);
			// 2.设置背景动画
			iv_frame.setBackgroundResource(R.anim.animationlist);
			// 3.得到背景
			AnimationDrawable rocketAnimation1 = (AnimationDrawable) iv_frame
					.getBackground();
			CustomAnimationDrawableNew mDrawableNew1 = new CustomAnimationDrawableNew(
					rocketAnimation1) {

				@SuppressLint("NewApi")
				@Override
				public void onAnimationFinish() {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getActivity(),
							PlayListActivity.class);
					IBinder iBinder = mBinder.getIBinder();
					Bundle bundle = new Bundle();
					bundle.putBinder("mBinder", iBinder);
					intent.putExtra("mBundle", bundle);
					getActivity().startActivityForResult(intent, 0);
					// getActivity().overridePendingTransition(R.anim.scale,
					// R.anim.alpha);
					iv_frame.setVisibility(View.GONE);
				}
			};
			// 4.启动动画
			// rocketAnimation.start();
			// 4.将图像重新设置背景
			iv_frame.setBackgroundDrawable(mDrawableNew1);//
			// rocketImage.setBackground(mDrawableNew);//16
			// 5.启动
			mDrawableNew1.start();
			// setMyAnimation(R.anim.rotate, false);
			break;
		default:
			break;
		}

	}

	private MyIBinder mBinder;

	public void setMYIBinder(MyIBinder iBinder) {
		this.mBinder = iBinder;

	}

	abstract class CustomAnimationDrawableNew extends AnimationDrawable {
		// 动画回调Handler
		Handler mAnimationHandler;

		// 主要作用：得到动画的帧数12,每帧播放耗时
		public CustomAnimationDrawableNew(AnimationDrawable aniDrawable) {
			// 加载 帧，时长
			int count = aniDrawable.getNumberOfFrames();
			for (int i = 0; i < count; i++) {
				// Add a frame to the animation
				this.addFrame(aniDrawable.getFrame(i),
						aniDrawable.getDuration(i));
			}
		}

		@Override
		public void start() {
			super.start();
			// 当总时间加载完之后，添加handler去调用onAnimationFinish()方法
			mAnimationHandler = new Handler();
			// postDelayed:Causes the Runnable r to be added to the message
			// queue,
			// to be run after the specified amount of time elapses.
			// The runnable will be run on the thread to which this handler is
			// attached.
			mAnimationHandler.postDelayed(new Runnable() {
				public void run() {
					onAnimationFinish();
				}
			}, getTotalDuration());// 等多久getTotalDuration()执行onAnimationFinish

		}

		// 所有帧播放完的时间
		public int getTotalDuration() {

			int iDuration = 0;
			for (int i = 0; i < this.getNumberOfFrames(); i++) {
				iDuration += this.getDuration(i);
			}
			return iDuration;
		}

		// 当所有动画播放完毕执行的操作
		public abstract void onAnimationFinish();
	}

	// 公共方法
	public void setMyAnimation(int r, boolean isFillAtter) {
		Animation animation = AnimationUtils.loadAnimation(getActivity(), r);
		animation.setFillAfter(isFillAtter);// 保持动画最后状态
		iv_frame.setAnimation(animation);
		iv_frame.startAnimation(animation);
		// 补间动画的事件监听
		// 动画播完之后跳到另一个Activity
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// 有心情播放声音

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// 动画重复

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// 动画结束
				iv_frame.setVisibility(View.GONE);
				// 跳
				Intent intent = new Intent(getActivity(),
						PlayListActivity.class);
				// 过渡动画
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.scale,
						R.anim.alpha);

			}
		});
	}
}
