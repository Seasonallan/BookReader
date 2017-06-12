package com.lectek.android.lereader.ui.basereader_leyue.digests;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Handler;
import android.text.ClipboardManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.application.MyAndroidApplication;
import com.lectek.android.lereader.lib.utils.DimensionsUtil;
import com.lectek.android.lereader.storage.dbase.digest.BookDigests;
import com.lectek.android.lereader.ui.basereader_leyue.BaseReaderActivityLeyue;
import com.lectek.android.lereader.ui.basereader_leyue.digests.AbsTextSelectHandler.TouchSelectRect;
import com.lectek.android.lereader.ui.basereader_leyue.digests.BookDigestsRemarksDialog.OnCloseDialogLisenter;
import com.lectek.android.lereader.utils.ToastUtil;


public class BookDigestsPopWin extends PopupWindow{
	public final static int VIEW_TYPE_MAGNIFIER = 1;
	public final static int VIEW_TYPE_MENU_1 = VIEW_TYPE_MAGNIFIER + 1;
	public final static int VIEW_TYPE_MENU_2 = VIEW_TYPE_MAGNIFIER + 2;
	private final static int SHOW_WIN_HIGHT = DimensionsUtil.dip2px(50, MyAndroidApplication.getInstance());//游标和view的距离，可依据需要调整
	
	private Activity mContext;
	private AbsTextSelectHandler mTextSelectHandler;
	private View mContentView;
	private View mParentView;
	private BookDigests mBookDigests;
	protected BookDigestsRemarksDialog mDialog;
	private View mViewMagnifier;
	private View mViewMenuOne;
	private View mViewMenuTwo;
	
	
	public BookDigestsPopWin(View contentView,View parentView, int width, int height,Activity context,AbsTextSelectHandler textSelectHandler){
		super(contentView,width, height);
		this.mParentView = parentView;
		this.mContext = context;
		this.mTextSelectHandler = textSelectHandler;
		mContentView = contentView;
		initView();

	}
	
	public void show(int type,float x, float y){
		show(type, x, y, null);
	}
	
	public void show(int type,float x, float y,Bitmap bitmap){
		int temp_x = 0;
		int temp_y = 0;
		int xy[] = new int [2];
		
		if(bitmap != null){
			int bitmapHight = bitmap.getHeight();
			int bitmapWidth = bitmap.getWidth();
			temp_x = (int)x - bitmapWidth/2;
			temp_y = (int)y - SHOW_WIN_HIGHT - bitmapHight;
			
			int[] location = new int[2];
			mParentView.getLocationInWindow(location );
			if(temp_y < location[1]){
				temp_y = (int)y + SHOW_WIN_HIGHT + bitmapHight;
			}
		}
		
		switch (type) {
			case VIEW_TYPE_MAGNIFIER:
				
				if(bitmap == null){
					dismiss();
					return;
				}
				TouchSelectRect tsr = mTextSelectHandler.getTouchSelectCursor();
				if(tsr != null){
					if(temp_y < 0 ){
						temp_y = (int)(y) + SHOW_WIN_HIGHT ;
					}
				}
				showMagnifier(bitmap);
				break;
				
			case VIEW_TYPE_MENU_1:
				showMenu(VIEW_TYPE_MENU_1);
				xy = countPosition_xy((int)x, (int)y, mParentView,VIEW_TYPE_MENU_1);
				temp_x = xy[0];
				temp_y = xy[1];
				break;
				
			case VIEW_TYPE_MENU_2:
				showMenu(VIEW_TYPE_MENU_2);
				xy = countPosition_xy((int)x, (int)y, mParentView,VIEW_TYPE_MENU_2);
				temp_x = xy[0];
				temp_y = xy[1];
				break;
				
			default:
				break;
		}
		if(this.isShowing()){
			update(temp_x, temp_y, -1, -1, false);
		}else{
			showAtLocation(mParentView, 0, temp_x, temp_y);
		}
	}
	
