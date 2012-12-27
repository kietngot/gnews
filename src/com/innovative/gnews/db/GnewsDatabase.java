package com.innovative.gnews.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;

import com.innovative.gnews.MainActivity;

import android.app.Activity;
import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;
/*
// TODO: 
	1. Make this class singleton (and use it in both NewsPageActivity and MainActivity)
	2. Add countries table and initialize
	3. Make the JavascriptEnabled value boolean on the interface level
*/
public class GnewsDatabase 
{
	public static class Category
	{
		private int mId = 0;
		public String mKey;
		public String mUrlItem;
		public boolean mPredefinedCategory = true; // Make this false for personal categories
		public Category(String key, String urlItem)
		{
			mId = 0;
			mKey = key;
			mUrlItem = urlItem;
			mPredefinedCategory = true;
		}
		
		public Category(String key, String urlItem, boolean predefined)
		{
			mId = 0;
			mKey = key;
			mUrlItem = urlItem;
			mPredefinedCategory = predefined;
		}
		
		public Category(int id, String key, String urlItem, boolean predefined)
		{
			mId = id;
			mKey = key;
			mUrlItem = urlItem;
			mPredefinedCategory = predefined;
		}
		
		public int getId()
		{
			return mId;
		}
		
		public String toString()
		{
			return mKey;
		}
	}
	
	public static final String DEFAULT_CATEGORY = "Top News";
	public static final String DEFAULT_COUNTRY = "USA";
	public static final String DEFAULT_JAVASCRIPT_VALUE = "0";
	
	private static final String DBNAME = "gnewsdb";
	private Activity mActivity = null;
	SQLiteDatabase mSqliteDb = null;
	private static final String CATEGORIES_TABLE_NAME = "Categories";
	private static final String CATEGORIES_CREATE = "create table Categories (ID integer primary key autoincrement, Name text not null, Link text not null, DefaultCategory boolean not null);";
	
	private static final String SETTINGS_TABLE_NAME = "Settings";
	private static final String SETTINGS_CREATE = "create table Settings (ID integer primary key autoincrement, Name text not null, Value text not null);";
	
	private HashMap<String, Category> mCategories = null;
	private HashMap<String, String> mSettings = null; //key-value pair
	
	private static class DatabaseSingletonHolder 
	{
		public static final GnewsDatabase INSTANCE = new GnewsDatabase();
	} //class DatabaseSingletonHolder

	// This should be called the first time.
	public static GnewsDatabase getDatabase(Activity activity) 
	{
		DatabaseSingletonHolder.INSTANCE.mActivity = activity;
		return DatabaseSingletonHolder.INSTANCE;
	} //getDatabase() 
	
	public static GnewsDatabase getDatabase() 
	{
		if (DatabaseSingletonHolder.INSTANCE.mActivity!=null)
			return DatabaseSingletonHolder.INSTANCE;
		else
			return null;
	} //getDatabase() 
	
	private GnewsDatabase()
	{
		
	} // GnewsDatabase()
	
	public boolean open()
	{
		boolean bRet = false;
		try
		{
			if (mActivity==null)
				return false;
			
			mSqliteDb = mActivity.openOrCreateDatabase(DBNAME, Context.MODE_PRIVATE, null);
			if (!isDbOpen())
				return false;
			
			// Make sure Categories table exists, if not create.
			if (!GnewsDatabase.TableExists(mSqliteDb, CATEGORIES_TABLE_NAME))
			{
				mSqliteDb.execSQL(CATEGORIES_CREATE);
				addDefaultCategories();
			}
			
			// Make sure Settings table exists, if not create.
			if (!GnewsDatabase.TableExists(mSqliteDb, SETTINGS_TABLE_NAME))
			{
				mSqliteDb.execSQL(SETTINGS_CREATE);
				addDefaultSettings();
			}
			
			// Load data form the database
			loadCategories();
			loadSettings();
			
			bRet = true;
		}
		catch (Exception e)
		{
			bRet = false;
		}
		return bRet;
	} //open()
	
