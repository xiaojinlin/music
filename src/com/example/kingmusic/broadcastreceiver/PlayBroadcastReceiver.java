package com.example.kingmusic.broadcastreceiver;

import java.io.Serializable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.kingmusic.entity.Mp3Info;
import com.example.kingmusic.service.Mp3Service;

public class PlayBroadcastReceiver extends BroadcastReceiver {
	private static final String TAG = "PlayBroadcastReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		// 得到action
		Mp3Info info = (Mp3Info) intent.getSerializableExtra("operInfo");
		Intent intent2 = new Intent(context, Mp3Service.class);
		intent2.putExtra("caozuoInfo", info);
		// 告诉服务
		context.startService(intent2);
	}

}