	private int[] countPosition_xy(int x, int y,View parentView,int type) {
		int xy[] = new int[2];
		Rect top = 	mTextSelectHandler.getSelectCursorRect(true);
		Rect down = mTextSelectHandler.getSelectCursorRect(false);
		DisplayMetrics  dm = new DisplayMetrics();    
	    mContext.getWindowManager().getDefaultDisplay().getMetrics(dm); 
	    int screenWidth = dm.widthPixels;
	    int screenHeight = dm.heightPixels; 
		
		if(top == null){
			xy[0] = x;
			xy[1] = y;
			return xy;
		}
		
		int height = 0;
		int width = 0;
		switch (type) {
		case VIEW_TYPE_MENU_1:
			height = mViewMenuOne.getHeight();
			width = mViewMenuOne.getWidth();
			break;
			
		case VIEW_TYPE_MENU_2:
			height = mViewMenuTwo.getHeight();	
			width = mViewMenuTwo.getWidth();
			break;


		default:
			break;
		}
		xy[0] = screenWidth / 2 - width / 2;
		xy[1] = top.centerY() - height - SHOW_WIN_HIGHT;
		
		int[] location = new int[2];
		parentView.getLocationInWindow(location );
		if(xy[1] >= location[1]){
			return xy;
		}

		
	    xy[1] = down.height() + down.centerY() + height + SHOW_WIN_HIGHT;
	    
		if(xy[1] <= screenHeight){
			xy[1] -= height;
			return xy;
		}
		
		xy[1] = screenHeight / 2 - height / 2;
		return xy;
	}

	@Override
	public void dismiss(){
		
		if(this.isShowing()){
			super.dismiss();
		}
		
	}
	
	private void showMagnifier(Bitmap bitmap){
		((ImageView) mContentView.findViewById(R.id.book_digests_view_magnifier_lv)).setImageBitmap(bitmap);
		setMeumVisible(VIEW_TYPE_MAGNIFIER);
	}
	
	private void showMenu(int type){
		setMeumVisible(type);
	}
	
