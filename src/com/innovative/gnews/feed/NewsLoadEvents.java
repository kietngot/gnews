package com.innovative.gnews.feed;

import com.innovative.gnews.db.GnewsDatabase.Category;

import android.graphics.Bitmap;

public interface NewsLoadEvents {
	void loadNewsCategorySuccess(NewsCategory newsCategory, Category currentCountry, Category currentCategory);
	void loadNewsCategoryFailed(Category currentCountry, Category currentCategory);
	void thumbLoaded(String itemTitle, Bitmap thumb, Category currentCountry, Category currentCategory);
	void allThumbsLoaded(Category currentCountry, Category currentCategory);
}
