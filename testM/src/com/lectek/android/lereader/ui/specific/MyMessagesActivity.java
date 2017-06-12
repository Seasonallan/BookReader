package com.lectek.android.lereader.ui.specific;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lectek.android.LYReader.R;
import com.lectek.android.app.ActivityManage;
import com.lectek.android.lereader.application.MyAndroidApplication;
import com.lectek.android.lereader.binding.model.user.MyMessageViewModel;
import com.lectek.android.lereader.binding.model.user.MyMessageViewModel.UserActionListener;
import com.lectek.android.lereader.ui.IPanelView;
import com.lectek.android.lereader.ui.common.BaseActivity;
import com.lectek.android.lereader.widgets.SlideTabWidget;
import com.lectek.android.widget.BaseViewPagerTabHostAdapter;

import java.util.ArrayList;

/**
 * 我的消息。展现信鸽推送的通知信息
 * @author ljp
 * @since 2014年5月4日13:28:34
 */
public class MyMessagesActivity extends BaseActivity {
    public final ViewPagerAdapter bChildViewPagerTabHostAdapter = new ViewPagerAdapter();

    public static final String EXTRA_BOOK_ID = "extra_book_id";

    private final String TAB_COMMENT_HOT = "tab_comment_hot";
    private final String TAB_COMMENT_LATEST = "tab_comment_latest";

    private String mBookId;

    public static void openActivity(Context context, String bookId){
        Intent intent = new Intent(context, BookCommentActivity.class);
        intent.putExtra(EXTRA_BOOK_ID, bookId);
        context.startActivity(intent);
    }

    @Override
    protected View newContentView(Bundle savedInstanceState) {
        View view = bindView(R.layout.book_comment_layout, this);
        mBookId = getIntent().getStringExtra(EXTRA_BOOK_ID);
        return view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //进入我的消息列表后先手动清除消息栏的推送消息
        MyAndroidApplication.clearNotityContent(this_);
        SlideTabWidget slideTabWidget = (SlideTabWidget)findViewById(android.R.id.tabs);
        slideTabWidget.initialize(0,null);
        bChildViewPagerTabHostAdapter.setItemLifeCycleListener(new BaseViewPagerTabHostAdapter.ItemLifeCycleListener() {
            @Override
            public void onDestroy(View view, int position) {
                if(view instanceof IPanelView){
                    ((IPanelView) view).onDestroy();
                }
            }

            @Override
            public boolean onCreate(View view, int position) {
                if(view instanceof IPanelView){
                    ((IPanelView) view).onCreate();
                }
                return true;
            }
        });
        setTitleContent(getString(R.string.my_msg_title));
    }

    private class ViewPagerAdapter extends BaseViewPagerTabHostAdapter{
        public ArrayList<String> mTags;
        public ViewPagerAdapter(){
            mTags = new ArrayList<String>();
            mTags.add(TAB_COMMENT_HOT);
            mTags.add(TAB_COMMENT_LATEST);
        }

        @Override
        public View getIndicator(int position) {
            // 字体描边效果。最土方式，使用层叠效果。
            View indicatorView;
            if(position==0){
                indicatorView = LayoutInflater.from(this_).inflate(
                        R.layout.comment_tab_layout_left, null);
            }else{
                indicatorView = LayoutInflater.from(this_).inflate(
                        R.layout.comment_tab_layout_right, null);
            }
            TextView textView1 = (TextView) indicatorView
                    .findViewById(R.id.text);
//			TextView textView2 = (TextView) indicatorView
//					.findViewById(R.id.text_bg);

            if (TAB_COMMENT_HOT.equals(getTab(position))) {
                String title = getResources().getString(R.string.msg_type_commend);
                textView1.setText(title);
//				textView2.setText(title);
            } else if (TAB_COMMENT_LATEST.equals(getTab(position))) {
                String title = getResources().getString(R.string.msg_type_push);
                textView1.setText(title);
//				textView2.setText(title);
            }
            return indicatorView;
        }

        @Override
        public String getTab(int position) {
            return mTags.get(position);
        }

        @Override
        public int getCount() {
            return mTags.size();
        }

        @Override
        public View getItemView(ViewGroup container, int position) {
            if(TAB_COMMENT_HOT.equals(getTab(position))){
                container = new BookCommentHotView(this_, mBookId);
            }else if(TAB_COMMENT_LATEST.equals(getTab(position))){
                container = new MyMessageView(this_);
            }
            return container;
        }
    }

}
