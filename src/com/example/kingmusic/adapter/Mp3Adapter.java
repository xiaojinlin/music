package com.example.kingmusic.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.kingmusic.entity.Mp3Info;

public class Mp3Adapter extends BaseAdapter {
	List<Mp3Info> lists = new ArrayList<Mp3Info>();
	LayoutInflater mInflater;
	Context context;

	public Mp3Adapter(List<Mp3Info> lists, Context context) {
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
			view = mInflater.inflate(
					android.R.layout.simple_expandable_list_item_2, null);
			mHolder = new ViewHolder();
			mHolder.tv_name = (TextView) view.findViewById(android.R.id.text1);
			mHolder.tv_duration = (TextView) view
					.findViewById(android.R.id.text2);
			view.setTag(mHolder);
		} else {
			view = convertView;
			mHolder = (ViewHolder) view.getTag();
		}
		Mp3Info mp3Info = lists.get(position);
		mHolder.tv_name.setText(mp3Info.name);
		int m = mp3Info.duration / 1000 / 60;
		int s = (mp3Info.duration / 1000) % 60;
		String sm = m + "";
		String ss = s + "";
		if (sm.length() == 1) {
			sm = "0" + sm;
		}
		if (ss.length() == 1) {
			ss = "0" + ss;
		}
		mHolder.tv_duration.setText(sm+":"+ss);

		return view;
	}

	private static class ViewHolder {
		TextView tv_name;
		TextView tv_duration;
	}

}
