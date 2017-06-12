package com.lectek.android.lereader.ui.specific;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.animation.OpenBookAnimManagement;
import com.lectek.android.lereader.binding.model.BaseLoadDataModel.ILoadDataCallBack;
import com.lectek.android.lereader.binding.model.bookShelf.BookShelfModelLeyue;
import com.lectek.android.lereader.binding.model.bookShelf.BookShelfRecentReadModelLeyue;
import com.lectek.android.lereader.binding.model.bookShelf.BookShelfViewModelLeyue.ActionbarHandler;
import com.lectek.android.lereader.binding.model.markgroup.AddGroupModel;
import com.lectek.android.lereader.binding.model.markgroup.DelGroupModel;
import com.lectek.android.lereader.binding.model.markgroup.SimpeLoadDataCallBack;
import com.lectek.android.lereader.binding.model.markgroup.UpdateGroupModel;
import com.lectek.android.lereader.binding.model.markgroup.UpdateGroupNameModel;
import com.lectek.android.lereader.data.BookShelfItem;
import com.lectek.android.lereader.data.DragItemView;
import com.lectek.android.lereader.download.DownloadHttpEngine;
import com.lectek.android.lereader.lib.utils.ApnUtil;
import com.lectek.android.lereader.lib.utils.FileUtil;
import com.lectek.android.lereader.net.response.DownloadInfo;
import com.lectek.android.lereader.permanent.AppBroadcast;
import com.lectek.android.lereader.permanent.DownloadAPI;
import com.lectek.android.lereader.presenter.DownloadPresenterLeyue;
import com.lectek.android.lereader.storage.dbase.GroupMessage;
import com.lectek.android.lereader.storage.dbase.mark.BookMark;
import com.lectek.android.lereader.storage.dbase.mark.BookMarkDB;
import com.lectek.android.lereader.storage.dbase.util.GroupInfoDB;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.ILoadDialog;
import com.lectek.android.lereader.ui.basereader_leyue.BaseReaderActivityLeyue;
import com.lectek.android.lereader.ui.basereader_leyue.Book;
import com.lectek.android.lereader.ui.common.BaseNetPanelView;
import com.lectek.android.lereader.ui.specific.BookShelfActivity.BookShelfAction;
import com.lectek.android.lereader.utils.CommonUtil;
import com.lectek.android.lereader.utils.DialogUtil;
import com.lectek.android.lereader.utils.DialogUtil.CancelListener;
import com.lectek.android.lereader.utils.DialogUtil.ConfirmListener;
import com.lectek.android.lereader.utils.ToastUtil;
import com.lectek.android.lereader.widgets.ShelfFileView;
import com.lectek.android.lereader.widgets.drag.DragAdapter;
import com.lectek.android.lereader.widgets.drag.DragController;
import com.lectek.android.lereader.widgets.drag.DragGridView;
import com.lectek.android.lereader.widgets.drag.DragLayer;
import com.lectek.android.lereader.widgets.drag.DragScrollView;
import com.lectek.android.lereader.widgets.drag.IDragItemView;
import com.lectek.android.lereader.widgets.drag.IDragListener;
import com.lectek.android.lereader.widgets.drag.OpenFolder;
import com.lectek.android.widget.AsyncImageView;
import com.lectek.android.widget.ViewPagerTabHost.ViewPagerChild;
import com.umeng.analytics.MobclickAgent;

/**
 * 书架主界面
 * 
 * @author yangwq
 * @date 2014年7月8日
 * @email 57890940@qq.com
 */
public class BookShelfViewNotBinding extends BaseNetPanelView implements ViewPagerChild,OnClickListener,ILoadDataCallBack, IDragListener {
	
	private final String TAG = BookShelfView.class.getSimpleName();
	
	private boolean mCanScroll;

	protected ActionbarHandler mActionbarHandler;
	private TextView rlNoDataLayout;
	private RelativeLayout fourth_lastest_read_book, third_lastest_read_book, second_lastest_read_book, first_lastest_read_book;
//	lastestReadBookLayout
    private View rl_bottom_layout;

	private AsyncImageView iv_first_book_img, iv_second_book_img, iv_third_book_img, iv_fourth_book_img;
    private ImageView iv_first_book_img_online, iv_second_book_img_online, iv_third_book_img_online, iv_fourth_book_img_online;
	
    private DragLayer mDragLayer;

    private ScrollView mOutScrollView;

	private DragGridView<BookShelfItem> bookShelfGrid;
	
	private BookShelfGridAdapter bookShelfGridAdapter;

	private BookShelfModelLeyue mBookShelfModel;
	
	private BookShelfRecentReadModelLeyue mBookShelfRecentReadModelLeyue;
	
	private DonwloadListener mDonwloadListener;
	
	private ArrayList<BookShelfItem> mBookShelfList;
	
	private ArrayList<BookShelfItem> mRecentBookShelfItems;
	
	private boolean isEditMode = false;	//编辑状态
	
	private static final int MSG_WHAT_ON_PROGRESS_CHANGE = 0 ;
	private static final int MSG_WHAT_ON_STATE_CHANGE = 1 ;
	
	private static final int CLEAR_TIP = 0 ;
	private static final int DELETE_TIP = 1 ;
	
	private View title_bar;
	private View sort_title_bar;
	private TextView tv_sort_finish;
	private BookShelfActivity _this;
	private static final int[] RECENT_BOOK_DELETE_IDS = {R.id.iv_first_book_delete, R.id.iv_second_book_delete, R.id.iv_third_book_delete, R.id.iv_fourth_book_delete};
	private ImageView iv_first_book_delete, iv_second_book_delete, iv_third_book_delete, iv_fourth_book_delete;
	
