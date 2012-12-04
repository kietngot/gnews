package com.innovative.gnews.feed;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import com.innovative.gnews.feed.NewsLoadEvents;

public class NewsLoader {
	private XmlHelperNews mXmlHelperNews = null;
	private NewsLoadEvents mLoadEvents = null;
	
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
	                	NewsCategory newsCategory = null;
	                	try 
	                	{
	                		newsCategory = mXmlHelperNews.ParseNewsFeedFromXmlStream(response.getEntity().getContent());
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
	                	if (newsCategory!=null)
	                		mLoadEvents.loadNewsCategorySuccess(newsCategory);
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
	}
	
} //class NewsLoader
