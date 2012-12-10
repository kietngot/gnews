package com.innovative.gnews;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class MenuCategoriesActivity extends Activity {
	String mTitleText = "Categories";
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    final boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	    setContentView(R.layout.activity_menu_categories);
	    
	    // We can access custom title elements only after setting FEATURE_CUSTOM_TITLE
		if ( customTitleSupported ) {
			//getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.main_title);
			//Titlebar.InitTitlebar(this, mTitleText);
		}
	} //onCreate()
}
