package com.innovative.gnews;
import com.innovative.gnews.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Titlebar {
	private static final int CATEGORIES_MENU_WIDTH = 150;
	private static final int MENU_ACTIVITY_REQUEST_CODE = 2001;
	
	private static ImageButton m_ibMenuImg = null;
	private static TextView m_tvTitle = null;
	private static Context m_context = null;
	private static Activity m_activity = null;
	
	// newpage_title controls
	private static ImageButton ibNewsList = null;
	private static ImageView ivFavIcon = null;
	private static TextView tvPageUrl = null;
	
	private static RelativeLayout rlMenuCategories = null;
	private static RelativeLayout rlMainNewsList = null;
	
	public static void Reset()
	{
		m_ibMenuImg = null;
		m_tvTitle = null;
		m_context = null;
		m_activity = null;
		
		ibNewsList = null;
		ivFavIcon = null;
		tvPageUrl = null;
		
		rlMenuCategories = null;
		rlMainNewsList = null;
	}
	
	public static void InitTitlebar(Activity activity, String titleString)
	{
		// Resets the values of previous activity 
		Reset();
		m_activity = activity;
		m_context = m_activity.getApplicationContext();
		
		//m_ibMenuImg = (ImageButton) activity.findViewById(R.id.ibMenuImg);
        //if (m_ibMenuImg!=null)
        	//m_ibMenuImg.setOnClickListener(mBtnClickListener);
		
        m_tvTitle = (TextView) activity.findViewById(R.id.tvAppTitle);
        if (m_tvTitle!=null)
        	m_tvTitle.setText(titleString);
        
        
        rlMenuCategories = (RelativeLayout) m_activity.findViewById(R.id.rlMenuCategories);
		rlMainNewsList = (RelativeLayout) m_activity.findViewById(R.id.rlMainNewsList);
		
		// Initialize main news list screen and the categories menu sizes.
		//showCategoriesMenu(false);
	}
	
	public static void InitTitlebar(Activity activity, String urlString, Bitmap favIcon)
	{
		// Resets the values of previous activity 
		Reset();
		m_activity = activity;
		m_context = m_activity.getApplicationContext();
		
		ibNewsList = (ImageButton) activity.findViewById(R.id.ibNewsList);
		if (ibNewsList!=null)
			ibNewsList.setOnClickListener(mBtnClickListener);
		
		ivFavIcon = (ImageView) activity.findViewById(R.id.ibPageFavIcon);
		if (ivFavIcon!=null && favIcon!=null)
			ivFavIcon.setImageBitmap(favIcon);
		
		tvPageUrl = (TextView) activity.findViewById(R.id.tvPageUrl);
		if (tvPageUrl!=null)
			tvPageUrl.setText(urlString);
	}
	
	
	public static void setTitle(String titleString)
	{
		if (m_activity!=null)
		{
			m_tvTitle = (TextView) m_activity.findViewById(R.id.tvAppTitle);
	        if (m_tvTitle!=null)
	        	m_tvTitle.setText(titleString);
		}
	} //setTitle()
	
	public static void showCategoriesMenu(boolean bShow)
	{
		if (rlMenuCategories==null || rlMainNewsList==null)
			return;
		rlMenuCategories.getLayoutParams().width = CATEGORIES_MENU_WIDTH;
		if (bShow)
		{
			rlMenuCategories.setVisibility(View.VISIBLE);
			
			rlMainNewsList.layout(LayoutParams.MATCH_PARENT, 
					rlMainNewsList.getTop(), 
					rlMainNewsList.getRight(), 
					rlMainNewsList.getBottom());
			
			rlMainNewsList.layout(CATEGORIES_MENU_WIDTH, 
					rlMainNewsList.getTop(), 
					rlMainNewsList.getRight()+CATEGORIES_MENU_WIDTH, 
					rlMainNewsList.getBottom());
		}
		else
		{
			rlMenuCategories.setVisibility(View.INVISIBLE);
			
			rlMainNewsList.layout(LayoutParams.MATCH_PARENT, 
					rlMainNewsList.getTop(), 
					rlMainNewsList.getRight(), 
					rlMainNewsList.getBottom());
		}
	} //showCategoriesMenu()
	
	public static void toggleCategoriesMenu()
	{
		if (rlMenuCategories==null || rlMainNewsList==null)
			return;
		
		boolean bShow = (rlMenuCategories.getVisibility()!=View.VISIBLE);
		showCategoriesMenu(bShow);
	} //toggleCategoriesMenu()
		
	static private OnClickListener mBtnClickListener = new OnClickListener() {
        public void onClick(View v) {
        	int id = v.getId();
        	Context appContext = m_context;
        	switch (id)
        	{
        	case R.id.ibMenuImg: //m_btnHome
	        	{
	        		toggleCategoriesMenu();
	        		//Toast.makeText(appContext, "Home", Toast.LENGTH_SHORT).show();
	        	}
        		break;
        		
        	case R.id.ibNewsList:
        		m_activity.finish();
        		break;
        	}
        }
    }; //mBtnClickListener
}
