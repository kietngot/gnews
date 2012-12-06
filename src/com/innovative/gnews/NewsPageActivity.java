package com.innovative.gnews;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Bitmap;

public class NewsPageActivity extends Activity {
	// Controls
	WebView wvNewsPage = null;
	
	String mNewsPageURL = null;
	
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
		
		init();
		loadPage();
		
    } //onCreate()
	
	private void init() 
	{
		wvNewsPage = (WebView) findViewById(R.id.wvNewsPage);
		if (wvNewsPage==null)
			return;
		
		wvNewsPage.loadData("<html><body><b>Loading...</b></body></html>", "text/html", "UTF-8");
		wvNewsPage.refreshDrawableState();
		
		// Get page URL 
		Bundle extras = getIntent().getExtras();
		if (extras!=null) 
			mNewsPageURL = (String)extras.get("NewsPageURL");
		
		if (mNewsPageURL!=null)
			Titlebar.InitTitlebar(this, mNewsPageURL, null);
	} //init()
	
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
					}
				});
			}
			
	        @Override
	        public void onPageFinished(WebView view, String url) {
	            super.onPageFinished(view, url);
	            //view.refreshDrawableState();
	        }

	        @Override
	        public void onReceivedSslError(android.webkit.WebView view, android.webkit.SslErrorHandler handler, android.net.http.SslError error)
	        {
	        	super.onReceivedSslError(view, handler, error);
	        }
	        
	        @Override
	        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) 
	        {
	            super.onReceivedError(view, errorCode, description, failingUrl);                 
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