	public void close()
	{
		if (isDbOpen())
			mSqliteDb.close();
		mSqliteDb = null;
	}
	
	public boolean isDbOpen()
	{
		return (mSqliteDb!=null && mSqliteDb.isOpen());
	}
	
	public static boolean isDbOpen(SQLiteDatabase db)
	{
		return (db!=null && db.isOpen());
	}
	
	// TableExists: Returns true if a table exists in the given SQLiteDatabase.
	// NOTE: The SQLiteDatabase should be OPENED! 
	private static boolean TableExists(SQLiteDatabase db, String tableName)
	{
		boolean bRet = false;
		try
		{
			if (!isDbOpen(db))
				return false;
			Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
			
			if (cursor !=null)
			{
				if (cursor.getCount()>0)
					bRet = true;
				cursor.close();
			}
		}
		catch (Exception e)
		{
			bRet = false;
		}
		return bRet;
	}
	
	// addDefaultCategfories: Adds default categories to the table.
	//	like, "Top News", "Technology", "Politics", etc.,
	public boolean addDefaultCategories()
	{
		boolean bRet = false;
		try
		{
			if (!isDbOpen())
				return false;
			addCategory(new Category("Top News", "h", true));
			addCategory(new Category("World", "w", true));
			addCategory(new Category("Technology", "tc", true));
			addCategory(new Category("Entertainment", "e", true));
			addCategory(new Category("Politics", "p", true));
			addCategory(new Category("Business", "b", true));
			addCategory(new Category("Health", "m", true));
			addCategory(new Category("Science", "snc", true));
			addCategory(new Category("Sports", "s", true));
			bRet = true;
		}
		catch (Exception e)
		{
			bRet = false;
		}
		return bRet;
	} //addDefaultCategories()
	
	public boolean addCategory(Category category)
	{
		boolean bRet = false;
		try
		{
			if (!isDbOpen())
				return false;
			
			int predefined = category.mPredefinedCategory==true?1:0;
			String sql =   "INSERT INTO " + CATEGORIES_TABLE_NAME + 
					" (Name, Link, DefaultCategory) " +
					"VALUES ('" + category.mKey + "', '" + 
					category.mUrlItem + "', '" + 
					String.valueOf(predefined) + "');";
			
			mSqliteDb.execSQL(sql);
			
			// Try and get the category from the database.
			String whereClause = "Name=?";
			String[] whereArgs = new String[] {
				category.mKey
			};
			
			Cursor cursor = mSqliteDb.query(CATEGORIES_TABLE_NAME, null, 
					whereClause, whereArgs, null, null, null);
			
			if (cursor!=null && cursor.getCount()>0 && cursor.moveToFirst())
			{
				int id = cursor.getInt(0);
				String name = cursor.getString(1);
				String link = cursor.getString(2);
				boolean isDefault = (cursor.getInt(3)>0);
				mCategories.put(name, new Category(id, name, link, isDefault));
			}
			else
			{
				mCategories.put(category.mKey, new Category(category.mKey, category.mUrlItem, category.mPredefinedCategory));
			}
			
			bRet = true;
		}
		catch (Exception e)
		{
			bRet = false;
		}
		return bRet;
	} //addCategory()
	
	// TODO
	public boolean deleteCategory(String name)
	{
		boolean bRet = false;
		try
		{
			if (!isDbOpen())
				return false;
			
			String whereClause = "Name=?";
			String[] whereArgs = new String[] {
				name
			};
			if (mSqliteDb.delete(CATEGORIES_TABLE_NAME, whereClause, whereArgs)>0)
			{
				mCategories.remove(name);
				bRet = true;
			}
		}
		catch (Exception e)
		{
			bRet = false;
		}
		return bRet;
	} //deleteCategory()
	
	
	
