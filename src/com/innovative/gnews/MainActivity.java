package com.innovative.gnews;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.innovative.gnews.Titlebar;
import com.innovative.gnews.R;
import com.innovative.gnews.db.GnewsDatabase;
import com.innovative.gnews.db.GnewsDatabase.Category;
import com.innovative.gnews.feed.NewsCategory;
import com.innovative.gnews.feed.NewsItem;
import com.innovative.gnews.feed.NewsLoadEvents;
import com.innovative.gnews.feed.NewsLoader;
import com.innovative.gnews.utils.Utils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;

/*
//TODO: 
	1. Make the Countries combo like google news web app
	2. Make the personal categories expand and shrinkdown
	3. Add about page
	4. Add current selection indicator in the category
	5. Download the news item to database (for "read later" feature)
	6. See if we can change the way we add the personal categories
		- like having a textbox with hint and the plus button would add directly.
	7. Figure a way not to let the menu to be hidden on choosing a category.
	8. Toast messages where necessary (when javascript enabled, etc.,)
	9. On adding a personal category, do not close the menu.
*/

public class MainActivity extends Activity implements NewsLoadEvents, AnimationListener {
	private HashMap<String, Category> mCountries = null;
	private HashMap<String, Category> mCategories = null;
	
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
	private RelativeLayout rlMainTitle = null;
	private ImageButton ibPersonalCategoriesAdd = null;
	private TextView tvTitle = null;
	
	ListView mNewsItemsList = null;
	List<NewsItem> mNewsList = null;
	String mTitleText = "";
	//boolean mJavascriptEnabled = false;
	Category mCountryLoaded = null;
	Category mCategoryLoaded = null;
	ArrayAdapter<Category> mAdapterCountry = null;
	ArrayAdapter<Category> mAdapterCategory = null;
	ArrayAdapter<Category> mAdapterCategoryPersonal = null;
	
	// Animation stuff
	AnimParams animParams = new AnimParams();
	RelativeLayout rlMenuCategories = null;
	RelativeLayout rlMainNewsList = null;
	boolean menuOut = false;
	protected GestureDetector gestureScanner;
	
	ListView lvPreconfiguredCategories = null;
	ListView lvPersonalCategories = null;
	TextView tvPersonalCategoriesHint = null;
	ImageButton ibRefreshNews = null;
	
	GnewsDatabase mDb = null;
	
	@Override
	public void onAnimationEnd(Animation arg0)
	{
		menuOut = !menuOut;
        if (!menuOut)
            rlMenuCategories.setVisibility(View.INVISIBLE);
        else
        	rlMenuCategories.setVisibility(View.VISIBLE);
        layoutApp(menuOut);
	}

	@Override
	public void onAnimationRepeat(Animation arg0) {
		
	}

	@Override
	public void onAnimationStart(Animation arg0) {
		
	}
	
