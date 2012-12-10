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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;

public class MainActivity extends Activity implements NewsLoadEvents, AnimationListener {
	
	static class AnimParams {
        int left, right, top, bottom;

        void init(int left, int top, int right, int bottom) 
        {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }
    }
	
	private NewsLoader mNewsLoader = null;
	private NewsItemListAdapter mNewsItemsAdapter = null;
	private TextView tvNewsItemsLoading = null;
	private Spinner spnCountryFeed = null;
	private TextView tvCountrySpinnerLbl = null;
	private ImageButton ibMenuImg = null;
	private TextView tvTitle = null;
	
	ListView mNewsItemsList = null;
	List<NewsItem> mNewsList = null;
	String mTitleText = "";
	String mCountry = "USA";
	
	// Animation stuff
	AnimParams animParams = new AnimParams();
	RelativeLayout rlMenuCategories = null;
	RelativeLayout rlMainNewsList = null;
	boolean menuOut = false;
	
	@Override
	public void onAnimationEnd(Animation arg0)
	{
		menuOut = !menuOut;
        if (!menuOut)
            rlMenuCategories.setVisibility(View.INVISIBLE);
        layoutApp(menuOut);
	}

	@Override
	public void onAnimationRepeat(Animation arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onAnimationStart(Animation arg0) {
		// TODO Auto-generated method stub
	}
	
	boolean showOption1 = false;
	private void animateToggleCategoriesMenu()
	{
	    Animation anim;
        int w = rlMainNewsList.getMeasuredWidth();
        int h = rlMainNewsList.getMeasuredHeight();
        int left = (int) (rlMainNewsList.getMeasuredWidth() * 0.8);
        
        if (!menuOut) 
        {
            //anim = AnimationUtils.loadAnimation(this, R.anim.push_right_in);
            anim = new TranslateAnimation(0, left, 0, 0);
            rlMenuCategories.setVisibility(View.VISIBLE);
            animParams.init(left, 0, left + w, h);
        }
        else 
        {
            anim = new TranslateAnimation(0, -left, 0, 0);
            rlMenuCategories.setVisibility(View.VISIBLE);
            animParams.init(0, 0, w, h);
        }
        
        anim.setDuration(500);
        anim.setAnimationListener(this);
        //Tell the animation to stay as it ended (we are going to set the rlMainNewsList.layout first than remove this property)
        anim.setFillAfter(true);


        // Only use fillEnabled and fillAfter if we don't call layout ourselves.
        // We need to do the layout ourselves and not use fillEnabled and fillAfter because when the anim is finished
        // although the View appears to have moved, it is actually just a drawing effect and the View hasn't moved.
        // Therefore clicking on the screen where the button appears does not work, but clicking where the View *was* does
        // work.
        // anim.setFillEnabled(true);
        // anim.setFillAfter(true);

        rlMainNewsList.startAnimation(anim);
	}
	
	void layoutApp(boolean menuOut) {
        rlMainNewsList.layout(animParams.left, animParams.top, animParams.right, animParams.bottom);
        //Now that we've set the app.layout property we can clear the animation, flicker avoided :)
        rlMainNewsList.clearAnimation();
    }
	
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
		
		ibMenuImg = (ImageButton) findViewById(R.id.ibMenuImg);
        if (ibMenuImg!=null)
        	ibMenuImg.setOnClickListener(mBtnClickListener);
        
        tvTitle = (TextView) findViewById(R.id.tvAppTitle);
        if (tvTitle!=null)
        	tvTitle.setText(mTitleText);
		
		rlMenuCategories = (RelativeLayout) findViewById(R.id.rlMenuCategories);
    	rlMainNewsList = (RelativeLayout) findViewById(R.id.rlMainNewsList);
		
		// load news
		LoadNews();
		
		loadCountryFeedList();
    }
    
    // Display the toolbar notification
    // THIS IS FOR FUTURE USE
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
        	case R.id.ibMenuImg: //m_btnHome
        		animateToggleCategoriesMenu();
        		break;
        		
        	case R.id.tvCountrySpinnerLbl:
        		if (tvCountrySpinnerLbl != null)
        		{
        			//showNotification("YAY, got it John!");
        			spnCountryFeed.performClick();
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
		if (!Utils.checkInternetConnection(this, true, tvNewsItemsLoading))
			return;
	}
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
        	animateToggleCategoriesMenu();
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
    		
    		spnCountryFeed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    	    	// onItemSelected
    	    	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
    				final String country = (String)parent.getItemAtPosition(pos);
    				MainActivity.this.runOnUiThread(new Runnable() {
    					public void run() {
    						tvCountrySpinnerLbl.setText(country);
    						layoutApp(menuOut);
    					} //OnItemClickListener::onItemSelected::run()
    				});
    			} //OnItemClickListener::onItemSelected
    			
    	    	// onNothingSelected
    			public void onNothingSelected(AdapterView<?> arg0) {
    				// TODO
    			} //OnItemClickListener::onNothingSelected()
    		});
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
				if (tvTitle!=null)
		        	tvTitle.setText(mTitleText);
				mNewsLoader.loadThumbs();
				if (tvNewsItemsLoading!=null)
					tvNewsItemsLoading.setVisibility(View.GONE);
				
				menuOut = false;
				layoutApp(menuOut);
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
