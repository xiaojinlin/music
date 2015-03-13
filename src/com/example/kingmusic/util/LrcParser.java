package com.example.kingmusic.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

import com.example.kingmusic.entity.LrcInfo;

public class LrcParser {
	private LrcInfo lrcinfo = new LrcInfo();
	// 存放临时时间
	private long currentTime = 0;
	// 存放临时歌词
	private String currentContent = null;
	// 用于保存时间点和歌词之间的对应关系
	// private Map<Long,String> maps =new HashMap<Long,String>();
	private Map<Long, String> maps = new TreeMap<Long, String>();

	/*
	 * 根据文件路径，读取文件，返回一个输入流
	 * 
	 * @param path 文件路径
	 * 
	 * @return InputStream 文件输入流
	 * 
	 * @throws FileNotFoundException
	 */

	/**
	 * @param inputStream
	 *            输入流
	 * @return
	 * 
	 * */
	public LrcInfo parser(String path) {
		// 包装对象

		InputStreamReader inr;
		try {
			File f = new File(path);
			InputStream ins = new FileInputStream(f);
			inr = new InputStreamReader(ins,"UTF-8");
			BufferedReader reader = new BufferedReader(inr);
			// 一行一行的读，每读一行解析一行
			String line = null;
			while ((line = reader.readLine()) != null) {
				parserLine(line);
//				Log.i("sb", line);
			}
			// 全部解析完后，设置info
			lrcinfo.setInfos(maps);
			Iterator<Entry<Long, String>> iter = maps.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<Long, String> entry = (Entry<Long, String>) iter.next();
				Long key = entry.getKey();
				String val = entry.getValue();
				mTime.add(Integer.parseInt(key.toString()));
				mStrings.add(val);
//				Log.e("---", "key=" + key + "   val=" + val);
			}
			reader.close();
			inr.close();
			ins.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			mStrings.add("没有歌词文件，赶紧去下载");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mStrings.add("没有读取到歌词");
		}

