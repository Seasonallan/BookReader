package com.lectek.android.lereader.ui.specific;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.lib.utils.DimensionsUtil;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.login_leyue.UserLoginLeYueNewActivity;
import com.lectek.android.lereader.utils.CommonUtil;
import com.lectek.android.lereader.utils.ToastUtil;

/**
 * 简单的程序初始引导界面。
 * @Description: Android实现左右滑动引导效果
 * @Author chendt
 * @Date 2013-1-22
 * @Version V1.0
 */
public class GuiderActivity extends Activity {
	 private ViewPager viewPager;  
	 private ArrayList<View> pageViews;  
	 private ImageView imageView;  
	 private ImageView[] imageViews;
	 /**包裹滑动图片Group*/ 
	 private ViewGroup picGroup;
	 /**包裹小圆点Group*/ 
	 private ViewGroup dotGroup;
	 private String[] mTextArrays;
	 private int[] mPicArrays;
	 private LayoutInflater inflater;
	 
	 private boolean exitApp = false;
	 private boolean isShowEnterBtn = true;
	 public static String SHOW_ENTER_BTN = "show_enter_btn";
	 private int currentPage;
	    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,                
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        if (getIntent().getExtras()!=null) {
        	isShowEnterBtn = getIntent().getExtras().getBoolean(SHOW_ENTER_BTN,true);
		}
    	inflater = getLayoutInflater();
    	picGroup = (ViewGroup)inflater.inflate(R.layout.guide_layout, null);  
    	dotGroup = (ViewGroup)picGroup.findViewById(R.id.viewGroup);  
	    viewPager = (ViewPager)picGroup.findViewById(R.id.guidePages);	
        drawPicView();
        
        //这期暂时不使用页面的点
        //drawDotView();
        setContentView(picGroup);
        
