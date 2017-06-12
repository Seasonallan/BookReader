package com.lectek.lereader.core.text;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;

import com.lectek.lereader.core.bookformats.PaserExceptionInfo;
import com.lectek.lereader.core.os.LayoutThreadPool;
import com.lectek.lereader.core.text.html.DataProvider;
import com.lectek.lereader.core.text.html.HtmlParser;
import com.lectek.lereader.core.text.html.HtmlParser.SizeInfo;
import com.lectek.lereader.core.text.html.HtmlParser.TagHandler;
import com.lectek.lereader.core.text.html.ICssProvider;
import com.lectek.lereader.core.text.layout.AbsPatch;
import com.lectek.lereader.core.text.layout.Layout;
import com.lectek.lereader.core.text.layout.Layout.LayoutCallback;
import com.lectek.lereader.core.text.layout.Page;
import com.lectek.lereader.core.util.ContextUtil;
import com.lectek.lereader.core.util.LogUtil;

/**
 * 页管理器
	1.Html绘制解析模块对外的唯一出入口，模块内部流程控制器
	2.配置模块内部相关接口以及必要参数
	3.管理页、请求页、绘制等逻辑
	4.管理页的缓存释放和绑定
	5.调用Layout排版。
	6.必须在主线程调用该类方法
	7.HTML排版解析逻辑运行子线程
 * @author linyiwei
 */
public class PageManager implements PatchParent{
	private static final String TAG = PageManager.class.getSimpleName();
	/** 请求绘制页结果：未知的请求页*/
	public static int RESULT_UN_KNOWN = -2;
	/** 请求绘制页结果：未初始化PageManager*/
	public static int RESULT_UN_INIT = -1;
	/** 请求绘制页结果：请求成功*/
	public static int RESULT_SUCCESS = 0;
	/** 请求绘制页结果：正在排版*/
	public static int RESULT_UN_LAYOUT = 1;
	
	/** 排版设置参数*/
	private SettingParam mSettingParam;
	/** 待执行章节任务列表*/
	private LinkedList<Integer> mNeedHandleChapterList;
	/** 需要初始化的章节*/
	private LinkedList<Integer> mNeedInitChapterList;
	/** 当前绑定的章节*/
	private ArrayList<ChapterTask> mChapterList;
	/** 当前绑定的页*/
	private LinkedList<Page> mBindPageList;
	/** 对外回调*/
	private PageManagerCallback mCallback;
	/** 每次重新排版都会生成ID如果和当前的任务ID不一致说明任务已经过期，主要用于短时间内多次触发排版逻辑时放弃前一页*/
	private volatile long mTaskID;
	/** 当前正在执行的章节任务*/
	private ChapterTask mCurrentRunChapterTask;
	/** 线程池*/
	private LayoutThreadPool mThreadPool;
	/** 总页数-1表示未全部排版完*/
	private Integer mTotalPageSize;
	/** 总页数用于计数（内部使用）*/
	private int mTempTotalPageSize;
	/** 临时的绑定页（为了解决翻页动画执行过程中，如果是跳章会导致不断绑定和释放页问题）*/
	private Page mBindTempPage;
	/** 临时页缓存*/
	private IPagePicture mTempPagePicture;
	/** 当前绑定页缓存*/
	private IPagePicture mBindPagePicture;
	/** 请求页缓存*/
	private IPagePicture mPagePicture;
	/** 大小信息*/
	private SizeInfo mSizeInfo;
	/** 是否整本排版(如果为非只排版当前章节的前后两章)*/
	private boolean isLayoutAll;
	/** 首次绘制是否结束*/
	private boolean isFirstDraw;
	
	private Handler mHandler;
	