	public BookShelfViewNotBinding(BookShelfActivity context) {
		super(context);
		_this = context;
		_this.setBookShelfAction(mBookShelfAction);
		mBookShelfModel = new BookShelfModelLeyue();
		mBookShelfModel.addCallBack(this);
		mBookShelfRecentReadModelLeyue = new BookShelfRecentReadModelLeyue();
		mBookShelfRecentReadModelLeyue.addCallBack(this);
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			if(what == MSG_WHAT_ON_PROGRESS_CHANGE){//下载进度改变
				Intent intent = (Intent) msg.obj;
				long id = intent.getLongExtra(DownloadAPI.BROAD_CAST_DATA_KEY_ID, -1);
				long currentSize  = intent.getLongExtra(DownloadAPI.BROAD_CAST_DATA_KEY_FILE_BYTE_CURRENT_SIZE, -1);
				long size = intent.getLongExtra(DownloadAPI.BROAD_CAST_DATA_KEY_FILE_BYTE_SIZE, -1);
				
				System.out.println("id:"+id+" currentSize:"+currentSize+" size:"+size);
				if (bookShelfGridAdapter != null){
                    BookShelfItem item = bookShelfGridAdapter.getItemById(id);
                    if (item != null){
                        item.mDownLoadInfo.current_size = currentSize;
                        item.mDownLoadInfo.size = size;
                        bookShelfGridAdapter.notifyDataSetChanged();
                    }
                }
			}else if(what == MSG_WHAT_ON_STATE_CHANGE){//下载状态改变
				Intent intent = (Intent) msg.obj;
				long id = intent.getLongExtra(DownloadAPI.BROAD_CAST_DATA_KEY_ID, -1);
				int state = intent.getIntExtra(DownloadAPI.BROAD_CAST_DATA_KEY_STATE, -1);
				System.out.println("id:"+id+" state:"+state);
				boolean isNewDate = true;
                if (bookShelfGridAdapter != null){
                    BookShelfItem item = bookShelfGridAdapter.getItemById(id);
                    if (item != null){
                        item.mDownLoadInfo.state = state;
                        bookShelfGridAdapter.notifyDataSetChanged();
                        isNewDate = false;
                    }
                }
				if(isNewDate){
					onStartLoadData(true);
				}
			}
		}
	};

	@Override
	public void onCreate() {
        DragController.getInstance().clear();
		MobclickAgent.onPageStart(TAG);
		setFocusable(true);
		LayoutInflater.from(getContext()).inflate(R.layout.book_shelf_layout, this);
		registerReceiver();
		initView();
		initData();
        DragController.getInstance().registerDragListener(this);
	}

	@Override
	public void onDestroy() {
		MobclickAgent.onPageEnd(TAG);
		unRegisterReceiver();
		DragController.getInstance().unRegisterDragListener(this);
	}

    EditText mOpenEditText;
    OpenFolder mOpenFolder;
    BookShelfGridAdapter mOpenAdapter;
    /**
	 * 初始化控件View
	 */
	private void initView(){
        mDragLayer = (DragLayer) findViewById(R.id.layer);
		bookShelfGrid = (DragGridView<BookShelfItem>) findViewById(R.id.book_shelf_grid);
		rlNoDataLayout = (TextView) findViewById(R.id.rl_no_data_layout);
		Drawable topDrawable = getResources().getDrawable(R.drawable.bg_zuijingyuedu);
		topDrawable.setBounds(0, 0, getResources().getDimensionPixelSize(R.dimen.size_80dip), getResources().getDimensionPixelSize(R.dimen.size_80dip));
		rlNoDataLayout.setCompoundDrawables(null, topDrawable, null, null);
		
        mOutScrollView = (ScrollView) findViewById(R.id.contentView);
        title_bar = findViewById(R.id.title_bar);
        sort_title_bar = findViewById(R.id.title_sort_bar);
        tv_sort_finish = (TextView) findViewById(R.id.tv_sort_finish);
        tv_sort_finish.setOnClickListener(this);
        findViewById(R.id.headView).setOnClickListener(this);
        first_lastest_read_book = (RelativeLayout) findViewById(R.id.first_lastest_read_book);
        second_lastest_read_book = (RelativeLayout) findViewById(R.id.second_lastest_read_book);
        third_lastest_read_book = (RelativeLayout) findViewById(R.id.third_lastest_read_book);
        fourth_lastest_read_book = (RelativeLayout) findViewById(R.id.fourth_lastest_read_book);
        
        first_lastest_read_book.setOnClickListener(this);
        second_lastest_read_book.setOnClickListener(this);
        third_lastest_read_book.setOnClickListener(this);
        fourth_lastest_read_book.setOnClickListener(this);
        
        iv_first_book_img = (AsyncImageView) findViewById(R.id.iv_first_book_img);
        iv_second_book_img = (AsyncImageView) findViewById(R.id.iv_second_book_img);
        iv_third_book_img = (AsyncImageView) findViewById(R.id.iv_third_book_img);
        iv_fourth_book_img = (AsyncImageView) findViewById(R.id.iv_fourth_book_img);
        iv_first_book_img.setDefaultImgRes(R.drawable.book_default);
        iv_second_book_img.setDefaultImgRes(R.drawable.book_default);
        iv_third_book_img.setDefaultImgRes(R.drawable.book_default);
        iv_fourth_book_img.setDefaultImgRes(R.drawable.book_default);

        iv_first_book_img_online = (ImageView) findViewById(R.id.iv_first_book_online);
        iv_second_book_img_online = (ImageView) findViewById(R.id.iv_second_book_online);
        iv_third_book_img_online = (ImageView) findViewById(R.id.iv_third_book_online);
        iv_fourth_book_img_online = (ImageView) findViewById(R.id.iv_fourth_book_online);
        
        iv_first_book_delete = (ImageView) findViewById(R.id.iv_first_book_delete);
        iv_second_book_delete = (ImageView) findViewById(R.id.iv_second_book_delete);
        iv_third_book_delete = (ImageView) findViewById(R.id.iv_third_book_delete);
        iv_fourth_book_delete = (ImageView) findViewById(R.id.iv_fourth_book_delete);
        
        iv_first_book_delete.setOnClickListener(this);
        iv_second_book_delete.setOnClickListener(this);
        iv_third_book_delete.setOnClickListener(this);
        iv_fourth_book_delete.setOnClickListener(this);
        
        mDragLayer.setScrollView(mOutScrollView);
        mDragLayer.setOnDragPage(bookShelfGrid.getCurrentLayer());

        mOpenFolder = new OpenFolder(getContext(), mDragLayer);
        mOpenFolder.setmOnFolderClosedListener(new OpenFolder.OnFolderClosedListener() {

            @Override
            public void onClosed() {
                if (mOpenEditText != null){
                    String content = mOpenEditText.getText().toString();
                    if (!TextUtils.isEmpty(content)){
                        BookShelfItem item = bookShelfGridAdapter.getItem(mOpenFolder.position);
                        String original = item.mGroupMessage.groupName;
                        if (!content.equals(original)){//更新
                            item.mGroupMessage.groupName = content;
                            bookShelfGridAdapter.notifyDataSetChanged();
                            new UpdateGroupNameModel(new AddGroupModel.CallBack() {
                                @Override
                                public void onCallBack(GroupMessage groupMessage) {
                                    BookShelfItem item = bookShelfGridAdapter.getItem(mOpenFolder.position);
                                    item.mGroupMessage = groupMessage;
                                }
                            }).start(item.mGroupMessage, content);
                        }
                    }
                    mOpenAdapter = null;
                    mDragLayer.setScrollView(mOutScrollView);
                    mDragLayer.setOnDragPage(bookShelfGrid.getCurrentLayer());
                    mDragLayer.setEdgeControllerEdge((int) (8 * getResources().getDisplayMetrics().density));
//                    bookShelfGrid.startDragThread();
                    DragController.getInstance().notifyDragDisable();
                    mOpenEditText = null;
                }
            }
        });
		bookShelfGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				 BookShelfItem info = bookShelfGridAdapter.getItem(position);
                if (DragController.getInstance().isDragWorking()){
                    bookShelfGridAdapter.selected(position);
                    dealDelView(bookShelfGridAdapter);
                    return;
                }
                if (info.isEmpty()){
                    View vOpenView = LayoutInflater.from(getContext()).inflate(R.layout.book_shelf_add_book_layout, null);
                    vOpenView.findViewById(R.id.ll_fuli_layout).setOnClickListener(BookShelfViewNotBinding.this);
                    vOpenView.findViewById(R.id.ll_wifi_layout).setOnClickListener(BookShelfViewNotBinding.this);
                    vOpenView.findViewById(R.id.ll_import_layout).setOnClickListener(BookShelfViewNotBinding.this);
                    mOpenFolder.openFolderView(vOpenView, true);
                    return;
                }
                if (info.isFile()) {
                    mOpenFolder.setItemPosition(position);
                    View vOpenView = LayoutInflater.from(getContext()).inflate(R.layout.file_open, null);
                    DragScrollView sc = (DragScrollView) vOpenView.findViewById(R.id.open_container);
                    mOpenEditText = (EditText) vOpenView.findViewById(R.id.open_edit_name);
                    mOpenEditText.setText(info.mGroupMessage.groupName);
                    vOpenView.findViewById(R.id.open_edit_clear).setOnClickListener(new OnClickListener(){

                        @Override
                        public void onClick(View view) {
                            mOpenEditText.setText("");
                        }
                    });
                    mOpenEditText.setFocusable(true);
                    List<BookShelfItem> datas = info.mItems;
                    if (info.mItems == null){
                        datas = new ArrayList<BookShelfItem>();
                    }
                    DragGridView<BookShelfItem> gd = (DragGridView<BookShelfItem>) vOpenView.findViewById(R.id.open_grid);
                    gd.setNumColumns(3);
                    mOpenAdapter = new BookShelfGridAdapter(getContext(), datas);
                    gd.setAdapter(mOpenAdapter);
                    gd.setCurrentPageId(1);
                    gd.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            BookShelfItem info = mOpenAdapter.getItem(position);
                            if (DragController.getInstance().isDragWorking()){
                                mOpenAdapter.selected(position);
                                dealDelView(mOpenAdapter);
                                return;
                            }
                            onGridItemClick(view, info);
                        }
                    });
                    mDragLayer.setOnDragPage(1);
                    mDragLayer.setScrollView(sc);
                    mDragLayer.setEdgeControllerEdge(mOpenFolder.getTopEdge());
                    mOpenFolder.openFolderView(vOpenView, false);
                    return;
                }
                onGridItemClick(view, info);
			}
		});
		
	}

    private void onGridItemClick(View view, BookShelfItem info){
        if (view != null){
            ShelfFileView bookCover = (ShelfFileView) view.findViewById(R.id.book_img_iv);
            OpenBookAnimManagement.getInstance().setOpenBookAnimVIew(bookCover, bookCover.getBitmap());
        }
        if (info == null) {
            return;
        }
        //TODO:暂时处理，到时候详情界面，广播通知修改。
        DownloadInfo dbInfo = DownloadPresenterLeyue.getDownloadUnitById(info.mDownLoadInfo.contentID);
        if (dbInfo!=null) {
            info.mDownLoadInfo.isOrderChapterNum = dbInfo.isOrderChapterNum;
            info.mDownLoadInfo.isOrder = dbInfo.isOrder;
            info.mDownLoadInfo.price = dbInfo.price;
            info.mDownLoadInfo.promotionPrice = dbInfo.promotionPrice;
            info.mDownLoadInfo.contentType = dbInfo.contentType;
            info.mDownLoadInfo.isDownloadFullVersonBook = dbInfo.isDownloadFullVersonBook;
        }

        if(info.mDownLoadInfo.state != DownloadAPI.STATE_ONLINE && downloadBook(info.mDownLoadInfo)){
            return;
        }

        if(!TextUtils.isEmpty(dbInfo.filePathLocation)){
            File file = new File(dbInfo.filePathLocation);
            if (!file.exists()) {
                gotoReadBook(info.mDownLoadInfo, true);
                return;
            }
            gotoReadBook(info.mDownLoadInfo, false);
        }else{//不大可能发生
            gotoReadBook(info.mDownLoadInfo, true);
            return;
        }
    }
	 
	

	/**
	 * 初始化数据
	 */
	private void initData(){
		//TODO: 其他初始化数据
		bookShelfGridAdapter = new BookShelfGridAdapter(getContext(), new ArrayList<BookShelfItem>());
		bookShelfGrid.setAdapter(bookShelfGridAdapter);
		onStartLoadData(true);
	}
	
	/**
	 * 开始加载数据
	 */
	private void onStartLoadData(boolean showLoadViewOrNot){
        if (showLoadViewOrNot){
            registerReceiver();
            showLoadView();
        }
		mBookShelfModel.start();
		mBookShelfRecentReadModelLeyue.start();
	}
	
	/**
	 * 更新最近阅读书籍界面
	 */
	private void onStartRecentBookLoadData(){
//        showLoadView();
		((ILoadDialog)_this).showLoadDialog();
		mBookShelfRecentReadModelLeyue.start();
	}
	
	/**
	 * 注册广播接收者
	 */
	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(DownloadAPI.ACTION_ON_DOWNLOAD_PROGRESS_CHANGE);
		filter.addAction(DownloadAPI.ACTION_ON_DOWNLOAD_STATE_CHANGE);
		filter.addAction(AppBroadcast.ACTION_UPDATE_BOOKSHELF);
		if (mDonwloadListener == null) {
			mDonwloadListener = new DonwloadListener();
		}
		getContext().registerReceiver(mDonwloadListener, filter);
	}
	
	/**
	 * 注销广播接收者
	 */
	private void unRegisterReceiver() {
		if(mDonwloadListener != null) {
			getContext().unregisterReceiver(mDonwloadListener);
			mDonwloadListener = null;
		}
	}
	
	/**
	 * 如果当前书架没有书籍显示无书架提示
	 */
	private void checkIsItemsEmpty(){
		if(bookShelfGridAdapter != null && bookShelfGridAdapter.getCount() > 0){
			bookShelfGrid.setVisibility(View.VISIBLE);
		}else{
			bookShelfGrid.setVisibility(View.GONE);
		}
	}
	
	/**
     * 判断是否为乐阅EPUB书籍，是的话可以通过ID去取Secretkey
     * @param contentID
     * @return
     */
    private boolean verifyLeyueEpubBook(String contentID){
        if (!TextUtils.isEmpty(contentID)){
            try{
                Long.parseLong(contentID);
                return true;
            }catch (Exception e){}
        }
        return false;
    }
	
	/**
	 * 阅读书籍
	 * @param isOnline
	 */
	private void gotoReadBook(DownloadInfo aDownInfo,boolean isOnline){
        if(!isOnline){
            if(!FileUtil.isSDcardExist()){
                ToastUtil.showToast(getContext(), R.string.sdcard_no_exist_download_tip);
                return;
            }
        }else{
            if(!DialogUtil.isDeviceNetword((Activity) getContext())){
                return;
            }
        }
        if (aDownInfo == null) {
            return;
        }
        Book book = new Book();
        book.setPath(aDownInfo.filePathLocation);
        book.setBookId(aDownInfo.contentID);
        book.setFeeStart(aDownInfo.isOrderChapterNum);
        //书籍阅读界面，将根据isOrder字段来判断需要排版的章节数，所以免费书籍也要在进入阅读前，设置为isOrder。
        if (TextUtils.isEmpty(aDownInfo.price) && TextUtils.isEmpty(aDownInfo.promotionPrice)) {
            book.setOrder(true);
        }else {
            book.setOrder(aDownInfo.isOrder);
        }
        book.setPrice(aDownInfo.price);
        book.setPromotionPrice(aDownInfo.promotionPrice);
        book.setBookType(aDownInfo.contentType);
        book.setBookName(aDownInfo.contentName);
        book.setAuthor(aDownInfo.authorName);
        book.setCoverPath(aDownInfo.logoUrl);
        book.setBookFormatType(aDownInfo.downloadType);
        book.setDownloadFullVersonBook(aDownInfo.isDownloadFullVersonBook);
        book.setOnline(isOnline);
        if(!isOnline){
            if(book.getBookFormatType().equals(DownloadHttpEngine.VALUE_DOWNLOAD_TYPE_BOOK)
                    && verifyLeyueEpubBook(aDownInfo.contentID)){
                BaseReaderActivityLeyue.openActivity(getContext(), book,true);
            }
        }
        BaseReaderActivityLeyue.openActivity(getContext(), book,true);
    }
	
	/**
     * 下载书籍，已经下载完成返回false
     * @return
     */
	public boolean downloadBook(DownloadInfo aDownloadInfo){
		if (aDownloadInfo.state == DownloadAPI.STATE_FINISH) {
			return false;
		}
		if (aDownloadInfo!=null && aDownloadInfo.size > FileUtil.getStorageSize()) {
			ToastUtil.showToast(getContext(), R.string.sdcard_no_exist_Free_Not_Enough_tip);
		}
		if(PreferencesUtil.getInstance(getContext()).getWifiDownloadBoolean() && !ApnUtil.isWifiWork((getContext()))){
			ToastUtil.showToast(getContext(), R.string.account_setting_allow_wifi_download_tip);
			return true;
		}
		ContentValues values = new ContentValues();
		int mState = -1;
		if (aDownloadInfo.state == DownloadAPI.STATE_PAUSE
				|| aDownloadInfo.state == DownloadAPI.STATE_FAIL
				|| aDownloadInfo.state == DownloadAPI.STATE_FAIL_OUT_MEMORY) {
			values.put(DownloadAPI.STATE, DownloadAPI.STATE_START);
			mState = DownloadAPI.STATE_START;
		} else {
			values.put(DownloadAPI.STATE, DownloadAPI.STATE_PAUSE);
			mState = DownloadAPI.STATE_PAUSE;
		}
		int successSize = getContext().getContentResolver().update(
				DownloadAPI.CONTENT_URI,
				values,
				DownloadHttpEngine.CONTENT_ID + " ='"
						+ aDownloadInfo.contentID + "'", null);
		if (successSize > 0) {
			aDownloadInfo.state = mState;
			bookShelfGridAdapter.notifyDataSetChanged();
		}
	    return true;
	}
	
	@Override
	public void onActivityResume(boolean isFirst) {
		super.onActivityResume(isFirst);
		OpenBookAnimManagement.getInstance().starCloseBookAnim(null);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if(mCanScroll){
				if(mActionbarHandler != null)
					mActionbarHandler.cancelEditMode();
				return false;
			}
		}
		return true;
	}
	
	
	@Override
	public boolean canScroll(ViewPager viewPager, int dx, int x, int y) {
		return false;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.headView:
			ActivityChannels.gotoSearchListActivity(getContext());
//			title_bar.setVisibility(View.GONE);
//			_this.startActivityForResult(new Intent(_this, SearchListActivity.class), BookShelfActivity.REQUEST_CODE_SEARCH);
			break;
		case R.id.first_lastest_read_book:
			onGridItemClick(null, mRecentBookShelfItems.get(0));
			break;
		case R.id.second_lastest_read_book:
			onGridItemClick(null, mRecentBookShelfItems.get(1));
			break;
		case R.id.third_lastest_read_book:
			onGridItemClick(null, mRecentBookShelfItems.get(2));
			break;
		case R.id.fourth_lastest_read_book:
            onGridItemClick(null, mRecentBookShelfItems.get(3));
			break;
		case R.id.tv_sort_finish:
			DragController.getInstance().notifyDragDisable();
			break;
		case R.id.tv_sort_delete:
            deleteBooks(mOpenAdapter == null? bookShelfGridAdapter:mOpenAdapter);
			break;
		case R.id.iv_first_book_delete:
//			DownloadInfo firstInfo = mRecentBookShelfItems.get(0).mDownLoadInfo;
//			BookMarkDB.getInstance().deleteRecentReadBook(firstInfo);
			BookMarkDB.getInstance().deleteRecentReadBook(mRecentBookShelfItems.get(0).mBookMark.contentID);
			onStartRecentBookLoadData();
			break;
		case R.id.iv_second_book_delete:
//			DownloadInfo secondInfo = mRecentBookShelfItems.get(1).mDownLoadInfo;
//			BookMarkDB.getInstance().deleteRecentReadBook(secondInfo);
			BookMarkDB.getInstance().deleteRecentReadBook(mRecentBookShelfItems.get(1).mBookMark.contentID);
			onStartRecentBookLoadData();
			break;
		case R.id.iv_third_book_delete:
//			DownloadInfo thirdInfo = mRecentBookShelfItems.get(2).mDownLoadInfo;
//			BookMarkDB.getInstance().deleteRecentReadBook(thirdInfo);
			BookMarkDB.getInstance().deleteRecentReadBook(mRecentBookShelfItems.get(2).mBookMark.contentID);
			onStartRecentBookLoadData();
			break;
		case R.id.iv_fourth_book_delete:
//			DownloadInfo fourthInfo = mRecentBookShelfItems.get(3).mDownLoadInfo;
//			BookMarkDB.getInstance().deleteRecentReadBook(fourthInfo);
			BookMarkDB.getInstance().deleteRecentReadBook(mRecentBookShelfItems.get(3).mBookMark.contentID);
			onStartRecentBookLoadData();
			break;
            case R.id.ll_wifi_layout:
                mOpenFolder.dismiss();
                _this.bWifiTransferBtnClick.onClick(v);
                break;
            case R.id.ll_import_layout:
                mOpenFolder.dismiss();
                _this.bImportLocalBtnClick.onClick(v);
                break;
            case R.id.ll_fuli_layout:
                mOpenFolder.dismiss();
                Intent intent = new Intent(_this, SlideActivityGroup.class);
                intent.putExtra(SlideActivityGroup.Extra_Switch_UI, SlideActivityGroup.BOOK_CITY);
                _this.startActivity(intent);
                SlideActivityGroup.PageController.getmInstance().notifyGo2Page(1);
                break;
		default:
			break;
		}
		
	}

	@Override
	public boolean onStartFail(String tag, String state, Object... params) {
		return false;
	}

	@Override
	public boolean onPreLoad(String tag, Object... params) {
		return false;
	}

	@Override
	public boolean onFail(Exception e, String tag, Object... params) {
		return false;
	}

	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {
        hideLoadView();
        ((ILoadDialog)_this).hideLoadDialog();
		if(result != null && !isCancel){
			if (mBookShelfModel.getTag().equals(tag)) {
				ArrayList<BookShelfItem> list = (ArrayList<BookShelfItem>)result;
                bookShelfGridAdapter.clear();
				mBookShelfList = list;
				refreshDrawableState();
                bookShelfGridAdapter.append(list);
                bookShelfGridAdapter.notifyDataSetChanged();
				checkIsItemsEmpty();
			}
		}
		if(mBookShelfRecentReadModelLeyue.getTag().equals(tag)){
			if(result == null){
				mRecentBookShelfItems = null;
			}else{
				mRecentBookShelfItems = (ArrayList<BookShelfItem>) result;
			}
			refreshRecentReadBook();
		}
		return false;
	}
	
	/**
	 * 刷新最近阅读书籍界面
	 */
	private void refreshRecentReadBook(){
		if(mRecentBookShelfItems != null && mRecentBookShelfItems.size() > 0){
			rlNoDataLayout.setVisibility(View.GONE);
			int size = mRecentBookShelfItems.size();
			if(size == 1){
				first_lastest_read_book.setVisibility(View.VISIBLE);
				second_lastest_read_book.setVisibility(View.GONE);
				third_lastest_read_book.setVisibility(View.GONE);
				fourth_lastest_read_book.setVisibility(View.GONE);
//				if(mRecentBookShelfItems.get(0).mDownLoadInfo!= null) {
//					iv_first_book_img.setImageUrl(mRecentBookShelfItems.get(0).mDownLoadInfo.logoUrl);
//				}
				if(mRecentBookShelfItems.get(0).mDownLoadInfo!= null) {
					iv_first_book_img.setImageUrl(mRecentBookShelfItems.get(0).mDownLoadInfo.logoUrl);
                    iv_first_book_img_online.setVisibility(mRecentBookShelfItems.get(0).mDownLoadInfo.state == DownloadAPI.STATE_ONLINE
                            ?View.VISIBLE:View.GONE);
				}
			}else if(size == 2){
				first_lastest_read_book.setVisibility(View.VISIBLE);
				second_lastest_read_book.setVisibility(View.VISIBLE);
				third_lastest_read_book.setVisibility(View.GONE);
				fourth_lastest_read_book.setVisibility(View.GONE);
				
//				if(mRecentBookShelfItems.get(0).mDownLoadInfo!= null) {
//					iv_first_book_img.setImageUrl(mRecentBookShelfItems.get(0).mDownLoadInfo.logoUrl);
//				}
//				if(mRecentBookShelfItems.get(1).mDownLoadInfo!= null) {
//					iv_second_book_img.setImageUrl(mRecentBookShelfItems.get(1).mDownLoadInfo.logoUrl);
//				}
				if(mRecentBookShelfItems.get(0).mDownLoadInfo!= null) {
					iv_first_book_img.setImageUrl(mRecentBookShelfItems.get(0).mDownLoadInfo.logoUrl);
                    iv_first_book_img_online.setVisibility(mRecentBookShelfItems.get(0).mDownLoadInfo.state == DownloadAPI.STATE_ONLINE
                            ?View.VISIBLE:View.GONE);
				}
				if(mRecentBookShelfItems.get(1).mDownLoadInfo!= null) {
					iv_second_book_img.setImageUrl(mRecentBookShelfItems.get(1).mDownLoadInfo.logoUrl);
                    iv_second_book_img_online.setVisibility(mRecentBookShelfItems.get(1).mDownLoadInfo.state == DownloadAPI.STATE_ONLINE
                            ?View.VISIBLE:View.GONE);
				}
				
			}else if(size == 3){
				first_lastest_read_book.setVisibility(View.VISIBLE);
				second_lastest_read_book.setVisibility(View.VISIBLE);
				third_lastest_read_book.setVisibility(View.VISIBLE);
				fourth_lastest_read_book.setVisibility(View.GONE);
				
//				if(mRecentBookShelfItems.get(0).mDownLoadInfo!= null) {
//					iv_first_book_img.setImageUrl(mRecentBookShelfItems.get(0).mDownLoadInfo.logoUrl);
//				}
//				if(mRecentBookShelfItems.get(1).mDownLoadInfo!= null) {
//					iv_second_book_img.setImageUrl(mRecentBookShelfItems.get(1).mDownLoadInfo.logoUrl);
//				}
//				if(mRecentBookShelfItems.get(2).mDownLoadInfo!= null) {
//					iv_third_book_img.setImageUrl(mRecentBookShelfItems.get(2).mDownLoadInfo.logoUrl);
//				}
				if(mRecentBookShelfItems.get(0).mDownLoadInfo!= null) {
					iv_first_book_img.setImageUrl(mRecentBookShelfItems.get(0).mDownLoadInfo.logoUrl);
                    iv_first_book_img_online.setVisibility(mRecentBookShelfItems.get(0).mDownLoadInfo.state == DownloadAPI.STATE_ONLINE
                            ?View.VISIBLE:View.GONE);
				}
				if(mRecentBookShelfItems.get(1).mDownLoadInfo!= null) {
					iv_second_book_img.setImageUrl(mRecentBookShelfItems.get(1).mDownLoadInfo.logoUrl);
                    iv_second_book_img_online.setVisibility(mRecentBookShelfItems.get(1).mDownLoadInfo.state == DownloadAPI.STATE_ONLINE
                            ?View.VISIBLE:View.GONE);
				}
				if(mRecentBookShelfItems.get(2).mDownLoadInfo!= null) {
					iv_third_book_img.setImageUrl(mRecentBookShelfItems.get(2).mDownLoadInfo.logoUrl);
                    iv_third_book_img_online.setVisibility(mRecentBookShelfItems.get(2).mDownLoadInfo.state == DownloadAPI.STATE_ONLINE
                            ?View.VISIBLE:View.GONE);
				}
			}else{
				first_lastest_read_book.setVisibility(View.VISIBLE);
				second_lastest_read_book.setVisibility(View.VISIBLE);
				third_lastest_read_book.setVisibility(View.VISIBLE);
				fourth_lastest_read_book.setVisibility(View.VISIBLE);
				
//				if(mRecentBookShelfItems.get(0).mDownLoadInfo!= null) {
//					iv_first_book_img.setImageUrl(mRecentBookShelfItems.get(0).mDownLoadInfo.logoUrl);
//				}
//				if(mRecentBookShelfItems.get(1).mDownLoadInfo!= null) {
//					iv_second_book_img.setImageUrl(mRecentBookShelfItems.get(1).mDownLoadInfo.logoUrl);
//				}
//				if(mRecentBookShelfItems.get(2).mDownLoadInfo!= null) {
//					iv_third_book_img.setImageUrl(mRecentBookShelfItems.get(2).mDownLoadInfo.logoUrl);
//				}
//				if(mRecentBookShelfItems.get(3).mDownLoadInfo!= null) {
//					iv_fourth_book_img.setImageUrl(mRecentBookShelfItems.get(3).mDownLoadInfo.logoUrl);
//				}
				if(mRecentBookShelfItems.get(0).mDownLoadInfo!= null) {
					iv_first_book_img.setImageUrl(mRecentBookShelfItems.get(0).mDownLoadInfo.logoUrl);
                    iv_first_book_img_online.setVisibility(mRecentBookShelfItems.get(0).mDownLoadInfo.state == DownloadAPI.STATE_ONLINE
                            ?View.VISIBLE:View.GONE);
				}
				if(mRecentBookShelfItems.get(1).mDownLoadInfo!= null) {
					iv_second_book_img.setImageUrl(mRecentBookShelfItems.get(1).mDownLoadInfo.logoUrl);
                    iv_second_book_img_online.setVisibility(mRecentBookShelfItems.get(1).mDownLoadInfo.state == DownloadAPI.STATE_ONLINE
                            ?View.VISIBLE:View.GONE);
				}
				if(mRecentBookShelfItems.get(2).mDownLoadInfo!= null) {
					iv_third_book_img.setImageUrl(mRecentBookShelfItems.get(2).mDownLoadInfo.logoUrl);
                    iv_third_book_img_online.setVisibility(mRecentBookShelfItems.get(2).mDownLoadInfo.state == DownloadAPI.STATE_ONLINE
                            ?View.VISIBLE:View.GONE);
				}
				if(mRecentBookShelfItems.get(3).mDownLoadInfo!= null) {
					iv_fourth_book_img.setImageUrl(mRecentBookShelfItems.get(3).mDownLoadInfo.logoUrl);
                    iv_fourth_book_img_online.setVisibility(mRecentBookShelfItems.get(3).mDownLoadInfo.state == DownloadAPI.STATE_ONLINE
                            ?View.VISIBLE:View.GONE);
				}
			}
			
		}else{
			rlNoDataLayout.setVisibility(View.VISIBLE);
			first_lastest_read_book.setVisibility(View.GONE);
			second_lastest_read_book.setVisibility(View.GONE);
			third_lastest_read_book.setVisibility(View.GONE);
			fourth_lastest_read_book.setVisibility(View.GONE);
		}
	}
	
	private void showDeleteBookDialog(ConfirmListener confirmListener, CancelListener cancelListener){
		View contentView = LayoutInflater.from(getContext()).inflate(R.layout.delete_book_shelf_book_layout, null);
		DialogUtil.commonConfirmDialog(_this, "", contentView, -1, -1, confirmListener, cancelListener);
		
	}
	
	private void showDeleteFileDialog(final IDeleteFileInterface fileInterface){
		View contentView = LayoutInflater.from(getContext()).inflate(R.layout.delete_book_shelf_file_layout, null);
		final Dialog dialog = DialogUtil.commonViewDialog(_this, contentView);
		TextView btn_del_group = (TextView) contentView.findViewById(R.id.btn_del_group);
		TextView btn_del_group_and_book = (TextView) contentView.findViewById(R.id.btn_del_group_and_book);
		TextView btn_cancel = (TextView) contentView.findViewById(R.id.btn_cancel);
		btn_del_group.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(fileInterface != null){
					fileInterface.onDeleteGroup();
				}
				dialog.cancel();
				
			}
		});
		
		btn_del_group_and_book.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(fileInterface != null){
					fileInterface.onDeleteGroupAndBook();
				}
				dialog.cancel();
				
			}
		});
		
		btn_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(fileInterface != null){
					fileInterface.onCancel();
				}
				dialog.cancel();
				
			}
		});
		
		dialog.show();
		
	}
	
	private interface IDeleteFileInterface{
		public void onDeleteGroup();
		public void onDeleteGroupAndBook();
		public void onCancel();
	}

    private void deleteBooks(final BookShelfGridAdapter adapter){
        final List<BookShelfItem> delItems = new ArrayList<BookShelfItem>();
        for(int i =0; i< adapter.getCount();i++){
            BookShelfItem item  = adapter.getItem(i);
            if (item.isDelSelect){
                delItems.add(item);
            }
        }
        boolean isFileDialog = false;
        for (BookShelfItem item:delItems){
            if (item.isFile){
                isFileDialog = true;
            }
        }
        if (isFileDialog){
            //删除包含文件夹弹窗
        	showDeleteFileDialog(new IDeleteFileInterface() {
				
				@Override
				public void onDeleteGroupAndBook() {
					delWithFile(adapter, delItems, true);
					refreshTopReadView(delItems);
					DragController.getInstance().notifyDragDisable();
				}
				
				@Override
				public void onDeleteGroup() {
					delWithFile(adapter, delItems, false);
					DragController.getInstance().notifyDragDisable();
				}
				
				@Override
				public void onCancel() {
				}
			});
            
        }else{
            //删除书籍
        	showDeleteBookDialog(new ConfirmListener() {
				
				@Override
				public void onClick(View v) {
					delWithoutFile(adapter, delItems);
					refreshTopReadView(delItems);
					DragController.getInstance().notifyDragDisable();
				}
			}, new CancelListener() {
				
				@Override
				public void onClick(View v) {}
			});
            
            
        }
    }

    private void refreshTopReadView(List<BookShelfItem> delItems){
        if (mRecentBookShelfItems != null){
            boolean needRefresh = false;
        	for (int i =0;i<Math.min(4, mRecentBookShelfItems.size()) ;i++){
                BookShelfItem item = mRecentBookShelfItems.get(i);
                if (container(delItems, item)){
//                    DownloadInfo thirdInfo = mRecentBookShelfItems.get(i).mDownLoadInfo;
//                    BookMarkDB.getInstance().deleteRecentReadBook(thirdInfo);
                	BookMarkDB.getInstance().deleteRecentReadBook(mRecentBookShelfItems.get(i).mBookMark.contentID);
                    needRefresh = true;
                }
            }
            if(needRefresh) {
            	onStartRecentBookLoadData();
            }
            return;
        }
    }

    private boolean container(List<BookShelfItem> delItems, BookShelfItem item){
        if (delItems != null){
            for (BookShelfItem delItem: delItems){
//                if (delItem.mDownLoadInfo != null && item.mDownLoadInfo != null){
//                    boolean res = delItem.mDownLoadInfo.contentID.equals(item.mDownLoadInfo.contentID);
//                    if (res){
//                        return true;
//                    }
//                }
            	if (delItem.mBookMark != null && item.mBookMark != null 
              		  && delItem.mBookMark.contentID.equals(item.mBookMark.contentID)){
                    return true;
                }
            }
        }
        return false;
    }

    private void delWithFile(BookShelfGridAdapter adapter, List<BookShelfItem> delItems, boolean isDelete){
        dismissDelView();
         for (BookShelfItem item: delItems){
             if (item.isFile){
                 adapter.remove(item);
                 if (!isDelete){
                     List<BookShelfItem> items = item.mItems;
                         if (items != null){
                         for (BookShelfItem it: items){
                             it.isFile = false;
                             it.isInFile = false;
                         }
                         adapter.add2Head(items);
                     }
                 }
             }else{
                 adapter.remove(item);
             }
         }
        adapter.notifyDataSetChanged();
        new DelGroupModel().start(delItems, isDelete);
    }

    private void delWithoutFile(BookShelfGridAdapter adapter ,List<BookShelfItem> delItems){
        dismissDelView();
        for (BookShelfItem item: delItems){
            adapter.remove(item);
        }
        adapter.notifyDataSetChanged();
        new DelGroupModel().start(delItems, false);
    }

    public class BookShelfGridAdapter extends DragAdapter<BookShelfItem>{

//        public void onItemReplace(int itemPosition, int itemPosition2){
//            if (itemPosition == itemPosition2){
//                return;
//            }
//            BookShelfItem item = getItem(itemPosition);
//            BookShelfItem item2 = getItem(itemPosition2);
//            if (item.isEmpty() || item2.isEmpty()){
//                return;
//            }
//            double position2 = item2.getShelfPosition();
//
//            if (itemPosition > itemPosition2){
//                double position1 = position2 - 2;
//                if (itemPosition2 > 0){
//                    BookShelfItem item1 = getItem(itemPosition2 - 1);
//                    position1 = item1.getShelfPosition();
//                }
//                double newValue = position1 + (position2 - position1)/2;
//                item.setShelfPosition(newValue);
//            }else{
//                double position1 = position2 + 2;
//                if (itemPosition2 < getCount() -1){
//                    BookShelfItem item1 = getItem(itemPosition2 + 1);
//                    if (!item1.isEmpty()){
//                        position1 = item1.getShelfPosition();
//                    }
//                }
//                double newValue = position2 + (position1 - position2)/2;
//                item.setShelfPosition(newValue);
//            }
//        }

        private SparseArray<DragItemView> itemView;
        public BookShelfGridAdapter(Context mContext, List<BookShelfItem> list) {
            super(mContext, list);
            itemView = new SparseArray<DragItemView>();
        }

        public BookShelfItem getItemById(long id){
            try {
                for (BookShelfItem item: lstDate){
                    if (item.mDownLoadInfo != null && item.mDownLoadInfo.id == id){
                        return item;
                    }
                }
            }catch (Exception e){}
            return null;
        }

        @Override
        public IDragItemView getItemView(int position) {
            return itemView.get(position);
        }

        /**
         * 是否有删除选中
         * @return
         */
        public boolean isSelected(){
            for (int i = 0;i< getCount(); i++){
                BookShelfItem item = getItem(i);
                if (item.isDelSelect){
                    return true;
                }
            }
            return false;
        }

        public void resetSelectState(){
            for (int i = 0;i< getCount(); i++){
                BookShelfItem item = getItem(i);
                item.isDelSelect = false;
            }
        }

        public void selected(int position){
            BookShelfItem item = getItem(position);
            item.isDelSelect = !item.isDelSelect;
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position) {
            DragItemView view = itemView.get(position);
            if (view == null){
                view = new DragItemView(getContext());
            }else{
                view.clearAnimation();
            }
            view.fillAdapter(getItem(position));
            itemView.put(position, view);
            return view;
        }
	}
	
	private class DonwloadListener extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(DownloadAPI.ACTION_ON_DOWNLOAD_STATE_CHANGE)){
				Message msg = new Message();
				msg.what = MSG_WHAT_ON_STATE_CHANGE;
				msg.obj = intent;
				mHandler.sendMessage(msg);
			}else if(intent.getAction().equals(DownloadAPI.ACTION_ON_DOWNLOAD_PROGRESS_CHANGE)){
				Message msg = new Message();
				msg.what = MSG_WHAT_ON_PROGRESS_CHANGE;
				msg.obj = intent;
				mHandler.sendMessage(msg);
			}else if(intent.getAction().equals(AppBroadcast.ACTION_UPDATE_BOOKSHELF)){
				onStartLoadData(false);
			}
		}
	}

    private BookShelfAction mBookShelfAction = new BookShelfAction() {
		
		@Override
		public void onSearchFinish() {
			title_bar.setVisibility(View.VISIBLE);
			
		}

		@Override
		public void onSortBook() {
			DragController.getInstance().notifyDragEnable();
			
		}
	};

    private void dealDelView(BookShelfGridAdapter adapter){
        if (adapter.isSelected()){
            showDelView();
        }else{
            dismissDelView();
        }
    }

    private void showDelView(){
        if (rl_bottom_layout == null){
            rl_bottom_layout = LayoutInflater.from(getContext()).inflate(R.layout.bottom_del, null);
            rl_bottom_layout.findViewById(R.id.tv_sort_delete).setOnClickListener(this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            mDragLayer.addView(rl_bottom_layout, params);
        }
    }

    private void dismissDelView(){
        if (rl_bottom_layout != null){
            mDragLayer.removeView(rl_bottom_layout);
            rl_bottom_layout = null;
        }
    }

    @Override
    public void onDragEnable() {
        sort_title_bar.setVisibility(View.VISIBLE);
        for (int i = 0; i < RECENT_BOOK_DELETE_IDS.length; i++) {
            findViewById(RECENT_BOOK_DELETE_IDS[i]).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDragDisable() {
        dismissDelView();
        if (bookShelfGridAdapter != null){
            bookShelfGridAdapter.resetSelectState();
        }
        if (mOpenAdapter != null){
            mOpenAdapter.resetSelectState();
        }
        sort_title_bar.setVisibility(View.GONE);
        for (int i = 0; i < RECENT_BOOK_DELETE_IDS.length; i++) {
            findViewById(RECENT_BOOK_DELETE_IDS[i]).setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemDelete(int page, int position) {}


    private boolean container(ArrayList<GroupMessage> list, int num){
        for(GroupMessage groupMessage: list){
            if (groupMessage.unNameNumber == num){
                return true;
            }
        }
        return false;
    }
    @Override
    public void onFileCreateOrUpdate(final int fileDataPosition, int itemDataPosition) {
        BookShelfItem file = bookShelfGridAdapter.getItem(fileDataPosition);
        BookShelfItem add = bookShelfGridAdapter.getItem(itemDataPosition);
        if (file.isFile){
            new UpdateGroupModel().start(file.mGroupMessage, file.mBookMark, add.mBookMark);
        }else{//createFile
            String groupName = "未命名分组";
            ArrayList<GroupMessage> list =  GroupInfoDB.getInstance().getMessageInfos();
            int unNameNumber = 1;
            while(container(list, unNameNumber)){
                unNameNumber += 1;
            }
            groupName += (unNameNumber+"");
            file.mGroupMessage = new GroupMessage();
            file.mGroupMessage.groupName = groupName;
            new AddGroupModel(new AddGroupModel.CallBack() {
                @Override
                public void onCallBack(GroupMessage groupMessage) {
                    BookShelfItem file = bookShelfGridAdapter.getItem(fileDataPosition);
                    file.mGroupMessage = groupMessage;
                }
            }).start(file.mBookMark, add.mBookMark, file.getShelfPosition(), groupName, unNameNumber);
        }
        file.addItem(add);
        file.isFile = true;
        bookShelfGridAdapter.set(fileDataPosition, file);
        bookShelfGridAdapter.remove(itemDataPosition);
    }

    @Override
    public <T> void onItemDelete(int totalPage, int page, int removePage, int position, T object) {

    }

    @Override
    public void onDragViewCreate(int page, IDragItemView itemView, MotionEvent event) {

    }

    @Override
    public void onDragViewDestroy(int page, MotionEvent event) {

    }

    @Override
    public void onItemMove(int page, MotionEvent event) {

    }

    @Override
    public void onPageChange(int lastPage, int currentPage) {

    }

    @Override
    public <T> void onPageChangeRemoveDragItem(int lastPage, int currentPage, T object) {

    }

    @Override
    public <T> void onPageChangeReplaceFirstItem(int lastPage, int currentPage, T object) {

    }

    @Override
    public void onPageChangeFinish() {

    }

}
