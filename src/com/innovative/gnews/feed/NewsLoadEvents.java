package com.innovative.gnews.feed;

public interface NewsLoadEvents {
	void loadNewsCategorySuccess(NewsCategory newsCategory);
	void loadNewsCategoryFailed();
}
