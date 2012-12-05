package com.innovative.gnews;

import java.util.ArrayList;
import java.util.List;
import com.innovative.gnews.Titlebar;
import com.innovative.gnews.R;
import com.innovative.gnews.feed.NewsCategory;
import com.innovative.gnews.feed.NewsItem;
import com.innovative.gnews.feed.NewsLoadEvents;
import com.innovative.gnews.feed.NewsLoader;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements NewsLoadEvents {
	private NewsLoader mNewsLoader = null;
	private NewsItemListAdapter mNewsItemsAdapter = null;
	ListView mNewsItemsList = null;
	List<NewsItem> mNewsList = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	mNewsLoader = new NewsLoader(this);
    	
        super.onCreate(savedInstanceState);
        final boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
        
	     // We can access custom title elements only after setting FEATURE_CUSTOM_TITLE
		if ( customTitleSupported ) {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.main_title);
			Titlebar.InitTitlebar(this, R.string.loading);
		}
		
		mNewsItemsList = (ListView) findViewById(R.id.lvNewsItemsList);
		mNewsList = new ArrayList<NewsItem>();
		mNewsItemsAdapter = new NewsItemListAdapter(this, R.layout.newsitem_view, mNewsList);
		mNewsItemsList.setAdapter(mNewsItemsAdapter);
		mNewsItemsList.setAdapter(mNewsItemsAdapter);
		mNewsItemsList.setClickable(true);
		mNewsItemsList.setOnItemClickListener(mListItemClickListener);
		
		// load news
		LoadNews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) 
		{
			case R.id.menu_exit:
				Titlebar.Reset(); // we reset title-bar here, because this is the only place where we exit from.
	    		finish();
	    		// Forcing the process to stop on exit (not a pretty solution).
	    		int pid = android.os.Process.myPid();
		        android.os.Process.killProcess(pid);
		        break;
		        
			case R.id.menu_settings:
				Context appCtx = getApplicationContext();
				Toast.makeText(appCtx, "Settings are not implemented yet.", Toast.LENGTH_SHORT).show();
				break;
		}
		return super.onOptionsItemSelected(item);
    }
    
    public void LoadNews()
    {
    	if (mNewsLoader==null)
    		return;
    	mNewsLoader.loadNewsCategory("http://news.google.com/news?ned=us&topic=h&output=rss");
    }

	@Override
	public void loadNewsCategorySuccess(NewsCategory newsCategory) {
		// TODO: send the UI a message to update the news
		//newsCategory.mCopyright = newsCategory.mCopyright;
		final NewsCategory newsCat = newsCategory;
		MainActivity.this.runOnUiThread(new Runnable() {
			public void run() {
				// TODO Auto-generated method stub
				displayNews(newsCat);
				Titlebar.InitTitlebar(MainActivity.this, R.string.category_topnews);
				mNewsLoader.loadThumbs();
			}
		});
	}

	@Override
	public void loadNewsCategoryFailed() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void thumbLoaded(final String itemTitle, final Bitmap thumb)
	{
		//mNewsItemsAdapter.notifyDataSetChanged();
		//mNewsItemsList.refreshDrawableState();
		MainActivity.this.runOnUiThread(new Runnable() {
			public void run() {
				if (mNewsItemsList!=null && mNewsItemsAdapter!=null)
				{
					for (int i=0; i<mNewsItemsAdapter.getCount(); i++)
					{
						NewsItem item = (NewsItem)mNewsItemsList.getAdapter().getItem(i);
						if (item.mTitle==itemTitle)
						{
							item.mThumbBitmap = thumb;
							mNewsItemsAdapter.notifyDataSetChanged();
							//mNewsItemsList.refreshDrawableState();
							break;
						}
					}
				}
			}
		});
		
		
	}
	
	// Presents news category (list of news items) on UI
	private void displayNews(NewsCategory newsCategory)
	{
		// TODO: Show some text that tells the user there are no news items
		if (newsCategory==null)
			return;
		
		List<NewsItem> newsList = null;
		if (newsCategory.mItemFeedMap!=null)
			newsList = new ArrayList<NewsItem>(newsCategory.mItemFeedMap.values());
		if (newsList!=null)
		{
			mNewsItemsAdapter = new NewsItemListAdapter(this, R.layout.newsitem_view, newsList);
			mNewsItemsList.setAdapter(mNewsItemsAdapter);
			mNewsItemsList.setClickable(true);
			mNewsItemsList.setOnItemClickListener(mListItemClickListener);
		}
	}
	
	private OnItemClickListener mListItemClickListener = new AdapterView.OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
		{
			NewsItem item = (NewsItem)mNewsItemsList.getItemAtPosition(position);
			
			// TODO: Get the item....
		}

	}; //OnItemClickListener
}
