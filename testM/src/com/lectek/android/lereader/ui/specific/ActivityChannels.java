package com.lectek.android.lereader.ui.specific;

import java.io.Serializable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.bookCity.SubjectInfoItem;
import com.lectek.android.lereader.data.Book;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.net.response.NotifyCustomInfo;
import com.lectek.android.lereader.net.response.SubjectResultInfo;
import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.ui.importlocal.ImportLocalActivity;
import com.lectek.android.lereader.ui.pay.AlipayRechargeActivity;
import com.lectek.android.lereader.ui.pay.PointRechargeActivity;
import com.lectek.android.lereader.ui.person.PersonInfoNickNameActivity;
import com.lectek.android.lereader.ui.wifiTransfer.WifiTransferActivity;
/**
 * Activity跳转通道
 * @author lyw
 */
public final class ActivityChannels{
	
	
	/**
	 * 通知栏过来的消息的，相应处理
	 * @param context
	 * @param info
	 * @return
	 */
	public static Intent getNotifyInfoIntent(Context context,NotifyCustomInfo info){
		Intent intent = new Intent();
		switch (info.getMsgType()) {
		case NotifyCustomInfo.TYPE_BOOK:
			intent.setClass(context, ContentInfoActivityLeyue.class);
			intent.putExtra(LeyueConst.GOTO_LEYUE_BOOK_DETAIL_TAG, 
					ContentInfoActivityLeyue.INTENT_FILTER_TAG+info.getMsgArgs());
			break;
		case NotifyCustomInfo.TYPE_SUBJECT:
           if (info.getMsgArgs().contains("url=")) {
                String targetUrl = info.getMsgArgs().substring(info.getMsgArgs().lastIndexOf("url=")+"url=".length());
                intent.setClass(context, ThirdUrlActivity.class);
                intent.putExtra(LeyueConst.GOTO_THIRD_PARTY_URL_TAG, targetUrl);
           }else{
               intent.setClass(context, SubjectDetailActivity.class);
               intent.putExtra(LeyueConst.GOTO_SUBJECT_DETAIL_TAG,
                       LeyueConst.ASSERT_FOLDER_PATH+LeyueConst.BOOK_CITY_SUBJECT_DETAIL_HTML+"?url="+info.getMsgArgs());
               intent.putExtra(SubjectDetailActivity.GE_TUI_SUBJECT_TAG, true);
           }
			break;
		case NotifyCustomInfo.TYPE_MY_INFO:
			intent.setClass(context, MyMessagesActivity.class);
			break;
		default:
			return null;
		}
		return intent;
	}
	
	public static Intent getSplashActivityIntent(Context context){
		return null;
//		Intent intent = new Intent();
//		intent.setAction(Intent.ACTION_MAIN);
//		intent.addCategory(Intent.CATEGORY_LAUNCHER);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//		intent.setComponent(new ComponentName(context, SplashActivity.class));
//		return intent;
	}
	
	public static Class<? extends Activity> getMainActivityClass(){
		return null;
//		return MainActivity.class;
	}
	/**
	 * 跳转主界面的下载界面
	 * @param context
	 */
	public static void gotoMainActivityDownload(Context context){
		context.startActivity(getMainActivityDownloadIntent(context));
	}
	
	/**
	 * 跳转到指定包月专区
	 * @param product
	 * @param context
	 */
//	public static void gotoMonthlyPaymentDetailActivity(Context context,MonthlyProduct product){
//		Intent intent = new Intent(context,MonthlyPaymentDetailActivity.class);
//		intent.putExtra(LeyueConst.GOTO_MONTHLY_PRODUCT_CONTENT_TAG, product);
//		context.startActivity(intent);
//	}
	
	public static void goToContentInfoActivity(Context context,String outBookId,String bookId){
		//天翼阅读书籍
		if(!TextUtils.isEmpty(outBookId)){
			ActivityChannels.gotoLeyueBookDetail(context, outBookId,
					LeyueConst.EXTRA_BOOLEAN_IS_SURFINGREADER, true,
					LeyueConst.EXTRA_LE_BOOKID, bookId
					);
		}else{
			//乐阅书籍
			ContentInfoActivityLeyue.openActivity(context, bookId);
		}
	}
	/**
	 * 进入乐阅 书籍详情
	 * @param context
	 * @param bookId
	 */
	public static void gotoLeyueBookDetail(Context context ,String bookId){
		gotoLeyueBookDetail(context, bookId, null);
	}
	