	public PageManager(Context context,PageManagerCallback callback){
		this(context, callback, true);
	}
	/**
	 * 
	 * @param context
	 * @param callback 不能为空
	 * @param layoutAll 是否整本排版(如果为非只排版当前章节的前后两章)
	 */
	public PageManager(Context context,PageManagerCallback callback,boolean layoutAll){
		ContextUtil.init(context.getApplicationContext());
		mCallback = callback;
		mChapterList = new ArrayList<ChapterTask>();
		mBindPageList = new LinkedList<Page>();
		mNeedHandleChapterList = new LinkedList<Integer>();
		mNeedInitChapterList = new LinkedList<Integer>();
		mHandler = new Handler(Looper.getMainLooper());
		mThreadPool = new LayoutThreadPool();
		mTempPagePicture = new PageBitmapPicture(-1, -1,null);
		mPagePicture = new PageBitmapPicture(-1, -1,null);
		mBindPagePicture = new PageBitmapPicture(-1, -1,null);
		isLayoutAll = layoutAll;
	}
	/**
	 * 初始化
	 * @param settingParam 设置参数
	 * @param chapterSize 章节大小
	 * @param requestChapterIndex 请求章节，会优秀排版该章
	 */
	public void init(SettingParam settingParam,int chapterSize,int requestChapterIndex){
		if(chapterSize <= 0){
			return;
		}
		LogUtil.e(TAG, "init start");
		mSizeInfo = new SizeInfo((int) settingParam.getSourcePaint().getTextSize(), settingParam.getPageRect());
		mSettingParam = settingParam;
		mTaskID = System.currentTimeMillis();
		mChapterList.clear();
		mNeedHandleChapterList.clear();
		mNeedInitChapterList.clear();
		mTotalPageSize = null;
		mTempTotalPageSize = 0;
		clearBindPage();
		for(int i = 0;i < chapterSize;i++){
			mChapterList.add(new ChapterTask(i));
			mNeedInitChapterList.add(i);
		}
		
		//确定排版章节数
		int layoutSize = chapterSize;
		if(!isLayoutAll){
			layoutSize = Math.min(3, chapterSize);
		}
		//根据当前请求章节调整任务预先级
		for (int i = 0,j = requestChapterIndex + 1; i < layoutSize - 1;) {
			if(j >= 0 && j < chapterSize){
				mNeedHandleChapterList.add(j);
				i++;
			}
			if(j < requestChapterIndex){
				j = requestChapterIndex * 2 - j + 1;
			}else{
				j = requestChapterIndex * 2 - j;
			}
		}
		//绑定章节
		bindChapter(requestChapterIndex);
		//章节任务在执行完成之后从mNeedHandleChapterList中获取有是否需要执行的任务继续执行。
		mChapterList.get(requestChapterIndex).startTask();
		mCallback.onLayoutChapterFinish(-1,0, mChapterList.size());
	}
	/**
	 * 是否为整本排版
	 */
	public boolean isLayoutAll(){
		return isLayoutAll;
	}
	/**
	 * 清除绑定页
	 */
	private void clearBindPage(){
		for (Page Page : mBindPageList) {
			if(Page.isBind()){
				Page.unBindPatchParent();
			}
		}
		mBindPageList.clear();
	}
	/**
	 * 绑定章节
	 * @param chapterIndex
	 */
	private void bindChapter(int chapterIndex){
		for (int i = 0; i < mChapterList.size(); i++) {
			ChapterTask chapterTask = mChapterList.get(i);
			chapterTask.setBind(Math.abs(chapterIndex - i) <= 1);
		}
	}
	/**
	 * 整理绑定页，绑定该绑定的页，释放不该绑定的页
	 * @param chapterIndex
	 * @param pageIndex
	 */
	private void clearUpBindPage(int chapterIndex,int pageIndex){
		//有效绑定页，不需要解除绑定的页
		ArrayList<Page> effectiveCachePage = new ArrayList<Page>();
		ArrayList<Page> oldCachePage = new ArrayList<Page>(mBindPageList);
		
		clearUpBindPage(chapterIndex, pageIndex,effectiveCachePage);
		ChapterTask chapterTask = mChapterList.get(chapterIndex);
		int[] nextIndexs = getNextIndex(chapterTask,pageIndex);
		if(nextIndexs != null && nextIndexs[0] >= 0){
			Page page = clearUpBindPage(nextIndexs[0], nextIndexs[1],effectiveCachePage);
			if(page != null){
				//为了预加载一些绘制数据
				page.advanceDraw(mTempPagePicture.getCanvas(1, 1));
			}
		}
		
		int[] preIndexs = getPreIndex(chapterTask,pageIndex);
		if(preIndexs != null && preIndexs[0] >= 0){
			Page page = clearUpBindPage(preIndexs[0], preIndexs[1],effectiveCachePage);
			if(page != null){
				//为了预加载一些绘制数据
				page.advanceDraw(mTempPagePicture.getCanvas(1, 1));
			}
		}
		//释放未包含在有效绑定页列表里面的页
		for (Page bindsPage : oldCachePage) {
			if(!effectiveCachePage.contains(bindsPage)){
				unBindPatchParent(bindsPage);
			}
		}
	}
	/**
	 * 绑定页并清理不需要绑定的页
	 * @param chapterIndex
	 * @param pageIndex
	 * @param effectiveCachePage
	 */
	private Page clearUpBindPage(int chapterIndex,int pageIndex,ArrayList<Page> effectiveCachePage){
		ChapterTask chapterTask = mChapterList.get(chapterIndex);
		Page page = chapterTask.getPage(pageIndex);
		if(page != null){
			effectiveCachePage.add(page);
			return bindPatchParent(chapterIndex,pageIndex,page,chapterTask.getStyleText()) ? page : null;
		}
		return null;
	}
	/**
	 * 请求下一页
	 * @param chapterIndex
	 * @param pageIndex
	 * @return 第一个位置是章节下标，第二个位置是页下标，如果返回空说明没有内容或者出错，如果放回-1说明还在等待排版
	 */
	public int[] requestNextPage(int chapterIndex,int pageIndex){
		if(chapterIndex >= 0 && chapterIndex < mChapterList.size()){
			ChapterTask chapterTask = mChapterList.get(chapterIndex);
			return getNextIndex(chapterTask, pageIndex);
		}
		return null;
	}
	/**
	 * 请求下一页
	 * @param chapterTask
	 * @param pageIndex
	 * @return 第一个位置是章节下标，第二个位置是页下标，如果返回空说明没有内容或者出错，如果放回-1说明还在等待排版
	 */
	private int[] getNextIndex(ChapterTask chapterTask,int pageIndex){
		pageIndex++;
		int chapterIndex = chapterTask.getIndex();
		if(!chapterTask.isSetoverBeyond(pageIndex)){
			return new int[]{chapterIndex,pageIndex};
		}else if(chapterTask.isLayout()){
			chapterIndex++;
			if(chapterIndex >= 0 && chapterIndex < mChapterList.size()){
				chapterTask = mChapterList.get(chapterIndex);
				if(!chapterTask.isSetoverBeyond(0)){
					return new int[]{chapterIndex,0};
				}else if(!chapterTask.isLayout()){
					return new int[]{-1,-1};
				}else if(chapterTask.isEmpty()){
					return getNextIndex(chapterTask, 0);
				}
			}
		}else{
			return new int[]{-1,-1};
		}
		return null;
	}
	
