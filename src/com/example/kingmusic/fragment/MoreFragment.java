package com.example.kingmusic.fragment;

import java.io.File;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.example.kingmusic.R;
import com.example.kingmusic.dao.Mp3Dao;

@SuppressLint("JavascriptInterface")
public class MoreFragment extends Fragment {
	private WebView webview;
	private String URL = "http://mo.kugou.com";
	private String path = "http://192.168.56.1:8080/WzMusic/yyh.mp3";
	private MediaPlayer mediaPlayer;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.more_activity, null);
		webview = (WebView) view.findViewById(R.id.webview);
		// webview.loadUrl(URL);
		webview.loadUrl("file:///android_asset/test.html");
		webview.getSettings().setJavaScriptEnabled(true);
		webview.addJavascriptInterface(new MyObject(), "xjl");

		return view;
	}

	public class MyObject {
		@JavascriptInterface
		// 1.显示所有用户信息
		public void showAllUsers() {
			Runnable runnable = new Runnable() {
				public void run() {
					Mp3Dao dao = new Mp3Dao(getActivity());
					String json = dao.getJson(dao.getAllmp3());
					System.out.println(json + "=========================");
					webview.loadUrl("javascript:showUsers(" + json + ")");
				}
			};
			getActivity().runOnUiThread(runnable);
		}

		@JavascriptInterface
		public void call() {
			// Intent intent = new Intent(Intent.ACTION_DIAL);
			// intent.setData(Uri.parse("tel:" + number));
			// startActivity(intent);

			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			try {
				mediaPlayer.setDataSource(path);
				mediaPlayer.prepare();
				mediaPlayer.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	};
}
