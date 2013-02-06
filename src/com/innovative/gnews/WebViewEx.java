package com.innovative.gnews;

import android.app.Activity;
import android.content.Context;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class WebViewEx extends WebView
{
	private View mFooter = null;
	private View mTitlebar = null;
	private long touchStartTime = -1;
	private Activity mActivity = null;
		
	public WebViewEx(Context context) {
		super(context);
		mActivity = (Activity) context;
	}
	
	public WebViewEx(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mActivity = (Activity) context;
	}
	
	public WebViewEx(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		mActivity = (Activity) context;
	}
	
	public void setAtributes(View titlebar, View footer)
	{
		mTitlebar = titlebar;
		mFooter = footer;
	} //setAtributes()
	
	
	public void toggleFullscreen()
	{
		boolean bFullScreen = (Boolean) this.getTag();
		bFullScreen = !bFullScreen;
		if (mFooter!=null)
			mFooter.setVisibility(bFullScreen?View.GONE:View.VISIBLE);
		if (mTitlebar!=null)
			mTitlebar.setVisibility(bFullScreen?View.GONE:View.VISIBLE);
		this.setTag(bFullScreen);
	} //toggleFullscreen()
	
	
	@Override
	protected void onAttachedToWindow()
	{
		getSettings().setBuiltInZoomControls(true);
		setTag(false); //full-screen is false
		WebSettings webSettings = getSettings();
		webSettings.setJavaScriptEnabled(AppSettings.JavascriptEnabled);
	} //onAttachedToWindow()
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if(event.getAction() == MotionEvent.ACTION_DOWN) 
        {
        	touchStartTime = SystemClock.uptimeMillis(); //System.currentTimeMillis();
        } //ACTION_DOWN
        else if(event.getAction() == MotionEvent.ACTION_UP) 
        {
        	long downTime = SystemClock.uptimeMillis() - touchStartTime;
        	if (touchStartTime>-1 && downTime<200)
        	{
        		mActivity.runOnUiThread(new Runnable() 
            	{
					 public void run() 
					 {
						 toggleFullscreen();
					 } //run()
				 }); //runOnUiThread()
        	}
        	touchStartTime = -1;
        } //ACTION_UP
        else if(event.getAction() == MotionEvent.ACTION_MOVE)
        {
        	// Reset the start-time to avoid toggling full-screen on fling or scroll. 
        	touchStartTime = -1;
        } //ACTION_MOVE
		return super.onTouchEvent(event);
	} //onTouchEvent()
} //class WebViewEx
