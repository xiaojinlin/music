package com.example.kingmusic.fragment;

import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.Images.Thumbnails;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.kingmusic.MvActivity;
import com.example.kingmusic.R;
import com.example.kingmusic.adapter.Mp4Adapter;
import com.example.kingmusic.dao.Mp4Dao;
import com.example.kingmusic.entity.LoadImage;
import com.example.kingmusic.entity.Mp4Info;

public class FindFragment extends Fragment {
	public FindFragment instance = null;
	ListView lv_video;
	Mp4Adapter mp4Adapter;// alt+shift+r
	List<Mp4Info> lists;
	int videoSize;
	Mp4Info info;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.acitvity_two, container, false);
		instance = this;
		// Service
		Mp4Dao dao = new Mp4Dao(getActivity());
		// 拿到集合给Adapter用
		lists = dao.getList();
		// 大小
		videoSize = lists.size();
		mp4Adapter = new Mp4Adapter(getActivity(), lists);
		lv_video = (ListView) v.findViewById(R.id.jievideolistfile);
		lv_video.setAdapter(mp4Adapter);
		// 点击事件
		lv_video.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Mp4Info info = (Mp4Info) mp4Adapter.getItem(position);
				Intent intent = new Intent(getActivity(), MvActivity.class);
				intent.putExtra("info", info);
				startActivity(intent);
			}
		});
		loadImages();
		return v;
	}

	/**
	 * Load images.
	 */
	private void loadImages() {
		final Object data = getActivity().getLastNonConfigurationInstance();
		if (data == null) {
			new LoadImagesFromSDCard().execute();
		} else {
			final LoadImage[] photos = (LoadImage[]) data;
			if (photos.length == 0) {
				new LoadImagesFromSDCard().execute();
			}
			for (LoadImage photo : photos) {
				addImage(photo);
			}
		}
	}

	private void addImage(LoadImage... value) {
		for (LoadImage image : value) {
			mp4Adapter.addPhoto(image);
			mp4Adapter.notifyDataSetChanged();
		}
	}

	public Object onRetainNonConfigurationInstance() {
		final ListView grid = lv_video;
		final int count = grid.getChildCount();
		final LoadImage[] list = new LoadImage[count];

		for (int i = 0; i < count; i++) {
			final ImageView v = (ImageView) grid.getChildAt(i);
			list[i] = new LoadImage(
					((BitmapDrawable) v.getDrawable()).getBitmap());
		}

		return list;
	}

	/**
	 * 获取视频缩略图
	 * 
	 * @param videoPath
	 * @param width
	 * @param height
	 * @param kind
	 * @return
	 */
	private Bitmap getVideoThumbnail(String videoPath, int width, int height,
			int kind) {
		Bitmap bitmap = null;
		bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	class LoadImagesFromSDCard extends AsyncTask<Object, LoadImage, Object> {
		@Override
		protected Object doInBackground(Object... params) {
			Bitmap bitmap = null;
			for (int i = 0; i < videoSize; i++) {
				bitmap = getVideoThumbnail(lists.get(i).getPath(), 120, 120,
						Thumbnails.MINI_KIND);
				if (bitmap != null) {
					publishProgress(new LoadImage(bitmap));
				}
			}
			return null;
		}

		@Override
		public void onProgressUpdate(LoadImage... value) {
			addImage(value);
		}
	}

}
