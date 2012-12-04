package com.innovative.gnews.feed;

public class NewsImage implements Cloneable {
	public String mTitle;
	public String mImageUrl; //<url>
	public String mLink;
	
	public NewsImage clone()
    {
		try
	    {
			return (NewsImage)super.clone();
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
