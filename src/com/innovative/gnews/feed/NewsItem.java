package com.innovative.gnews.feed;

public class NewsItem implements Cloneable {
	public String mTitle = null;
	public String mLink = null;
	public String mGuid = null;
	public boolean mIsGuidPermanentLink = false;
	public String mCategory = null;
	public String mPubDate = null;
	//public String mDescription = null;
	public String mThumbImageLink = null; //Extracted from Description
	
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
		thumbLink = thumbLink.replace("//", "");
		return thumbLink;
	} //getThumbLink()
	
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
