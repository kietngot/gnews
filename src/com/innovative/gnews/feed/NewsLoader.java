package com.innovative.gnews.feed;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.BitmapFactory;

import com.innovative.gnews.feed.NewsLoadEvents;

// This is the class that loads the news.
// Create an instance of NewsLoader and call loadNewsCategory
// TODO: For the current app, it is ok, but generally we need some locking
//	if accessed from multiple threads.
public class NewsLoader {
	private XmlHelperNews mXmlHelperNews = null;
	private NewsLoadEvents mLoadEvents = null;
	private NewsCategory mNewsCategory = null;
	private Thread mLoadThread = null;
	
	public NewsLoader(NewsLoadEvents loadEvents)
	{
		mLoadEvents = loadEvents;
		mXmlHelperNews = new XmlHelperNews();
	} //NewsLoader()
	
	public NewsCategory getNewsCategory()
	{
		return mNewsCategory;
	}
	
	// This function starts its own thread for loading the news
	public boolean loadNewsCategory(String url)
	{
		if (url==null || url.isEmpty())
			return false;
		
		// reset the mNewsCategory
		mNewsCategory = null;
		
		final String urlNewsCategory = url;
		boolean bRet = false;
		try
		{
			mLoadThread = new Thread(new Runnable() 
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
	                catch (Exception e) 
	                {
	                	response = null;
	        		}
	                
	                if (response!=null)
	                {
	                	try 
	                	{
	                		HttpEntity respEntity = response.getEntity();
	                		if (respEntity!=null)
	                			mNewsCategory = mXmlHelperNews.parseNewsFeedFromXmlStream(respEntity.getContent());
						} 
	                	catch (Exception e) 
	                	{
	                		mNewsCategory = null;
						} 
	                	if (mNewsCategory!=null)
	                	{
	                		mLoadEvents.loadNewsCategorySuccess(mNewsCategory);
	                		
	                		// load thumbs now in another thread
	                		loadThumbImages();
	                	}
	                	else 
	                		mLoadEvents.loadNewsCategoryFailed();
	                	//mHandler.post(updateRunnable);
	                } //if (response!=null)
	                else
	                {
	                	mLoadEvents.loadNewsCategoryFailed();
	                }
	            } //run()
	          });
	        mLoadThread.start(); //new Thread
	        bRet = true;
		}
		catch (Exception ex)
		{
			bRet = false;
		}
		return bRet;
	} //loadNewsCategory()
	
	
	// The loadNewCategory only loads the text and the link to the thumbs
	// Now this function loads the thumb images, and notifies for each thumb
	// so UI can update.
	private void loadThumbImages()
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
            		loadThumbImage(newsItem);
            		if (mLoadEvents!=null)
            			mLoadEvents.thumbLoaded(newsItem.mTitle, newsItem.mThumbBitmap);
            	}
            	if (mLoadEvents!=null)
        			mLoadEvents.allThumbsLoaded();
            }
    	}
    	catch (Exception ex)
    	{
    		;
    	}
	} //loadThumbImages()
	
	// Loads thumb from given thumb link
	public void loadThumbImage(NewsItem newsItem) 
	{
		newsItem.mThumbBitmap = null;
		if (newsItem.mThumbImageLink==null || newsItem.mThumbImageLink.isEmpty())
			return;
		try
    	{
            URL aURL = new URL(newsItem.mThumbImageLink); 
            URLConnection conn = aURL.openConnection(); 
            conn.connect(); 
            InputStream is = conn.getInputStream(); 
            BufferedInputStream bis = new BufferedInputStream(is); 
            newsItem.mThumbBitmap = BitmapFactory.decodeStream(bis); 
            bis.close(); 
            is.close();
    	}
    	catch (Exception ex)
    	{
    		newsItem.mThumbBitmap = null;
    	}
    } 
} //class NewsLoader
