package com.innovative.gnews;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageButton;

public class InfoActivity extends Activity {
	// Controls
	WebView wvInfoDescription = null;
	ImageButton ibInfoBack = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {		
    	super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_info);
        
        wvInfoDescription = (WebView) findViewById(R.id.wvInfoDescription);
        if (wvInfoDescription!=null){
        	wvInfoDescription.loadData("<html><body>I&apos;ve built this version of Gnews app to " +
        			"showcase my app to a potential employer. So if you like the app, and " +
        			"would like to know more about me (professionally), <a href=\"mailto:johngummadi@gmail.com?" +
        			"Subject=Hello from Gnews user\"> shoot me an email</a>. And oh, BTW this app or I have no relation with " +
        			"Google, Inc. <br><br>- John Gummadi!</body></html>", 
        			"text/html", "UTF-8");
        }
        
        ibInfoBack = (ImageButton) findViewById(R.id.ibInfoBack);
        if (ibInfoBack!=null)
        	ibInfoBack.setOnClickListener(mBtnClickListener);
    } //onCreate()
	
	private OnClickListener mBtnClickListener = new OnClickListener() {
        public void onClick(View v) {
        	int id = v.getId();
        	switch (id)
        	{
        	case R.id.ibInfoBack:
        		finish();
        		break;
        	}
        } //onClick
    }; //mBtnClickListener
} //class NewsPageActivity
