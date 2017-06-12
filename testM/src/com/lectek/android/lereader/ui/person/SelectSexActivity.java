package com.lectek.android.lereader.ui.person;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.widgets.wheel.WheelAdapter;
import com.lectek.android.lereader.widgets.wheel.WheelView;

/**
 * 选择年龄Activity
 * 
 * @author yangwq
 * @date 2014年7月18日
 * @email 57890940@qq.com
 */
public class SelectSexActivity extends Activity implements OnClickListener {

	private TextView tv_cancel, tv_save;
	private WheelView wheelView;
	private Intent intent;
	private WheelAdapter adapter;
	private ArrayList<String> dataList;
	
	public static final String EXTRA_SEX = "extra_sex"; 
	public static final int RESULT_CODE_SELECT_OK = 325;
	private static final int[] SEX_RES_ID = {R.string.person_info_sex_boy, R.string.person_info_sex_girl, R.string.person_info_sex_secert};
	private static final int MAX = Integer.MAX_VALUE;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_pop_select_sex_layout);
		getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		initView();
		initData();
	}
	

	// 实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}
	
	private void initView(){
		tv_cancel = (TextView) findViewById(R.id.tv_cancel);
		tv_cancel.setOnClickListener(this);
		tv_save = (TextView) findViewById(R.id.tv_save);
		tv_save.setOnClickListener(this);
		wheelView = (WheelView) findViewById(R.id.wv_sex);
	}
	
	private void initData(){
		
		dataList = new ArrayList<String>();
		for (int i = 0; i < SEX_RES_ID.length; i++) {
			String temp = getResources().getString(SEX_RES_ID[i]);
			dataList.add(temp);
		}
		adapter = new WheelAdapter() {
			
			@Override
			public int getMaximumLength() {
				return MAX;
			}
			
			@Override
			public int getItemsCount() {
				return SEX_RES_ID.length;
			}
			
			@Override
			public String getItem(int index) {
				return dataList.get(index);
			}
		};
		wheelView.setAdapter(adapter);
		initWheelViewItem();
	}
	
	private void initWheelViewItem(){
		intent = getIntent();
		String sexStr = intent.getStringExtra(EXTRA_SEX);
		for (int i = 0; i < SEX_RES_ID.length; i++) {
			String temp = getResources().getString(SEX_RES_ID[i]);
			if(sexStr.equals(temp)){
				wheelView.setCurrentItem(i);
				break;
			}
		}
	}


	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_cancel:
			finish();
			break;
		case R.id.tv_save:
			int currentItem = wheelView.getCurrentItem();
			String sexStr = getResources().getString(SEX_RES_ID[currentItem]);
			Intent intent = new Intent();
			intent.putExtra(EXTRA_SEX, sexStr);
			setResult(RESULT_CODE_SELECT_OK, intent);
			finish();
			break;
		default:
			break;
		}

	}

}