	boolean showOption1 = false;
	private void animateToggleCategoriesMenu()
	{
	    Animation anim;
        int w = rlMainNewsList.getMeasuredWidth();
        int h = rlMainNewsList.getMeasuredHeight();
        int left = (int) (rlMenuCategories.getMeasuredWidth());
        
        menuOut = (rlMainNewsList.getLeft()>0);
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
	
	void layoutApp(boolean menuOut) 
	{
		// this happens when not initialized.
		if (rlMainNewsList==null || mNewsItemsList==null || 
				AppSettings.CurrentCountry==null || mCountryLoaded==null || 
				mCategories==null || AppSettings.CurrentCategory==null || 
				mCategoryLoaded==null)
		{
			return;
		}
		
        rlMainNewsList.layout(animParams.left, animParams.top, animParams.right, animParams.bottom);
        //Now that we've set the app.layout property we can clear the animation, flicker avoided :)
        rlMainNewsList.clearAnimation();
        
        // This is to enable the user to touch on listview to close the menu.
        if (menuOut)
    	{
    		mNewsItemsList.setOnTouchListener(mOnTouchListener);
    	}
    	else
    	{
    		mNewsItemsList.setOnTouchListener(null);
    		if (AppSettings.CurrentCountry.compareToIgnoreCase(mCountryLoaded.mKey) !=0 ||
    				AppSettings.CurrentCategory.compareToIgnoreCase(mCategoryLoaded.mKey)!=0)
    		{
    			loadNews();
    		}
    	}
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
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
		mNewsItemsList.setClickable(true);
		//mNewsItemsList.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
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
		
		rlMainTitle = (RelativeLayout) findViewById(R.id.rlMainTitle);
        if (rlMainTitle!=null)
        	rlMainTitle.setOnClickListener(mBtnClickListener);
		
        ibMenuImg = (ImageButton) findViewById(R.id.ibMenuImg);
        if (ibMenuImg!=null)
        	ibMenuImg.setOnClickListener(mBtnClickListener);
        
        ibPersonalCategoriesAdd = (ImageButton) findViewById(R.id.ibPersonalCategoriesAdd);
        if (ibPersonalCategoriesAdd!=null)
        	ibPersonalCategoriesAdd.setOnClickListener(mBtnClickListener);
        
        tvTitle = (TextView) findViewById(R.id.tvAppTitle);
        if (tvTitle!=null)
        	tvTitle.setText(mTitleText);
		
		rlMenuCategories = (RelativeLayout) findViewById(R.id.rlMenuCategories);
    	rlMainNewsList = (RelativeLayout) findViewById(R.id.rlMainNewsList);
		if (rlMainNewsList!=null)
			rlMainNewsList.setOnTouchListener(mOnTouchListener);
		
		lvPreconfiguredCategories = (ListView) findViewById(R.id.lvPreconfiguredCategories);
		lvPersonalCategories = (ListView) findViewById(R.id.lvPersonalCategories);
		tvPersonalCategoriesHint = (TextView) findViewById(R.id.tvPersonalCategoriesHint); 
		
		ibRefreshNews = (ImageButton) findViewById(R.id.ibRefreshNews);
		if (ibRefreshNews!=null)
			ibRefreshNews.setOnClickListener(mBtnClickListener);
		
		
		// Register the context menu
		registerForContextMenu(lvPersonalCategories);
		
		// Open database
		mDb = GnewsDatabase.getDatabase(this);
		if (mDb!=null && mDb.open())
		{
			// Initialize settings
			String jsEnabledStr = mDb.getSetting("JavaScriptEnabled");
			AppSettings.JavascriptEnabled = (Integer.parseInt(jsEnabledStr)==0)?false:true;
		}
		
		// Menu items
		prepareCategoriesMap();
		loadCountryFeedList();
		loadPersonalCategories();
		loadCategoriesList();
		
		// load news
		loadNews();
    }
    
    @Override
    public void onDestroy()
    {
    	if (mDb!=null)
    		mDb.close();
    	mDb = null;
    	super.onDestroy();
    }
    
    private void updateCurrentCountry(String value)
    {
    	AppSettings.CurrentCountry = value;
    	if (mDb!=null)
    		mDb.setSetting("CurrentCountry", value);
    } //updateCurrentCountry()
    
    private void updateCurrentCategory(String value)
    {
    	AppSettings.CurrentCategory = value;
    	if (mDb!=null)
    		mDb.setSetting("CurrentCategory", value);
    } //updateCurrentCategory()
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.personalcat_context_menu, menu);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete_personal_category:
            	{
            		if (mDb!=null)
            		{
            			int arrayAdapterPosition = menuInfo.position;
            			Category category = mAdapterCategoryPersonal.getItem(arrayAdapterPosition);
            			if(category!=null)
            			{
            				if (mDb.deleteCategory(category.mKey))
            				{
            					mAdapterCategoryPersonal.remove(category);
            					lvPersonalCategories.refreshDrawableState();
            					Toast.makeText(this, "Personal category \"" + category.mKey + "\" deleted!", Toast.LENGTH_SHORT).show();
            					showHidePersonalCategoriesHint();
            					
            					// If the current category is same as deleted one, 
            					//	reload the news with default category.
            					if (AppSettings.CurrentCategory.compareToIgnoreCase(category.mKey)==0)
            					{
            						updateCurrentCategory(GnewsDatabase.DEFAULT_CATEGORY);
            						loadNews();
            					}
            				}
            			}
            		}
            		lvPersonalCategories.getSelectedItem();
            		layoutApp(menuOut);
            	}
            	return super.onContextItemSelected(item);
            default:
                return super.onContextItemSelected(item);
        }
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
    
 	// TODO: Load countries from the database
 	private void prepareCountriesMap()
 	{
 		if (mDb!=null)
 		{
 			mCountries = mDb.getCountries(false);
 		}
 		else
 		{
 			// TODO: Remove this - This will be obsolete.
	 		mCountries = new HashMap<String, Category>();
	 		mCountries.put("U.S", new Category("U.S", "us")); //ned=us
			mCountries.put("Argentina", new Category("Argentina", "ar")); //ned=ar
			mCountries.put("Australia", new Category("Australia", "au")); //ned=au
			mCountries.put("Austria", new Category("Austria", "at")); //ned=at
			mCountries.put("Brazil", new Category("Brazil", "br"));
			mCountries.put("Canada", new Category("Canada", "ca"));
			mCountries.put("Chile", new Category("Chile", "cl"));
			mCountries.put("France", new Category("France", "fr"));
			mCountries.put("Germany", new Category("Germany", "de"));
			mCountries.put("India", new Category("India", "in"));
			mCountries.put("Pakistan", new Category("Pakistan", "en_pk"));
			mCountries.put("Ireland", new Category("Ireland", "ie"));
			mCountries.put("Italy", new Category("Italy", "it"));
			mCountries.put("Mexico", new Category("Mexico", "mx"));
			mCountries.put("Peru", new Category("Peru", "pe"));
			mCountries.put("Portugal", new Category("Portugal", "pt-PT_pt"));
			mCountries.put("Russia", new Category("Russia", "ru_ru"));
			mCountries.put("Spain", new Category("Spain", "es"));
			mCountries.put("UK", new Category("UK", "uk"));
			mCountries.put("Venezuela", new Category("Venezuela", "es_ve"));
			mCountries.put("Vietnam", new Category("Vietnam", "vi_vn"));
 		}
		AppSettings.CurrentCountry = mDb.getSetting("CurrentCountry");
 	} //prepareCountriesMap()
 	
 	
 	private void prepareCategoriesMap()
 	{
 		if (mDb!=null)
 		{
 			mCategories = mDb.getCategories(false);
 		}
 		else
 		{
 			// TODO: Remove this. This will be obsolete.
	 		mCategories = new HashMap<String, Category>();
	 		mCategories.put("Top News", new Category("Top News", "h")); //topic=h
	 		mCategories.put("World", new Category("World", "w")); //topic=w
	 		mCategories.put("Technology", new Category("Technology", "tc")); //topic=tc
	 		mCategories.put("Entertainment", new Category("Entertainment", "e")); //topic=e
	 		mCategories.put("Politics", new Category("Politics", "p")); //topic=p
	 		mCategories.put("Business", new Category("Business", "b")); //topic=b
	 		mCategories.put("Health", new Category("Health", "m")); //topic=m
	 		mCategories.put("Science", new Category("Science", "snc")); //topic=snc
	 		mCategories.put("Sports", new Category("Sports", "s")); //topic=s
 		}
 		AppSettings.CurrentCategory = mDb.getSetting("CurrentCategory");
 	} //prepareCategoriesMap()
 	
 	private void loadPersonalCategories()
 	{
 		mAdapterCategoryPersonal = new ArrayAdapter<Category>(this, R.layout.categories_item);
 		for (String key : mCategories.keySet())
		{
			Category category = mCategories.get(key);
			if (!category.mPredefinedCategory)
				mAdapterCategoryPersonal.add(category);
		}
 		mAdapterCategoryPersonal.setNotifyOnChange(true);
		lvPersonalCategories.setAdapter(mAdapterCategoryPersonal);
		lvPersonalCategories.setOnItemClickListener(onPersonalCategoriesClick);
		
		lvPersonalCategories.setSelector(R.drawable.list_selector);
		lvPersonalCategories.setSelected(true);
		if (mAdapterCategoryPersonal.getCount()>0)
			lvPersonalCategories.setVisibility(View.VISIBLE);
		else
			lvPersonalCategories.setVisibility(View.GONE);
		showHidePersonalCategoriesHint();
 	}
 	
 	private void showHidePersonalCategoriesHint()
 	{
 		if (lvPersonalCategories!=null && 
 				tvPersonalCategoriesHint!=null)
 		{
 			if (lvPersonalCategories.getCount()>0)
 				tvPersonalCategoriesHint.setVisibility(View.VISIBLE);
 			else
 				tvPersonalCategoriesHint.setVisibility(View.GONE);
 		}
 	}
 	
 	private void loadCategoriesList()
    {
    	if (lvPreconfiguredCategories != null && mCategories!=null)
    	{
    		//mAdapterCategory = new ArrayAdapter<Category>(this, android.R.layout.simple_list_item_checked);
    		mAdapterCategory = new ArrayAdapter<Category>(this, R.layout.categories_item);
    		for (String key : mCategories.keySet())
    		{
    			Category category = mCategories.get(key);
    			if (category.mPredefinedCategory)
    				mAdapterCategory.add(category);
    		}
    		mAdapterCategory.setNotifyOnChange(true);
    		lvPreconfiguredCategories.setAdapter(mAdapterCategory);
    		lvPreconfiguredCategories.setOnItemClickListener(onCategoriesClick);
    		lvPreconfiguredCategories.setSelector(R.drawable.list_selector);
    		lvPreconfiguredCategories.setSelected(true);
    		lvPreconfiguredCategories.setSelection(0);
    		layoutApp(menuOut);
    	}
    }
 	
 	AdapterView.OnItemClickListener onCategoriesClick = new AdapterView.OnItemClickListener()
	{
		public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) 
		{
			Category category = (Category)lvPreconfiguredCategories.getItemAtPosition(position);
			updateCurrentCategory(category.mKey);
			MainActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					lvPreconfiguredCategories.setItemChecked(position, true);
					
					// TODO: Not sure why I put notifyDataSetChanged here, it is causing the news screen  
					//	to come back to full mode automatically, before animateToggleCategoriesMenu.
					// Now it seems to OK.
					
					//if (mAdapterCategory!=null)
						//mAdapterCategory.notifyDataSetChanged();
    				animateToggleCategoriesMenu();
				} //OnItemClickListener::onItemSelected::run()
			});
		}
	};
	
	AdapterView.OnItemClickListener onPersonalCategoriesClick = new AdapterView.OnItemClickListener()
	{
		public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) 
		{
			Category category = (Category)lvPersonalCategories.getItemAtPosition(position);
			updateCurrentCategory(category.mKey);
			
			MainActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					lvPersonalCategories.setItemChecked(position, true);
					
					// TODO: Not sure why I put notifyDataSetChanged here, it is causing the news screen  
					//	to come back to full mode automatically, before animateToggleCategoriesMenu.
					// Now it seems to OK.
					
					//if (mAdapterCategory!=null)
						//mAdapterCategory.notifyDataSetChanged();
    				animateToggleCategoriesMenu();
				} //OnItemClickListener::onItemSelected::run()
			});
		}
	};
	
    private OnClickListener mBtnClickListener = new OnClickListener() {
        public void onClick(View v) {
        	int id = v.getId();
        	switch (id)
        	{
        	case R.id.rlMainTitle: //m_btnHome
        	case R.id.ibMenuImg:
        		animateToggleCategoriesMenu();
        		break;
        		
        	case R.id.tvCountrySpinnerLbl:
        		if (tvCountrySpinnerLbl != null)
        		{
        			//showNotification("YAY, got it John!");
        			spnCountryFeed.performClick();
        		}
        		break;
        		
        	case R.id.ibPersonalCategoriesAdd:
	        	{
	        		if (mCategories!=null)
	        			doAddPersonalCategory();
	        	}
        		break;
        		
        	case R.id.ibRefreshNews:
        		mNewsItemsAdapter.clear();
        		mNewsItemsAdapter.notifyDataSetChanged();
        		loadNews();
        		break;
        	}
        } //onClick
    }; //mBtnClickListener
    
    private void doAddPersonalCategory()
    {
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Add Personal Category");
		alert.setMessage("Category Text:");
		
		// Set an EditText view to get user input   
		 final EditText input = new EditText(this); 
		 alert.setView(input);
		 alert.setPositiveButton("Add", new DialogInterface.OnClickListener() 
		 {
			 public void onClick(DialogInterface dialog, int whichButton) 
			 {
				 MainActivity.this.runOnUiThread(new Runnable() 
				 {
					 public void run() 
					 {
						 String categoryText = input.getText().toString();
						 if (categoryText==null || categoryText.isEmpty())
							 return;
						 String categoryUrlItem = "";
						 try
						 {
							 categoryUrlItem = java.net.URLEncoder.encode(categoryText, "UTF-8"); 
						 }
						 catch (Exception e) 
						 {
							 ;
						 }
						 
						 Category category = new Category(categoryText, categoryUrlItem, false);
						 mDb.addCategory(category);
						 mAdapterCategoryPersonal.add(category);
						 mAdapterCategoryPersonal.notifyDataSetChanged();
						 lvPersonalCategories.setVisibility(View.VISIBLE);
						 if (lvPersonalCategories.getMeasuredHeight()>260)
						 {
							 lvPersonalCategories.getLayoutParams().height = 260;
						 }
						 else
						 {
							 lvPersonalCategories.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
						 }
						 showHidePersonalCategoriesHint();
						 
						 // Attempting to load the newly added item.
						 updateCurrentCategory(category.mKey);
						 menuOut = false;
						 layoutApp(menuOut);
					 } //OnItemClickListener::onItemSelected::run()
				 });
				 return;
			 }  
		 });
		 
		 alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
		 {
			 public void onClick(DialogInterface dialog, int which) 
			 {
				 // TODO Auto-generated method stub
				 return;   
			 }
		 });
		 alert.show();
    }
    
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
    
    public OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) 
        {
        	if (menuOut)
				animateToggleCategoriesMenu();
        	else
        	{
        		return true;
        	}
        	return true;
            //return gestureScanner.onTouchEvent(event);
        }
    };
    
    
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
    
    public void loadNews()
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
    	
    	mNewsItemsAdapter.clear();
    	mAdapterCategory.notifyDataSetChanged();
    	if (tvNewsItemsLoading!=null)
		{
			tvNewsItemsLoading.setText(R.string.loading);
			tvNewsItemsLoading.setVisibility(View.VISIBLE);
		}
    	
    	mTitleText = AppSettings.CurrentCategory + " (" + AppSettings.CurrentCountry + ")";
    	
		if (tvTitle!=null)
        	tvTitle.setText(mTitleText);
    	
    	if (mAdapterCountry!=null)
    	{
    		int pos = mAdapterCountry.getPosition(mCountries.get(AppSettings.CurrentCountry));
    		if (pos>0)
    			spnCountryFeed.setSelection(pos);
    	}
    	
    	if (mAdapterCategory!=null)
    	{
    		int pos = mAdapterCategory.getPosition(mCategories.get(AppSettings.CurrentCategory));
    		if (pos>0)
    			lvPreconfiguredCategories.setSelection(pos);
    	}
    	
    	String url = "";
    	Category category = mCategories.get(AppSettings.CurrentCategory);
    	Category country = mCountries.get(AppSettings.CurrentCountry);
    	if (category.mPredefinedCategory)
    		url = "http://news.google.com/news?ned=" + country.mUrlItem + "&topic=" + category.mUrlItem + "&output=rss&num=25";
    	else
    		url = "http://news.google.com/news?ned=" + country.mUrlItem + "&q=" + category.mUrlItem + "&output=rss&num=25";
    	if (mNewsLoader.loadNewsCategory(url))
    	{
    		if (ibRefreshNews!=null)
    			ibRefreshNews.setEnabled(false);
    		mCategoryLoaded = new Category(category.mKey, category.mUrlItem);
    		mCountryLoaded = new Category(country.mKey, country.mUrlItem);
    	}
    }
    
    
    
    private void loadCountryFeedList()
    {
    	prepareCountriesMap();
    	if (spnCountryFeed != null)
    	{
    		mAdapterCountry = new ArrayAdapter<Category>(this, R.layout.countries_spinner_item);
    		for (String key : mCountries.keySet())
    		{
    			mAdapterCountry.add(mCountries.get(key));
    		}
    		spnCountryFeed.setAdapter(mAdapterCountry);
    		
    		Category cat = mCountries.get(AppSettings.CurrentCountry);
    		int pos = mAdapterCountry.getPosition(cat);
    		if (pos>=0)
    			spnCountryFeed.setSelection(pos);
    		
    		spnCountryFeed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() 
    		{
    	    	// onItemSelected
    	    	public void onItemSelected(final AdapterView<?> parent, View view, final int pos, long id) 
    	    	{
    				MainActivity.this.runOnUiThread(new Runnable() 
    				{
    					public void run() 
    					{
    						Category country = (Category)parent.getItemAtPosition(pos);
    						updateCurrentCountry(country.mKey);
    						tvCountrySpinnerLbl.setText(AppSettings.CurrentCountry + " edition");
    						//mAdapterCountry.notifyDataSetChanged();
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
				mTitleText = AppSettings.CurrentCategory + " (" + AppSettings.CurrentCountry + ")";
				if (mNewsItemsAdapter.getCount()>0)
					tvNewsItemsLoading.setVisibility(View.INVISIBLE);
				else
					showNoNews();
			}
		});
	}

	@Override
	public void loadNewsCategoryFailed() {
		MainActivity.this.runOnUiThread(new Runnable() {
			public void run() {
				if (tvNewsItemsLoading!=null)
					tvNewsItemsLoading.setVisibility(View.GONE);
				if (ibRefreshNews!=null)
					ibRefreshNews.setEnabled(true);
			}
		});
	}
	
	@Override
	public void thumbLoaded(final String itemTitle, final Bitmap thumb)
	{
		// TODO: The update in this function seems to be unnecessary!
		MainActivity.this.runOnUiThread(new Runnable() {
			public void run() {
				if (mNewsItemsList!=null && mNewsItemsAdapter!=null)
				{
					int count = mNewsItemsAdapter.getCount();
					for (int i=0; i<count; i++)
					{
						NewsItem item = (NewsItem)mNewsItemsAdapter.getItem(i);
						if (item.mTitle==itemTitle)
						{
							if (thumb!=null)
							{
								item.mThumbBitmap = thumb;
							}
							else
							{
								BitmapDrawable d = (BitmapDrawable)getResources().getDrawable(R.drawable.ic_noimage);
								item.mThumbBitmap = ((BitmapDrawable)d).getBitmap();
							}
							mNewsItemsAdapter.notifyDataSetChanged();
							break;
						}
					}
				}
			}
		});
	}
	
	@Override
	public void allThumbsLoaded()
	{
		MainActivity.this.runOnUiThread(new Runnable() {
			public void run() {
				int count = mNewsItemsList.getCount();
				if (count>0)
				{
					//mNewsItemsList.setSelection(count - 1);
					//mNewsItemsList.smoothScrollToPosition(0);
					if (ibRefreshNews!=null)
						ibRefreshNews.setEnabled(true);
				}
			}
		});
	}
	
	private void showNoNews()
	{
		if (tvNewsItemsLoading!=null)
		{
			tvNewsItemsLoading.setText(R.string.nonews);
			tvNewsItemsLoading.setVisibility(View.VISIBLE);
		}
	} //showNoNews()
	
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
			updateNewsList();
			menuOut = false;
			layoutApp(menuOut);
		}
	}
	
	// No loading, just setting the adapter
	private void updateNewsList()
	{
		if (mNewsItemsList!=null && mNewsItemsAdapter!=null)
		{
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
