/*
 * ========================================================
 * ClassName:ContentInfoViewModelLeyue.java* 
 * Description:
 * Copyright (C) 
 * ========================================================
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *===================================================================*
 * Revision History
 *
 * Modification                    Tracking
 * Date         Author          Number       Description of changes
 *____________________________________________________________________
 * 
 * 2013-9-28     chendt          #00000       create
 */
package com.lectek.android.lereader.binding.model.contentinfo;

import gueei.binding.Command;
import gueei.binding.collections.ArrayListObservable;
import gueei.binding.converters.HIGHLIGHT_SPAN.SpanListCreatorCommand;
import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.FloatObservable;
import gueei.binding.observables.IntegerObservable;
import gueei.binding.observables.SpanObservable;
import gueei.binding.observables.SpanObservable.Span;
import gueei.binding.observables.StringObservable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lectek.android.LYReader.R;
import com.lectek.android.LYReader.pay.PayUtil;
import com.lectek.android.LYReader.pay.PayUtil.OnPayCallback;
import com.lectek.android.binding.command.OnClickCommand;
import com.lectek.android.binding.command.OnItemClickCommand;
import com.lectek.android.lereader.account.AccountManager;
import com.lectek.android.lereader.analysis.MobclickAgentUtil;
import com.lectek.android.lereader.animation.OpenBookAnimManagement;
import com.lectek.android.lereader.binding.model.BaseLoadNetDataViewModel;
import com.lectek.android.lereader.binding.model.INetAsyncTask;
import com.lectek.android.lereader.binding.model.collect.CollectAddModel;
import com.lectek.android.lereader.binding.model.collect.CollectDelModel;
import com.lectek.android.lereader.binding.model.collect.CollectQueryModel;
import com.lectek.android.lereader.binding.model.contentinfo.BookCommentLatestViewModel.CommentItem;
import com.lectek.android.lereader.download.DownloadHttpEngine;
import com.lectek.android.lereader.lib.net.ResponseResultCode;
import com.lectek.android.lereader.lib.net.exception.GsonResultException;
import com.lectek.android.lereader.lib.recharge.IDealPayRunnable;
import com.lectek.android.lereader.lib.recharge.IPayHandler;
import com.lectek.android.lereader.lib.recharge.OrderInfo;
import com.lectek.android.lereader.lib.recharge.PayConst;
import com.lectek.android.lereader.lib.utils.ApnUtil;
import com.lectek.android.lereader.lib.utils.DateUtil;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.net.exception.ResultCodeControl;
import com.lectek.android.lereader.net.response.BookCommentInfo;
import com.lectek.android.lereader.net.response.BookTagInfo;
import com.lectek.android.lereader.net.response.CollectAddResultInfo;
import com.lectek.android.lereader.net.response.CollectDeleteResultInfo;
import com.lectek.android.lereader.net.response.CollectQueryResultInfo;
import com.lectek.android.lereader.net.response.ContentInfoLeyue;
import com.lectek.android.lereader.net.response.DownloadInfo;
import com.lectek.android.lereader.net.response.ScoreUploadResponseInfo;
import com.lectek.android.lereader.net.response.tianyi.ContentInfo;
import com.lectek.android.lereader.permanent.AppBroadcast;
import com.lectek.android.lereader.permanent.DownloadAPI;
import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.presenter.DownloadPresenterLeyue;
import com.lectek.android.lereader.share.util.UmengShareUtils;
import com.lectek.android.lereader.storage.dbase.TianYiUserInfo;
import com.lectek.android.lereader.storage.dbase.UserScoreInfo;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.IBaseUserAction;
import com.lectek.android.lereader.ui.INetLoadView;
import com.lectek.android.lereader.ui.basereader_leyue.BaseReaderActivityLeyue;
import com.lectek.android.lereader.ui.basereader_leyue.Book;
import com.lectek.android.lereader.ui.common.BaseActivity;
import com.lectek.android.lereader.ui.login_leyue.UserLoginLeYueNewActivity;
import com.lectek.android.lereader.ui.model.dataDefine.BookDetailData;
import com.lectek.android.lereader.ui.specific.ActivityChannels;
import com.lectek.android.lereader.ui.specific.BookCatalogListActivity;
import com.lectek.android.lereader.ui.specific.BookCommentActivity;
import com.lectek.android.lereader.ui.specific.BookCommentActivity1;
import com.lectek.android.lereader.ui.specific.BookCommentDetailActivity;
import com.lectek.android.lereader.ui.specific.ContentInfoActivityLeyue;
import com.lectek.android.lereader.ui.specific.PublishCommentActivity;
import com.lectek.android.lereader.ui.specific.ScoreExchangeBookActivity;
import com.lectek.android.lereader.utils.CommonUtil;
import com.lectek.android.lereader.utils.Constants;
import com.lectek.android.lereader.utils.PriceUtil;
import com.lectek.android.lereader.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * @description
 * 
 * @author chendt
 * @date 2013-9-28
 * @Version 1.0
 * @SEE ContentInfoActivityLeyue
 * @SEE ContentInfoModelLeyue
 * @SEE OrderInfoModelLeyue
 */
