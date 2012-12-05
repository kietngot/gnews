package com.innovative.gnews.feed;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.BitmapFactory;

import com.innovative.gnews.feed.NewsLoadEvents;

public class NewsLoader {
	private XmlHelperNews mXmlHelperNews = null;
	private NewsLoadEvents mLoadEvents = null;
	NewsCategory mNewsCategory = null;
	
	public NewsLoader(NewsLoadEvents loadEvents)
	{
		mLoadEvents = loadEvents;
		mXmlHelperNews = new XmlHelperNews();
	} //NewsLoader()
	
	public boolean loadNewsCategory(String url)
	{
		if (url==null || url.isEmpty())
			return false;
		
		final String urlNewsCategory = url;
		boolean bRet = false;
		try
		{
			Thread th1 = new Thread(new Runnable() 
	    	{
	            public void run() 
	            {
	            	mNewsCategory = null;
	            	HttpClient httpClient = new DefaultHttpClient();
	                HttpPost postRequest = new HttpPost(urlNewsCategory);
	                HttpResponse response = null;
	                try 
	                {
	                	response = httpClient.execute(postRequest);
	        		} 
	                catch (ClientProtocolException e) 
	                {
	        			// TODO Auto-generated catch block
	        			e.printStackTrace();
	        		}
	                catch (IOException e) 
	        		{
	        			// TODO Auto-generated catch block
	        			e.printStackTrace();
	        		}
	                
	                if (response!=null)
	                {
	                	try 
	                	{
	                		mNewsCategory = mXmlHelperNews.ParseNewsFeedFromXmlStream(response.getEntity().getContent());
						} 
	                	catch (IllegalStateException e) 
	                	{
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
	                	catch (IOException e) 
	                	{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                	if (mNewsCategory!=null)
	                		mLoadEvents.loadNewsCategorySuccess(mNewsCategory);
	                	else 
	                		mLoadEvents.loadNewsCategoryFailed();
	                	//mHandler.post(updateRunnable);
	                } //if (response!=null)
	            } //run()
	          });
	        th1.start(); //new Thread
	        bRet = true;
		}
		catch (Exception ex)
		{
			bRet = false;
		}
		return bRet;
	} //loadNewsCategory()
	
	public void loadThumbs()
	{
		Thread th1 = new Thread(new Runnable() 
    	{
            public void run() 
            {
            	try
            	{
            		if (mNewsCategory!=null && mNewsCategory.mItemFeedMap!=null)
                    {
                    	Set<String> keys = mNewsCategory.mItemFeedMap.keySet();
                    	Iterator<String> keysItr = keys.iterator();
                    	while (keysItr.hasNext())
                    	{
                    		String key = keysItr.next();
                    		NewsItem newsItem = mNewsCategory.mItemFeedMap.get(key);
                    		newsItem.loadThumbImage();
                    		if (mLoadEvents!=null)
                    			mLoadEvents.thumbLoaded(newsItem.mTitle, newsItem.mThumbBitmap);
                    	}
                    }
            	}
            	catch (Exception ex)
            	{
            		;
            	}
            } //run
    	}); //new Thread
    	th1.start();
	} //loadThumbs()
} //class NewsLoader
