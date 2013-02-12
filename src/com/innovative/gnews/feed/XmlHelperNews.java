package com.innovative.gnews.feed;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.webkit.MimeTypeMap;


public class XmlHelperNews extends DefaultHandler {
	private NewsCategory mNewsCategory = null;
	
	private NewsImage mTmpNewsImage = null;
	private NewsItem mTmpNewsItem = null;
	private String mTmpText = "";
	
	private boolean mStopParsing = false;
	private Object mStopParsingLock = new Object();
	
	private boolean mIsParsing = false;
	private Object mIsParsingLock = new Object();
	
	private Object mParsingLock = new Object();
		
	private void setIsParsing(boolean bIsParsing) {
		synchronized(mIsParsingLock) {
			mIsParsing = bIsParsing;
		}
	} //setParsing()
	
	private void setStopParsing(boolean stopParsing) {
		synchronized(mStopParsingLock) {
			mStopParsing = stopParsing;
		}
	} //setParsing()
	
	private boolean stopRequested() {
		boolean bStopRequested = false;
		synchronized(mStopParsingLock) {
			bStopRequested = mStopParsing;
		}
		return bStopRequested;
	} //stopRequested()
	
	XmlHelperNews() {
		mStopParsing = false;
		mIsParsing = false;
	}
	
	public void waitForParsing() {
		try {
			mParsingLock.wait();
		} 
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} //waitForParsing()()
	
	public boolean isParsing() {
		return mIsParsing;
	} //isParsing()
	
	public void stopParsing() {
		setStopParsing(true);
	} //setParsing()
	
	public NewsCategory parseNewsFeedFromXmlFile(String xmlFilePath) {
		FileInputStream fstream = null;
		setIsParsing(false);
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
			return parseNewsFeedFromXmlStream(fstream);
		return null;
	} //parseBooksFromXmlFile()
	
	
	public NewsCategory parseNewsFeedFromXmlString(String xmlString) {
		InputStream inStream = null;
		try {
			inStream = new ByteArrayInputStream(xmlString.getBytes("UTF8"));
		}
		catch (UnsupportedEncodingException e) {
			inStream = null;
			e.printStackTrace();
		}
		if (inStream!=null)
			return parseNewsFeedFromXmlStream(inStream);
		return null;
	} //parseBooksFromXmlString()
	
	
	public NewsCategory parseNewsFeedFromXmlStream(InputStream inStream) {
		synchronized(mParsingLock) {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			if (inStream==null)
				return null;
			
			try {
				setIsParsing(true);
				//get a new instance of parser
				SAXParser sp = spf.newSAXParser();
				
				//parse
				sp.parse(inStream, this);
			}
			catch (Exception e) {
				mNewsCategory = null;
				e.printStackTrace();
			}
			setIsParsing(false);
			setStopParsing(false); //reset the stopParsing flag to false here, coz we're no more parsing now.
			mParsingLock.notifyAll();
		} //synchronized(mParsingLock)
		return mNewsCategory;
	} //parseBooksFromXmlStream()
	
	
	private int makeInt(String intValueStr) {
		if (intValueStr==null || intValueStr.length()<=0)
			return 0;
		return Integer.parseInt(intValueStr);
	} //makeInt()
	
	
	//Event Handlers
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (stopRequested())
			throw new SAXException("\nXmlHelperNews::startElement - Stop requested");
		//reset
		mTmpText = "";
		if(localName.equalsIgnoreCase("channel")) {
			mTmpNewsItem = null;
			mTmpNewsImage = null;
			mNewsCategory = new NewsCategory();
			mNewsCategory.mItemFeedMap = new HashMap<String, NewsItem>();
		}
		else if(localName.equalsIgnoreCase("image")) {
			mTmpNewsImage = new NewsImage();
		}
		else if(localName.equalsIgnoreCase("item")) {
			mTmpNewsItem = new NewsItem();
		}
		else if (mTmpNewsItem!=null && localName.equalsIgnoreCase("guid")) {
			String tmpStr = attributes.getValue("isPermaLink");
			mTmpNewsItem.mIsGuidPermanentLink = tmpStr.equalsIgnoreCase("true");
		}
	} //startElement()
	
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (stopRequested())
			throw new SAXException("\nXmlHelperNews::characters - Stop requested");
		String tmpText = new String(ch,start,length);
		mTmpText += tmpText.trim();
	} //characters()
	
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (stopRequested())
			throw new SAXException("\nXmlHelperNews::characters - Stop requested");
		
		if (mNewsCategory==null)
			return;
		if (mTmpNewsItem != null) {
			if(localName.equalsIgnoreCase("item")) {
				// TODO: Copy the mTmpNewsImage (not just reference)
				mNewsCategory.mItemFeedMap.put(mTmpNewsItem.mSummary, mTmpNewsItem.clone());
				mTmpNewsItem = null;
			}
			else if(localName.equalsIgnoreCase("title")) {
				mTmpNewsItem.mSource = NewsItem.getSource(mTmpText);
				mTmpNewsItem.mSummary = NewsItem.getSummary(mTmpText);
			}
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
		else if (mTmpNewsImage != null) {
			if(localName.equalsIgnoreCase("image")) {
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
		else { // Contents of Main News Feed (i.e., "channel")
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
	} //endElement()
	
} //class XmlHelperNews