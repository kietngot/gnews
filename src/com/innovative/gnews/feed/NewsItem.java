package com.innovative.gnews.feed;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class NewsItem implements Cloneable {
	public String mTitle = null;
	public String mLink = null;
	public String mGuid = null;
	public boolean mIsGuidPermanentLink = false;
	public String mCategory = null;
	public String mPubDate = null;
	//public String mDescription = null;
	public String mThumbImageLink = null; //Extracted from Description
	public Bitmap mThumbBitmap = null;
	
	
	public static String parseThumbLink(String description)
	{
		String thumbLink = "";
		
		if (description==null)
			return null;
		String startSearchStr = "<img src=\"";
		String endSearchStr = "\"";
		int startIndex = description.indexOf(startSearchStr);
		startIndex += startSearchStr.length();
		description = description.substring(startIndex);
		int endIndex = description.indexOf(endSearchStr);
		//endIndex += startSearchStr.length();
		thumbLink = description.substring(0, endIndex);
		thumbLink = thumbLink.replace("//", "http://");
		return thumbLink;
	} //getThumbLink()
	
	// Loads thumb in a thread
	public void loadThumbImage() 
	{
		mThumbBitmap = null;
		if (mThumbImageLink==null || mThumbImageLink.isEmpty())
			return;
		try
    	{
            URL aURL = new URL(mThumbImageLink); 
            URLConnection conn = aURL.openConnection(); 
            conn.connect(); 
            InputStream is = conn.getInputStream(); 
            BufferedInputStream bis = new BufferedInputStream(is); 
            mThumbBitmap = BitmapFactory.decodeStream(bis); 
            bis.close(); 
            is.close();
    	}
    	catch (Exception ex)
    	{
    		mThumbBitmap = null;
    	}
    } 
	
	public NewsItem clone()
    {
		try
	    {
			return (NewsItem)super.clone();
	    }
		catch( CloneNotSupportedException e )
		{
			;
		}
		catch (Exception ex)
		{
			;
		}
		return null;
    } //clone()
}
