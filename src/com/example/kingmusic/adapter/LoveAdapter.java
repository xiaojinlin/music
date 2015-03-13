package com.example.kingmusic.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.kingmusic.R;
import com.example.kingmusic.entity.Mp3Info;

public class LoveAdapter extends BaseAdapter {
	private static final String TAG = "LoveAdapter";
	List<Mp3Info> lists = new ArrayList<Mp3Info>();
	LayoutInflater mInflater;
	Context context;

	public LoveAdapter(List<Mp3Info> lists, Context context) {
		super();
		this.lists = lists;
		mInflater = LayoutInflater.from(context);
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return lists.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return lists.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		ViewHolder mHolder = null;
		if (convertView == null) {
			view = mInflater.inflate(R.layout.lv_item_love, null);
			mHolder = new ViewHolder();
			mHolder.tv_title = (TextView) view.findViewById(R.id.tv_title);
			mHolder.tv_artist = (TextView) view.findViewById(R.id.tv_artist);
			mHolder.tv_duration = (TextView) view
					.findViewById(R.id.tv_duration);
			view.setTag(mHolder);
		} else {
			view = convertView;
			mHolder = (ViewHolder) view.getTag();
		}
		Mp3Info loveInfo = lists.get(position);
//		Log.i(TAG, loveInfo.toString());
		mHolder.tv_title.setText(loveInfo.title);
		int m = (int) (loveInfo.duration / 1000 / 60);
		int s = (int) ((loveInfo.duration / 1000) % 60);
		String sm = m + "";
		String ss = s + "";
		if (sm.length() == 1) {
			sm = "0" + sm;
		}
		if (ss.length() == 1) {
			ss = "0" + ss;
		}
		mHolder.tv_artist.setText(loveInfo.artist);
		mHolder.tv_duration.setText(sm + ":" + ss);
		return view;
	}

	private static class ViewHolder {
		TextView tv_title;
		TextView tv_artist;
		TextView tv_duration;
	}

}
