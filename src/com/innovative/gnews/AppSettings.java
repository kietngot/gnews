package com.innovative.gnews;

import java.util.ArrayList;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import com.innovative.gnews.db.GnewsDatabase;

// TODO: Persist these settings
public class AppSettings {
	public static boolean JavascriptEnabled = false;
	public static String CurrentCategory = GnewsDatabase.DEFAULT_CATEGORY;
	public static String CurrentCountry = GnewsDatabase.DEFAULT_COUNTRY;
	
	/*
	public static void updateCurrentCountry(Context context)
	{
		LocationManager locationManager =
	            (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	    final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

	    Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	    Geocoder code = new Geocoder(context);
	    try 
	    {
	        ArrayList<Address> adr = (ArrayList<Address>)code.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
	        CurrentCountry = adr.get(0).getCountryName();
	    }
	    catch (Exception e)
	    {
	    	e.printStackTrace();
	    }
	} //updateCurrentCountry
	*/
}
