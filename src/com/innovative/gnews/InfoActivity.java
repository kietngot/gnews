package com.innovative.gnews;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class InfoActivity extends Activity {
	// Controls
	
	@Override
    public void onCreate(Bundle savedInstanceState) {		
    	super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_info);
    } //onCreate()
} //class NewsPageActivity
