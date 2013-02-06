package com.innovative.gnews;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnTouchListener;

// We are not using this class, kept it in the source tree 
//	just as a reference if we need in the future.
public class GestureListenerEx extends SimpleOnGestureListener implements OnTouchListener 
{
	public interface GestureListenerExEvents
	{
		void onSingleTap(MotionEvent e);
		void onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY);
		public void onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);
	} //interface GestureListenerExEvents
	
	private GestureListenerExEvents mEvents = null;
	Context context;
    GestureDetector gDetector;

    public GestureListenerEx()
    {
        super();
    }

    public GestureListenerEx(Context context) {
        this(context, null);
    }

    public GestureListenerEx(Context context, GestureDetector gDetector) {

        if(gDetector == null)
            gDetector = new GestureDetector(context, this);
        this.context = context;
        this.gDetector = gDetector;
    }
    
    public void setEvents(GestureListenerExEvents events)
    {
    	mEvents = events;
    } //setEvents()

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
    {
    	if (mEvents!=null)
    		mEvents.onFling(e1, e2, velocityX, velocityY);
    	super.onFling(e1, e2, velocityX, velocityY);
    	return true;
        //return super.onFling(e1, e2, velocityX, velocityY);
    }
    
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
    		float distanceY)
    {
    	if (mEvents!=null)
    		mEvents.onScroll(e1, e2, distanceX, distanceY);
    	super.onScroll(e1, e2, distanceX, distanceY);
    	return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
    	super.onDown(e);
    	return true;
    }
    
    @Override
    public boolean onDoubleTap(MotionEvent e) {
    	super.onDoubleTap(e);
    	return true;
    }
    
    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
    	super.onDoubleTapEvent(e);
    	return true;
    }
    
    
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
    	super.onSingleTapUp(e);
    	return true;
    }
    
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e)
    {
    	if (mEvents!=null)
    		mEvents.onSingleTap(e);
    	super.onSingleTapConfirmed(e);
    	return true;
        //return super.onSingleTapConfirmed(e);
    }

    public boolean onTouch(View v, MotionEvent event) 
    {
        // Within the GestureListenerEx class you can now manage the event.getAction() codes.
    	
        // Note that we are now calling the gesture Detectors onTouchEvent. And given we've set this class as the GestureDetectors listener 
        // the onFling, onSingleTap etc methods will be executed.
        gDetector.onTouchEvent(event);
        return true;
    }


    public GestureDetector getDetector()
    {
        return gDetector;
    }
} //class GestureListenerEx
