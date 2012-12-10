package com.innovative.gnews;

import java.util.ArrayList;
import java.util.List;
import com.innovative.gnews.Titlebar;
import com.innovative.gnews.R;
import com.innovative.gnews.feed.NewsCategory;
import com.innovative.gnews.feed.NewsItem;
import com.innovative.gnews.feed.NewsLoadEvents;
import com.innovative.gnews.feed.NewsLoader;
import com.innovative.gnews.utils.Utils;

import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewTreeObserver;

public class MainActivity extends Activity implements NewsLoadEvents {
	private NewsLoader mNewsLoader = null;
	private NewsItemListAdapter mNewsItemsAdapter = null;
	private TextView tvNewsItemsLoading = null;
	private Spinner spnCountryFeed = null;
	private TextView tvCountrySpinnerLbl = null;
	
	
	ListView mNewsItemsList = null;
	List<NewsItem> mNewsList = null;
	String mTitleText = "";
	String mCountry = "USA";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	mNewsLoader = new NewsLoader(this);
    	
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        
        mTitleText = "Gnews"; //getString(R.string.loading);
        
		tvNewsItemsLoading = (TextView)findViewById(R.id.tvNewsItemsLoading);
		if (tvNewsItemsLoading!=null)
		{
			tvNewsItemsLoading.setText(R.string.loading);
			tvNewsItemsLoading.setVisibility(View.VISIBLE);
		}
		mNewsItemsList = (ListView) findViewById(R.id.lvNewsItemsList);
		mNewsList = new ArrayList<NewsItem>();
		mNewsItemsAdapter = new NewsItemListAdapter(this, R.layout.newsitem_view, mNewsList);
		mNewsItemsList.setAdapter(mNewsItemsAdapter);
		mNewsItemsList.setAdapter(mNewsItemsAdapter);
		mNewsItemsList.setClickable(true);
		mNewsItemsList.setOnItemClickListener(mListItemClickListener);
		
		// Categories controls
		spnCountryFeed = (Spinner)findViewById(R.id.spnCountryFeedList);
		if (spnCountryFeed!=null)
		{
			spnCountryFeed.setVisibility(View.INVISIBLE);
		}
		
		tvCountrySpinnerLbl = (TextView) findViewById(R.id.tvCountrySpinnerLbl);
		if (tvCountrySpinnerLbl != null)
		{
			tvCountrySpinnerLbl.setVisibility(View.VISIBLE);
			tvCountrySpinnerLbl.setOnClickListener(mBtnClickListener);
		}
		