	/**
	 * 请求上一页
	 * @param chapterIndex
	 * @param pageIndex
	 * @return 第一个位置是章节下标，第二个位置是页下标，如果返回空说明没有内容或者出错，如果放回-1说明还在等待排版
	 */
	public int[] requestPretPage(int chapterIndex,int pageIndex){
		if(chapterIndex >= 0 && chapterIndex < mChapterList.size()){
			ChapterTask chapterTask = mChapterList.get(chapterIndex);
			return getPreIndex(chapterTask, pageIndex);
		}
		return null;
	}
	/**
	 * 请求上一页
	 * @param chapterIndex
	 * @param pageIndex
	 * @return 第一个位置是章节下标，第二个位置是页下标，如果返回空说明没有内容或者出错，如果放回-1说明还在等待排版
	 */
	private int[] getPreIndex(ChapterTask chapterTask,int pageIndex){
		pageIndex--;
		int chapterIndex = chapterTask.getIndex();
		if(!chapterTask.isSetoverBeyond(pageIndex)){
			return new int[]{chapterIndex,pageIndex};
		}else if(pageIndex == -1){
			chapterIndex--;
			if(chapterIndex >= 0 && chapterIndex < mChapterList.size()){
				chapterTask = mChapterList.get(chapterIndex);
				if(chapterTask.isLayout()){
					if(chapterTask.isEmpty()){
						return getPreIndex(chapterTask, 0);
					}
					return new int[]{chapterIndex,chapterTask.getPages().size() - 1};
				}else{
					return new int[]{-1,-1};
				}
			}
		}
		return null;
	}
	/**
	 * 重新排版后，首次绘制是否结束
	 * @return
	 */
	public boolean isFirstDraw(){
		return isFirstDraw;
	}
	/**
	 * 绑定TempPage
	 * @param chapterIndex
	 * @param pageIndex
	 * @param bindChapterIndex
	 * @param bindPageIndex
	 * @param page
	 * @param styleText
	 */
	private void bindTempPage(final int chapterIndex,final int pageIndex,int bindChapterIndex,int bindPageIndex,Page page,StyleText styleText) {
		if(chapterIndex == bindChapterIndex && pageIndex == bindPageIndex){
			if(mBindTempPage != null && !mBindPageList.contains(mBindTempPage)){
				if(mBindTempPage.isBind()){
					mBindTempPage.unBindPatchParent();
				}
				mBindTempPage = null;
			}
			return;
		}
		if(page.equals(mBindTempPage)){
			if(mBindTempPage.isBind()){
				return;
			}else{
				mBindTempPage = null;
			}
		}
		if(!mBindPageList.contains(page)){
			if(mBindTempPage != null && !mBindPageList.contains(mBindTempPage)){
				if(mBindTempPage.isBind()){
					mBindTempPage.unBindPatchParent();
				}
				mBindTempPage = null;
			}
			mBindTempPage = page;
			bindPatch(chapterIndex, pageIndex, page, styleText);
		}
	}
	/**
	 * 绑定页
	 * @param page
	 */
	private boolean bindPatchParent(final int chapterIndex,final int pageIndex,Page page,StyleText styleText) {
		if(!mBindPageList.contains(page)){
			mBindPageList.addLast(page);
			return bindPatch(chapterIndex, pageIndex, page, styleText);
		}
		return false;
	}
	/**
	 * 绑定页
	 * @param chapterIndex
	 * @param pageIndex
	 * @param page
	 * @param styleText
	 */
	private boolean bindPatch(final int chapterIndex,final int pageIndex,Page page,StyleText styleText) {
		if(page.isBind()){
			return false;
		}
		page.bindPatchParent(this,styleText,new DrawCallback() {
			@Override
			public void onPostDrawContent(Canvas canvas,boolean isFullScreen) {
				mCallback.onPostDrawContent(canvas, chapterIndex, pageIndex,isFullScreen);
			}
			
			@Override
			public void onPreDrawContent(Canvas canvas,boolean isFullScreen) {
				mCallback.onPreDrawContent(canvas, chapterIndex, pageIndex,isFullScreen);
			}
		});
		return true;
	}
	/**
	 * 解除绑定
	 * @param page
	 */
	private void unBindPatchParent(Page page) {
		if(mBindPageList.remove(page) && page.isBind()){
			page.unBindPatchParent();
		}
	}
	/**
	 * 验证是否初始化
	 * @return
	 */
	public boolean isInit(){
		return mSettingParam != null && mCallback != null && mChapterList.size() > 0;
	}
	/**
	 * 清除初始化
	 */
	public void setUnInit(){
		mTaskID = System.currentTimeMillis();
		mChapterList.clear();
		mBindPagePicture.init(-1, -1,null);
		mPagePicture.init(-1, -1,null);
		mTempPagePicture.init(-1, -1, null);
	}
	/**
	 * 动态设置样式
	 * @param chapterIndex
	 * @param what
	 * @param start
	 * @param end
	 * @param flags
	 */
	public void setSpan(int chapterIndex,Object what, int start, int end, int flags){
		if(!isInit()){
			return;
		}
		if(chapterIndex < 0 || chapterIndex >= mChapterList.size()){
			return;
		}
		ChapterTask chapterTask = mChapterList.get(chapterIndex);
		if(!chapterTask.isPositionSetoverBeyond(start,end)){
			chapterTask.mStyleText.getDataSource().setSpan(what, start, end, flags);
		}
		invalidateCachePage();
	}
	/**
	 * 删除动态设置的样式
	 * @param chapterIndex
	 * @param what
	 */
	public void removeSpan(int chapterIndex,Object what) {
		if(!isInit()){
			return;
		}
		if(chapterIndex < 0 || chapterIndex >= mChapterList.size()){
			return;
		}
		ChapterTask chapterTask = mChapterList.get(chapterIndex);
		if(chapterTask.mStyleText != null){
			chapterTask.mStyleText.getDataSource().removeSpan(what);
		}
		invalidateCachePage();
	}
	
