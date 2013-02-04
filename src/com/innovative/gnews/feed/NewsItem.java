package com.innovative.gnews.feed;
import android.graphics.Bitmap;

public class NewsItem implements Cloneable {
	public String mSummary = null;
	public String mSource = null;
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
		if (startIndex<0)
			return null;
		
		startIndex += startSearchStr.length();
		description = description.substring(startIndex);
		int endIndex = description.indexOf(endSearchStr);
		if (endIndex<0)
			return null;
		//endIndex += startSearchStr.length();
		thumbLink = description.substring(0, endIndex);
		thumbLink = thumbLink.replace("//", "http://");
		return thumbLink;
	} //getThumbLink()
	
	public static String getSource(String itemText)
	{
		String source = "Unknown Source";
		
		int index = itemText.lastIndexOf("-");
		if (index>=0)
			source = itemText.substring(index+1);
		source = source.trim();
		return source;
	} //getSource()
	
	public static String getSummary(String itemText)
	{
		String summary = itemText;
		
		int index = itemText.lastIndexOf("-");
		if (index>=0)
			summary = itemText.substring(0, index);
		summary = summary.trim();
		return summary;
	} //getSummary()
	
	
	public NewsItem clone()
    {
		NewsItem newsItem = null;
		try
	    {
			newsItem = (NewsItem)super.clone();
	    }
		catch( CloneNotSupportedException e )
		{
			newsItem = null;
		}
		catch (Exception ex)
		{
			newsItem = null;
		}
		return newsItem;
    } //clone()
}
