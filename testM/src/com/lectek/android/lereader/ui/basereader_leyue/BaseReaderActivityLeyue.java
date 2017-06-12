package com.lectek.android.lereader.ui.basereader_leyue;


import static com.lectek.android.lereader.ui.basereader_leyue.view.ReaderViewFactory.newReaderView;
import gueei.binding.Binder;
import gueei.binding.Binder.InflateResult;
import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.BooleanObservable;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lectek.android.LYReader.R;
import com.lectek.android.LYReader.pay.PayUtil;
import com.lectek.android.app.BaseContextActivity;
import com.lectek.android.binding.command.OnItemClickCommand;
import com.lectek.android.lereader.account.AccountManager;
import com.lectek.android.lereader.account.thirdPartApi.net.Callback;
import com.lectek.android.lereader.account.thirdPartApi.net.NetworkUtil;
import com.lectek.android.lereader.analysis.MobclickAgentUtil;
import com.lectek.android.lereader.animation.OpenBookAnimManagement;
import com.lectek.android.lereader.animation.OpenBookAnimManagement.PreRunnable;
import com.lectek.android.lereader.application.MyAndroidApplication;
import com.lectek.android.lereader.binding.model.BaseLoadDataModel.ILoadDataCallBack;
import com.lectek.android.lereader.binding.model.BaseViewModel;
import com.lectek.android.lereader.binding.model.contentinfo.ContentInfoViewModelLeyue;
import com.lectek.android.lereader.binding.model.contentinfo.ContentInfoViewModelLeyue.ItemRecommendBook;
import com.lectek.android.lereader.binding.model.contentinfo.RecommendedBookViewModel;
import com.lectek.android.lereader.binding.model.contentinfo.ScoreUploadModel;
import com.lectek.android.lereader.download.DownloadHttpEngine;
import com.lectek.android.lereader.lib.net.ResponseResultCode;
import com.lectek.android.lereader.lib.net.exception.GsonResultException;
import com.lectek.android.lereader.lib.recharge.IDealPayRunnable;
import com.lectek.android.lereader.lib.recharge.IPayHandler;
import com.lectek.android.lereader.lib.recharge.PayConst;
import com.lectek.android.lereader.lib.share.entity.UmengShareInfo;
import com.lectek.android.lereader.lib.thread.ThreadFactory;
import com.lectek.android.lereader.lib.thread.ThreadPoolFactory;
import com.lectek.android.lereader.lib.thread.internal.ITerminableThread;
import com.lectek.android.lereader.lib.utils.ClientInfoUtil;
import com.lectek.android.lereader.lib.utils.DateUtil;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.lib.utils.PkgManagerUtil;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.BookDecodeInfo;
import com.lectek.android.lereader.net.response.BookTypeInfo;
import com.lectek.android.lereader.net.response.ContentInfoLeyue;
import com.lectek.android.lereader.net.response.ScoreUploadResponseInfo;
import com.lectek.android.lereader.permanent.AppBroadcast;
import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.permanent.ShareConfig;
import com.lectek.android.lereader.plugin.ExPluginManager;
import com.lectek.android.lereader.plugin.ExPluginType;
import com.lectek.android.lereader.presenter.DownloadPresenterLeyue;
import com.lectek.android.lereader.presenter.SyncPresenter;
import com.lectek.android.lereader.share.ShareWeiXin;
import com.lectek.android.lereader.share.ShareYiXin;
import com.lectek.android.lereader.share.entity.MutilMediaInfo;
import com.lectek.android.lereader.share.util.UmengShareUtils;
import com.lectek.android.lereader.share.util.UmengShareUtils.YXHanlder;
import com.lectek.android.lereader.storage.dbase.UserScoreInfo;
import com.lectek.android.lereader.storage.dbase.mark.BookMark;
import com.lectek.android.lereader.storage.dbase.mark.BookMarkDB;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.INetLoadView;
import com.lectek.android.lereader.ui.basereader_leyue.ReaderMenuPopWin.IActionCallback;
import com.lectek.android.lereader.ui.basereader_leyue.anim.AbsVerGestureAnimController;
import com.lectek.android.lereader.ui.basereader_leyue.bookmarks.BookMarkDatas;
import com.lectek.android.lereader.ui.basereader_leyue.digests.AbsTextSelectHandler.ITouchEventDispatcher;
import com.lectek.android.lereader.ui.basereader_leyue.view.EpubCartoonView;
import com.lectek.android.lereader.ui.basereader_leyue.view.IReaderView;
import com.lectek.android.lereader.ui.basereader_leyue.view.IReaderView.IReadCallback;
import com.lectek.android.lereader.ui.basereader_leyue.view.NetEpubCartoonView;
import com.lectek.android.lereader.ui.basereader_leyue.widgets.ClickDetector;
import com.lectek.android.lereader.ui.basereader_leyue.widgets.ClickDetector.OnClickCallBack;
import com.lectek.android.lereader.ui.basereader_leyue.widgets.ReaderMediaPlayer;
import com.lectek.android.lereader.ui.login_leyue.UserLoginLeYueNewActivity;
import com.lectek.android.lereader.ui.specific.ActivityChannels;
import com.lectek.android.lereader.ui.specific.BookCommentActivity;
import com.lectek.android.lereader.ui.specific.ContentInfoActivityLeyue;
import com.lectek.android.lereader.ui.specific.ScoreExchangeBookActivity;
import com.lectek.android.lereader.ui.specific.ScoreRuleActivity;
import com.lectek.android.lereader.ui.specific.SlideActivityGroup;
import com.lectek.android.lereader.utils.AES;
import com.lectek.android.lereader.utils.CommonUtil;
import com.lectek.android.lereader.utils.DialogUtil;
import com.lectek.android.lereader.utils.Html;
import com.lectek.android.lereader.utils.ToastUtil;
import com.lectek.android.lereader.widgets.PullRefreshLayout;
import com.lectek.lereader.core.bookformats.BookInfo;
import com.lectek.lereader.core.bookformats.Catalog;
import com.lectek.lereader.core.bookformats.FormatPlugin;
import com.lectek.lereader.core.bookformats.PluginManager;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.UMSsoHandler;

/**
 * 阅读界面
 */