	private boolean loadCategories()
	{
		boolean bRet = false;
		try
		{
			if (!isDbOpen())
				return false;
			
			if (mCategories==null)
				mCategories = new HashMap<String, Category>();
			mCategories.clear();
			
			Cursor cursor = mSqliteDb.query(CATEGORIES_TABLE_NAME, null, null, null, null, null, null);
			if (cursor!=null && cursor.moveToFirst())
			{
				do
				{
					int id = cursor.getInt(0);
					String name = cursor.getString(1);
					String link = cursor.getString(2);
					boolean isDefault = (cursor.getInt(3)>0);
					mCategories.put(name, new Category(id, name, link, isDefault));
				} while (cursor.moveToNext());
				
			}
			bRet = true;
		} //try
		catch (Exception e)
		{
			bRet = false;
		}
		return bRet;
	} //loadCategories()
	
	public HashMap<String, Category> getCategories(boolean bReload)
	{
		if (bReload)
			loadCategories();
		return mCategories;
	} //getCategories()
	
	
	public boolean addDefaultSettings()
	{
		boolean bRet = false;
		try
		{
			setSetting(new SimpleEntry("CurrentCategory", DEFAULT_CATEGORY));
			setSetting(new SimpleEntry("CurrentCountry", DEFAULT_COUNTRY));
			setSetting(new SimpleEntry("JavaScriptEnabled", DEFAULT_JAVASCRIPT_VALUE));
			bRet = true;
		}
		catch (Exception e)
		{
			bRet = false;
		}
		return bRet;
	} //addDefaultSettings()
	
	public boolean setSetting(String key, String value)
	{
		SimpleEntry<String, String> setting = new SimpleEntry<String, String>(key, value);
		return setSetting(setting);
	} //addSetting()
	
	public boolean setSetting(SimpleEntry<String, String> setting)
	{
		boolean bRet = false;
		try
		{
			if (setting==null || !isDbOpen())
				return false;
			
			// Try and get the setting from the database (if exists).
			String whereClause = "Name=?";
			String[] whereArgs = new String[] {
					setting.getValue()
			};
			Cursor cursor = mSqliteDb.query(SETTINGS_TABLE_NAME, null, 
					whereClause, whereArgs, null, null, null);
			
			if (cursor!=null && cursor.getCount()>0 && cursor.moveToFirst())
			{
				ContentValues values = new ContentValues();
				values.put(setting.getKey(), setting.getValue());
				mSqliteDb.update(SETTINGS_TABLE_NAME, values, whereClause, whereArgs);
			}
			else
			{
				String sql =   "INSERT INTO " + SETTINGS_TABLE_NAME + 
						" (Name, Value) " +
						"VALUES ('" + setting.getKey() + "', '" + 
						setting.getValue() + "');";
				mSqliteDb.execSQL(sql);
			}
			
			bRet = true;
		}
		catch (Exception e)
		{
			bRet = false;
		}
		return bRet;
	} //addSetting()
	
	public boolean loadSettings()
	{
		boolean bRet = false;
		try
		{
			if (!isDbOpen())
				return false;
			
			if (mSettings==null)
				mSettings = new HashMap<String, String>();
			mSettings.clear();
			
			Cursor cursor = mSqliteDb.query(SETTINGS_TABLE_NAME, null, null, null, null, null, null);
			if (cursor!=null && cursor.moveToFirst())
			{
				do
				{
					String name = cursor.getString(1);
					String value = cursor.getString(2);
					mSettings.put(name, value);
				} while (cursor.moveToNext());
				
			}
			bRet = true;
		} //try
		catch (Exception e)
		{
			bRet = false;
		}
		return bRet;
	} //loadSettings()
	
	public HashMap<String, String> getSettings(boolean bReload)
	{
		if (bReload)
			loadSettings();
		return mSettings;
	} //getSettings()
	
	public String getSetting(String key)
	{
		String val = (String) mSettings.get(key);
		return val;
	} //getSetting()
	
	
	// TODO
	public boolean functionTemplate()
	{
		boolean bRet = false;
		try
		{
			// TODO
			bRet = true;
		}
		catch (Exception e)
		{
			bRet = false;
		}
		return bRet;
	} //functionTemplate()
	
} //class GnewsDatabase
