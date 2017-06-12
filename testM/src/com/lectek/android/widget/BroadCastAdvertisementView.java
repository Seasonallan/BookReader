package com.lectek.android.widget;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.lib.net.exception.GsonResultException;
import com.lectek.android.lereader.lib.utils.ApnUtil;
import com.lectek.android.lereader.net.response.BookSubjectClassification;
import com.lectek.android.lereader.net.response.tianyi.CommonEntiyPaser;
import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.specific.ActivityChannels;
import com.lectek.android.lereader.ui.specific.SubjectDetailActivity;
import com.lectek.android.lereader.ui.specific.ThirdUrlActivity;
import com.lectek.android.lereader.utils.CommonUtil;

/**
 * 广告通知自定义布局类
 * @author Shizq
 *
 */
public class BroadCastAdvertisementView extends LinearLayout {
	
	ArrayList<BookSubjectClassification> mlist;
	private CustomMarqueeTextView mFirstTextView;
	private TextView mSecondTextView;
	
	/**
	 * 书籍详情
	 */
	private static final String TYPE_BOOK_DETAIL = "1";
	/**
	 * URL外链
	 */
	private static final String TYPE_EXTERNAL_URL = "3";
	/**
	 * 天翼阅读包月专区
	 */
	private static final String TYPE_TYYD_MONTH = "4";
	
	private static final String TYPE_SUBJECT_CATALOG = "6";
	
	private int currentIndex = 0;
	

	public BroadCastAdvertisementView(Context context) {
		super(context);
		init();
	}
	
