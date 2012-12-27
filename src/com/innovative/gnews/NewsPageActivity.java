package com.innovative.gnews;
import com.innovative.gnews.db.GnewsDatabase;
import com.innovative.gnews.utils.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap;

/*
//TODO: 
	1. Adjust the menu controls (at the bottom)
	2. Javascript enabled flag should be loaded form the database setting (and written back)
	3. Allow the web view to zoom the page (by pinching)
*/
public class NewsPageActivity extends Activity {
	// Controls
	WebView wvNewsPage = null;
	TextView tvPageLoading = null;
	
	ImageButton ibBrowserBack = null;
	ImageButton ibBrowserForward = null;
	ImageButton ibBrowserRefresh = null;
	ImageButton ibBrowserJavascript = null;
	
	String mNewsPageURL = null;
	
	GnewsDatabase mDb = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {		
    	super.onCreate(savedInstanceState);
        final boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_newspage);
        
        // We can access custom title elements only after setting FEATURE_CUSTOM_TITLE
		if ( customTitleSupported ) {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.newpage_title);
			Titlebar.InitTitlebar(this, getString(R.string.loading), null);
		}
		
		mDb = GnewsDatabase.getDatabase();
		init();
    } //onCreate()
	
	@Override
	public void onStart() 
	{
		super.onStart();
		
		if (!Utils.checkInternetConnection(this, true, tvPageLoading))
			return;
		
		Thread th1 = new Thread(new Runnable() 
    	{
            public void run() 
            {
            	loadPage();
            } //run()
          });
        th1.start(); //new Thread
	}
	
	private void init() 
	{
		wvNewsPage = (WebView) findViewById(R.id.wvNewsPage);
		if (wvNewsPage==null)
			return;
		
		tvPageLoading = (TextView)findViewById(R.id.tvPageLoading);
		if (tvPageLoading!=null)
		{
			tvPageLoading.setText(R.string.loading);
			tvPageLoading.setVisibility(View.VISIBLE);
		}
		
		// Get preferences
		// Get page URL 
		Bundle extras = getIntent().getExtras();
		if (extras!=null) 
			mNewsPageURL = (String)extras.get("NewsPageURL");
		
		if (mNewsPageURL!=null)
			Titlebar.InitTitlebar(this, mNewsPageURL, null);
		
		// Buttons and listeners
		ibBrowserBack = (ImageButton)findViewById(R.id.ibBrowserBack);
		if (ibBrowserBack!=null)
			ibBrowserBack.setOnClickListener(mBtnClickListener);
		
		ibBrowserForward = (ImageButton)findViewById(R.id.ibBrowserForward);
		if (ibBrowserForward!=null)
			ibBrowserForward.setOnClickListener(mBtnClickListener);
		
		ibBrowserRefresh = (ImageButton)findViewById(R.id.ibBrowserRefresh);
		if (ibBrowserRefresh!=null)
			ibBrowserRefresh.setOnClickListener(mBtnClickListener);
		
		ibBrowserJavascript = (ImageButton)findViewById(R.id.ibBrowserJavascript);
		if (ibBrowserJavascript!=null)
			ibBrowserJavascript.setOnClickListener(mBtnClickListener);
		
		WebSettings webSettings = wvNewsPage.getSettings();
		webSettings.setJavaScriptEnabled(AppSettings.JavascriptEnabled);
		if (AppSettings.JavascriptEnabled)
			ibBrowserJavascript.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_browser_javascript_yes));
		else
			ibBrowserJavascript.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_browser_javascript_no));
		
	} //init()
	
	private void updateJavascriptEnabled(boolean value)
    {
    	AppSettings.JavascriptEnabled= value;
    	
    	if (mDb!=null)
    		mDb.setSetting("JavaScriptEnabled", value?"1":"0");
    } //updateJavascriptEnabled()
	
	private OnClickListener mBtnClickListener = new OnClickListener() {
        public void onClick(View v) {
        	int id = v.getId();
        	switch (id)
        	{
        	case R.id.ibBrowserBack: //m_btnHome
        		if (wvNewsPage!=null)
        			wvNewsPage.goBack();
        		break;
        		
        	case R.id.ibBrowserForward:
        		if (wvNewsPage!=null)
        			wvNewsPage.goForward();
        		break;
        		
        	case R.id.ibBrowserRefresh:
        		if (wvNewsPage!=null)
        		{
        			//wvNewsPage.loadData("", "text/html", null);
        			wvNewsPage.reload();
        		}
        		break;
        		
        	case R.id.ibBrowserJavascript:
        		WebSettings webSettings = wvNewsPage.getSettings();
        		updateJavascriptEnabled(!webSettings.getJavaScriptEnabled());
        		webSettings.setJavaScriptEnabled(AppSettings.JavascriptEnabled);
        		if (AppSettings.JavascriptEnabled)
        			ibBrowserJavascript.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_browser_javascript_yes));
        		else
        			ibBrowserJavascript.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_browser_javascript_no));
        		break;
        	}
        }
    }; //mBtnClickListener
	
	private void loadPage()
	{
		if (wvNewsPage==null || mNewsPageURL==null && mNewsPageURL.isEmpty())
			return;
		wvNewsPage.setWebViewClient(new WebViewClient()
		{
			@Override
			public void onPageStarted(final WebView view, final String url, final Bitmap favicon)
			{
				NewsPageActivity.this.runOnUiThread(new Runnable() 
				{
					public void run() 
					{
						Titlebar.InitTitlebar(NewsPageActivity.this, url, favicon);
						if (tvPageLoading!=null)
			    		{
							tvPageLoading.setText(R.string.loading);
			    			tvPageLoading.setVisibility(View.VISIBLE);
			    		}
					}
				});
			}
			
	        @Override
	        public void onPageFinished(WebView view, String url) {
	            super.onPageFinished(view, url);
	            if (tvPageLoading!=null)
	    		{
	    			tvPageLoading.setVisibility(View.GONE);
	    		}
	            //view.refreshDrawableState();
	        }

	        @Override
	        public void onReceivedSslError(android.webkit.WebView view, android.webkit.SslErrorHandler handler, android.net.http.SslError error)
	        {
	        	super.onReceivedSslError(view, handler, error);
	        	if (tvPageLoading!=null)
	    		{
	    			tvPageLoading.setVisibility(View.GONE);
	    		}
	        }
	        
	        @Override
	        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) 
	        {
	            super.onReceivedError(view, errorCode, description, failingUrl);  
	            if (tvPageLoading!=null)
	    		{
	    			tvPageLoading.setVisibility(View.GONE);
	    		}
	        }

	    });
		
		wvNewsPage.loadUrl(mNewsPageURL);
	} //loadPage
	
	
	
	/*
	// TODO: Use this when we ad controls to the footer
	private OnClickListener mClickListener = new OnClickListener() 
	{
		public void onClick(View v) 
		{
			int id = v.getId();
		} //onClick()
	}; //OnClickListener
	*/
	
} //class NewsPageActivity
