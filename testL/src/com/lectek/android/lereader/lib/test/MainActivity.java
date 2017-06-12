package com.lectek.android.lereader.lib.test;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.widget.TextView;

import com.lectek.android.lereader.lib.R;

public class MainActivity extends Activity {

	private LogStream mLogStream;
	
	public static MainActivity Instance;
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Instance = this;
        Handler handler = new Handler(Looper.getMainLooper());
        
        mLogStream = new LogStream((TextView)findViewById(R.id.logout));
        
        handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				ArrayList<Class<? extends AbsTestUnit>> testUnits = AutoTestConfig.TEST_UNITS;
				final int count = testUnits.size();
				try {
					for(int i = 0; i < count; i++) {
						Class<? extends AbsTestUnit> unitClass = testUnits.get(i);
						
						AbsTestUnit unit = unitClass.newInstance();
						mLogStream.write(String.format("Test %s model......\n", unit.getTag()).getBytes());
						unit.setLogOutputStream(mLogStream);
						unit.runTest();
						mLogStream.write(String.format("%s's test finish.\n", unit.getTag()).getBytes());
					}
					
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch(IllegalAccessException e) {
					e.printStackTrace();
				} catch(InstantiationException e) {
					e.printStackTrace();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}, 20);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    private class LogStream extends OutputStream {

		private StringBuilder content = new StringBuilder();
    	private TextView textView;
    	
    	public LogStream(TextView textView) {
    		this.textView = textView;
    	}
    	
    	@Override
		public void write(int oneByte) throws IOException {
    		content.append((char)oneByte);
    	}
    	
    	@Override
		public void flush() throws IOException {
			super.flush();
			textView.post(new Runnable() {
				
				@Override
				public void run() {
					textView.setText(content.toString());
				}
			});
		}
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	Instance = null;
    	try {
    		mLogStream.close();
    	}catch(IOException e){}
    }
}
