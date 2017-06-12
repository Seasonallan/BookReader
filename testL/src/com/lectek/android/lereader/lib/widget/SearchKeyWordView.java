package com.lectek.android.lereader.lib.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.lectek.android.lereader.lib.utils.IProguardFilter;
import com.lectek.android.lereader.lib.utils.IProguardFilterOnlyPublic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

/**
 * 自定义搜索关键字控件
 * @author shizq
 *
 */
public class SearchKeyWordView extends View {
	
	private ArrayList<? extends BaseKeyWord> mTotalKeywords;
	private ArrayList<? extends BaseKeyWord> mCurKeywords;
	private ArrayList<KeyWordInfo> keyWordInfos = new ArrayList<KeyWordInfo>();
	private HashMap<Integer,Boolean> mLoadedKeywordIndex = new HashMap<Integer,Boolean>();//用于记录寂静用过的热词索引
	private int mCurWitdh;
	private int mCurHeight;
	
	private int keyWordCount = 10;
	
	private int randomCount;//用于计算获取同一个文字位置次数
	
	OnItemClickListener mOnItemClickListener;
	
	private boolean isInit;
	
	private final int POSITION_A = 1;
	private final int POSITION_B = POSITION_A+1;
	private int positionType = POSITION_A;
	
	//为了调整屏幕文字位置分布A
	private int leftPosCount;
	private int leftPosRate;
	private int hMidPosCount;
	private int hMidPosRate;
	private int topPosCount;
	private int topPosRate;
	private int vMidPosCount;
	private int vMidPosRate;
	private int bottomPosCount;
	private int bottomPosRate;
	
	
	//为了调整屏幕文字位置分布B
	int currentWordPosY;//用于计算当前设置文字位置数
	int verticalWordHeight;
	
	
	Random random = new Random();
	
	private float mTextSizes[];
	private int mTextColors[];
	
	public SearchKeyWordView(Context context) {
		super(context);
		init();
	}

	public SearchKeyWordView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public ArrayList<? extends BaseKeyWord> getkeywords(){
		return mCurKeywords;
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mCurWitdh = w;
		mCurHeight = h;
		if(!isInit){
			isInit = true;
			initKeyWordState();
			executeShowAnimation();
		}
		super.onSizeChanged(w, h, oldw, oldh);
	}

	private void init(){
		setClickable(true);
		mTextColors = new int[]{Color.BLACK};
		mTextSizes = new float[]{getDIPToPX(15f),getDIPToPX(17f), getDIPToPX(19f), getDIPToPX(21f), getDIPToPX(23f), getDIPToPX(25f)};
	}
	
	public void setTextColors(int[] colors){
		mTextColors = colors;
	}
	
	
	private void initKeyWordState(){
		if(mCurKeywords == null)
			return;
		
		int total = mCurKeywords.size();
		leftPosRate = (int)Math.ceil((float)total/4);
		hMidPosRate = (int)Math.ceil(total/4);
		topPosRate = (int)Math.ceil((float)total/2);
		vMidPosRate = (int)Math.ceil(total/2);
		bottomPosRate = (int)Math.ceil(total/10);
		
		verticalWordHeight = mCurHeight/keyWordCount;
		
		positionType = POSITION_B;
		for(int i = 0; i < mCurKeywords.size();i++){
			keyWordInfos.add(getNextKeyWordInfo(mCurKeywords.get(i).getKeyWord()));
		}
		
		leftPosCount = 0;
		hMidPosCount = 0;
		topPosCount = 0;
		vMidPosCount = 0;
		bottomPosCount = 0;
		
		currentWordPosY = 0;
	}
	
