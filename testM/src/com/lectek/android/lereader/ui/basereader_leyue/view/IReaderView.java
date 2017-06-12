package com.lectek.android.lereader.ui.basereader_leyue.view;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.lectek.android.lereader.storage.dbase.mark.BookMark;
import com.lectek.android.lereader.ui.basereader_leyue.digests.TextSelectHandler;
import com.lectek.android.lereader.ui.basereader_leyue.digests.AbsTextSelectHandler.ITouchEventDispatcher;
import com.lectek.lereader.core.bookformats.BookInfo;
import com.lectek.lereader.core.bookformats.Catalog;

public interface IReaderView {

    /** 获取书籍信息失败 */
    public static final int ERROR_GET_CONTENT_INFO = -1;
    /** 获取书籍目录失败 */
    public static final int ERROR_GET_CATALOG_INFO = -2;
    /** 获取书籍章节信息失败 */
    public static final int ERROR_GET_CHAPTER_INFO = -3;
    /** 书籍已下线 */
    public static final int ERROR_BOOK_OFFLINE = -4;
    /** 书籍秘钥异常 */
    public static final int ERROR_BOOK_SECRET_KEY = -5;
    /** 本地epub文件秘钥缺失 */
    public static final int ERROR_LOCAL_EPUB_SECRET = -6;
    /** 成功 */
    public static final int SUCCESS = 1;

	/**
	 * 生命周期开始
	 */
	public void onCreate(Bundle savedInstanceState);
	/**
	 * 初始化方法，运行在子线程，失败返回false退出阅读界面
	 * @return
	 */
	public int onInitReaderInBackground(int fRequestCatalogIndex, int fRequestPageCharIndex ,String secretKey);
	/**
	 * 生命周期结束
	 */
	public void onDestroy();
	/**
	 * Activity Resume
	 */
	public void onActivityResume();
	/**
	 * Activity Pause
	 */
	public void onActivityPause();
	/**
	 * Activity onSaveInstanceState
	 * @param outState
	 */
	public void onActivitySaveInstanceState(Bundle outState);
	/**
	 * Activity onRetainNonConfigurationInstance
	 * @return
	 */
	public Object onActivityRetainNonConfigurationInstance();
	/**
	 * Activity  onRestoreInstanceState
	 * @param savedInstanceState
	 */
	public void onActivityRestoreInstanceState(Bundle savedInstanceState);
	/**
	 * 派遣Activity DispatchTouchEvent 事件
	 * @param ev
	 * @return
	 */
	public boolean onActivityDispatchTouchEvent(MotionEvent ev);
	/**
	 * 派遣Activity DispatchKeyEvent 事件
	 * @param event
	 * @return
	 */
	public boolean onActivityDispatchKeyEvent(KeyEvent event);
	/**
	 * 添加章节信息
	 */
	public ArrayList<Catalog> getChapterList();
	/**
	 * 制订调整进度字符，返回空折使用默认
	 * @param curPage
	 * @param pageNums
	 * @return
	 */
	public String getJumpProgressStr(int curPage, int pageNums);
	/**
	 * 跳章（点击目录）
	 * @param catalog 章节
	 * @param isStartAnim 是否播放动画
	 */
	public void gotoChapter(Catalog catalog,boolean isStartAnim);
	/**
	 * 跳章（点击目录）
	 * @param requestChapterIndex 章节数
	 * @param isStartAnim 是否播放动画
	 */
	public void gotoChapter(int requestChapterIndex,boolean isStartAnim);
	/**
	 * 是否有上一章
	 * @return
	 */
	public boolean hasPreChapter();
	/**
	 * 是否有下一章
	 * @return
	 */
	public boolean hasNextChapter();
	/**
	 * 跳转下一页
	 */
	public void gotoNextPage();
	/**
	 * 跳转上一页
	 */
	public void gotoPrePage();
	/**
	 * 跳转上一章
	 */
	public void gotoPreChapter();
	/**
	 * 跳转下一章
	 */
	public void gotoNextChapter();
	/**
	 * 跳转具体页（主要用于进度条跳转）
	 * @param requestProgress 具体页数或者百分比
	 * @param isStartAnim 是否播放动画
	 */
	public void gotoPage(int requestProgress,boolean isStartAnim);
	/**
	 * 跳转具体字符所在页
	 * @param requestChapterIndex
	 * @param requestCharIndex
	 * @param isStartAnim
	 */
	public void gotoChar(int requestChapterIndex,int requestCharIndex,boolean isStartAnim);
//	/**
//	 * 跳转书摘
//	 * @param bookDigests
//	 * @param isStartAnim
//	 */
//	public void gotoBookDigest(BookDigests bookDigests,boolean isStartAnim);
	/**
	 * 跳转书签
	 * @param bookmark
	 * @param isStartAnim
	 */
	public void gotoBookmark(BookMark bookmark,boolean isStartAnim);
	/**
	 * 是否有上一级
	 * @return
	 */
	public boolean hasPreSet();
	/**
	 * 是否有下一集
	 * @return
	 */
	public boolean hasNextSet();
	/**
	 * 跳转上一级
	 */
	public void gotoPreSet();
	/**
	 * 跳转下一级
	 */
	public void gotoNextSet();
	/**
	 * 生成系统书签
	 * @return
	 */
	public BookMark newSysBookmark();
	/**
	 * 生成用户书签
	 * @return
	 */
	public BookMark newUserBookmark();
	/**
	 * 最大阅读进度
	 * @return
	 */
	public int getMaxReadProgress();
	/**
	 * 当前阅读进度
	 * @return
	 */
	public int getCurReadProgress();
	/**
	 * 排版进度
	 * @return
	 */
	public int getLayoutChapterProgress();
	/**
	 * 排版进度最大值
	 * @return
	 */
	public int getLayoutChapterMax();
	/**
	 * 当内容显示
	 */
	public void onShowContentView();
	/**
	 * 当内容隐藏
	 */
	public void onHideContentView();
	/**
	 * 获取书记信息
	 * @return
	 */
	public BookInfo getBookInfo();
	/**
	 * 获取章节目录对象
	 */
	public Catalog getCurrentCatalog();
	/**
	 * 当前章节
	 */
	public int getCurChapterIndex();
	/**
	 * 当前某页开始字符位置
	 * @return
	 */
	public int getPageStartIndex(int chapterIndex, int pageIndex);
//	/**
//	 * 当前页结束字符位置
//	 * @return
//	 */
//	public int getCurPageEndIndex();
//	/**
//	 * 关闭书摘
//	 * @return 有效操作
//	 */
//	public boolean closeTextSelect();
	/**
	 * 书摘处理事件
	 * @param event
	 * @param touchEventDispatcher
	 * @return
	 */
	public boolean handlerSelectTouchEvent(MotionEvent event,ITouchEventDispatcher touchEventDispatcher);
	/**
	 * 处理触屏事件
	 * @param event
	 * @return
	 */
	public boolean handlerTouchEvent(MotionEvent event);
	/**
	 * 派遣点击事件
	 * @param event
	 * @return
	 */
	public boolean dispatchClickEvent(MotionEvent event);
	/**
	 * 是否可添加用户书签
	 * @return
	 */
	public boolean canAddUserBookmark();
	/**
	 * 获取书摘处理者
	 * @return
	 */
	public TextSelectHandler getTextSelectHandler();
	/**
	 * 获取内容View，如果自己就是View就返回自己
	 * @return
	 */
	public View getContentView();
	/**
	 * 处理购买结果
	 * @param chapterId
	 */
	public void dealBuyResult(int chapterId);
	/**
	 * 触发长按事件
	 */
	public boolean onLongPress();
	/**
	 * 搜索
	 * @param direction	1 往后搜  -1  往前搜
	 * @param keyWord 关键字
	 */
	public void search(int direction, String keyWord);

