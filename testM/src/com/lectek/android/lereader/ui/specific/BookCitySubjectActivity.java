package com.lectek.android.lereader.ui.specific;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.bookCity.BookCitySubjectViewModelLeyue;
import com.lectek.android.lereader.ui.common.BaseActivity;

public class BookCitySubjectActivity extends BaseActivity {

	public static final String BOOKCITY_SUBJECT_ID = "BOOKCITY_SUBJECT_ID";
	private BookCitySubjectViewModelLeyue mBookCitySubjectViewModelLeyue;

	public static void openActivity(Context context, int subjectId){
		Intent intent = new Intent(context, BookCitySubjectActivity.class);
        intent.putExtra(BookCitySubjectActivity.BOOKCITY_SUBJECT_ID, subjectId);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitleBarEnabled(!getIntent().getBooleanExtra(BookCityActivityGroup.Extra_Embed_Activity_Hide_Title_Bar, false));
		mBookCitySubjectViewModelLeyue.onStart();
	}

	@Override
	protected View newContentView(Bundle savedInstanceState) {
		int subjectId = getIntent().getIntExtra(BOOKCITY_SUBJECT_ID, 0);
		mBookCitySubjectViewModelLeyue = new BookCitySubjectViewModelLeyue(this_, this, subjectId);
		return bindView(R.layout.bookcity_subject_activity_lay, this, mBookCitySubjectViewModelLeyue);
	}

}