	private KeyWordInfo getNextKeyWordInfo(String text){
		Paint paint = new Paint();
		paint.setColor(mTextColors[getRandomInt(0, mTextColors.length)]);
		paint.setTextSize(mTextSizes[getRandomInt(0, mTextSizes.length)]);
		paint.setAntiAlias(true);
		Rect rect = new Rect();
		paint.getTextBounds(text, 0, text.length(), rect);
		int kWidth = rect.width();
		int kHeight = rect.height();
		Point StartPoint = getNextStartPos(rect.width(),rect.height());
		rect.top = StartPoint.y;
		rect.left = StartPoint.x;
		rect.right = rect.left+kWidth;
		rect.bottom = rect.top+kHeight;
		return new KeyWordInfo(text, paint, rect);
	}
	
	/**
	 * 随机按规定的比率排版文字位置
	 * @param randomStart
	 * @param randomEnd
	 * @return
	 */
	private Point calculatePositionA(int randomStart, int randomEnd){
		
		int posX = 0;
		int posY = 0;
		
		if(leftPosCount < leftPosRate){
			posX = getRandomInt(0,mCurWitdh/5);
			leftPosCount++;
		}else if(leftPosCount == leftPosRate && hMidPosCount < hMidPosRate){
			posX = getRandomInt(mCurWitdh/2-mCurWitdh/4,mCurWitdh/2+mCurWitdh/4);
			leftPosCount++;
		}else{
			posX = getRandomInt(0,mCurWitdh);
		}
		
		if(hMidPosCount == hMidPosRate && topPosCount < topPosRate){
			posY = getRandomInt(0,mCurHeight/5);
			topPosCount++;
		}else if(topPosCount == topPosRate && vMidPosCount < vMidPosRate){
			posY = getRandomInt(mCurHeight/2-mCurHeight/4,mCurHeight/2+mCurHeight/4);
			vMidPosCount++;
		}else if(vMidPosCount == vMidPosRate && bottomPosCount < bottomPosRate){
			posY = getRandomInt(mCurHeight-mCurHeight/4,mCurHeight);
			bottomPosCount++;
		}else if(randomStart == 0 && randomEnd == 0){
			posY = getRandomInt(0,mCurHeight);
		}else{
			posY = getRandomInt(randomStart,randomEnd);
		}
		
		return new Point(posX, posY);
	}
	
	private Point calculatePositionB(int randomStart, int randomEnd){
		int posX = 0;
		int posY = 0;
		
		if(keyWordInfos.size() >1 && leftPosCount < leftPosRate){
			posX = getRandomInt(0,mCurWitdh/5);
			leftPosCount++;
		}else if(leftPosCount == leftPosRate && hMidPosCount < hMidPosRate){
			posX = getRandomInt(mCurWitdh/2-mCurWitdh/4,mCurWitdh/2+mCurWitdh/4);
			leftPosCount++;
		}else{
			posX = getRandomInt(0,mCurWitdh);
		}
		
		if(currentWordPosY < mCurHeight){
			posY = getRandomInt(currentWordPosY, currentWordPosY+verticalWordHeight);
		}else if(randomStart == 0 && randomEnd == 0){
			posY = getRandomInt(0,mCurHeight);
		}else{
			posY = getRandomInt(randomStart,randomEnd);
		}
		return new Point(posX, posY);
	}
	
	private Point getNextStartPos(int width, int height){
		return getNextStartPos(width,height,0,0);
	}
	
	private Point getNextStartPos(int width, int height,int randomStart, int randomEnd){
		int posX = 0;
		int posY = 0;
		
		if(positionType == POSITION_A){
			Point pos = calculatePositionA(randomStart, randomEnd);
			posX = pos.x;
			posY = pos.y;
		}else if(positionType == POSITION_B){
			Point pos = calculatePositionB(randomStart, randomEnd);
			posX = pos.x;
			posY = pos.y;
		}
		
		if(posX + width > mCurWitdh){
			posX = mCurWitdh - (width+10);
		}
		
		if(posY + height >= mCurHeight){
			posY = mCurHeight - (height+10);
		}
		
		
		Rect rect = new Rect(posX, posY, posX + width, posY + height);
		
		if(checkPosConflict(rect)){
			randomCount++;
			Log.i("searchkeyword", "Conflict");
			if(randomCount >= 10){
				//当同一个文字分配位置冲突超过10次时，我们把随机范围调整一下，避免随机取值范围过小导致无限递归
				
				int start = 0;
				int end = 0;
				if(rect.top <= mCurHeight/2){
					end = mCurHeight;
				}else{
					start = mCurHeight/2;
					end = mCurHeight;
				}
				
				return getNextStartPos(width, height,start,end);
			}else{
				return getNextStartPos(width, height,0,0);
			}
		}
		
//		System.out.println("posX:"+posX+" posY:"+posY);
		randomCount = 0;
		
		currentWordPosY += verticalWordHeight;
		
		return new Point(posX, posY);
	}
	
