package com.innovative.gnews;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.RemoteViews.ActionException;

public class InfoActivity extends Activity {
	// Controls
	WebView wvInfoDescription = null;
	ImageButton ibInfoBack = null;
	TextView tvInfoFeedback = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {		
    	super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_info);
        
        wvInfoDescription = (WebView) findViewById(R.id.wvInfoDescription);
        if (wvInfoDescription!=null){
        	wvInfoDescription.loadData("<html><body>I&apos;ve built this version of Gnews app to " +
        			"give the user a joyous experience of news reading on a mobile phone, with clean and elegant interface. " +
        			"If you like the app, please be vocal and <a href=\"market://details?id=com.innovative.gnews\">rate the app and/or write a review</a>. And if " +
        			"would like to know more about me (professionally), <a href=\"mailto:johngummadi@gmail.com?" +
        			"Subject=Hello from Gnews user\"> shoot me an email</a>. And oh, BTW this app or I have no relation with " +
        			"Google, Inc. <br><br>- John Gummadi!</body></html>", 
        			"text/html", "UTF-8");
        }
        
        ibInfoBack = (ImageButton) findViewById(R.id.ibInfoBack);
        if (ibInfoBack!=null)
        	ibInfoBack.setOnClickListener(mBtnClickListener);
        
        tvInfoFeedback = (TextView) findViewById(R.id.tvInfoFeedback);
        if (tvInfoFeedback!=null)
        	tvInfoFeedback.setOnClickListener(mBtnClickListener);
        
    } //onCreate()
	
	private OnClickListener mBtnClickListener = new OnClickListener() {
        public void onClick(View v) {
        	int id = v.getId();
        	switch (id)
        	{
        	case R.id.ibInfoBack:
        		finish();
        		break;
        		
        	case R.id.tvInfoFeedback:
	        	{
	        		Uri uri = Uri.parse("mailto:johngummadi@gmail.com?subject=Feedback from Gnews Android app user");
	        		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
	        		startActivity(intent);
	        	}
        		break;
        	}
        } //onClick
    }; //mBtnClickListener
} //class NewsPageActivity