	public BroadCastAdvertisementView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init(){
		if(ApnUtil.isNetAvailable(getContext())){
			String gsonStr = PreferencesUtil.getInstance(getContext()).getBroadCastAdvertisementInfo();
			if(!TextUtils.isEmpty(gsonStr)){
				try {
					CommonEntiyPaser<BookSubjectClassification> paser = new CommonEntiyPaser<BookSubjectClassification>();
                    mlist =paser.parseLeyueListEntity(gsonStr, BookSubjectClassification.class);
				} catch (GsonResultException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(mlist != null && mlist.size() > 0){
					this.setVisibility(View.VISIBLE);
					return;
				}
			}
		}
		
		this.setVisibility(View.GONE);
		
	}

	//onFinishInflate回调在onAttachedToWindow回调之前被调用
	@Override
	protected void onFinishInflate() {
		
		mFirstTextView = (CustomMarqueeTextView)findViewById(R.id.first_textview);
//		mFirstTextView.setEllipsize(TruncateAt.MARQUEE);
		mSecondTextView = (TextView)findViewById(R.id.second_textview);
		findViewById(R.id.close_btn).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				BroadCastAdvertisementView.this.setVisibility(View.GONE);
			}
		});
		
		BroadCastAdvertisementView.this.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				gotoSubjectDetail();
			}
		});
		
		if(mlist != null && mlist.size() > 0 ){
			mFirstTextView.setText(mlist.get(0).getSubjectName());
			if(mlist.size() > 1){
				mSecondTextView.setText(mlist.get(1).getSubjectName());
			}
		}
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		postDelayed(new Runnable() {
			
			@Override
			public void run() {
				startMarquee(mFirstTextView,1);
				
				if(mlist == null || mlist.size() < 2)
					return;
				
				mFirstTextView.startRun();
				mFirstTextView.setOnMarqueeFinishedListener(new CustomMarqueeTextView.OnMarqueeFinishedListener() {
					
					@Override
					public void marqueeFinished() {
						System.out.println("on MarqueeFinished");
						mHandler.post(new Runnable() {
							
							@Override
							public void run() {
								changeMessageAnimation();
							}
						});
						
					}
				});
			}
		}, 1500);
	}
	
	private void gotoSubjectDetail(){
		BookSubjectClassification subjectInfo = mlist.get(currentIndex);
		//单本书籍详情
		if(subjectInfo.getSubjectType().equals(TYPE_BOOK_DETAIL) && TextUtils.isEmpty(subjectInfo.getOutBookId())){
			ActivityChannels.gotoLeyueBookDetail(getContext(), subjectInfo.getMemo());
		}else if(subjectInfo.getSubjectType().equals(TYPE_EXTERNAL_URL)){
			//URL外链
			
			Intent mIntent = new Intent(getContext(),ThirdUrlActivity.class);
			String url = subjectInfo.getMemo();
			url += ("?"+ CommonUtil.getWoInfoUrlParams(getContext()));
			mIntent.putExtra(LeyueConst.GOTO_THIRD_PARTY_URL_TAG, url);
			mIntent.putExtra(ThirdUrlActivity.EXTRA_TITLE, subjectInfo.getSubjectName());
			getContext().startActivity(mIntent);
			
		}else if (subjectInfo.getSubjectType().equals(TYPE_BOOK_DETAIL) && !TextUtils.isEmpty(subjectInfo.getOutBookId())) {
			//天翼阅读书籍详情
			String surfingBookId = subjectInfo.getOutBookId();
            String leBookId = subjectInfo.getMemo();
			if (!TextUtils.isEmpty(surfingBookId)) {
				ActivityChannels.gotoLeyueBookDetail(getContext(), surfingBookId,
                        LeyueConst.EXTRA_BOOLEAN_IS_SURFINGREADER, true,
                        LeyueConst.EXTRA_LE_BOOKID, leBookId
                        );
			}
		}else if(subjectInfo.getSubjectType().equals(TYPE_TYYD_MONTH)){
			//天翼阅读包月专区
			//edited by chends 2014-05-27 疑是无用类，删除
//			Intent mIntent = new Intent(getContext(),MonthlyPaymentActivity.class);
//			mIntent.putExtra(LeyueConst.GOTO_MONTHLY_PAYMENT_ZONE_TAG, subjectInfo.getMemo());
//			getContext().startActivity(mIntent);
			
		}else if(subjectInfo.getSubjectType().equals(TYPE_SUBJECT_CATALOG)){
			//专题栏目
			Intent intent = new Intent(getContext(),SubjectDetailActivity.class);
			intent.putExtra(LeyueConst.GOTO_SUBJECT_DETAIL_TAG, LeyueConst.ASSERT_FOLDER_PATH.concat(LeyueConst.BOOK_CITY_SUBJECT_CATALOG_HTML)+"?url="+subjectInfo.getSubjectId()+LeyueConst.GET_TITLE_TAG+subjectInfo.getSubjectName());
			intent.putExtra(SubjectDetailActivity.EXTRA_SUBJECT_TYPE, SubjectDetailActivity.SUBJECT_TYPE_CATALOG);
			getContext().startActivity(intent);
		}else{
			//专题详情
			
			Intent intent = new Intent(getContext(),SubjectDetailActivity.class);
			intent.putExtra(LeyueConst.GOTO_SUBJECT_DETAIL_TAG, LeyueConst.ASSERT_FOLDER_PATH.concat(LeyueConst.BOOK_CITY_SUBJECT_DETAIL_HTML)+"?url="+subjectInfo.getSubjectId()+LeyueConst.GET_TITLE_TAG+subjectInfo.getSubjectName());
			getContext().startActivity(intent);
		}
	}
	
	/**
	 * 通过动画切换公告
	 */
	private void changeMessageAnimation(){
		
		if(currentIndex < mlist.size()-1){
			currentIndex++;
		}else{
			currentIndex = 0;
		}
		
		mFirstTextView.clearAnimation();
		mSecondTextView.clearAnimation();
		
		mFirstTextView.setMarqueeRepeatLimit(0);
		
		TranslateAnimation leave = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1);
		leave.setDuration(500);
		leave.setRepeatMode(Animation.INFINITE);
//		leave.setFillAfter(true);
		
		mSecondTextView.setVisibility(View.VISIBLE);
		mSecondTextView.setText(mlist.get(currentIndex).getSubjectName());
		TranslateAnimation come = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0);
		come.setDuration(500);
		come.setRepeatMode(Animation.INFINITE);
//		come.setFillAfter(true);
		
		mFirstTextView.startAnimation(leave);
		mSecondTextView.startAnimation(come);
		
		come.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				mSecondTextView.setVisibility(View.GONE);
//				mSecondTextView.setText(mlist.get(0).getSubjectName());
				mFirstTextView.setText(mlist.get(currentIndex).getSubjectName());
				
				mFirstTextView.setSelected(false);
				startMarquee(mFirstTextView, 1);
				mFirstTextView.startRun();
			}
		});
		
	}
	
	private Handler mHandler = new Handler(getContext().getMainLooper());
	
	
	private void startMarquee(TextView tv, int repeatLimit){
		tv.setMarqueeRepeatLimit(repeatLimit);
		tv.setFocusable(true);
		tv.setFocusableInTouchMode(true);
		tv.setSelected(true);
	}

}