public class ContentInfoViewModelLeyue extends BaseLoadNetDataViewModel
		implements INetAsyncTask {

	/** 限免 */
	public static final int LIMIT_TYPE_FREE = 1;
	/** 限价 */
	public static final int LIMIT_TYPE_PRICE = 2;

	private final String EVENT_ID = "contentInfo";
	private final String EVENT_TRY_READ_ID = "TryReadButton";
	private final String EVENT_BUY_BOOK_ID = "BuyBookButton";
	private final String VALUE_SHOW_BUY_POINT_COMFIRM = "点击确认";
	private final String VALUE_SHOW_BUY_POINT_CANCEL = "点击取消";

	private static final int DEFAULT_COMMENT_COUNT = 3;

	private boolean needDownloadFullVersion;

	private static final int MSG_WHAT_ON_PROGRESS_CHANGE = 0;
	private static final int MSG_WHAT_ON_STATE_CHANGE = 1;

	private static final String FOR_FREE_TAG = "0";// 免费
	private static final int ORDER_TYPE_FREE = 0;// 书籍免费
	private static final int ORDER_TYPE_BOOK = 1;// 按本收费
	private static final String NOT_ORDER = "0";// 未购买
	private static final int ORDER_PURCHASE_TYPE_ALIPAY = 2;// 支付宝支付方式
	private static final int ORDER_PURCHASE_TYPE_WOPAY = 9;// 沃支付支付方式
	private String mRealPrice;// 实际购买价格
	private ContentInfoUserAciton mUserAciton;
	private ContentInfoModelLeyue mContentModel;// 书籍详情信息
	// private ContentInfoModel mSurfingReaderContentInfoModel;//书籍具体内容
	private ContentInfoCoverModel mContentInfoCoverModel;// 封面
	private OrderInfoModelLeyue mOrderModel;// 订单详情
	private CollectDelModel mCollectDelModel;
	private CollectAddModel mCollectAddModel;
	private CollectQueryModel mCollectQueryModel;
	private ContentInfoLeyue mContentInfo;
	private ContentInfo mSurfingReaderContentInfo;
	private OrderInfo mOrderInfo;
	private String mBookId, mUserId;
	private boolean isFreeRead, isOrder;
	private boolean isDownloading;
	private boolean isFirstEnter;// 第一次进入界面
	private BaseActivity mActivity;
	private DownloadInfo mDownloadInfo;
	private boolean isFreeExchangeBookClick;
	private UserActionListener mUserActionListener;
	public final StringObservable bCoverUrl = new StringObservable();
	public final StringObservable bBookName = new StringObservable();
	public final StringObservable bAuthorName = new StringObservable();
	public final StringObservable bBuy = new StringObservable();
	public final StringObservable bReadBtn = new StringObservable();
	public final StringObservable bDesc = new StringObservable();
	public final StringObservable bPrice = new StringObservable();// 原价
	public final StringObservable bSize = new StringObservable();// 
	public ArrayListObservable<Span> bPriceSpan = new ArrayListObservable<SpanObservable.Span>(
			Span.class);
	public final StringObservable bRechargeText = new StringObservable(
			getContext().getString(R.string.btn_text_free_exchange_book));
	public final StringObservable bRechargeUnderlineText = new StringObservable(
			getContext().getString(R.string.text_free_exchange_book));
	public ArrayListObservable<Span> bRechargeListSpan = new ArrayListObservable<SpanObservable.Span>(
			Span.class);
	public final ArrayListObservable<CommentItem> bItems = new ArrayListObservable<CommentItem>(
			CommentItem.class);
	public final StringObservable bDiscount = new StringObservable();// 折扣
	public final FloatObservable bBookRating = new FloatObservable();// RatingBar
	public final BooleanObservable bBookRatingVisibility = new BooleanObservable();
	public final StringObservable bDesc4Line = new StringObservable();
	public final BooleanObservable bDesAllLayout = new BooleanObservable(true);
	public final BooleanObservable bShowDescTv = new BooleanObservable();
	public final IntegerObservable bContentMaxLines = new IntegerObservable(4);
	public final StringObservable bProgressText = new StringObservable("0%");
	public final BooleanObservable bNoCommentVisibility = new BooleanObservable(
			false);
	public final BooleanObservable bCommentContentVisibility = new BooleanObservable(
			!bNoCommentVisibility.get());
	public final BooleanObservable bDiscountViewVisible = new BooleanObservable(
			true);
	public final BooleanObservable bPriceViewVisible = new BooleanObservable(
			true);
	public final BooleanObservable bBuyBtnVisibility = new BooleanObservable(
			true);
	public final BooleanObservable bLimitFreeVisibility = new BooleanObservable(
			false);
	public final BooleanObservable bLimitPriceVisibility = new BooleanObservable(
			false);
	public final IntegerObservable bReadBtnBackground = new IntegerObservable(
			R.drawable.btn_content_info_read);
	public final IntegerObservable bReadBtnTextColor = new IntegerObservable(
			R.color.black);
	public final BooleanObservable bReadBtnVisible = new BooleanObservable();
	public final StringObservable bOnlineReadBtn = new StringObservable();
	public final BooleanObservable bOnlineReadVisibility = new BooleanObservable();
	public final BooleanObservable bDownloadViewVisibility = new BooleanObservable();
	public final BooleanObservable bFreeExchangeBookVisibility = new BooleanObservable(
			false);
	public final BooleanObservable bLayoutVisible = new BooleanObservable(false);
	public final BooleanObservable bBuyBtnEnabled = new BooleanObservable(true);
	public final BooleanObservable bRecommendedRelatedTagVisible = new BooleanObservable(
			true);
	public final BooleanObservable bTyydVisibility = new BooleanObservable(
			false);
	public final StringObservable bCommentEditText = new StringObservable();
	public final ArrayListObservable<ItemRecommendBook> bRecommendItems = new ArrayListObservable<ItemRecommendBook>(
			ItemRecommendBook.class);
	public final IntegerObservable bCollectBg = new IntegerObservable(
			R.drawable.bookinfo_collect_normal);
	public final BooleanObservable bSrFromVisibility = new BooleanObservable(
			false);// 天翼阅读出版社字段是否可见
	public final StringObservable bSrFromText = new StringObservable();// 天翼阅读出版社字段
	public final StringObservable bPublishCompany = new StringObservable();// 出版社
	public final StringObservable bPublishTime = new StringObservable();// 出版时间
	public final StringObservable bMoreCommend = new StringObservable();
	public final BooleanObservable bBookDetailVisible = new BooleanObservable();
	public final BooleanObservable bPublishCompanyVisible = new BooleanObservable();
	public final BooleanObservable bPublishTimeVisible = new BooleanObservable();
	public final BooleanObservable bBookTipVisiblity = new BooleanObservable();
	
	private DonwloadListener mDonwloadListener;
	private ScoreUploadModel mUploadModel;
	private AddBookCommentModel mAddBookCommentModel;// 添加评论
//	private PayModel mPayModel;
	private boolean needOpenBook;
	private int mPurchaseType = 0;
	private boolean isCommented;
	private boolean isLimitFree;// 判断是否是限免下载的
	private boolean mIsSurfingReader;// 是否是天翼阅读书籍
	private Integer mCollectId;
	private boolean mIsCollect;
	private LinearLayout ll_container;

	/**
	 * 加载完contentinfo后是否处理购买，（阅读界面点击购买）
	 */
	private boolean onPostBuy = false;

	private IPayHandler mPayHandler;
	
	public void setOnPostBuy(boolean onPostBuy) {
		this.onPostBuy = onPostBuy;
	}

	public final SpanListCreatorCommand onCreateRechargeSpanList = new SpanListCreatorCommand() {

		@Override
		public List<Span> onCreateSpanList(int arg0) {
			ArrayList<Span> list = new ArrayList<Span>();
			Span s;
			s = new Span(new UnderlineSpan());
			list.add(s);
			return list;
		}
	};

	public ContentInfoViewModelLeyue(Context context, INetLoadView loadView,
			ContentInfoUserAciton mUserAciton) {
		super(context, loadView);
		mActivity = (BaseActivity) context;
		this.mUserAciton = mUserAciton;
		mContentModel = new ContentInfoModelLeyue();
		mContentModel.addCallBack(this);
		// mSurfingReaderContentInfoModel = new ContentInfoModel();
		// mSurfingReaderContentInfoModel.addCallBack(this);
		mContentInfoCoverModel = new ContentInfoCoverModel();
		mContentInfoCoverModel.addCallBack(this);
		mOrderModel = new OrderInfoModelLeyue();
		mOrderModel.addCallBack(this);
		mUploadModel = new ScoreUploadModel();
		mUploadModel.addCallBack(this);
		mCollectDelModel = new CollectDelModel();
		mCollectDelModel.addCallBack(this);
		mCollectAddModel = new CollectAddModel();
		mCollectAddModel.addCallBack(this);
		mCollectQueryModel = new CollectQueryModel();
		mCollectQueryModel.addCallBack(this);
		mAddBookCommentModel = new AddBookCommentModel();
		mAddBookCommentModel.addCallBack(this);
//		mPayModel = new PayModel();
//		mPayModel.addCallBack(this);
		// 注册购买成功通知
		mActivity.registerReceiver(mBroadcastReceiver, new IntentFilter(
				AppBroadcast.ACTION_BUY_SUCCEED));
		mActivity.registerReceiver(mBroadcastReceiver, new IntentFilter(
				AppBroadcast.ACTION_BUY_FAIL));
		IntentFilter filter = new IntentFilter();
		filter.addAction(DownloadAPI.ACTION_ON_DOWNLOAD_PROGRESS_CHANGE);
		filter.addAction(DownloadAPI.ACTION_ON_DOWNLOAD_STATE_CHANGE);
		if (mDonwloadListener == null) {
			mDonwloadListener = new DonwloadListener();
			mActivity.registerReceiver(mDonwloadListener, filter);
		}
	}

	public ContentInfoViewModelLeyue(Context context, INetLoadView loadView,
			ContentInfoUserAciton mUserAciton, LinearLayout tag_container) {
		this(context, loadView, mUserAciton);
		this.ll_container = tag_container;
	}

	public OnClickCommand bReadClick = new OnClickCommand() {

		@Override
		public void onClick(View v) {
			MobclickAgentUtil
					.uploadUmengMsg(
							EVENT_TRY_READ_ID,
							"book_name",
							mContentInfo.getBookName(),
							"book_type",
							mContentInfo.getBookType(),
							"book_price",
							!TextUtils.isEmpty(mContentInfo.getPromotionPrice()) ? mContentInfo
									.getPromotionPrice()
									: !TextUtils.isEmpty(mContentInfo
											.getPrice()) ? mContentInfo
											.getPrice() : "0");
			performReadBtnClick();
		}
	};

	// 在线阅读
	public OnClickCommand bOnlineReadClick = new OnClickCommand() {
		@Override
		public void onClick(View v) {
			if (mIsSurfingReader) {
				readCEBBook();
			} else {
				readBook(true);
			}
		}
	};

	public OnClickCommand bBuyClick = new OnClickCommand() {

		@Override
		public void onClick(View v) {
			onPostDealBuy();
		}
	};

	/**
	 * 积分兑换书籍
	 */
	public OnClickCommand bFreeExchangeBook = new OnClickCommand() {

		@Override
		public void onClick(View v) {
//			boolean isUserLoginSuccess = PreferencesUtil.getInstance(mActivity).getIsLogin();
//			
//			if (!AccountManager.getInstance().isLogin()) { // 联网且游客身份，必须先登陆
//				mUserAciton.gotoLoginAcitity(new Runnable() {
//
//					@Override
//					public void run() {
////						onLoginSuccessCheckInfo();
//						
//					}
//				});
//				isFreeExchangeBookClick = true;
//				return;
//			}
//			ScoreExchangeBookActivity.openActivity(mActivity, mContentInfo);
			doRewardPointPay();
		}
	};

	/**
	 * 执行积分兑换书籍
	 */
	private void doRewardPointPay() {
		if (!AccountManager.getInstance().isLogin()) { // 联网且游客身份，必须先登陆
			mUserAciton.gotoLoginAcitity(new Runnable() {
				@Override
				public void run() {
					doRewardPointPay();
				}
			});
		}else {
			ScoreExchangeBookActivity.openActivity(mActivity, mContentInfo);
		}
	}
	
	// 目录
	public final Command bAllCatalogClick = new Command() {

		@Override
		public void Invoke(View v, Object... arg1) {
			if (mIsSurfingReader) {
				gotoSurfingReaderCatalogList();
			} else {
				gotoLeReaderCatalogList();
			}
		}
	};

	private void gotoSurfingReaderCatalogList() {

		boolean hadOrder = false;
		if (isFreeRead == false && isOrder == true) {
			hadOrder = true;
		}

		Book book = getCEBBook();
		BookCatalogListActivity.openActivity(getContext(), mBookId, true, book,
				hadOrder);
	}

	/**
	 * 进入乐阅目录列表
	 */
	private void gotoLeReaderCatalogList() {

		String key = null;
		getDownloadInfo();
		if (mDownloadInfo != null) {
			key = mDownloadInfo.getDecryptSecret_key();
		}

		boolean hadOrder = false;
		if (isFreeRead == false && isOrder == true) {
			hadOrder = true;
		}

		// 本地阅读
		if (!TextUtils.isEmpty(key)) {
			BookCatalogListActivity.openActivity(getContext(), mBookId, key,
					getLocalBookInfo(), hadOrder);
		} else {
			// 在线阅读

			if (mContentInfo == null)
				return;

			Book book = new Book();
			book.setPath(mContentInfo.getFilePath());
			book.setOnline(true);
			book.setBookId(mContentInfo.getBookId());
			book.setBookFormatType(DownloadHttpEngine.VALUE_DOWNLOAD_TYPE_BOOK);
			book.setBookType(mContentInfo.getBookType());
			book.setFeeStart(mContentInfo.getFeeStart());
			// 书籍阅读界面，将根据isOrder字段来判断需要排版的章节数，所以免费书籍也要在进入阅读前，设置为isOrder。
			if (FOR_FREE_TAG.equals(mContentInfo.getIsFee())) {
				book.setOrder(true);
			} else {
				book.setOrder(mContentInfo.isOrder());
			}
			book.setPrice(mContentInfo.getPrice());
			book.setPromotionPrice(mContentInfo.getPromotionPrice());
			book.setAuthor(mContentInfo.getAuthor());
			book.setBookName(mContentInfo.getBookName());
			book.setCoverPath(mContentInfo.getCoverPath());
			BookCatalogListActivity.openActivity(getContext(), mBookId, key,
					book, hadOrder);
		}
	}

	// 收藏
	public OnClickCommand bGoCollectClick = new OnClickCommand() {
		@Override
		public void onClick(View v) {
//			boolean isLogin = PreferencesUtil.getInstance(getContext())
//					.getIsLogin();
//			if (isLogin) {
			if(AccountManager.getInstance().isLogin()) {
				if (mIsCollect) {
					cancelCollect(mCollectId);
				} else {
					
					if (mIsSurfingReader) {
						addCollect(leBookId);
					} else {
						addCollect(mBookId);
					}
				}
			} else {
				Intent intent = new Intent(getContext(),UserLoginLeYueNewActivity.class);
				((Activity) getContext()).startActivityForResult(intent,ContentInfoActivityLeyue.COLLECTREQUESTCODE);
			}
		}
	};

	// 分享
	public OnClickCommand bGoShareClick = new OnClickCommand() {
		@Override
		public void onClick(View v) {
			mUserActionListener.share(v);
		}
	};

	public OnClickCommand bGotoCommentClick = new OnClickCommand() {

		@Override
		public void onClick(View v) {
			showCommentActivity();
		}

	};
	public void showCommentActivity() {//评论Activity
		PublishCommentActivity.openActivity(mActivity, mBookId, leBookId,mIsSurfingReader);
	}

	/**
	 * 取消收藏
	 */
	private void cancelCollect(Integer collectionId) {
		String userId = PreferencesUtil.getInstance(getContext()).getUserId();
		if (!TextUtils.isEmpty(userId) && collectionId != null) {
			mCollectDelModel.start(collectionId, userId);
		}
	}

	/**
	 * 添加收藏
	 */
	private void addCollect(String bookId) {
		String userId = PreferencesUtil.getInstance(getContext()).getUserId();
		if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(mBookId)) {
			mCollectAddModel.start(userId, bookId);
		}
	}

	/**
	 * 获取本地书籍信息
	 * 
	 * @return
	 */
	private Book getLocalBookInfo() {
		if (mDownloadInfo != null) {
			Book book = new Book();
			book.setPath(mDownloadInfo.filePathLocation);
			book.setBookId(mContentInfo.getBookId());
			book.setBookType(mContentInfo.getBookType());
			book.setFeeStart(mContentInfo.getFeeStart());
			// 书籍阅读界面，将根据isOrder字段来判断需要排版的章节数，所以免费书籍也要在进入阅读前，设置为isOrder。
			if (FOR_FREE_TAG.equals(mContentInfo.getIsFee())) {
				book.setOrder(true);
			} else {
				book.setOrder(mContentInfo.isOrder());
			}
			book.setPrice(mContentInfo.getPrice());
			book.setPromotionPrice(mContentInfo.getPromotionPrice());
			book.setAuthor(mContentInfo.getAuthor());
			book.setBookName(mContentInfo.getBookName());
			book.setCoverPath(mContentInfo.getCoverPath());
			return book;
		}
		return null;
	}

	private void getDownloadInfo() {
		if (mContentInfo == null)
			return;

		DownloadInfo downloadInfo = DownloadPresenterLeyue
				.getDownloadUnitById(mContentInfo.getBookId());
		if (downloadInfo != null && downloadInfo.isOrder != isOrder) {// 已下载书籍与当前用户状态不符，需重新下载
			mDownloadInfo = null;
			DownloadPresenterLeyue.deleteDB(downloadInfo);
		} else {
			if (downloadInfo != null
					&& downloadInfo.state == DownloadAPI.STATE_FINISH) {
				mDownloadInfo = downloadInfo;
				if (!CommonUtil.isBookExist(mContentInfo.getBookId())) {
					// 本地书籍不存在，删除数据库记录。重新下载
					DownloadPresenterLeyue.deleteDB(mDownloadInfo);
				}
			}
		}
	}

	/** 构建下载信息实体 */
