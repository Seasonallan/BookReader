package com.lectek.android.lereader.widgets;

import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
/*
 * by zlq
 * 2012-9-4
 * */
public class VerifyCodeView extends View {

	Paint mPaint;
	private final int TEXT_SIZE = 20;
	private final int VERIFYCODE_SIZE = 4;
	private String character;
	private float skewX;
	private Random random = new Random();
	private static final char[] CHARS = {  
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',  
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',   
        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',  
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',   
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'  
    };
	
	public VerifyCodeView(Context context) {
		super(context);
		init();
	}

	public VerifyCodeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		getRandomText();
		init();
	}

	public VerifyCodeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {  
		mPaint = new Paint();
		/* Sawtooth */
		mPaint.setAntiAlias(true);
		/* Set the frame width of the paint */
		mPaint.setStrokeWidth(4);
		/*set the font sieze*/ 
		mPaint.setTextSize(getRawSize(TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE));
		mPaint.setTextAlign(Paint.Align.CENTER);
	}

	public void ChangeData(){
		getRandomText();
		invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		
		canvas.drawColor(Color.GRAY);
		canvas.save();
		mPaint.setColor(Color.BLUE);
		mPaint.setTextSkewX(skewX);
		canvas.drawText(character, getRawSize(TypedValue.COMPLEX_UNIT_DIP, 48), getRawSize(TypedValue.COMPLEX_UNIT_DIP, 21), mPaint);
		mPaint.setColor(Color.WHITE);
		mPaint.setStrokeWidth(0);
		canvas.drawLine(getRawSize(TypedValue.COMPLEX_UNIT_DIP, 0), getRawSize(TypedValue.COMPLEX_UNIT_DIP, 15), 
				getRawSize(TypedValue.COMPLEX_UNIT_DIP, 80), getRawSize(TypedValue.COMPLEX_UNIT_DIP, 9), mPaint);
		saveCode(character);
		canvas.restore();
		
		super.onDraw(canvas);
	}
	
	private void getRandomText(){
		float skewX = random.nextInt(11) / 10;  
        skewX = random.nextBoolean() ? skewX : -skewX;
		String sRand = "";
		for (int i = 0; i < VERIFYCODE_SIZE; i++) {
			String character = String.valueOf(CHARS[random.nextInt(CHARS.length)]);
			sRand += character+" ";
		}
		this.skewX = skewX;
		this.character = sRand;
	}

	public float getRawSize(int unit, float size) { 
        Context c = getContext(); 
        Resources r; 
 
        if (c == null) 
            r = Resources.getSystem(); 
        else 
            r = c.getResources(); 
         
        return TypedValue.applyDimension(unit, size, r.getDisplayMetrics()); 
    }
	
	/**
	 * 
	 * Save coding in here
	 * @param code
	 */

	private void saveCode(String code) {
		StringBuilder sb = new StringBuilder();
		for(char result:code.toCharArray()){
			if(result != ' '){
				sb.append(result);
			}
		}
//		PreferencesUtil.getInstance(getContext()).setVerifyCode(sb.toString());
	}
}
