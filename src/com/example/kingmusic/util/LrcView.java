package com.example.kingmusic.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.kingmusic.entity.Mp3Info;

public class LrcView extends TextView {
	private List mWordsList = new ArrayList();
	private Paint mLoseFocusPaint;
	private Paint mOnFocusePaint;
	private float mX = 0;
	private float mMiddleY = 0;
	private float mY = 0;
	private static final int DY = 50;
	private int mIndex = 0;

	public LrcView(Context context,Mp3Info info) throws IOException {
		super(context);
		init(info);
	}
	public void setMIndex(int i){
		if(i!=-1){
		this.mIndex=i;
		}
	}
	public LrcView(Context context, AttributeSet attrs,Mp3Info info) throws IOException {
		super(context, attrs);
		init(info);
	}

	public LrcView(Context context, AttributeSet attrs, int defStyle,Mp3Info info)
			throws IOException {
		super(context, attrs, defStyle);
		init(info);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		Paint p = mLoseFocusPaint;
		p.setTextAlign(Paint.Align.CENTER);
		Paint p2 = mOnFocusePaint;
		p2.setTextAlign(Paint.Align.CENTER);
//		Log.i("sb", mWordsList.toString());
//		Log.i("sb", mIndex+"");
		canvas.drawText((String) mWordsList.get(mIndex), mX, mMiddleY, p2);
		
		int alphaValue = 25;
		float tempY = mMiddleY;
		for (int i = mIndex - 1; i >= 0; i--) {
			tempY -= DY;
			if (tempY < 0) {
				break;
			}
			p.setColor(Color.argb(255 - alphaValue, 245, 245, 245));
			canvas.drawText((String) mWordsList.get(i), mX, tempY, p);
			alphaValue += 25;
		}
		alphaValue = 25;
		tempY = mMiddleY;
		for (int i = mIndex + 1, len = mWordsList.size(); i < len; i++) {
			tempY += DY;
			if (tempY > mY) {
				break;
			}
			p.setColor(Color.argb(255 - alphaValue, 245, 245, 245));
			canvas.drawText((String) mWordsList.get(i), mX, tempY, p);
			alphaValue += 25;
		}
//		if(mIndex){
//			mIndex++;
//		}
	}
	public void setMWords(List  mWordsList ){
		this.mWordsList=mWordsList;
	}
	@Override
	protected void onSizeChanged(int w, int h, int ow, int oh) {
		super.onSizeChanged(w, h, ow, oh);

		mX = w * 0.5f;
		mY = h;
		mMiddleY = h* 0.6f ;
	}
	private Mp3Info info;
	public void getInfo(Mp3Info info){
		this.info=info;
	}
	@SuppressLint("SdCardPath")
	private void init(Mp3Info info) throws IOException {
		setFocusable(true);

		LrcParser lrcParser = new LrcParser();
			lrcParser.parser(info.getPath().substring(0, info.getPath().length()-4)+".lrc");
		mWordsList = lrcParser.getWords();
//		Log.i("sb",mWordsList.toString());
		WindowManager wmManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		Display display = wmManager.getDefaultDisplay();
		int s = display.getWidth()/30;
		mLoseFocusPaint = new Paint();
		mLoseFocusPaint.setAntiAlias(true);
		mLoseFocusPaint.setTextSize(s);
		mLoseFocusPaint.setColor(Color.WHITE);
		mLoseFocusPaint.setTypeface(Typeface.MONOSPACE);

		mOnFocusePaint = new Paint();
		mOnFocusePaint.setAntiAlias(true);
		mOnFocusePaint.setColor(Color.rgb(255, 255, 128));
		mOnFocusePaint.setTextSize(s+10);
		mOnFocusePaint.setTypeface(Typeface.SANS_SERIF);
	}
}
