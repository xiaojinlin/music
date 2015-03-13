package com.example.kingmusic.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kingmusic.R;
import com.example.kingmusic.entity.LoadImage;
import com.example.kingmusic.entity.Mp4Info;

public class Mp4Adapter extends BaseAdapter {
	List<Mp4Info> lists = new ArrayList<Mp4Info>();
	int local_postion = 0;
	boolean imageChage = false;
	private LayoutInflater mLayoutInflater;
	Context context;
	private ArrayList<LoadImage> photos = new ArrayList<LoadImage>();

	public Mp4Adapter(Context context,List<Mp4Info> lists) {
		super();
		this.lists = lists;
		mLayoutInflater = LayoutInflater.from(context);
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return photos.size();
	}
	
	public void addPhoto(LoadImage image) {
		photos.add(image);
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
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.lv_video_item, null);
			holder.iv_img = (ImageView) convertView.findViewById(R.id.iv_img);
			holder.tv_title = (TextView) convertView
					.findViewById(R.id.tv_title);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.iv_img.setImageBitmap(photos.get(position).getBitmap());
		holder.tv_title.setText(lists.get(position).title);
		long min = lists.get(position).getDuration() / 1000 / 60;
		long sec = lists.get(position).getDuration() / 1000 % 60;
		holder.tv_time.setText(min + " : " + sec);
		

		return convertView;
	}
	public static class ViewHolder {
		public ImageView iv_img;
		public TextView tv_title;
		public TextView tv_time;
	}
}
