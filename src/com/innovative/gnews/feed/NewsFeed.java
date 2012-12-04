package com.innovative.gnews.feed;

import java.util.HashMap;

// The class NewsFeed represents a 'channel' in the google news feed
public class NewsFeed implements Cloneable {
	public String mTitle;
	public String mLink; //URI?
	public String mLanguage;
	public String mWebMaster; //Webmaster email
	public String mCopyright;
	public String mPubDate;
	public String mLastBuildDate;
	
	public ImageFeed mImage; // Main image (Google logo usually for gnews)
	public HashMap<String, ItemFeed> mItemFeedMap;
}
