package com.example.kingmusic.service;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.example.kingmusic.R;

public class ViewService {
	private LayoutInflater mInflater;
	private Context context;

	public ViewService(Context context) {
		super();
		this.context = context;
		mInflater = LayoutInflater.from(context);
	}

	public List<View> getPagerView() {
		List<View> lists = new ArrayList<View>();
		// 造三个View
		View v1 = mInflater.inflate(R.layout.vp_item_1, null);
		lists.add(v1);
		View v2 = mInflater.inflate(R.layout.vp_item_2, null);
		lists.add(v2);
		View v3 = mInflater.inflate(R.layout.vp_item_3, null);
		lists.add(v3);
		return lists;
	}
}
