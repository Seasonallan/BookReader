package com.lectek.android.lereader.data;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.permanent.DownloadAPI;
import com.lectek.android.lereader.widgets.RoundProgressBar;
import com.lectek.android.lereader.widgets.ShelfFileView;
import com.lectek.android.lereader.widgets.drag.DragController;
import com.lectek.android.lereader.widgets.drag.DragGridView;
import com.lectek.android.lereader.widgets.drag.ICallBack;
import com.lectek.android.lereader.widgets.drag.IDragDatas;
import com.lectek.android.lereader.widgets.drag.IDragItemView;

import java.text.DecimalFormat;

/** 
 *  
 * @author laijp
 * @date 2014-7-7
 * @email 451360508@qq.com
 */
public class DragItemView extends LinearLayout implements IDragItemView {

	
	private ImageView mBgView;
    private ImageView mShadowView;
	public DragItemView(Context context) {
		super(context);
		init();
	}
	
	public DragItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
    Drawable redot;
    ViewHolder holder = new ViewHolder();
	private void init(){
		LayoutInflater.from(getContext()).inflate(R.layout.book_shelf_item_layout, this);
		mBgView = (ImageView) findViewById(R.id.imageView_bg_file);
        mShadowView = (ImageView) findViewById(R.id.imageView_bg);
        View convertView = this;
       // holder.shadowView = convertView.findViewById(R.id.book_shelf_item_book_shadow);
        holder.book_img_iv = (ShelfFileView) convertView.findViewById(R.id.book_img_iv);
        holder.fl_item_download_state = (RelativeLayout) convertView.findViewById(R.id.fl_item_download_state);
        holder.tv_download_state_value = (TextView) convertView.findViewById(R.id.tv_download_state_value);
        holder.tv_book_shelf_item_name = (TextView) convertView.findViewById(R.id.tv_book_shelf_item_name);
        holder.tv_book_shelf_item_reader = (TextView) convertView.findViewById(R.id.tv_book_shelf_item_reader);
        holder.pb_download = (RoundProgressBar) convertView.findViewById(R.id.pb_download);
        holder.iv_delete = convertView.findViewById(R.id.imageView_del);
        holder.iv_online_book_tip = (ImageView) convertView.findViewById(R.id.imageView_online);
        holder.iv_book_shelf_item_name_dot = (ImageView) convertView.findViewById(R.id.iv_book_shelf_item_name_dot);
        redot = getResources().getDrawable(R.drawable.icon_redot);
    }
	private BookShelfItem mData;

    public final class ViewHolder{
       // public View shadowView;
        public ShelfFileView book_img_iv;
        public RelativeLayout fl_item_download_state;
        public ImageView iv_download_state;
        public TextView tv_download_state_value;
        public ImageView iv_online_book_tip;
        public TextView tv_book_shelf_item_name;
        public ImageView iv_book_shelf_item_name_dot;
        public TextView tv_book_shelf_item_reader;
        public RoundProgressBar pb_download;
        private View iv_delete;
        
    }
	public void fillAdapter(BookShelfItem downloadInfo){
		this.mData = downloadInfo;
        this.isFileOpen = false;
//        this.isAnimating = false;

		//findViewById(R.id.imageView_del).setOnClickListener(this);
        holder.iv_online_book_tip.setVisibility(View.GONE);
        holder.fl_item_download_state.setVisibility(View.GONE);
        mBgView.clearAnimation();
        if (downloadInfo.isEmpty){
            holder.iv_delete.setVisibility(View.GONE);
            mShadowView.setVisibility(View.INVISIBLE);
            mBgView.setBackgroundResource(R.drawable.btn_jia);
            holder.book_img_iv.setShelfItem(null);
            mBgView.setVisibility(View.VISIBLE);
            holder.iv_book_shelf_item_name_dot.setVisibility(View.INVISIBLE);
            holder.tv_book_shelf_item_name.setText("");
            holder.tv_book_shelf_item_reader.setText("");
            if (DragController.getInstance().isDragWorking()){
                mBgView.setVisibility(View.INVISIBLE);
            }
        }else{
            mShadowView.setVisibility(View.VISIBLE);
            holder.iv_delete.setVisibility(DragController.getInstance().isDragWorking() ? View.VISIBLE : View.GONE);
            holder.iv_delete.setSelected(mData.isDelSelect);
            holder.book_img_iv.setShelfItem(downloadInfo);
            if (downloadInfo.isFile){
                holder.iv_book_shelf_item_name_dot.setVisibility(View.INVISIBLE);
                mShadowView.setBackgroundColor(0x00000000);
                mBgView.setVisibility(View.VISIBLE);
                if (downloadInfo.mGroupMessage != null){
                    holder.tv_book_shelf_item_name.setText(downloadInfo.mGroupMessage.groupName);
                    int num = downloadInfo.mItems.size();
                    String bookNum = getContext().getResources().getString(R.string.bookshelf_item_reader_group_num, num + "");
                    holder.tv_book_shelf_item_reader.setText(bookNum);
                }else{
                    holder.tv_book_shelf_item_name.setText("");
                }
            }else{
                //   holder.shadowView.setBackgroundResource(R.drawable.icon_bookshelf_item_shadow_bg);
                holder.tv_book_shelf_item_name.setText(downloadInfo.mDownLoadInfo.contentName + "");
                if(downloadInfo.isReaded()){
                    //已读
                    float percent = downloadInfo.getReadPercent() * 100;
                    DecimalFormat df = new DecimalFormat("#.##");
                    //int num = (int) (downloadInfo.getReadPercent() * 100);	//已读进度百分之几
                    String readPercentStr = getContext().getResources().getString(R.string.bookshelf_item_reader_readed_percent, df.format(percent) + "%");
                    holder.tv_book_shelf_item_reader.setText(readPercentStr);
                    holder.iv_book_shelf_item_name_dot.setVisibility(View.INVISIBLE);
                }else{
                    //未读
                    String unReadTip = getContext().getResources().getString(R.string.bookshelf_item_reader_unread_tip);
                    holder.tv_book_shelf_item_reader.setText(unReadTip);
                    holder.iv_book_shelf_item_name_dot.setVisibility(View.VISIBLE);
                }
                mBgView.setVisibility(View.INVISIBLE);
                mShadowView.setBackgroundResource(R.drawable.icon_bookshelf_item_shadow_bg);

                // holder.book_img_iv.setImageUrl(downloadInfo.mDownLoadInfo.logoUrl);
                if(downloadInfo.mDownLoadInfo != null && downloadInfo.mDownLoadInfo.state != DownloadAPI.STATE_FINISH
                        && downloadInfo.mDownLoadInfo.state != DownloadAPI.STATE_FAIL
                        && downloadInfo.mDownLoadInfo.state != DownloadAPI.STATE_ONLINE){
                    holder.fl_item_download_state.setVisibility(View.VISIBLE);
                    if (downloadInfo.mDownLoadInfo.state == DownloadAPI.STATE_START
                            || downloadInfo.mDownLoadInfo.state == DownloadAPI.STATE_STARTING) {
//                holder.iv_download_state.setImageResource(R.drawable.download_pause);
                    } else {
//                holder.iv_download_state.setImageResource(R.drawable.download_start);
                    }
                    long totalBytes = downloadInfo.mDownLoadInfo.size;
                    long currentBytes = 0;
                    int progressAmount = 0;
                    currentBytes = downloadInfo.mDownLoadInfo.current_size;
                    if (totalBytes == 0) {
                        progressAmount = 0;
                    } else {
                        progressAmount = (int) (currentBytes * 100 / totalBytes);
                    }
                    holder.pb_download.setProgress(progressAmount);
                    holder.tv_download_state_value.setText(progressAmount+" %");
                }
                if(downloadInfo.mDownLoadInfo != null && downloadInfo.mDownLoadInfo.state == DownloadAPI.STATE_ONLINE){
                    holder.iv_online_book_tip.setVisibility(View.VISIBLE);
                }
            }
        }

	}

//	@Override
//	public boolean point2Position(int x, int y) {
//		Rect rect = new Rect();
//		getHitRect(rect);
//		int left = rect.left;
//		int right = rect.right;
//		if (left + (right - left)/4 < x && left + (right - left)*3/4 > x) {
//			int top = rect.top;
//			int bottom = rect.bottom;
//			if (top + (bottom - top)/4 < y && top + (bottom - top)*3/4 > y) {
//				return false;
//			}
//		}
//		return true;
//	}