	/**
	 * 截取数据源
	 * @param chapterIndex
	 * @param start
	 * @param end
	 * @return
	 */
	public String subSequence(int chapterIndex,int start, int end) {
		if(!isInit()){
			return null;
		}
		if(chapterIndex < 0 || chapterIndex >= mChapterList.size()){
			return null;
		}
		ChapterTask chapterTask = mChapterList.get(chapterIndex);
		if(start >= end || chapterTask.isPositionSetoverBeyond(start,start + 1)){
			return null;
		}else{
			if(chapterTask.mStyleText.getTotalLength() < end){
				end = chapterTask.mStyleText.getTotalLength();
			}
			return chapterTask.mStyleText.getDataSource().subSequence(start, end).toString();
		}
	}
	/**
	 * 找到坐标所在的绘制元素，在数据源中的位置（相当于文字位置，也可以是图片等）
	 * @param pageIndex
	 * @param x
	 * @param y
	 * @param isAccurate
	 * @return
	 */
	public int findIndexByLocation(int chapterIndex,int pageIndex,int x, int y, boolean isAccurate) {
		if(!isInit()){
			return -1;
		}
		if(chapterIndex < 0 || chapterIndex >= mChapterList.size()){
			return -1;
		}
		ChapterTask chapterTask = mChapterList.get(chapterIndex);
		if(chapterTask.isSetoverBeyond(pageIndex)){
			return -1;
		}else{
			Page page = chapterTask.getPage(pageIndex);
			if(page == null){
				return -1;
			}else{
				return page.findIndexByLocation(x, y,isAccurate);
			}
		}
	}
	/**
	 * 返回绘制元素的宽高等信息
	 */
	public Rect findRectByPosition(int chapterIndex,int pageIndex, int position) {
		if(!isInit()){
			return null;
		}
		if(chapterIndex < 0 || chapterIndex >= mChapterList.size()){
			return null;
		}
		ChapterTask chapterTask = mChapterList.get(chapterIndex);
		if(chapterTask.isSetoverBeyond(pageIndex)){
			return null;
		}else{
			Page page = chapterTask.getPage(pageIndex);
			if(page == null){
				return null;
			}else{
				return page.findRectByPosition(position);
			}
		}
	}
	/**
	 * 获取当前页的第一个位置
	 * @return
	 */
	public int findPageFirstIndex(int chapterIndex,int pageIndex){
		if(!isInit()){
			return -1;
		}
		if(chapterIndex < 0 || chapterIndex >= mChapterList.size()){
			return -1;
		}
		ChapterTask chapterTask = mChapterList.get(chapterIndex);
		if(chapterTask.isSetoverBeyond(pageIndex)){
			return -1;
		}else{
			Page page = chapterTask.getPage(pageIndex);
			if(page == null){
				return -1;
			}else{
				return page.getStart();
			}
		}
	}
	/**
	 * 获取当前页的最后一个位置
	 * @return
	 */
	public int findPageLastIndex(int chapterIndex,int pageIndex){
		if(!isInit()){
			return -1;
		}
		if(chapterIndex < 0 || chapterIndex >= mChapterList.size()){
			return -1;
		}
		ChapterTask chapterTask = mChapterList.get(chapterIndex);
		if(chapterTask.isSetoverBeyond(pageIndex)){
			return -1;
		}else{
			Page page = chapterTask.getPage(pageIndex);
			if(page == null){
				return -1;
			}else{
				return page.getEnd();
			}
		}
	}
	/**
	 * 调整章节任务优先级
	 * @param chapterIndex
	 */
	private void reprioritizeChapterTasks(int chapterIndex){
		if(chapterIndex >= 0 && chapterIndex < mChapterList.size()){
			ChapterTask chapterTask = mChapterList.get(chapterIndex);
			Integer chapterTaskIndex = mNeedHandleChapterList.peekFirst();
			if(chapterTaskIndex == null){
				chapterTaskIndex = -1;
			}
			if(chapterTask.isNeedRunTask() && !chapterTask.equals(mCurrentRunChapterTask) && 
					chapterTaskIndex != chapterIndex){
				mNeedHandleChapterList.remove(Integer.valueOf(chapterIndex));
				mNeedHandleChapterList.addFirst(chapterIndex);
			} 
		}
	} 


	public SettingParam getSettingParam() {
		return mSettingParam;
	}
	
	/**
	 * 通过位置获取章节与当前页码
	 * @param pageIndex
	 * @return
	 */
	public int[] getPage(int pageIndex){  
		if(mChapterList.size() <= 0){
			return null;
		}
		for (int i = 0; i < mChapterList.size(); i++) {
			ChapterTask chapterTask = mChapterList.get(i); 
			LinkedList<Page> pages = chapterTask.getPages();  
			if(pages.size() <= pageIndex){
				pageIndex -= pages.size();
			}else{
				return new int[]{i, pageIndex};
			}
		}
		return null;
	}
	
	/**
	 * 通过章节和页码获取当前页位置
	 * @param chapterIndex
	 * @param pageIndex
	 * @return
	 */
	public int getPageIndex(int chapterIndex, int pageIndex){  
		if(mChapterList.size() <= chapterIndex || chapterIndex < 0){
			return 0;
		}  
		int realIndex = pageIndex;
		for (int i = 0; i < chapterIndex; i++) {
			ChapterTask chapterTask = mChapterList.get(i); 
			LinkedList<Page> pages = chapterTask.getPages(); 
			realIndex += pages.size();
		} 
		return realIndex;
	} 
	