	private boolean checkPosConflict(Rect rect){
		for(int i = 0; i < keyWordInfos.size();i++){
			if(conflict(rect, keyWordInfos.get(i).keyWordRect)){
				return true;
			}
		}
		return false;
	}
	
	private boolean conflict(Rect rect1, Rect rect2){
		if((rect1.left <= rect2.right && rect1.top <= rect2.bottom) && (rect1.right >=rect2.left && rect1.bottom >=rect2.top)){
//			System.out.println("rect1.left:"+rect1.left+" rect2.right:"+rect2.right+" rect1.top:"+rect1.top+" rect2.bottom:"+rect2.bottom);
			return true;
		}else if((rect1.right >= rect2.left && rect1.bottom >= rect2.top) && (rect1.left <= rect2.right && rect1.top <= rect2.bottom)){
			return true;
		}
		return false;
	}
	
	private int getRandomInt(int min, int max){
		if(max == 0)
			return 0;
		
		if(min >= max){
			min = 0;
		}
//		random.setSeed(System.currentTimeMillis());
		int num = min + random.nextInt(max-min);
		return num;
	}
	
	private void drawKeyWordInfos(Canvas canvas){
		for(KeyWordInfo keyWordInfo : keyWordInfos){
//			System.out.println("\""+keyWordInfo.keyWord+"\""+" left:"+keyWordInfo.keyWordRect.left+" top:"+keyWordInfo.keyWordRect.top+" right:"+keyWordInfo.keyWordRect.right+" bottom:"+keyWordInfo.keyWordRect.bottom);
			Paint paint = new Paint();
			paint.setColor(Color.WHITE);
			if(keyWordInfo.isPress){
				Rect rect = new Rect();
				rect.left = keyWordInfo.keyWordRect.left;
				rect.top = keyWordInfo.keyWordRect.top;
				rect.right = keyWordInfo.keyWordRect.right;
				rect.bottom = keyWordInfo.keyWordRect.bottom+(int)keyWordInfo.paint.descent();
				canvas.drawRect(rect, keyWordInfo.paint);
				paint.setTextSize(keyWordInfo.paint.getTextSize());
				canvas.drawText(keyWordInfo.keyWord, keyWordInfo.keyWordRect.left, keyWordInfo.keyWordRect.bottom, paint);
			}else{
				canvas.drawText(keyWordInfo.keyWord, keyWordInfo.keyWordRect.left, keyWordInfo.keyWordRect.bottom, keyWordInfo.paint);
			}
		}
	}

	@Override
	public void draw(Canvas canvas) {
		drawKeyWordInfos(canvas);
		super.draw(canvas);
	}
	
