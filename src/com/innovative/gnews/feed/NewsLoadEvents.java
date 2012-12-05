package com.innovative.gnews.feed;

import android.graphics.Bitmap;

public interface NewsLoadEvents {
	void loadNewsCategorySuccess(NewsCategory newsCategory);
	void loadNewsCategoryFailed();
	void thumbLoaded(String itemTitle, Bitmap thumb);
}