	/**
	 * 请求绘制页
	 * @param canvas 需要绘制内容的画布
	 * @param chapterIndex 当前需要绘制章节下标
	 * @param pageIndex 当前需要绘制页下标
	 * @param bindChapterIndex 必须是固定的值，其实就是当前章或者请求章
	 * @param bindPageIndex 必须是固定的值，其实就是当前页或者请求页
	 * 连接页会执行预加载上下两页，和前后两章
	 * @return 返回请求结果
	 */
	public int requestDrawPage(Canvas canvas,int chapterIndex,int pageIndex,int bindChapterIndex,int bindPageIndex){
		int result = RESULT_UN_INIT;
		if(!isInit()){
			mCallback.drawWaitingContent(canvas, chapterIndex,isFirstDraw);
		}else{
//			recycleChapterTask(bindChapterIndex);
			//绑定页和章节 主要为了处理预加载
			bindChapter(bindChapterIndex);
			//调整任务优先级
			reprioritizeChapterTasks(bindChapterIndex - 1);
			reprioritizeChapterTasks(bindChapterIndex + 1);
			
			if(!mCallback.handRequestIndex(canvas,chapterIndex, pageIndex, bindChapterIndex, bindPageIndex)){
				clearUpBindPage(bindChapterIndex, bindPageIndex);
				//处理当前需要绘制的内容
				ChapterTask chapterTask = mChapterList.get(chapterIndex);
				chapterTask.setBind(true);
				Page page = chapterTask.getPage(pageIndex);
				if(page != null){
					bindTempPage(chapterIndex,pageIndex,bindChapterIndex, bindPageIndex,page,chapterTask.getStyleText());
					drawContent(canvas,page, chapterIndex, pageIndex, bindChapterIndex, bindPageIndex);
					result = RESULT_SUCCESS;
				}else if(chapterTask.isNeedRunTask()){
					if(mCurrentRunChapterTask == null){
						chapterTask.startTask();
					}else{
						reprioritizeChapterTasks(chapterIndex);
					}
					result = RESULT_UN_LAYOUT;
					mCallback.drawWaitingContent(canvas, chapterIndex,isFirstDraw);
				}else{
					result = RESULT_UN_LAYOUT;
					mCallback.drawWaitingContent(canvas, chapterIndex,isFirstDraw);
				}
			}
			
			if(mCurrentRunChapterTask == null){
				Integer chapterTaskIndex = mNeedHandleChapterList.pollFirst();
				if(chapterTaskIndex != null){
					mChapterList.get(chapterTaskIndex).startTask();
				}
			}
		}
		return result;
	}
	/**
	 * 绘制内容
	 * @param canvas
	 * @param page
	 * @param chapterIndex
	 * @param pageIndex
	 * @param bindChapterIndex
	 * @param bindPageIndex
	 */
	private void drawContent(Canvas canvas,Page page,int chapterIndex,int pageIndex,int bindChapterIndex,int bindPageIndex){
		boolean isNeedDraw = isFirstDraw || page.isFinish();
		if(chapterIndex == bindChapterIndex && pageIndex == bindPageIndex){
			if(!mBindPagePicture.equals(chapterIndex, pageIndex)){
				mBindPagePicture.init(chapterIndex, pageIndex,page);
				page.draw(mBindPagePicture.getCanvas(mSettingParam.getFullPageRect().width(), mSettingParam.getFullPageRect().height()));
			}
			if(isNeedDraw){
				isFirstDraw = true;
				mBindPagePicture.onDraw(canvas);
			}else{
				mCallback.drawWaitingContent(canvas, chapterIndex,isFirstDraw);
			}
		}else{
			if(!mPagePicture.equals(chapterIndex, pageIndex)){
				mPagePicture.init(chapterIndex, pageIndex,page);
				page.draw(mPagePicture.getCanvas(mSettingParam.getFullPageRect().width(), mSettingParam.getFullPageRect().height()));
			}
			if(isNeedDraw){
				isFirstDraw = true;
				mPagePicture.onDraw(canvas);
			}else{
				mCallback.drawWaitingContent(canvas, chapterIndex,isFirstDraw);
			}
		}
	}
	
	/**
	 * 根据字符串位置找到是属于第几页的
	 * 返回-1代表没有找到
	 * @param chapterIndex
	 * @param pageCharIndex
	 * @return
	 */
	public int findPageIndex(int chapterIndex,int pageCharIndex){
		if(!isInit()){
			return -1;
		}
		if(chapterIndex >= 0 && chapterIndex < mChapterList.size()){
			ChapterTask chapterTask = mChapterList.get(chapterIndex);
			return chapterTask.findPageIndex(pageCharIndex);
		}
		return -1;
	}
	
	@Override
	public void invalidate(AbsPatch patch) {
		if(patch instanceof Page){
			if(mBindPagePicture.equals((Page)patch)){
				mBindPagePicture.init(-1, -1,null);
			}
			if(mPagePicture.equals((Page)patch)){
				mPagePicture.init(-1, -1,null);
			}
		}
		mCallback.invalidateView(null);
	}
	/**
	 * 在绘制元素充满全屏时，用来获取实际的内容区域
	 * @param chapterIndex
	 * @param pageIndex
	 * @return
	 */
	public Rect getFullScreenContentRect(int chapterIndex,int pageIndex){
		if(chapterIndex >= 0 && chapterIndex < mChapterList.size()){
			ChapterTask chapterTask = mChapterList.get(chapterIndex);
			if(!chapterTask.isSetoverBeyond(pageIndex)){
				Page page = chapterTask.getPage(pageIndex);
				if(page != null){
					return page.getFullScreenContentRect();
				}
			}
		}
		return null;
	}
	/**
	 * 刷新绑定页
	 * @param canvas
	 * @param pageIndex
	 * @return
	 */
	public void invalidateCachePage() {
		clearPageCache();
		mCallback.invalidateView(null);
	}
	/**
	 * 清除页图片缓存
	 */
	public void clearPageCache(){
		if(!isInit()){
			return;
		}
		mBindPagePicture.init(-1, -1,null);
		mPagePicture.init(-1, -1,null);
		mTempPagePicture.init(-1, -1, null);
	}

