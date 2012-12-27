package com.innovative.gnews;

import com.innovative.gnews.db.GnewsDatabase.Category;


// TODO: Persist these settings
public class AppSettings {
	public static final Category DefaultCategory = new Category("Technology", "tc");
	public static final Category DefaultCountry = new Category("USA", "us");
	public static boolean JavascriptEnabled = false;
	public static Category CurrentCategory = new Category(DefaultCategory.mKey, DefaultCategory.mUrlItem);
	public static Category CurrentCountry = new Category(DefaultCountry.mKey, DefaultCountry.mUrlItem);
}
