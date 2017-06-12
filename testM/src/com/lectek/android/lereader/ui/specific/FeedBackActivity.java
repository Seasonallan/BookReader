package com.lectek.android.lereader.ui.specific;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.feedback.FeedbackViewModel;
import com.lectek.android.lereader.binding.model.feedback.FeedbackViewModel.FeedbackUserAction;
import com.lectek.android.lereader.data.FeedbackAdapterInfo;
import com.lectek.android.lereader.lib.image.ImageLoader;
import com.lectek.android.lereader.lib.utils.BitmapUtil;
import com.lectek.android.lereader.lib.utils.FileUtil;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.net.response.FeedBackInfo;
import com.lectek.android.lereader.net.response.FeedBackUserReplyInfos;
import com.lectek.android.lereader.ui.common.BaseActivity;
import com.lectek.android.lereader.utils.Constants;
import com.lectek.android.lereader.utils.DialogUtil;
import com.lectek.android.lereader.utils.KeyBoardUtil;
import com.lectek.android.lereader.utils.DialogUtil.ConfirmListener;
import com.lectek.android.lereader.utils.ToastUtil;
import com.lectek.android.lereader.widgets.PullRefreshLayout;
import com.lectek.android.lereader.widgets.PullRefreshLayout.OnPullListener;
import com.lectek.android.lereader.widgets.PullRefreshLayout.OnPullStateListener;
import com.lectek.android.lereader.widgets.adapter.FeedbackAdater;
import com.lectek.android.lereader.widgets.adapter.FeedbackAdater.FeedbackAdaterAction;

/**
 * @description 意见反馈
 * @author donghz
 * @date 2014-3-21
 *
 */

public class FeedBackActivity extends BaseActivity implements FeedbackUserAction,OnPullListener,OnPullStateListener{
	
	private View mContentView;
	private FeedbackAdater mFeedbackAdater;
	private FeedbackViewModel mFeedbackViewModel;
	private ListView mFeedbackLv;
	private EditText mFeedBackEdit;
	
	private PullRefreshLayout mPullRefreshLayout;
	private static final int CHOOSE_PICTURE =10001;
	
	private boolean mInLoading = false;
	private TextView mActionText;
	private TextView mTimeText;
	private View mProgress;
	private View mActionImage;
	private Animation mRotateUpAnimation;
	private Animation mRotateDownAnimation;
	private String mLastTimeStr="";
	
	private final static int MSG_LOADING = 1;
	private final static int MSG_LOADED = 2;
	
