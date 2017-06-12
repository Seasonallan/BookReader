package com.lectek.android.lereader.ui.basereader_leyue;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.data.ReadStyleItem;
import com.lectek.android.lereader.download.DownloadHttpEngine;
import com.lectek.android.lereader.ui.basereader_leyue.widgets.ReaderMediaPlayer;
import com.lectek.android.lereader.ui.basereader_leyue.widgets.ReaderMediaPlayer.PlayerListener;
import com.lectek.android.lereader.utils.ToastUtil;
import com.lectek.android.lereader.widgets.adapter.ReadStytleItemAdapter;
import com.lectek.android.widget.BasePopupWindow;
import com.lectek.android.widget.CheckedGridView;
import com.lectek.android.widget.CheckedGridView.OnItemCheckedStateChangeListener;
import com.lectek.lereader.core.bookformats.BookInfo;
import com.lectek.lereader.core.pdf.PdfReaderView;

public class ReaderMenuPopWin extends BasePopupWindow implements PlayerListener{
    private static final int FONT_INCREASE_UNIT = 1;
    private static final int MAX_MENU_SIZE = 5;
    private Activity mActivity;
    private Handler mHandler;
    private Book mBook;
    private IActionCallback mActionCallback;
    private CheckedGridView mGridView;
    private View addBookmarkIB;
    private ViewGroup mChildMenuLayout;
    private TextView mTitleTV;
    private View mBuyBut;
    private boolean mHasBookmark;
    private ArrayList<MenuItem> mMoreMenuItems;
    private View mJumpPageView;
    private int mTotalPageNums;
    private TextView mJumpPageTip;
    private SeekBar mJumpSeekBar;
    private View mJumpPreBut;
    private View mJumpNextBut;
    private View mBrightessSettingView;
    private View mMoreView;
    private View mThemeView;
    private View mFontSettingView;
    private View mCutFontSizeBut;
    private View mAddFontSizeBut;
    private View mSimpliedView, mTraditionalView;
    private ReadSetting mReadSetting;
    private RadioGroup mLineSpacingRG;
    private RadioGroup mNightChangeRG;
    private View mVoiceLayout;
    private SeekBar mVoiceSeekBar;
    private TextView mVoiceMaxProgressTV;
    private TextView mVoiceProgressTV;
    private ImageButton mVoiceCloseBut;
    private ImageButton mVoiceStateBut;
    private boolean isVoicePlay;
    private AudioManager mAudioManager;
    private View mShareBtn;
    private ReaderMediaPlayer mMediaPlayer;
    private TextView mPdfPageTV;
    private Button mSearchBtn;
    private Button mCancelBtn;
    private EditText mSearchET;
    private View mSearchPre;
    private View mSearchNext;

