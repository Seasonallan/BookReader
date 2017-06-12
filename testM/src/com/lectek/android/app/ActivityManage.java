package com.lectek.android.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

import android.app.Activity;

public class ActivityManage {
	private static Stack<Activity> mActivityStack = new Stack<Activity>();
	
	public static void handlerResume(Activity activity){
		if(activity == null){
			return;
		}
		if(activity.isFinishing()){
			removeActivity(activity);
			return;
		}
		if(!mActivityStack.contains(activity)){
			mActivityStack.push(activity);
		}else{
			Activity topActivity = mActivityStack.peek();
			if(!activity.equals(topActivity)){
				mActivityStack.remove(activity);
				mActivityStack.push(activity);
			}
		}
	}
	
	public static Activity getTopActivity(){
		if(mActivityStack == null || mActivityStack.size() == 0){
			return null;
		}
		return mActivityStack.peek();
	}
	
	public static void removeActivity(Activity activity){
		if(mActivityStack.contains(activity)){
//			Log.e("removeActivity", "removeActivity="+activity.getClass().getName());
			mActivityStack.remove(activity);
		}
	}
	
	public static void removeAllActivity(Collection<Activity> collection){
		if(collection == null){
			return;
		}
		for(Activity activity : collection){
			if(!activity.isFinishing()){
				activity.finish();
			}
			removeActivity(activity);
		}
	}
	
	public static void finishActivity(Activity activity){
		if(activity == null){
			return;
		}
		if(!activity.isFinishing()){
			activity.finish();
		}
		removeActivity(activity);
	}
	
	public static Activity findActivity(Class<? extends Activity> activityClass){
		Activity activity = null;
		for(Activity tempActivity : mActivityStack){
			if(tempActivity.getClass().getName().equals(activityClass.getName())){
				return tempActivity;
			}
		}
		return activity;
	}
	
	public static void finishActivity(Class<? extends Activity> activityClass){
		if(activityClass == null){
			return;
		}
		Activity activity = findActivity(activityClass);
		if(activity != null && !activity.isFinishing()){
			activity.finish();
		}
	}
	
	public static void finishBottomActivitys(Class<? extends Activity> activityClass){
		ArrayList<Activity> activitys = getBottomActivitys(activityClass);
		for(Activity tempActivity : activitys){
			if(!tempActivity.isFinishing()){
				tempActivity.finish();
			}
		}
		removeAllActivity(activitys);
	}
	
	public static ArrayList<Activity> getBottomActivitys(Class<? extends Activity> activityClass){
		ArrayList<Activity> activitys = new ArrayList<Activity>();
		for(Activity tempActivity : mActivityStack){
			if(tempActivity.getClass().getName().equals(activityClass.getName())){
				break;
			}
			activitys.add(tempActivity);
		}
		return activitys;
	}
	
	public static void finishAll(){
		finishAll(null);
	}
	
	public static void finishAll(Class<? extends Activity> filterActivity){
		for(Activity tempActivity : mActivityStack){
			if(filterActivity == null || !tempActivity.getClass().getName().equals(filterActivity.getName())){
				if(!tempActivity.isFinishing()){
					tempActivity.finish();
				}
			}
		}
		mActivityStack.clear();
	}
}
