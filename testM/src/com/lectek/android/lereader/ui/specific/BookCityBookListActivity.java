package com.lectek.android.lereader.ui.specific;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.bookCity.BookCityListViewModelLeyue;
import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.ui.common.BaseActivity;

/**
 * 推荐书籍二级界面
 * @author Administrator
 *
 */
public class BookCityBookListActivity extends BaseActivity {

	public static String BOOKCITY_LIST_ACTIVITY_COLUMN = "BOOKCITY_LIST_ACTIVITY_COLUMN";
	public static int HEAVY_RECOMMEND_BOOKS_COLUMN = 100001;	//重磅推荐
	public static int NEW_BOOKS_RECOMMEND_COLUMN = 100002;		//新书抢先看
	public static int ALL_READ_BOOKS_RECOMMEND_COLUMN = 100009;	//大家都在看
	public static int LATEST_PRICE_BOOKS_RECOMMEND_COLUMN = 100003;		//最新特价
	public static int FREE_BOOKS_RECOMMEND_COLUMN = 100004;		//免费专区

	private BookCityListViewModelLeyue mBookCityListViewModelLeyue;
	public static void openActivity(Context context, int columnId){
		Intent intent = new Intent(context, BookCityBookListActivity.class);
        intent.putExtra(BookCityBookListActivity.BOOKCITY_LIST_ACTIVITY_COLUMN, columnId);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitleBarEnabled(!getIntent().getBooleanExtra(BookCityActivityGroup.Extra_Embed_Activity_Hide_Title_Bar, false));
		mBookCityListViewModelLeyue.onStart();
	}
	
	@Override
	protected View newContentView(Bundle savedInstanceState) {
		Intent intent = getIntent();
		int column = intent.getIntExtra(BOOKCITY_LIST_ACTIVITY_COLUMN, HEAVY_RECOMMEND_BOOKS_COLUMN);
		mBookCityListViewModelLeyue = new BookCityListViewModelLeyue(this_, this,column);
		return bindTempView(R.layout.bookcity_common_listview, mBookCityListViewModelLeyue);
	}

}
