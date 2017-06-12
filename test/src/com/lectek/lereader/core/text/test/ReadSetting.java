package com.lectek.lereader.core.text.test;

import java.lang.ref.WeakReference;
import java.util.LinkedList;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;

import com.lectek.bookformats.R;


/**
 * 阅读界面设置管理
 * @author lyw
 */
public class ReadSetting{
	/** 字体大小设置*/
	public static final String SETTING_TYPE_FONT_SIZE = "SETTING_TYPE_FONT_SIZE";
	/** 行间距设置*/
	public static final String SETTING_TYPE_FONT_LINE_SPACE_TYPE = "SETTING_TYPE_FONT_LINE_SPACE_TYPE";
	/** 主题设置*/
	public static final String SETTING_TYPE_THEME = "SETTING_TYPE_THEME";
	/** 亮度设置*/
	public static final String SETTING_TYPE_BRIGHTESS_LEVEL = "SETTING_TYPE_BRIGHTESS_LEVEL";
	/*		定义主题类型		*/
	public static final int THEME_TYPE_DAY = 0;
	public static final int THEME_TYPE_NIGHT = 1;
	public static final int THEME_TYPE_OTHERS_1 = 2;
	public static final int THEME_TYPE_OTHERS_2 = 3;
	public static final int THEME_TYPE_OTHERS_3 = 4;
	public static final int THEME_TYPE_OTHERS_4 = 5;
	/*		定义行间距类型		*/
	public static final float FONT_LINE_SPACE_TYPE_1 = 0f;
	public static final float FONT_LINE_SPACE_TYPE_2 = 2f;
	public static final float FONT_LINE_SPACE_TYPE_3 = 4f;
	/*		定义字体大小类型		*/
	public static final int FONT_SIZE_MIN = 15;
	public static final int FONT_SIZE_MAX = 25;
	public static final int FONT_SIZE_NUM = FONT_SIZE_MAX - FONT_SIZE_MIN;
	public static final int FONT_SIZE_DEFALUT = FONT_SIZE_MIN + (FONT_SIZE_NUM >> 1) - 2;

	private static final String PREFS_MODULE_INFO = "read_setting_prefs";
	private static ReadSetting this_;
	private Context mContext;
	private LinkedList<WeakReference<SettingListener>> mSettingListenerList;
	private Handler mHandler;
	private SharedPreferences mSharedPreferences;
	private int mCurrentFontLevel;
	private float mCurrentLineSpaceType;
	private int mCurrentThemeType;
	private int mCurrentBrightessLevel;
	
	public static ReadSetting getInstance(Context context){
		if(this_ == null){
			this_ = new ReadSetting(context);
		}
		return this_;
	}
	
	private ReadSetting(Context context){
		mContext = context.getApplicationContext();
		mSharedPreferences = mContext.getSharedPreferences(PREFS_MODULE_INFO, Context.MODE_PRIVATE);
		mCurrentFontLevel = loadSaveFontLevel();
		mCurrentLineSpaceType = loadSaveLineSpaceType();
		mCurrentThemeType = loadSaveThemeType();
		mCurrentBrightessLevel = loadSaveBrightessLevel();
		mSettingListenerList = new LinkedList<WeakReference<SettingListener>>();
		mHandler = new Handler(Looper.getMainLooper());
	}
	
	public void clearSetting(){
		mSharedPreferences.edit().clear();
	}
	
	private boolean containsSettingListeners(SettingListener l){
		for (WeakReference<SettingListener> settingListener : mSettingListenerList) {
			if(settingListener.get() != null && settingListener.get().equals(l)){
				return true;
			}
		}
		return false;
	}
	
	public void addDataListeners(SettingListener listener){
		if(listener == null){
			return;
		}
		if(!containsSettingListeners(listener)){
			mSettingListenerList.add(new WeakReference<ReadSetting.SettingListener>(listener));
		}
	}
	
