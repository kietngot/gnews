package com.innovative.gnews.feed;

import java.util.HashMap;

// The class NewsFeed represents a 'channel' in the google news feed
public class NewsCategory {
	public String mTitle = null;
	public String mLink = null;
	public String mLanguage = null;
	public String mWebMaster = null; //Webmaster's email
	public String mCopyright = null;
	public String mPubDate = null;
	public String mLastBuildDate = null;
	
	public NewsImage mNewsImage = null; // Main image (Google logo usually for gnews)
	public HashMap<String, NewsItem> mItemFeedMap = null;
}