	public final static int FROM_TAG =1;
	public final static int TO_TAG =2;
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_LOADING:
					buildLastTimeStr();
					if(!TextUtils.isEmpty(mLastTimeStr)){
						mFeedbackViewModel.getFeedBackListInfo(mLastTimeStr,true);
					}
					sendEmptyMessageDelayed(MSG_LOADED, 400);
					break;
				case MSG_LOADED:
					dataLoaded();
					break;
			}
		}
	};
	
	/**
	 * 组建请求数据的时间格式
	 */
	private void buildLastTimeStr(){
		String time =mFeedbackViewModel.mLastUpdateTime;
		if(TextUtils.isEmpty(time))
			return;
		String tempTime = time.replaceAll(" ", "%20");
		String temp = tempTime.replaceAll(":","%3A");
		mLastTimeStr = temp;
	}
	
	private  ArrayList<FeedbackAdapterInfo> mAdapterInfos;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitleContent(getString(R.string.feedback_str));
		init();
	}

	@Override
	protected View newContentView(Bundle savedInstanceState) {
		 mFeedbackViewModel = new FeedbackViewModel(FeedBackActivity.this, this, this);
		 mContentView = bindView(R.layout.feedback_layout,mFeedbackViewModel);	
		 mFeedbackViewModel.onStart();
		 return mContentView;
	}
	
	private void init(){
		mFeedbackLv = (ListView)mContentView.findViewById(R.id.feedback_list);
		mFeedBackEdit =(EditText)mContentView.findViewById(R.id.feedback_edit);
		mAdapterInfos = new ArrayList<FeedbackAdapterInfo>();
		mFeedbackAdater = new FeedbackAdater(FeedBackActivity.this,mAdapterInfos);
		mFeedbackLv.setAdapter(mFeedbackAdater);
		mFeedbackAdater.setAdatperAction(adaterAction);
		initPullFresh();
		copyAccesFile();
	}
	
	/**
	 * 下拉刷新初始化
	 */
	private void initPullFresh(){
		mRotateUpAnimation = AnimationUtils.loadAnimation(this,R.anim.rotate_up);
		mRotateDownAnimation = AnimationUtils.loadAnimation(this,R.anim.rotate_down);
		mPullRefreshLayout = (PullRefreshLayout)findViewById(R.id.feedback_pull_container);
		mPullRefreshLayout.setOnActionPullListener(this);
		mPullRefreshLayout.setOnPullStateChangeListener(this);
		mProgress = findViewById(android.R.id.progress);
		mActionImage = findViewById(android.R.id.icon);
		mActionText = (TextView) findViewById(R.id.pull_note);
		mTimeText = (TextView) findViewById(R.id.refresh_time);
	}
	
	/**
	 * 用于复制意见反馈acess文件
	 */
	private void copyAccesFile(){
		FileUtil.createFileDir(Constants.BOOKS_TEMP_FEEDBACK_IMAGE);
		String fileName="fdfs_client.conf";
		String filePath=Constants.BOOKS_TEMP_FEEDBACK_IMAGE;
		FileUtil.copyAssets(this_,fileName,filePath);
	}
	
	@Override
	public void exceptionHandle(String str) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void optToast(String str) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getFeedBackListOk(FeedBackInfo infos,boolean reFresh) {
		if(infos!=null){
			buidAdaterData(infos);
			notifyDataSet();
		}
	}
	
	/**
	 * 组建适配器数据
	 * @param info
	 */
	private void buidAdaterData(FeedBackInfo info){
		//组建话题项数据
		FeedbackAdapterInfo firstitem = new FeedbackAdapterInfo();
		firstitem.setContent(info.getContent());
		firstitem.setCreateTime(info.getCreateTime());
		firstitem.setContentType(info.getContentType());
		firstitem.setFromTag(TO_TAG);
		mAdapterInfos.add(firstitem);
		
		//组建反馈列表项数据
		if(info.getFeedBackUserReplyInfos()!=null){
			for(FeedBackUserReplyInfos reInfo:info.getFeedBackUserReplyInfos()){
				FeedbackAdapterInfo reItem = new FeedbackAdapterInfo();
				reItem.setContent(reInfo.getContent());
				reItem.setCreateTime(reInfo.getCreateTime());
				reItem.setContentType(reInfo.getContentType());
				if(reInfo.getUserType().equals("1")){//1客户端用户或游客,2后台客服
					reItem.setFromTag(TO_TAG);
				}else{
					reItem.setFromTag(FROM_TAG);
				}
				mAdapterInfos.add(reItem);
			}
		}
		//组建评分项数据
		if(info.getIsClose().equals("1")){
			buildScoreItem(info.getId(),info.getUpdateTime(),info.getScore(),false);
		}
		
	}
	
	/**
	 * 添加评分项数据
	 * @param feedbackId
	 * @param needNotify (刷新数据)
	 */
	private void buildScoreItem(Integer feedbackId,String updateTime,Integer score,boolean needNotify){
		FeedbackAdapterInfo scoreItem = new FeedbackAdapterInfo();
		scoreItem.setCloseTag("1");
		scoreItem.setCreateTime(updateTime);
		scoreItem.setScore(score);
		if(feedbackId!=null){
			scoreItem.setFeedbackId(Integer.valueOf(feedbackId));
		}
		LogUtil.e("dhz", "feedbackId " + feedbackId + "buildScoreItem");
		mAdapterInfos.add(scoreItem);
		if(needNotify){
			notifyDataSet();
		}
	}
	
	/**
	 * 组建回复信息数据添加到适配器
	 */
	private void buildAddMessageInfo(String mRcontentStr,String mRcreateTime,String contentType){
		FeedbackAdapterInfo addMesInfo = new FeedbackAdapterInfo();
		addMesInfo.setContent(mRcontentStr);
		addMesInfo.setCreateTime(mRcreateTime);
		addMesInfo.setContentType(contentType);
		addMesInfo.setFromTag(TO_TAG);
		mAdapterInfos.add(addMesInfo);
		notifyDataSet();
	}
	
	/**
	 * 刷新数据
	 */
	private void notifyDataSet(){
        Collections.sort(mAdapterInfos,mTimeComparator);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mFeedbackAdater.notifyDataSetChanged();
                mFeedbackLv.setSelection(mFeedbackAdater.getCount() - 1);
            }
        });
	}
	
	/**
	 * 按照时间对适配器数据排序
	 */
	Comparator<FeedbackAdapterInfo> mTimeComparator = new Comparator<FeedbackAdapterInfo>() {
		@Override
		public int compare(FeedbackAdapterInfo object1, FeedbackAdapterInfo object2) {
			FeedbackAdapterInfo feed1 = (FeedbackAdapterInfo)object1;
			FeedbackAdapterInfo feed2 = (FeedbackAdapterInfo)object2;
		    return  feed1.getCreateTime().compareTo(feed2.getCreateTime())>0 ? 1 :-1; 
		}
	};
	
	@Override
	public void showScoreItem(Integer feedbackId,String updateTime,Integer score,boolean needNotify) {
		buildScoreItem(feedbackId,updateTime,score,needNotify);
	}
	@Override
	public void getFeedBackListFail() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hideKeyBoard() {
		KeyBoardUtil.hideInputMethodManager((InputMethodManager)this_.
				getSystemService(Context.INPUT_METHOD_SERVICE),mFeedBackEdit);
	}

	/**
	 * 评分事件
	 */
	private FeedbackAdaterAction adaterAction = new FeedbackAdaterAction() {
		@Override
		public void selectScore(final Integer feedbackId, final int Score) {
			LogUtil.e("dhz", "feedbackId " + feedbackId + "score" + Score);
			String scoreStr= getString(R.string.satisfactory_well);
			if(Score==2){//1满意，2不满意 
				scoreStr = getString(R.string.satisfactory_un);
			}
			String titleStr = getString(R.string.feedback_reply_str);
			String message = getString(R.string.feedback_your_score_str,scoreStr);
			DialogUtil.commonConfirmDialog(FeedBackActivity.this, titleStr, message, 
					R.string.btn_text_confirm, R.string.btn_text_cancel,
					new ConfirmListener() {
						@Override
						public void onClick(View v) {
							mFeedbackViewModel.feedbackScore(feedbackId,Score);
						}
					},null);
		}

		@Override
		public void showImg(String url) {
			showImgPopWindow(url);
		}
	};
	
	/**
	 * 显示图片
	 * @param url
	 */
	private void showImgPopWindow(String url){
		ImageLoader imageLoader = new ImageLoader(FeedBackActivity.this);
		View contentView =LayoutInflater.from(FeedBackActivity.this).inflate(R.layout.show_img_pop_layout, null);
		ImageView showImg = (ImageView)contentView.findViewById(R.id.pop_img_iv);
		imageLoader.setImageViewBitmap(Constants.BOOKS_TEMP, Constants.BOOKS_TEMP_IMAGE,url,url,showImg,R.drawable.book_default);
		PopupWindow showImgPop = new PopupWindow(contentView, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, true);
		showImgPop.setTouchable(true); // 设置PopupWindow可触摸  
		showImgPop.setBackgroundDrawable(getResources().getDrawable(R.drawable.transparent_background));
		showImgPop.setFocusable(true);
		showImgPop.showAtLocation(mContentView, Gravity.FILL, 0, 0);  
	}
	
	@Override
	public void onPullOut() {
		if (!mInLoading) {
			mActionText.setText(R.string.note_pull_refresh);
			mActionImage.clearAnimation();
			mActionImage.startAnimation(mRotateUpAnimation);
		}
	}

	@Override
	public void onPullIn() {
		if (!mInLoading) {
			mActionText.setText(R.string.note_pull_down);
			mActionImage.clearAnimation();
			mActionImage.startAnimation(mRotateDownAnimation);
		}
	}

	@Override
	public void onSnapToTop() {
		if (!mInLoading) {
			mInLoading = true;
			mPullRefreshLayout.setEnableStopInActionView(true);
			mActionImage.clearAnimation();
			mActionImage.setVisibility(View.GONE);
			mProgress.setVisibility(View.VISIBLE);
			mActionText.setText(R.string.note_pull_loading);
			mHandler.sendEmptyMessage(MSG_LOADING);
		}
	}
	
	private void dataLoaded() {
		if (mInLoading) {
			mInLoading = false;
			mPullRefreshLayout.setEnableStopInActionView(false);
			mPullRefreshLayout.hideActionView();
			mActionImage.setVisibility(View.VISIBLE);
			mProgress.setVisibility(View.GONE);

			if (mPullRefreshLayout.isPullOut()) {
				mActionText.setText(R.string.note_pull_refresh);
				mActionImage.clearAnimation();
				mActionImage.startAnimation(mRotateUpAnimation);
			} else {
				mActionText.setText(R.string.note_pull_down);
			}

			mTimeText.setText(getString(R.string.note_update_at, DateFormat.getTimeFormat(this)
					.format(new Date(System.currentTimeMillis()))));
		}
	}

	@Override
	public void onShow() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onHide() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isPullEnabled() {
		return true;
	}
	
	@Override
	public void showNoDataView(tipImg tip, boolean bShow) {
		showTipView(tip, bShow,"",null);
		setOprBtnGone();
	}

	@Override
	public void addMesToAdapter(String mRcontentStr, String mRcreateTime,String contentType) {
		buildAddMessageInfo(mRcontentStr,mRcreateTime,contentType);
	}

	@Override
	public void sendImg() {
		getLocalImage();
	}
	/**
	 * 获取本地图片
	 */
	private void getLocalImage() {
		new AsyncTask<String, Integer, Boolean>() {

			@Override
			protected void onPreExecute() {
				ToastUtil.showToast(FeedBackActivity.this,getString(R.string.waitfor_image_local));
			}

			@Override
			protected Boolean doInBackground(String... params) {
				return true;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				try {
					Intent mIntent = new Intent(Intent.ACTION_GET_CONTENT);
					mIntent.setType("image/*");
					mIntent.putExtra("crop", "true");
					mIntent.putExtra("aspectX", 1);
					mIntent.putExtra("aspectY", 1);
					mIntent.putExtra("outputX", 128);
					mIntent.putExtra("outputY", 128);
					mIntent.putExtra("return-data", true);
					startActivityForResult(mIntent, CHOOSE_PICTURE);
				} catch (Exception e) {
					ToastUtil.showToast(FeedBackActivity.this,R.string.get_local_image_error);
				}
			}
		}.execute();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		 if (resultCode == RESULT_OK) {
	         switch (requestCode) {
	            case CHOOSE_PICTURE:
	                    Bitmap photoBitmap = data.getParcelableExtra("data");
	                    if (photoBitmap==null&&data.getData()!=null) {
	        				LogUtil.i("out", "userinfoset,onActivityResult:" + "mBitmap==null or get data==null");
							String path = data.getData().getPath();
							LogUtil.e("path:" + path);
							path = path.replace("//", "/");
							path = path.replace("/mimetype", "");
							photoBitmap = BitmapUtil.getBitmapWithPath(path, 100);
	                    }
	                    FileUtil.createFileDir(Constants.BOOKS_TEMP_FEEDBACK_IMAGE);
	                    String savePath = Constants.BOOKS_TEMP_FEEDBACK_IMAGE+"temp";
	                    boolean saveResult = FileUtil.saveBitmap(savePath, photoBitmap);
	                    if(saveResult){//图片保存成功
	                    	mFeedbackViewModel.getImgUrl(savePath);
	                    	LogUtil.e("dhz", "pic----temp savePath: " + savePath);
	                    }
	                    if (photoBitmap != null) {
	                    	photoBitmap.recycle();
	                    	photoBitmap=null;
	                    }
	                break;
	                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   
	            default:
	                break;
	            }
	    }
	}

	@Override
	public void scoreSuccessNotify(Integer scorefeedbackId,Integer score) {
		if(scorefeedbackId==null)
			return;
		for(FeedbackAdapterInfo item:mAdapterInfos){
			 if(item.getFeedbackId()==scorefeedbackId){
				 item.setScore(score);
				 mFeedbackAdater.notifyDataSetChanged();
			 }
		}
	}

}