//	private static DownloadInfo getDownloadInfo(ContentInfoLeyue contentInfo,
//			String key) {
//		DownloadInfo downloadInfo = new DownloadInfo();
//		downloadInfo.contentID = contentInfo.getBookId();
//		downloadInfo.contentName = contentInfo.getBookName();
//		downloadInfo.authorName = contentInfo.getAuthor();
//		downloadInfo.contentType = contentInfo.getBookType();
//		downloadInfo.logoUrl = contentInfo.getCoverPath();
//		downloadInfo.url = contentInfo.getFilePath();
//		downloadInfo.isOrder = contentInfo.isOrder();// 购买；未购买
//		downloadInfo.isOrderChapterNum = contentInfo.getFeeStart();// 购买点
//		downloadInfo.price = contentInfo.getPrice();// 原价
//		downloadInfo.promotionPrice = contentInfo.getPromotionPrice();// 优惠价
//		downloadInfo.secret_key = key;
//		// downloadInfo.filePathLocation = DownloadConstants.BOOKS_DOWNLOAD +
//		// downloadInfo.contentID+".epub";
//		downloadInfo.state = 4;
//		// downloadInfo.timestamp = System.currentTimeMillis();
//		return downloadInfo;
//	}

	private Dialog mCommentDialog;

	// 评论对话框