	private void notify(final String type){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				SettingListener listener = null;
				for (WeakReference<SettingListener> settingListener : mSettingListenerList) {
					listener = settingListener.get();
					if(listener != null){
						listener.onSettingChange(ReadSetting.this , type);
					}
				}
			}
		});
	}
	
	/**
	 * 设置字体等级 
	 * @param level 1-10级
	 */
	public void setFontLevel(int level){
		if(mCurrentFontLevel == level){
			return;
		}
		mCurrentFontLevel = level;
		saveFontLevel(level);
	}
	/**
	 * 获取字体等级 
	 * @return level 1-10级
	 */
	public int getFontLevel(){
		return mCurrentFontLevel;
	}
	
	public int getFontSize(){
		int size = formaLevelToSize(mCurrentFontLevel);
		DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
		return (int) (dm.density * size + 0.5f);
	}
	
	public int getMinFontSize(){
		int size = formaLevelToSize(0);
		DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
		return (int) (dm.density * size + 0.5f);
	}
	
	private int formaSizeToLevel(int size){
		if(size < FONT_SIZE_MIN){
			size = FONT_SIZE_MIN;
		}
		if(size > FONT_SIZE_MAX){
			size = FONT_SIZE_MAX;
		}
		int level = size - FONT_SIZE_MIN;
		return level;
	}
	/**
	 * 
	 * @param level
	 * @return dip单位使用前需要转换像素
	 */
	private int formaLevelToSize(int level){
		int size = FONT_SIZE_MIN + level;
		if(size < FONT_SIZE_MIN){
			size = FONT_SIZE_MIN;
		}
		if(size > FONT_SIZE_MAX){
			size = FONT_SIZE_MAX;
		}
		return size;
	}

	private void saveFontLevel(int level){
		mSharedPreferences.edit().putInt(SETTING_TYPE_FONT_SIZE, level).commit();
		notify(SETTING_TYPE_FONT_SIZE);
	}
	
	private int loadSaveFontLevel(){
		return mSharedPreferences.getInt(SETTING_TYPE_FONT_SIZE, formaSizeToLevel(FONT_SIZE_DEFALUT));
	}
	
	public int getLineSpaceSize(){
		int size = getFontSize();
		return (int) (size / 6 * mCurrentLineSpaceType);
	}
	
	public int getParagraphSpaceSize(){
		return (int) (getLineSpaceSize() * 3.5f);
	}
	
	public float getLineSpaceType(){
		return mCurrentLineSpaceType;
	}
	
	public void setLineSpaceType(float type){
		if(mCurrentLineSpaceType == type){
			return;
		}
		mCurrentLineSpaceType = type;
		saveLineSpaceType(type);
	}
	
	private float loadSaveLineSpaceType(){
		return mSharedPreferences.getFloat(SETTING_TYPE_FONT_LINE_SPACE_TYPE, FONT_LINE_SPACE_TYPE_2);
	}
	
	private void saveLineSpaceType(float type){
		mSharedPreferences.edit().putFloat(SETTING_TYPE_FONT_LINE_SPACE_TYPE, type).commit();
		notify(SETTING_TYPE_FONT_LINE_SPACE_TYPE);
	}
	/**
	 * 章节名和页码等装饰的字体颜色
	 * @return
	 */
	public int getThemeDecorateTextColor(){
		int textColor = 0;
		switch (mCurrentThemeType) {
		case THEME_TYPE_DAY:
			textColor = 0x88464646;
			break;
		case THEME_TYPE_NIGHT:
			textColor = 0x881e1e1e;
			break;
		case THEME_TYPE_OTHERS_1:
			textColor = 0x88cccccc;
			break;
		case THEME_TYPE_OTHERS_2:
			textColor = 0x88464646;
			break;
		case THEME_TYPE_OTHERS_3:
			textColor = 0x88cccccc;
			break;
		case THEME_TYPE_OTHERS_4:
			textColor = 0x88464646;
			break;
		}
		return textColor;
	}
	/**
	 * 书名背景字体颜色
	 * @return
	 */
	public int getThemeBookNameBGColor(){
		int bgColor = 0;
		switch (mCurrentThemeType) {
		case THEME_TYPE_DAY:
			bgColor = 0xffb78a1d;
			break;
		case THEME_TYPE_NIGHT:
			bgColor = 0xff291e01;
			break;
		case THEME_TYPE_OTHERS_1:
			bgColor = 0xfffcc42d;
			break;
		case THEME_TYPE_OTHERS_2:
			bgColor = 0xffb78a1d;
			break;
		case THEME_TYPE_OTHERS_3:
			bgColor = 0xfffcc42d;
			break;
		case THEME_TYPE_OTHERS_4:
			bgColor = 0xffb78a1d;
			break;
		}
		return bgColor;
	}
	/**
	 * 书名字体颜色
	 * @return
	 */
	public int getThemeBookNameTextColor(){
		int textColor = 0;
		switch (mCurrentThemeType) {
		case THEME_TYPE_DAY:
			textColor = 0xffffffff;
			break;
		case THEME_TYPE_NIGHT:
			textColor = 0xff5a5a5a;
			break;
		case THEME_TYPE_OTHERS_1:
			textColor = 0xffffffff;
			break;
		case THEME_TYPE_OTHERS_2:
			textColor = 0xffffffff;
			break;
		case THEME_TYPE_OTHERS_3:
			textColor = 0xffffffff;
			break;
		case THEME_TYPE_OTHERS_4:
			textColor = 0xffffffff;
			break;
		}
		return textColor;
	}
	/**
	 * 获取字体颜色
	 * @return
	 */
	public int getThemeTextColor(){
		int textColor = 0;
		switch (mCurrentThemeType) {
		case THEME_TYPE_DAY:
			textColor = 0xff464646;
			break;
		case THEME_TYPE_NIGHT:
			textColor = 0xff1e1e1e;
			break;
		case THEME_TYPE_OTHERS_1:
			textColor = 0xffcccccc;
			break;
		case THEME_TYPE_OTHERS_2:
			textColor = 0xff464646;
			break;
		case THEME_TYPE_OTHERS_3:
			textColor = 0xffcccccc;
			break;
		case THEME_TYPE_OTHERS_4:
			textColor = 0xff464646;
			break;
		}
		return textColor;
	}
	/**
	 * 获取背景颜色值
	 * @return
	 */
	public int getThemeBGColor(){
		int bgColor = 0;
		switch (mCurrentThemeType) {
		case THEME_TYPE_DAY:
			bgColor = 0xffffffff;
			break;
		case THEME_TYPE_NIGHT:
			bgColor = 0xff141414;
			break;
		case THEME_TYPE_OTHERS_1:
			bgColor = 0xff1b2932;
			break;
		case THEME_TYPE_OTHERS_2:
			bgColor = 0xfff0ead4;
			break;
		case THEME_TYPE_OTHERS_3:
			bgColor = 0xff3c3640;
			break;
		case THEME_TYPE_OTHERS_4:
			bgColor = 0xffe5d4a8;
			break;
		}
		return bgColor;
	}
	/**
	 * 获取背景图片资源ID
	 * @return 返回-1 代表没有背景图片
	 */
	public int getThemeBGImgRes(){
		int bgImgRes = -1;
		switch (mCurrentThemeType) {
		case THEME_TYPE_NIGHT:
			bgImgRes = R.drawable.read_style_night_bg;
			break;
		case THEME_TYPE_OTHERS_1:
			bgImgRes = R.drawable.read_style_other_bg_1;
			break;
		case THEME_TYPE_OTHERS_2:
			bgImgRes = R.drawable.read_style_other_bg_2;
			break;
		case THEME_TYPE_OTHERS_3:
			bgImgRes = R.drawable.read_style_other_bg_3;
			break;
		case THEME_TYPE_OTHERS_4:
			bgImgRes = R.drawable.read_style_other_bg_4;
			break;
		}
		return bgImgRes;
	}
	
	public int getThemeType(){
		return mCurrentThemeType;
	}
	
	public void setThemeType(int type){
		if(mCurrentThemeType == type){
			return;
		}
		mCurrentThemeType = type;
		saveThemeType(type);
	}
	
	private int loadSaveThemeType(){
		return mSharedPreferences.getInt(SETTING_TYPE_THEME, THEME_TYPE_OTHERS_2);
	}
	
	private void saveThemeType(int type){
		mSharedPreferences.edit().putInt(SETTING_TYPE_THEME, type).commit();
		notify(SETTING_TYPE_THEME);
	}
	
	public int getBrightessLevel(){
		return mCurrentBrightessLevel;
	}
	
	public void setBrightessLevel(int level){
		if(mCurrentBrightessLevel == level){
			return;
		}
		mCurrentBrightessLevel = level;
		saveBrightessLevel(level);
	}

	private int loadSaveBrightessLevel(){
		return mSharedPreferences.getInt(SETTING_TYPE_BRIGHTESS_LEVEL, 50);
	}
	
	private void saveBrightessLevel(int type){
		mSharedPreferences.edit().putInt(SETTING_TYPE_BRIGHTESS_LEVEL, type).commit();
		notify(SETTING_TYPE_BRIGHTESS_LEVEL);
	}
	
	private final void runOnUiThread(Runnable action) {
		mHandler.post(action);
	}
	
	public interface SettingListener{
		public void onSettingChange(ReadSetting readSetting,String type);
	}
}