    public ReaderMenuPopWin(View parent,Activity activity,Book book,IActionCallback callback) {
        super(parent, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        mActivity = activity;
        mReadSetting = ReadSetting.getInstance(mActivity);
        mHandler = new Handler(Looper.getMainLooper());
        mBook = book;
        mActionCallback = callback;
        //设置屏幕亮度
        setScreenBrightess(mReadSetting.getBrightessLevel());
        mMediaPlayer = ReaderMediaPlayer.getInstance();
        if(mMediaPlayer != null){
            mMediaPlayer.addPlayerListener(this);
            isVoicePlay = mMediaPlayer.isPlaying();
        }
        mAudioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
    }

    public void validateBookmarkState(boolean hasBookmark) {
        mHasBookmark = hasBookmark;
        if(addBookmarkIB == null) {
            return;
        }
        if(!hasBookmark) {
            addBookmarkIB.setBackgroundResource(R.drawable.bookmark_icon);
        }else {
            addBookmarkIB.setBackgroundResource(R.drawable.bookmark_delete_icon);
        }
    }

    @Override
    protected View onCreateContentView() {
        View contentView = getLayoutInflater().inflate(R.layout.reader_menu_leyue, null);
        mVoiceLayout = contentView.findViewById(R.id.menu_reader_voice_layout);
        mVoiceStateBut = (ImageButton)contentView.findViewById(R.id.menu_reader_voice_state_but);
        mVoiceCloseBut = (ImageButton)contentView.findViewById(R.id.menu_reader_voice_close_but);
        mVoiceProgressTV = (TextView)contentView.findViewById(R.id.menu_reader_voice_progress_tv);
        mVoiceMaxProgressTV = (TextView)contentView.findViewById(R.id.menu_reader_voice_max_progress_tv);
        mSearchET = (EditText) contentView.findViewById(R.id.menu_search_et);
        mPdfPageTV = (TextView) contentView.findViewById(R.id.pdf_page_text);
        mVoiceSeekBar = (SeekBar)contentView.findViewById(R.id.menu_reader_voice_seek);

        if(mMediaPlayer != null){
            try {
                onProgressChange(mMediaPlayer.getCurrentPosition(), mMediaPlayer.getDuration(), null);
            } catch (Exception e) {
            }
        }
        if(isVoicePlay){
            mVoiceStateBut.setImageResource(R.drawable.ic_menu_reader_voice_play);
        }else{
            mVoiceStateBut.setImageResource(R.drawable.ic_menu_reader_voice_pause);
        }
        mVoiceCloseBut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.stop();
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_left_hide);
                animation.setAnimationListener(new AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationRepeat(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mVoiceLayout.setVisibility(View.GONE);
                    }
                });
                mVoiceLayout.startAnimation(animation);
            }
        });
        mVoiceStateBut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isVoicePlay = !isVoicePlay;
                mMediaPlayer.setPlayState(isVoicePlay);
            }
        });
        mVoiceSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMediaPlayer.seekTo(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mMediaPlayer.pause();
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                onProgressChange(progress, seekBar.getMax(), null);
            }
        });

        mChildMenuLayout = (ViewGroup) contentView.findViewById(R.id.menu_child_layout);
        contentView.findViewById(R.id.transparent_view).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                dismiss();
            }
        });
        addBookmarkIB = contentView.findViewById(R.id.menu_add_bookmark_ib);
        addBookmarkIB.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mHasBookmark){
                    mActionCallback.onSaveUserBookmark();
                }else{
                    mActionCallback.onDeleteUserBookmark();
                }
            }
        });
        mShareBtn = contentView.findViewById(R.id.menu_share_btn);
        mShareBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mActionCallback.shareClick(v);
            }
        });
        if (mActionCallback.canAddUserBookmark()) {
            addBookmarkIB.setVisibility(View.VISIBLE);
        }

        mSearchBtn = (Button) contentView.findViewById(R.id.menu_search_btn);
        mSearchBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchView();
            }
        });

        mSearchPre = contentView.findViewById(R.id.pdf_search_pre);
        mSearchPre.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mActionCallback != null) {
                    mActionCallback.onSearch(-1, mSearchET.getText().toString().trim());
                }
            }
        });

        mSearchNext = contentView.findViewById(R.id.pdf_search_next);
        mSearchNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mActionCallback != null) {
                    mActionCallback.onSearch(1, mSearchET.getText().toString().trim());
                }
            }
        });

        mCancelBtn = (Button) contentView.findViewById(R.id.menu_cancel_btn);
        mCancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                //如果不延时，布局界面会跳动
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        exitSearchView();
                    }
                }, 500);
            }
        });

        //处理点击键盘搜素键
        mSearchET.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard();
                    if(mActionCallback != null) {
                        mActionCallback.onSearch(1, mSearchET.getText().toString().trim());
                    }
                }
                return false;
            }
        });

        mTitleTV = (TextView) contentView.findViewById(R.id.menu_body_title);
        mBuyBut = contentView.findViewById(R.id.menu_buy);
        ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();

        if(isReadCartoon()){
            menuItems.add(new MenuItem(MenuItem.MENU_ITEM_ID_CATALOG, R.drawable.menu_icon_mark, getString(R.string.reader_menu_item_catalog_tip)));
            menuItems.add(new MenuItem(MenuItem.MENU_ITEM_ID_FONT, R.drawable.menu_icon_fit_width, getString(R.string.cartoon_menu_landscape)));
            menuItems.add(new MenuItem(MenuItem.MENU_ITEM_ID_BRIGHTNESS, R.drawable.menu_icon_brightness, getString(R.string.reader_menu_item_brightness_tip)));
        }else{
            menuItems.add(new MenuItem(MenuItem.MENU_ITEM_ID_CATALOG, R.drawable.btn_mulu, ""));
            menuItems.add(new MenuItem(MenuItem.MENU_ITEM_ID_N_SET, R.drawable.btn_shezhi, ""));

            if(mReadSetting.getThemeType()==ReadSetting.THEME_TYPE_NIGHT){
                menuItems.add(new MenuItem(MenuItem.MENU_ITEM_ID_N_NIGHT_DAY, R.drawable.btn_day, ""));
            }else{
                menuItems.add(new MenuItem(MenuItem.MENU_ITEM_ID_N_NIGHT_DAY, R.drawable.btn_night, ""));
            }
            if(!isReadLocal()){
                menuItems.add(new MenuItem(MenuItem.MENU_ITEM_ID_N_MORE, R.drawable.menu_icon_brightness, getString(R.string.reader_menu_item_brightness_tip)));
                menuItems.add(new MenuItem(MenuItem.MENU_ITEM_ID_N_MORE_DETAIL, -1, "查看书籍详情"));
                menuItems.add(new MenuItem(MenuItem.MENU_ITEM_ID_N_MORE_COMMENT, -1, "查看评论详情"));
            }else{

            }
        }
        if(menuItems.size() > MAX_MENU_SIZE){
            mMoreMenuItems = new ArrayList<MenuItem>(menuItems.subList(MAX_MENU_SIZE - 1, menuItems.size()));
            menuItems = new ArrayList<MenuItem>(menuItems.subList(0, MAX_MENU_SIZE - 2));
            menuItems.add(new MenuItem(MenuItem.MENU_ITEM_ID_MORE, R.drawable.btn_more, ""));
        }

        // pdf阅读
        if(isReadPDF()) {

            mShareBtn.setVisibility(View.GONE);
            mSearchBtn.setVisibility(View.VISIBLE);
            mCancelBtn.setVisibility(View.GONE);

            menuItems.clear();
            menuItems.add(new MenuItem(MenuItem.MENU_ITEM_ID_CATALOG, R.drawable.menu_icon_mark, getString(R.string.reader_menu_item_catalog_tip)));
            int layoutType = mReadSetting.getPdfLayoutType();
            if(layoutType == PdfReaderView.LAYOUT_TYPE_FITSCREEN) {
                menuItems.add(new MenuItem(MenuItem.MENU_ITEM_ID_PDF_LAYOUT_TYPE, R.drawable.menu_icon_fit_width, getString(R.string.reader_menu_item_fit_width)));
            } else if (layoutType == PdfReaderView.LAYOUT_TYPE_FITWIDTH) {
                menuItems.add(new MenuItem(MenuItem.MENU_ITEM_ID_PDF_LAYOUT_TYPE, R.drawable.menu_icon_fit_screen, getString(R.string.reader_menu_item_fit_screen)));
            }
            menuItems.add(new MenuItem(MenuItem.MENU_ITEM_ID_PDF_ORIENTATION, R.drawable.menu_icon_screen_orientatioon, getString(R.string.reader_menu_item_screen_orientation)));
            menuItems.add(new MenuItem(MenuItem.MENU_ITEM_ID_BRIGHTNESS, R.drawable.menu_icon_brightness, getString(R.string.reader_menu_item_brightness_tip)));
        }

        adapter = new MenuItemAdapter(getContext(), menuItems);
        mGridView = (CheckedGridView) contentView.findViewById(R.id.reader_menu_gv);
        mGridView.setChoiceMode(CheckedGridView.CHOICE_MODE_SINGLE);
        mGridView.setAdapter(adapter);
        mGridView.setNumColumns(menuItems.size());
        mGridView.setOnItemCheckedStateChangeListener(new OnItemCheckedStateChangeListener() {
            @Override
            public void onItemCheckedStateChange(AdapterView<?> parent, int position,boolean isChecked) {
            }
            @Override
            public boolean onPreItemCheckedStateChange(AdapterView<?> parent,
                                                       int position, boolean isChecked) {
                MenuItem item = (MenuItem) parent.getItemAtPosition(position);
                if(isChecked){
                    return handlerMenuItemAction(item);
                }else{
                    showJumpPageView();
                }
                return false;
            }
        });
        mTitleTV.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View view) {
                mActivity.finish();
            }
        });
        // 设置显示键盘时，popwin的布局不会被挤掉
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        return contentView;
    }
    /**
     * 是否是漫画阅读
     * @return
     */
    private boolean isReadCartoon() {
        //TODO: 卡通视图
        BookInfo info = mActionCallback.getBookInfo();
        if(info != null){
            return info.isCartoon;
        }
        return false;
    }
    /**
     * 是否是本地阅读
     * @return
     */
    private boolean isReadLocal() {
        return isReadPDF() || isReadTXT();
    }
    /**
     * 是否是txt阅读
     * @return
     */
    private boolean isReadTXT() {
        if(mBook != null && DownloadHttpEngine.VALUE_DOWNLOAD_TYPE_TEXT_UND.equals(mBook.getBookFormatType())) {
            return true;
        }
        return false;
    }
    /**
     * 是否是pdf阅读
     * @return
     */
    private boolean isReadPDF() {
        if(mBook != null && DownloadHttpEngine.VALUE_DOWNLOAD_TYPE_PDF.equals(mBook.getBookFormatType())) {
            return true;
        }
        return false;
    }

    /**
     * 显示键盘
     */
    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.showSoftInput(mSearchET, 0);
    }

    /**
     * 隐藏键盘
     */
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.hideSoftInputFromWindow(mSearchET.getWindowToken(), 0);
    }

    private void updateState(){
        if(mActionCallback.isNeedBuy()){
            mBuyBut.setVisibility(View.VISIBLE);
            mBuyBut.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActionCallback.onGotoBuyBook();
                    dismiss();
                }
            });
        }else{
            mBuyBut.setVisibility(View.GONE);
        }
        mTitleTV.setText(mBook.getBookName());
       /* if(mBuyBut.getVisibility() == View.VISIBLE || mSearchET.getVisibility() == View.VISIBLE){
            mTitleTV.setVisibility(View.GONE);
        }else{
            mTitleTV.setVisibility(View.VISIBLE);
            mTitleTV.setText(mBook.getBookName());
        }*/
        validateBookmarkState(mHasBookmark);
    }

    public void setLayoutChapterProgress(int progress,int max){
        if(mJumpPageView != null){
            if(progress == max){
                setJumpSeekBarProgress(mActionCallback.getCurPage(), mActionCallback.getPageNums());
                mJumpSeekBar.setSecondaryProgress(0);
                mJumpPageTip.setTextColor(getResources().getColor(R.color.common_white_2));
            }else{
                mJumpSeekBar.setProgress(0);
                mJumpSeekBar.setSecondaryProgress(progress);
                mJumpSeekBar.setMax(max);
                mJumpSeekBar.setEnabled(false);
                mJumpPreBut.setEnabled(mActionCallback.hasPreChapter());
                mJumpNextBut.setEnabled(mActionCallback.hasNextChapter());
                mJumpPageTip.setText(getString(R.string.reader_menu_item_seek_layouting_tip, (int)(progress * 1f / max * 100)+"%"));
                mJumpPageTip.setTextColor(Color.parseColor("#60ffffff"));
            }
        }
    }

    public void setJumpSeekBarProgress(int progress,int max){
        if(mJumpPageView != null){
            mTotalPageNums = max;
            updateJumpSeekBar(progress, mTotalPageNums);
        }
    }

    private void showPageNum(int curPage, int pageNums){
        if(pageNums > 1){
            curPage += 1;
        }
        if(mJumpPageTip != null){
            String txt = getString(R.string.reader_menu_item_seek_page_tip, curPage, pageNums);
            if(isReadTXT()){
                txt = getJumpProgressStr(curPage, pageNums);
            }
            mJumpPageTip.setText(txt);
        }
        if(isReadPDF() && mPdfPageTV != null && mSearchET.getVisibility() != View.VISIBLE) {
            mPdfPageTV.setVisibility(View.VISIBLE);
            mPdfPageTV.setText(getString(R.string.reader_menu_item_seek_page_tip, curPage, pageNums));
        }
    }

    public String getJumpProgressStr(int curPage, int pageNums) {
        DecimalFormat df = new DecimalFormat("0.00");
        float percent = curPage * 100.0f / (pageNums * 1.0f);
        if(percent > 100){
            percent = 100;
        }else if(percent < 0){
            percent = 0;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(df.format(percent));
        sb.append("%");
        return sb.toString();
    }

    private void gotoPage(int page,int oldPage){
        mActionCallback.onGotoPage(page);
    }

    private void showJumpPageView(){
        int pageNums = mActionCallback.getPageNums();
        int curPage = mActionCallback.getCurPage();
        mTotalPageNums = pageNums;
        if(mJumpPageView == null){
            mJumpPageView = getLayoutInflater().inflate(R.layout.menu_jump_page_leyue, null);
            mJumpPageTip = (TextView) mJumpPageView.findViewById(R.id.page_text);
            mJumpSeekBar = ((SeekBar) mJumpPageView.findViewById(R.id.jump_page_seek));
            mJumpSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
                int oldPage = 0;
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    showPageNum(seekBar.getProgress(), mTotalPageNums);
                    gotoPage(seekBar.getProgress(),oldPage);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    oldPage = seekBar.getProgress();
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {
                    showPageNum(seekBar.getProgress(), mTotalPageNums);
                }
            });
            mJumpPreBut = mJumpPageView.findViewById(R.id.jump_pre_but);
            mJumpPreBut.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActionCallback.gotoPreChapter();
                    mJumpPreBut.setEnabled(mActionCallback.hasPreChapter());
                    mJumpNextBut.setEnabled(mActionCallback.hasNextChapter());
                }
            });
            mJumpNextBut = mJumpPageView.findViewById(R.id.jump_next_but);
            mJumpNextBut.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActionCallback.gotoNextChapter();
                    mJumpPreBut.setEnabled(mActionCallback.hasPreChapter());
                    mJumpNextBut.setEnabled(mActionCallback.hasNextChapter());
                }
            });

            // XXX 如果是pdf阅读，只显示进度条
            if(isReadPDF()) {
                mJumpPageView.findViewById(R.id.reader_menu_page_discription).setVisibility(View.GONE);
                mJumpPreBut.setVisibility(View.GONE);
                mJumpNextBut.setVisibility(View.GONE);
            }
        }
        updateJumpSeekBar(curPage, mTotalPageNums);
        showChildMenu(mJumpPageView);
    }

    private void updateJumpSeekBar(int curPage,int max){
        if(max < 0){
            setLayoutChapterProgress(mActionCallback.getLayoutChapterProgress(), mActionCallback.getLayoutChapterMax());
        }else{
            if(max == 1){
                curPage = 1;
                max = 1;
                mJumpSeekBar.setMax(max);
                mJumpSeekBar.setProgress(curPage);
                mJumpSeekBar.setEnabled(false);
            }else{
                mJumpSeekBar.setMax(max - 1);
                mJumpSeekBar.setProgress(curPage);
                mJumpSeekBar.setEnabled(true);
            }
            mJumpPreBut.setEnabled(mActionCallback.hasPreChapter());
            mJumpNextBut.setEnabled(mActionCallback.hasNextChapter());
            showPageNum(curPage, max);
        }
    }

    private void showBrightessSettingView(){
        SeekBar seekBar = null;
        if(mBrightessSettingView == null){
            mBrightessSettingView = getLayoutInflater().inflate(R.layout.menu_brightness_setting_layout, null);
            seekBar = (SeekBar) mBrightessSettingView.findViewById(R.id.brightness_seek);
            seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    mReadSetting.setBrightessLevel(seekBar.getProgress());
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {
                    setScreenBrightess(progress);
                }
            });
            mBrightessSettingView.findViewById(R.id.brightness_auto_but).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO 亮度自动调整
                }
            });

            mNightChangeRG = (RadioGroup)mBrightessSettingView.findViewById(R.id.menu_settings_night_rg);
            mBrightessSettingView.findViewById(R.id.menu_settings_night_but).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {//夜间模式
                    mReadSetting.setThemeType(ReadSetting.THEME_TYPE_NIGHT);
                }
            });

            mBrightessSettingView.findViewById(R.id.menu_settings_bright_but).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {// 白天模式
                    int themeType = mReadSetting.getLastThemeType();
                    mReadSetting.setThemeType(themeType);
                }
            });

            //pdf阅读
            if(isReadPDF()) {
                mBrightessSettingView.findViewById(R.id.nightSetLayout).setVisibility(View.GONE);
            }

        }
        if(mReadSetting.getThemeType()==ReadSetting.THEME_TYPE_NIGHT){
            mNightChangeRG.check(R.id.menu_settings_night_but);
        }else{
            mNightChangeRG.check(R.id.menu_settings_bright_but);
        }
        seekBar = (SeekBar) mBrightessSettingView.findViewById(R.id.brightness_seek);
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        float oldValue = lp.screenBrightness;
        if(oldValue < 0){
            seekBar.setProgress(50);
        }else{
            if(oldValue <= 0.17f){
                seekBar.setProgress(0);
            }else{
                seekBar.setProgress((int) (oldValue * 100));
            }
        }
        showChildMenu(mBrightessSettingView);
    }

    private void showThemeView(){
        if(mThemeView == null){
            mThemeView = getLayoutInflater().inflate(R.layout.reader_background, null);
        }
        GridView gridView  = (GridView) mThemeView;
        ArrayList<ReadStyleItem> readStyleItems = new ArrayList<ReadStyleItem>();
        int style = mReadSetting.getThemeType();
        readStyleItems.add(new ReadStyleItem(ReadSetting.THEME_TYPE_DAY, R.drawable.ic_read_style_day,
                style == ReadSetting.THEME_TYPE_DAY));
        readStyleItems.add(new ReadStyleItem(ReadSetting.THEME_TYPE_OTHERS_1, R.drawable.ic_read_style_other_1,
                style == ReadSetting.THEME_TYPE_OTHERS_1));
        readStyleItems.add(new ReadStyleItem(ReadSetting.THEME_TYPE_OTHERS_2, R.drawable.ic_read_style_other_2,
                style == ReadSetting.THEME_TYPE_OTHERS_2));
        readStyleItems.add(new ReadStyleItem(ReadSetting.THEME_TYPE_OTHERS_3, R.drawable.ic_read_style_other_3,
                style == ReadSetting.THEME_TYPE_OTHERS_3));
        readStyleItems.add(new ReadStyleItem(ReadSetting.THEME_TYPE_OTHERS_4, R.drawable.ic_read_style_other_4,
                style == ReadSetting.THEME_TYPE_OTHERS_4));

        final ReadStytleItemAdapter adapter = new ReadStytleItemAdapter(getContext(), readStyleItems);
        gridView.setNumColumns(readStyleItems.size());
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                adapter.setSeleted(position);
                ReadStyleItem item = (ReadStyleItem) parent.getItemAtPosition(position);
                if(item != null){
                    int styleId = item.id;
                    mReadSetting.setThemeType(styleId);
                    mReadSetting.setLastThemeType(styleId);
//					updateLightState(styleId);
                }
            }
        });
        showChildMenu(mThemeView);
    }