public class BaseReaderActivityLeyue extends BaseContextActivity implements ITouchEventDispatcher
        ,YXHanlder,ILoadDataCallBack,INetLoadView, IReadCallback, PullRefreshLayout.OnPullListener, PullRefreshLayout.OnPullStateListener, AbsVerGestureAnimController.IVertialTouchEventDispatcher {
    private final String EVENT_SHOW_BUY_POINT_ID = "ShowBuyPoint";
    private final String VALUE_SHOW_BUY_POINT_COMFIRM = "点击确认";
    private final String VALUE_SHOW_BUY_POINT_CANCEL = "点击取消";

    /**内容密钥 适用于本地epub书籍解密*/
    private String secretKey = null;
    private final String PAGE_NAME = "阅读界面";
    @SuppressWarnings("unused")
    private static final String TAG = BaseReaderActivityLeyue.class.getSimpleName();
    private static final String EXTRA_BOOK = "extra_book";
    private BaseReaderActivityLeyue this_ = this;
    private IReaderView mReadView;
    private ViewGroup mReadViewLay;
    private CatalogView mCatalogView;
    private RelativeLayout mCatalogLay;
    private Book mBook;
    private ReaderMenuPopWin mReaderMenuPopWin;
    private int toolbarLP;
    private int toolbarRP;
    private int toolbarTP;
    private int toolbarBP;
    private int screenWidth;
    private int screenHeight;
    private int toolTouchAreaW;
    private int toolTouchAreaH;
    private boolean isInit;
    private boolean isShowBuyDialog;
    private BookMarkDB bookMarkHandle;
    private ClickDetector mClickDetector;
    private PreferencesUtil mPreferencesUtil;

    private UmengShareUtils utils = null;
    private boolean isShareQZone = false;
    private PopupWindow popupWindow;
    private ScoreUploadModel mUploadModel;

    private final String EVENT_ID = "BaseReaderActivityLeyue";

    private ITerminableThread mInitThread;
    private RecommendedBookViewModel mRecommendedBookViewModel;

    public final ArrayListObservable<ItemRecommendBook> bRecommendItems = new ArrayListObservable<ItemRecommendBook>(ItemRecommendBook.class);
    public final BooleanObservable bRecommendedLoadVisible = new BooleanObservable(true);
    public final BooleanObservable bRecommendedRelatedTagVisible = new BooleanObservable(false);

    public OnItemClickCommand bGridItemClickedCommand = new OnItemClickCommand() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            ItemRecommendBook book = bRecommendItems.get(position);
            if(!TextUtils.isEmpty(book.outBookId)){
                ActivityChannels.gotoLeyueBookDetail(this_, book.outBookId,
                        LeyueConst.EXTRA_BOOLEAN_IS_SURFINGREADER, true,
                        LeyueConst.EXTRA_LE_BOOKID, book.bookId
                );
            }else{
                ActivityChannels.gotoLeyueBookDetail(this_, book.bookId);
            }
            buyDialog.dismiss();
            buyDialog = null;

            finish();
        }
    };

    private Dialog dialog;

    public static void openActivity(Context context, Book book) {
        openActivity(context, book, -1, -1);
    }

    public static void openActivity(final Context context, final Book book, boolean isPlayAnimation){
        openActivity(context, book, -1, -1, isPlayAnimation);
    }

    private static  void open(final Context context, final Book book, int requestChapterIndex, int requestPageIndex){
        Intent intent = new Intent(context, BaseReaderActivityLeyue.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_BOOK, book);
        intent.putExtra(EXTRA_REQUEST_CHAPTERID, requestChapterIndex);
        intent.putExtra(EXTRA_REQUEST_POSITION, requestPageIndex);
        context.startActivity(intent);
    }

    private static final String EXTRA_REQUEST_CHAPTERID = "extra_chapter_id";
    private static final String EXTRA_REQUEST_POSITION = "extra_chapter_position";
    private int mRequestChapterIndex = -1, mRequestPageIndex = -1;
    public static void openActivity(final Context context, final Book book, final int requestChapterIndex, final int requestPageIndex){
        if(checkPdfPlugin((Activity) context,book, new ExPluginManager.PluginInstallCallback(){

            @Override
            public void onSuccess(Object paramObject) {
                open(context, book, requestChapterIndex, requestPageIndex);
            }
        })){
            open(context, book, requestChapterIndex, requestPageIndex);
        }
    }

    public static void openActivity(final Context context, final Book book, final int requestChapterIndex, final int requestPageIndex, boolean isPlayAnimation){
        if(isPlayAnimation){
            if(checkPdfPlugin((Activity) context,book, new ExPluginManager.PluginInstallCallback(){

                @Override
                public void onSuccess(Object paramObject) {
                    OpenBookAnimManagement.getInstance().starOpenBookAnim(new PreRunnable() {

                        @Override
                        public boolean run() {
                            openActivity(context, book, requestChapterIndex, requestPageIndex);
                            return true;
                        }
                    });
                }
            })){
                OpenBookAnimManagement.getInstance().starOpenBookAnim(new PreRunnable() {

                    @Override
                    public boolean run() {
                        openActivity(context, book, requestChapterIndex, requestPageIndex);
                        return true;
                    }
                });
            }
        }else{
            openActivity(context, book, requestChapterIndex, requestPageIndex);
        }
    }

    private static boolean checkPdfPlugin(Activity activity, Book book, Callback callback){
    	if(book.getBookFormatType() == null)
    		return true;
        if(book.getBookFormatType().equals(DownloadHttpEngine.VALUE_DOWNLOAD_TYPE_PDF)){
            ExPluginManager pluginManager = new ExPluginManager(activity, ExPluginType.PDF_SO, callback);
            if (pluginManager.checkPlugin()){
                return true;
            }else{
                pluginManager.install();
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        mBook = (Book) intent.getSerializableExtra(EXTRA_BOOK);
        mRequestChapterIndex = intent.getIntExtra(EXTRA_REQUEST_CHAPTERID, -1);
        mRequestPageIndex = intent.getIntExtra(EXTRA_REQUEST_POSITION, -1);
        if (mBook == null) {
            finish();
            return;
        }
        setContentView(R.layout.activity_reader_lay);
        mPreferencesUtil = PreferencesUtil.getInstance(this_);
        bookMarkHandle = BookMarkDB.getInstance();

        initPullView();
        mReadViewLay = (ViewGroup) findViewById(R.id.read_view);
        mCatalogLay = (RelativeLayout) findViewById(R.id.content_lay);

        startInitViewThread();

        overridePendingTransition(0, 0);

        //初始化分享地址
        if(DownloadHttpEngine.VALUE_DOWNLOAD_TYPE_CEB.equals(mBook.getBookFormatType())){
            UmengShareUtils.contentUrl = LeyueConst.WX_YYB_PATH + mBook.getBookId() + "&bookType=1";	//bookType=1代表是天翼阅读的书籍
        }else{
            UmengShareUtils.contentUrl = LeyueConst.WX_YYB_PATH + mBook.getBookId() + "&bookType=0";	//bookType=0代表是乐阅的书籍
        }
    }

    private void initView(){
        initScreenParams();
        initReaderCatalogView();
        showReaderContentView();
        initMenu();
        initClickDetector();
        initHelpView();
        mTipDialogBroadcastReceiver = new TipReceiver();
        registerReceiver(mTipDialogBroadcastReceiver,
                new IntentFilter(ContentInfoActivityLeyue.ACTION_SHOW_TIP_DIALOG_AFTER_SHARE));

        mRecommendedBookViewModel = new RecommendedBookViewModel(this_, this, mBook.getBookId());
        mRecommendedBookViewModel.onStart();
    }

    private Animation mRotateUpAnimation;
    private Animation mRotateDownAnimation;
    private PullRefreshLayout mPullLayout;
    private TextView mActionText;
    private TextView mTimeText;
    private ImageView mBookMarkSignImage;
    private View mProgress;
    private View mActionImage;
    protected void initPullView() {
        mRotateUpAnimation = AnimationUtils.loadAnimation(this_,
                R.anim.rotate_up);
        mRotateDownAnimation = AnimationUtils.loadAnimation(this_,
                R.anim.rotate_down);

        mPullLayout = (PullRefreshLayout) findViewById(R.id.pull_container);
        mPullLayout.setOnActionPullListener(this);
        mPullLayout.setOnPullStateChangeListener(this);

        mProgress = findViewById(android.R.id.progress);
        mActionImage = findViewById(android.R.id.icon);
        mActionText = (TextView) findViewById(R.id.pull_note);
        mTimeText = (TextView) findViewById(R.id.refresh_time);
        mBookMarkSignImage = (ImageView) findViewById(R.id.iv_book_mark_sign);

        mTimeText.setText(R.string.note_not_update);
        mActionText.setText(R.string.note_pull_down);
    }
    @Override
    public Object onRetainNonConfigurationInstance() {
        if(mReadView != null) {
            Object o = mReadView.onActivityRetainNonConfigurationInstance();
            if(o != null) {
                return o;
            }
        }
        return super.onRetainNonConfigurationInstance();
    }

    private void initScreenParams() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        toolTouchAreaW = screenWidth >> 1;
        toolTouchAreaH = screenHeight >> 1;
        toolbarLP = toolTouchAreaW >> 1;
        toolbarRP = screenWidth - toolbarLP;
        toolbarTP = toolTouchAreaH >> 1;
        toolbarBP = screenHeight - toolbarTP;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 横竖屏切换后需要初始化一些屏幕参数
        initScreenParams();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mInitThread != null){
            mInitThread.cancel();
            mInitThread = null;
        }
        ThreadPoolFactory.destroyReaderThreadPools();
        BookMarkDatas.getInstance().clearCache();
        if(buyDialog != null) {
            buyDialog.dismiss();
            buyDialog = null;
        }

        if(mUploadModel != null) {
            mUploadModel.release();
        }

        if(mRecommendedBookViewModel != null) {
            mRecommendedBookViewModel.finish();
        }

        if(mReadView != null){
            BookMark bookmark = mReadView.newSysBookmark();
            mReadView.onDestroy();
            mReadView = null;
            if(bookmark != null){
                if(DownloadPresenterLeyue.checkBookDownloadedExist(mBook.getBookId())){
                    DownloadPresenterLeyue.updateDownloadinfoTime(mBook.getBookId());
                }else{
                    DownloadPresenterLeyue.addBookDownloadedInfo(mBook, secretKey);
                }
                BookMarkDB.getInstance().updateOrCreateSysBookMark(bookmark, bookmark.getIsRecentRead() == BookMark.RECENT_READ_STATUS_RECENT 
                													|| bookmark.getChapterID() > 0 || bookmark.getPosition() > 0);
                sendBroadcast(new Intent(AppBroadcast.ACTION_UPDATE_BOOKSHELF));
            }
        }
        MobclickAgent.onKVEventEnd(this_, EVENT_ID + MobclickAgentUtil.findTypePosition(R.array.type, mBook.getBookType()), "reader");
        if (mTipDialogBroadcastReceiver!=null) {
            unregisterReceiver(mTipDialogBroadcastReceiver);
            mTipDialogBroadcastReceiver = null;
        }
        if(isInit){
            SyncPresenter.startSyncLocalSysBookMarkTask();
            SyncPresenter.startSyncTask(mBook.getBookId());
        }
    }
    BroadcastReceiver mTipDialogBroadcastReceiver;

    private void initClickDetector(){
        mClickDetector = new ClickDetector(new OnClickCallBack() {
            @Override
            public boolean onLongClickCallBack(MotionEvent event) {
                return false;
            }

            @Override
            public boolean onClickCallBack(MotionEvent ev) {
                float x = ev.getX();
                float y = ev.getY();
                if (mReadView.dispatchClickEvent(ev)) {
                    return true;
                } else if (x > toolbarLP && x < toolbarRP &&
                        y > toolbarTP && y < toolbarBP) {
                    showMenu();
                    return true;
                }
                return false;
            }

            @Override
            public void dispatchTouchEventCallBack(MotionEvent event) {
                onTouchEvent(event);
            }
        },false);
    }

    private void initMenu() {
        mReaderMenuPopWin = new ReaderMenuPopWin(mReadViewLay, this, mBook,
                new IActionCallback() {
                    @Override
                    public void onShowReaderCatalog() {
                        if(mCatalogView != null){
                            mCatalogView.showReaderCatalogView(mCatalogLay);
                        }
                    }

                    @Override
                    public void onSaveUserBookmark() {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onGotoPage(int pageNum) {
                        mReadView.gotoPage(pageNum, true);
                    }

                    @Override
                    public void onGotoBuyBook() {
                        onBuyAction();
                    }

                    @Override
                    public void onDeleteUserBookmark() {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public boolean isNeedBuy() {
                        if (mBook.isOrder()) {
                            return false;
                        }
                        int feeIndex = getBuyIndex();
                        if(feeIndex != -1){
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public int getPageNums() {
                        return mReadView.getMaxReadProgress();
                    }

                    @Override
                    public int getCurPage() {
                        return mReadView.getCurReadProgress();
                    }

                    @Override
                    public boolean canAddUserBookmark() {
                        // TODO Auto-generated method stub
                        return false;
                    }

                    @Override
                    public void gotoPreChapter() {
                        mReadView.gotoPreChapter();
                    }

                    @Override
                    public void gotoNextChapter() {
                        mReadView.gotoNextChapter();
                    }

                    @Override
                    public boolean hasPreChapter() {
                        return mReadView.hasPreChapter();
                    }

                    @Override
                    public boolean hasNextChapter() {
                        return mReadView.hasNextChapter();
                    }

                    @Override
                    public int getLayoutChapterProgress() {
                        return mReadView.getLayoutChapterProgress();
                    }

                    @Override
                    public int getLayoutChapterMax() {
                        return mReadView.getLayoutChapterMax();
                    }

                    @Override
                    public void shareClick(View v) {
                        dismissMenu();
                        onShareAction(v);
                    }

                    @Override
                    public void onGotoDetail() {
                        String bookId = mBook.getBookId();
                        if(!TextUtils.isEmpty(cebBookId)){
                            ActivityChannels.gotoLeyueBookDetail(this_, cebBookId,
                                    LeyueConst.EXTRA_BOOLEAN_IS_SURFINGREADER, true,
                                    LeyueConst.EXTRA_LE_BOOKID, bookId
                            );
                        }else{
                            ActivityChannels.gotoLeyueBookDetail(this_, bookId);
                        }
                        finish();
                    }

                    @Override
                    public void onGotoCommend() {
                        String bookId = mBook.getBookId();
                        boolean mIsSurfingReader = false;
                        if(!TextUtils.isEmpty(cebBookId)){
                            mIsSurfingReader = true;
                        }
                        BookCommentActivity.openActivity(this_, bookId, cebBookId, mIsSurfingReader);
                        finish();
                    }

                    @Override
                    public void onSearch(int direction, String keyWord) {
                        mReadView.search(direction, keyWord);
                    }

                    @Override
                    public BookInfo getBookInfo() {
                        return mReadView.getBookInfo();
                    }
                });
    }

    public void onShareAction(View view){
        if (view == null){
            view = mReadViewLay;
        }
        if (mBook!=null) {
            utils = new UmengShareUtils();
            utils.baseInit(this_);
            utils.setMailSubjectTitle("书籍分享");
            String imagePath = null;
            if (!TextUtils.isEmpty(mBook.getCoverPath())) {
                imagePath = String.valueOf(mBook.getCoverPath().hashCode());
            }
            Bitmap bitmap = null;
            if (!TextUtils.isEmpty(imagePath)) {
                bitmap = CommonUtil.getImageInSdcard(imagePath);
            }
            utils.setShareInfo(this_, new UmengShareInfo(
                    getString(R.string.share_for_book, mBook.getBookName(),UmengShareUtils.contentUrl), ""),bitmap);
            if (popupWindow == null) {
                popupWindow = utils.showPopupWindow(this_,
                        view,this_,snsListener);
            }else {
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                UmengShareUtils.popWindowShow(view, popupWindow);
            }
            mUploadModel = new ScoreUploadModel();
            mUploadModel.addCallBack(BaseReaderActivityLeyue.this);
        }else {
            ToastUtil.showToast(this_, "暂时无法分享，请重新进入该界面！");
        }
    }

    /**
     * 初始化目录视图
     */
    private void initReaderCatalogView() {
        mCatalogView = new CatalogView(this_, mReadView,getBuyIndex(), mBook.isOrder());
        mCatalogView.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mReadViewLay.setVisibility(View.VISIBLE);
                mCatalogLay.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 显示阅读帮助界面
     */
    private void initHelpView(){
        if(PreferencesUtil.getInstance(this_).isReadBookHelpTip()){
            final View readBookHelpView = findViewById(R.id.help_lay);
            readBookHelpView.setVisibility(View.VISIBLE);
            readBookHelpView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    PreferencesUtil.getInstance(this_).setReadBookHelpTip(false);
                    readBookHelpView.setVisibility(View.GONE);
                }
            });
        }
    }

    private void showMenu() {
        // 由于popupwindow设置了softinputmode为adjustPan，不设置偏移位置的话，popupwindow的顶部会被status bar遮住
        int height = ClientInfoUtil.screenHeight;
        int orientation = ReadSetting.getInstance(this_).getOrientationType();
        if(orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            height = ClientInfoUtil.screenWidth;
        }
//		mReaderMenuPopWin.showAsDropDown(0, -height + PreferencesUtil.getInstance(this_).getStatusBarHeight());
        if (!mReaderMenuPopWin.isShowing() && !mCatalogLay.isShown()) {
            Rect frame = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            mReaderMenuPopWin.showAtLocation(Gravity.DISPLAY_CLIP_VERTICAL,0,frame.top);
        }
    }

    private void dismissMenu() {
        if (mReaderMenuPopWin.isShowing()) {
            mReaderMenuPopWin.dismiss();
        }
    }

    protected boolean showReaderContentView() {
        if(mCatalogView != null && mCatalogView.isShown()){
            mCatalogView.dismissCatalogView();
            return false;
        }
        return true;
    }

    protected void reflashCurrentPageBookmark() {
        if (Build.VERSION.SDK_INT >= 10) {
            // saveSystemBookmarkInUi();
        }
    };

    private boolean isCartoonShowed(){
        //TODO: 卡通视图
        BookInfo book = mReadView.getBookInfo();
        if(book != null  && book.isCartoon){
            return true;
        }
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(isInit && mReadView.onActivityDispatchKeyEvent(event)){
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!isInit) {
            return false;
        }
        if(PreferencesUtil.getInstance(this_).isReadBookHelpTip()){
            return super.dispatchTouchEvent(ev);
        }
        if(mReadView.onActivityDispatchTouchEvent(ev)){
            return false;
        }
        if(mCatalogLay.isShown() /*|| mHelpView.isShown()*/){
            return super.dispatchTouchEvent(ev);
        }
        if(mReadView.handlerSelectTouchEvent(ev,this)){
            return false;
        }
        if(mClickDetector.onTouchEvent(ev, isCartoonShowed())){
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }

    private AbsVerGestureAnimController mAbsVerGestureAnimController = new AbsVerGestureAnimController();

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isInit) {
            return false;
        }
        if(isPullEnabled() && mAbsVerGestureAnimController.handlerTouch(ev, this)){
            return false;
        }
        return mReadView.handlerTouchEvent(ev);
    }
    @Override
    public void verticalTouchEventCallBack(MotionEvent ev) {
        mPullLayout.dispatchTouchEvent(ev);
    }

    @Override
    public void unVerticalTouchEventCallBack(MotionEvent ev) {
        onTouchEvent(ev);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("menu");// 必须创建一项
        return super.onCreateOptionsMenu(menu);
    }

//	@Override
//	public boolean onMenuOpened(int featureId, Menu menu) {
//		if (!mReaderMenuPopWin.isShowing()) {
//			showMenu();
//		} else {
//			dismissMenu();
//		}
//		return false;
//	}

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (!isInit && keyCode != KeyEvent.KEYCODE_BACK) {
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mReaderMenuPopWin != null && mReaderMenuPopWin.isShowing()) {
                dismissMenu();
                return true;
            }
            if (mCatalogLay != null && mCatalogLay.isShown()) {
                showReaderContentView();
                return true;
            }
        }
        ReaderMediaPlayer mediaPlayer = ReaderMediaPlayer.getInstance();
        boolean isNeedControlVolume = mediaPlayer != null && mediaPlayer.isNeedControlVolume();
        if(mPreferencesUtil.isVolumnTurnPage() && !isNeedControlVolume){
            if(keyCode == KeyEvent.KEYCODE_VOLUME_UP){
                return true;
            }
            if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
                if(mReadView != null){
                    return true;
                }
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!isInit && keyCode != KeyEvent.KEYCODE_BACK) {
            return true;
        }
        ReaderMediaPlayer mediaPlayer = ReaderMediaPlayer.getInstance();
        boolean isNeedControlVolume = mediaPlayer != null && mediaPlayer.isNeedControlVolume();
        if(mPreferencesUtil.isVolumnTurnPage() && !isNeedControlVolume){
            if(keyCode == KeyEvent.KEYCODE_VOLUME_UP){
                mReadView.gotoPrePage();
                return true;
            }
            if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
                mReadView.gotoNextPage();
                return true;
            }
        }
        if (keyCode == KeyEvent.KEYCODE_MENU && isInit) {//直接监听，不走模拟系统menu方式。由于不明原因，系统方式，onMenuOpen调不到
            if (!mReaderMenuPopWin.isShowing()) {
                showMenu();
            } else {
                dismissMenu();
            }
            return true;
        }
        if (!showReaderContentView()) {
            return true;
        }
        if (!LeyueConst.isLeyueVersion) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (LeyueConst.CHANNEL_360.equals(PkgManagerUtil.getUmengChannelId(this_))) {
                    if (DateUtil.isAvoidAccessBookCity(this_,PreferencesUtil.getInstance(this_).getAccessBookCityDeadline())) {
                        startActivity(new Intent(this, SlideActivityGroup.class));
                    }
                }else {
                    if (!PreferencesUtil.getInstance(getApplicationContext()).getIsFirstEnterApp(true)) {
                        startActivity(new Intent(this, SlideActivityGroup.class));
                    }
                }
                PreferencesUtil.getInstance(getApplicationContext()).setIsFirstEnterApp(false);
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private TextView mLoadingText;
    private void showLoadingViewInUIThread(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLoadingText = new TextView(this_);
                mLoadingText.setText(getResources().getString(R.string.reader_transition_tip));
                mLoadingText.setTextColor(Color.BLACK);
                mLoadingText.setTextSize(18);
                FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                param.gravity = Gravity.CENTER;
                mReadViewLay.addView(mLoadingText, param);
            }
        });
    }

    private void dismissLoadingViewInUiThread(){
        if (mLoadingText != null){
            mReadViewLay.removeView(mLoadingText);
        }
    }

    private void startInitViewThread() {
        mReadView = newReaderView(this_, mBook, secretKey, BaseReaderActivityLeyue.this);
        mInitThread = ThreadFactory.createTerminableThread(new Runnable(){
            @Override
            public void run() {
                //showLoadingViewInUIThread();
                if((mBook.getBookFormatType() == null || DownloadHttpEngine.VALUE_DOWNLOAD_TYPE_BOOK.equals(mBook.getBookFormatType()))){
                    if(mBook.isOnline()){//在线epub阅读区分漫画阅读
                        try {
                            BookTypeInfo bookTypeInfo = ApiProcess4Leyue.getInstance(this_).getBookType(mBook.getBookId());
                            if (bookTypeInfo != null && bookTypeInfo.getContentFormat() != null && bookTypeInfo.getContentFormat() == 1){
                                mReadView = new NetEpubCartoonView(this_, mBook, this_);
                            }
                        } catch (GsonResultException exception) {
                        }
                    }else{//本地epub阅读需要秘钥， 需要区分是否是漫画阅读器
                        try {
                            //获取秘钥
                            if(TextUtils.isEmpty(secretKey) && TextUtils.isDigitsOnly(mBook.getBookId())){//乐阅epub书籍秘钥验证
                                secretKey = DownloadPresenterLeyue.getDownloadinfoSecretKey(mBook.getBookId());
                                if(!TextUtils.isEmpty(secretKey)){
                                    secretKey = AES.decrypt(secretKey);
                                }else{
                                    BookDecodeInfo info = ApiProcess4Leyue.getInstance(this_).getBookDecodeKey(mBook.getBookId(), mPreferencesUtil.getUserId());
                                    if(info != null){
                                        secretKey = info.getEncodeSecretKey();
                                        if(!TextUtils.isEmpty(secretKey)){
                                            DownloadPresenterLeyue.updateDownloadinfoSecretKey(AES.encrypt(secretKey), mBook.getBookId());
                                        }
                                    }
                                }
                            }
                            //验证秘钥
                            FormatPlugin mPlugin = null;
                            try {
                                mPlugin = PluginManager.instance().getPlugin(mBook.getPath(), secretKey);
                            }catch (Exception e){
                            }
                            try {
                                if (mPlugin == null || mPlugin.getCatalog().isEmpty()){//key已经过期，重新获取
                                    BookDecodeInfo info = ApiProcess4Leyue.getInstance(this_).getBookDecodeKey(mBook.getBookId(), mPreferencesUtil.getUserId());
                                    if(info != null){
                                        secretKey = info.getEncodeSecretKey();
                                        if(!TextUtils.isEmpty(secretKey)){
                                            DownloadPresenterLeyue.updateDownloadinfoSecretKey(AES.encrypt(secretKey), mBook.getBookId());
                                        }
                                        mPlugin = PluginManager.instance().getPlugin(mBook.getPath(), secretKey);
                                    }
                                }
                            }catch (Exception e){
                            }
                            BookInfo bookInfo = mPlugin.getBookInfo();
                            if(bookInfo.isCartoon){
                                mReadView = new EpubCartoonView(this_, mBook, this_);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                BookMarkDatas.getInstance().loadBookMarks(mBook.getBookId());
                if(mRequestChapterIndex < 0){
                    BookMark bookMark = bookMarkHandle.getSepecificBookMark(mBook.getBookId());
                    if(bookMark != null){
                        mRequestChapterIndex = bookMark.getChapterID();
                        mRequestPageIndex = bookMark.getPosition();
                    }
                }
                mRequestChapterIndex = mRequestChapterIndex < 0? 0:mRequestChapterIndex;
                mRequestPageIndex = mRequestPageIndex < 0? 0:mRequestPageIndex;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //dismissLoadingViewInUiThread();
                        if (mReadView == null){
                            dealInitResult(IReaderView.ERROR_GET_CONTENT_INFO);
                            return;
                        }
                        mReadViewLay.addView(mReadView.getContentView());
                        mReadView.onCreate(null);
                        mInitThread =  ThreadFactory.createTerminableThread(new Runnable(){
                            @Override
                            public void run() {
                                int resultCode = mReadView.onInitReaderInBackground(mRequestChapterIndex, mRequestPageIndex, secretKey);
                                // 读章节信息
                                dealInitResult(resultCode);
                            }
                        });
                        mInitThread.start();
                    }
                });
            }
        });
        mInitThread.start();
    }

    private void dealInitResult(final int resultCode){
        if (mInitThread == null){
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(resultCode != IReaderView.SUCCESS){
                    ToastUtil.showToast(BaseReaderActivityLeyue.this ,
                            resultCode == IReaderView.ERROR_GET_CATALOG_INFO? R.string.book_get_book_catalog_fault:
                                    resultCode == IReaderView.ERROR_BOOK_SECRET_KEY? R.string.book_decode_localbook:
                                            resultCode == IReaderView.ERROR_BOOK_OFFLINE? R.string.result_find_book_offline: R.string.book_get_book_info_fault);
                    finish();
                }else{
                    isInit = true;
                    initView();
                }
            }
        });
    }


    private Runnable breakReminder = new Runnable(){
        @Override
        public void run() {
            ToastUtil.showToast(this_, R.string.reader_menu_item_rest_reminder_toast);
            startRestRemark();
        }
    };

    @Override
    protected void onStop() {
        stopRestRemark();
        super.onStop();
    };

    @Override
    protected void onResume() {
        super.onResume();
        // 添加友盟统计
        startRestRemark();
        setScreenLockTime();
        MobclickAgent.onPageStart(PAGE_NAME);
        MobclickAgent.onResume(this);

        String price = "0";

        if(!TextUtils.isEmpty(mBook.getPromotionPrice())){
            price = mBook.getPromotionPrice();
        }else if(!TextUtils.isEmpty(mBook.getPrice())){
            price = mBook.getPrice();
        }

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("book_name", mBook.getBookName() + (mBook.isOrder() || (TextUtils.isEmpty(mBook.getPromotionPrice()) && TextUtils.isEmpty(mBook.getPrice()))? "" : "(试读)"));
        map.put("book_type", mBook.getBookType());
        map.put("book_price",price + (mBook.isOrder() || (TextUtils.isEmpty(mBook.getPromotionPrice()) && TextUtils.isEmpty(mBook.getPrice()))? "" : "(试读)"));
        MobclickAgent.onKVEventBegin(this_, EVENT_ID + MobclickAgentUtil.findTypePosition(R.array.type, mBook.getBookType()), map, "reader");
    }

    private void startRestRemark(){
        stopRestRemark();
        int restType = mPreferencesUtil.getRestReminder();
        long time = 0;
        switch (restType) {
            case 0:
                time = 45 * 60 * 1000;
                break;
            case 1:
                time = 90 * 60 * 1000;
                break;
            case 2:
                time = 120 * 60 * 1000;
                break;
            default:
                break;
        }
        MyAndroidApplication.getHandler().postDelayed(breakReminder, time);
    }

    private void stopRestRemark(){
        MyAndroidApplication.getHandler().removeCallbacks(breakReminder);
    }

    private void setScreenLockTime(){
        int time = -1;//无锁屏时间
        int lockType = mPreferencesUtil.getScreensaver();
        switch (lockType) {
            case 0:
                time = 2 * 60 * 1000;
                break;
            case 1:
                time = 5 * 60 * 1000;
                break;
            case 2:
                time = 10 * 60 * 1000;
                break;
            case 3:

                break;
            default:
                break;
        }
        setScreenLock(time);
    }

    private void setScreenLock(int lockTime){
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, lockTime);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 添加友盟统计
        MobclickAgent.onPageEnd(PAGE_NAME);
        MobclickAgent.onPause(this);
    }

    @Override
    public void dispatchTouchEventCallBack(MotionEvent ev) {
        dispatchTouchEvent(ev);
    }

    /** 获取购买点*/
    private int getBuyIndex(){
        if(mCatalogView != null){
            int index = mCatalogView.getFeeIndex();
            if(index >= 0){
                return index;
            }
        }
        return getFeeIndexByFeeStart(mBook.getFeeStart());
    }

    private int getFeeIndexByFeeStart(String feeStart){
        if(!TextUtils.isEmpty(feeStart) && !"null".equals(feeStart)){
            try {
                return Integer.valueOf(feeStart);
            } catch (Exception e) {
                LogUtil.e(e.getMessage());
            }
            try {
                int start = feeStart.lastIndexOf("chapter")+7;
                int end = feeStart.lastIndexOf(".");
                return Integer.valueOf(feeStart.substring(start, end));
            } catch (Exception e) {
                LogUtil.e(e.getMessage());
            }
        }
        return -1;
    }


    @Override
    public void onNetworkChange(boolean isAvailable) {
        super.onNetworkChange(isAvailable);
        if(isAvailable){
            if(NetworkUtil.isNetAvailable(this_)){

            }else {
                ToastUtil.showToast(this_, "服务器连接异常，请稍后再试");
            }
        }
    }

    private void onBuyAction(){
        onPostDealBuy();
        if (true){
            return;
        }
        String bookId = mBook.getBookId();
        if(!TextUtils.isEmpty(cebBookId)){
            ActivityChannels.gotoLeyueBookDetail(this_, cebBookId,
                    LeyueConst.EXTRA_BOOLEAN_IS_SURFINGREADER, true,
                    LeyueConst.EXTRA_LE_BOOKID, bookId,
                    ContentInfoActivityLeyue.BUY_BOOK_FROM_READER_CODE, true
            );
        }else{
            ActivityChannels.gotoLeyueBookDetail(this_, bookId, ContentInfoActivityLeyue.BUY_BOOK_FROM_READER_CODE, true);
        }
        finish();
    }

    Dialog buyDialog;
    private void getBuyDialog(Context context){

        if(isShowBuyDialog)
            return;
        isShowBuyDialog = true;

        String tempPrice = mBook.getPrice();
        if(!TextUtils.isEmpty(mBook.getLimitPrice())){
            tempPrice = mBook.getLimitPrice();
        }else if(!TextUtils.isEmpty(mBook.getPromotionPrice())){
            tempPrice = mBook.getPromotionPrice();
        }


        final String realPrice = tempPrice;

        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        buyDialog = new Dialog(context, R.style.notBackgroundDialog);
        LayoutParams params = new LayoutParams(display.getWidth(), display.getHeight());
        buyDialog.setContentView(LayoutInflater.from(context).inflate(R.layout.buy_dialog, null),params);

        //获取阅读的背景图片
        int backgroundResId = ReadSetting.getInstance(this_).getThemeBGImgRes();
        View buyDialogLayout = buyDialog.findViewById(R.id.buy_dialog_bg);
        if(backgroundResId >= 0){
            buyDialogLayout.setBackgroundResource(backgroundResId);
        }else{
            buyDialogLayout.setBackgroundColor(ReadSetting.getInstance(this_).getThemeBGColor());
        }
        buyDialogLayout.setOnTouchListener(new OnTouchListener() {

            //用户在最后一页向左滑动界面进入书架
            float startX = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    //当用户点击画面左侧屏幕二分之一的区域为退出当前界面
                    if (event.getX() <= getWindow().getWindowManager().getDefaultDisplay().getWidth() / 2) {
                        if (buyDialog.isShowing()) {
                            buyDialog.dismiss();
                        }
                        return true;
                    }

                    startX = event.getX();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    float length = event.getX() - startX;
                    if (length > 0 && buyDialog != null) {
                        if (Math.abs(length) >= getWindow().getWindowManager().getDefaultDisplay().getWidth() * 1 / 5) {
                            if (buyDialog.isShowing()) {
                                buyDialog.dismiss();
                            }
                        }
                    }
                    startX = 0;
                }
                return false;
            }
        });

        TextView buyDialogTipTV = (TextView) buyDialog.findViewById(R.id.text_two);//提示语
        Button buyBtn = (Button) buyDialog.findViewById(R.id.buy_btn);//购买按钮或下载按钮
        View exchangeBtn = buyDialog.findViewById(R.id.exchange_btn);//免费兑书

        //当前为已经订购的书籍
        if(mBook.isDownloadFullVersonBook()){
            buyBtn.setText(getResources().getString(R.string.btn_download_tip));
            buyDialogTipTV.setText(getResources().getString(R.string.text_download_book_tip));
            exchangeBtn.setVisibility(View.GONE);
        }else{
            //当前为未订购书籍
            buyBtn.setText(getResources().getString(R.string.content_info_btn_buy, tempPrice));
            buyDialogTipTV.setText(getResources().getString(R.string.text_buy_book_tip));
            exchangeBtn.setVisibility(View.VISIBLE);
        }
        if(!TextUtils.isEmpty(cebBookId)){
            exchangeBtn.setVisibility(View.GONE);
        }

        //获取前景的字体颜色
        int textColorResId = ReadSetting.getInstance(this_).getThemeTextColor();
        ((TextView) buyDialog.findViewById(R.id.text_one)).setTextColor(textColorResId);
        ((TextView) buyDialog.findViewById(R.id.text_two)).setTextColor(textColorResId);

        TextView exchangeTV = (TextView) buyDialog.findViewById(R.id.free_exchange_tv);
        exchangeTV.setText(Html.fromHtml("<u>积分免费兑书</u>"));

        ImageView newIv = (ImageView) buyDialog.findViewById(R.id.new_anim_iv);
        newIv.setBackgroundResource(R.anim.word_new_animation);
        AnimationDrawable newAnimation = (AnimationDrawable)newIv.getBackground();
        newAnimation.start();

        buyBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBuyAction();
            }
        });

        exchangeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isUserLoginSuccess = PreferencesUtil.getInstance(this_).getIsLogin();
                if(!isUserLoginSuccess) {	//联网且游客身份，必须先登陆
//					Intent intent = new Intent(this_, UserLoginActivityLeYue.class);
                    Intent intent = new Intent(this_, UserLoginLeYueNewActivity.class);
                    startActivity(intent);
                    return;
                }
                ContentInfoLeyue info = new ContentInfoLeyue();
                info.setBookId(mBook.getBookId());
                info.setBookName(mBook.getBookName());
                info.setPrice(mBook.getPrice());
                info.setPromotionPrice(mBook.getPromotionPrice());
                if(!TextUtils.isEmpty(mBook.getLimitPrice())){
                    info.setLimitType(ContentInfoViewModelLeyue.LIMIT_TYPE_PRICE);
                    info.setLimitPrice(Double.parseDouble(mBook.getLimitPrice()));
                }
                info.setCoverPath(mBook.getCoverPath());
                ScoreExchangeBookActivity.openActivity(BaseReaderActivityLeyue.this, info);
            }
        });

        buyDialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                isShowBuyDialog = false;
            }
        });

        buyDialog.show();
    }



    public void getRecommendBook(ArrayList<ContentInfoLeyue> list){

        for(int i = 0; i < list.size(); i++){
            ContentInfoLeyue info = list.get(i);

            ItemRecommendBook book = new ItemRecommendBook();
            book.bookId = info.getBookId();
            book.outBookId = info.getOutBookId();
            book.bRecommendedBookName.set(info.getBookName());
            book.bRecommendedAuthorName.set(info.getAuthor());
            book.bRecommendedCoverUrl.set(info.getCoverPath());
            bRecommendItems.add(book);
        }

    }

    private void getRecommendBookDialog(Context context){
        Display display = getWindowManager().getDefaultDisplay();
//		final Dialog buyDialog = new Dialog(context, R.style.notBackgroundDialog);
        if(buyDialog == null) {

            buyDialog = new Dialog(context, R.style.notBackgroundDialog);
            LayoutParams params = new LayoutParams(display.getWidth(), display.getHeight());
            InflateResult result = Binder.inflateView(context, R.layout.recommend_book_layout, null, false);
            Binder.bindView(context, result, this);
            buyDialog.setContentView(result.rootView,params);

            //获取阅读的背景图片
            int backgroundResId = ReadSetting.getInstance(this_).getThemeBGImgRes();
            View recommendedLayout = buyDialog.findViewById(R.id.recommended_book_bg);
            if(backgroundResId >= 0){
                recommendedLayout.setBackgroundResource(backgroundResId);
            }else{
                recommendedLayout.setBackgroundColor(ReadSetting.getInstance(this_).getThemeBGColor());
            }
            recommendedLayout.setOnTouchListener(new OnTouchListener() {

                //用户在最后一页向左滑动界面进入书架
                float startX = 0;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN){

                        //当用户点击画面左侧屏幕二分之一的区域为退出当前界面
                        if(event.getX() <= getWindow().getWindowManager().getDefaultDisplay().getWidth()/2){
                            if(buyDialog.isShowing()){
                                buyDialog.dismiss();
                            }
                            return true;
                        }

                        startX = event.getX();
                    }else if(event.getAction() == MotionEvent.ACTION_UP){
                        float length = event.getX()-startX;
                        if(length > 0 && buyDialog != null){
                            if(Math.abs(length) >= getWindow().getWindowManager().getDefaultDisplay().getWidth()*1/5){
                                if(buyDialog.isShowing()){
                                    buyDialog.dismiss();
                                }
                            }
                        }
                        startX = 0;
                    }
                    return false;
                }
            });

        }

        buyDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ContentInfoActivityLeyue.REQUEST_CODE_EXCHANGE_BOOK) {
            if (resultCode == ScoreExchangeBookActivity.TAG_BUY_SUCCESS||resultCode == ScoreExchangeBookActivity.TAG_RELOAD) {
                ActivityChannels.gotoLeyueBookDetail(this_, mBook.getBookId());
                //TODO:刷新购买页内容为 ： 未完待续  <br/> 下载完整版，后面更多精彩... <br/> (按钮) 下载完整版 （点击进入书籍详情，同时传个唯一标识，去将书籍详情页面阅读按钮内容更改为：下载完整版）<br/>(隐藏积分兑书)
                finish();
            }
        }
        activityResult(requestCode, resultCode, data);
    }

    @Override
    public void handleForYiXin(int type) {
        ShareYiXin shareYiXin = new ShareYiXin(this_);
        if (shareYiXin.isYxInstall()) {
            if (!TextUtils.isEmpty(mBook.getCoverPath())) {
                String imagePath = String.valueOf(mBook.getCoverPath().hashCode());
                LogUtil.e("-----cover-localName=="+imagePath);
                shareYiXin.sendTextWithPic(new MutilMediaInfo("",getString(R.string.share_for_book_wx, mBook.getBookName()), imagePath,type,UmengShareUtils.contentUrl));
            }else {
                shareYiXin.sendTextWithPic(new MutilMediaInfo("",getString(R.string.share_for_book_wx, mBook.getBookName()), "",type,UmengShareUtils.contentUrl));
            }
        }else {
            Toast.makeText(this_, "你还没有安装易信！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void handleForWeiXin(int type) {
        ShareWeiXin shareWeiXin = new ShareWeiXin(this_);
        if (shareWeiXin.isWxInstall()) {
            if (shareWeiXin.isSupportVersion()) {
                if (!TextUtils.isEmpty(mBook.getCoverPath())) {
                    String imagePath = String.valueOf(mBook.getCoverPath().hashCode());
                    switch (type) {
                        case MutilMediaInfo.WX_FRIEND:
                            shareWeiXin.sendTextWithPic(new MutilMediaInfo("",getString(R.string.share_for_book_wx, mBook.getBookName()), imagePath,type,UmengShareUtils.contentUrl));
                            break;
                        case MutilMediaInfo.WX_FRIEND_ZONE:
                            shareWeiXin.sendTextWithPic(new MutilMediaInfo(getString(R.string.share_for_book_wx, mBook.getBookName()),"", imagePath,type,UmengShareUtils.contentUrl));
                            break;

                        default:
                            break;
                    }
                }else {
                    shareWeiXin.sendTextWithPic(new MutilMediaInfo(getString(R.string.share_for_book_title),getString(R.string.share_for_book_wx, mBook.getBookName()), "",type,LeyueConst.WX_YYB_PATH));
                }
            }else {
                Toast.makeText(this_, "请更新微信到最新版本！", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this_, "你还没有安装微信！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void handleForQQ() {
        //友盟3.2 sdk 支持本地图片分享 —— 【QQ分享内容为音乐，视频的时候，其形式必须为url;图片支持url跟本地图片类型.】
        if (utils!=null && !TextUtils.isEmpty(mBook.getCoverPath())) {
            String imagePath = String.valueOf(mBook.getCoverPath().hashCode());
            Bitmap bitmap = null;
            if (!TextUtils.isEmpty(imagePath)) {
                bitmap = CommonUtil.getImageInSdcard(imagePath);
            }
            QQShareContent qqShareContent = new QQShareContent();
            qqShareContent.setShareContent(getString(R.string.share_for_book, mBook.getBookName(),UmengShareUtils.contentUrl));
            qqShareContent.setTitle("软件分享");
            qqShareContent.setShareImage(new UMImage(this, bitmap));
            qqShareContent.setTargetUrl(UmengShareUtils.contentUrl);
            utils.shareForQQ(qqShareContent);
        }


    }

    @Override
    public void handleForQQZONE() {
        //Qzone 使用自定义分享接口
        if (utils!=null && !TextUtils.isEmpty(mBook.getCoverPath())) {
            utils.setShareInfo(this_, new UmengShareInfo(
                    getString(R.string.share_for_book, mBook.getBookName(),UmengShareUtils.contentUrl), mBook.getCoverPath()),null);
        }
        utils.shareToQzone(this_);
        isShareQZone = true;
    }

    @Override
    public void handleForSMS() {
        //短信不带图片
        if (utils!=null) {
            utils.setShareInfo(this_, new UmengShareInfo(
                    getString(R.string.share_for_book, mBook.getBookName(),UmengShareUtils.contentUrl), ""),null);
        }
    }

    @Override
    public void saveSourceId() {
        UmengShareUtils.LAST_SHARE_SOURCEID = mBook.getBookId();
        UmengShareUtils.shareContext = BaseReaderActivityLeyue.this;
    }

    public void activityResult(int requestCode, int resultCode, Intent data){
        if (!isShareQZone) {
            /**使用SSO授权必须添加如下代码 */
            UmengShareUtils utils = new UmengShareUtils();
            UMSsoHandler ssoHandler = utils.getSsoHandler(requestCode);
            if(ssoHandler != null){
                ssoHandler.authorizeCallBack(requestCode, resultCode, data);
            }
        }else {
            isShareQZone = false;
        }
    }

    public void uploadShareInfo(){
        if (UserScoreInfo.SINA.equals(UmengShareUtils.LAST_SHARE_TYPE) ||
                UserScoreInfo.QQ_FRIEND.equals(UmengShareUtils.LAST_SHARE_TYPE)) {
            if (mUploadModel!=null) {
                mUploadModel.start(UmengShareUtils.LAST_SHARE_TYPE,UmengShareUtils.LAST_SHARE_SOURCEID);
            }
        }
    }

    @Override
    public boolean onStartFail(String tag, String state, Object... params) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onPreLoad(String tag, Object... params) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onFail(Exception e, String tag, Object... params) {
        if (tag.equals(mUploadModel.getTag())) {
            ToastUtil.showToast(this_, R.string.share_record_fail_tip);
//			hideLoadView();
        }
        return false;
    }

    @Override
    public boolean onPostLoad(Object result, String tag, boolean isSucceed,
                              boolean isCancel, Object... params) {
        if (mUploadModel.getTag().equals(tag)) {
            ScoreUploadResponseInfo info = (ScoreUploadResponseInfo) result;
            CommonUtil.handleForShareTip(this_, info);
        }
        return false;
    }

    private SnsPostListener snsListener = new SnsPostListener() {

        @Override
        public void onStart() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onComplete(SHARE_MEDIA arg0, int eCode, SocializeEntity arg2) {
            LogUtil.e("--- eCode--"+ eCode);
            switch (arg0) {
                case QQ:
                    if (eCode == ShareConfig.SNS_SUCEESS_CODE) {
                        uploadShareInfo();
                    }else {
                        ToastUtil.showToast(this_, "分享失败");
                    }
                    break;
                case SINA:
                    if (eCode == ShareConfig.SNS_SUCEESS_CODE) {
                        uploadShareInfo();
                    }else if(eCode == 5016){
                        ToastUtil.showToast(this_, "分享内容重复");
                    }else {
                        ToastUtil.showToast(this_, "分享失败");
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private boolean mInLoading = false;
    @Override
    public void onPullOut() {
        if (!mInLoading) {
            if(isCurrentBookMarked()){
                mBookMarkSignImage.setImageResource(R.drawable.book_mark_unsign);
                mActionText.setText(R.string.book_label_del_pull_refresh);
            }else{
                mBookMarkSignImage.setImageResource(R.drawable.book_mark_sign);
                mActionText.setText(R.string.book_label_add_pull_refresh);
            }
            mActionImage.clearAnimation();
            mActionImage.startAnimation(mRotateUpAnimation);
        }

    }

    @Override
    public void onPullIn() {
        if (!mInLoading) {
            if(isCurrentBookMarked()){
                mBookMarkSignImage.setImageResource(R.drawable.book_mark_sign);
                mActionText.setText(R.string.book_label_del_pull);
            }else{
                mBookMarkSignImage.setImageResource(R.drawable.book_mark_unsign);
                mActionText.setText(R.string.book_label_add_pull);
            }

            mActionImage.clearAnimation();
            mActionImage.startAnimation(mRotateDownAnimation);
        }
    }

    /**
     * 当前页面是否书签
     */
    private boolean isCurrentBookMarked(){
        return BookMarkDatas.getInstance().findBookMarkPosition(mReadView.newUserBookmark()) != -1 ;
    }

    private final static int MSG_LOADING = 0x1;
    private final static int MSG_LOADED = 0x2;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            int what = msg.what;
            switch (what) {
                case MSG_LOADING:
                    //下拉后做的网路操作
                    if(isCurrentBookMarked()){
                        delBookLabel();
                    }else{
                        addBookLabel();
                    }
                    break;
                case MSG_LOADED:
                    dataLoaded();
                    break;

                default:
                    break;
            }
        };
    };

    @Override
    public boolean hasShowBookMark(int chapterId, int pageStart, int pageEnd) {
        return BookMarkDatas.getInstance().isPageMarked(chapterId, pageStart, pageEnd);
    }

    @Override
    public boolean setFreeStart_Order_Price(int feeStart, boolean isOrdered, String price, String limitPrice) {
        mBook.setOrder(isOrdered);
        mBook.setPromotionPrice(price);
        mBook.setLimitPrice(limitPrice);
        mBook.setFeeStart(feeStart+"");
        if(mCatalogView != null){
            int position = feeStart;
            mCatalogView.setFeeIndex(position, isOrdered);
        }
        return false;
    }

    /**
     * 添加书签
     */
    private void addBookLabel(){
        BookMark userMark = mReadView.newUserBookmark();
        if(BookMarkDatas.getInstance().addBookMark(userMark)){
            ToastUtil.showLongToast(this_, R.string.book_label_add_success);
            mReadView.getContentView().invalidate();
        }
        dataLoaded();
    }

    /**
     * 删除用户书签
     */
    private void delBookLabel(){
        BookMark userMark = mReadView.newUserBookmark();
        if(BookMarkDatas.getInstance().deleteBookMark(userMark)){
            ToastUtil.showToast(this_, R.string.book_label_del_success);
            mReadView.getContentView().invalidate();
        }
        dataLoaded();
    }
    /**刷新加载结束*/
    private void dataLoaded() {
        if (mInLoading) {
            mInLoading = false;
            mPullLayout.setEnableStopInActionView(false);
            mPullLayout.hideActionView();
            mActionImage.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.GONE);

            if (mPullLayout.isPullOut()) {
                mActionText.setText(R.string.note_pull_refresh);
                mActionImage.clearAnimation();
                mActionImage.startAnimation(mRotateUpAnimation);
            } else {
                mActionText.setText(R.string.note_pull_down);
            }
        }
    }
    @Override
    public void onSnapToTop() {
        if (!mInLoading) {
            mInLoading = true;
            mPullLayout.setEnableStopInActionView(false);
            mActionImage.clearAnimation();
            mActionImage.setVisibility(View.GONE);
            mProgress.setVisibility(View.VISIBLE);
            if(isCurrentBookMarked()){
                mBookMarkSignImage.setImageResource(R.drawable.book_mark_unsign);
                mActionText.setText(R.string.book_label_del_pull_loading);
            }else{
                mBookMarkSignImage.setImageResource(R.drawable.book_mark_sign);
                mActionText.setText(R.string.book_label_add_pull_loading);
            }
            mHandler.sendEmptyMessage(MSG_LOADING);
            mTimeText.setText(this_.getString(R.string.note_update_at, DateUtil.getCurrentTimeByMDHM()));
        }

    }
    @Override
    public void onShow() {

    }

    @Override
    public void onHide() {

    }

    @Override
    public boolean isPullEnabled() {
        return mReadView.getTextSelectHandler() != null;
    }

    class TipReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent == null){
                return;
            }
            final String soureName = intent.getStringExtra(ContentInfoActivityLeyue.ACTION_SHOW_TIP_DIALOG_AFTER_SHARE);
            if(ContentInfoActivityLeyue.ACTION_SHOW_TIP_DIALOG_AFTER_SHARE.equals(intent.getAction())
                    && CommonUtil.isOnCurrentActivityView(BaseReaderActivityLeyue.this)){
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        DialogUtil.commonConfirmDialog(BaseReaderActivityLeyue.this,
                                BaseReaderActivityLeyue.this.getString(R.string.share_tip),
                                BaseReaderActivityLeyue.this.getString(R.string.share_book_repeat_tip,soureName),
                                R.string.share_enter_tip,R.string.share_exit_tip,new DialogUtil.ConfirmListener() {

                            @Override
                            public void onClick(View v) {
                                ScoreRuleActivity.gotoScoreRuleActivity(BaseReaderActivityLeyue.this);
                            }
                        },null).show();
                    }
                });
            }
        }
    };

    @Override
    public void showLoadView() {
        // TODO Auto-generated method stub

    }

    @Override
    public void hideLoadView() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean bindDialogViewModel(Context context,
                                       BaseViewModel baseViewModel) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getRes(String type) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void registerNetworkChange(NetworkChangeListener l) {
        // TODO Auto-generated method stub

    }

    @Override
    public void showNetSettingView() {
        // TODO Auto-generated method stub

    }

    @Override
    public void showNetSettingDialog() {
        // TODO Auto-generated method stub

    }

    @Override
    public void hideNetSettingView() {
        // TODO Auto-generated method stub

    }

    @Override
    public void showRetryView(Runnable retryTask) {
        // TODO Auto-generated method stub

    }

    @Override
    public void hideRetryView() {
        // TODO Auto-generated method stub

    }


    @Override
    public void onLayoutProgressChange(int progress, int max) {
        LogUtil.e("消息","progress="+progress+" max="+max);
        if (mReaderMenuPopWin != null)
            mReaderMenuPopWin.setLayoutChapterProgress(progress, max);
    }

    @Override
    public void onPageChange(int totalPageIndex,int max) {
        LogUtil.e("消息","onPageChange="+totalPageIndex+" max="+max);
        if (mReaderMenuPopWin != null)
            mReaderMenuPopWin.setJumpSeekBarProgress(totalPageIndex, max);
    }

    @Override
    public void onNotPreContent() {
        ToastUtil.showToast(this, "已经是第一页了");
    }

    @Override
    public void onNotNextContent() {
        getRecommendBookDialog(this_);
//		ToastUtil.showToast(this, "已经是最后一页了");
    }

    @Override
    public boolean checkNeedBuy(int catalogIndex, boolean isNeedBuy){
        if(isNeedBuy){
            getBuyDialog(this);
            return true;
        }
        if (mBook.isOrder()) {
            return false;
        }
        int feeIndex = getBuyIndex();
        if(catalogIndex >= feeIndex && !mBook.isOrder() && feeIndex!= -1){
            getBuyDialog(this);
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void onChapterChange(ArrayList<Catalog> chapters) {
        if(mCatalogView != null){
            mCatalogView.setCatalogData(chapters);
            mCatalogView.refreshCatalog();
        }
    }

    private Dialog mLoadingDialog;

    @Override
    public void showLoadingDialog(int resId) {
        if(mLoadingDialog != null && mLoadingDialog.isShowing()) {
            return;
        }
        mLoadingDialog = DialogUtil.getLoadingDialog(this_, resId);
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.setCanceledOnTouchOutside(false);
        if(!isFinishing()) {
            mLoadingDialog.show();
        }
    }

    private String cebBookId;
    /**
     * epub书籍id设置，用于弹窗购买窗口后挑战到书籍详情
     */
    @Override
    public void setCebBookId(String cebBookId){
        this.cebBookId = cebBookId;
    }

    @Override
    public void hideLoadingDialog() {
        if(mLoadingDialog != null && mLoadingDialog.isShowing()){
            mLoadingDialog.dismiss();
        }
    }


    private void onPostDealBuy() {
        if (!AccountManager.getInstance().isLogin()) {
            Intent intent = new Intent(this, UserLoginLeYueNewActivity.class);
            startActivity(intent);
            return;
        }

        PayUtil.BuyInfo buyInfo = new PayUtil.BuyInfo();
        buyInfo.isSurfingBook = false;
        buyInfo.bookName = mBook.getBookName();
        buyInfo.price = mBook.getPrice();
        buyInfo.discountPrice = mBook.getPromotionPrice();

        PayUtil.showBuyDialog(CommonUtil.getRealActivity(this_), buyInfo, new PayUtil.OnPayCallback() {
            @Override
            public void onBuyBtnClick(int payType) {
                // 确认购买按钮点击
                if (this_ != null && !this_.isFinishing() && mBook != null) {  
                    buyBook(payType);
                }
            }
        });
    }

    /**
     * 处理乐阅购买流程
     *
     * @param purchaseType
     */
    public void buyBook(int purchaseType) {
        final IPayHandler mPayHandler = PayUtil.dealPay(this_, purchaseType);
        mPayHandler.execute(new IDealPayRunnable() {

            @Override
            public void bindService(Intent arg0, ServiceConnection arg1, int arg2) {
                CommonUtil.getRealActivity(this_).bindService(arg0, arg1, arg2);
            }

            @Override
            public Object onGetOrder(int arg0) {
                Object result = null;
                String charge = (mBook.getLimitPrice() != null
                        && !TextUtils.isEmpty(mBook.getLimitPrice())) ? mBook.getLimitPrice()
                        :( !TextUtils.isEmpty(mBook.getPromotionPrice()) && Float.valueOf(mBook.getPromotionPrice()) > 0 ?
                        mBook.getPromotionPrice() : mBook.getPrice());
                switch(arg0) {
                    case PayConst.PAY_TYPE_ALIPAY:

                        result = PayUtil.getAliPayOrderInfo(this_, mBook.getBookId(), charge, mBook.getBookName());
                        break;
                    case PayConst.PAY_TYPE_CHINATELECOM_MESSAGE_PAY:
                        result = PayUtil.getCTCMessageOrderInfo(CommonUtil.getRealActivity(this_), mBook.getBookId(), Double.valueOf(charge),
                                mBook.getBookName());

                    case PayConst.PAY_TYPE_TY_READ_POINT:
//						result = PayUtil.getTYReadPointPayOrderInfo(this_);
                        break;
                }

                return result;
            }

            @Override
            public void onPayComplete(boolean arg0, int arg1, String arg2, Object resultData) {
                if(mPayHandler != null && !mPayHandler.isAbort()) {

                    if(arg0) {
                        onBuySuccess();
                        CommonUtil.getRealActivity(this_).sendBroadcast(new Intent(AppBroadcast.ACTION_BUY_SUCCEED));
                        ToastUtil.showToast(CommonUtil.getRealActivity(this_),CommonUtil.getRealActivity(this_).getResources().getString(R.string.account_book_recharge_successed));
                    }else {

                        if(mPayHandler.getPayType() == PayConst.PAY_TYPE_TY_READ_POINT
                                && arg1 == ResponseResultCode.NOT_SUFFICIENT_FUNDS) {

                        }else {
                            CommonUtil.getRealActivity(this_).sendBroadcast(new Intent(AppBroadcast.ACTION_BUY_FAIL));
                            ToastUtil.showToast(CommonUtil.getRealActivity(this_),CommonUtil.getRealActivity(this_).getResources().getString(R.string.book_recharge_fail));
                        }
                    }
                }
                hideLoadView();

                mPayHandler.abort();
            }
            @Override
            public void onRegisterSmsReceiver(String arg0, BroadcastReceiver arg1) {
                IntentFilter filter = new IntentFilter(arg0);
                CommonUtil.getRealActivity(this_).registerReceiver(arg1, filter);
            }

            @Override
            public void onUnregisterSmsReceiver(BroadcastReceiver arg0) {
                CommonUtil.getRealActivity(this_).unregisterReceiver(arg0);
            }

            @Override
            public void startActivity(Intent arg0) {
                CommonUtil.getRealActivity(this_).startActivity(arg0);
            }

            @Override
            public void unbindService(ServiceConnection arg0) {
                try {
                    CommonUtil.getRealActivity(this_).unbindService(arg0);
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void onBuySuccess() {
        mBook.setOrder(true);
        if(mCatalogView != null){
            mCatalogView.setFeeIndex(-1, true);
        }
        if (buyDialog != null){
            buyDialog.dismiss();
        }
    }

}
