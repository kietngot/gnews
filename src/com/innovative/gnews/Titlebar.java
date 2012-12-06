package com.innovative.gnews;
import com.innovative.gnews.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Titlebar {
	private static ImageButton m_btnHome = null;
	private static TextView m_tvTitle = null;
	private static Context m_context = null;
	private static Activity m_activity = null;
	
	// newpage_title controls
	private static ImageButton ibNewsList = null;
	private static ImageView ivFavIcon = null;
	private static TextView tvPageUrl = null;
	
	public static void Reset()
	{
		m_btnHome = null;
		m_tvTitle = null;
		m_context = null;
		m_activity = null;
		
		ibNewsList = null;
		ivFavIcon = null;
		tvPageUrl = null;
	}
	
	public static void InitTitlebar(Activity activity, int titleStringID)
	{
		// Resets the values of previous activity 
		Reset();
		m_activity = activity;
		m_context = m_activity.getApplicationContext();
		
        m_btnHome = (ImageButton) activity.findViewById(R.id.ibLogo);
        if (m_btnHome!=null)
        	m_btnHome.setOnClickListener(mBtnClickListener);
        
        m_tvTitle = (TextView) activity.findViewById(R.id.tvAppTitle);
        if (m_tvTitle!=null)
        	m_tvTitle.setText(titleStringID);
	}
	
	public static void InitTitlebar(Activity activity, String urlString, Bitmap favIcon)
	{
		// Resets the values of previous activity 
		Reset();
		m_activity = activity;
		m_context = m_activity.getApplicationContext();
				
		ibNewsList = (ImageButton) activity.findViewById(R.id.ibNewsList);
		if (ibNewsList!=null)
			ibNewsList.setOnClickListener(mBtnClickListener);
		
		ivFavIcon = (ImageView) activity.findViewById(R.id.ibPageFavIcon);
		if (ivFavIcon!=null && favIcon!=null)
			ivFavIcon.setImageBitmap(favIcon);
		
		tvPageUrl = (TextView) activity.findViewById(R.id.tvPageUrl);
		if (tvPageUrl!=null)
			tvPageUrl.setText(urlString);
	}
		
	static private OnClickListener mBtnClickListener = new OnClickListener() {
        public void onClick(View v) {
        	int id = v.getId();
        	Context appContext = m_context;
        	switch (id)
        	{
        	case R.id.ibLogo: //m_btnHome
	        	{
	        		Toast.makeText(appContext, "Home", Toast.LENGTH_SHORT).show();
	        	}
        		break;
        		
        	case R.id.ibNewsList:
        		m_activity.finish();
        		break;
        	}
        }
    }; //mBtnClickListener
}
