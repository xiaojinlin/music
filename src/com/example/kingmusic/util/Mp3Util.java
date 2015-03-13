package com.example.kingmusic.util;

import java.util.List;

import com.example.kingmusic.entity.Mp3Info;

public class Mp3Util {
	/**
	 * 得到下一首播放的音乐info信息
	 * 
	 * @param list
	 * @param info
	 * @return
	 */
	public static Mp3Info nextTo(List<Mp3Info> list, Mp3Info info) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).get_id() == info._id) {
				int s = i;
				if (i == list.size()) {
					s = 0;
				} else {
					s += 1;
				}
				if (s >= 0 && s < list.size()) {
					info = list.get(s);
				}
				return info;
			}

		}
		return null;
	}
	/**
	 * 得到上一首播放的音乐info信息
	 * @param list
	 * @param info
	 * @return
	 */
	public static Mp3Info upTo(List<Mp3Info> list, Mp3Info info) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).get_id() == info._id) {
				int s = i;
				if (i == list.size()) {
					s = 0;
				} else {
					s -= 1;
				}
				if (s >= 0 && s <= list.size()) {
					info = list.get(s);
				}
				return info;
			}

		}
		return null;
	}
}
