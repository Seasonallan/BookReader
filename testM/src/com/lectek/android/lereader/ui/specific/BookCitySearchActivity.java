package com.lectek.android.lereader.ui.specific;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.application.MyAndroidApplication;
import com.lectek.android.lereader.binding.model.bookCitySearch.BookCitySearchViewModelLeyue;
import com.lectek.android.lereader.binding.model.bookCitySearch.BookCitySearchViewModelLeyue.BookCitySearchViewUserAciton;
import com.lectek.android.lereader.lib.utils.DimensionsUtil;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.net.response.KeyWord;
import com.lectek.android.lereader.ui.common.BaseActivity;
import com.lectek.android.lereader.utils.CommonUtil;
import com.lectek.android.lereader.widgets.SlidingView.ISlideGuestureHandler;
/**
 * 书城搜索
 * @author Administrator
 *
 */
public class BookCitySearchActivity extends BaseActivity implements BookCitySearchViewUserAciton,ISlideGuestureHandler{

	public static final String TAG = BookCitySearchActivity.class.getSimpleName();
	private BookCitySearchViewModelLeyue mBookCitySearchViewModelLeyue;
	private LinearLayout keyword_container;
	private ArrayList<TextView> textViewList;
	private int screenWidth;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitleBarEnabled(false);
	}

	@Override
	protected void onDestroy() {
		mBookCitySearchViewModelLeyue.onRelease();
		super.onDestroy();
	}

	@Override
	public void onPause() {
		super.onPause();
		mBookCitySearchViewModelLeyue.onRelease();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mBookCitySearchViewModelLeyue.onStart();
	}
	
	@Override
	protected View newContentView(Bundle savedInstanceState) {
		mBookCitySearchViewModelLeyue = new BookCitySearchViewModelLeyue(this, this, this);
		View view = bindView(R.layout.bookcity_search_lay, mBookCitySearchViewModelLeyue);
		keyword_container = (LinearLayout) view.findViewById(R.id.keyword_container);
		textViewList = new ArrayList<TextView>();
		screenWidth = CommonUtil.getScreenWidth(this_) - DimensionsUtil.dip2px(20, this);
		return view;
	}
	
	@Override
	public void exceptionHandle(String str) {
		setTipImageResource(R.drawable.icon_request_fail_tip);
	}
	
	@Override
	public void optToast(String str) {
		
	}
	
	private TextView getTextView(String keyWord){
		TextView textView = (TextView) View.inflate(this_, R.layout.bookcity_hot_search_keywork_view, null);
		textView.setText(keyWord);
		return textView;
	}
	
	
	@Override
	public void loadKeyWordOver(ArrayList<KeyWord> keyWordList) {
		keyword_container.removeAllViews();
		for(int i=0;i< (keyWordList.size()>10? 10:keyWordList.size());){
			LinearLayout ll=new LinearLayout(this_);
			ll.setHorizontalGravity(Gravity.LEFT);
			ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
			
			int tempLength = 0;
			while(true && i< (keyWordList.size()>10? 10:keyWordList.size())){
				TextView textView = getTextView(keyWordList.get(i).getSearchContent());
				float textLength = textView.getPaint().measureText(keyWordList.get(i).getSearchContent())+DimensionsUtil.dip2px(20, this);
				tempLength +=textLength;
				if(tempLength < screenWidth){
					textView.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(this_, SearchListActivity.class);
							intent.putExtra(SearchListActivity.SEARCH_KEYWORD, ((TextView)v).getText());
							startActivity(intent);
						}
					});
					
					ll.addView(textView);
					LinearLayout spaceview=new LinearLayout(this_);
					spaceview.setLayoutParams(new LinearLayout.LayoutParams(20,LinearLayout.LayoutParams.WRAP_CONTENT));
					ll.addView(spaceview);
					tempLength +=20;
					i++;
					continue;
				}
				keyword_container.addView(ll);
				LinearLayout spaceview=new LinearLayout(this_);
				spaceview.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,20));
				keyword_container.addView(spaceview);
				break;
			}
		}
	}

	@Override
	public boolean consumeHorizontalSlide(float x, float y, float deltaX) {
		// TODO Auto-generated method stub
//		LogUtil.e(Tag, "consumeHorizontalSlide");
		return false;
	}

}