//	private void showCommentDialog() {
//		if (mCommentDialog == null) {
//			View layout = LayoutInflater.from(getContext()).inflate(
//					R.layout.comment_dialog_layout, null);
//			mCommentDialog = DialogUtil.commonViewDialog(getContext(), layout);
//			final EditText commentContentET = (EditText) layout
//					.findViewById(R.id.comment_et);
//			final RatingBar ratingBar = (RatingBar) layout
//					.findViewById(R.id.ratingbar);
//			final TextView ratingTv = (TextView) layout
//					.findViewById(R.id.star_num_tv);
//			ratingTv.setText(getResources().getString(
//					R.string.comment_rating_score, 10));
//			ratingBar
//					.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
//
//						@Override
//						public void onRatingChanged(RatingBar ratingBar,
//								float rating, boolean fromUser) {
//							ratingBar.setRating(rating);
//							ratingTv.setText(getResources().getString(
//									R.string.comment_rating_score, rating * 2));
//						}
//					});
//
//			OnClickListener confirmOnClickListener = new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//
//					String commentContent = commentContentET.getText()
//							.toString();
//					commentContent = commentContent.replace("&", "");
//					commentContent = commentContent.trim();
//
//					if (!TextUtils.isEmpty(commentContent) && !isCommented) {
//
//						boolean isUserLoginSuccess = PreferencesUtil
//								.getInstance(getContext()).getIsLogin();
//						if (!isUserLoginSuccess) { // 联网且游客身份，必须先登陆
//							Intent intent = new Intent(getContext(),
//									UserLoginLeYueNewActivity.class);
//							getContext().startActivity(intent);
//							return;
//						}
//
//						// if (mUserActionListener != null) {
//						// mUserActionListener.hideSoftKeyboard();
//						// }
//
//						if (mIsSurfingReader) {
//							mAddBookCommentModel.start(leBookId,
//									commentContent,
//									(int) (ratingBar.getRating() * 2));
//						} else {
//							mAddBookCommentModel.start(mBookId, commentContent,
//									(int) (ratingBar.getRating() * 2));
//						}
//						mCommentDialog.dismiss();
//					} else if (isCommented) {
//						ToastUtil.showToast(getContext(), getResources()
//								.getString(R.string.result_code_had_commented));
//					} else {
//						ToastUtil.showToast(getContext(), getResources()
//								.getString(R.string.comment_not_null));
//					}
//				}
//
//			};
//
//			OnClickListener cancelOnClickListener = new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					mCommentDialog.dismiss();
//				}
//
//			};
//
//			DialogUtil.dealDialogBtn(mCommentDialog, R.string.btn_text_comment,
//					confirmOnClickListener, R.string.btn_text_cancel,
//					cancelOnClickListener);
//		} else {
//			((EditText) mCommentDialog.findViewById(R.id.comment_et))
//					.setText("");
//			((RatingBar) mCommentDialog.findViewById(R.id.ratingbar))
//					.setRating(10);
//			((TextView) mCommentDialog.findViewById(R.id.star_num_tv))
//					.setText(getResources().getString(
//							R.string.comment_rating_score, 10));
//		}
//		mCommentDialog.show();
//	}

	public OnClickCommand bAddCommentClick = new OnClickCommand() {

		@Override
		public void onClick(View v) {
			BookCommentActivity1.OpenActivity(getContext(), mBookId, true);
		}
	};

	public OnClickCommand bCommentClick = new OnClickCommand() {

		@Override
		public void onClick(View v) {
//			if (mIsSurfingReader) {
				BookCommentActivity.openActivity(getContext(), mBookId,leBookId, mIsSurfingReader);
//			} else {
//				BookCommentActivity.openActivity(getContext(), mBookId, mIsSurfingReader);
//			}
		}
	};

	public OnItemClickCommand bCommentItemClick = new OnItemClickCommand() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			CommentItem item = bItems.get(position);
			BookCommentDetailActivity.openActivity(getContext(),
					item.commentId, item.bUserNameText.get());
		}
	};

	/**
	 * 显示所有简介
	 */
	public OnClickCommand bShowAllDescClick = new OnClickCommand() {
		@Override
		public void onClick(View v) {
			bDesAllLayout.set(false);
			bShowDescTv.set(true);
		}
	};

	/**
	 * 只显示4行简介
	 */
	public OnClickCommand bShow4LineDescClick = new OnClickCommand() {
		@Override
		public void onClick(View v) {
			bDesAllLayout.set(true);
			bShowDescTv.set(false);
			mUserActionListener.getLineCount();
		}
	};

	// 相关书籍推荐的点击事件
	public OnItemClickCommand bGridItemClickedCommand = new OnItemClickCommand() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			ItemRecommendBook book = bRecommendItems.get(position);

			// 天翼阅读书籍
			if (!TextUtils.isEmpty(book.outBookId)) {
				ActivityChannels.gotoLeyueBookDetail(getContext(),
						book.outBookId,
						LeyueConst.EXTRA_BOOLEAN_IS_SURFINGREADER, true,
						LeyueConst.EXTRA_LE_BOOKID, book.bookId);
			} else {
				// 乐阅书籍
				ContentInfoActivityLeyue.openActivity(getContext(), book.bookId);
			}
		}
	};

	public void setNeedDownloadFullVersion(boolean needDownloadFullVersion) {
		this.needDownloadFullVersion = needDownloadFullVersion;
	}

	/**
	 * 处理乐阅购买流程
	 * 
	 * @param purchaseType
	 */
	public void buyBook(int purchaseType) {
		if (isFreeRead) {
			mOrderModel.start(mContentInfo.getBookId(), 0, ORDER_TYPE_FREE,
										mContentInfo.getBookName(), purchaseType);
			
		} else {
			changeStatusUI(false, isOrder, false);
			mPayHandler = PayUtil.dealPay(mActivity, purchaseType);
			
			mActivity.showLoadDialog();
			mPayHandler.execute(new IDealPayRunnable() {
				
				@Override
				public void bindService(Intent arg0, ServiceConnection arg1, int arg2) {
					CommonUtil.getRealActivity(mActivity).bindService(arg0, arg1, arg2);
				}

				@Override
				public Object onGetOrder(int arg0) {
					Object result = null;
					String charge = (mContentInfo.getLimitType() != null && mContentInfo.getLimitType() == LIMIT_TYPE_PRICE && mContentInfo.getLimitPrice() > 0) ? String.valueOf(mContentInfo.getLimitPrice()) 
							:( !TextUtils.isEmpty(mContentInfo.getPromotionPrice()) && Float.valueOf(mContentInfo.getPromotionPrice()) > 0 ?
									mContentInfo.getPromotionPrice() : mContentInfo.getPrice());
					switch(arg0) {
					case PayConst.PAY_TYPE_ALIPAY:
						result = PayUtil.getAliPayOrderInfo(mActivity, mBookId, charge, mContentInfo.getBookName());
						break;
					case PayConst.PAY_TYPE_CHINATELECOM_MESSAGE_PAY:
						result = PayUtil.getCTCMessageOrderInfo(CommonUtil.getRealActivity(mActivity), mBookId, Double.valueOf(charge), 
								mContentInfo.getBookName());
						
					case PayConst.PAY_TYPE_TY_READ_POINT:
//						result = PayUtil.getTYReadPointPayOrderInfo(mActivity);
						break;
					}
					
					return result;
				}

				@Override
				public void onPayComplete(boolean arg0, int arg1, String arg2, Object resultData) {
					if(mPayHandler != null && !mPayHandler.isAbort()) {
						
						if(arg0) {
							CommonUtil.getRealActivity(mActivity).sendBroadcast(new Intent(AppBroadcast.ACTION_BUY_SUCCEED));
							ToastUtil.showToast(CommonUtil.getRealActivity(mActivity),CommonUtil.getRealActivity(mActivity).getResources().getString(R.string.account_book_recharge_successed));
						}else {
							
							if(mPayHandler.getPayType() == PayConst.PAY_TYPE_TY_READ_POINT 
									&& arg1 == ResponseResultCode.NOT_SUFFICIENT_FUNDS) {
								
							}else {
								CommonUtil.getRealActivity(mActivity).sendBroadcast(new Intent(AppBroadcast.ACTION_BUY_FAIL));
								ToastUtil.showToast(CommonUtil.getRealActivity(mActivity),CommonUtil.getRealActivity(mActivity).getResources().getString(R.string.book_recharge_fail));
							}
						}
					}
					mActivity.hideLoadDialog();
					hideLoadView();
					
					mPayHandler.abort();
					mPayHandler = null;
				}

				@Override
				public void onRegisterSmsReceiver(String arg0, BroadcastReceiver arg1) {
					IntentFilter filter = new IntentFilter(arg0);
					CommonUtil.getRealActivity(mActivity).registerReceiver(arg1, filter);
				}

				@Override
				public void onUnregisterSmsReceiver(BroadcastReceiver arg0) {
					CommonUtil.getRealActivity(mActivity).unregisterReceiver(arg0);
				}

				@Override
				public void startActivity(Intent arg0) {
					CommonUtil.getRealActivity(mActivity).startActivity(arg0);
				}

				@Override
				public void unbindService(ServiceConnection arg0) {
					try {
						CommonUtil.getRealActivity(mActivity).unbindService(arg0);
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	@Override
	public boolean onPreLoad(String tag, Object... params) {
		if (mContentModel.getTag().equals(tag) || mOrderModel.getTag().equals(tag)
				// ||mSurfingReaderContentInfoModel.getTag().equals(tag)
//				|| mPayModel.getTag().equals(tag)
				) {
			showLoadView();
			bLayoutVisible.set(false);
		}
		return false;
	}

	@Override
	public boolean onFail(Exception e, String tag, Object... params) {
		if (tag.equals(mUploadModel.getTag())) {
			ToastUtil.showToast(mActivity, R.string.share_record_fail_tip);
			return false;
		} else if (mOrderModel.getTag().equals(tag)) {
			bBuyBtnEnabled.set(true);
			ToastUtil.showToast(mActivity, "购买失败，请稍后再试！");
		}
		if (e instanceof GsonResultException) {
			GsonResultException exception = (GsonResultException) e;
			if (exception.getResponseInfo() != null
					&& !TextUtils.isEmpty(exception.getResponseInfo()
							.getErrorCode())) {
				String toastStr = ResultCodeControl.getResultString(
						getContext(), exception.getResponseInfo()
								.getErrorCode());
				mUserAciton.exceptionHandle(toastStr);
			}
			if (exception.getResponseInfo() != null
					&& ResponseResultCode.STATUS_NO_FIND_BOOK_OFF_LINE
							.equals(exception.getResponseInfo().getErrorCode())) {
				ToastUtil.showLongToast(getContext(), "该书籍已下线");
				mUserAciton.finishActivity();
			}
		}
		bLayoutVisible.set(true);
		hideLoadView();
		return false;
	}

	// 刷新天翼书籍详情
	private void handleTYBookDetail(ContentInfo info) {
		mSurfingReaderContentInfo = info;

		// 查询收藏状态
		queryFavouritesState();

		isFreeRead = ContentInfo.FEE_TYPE_FREE.equals(mSurfingReaderContentInfo
				.getFeeType()) ? true : false;
		isOrder = mSurfingReaderContentInfo.isOrdered();

		if (TextUtils.isEmpty(mSurfingReaderContentInfo.getConverPath())) {
			mContentInfoCoverModel.start(mBookId);
		}
		bCoverUrl.set(mSurfingReaderContentInfo.getConverPath());
		bBookName.set(mSurfingReaderContentInfo.getBookName());
		bAuthorName.set(getContext().getString(R.string.content_info_author,
				mSurfingReaderContentInfo.getAuthorName()));

		bDesc4Line.set(mSurfingReaderContentInfo.getLongDescription());
		bDesc.set(mSurfingReaderContentInfo.getLongDescription());
		if (mUserActionListener != null) {
			mUserActionListener.getLineCount();
		}
		bShowDescTv.set(false);

		if (!TextUtils.isEmpty(mSurfingReaderContentInfo.getPublisher())) {
			bSrFromVisibility.set(true);
			bSrFromText.set(mSurfingReaderContentInfo.getPublisher());
		} else {
			bSrFromVisibility.set(false);
		}

		bSize.set(getContext().getString(
				R.string.book_size,
				caculateBookSize(Long.parseLong(mSurfingReaderContentInfo
						.getFilesize()))));
		bPrice.set(getContext().getString(R.string.price,
				mSurfingReaderContentInfo.getFeePrice()));

		mUserAciton.setPriceNoSpan();
		bDiscountViewVisible.set(false);
		if (isFreeRead) {// 免费
			bPriceSpan = null;
			bPrice.set(getContext().getString(R.string.for_free));
		} else {// 原价
			bPriceSpan = null;
			bBuy.set(getContext().getString(
					R.string.content_info_btn_buy,
					PriceUtil.formatPrice(mSurfingReaderContentInfo
							.getFeePrice())));
		}

		changeSurfingReaderBtnState(mSurfingReaderContentInfo.isOrdered(),
				isFreeRead);

		ContentInfoLeyue contentInfoLeyue = new ContentInfoLeyue();
		contentInfoLeyue
				.setCoverPath(mSurfingReaderContentInfo.getConverPath());
		contentInfoLeyue.setBookName(mSurfingReaderContentInfo.getBookName());
		contentInfoLeyue.setBookId(mSurfingReaderContentInfo.getBookId());
		mUserAciton.loadOver(contentInfoLeyue);

		// mBookCommentListModel.start(leBookId,3);
		// mRecommendedBookModel.start(leBookId);

		// 如果从阅读界面点击购买过来，处理购买
		if (onPostBuy) {
			setOnPostBuy(false);
			onPostDealBuy();
		}
	}

	// 处理书籍评论界面
	private void handleBookComment(ArrayList<BookCommentInfo> commentList) {
		ArrayList<BookCommentInfo> list = commentList;
		if(list != null && list.size() > 0) {
			loadCommentItems(list);
			if (mUserActionListener != null) {
				mUserActionListener.calculateAndSetListViewHeight();
			}
			bCommentContentVisibility.set(true);
			bNoCommentVisibility.set(false);
			bMoreCommend.set(getString(R.string.show_comment_more, list.size()));
		}else {
			bNoCommentVisibility.set(true);
			bCommentContentVisibility.set(false);
		}
	}

	// 处理推荐书籍列表
	private void handleRecommendBooks(ArrayList<ContentInfoLeyue> recommendList) {
		boolean bottomVPVisibility = false;
		ArrayList<ContentInfoLeyue> recommendBooklist = new ArrayList<ContentInfoLeyue>();
		if (recommendList != null && recommendList.size() > 0) {
			recommendBooklist.addAll(recommendList);
			bottomVPVisibility = true;
			if (mUserActionListener != null) {
				mUserActionListener.backToTop();
			}
		}

		mUserActionListener.setBottonViewPager(recommendBooklist,
				bottomVPVisibility);
	}
	
	/**
	 * 处理乐阅书籍信息
	 * @param info
	 */
	private void handleLeyueBookDetail(ContentInfoLeyue info) {
		mContentInfo = info;
		LogUtil.i("yyl", mContentInfo.toString());
		// 设置Rating
		try {
			bBookRating.set(mContentInfo.starLevel.floatValue());
		} catch (Exception e) {
			bBookRatingVisibility.set(false);
		}
		
		//显示图书信息
		String publishDate = null;
		if(info.getPublishDate() != null){
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 
			publishDate = sdf.format(info.getPublishDate());
		}
		
		if(TextUtils.isEmpty(publishDate) && TextUtils.isEmpty(info.getPublisher())){
			bBookDetailVisible.set(false);
		}else{
			bBookDetailVisible.set(true);
			if(!TextUtils.isEmpty(info.getPublisher())){
				bPublishCompanyVisible.set(true);
				bPublishCompany.set(getString(R.string.book_detail_publisher, info.getPublisher()));
			}else{
				bPublishCompanyVisible.set(false);
			}
			
			if(!TextUtils.isEmpty(publishDate)){
				bPublishTimeVisible.set(true);
				bPublishTime.set(getString(R.string.book_detail_publish_date, publishDate));
			}else{
				bPublishTimeVisible.set(false);
			}
		}
		
		doMobclickAgentEventBegin();
		// 查询收藏状态
		queryFavouritesState();
		isFirstEnter = true;
		isFreeRead = FOR_FREE_TAG.equals(mContentInfo.getIsFee()) ? true : false;
		isOrder = mContentInfo.isOrder();
		bCoverUrl.set(mContentInfo.getCoverPath());
		bBookName.set(mContentInfo.getBookName());
		bAuthorName.set(getContext().getString(R.string.content_info_author,
				mContentInfo.getAuthor()));
		bDesc4Line.set(mContentInfo.getIntroduce());
		bDesc.set(mContentInfo.getIntroduce());
		if (mUserActionListener != null) {
			mUserActionListener.getLineCount();
		}
		bShowDescTv.set(false);
		bSize.set(getContext().getString(R.string.book_size,
				caculateBookSize(mContentInfo.getFileSize())));
		bPrice.set(getContext().getString(R.string.price,
				mContentInfo.getPrice()));

		// 限价书籍
		if (mContentInfo.getLimitType() != null
				&& mContentInfo.getLimitType() == LIMIT_TYPE_PRICE
				&& mContentInfo.getLimitPrice() > 0) {

			isFreeRead = false;

			// 购买按钮显示限价价格
			bBuy.set(getContext().getString(R.string.content_info_btn_buy,
					PriceUtil.formatPrice(mContentInfo.getLimitPrice() + "")));

			if (mContentInfo.getPrice() == null) {
				mUserAciton.setPriceNoSpan();
				bPrice.set(getContext().getString(R.string.price,
						mContentInfo.getLimitPrice()));
				bDiscountViewVisible.set(false);
			} else {
				// 显示打折率
				bDiscountViewVisible.set(true);
				// 给价格文字添加中间线
				mUserAciton.setPriceStrikeSpan();
			}
		} else if (!TextUtils.isEmpty(mContentInfo.getPromotionPrice())
				&& Float.valueOf(mContentInfo.getPromotionPrice()) > 0) {
			// 优惠书籍

			// 购买按钮显示优惠价格
			bBuy.set(getContext().getString(R.string.content_info_btn_buy,
					PriceUtil.formatPrice(mContentInfo.getPromotionPrice())));
			bDiscountViewVisible.set(true);
			mUserAciton.setPriceStrikeSpan();
		} else {

			// 隐藏打折率
			bDiscountViewVisible.set(false);
			// 去掉价格文字的中间线
			mUserAciton.setPriceNoSpan();
			if (isFreeRead) {// 免费
				bPriceSpan = null;
				bPrice.set(getContext().getString(R.string.for_free));
			} else {// 原价
				bPriceSpan = null;
				// bPriceViewVisible.set(true);
				// 如果是限免书籍为价格添加中间线
				if (mContentInfo.getLimitType() != null
						&& mContentInfo.getLimitType() == LIMIT_TYPE_FREE) {
					mUserAciton.setPriceStrikeSpan();
				}
				bBuy.set(getContext().getString(R.string.content_info_btn_buy,
						PriceUtil.formatPrice(mContentInfo.getPrice())));
			}
		}

		// 免费书籍或者是限免书籍隐藏积分兑换按钮
		if (isFreeRead
				|| (mContentInfo.getLimitType() != null && mContentInfo
						.getLimitType() == LIMIT_TYPE_FREE)) {
			bFreeExchangeBookVisibility.set(false);
		} else {
			// 如果是付费书籍；为购买显示积分兑换按钮，已购买隐藏积分兑换按钮
			if (!isOrder)
				bFreeExchangeBookVisibility.set(true);
			else
				bFreeExchangeBookVisibility.set(false);
		}
		DownloadInfo download = DownloadPresenterLeyue
				.getDownloadUnitById(mBookId);
		if (download != null) {
			isDownloading = download.state == DownloadAPI.STATE_STARTING;
			mDownloadMap.put(mBookId, download.id);
		}

		// 计算并赋值打折率
		bDiscount.set(getContext().getString(R.string.discount,
				calculateDisCountValue(mContentInfo)));

		if (changeDownloadStatusOnlyLoginSuccess) {
			changeStatusUI(false, isOrder, isFreeRead);
		} else {
			changeStatusUI(isDownloading, isOrder, isFreeRead);
		}

		DownloadPresenterLeyue.updateDownloadinfoBookPrice(
				// 及时更新该本身，本地存储价格信息。
				mContentInfo.getPrice(), mContentInfo.getPromotionPrice(),
				mContentInfo.getFeeStart(), mContentInfo.getBookId(),
				mContentInfo.getFilePath());
		mUserAciton.loadOver(mContentInfo);
		if (needDownloadFullVersion) {
			autoDownloadFullVersionBook();
		}

		// 如果从阅读界面点击购买过来，处理购买
		if (onPostBuy) {
			setOnPostBuy(false);
			onPostDealBuy();
		}
	}

	public boolean handlerOnBackPress() {
		if(mPayHandler != null) {
			mPayHandler.abort();
			mPayHandler = null;
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {

		if (!isCancel && result != null) {
			if (mContentInfoCoverModel.getTag().equals(tag)) {
				String coverPath = (String) result;
				if (mSurfingReaderContentInfo != null
						&& !TextUtils.isEmpty(coverPath)) {
					mSurfingReaderContentInfo.setConverPath(coverPath);
					bCoverUrl.set(mSurfingReaderContentInfo.getConverPath());

					ContentInfoLeyue contentInfoLeyue = new ContentInfoLeyue();
					contentInfoLeyue.setCoverPath(mSurfingReaderContentInfo.getConverPath());
					contentInfoLeyue.setBookName(mSurfingReaderContentInfo
							.getBookName());
					contentInfoLeyue.setBookId(mSurfingReaderContentInfo
							.getBookId());
					mUserAciton.loadOver(contentInfoLeyue);
				}
			} 
//			else if (mPayModel.getTag().equals(tag)) {
//				hideLoadView();
//				// if(mBuyDialog != null)
//				// mBuyDialog.dismiss();
//				PayMonthlyInfo info = (PayMonthlyInfo) result;
//				if (info.type.equals(OrderDialogBuildUtil.EXTRA_NAME_SSO_ORDER)) {
//					if (info.sso2PayAfterResult != null) {
//						switch (info.sso2PayAfterResult.getResultCode()) {
//						case ResponseResultCode.SSO_ORDER_OK:
//							handleForPaySuccess();
//							updatePayInfo();
//							break;
//						case ResponseResultCode.UNSUBCRIBE_FAIL_NOT_OAUTH:
//							// mUserAction.exceptionToast(getContext().getString(R.string.sso_book_fail));
//							break;
//						default:
//							handleForPayFail();
//							break;
//						}
//					} else {
//						switch (info.sso1PayResult.getSurfingCode()) {
//						case ResponseResultCode.UNSUBCRIBE_FAIL_NOT_OAUTH:
//							// mUserAction.exceptionToast(getContext().getString(R.string.sso_book_fail));
//							break;
//						default:
//							handleForPayFail();
//							break;
//						}
//					}
//				} else if (info.type
//						.equals(OrderDialogBuildUtil.EXTRA_NAME_READ_POINT_ORDER)) {
//					if (info.pointPayOrderResult != null) {
//						switch (info.pointPayOrderResult.getCode()) {
//						case ResponseResultCode.ORDER_OK:
//							handleForPaySuccess();
//							updatePayInfo();
//							break;
//						case ResponseResultCode.NOT_SUFFICIENT_FUNDS:
//							// BuyInfo buyInfo = new BuyInfo();
//							// buyInfo.setBuyDialogShow(true);
//							// buyInfo.setMonthPackage(true);
//							// buyInfo.setMultiChapterPay(false);
//							// buyInfo.setPayType(info.type);
//							// buyInfo.setPrice(""+Float.valueOf(mSurfingReaderContentInfo.getReadpointPrice())/100);
//							// buyInfo.setReadPointEnough(false);
//							// buyInfo.setReadPointPrice(mSurfingReaderContentInfo.getReadpointPrice());
//							// ProcessOrderUtil.showRechargeDialog(getContext(),
//							// null,mSurfingReaderContentInfo.getReadpointPrice(),
//							// buyInfo ,AppBroadcast.ACTION_BOOKINFO_BRO);
//							DialogUtil
//									.oneConfirmBtnDialog(
//											(Activity) getContext(),
//											getResources()
//													.getString(
//															R.string.service_agreement_terms_title),
//											getResources()
//													.getString(
//															R.string.not_enough_readpoint_tip),
//											R.string.account_recharge,
//											new DialogUtil.ConfirmListener() {
//
//												@Override
//												public void onClick(View v) {
//													getContext()
//															.startActivity(
//																	new Intent(
//																			getContext(),
//																			AlipayRechargeActivity.class));
//												}
//											});
//							break;
//
//						default:
//							handleForPayFail();
//							break;
//						}
//					} else {
//						handleForPayFail();
//					}
//
//				} else {
//					handleForPayFail();
//				}
//			}

			// 乐阅书籍详情model
			if (mContentModel.getTag().equals(tag)) {
				// 天翼阅读书籍详情model
				if (mIsSurfingReader) {
					handleTYBookDetail(((BookDetailData) result).tyBookInfo);
				} else {
					handleLeyueBookDetail(((BookDetailData) result).bookInfo);
				}
				
				if(((BookDetailData) result).tagInfos != null && ((BookDetailData) result).tagInfos.size() > 0){
					bBookTipVisiblity.set(true);
					mUserActionListener.getTags(((BookDetailData) result).tagInfos);
				}else{
					bBookTipVisiblity.set(false);
				}
				
				handleBookComment(((BookDetailData) result).bookComments);
				handleRecommendBooks(((BookDetailData) result).recommendBooks);
				hideLoadView();
			}
			if (mOrderModel.getTag().equals(tag)) {
				mOrderInfo = (OrderInfo) result;
				hideLoadView();
				if (isLimitFree) {
					isLimitFree = false;
					mContentInfo.setOrder(true);
					isOrder = true;
					startDownload();
				}
			}
			if (mUploadModel.getTag().equals(tag)) {
				ScoreUploadResponseInfo info = (ScoreUploadResponseInfo) result;
				CommonUtil.handleForShareTip(mActivity, info);
			}

			if (mAddBookCommentModel.getTag().equals(tag)) {
				boolean isSuccess = Boolean.parseBoolean(result.toString());
				if (isSuccess) {
					bCommentEditText.set("");
					isCommented = true;
					ToastUtil.showToast(getContext(),
							getResources().getString(R.string.comment_success));
					getContext()
							.sendBroadcast(
									new Intent(
											BookCommentDetailActivity.ACTION_REFREASH_DATA_BROADCAST));
				} else {
					ToastUtil.showToast(getContext(),
							getResources().getString(R.string.comment_fault));
				}
				// hideLoadView();
			}

			// 查询收藏状态
			if (mCollectQueryModel.getTag().equals(tag)) {
				CollectQueryResultInfo infoQueryResultInfo = (CollectQueryResultInfo) result;
				mCollectId = infoQueryResultInfo.getCollectionId();
				if (mCollectId != null) {
					mIsCollect = true;
				} else {
					mIsCollect = false;
				}

				if (mIsCollect) {// 已收藏
					bCollectBg.set(R.drawable.icon_shoucang_yi);
				} else {// 未收藏
					bCollectBg.set(R.drawable.icon_shoucang);
				}
			}

			// 添加收藏
			if (mCollectAddModel.getTag().equals(tag)) {
				CollectAddResultInfo addResultInfo = (CollectAddResultInfo) result;
				mCollectId = addResultInfo.getCollectionId();
				mIsCollect = true;
				bCollectBg.set(R.drawable.icon_shoucang_yi);
				ToastUtil.showToast(mActivity, R.string.dialog_hold_title);
			}

			// 删除收藏
			if (mCollectDelModel.getTag().equals(tag)) {
				CollectDeleteResultInfo isDelete = (CollectDeleteResultInfo) result;
				if (isDelete.isDelete()) {
					mIsCollect = false;
					bCollectBg.set(R.drawable.icon_shoucang);
					ToastUtil.showToast(mActivity,
							R.string.collect_cancel_success);
				}
			}

		} else {
			hideLoadView();
			if (mContentModel.getTag().equals(tag)) {
				showRetryView();
			}
		}
		bLayoutVisible.set(true);
		return false;
	}

//	private void handleForPaySuccess() {
//		// if(mBuyDialog != null)
//		// mBuyDialog.dismiss();
//		ToastUtil.showToast(getContext(), "订购成功");
//		mSurfingReaderContentInfo.setOrdered(true);
//		changeSurfingReaderBtnState(true, false);
//	}
//
//	private void handleForPayFail() {
//		// if(mBuyDialog != null)
//		// mBuyDialog.dismiss();
//		// mUserAction.exceptionToast("订购失败！");
//		ToastUtil.showToast(getContext(), "订购失败！");
//	}
//
//	private void updatePayInfo() {
//		String price = "";
//		if (ApiProcess4TianYi.FEE_PRICE == mPurchaseType) {
//			price = mSurfingReaderContentInfo.getFeePrice();
//		} else {
//			price = mSurfingReaderContentInfo.getReadpointPrice();
//		}
//		if (mPurchaseType != 0) {
//			CommonUtil.recordBuyInfo(leBookId,
//					mSurfingReaderContentInfo.getBookName(), price, 1,
//					mPurchaseType);
//		}
//	}

	/**
	 * 查询收藏状态
	 */
	public void queryFavouritesState() {
		String userId = PreferencesUtil.getInstance(getContext()).getUserId();
		if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(mBookId)) {
			if (!userId.equals(LeyueConst.TOURIST_USER_ID)) {
				if (mIsSurfingReader) {
					mCollectQueryModel.start(userId, leBookId);
				} else {
					mCollectQueryModel.start(userId, mBookId);
				}
			}
		}
	}

	/** 重试时清除容器内容 */
	private void cleanCache() {
		bItems.clear();
		bRecommendItems.clear();
		bReadBtnBackground.set(R.drawable.btn_content_info_read);
		bReadBtnTextColor.set(R.color.black);
		bFreeExchangeBookVisibility.set(false);
		bLimitFreeVisibility.set(false);
		bLimitPriceVisibility.set(false);
		bCollectBg.set(R.drawable.bookinfo_collect_normal);
		mDownloadInfo = null;
		isCommented = false;
	}

	private void loadCommentItems(ArrayList<BookCommentInfo> list) {
		// 根据点赞的次数降序排序
		Collections.sort(list, new Comparator<BookCommentInfo>() {

			@Override
			public int compare(BookCommentInfo lhs, BookCommentInfo rhs) {
				if (lhs.getSupportNum() > rhs.getSupportNum()) {
					return -1;
				}
				if (lhs.getSupportNum() < rhs.getSupportNum()) {
					return 1;
				}
				return 0;
			}
		});
		for (int i = 0; i < list.size() && i < 3; i++) {
			BookCommentInfo info = list.get(i);
			CommentItem item = new CommentItem();
			item.commentId = info.getId();

			// 判断当前评论是否是本机的用户所评论的
			String currentUserId = PreferencesUtil.getInstance(getContext())
					.getUserId();
			// 如果是本机用户的评论，并且当前账号不是有课，则同步以前的本用户评论的用户名为当前的用户
			if (currentUserId.equals(info.getUserId() + "")
					&& !AccountManager.getInstance().isVisitor()) {
				String userNickName = PreferencesUtil.getInstance(getContext())
						.getUserNickName();
				userNickName = userNickName != null ? userNickName.trim()
						: null;
				if (!TextUtils.isEmpty(userNickName)) {
					if (CommonUtil.isMobileNO(userNickName)) {
						userNickName = CommonUtil
								.getEllipsisPhone(userNickName);
					}
					item.bUserNameText.set(userNickName);
				} else {
					if (!TextUtils.isEmpty(info.getUsername())) {
						String name = info.getUsername();
						if (CommonUtil.isMobileNO(name)) {
							name = CommonUtil.getEllipsisPhone(name);
						}
						item.bUserNameText.set(name);
					}
				}
			} else {
				if (!TextUtils.isEmpty(info.getUsername())) {
					String name = info.getUsername();
					if (CommonUtil.isMobileNO(name)) {
						name = CommonUtil.getEllipsisPhone(name);
					}
					// 不是当前用户，或者是游客，则直接显示服务器默认游客名字
					item.bUserNameText.set(name);
				}
			}
			item.bTimeText.set(DateUtil.getFormateTimeString(info
					.getCreateTime()));
			item.bCommentText.set(info.getContent());
			item.bRatingItemValue.set(info.getStarsNum()/2f);
			item.bSupportCountText.set(info.getSupportNum()+"");
			item.bReplyCountText.set(info.getCommentReplyNum()+"");
			item.bUserNameText.set(info.getUsername());
			if(info.supportNum==0){
				item.bZanSrc.set(R.drawable.icon_zan_grey);
			}else{
				item.bZanSrc.set(R.drawable.icon_zan_red);
			}
			bItems.add(item);
		}

	}

	/**
	 * 自动下载完本书籍
	 */
	private void autoDownloadFullVersionBook() {
		performReadBtnClick();
	}

	private void doMobclickAgentEventBegin() {
		if (isFirstEnter)
			return;

		if (mContentInfo != null) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("book_name", mContentInfo.getBookName());
			map.put("book_type", mContentInfo.getBookType());
			map.put("book_price",
					!TextUtils.isEmpty(mContentInfo.getPromotionPrice()) ? mContentInfo
							.getPromotionPrice() : !TextUtils
							.isEmpty(mContentInfo.getPrice()) ? mContentInfo
							.getPrice() : "0");
			MobclickAgent.onKVEventBegin(
					getContext(),
					EVENT_ID
							+ MobclickAgentUtil.findTypePosition(R.array.type,
									mContentInfo.getBookType()), map, "info");
		}
	}

//	private String getCurrentTimeToLocaleString() {
//		Date date = new Date(System.currentTimeMillis());
//		return date.toLocaleString();
//	}

	@Override
	protected boolean hasLoadedData() {
		return mContentInfo != null;
	}

	private void changeSurfingReaderBtnState(boolean isOrdered, boolean isFree) {

		// 天翼阅读都是在线阅读，所以不会出现本地阅读按钮
		bReadBtnVisible.set(false);
		bDownloadViewVisibility.set(false);

		bOnlineReadVisibility.set(true);
		bBuyBtnVisibility.set(false);
		if (isFree) {
			bOnlineReadBtn.set(getContext().getString(
					R.string.btn_text_online_read));
		} else {
			if (!isOrdered) {
				bBuyBtnVisibility.set(true);
				bOnlineReadBtn.set(getContext().getString(
						R.string.btn_text_online_try_read));
			} else {
				bBuyBtnVisibility.set(false);
				bOnlineReadBtn.set(getContext().getString(
						R.string.btn_text_online_read));
			}
		}
	}

	/**
	 * 根据不同场景，对应的UI显示
	 * 
	 * @param isDownloading
	 *            是否下载完成
	 * @param isOrdered
	 *            是否已购买
	 * @param isFree
	 *            是否免费
	 */
	private void changeStatusUI(boolean isDownloading, boolean isOrdered,
			boolean isFree) {
		if (!isDownloading) {
			bReadBtnVisible.set(true);
			bDownloadViewVisibility.set(false);
		} else {
			bReadBtnVisible.set(false);
			bDownloadViewVisibility.set(true);
		}

		if (mContentInfo.getLimitType() != null) {
			int limitType = mContentInfo.getLimitType();
			// 限免
			if (limitType == LIMIT_TYPE_FREE) {
				bLimitFreeVisibility.set(true);
				bLimitPriceVisibility.set(false);
				isFree = true;
			} else if (limitType == LIMIT_TYPE_PRICE) {
				// 限价
				bLimitPriceVisibility.set(true);
				bLimitFreeVisibility.set(false);
			}
		} else {
			bLimitFreeVisibility.set(false);
			bLimitPriceVisibility.set(false);
		}

		// 免费书籍显示“免费阅读”按钮
		if (isFree) {
			bOnlineReadBtn.set(getContext().getString(
					R.string.btn_text_online_read));
			bReadBtn.set(getContext().getString(
					R.string.btn_text_bookinfo_all_free_read));// 下载阅读
			bReadBtnBackground.set(R.drawable.btn_content_info_read);
			bReadBtnTextColor.set(getContext().getResources().getColor(
					R.color.item_head));
			bBuyBtnVisibility.set(false);
		} else {
			// 如果是付费书籍：未购买显示“免费试读”，已购买显示“阅读”
			if (isOrdered) {
				bOnlineReadBtn.set(getContext().getString(
						R.string.btn_text_online_read));
				bReadBtn.set(getContext().getString(
						R.string.content_info_btn_download));// 阅读
				bBuyBtnVisibility.set(false);
				bReadBtnBackground.set(R.drawable.btn_content_info_read);
				bReadBtnTextColor.set(getContext().getResources().getColor(R.color.item_head));
			} else {
				bOnlineReadBtn.set(getContext().getString(
						R.string.btn_text_online_try_read));// 在线试读
				bReadBtn.set(getContext().getString(
						R.string.content_info_btn_try_read));// 免费试读
				bBuyBtnVisibility.set(true);// 显示购买按钮
				bReadBtnVisible.set(false);
			}
		}
		bOnlineReadVisibility.set(true);

		DownloadInfo downloadInfo = DownloadPresenterLeyue
				.getDownloadUnitById(mContentInfo.getBookId());
		if (downloadInfo != null
				&& downloadInfo.state == DownloadAPI.STATE_FINISH) {// 下载完成的情况
			bReadBtn.set(getContext()
					.getString(R.string.btn_text_bookinfo_read));// 免费阅读
			bReadBtnVisible.set(true);
			bDownloadViewVisibility.set(false);
			bOnlineReadVisibility.set(false);
			bBuyBtnVisibility.set(false);
		} 
//		else {
//			bReadBtn.set(getContext().getString(
//					R.string.btn_text_bookinfo_all_free_read));// 下载阅读
//		}
	}

	/** 设置对应的颜色值 */
	public void setSpan(int foregroundColor) {
		Span priceSpan = new Span(new ForegroundColorSpan(foregroundColor));
		if (bPriceSpan != null) {
			bPriceSpan.add(priceSpan);
		}
	}

	/** 计算折扣值 */
	private String calculateDisCountValue(ContentInfoLeyue info) {
		if (info.getPrice() == null || info.getPromotionPrice() == null
				&& info.getLimitType() == null) {
			return null;
		}
		float originalPrice = info.getPrice() != null ? Float.valueOf(info
				.getPrice()) : 0;
		float promitionPrice = info.getPromotionPrice() != null ? Float
				.valueOf(info.getPromotionPrice()) : 0;
		if (info.getLimitType() != null
				&& info.getLimitType() == LIMIT_TYPE_PRICE
				&& info.getLimitPrice() > 0) {
			promitionPrice = info.getLimitPrice().floatValue();
		}
		if (originalPrice == 0) {
			return null;
		}
		float result = (promitionPrice / originalPrice) * 10;
		if (result < 0.1) {
			return "0.1";
		} else if (result > 9.9) {
			return "9.9";
		} else {
			BigDecimal bd = new BigDecimal(result);
			bd = bd.setScale(1, BigDecimal.ROUND_UP);
			return bd.toString();
		}
	}

	@Override
	public boolean isNeedReStart() {
		return !hasLoadedData();
	}

	@Override
	public boolean isStop() {
		return !mContentModel.isStart();
	}

	@Override
	public void start() {
		// 获取天翼阅读书籍信息
		if (mIsSurfingReader) {
			mContentModel.start(true, leBookId, mUserId, DEFAULT_COMMENT_COUNT,
					mBookId);
		} else {
			// 获取乐阅书籍信息
			mContentModel.start(false, mBookId, mUserId, DEFAULT_COMMENT_COUNT);
		}
	}

	public void onStart(String bookId, String userId, boolean isSurfingReader) {
		super.onStart();
		if (mBookId != null && !mBookId.equals(bookId)) {
			if (isDownloading) {
				isDownloading = false;
				// mDownloadInfo = null;
				bProgressText.set("0%");
			}
		}
		mBookId = bookId;
		mUserId = userId;
		mIsSurfingReader = isSurfingReader;

		if (mIsSurfingReader) {
			bTyydVisibility.set(true);
		} else {
			bTyydVisibility.set(false);
		}

		cleanCache();
		tryStartNetTack(this);
	}

	private ContentInfoLeyue fillTempContentInfo(ContentInfoLeyue info) {
		ContentInfoLeyue tempContentInfo = new ContentInfoLeyue();
		tempContentInfo.setAuthor(info.getAuthor());
		tempContentInfo.setBookId(info.getBookId());
		tempContentInfo.setBookName(info.getBookName());
		tempContentInfo.setBookType(info.getBookType());
		tempContentInfo.setCoverPath(info.getCoverPath());
		tempContentInfo.setFeeStart(info.getFeeStart());
		tempContentInfo.setFeeType(info.getFeeType());
		tempContentInfo.setFileName(info.getFileName());
		tempContentInfo.setFilePath(info.getFilePath());
		tempContentInfo.setFileSize(info.getFileSize());
		tempContentInfo.setIntroduce(info.getIntroduce());
		tempContentInfo.setIsFee(info.getIsFee());
		tempContentInfo.setOrder(info.isOrder());
		tempContentInfo.setPrice(info.getPrice());
		tempContentInfo.setPromotionPrice(info.getPromotionPrice());
		tempContentInfo.setTryFileName(info.getTryFileName());
		tempContentInfo.setTryFilePath(info.getTryFilePath());
		tempContentInfo.setTryFileSize(info.getTryFileSize());
		return tempContentInfo;
	}

	private void downLoadContent() {
		ArrayList<ContentInfoLeyue> contentInfos = new ArrayList<ContentInfoLeyue>();
		ContentInfoLeyue tempInfo = fillTempContentInfo(mContentInfo);
		contentInfos.add(tempInfo);
		if (isFreeRead || isOrder) {
			tempInfo.setFilePath(mContentInfo.getFilePath());
		} else {
			tempInfo.setFilePath(mContentInfo.getTryFilePath());
		}
		DownloadPresenterLeyue.bulkDownloadBooks(contentInfos, true,
				new DownloadPresenterLeyue.DatchDownloadsUIRunnadle(
						getContext()) {

					@Override
					public boolean onPreRunInThread() {
						DownloadInfo downloadInfo = DownloadPresenterLeyue
								.getDownloadUnitById(mContentInfo.getBookId());
						if (downloadInfo != null
								&& downloadInfo.isOrder != isOrder) {// 已下载书籍与当前用户状态不符，需重新下载
							mDownloadInfo = null;
							DownloadPresenterLeyue.deleteDB(downloadInfo);
							return super.onPreRunInThread();
						} else {
							if (downloadInfo != null
									&& downloadInfo.state == DownloadAPI.STATE_FINISH) {
								mDownloadInfo = downloadInfo;
								if (CommonUtil.isBookExist(mContentInfo
										.getBookId())) {
									isDownloading = false;
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											performReadBtnClick();
										}
									});
									return true;
								} else {
									// 本地书籍不存在，删除数据库记录。重新下载
									DownloadPresenterLeyue
											.deleteDB(mDownloadInfo);
								}
							}
						}
						return super.onPreRunInThread();
					}

					@Override
					public boolean onPreRun() {
						return super.onPreRun();
					}

					@Override
					public void onPostRun(int successSize, int hasSize,
							int finishedSize, int state) {
						LogUtil.e("---onPostRun---successSize= " + successSize
								+ ",hasSize = " + hasSize + ",finishedSize = "
								+ finishedSize + ",state= " + state);
						if (state < -1) {
							return;
						} else {
							changeStatusUI(isDownloading, isOrder, isFreeRead);
							if (successSize > 0) {// 数据库不存在该记录，successSize++，开始下载

							} else if (hasSize > 0) {// 数据库存在该记录，未下载完成，hasSize++,继续下载这本书
								ToastUtil.showToast(
										getContext(),
										getResources().getString(
												R.string.download_add_has));
							} else if (finishedSize > 0) {// 数据库存在该记录，已下载完成,finishedSize++
								ToastUtil
										.showToast(
												getContext(),
												getResources()
														.getString(
																R.string.download_add_finished));
							}
						}
						super.onPostRun(successSize, hasSize, finishedSize,
								state);
					}
				});
	}

	/** 监听其他界面下载书籍发送的广播 */
	private class DonwloadListener extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (mDownloadInfo == null) {
				if (mContentInfo != null) {
					// 实时插入key
					// DownloadPresenterLeyue.updateDownloadinfoSecretKey(mContentInfo.tempSecretKey,
					// mContentInfo.getBookId());
					mDownloadInfo = DownloadPresenterLeyue.getDownloadUnitById(mContentInfo.getBookId());
					LogUtil.e("----onReceive----");
				}
			}
			/*
			 * if(mDownloadInfo == null || mDownloadInfo.id !=
			 * intent.getLongExtra(DownloadAPI.BROAD_CAST_DATA_KEY_ID, -1)){
			 * return; }
			 */
			if (intent.getAction().equals(DownloadAPI.ACTION_ON_DOWNLOAD_STATE_CHANGE)) {
				Message msg = new Message();
				msg.what = MSG_WHAT_ON_STATE_CHANGE;
				msg.obj = intent;
				mHandler.sendMessage(msg);
			} else if (intent.getAction().equals(
					DownloadAPI.ACTION_ON_DOWNLOAD_PROGRESS_CHANGE)) {
				Message msg = new Message();
				msg.what = MSG_WHAT_ON_PROGRESS_CHANGE;
				msg.obj = intent;
				mHandler.sendMessage(msg);
			}
		}
	}

	private HashMap<String, Long> mDownloadMap = new HashMap<String, Long>();

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			if (what == MSG_WHAT_ON_PROGRESS_CHANGE) {
				Intent intent = (Intent) msg.obj;
				long id = intent.getLongExtra(
						DownloadAPI.BROAD_CAST_DATA_KEY_ID, -1);
				if (mDownloadMap.containsKey(mBookId)) {
					long downId = mDownloadMap.get(mBookId);
					if (id != downId) {
						return;
					}
				} else {
					return;
				}
				long currentSize = intent.getLongExtra(
						DownloadAPI.BROAD_CAST_DATA_KEY_FILE_BYTE_CURRENT_SIZE,
						-1);
				long size = intent.getLongExtra(
						DownloadAPI.BROAD_CAST_DATA_KEY_FILE_BYTE_SIZE, -1);
				int progressAmount = 0;
				if (size == 0) {
					progressAmount = 0;
				} else {
					progressAmount = (int) (currentSize * 100 / size);
				}
				StringBuilder sb = new StringBuilder();
				sb.append(progressAmount);
				sb.append("%");
				isDownloading = true;
				bProgressText.set(sb.toString());
				changeStatusUI(isDownloading, isOrder, isFreeRead);
			} else if (what == MSG_WHAT_ON_STATE_CHANGE) {
				Intent intent = (Intent) msg.obj;
				long id = intent.getLongExtra(
						DownloadAPI.BROAD_CAST_DATA_KEY_ID, -1);
				int state = intent.getIntExtra(
						DownloadAPI.BROAD_CAST_DATA_KEY_STATE, -1);
				if (state == DownloadAPI.STATE_FINISH) {
					if (mDownloadMap.containsKey(mBookId)) {
						long downId = mDownloadMap.get(mBookId);
						if (id != downId) {
							return;
						}
					} else {
						return;
					}
					isDownloading = false;
					changeStatusUI(isDownloading, isOrder, isFreeRead);
					performReadBtnClick();// 下载成功，直接进行阅读操作
					bProgressText.set("0%");
				} else if (state == DownloadAPI.STATE_STARTING) {
					isDownloading = true;
					mDownloadMap.put(mBookId, id);
					bProgressText.set("0%");
					changeStatusUI(isDownloading, isOrder, isFreeRead);
				}
			}
			// else if(what == MSG_WHAT_ON_CHECK_ORDER){
			// //轮询查询订单结果
			// OrderInfo orderInfo = (OrderInfo) msg.obj;
			// if(orderInfo.getPayOrderStatus().equals(OrderInfo.PAY_ORDER_STATUS_SUCCESS)){
			// mActivity.hideLoadDialog();
			// ToastUtil.showToast(mActivity.getApplicationContext(),mActivity.getApplicationContext().getResources().getString(R.string.account_book_recharge_successed));
			// mActivity.sendBroadcast(new
			// Intent(AppBroadcast.ACTION_BUY_SUCCEED));
			// return;
			// }else
			// if(orderInfo.getPayOrderStatus().equals(OrderInfo.PAY_ORDER_STATUS_FAIL)){
			// mActivity.hideLoadDialog();
			// ToastUtil.showToast(mActivity.getApplicationContext(),mActivity.getApplicationContext().getResources().getString(R.string.book_recharge_fail));
			// mActivity.sendBroadcast(new
			// Intent(AppBroadcast.ACTION_BUY_FAIL));
			// return;
			// }
			//
			// if(mCheckOrderNum == MAX_CHECK_ORDER_NUM){
			// //支付超时，失败
			// mActivity.hideLoadDialog();
			// ToastUtil.showToast(mActivity.getApplicationContext(),mActivity.getApplicationContext().getResources().getString(R.string.book_recharge_fail_with_net_error));
			// mActivity.sendBroadcast(new
			// Intent(AppBroadcast.ACTION_BUY_FAIL));
			//
			// }else{
			// Thread thread = new Thread(new Runnable() {
			//
			// @Override
			// public void run() {
			// try {
			// Thread.sleep(PERIOD);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// if(mOrderInfoByIdModel != null) {
			// mOrderInfoByIdModel.start(mOrderId + "");
			// }
			// }
			// });
			// thread.start();
			//
			// }
			// }
		}
	};

	private void onPostDealBuy() {

		if (mIsSurfingReader) {
			TianYiUserInfo userInfo = AccountManager.getInstance().getTYAccountInfo(AccountManager.getInstance().getUserID());

			// 如果没有登录天翼账号，让用户登录天翼账号
			if (userInfo == null) {
				mUserAciton.gotoLoginAcitity(new Runnable() {

					@Override
					public void run() {
						onPostDealBuy();
					}
				});
				return;
			}

		} else if (!AccountManager.getInstance().isLogin()) {
			mUserAciton.gotoLoginAcitity(new Runnable() {

				@Override
				public void run() {
					onPostDealBuy();
				}
			});
			return;
		}
		
		PayUtil.BuyInfo buyInfo = new PayUtil.BuyInfo();
		buyInfo.isSurfingBook = mIsSurfingReader;
		buyInfo.bookName = mIsSurfingReader ? mSurfingReaderContentInfo.getBookName() : mContentInfo.getBookName();
		buyInfo.price = mIsSurfingReader ? mSurfingReaderContentInfo.getFeePrice() : mContentInfo.getPrice();
		buyInfo.discountPrice = mIsSurfingReader ? mSurfingReaderContentInfo.getFeePrice() : mContentInfo.getPromotionPrice();
		
		PayUtil.showBuyDialog(CommonUtil.getRealActivity(mActivity), buyInfo, new OnPayCallback() {
					@Override
					public void onBuyBtnClick(int payType) {
						// 确认购买按钮点击
						if (mActivity != null && !mActivity.isFinishing() && !mIsSurfingReader && mContentInfo != null) {
							MobclickAgentUtil.uploadUmengMsg(
									EVENT_BUY_BOOK_ID,
									"click_state",
									VALUE_SHOW_BUY_POINT_COMFIRM,
									"book_name",
									mContentInfo.getBookName(),
									"book_type",
									mContentInfo.getBookType(),
									"book_price",
									!TextUtils.isEmpty(mContentInfo
											.getPromotionPrice()) ? mContentInfo
											.getPromotionPrice()
											: !TextUtils.isEmpty(mContentInfo
													.getPrice()) ? mContentInfo
													.getPrice() : "0");
							// 屏蔽购买入口。
							bBuyBtnEnabled.set(false);
							mPurchaseType = payType;
							buyBook(payType);
						}
					}
				});
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent == null) {
				return;
			}
			if (AppBroadcast.ACTION_BUY_SUCCEED.equals(intent.getAction())) {
				// 需求 要求 ，书籍在客户端支付 即认为支付成功，可以看书，此处逻辑 变更。
				// 修改书架当前书籍为已订购书籍
				DownloadPresenterLeyue.updateDownloadinfoFullVersionState(true,
						mBookId);
				try {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							buyBookSuccess();
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (AppBroadcast.ACTION_BUY_FAIL.equals(intent.getAction())) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// 恢复购买入口。
						bBuyBtnEnabled.set(true);
					}
				});
			}
		}
	};

	/** 购买书籍成功 */
	public void buyBookSuccess() {
		mContentInfo.setOrder(true);
		isOrder = true;
		changeStatusUI(isDownloading, isOrder, isFreeRead);
		// 恢复购买入口。
		bBuyBtnEnabled.set(true);
		bFreeExchangeBookVisibility.set(false);
	}

	/** 计算将文件大小转化为M.精确小数点2位 */
	private String caculateBookSize(float size) {
		BigDecimal bd = new BigDecimal(size / (1024 * 1024) < 0.01 ? 0.01
				: size / (1024 * 1024));
		bd = bd.setScale(2, BigDecimal.ROUND_DOWN);
		return bd.toString();
	}

	private void readCEBBook() {
		Book book = getCEBBook();
		ImageView bookCover = (ImageView) ((Activity) getContext())
				.findViewById(R.id.book_cover);
		OpenBookAnimManagement.getInstance().setOpenBookAnimVIew(bookCover);
		BaseReaderActivityLeyue.openActivity(mActivity, book, true);
	}

	private Book getCEBBook() {
		Book book = new Book();
		// book.setPath(mDownloadInfo.filePathLocation);
		book.setBookId(leBookId);
		// book.setBookType(mContentInfo.getBookType());
		book.setBookFormatType(DownloadHttpEngine.VALUE_DOWNLOAD_TYPE_CEB);
		// book.setFeeStart(mContentInfo.getFeeStart());
		// 书籍阅读界面，将根据isOrder字段来判断需要排版的章节数，所以免费书籍也要在进入阅读前，设置为isOrder。
		if (ContentInfo.FEE_TYPE_FREE.equals(mSurfingReaderContentInfo
				.getFeeType())) {
			book.setOrder(true);
		} else {
			book.setOrder(mSurfingReaderContentInfo.isOrdered());
		}
		book.setPrice(mSurfingReaderContentInfo.getFeePrice());
		// book.setPromotionPrice(mContentInfo.getPromotionPrice());
		book.setAuthor(mSurfingReaderContentInfo.getAuthorName());
		book.setBookName(mSurfingReaderContentInfo.getBookName());
		book.setCoverPath(mSurfingReaderContentInfo.getConverPath());
		book.setOnline(true);
		return book;
	}

	private void performReadBtnClick() {
		getDownloadInfo();
		if (mDownloadInfo != null) {
			if (isOrder != mDownloadInfo.isOrder
					|| !CommonUtil.isBookExist(mContentInfo.getBookId())) {// 属于重新下载书籍
				startDownload();
			} else {
				if (!CommonUtil.isBookExist(mContentInfo.getBookId())) {
					startDownload();
				} else {
					readBook(false);
				}
			}
		} else {
			startDownload();
		}
	}

	private void readBook(boolean isOnline) {
		Book book = new Book();
		book.setBookId(mContentInfo.getBookId());
		book.setBookType(mContentInfo.getBookType());
		book.setBookFormatType(DownloadHttpEngine.VALUE_DOWNLOAD_TYPE_BOOK);
		book.setFeeStart(mContentInfo.getFeeStart());
		// 书籍阅读界面，将根据isOrder字段来判断需要排版的章节数，所以免费书籍也要在进入阅读前，设置为isOrder。
		if (FOR_FREE_TAG.equals(mContentInfo.getIsFee())) {
			book.setOrder(true);
		} else {
			book.setOrder(mContentInfo.isOrder());
		}
		if (mDownloadInfo != null)
			book.setPath(mDownloadInfo.filePathLocation);
		book.setPrice(mContentInfo.getPrice());
		book.setPromotionPrice(mContentInfo.getPromotionPrice());
		book.setAuthor(mContentInfo.getAuthor());
		book.setBookName(mContentInfo.getBookName());
		book.setCoverPath(mContentInfo.getCoverPath());
		book.setOnline(isOnline);
		if (mContentInfo.getLimitType() != null && mContentInfo.getLimitType() == LIMIT_TYPE_PRICE) {
			book.setLimitPrice(mContentInfo.getLimitPrice() + "");
		}
		ImageView bookCover = (ImageView) ((Activity) getContext()).findViewById(R.id.book_cover);
		OpenBookAnimManagement.getInstance().setOpenBookAnimVIew(bookCover);
		BaseReaderActivityLeyue.openActivity(mActivity, book, true);
		if (LeyueConst.IS_DEBUG) {
			writeBookJsonInfoToSdCard(mContentInfo);
		}
	}

	/** 下载解密key */
	private void startDownload() {
		if (PreferencesUtil.getInstance(getContext()).getWifiDownloadBoolean()
				&& !ApnUtil.isWifiWork((getContext()))) {
			ToastUtil.showToast(getContext(),R.string.account_setting_allow_wifi_download_tip);
			return;
		}

		isDownloading = true;

		if ((mContentInfo.getLimitType() != null && mContentInfo.getLimitType() == LIMIT_TYPE_FREE)
											&& !mContentInfo.isOrder()) {

//			boolean isUserLoginSuccess = PreferencesUtil.getInstance(mActivity).getIsLogin();
			if (!AccountManager.getInstance().isLogin()) { // 联网且游客身份，必须先登陆
				mUserAciton.gotoLoginAcitity(new Runnable() {

					@Override
					public void run() {
						onLoginSuccessCheckInfo();
					}
				});
				return;
			}

			mOrderModel.start(mContentInfo.getBookId(), "0",  PayConst.ORDER_TYPE_BOOK ,mContentInfo.getBookName(), PayConst.PAY_TYPE_ALIPAY);
			isLimitFree = true;
			isDownloading = false;
			return;
		}

		downLoadContent();
	}

	public interface ContentInfoUserAciton extends IBaseUserAction {
		public void gotoLoginAcitity(Runnable loginSuccessRunnable);

		public void setPriceNoSpan();

		public void finishActivity();

		public void loadOver(ContentInfoLeyue info);

		public void setPriceStrikeSpan();
	}

	public void onDestory() {
		super.onRelease();

		if (mContentModel != null) {
			mContentModel.release();
		}

		// if(mSurfingReaderContentInfoModel != null) {
		// mSurfingReaderContentInfoModel.release();
		// }

		if(mPayHandler != null) {
			mPayHandler.abort();
		}
		
		if (mContentInfoCoverModel != null) {
			mContentInfoCoverModel.release();
		}

		// if(mRecommendedBookModel != null) {
		// mRecommendedBookModel.release();
		// }

		if (mOrderModel != null) {
			mOrderModel.release();
		}

		if (mCollectDelModel != null) {
			mCollectDelModel.release();
		}

		if (mCollectAddModel != null) {
			mCollectAddModel.release();
		}

		if (mCollectQueryModel != null) {
			mCollectQueryModel.release();
		}

		if (mBroadcastReceiver != null) {
			mActivity.unregisterReceiver(mBroadcastReceiver);
			mBroadcastReceiver = null;
		}
		if (mDonwloadListener != null) {
			mActivity.unregisterReceiver(mDonwloadListener);
			mDonwloadListener = null;
		}
		if (mContentInfo != null)
			MobclickAgent.onKVEventEnd(
					getContext(),
					EVENT_ID
							+ MobclickAgentUtil.findTypePosition(R.array.type,
									mContentInfo.getBookType()), "info");
	}

	private boolean changeDownloadStatusOnlyLoginSuccess = false;

	public void onLoginSuccessCheckInfo() {
		mUserId = PreferencesUtil.getInstance(getContext()).getUserId();
		onStart(mBookId, mUserId, mIsSurfingReader);
		changeStatusUI(false, isOrder, isFreeRead);
		changeDownloadStatusOnlyLoginSuccess = true;
//		if (isFreeExchangeBookClick) {
//			bFreeExchangeBook.onClick(null);
//		} else {
//			onPostDealBuy();
//		}
		onPostDealBuy();
	}

	// TODO:用于打包单本书时 输出书籍信息到本地。
	private final String book_info_path = Constants.bookStoredDiretory + "/dbs";

	private void writeBookJsonInfoToSdCard(ContentInfoLeyue info) {
		if (FOR_FREE_TAG.equals(mContentInfo.getIsFee())) {
			mContentInfo.setFeeStart("");// 免费书籍以无购买点为判断依据。
		}
		// TODO:以DownloadInfo形式导出。
		String jsonStr = getDownloadInfoByContentInfo(info).toJsonObject()
				.toString();// new
							// Gson().toJson(getDownloadInfoByContentInfo(info),DownloadInfo.class);
		LogUtil.e("---jsonStr--" + jsonStr);
		FileOutputStream fos = null;
		File file = new File(book_info_path);
		if (!file.exists()) {
			file.mkdirs();
		}
		String savePath = book_info_path + "/" + info.getBookName() + ".txt";
		File saveFile = new File(savePath);
		if (saveFile.exists()) {
			saveFile.delete();
		}
		try {
			fos = new FileOutputStream(saveFile);
			fos.write(jsonStr.getBytes());
		} catch (IOException e) {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e1) {
				e.printStackTrace();
			}
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private DownloadInfo getDownloadInfoByContentInfo(ContentInfoLeyue info) {
		DownloadInfo downloadInfo = new DownloadInfo();
		downloadInfo.authorName = info.getAuthor();
		downloadInfo.contentID = info.getBookId();
		downloadInfo.contentName = info.getBookName();
		downloadInfo.contentType = info.getBookType();
		downloadInfo.secret_key = info.tempSecretKey;
		downloadInfo.price = info.getPrice();
		downloadInfo.promotionPrice = info.getPromotionPrice();
		downloadInfo.isOrderChapterNum = info.getFeeStart();
		downloadInfo.logoUrl = info.getCoverPath();
		downloadInfo.size = info.getFileSize();
		downloadInfo.id = 1;
		if (FOR_FREE_TAG.equals(info.getIsFee())) {
			downloadInfo.isOrder = true;
			downloadInfo.url = info.getFilePath();
		} else {
			downloadInfo.isOrder = false;
			downloadInfo.url = info.getTryFilePath();
		}
		downloadInfo.state = 3;
		return downloadInfo;
	}

	public void uploadShareInfo() {
		if (UserScoreInfo.SINA.equals(UmengShareUtils.LAST_SHARE_TYPE)
				|| UserScoreInfo.QQ_FRIEND
						.equals(UmengShareUtils.LAST_SHARE_TYPE)) {
			mUploadModel.start(UmengShareUtils.LAST_SHARE_TYPE,
					UmengShareUtils.LAST_SHARE_SOURCEID);
		}
	}

	private void getRecommendBook(ArrayList<ContentInfoLeyue> list) {
		for (int i = 0; i < list.size() && i < 3; i++) {
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

	private String leBookId;

	/**
	 * 设置天翼阅读书籍对应的乐阅ID，书签笔记同步使用该ID
	 * 
	 * @param leBookId
	 */
	public void setLeBookId(String leBookId) {
		this.leBookId = leBookId;
	}

	public static class ItemRecommendBook {
		public String bookId;
		public String outBookId;
		public StringObservable bRecommendedCoverUrl = new StringObservable();
		public StringObservable bRecommendedBookName = new StringObservable();
		public StringObservable bRecommendedAuthorName = new StringObservable();
	}

	public void setUserActionListener(UserActionListener listener) {
		mUserActionListener = listener;
	}

	public static interface UserActionListener {
		/*
		 * 计算当前LISTVIEW在SCROLLVIEW中的高度
		 */
		public void calculateAndSetListViewHeight();

		/*
		 * 回到页面顶部
		 */
		public void backToTop();

		public void share(View view);

		public void getLineCount();

		public void getTags(ArrayList<BookTagInfo> tags);

		public void setBottonViewPager(ArrayList<ContentInfoLeyue> datas,
				Boolean visibility);

	}

}
