package com.example.kingmusic.fragment;

import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.example.kingmusic.R;
import com.example.kingmusic.entity.Mp3Info;
import com.example.kingmusic.iface.IMusic;
import com.example.kingmusic.iface.MyIBinder;
import com.example.kingmusic.util.LrcParser;
import com.example.kingmusic.util.LrcView;

public class LrcFragment extends Fragment {
	protected static final String TAG = "LrcFragment";
	private View view;
	private LinearLayout ll_lrc;
	private LrcParser lp;
	private Mp3Info info;
	private LrcView lv;
	private MyIBinder myIBinder;
	private IMusic iMusic;
	private List<Integer> time;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.vp_lrc, null, false);
		iMusic = (IMusic) myIBinder.getIBinder();
		init();

		LrcOperate();
		return view;
	}

	public void setImusic(MyIBinder myIBinder) {
		this.myIBinder = myIBinder;
	}

	private Thread mThread = new Thread() {
		public void run() {
			int i = 0;
			while (true) {
				Message message = mHandler.obtainMessage();
				int currPosition = iMusic.getCurrentPosition();
//				Log.i(TAG, currPosition+"====="+time.get(0)+"==="+time.get(1));
				for (int j = 0; j < time.size() - 1; j++) {
					if (time.get(j)<currPosition
							&& time.get(j + 1)>=currPosition) {
						i = j;
					}
				}
				message.what = 1;
				message.obj = i;
				mHandler.sendMessage(message);
				SystemClock.sleep(100);
			}
		};
	};

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int id = msg.what;
			int i =(Integer) msg.obj;
			switch (id) {
			case 1:
				lv.setMIndex(i-1);
				lv.invalidate();
				break;

			default:
				break;
			}
		};
	};

	private void LrcOperate() {
		// TODO Auto-generated method stub

	}

	private void init() {
		ll_lrc = (LinearLayout) view.findViewById(R.id.ll_lrc);

		try {
			lp = new LrcParser();
			info = iMusic.getInfo();
			lv = new LrcView(getActivity(), info);
			lp.parser(info.getPath().substring(0, info.getPath().length() - 4)
					+ ".lrc");
			time = lp.getTime();
			lv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT));
			ll_lrc.addView(lv);
			mThread.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
