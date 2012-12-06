package com.innovative.gnews.utils;

import com.innovative.gnews.R;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Utils 
{
	public static boolean isNetworkConnected(Context ctx)
	{
		boolean bRet = false;
		try
		{
			ConnectivityManager conMgr =  (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
			WifiManager wifiMgr =  (WifiManager)ctx.getSystemService(Context.WIFI_SERVICE);
			// if we cannot determine network state, let us assume it is connected and load
			if (conMgr==null || wifiMgr==null)
				return true; 
			
			State netState = conMgr.getNetworkInfo(0).getState();
			int wifiState = wifiMgr.getWifiState();
			
			if (netState==State.CONNECTED || 
				netState==State.CONNECTING || 
				wifiState==WifiManager.WIFI_STATE_ENABLED ||
				wifiState==WifiManager.WIFI_STATE_UNKNOWN)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception ex)
		{
			bRet = true;
		}
		return bRet;
	}
	
	public static boolean checkInternetConnection(Context ctx, boolean bShowMessage, TextView tvMsg)
	{
		if (!isNetworkConnected(ctx))
    	{
			if (!bShowMessage)
				return false;
    		if (tvMsg!=null)
    		{
    			tvMsg.setText(R.string.nonetwork);
    			tvMsg.setVisibility(View.VISIBLE);
    		}
    		else
    		{
				Toast.makeText(ctx, R.string.nonetwork, Toast.LENGTH_SHORT).show();
    		}
    		return false;
    	}
		return true;
	} //checkInternetConnection()
}