	private void setMeumVisible(int type) {
		
		switch(type){
		case VIEW_TYPE_MAGNIFIER:
			mViewMenuOne.setVisibility(View.GONE);
			mViewMenuTwo.setVisibility(View.GONE);
			mViewMagnifier.setVisibility(View.VISIBLE);
			break;
			
		case VIEW_TYPE_MENU_1:
			mViewMenuOne.setVisibility(View.VISIBLE);
			mViewMenuTwo.setVisibility(View.GONE);
			mViewMagnifier.setVisibility(View.GONE);
			break;
			
		case VIEW_TYPE_MENU_2:
			mViewMenuOne.setVisibility(View.GONE);
			mViewMenuTwo.setVisibility(View.VISIBLE);
			mViewMagnifier.setVisibility(View.GONE);
			break;

		default:break;
		
		}
		
	}
	private View.OnClickListener choseListener=new OnClickListener() {
		
		@Override
		public void onClick(final View v) {
			BookDigests bookDigests = mTextSelectHandler.getCurrentBookDigests();
			ClipboardManager cm;
			int id = v.getId();
			if(id == R.id.copy){
				cm =(ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
				cm.setText(mTextSelectHandler.getData(mBookDigests));
				ToastUtil.showToast(mContext, R.string.copy_sucess);
				mTextSelectHandler.closeEdit();
			}else if(id == R.id.beizhu){
				mDialog=new BookDigestsRemarksDialog(mContext,R.style.remark_Dialog,mTextSelectHandler,mBookDigests);
				mDialog.show();
				mDialog.setOnCloseDialogLisenter(new OnCloseDialogLisenter(){

					@Override
					public void onCloseDialog(boolean isCloseDialog) {
						if(isCloseDialog){
							mDialog.dismiss();
						}
					}
				});
				mTextSelectHandler.closeEdit();
			}else if(id == R.id.delete){
				mTextSelectHandler.deleteBookDigests(mBookDigests);
				mTextSelectHandler.closeEdit();
			} else if(id == R.id.copy_){
				cm =(ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
				cm.setText(mTextSelectHandler.getData(bookDigests));
				mTextSelectHandler.setSelect(false);
				ToastUtil.showToast(mContext, R.string.copy_sucess);
			}else if(id == R.id.digest){
				bookDigests.setDate(new Date().getTime());
				bookDigests.setContent(mTextSelectHandler.getData(bookDigests));
				mTextSelectHandler.setSelect(false);
				mTextSelectHandler.createOrUpdateBookDigests(bookDigests);  
			}else if(id == R.id.beizhu_){
				mDialog=new BookDigestsRemarksDialog(mContext,R.style.remark_Dialog,mTextSelectHandler);
				mDialog.show();
				mDialog.setOnCloseDialogLisenter(new OnCloseDialogLisenter(){
					@Override
					public void onCloseDialog(boolean isCloseDialog) {
						if(isCloseDialog){
							mDialog.cancel();
						}
					}
				});
				mTextSelectHandler.setSelect(false);
			}else if (id == R.id.share_ || id == R.id.share){
                if (id == R.id.share){
                    mTextSelectHandler.closeEdit();
                }else{
                    mTextSelectHandler.setSelect(false);
                }
                if (mContext instanceof BaseReaderActivityLeyue){
                    ((BaseReaderActivityLeyue)mContext).onShareAction(null);
                }
            }
//			else if(id == R.id.share_){
//				String content_ = mTextSelectHandler.getData(bookDigests);
//				String contentName_ = bookDigests.getContentName();
//				String msg_ = bookDigests.getMsg();
//				String shareContent_ = mContext.getString(R.string.share_bookdigests_content, contentName_,content_);
//				String shareBookId_ = bookDigests.getContentId();
//				shareBookId_ = mContext.getString(R.string.share_bookdigests_book_id,shareBookId_);
//				if(!TextUtils.isEmpty(msg_)){
//					msg_ = mContext.getString(R.string.share_bookdigests_remark, msg_);
//					shareContent_ += msg_;
//				}
//				shareContent_ = CommonUtil.clipStr(shareContent_, shareBookId_);
//				CommonUtil.showShareOrRecommendDialog(mContext, shareContent_, contentName_, 0);
//				mTextSelectHandler.setSelect(false);
//			}
		}
	};

	private void initView(){
		mViewMenuOne = mContentView.findViewById(R.id.book_digests_meum1);
		mViewMenuTwo = mContentView.findViewById(R.id.book_digests_meum2);
		mViewMagnifier = mContentView.findViewById(R.id.book_digests_view_magnifier_lay);
		
		Resources res = mContext.getResources();
		String[] item = res.getStringArray(R.array.book_digests_menu_one);
		((Button) mContentView.findViewById(R.id.copy_)).setText(item[1]);
		((Button) mContentView.findViewById(R.id.digest)).setText(item[0]);
		((Button) mContentView.findViewById(R.id.beizhu_)).setText(item[2]);
		((Button) mContentView.findViewById(R.id.share_)).setText(item[3]);
		//((Button) mContentView.findViewById(R.id.share_)).setVisibility(View.GONE);
		
		((Button) mContentView.findViewById(R.id.copy_)).setOnClickListener(choseListener);
		((Button) mContentView.findViewById(R.id.digest)).setOnClickListener(choseListener);
		((Button) mContentView.findViewById(R.id.beizhu_)).setOnClickListener(choseListener);
		((Button) mContentView.findViewById(R.id.share_)).setOnClickListener(choseListener);
		
		item = res.getStringArray(R.array.book_digests_menu_two);
		((Button) mContentView.findViewById(R.id.copy)).setText(item[0]);
		((Button) mContentView.findViewById(R.id.delete)).setText(item[3]);
		((Button) mContentView.findViewById(R.id.beizhu)).setText(item[1]);
		((Button) mContentView.findViewById(R.id.share)).setText(item[2]);

		((Button) mContentView.findViewById(R.id.beizhu)).setOnClickListener(choseListener);
		((Button) mContentView.findViewById(R.id.copy)).setOnClickListener(choseListener);
		((Button) mContentView.findViewById(R.id.delete)).setOnClickListener(choseListener);
		((Button) mContentView.findViewById(R.id.share)).setOnClickListener(choseListener);
		//((Button) mContentView.findViewById(R.id.share)).setVisibility(View.GONE);
		
		
	}

	public BookDigests getmBookDigests() {
		return mBookDigests;
	}

	public void setmBookDigests(BookDigests mBookDigests) {
		this.mBookDigests = mBookDigests;
	}
	
	public void setmTextSelectHandler(AbsTextSelectHandler mTextSelectHandler) {
		this.mTextSelectHandler = mTextSelectHandler;
	}
	
}
