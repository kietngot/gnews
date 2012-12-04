package com.innovative.gnews.feed;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class XmlHelperNews extends DefaultHandler {
	boolean isParsing = false;
	ImageFeed mTmpImageFeed = null;
	NewsFeed mNewsFeed = null;
	ItemFeed mTmpItemFeed = null;
	String mTmpText = "";
	
	public NewsFeed ParseNewsFeedFromXmlFile(String xmlFilePath) {
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
	
	public NewsFeed ParseNewsFeedFromXmlString(String xmlString)
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
	
	public NewsFeed ParseNewsFeedFromXmlStream(InputStream inStream) 
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
			mNewsFeed = null;
			se.printStackTrace();
		}
		catch(ParserConfigurationException pce) 
		{
			mNewsFeed = null;
			pce.printStackTrace();
		}
		catch (IOException ie) 
		{
			mNewsFeed = null;
			ie.printStackTrace();
		}
		isParsing = false;
		return mNewsFeed;
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
			mTmpItemFeed = null;
			mTmpImageFeed = null;
			mNewsFeed = new NewsFeed();
		}
		else if(localName.equalsIgnoreCase("image"))
		{
			mTmpImageFeed = new ImageFeed();
		}
		else if(localName.equalsIgnoreCase("item"))
		{
			mTmpItemFeed = new ItemFeed();
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException 
	{
		mTmpText = new String(ch,start,length);
		mTmpText = mTmpText.trim();
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException 
	{
		if (mNewsFeed==null)
			return;
		if (mTmpItemFeed != null)
		{
			if(localName.equalsIgnoreCase("item")) 
			{
				// TODO: Clone this and insert (not just reference)
				mNewsFeed.mItemFeedMap.put(mTmpItemFeed.mTitle, mTmpItemFeed);
			}
		}
		else if (mTmpImageFeed != null)
		{
			if(localName.equalsIgnoreCase("image")) 
			{
				mNewsFeed.mItemFeedMap.put(mTmpItemFeed.mTitle, mTmpItemFeed);
			}
		}
		else // Main News Feed (i.e., "channel")
		{
			if(localName.equalsIgnoreCase("title")) 
			{
				mNewsFeed.mTitle = mTmpText;
			}
		}
		// reset text
		mTmpText = "";
	}
}