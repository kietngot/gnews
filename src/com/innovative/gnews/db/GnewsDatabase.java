package com.innovative.gnews.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
	
	private static final String DBNAME = "gnewsdb";
	private Activity mActivity = null;
	SQLiteDatabase mSqliteDb = null;
	private static final String CATEGORIES_TABLE_NAME = "Categories";
	private static final String CATEGORIES_CREATE = "create table Categories (ID integer primary key autoincrement, Name text not null, Link text not null, DefaultCategory boolean not null);";
	
	
	private HashMap<String, Category> mCategories = null;
	
	public GnewsDatabase(Activity activity)
	{
		mActivity = activity;
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
			
			// Make sure tables exist, if not create them.
			if (!GnewsDatabase.TableExists(mSqliteDb, CATEGORIES_TABLE_NAME))
			{
				mSqliteDb.execSQL(CATEGORIES_CREATE);
				addDefaultCategories();
			}
			
			// Load data form the database
			loadCategories();
			
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
	} //addDefaultCategfories()
	
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
		}
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
	}
	
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
