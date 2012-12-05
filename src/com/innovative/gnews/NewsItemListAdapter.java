package com.innovative.gnews;

import java.util.List;

import com.innovative.gnews.feed.NewsItem;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsItemListAdapter extends ArrayAdapter<NewsItem>  
{
	public static final int KEY_LINK = 1001;
	private LayoutInflater mLayoutx = null;
	private int mResource = 0;
	public NewsItemListAdapter(Context context, int resource, int textViewResourceId, List<NewsItem> objects) 
	{
		super(context, resource, textViewResourceId, objects);
		mResource = resource;
		mLayoutx = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		NewsItem newsItem = getItem(position);
		
		if(null == convertView)
             convertView = mLayoutx.inflate(mResource, null);
		
		ImageView ivThumbImage = (ImageView) convertView.findViewById(R.id.ivNewsItemThumb);
		if (ivThumbImage!=null)
			ivThumbImage.setImageURI(Uri.parse(newsItem.mThumbImageLink));
		
		TextView tvNewsItemTitle = (TextView) convertView.findViewById(R.id.tvNewsItemTitle);
		if (tvNewsItemTitle!=null)
			tvNewsItemTitle.setText(newsItem.mTitle);
		
		TextView tvNewsItemDatePublished = (TextView) convertView.findViewById(R.id.tvNewsItemDatePublished);
		if (tvNewsItemTitle!=null)
			tvNewsItemTitle.setText(newsItem.mPubDate);
		
		convertView.setTag(KEY_LINK, newsItem.mLink);
		return convertView;
	}

}