	private boolean isFileOpen =false;
//	private boolean isAnimating = false;
	@Override
	public boolean openFile() {
		if (isFileOpen) {
			return true;
		}
//		if (isAnimating) {
//			return false;
//		}
//        isAnimating = true;
        if (!mData.isFile){
            mBgView.setVisibility(View.VISIBLE);
        }
//        if(Build.VERSION.SDK_INT == 10) {
//            isFileOpen = true;
//            isAnimating = false;
//            return false;
//        }
        isFileOpen = true;
//        isAnimating = true;
        
        if(mBgView.getAnimation() != null) {
        	mBgView.getAnimation().cancel();
        }
		ScaleAnimation scaleAnimation = new ScaleAnimation(1, size, 1, size, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		scaleAnimation.setDuration(DragGridView.DURATION);
//        scaleAnimation.setFillEnabled(true);
		scaleAnimation.setFillBefore(true);
		scaleAnimation.setFillAfter(true);
		scaleAnimation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isFileOpen = true;
//                isAnimating = false;
            }
        });
		mBgView.startAnimation(scaleAnimation);
		return false;
	}

    private float size = 1.1f;
	@Override
	public boolean closeFile() {
		if (!isFileOpen) {
			return true;
		}
//		if (isAnimating) {
//			return false;
//		}
//        isAnimating = true;
//        if(Build.VERSION.SDK_INT == 10) {
//            isAnimating = false;
//            isFileOpen =false;
//            if (!mData.isFile){
//                mBgView.setVisibility(View.INVISIBLE);
//            }
//            return false;
//        }
        if(mBgView.getAnimation() != null) {
        	mBgView.getAnimation().cancel();
        }
        
        isFileOpen =false;
        
        ScaleAnimation scaleAnimation = new ScaleAnimation(size, 1, size, 1,  Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        scaleAnimation.setFillEnabled(false);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setFillBefore(true);
		scaleAnimation.setDuration(DragGridView.DURATION);
		scaleAnimation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {

			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
//				isAnimating = false;
				isFileOpen =false;
                if (!mData.isFile){
                    mBgView.setVisibility(View.INVISIBLE);
                }
			}
		});
		mBgView.startAnimation(scaleAnimation);
		return false;
	}

	@Override
	public boolean isFileOpen() {
		return isFileOpen;
	}

    @Override
	public <T extends IDragDatas> void onItemAdded(T item, final ICallBack callBack) {
		closeFile();
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				callBack.onResult();
			}
		}, DragGridView.DURATION);
	}

    @Override
    public Bitmap createBitmap() {
        destroyDrawingCache();
        setDrawingCacheEnabled(true);
        setDrawingCacheBackgroundColor(0x00000000);
        return Bitmap.createBitmap(getDrawingCache(true));
    }

}




