	/**
	 * 进入乐阅 书籍详情
	 * @param context
	 * @param bookId
	 * @param extraParams
	 */
	public static void gotoLeyueBookDetail(Context context ,String bookId, Object... extraParams){
		Intent intent = new Intent(context,ContentInfoActivityLeyue.class);
		intent.putExtra(LeyueConst.GOTO_LEYUE_BOOK_DETAIL_TAG, bookId);
		
		if(extraParams != null){
			if(extraParams.length > 0 && extraParams.length % 2 == 0){
				for(int i = 0; i< extraParams.length; i = i+2){
					String extra = extraParams[i].toString();
					Object value = extraParams[i + 1];
					if(value instanceof String)
						intent.putExtra(extra, value.toString());
					else if(value instanceof Boolean)
						intent.putExtra(extra, Boolean.valueOf(value.toString()));
					else if(value instanceof Integer)
						intent.putExtra(extra, Integer.parseInt(value.toString()));
					else if(value instanceof Long)
						intent.putExtra(extra, Long.parseLong(value.toString()));
					else if(value instanceof Float)
						intent.putExtra(extra, Float.parseFloat(value.toString()));
					else if(value instanceof Double)
						intent.putExtra(extra, Double.parseDouble(value.toString()));
					else if(value instanceof Serializable)
						intent.putExtra(extra, (Serializable)value);
					else if(value instanceof Parcelable)
						intent.putExtra(extra, (Parcelable)value);
				}
			}
		}
		
		context.startActivity(intent);
	}
	
//	public static void gotoLoginActivity(Context context){
//		context.startActivity(new Intent(context, UserLoginActivityLeYue.class));
//	}
	
	public static void gotoUserInfoActivity(Context context) {
//		if(UserManager.getInstance(context).isGuset()){//如果是游客就跳转到登录界面
//			context.startActivity(new Intent(context, LoginActivity.class));
//		}else{
			context.startActivity(new Intent(context, UserInfoActivity.class));
//		}
	}
	
	public static void gotoSearchListActivity(Context context){
		context.startActivity(new Intent(context, SearchListActivity.class));
	}
	
	public static void gotoPointManageActivity(Context context) {
		context.startActivity(new Intent(context, PointManageActivity.class));
	}
	
	public static void gotoPointRechargeActivity(Context context) {
		context.startActivity(new Intent(context, PointRechargeActivity.class));
	}
	
	public static void gotoAlipayRechargeActivity(Context context) {
		context.startActivity(new Intent(context, AlipayRechargeActivity.class));
	}
	
	public static void gotoMyOrderActivity(Context context) {
		context.startActivity(new Intent(context, MyOrderActivity.class));
	}
	
	public static void gotoPointManagerActivity(Context context) {
		context.startActivity(new Intent(context, PointManageActivity.class));
	}
	
	
	public static void gotoMyInfoActivity(Context context) {
		context.startActivity(new Intent(context, PersonInfoNickNameActivity.class));
	}
	
//	public static void gotoMyMonthProductActivity(Context context) {
//		context.startActivity(new Intent(context, MyMonthProductActivity.class));
//	}
	
	public static void gotoImportLocalActivity(Context context) {
		context.startActivity(new Intent(context, ImportLocalActivity.class));
	}
	
	public static void gotoWifiTransferActivity(Context context) {
		context.startActivity(new Intent(context, WifiTransferActivity.class));
	}
	
//	public static void gotoContentActivity(Context context, ContentInfo info){
//		ContentInfoActivity.openActitiy(context, info);
//	}
	
	/**
	 * 进入关于界面入口
	 * @param context
	 */
	public static void gotoAboutUsActivity(Context context){
		context.startActivity(new Intent(context, AboutUsActivity.class));
	}
	
	/**
	 * 进入用户设置入口
	 * @param context
	 */
	public static void gotoUserSettingActivity(Context context){
		context.startActivity(new Intent(context, UserSettingActivity.class));
	}


