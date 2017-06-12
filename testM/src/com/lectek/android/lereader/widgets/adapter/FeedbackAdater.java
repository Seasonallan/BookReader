package com.lectek.android.lereader.widgets.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.feedback.FeedbackViewModel;
import com.lectek.android.lereader.data.FeedbackAdapterInfo;
import com.lectek.android.lereader.lib.image.ImageLoader;
import com.lectek.android.lereader.ui.specific.FeedBackActivity;
import com.lectek.android.lereader.utils.Constants;

import java.util.ArrayList;

/**
 * 意见反馈 适配器
 * @author donghz
 * @date 2013-3-23
 */
public class FeedbackAdater extends BaseAdapter{

	private Context mContext;
	private ArrayList<FeedbackAdapterInfo> mFeedbackAdapterInfos;
	private FeedbackAdaterAction mAdaterAction;
	
	public FeedbackAdater(Context context, ArrayList<FeedbackAdapterInfo> infos){
		mContext = context;
		mFeedbackAdapterInfos = infos;
	}
	
	public void setAdatperAction(FeedbackAdaterAction adaterAction){
		mAdaterAction = adaterAction;
	}
	
	@Override
	public int getCount() {
		if(mFeedbackAdapterInfos!=null){
			return mFeedbackAdapterInfos.size();
		}else{
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		if(mFeedbackAdapterInfos !=null && position < mFeedbackAdapterInfos.size()){
			return mFeedbackAdapterInfos.get(position);
		}else{
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FeedbackViewHolder viewHolder =null;
		final FeedbackAdapterInfo item  =(FeedbackAdapterInfo)getItem(position);
		if(convertView==null){
			viewHolder = new FeedbackViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.feedback_item_layout,null);
			
			viewHolder.leftTimeTv = (TextView)convertView.findViewById(R.id.feedback_left_time_tv);
			viewHolder.leftContentTv =(TextView)convertView.findViewById(R.id.feedback_left_content_tv);
			viewHolder.leftImgIv =(ImageView)convertView.findViewById(R.id.feedback_left_img_iv);
			viewHolder.leftLayout=(LinearLayout)convertView.findViewById(R.id.feedback_left_msg_layout);
			viewHolder.leftImgLayout = (LinearLayout)convertView.findViewById(R.id.feedback_left_img_layout);
			
			viewHolder.rightContentTv=(TextView)convertView.findViewById(R.id.feedback_right_content_tv);
			viewHolder.rightImgIv=(ImageView)convertView.findViewById(R.id.feedback_right_img_iv);
			viewHolder.rightTimeTv=(TextView)convertView.findViewById(R.id.feedback_right_time_tv);
			viewHolder.rightLayout=(LinearLayout)convertView.findViewById(R.id.feedback_right_msg_layout);
			viewHolder.rightImgLayout=(LinearLayout)convertView.findViewById(R.id.feedback_right_img_layout);
			
			viewHolder.scoreTimeTv=(TextView)convertView.findViewById(R.id.feedback_score_time_tv);
			viewHolder.wellScoreBtn=(RadioButton)convertView.findViewById(R.id.feedback_well_rb);
			viewHolder.badScoreBtn=(RadioButton)convertView.findViewById(R.id.feedback_bad_rb);
			viewHolder.scoreValueTv =(TextView)convertView.findViewById(R.id.score_value_tv);
			viewHolder.scoreRadioGroup=(RadioGroup)convertView.findViewById(R.id.score_radio_group);
			
			viewHolder.msgLayout =(LinearLayout)convertView.findViewById(R.id.feedback_msg_layout);
			viewHolder.scoreLayout =(LinearLayout)convertView.findViewById(R.id.feedback_score_layout);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (FeedbackViewHolder) convertView.getTag();
		}
		
		String time = item.getCreateTime();
		if(!TextUtils.isEmpty(time)){
		    time = item.getCreateTime().substring(5, item.getCreateTime().length()-3);
		}
		
		if(item.getFromTag()==FeedBackActivity.FROM_TAG){//左边(服务器发送)
			viewHolder.leftLayout.setVisibility(View.VISIBLE);
			viewHolder.rightLayout.setVisibility(View.GONE);
			
			if(TextUtils.isEmpty(item.getContentType())){
				item.setContentType("1");
			}
			
			if(item.getContentType().equals("2")){//图片
				viewHolder.leftImgLayout.setVisibility(View.VISIBLE);
				viewHolder.leftContentTv.setVisibility(View.GONE);
				ImageLoader imageLoader = new ImageLoader(mContext);
				imageLoader.setImageViewBitmap(Constants.BOOKS_TEMP, Constants.BOOKS_TEMP_IMAGE,item.getContent(),item.getContent(),viewHolder.leftImgIv,R.drawable.book_default);
			}else{
				viewHolder.leftImgLayout.setVisibility(View.GONE);//文字
				viewHolder.leftContentTv.setVisibility(View.VISIBLE);
				viewHolder.leftContentTv.setText(item.getContent());
			}
			viewHolder.leftTimeTv.setText(time);
			
		}else if(item.getFromTag()==FeedBackActivity.TO_TAG){
			viewHolder.rightLayout.setVisibility(View.VISIBLE);//右边（客户端）
			viewHolder.leftLayout.setVisibility(View.GONE);
			
			if(TextUtils.isEmpty(item.getContentType())){
				item.setContentType("1");
			}
			if(item.getContentType().equals("2")){//图片
				String imgUrl =  FeedbackViewModel.URL_HEAD+item.getContent();
				viewHolder.rightImgLayout.setVisibility(View.VISIBLE);
				viewHolder.rightContentTv.setVisibility(View.GONE);
				ImageLoader imageLoader = new ImageLoader(mContext);
				imageLoader.setImageViewBitmap(Constants.BOOKS_TEMP, Constants.BOOKS_TEMP_IMAGE,imgUrl,imgUrl,viewHolder.rightImgIv,R.drawable.book_default);
			}else{
				viewHolder.rightImgLayout.setVisibility(View.GONE);//文字
				viewHolder.rightContentTv.setVisibility(View.VISIBLE);
				viewHolder.rightContentTv.setText(item.getContent());
			}
			viewHolder.rightTimeTv.setText(time);
		}
	
		//放大显示图片
//		if(!TextUtils.isEmpty(item.getContentType())){
//			if(item.getContentType().equals("2")){
//				final String urlStr =  FeedbackViewModel.URL_HEAD+item.getContent();	
//				viewHolder.rightImgLayout.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						mAdaterAction.showImg(urlStr);
//					}
//				});
//			}
//		}
		
//		LogUtil.i("dhz","content " + item.getContent() +" fromTag "+ item.getFromTag());

		//显示评分项
		if(!TextUtils.isEmpty(item.getCloseTag())){
			if(item.getCloseTag().equals("1")){
				viewHolder.scoreTimeTv.setText(time);
				viewHolder.scoreLayout.setVisibility(View.VISIBLE);
				viewHolder.msgLayout.setVisibility(View.GONE);
				if(item.getScore()!=null){//评分过，不显示评分选项
					viewHolder.scoreValueTv.setVisibility(View.VISIBLE);
					viewHolder.scoreRadioGroup.setVisibility(View.GONE);
					if(item.getScore()==1){
						viewHolder.scoreValueTv.setText("满意");
					}else if(item.getScore()==2){
						viewHolder.scoreValueTv.setText("不满意");
					}
				}else{//显示评分选项
					viewHolder.scoreValueTv.setVisibility(View.GONE);
					viewHolder.scoreRadioGroup.setVisibility(View.VISIBLE);
				}
			}else{
				viewHolder.scoreLayout.setVisibility(View.GONE);
				viewHolder.msgLayout.setVisibility(View.VISIBLE);
			}
		}else{
			viewHolder.scoreLayout.setVisibility(View.GONE);
			viewHolder.msgLayout.setVisibility(View.VISIBLE);
		}
		
	
		
		viewHolder.wellScoreBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					mAdaterAction.selectScore(item.getFeedbackId(),1);
				}
			}
		});
		
		viewHolder.badScoreBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					mAdaterAction.selectScore(item.getFeedbackId(),2);
				}
			}
		});
		
		return convertView;
	}
	
	private class FeedbackViewHolder{
		private TextView leftTimeTv;
		private TextView leftContentTv;
		private ImageView leftImgIv;
		private LinearLayout leftLayout;
		private LinearLayout leftImgLayout;
		
		private TextView rightTimeTv;
		private TextView rightContentTv;
		private ImageView rightImgIv;
		private LinearLayout rightLayout;
		private LinearLayout rightImgLayout;
		
		private TextView scoreTimeTv;
		private RadioButton wellScoreBtn;
		private RadioButton badScoreBtn;
		private TextView scoreValueTv;
		private RadioGroup scoreRadioGroup;
		
		private LinearLayout scoreLayout;
		private LinearLayout msgLayout;
	}
	
	public static interface  FeedbackAdaterAction{
		public void selectScore(Integer feedbackId, int Score);
		public void showImg(String url);
	}
	
}