    /**
     * 横竖屏切换时回收图片 用来下次重新赋值宽高
     */
    public void onConfigurationChanged(){
        mBindPagePicture.release();
        mPagePicture.release();
        mTempPagePicture.release();
    }

	/**
	 * 释放资源
	 */
	public void release() {
		if(!isInit()){
			return;
		}
		mThreadPool.destroy();
		mTaskID = System.currentTimeMillis();
		
		clearBindPage();
		
		mBindPagePicture.release();
		mPagePicture.release();
		mTempPagePicture.release();
	}
	/**
	 * 是否是全屏页
	 * @param chapterIndex
	 * @param pageIndex
	 * @return
	 */
	public boolean isFullScreen(int chapterIndex,int pageIndex){
		if(chapterIndex >= 0 && chapterIndex < mChapterList.size()){
			ChapterTask chapterTask = mChapterList.get(chapterIndex);
			if(!chapterTask.isSetoverBeyond(pageIndex)){
				Page page = chapterTask.getPage(pageIndex);
				if(page != null){
					return page.isFullScreen();
				}
			}
		}
		return false;
	}
	/**
	 * 获取章节总页数
	 * @return
	 */
	public int getChapterPageSize(int chapterIndex){
		if(chapterIndex >= 0 && chapterIndex < mChapterList.size()){
			ChapterTask chapterTask = mChapterList.get(chapterIndex);
			if(chapterTask.isLayout()){
				return chapterTask.getPages().size();
			}
		}
		return -1;
	}
	/**
	 * 获取总页数-1表示未全部排版完
	 * @return
	 */
	public int getTotalPageSize(){
		if(mTotalPageSize == null){
			return -1;
		}
		return mTotalPageSize;
	}
	/**
	 * 获取相对于总页数的位置
	 * @param chapterIndex
	 * @param pageIndex
	 * @return
	 */
	public int getTotalPageIndex(int chapterIndex,int pageIndex){
		if(getTotalPageSize() == -1){
			return -1;
		}
		int totalPageIndex = 0;
		for (ChapterTask chapterTask : mChapterList) {
			if(chapterTask.getIndex() < chapterIndex){
				totalPageIndex += chapterTask.getPages().size();
			}else if(chapterTask.getIndex() == chapterIndex){
				totalPageIndex += pageIndex;
			}else{
				break;
			}
		}
		return totalPageIndex;
	}
	/**
	 * 解析总页数的位置相对的章节位置和页位置
	 * @param totalPageIndex
	 * @return
	 */
	public int[] findPageIndexByTotal(int totalPageIndex){
		if(getTotalPageSize() > totalPageIndex){
			for (int i = 0;i < mChapterList.size();i++) {
				ChapterTask chapterTask = mChapterList.get(i);
				if(totalPageIndex >= chapterTask.getPages().size()){
					totalPageIndex -= chapterTask.getPages().size();
				}else{
					return new int[]{i,totalPageIndex};
				}
			}
		}
		return null;
	}
	/**
	 * 派遣点击事件
	 * @param v
	 * @param x
	 * @param y
	 * @param pageIndex
	 * @return
	 */
	public boolean dispatchClick(View v,int x,int y,int chapterIndex,int pageIndex){
		if(chapterIndex >= 0 && chapterIndex < mChapterList.size()){
			ChapterTask chapterTask = mChapterList.get(chapterIndex);
			if(!chapterTask.isSetoverBeyond(pageIndex)){
				Page page = chapterTask.getPage(pageIndex);
				if(page != null && page.dispatchClick(v, x, y)){
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * 获取当前排版进度
	 * @return
	 */
	public int getLayoutChapterProgress(){
		return mChapterList.size() - mNeedInitChapterList.size();
	}
	/**
	 * 总章节数
	 * @return
	 */
	public int getLayoutChapterMax(){
		return mChapterList.size();
	}
	/**
	 * 运行在主线程
	 * @param action
	 */
	private final void runOnUiThread(Runnable action) {
		if(action == null){
			return;
		}
		if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
			mHandler.post(action);
		} else {
		    action.run();
		}
	}
	/**
	 * 代表章节的解析排版任务，每个任务会在结束时自动下一个任务列表里的任务
	 * @author linyiwei
	 *
	 */
	private class ChapterTask implements LayoutCallback{
		private StyleText mStyleText;
		private SoftReference<StyleText> mCacheStyleText;
		private LinkedList<Page> mPages;
		private int mIndex;
		private boolean isBind;
		private boolean isLayout;
		private long mRunTime;
		private Boolean hasDBLayoutData;
//		private TaskListener mTaskListener;
		
		private ChapterTask(int index){
			mIndex = index;
			mPages = new LinkedList<Page>();
			mCacheStyleText = new SoftReference<StyleText>(null);
			hasDBLayoutData = mCallback.hasDataDB(mSettingParam.getContentId(), getKey());
		}
		/**
		 * 根据字符位置查找页的位置
		 * @param pageCharIndex
		 * @return
		 */
		private int findPageIndex(int pageCharIndex) {
			if(pageCharIndex < 0){
				return mPages.size() > 0 ? 0 : -1;
			}
			int i = 0;
			for (Page page : mPages) {
				if(page.getStart() <= pageCharIndex && page.getEnd() >= pageCharIndex){
					return i;
				}
				i++;
			}
			if(isLayout){
				return mPages.size() - 1;
			}
			return -1;
		}
		
//		private void stop(){
//			if(mTaskListener != null){
//				mTaskListener.stop();
//			}
//		}
		/**
		 * 开始执行任务
		 */
		private void startTask() {
			mRunTime = 0;
			LogUtil.i(TAG, "startTask index="+mIndex + " isLayout="+isLayout + " isBind="+isBind + " isNeedRunTask="+isNeedRunTask());
			mCurrentRunChapterTask = this;
			final TaskListener taskListener = new TaskListener(PageManager.this, mTaskID);
//			mTaskListener = taskListener;
			final HtmlParser htmlParser = HtmlParser.create(mSettingParam.getParserType(),mCallback.getCssProvider()
					,mCallback.getDataProvider(),taskListener,mCallback.getTagHandler(),mSizeInfo);
			final Layout layout;
			if(!isLayout){
				long lastTiem = System.currentTimeMillis();
				LogUtil.i(TAG, "hasDataDB "+hasDBLayoutData+" time="+(System.currentTimeMillis() - lastTiem));
				if(!hasDBLayoutData){
					layout = new Layout(htmlParser.getStyleText(), mSettingParam,this,taskListener);
				}else{
					layout = null;
				}
			}else{
				layout = null;
			}
			mThreadPool.addTask(new Runnable() {
				@Override
				public void run() {
                    boolean isNeedSaveLayoutInDB = false;
					try{
						long lastTiem = System.currentTimeMillis();
						htmlParser.start(mCallback.getChapterInputStream(mIndex));
						if(taskListener.isStop()){
							return;
						}
						if(layout != null){
							layout.startLayout(0, htmlParser.getStyleText().getTotalLength() - 1);
							isNeedSaveLayoutInDB = true;
							layout.setCallback(null);
						}else if(hasDBLayoutData && !isLayout){
							loadLayoutInDB(taskListener);
						}
						if(taskListener.isStop()){
							return;
						}
						mRunTime += System.currentTimeMillis() - lastTiem;
						onTaskFinish(taskListener,htmlParser.getStyleText(),isNeedSaveLayoutInDB);
					}catch (Exception e) {
						PaserExceptionInfo info = mCallback.getPaserExceptionInfo();
						StringBuilder sb = new StringBuilder();
						if(info != null){
							sb.append("书籍id= "+info.getBookId());
							sb.append(";书籍名称= "+info.getBookName());
							sb.append(";章节数= "+info.getChapterNum());
                            sb.append(";章节= "+mIndex);
						}
						LogUtil.e("解析异常", sb.toString()+e);
                        onTaskFinish(taskListener,htmlParser.getStyleText(),isNeedSaveLayoutInDB);
						//throw new RuntimeException(sb.toString()+e);
					}
				}
			});
		}
		/**
		 * 保存排版结果的key，它是由任何影响排版的结果的值组合而成
		 * @return
		 */
		private String getKey(){
			return mSettingParam.getKey() + "-" + mIndex;
		}
		/**
		 * 从数据库读取排版结果（有bug 暂时没有作用）
		 * @param task
		 */
		private void loadLayoutInDB(TaskListener taskListener){
			if(taskListener.isStop()){
				return;
			}
			String data = mCallback.getDataDB(mSettingParam.getContentId(),getKey());
			if(taskListener.isStop()){
				return;
			}
			if(!TextUtils.isEmpty(data)){
				long lastTiem = System.currentTimeMillis();
				int pageSize = 0;
				int start = 0;
				int end = -1;
				for (int i = 0; i < data.length(); i++) {
					char c = data.charAt(i);
					if(c == '-'){
						end = 0;
					}else if(c == ';'){
						Page page = new Page(mSettingParam,pageSize++);
						page.setContent(start, end);
						onLayoutFinishPage(taskListener,page,end);//TODO 计算字符总长度
						end = -1;
						start = 0;
					}else{
						if(end < 0){
							start = start * 10 + (c - 48) ;
						}else{
							end = end * 10 + (c - 48) ;
						}
					}
				}
				LogUtil.i(TAG, "loadLayoutInDB pageSize=" +pageSize+ " time="+(System.currentTimeMillis() - lastTiem));
			}
		}
		/**
		 * 把排版结果保存到数据库（有bug 暂时没有作用）
		 * @param task
		 */
		private void saveLayoutInDB(TaskListener taskListener){
			if(taskListener.isStop()){
				return;
			}
			synchronized (mPages) {
				if(mPages.size() > 0){
					long lastTiem = System.currentTimeMillis();
					StringBuffer sb = new StringBuffer();
					for (Page page : mPages) {
						sb.append(page.getStart());
						sb.append("-");
						sb.append(page.getEnd());
						sb.append(";");
					}
					mCallback.saveDataDB(mSettingParam.getContentId(), getKey(), sb.toString());
					LogUtil.i(TAG, "saveLayoutInDB pageSize=" +mPages.size()+ " time="+(System.currentTimeMillis() - lastTiem));
				}
			}
		}
		/**
		 * 任务执行结束点，并执行下一个任务,绑定章节
		 */
		private void onTaskFinish(final TaskListener taskListener,final StyleText styleText,final boolean isNeedSaveLayoutInDB){
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(taskListener.isStop()){
						return;
					}
					if(isNeedSaveLayoutInDB){
						mThreadPool.addTask(new Runnable() {
							@Override
							public void run() {
								saveLayoutInDB(taskListener);
							}
						});
					}
					LogUtil.i(TAG, "OnTaskFinish index="+mIndex + " styleText="+styleText + " isBind="+isBind + " mRunTime="+mRunTime);
					isLayout = true;
					if(isBind){
						mStyleText = styleText;
						mCacheStyleText = new SoftReference<StyleText>(mStyleText);
					}else{
						mStyleText = null;
					}
					if(mNeedInitChapterList.contains(mIndex)){
						mNeedInitChapterList.remove(Integer.valueOf(mIndex));
						if(mNeedInitChapterList.size() == 0){
							mTotalPageSize = mTempTotalPageSize;
							mTempTotalPageSize = 0;
							LogUtil.e(TAG, "init end");
						}
						mCallback.onLayoutChapterFinish(mIndex,mChapterList.size() - mNeedInitChapterList.size(), mChapterList.size());
					}
					mCurrentRunChapterTask = null;
					Integer chapterIndex = mNeedHandleChapterList.pollFirst();
					if(chapterIndex != null){
						mChapterList.get(chapterIndex).startTask();
					}
				}
			});
		}
		
		@Override
		public void onLayoutFinishPage(final TaskListener taskListener,final Page page,final int totalLength) {
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {}
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(taskListener.isStop()){
						return;
					}
					mPages.add(page);
					mCallback.onLayoutPageFinish(mIndex, mPages.size() - 1,page.getEnd(),totalLength);
					mTempTotalPageSize++;
				}
			});
		}
		
		private Page getPage(int index){
			Page page = null;
			if(!isSetoverBeyond(index)){
				page = mPages.get(index);
			}
			if(page != null 
					&& mStyleText != null 
					&& mStyleText.getDataSource() != null 
					&& mStyleText.getTotalLength() > 0
					&& mStyleText.getTotalLength() > page.getEnd()){
				return page;
			}else{
				return null;
			}
		}
		
		private int getIndex(){
			return mIndex;
		}
		
		private LinkedList<Page> getPages(){
			return mPages;
		}
		
		private boolean isSetoverBeyond(int index){
			return index < 0 || index >= mPages.size();
		}
		
		private boolean isPositionSetoverBeyond(int start, int end){
			return mStyleText == null || start >= end || start < 0 || end <= 0 || mStyleText.getTotalLength() < end;
		}
		
		private boolean isNeedRunTask(){
			return mStyleText == null || !isLayout;
		}
		
		private boolean isLayout(){
			return isLayout;
		}
		
		private boolean isEmpty(){
			return mPages != null && mPages.size() == 0;
		}
		
		private void setBind(boolean isBind){
			if(!isBind){
				if(mStyleText != null){
					mCacheStyleText = new SoftReference<StyleText>(mStyleText);
					mStyleText = null;
				}
			}else{
				if(mCacheStyleText.get() != null){
					mStyleText = mCacheStyleText.get();
					mCacheStyleText.clear();
				}
			}
			this.isBind = isBind;
		}
		
		private StyleText getStyleText(){
			return mStyleText;
		}
	}
	
	public static class TaskListener{
		private boolean isStop;
		private long mTaskID;
		private WeakReference<PageManager> mPageManager;
		public TaskListener(PageManager pageManager,long taskID){
			mPageManager = new WeakReference<PageManager>(pageManager);
			mTaskID = taskID;
		}
		
		public void stop(){
			isStop = true;
		}
		
		public boolean isStop(){
			return isStop || mPageManager.get() != null ? mPageManager.get().mTaskID != mTaskID : true;
		};
	}

	/**
	 * 对外回调
	 * @author lyw
	 */
	public interface PageManagerCallback{
		/**
		 * 刷新界面
		 */
		public void invalidateView(Rect dirty);
		/**
		 * 页布局进度
		 * @param chapterIndex
		 * @param pageIndex
		 */
		public void onLayoutPageFinish(int chapterIndex, int pageIndex,int curChar, int maxChar);
		/**
		 * 章节布局进度
		 * @param progress
		 * @param max
		 */
		public void onLayoutChapterFinish(int chapterIndex,int progress,int max);
		/**
		 * 获取html扩展者
		 * @return
		 */
		public TagHandler getTagHandler();
		/**
		 * 获取章节数据
		 * @param chapterIndex
		 * @return
		 */
		public String getChapterInputStream(int chapterIndex);
		/**
		 * 获取Css加载器
		 * @return
		 */
		public ICssProvider getCssProvider();
		/**
		 * 获取数据加载器
		 * @return
		 */
		public DataProvider getDataProvider();
		/**
		 * 绘制等待页面
		 */
		public void drawWaitingContent(Canvas canvas,int chapterIndex,boolean isFirstDraw);
		/**
		 * 绘制内容页之后
		 * @param canvas 需要绘制内容的画布
		 * @param chapterIndex 章节下标
		 * @param pageIndex 页下标
		 */
		public void onPostDrawContent(Canvas canvas,int chapterIndex,int pageIndex,boolean isFullScreen);
		/**
		 * 绘制内容页之前
		 * @param canvas 需要绘制内容的画布
		 * @param chapterIndex 章节下标
		 * @param pageIndex 页下标
		 */
		public void onPreDrawContent(Canvas canvas,int chapterIndex,int pageIndex,boolean isFullScreen);
		/**
		 * 保存排版结果
		 * @param contentId
		 * @param key
		 * @param date
		 */
		public void saveDataDB(String contentId,String key,String data);
		/**
		 * 获取排版数据
		 * @param contentId
		 * @param key
		 * @return
		 */
		public String getDataDB(String contentId,String key);
		/**
		 * 查询是否数据
		 * @param contentId
		 * @param key
		 */
		public boolean hasDataDB(String contentId,String key);
		/**
		 * 给外部优先处理下标机会
		 * @param chapterIndex
		 * @param pageIndex
		 * @return true 代表已经处理
		 */
		public boolean handRequestIndex(Canvas canvas,int chapterIndex,int pageIndex,int bindChapterIndex,int bindPageIndex);
		/**
		 * 获取书籍信息
		 * @return
		 */
		public PaserExceptionInfo getPaserExceptionInfo();
	}
}