    /**
     * 进入关于反馈入口
     * @param context
     */
    public static void gotoFeedbackActivity(Context context){
        context.startActivity(new Intent(context, FeedBackNewActivity.class));
    }

//	public static void readBookActivity(Context context, Book book){
//		BaseReaderActivity.openReader(context, book);
//	}
	
	public static Intent getMainActivityDownloadIntent(Context context){
//		return null;
		Intent intent = new Intent(context, MainActivity.class);
		intent.putExtra(MainActivity.EXTRA_NAME_GO_TO_VIEW, MainActivity.RESULT_CODE_GOTO_DOWNLOAD);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return intent;
	}
	
	public static void gotoMainActivityBookCity(Context context){
		context.startActivity(getMainActivityBookCityIntent(context));
	}
	
	public static Intent getMainActivityBookCityIntent(Context context){
		return null;
//		Intent intent = new Intent(context, MainActivity.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		intent.putExtra(MainActivity.EXTRA_NAME_GO_TO_VIEW, MainActivity.RESULT_CODE_GOTO_BOOK_CITY);
//		return intent;
	}
	
	public static void tryGotoMainActivity(Activity activity){
//		Context context = activity.getApplicationContext();
//		Intent intent = new Intent(context,MainActivity.class);
//		Intent activityIntent = activity.getIntent();
//		if(ApnUtil.isNetAvailable(context) && ClientInfoUtil.isConnect(context)){
//			if(activityIntent != null){
//				intent.putExtra(MainActivity.EXTRA_NAME_LOGIN_TYPE
//						,activityIntent.getIntExtra(MainActivity.EXTRA_NAME_LOGIN_TYPE, MainActivity.LOGIN_TYPE_EXPLICIT));
//			}
//			intent.putExtra(MainActivity.EXTRA_NAME_IS_LOGIN_SUCCESS, ClientInfoUtil.isConnect(context));
//		}else{
//			intent.putExtra(MainActivity.EXTRA_NAME_GO_TO_VIEW,MainActivity.RESULT_CODE_GOTO_DOWNLOAD);
//		}
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		if(activityIntent != null){
//			intent.putExtra(MainActivity.EXTRA_NAME_FROM_VIEW,
//					activityIntent.getIntExtra(MainActivity.EXTRA_NAME_FROM_VIEW, -1));
//		}
//		context.startActivity(intent);
	}
	
	public static void gotoVoiceCommentActivity(Context context, String content_id, String book_name){
//		VoiceCommentActivity.openActivity(context, content_id, book_name);
	}
	
	public static void gotoVoiceBookInfoActivity(Context context, String content_id, String book_name){
		context.startActivity(getVoiceBookInfoActivityIntent(context, content_id, book_name));
	}
	
	public static Intent getVoiceBookInfoActivityIntent(Context context, String content_id, String book_name){
		return null;
//		Intent intent = new Intent(context, VoiceBookInfoActivity.class);
//		intent.putExtra(VoiceBookInfoActivity.EXTRA_NAME_CONTENT_ID, content_id);
//		intent.putExtra(VoiceBookInfoActivity.EXTRA_NAME_CONTENT_NAME, book_name);
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		return intent;
	}
	
	public static void gotoAlsoLikeContentActivity(Context context, String contentID, String name
			, String author, String type, boolean isBuySuccess){
//		Intent intent = new Intent(context, AlsoLikeContentActivity.class);
//		intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		intent.putExtra(AlsoLikeContentActivity.EXTRA_NAME_CONTENT_ID,contentID);
//		intent.putExtra(AlsoLikeContentActivity.EXTRA_NAME_CONTENT_NAME,name);
//		intent.putExtra(AlsoLikeContentActivity.EXTRA_NAME_AUTHOR_NAME,author);
//		intent.putExtra(AlsoLikeContentActivity.EXTRA_NAME_TYPE,type);
//		intent.putExtra(AlsoLikeContentActivity.EXTRA_NAME_IS_BUY,isBuySuccess);
//		context.startActivity(intent);
	}
	
	public static boolean canGift(){
		return false;
//		return GiftActivity.canGift();
	}
	
	public static void gotoGiftActivity(Context context, Book book){
//		GiftActivity.openActivity(context, book);
	}
	
	public static void gotoBookInfoActivity(Context context, String content_id, String book_name){
//		BookInfoActivity.openBookInfoActivity(this_, content_id, book_name);
	}
	
	public static Intent getBookInfoActivityIntent(Context context, String content_id, String book_name){
		return null;
//		Intent intent = new Intent(context,BookInfoActivity.class);
//		intent.putExtra(BookInfoActivity.EXTRA_NAME_CONTENT_ID, content_id);
//		intent.putExtra(BookInfoActivity.EXTRA_NAME_CONTENT_NAME,book_name);
//		return intent;
	}
	
	public static void onReaderActivityFinish(Activity activity,boolean isBuySuccess,boolean isLogin){
//		if(isBuySuccess){
//			activity.setResult(BookInfoActivity.RESULT_CODE_BOOK_BUY_FLASH);
//		}else{
//			if(isLogin){
//				activity.setResult(BookInfoActivity.RESULT_CODE_BOOK_BUY_FLASH);
//			}else{
//				activity.setResult(Activity.RESULT_CANCELED);
//			}
//		}
	}
	
	
	public static void gotoCommentActivity(Context context, String content_id, String book_name){
//		Intent intent = new Intent(context, CommentActivity.class);
//		intent.putExtra(CommentActivity.EXTRA_NAME_CONTENT_ID, content_id);
//		intent.putExtra(CommentActivity.EXTRA_NAME_COMMENT_COUNT,0);
//		intent.putExtra(CommentActivity.EXTRA_NAME_CONTENT_NAME, book_name);
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		context.startActivity(intent);
	}
	
	
	public static void gotoNetHelpActivity(Activity context){
//		NetHelpActivity.openNetHelpActivity(context);
	}
	
	
//	public static void gotoSpecialSubjectActivity(Context context, SpecialSubjectInfo info){
//		SpecialSubjectActivity.openSpecialSubjectActivity(context, info, true);
//	}
	
	public static Intent getSpecialSubjectActivityIntent(Context context,String subject_id,String subject_type,String subject_name,boolean isVoice){
		return null;
//		Intent subjectIntent = new Intent(context, SpecialSubjectActivity.class);
//		subjectIntent.putExtra(SpecialSubjectActivity.EXTRA_NAME_SPECIAL_SUBJECT_ID, subject_id);
//		subjectIntent.putExtra(SpecialSubjectActivity.EXTRA_NAME_SPECIAL_SUBJECT_TYPE, subject_type);
//		subjectIntent.putExtra(SpecialSubjectActivity.EXTRA_NAME_SPECIAL_SUBJECT_NAME, subject_name);
//		subjectIntent.putExtra(SpecialSubjectActivity.EXTRA_NAME_IS_VOICE_READ, isVoice);
//		subjectIntent.putExtra(SpecialSubjectActivity.EXTRA_NAME_IS_SHOW_ALL_SUBJECT, true);
//		return subjectIntent;
	}
	
//	public static void gotoAreaContentActivity(Activity context, CatalogInfo info){
//		AreaContentActivity.openAreaContent(context, info, AreaContentActivity.RECOMMEND_PACK_PRODUCT);
//	}
	
//	public static Intent getAreaContentActivityIntent(Activity context, CatalogInfo info,boolean isVoice){
//		return null;
//		Intent areaIntent = new Intent(context, AreaContentActivity.class);
//		areaIntent.putExtra(AreaContentActivity.EXTRA_CATALOG_INFO, info);
//		areaIntent.putExtra(AreaContentActivity.EXTRA_PACK_TYPE,AreaContentActivity.RECOMMEND_PACK_PRODUCT);
//		if(isVoice){
//			areaIntent.putExtra(AreaContentActivity.EXTRA_CONTENT_TYPE, AreaContentActivity.CONTENT_TYPE_VOICE);
//		}else{
//			areaIntent.putExtra(AreaContentActivity.EXTRA_CONTENT_TYPE, AreaContentActivity.CONTENT_TYPE_BOOK);
//		}
//		return areaIntent;
//	}
	
	public static void gotoWholeStationPkgActivity(Activity context){
//		WholeStationPkgActivity.openWholePackage(context);
	}
	
//	public static void gotoVoiceAreaContentActivity(Activity context, CatalogInfo info){
//		AreaContentActivity.openAreaContent(context, info, AreaContentActivity.RECOMMEND_PACK_PRODUCT, AreaContentActivity.CONTENT_TYPE_VOICE);
//	}
	
	public static void gotoFeedbackActivity(Activity context){
//		Intent intent = new Intent(context, FeedbackActivity.class);
//		context.startActivity(intent);
	}
	
	public static void gotoVoicePlayer(Context context,String contentId){
		
	}

	public static Class<? extends Activity> getVoicePlayerActivity(){
		return null;
	}

	public static Intent getVoicePlayerIntent(Context context,String contentId,int position,boolean IsReadLocal){
		return null;
//		Intent intent = new Intent(context,VoicePlayAudioActivity.class);
//		intent.putExtra(VoicePlayAudioActivity.EXTRA_NAME_CONTENT_ID,contentId);
//		intent.putExtra(VoicePlayAudioActivity.EXTRA_IS_READ_LOCAL,IsReadLocal);
//		intent.putExtra(VoicePlayAudioActivity.AUDIO_LIST_INDEX, position);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//	    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//	    return intent;
	}
	
	public static Intent getResumeBackgroundVoicePlayerIntent(Context context,String contentId){
		return null;
//		Intent intent = new Intent(context,VoicePlayAudioActivity.class);
//		intent.putExtra(VoicePlayAudioActivity.EXTRA_NAME_CONTENT_ID,contentId);
//		intent.putExtra(VoicePlayAudioActivity.EXTRA_IS_RESUME_BACKGROUND,true);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//	    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//	    return intent;
	}
	
	public static Intent getNoticeActivityIntent(Context context){
		return null;
//		Intent newIntent = new Intent(context, MyNoticeActivity.class);
//		return newIntent;
	}

    /**
     * 在书城中嵌入activity，要嵌入的activity必须在书城BookCityActivityGroup登记activityID和Class<?>。
     * 这种限制是必须的
     * @param context
     */
    public static void embedActivityInBookCity(Context context, String activityID,String title, Bundle activityExtra){
        Intent intent = new Intent(context, SlideActivityGroup.class);
        intent.putExtra(SlideActivityGroup.Extra_Switch_BookCity_UI, true);
        if(activityExtra == null){
        	activityExtra = new Bundle();
        }
        if(activityExtra != null) {
        	activityExtra.putBoolean(BookCityActivityGroup.Extra_Embed_Activity_Hide_Title_Bar, true);
        	intent.putExtra(BookCityActivityGroup.Extra_Embed_Activity_Extra, activityExtra);
        }
        intent.putExtra(BookCityActivityGroup.Extra_Embed_Activity_ID, activityID);
        intent.putExtra(BookCityActivityGroup.Extra_Embed_Activity_Title, title);
        context.startActivity(intent);
    }
    
    /**
     * 从书城移除嵌入的activity，要移除的activity必须在书城BookCityActivityGroup登记activityID和Class<?>。
     * 这种限制是必须的
     * @param context
     */
    public static void removeEmbedActivityFromBookCity(Context context, String activityID){
    	Intent intent = new Intent(context, SlideActivityGroup.class);
        intent.putExtra(SlideActivityGroup.Extra_Switch_BookCity_UI, true);
        intent.putExtra(BookCityActivityGroup.Extra_Remove_Embed_Activity, true);
        intent.putExtra(BookCityActivityGroup.Extra_Embed_Activity_ID, activityID);
        context.startActivity(intent);
    }
    
    
    /**
     * 书籍详情界面嵌入书城界面
     */
    public static void embedContentInfoLeyueActivityToBookCity(Context context,String outBookId,String bookId){
    	Bundle bundle = new Bundle();
    	if(!TextUtils.isEmpty(outBookId)){
    		bundle.putString(LeyueConst.GOTO_LEYUE_BOOK_DETAIL_TAG, outBookId);
    		bundle.putBoolean(LeyueConst.EXTRA_BOOLEAN_IS_SURFINGREADER, true);
    		bundle.putString(LeyueConst.EXTRA_LE_BOOKID, bookId);
    	}else{
    		bundle.putString(LeyueConst.GOTO_LEYUE_BOOK_DETAIL_TAG, bookId);
    		bundle.putBoolean("from_notification", false);
    	}
    	embedActivityInBookCity(context, BookCityActivityGroup.TAB_Embed_CONTENTINFO,context.getString(R.string.content_info), bundle);
    }
    
    /**
     * 专题详情界面嵌入书城界面
     */
    public static void embedSubjectDetailActivityToBookCity(Context context,int subjectId){
    	Bundle bundle = new Bundle();
    	bundle.putInt(BookCitySubjectActivity.BOOKCITY_SUBJECT_ID, subjectId);
    	embedActivityInBookCity(context, BookCityActivityGroup.TAB_Embed_SUBJECT_DETAIL,context.getString(R.string.subject_title), bundle);
    }
    
    /**
     * 推荐二级界面嵌入书城界面
     */
    public static void embedBookRecommendActivityToBookCity(Context context,int columnId,String title){
    	Bundle bundle = new Bundle();
    	bundle.putInt(BookCityBookListActivity.BOOKCITY_LIST_ACTIVITY_COLUMN, columnId);
    	embedActivityInBookCity(context, BookCityActivityGroup.TAB_Embed_BOOK_RECOMMEND_LIST,title, bundle);
    }
    
    /**
     * 分类二级界面嵌入书城界面
     */
    public static void embedBookClassifyActivityToBookCity(Context context,int bookType,String title){
    	Bundle bundle = new Bundle();
    	bundle.putInt(BookCityClassifyDetailActivity.BOOKCITY_CLASSIFY_DETAIL_TYPE, bookType);
    	embedActivityInBookCity(context, BookCityActivityGroup.TAB_Embed_CLASSIFY_DETAIL, title ,bundle);
    }
    
    /**
     *  个人中心--我的收藏界面
     * @param context
     */
    public static void gotoCollectBookActivity(Context context){
        Intent intent = new Intent(context,CollectBookActivity.class);
        context.startActivity(intent);
    }
    
    /**
     * 进入专题详情
     */
    public static void goToSubjectDetailActivity(Context context,SubjectResultInfo info){
    	LogUtil.e("专题类型：type = " + info.type);
    	if(info.type == 1){
			//书籍详情
			ContentInfoActivityLeyue.openActivity(context, info.memo);
		}else if(info.type == 2){
			//专题详情
			BookCitySubjectActivity.openActivity(context, info.getSubjectId());
		}else if(info.type == 3){
			//活动详情
			Intent intent = new Intent(context, SubjectDetailActivity.class);
			intent.putExtra(LeyueConst.GOTO_SUBJECT_DETAIL_TAG, info.memo);
			context.startActivity(intent);
		}else if(info.type == 4){
			//包月详情
		}else if(info.type == 5){
			//限免
		}else if(info.type == 6){
			//栏目
		}else if(info.type == 7){
			//包月栏目
		}
    }
    
    /**
     * 进入专题详情
     */
    public static void goToSubjectDetailActivity(Context context,SubjectInfoItem info){
    	LogUtil.e("专题类型：type = " + info.type);
    	if(info.type == 1){
			//书籍详情
//			ContentInfoActivityLeyue.openActivity(context, info.memo);
			embedContentInfoLeyueActivityToBookCity(context, null, info.memo);
		}else if(info.type == 2 || info.type == 0){
			//专题详情
			embedSubjectDetailActivityToBookCity(context, info.subjectId);
//			BookCitySubjectActivity.openActivity(context, info.subjectId,
//					info.subjectTitle, info.subjectContent);
		}else if(info.type == 3){
			//活动详情
			Intent intent = new Intent(context, SubjectDetailActivity.class);
			intent.putExtra(LeyueConst.GOTO_SUBJECT_DETAIL_TAG, info.memo);
			context.startActivity(intent);
		}else if(info.type == 4){
			//包月详情
		}
    }
    
}