		return lrcinfo;
	}

	private List<Integer> mTime = new ArrayList<Integer>();

	private List<String> mStrings = new ArrayList<String>();

	/**
	 * 利用正则表达式解析每行具体语句 并将解析完的信息保存到LrcInfo对象中
	 * 
	 * @param line
	 */
	private void parserLine(String line) {
		// 获取歌曲名信息
		if (line.startsWith("[ti:")) {
			String title = line.substring(4, line.length() - 1);
			Log.i("", "title-->" + title);
			lrcinfo.setTitle(title);
		}
		// 取得歌手信息
		else if (line.startsWith("[ar:")) {
			String artist = line.substring(4, line.length() - 1);
			Log.i("", "artist-->" + artist);
			lrcinfo.setArtist(artist);
		}
		// 取得专辑信息
		else if (line.startsWith("[al:")) {
			String album = line.substring(4, line.length() - 1);
			Log.i("", "album-->" + album);
			lrcinfo.setAlbum(album);
		}
		// 取得歌词制作者
		else if (line.startsWith("[by:")) {
			String bysomebody = line.substring(4, line.length() - 1);
			Log.i("", "by-->" + bysomebody);
			lrcinfo.setBySomeBody(bysomebody);
		}
		// 通过正则表达式取得每句歌词信息
		else {
			// 设置正则表达式
			String reg = "\\[(\\d{1,2}:\\d{1,2}\\.\\d{1,2})\\]|\\[(\\d{1,2}:\\d{1,2})\\]";
			Pattern pattern = Pattern.compile(reg);
			Matcher matcher = pattern.matcher(line);
			// 如果存在匹配项则执行如下操作
			while (matcher.find()) {
				// 得到匹配的内容
				String msg = matcher.group();
				// 得到这个匹配项开始的索引
				int start = matcher.start();
				// 得到这个匹配项结束的索引
				int end = matcher.end();
				// 得到这个匹配项中的数组
				int groupCount = matcher.groupCount();
				for (int index = 0; index < groupCount; index++) {
					String timeStr = matcher.group(index);
					Log.i("", "time[" + index + "]=" + timeStr);
					if (index == 0) {
						// 将第二组中的内容设置为当前的一个时间点
						currentTime = str2Long(timeStr.substring(1,
								timeStr.length() - 1));
					}
				}
				// 得到时间点后的内容
				String[] content = pattern.split(line);
					// if(index==content.length-1){
					// 将内容设置魏当前内容
					if (content!=null&&content.length!=0) {
					currentContent = content[content.length - 1];
					// }
					// }
					// 设置时间点和内容的映射
					maps.put(currentTime, currentContent);
					Log.i("", "currentTime--->" + currentTime
							+ "   currentContent--->" + currentContent);
					// 遍历map
				}else{
					maps.put(currentTime,"");
				}
				// for(int index =0; index<content.length; index++){
			}
		}
	}

	private long str2Long(String timeStr) {
		// 将时间格式为xx:xx.xx，返回的long要求以毫秒为单位
		Log.i("", "timeStr=" + timeStr);
		String[] s = timeStr.split("\\:");
		int min = Integer.parseInt(s[0]);
		int sec = 0;
		int mill = 0;
		if (s[1].contains(".")) {
			String[] ss = s[1].split("\\.");
			sec = Integer.parseInt(ss[0]);
			mill = Integer.parseInt(ss[1]);
			Log.i("", "s[0]=" + s[0] + "s[1]" + s[1] + "ss[0]=" + ss[0]
					+ "ss[1]=" + ss[1]);
		} else {
			sec = Integer.parseInt(s[1]);
			Log.i("", "s[0]=" + s[0] + "s[1]" + s[1]);
		}
		return min * 60 * 1000 + sec * 1000 + mill * 10;
	}

	public List<String> getWords() {
		return mStrings;
	}

	public List<Integer> getTime() {
		mTime.add(0, 0);
		return mTime;
	}
	// private List<String> mWords = new ArrayList<String>();
	// private List<Integer> mTimeList = new ArrayList<Integer>();
	//
	// // 处理歌词文件
	// public void readLRC(String path) {
	// File file = new File(path);
	// FileInputStream fileInputStream = new FileInputStream(file);
	//
	// // InputStreamReader inputStreamReader = new InputStreamReader(
	// // fileInputStream, "GBK");
	// // BufferedReader bufferedReader = new BufferedReader(
	// // inputStreamReader);
	// // String s = "";
	// // while ((s = bufferedReader.readLine()) != null) {
	// // addTimeToList(s);
	// // if ((s.indexOf("[ar:") != -1) || (s.indexOf("[ti:") != -1)
	// // || (s.indexOf("[by:") != -1)) {
	// // s = s.substring(s.indexOf(":") + 1, s.indexOf("]"));
	// // } else {
	// // // ss为时间 [02:12.22]
	// // String ss = s.substring(s.indexOf("["), s.indexOf("]") + 1);
	// // // s为ss时间后的歌词
	// // s = s.replace(ss, "");
	// // }
	// // mWords.add(s);
	// // }
	// //
	// // bufferedReader.close();
	// // inputStreamReader.close();
	// // fileInputStream.close();
	// // } catch (FileNotFoundException e) {
	// // e.printStackTrace();
	// // mWords.add("没有歌词文件，赶紧去下载");
	// // } catch (IOException e) {
	// // e.printStackTrace();
	// // mWords.add("没有读取到歌词");
	// // }
	// // }
	// //
	// // public List<String> getWords() {
	// // return mWords;
	// // }
	// //
	// // public List<Integer> getTime() {
	// // return mTimeList;
	// // }
	// //
	// // // 分离出时间
	// // private int timeHandler(String string) {
	// // string = string.replace(".", ":");
	// // String timeData[] = string.split(":");
	// // // 分离出分、秒并转换为整型
	// // int minute = Integer.parseInt(timeData[0]);
	// // int second = Integer.parseInt(timeData[1]);
	// // int millisecond = Integer.parseInt(timeData[2]);
	// //
	// // // 计算上一行与下一行的时间转换为毫秒数
	// // int currentTime = (minute * 60 + second) * 1000 + millisecond * 10;
	// //
	// // return currentTime;
	// // }
	// //
	// // private void addTimeToList(String string) {
	// // Matcher matcher = Pattern.compile(
	// // "\\[\\d{1,2}:\\d{1,2}([\\.:]\\d{1,2})?\\]").matcher(string);
	// // if (matcher.find()) {
	// // String str = matcher.group();
	// // mTimeList.add(new LrcParser().timeHandler(str.substring(1,
	// // str.length() - 1)));
	// // }
	// // }
}