		// Adjust the categories once the layout is created.
		View v = getWindow().getDecorView().findViewById(android.R.id.content);
		v.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
	        @Override
	        public void onGlobalLayout() {
	            Titlebar.showCategoriesMenu(false);
	        }
	    });
		
		// load news
		LoadNews();
		
		loadCountryFeedList();
    }
    
 // Display the topbar notification
 	private void showNotification(String text) {
 		Notification n = new Notification();
 				
 		n.flags |= Notification.FLAG_SHOW_LIGHTS;
       	n.flags |= Notification.FLAG_AUTO_CANCEL;

         n.defaults = Notification.DEFAULT_ALL;
       	
         
 		n.icon = R.drawable.ic_webkit;
 		n.when = System.currentTimeMillis();

 		// Simply open the parent activity
 		PendingIntent pi = PendingIntent.getActivity(this, 0,
 				new Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER), 0);
 		
 		// Change the name of the notification here
 		n.setLatestEventInfo(this, "Gnews", text, pi);

 		NotificationManager mNotifMan = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
 		mNotifMan.notify(0, n);
 	}
    
    private OnClickListener mBtnClickListener = new OnClickListener() {
        public void onClick(View v) {
        	int id = v.getId();
        	switch (id)
        	{
        	case R.id.tvCountrySpinnerLbl:
        		if (tvCountrySpinnerLbl != null)
        		{
        			showNotification("YAY, got it John!");
        			//tvCountrySpinnerLbl.setVisibility(View.INVISIBLE);
        			//spnCountryFeed.performClick();
        			//spnCountryFeed.setVisibility(View.VISIBLE);
        			
        		}
        		break;
        	}
        } //onClick
    }; //mBtnClickListener
    
    @Override
    public void onStart() {
    	super.onStart();
    }
    
    @Override
	public void onResume() 
	{
		super.onResume();
		Titlebar.InitTitlebar(this, mTitleText);
		if (!Utils.checkInternetConnection(this, true, tvNewsItemsLoading))
			return;
	}
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
        	Titlebar.toggleCategoriesMenu();
        	return true;
        }
        return super.onKeyUp(keyCode, event);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.layout.activity_menu_categories, menu);
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
    	if (!Utils.isNetworkConnected(this))
    	{
    		if (tvNewsItemsLoading!=null)
    		{
    			tvNewsItemsLoading.setText(R.string.nonetwork);
				tvNewsItemsLoading.setVisibility(View.VISIBLE);
    		}
    		else
    		{
				Toast.makeText(this, R.string.nonetwork, Toast.LENGTH_SHORT).show();
    		}
    		return;
    	}
    	
    	if (mNewsLoader==null)
    		return;
    	mNewsLoader.loadNewsCategory("http://news.google.com/news?ned=us&topic=h&output=rss&num=50");
    }
    
    private void loadCountryFeedList()
    {
    	if (spnCountryFeed != null)
    	{
    		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line);
    		adapter.add(new String("USA"));
    		adapter.add(new String("Argentina"));
    		adapter.add(new String("Australia"));
    		adapter.add(new String("Austria"));
    		adapter.add(new String("Brazil"));
    		adapter.add(new String("Canada"));
    		adapter.add(new String("Chile"));
    		adapter.add(new String("France"));
    		adapter.add(new String("Germany"));
    		adapter.add(new String("India"));
    		adapter.add(new String("Ireland"));
    		adapter.add(new String("Italy"));
    		adapter.add(new String("Mexico"));
    		adapter.add(new String("Peru"));
    		adapter.add(new String("Portugal"));
    		adapter.add(new String("Russia"));
    		adapter.add(new String("Spain"));
    		adapter.add(new String("UK"));
    		adapter.add(new String("Venezuela"));
    		
    		spnCountryFeed.setAdapter(adapter);
    	}
    }

	@Override
	public void loadNewsCategorySuccess(NewsCategory newsCategory) {
		// TODO: send the UI a message to update the news
		//newsCategory.mCopyright = newsCategory.mCopyright;
		final NewsCategory newsCat = newsCategory;
		MainActivity.this.runOnUiThread(new Runnable() {
			public void run() {
				displayNews(newsCat);
				mTitleText = getString(R.string.category_topnews);
				Titlebar.setTitle(mTitleText);
				mNewsLoader.loadThumbs();
				if (tvNewsItemsLoading!=null)
					tvNewsItemsLoading.setVisibility(View.GONE);
			}
		});
	}

	@Override
	public void loadNewsCategoryFailed() {
		// TODO Auto-generated method stub
		if (tvNewsItemsLoading!=null)
			tvNewsItemsLoading.setVisibility(View.GONE);
	}
	
	@Override
	public void thumbLoaded(final String itemTitle, final Bitmap thumb)
	{
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
			if (tvNewsItemsLoading!=null)
				tvNewsItemsLoading.setVisibility(View.GONE);
			
			NewsItem item = (NewsItem)mNewsItemsList.getItemAtPosition(position);
			Intent pageIntent = new Intent(MainActivity.this, NewsPageActivity.class);
			pageIntent.putExtra("NewsPageURL", new String(item.mLink));
			startActivityForResult(pageIntent, 1);
		}

	}; //OnItemClickListener
}