        viewPager.setAdapter(new PageViewerAdapter());  
        viewPager.setOnPageChangeListener(new PageViewerChangeListener());  
        PreferencesUtil.getInstance(this).setIsFirstGuideFinish(true);
        PreferencesUtil.getInstance(this).resetAppVersion();
    }
    
    /**
     * 组建圆点界面
     */
    private void drawDotView() {
    	imageViews = new ImageView[pageViews.size()];  
        for (int i = 0; i < pageViews.size(); i++) {  
            imageView = new ImageView(GuiderActivity.this);  
            int left = DimensionsUtil.dip2px(5, this);
            int right = DimensionsUtil.dip2px(5, this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            		LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lp.setMargins(left, 0, right, 0);
            imageView.setLayoutParams(lp);
            imageViews[i] = imageView;  
            
            if (i == 0) {  
                //默认选中第一张图片
                imageViews[i].setBackgroundResource(R.drawable.guide_dot_choosed);  
            } else {  
                imageViews[i].setBackgroundResource(R.drawable.guide_dot_unchoosed);  
            }  
            
            dotGroup.addView(imageViews[i]);  
        }
	}

	/**
	 * 组建图片界面
	 */
	private void drawPicView() {
		//组建界面，存储到pageViews集合里。
    	pageViews = new ArrayList<View>();  
    	//实现方式1.
        mPicArrays = CommonUtil.getIntArray(this,R.array.view_pager_pic);

        for (int i = 0; i < mPicArrays.length; i++) {
        	View mView = inflater.inflate(R.layout.page_view, null);
//        	TextView mText = (TextView) mView.findViewById(R.id.textView);
        	mView.setBackgroundDrawable(getResources().getDrawable(mPicArrays[i]));
//        	mText.setText(mTextArrays[i]);
        	if (i == mPicArrays.length -1) {
        		if (isShowEnterBtn) {
        			//本期展示屏蔽按钮功能
//        			Button mButton = (Button) mView.findViewById(R.id.enter_app);
//        			mButton.setVisibility(View.VISIBLE);
//        			mButton.setOnClickListener(gotoLoginListener);
        			mView.setOnClickListener(gotoLoginListener);
				}
			}
        	pageViews.add(mView);
		}
	}

	/**
	 * 进入登录入口监听
	 */
	OnClickListener gotoLoginListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			boolean isLogin = PreferencesUtil.getInstance(GuiderActivity.this).getIsLogin();
			Intent intent = new Intent(GuiderActivity.this, SlideActivityGroup.class);//MyZoneActivity
			startActivity(intent);
			if(isLogin){
				ActivityChannels.gotoUserInfoActivity(GuiderActivity.this);
			}else{
				//注释原来登录逻辑，调用新的融合天翼用户登录逻辑 wuwq 2014-04-18
//				intent = new Intent(GuiderActivity.this, UserLoginActivityLeYue.class);
				intent = new Intent(GuiderActivity.this, UserLoginLeYueNewActivity.class);
				startActivity(intent);
			}
			finish();
		}
	};
	
	// 引导页Pager适配器
    class PageViewerAdapter extends PagerAdapter {  
  	  
        @Override  
        public int getCount() {  
            return pageViews.size();  
        }  
  
        @Override  
        public boolean isViewFromObject(View arg0, Object arg1) {  
            return arg0 == arg1;  
        }  
  
        @Override  
        public int getItemPosition(Object object) {  
            // TODO Auto-generated method stub  
            return super.getItemPosition(object);  
        }  
  
        @Override  
        public void destroyItem(View arg0, int arg1, Object arg2) {  
            ((ViewPager) arg0).removeView(pageViews.get(arg1));  
        }  
  
        @Override  
        public Object instantiateItem(View arg0, int arg1) {  
            ((ViewPager) arg0).addView(pageViews.get(arg1));  
            return pageViews.get(arg1);  
        }  
  
        @Override  
        public void restoreState(Parcelable arg0, ClassLoader arg1) {  
            // TODO Auto-generated method stub  
        }  
  
        @Override  
        public Parcelable saveState() {  
            // TODO Auto-generated method stub  
            return null;  
        }  
  
        @Override  
        public void startUpdate(View arg0) {  
            // TODO Auto-generated method stub  
        }  
  
        @Override  
        public void finishUpdate(View arg0) {  
            // TODO Auto-generated method stub  
        }  
    } 
    
    // Pager事件监听
    class PageViewerChangeListener implements OnPageChangeListener {  
    	  
        @Override  
        public void onPageScrollStateChanged(int arg0) {  
            // TODO Auto-generated method stub  
        }  
  
        @Override  
        public void onPageScrolled(int arg0, float arg1, int arg2) {  
            // TODO Auto-generated method stub  
        }  
  
        @Override  
        public void onPageSelected(int arg0) {  
        	currentPage = arg0;
        	//本期不需要显示和切换点状态，所以暂屏蔽这些代码
//        	imageViews[arg0].setBackgroundResource(R.drawable.guide_dot_choosed);
//        	
//            for (int i = 0; i < imageViews.length; i++) {  
//                if (arg0 != i) {//未处于当前界面的圆点都设为未选中  
//                    imageViews[i].setBackgroundResource(R.drawable.guide_dot_unchoosed);  
//                }  
//            }
        }  
    }  
    
    private long lastExitTime = 0;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (!isShowEnterBtn) {//设置界面查看欢迎页，不监听返回键
			return super.onKeyDown(keyCode, event);
		}
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if(System.currentTimeMillis()-lastExitTime > 1500){
				lastExitTime = System.currentTimeMillis();
				ToastUtil.showToast(GuiderActivity.this, "再按一次退出！");
			}else {
				PreferencesUtil.getInstance(this).setIsFirstGuideFinish(false);
				GuiderActivity.this.finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	//用户在最后一页向左滑动界面进入书架
	float startX = 0;
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if(ev.getAction() == MotionEvent.ACTION_DOWN){
			startX = ev.getX();
		}else if(ev.getAction() == MotionEvent.ACTION_UP){
			float length = ev.getX()-startX;
			if(length < 0 && currentPage == pageViews.size()-1){
				if(Math.abs(length) >= getWindow().getWindowManager().getDefaultDisplay().getWidth()*1/5){
					Intent intent = new Intent(GuiderActivity.this, SlideActivityGroup.class);//MyZoneActivity
					startActivity(intent);
					finish();
				}
			}
			startX = 0;
		}
		return super.dispatchTouchEvent(ev);
	}
}