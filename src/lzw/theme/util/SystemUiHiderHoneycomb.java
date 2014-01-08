package lzw.theme.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SystemUiHiderHoneycomb extends 
 SystemUiHiderBase{
	private int mShowFlags;
	private int mHideFlags;
	private int mTestFlags;
	private boolean mVisible=true;
	
	protected SystemUiHiderHoneycomb(Activity activity,
	 View anchorView,int flags){
		super(activity,anchorView,flags);
		mShowFlags=View.SYSTEM_UI_FLAG_VISIBLE;
		mHideFlags=View.SYSTEM_UI_FLAG_LOW_PROFILE;
		mTestFlags=View.SYSTEM_UI_FLAG_LOW_PROFILE;
		if((mFlags&FLAG_FULLSCREEN)!=0){
			mShowFlags|=View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
			mHideFlags|=View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
			  View.SYSTEM_UI_FLAG_FULLSCREEN;
		}
		if((mFlags & FLAG_HIDE_NAVIGATION)!=0){
			mShowFlags|=
			  View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
			mHideFlags|=
				View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
	  }
	}
	
	@Override
	public void setup(){
		mAnchorView.setOnSystemUiVisibilityChangeListener(
		  mSystemUiVisibilityChangeListener);
	}
	
	public void hide(){
		mAnchorView.setSystemUiVisibility(mHideFlags);
	}
	
	public boolean isVisible(){
		return mVisible;
	}
	
	private View.OnSystemUiVisibilityChangeListener
	 mSystemUiVisibilityChangeListener=new 
	 View.OnSystemUiVisibilityChangeListener(){
		@Override
		public void onSystemUiVisibilityChange(int vis){
			if((vis &mTestFlags)!=0){
				if(Build.VERSION.SDK_INT<
					 Build.VERSION_CODES.JELLY_BEAN){
					mActivity.getActionBar().hide();
					mActivity.getWindow().setFlags(
					  WindowManager.LayoutParams.FLAG_FULLSCREEN,
					  WindowManager.LayoutParams.FLAG_FULLSCREEN);
				}
				mOnVisibilityChangeListener.onVisibilityChange(false);
				mVisible=false;
			}else{
				mAnchorView.setSystemUiVisibility(mShowFlags);
				if(Build.VERSION.SDK_INT<
					 Build.VERSION_CODES.JELLY_BEAN){
					mActivity.getActionBar().show();
					mActivity.getWindow().setFlags(0, WindowManager
						.LayoutParams.FLAG_FULLSCREEN);
				}
				mOnVisibilityChangeListener
				  .onVisibilityChange(true);
				mVisible=true;
			} 
		}
	};
}