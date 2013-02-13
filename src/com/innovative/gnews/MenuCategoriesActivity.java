package com.innovative.gnews;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class MenuCategoriesActivity extends Activity {
	String mTitleText = "Categories";
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_menu_categories);
	} //onCreate()
}