    /**
     * 章节目录信息转换
     * @param chaptersId
     * @return
     */
    public String getChapterId(int chaptersId);
	/**
	 * 对外接口
	 * @author lyw
	 */
	public interface IReadCallback{
		/**
		 * 章节添加【txt章节获取】
		 * */
		public void onChapterChange(ArrayList<Catalog> chapters);
		
		/**
		 * 当前页变化
		 * */
		public void onPageChange(int progress,int max);
		/**
		 * 章节排版布局进度
		 * @param progress 当前排版章节数
		 * @param max 最大章节数
		 */
		public void onLayoutProgressChange(int progress, int max);
		/**
		 * 没有上一页内容
		 */
		public void onNotPreContent();
		/**
		 * 没有下一页内容
		 */
		public void onNotNextContent();
		/**
		 * 检测是否需要购买
		 * @param catalogIndex
		 * @param isNeedBuy
		 * @return
		 */
		public boolean checkNeedBuy(int catalogIndex, boolean isNeedBuy);
		/**
		 * 显示等待对话框
		 */
		public void showLoadingDialog(int resId);
		/**
		 * 隐藏等待对话框
		 */
		public void hideLoadingDialog();

        /**
         * 该页是否已经标注为用户书签
         */
        public boolean hasShowBookMark(int chapterId, int pageStart, int pageEnd);

        /**
         * epub书籍目录收费章节起始修改
         */
        public boolean setFreeStart_Order_Price(int feeStart, boolean isOrdered, String price, String limitPrice);

        /**
         * epub书籍id设置，用于弹窗购买窗口后挑战到书籍详情
         */
        public void setCebBookId(String cebBookId);
	}
}
