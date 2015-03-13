package com.example.kingmusic.iface;

import java.util.List;

import com.example.kingmusic.entity.Mp3Info;

public interface IMusic {
	/**播放方法*/
	public void play(Mp3Info info);
	/** 暂停 */
	public Mp3Info pause();

	/** 继续 */
	public void moveon();

	/** 停止 */
	public void stop();
	/**得到下一首的位置*/
	public Mp3Info nextTo(List<Mp3Info> list,Mp3Info info);
	/**得到上一首的位置*/
	public Mp3Info upTo(List<Mp3Info> list, Mp3Info info);
	/**音乐播放进度*/
	public void seekTo(int aMsec);
	/**当前播放的歌曲*/
	public Mp3Info getInfo();
	/**音乐当前位置*/
	public int getCurrentPosition();
	/**得到音乐的进度条最大值*/
	public int getProgressMax(); 
	/**得到音乐当前状态*/
	public boolean getCurrIsPlaying();
}
