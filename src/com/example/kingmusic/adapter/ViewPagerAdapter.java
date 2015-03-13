package com.example.kingmusic.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class ViewPagerAdapter extends PagerAdapter {
	// 传List过来View
	List<View> lists = new ArrayList<View>();

	public ViewPagerAdapter(List<View> lists) {
		super();
		this.lists = lists;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return lists.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0==arg1;
	}

	// 销毁
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(lists.get(position));// 移除指定的View 一定要这种
		// super.destroyItem(container, position, object);//必须删除
	}

	// 初始化条目
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View view = lists.get(position);// 加载布局文件
		container.addView(view);
		return view;
	}
}
