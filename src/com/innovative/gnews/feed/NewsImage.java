package com.innovative.gnews.feed;

public class NewsImage implements Cloneable {
	public String mTitle;
	public String mImageUrl; //<url>
	public String mLink;
	
	@Override
	public NewsImage clone()
	{
		NewsImage retImage = null;
		try 
		{
			retImage = (NewsImage) super.clone();
		}
		catch (CloneNotSupportedException e) 
		{
			retImage = null;
		}
		return retImage;
	} //clone()
}