//	private void updateLightState(int styleId){
//		if(styleId != ReadSetting.THEME_TYPE_NIGHT){
//			mPullLightsIB.setBackgroundResource(R.drawable.ic_reader_menu_light);
//		}else{
//			mPullLightsIB.setBackgroundResource(R.drawable.ic_reader_menu_dark);
//		}
//	}

    private void showFontSettingView(){
        if (isReadCartoon()) {
            dismiss();
            int configure = Configuration.ORIENTATION_LANDSCAPE;
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mActivity
                        .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                configure = Configuration.ORIENTATION_PORTRAIT;
            } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                mActivity
                        .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
            updateCartoonLayoutIcon(configure);
            return;
        }
        if(mFontSettingView == null){
            //初始化字体大小设置逻辑
            mFontSettingView = getLayoutInflater().inflate(R.layout.reader_menu_font_settings, null);
            mCutFontSizeBut = mFontSettingView.findViewById(R.id.menu_settings_font_size_sut_but);
            mAddFontSizeBut = mFontSettingView.findViewById(R.id.menu_settings_font_size_add_but);
            mAddFontSizeBut.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int temtFontProgress = mReadSetting.getFontLevel();
                    temtFontProgress += FONT_INCREASE_UNIT;
                    if(temtFontProgress > 10){
                        temtFontProgress = 10;
                    }
                    if(temtFontProgress == 10){
                        ToastUtil.showToast(mActivity, R.string.reader_menu_item_font_size_max_tip);
                        v.setEnabled(false);
                    }
                    mReadSetting.setFontLevel(temtFontProgress);
                    if(!mCutFontSizeBut.isEnabled()){
                        mCutFontSizeBut.setEnabled(true);
                    }
                }
            });
            mCutFontSizeBut.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int temtFontProgress = mReadSetting.getFontLevel();
                    temtFontProgress -= FONT_INCREASE_UNIT;
                    if(temtFontProgress < 0){
                        ToastUtil.showToast(mActivity, R.string.reader_menu_item_font_size_min_tip);
                        temtFontProgress = 0;
                    }
                    if(temtFontProgress == 0){
                        v.setEnabled(false);
                    }
                    mReadSetting.setFontLevel(temtFontProgress);
                    if(!mAddFontSizeBut.isEnabled()){
                        mAddFontSizeBut.setEnabled(true);
                    }
                }
            });
            //初始化行距设置逻辑
            mLineSpacingRG = (RadioGroup)mFontSettingView.findViewById(R.id.menu_settings_line_spacing_rg);
            mLineSpacingRG.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    float lineSpaceType = ReadSetting.FONT_LINE_SPACE_TYPE_1;
                    switch (checkedId) {
                        case R.id.menu_settings_line_spacing_but_1:
                            lineSpaceType = ReadSetting.FONT_LINE_SPACE_TYPE_1;
                            break;
                        case R.id.menu_settings_line_spacing_but_2:
                            lineSpaceType = ReadSetting.FONT_LINE_SPACE_TYPE_2;
                            break;
                        case R.id.menu_settings_line_spacing_but_3:
                            lineSpaceType = ReadSetting.FONT_LINE_SPACE_TYPE_3;
                            break;
                    }
                    mReadSetting.setLineSpaceType(lineSpaceType);
                }
            });
        }

        //同步字体大小设置状态
        int temtFontProgress = mReadSetting.getFontLevel();
        if(temtFontProgress == 10){
            mAddFontSizeBut.setEnabled(false);
        }else if(temtFontProgress == 0){
            mCutFontSizeBut.setEnabled(false);
        }
        //同步行距设置状态
        float lineSpaceType = mReadSetting.getLineSpaceType();
        if(lineSpaceType == ReadSetting.FONT_LINE_SPACE_TYPE_1){
            mLineSpacingRG.check(R.id.menu_settings_line_spacing_but_1);
        }else if(lineSpaceType == ReadSetting.FONT_LINE_SPACE_TYPE_2){
            mLineSpacingRG.check(R.id.menu_settings_line_spacing_but_2);
        }else if(lineSpaceType == ReadSetting.FONT_LINE_SPACE_TYPE_3){
            mLineSpacingRG.check(R.id.menu_settings_line_spacing_but_3);
        }
        //显示界面
        showChildMenu(mFontSettingView);
    }

    private void showMoreView(){
        if(mMoreView == null){
            mMoreView = getLayoutInflater().inflate(R.layout.reader_menu_more, null);
            if(mMoreMenuItems != null){
                MenuMoreItemAdapter adapter = new MenuMoreItemAdapter(getContext(), mMoreMenuItems);
                GridView gridView = (GridView) mMoreView;
                gridView.setAdapter(adapter);
                gridView.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        MenuItem item = (MenuItem) parent.getItemAtPosition(position);
                        handlerMenuItemAction(item);
                    }
                });
            }
        }
        showChildMenu(mMoreView);
    }

    private void showChildMenu(View childContentView){
        hideAllViews();
        childContentView.setAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
        if(childContentView.getParent() == null){
            mChildMenuLayout.addView(childContentView);
        }else{
            childContentView.setVisibility(View.VISIBLE);
        }
    }

    private void hideAllViews(){
        int count = mChildMenuLayout.getChildCount();
        View child = null;
        for(int i = 0;i < count;i++){
            child = mChildMenuLayout.getChildAt(i);
            child.setVisibility(View.GONE);
            child.setAnimation(null);
        }
    }

    /** 设置屏幕亮度
     * @param value
     */
    private void setScreenBrightess(int value){
        final WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.screenBrightness = value * 1.0f / 100.0f;
        if(lp.screenBrightness < 0.17){
            lp.screenBrightness = 0.17f;
        }
        mActivity.getWindow().setAttributes(lp);
    }

    @Override
    protected void onPreShow() {
        hideAllViews();
        mHandler.removeCallbacks(mFullScreenRunnable);
//		if(Build.MANUFACTURER.equals(Manufacturer.SAMSUNG)){
//			mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		}else{
        mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
//		}
        if(mMediaPlayer == null || !mMediaPlayer.isPlayerStop()){
            mVoiceLayout.setVisibility(View.GONE);
        }else{
            mVoiceLayout.setVisibility(View.VISIBLE);
        }
        updateState();
        mGridView.clearChoices();
        showJumpPageView();
        super.onPreShow();
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
        mHandler.removeCallbacks(mFullScreenRunnable);
        mHandler.postDelayed(mFullScreenRunnable,500);
    }

    @Override
    protected boolean dispatchKeyEvent(KeyEvent event){
        //TODO:由于Reader界面不采用模拟系统menu 调用方式。故此处屏蔽取消
//		if(event.getKeyCode() == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_UP){

        //如果pop isShowing，点击menu事件 会被其拦截；所以用down来处理menu的消失。
        //如果pop dismiss，点击menu事件 会被界面的onKeyDown 拦截；用来处理调用pop，即menu的产生。
        if(event.getKeyCode() == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_DOWN){
            if (isShowing()) {
                dismiss();
            }
            return true;
        }
        if(mMediaPlayer != null && mMediaPlayer.isNeedControlVolume()){
            int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if(event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP){
                int streamVolumeMax = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                streamVolume++;
                if(streamVolume > streamVolumeMax){
                    streamVolume = streamVolumeMax;
                }
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,streamVolume, AudioManager.FLAG_SHOW_UI);
                return true;
            }
            if(event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN){
                streamVolume--;
                if(streamVolume < 0){
                    streamVolume = 0;
                }
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, streamVolume, AudioManager.FLAG_SHOW_UI);
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void dismiss() {
        if(mActivity.isFinishing()){
            return;
        }
        super.dismiss();
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        if(mActivity.isFinishing()){
            return;
        }
        super.showAtLocation(parent, gravity, x, y);
    }

    private boolean handlerMenuItemAction(MenuItem item){
        if(item != null){
            ToastUtil.dismissToast();
            switch(item.id){
                case MenuItem.MENU_ITEM_ID_N_SET:
                    showSetView();
                    break;
                case MenuItem.MENU_ITEM_ID_N_MORE_COMMENT:
                    mActionCallback.onGotoCommend();
                    break;
                case MenuItem.MENU_ITEM_ID_N_MORE_DETAIL:
                    mActionCallback.onGotoDetail();
                    break;
                case MenuItem.MENU_ITEM_ID_N_NIGHT_DAY:
                    if(mReadSetting.getThemeType()==ReadSetting.THEME_TYPE_NIGHT){
                        int themeType = mReadSetting.getLastThemeType();
                        mReadSetting.setThemeType(themeType);
                        if( adapter != null) {
                            adapter.updateDayNightLayoutIcon(mGridView, R.drawable.btn_night);
                        }
                    }else{
                        mReadSetting.setThemeType(ReadSetting.THEME_TYPE_NIGHT);
                        adapter.updateDayNightLayoutIcon(mGridView, R.drawable.btn_day);
                    }
                    return true;
                case MenuItem.MENU_ITEM_ID_BRIGHTNESS:
                    ToastUtil.showToast(getContext(), R.string.reader_menu_automatic_brightness_tip);
                    showBrightessSettingView();
                    break;
                case MenuItem.MENU_ITEM_ID_FONT:
                    showFontSettingView();
                    break;
                case MenuItem.MENU_ITEM_ID_SETTING:
                    Intent i = new Intent(mActivity, ReaderSettingActivity.class);
                    mActivity.startActivity(i);
                    dismiss();
                    return true;
                case MenuItem.MENU_ITEM_ID_CATALOG:
                    mActionCallback.onShowReaderCatalog();
                    dismiss();
                    return true;
                case MenuItem.MENU_ITEM_ID_MORE:
                    showMoreView();
                    break;
                case MenuItem.MENU_ITEM_ID_THEME:
                    showThemeView();
                    break;
                case MenuItem.MENU_ITEM_ID_PDF_LAYOUT_TYPE:	//pdf布局设置
                    changePdfLayoutType();
                    dismiss();
                    return true;
                case MenuItem.MENU_ITEM_ID_PDF_ORIENTATION:	//pdf横竖屏切换
                    changePdfOrientationType();
                    dismiss();
                    return true;
            }
        }
        return false;
    }



    /**
     * 更新pdf布局类型图标
     * @param configure
     */
    private void updateCartoonLayoutIcon(int configure) {
        if(isReadCartoon() && adapter != null) {
            adapter.updateCartoonLayoutIcon(mGridView, configure);
        }
    }
    /**
     * 改变pdf布局类型
     */
    private void changePdfLayoutType() {
        int layoutType = mReadSetting.getPdfLayoutType();
        if(layoutType == PdfReaderView.LAYOUT_TYPE_FITSCREEN) {
            layoutType = PdfReaderView.LAYOUT_TYPE_FITWIDTH;
        } else if (layoutType == PdfReaderView.LAYOUT_TYPE_FITWIDTH) {
            layoutType = PdfReaderView.LAYOUT_TYPE_FITSCREEN;
        }
        mReadSetting.setPdfLayoutType(layoutType);
        updatePdfLayoutIcon(layoutType);
    }

    /**
     * 更新pdf布局类型图标
     * @param layoutType
     */
    private void updatePdfLayoutIcon(int layoutType) {
        if(mBook != null && isReadPDF() && adapter != null) {
            adapter.updatePdfLayoutIcon(mGridView, layoutType);
        }
    }

    /**
     * pdf阅读横竖屏切换
     */
    private void changePdfOrientationType() {
        mReadSetting.changeOrientationType();
    }

    private void showSearchView() {
        mTitleTV.setVisibility(View.GONE);
        mSearchET.setVisibility(View.VISIBLE);
        mSearchBtn.setVisibility(View.GONE);
        mCancelBtn.setVisibility(View.VISIBLE);
        mSearchET.requestFocus();
        mSearchPre.setVisibility(View.VISIBLE);
        mSearchNext.setVisibility(View.VISIBLE);
        mPdfPageTV.setVisibility(View.GONE);
        mGridView.setVisibility(View.GONE);
        mChildMenuLayout.setVisibility(View.GONE);
        showKeyboard();
    }

    private void exitSearchView() {
        mSearchET.setText("");
        mTitleTV.setVisibility(View.VISIBLE);
        mSearchET.setVisibility(View.GONE);
        mCancelBtn.setVisibility(View.GONE);
        mSearchBtn.setVisibility(View.VISIBLE);
        mSearchPre.setVisibility(View.GONE);
        mSearchNext.setVisibility(View.GONE);
        mPdfPageTV.setVisibility(View.VISIBLE);
        mGridView.setVisibility(View.VISIBLE);
        mChildMenuLayout.setVisibility(View.VISIBLE);
    }

    private Runnable mFullScreenRunnable = new Runnable() {
        @Override
        public void run() {
//			if(Build.MANUFACTURER.equals(Manufacturer.SAMSUNG)){
//				mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//			}else{
            mActivity.getWindow().setFlags(~WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
//			}
        }
    };
    private MenuItemAdapter adapter;

    public interface IActionCallback{
        public int getLayoutChapterProgress();
        public int getLayoutChapterMax();
        public int getPageNums();
        public int getCurPage();
        public void onGotoPage(int pageNum);
        public void onGotoBuyBook();
        public boolean isNeedBuy();
        public boolean canAddUserBookmark();
        public void onSaveUserBookmark();
        public void onDeleteUserBookmark();
        public void onShowReaderCatalog();
        public void gotoPreChapter();
        public void gotoNextChapter();
        public boolean hasPreChapter();
        public boolean hasNextChapter();
        public void shareClick(View v);
        public void onGotoDetail();
        public void onGotoCommend();
        public void onSearch(int direction, String keyWord);
        public BookInfo getBookInfo();
    }

    @Override
    public void onPlayStateChange(int playState, String voiceSrc) {
        isVoicePlay = playState == ReaderMediaPlayer.STATE_START;
        if(mVoiceLayout != null){
            if(isVoicePlay){
                mVoiceStateBut.setImageResource(R.drawable.ic_menu_reader_voice_play);
            }else{
                mVoiceStateBut.setImageResource(R.drawable.ic_menu_reader_voice_pause);
            }
            if(!mMediaPlayer.isPlayerStop()){
                mVoiceLayout.setVisibility(View.GONE);
            }else{
                mVoiceLayout.setVisibility(View.VISIBLE);
            }
            mVoiceSeekBar.setEnabled(mMediaPlayer.isPrepare());
        }
    }

    @Override
    public void onProgressChange(long currentPosition, long maxPosition, String voiceSrc) {
        if(mVoiceLayout != null){
            mVoiceSeekBar.setMax((int) maxPosition);
            mVoiceSeekBar.setProgress((int) currentPosition);
            mVoiceProgressTV.setText(ReaderMediaPlayer.getTimeStr((int) (currentPosition / 1000)));
            mVoiceMaxProgressTV.setText(ReaderMediaPlayer.getTimeStr((int) (maxPosition / 1000)));
        }
    }


    private void showSetView(){
        if(mFontSettingView == null){
            //初始化字体大小设置逻辑
            mFontSettingView = getLayoutInflater().inflate(R.layout.reader_menu_n_settings, null);

            mCutFontSizeBut = mFontSettingView.findViewById(R.id.menu_settings_font_size_sut_but);
            mAddFontSizeBut = mFontSettingView.findViewById(R.id.menu_settings_font_size_add_but);
            mAddFontSizeBut.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int temtFontProgress = mReadSetting.getFontLevel();
                    temtFontProgress += FONT_INCREASE_UNIT;
                    if(temtFontProgress > 10){
                        temtFontProgress = 10;
                    }
                    if(temtFontProgress == 10){
                        ToastUtil.showToast(mActivity, R.string.reader_menu_item_font_size_max_tip);
                        v.setEnabled(false);
                    }
                    mReadSetting.setFontLevel(temtFontProgress);
                    if(!mCutFontSizeBut.isEnabled()){
                        mCutFontSizeBut.setEnabled(true);
                    }
                }
            });
            mCutFontSizeBut.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int temtFontProgress = mReadSetting.getFontLevel();
                    temtFontProgress -= FONT_INCREASE_UNIT;
                    if(temtFontProgress < 0){
                        ToastUtil.showToast(mActivity, R.string.reader_menu_item_font_size_min_tip);
                        temtFontProgress = 0;
                    }
                    if(temtFontProgress == 0){
                        v.setEnabled(false);
                    }
                    mReadSetting.setFontLevel(temtFontProgress);
                    if(!mAddFontSizeBut.isEnabled()){
                        mAddFontSizeBut.setEnabled(true);
                    }
                }
            });

            mSimpliedView = mFontSettingView.findViewById(R.id.menu_settings_font_jt_but);
            mTraditionalView  = mFontSettingView.findViewById(R.id.menu_settings_font_ft_but);
            mSimpliedView.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View view) {
                    if (!mReadSetting.isSimplified()){
                        mSimpliedView.setSelected(true);
                        mTraditionalView.setSelected(false);
                        mReadSetting.setSimplified(true);
                    }
                }
            });
            mTraditionalView.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View view) {
                    if (mReadSetting.isSimplified()){
                        mSimpliedView.setSelected(false);
                        mTraditionalView.setSelected(true);
                        mReadSetting.setSimplified(false);
                    }
                }
            });
        }

        if (mReadSetting.isSimplified()){
            mSimpliedView.setSelected(true);
        }else{
            mTraditionalView.setSelected(true);
        }

        //同步字体大小设置状态
        int temtFontProgress = mReadSetting.getFontLevel();
        if(temtFontProgress == 10){
            mAddFontSizeBut.setEnabled(false);
        }else if(temtFontProgress == 0){
            mCutFontSizeBut.setEnabled(false);
        }
        SeekBar seekBar = null;
        if(mBrightessSettingView == null){
            mBrightessSettingView = mFontSettingView.findViewById(R.id.menu_brightness_setting_layout);
            seekBar = (SeekBar) mBrightessSettingView.findViewById(R.id.brightness_seek);
            seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    mReadSetting.setBrightessLevel(seekBar.getProgress());
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {
                    setScreenBrightess(progress);
                }
            });
        }
        seekBar = (SeekBar) mBrightessSettingView.findViewById(R.id.brightness_seek);
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        float oldValue = lp.screenBrightness;
        if(oldValue < 0){
            seekBar.setProgress(50);
        }else{
            if(oldValue <= 0.17f){
                seekBar.setProgress(0);
            }else{
                seekBar.setProgress((int) (oldValue * 100));
            }
        }
        if(mThemeView == null){
            mThemeView = mFontSettingView.findViewById(R.id.reader_background);
        }
        GridView gridView  = (GridView) mThemeView;
        ArrayList<ReadStyleItem> readStyleItems = new ArrayList<ReadStyleItem>();
        int style = mReadSetting.getThemeType();
        readStyleItems.add(new ReadStyleItem(ReadSetting.THEME_TYPE_DAY, R.drawable.btn_color1_selector,
                style == ReadSetting.THEME_TYPE_DAY));
        readStyleItems.add(new ReadStyleItem(ReadSetting.THEME_TYPE_OTHERS_1, R.drawable.btn_color2_selector,
                style == ReadSetting.THEME_TYPE_OTHERS_1));
        readStyleItems.add(new ReadStyleItem(ReadSetting.THEME_TYPE_OTHERS_2, R.drawable.btn_color3_selector,
                style == ReadSetting.THEME_TYPE_OTHERS_2));
        readStyleItems.add(new ReadStyleItem(ReadSetting.THEME_TYPE_OTHERS_3, R.drawable.btn_color4_selector,
                style == ReadSetting.THEME_TYPE_OTHERS_3));

        final ReadStytleItemAdapter adapter = new ReadStytleItemAdapter(getContext(), readStyleItems);
        gridView.setNumColumns(readStyleItems.size());
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                adapter.setSeleted(position);
                ReadStyleItem item = (ReadStyleItem) parent.getItemAtPosition(position);
                if(item != null){
                    int styleId = item.id;
                    mReadSetting.setThemeType(styleId);
                    mReadSetting.setLastThemeType(styleId);
//					updateLightState(styleId);
                }
            }
        });
        if (isReadCartoon()){
            mFontSettingView.findViewById(R.id.reader_background).setVisibility(View.GONE);
        }
        //显示界面
        showChildMenu(mFontSettingView);
    }
}
