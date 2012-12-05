package com.innovative.gnews.feed;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.webkit.MimeTypeMap;


public class XmlHelperNews extends DefaultHandler {
	boolean isParsing = false;
	NewsCategory mNewsCategory = null;
	NewsImage mTmpNewsImage = null;
	NewsItem mTmpNewsItem = null;
	String mTmpText = "";
	
	XmlHelperNews()
	{
	}
	
	public NewsCategory ParseNewsFeedFromXmlFile(String xmlFilePath) {
		FileInputStream fstream = null;
		try 
		{
			fstream = new FileInputStream(xmlFilePath);
		}
		catch (FileNotFoundException e1) 
		{
			fstream = null;
			e1.printStackTrace();
		}
		if (fstream!=null)
			return ParseNewsFeedFromXmlStream(fstream);
		return null;
	} //ParseBooksFromXmlFile()
	
	public NewsCategory ParseNewsFeedFromXmlString(String xmlString)
	{
		InputStream inStream = null;
		try 
		{
			inStream = new ByteArrayInputStream(xmlString.getBytes("UTF8"));
		}
		catch (UnsupportedEncodingException e) 
		{
			inStream = null;
			e.printStackTrace();
		}
		if (inStream!=null)
			return ParseNewsFeedFromXmlStream(inStream);
		return null;
	} //ParseBooksFromXmlString()
	
	public NewsCategory ParseNewsFeedFromXmlStream(InputStream inStream) 
	{
		SAXParserFactory spf = SAXParserFactory.newInstance();
		if (inStream==null)
			return null;
		
		try
		{
			isParsing = true;
			//get a new instance of parser
			SAXParser sp = spf.newSAXParser();
			
			//parse
			sp.parse(inStream, this);
		}
		catch(SAXException se) 
		{
			mNewsCategory = null;
			se.printStackTrace();
		}
		catch(ParserConfigurationException pce) 
		{
			mNewsCategory = null;
			pce.printStackTrace();
		}
		catch (IOException ie) 
		{
			mNewsCategory = null;
			ie.printStackTrace();
		}
		isParsing = false;
		return mNewsCategory;
	} //ParseBooksFromXmlStream()
	
	int makeInt(String intValueStr)
	{
		if (intValueStr==null || intValueStr.length()<=0)
			return 0;
		return Integer.parseInt(intValueStr);
	}
	//Event Handlers
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException 
	{
		//reset
		mTmpText = "";
		
		if(localName.equalsIgnoreCase("channel"))
		{
			mTmpNewsItem = null;
			mTmpNewsImage = null;
			mNewsCategory = new NewsCategory();
			mNewsCategory.mItemFeedMap = new HashMap<String, NewsItem>();
		}
		else if(localName.equalsIgnoreCase("image"))
		{
			mTmpNewsImage = new NewsImage();
		}
		else if(localName.equalsIgnoreCase("item"))
		{
			mTmpNewsItem = new NewsItem();
		}
		else if (mTmpNewsItem!=null && localName.equalsIgnoreCase("guid"))
		{
			String tmpStr = attributes.getValue("isPermaLink");
			mTmpNewsItem.mIsGuidPermanentLink = tmpStr.equalsIgnoreCase("true");
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException 
	{
		String tmpText = new String(ch,start,length);
		mTmpText += tmpText.trim();
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException 
	{
		if (mNewsCategory==null)
			return;
		if (mTmpNewsItem != null)
		{
			if(localName.equalsIgnoreCase("item")) 
			{
				// TODO: Copy the mTmpNewsImage (not just reference)
				mNewsCategory.mItemFeedMap.put(mTmpNewsItem.mTitle, mTmpNewsItem.clone());
				mTmpNewsItem = null;
			}
			else if(localName.equalsIgnoreCase("title"))
				mTmpNewsItem.mTitle = mTmpText;
			else if(localName.equalsIgnoreCase("link"))
				mTmpNewsItem.mLink = mTmpText;
			else if(localName.equalsIgnoreCase("guid"))
				mTmpNewsItem.mGuid = mTmpText;
			else if(localName.equalsIgnoreCase("category"))
				mTmpNewsItem.mCategory = mTmpText;
			else if(localName.equalsIgnoreCase("pubDate"))
				mTmpNewsItem.mPubDate = mTmpText;
			else if(localName.equalsIgnoreCase("description"))
				mTmpNewsItem.mThumbImageLink = NewsItem.parseThumbLink(mTmpText);
		}
		else if (mTmpNewsImage != null)
		{
			if(localName.equalsIgnoreCase("image")) 
			{
				// TODO: Copy the mTmpNewsImage (not just reference)
				mNewsCategory.mNewsImage = mTmpNewsImage.clone();
				mTmpNewsImage = null;
			}
			else if(localName.equalsIgnoreCase("title"))
				mTmpNewsImage.mTitle = mTmpText;
			else if(localName.equalsIgnoreCase("url"))
				mTmpNewsImage.mImageUrl = mTmpText;
			else if(localName.equalsIgnoreCase("link"))
				mTmpNewsImage.mLink = mTmpText;
		}
		else // Contents of Main News Feed (i.e., "channel")
		{
			if(localName.equalsIgnoreCase("title")) 
				mNewsCategory.mTitle = mTmpText;
			else if (localName.equalsIgnoreCase("link"))
				mNewsCategory.mLink = mTmpText;
			else if (localName.equalsIgnoreCase("language"))
				mNewsCategory.mLanguage = mTmpText;
			else if (localName.equalsIgnoreCase("webMaster"))
				mNewsCategory.mWebMaster = mTmpText;
			else if (localName.equalsIgnoreCase("copyright"))
				mNewsCategory.mCopyright = mTmpText;
			else if (localName.equalsIgnoreCase("pubDate"))
				mNewsCategory.mPubDate = mTmpText;
			else if (localName.equalsIgnoreCase("lastBuildDate"))
				mNewsCategory.mLastBuildDate = mTmpText;
		}
		// reset text
		mTmpText = "";
	}
}