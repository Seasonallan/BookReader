package com.lectek.android.lereader.ui.specific;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.bookCityClassify.BookCityClassifyDetailViewModelLeyue;
import com.lectek.android.lereader.ui.common.BaseActivity;
/**
 * 分类二级页面
 * @author Administrator
 *
 */
public class BookCityClassifyDetailActivity extends BaseActivity {

	public static String BOOKCITY_CLASSIFY_DETAIL_TYPE = "BOOKCITY_CLASSIFY_DETAIL_TYPE";
	
	private BookCityClassifyDetailViewModelLeyue mBookCityClassifyDetailViewModelLeyue;
	public static void openActivity(Context context, int bookType){
		Intent intent = new Intent(context, BookCityClassifyDetailActivity.class);
        intent.putExtra(BookCityClassifyDetailActivity.BOOKCITY_CLASSIFY_DETAIL_TYPE, bookType);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitleBarEnabled(!getIntent().getBooleanExtra(BookCityActivityGroup.Extra_Embed_Activity_Hide_Title_Bar, false));
		mBookCityClassifyDetailViewModelLeyue.onStart();
	}

	@Override
	protected View newContentView(Bundle savedInstanceState) {
		int bookType = getIntent().getIntExtra(BOOKCITY_CLASSIFY_DETAIL_TYPE, 0);
		
		mBookCityClassifyDetailViewModelLeyue = new BookCityClassifyDetailViewModelLeyue(this_, this, bookType);
		return bindTempView(R.layout.bookcity_common_listview, mBookCityClassifyDetailViewModelLeyue);
	}
	
	/*@Override
	public boolean isPullEnabled() {
		// TODO Auto-generated method stub
		return true;
	}*/

}