	float startPressPointX;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			setKeyWordPress(new Point((int)event.getX(),(int)event.getY()));
			startPressPointX = event.getX();
		}else if(event.getAction() == MotionEvent.ACTION_UP){
			if(Math.abs(startPressPointX - event.getX()) >= mCurWitdh/5){
				reloadKeyWord();
			}else{
				performClick(new Point((int)event.getX(),(int)event.getY()));
			}
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
	}
	
	public void reloadKeyWord(){
		keyWordInfos.clear();
		resetKeyWordList();
		initKeyWordState();
		executeShowAnimation();
	}
	
	private void executeShowAnimation(){
		if(mCurKeywords == null)
			return;
		
		ScaleAnimation animation = new ScaleAnimation(0, 1f, 0, 1f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
		animation.setDuration(500);
		startAnimation(animation);
	}
	
	public void resetKeyWordList(){
		
		if(mTotalKeywords == null)
			return;
		
		if(mCurKeywords != null){
			mCurKeywords.clear();
			mCurKeywords = null;
		}
		
		
		ArrayList<BaseKeyWord> tempList = new ArrayList<BaseKeyWord>();
		
		ArrayList<Integer> usedKey = new ArrayList<Integer>();//重新设置完已经添加的热词
		
		int start = 0;
		int end = mTotalKeywords.size();
		int i = 1;
		while(i <= keyWordCount){
			
			//如果已经用过的热词为全部热词，则清空热词记录，重新记录
			if(mLoadedKeywordIndex.size() == mTotalKeywords.size()){
				mLoadedKeywordIndex.clear();
				for(int j = 0; j < usedKey.size(); j++){
					mLoadedKeywordIndex.put(usedKey.get(j), true);
				}
			}
			
			int selectedIndex = getRandomInt(start, end);
			if(mLoadedKeywordIndex.get(selectedIndex) == null){
				mLoadedKeywordIndex.put(selectedIndex, true);
				tempList.add(mTotalKeywords.get(selectedIndex));
				usedKey.add(selectedIndex);
				i++;
			}
		}
		
		mCurKeywords = tempList;
	}
	
	private void setKeyWordPress(Point point){
		for(int i = 0; i < keyWordInfos.size();i++){
			Rect rect = keyWordInfos.get(i).keyWordRect;
			if(point.x >= rect.left && point.x <= rect.right 
					&& point.y >= rect.top && point.y <= rect.bottom){
				keyWordInfos.get(i).isPress = true;
				invalidate();
				break;
			}
		}
	}
	
	private void performClick(Point point){
		for(int i = 0; i < keyWordInfos.size();i++){
			Rect rect = keyWordInfos.get(i).keyWordRect;
			if(point.x >= rect.left && point.x <= rect.right 
					&& point.y >= rect.top && point.y <= rect.bottom){
				if(keyWordInfos.get(i).isPress){
//					Toast.makeText(getContext(), keyWordInfos.get(i).keyWord, Toast.LENGTH_LONG).show();
					if(mOnItemClickListener != null){
						mOnItemClickListener.onClick(i);
					}
				}
				break;
			}
		}
		resetKeyWordPressState();
	}
	
	private void resetKeyWordPressState(){
		for(int i = 0; i < keyWordInfos.size();i++){
			keyWordInfos.get(i).isPress = false;
		}
		invalidate();
	}
	
	private class KeyWordInfo implements IProguardFilter{
		String keyWord;
		Paint paint;
		Rect keyWordRect;
		boolean isPress = false;
		KeyWordInfo(String keyWord, Paint paint, Rect keyWordRect){
			this.keyWord = keyWord;
			this.paint = paint;
			this.keyWordRect = keyWordRect;
		}
	}
	
	public static interface BaseKeyWord extends IProguardFilterOnlyPublic{
		public String getKeyWord();
	}
	
	public float getDIPToPX(float dipValue){ 
        final float scale = getResources().getDisplayMetrics().density; 
        return (dipValue * scale + 0.5f);
	}
	
	public void setOnItemClickListener(OnItemClickListener listener){
		mOnItemClickListener = listener;
	}
	
	public static interface OnItemClickListener extends IProguardFilterOnlyPublic{
		public void onClick(int position);
	}
	
	public void setAdapter(ArrayList<? extends BaseKeyWord> list){
		if(list == null || list.size() < 1)
			return;
		
		if(mTotalKeywords != null){
			mTotalKeywords.clear();
			mTotalKeywords = null;
		}
		mTotalKeywords = list;
		if(mTotalKeywords.size() < keyWordCount){
			keyWordCount = mTotalKeywords.size();
		}
		resetKeyWordList();
	}
}
