package com.lectek.android.lereader.ui.basereader_leyue.digests;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.data.BookDigestColorItem;
import com.lectek.android.lereader.storage.dbase.digest.BookDigests;
import com.lectek.android.lereader.utils.CommonUtil;

public class BookDigestsRemarksDialog extends Dialog{
	public final static int YELLOW = Color.parseColor("#e6ef50") + 0x66000000;
	public final static int ORANGE = Color.parseColor("#ff9900") + 0x66000000;
	public final static int GREEN = Color.parseColor("#66cc00") + 0x66000000;
	public final static int BLUE = Color.parseColor("#6de8d5") + 0x66000000;
	public final static int PINK = Color.parseColor("#f898f8") + 0x66000000;
	public final static int HSPAC = 5;
	public final static int LAYOUT_MARGIN = 5;
	private EditText mRemarks_et;
	private AbsTextSelectHandler mTextSelectHandler;
	private Activity mContext;
	private BookDigests mBookDigests;
	private OnCloseDialogLisenter mOnCloseDialogLisenter;
	private int mColor = -1;
	
	
	public BookDigestsRemarksDialog(Activity context,int theme, AbsTextSelectHandler textSelectHandler) {
		super(context,theme);
		this.mTextSelectHandler = textSelectHandler;
		this.mContext = context;
		
	}
	
	public BookDigestsRemarksDialog(Activity context, int theme,AbsTextSelectHandler textSelectHandler, BookDigests bookDigests) {
		super(context,theme);
		this.mTextSelectHandler = textSelectHandler;
		this.mContext = context;
		this.mBookDigests = bookDigests;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_edit_bookdigest_remarks);
//		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		mRemarks_et = (EditText) findViewById(R.id.remarks_edit);
		EditText content = (EditText) findViewById(R.id.digests_content);
		if(mBookDigests == null){
			
			mBookDigests = mTextSelectHandler.getCurrentBookDigests();
		}
		if(mBookDigests.getContent() == null){
			
			mBookDigests.setContent(mTextSelectHandler.getData(mBookDigests));
		}
		content.setText(mBookDigests.getContent());
		
		if(mBookDigests.getMsg() != null){
			
			mRemarks_et.setText(mBookDigests.getMsg());
		}
		preColorView();
	
	}
	
	private ArrayList<BookDigestColorItem> getItems(){
		ArrayList<BookDigestColorItem> items = new ArrayList<BookDigestColorItem>();
		items.add(new BookDigestColorItem(YELLOW,-1,YELLOW == mBookDigests.getBGColor()));
		items.add(new BookDigestColorItem(ORANGE,-1,ORANGE == mBookDigests.getBGColor()));
		items.add(new BookDigestColorItem(GREEN,-1,GREEN == mBookDigests.getBGColor()));
		items.add(new BookDigestColorItem(BLUE,-1,BLUE == mBookDigests.getBGColor()));
		items.add(new BookDigestColorItem(PINK,-1,PINK == mBookDigests.getBGColor()));
		return items;
		
		
	}
	private void preColorView() {
		
		ArrayList<BookDigestColorItem> bookDigestColorItems = getItems();
		final BookDigestColorItemAdapter adapter = new BookDigestColorItemAdapter(mContext, bookDigestColorItems);
		GridView gridView = (GridView) findViewById(R.id.color_gv);
		gridView.setHorizontalSpacing(CommonUtil.convertDipOrPx(mContext, HSPAC));
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				adapter.setSeleted(position);
				BookDigestColorItem item = (BookDigestColorItem) parent.getItemAtPosition(position);
				mColor = item.id;
			}
		});
		 
		Button leftButton = (Button) findViewById(R.id.remarks_save_btn);
		leftButton.setOnClickListener(choseListener);
		Button rightButton = (Button) findViewById(R.id.remarks_cancel_btn);
		rightButton.setOnClickListener(choseListener);
		
		if(Build.VERSION.SDK_INT >= 14){//4.0
			String tempStr = leftButton.getText().toString();
			int tempId = leftButton.getId();
			leftButton.setText(rightButton.getText());
			leftButton.setId(rightButton.getId());
			rightButton.setText(tempStr);
			rightButton.setId(tempId);
		}
	}
	
	private View.OnClickListener choseListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			if(id == R.id.remarks_save_btn){
				mBookDigests.setMsg(mRemarks_et.getText().toString());
				if(mColor != -1){
				mBookDigests.setBGColor(mColor);
				}
				if(mTextSelectHandler.isSelect()){
					mTextSelectHandler.setSelect(false);
				}
				mBookDigests.setDate(new Date().getTime()); 
				mTextSelectHandler.createOrUpdateBookDigests(mBookDigests); 
				closeDialog(true);
			}else if(id == R.id.remarks_cancel_btn){
				closeDialog(true);
			}
		}
			
	};

	public void setOnCloseDialogLisenter(OnCloseDialogLisenter onCloseDialog){
		   mOnCloseDialogLisenter = onCloseDialog;
	    }
	    private void closeDialog(boolean isCloseDialog){
	    	if(mOnCloseDialogLisenter != null){
	    		
	    		mOnCloseDialogLisenter.onCloseDialog(isCloseDialog);
	    	}
	    }
	    public interface OnCloseDialogLisenter{
	    	public void onCloseDialog(boolean isCloseDialog);
	    }
}
