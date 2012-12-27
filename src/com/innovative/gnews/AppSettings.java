package com.innovative.gnews;

import com.innovative.gnews.db.GnewsDatabase;

// TODO: Persist these settings
public class AppSettings {
	public static boolean JavascriptEnabled = false;
	public static String CurrentCategory = GnewsDatabase.DEFAULT_CATEGORY;
	public static String CurrentCountry = GnewsDatabase.DEFAULT_COUNTRY;
}
